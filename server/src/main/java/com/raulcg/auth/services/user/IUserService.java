package com.raulcg.auth.services.user;

import com.raulcg.auth.models.User;
import com.raulcg.auth.requires.CreateUserRequire;

import java.util.Optional;

public interface IUserService {
    User RegisterUser(CreateUserRequire user);

    User createUser(User user);

    boolean existUserByEmail(String mail);

    Optional<User> findByEmail(String email);

    String getUserSecret(String email);
}
