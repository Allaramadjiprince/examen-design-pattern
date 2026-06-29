package com.exam.badwallet.proxy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaiementExterneResponse {
    private String walletCode;
    private String unite;
    private BigDecimal montantTotal;
    private List<String> referencesPayees;
    private String message;
}
