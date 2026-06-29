package com.exam.paymentservice.repository;

import com.exam.paymentservice.model.Facture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FactureRepository extends JpaRepository<Facture, Long> {

    Optional<Facture> findByReference(String reference);

    List<Facture> findByWalletCode(String walletCode);

    // Factures impayees d'un wallet sur une periode donnee
    @Query("SELECT f FROM Facture f WHERE f.walletCode = :walletCode " +
           "AND f.payee = false " +
           "AND f.dateEmission BETWEEN :debut AND :fin")
    List<Facture> findImpayeesParPeriode(@Param("walletCode") String walletCode,
                                         @Param("debut") LocalDate debut,
                                         @Param("fin") LocalDate fin);

    // Factures impayees d'un wallet sur une periode, filtrees par unite
    @Query("SELECT f FROM Facture f WHERE f.walletCode = :walletCode " +
           "AND f.payee = false " +
           "AND f.unite = :unite " +
           "AND f.dateEmission BETWEEN :debut AND :fin")
    List<Facture> findImpayeesParPeriodeEtUnite(@Param("walletCode") String walletCode,
                                                @Param("unite") String unite,
                                                @Param("debut") LocalDate debut,
                                                @Param("fin") LocalDate fin);
}
