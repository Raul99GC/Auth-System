package com.raulcg.auth.security.jwt;

import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import com.raulcg.auth.security.service.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@Component
public class JwtUtils {

    @Value("${spring.app.jwtHeader}")
    private String jwtHeader;

    private int jwtExpirationMs = 7200000;

    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(jwtHeader);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private SecretKey key(String jwtSecret) {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    private Claims getClaimsFromToken(String token, String jwtSecret) {
        return Jwts.parser()
                .verifyWith(key(jwtSecret))
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }

    public String getSubFromToken(String token, String jwtSecret) {
        return String.valueOf(Objects.requireNonNull(getClaimsFromToken(token, token)).get("sub"));
    }

    public List<String> getAuthorities(String token, String jwtSecret) {
        return getClaimsFromToken(token, jwtSecret).get("authorities", List.class);
    }

    public String generateTokenFromUserDetails(UserDetailsImpl userDetails, String jwtSecret) {
        String email = userDetails.getEmail();
        String username = userDetails.getUsername();
        List<String> authorities = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        JwtBuilder token = Jwts.builder()
                .header().keyId(String.valueOf(userDetails.getId())).and()
                .subject(username)
                .claim("authorities", authorities)
                .issuedAt(new java.util.Date())
                .expiration(new java.util.Date((new java.util.Date().getTime() + jwtExpirationMs))) // 2 hrs
                .signWith(key(jwtSecret));

        if (email != null) token.claim("email", email);

        return token.compact();
    }

    public String getKidFromToken(String token) {
        String[] parts = token.split("\\.");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Token inválido");
        }
        String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]));
        JsonObject header = JsonParser.parseString(headerJson).getAsJsonObject();
        return header.get("kid").getAsString();
    }

    public boolean validateToken(String authToken, String jwtSecret) {
        try {
            Jwts.parser().verifyWith((SecretKey) key(jwtSecret)).build().parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        } catch (ExpiredJwtException e) {
            throw new IllegalArgumentException("Expired JWT token", e);
        } catch (UnsupportedJwtException e) {
            throw new IllegalArgumentException("Unsupported JWT token", e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("JWT claims string is empty", e);
        }
    }
}