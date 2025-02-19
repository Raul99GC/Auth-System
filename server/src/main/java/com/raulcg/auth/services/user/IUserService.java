package com.raulcg.auth.services.user;

import com.raulcg.auth.models.User;
import com.raulcg.auth.requires.CreateUserRequire;

public interface IUserService {
    User RegisterUser(CreateUserRequire user);

    boolean existUserByEmail(String mail);
}
