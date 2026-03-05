package com.tulip.host.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for the MSG91 WhatsApp bulk outbound message API.
 *
 * <p>Supports template-based messaging with:
 * <ul>
 *   <li>Text body variables — resolved at send time via {@link Component#text}</li>
 *   <li>Document headers   — a PDF/media URL attached via {@link Component#document}</li>
 * </ul>
 *
 * @see <a href="https://api.msg91.com/api/v5/whatsapp/whatsapp-outbound-message/bulk/">MSG91 API docs</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WhatsAppBulkRequest {

    /** WhatsApp-registered sender number in E.164 format (e.g. {@code 919059635061}). */
    @JsonProperty("integrated_number")
    private String integratedNumber;

    @JsonProperty("content_type")
    @Builder.Default
    private String contentType = "template";

    private Payload payload;

    // ─── Nested types ────────────────────────────────────────────────────────

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Payload {

        @JsonProperty("messaging_product")
        @Builder.Default
        private String messagingProduct = "whatsapp";

        @Builder.Default
        private String type = "template";

        private Template template;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Template {

        /** MSG91 template name (e.g. {@code "payment_confirmation"}). */
        private String name;

        private Language language;

        /** MSG91 namespace GUID scoping the approved template library. */
        private String namespace;

        @JsonProperty("to_and_components")
        private List<Recipient> toAndComponents;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Language {

        @Builder.Default
        private String code = "en";

        @Builder.Default
        private String policy = "deterministic";
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Recipient {

        /** Destination phone numbers in E.164 format (e.g. {@code "919876543210"}). */
        private List<String> to;

        /**
         * Template component values keyed by the MSG91 component name.
         * e.g. {@code "header_1"}, {@code "body_name"}, {@code "body_amount"}.
         */
        private Map<String, Component> components;
    }

    /**
     * A single template slot — either a resolved text variable or a document header.
     *
     * <p>Text:     {@code { "type": "text", "parameter_name": "name", "value": "John" }}
     * <p>Document: {@code { "type": "document", "filename": "invoice.pdf", "value": "https://..." }}
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Component {

        /** {@code "text"} or {@code "document"}. */
        private String type;

        /** Resolved text value or public document URL. */
        private String value;

        /** For text components: MSG91 parameter name matching the template placeholder. */
        @JsonProperty("parameter_name")
        private String parameterName;

        /** For document components: the display filename shown to the recipient. */
        private String filename;

        public static Component text(String parameterName, String value) {
            return Component.builder().type("text").parameterName(parameterName).value(value).build();
        }

        public static Component document(String filename, String url) {
            return Component.builder().type("document").filename(filename).value(url).build();
        }
    }
}
