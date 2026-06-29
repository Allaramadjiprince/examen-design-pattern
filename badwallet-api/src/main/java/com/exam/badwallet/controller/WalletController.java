package com.exam.badwallet.controller;

import com.exam.badwallet.dto.*;
import com.exam.badwallet.service.SeedService;
import com.exam.badwallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final SeedService seedService;

    // 1.1 Seeder (async)
    @PostMapping("/seed")
    public ResponseEntity<ApiResponse> seed(
            @RequestParam(defaultValue = "10") int numWallets,
            @RequestParam(defaultValue = "100") int eventsPerWallet) {
        seedService.seed(numWallets, eventsPerWallet);
        return ResponseEntity.accepted().body(ApiResponse.builder()
                .success(true)
                .message("Seeding lance en arriere-plan : " + numWallets
                        + " wallets x " + eventsPerWallet + " evenements.")
                .build());
    }

    // 1.2 Creation
    @PostMapping
    public ResponseEntity<WalletDTO> create(@RequestBody CreateWalletRequest req) {
        return ResponseEntity.ok(walletService.createWallet(req));
    }

    // 1.3 Liste paginee
    @GetMapping
    public ResponseEntity<Page<WalletDTO>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(walletService.listWallets(pageable));
    }

    // 1.7 Retrait  (place AVANT /{phoneNumber} pour eviter le conflit de route)
    @PostMapping("/withdraw")
    public ResponseEntity<TransactionDTO> withdraw(@RequestBody WithdrawRequest req) {
        return ResponseEntity.ok(walletService.withdraw(req));
    }

    // 1.8 Transfert
    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse> transfer(@RequestBody TransferRequest req) {
        return ResponseEntity.ok(walletService.transfer(req));
    }

    // 1.9 Paiement facture du mois en cours (montant)
    @PostMapping("/pay")
    public ResponseEntity<ApiResponse> pay(@RequestBody PayRequest req) {
        return ResponseEntity.ok(walletService.payFactures(req, false));
    }

    // 1.10 Paiement de factures specifiques (references)
    @PostMapping("/pay-factures")
    public ResponseEntity<ApiResponse> payFactures(@RequestBody PayRequest req) {
        return ResponseEntity.ok(walletService.payFactures(req, true));
    }

    // 1.6 Depot (Strategy) - {id} numerique
    @PostMapping("/{id}/deposit")
    public ResponseEntity<TransactionDTO> deposit(
            @PathVariable Long id, @RequestBody DepositRequest req) {
        return ResponseEntity.ok(walletService.deposit(id, req));
    }

    // 1.5 Solde a jour
    @GetMapping("/{phoneNumber}/balance")
    public ResponseEntity<BigDecimal> balance(@PathVariable String phoneNumber) {
        return ResponseEntity.ok(walletService.getBalance(phoneNumber));
    }

    // 1.11 Historique des transactions
    @GetMapping("/{phoneNumber}/transactions")
    public ResponseEntity<List<TransactionDTO>> transactions(@PathVariable String phoneNumber) {
        return ResponseEntity.ok(walletService.getTransactions(phoneNumber));
    }

    // 1.4 Consultation par numero de telephone
    @GetMapping("/{phoneNumber}")
    public ResponseEntity<WalletDTO> getByPhone(@PathVariable String phoneNumber) {
        return ResponseEntity.ok(walletService.getWallet(phoneNumber));
    }
}
