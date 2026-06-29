package com.exam.paymentservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "factures")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Reference unique de la facture, ex : FAC-ISM-3-1
    @Column(unique = true, nullable = false)
    private String reference;

    // Code du portefeuille concerne, ex : WLT-0000003
    @Column(nullable = false)
    private String walletCode;

    // Unite / fournisseur de service : ISM, WOYAFAL ...
    @Column(nullable = false)
    private String unite;

    @Column(nullable = false)
    private BigDecimal montant;

    // Date d'emission de la facture
    @Column(nullable = false)
    private LocalDate dateEmission;

    // true si deja payee
    @Column(nullable = false)
    private boolean payee;
}
