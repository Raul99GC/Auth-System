package com.raulcg.auth.services.accountValidationToken;

import com.raulcg.auth.exceptions.UserNotFoundException;
import com.raulcg.auth.models.AccountValidationToken;
import com.raulcg.auth.models.User;
import com.raulcg.auth.repositories.AccountValidationTokenRepository;
import com.raulcg.auth.repositories.UserRepository;
import com.raulcg.auth.requires.ActivateAccountRequest;
import com.raulcg.auth.services.email.EmailService;
import com.raulcg.auth.utils.OtpTokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AccountValidationTokenService implements IAccountValidationTokenService {

    private final AccountValidationTokenRepository accountValidationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final OtpTokenGenerator otpTokenGenerator;

    private EmailService emailService;


    public AccountValidationTokenService(AccountValidationTokenRepository accountValidationTokenRepository, PasswordEncoder passwordEncoder, UserRepository userRepository, OtpTokenGenerator otpTokenGenerator) {
        this.accountValidationTokenRepository = accountValidationTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.otpTokenGenerator = otpTokenGenerator;
    }

    @Autowired(required = false)
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Transactional
    @Override
    public AccountValidationToken createToken(User user, String token) {
        String tokenHashed = passwordEncoder.encode(token);
        AccountValidationToken accountValidationToken = new AccountValidationToken(tokenHashed, user);
        accountValidationToken.setExpiryDate(LocalDateTime.now().plusMinutes(5));
        return accountValidationTokenRepository.save(accountValidationToken);
    }

    @Transactional
    @Override
    public boolean validateToken(ActivateAccountRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        AccountValidationToken tokenSaved = accountValidationTokenRepository.findFirstByUserOrderByCreatedAtDesc(user);
        boolean tokenHashed = passwordEncoder.matches(request.getToken(), tokenSaved.getToken());

        if (!tokenHashed || tokenSaved.isUsed() || tokenSaved.getExpiryDate().isBefore(LocalDateTime.now())) {
            return false;
        }
        user.setAccountNonLocked(true);
        user.setEnabled(true);
        userRepository.save(user);

        tokenSaved.setUsed(true);
        accountValidationTokenRepository.save(tokenSaved);

        return true;
    }

    @Override
    public boolean sendNewToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if(user.isAccountNonLocked() || user.isEnabled()) {
            return false;
        }

        String token = otpTokenGenerator.generateToken();
        AccountValidationToken tokenSaved = new AccountValidationToken(passwordEncoder.encode(token), user);
        tokenSaved.setExpiryDate(LocalDateTime.now().plusMinutes(5));
        accountValidationTokenRepository.save(tokenSaved);

        if (emailService != null) {
            emailService.sendConfirmationEmail(user.getEmail(), user.getUsername(), token);
        }
        return true;
    }

}
