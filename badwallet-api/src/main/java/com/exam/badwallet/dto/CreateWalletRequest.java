package com.exam.badwallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateWalletRequest {
    private String phoneNumber;
    private String email;
    private BigDecimal initialBalance;
    private String code;
    private String currency;
}
