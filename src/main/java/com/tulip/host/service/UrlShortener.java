package com.tulip.host.service;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.tulip.host.config.ApplicationProperties;

import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlShortener {

    private final RestTemplate restTemplate;
    private final ApplicationProperties applicationProperties;
    private static final String API_URL = "https://api.tinyurl.com/create?api_token=";

    public String shortenUrl(String longUrl, String description) {
        Map<String, Object> payload = Map.of("url", longUrl, "domain",
                "tinyurl.com", "description", description);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        Map<String, Object> response = restTemplate.postForObject(
                API_URL + applicationProperties.getTinyurl().getKey(),
                request, Map.class);
        if (response != null && response.containsKey("data")) {
            Map<String, Object> data = (Map<String, Object>) response.get("data");
            String tinyUrl = (String) data.get("tiny_url");
            log.info("Shortened URL: {} to {}", longUrl, tinyUrl);
            return tinyUrl;
        }

        return null;
    }
}
