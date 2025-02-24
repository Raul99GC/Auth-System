package com.raulcg.auth.exceptions;

public class RereshTokenNotFoundException extends RuntimeException {
    public RereshTokenNotFoundException(String message) {
        super(message);
    }
}
