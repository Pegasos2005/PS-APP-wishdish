package com.wishdish.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    @Value("${stripe.secret.key:}")
    private String secretKey;

    @PostConstruct
    public void init() {
        if (secretKey == null || secretKey.isBlank() || secretKey.startsWith("sk_test_REPLACE_ME")) {
            // Fail-fast en arranque: si no hay clave válida, /api/payments no funcionará.
            // Lanzamos un warning visible y dejamos Stripe sin inicializar (las llamadas fallarán claras).
            System.err.println("[StripeConfig] stripe.secret.key no configurada. " +
                    "Define la clave en application-local.properties y arranca con --spring.profiles.active=local.");
            return;
        }
        Stripe.apiKey = secretKey;
    }
}
