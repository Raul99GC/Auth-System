package com.raulcg.auth.security.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.raulcg.auth.enums.Providers;
import com.raulcg.auth.models.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails {


    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    @JsonIgnore
    private String password;
    @JsonIgnore
    private String userSecret;

    private Providers provider;

    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(UUID id, String username, String firstName, String lastName, String email, String password, String userSecret, Providers provider, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.userSecret = userSecret;
        this.provider = provider;
        this.authorities = authorities;
    }


    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword(),
                user.getUserSecret(),
                user.getProvider(),
                authorities
        );
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
