package com.exam.badwallet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletDTO {
    private Long id;
    private String phoneNumber;
    private String email;
    private String code;
    private BigDecimal balance;
    private String currency;
    private LocalDateTime createdAt;
}
