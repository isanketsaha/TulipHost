package com.tulip.host.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tulip.host.data.GalleryImageDTO;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Reads the carousel image manifest from S3 and returns pre-signed URLs.
 *
 * Manifest location: carousel/manifest.json in the docs bucket.
 * Manifest format:
 * {
 *   "images": [
 *     { "key": "school-front-2025.jpg", "title": "Our Campus", "active": true, "order": 1 }
 *   ]
 * }
 *
 * Results are cached in-memory for 30 minutes to avoid hitting S3 on every request.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GalleryService {

    private static final String CAROUSEL_PREFIX = "carousel/";
    private static final String MANIFEST_KEY = CAROUSEL_PREFIX + "manifest.json";
    private static final long CACHE_TTL_MS = 30 * 60 * 1000L;

    private final ObjectStorageService storageService;
    private final ObjectMapper objectMapper;

    private volatile List<GalleryImageDTO> cache = null;
    private volatile long cacheExpiry = 0;

    public List<GalleryImageDTO> getGalleryImages() {
        if (cache != null && System.currentTimeMillis() < cacheExpiry) {
            return cache;
        }
        return refresh();
    }

    /** Forces a cache refresh from S3. */
    public List<GalleryImageDTO> refresh() {
        try {
            byte[] bytes = storageService.downloadObject(MANIFEST_KEY);
            Map<?, ?> manifest = objectMapper.readValue(bytes, Map.class);
            List<?> rawImages = (List<?>) manifest.get("images");
            if (rawImages == null) {
                return Collections.emptyList();
            }

            List<GalleryImageDTO> result = rawImages
                .stream()
                .map(item -> (Map<String, Object>) item)
                .filter(item -> Boolean.TRUE.equals(item.get("active")))
                .sorted(Comparator.comparingInt(item -> (int) item.getOrDefault("order", 0)))
                .map(item -> {
                    String key = CAROUSEL_PREFIX + item.get("key");
                    String url = storageService.createURL(key).toString();
                    String title = (String) item.getOrDefault("title", "");
                    int order = (int) item.getOrDefault("order", 0);
                    return GalleryImageDTO.builder().url(url).title(title).order(order).build();
                })
                .toList();

            cache = result;
            cacheExpiry = System.currentTimeMillis() + CACHE_TTL_MS;
            log.info("Gallery cache refreshed: {} images loaded", result.size());
            return result;
        } catch (Exception e) {
            log.error("Failed to load gallery manifest from S3, returning cached or empty list", e);
            return cache != null ? cache : Collections.emptyList();
        }
    }
}
