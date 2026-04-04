package com.tulip.host.service;

import com.tulip.host.config.ApplicationProperties;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlShortener {

    private final RestTemplate restTemplate;
    private final ApplicationProperties applicationProperties;
    private static final String API_URL = "https://api.tinyurl.com/create?api_token=";

    public String shortenUrl(String longUrl, String description) {
        try {
            Map<String, Object> payload = Map.of("url", longUrl, "domain", "tinyurl.com", "description", description);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(
                API_URL + applicationProperties.getTinyurl().getKey(),
                request,
                Map.class
            );
            if (response != null && response.containsKey("data")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                String tinyUrl = (String) data.get("tiny_url");
                log.info("Shortened URL: {} to {}", longUrl, tinyUrl);
                return tinyUrl;
            }
        } catch (Exception e) {
            log.error("Failed to shorten URL, falling back to original. url={}, error={}", longUrl, e.getMessage());
        }
        return longUrl;
    }
}
