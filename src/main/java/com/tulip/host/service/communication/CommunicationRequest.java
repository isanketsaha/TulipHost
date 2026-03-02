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

    String[] mailRecipient;
    List<Map<String, String>> smsRecipient;

    @Builder.Default
    String[] cc = new String[] {};

    String subject;
    String content;
    String entityType;
    Long entityId;
    List<MailService.EmailAttachment> attachments;
}
