package com.raulcg.auth.controllers;

import com.raulcg.auth.models.User;
import com.raulcg.auth.requires.CreateUserRequire;
import com.raulcg.auth.response.SignupResponse;
import com.raulcg.auth.services.user.IUserService;
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

    public AuthController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> register(@Validated @RequestBody CreateUserRequire createUserRequire) {
        userService.RegisterUser(createUserRequire);
        var response = new SignupResponse(true, "User registered successfully");

        return ResponseEntity.ok(response);
    }
}
