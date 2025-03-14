package com.raulcg.auth.security.jwt.services;

import com.raulcg.auth.security.service.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

public interface IJwtService {
    String getJwtFromHeader(HttpServletRequest request);

    String getSubFromToken(String jwtToken, String userSecret);

    List<String> getAuthorities(String jwtToken, String userSecret);

    String generateTokenFromUserDetails(UserDetailsImpl userDetails);
    
    boolean validateToken(String jwtToken, String userSecret);

    Map<String, Object> getClaimsWithoutSignature(String jwt) throws Exception;

}
