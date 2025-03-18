package com.raulcg.auth.controllers;

import com.raulcg.auth.models.User;
import com.raulcg.auth.repositories.EditUserRequest;
import com.raulcg.auth.response.GenericResponse;
import com.raulcg.auth.services.user.IUserService;
import com.raulcg.auth.utils.AuthUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final AuthUtils authUtils;
    private final IUserService userService;

    public UserController(AuthUtils authUtils, IUserService userService) {
        this.authUtils = authUtils;
        this.userService = userService;
    }

    @PutMapping("/me")
    public ResponseEntity<GenericResponse<?>> editMyProfile(@Validated @RequestBody EditUserRequest request) {
        User user = authUtils.loggedInUser().get();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
         userService.editUser(request, user.getEmail());
         return ResponseEntity.ok(new GenericResponse<>(null, "User updated successfully", true));
    }
}
