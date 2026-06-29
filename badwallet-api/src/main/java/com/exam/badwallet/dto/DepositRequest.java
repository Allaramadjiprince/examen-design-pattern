package com.exam.badwallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepositRequest {
    private BigDecimal amount;
    // CREDIT_CARD ou WALLET_TARGET
    private String paymentMethod;
}
