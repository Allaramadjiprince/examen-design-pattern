package com.exam.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaiementResponse {
    private String walletCode;
    private String unite;
    private BigDecimal montantTotal;
    private List<String> referencesPayees;
    private String message;
}
