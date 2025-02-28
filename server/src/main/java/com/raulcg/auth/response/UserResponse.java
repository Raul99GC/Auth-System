package com.raulcg.auth.response;

import com.raulcg.auth.enums.Providers;
import com.raulcg.auth.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private Providers provider;

    List<UserRole> roles;
}
