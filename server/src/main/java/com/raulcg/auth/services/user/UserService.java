package com.raulcg.auth.services.user;

import com.raulcg.auth.dtos.OAuthUserRegistrationDTO;
import com.raulcg.auth.enums.Providers;
import com.raulcg.auth.enums.UserRole;
import com.raulcg.auth.exceptions.EmailAlreadyExistException;
import com.raulcg.auth.exceptions.RoleNotFoundException;
import com.raulcg.auth.exceptions.UserNotFoundException;
import com.raulcg.auth.exceptions.UsernameAlreadyExistException;
import com.raulcg.auth.models.Role;
import com.raulcg.auth.models.User;
import com.raulcg.auth.repositories.EditUserRequest;
import com.raulcg.auth.repositories.RoleRepository;
import com.raulcg.auth.repositories.UserRepository;
import com.raulcg.auth.requires.CreateUserRequire;
import com.raulcg.auth.services.accountValidationToken.IAccountValidationTokenService;
import com.raulcg.auth.services.email.IEmailService;
import com.raulcg.auth.utils.OtpTokenGenerator;
import com.raulcg.auth.utils.SecureTokensGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureTokensGenerator secureTokensGenerator;
    private final OtpTokenGenerator otpTokenGenerator;

    private final IAccountValidationTokenService accountValidationTokenService;


    private IEmailService emailService;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, SecureTokensGenerator secureTokensGenerator, PasswordEncoder passwordEncoder, OtpTokenGenerator otpTokenGenerator, IAccountValidationTokenService accountValidationTokenService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.secureTokensGenerator = secureTokensGenerator;
        this.otpTokenGenerator = otpTokenGenerator;
        this.accountValidationTokenService = accountValidationTokenService;
    }

    @Autowired(required = false)
    public void setEmailService(IEmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public User RegisterUser(CreateUserRequire user) {
        isUserExist(user.getUsername(), user.getEmail());

        String encodePassword = passwordEncoder.encode(user.getPassword());
        String userSecret = secureTokensGenerator.generateUserSecret();
        Role defaultRole = roleRepository.findByName(UserRole.USER)
                .orElseThrow(() -> new RoleNotFoundException("Default role not found"));

        User newUser = new User(user.getUsername(), user.getEmail(), encodePassword);
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setUserSecret(userSecret);
        newUser.setRoles(Collections.singleton(defaultRole));
        newUser.setProvider(Providers.DEFAULT);

        User savedUser = userRepository.save(newUser);

        String token = otpTokenGenerator.generateToken();
        accountValidationTokenService.createToken(savedUser, token);

        if (emailService != null) {
            emailService.sendConfirmationEmail(user.getEmail(), user.getUsername(), token);
        }
        return savedUser;
    }

    @Override
    public User createUserByProvider(OAuthUserRegistrationDTO user) {
        // ! here I can have a problem if a username is already exist and its the username of the provider
        // TODO: fix this xd (maybe create a new user with a random username) idk
        isUserExist(user.getUsername(), user.getEmail());

        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setEmail(user.getEmail());
        newUser.setProvider(user.getProvider());
        newUser.setEnabled(true);
        newUser.setAccountNonLocked(true);
        newUser.setUserSecret(secureTokensGenerator.generateUserSecret());

        Role defaultRole = roleRepository.findByName(UserRole.USER)
                .orElseThrow(() -> new RoleNotFoundException("Default role not found"));
        newUser.setRoles(Collections.singleton(defaultRole));

        return userRepository.save(newUser);
    }

    private void isUserExist(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistException("Username already exist");
        } else if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistException("Email already exist");
        }
    }

    @Override
    @Transactional
    public User createUser(User user) {
        isUserExist(user.getUsername(), user.getEmail());
        return userRepository.save(user);
    }

    @Override
    public boolean existUserByEmail(String mail) {
        return userRepository.existsByEmail(mail);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public String getUserSecret(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
        return user.getUserSecret();
    }

    @Transactional
    @Override
    public void editUser(EditUserRequest request, String email) {
        User userSaved = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found papu"));
        userSaved.setFirstName(request.getFirstName());
        userSaved.setLastName(request.getLastName());
        if(userRepository.existsByUsername(request.getUsername()) && !userSaved.getUsername().equals(request.getUsername())) {
            throw new UsernameAlreadyExistException("Username already exist");
        }
        userSaved.setUsername(request.getUsername());
        userRepository.save(userSaved);
    }

}
