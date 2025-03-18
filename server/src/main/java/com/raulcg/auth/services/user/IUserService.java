package com.raulcg.auth.services.user;

import com.raulcg.auth.dtos.OAuthUserRegistrationDTO;
import com.raulcg.auth.models.User;
import com.raulcg.auth.repositories.EditUserRequest;
import com.raulcg.auth.requires.CreateUserRequire;

import java.util.Optional;

public interface IUserService {
    User RegisterUser(CreateUserRequire user);

    User createUserByProvider(OAuthUserRegistrationDTO user);

    User createUser(User user);

    boolean existUserByEmail(String mail);

    Optional<User> findByEmail(String email);

    String getUserSecret(String email);

    void editUser(EditUserRequest request, String email);
}
