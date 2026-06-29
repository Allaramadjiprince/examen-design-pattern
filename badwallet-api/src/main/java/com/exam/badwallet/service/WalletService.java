package com.exam.badwallet.service;

import com.exam.badwallet.dto.*;
import com.exam.badwallet.exception.BusinessException;
import com.exam.badwallet.model.Transaction;
import com.exam.badwallet.model.Wallet;
import com.exam.badwallet.proxy.FactureExterne;
import com.exam.badwallet.proxy.FacturationService;
import com.exam.badwallet.proxy.PaiementExterneResponse;
import com.exam.badwallet.repository.TransactionRepository;
import com.exam.badwallet.repository.WalletRepository;
import com.exam.badwallet.service.factory.TransactionFactory;
import com.exam.badwallet.service.strategy.DepositStrategy;
import com.exam.badwallet.service.strategy.DepositStrategyResolver;
import com.exam.badwallet.service.strategy.FeeStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionFactory transactionFactory;          // Pattern Factory
    private final DepositStrategyResolver depositStrategyResolver; // Pattern Strategy
    private final FeeStrategy withdrawFeeStrategy;                 // Pattern Strategy
    private final FacturationService facturationService;          // Pattern Proxy

    // ---------- Helpers ----------

    private Wallet getByPhone(String phone) {
        return walletRepository.findByPhoneNumber(phone)
                .orElseThrow(() -> new BusinessException("Portefeuille introuvable : " + phone));
    }

    private Wallet getById(Long id) {
        return walletRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Portefeuille introuvable, id : " + id));
    }

    private WalletDTO toDTO(Wallet w) {
        return WalletDTO.builder()
                .id(w.getId()).phoneNumber(w.getPhoneNumber()).email(w.getEmail())
                .code(w.getCode()).balance(w.getBalance()).currency(w.getCurrency())
                .createdAt(w.getCreatedAt()).build();
    }

    private TransactionDTO toDTO(Transaction t) {
        return TransactionDTO.builder()
                .id(t.getId()).walletPhone(t.getWalletPhone()).type(t.getType())
                .amount(t.getAmount()).fees(t.getFees()).balanceAfter(t.getBalanceAfter())
                .description(t.getDescription()).createdAt(t.getCreatedAt()).build();
    }

    // ---------- 1.2 Creation ----------

    @Transactional
    public WalletDTO createWallet(CreateWalletRequest req) {
        if (walletRepository.existsByPhoneNumber(req.getPhoneNumber())) {
            throw new BusinessException("Un portefeuille existe deja avec ce numero.");
        }
        if (req.getCode() != null && walletRepository.existsByCode(req.getCode())) {
            throw new BusinessException("Un portefeuille existe deja avec ce code.");
        }
        Wallet wallet = Wallet.builder()
                .phoneNumber(req.getPhoneNumber())
                .email(req.getEmail())
                .code(req.getCode())
                .balance(req.getInitialBalance() == null ? BigDecimal.ZERO : req.getInitialBalance())
                .currency(req.getCurrency() == null ? "XOF" : req.getCurrency())
                .createdAt(LocalDateTime.now())
                .build();
        return toDTO(walletRepository.save(wallet));
    }

    // ---------- 1.3 Liste paginee ----------

    public Page<WalletDTO> listWallets(Pageable pageable) {
        return walletRepository.findAll(pageable).map(this::toDTO);
    }

    // ---------- 1.4 / 1.5 Consultation ----------

    public WalletDTO getWallet(String phone) {
        return toDTO(getByPhone(phone));
    }

    public BigDecimal getBalance(String phone) {
        return getByPhone(phone).getBalance();
    }

    // ---------- 1.6 Depot (Strategy) ----------

    @Transactional
    public TransactionDTO deposit(Long walletId, DepositRequest req) {
        Wallet wallet = getById(walletId);
        if (req.getAmount() == null || req.getAmount().signum() <= 0) {
            throw new BusinessException("Le montant du depot doit etre positif.");
        }
        DepositStrategy strategy = depositStrategyResolver.resolve(req.getPaymentMethod());
        BigDecimal credited = strategy.computeCreditedAmount(req.getAmount());

        wallet.setBalance(wallet.getBalance().add(credited));
        walletRepository.save(wallet);

        Transaction tx = transactionFactory.depot(
                wallet.getPhoneNumber(), credited, wallet.getBalance(), strategy.getPaymentMethod());
        return toDTO(transactionRepository.save(tx));
    }

    // ---------- 1.7 Retrait (Strategy frais 1% plafonne 5000) ----------

    @Transactional
    public TransactionDTO withdraw(WithdrawRequest req) {
        Wallet wallet = getByPhone(req.getPhoneNumber());
        if (req.getAmount() == null || req.getAmount().signum() <= 0) {
            throw new BusinessException("Le montant du retrait doit etre positif.");
        }
        BigDecimal fees = withdrawFeeStrategy.computeFees(req.getAmount());
        BigDecimal total = req.getAmount().add(fees);

        if (wallet.getBalance().compareTo(total) < 0) {
            throw new BusinessException("Solde insuffisant. Requis (montant + frais) : " + total
                    + " CFA, solde : " + wallet.getBalance() + " CFA.");
        }
        wallet.setBalance(wallet.getBalance().subtract(total));
        walletRepository.save(wallet);

        Transaction tx = transactionFactory.retrait(
                wallet.getPhoneNumber(), req.getAmount(), fees, wallet.getBalance());
        return toDTO(transactionRepository.save(tx));
    }

    // ---------- 1.8 Transfert ----------

    @Transactional
    public ApiResponse transfer(TransferRequest req) {
        if (req.getSenderPhone().equals(req.getReceiverPhone())) {
            throw new BusinessException("Impossible de transferer vers le meme portefeuille.");
        }
        if (req.getAmount() == null || req.getAmount().signum() <= 0) {
            throw new BusinessException("Le montant du transfert doit etre positif.");
        }
        Wallet sender = getByPhone(req.getSenderPhone());
        Wallet receiver = getByPhone(req.getReceiverPhone());

        if (sender.getBalance().compareTo(req.getAmount()) < 0) {
            throw new BusinessException("Solde insuffisant pour le transfert.");
        }
        sender.setBalance(sender.getBalance().subtract(req.getAmount()));
        receiver.setBalance(receiver.getBalance().add(req.getAmount()));
        walletRepository.save(sender);
        walletRepository.save(receiver);

        Transaction txOut = transactionFactory.transfertEnvoye(
                sender.getPhoneNumber(), req.getAmount(), sender.getBalance(), receiver.getPhoneNumber());
        Transaction txIn = transactionFactory.transfertRecu(
                receiver.getPhoneNumber(), req.getAmount(), receiver.getBalance(), sender.getPhoneNumber());
        transactionRepository.save(txOut);
        transactionRepository.save(txIn);

        return ApiResponse.builder().success(true)
                .message("Transfert de " + req.getAmount() + " CFA effectue avec succes.")
                .data(toDTO(txOut)).build();
    }

    // ---------- 1.9 / 1.10 Paiement de factures (Proxy + Factory) ----------

    @Transactional
    public ApiResponse payFactures(PayRequest req, boolean referencesSpecifiques) {
        Wallet wallet = getByPhone(req.getPhoneNumber());

        // 1. On determine les references a payer en interrogeant le payment-service (Proxy)
        List<String> references;
        BigDecimal montantAPayer;

        if (referencesSpecifiques) {
            if (req.getFactureReferences() == null || req.getFactureReferences().isEmpty()) {
                throw new BusinessException("Aucune reference de facture fournie.");
            }
            references = req.getFactureReferences();
            // On recupere les factures du mois pour calculer le montant exact des references demandees
            List<FactureExterne> factures = facturationService
                    .facturesMoisEnCours(wallet.getCode(), req.getServiceName());
            montantAPayer = factures.stream()
                    .filter(f -> references.contains(f.getReference()))
                    .map(FactureExterne::getMontant)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (montantAPayer.signum() == 0) {
                throw new BusinessException("Les references fournies sont introuvables ou deja payees.");
            }
        } else {
            references = null; // le payment-service paiera tout le mois en cours pour ce service
            montantAPayer = req.getAmount();
            if (montantAPayer == null || montantAPayer.signum() <= 0) {
                throw new BusinessException("Le montant a payer doit etre positif.");
            }
        }

        // 2. Verification du solde
        if (wallet.getBalance().compareTo(montantAPayer) < 0) {
            throw new BusinessException("Solde insuffisant pour payer la facture. Requis : "
                    + montantAPayer + " CFA, solde : " + wallet.getBalance() + " CFA.");
        }

        // 3. Appel du payment-service pour marquer les factures payees (Proxy)
        PaiementExterneResponse response = facturationService
                .payer(wallet.getCode(), req.getServiceName(), references);

        // Le montant reel debite vient du service externe quand on paie tout le mois
        BigDecimal montantReel = (response != null && response.getMontantTotal() != null
                && !referencesSpecifiques)
                ? response.getMontantTotal()
                : montantAPayer;

        if (wallet.getBalance().compareTo(montantReel) < 0) {
            throw new BusinessException("Solde insuffisant apres calcul des factures du mois.");
        }

        // 4. Debit du portefeuille
        wallet.setBalance(wallet.getBalance().subtract(montantReel));
        walletRepository.save(wallet);

        // 5. Transaction (Factory)
        Transaction tx = transactionFactory.paiement(
                wallet.getPhoneNumber(), montantReel, wallet.getBalance(), req.getServiceName());
        transactionRepository.save(tx);

        return ApiResponse.builder().success(true)
                .message("Paiement de " + montantReel + " CFA pour " + req.getServiceName()
                        + " effectue. " + (response != null ? response.getMessage() : ""))
                .data(response).build();
    }

    // ---------- 1.11 Historique ----------

    public List<TransactionDTO> getTransactions(String phone) {
        getByPhone(phone); // valide l'existence
        return transactionRepository.findByWalletPhoneOrderByCreatedAtDesc(phone)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ---------- Partie 2 : Proxy factures ----------

    public List<FactureExterne> facturesMoisEnCours(String walletCode, String unite) {
        return facturationService.facturesMoisEnCours(walletCode, unite);
    }

    public List<FactureExterne> facturesParPeriode(String walletCode, LocalDate debut, LocalDate fin) {
        return facturationService.facturesParPeriode(walletCode, debut, fin);
    }
}
