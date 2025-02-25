package com.raulcg.auth.services.user;

import com.raulcg.auth.models.User;
import com.raulcg.auth.requires.CreateUserRequire;

import java.util.Optional;
import java.util.UUID;

public interface IUserService {
    User RegisterUser(CreateUserRequire user);

    User createUser(User user);

    boolean existUserByEmail(String mail);

    Optional<User> findByEmail(String email);

    String getUserSecret(UUID userId);
}
