package com.tulip.host.config;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;
import org.springframework.util.unit.DataSize;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import tech.jhipster.config.JHipsterProperties;

/**
 * Configuration of web application with Servlet 3.0 APIs.
 */
@Configuration
public class WebConfigurer implements ServletContextInitializer {

    private final Logger log = LoggerFactory.getLogger(WebConfigurer.class);

    private final Environment env;

    private final JHipsterProperties jHipsterProperties;

    public WebConfigurer(Environment env, JHipsterProperties jHipsterProperties) {
        this.env = env;
        this.jHipsterProperties = jHipsterProperties;
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        if (env.getActiveProfiles().length != 0) {
            log.info("Web application configuration, using profiles: {}", (Object[]) env.getActiveProfiles());
        }
        log.info("Web application fully configured");
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = jHipsterProperties.getCors();
        if (!CollectionUtils.isEmpty(config.getAllowedOrigins()) || !CollectionUtils.isEmpty(config.getAllowedOriginPatterns())) {
            log.debug("Registering CORS filter");
            source.registerCorsConfiguration("/api/**", config);
            source.registerCorsConfiguration("/**", config);
            source.registerCorsConfiguration("/management/**", config);
            source.registerCorsConfiguration("/v3/api-docs", config);
            source.registerCorsConfiguration("/swagger-ui/**", config);
        }
        return new CorsFilter(source);
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofBytes(20000000L));
        factory.setMaxRequestSize(DataSize.ofBytes(20000000L));
        return factory.createMultipartConfig();
    }
}
