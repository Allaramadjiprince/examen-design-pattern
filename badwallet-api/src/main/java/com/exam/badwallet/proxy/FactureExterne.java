package com.exam.badwallet.proxy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Representation locale d'une facture renvoyee par le payment-service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FactureExterne {
    private String reference;
    private String walletCode;
    private String unite;
    private BigDecimal montant;
    private LocalDate dateEmission;
    private boolean payee;
}
