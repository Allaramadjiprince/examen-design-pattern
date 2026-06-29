package com.exam.badwallet.proxy;

import java.time.LocalDate;
import java.util.List;

/**
 * Pattern PROXY : interface commune.
 * badwallet-api ne connait que cette abstraction, pas l'implementation HTTP reelle.
 */
public interface FacturationService {

    List<FactureExterne> facturesMoisEnCours(String walletCode, String unite);

    List<FactureExterne> facturesParPeriode(String walletCode, LocalDate debut, LocalDate fin);

    PaiementExterneResponse payer(String walletCode, String unite, List<String> references);
}
