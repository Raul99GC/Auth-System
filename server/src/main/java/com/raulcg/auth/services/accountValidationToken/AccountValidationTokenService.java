package com.raulcg.auth.services.accountValidationToken;

import com.raulcg.auth.models.AccountValidationToken;
import com.raulcg.auth.models.User;
import com.raulcg.auth.repositories.AccountValidationTokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AccountValidationTokenService implements IAccountValidationTokenService {

    private final AccountValidationTokenRepository accountValidationTokenRepository;
    private final PasswordEncoder passwordEncoder;


    public AccountValidationTokenService(AccountValidationTokenRepository accountValidationTokenRepository, PasswordEncoder passwordEncoder) {
        this.accountValidationTokenRepository = accountValidationTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AccountValidationToken createToken(User user, String token) {
        String tokenHashed = passwordEncoder.encode(token);
        AccountValidationToken accountValidationToken = new AccountValidationToken(tokenHashed, user);
        accountValidationToken.setExpiryDate(LocalDateTime.now().plusMinutes(5));
        return accountValidationTokenRepository.save(accountValidationToken);
    }
}
