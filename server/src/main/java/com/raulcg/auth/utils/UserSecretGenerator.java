package com.raulcg.auth.utils;

import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class UserSecretGenerator {
    private final SecureRandom secureRandom = new SecureRandom();

    public String generate() {
        byte[] secretBytes = new byte[32]; // 32 bytes = 256 bits
        secureRandom.nextBytes(secretBytes);
        return String.valueOf(Hex.encode(secretBytes));
    }
}