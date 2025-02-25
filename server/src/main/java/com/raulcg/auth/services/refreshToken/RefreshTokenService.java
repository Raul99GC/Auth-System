package com.raulcg.auth.services.refreshToken;

import com.raulcg.auth.exceptions.RereshTokenNotFoundException;
import com.raulcg.auth.models.RefreshToken;
import com.raulcg.auth.models.User;
import com.raulcg.auth.repositories.RefreshTokenRepository;
import com.raulcg.auth.utils.SecureTokensGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RefreshTokenService implements IRefreshTokenService {

    private final SecureTokensGenerator secureTokensGenerator;
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(SecureTokensGenerator secureTokensGenerator, RefreshTokenRepository refreshTokenRepository) {
        this.secureTokensGenerator = secureTokensGenerator;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public RefreshToken createToken(User user, int expiryDays) {
        String token = secureTokensGenerator.generateRefreshToken();
        var refreshToken = new RefreshToken(token, user);
        // set expiry date with 1 week
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(expiryDays));
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken disabelToken(RefreshToken token) {
        RefreshToken refreshToken = refreshTokenRepository.findById(token.getId())
                .orElseThrow( () -> new RereshTokenNotFoundException("Refresh token not found"));
        refreshToken.setEnabled(false);
        return refreshTokenRepository.save(refreshToken);
    }
}
