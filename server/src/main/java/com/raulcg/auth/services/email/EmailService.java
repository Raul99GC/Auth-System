package com.raulcg.auth.services.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService implements IEmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine thymeleafEngine;

    public EmailService(JavaMailSender mailSender, TemplateEngine thymeleafEngine) {
        this.mailSender = mailSender;
        this.thymeleafEngine = thymeleafEngine;
    }

    @Override
    public void sendConfirmationEmail(String to, String userName, String authCode) throws MessagingException {
        Context context = new Context();
        context.setVariable("userName", userName);
        context.setVariable("authCode", authCode);
        String emailContent = thymeleafEngine.process("account-confirmation-email", context);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject("Confirm your email address");
        helper.setText(emailContent, true);
        mailSender.send(message);
    }
}
