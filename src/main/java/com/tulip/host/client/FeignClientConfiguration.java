package com.tulip.host.client;

import feign.Logger;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Feign clients.
 * Configures logging level and adds request/response interceptors.
 */
@Slf4j
@Configuration
public class FeignClientConfiguration {

    @Value("${app.feign.auth.tokens.eoffice:}")
    private String eofficeToken;

    /**
     * Set Feign logging level to FULL for debugging.
     * Log levels: NONE, BASIC, HEADERS, FULL
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    /**
     * Configure request interceptor to add authentication headers.
     */
    @Bean
    public RequestInterceptor feignRequestInterceptor() {
        return requestTemplate -> {
            // Add authentication header for eOffice service
            if (requestTemplate.url().contains("api.etimeoffice.com") || requestTemplate.url().contains("DownloadInOutPunchData")) {
                if (eofficeToken != null && !eofficeToken.isEmpty()) {
                    requestTemplate.header("Authorization", eofficeToken);
                    log.debug("Added Authorization header for eOffice service");
                } else {
                    log.warn("eOffice authentication token is not configured. Service may fail.");
                }
            }

            // Add common headers
            if (!requestTemplate.headers().containsKey("Accept")) {
                requestTemplate.header("Accept", "application/json");
            }
            if (!requestTemplate.headers().containsKey("Content-Type")) {
                requestTemplate.header("Content-Type", "application/json");
            }

            log.debug("Feign request prepared - URL: {}, Method: {}", requestTemplate.url(), requestTemplate.method());
        };
    }
}
