package com.tulip.host.client;

import com.tulip.host.config.FeignAuthProperties;
import feign.Logger;
import feign.RequestInterceptor;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Feign clients.
 * Configures logging level and adds request/response interceptors.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class FeignClientConfiguration {

    private final FeignAuthProperties feignAuthProperties;

    /**
     * Set Feign logging level to FULL for debugging.
     * Log levels: NONE, BASIC, HEADERS, FULL
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    /**
     * Configure request interceptor to add authentication headers based on client name.
     */
    @Bean
    public RequestInterceptor feignRequestInterceptor() {
        return requestTemplate -> {
            String clientName = requestTemplate.feignTarget().name();
            Map<String, String> tokens = feignAuthProperties.getTokens();

            // Look up token by client name (case-insensitive)
            String token = tokens
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(clientName))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);

            if (token != null && !token.isEmpty()) {
                requestTemplate.header(clientName.equals("msg-gateway") ? "authkey" : "Authorization", token);
                log.debug("Added Authorization header for {} service", clientName);
            } else {
                log.warn("Authentication token not configured for client: {}. Service may fail.", clientName);
            }

            // Add common headers
            if (!requestTemplate.headers().containsKey("Accept")) {
                requestTemplate.header("Accept", "application/json");
            }
            if (!requestTemplate.headers().containsKey("Content-Type")) {
                requestTemplate.header("Content-Type", "application/json");
            }

            log.info(
                "Feign request prepared - Client: {}, URL: {}, Method: {}",
                clientName,
                requestTemplate.url(),
                requestTemplate.method()
            );
        };
    }
}
