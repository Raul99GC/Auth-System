package com.raulcg.auth.security.jwt;

import com.raulcg.auth.security.service.UserDetailsServiceImpl;
import com.raulcg.auth.services.user.IUserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final IUserService userService;

    public AuthTokenFilter(JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsService, @Lazy IUserService userService) {
        this.jwtUtils = jwtUtils;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (jwt != null) {

                String kid = jwtUtils.getKidFromToken(jwt);

                // Utilizar el 'kid' para obtener el secreto asociado al usuario desde la BD
                String userSecret = userService.getUserSecret(kid);

                if (jwtUtils.validateToken(jwt, userSecret)) {
                    String sub = jwtUtils.getSubFromToken(jwt, userSecret);
                    List<String> authorities = jwtUtils.getAuthorities(jwt, userSecret);

                    // Create Authentication object
                    List<GrantedAuthority> authoritiesList = authorities.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());
                    Authentication auth = new UsernamePasswordAuthenticationToken(sub, null, authoritiesList);

                    // set user in security context
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }

        } catch (Exception e) {
//            TODO: Handle exception better xD
            logger.error("Cannot set user authentication", e);
        }
        filterChain.doFilter(request, response);

    }

    private String parseJwt(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("token".equals(cookie.getName())) { // El nombre debe coincidir con el establecido en el controlador
                    return cookie.getValue();
                }
            }
        } else {
            return jwtUtils.getJwtFromHeader(request);
        }
        return null;
    }
}