package com.exam.paymentservice.config;

import com.exam.paymentservice.model.Facture;
import com.exam.paymentservice.repository.FactureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Au demarrage, on cree des factures pour les wallets WLT-0000001 a WLT-0000010.
 * Les references suivent le format FAC-<UNITE>-<numWallet>-<index>
 * pour coller aux exemples du sujet (FAC-ISM-3-1, FAC-ISM-3-3 ...).
 */
@Component
@RequiredArgsConstructor
public class FactureSeeder implements CommandLineRunner {

    private final FactureRepository factureRepository;

    @Override
    public void run(String... args) {
        if (factureRepository.count() > 0) return;

        String[] unites = {"ISM", "WOYAFAL"};
        List<Facture> factures = new ArrayList<>();
        LocalDate moisEnCours = LocalDate.now().withDayOfMonth(5);

        for (int w = 1; w <= 10; w++) {
            String walletCode = String.format("WLT-%07d", w);
            for (String unite : unites) {
                // 3 factures impayees du mois en cours par unite
                for (int i = 1; i <= 3; i++) {
                    factures.add(Facture.builder()
                            .reference("FAC-" + unite + "-" + w + "-" + i)
                            .walletCode(walletCode)
                            .unite(unite)
                            .montant(BigDecimal.valueOf(2500L * i))
                            .dateEmission(moisEnCours)
                            .payee(false)
                            .build());
                }
                // 1 facture impayee d'un mois precedent (pour tester la recherche par periode)
                factures.add(Facture.builder()
                        .reference("FAC-" + unite + "-" + w + "-OLD")
                        .walletCode(walletCode)
                        .unite(unite)
                        .montant(BigDecimal.valueOf(4000))
                        .dateEmission(LocalDate.now().minusMonths(1).withDayOfMonth(10))
                        .payee(false)
                        .build());
            }
        }

        factureRepository.saveAll(factures);
        System.out.println(">>> [payment-service] " + factures.size() + " factures generees.");
    }
}
