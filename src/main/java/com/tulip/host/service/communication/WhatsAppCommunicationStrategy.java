package com.tulip.host.service.communication;

import static com.tulip.host.utils.CommonUtils.isProdProfile;

import com.tulip.host.client.MessageCommunication;
import com.tulip.host.client.WhatsAppBulkRequest;
import com.tulip.host.config.ApplicationProperties;
import com.tulip.host.domain.OutboundCommunication;
import com.tulip.host.enums.CommunicationChannel;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Sends WhatsApp messages via MSG91's bulk template API, reusing the shared
 * {@code msg-gateway} Feign client. Authentication ({@code authkey} header) is
 * injected automatically by {@link com.tulip.host.client.FeignClientConfiguration}.
 *
 * <p>In non-production profiles the message is redirected to the configured
 * {@code default_phone}, so no real recipient is hit during development.
 *
 * <h3>Caller example — payment confirmation with PDF receipt</h3>
 * <pre>{@code
 * outboundCommunicationService.send(
 *     CommunicationRequest.builder()
 *         .channel(CommunicationChannel.WHATSAPP)
 *         .whatsAppRecipient(List.of(student.getPhoneNumber()))
 *         .templateId("payment_confirmation")
 *         .templateVariables(Map.of(
 *             "name",         student.getName(),
 *             "amount",       "5000",
 *             "payment_type", "Fee",
 *             "payment_mode", "UPI"
 *         ))
 *         .documentUrl(invoicePublicUrl)       // omit when template has no document header
 *         .documentFilename("Invoice_Jan.pdf")
 *         .entityType("PAYMENT")
 *         .entityId(transaction.getId())
 *         .build()
 * );
 * }</pre>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WhatsAppCommunicationStrategy implements CommunicationStrategy {

    private final MessageCommunication messageCommunication;
    private final ApplicationProperties properties;
    private final Environment env;

    @Override
    public CommunicationChannel channel() {
        return CommunicationChannel.WHATSAPP;
    }

    @Override
    public void send(CommunicationRequest request, OutboundCommunication outboundCommunication) {
        WhatsAppBulkRequest bulkRequest = WhatsAppBulkRequest.builder()
            .integratedNumber(properties.getWhatsapp().getIntegratedNumber())
            .payload(
                WhatsAppBulkRequest.Payload.builder()
                    .template(
                        WhatsAppBulkRequest.Template.builder()
                            .name(request.getTemplateId())
                            .namespace(properties.getWhatsapp().getNamespace())
                            .language(WhatsAppBulkRequest.Language.builder().build())
                            .toAndComponents(
                                List.of(
                                    WhatsAppBulkRequest.Recipient.builder()
                                        .to(resolveRecipients(request))
                                        .components(buildComponents(request))
                                        .build()
                                )
                            )
                            .build()
                    )
                    .build()
            )
            .build();

        log.info("Dispatching WhatsApp template='{}' to {} recipient(s)", request.getTemplateId(), request.getWhatsAppRecipient().size());

        String response = messageCommunication.sendWhatsApp(bulkRequest);
        log.info("MSG91 WhatsApp response: {}", response);
        outboundCommunication.setProviderMessageId(response);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    /**
     * Production → use actual recipients from the request.
     * Non-production → redirect to the safe default phone so no real student is messaged.
     */
    private List<String> resolveRecipients(CommunicationRequest request) {
        if (isProdProfile(env.getActiveProfiles())) {
            return request.getWhatsAppRecipient();
        }
        String safePhone = properties.getTwilioConfig().getDefaultPhone();
        log.debug("Non-prod profile — redirecting WhatsApp to default phone: {}", safePhone);
        return List.of(safePhone);
    }

    /**
     * Assembles the MSG91 component map from the request.
     *
     * <p>Ordering: document header first (if present), then text body variables.
     * Keys follow MSG91 naming conventions:
     * <ul>
     *   <li>{@code "header_1"} — document component</li>
     *   <li>{@code "body_<paramName>"} — each text variable</li>
     * </ul>
     */
    private Map<String, WhatsAppBulkRequest.Component> buildComponents(CommunicationRequest request) {
        Map<String, WhatsAppBulkRequest.Component> components = new LinkedHashMap<>();

        if (StringUtils.isNotBlank(request.getDocumentUrl())) {
            components.put("header_1", WhatsAppBulkRequest.Component.document(request.getDocumentFilename(), request.getDocumentUrl()));
        }

        if (request.getTemplateVariables() != null) {
            request
                .getTemplateVariables()
                .forEach((paramName, value) -> components.put("body_" + paramName, WhatsAppBulkRequest.Component.text(paramName, value)));
        }

        return components;
    }
}
