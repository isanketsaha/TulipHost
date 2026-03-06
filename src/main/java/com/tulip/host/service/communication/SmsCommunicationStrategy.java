package com.tulip.host.service.communication;

import static com.tulip.host.config.Constants.CALLING_CODE;
import static com.tulip.host.utils.CommonUtils.isProdProfile;

import com.fasterxml.jackson.databind.JsonNode;
import com.tulip.host.client.MessageCommunication;
import com.tulip.host.client.SmsRequest;
import com.tulip.host.config.ApplicationProperties;
import com.tulip.host.domain.OutboundCommunication;
import com.tulip.host.enums.CommunicationChannel;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsCommunicationStrategy implements CommunicationStrategy {

    private final ApplicationProperties properties;
    private final MessageCommunication messageCommunication;
    private final Environment env;

    @Override
    public CommunicationChannel channel() {
        return CommunicationChannel.SMS;
    }

    @Override
    public void send(CommunicationRequest request, OutboundCommunication outboundCommunication) {
        validateRequest(request);

        List<Map<String, String>> recipients = request.getSmsRecipient();
        String templateId = request.getContent();

        if (!isProdProfile(env.getActiveProfiles())) {
            String defaultPhone = CALLING_CODE + properties.getTwilioConfig().getDefaultPhone();
            if (StringUtils.isNotBlank(defaultPhone)) {
                recipients.stream().forEach(item -> item.put("mobiles", defaultPhone));
                log.debug("Non-prod environment: Overriding SMS recipient to default phone: {}", defaultPhone);
            } else {
                log.warn("Non-prod environment: defaultPhone not configured, using original recipients");
            }
        }
        try {
            SmsRequest smsRequest = SmsRequest.builder().recipients(recipients).shortUrl(0).templateId(templateId).build();
            log.info("Sending SMS to {} recipients with template ID: {}", recipients.size(), templateId);
            String sid = messageCommunication.sendSMS(smsRequest);
            if (StringUtils.isNotBlank(sid)) {
                outboundCommunication.setProviderMessageId(sid);
                log.info("SMS sent successfully. SID: {}, Recipient: {}", sid, outboundCommunication.getRecipient());
            } else {
                log.warn("SMS API returned empty SID for recipient: {}", outboundCommunication.getRecipient());
            }
        } catch (Exception e) {
            log.error(
                "Failed to send SMS. Recipient: {}, Template ID: {}, Error: {}",
                outboundCommunication.getRecipient(),
                templateId,
                e.getMessage(),
                e
            );
            throw new RuntimeException("Failed to send SMS: " + e.getMessage(), e);
        }
    }

    private void validateRequest(CommunicationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("CommunicationRequest cannot be null");
        }

        if (request.getSmsRecipient() == null || request.getSmsRecipient().isEmpty()) {
            throw new IllegalArgumentException("SMS recipient list cannot be null or empty");
        }

        if (StringUtils.isBlank(request.getContent())) {
            throw new IllegalArgumentException("SMS template ID (content) cannot be null or empty");
        }
    }
}
