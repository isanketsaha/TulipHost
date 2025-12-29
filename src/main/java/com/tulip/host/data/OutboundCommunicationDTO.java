package com.tulip.host.data;

import java.time.LocalDateTime;

import com.tulip.host.enums.CommunicationChannel;
import com.tulip.host.enums.OutboundCommunicationStatus;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OutboundCommunicationDTO {
    Long id;
    CommunicationChannel channel;
    String recipient;
    String entityType;
    Long entityId;
    String subject;
    String content;
    OutboundCommunicationStatus status;
    String error;
    String providerMessageId;
    LocalDateTime sentDate;
    String createdBy;
    LocalDateTime createdDate;
    String lastModifiedBy;
    LocalDateTime lastModifiedDate;
}
