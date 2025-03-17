package com.raulcg.auth.dtos;

import com.raulcg.auth.enums.Providers;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OAuthUserRegistrationDTO {
    String username;
    String email;
    Providers provider;
}
