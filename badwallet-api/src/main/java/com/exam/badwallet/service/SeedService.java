package com.exam.badwallet.service;

import com.exam.badwallet.model.Transaction;
import com.exam.badwallet.model.TransactionType;
import com.exam.badwallet.model.Wallet;
import com.exam.badwallet.repository.TransactionRepository;
import com.exam.badwallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Seeder de badwallet-api (point 1.1 du sujet).
 * Cree numWallets portefeuilles + eventsPerWallet transactions chacun.
 * Les numeros suivent le format +22177000000X et les codes WLT-000000X
 * pour coller aux exemples du sujet.
 */
@Service
@RequiredArgsConstructor
public class SeedService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final Random random = new Random();

    @Async
    @Transactional
    public void seed(int numWallets, int eventsPerWallet) {
        if (walletRepository.count() > 0) {
            return;
        }
        List<Wallet> wallets = new ArrayList<>();
        for (int i = 1; i <= numWallets; i++) {
            String phone = String.format("+2217700000%02d", i);
            String code = String.format("WLT-%07d", i);
            wallets.add(Wallet.builder()
                    .phoneNumber(phone)
                    .email("client" + i + "@badwallet.sn")
                    .code(code)
                    .balance(BigDecimal.valueOf(100000))
                    .currency("XOF")
                    .createdAt(LocalDateTime.now())
                    .build());
        }
        walletRepository.saveAll(wallets);

        List<Transaction> txs = new ArrayList<>();
        for (Wallet w : wallets) {
            BigDecimal running = w.getBalance();
            for (int e = 0; e < eventsPerWallet; e++) {
                BigDecimal amount = BigDecimal.valueOf((random.nextInt(20) + 1) * 1000);
                TransactionType type = (e % 2 == 0) ? TransactionType.DEPOT : TransactionType.RETRAIT;
                running = type == TransactionType.DEPOT ? running.add(amount) : running.subtract(amount);
                txs.add(Transaction.builder()
                        .walletPhone(w.getPhoneNumber())
                        .type(type)
                        .amount(amount)
                        .fees(BigDecimal.ZERO)
                        .balanceAfter(running)
                        .description("Evenement seede #" + e)
                        .createdAt(LocalDateTime.now().minusDays(random.nextInt(30)))
                        .build());
            }
            w.setBalance(running);
        }
        transactionRepository.saveAll(txs);
        walletRepository.saveAll(wallets);
        System.out.println(">>> [badwallet-api] Seeding termine : "
                + numWallets + " wallets, " + txs.size() + " transactions.");
    }
}
