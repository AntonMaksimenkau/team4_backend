package com.team4.testingsystem.handlers;

import com.team4.testingsystem.dto.NotificationDTO;
import com.team4.testingsystem.services.EmailNotificationTemplateResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@ExtendWith(MockitoExtension.class)
class EmailNotificationEventHandlerTest {
    @Mock
    private EmailNotificationTemplateResolver templateResolver;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailNotificationEventHandler notificationEventHandler;

    @Mock
    private NotificationDTO notificationDTO;

    @Mock
    private MimeMessage mimeMessage;

    private static final String EMAIL_TEXT = "raw email";
    private static final String USER_EMAIL = "some@e.mail";

    @Test
    void sendEmail() throws MessagingException {
        Mockito.when(notificationDTO.getUserEmail()).thenReturn(USER_EMAIL);
        Mockito.when(templateResolver.resolve(notificationDTO)).thenReturn(EMAIL_TEXT);
        Mockito.when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        try (MockedConstruction<MimeMessageHelper> mock = Mockito.mockConstruction(MimeMessageHelper.class)) {
            notificationEventHandler.onApplicationEvent(notificationDTO);

            MimeMessageHelper helper = mock.constructed().get(0);
            Mockito.verify(mailSender).send(mimeMessage);
            Mockito.verify(helper).setFrom("untitled.noreply@gmail.com");
            Mockito.verify(helper).setTo(USER_EMAIL);
            Mockito.verify(helper).setSubject("Untitled testing system");
            Mockito.verify(helper).setText(EMAIL_TEXT, true);
        }
    }
}
