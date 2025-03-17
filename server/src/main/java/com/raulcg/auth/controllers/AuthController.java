package com.raulcg.auth.controllers;

import com.raulcg.auth.mapper.UserMapper;
import com.raulcg.auth.models.RefreshToken;
import com.raulcg.auth.models.User;
import com.raulcg.auth.requires.ActivateAccountRequest;
import com.raulcg.auth.requires.CreateUserRequire;
import com.raulcg.auth.requires.LoginRequest;
import com.raulcg.auth.requires.ResetPasswordRequire;
import com.raulcg.auth.response.CheckAuthResponse;
import com.raulcg.auth.response.GenericResponse;
import com.raulcg.auth.response.LoginResponse;
import com.raulcg.auth.response.SignupResponse;
import com.raulcg.auth.security.jwt.services.JwtService;
import com.raulcg.auth.security.service.UserDetailsImpl;
import com.raulcg.auth.services.accountValidationToken.IAccountValidationTokenService;
import com.raulcg.auth.services.passwordResetToken.IPasswordResetTokenService;
import com.raulcg.auth.services.refreshToken.IRefreshTokenService;
import com.raulcg.auth.services.user.IUserService;
import com.raulcg.auth.utils.AuthUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final IUserService userService;
    private final IAccountValidationTokenService accountValidationTokenService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final IRefreshTokenService refreshTokenService;
    private AuthUtils authUtils;
    private final IPasswordResetTokenService passwordResetTokenService;

    public AuthController(IUserService userService, IAccountValidationTokenService accountValidationTokenService, AuthenticationManager authenticationManager, JwtService jwtService, IRefreshTokenService refreshTokenService, IPasswordResetTokenService passwordResetTokenService) {
        this.userService = userService;
        this.accountValidationTokenService = accountValidationTokenService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.passwordResetTokenService = passwordResetTokenService;
    }

    @Autowired
    public void setAuthUtils(AuthUtils authUtils) {
        this.authUtils = authUtils;
    }


    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> register(@Validated @RequestBody CreateUserRequire createUserRequire) {
        userService.RegisterUser(createUserRequire);
        var response = new SignupResponse(true, "User registered successfully");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        Optional<User> userOptional = userService.findByEmail(loginRequest.getEmail());
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new GenericResponse<>(null, "Invalid email or password", false));
        }

        // Authenticate the user
        Authentication authentication;
        authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Get the user details
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtService.generateTokenFromUserDetails(userDetails);

        RefreshToken refreshToken = refreshTokenService.createToken(userOptional.get(), 15); // Expira en 15 dias

        Cookie tokenCookie = new Cookie("jwtToken", jwt);
        tokenCookie.setHttpOnly(true);
        tokenCookie.setSecure(true);
        tokenCookie.setPath("/");
        tokenCookie.setMaxAge(60 * 60 * 2); // Expira en 2 hora

        // add refresh token cookie
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken.getToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(60 * 60 * 2 * 7); // 7 dias

        response.addCookie(tokenCookie);
        response.addCookie(refreshTokenCookie);

        LoginResponse responseBody = new LoginResponse(jwt, refreshToken.getToken());
        responseBody.setMessage("Logged in successfully!");
        responseBody.setUser(UserMapper.mapToUserResponse(userOptional.get()));
        responseBody.setStatus(true);

        return ResponseEntity.ok(responseBody);
    }

    @PostMapping("/activate-account")
    public ResponseEntity<GenericResponse<?>> activateAccount(@Validated @RequestBody ActivateAccountRequest token) {
        boolean isValid = accountValidationTokenService.validateToken(token);
        if (!isValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new GenericResponse<>(null, "Invalid token", false));
        }
        return ResponseEntity.ok(new GenericResponse<>(null, "Account activated", true));
    }

    @PostMapping("/create-new-token")
    public ResponseEntity<GenericResponse<?>> createNewToken(@Validated @RequestParam String email) {
        boolean isValid = accountValidationTokenService.sendNewToken(email);
        if (!isValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new GenericResponse<>(null, "Error sending token", false));
        }
        return ResponseEntity.ok(new GenericResponse<>(null, "Token sent successfully", true));
    }

    @GetMapping("/check-auth")
    public ResponseEntity<?> checkAuth() {
        if (authUtils == null) {
            throw new IllegalStateException("Something went wrong");
        }
        Optional<User> user = authUtils.loggedInUser();

        if (user.isEmpty()) {
            GenericResponse<User> response = new GenericResponse<>();
            response.setStatus(false);
            response.setMessage("You are not logged in.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        CheckAuthResponse checkAuthResponse = new CheckAuthResponse();
        checkAuthResponse.setStatus(true);
        checkAuthResponse.setMessage("You are logged in.");
        checkAuthResponse.setUser(UserMapper.mapToUserResponse(user.get()));
        return ResponseEntity.ok(checkAuthResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<GenericResponse<?>> logout(HttpServletResponse response) {
        SecurityContextHolder.clearContext();

        Cookie cookie = new Cookie("jwtToken", "");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        response.addCookie(cookie);

        Cookie refreshTokenCookie = new Cookie("refreshToken", "");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0);
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok(new GenericResponse<>(null, "Logged out successfully", true));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<GenericResponse<?>> changePassword(@RequestParam String email) {

        passwordResetTokenService.generateRefreshToken(email);
        return ResponseEntity.ok(new GenericResponse<>(null, "Password reset email sent", true));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<GenericResponse<?>> resetPassword(@Validated @RequestBody ResetPasswordRequire resetPasswordRequire) {

        passwordResetTokenService.resetPassword(resetPasswordRequire.getToken(), resetPasswordRequire.getPassword());
        return ResponseEntity.ok(new GenericResponse<>(null, "Password reset successfully", true));
    }

}
