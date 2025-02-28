package com.raulcg.auth.response;

import lombok.Data;

@Data
public class CheckAuthResponse {
    private boolean status;
    private String message;
    private UserResponse user;
}
