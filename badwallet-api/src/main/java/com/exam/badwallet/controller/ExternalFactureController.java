package com.exam.badwallet.controller;

import com.exam.badwallet.proxy.FactureExterne;
import com.exam.badwallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Partie 2 : endpoints Proxy exposes par badwallet-api,
 * qui relaient les appels vers le payment-service (port 8081).
 */
@RestController
@RequestMapping("/api/external/factures")
@RequiredArgsConstructor
public class ExternalFactureController {

    private final WalletService walletService;

    // 2.2 / 2.3 Factures impayees du mois en cours (filtre optionnel par unite)
    @GetMapping("/{walletCode}/current")
    public ResponseEntity<List<FactureExterne>> current(
            @PathVariable String walletCode,
            @RequestParam(required = false) String unite) {
        return ResponseEntity.ok(walletService.facturesMoisEnCours(walletCode, unite));
    }

    // 2.4 Factures impayees sur une periode
    @GetMapping("/{walletCode}/periode")
    public ResponseEntity<List<FactureExterne>> periode(
            @PathVariable String walletCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(walletService.facturesParPeriode(walletCode, debut, fin));
    }
}
