package com.exam.badwallet.service.strategy;

import java.math.BigDecimal;

/**
 * Pattern STRATEGY pour le calcul des frais (extensible : retrait, transfert, etc.).
 */
public interface FeeStrategy {
    BigDecimal computeFees(BigDecimal amount);
}
