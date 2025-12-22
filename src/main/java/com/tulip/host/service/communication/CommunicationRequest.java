package com.tulip.host.service.communication;

import java.util.Map;

import com.tulip.host.enums.CommunicationChannel;

public record CommunicationRequest(
        CommunicationChannel channel,
        String[] recipient,
        String[] cc,
        String subject,
        String content,
        String entityType,
        Long entityId,
        Map<String, Object> metadata) {
}
