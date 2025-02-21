package com.raulcg.auth.controllers;

import com.raulcg.auth.requires.ActivateAccountRequest;
import com.raulcg.auth.requires.CreateUserRequire;
import com.raulcg.auth.response.GenericResponse;
import com.raulcg.auth.response.SignupResponse;
import com.raulcg.auth.services.accountValidationToken.IAccountValidationTokenService;
import com.raulcg.auth.services.user.IUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final IUserService userService;
    private final IAccountValidationTokenService accountValidationTokenService;

    public AuthController(IUserService userService, IAccountValidationTokenService accountValidationTokenService) {
        this.userService = userService;
        this.accountValidationTokenService = accountValidationTokenService;
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> register(@Validated @RequestBody CreateUserRequire createUserRequire) {
        userService.RegisterUser(createUserRequire);
        var response = new SignupResponse(true, "User registered successfully");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/activate-account")
    public ResponseEntity<GenericResponse<?>> activateAccount(@Validated @RequestBody ActivateAccountRequest token) {
        boolean isValid = accountValidationTokenService.validateToken(token);
        if (!isValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new GenericResponse<>(null, "Invalid token", false));
        }
        return ResponseEntity.ok(new GenericResponse<>(null, "Account activated", true));

    }

}
