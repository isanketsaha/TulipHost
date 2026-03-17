package com.tulip.host.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

/**
 * Logs every incoming API request: method, URI, client IP, and query string.
 * Set logging.level.org.springframework.web.filter.CommonsRequestLoggingFilter=DEBUG
 * in application.yml to activate (safe to leave enabled — logs at DEBUG only).
 */
@Configuration
public class RequestLoggingConfiguration {

    @Bean
    CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeClientInfo(true);
        filter.setIncludeQueryString(true);
        filter.setIncludeHeaders(false); // avoid leaking auth tokens in logs
        filter.setIncludePayload(false); // avoid logging sensitive request bodies
        filter.setMaxPayloadLength(1000);
        filter.setAfterMessagePrefix("COMPLETED ");
        return filter;
    }
}
