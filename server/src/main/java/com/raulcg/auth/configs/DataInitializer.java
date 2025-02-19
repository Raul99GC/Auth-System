package com.raulcg.auth.configs;

import com.raulcg.auth.enums.UserRole;
import com.raulcg.auth.models.Role;
import com.raulcg.auth.models.User;
import com.raulcg.auth.repositories.RoleRepository;
import com.raulcg.auth.services.user.IUserService;
import com.raulcg.auth.utils.UserSecretGenerator;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer {

    private final IUserService userService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserSecretGenerator userSecretGenerator;

    public DataInitializer(IUserService userService, RoleRepository roleRepository, PasswordEncoder passwordEncoder, UserSecretGenerator userSecretGenerator) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userSecretGenerator = userSecretGenerator;
    }

    @PostConstruct
    @Transactional
    public void init() {

        if (roleRepository.findByName(UserRole.ADMIN).isEmpty()) {
            roleRepository.save(new Role(UserRole.ADMIN));
        }
        if (roleRepository.findByName(UserRole.USER).isEmpty()) {
            roleRepository.save(new Role(UserRole.USER));
        }
        if (roleRepository.findByName(UserRole.SUPER_ADMIN).isEmpty()) {
            roleRepository.save(new Role(UserRole.SUPER_ADMIN));
        }

        if (!userService.existUserByEmail("super-admin@gmail.com")) {
            User user = new User("super-admin", "super-admin@gmail.com", passwordEncoder.encode("admin"));
            // setear roles
            Set<Role> roles = new HashSet<>(roleRepository.findAll());
            user.setRoles(roles);
            user.setAccountNonLocked(true);
            user.setEnabled(true);
            user.setUserSecret(userSecretGenerator.generate());

            userService.createUser(user);
        }
    }
}