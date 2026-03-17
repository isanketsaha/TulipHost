package com.tulip.host.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "msg-gateway", url = "${feign.client.config.msg-gateway.url}", configuration = FeignClientConfiguration.class)
public interface MessageCommunication {
    @PostMapping("/flow")
    String sendSMS(@RequestBody SmsRequest request);

    @PostMapping("/whatsapp/whatsapp-outbound-message/bulk/")
    String sendWhatsApp(@RequestBody WhatsAppBulkRequest request);

    @GetMapping("/report/analytics/p/sms")
    JsonNode getReport();
}
