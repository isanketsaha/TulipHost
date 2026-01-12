package com.tulip.host.service.communication;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import com.tulip.host.domain.OutboundCommunication;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.tulip.host.config.ApplicationProperties;
import com.tulip.host.enums.CommunicationChannel;
import com.twilio.Twilio;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import static com.tulip.host.utils.CommonUtils.isProdProfile;

@Service
@RequiredArgsConstructor
public class SmsCommunicationStrategy implements CommunicationStrategy {

    public static final int COUNTRY_CODE = +91;
    private final ApplicationProperties properties;

    private final Environment env;

    @PostConstruct
    void init() {
        Twilio.init(properties.getTwilioConfig()
            .getAccountSid(), properties.getTwilioConfig()
            .getKey());
    }

    @Override
    public CommunicationChannel channel() {
        return CommunicationChannel.SMS;
    }

    @Override
    public void send(CommunicationRequest request, OutboundCommunication outboundCommunication) {
        boolean isDev = !isProdProfile(env.getDefaultProfiles());
        String sid = Arrays.stream(request.getRecipient())
            .map(to -> {
                Message message = Message.creator(
                        new PhoneNumber(COUNTRY_CODE + (!isDev ? to : properties.getTwilioConfig()
                            .getDefaultPhone())), properties.getTwilioConfig()
                            .getMessageSid(), request.getContent())
                    .create();
                System.out.println(message.getSid());
                outboundCommunication.setProviderMessageId(message.getSid());
                return message.getSid();
            })
            .collect(Collectors.joining(", "));
        outboundCommunication.setProviderMessageId(sid);
    }
}
