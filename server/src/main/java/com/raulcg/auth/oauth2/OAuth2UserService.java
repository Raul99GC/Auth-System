package com.raulcg.auth.oauth2;

import com.raulcg.auth.dtos.ProcessUserResponseDto;
import com.raulcg.auth.exceptions.EmailAlreadyExistException;
import com.raulcg.auth.exceptions.UsernameAlreadyExistException;
import com.raulcg.auth.oauth2.providers.OAuth2UserStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OAuth2UserService {

    private final Map<String, OAuth2UserStrategy> strategies;

    // ? @Autowired para dejar claro que se inyectan todas las implementaciones de OAuth2UserStrategy
    @Autowired
    public OAuth2UserService(List<OAuth2UserStrategy> strategies) {
        this.strategies = strategies.stream()
                .collect(Collectors.toMap(OAuth2UserStrategy::getRegistrationId, strategy -> strategy));
    }

    public ProcessUserResponseDto processUser(OAuth2AuthenticationToken token) throws EmailAlreadyExistException, UsernameAlreadyExistException {
        String registrationId = token.getAuthorizedClientRegistrationId();

        OAuth2UserStrategy strategy = strategies.get(registrationId);

        ProcessUserResponseDto response = new ProcessUserResponseDto();

        response.setUser(strategy.processUser(token));
        response.setNameAttributeKey(strategy.geAttributeKey());
        return response;
    }
}
