package com.tulip.host.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private Integer shortUrlExpiry = 150;

    @JsonProperty("realTimeResponse")
    private Integer realTimeResponse;

    @JsonProperty("recipients")
    private List<Map<String, String>> recipients;
}
