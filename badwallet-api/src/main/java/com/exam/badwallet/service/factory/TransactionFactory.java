package com.exam.badwallet.service.factory;

import com.exam.badwallet.model.Transaction;
import com.exam.badwallet.model.TransactionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Pattern FACTORY METHOD.
 * Centralise la creation des objets Transaction selon leur type,
 * pour eviter de dupliquer la logique de construction dans le service.
 */
@Component
public class TransactionFactory {

    public Transaction create(String walletPhone, TransactionType type,
                              BigDecimal amount, BigDecimal fees,
                              BigDecimal balanceAfter, String description) {
        return Transaction.builder()
                .walletPhone(walletPhone)
                .type(type)
                .amount(amount)
                .fees(fees == null ? BigDecimal.ZERO : fees)
                .balanceAfter(balanceAfter)
                .description(description)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Transaction depot(String phone, BigDecimal amount, BigDecimal balanceAfter, String method) {
        return create(phone, TransactionType.DEPOT, amount, BigDecimal.ZERO, balanceAfter,
                "Depot via " + method);
    }

    public Transaction retrait(String phone, BigDecimal amount, BigDecimal fees, BigDecimal balanceAfter) {
        return create(phone, TransactionType.RETRAIT, amount, fees, balanceAfter,
                "Retrait (frais : " + fees + " CFA)");
    }

    public Transaction transfertEnvoye(String phone, BigDecimal amount, BigDecimal balanceAfter, String dest) {
        return create(phone, TransactionType.TRANSFERT_ENVOYE, amount, BigDecimal.ZERO, balanceAfter,
                "Transfert envoye vers " + dest);
    }

    public Transaction transfertRecu(String phone, BigDecimal amount, BigDecimal balanceAfter, String src) {
        return create(phone, TransactionType.TRANSFERT_RECU, amount, BigDecimal.ZERO, balanceAfter,
                "Transfert recu de " + src);
    }

    public Transaction paiement(String phone, BigDecimal amount, BigDecimal balanceAfter, String service) {
        return create(phone, TransactionType.PAIEMENT, amount, BigDecimal.ZERO, balanceAfter,
                "Paiement facture " + service);
    }
}
