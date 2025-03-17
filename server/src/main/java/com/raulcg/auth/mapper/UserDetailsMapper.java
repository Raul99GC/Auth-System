package com.raulcg.auth.mapper;

import com.raulcg.auth.models.User;
import com.raulcg.auth.security.service.UserDetailsImpl;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserDetailsMapper {

    public UserDetailsImpl mapToOauth2UserDetails(User user) {
        if (user == null) {
            return null;
        }

        // Mapea los roles del usuario a SimpleGrantedAuthority
        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        // Crea la instancia de UserDetailsImpl y asigna los atributos
        UserDetailsImpl userDetails = new UserDetailsImpl();
        userDetails.setId(user.getId());
        userDetails.setEmail(user.getEmail());
        userDetails.setUsername(user.getUsername());
        userDetails.setPassword(user.getPassword());
        userDetails.setAuthorities(authorities);
        userDetails.setUserSecret(user.getUserSecret());
        return userDetails;
    }
}