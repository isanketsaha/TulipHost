package com.tulip.host.service.communication;

import org.springframework.stereotype.Component;

import com.tulip.host.enums.CommunicationChannel;

@Component
public class WhatsAppCommunicationStrategy implements CommunicationStrategy {

    @Override
    public CommunicationChannel channel() {
        return CommunicationChannel.WHATSAPP;
    }

    @Override
    public void send(CommunicationRequest request) {
        throw new UnsupportedOperationException("WhatsApp sending not implemented yet");
    }
}
