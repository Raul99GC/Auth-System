package com.raulcg.auth.oauth2.providers;

import com.raulcg.auth.dtos.OAuthUserRegistrationDTO;
import com.raulcg.auth.enums.Providers;
import com.raulcg.auth.models.User;
import com.raulcg.auth.services.user.IUserService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class GoogleOAuth2User implements OAuth2UserStrategy {

    private static final String REGISTRATION_ID = "google";
    private static final String ATTRIBUTE_KEY = "sub";
    private final IUserService userService;

    public GoogleOAuth2User(IUserService userService) {
        this.userService = userService;
    }

    @Override
    public String getRegistrationId() {
        return REGISTRATION_ID;
    }

    @Override
    public String geAttributeKey() {
        return ATTRIBUTE_KEY;
    }

    @Override
    public User processUser(OAuth2AuthenticationToken token) {

        DefaultOAuth2User principal = (DefaultOAuth2User) token.getPrincipal();
        Map<String, Object> attributes = principal.getAttributes();
        String email = attributes.get("email").toString();

        Optional<User> userDb = userService.findByEmail(email);

        if (userDb.isPresent()) {
            if (userDb.get().getProvider().name().equals(REGISTRATION_ID)) {
                return userDb.get();
            }
            return userDb.get();
        }

        String realName = attributes.get("name") != null ? attributes.get("name").toString() : "";
        String username = generateUsername(email, realName);

        OAuthUserRegistrationDTO user = new OAuthUserRegistrationDTO();
        user.setEmail(email);
        user.setUsername(username);
        user.setProvider(Providers.GOOGLE);
        return userService.createUserByProvider(user);
    }

    private String generateUsername(String email, String fullName) {
        if (fullName != null && !fullName.trim().isEmpty()) {
            String[] nombres = fullName.trim().split("\\s+");
            if (nombres.length >= 2) {
                String username = nombres[0].substring(0, 1) + nombres[nombres.length - 1];
                return username.toLowerCase();
            } else {
                return fullName.replaceAll("\\s+", "").toLowerCase();
            }
        } else {
            int indiceArroba = email.indexOf("@");
            if (indiceArroba > 0) {
                return email.substring(0, indiceArroba).toLowerCase();
            } else {
                return email.toLowerCase();
            }
        }
    }



}
