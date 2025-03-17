package com.raulcg.auth.oauth2.providers;

import com.raulcg.auth.models.User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

public interface OAuth2UserStrategy {

    String getRegistrationId();

    String geAttributeKey();

    User processUser(OAuth2AuthenticationToken token);
}
