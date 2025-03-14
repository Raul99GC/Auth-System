package com.raulcg.auth.services.passwordResetToken;

public interface IPasswordResetTokenService {
    void generateRefreshToken(String email);
    void resetPassword(String token, String password);
}
