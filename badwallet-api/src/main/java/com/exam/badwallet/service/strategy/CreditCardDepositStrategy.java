package com.exam.badwallet.service.strategy;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;

/**
 * Depot par carte de credit : aucun frais cote wallet, montant credite integralement.
 */
@Component
public class CreditCardDepositStrategy implements DepositStrategy {
    @Override
    public String getPaymentMethod() {
        return "CREDIT_CARD";
    }
    @Override
    public BigDecimal computeCreditedAmount(BigDecimal amount) {
        return amount;
    }
}
