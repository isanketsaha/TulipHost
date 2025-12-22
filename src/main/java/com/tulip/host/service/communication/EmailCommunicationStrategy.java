package com.tulip.host.service.communication;

import org.springframework.stereotype.Component;

import com.tulip.host.enums.CommunicationChannel;
import com.tulip.host.service.MailService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailCommunicationStrategy implements CommunicationStrategy {

    private final MailService mailService;

    @Override
    public CommunicationChannel channel() {
        return CommunicationChannel.EMAIL;
    }

    @Override
    public void send(CommunicationRequest request) {
        mailService.sendEmail(request.recipient(), request.cc(), request.subject(), request.content(), false, true);
    }
}
