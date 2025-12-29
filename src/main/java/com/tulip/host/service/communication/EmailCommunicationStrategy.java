package com.tulip.host.service.communication;

import com.tulip.host.config.ApplicationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.tulip.host.enums.CommunicationChannel;
import com.tulip.host.service.MailService;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static com.tulip.host.utils.CommonUtils.isDevProfile;

@Component
@RequiredArgsConstructor
public class EmailCommunicationStrategy implements CommunicationStrategy {

    private final MailService mailService;

    private final Environment env;
    private final ApplicationProperties properties;

    @Override
    public CommunicationChannel channel() {
        return CommunicationChannel.EMAIL;
    }

    @Override
    public void send(CommunicationRequest request) {
        if (isDevProfile(env.getDefaultProfiles())) {
            request.setRecipient(new String[]{properties.getTwilioConfig().getDefaultEmail()});
            request.setCc(new String[]{});
        }
        if (request.getCc().length > 0 || request.getRecipient().length > 0)
            mailService.sendEmail(request.getRecipient(), request.getCc(), request.getSubject(),
                request.getContent(), false, true, request.getAttachments());
    }
}
