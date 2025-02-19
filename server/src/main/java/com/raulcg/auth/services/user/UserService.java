package com.raulcg.auth.services.user;

import com.raulcg.auth.enums.UserRole;
import com.raulcg.auth.models.Role;
import com.raulcg.auth.models.User;
import com.raulcg.auth.repositories.RoleRepository;
import com.raulcg.auth.repositories.UserRepository;
import com.raulcg.auth.requires.CreateUserRequire;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final SecureRandom secureRandom = new SecureRandom();
    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User RegisterUser(CreateUserRequire user) {
        User newUser = new User(user.getUsername(), user.getEmail(), encodePassword(user.getPassword()));
        newUser.setAccountNonLocked(true);
        newUser.setEnabled(true);
        newUser.setUserSecret(generateUserSecret());

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

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private String generateUserSecret() {
        byte[] secretBytes = new byte[32]; // 32 bytes = 256 bits
        secureRandom.nextBytes(secretBytes);
        return String.valueOf(Hex.encode(secretBytes));
    }

}
