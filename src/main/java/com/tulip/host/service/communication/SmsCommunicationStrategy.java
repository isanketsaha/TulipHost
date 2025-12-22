package com.tulip.host.service.communication;

import org.springframework.stereotype.Component;

import com.tulip.host.enums.CommunicationChannel;

@Component
public class SmsCommunicationStrategy implements CommunicationStrategy {

    @Override
    public CommunicationChannel channel() {
        return CommunicationChannel.SMS;
    }

    @Override
    public void send(CommunicationRequest request) {
        throw new UnsupportedOperationException("SMS sending not implemented yet");
    }
}
