package com.raulcg.auth.services.email;

import jakarta.mail.MessagingException;

public interface IEmailService {
    void sendConfirmationEmail(String to, String userName, String authCode) throws MessagingException;

    void sendPasswordResetEmail(String to, String userName, String resetUrl) throws MessagingException;
}
