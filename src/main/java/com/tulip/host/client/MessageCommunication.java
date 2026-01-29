package com.tulip.host.client;

import com.tulip.host.data.AttendanceResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "msg-gateway", url = "${feign.client.config.msg-gateway.url}", configuration = FeignClientConfiguration.class)
public interface MessageCommunication {
    @PostMapping("/flow")
    String sendSMS(@RequestParam SmsRequest request);
}
