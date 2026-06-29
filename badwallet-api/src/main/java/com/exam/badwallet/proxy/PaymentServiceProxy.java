package com.exam.badwallet.proxy;

import com.exam.badwallet.dto.PayRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pattern PROXY (Remote Proxy).
 * Implementation concrete de FacturationService : elle delegue chaque appel
 * au service externe payment-service (port 8081) via HTTP, tout en exposant
 * a badwallet-api une interface locale simple.
 */
@Component
public class PaymentServiceProxy implements FacturationService {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public PaymentServiceProxy(RestTemplate restTemplate,
                               @Value("${payment-service.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @Override
    public List<FactureExterne> facturesMoisEnCours(String walletCode, String unite) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/api/factures/" + walletCode + "/current");
        if (unite != null && !unite.isBlank()) {
            builder.queryParam("unite", unite);
        }
        ResponseEntity<List<FactureExterne>> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<FactureExterne>>() {});
        return response.getBody() == null ? Collections.emptyList() : response.getBody();
    }

    @Override
    public List<FactureExterne> facturesParPeriode(String walletCode, LocalDate debut, LocalDate fin) {
        String url = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/api/factures/" + walletCode + "/periode")
                .queryParam("debut", debut)
                .queryParam("fin", fin)
                .toUriString();
        ResponseEntity<List<FactureExterne>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<FactureExterne>>() {});
        return response.getBody() == null ? Collections.emptyList() : response.getBody();
    }

    @Override
    public PaiementExterneResponse payer(String walletCode, String unite, List<String> references) {
        Map<String, Object> body = new HashMap<>();
        body.put("walletCode", walletCode);
        body.put("unite", unite);
        body.put("factureReferences", references);

        return restTemplate.postForObject(
                baseUrl + "/api/factures/pay",
                body,
                PaiementExterneResponse.class);
    }
}
