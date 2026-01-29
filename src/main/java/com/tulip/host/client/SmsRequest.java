package com.tulip.host.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SmsRequest {

    @JsonProperty("template_id")
    private String templateId;

    @JsonProperty("short_url")
    private Integer shortUrl; // 1 or 0

    @JsonProperty("short_url_expiry")
    private Integer shortUrlExpiry; // seconds (optional)

    @JsonProperty("realTimeResponse")
    private Integer realTimeResponse; // 1 (optional)

    private HashMap<String, String> recipients;
}
