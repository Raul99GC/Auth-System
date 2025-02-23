package com.raulcg.auth.utils;

import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class SecureTokensGenerator {
    private final SecureRandom secureRandom = new SecureRandom();

    public String generateUserSecret() {
        byte[] secretBytes = new byte[32];
        secureRandom.nextBytes(secretBytes);
        return String.valueOf(Hex.encode(secretBytes));
    }

    public String generateRefreshToken() {
        byte[] secretBytes = new byte[64];
        secureRandom.nextBytes(secretBytes);
        return String.valueOf(Hex.encode(secretBytes));
    }
}