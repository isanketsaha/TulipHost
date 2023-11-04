package com.tulip.host.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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

    @Data
    public static class Page {

        private int size;
    }

    @Data
    public static class Whatsapp {

        private String key;
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
        }

        @Data
        public static class Region {

            private String value;
        }
    }
}
