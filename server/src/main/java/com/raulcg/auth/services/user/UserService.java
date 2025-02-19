package com.raulcg.auth.services.user;

import com.raulcg.auth.enums.UserRole;
import com.raulcg.auth.models.Role;
import com.raulcg.auth.models.User;
import com.raulcg.auth.repositories.RoleRepository;
import com.raulcg.auth.repositories.UserRepository;
import com.raulcg.auth.requires.CreateUserRequire;
import com.raulcg.auth.utils.UserSecretGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserSecretGenerator userSecretGenerator;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, UserSecretGenerator userSecretGenerator, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userSecretGenerator = userSecretGenerator;
    }

    @Override
    public User RegisterUser(CreateUserRequire user) {
        String encodePassword = passwordEncoder.encode(user.getPassword());
        User newUser = new User(user.getUsername(), user.getEmail(), encodePassword);
        newUser.setAccountNonLocked(true);
        newUser.setEnabled(true);

        String userSecret = userSecretGenerator.generate();
        newUser.setUserSecret(userSecret);

        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByRole(UserRole.USER).orElseThrow());
        newUser.setRoles(roles);

        userRepository.save(newUser);

        return newUser;
    }

    @Override
    public boolean existUserByEmail(String mail) {
        return userRepository.existsByEmail(mail);
    }

}
