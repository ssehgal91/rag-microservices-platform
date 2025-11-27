package com.rag.api.gateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Set;

// API Gateway (WebFlux)
// ✔ Validate external client using X-API-KEY
// ✔ Add X-INTERNAL-KEY for downstream services
@Component
public class ApiKeyFilter implements GlobalFilter, Ordered {

    @Value("${GATEWAY_API_KEY:}")
    private String gatewayApiKey;

    @Value("${APP_INTERNAL_SERVICE_KEY}")
    private String internalServiceKey;

    private static final Set<String> EXCLUDED_PREFIXES = Set.of(
            "/actuator",
            "/v3/api-docs",
            "/swagger-ui",
            "/swagger-ui.html"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        // Skip API key validation for public endpoints
        for (String prefix : EXCLUDED_PREFIXES) {
            if (path.startsWith(prefix)) {
                return chain.filter(exchange);
            }
        }

        // Validate external API KEY
        String providedKey = exchange.getRequest().getHeaders().getFirst("X-API-KEY");

        if (gatewayApiKey != null && !gatewayApiKey.isBlank()) {
            if (providedKey == null || !providedKey.equals(gatewayApiKey)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        }

        // ***** ADD INTERNAL KEY TO DOWNSTREAM MICROSERVICE *****
        exchange = exchange.mutate()
                .request(req -> req.headers(headers ->
                        headers.add("X-INTERNAL-KEY", internalServiceKey)))
                .build();

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -100;
    }
}