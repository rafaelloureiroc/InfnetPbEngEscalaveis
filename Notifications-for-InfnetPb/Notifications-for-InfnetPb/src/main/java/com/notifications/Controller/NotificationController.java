package com.notifications.Controller;

import com.notifications.Service.NotificationService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/sendEmail")
    public void sendEmail(@RequestBody EmailRequest emailRequest) {
        notificationService.sendEmail(emailRequest.getTo(), emailRequest.getSubject(), emailRequest.getBody());
    }
    @Data
    public static class EmailRequest {
        private String to;
        private String subject;
        private String body;

    }
}