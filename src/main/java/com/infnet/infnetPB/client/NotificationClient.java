package com.infnet.infnetPB.client;

import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service", url = "http://localhost:8081")
public interface NotificationClient {

    @PostMapping("/sendEmail")
    void sendNotification(@RequestBody NotificationRequest request);

    @Data
    class NotificationRequest {
        private String to;
        private String subject;
        private String body;

    }
}