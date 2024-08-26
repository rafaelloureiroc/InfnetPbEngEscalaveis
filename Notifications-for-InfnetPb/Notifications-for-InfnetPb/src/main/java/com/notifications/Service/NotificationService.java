package com.notifications.Service;

import com.notifications.Model.Notification;
import com.notifications.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private NotificationRepository notificationRepository;

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);

        Notification notification = new Notification();
        notification.setRecipient(to);
        notification.setSubject(subject);
        notification.setBody(body);
        notificationRepository.save(notification);

    }
}