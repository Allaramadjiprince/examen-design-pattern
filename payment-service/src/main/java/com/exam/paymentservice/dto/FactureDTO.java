package com.exam.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FactureDTO {
    private String reference;
    private String walletCode;
    private String unite;
    private BigDecimal montant;
    private LocalDate dateEmission;
    private boolean payee;
}
