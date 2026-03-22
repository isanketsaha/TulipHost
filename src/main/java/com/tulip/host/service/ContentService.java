package com.tulip.host.service;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tulip.host.data.AboutContentDTO;
import com.tulip.host.data.ActivityItemDTO;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Serves dynamic content sections (About image, Activities) sourced from S3.
 *
 * S3 layout:
 *   content/about/photo.jpg          – fixed key; overwrite to update the about image
 *   content/activities/manifest.json – list of activity items (see format below)
 *
 * Activities manifest format:
 * {
 *   "items": [
 *     { "key": "sports-day.jpg", "title": "...", "description": "...", "date": "2025-03-10", "active": true }
 *   ]
 * }
 * All fields except "key" are optional. active=false hides an item without deleting it.
 *
 * Results are cached in-memory for 30 minutes.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ContentService {

    private static final String ABOUT_IMAGE_KEY = "content/about/photo.jpg";
    private static final String ACTIVITIES_MANIFEST_KEY = "content/activities/manifest.json";
    private static final String ACTIVITIES_PREFIX = "content/activities/";
    private static final long CACHE_TTL_MS = 30 * 60 * 1000L;

    private final ObjectStorageService storageService;
    private final ObjectMapper objectMapper;

    // About image cache
    private volatile String aboutImageUrlCache = null;
    private volatile long aboutCacheExpiry = 0;
    private volatile boolean aboutImageExists = false;

    // Activities cache
    private volatile List<ActivityItemDTO> activitiesCache = null;
    private volatile long activitiesCacheExpiry = 0;

    public AboutContentDTO getAboutContent() {
        if (aboutCacheExpiry > System.currentTimeMillis()) {
            return AboutContentDTO.builder().imageUrl(aboutImageUrlCache).build();
        }
        return refreshAbout();
    }

    public List<ActivityItemDTO> getActivities() {
        if (activitiesCache != null && System.currentTimeMillis() < activitiesCacheExpiry) {
            return activitiesCache;
        }
        return refreshActivities();
    }

    private AboutContentDTO refreshAbout() {
        try {
            String url = storageService.createURL(ABOUT_IMAGE_KEY).toString();
            aboutImageUrlCache = url;
            aboutImageExists = true;
        } catch (AmazonS3Exception e) {
            // Key does not exist yet — return null gracefully
            log.info("About image not found at S3 key: {}", ABOUT_IMAGE_KEY);
            aboutImageUrlCache = null;
        } catch (Exception e) {
            log.error("Failed to load about image URL from S3", e);
            aboutImageUrlCache = null;
        }
        aboutCacheExpiry = System.currentTimeMillis() + CACHE_TTL_MS;
        return AboutContentDTO.builder().imageUrl(aboutImageUrlCache).build();
    }

    private List<ActivityItemDTO> refreshActivities() {
        try {
            byte[] bytes = storageService.downloadObject(ACTIVITIES_MANIFEST_KEY);
            Map<?, ?> manifest = objectMapper.readValue(bytes, Map.class);
            List<?> rawItems = (List<?>) manifest.get("items");
            if (rawItems == null) {
                activitiesCache = Collections.emptyList();
            } else {
                activitiesCache = rawItems
                    .stream()
                    .map(item -> (Map<String, Object>) item)
                    .filter(item -> !Boolean.FALSE.equals(item.get("active")))
                    .sorted(Comparator.comparing(item -> (String) item.getOrDefault("date", ""), Comparator.reverseOrder()))
                    .map(item -> {
                        String key = (String) item.get("key");
                        String imageUrl = null;
                        if (key != null) {
                            try {
                                imageUrl = storageService.createURL(ACTIVITIES_PREFIX + key).toString();
                            } catch (Exception e) {
                                log.warn("Could not generate URL for activity image key: {}", key);
                            }
                        }
                        return ActivityItemDTO.builder()
                            .imageUrl(imageUrl)
                            .title((String) item.get("title"))
                            .description((String) item.get("description"))
                            .date((String) item.get("date"))
                            .build();
                    })
                    .toList();
            }
        } catch (Exception e) {
            log.warn("Failed to load activities manifest from S3, returning cached or empty list", e);
            if (activitiesCache == null) activitiesCache = Collections.emptyList();
        }
        activitiesCacheExpiry = System.currentTimeMillis() + CACHE_TTL_MS;
        return activitiesCache;
    }
}
