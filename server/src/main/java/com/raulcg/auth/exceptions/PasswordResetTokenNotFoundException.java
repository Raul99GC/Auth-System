package com.raulcg.auth.exceptions;

public class PasswordResetTokenNotFoundException  extends RuntimeException {
    public PasswordResetTokenNotFoundException(String message) {
        super(message);
    }
}
