package com.exam.paymentservice.service;

import com.exam.paymentservice.dto.FactureDTO;
import com.exam.paymentservice.dto.PaiementRequest;
import com.exam.paymentservice.dto.PaiementResponse;
import com.exam.paymentservice.model.Facture;
import com.exam.paymentservice.repository.FactureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FactureService {

    private final FactureRepository factureRepository;

    private FactureDTO toDTO(Facture f) {
        return FactureDTO.builder()
                .reference(f.getReference())
                .walletCode(f.getWalletCode())
                .unite(f.getUnite())
                .montant(f.getMontant())
                .dateEmission(f.getDateEmission())
                .payee(f.isPayee())
                .build();
    }

    // Bornes du mois en cours
    private LocalDate debutMois() {
        return LocalDate.now().withDayOfMonth(1);
    }

    private LocalDate finMois() {
        return LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
    }

    // Factures impayees du mois en cours
    public List<FactureDTO> facturesImpayeesMoisEnCours(String walletCode) {
        return factureRepository
                .findImpayeesParPeriode(walletCode, debutMois(), finMois())
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Factures impayees du mois en cours filtrees par unite
    public List<FactureDTO> facturesImpayeesMoisEnCoursParUnite(String walletCode, String unite) {
        return factureRepository
                .findImpayeesParPeriodeEtUnite(walletCode, unite, debutMois(), finMois())
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Factures impayees sur une periode personnalisee
    public List<FactureDTO> facturesImpayeesParPeriode(String walletCode, LocalDate debut, LocalDate fin) {
        return factureRepository
                .findImpayeesParPeriode(walletCode, debut, fin)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Paiement : si references fournies on les paie, sinon on paie tout le mois en cours pour l'unite
    @Transactional
    public PaiementResponse payer(PaiementRequest request) {
        List<Facture> aPayer;

        if (request.getFactureReferences() != null && !request.getFactureReferences().isEmpty()) {
            aPayer = request.getFactureReferences().stream()
                    .map(ref -> factureRepository.findByReference(ref)
                            .orElseThrow(() -> new IllegalArgumentException("Facture introuvable : " + ref)))
                    .filter(f -> !f.isPayee())
                    .collect(Collectors.toList());
        } else {
            aPayer = factureRepository.findImpayeesParPeriodeEtUnite(
                    request.getWalletCode(), request.getUnite(), debutMois(), finMois());
        }

        BigDecimal total = BigDecimal.ZERO;
        for (Facture f : aPayer) {
            f.setPayee(true);
            total = total.add(f.getMontant());
        }
        factureRepository.saveAll(aPayer);

        return PaiementResponse.builder()
                .walletCode(request.getWalletCode())
                .unite(request.getUnite())
                .montantTotal(total)
                .referencesPayees(aPayer.stream().map(Facture::getReference).collect(Collectors.toList()))
                .message(aPayer.isEmpty()
                        ? "Aucune facture impayee a regler."
                        : aPayer.size() + " facture(s) payee(s) avec succes.")
                .build();
    }
}
