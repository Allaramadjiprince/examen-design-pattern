package com.exam.paymentservice.controller;

import com.exam.paymentservice.dto.FactureDTO;
import com.exam.paymentservice.dto.PaiementRequest;
import com.exam.paymentservice.dto.PaiementResponse;
import com.exam.paymentservice.service.FactureService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/factures")
@RequiredArgsConstructor
public class FactureController {

    private final FactureService factureService;

    // Factures impayees du mois en cours, filtre optionnel par unite
    @GetMapping("/{walletCode}/current")
    public ResponseEntity<List<FactureDTO>> facturesMoisEnCours(
            @PathVariable String walletCode,
            @RequestParam(required = false) String unite) {

        if (unite != null && !unite.isBlank()) {
            return ResponseEntity.ok(factureService.facturesImpayeesMoisEnCoursParUnite(walletCode, unite));
        }
        return ResponseEntity.ok(factureService.facturesImpayeesMoisEnCours(walletCode));
    }

    // Factures impayees sur une periode personnalisee
    @GetMapping("/{walletCode}/periode")
    public ResponseEntity<List<FactureDTO>> facturesParPeriode(
            @PathVariable String walletCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(factureService.facturesImpayeesParPeriode(walletCode, debut, fin));
    }

    // Paiement de factures (appele par badwallet-api)
    @PostMapping("/pay")
    public ResponseEntity<PaiementResponse> payer(@RequestBody PaiementRequest request) {
        return ResponseEntity.ok(factureService.payer(request));
    }
}
