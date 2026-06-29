package com.exam.badwallet.service.strategy;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;

/**
 * Depot via un autre portefeuille cible : credite integralement.
 */
@Component
public class WalletTargetDepositStrategy implements DepositStrategy {
    @Override
    public String getPaymentMethod() {
        return "WALLET_TARGET";
    }
    @Override
    public BigDecimal computeCreditedAmount(BigDecimal amount) {
        return amount;
    }
}
