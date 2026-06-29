package com.exam.badwallet.service.strategy;

import java.math.BigDecimal;

/**
 * Pattern STRATEGY.
 * Definit comment un depot est traite selon le moyen utilise (CREDIT_CARD, WALLET_TARGET).
 */
public interface DepositStrategy {
    // Identifie le moyen de paiement gere par cette strategie
    String getPaymentMethod();
    // Retourne le montant reellement credite (apres frais eventuels du moyen)
    BigDecimal computeCreditedAmount(BigDecimal amount);
}
