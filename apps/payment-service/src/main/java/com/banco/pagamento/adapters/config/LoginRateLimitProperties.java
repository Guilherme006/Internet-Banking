package com.banco.pagamento.adapters.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.rate-limit.login")
public record LoginRateLimitProperties(
    boolean enabled,
    int maxAttempts,
    long windowSeconds,
    long blockSeconds
) {
}
