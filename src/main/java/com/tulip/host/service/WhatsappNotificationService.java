package com.tulip.host.service;

import static java.util.stream.Collectors.toList;

import com.tulip.host.config.ApplicationProperties;
import com.whatsapp.api.WhatsappApiFactory;
import com.whatsapp.api.domain.messages.BodyComponent;
import com.whatsapp.api.domain.messages.Language;
import com.whatsapp.api.domain.messages.Message;
import com.whatsapp.api.domain.messages.TemplateMessage;
import com.whatsapp.api.domain.messages.TextParameter;
import com.whatsapp.api.domain.templates.type.LanguageType;
import com.whatsapp.api.impl.WhatsappBusinessCloudApi;
import java.util.Arrays;
import java.util.List;
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

    Message buildMessageWithTemplate(String contactNumber, String templateName, List<String> params) {
        return Message.MessageBuilder
            .builder()
            .setTo(contactNumber)
            .buildTemplateMessage(
                new TemplateMessage()
                    .setLanguage(new Language(LanguageType.EN_GB))
                    .setName(templateName)
                    .addComponent(new BodyComponent().setParameters(params.stream().map(TextParameter::new).collect(toList())))
            );
    }

    public void send() {
        getWhatsappCloudApi()
            .sendMessage(
                "113753028497604",
                buildMessageWithTemplate("918886860461", "paymentconfirmation", Arrays.asList("Sanket Saha", "March Fees"))
            );
    }
}
