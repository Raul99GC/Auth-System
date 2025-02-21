package com.raulcg.auth.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SignupResponse {
    boolean status;
    String message;
}
