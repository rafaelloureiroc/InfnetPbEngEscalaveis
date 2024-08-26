package com.notifications;

import com.notifications.Service.NotificationService;
import com.notifications.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class NotificationsForInfnetPbApplicationTests {

	@Mock
	private JavaMailSender mailSender;

	@Mock
	private NotificationRepository notificationRepository;

	@InjectMocks
	private NotificationService notificationService;

	@Test
	public void testSendEmail() {
		String to = "test@example.com";
		String subject = "Test Subject";
		String body = "Test Body";

		notificationService.sendEmail(to, subject, body);

		verify(mailSender, times(1)).send(any(SimpleMailMessage.class));

		verify(notificationRepository, times(1)).save(argThat(notification ->
				to.equals(notification.getRecipient()) &&
						subject.equals(notification.getSubject()) &&
						body.equals(notification.getBody())
		));
	}
}
