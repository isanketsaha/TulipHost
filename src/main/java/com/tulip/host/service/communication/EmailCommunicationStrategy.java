package com.tulip.host.service.communication;

import static com.tulip.host.utils.CommonUtils.isProdProfile;

import com.tulip.host.config.ApplicationProperties;
import com.tulip.host.domain.OutboundCommunication;
import com.tulip.host.enums.CommunicationChannel;
import com.tulip.host.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

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
    public void send(CommunicationRequest request, OutboundCommunication outboundCommunication) {
        if (!isProdProfile(env.getActiveProfiles())) {
            request.setMailRecipient(new String[] { properties.getTwilioConfig().getDefaultEmail() });
            request.setCc(new String[] {});
        }
        if (request.getCc().length > 0 || request.getMailRecipient().length > 0) mailService.sendEmail(
            request.getMailRecipient(),
            request.getCc(),
            request.getSubject(),
            request.getContent(),
            false,
            true,
            request.getAttachments()
        );
    }
}
