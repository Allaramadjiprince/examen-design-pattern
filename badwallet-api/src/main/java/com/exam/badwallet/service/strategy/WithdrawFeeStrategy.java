package com.exam.badwallet.service.strategy;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;

/**
 * Frais de retrait : 1% du montant, plafonnes a 5000 CFA (regle du sujet, point 1.7).
 */
@Component
public class WithdrawFeeStrategy implements FeeStrategy {

    private static final BigDecimal RATE = new BigDecimal("0.01");
    private static final BigDecimal CAP = new BigDecimal("5000");

    @Override
    public BigDecimal computeFees(BigDecimal amount) {
        BigDecimal fees = amount.multiply(RATE);
        return fees.min(CAP);
    }
}
