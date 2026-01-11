package com.tulip.host.service.communication;

import com.tulip.host.domain.OutboundCommunication;
import com.tulip.host.enums.CommunicationChannel;

public interface CommunicationStrategy {
    CommunicationChannel channel();

    void send(CommunicationRequest request, OutboundCommunication outboundCommunication );
}
