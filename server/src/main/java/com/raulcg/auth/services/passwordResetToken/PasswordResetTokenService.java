package com.raulcg.auth.services.passwordResetToken;

import com.raulcg.auth.exceptions.PasswordResetTokenNotFoundException;
import com.raulcg.auth.exceptions.UserNotFoundException;
import com.raulcg.auth.models.PasswordResetToken;
import com.raulcg.auth.models.User;
import com.raulcg.auth.repositories.PasswordResetTokenRepository;
import com.raulcg.auth.repositories.UserRepository;
import com.raulcg.auth.services.email.IEmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class PasswordResetTokenService implements IPasswordResetTokenService {

    private final PasswordEncoder passwordEncoder;
    @Value("${frontend.url}")
    private String frontendUrl;

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final IEmailService emailService;

    public PasswordResetTokenService(PasswordResetTokenRepository passwordResetTokenRepository, UserRepository userRepository, IEmailService emailService, PasswordEncoder passwordEncoder) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
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

    @Override
    public void resetPassword(String token, String password) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new PasswordResetTokenNotFoundException("Invalid token"));

        if (resetToken.isUsed() || resetToken.getExpiryDate().isBefore(Instant.now())) {
            throw new PasswordResetTokenNotFoundException("Invalid token");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
        emailService.sendPasswordChangedEmail(user.getEmail(), user.getUsername());

    }
}
