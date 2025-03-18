package com.raulcg.auth.security.jwt;

import com.raulcg.auth.security.jwt.services.IJwtService;
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
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    private final IJwtService jwtService;
    private final IUserService userService;

    public AuthTokenFilter(IJwtService jwtService, UserDetailsServiceImpl userDetailsService, @Lazy IUserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (jwt != null) {

                Map<String, Object> claims = jwtService.getClaimsWithoutSignature(jwt);
                String userSecret = userService.getUserSecret(claims.get("sub").toString());

                if (jwtService.validateToken(jwt, userSecret)) {
                    String sub = jwtService.getSubFromToken(jwt, userSecret);
                    List<String> authorities = jwtService.getAuthorities(jwt, userSecret);

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
                if ("jwtToken".equals(cookie.getName())) { // El nombre debe coincidir con el establecido en el controlador
                    return cookie.getValue();
                }
            }
        } else {
            return jwtService.getJwtFromHeader(request);
        }
        return null;
    }
}