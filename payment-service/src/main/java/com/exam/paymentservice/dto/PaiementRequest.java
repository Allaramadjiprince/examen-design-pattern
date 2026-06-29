package com.exam.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaiementRequest {
    private String walletCode;
    private String unite;
    // Si vide : on paie toutes les factures impayees du mois en cours pour cette unite
    private List<String> factureReferences;
}
