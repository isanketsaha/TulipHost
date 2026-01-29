package com.tulip.host.service.communication;

import static com.tulip.host.utils.CommonUtils.isProdProfile;

import com.tulip.host.client.MessageCommunication;
import com.tulip.host.client.SmsRequest;
import com.tulip.host.config.ApplicationProperties;
import com.tulip.host.domain.OutboundCommunication;
import com.tulip.host.enums.CommunicationChannel;
import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmsCommunicationStrategy implements CommunicationStrategy {

    public static final int COUNTRY_CODE = +91;
    private final ApplicationProperties properties;

    private MessageCommunication messageCommunication;
    private final Environment env;

    @PostConstruct
    void init() {}

    @Override
    public CommunicationChannel channel() {
        return CommunicationChannel.SMS;
    }

    @Override
    public void send(CommunicationRequest request, OutboundCommunication outboundCommunication) {
        boolean isProd = isProdProfile(env.getActiveProfiles());
        String sid = Arrays.stream(request.getRecipient())
            .map(to -> {
                //                Message message = Message.creator(
                //                        new PhoneNumber(COUNTRY_CODE + (isProd ? to : properties.getTwilioConfig()
                //                            .getDefaultPhone())), properties.getTwilioConfig()
                //                            .getMessageSid(), request.getContent())
                //                    .create();
                //                System.out.println(message.getSid());
                //                outboundCommunication.setProviderMessageId(message.getSid());
                //                return message.getSid();
                return "SMxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
            })
            .collect(Collectors.joining(", "));
        outboundCommunication.setProviderMessageId(sid);
    }
}
