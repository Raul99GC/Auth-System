package com.raulcg.auth.services.email;

public interface IEmailService {
    void sendConfirmationEmail(String to, String userName, String authCode);

    void sendPasswordResetEmail(String to, String userName, String resetUrl);

    void sendPasswordChangedEmail(String to, String username);
}
