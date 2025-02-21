package com.raulcg.auth.requires;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ActivateAccountRequest {

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    @Size(min = 5, max = 255, message = "Email must be at least 5 characters")
    String email;

    @Size(min = 6, max = 6, message = "Token must be at least 6 characters")
    @NotBlank(message = "Token is required")
    String token;
}
