package ru.dovakun.services;

import jakarta.mail.*;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class MailService {

    @Value("${mail.username}")
    private String username;

    @Value("${mail.password}")
    private String password;
    @Async
    public void sendEmail(String recipientEmail, String subject, String body) {
        // Настройки SMTP
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.yandex.ru");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.yandex.ru");

        // Авторизация
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Создание письма
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setContent(body, "text/html; charset=UTF-8");

            Transport.send(message);

            System.out.println("Письмо успешно отправлено на " + recipientEmail);

        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка отправки письма: " + e.getMessage());
        }
    }
}
