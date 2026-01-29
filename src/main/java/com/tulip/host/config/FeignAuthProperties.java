package com.tulip.host.config;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for Feign client authentication tokens by service.
 */
@Component
@ConfigurationProperties(prefix = "app.feign.auth")
public class FeignAuthProperties {

    private Map<String, String> tokens = new HashMap<>();

    public Map<String, String> getTokens() {
        return tokens;
    }

    public void setTokens(Map<String, String> tokens) {
        this.tokens = tokens;
    }

    public String getToken(String serviceName) {
        return tokens.get(serviceName);
    }

    public boolean hasToken(String serviceName) {
        return tokens.containsKey(serviceName);
    }
}
