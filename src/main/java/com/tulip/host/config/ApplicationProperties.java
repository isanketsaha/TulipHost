package com.tulip.host.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Tulip Host.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
@Data
public class ApplicationProperties {

    public final Page page = new Page();
    public final Aws aws = new Aws();
    public final Whatsapp whatsapp = new Whatsapp();
    public final TwilioConfig twilioConfig = new TwilioConfig();
    public final TinyUrl tinyurl = new TinyUrl();
    private String staticPath;

    // Logging performance thresholds
    private long slowMethodThresholdMs = 2000;
    private long slowQueryThresholdMs = 1000;

    @Data
    public static class Page {

        private int size;
    }

    @Data
    public static class Whatsapp {

        /** MSG91 authkey — kept for reference; auth is injected via {@code FeignAuthProperties}. */
        private String key;

        /** WhatsApp-registered sender number in E.164 format (e.g. {@code 919059635061}). */
        private String integratedNumber;

        /** MSG91 namespace GUID that scopes the approved template library. */
        private String namespace;
    }

    @Data
    public static class Aws {

        public final Credential credential = new Credential();
        public final Region region = new Region();

        @Data
        public static class Credential {

            private String accessKey;
            private String secret;
            private String profileName;
            private String bucketName;
            private String invoiceBucketName;
        }

        @Data
        public static class Region {

            private String value;
        }
    }

    @Data
    public static class TwilioConfig {

        private String accountSid;
        private String key;
        private String messageSid;
        private String defaultEmail;
        private String defaultPhone;
        private boolean communicationEnabled = false;
    }

    @Data
    public static class TinyUrl {

        private String key;
    }
}
