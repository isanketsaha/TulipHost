package com.tulip.host.service.communication;

import com.tulip.host.enums.CommunicationChannel;
import com.tulip.host.service.MailService;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

@Builder
@Data
public class CommunicationRequest {

    @Builder.Default
    CommunicationChannel channel = CommunicationChannel.EMAIL;

    // ─── Email ───────────────────────────────────────────────────────────────

    String[] mailRecipient;

    @Builder.Default
    String[] cc = new String[] {};

    String subject;
    String content;
    List<MailService.EmailAttachment> attachments;

    // ─── SMS ─────────────────────────────────────────────────────────────────

    /** Each entry is a variable map for one recipient; must include a {@code mobiles} key. */
    List<Map<String, String>> smsRecipient;

    // ─── WhatsApp ────────────────────────────────────────────────────────────

    /** Destination phone numbers in E.164 format (e.g. {@code "919876543210"}). */
    List<String> whatsAppRecipient;

    /** MSG91 template name (e.g. {@code "payment_confirmation"}). */
    String templateId;

    /**
     * Template body variable values keyed by MSG91 parameter name.
     * Example: {@code {"name" -> "John", "amount" -> "5000"}}
     */
    Map<String, String> templateVariables;

    /**
     * Public URL of a document (PDF / image) to send as the template header.
     * Required when the MSG91 template defines a {@code header_1} document component.
     */
    String documentUrl;

    /** Display filename for the header document (e.g. {@code "Invoice_Jan.pdf"}). */
    String documentFilename;

    // ─── Shared ──────────────────────────────────────────────────────────────

    String entityType;
    Long entityId;
}
