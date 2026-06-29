package com.exam.badwallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayRequest {
    private String phoneNumber;
    private String serviceName;
    private BigDecimal amount;
    // Pour /pay-factures : references precises a payer
    private List<String> factureReferences;
}
