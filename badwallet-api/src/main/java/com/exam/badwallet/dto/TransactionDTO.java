package com.exam.badwallet.dto;

import com.exam.badwallet.model.TransactionType;
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
public class TransactionDTO {
    private Long id;
    private String walletPhone;
    private TransactionType type;
    private BigDecimal amount;
    private BigDecimal fees;
    private BigDecimal balanceAfter;
    private String description;
    private LocalDateTime createdAt;
}
