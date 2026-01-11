package com.tulip.host.service.communication;

import com.tulip.host.domain.OutboundCommunication;
import org.springframework.stereotype.Component;

import com.tulip.host.enums.CommunicationChannel;

@Component
public class WhatsAppCommunicationStrategy implements CommunicationStrategy {

    @Override
    public CommunicationChannel channel() {
        return CommunicationChannel.WHATSAPP;
    }

    @Override
    public void send(CommunicationRequest request, OutboundCommunication outboundCommunication) {
        throw new UnsupportedOperationException("WhatsApp sending not implemented yet");
    }
}
