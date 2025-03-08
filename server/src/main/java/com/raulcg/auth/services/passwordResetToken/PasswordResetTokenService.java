package com.raulcg.auth.services.passwordResetToken;

import com.raulcg.auth.exceptions.UserNotFoundException;
import com.raulcg.auth.models.PasswordResetToken;
import com.raulcg.auth.models.User;
import com.raulcg.auth.repositories.PasswordResetTokenRepository;
import com.raulcg.auth.repositories.UserRepository;
import com.raulcg.auth.services.email.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class PasswordResetTokenService implements IPasswordResetTokenService {

    @Value("${frontend.url}")
    private String frontendUrl;

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public PasswordResetTokenService(PasswordResetTokenRepository passwordResetTokenRepository, UserRepository userRepository, EmailService emailService) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Override
    public void generateRefreshToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        String token = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plus(2, ChronoUnit.HOURS);
        PasswordResetToken resetToken = new PasswordResetToken(token, expiryDate, user);

        String resetUrl = frontendUrl + "reset-password?token=" + token;

        emailService.sendPasswordResetEmail(user.getEmail(), user.getUsername(), resetUrl);
        passwordResetTokenRepository.save(resetToken);
    }
}
