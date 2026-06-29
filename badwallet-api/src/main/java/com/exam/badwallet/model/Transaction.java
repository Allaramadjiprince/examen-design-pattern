package com.exam.badwallet.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Portefeuille concerne par la transaction
    @Column(nullable = false)
    private String walletPhone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false)
    private BigDecimal amount;

    // Frais eventuels (retrait, etc.)
    private BigDecimal fees;

    // Solde du portefeuille apres l'operation
    private BigDecimal balanceAfter;

    // Description libre : contrepartie, service paye ...
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
