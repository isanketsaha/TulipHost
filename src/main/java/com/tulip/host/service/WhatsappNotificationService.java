package com.tulip.host.service;

import com.tulip.host.config.ApplicationProperties;
import com.whatsapp.api.WhatsappApiFactory;
import com.whatsapp.api.domain.messages.Message;
import com.whatsapp.api.domain.messages.TextMessage;
import com.whatsapp.api.impl.WhatsappBusinessCloudApi;
import com.whatsapp.api.utils.Formatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WhatsappNotificationService {

    private final ApplicationProperties properties;

    private WhatsappBusinessCloudApi getWhatsappCloudApi() {
        WhatsappApiFactory factory = WhatsappApiFactory.newInstance(properties.getWhatsapp().getKey());
        return factory.newBusinessCloudApi();
    }

    Message buildMessage(String contactNumber) {
        return Message.MessageBuilder
            .builder() //
            .setTo(contactNumber) //
            .buildTextMessage(
                new TextMessage() //
                    .setBody(Formatter.bold("Hey Sanket!") + "\nSome code here: \n" + Formatter.code("hello world code here"))
                    .setPreviewUrl(false)
            );
    }

    public void send() {
        getWhatsappCloudApi().sendMessage("113753028497604", buildMessage("918886860461"));
    }
}
