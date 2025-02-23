package com.raulcg.auth.utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class OtpTokenGenerator {
    // Puedes ajustar el conjunto de caracteres según tus necesidades.
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int TOKEN_LENGTH = 6;
    private final SecureRandom secureRandom = new SecureRandom();

    public String generateToken() {
        StringBuilder token = new StringBuilder(TOKEN_LENGTH);
        for (int i = 0; i < TOKEN_LENGTH; i++) {
            int index = secureRandom.nextInt(CHARACTERS.length());
            token.append(CHARACTERS.charAt(index));
        }
        return token.toString();
    }
}