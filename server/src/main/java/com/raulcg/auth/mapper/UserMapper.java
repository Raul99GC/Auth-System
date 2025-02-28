package com.raulcg.auth.mapper;

import com.raulcg.auth.models.Role;
import com.raulcg.auth.models.User;
import com.raulcg.auth.response.UserResponse;

import java.util.stream.Collectors;

public class UserMapper {

    public static UserResponse mapToUserResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setUsername(user.getUsername());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setEmail(user.getEmail());
        userResponse.setProvider(user.getProvider());
        // set roles
        userResponse.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));

        return userResponse;
    }
}
