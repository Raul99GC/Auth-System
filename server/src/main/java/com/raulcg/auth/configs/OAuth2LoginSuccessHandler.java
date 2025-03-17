package com.raulcg.auth.configs;

import com.raulcg.auth.dtos.ProcessUserResponseDto;
import com.raulcg.auth.enums.UserRole;
import com.raulcg.auth.exceptions.EmailAlreadyExistException;
import com.raulcg.auth.exceptions.UsernameAlreadyExistException;
import com.raulcg.auth.mapper.UserDetailsMapper;
import com.raulcg.auth.models.RefreshToken;
import com.raulcg.auth.models.Role;
import com.raulcg.auth.oauth2.OAuth2UserService;
import com.raulcg.auth.security.jwt.services.IJwtService;
import com.raulcg.auth.security.service.UserDetailsImpl;
import com.raulcg.auth.services.refreshToken.IRefreshTokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final OAuth2UserService oAuth2UserService;
    private final IJwtService jwtService;
    private final UserDetailsMapper userDetailsMapper;
    private final IRefreshTokenService refreshTokenService;

    @Value("${frontend.url}")
    private String frontendUrl;

    public OAuth2LoginSuccessHandler(OAuth2UserService oAuth2UserService, IJwtService jwtService, UserDetailsMapper userDetailsMapper, IRefreshTokenService refreshTokenService) {
        this.oAuth2UserService = oAuth2UserService;
        this.jwtService = jwtService;
        this.userDetailsMapper = userDetailsMapper;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, java.io.IOException {

        OAuth2AuthenticationToken oAuth2Token = (OAuth2AuthenticationToken) authentication;

        String targetUrl;
        ProcessUserResponseDto procesorUserResult;
        try {
            procesorUserResult = oAuth2UserService.processUser(oAuth2Token);
        } catch (EmailAlreadyExistException | UsernameAlreadyExistException e) {
            targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/oauth2/account-exists").build().toUriString();
            this.setDefaultTargetUrl(targetUrl);
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }

        // Obtener los datos del usuario autenticado desde el principal de OAuth2
        DefaultOAuth2User principal = (DefaultOAuth2User) oAuth2Token.getPrincipal();
        Map<String, Object> attributes = principal.getAttributes();
        List<String> roles = procesorUserResult.getUser().getRoles().stream().map(Role::getName).map(UserRole::name).toList();

        List<SimpleGrantedAuthority> grantedAuthorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        DefaultOAuth2User oAuth2User = new DefaultOAuth2User(
                grantedAuthorities,
                attributes,
                procesorUserResult.getNameAttributeKey()
        );

        Authentication securityContext = new OAuth2AuthenticationToken(
                oAuth2User,
                grantedAuthorities,
                procesorUserResult.getNameAttributeKey()
        );

        this.setAlwaysUseDefaultTargetUrl(true);

        SecurityContextHolder.getContext().setAuthentication(securityContext);

        UserDetailsImpl userDetails = userDetailsMapper.mapToOauth2UserDetails(procesorUserResult.getUser());

        // create a jwt token
        String jwtToken = jwtService.generateTokenFromUserDetails(userDetails);
        RefreshToken refreshToken = refreshTokenService.createToken(procesorUserResult.getUser(), 15);


        targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/oauth2/login")
                .queryParam("token", jwtToken)
                .queryParam("refreshToken", refreshToken.getToken())
                .build().toUriString();

        this.setDefaultTargetUrl(targetUrl);
        super.onAuthenticationSuccess(request, response, authentication);
    }

}
