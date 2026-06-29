package com.exam.badwallet.service.strategy;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Selectionne dynamiquement la DepositStrategy correspondant au paymentMethod recu.
 * Spring injecte automatiquement toutes les implementations de DepositStrategy.
 */
@Component
public class DepositStrategyResolver {

    private final Map<String, DepositStrategy> strategies;

    public DepositStrategyResolver(List<DepositStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(DepositStrategy::getPaymentMethod, Function.identity()));
    }

    public DepositStrategy resolve(String paymentMethod) {
        DepositStrategy strategy = strategies.get(paymentMethod);
        if (strategy == null) {
            throw new IllegalArgumentException("Moyen de paiement non supporte : " + paymentMethod);
        }
        return strategy;
    }
}
