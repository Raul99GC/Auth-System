package com.raulcg.auth.services.user;

import com.raulcg.auth.enums.UserRole;
import com.raulcg.auth.exceptions.EmailAlreadyExistException;
import com.raulcg.auth.exceptions.RoleNotFoundException;
import com.raulcg.auth.exceptions.UsernameAlreadyExistException;
import com.raulcg.auth.models.Role;
import com.raulcg.auth.models.User;
import com.raulcg.auth.repositories.RoleRepository;
import com.raulcg.auth.repositories.UserRepository;
import com.raulcg.auth.requires.CreateUserRequire;
import com.raulcg.auth.services.accountValidationToken.IAccountValidationTokenService;
import com.raulcg.auth.services.email.EmailService;
import com.raulcg.auth.utils.AuthTokenGenerator;
import com.raulcg.auth.utils.SecureTokensGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureTokensGenerator secureTokensGenerator;
    private final AuthTokenGenerator authTokenGenerator;

    private final IAccountValidationTokenService accountValidationTokenService;


    private EmailService emailService;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, SecureTokensGenerator secureTokensGenerator, PasswordEncoder passwordEncoder, AuthTokenGenerator authTokenGenerator, IAccountValidationTokenService accountValidationTokenService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.secureTokensGenerator = secureTokensGenerator;
        this.authTokenGenerator = authTokenGenerator;
        this.accountValidationTokenService = accountValidationTokenService;
    }

    @Autowired(required = false)
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public User RegisterUser(CreateUserRequire user) {
        isUserExist(user);

        String encodePassword = passwordEncoder.encode(user.getPassword());
        String userSecret = secureTokensGenerator.generateUserSecret();
        Role defaultRole = roleRepository.findByName(UserRole.USER)
                .orElseThrow(() -> new RoleNotFoundException("Default role not found"));

        User newUser = new User(user.getUsername(), user.getEmail(), encodePassword);
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setUserSecret(userSecret);
        newUser.setRoles(Collections.singleton(defaultRole));

        User savedUser = userRepository.save(newUser);

        String token = authTokenGenerator.generateToken();
        accountValidationTokenService.createToken(savedUser, token);

        if (emailService != null) {
            emailService.sendConfirmationEmail(user.getEmail(), user.getUsername(), token);
        }
        return savedUser;
    }

    private void isUserExist(CreateUserRequire user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UsernameAlreadyExistException("Username already exist");
        } else if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistException("Email already exist");
        }
    }

    @Override
    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public boolean existUserByEmail(String mail) {
        return userRepository.existsByEmail(mail);
    }

}
