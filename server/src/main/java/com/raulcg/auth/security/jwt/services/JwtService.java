package com.raulcg.auth.security.jwt.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raulcg.auth.security.service.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class JwtService implements IJwtService {

    private final ObjectMapper objectMapper;
    @Value("${spring.app.jwtHeader}")
    private String jwtHeader;

    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    private int jwtExpirationMs = 7200000;

    public JwtService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(jwtHeader);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }


    private SecretKey key(String userSecret) {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(userSecret + this.jwtSecret));
    }

    private Claims getClaimsFromToken(String jwtToken, String userSecret) {
        return Jwts.parser()
                .verifyWith(key(userSecret))
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();

    }

    @Override
    public String getSubFromToken(String jwtToken, String userSecret) {
        return String.valueOf(Objects.requireNonNull(getClaimsFromToken(jwtToken, userSecret)).get("sub"));
    }

    @Override
    public List<String> getAuthorities(String jwtToken, String userSecret) {
        return getClaimsFromToken(jwtToken, userSecret).get("authorities", List.class);
    }

    @Override
    public String generateTokenFromUserDetails(UserDetailsImpl userDetails) {
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
                .signWith(key(userDetails.getUserSecret()));

        if (email != null) token.claim("email", email);

        return token.compact();
    }
    
    @Override
    public boolean validateToken(String jwtToken, String userSecret) {
        try {
            Jwts.parser().verifyWith((SecretKey) key(userSecret)).build().parseSignedClaims(jwtToken);
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

    @Override
    public Map<String, Object> getClaimsWithoutSignature(String jwt) throws Exception {
        // Separamos el token en partes
        String[] partes = jwt.split("\\.");
        if (partes.length < 2) {
            throw new IllegalArgumentException("Token JWT inválido");
        }

        // Decodificamos el payload (la segunda parte)
        byte[] decodedBytes = Base64.getUrlDecoder().decode(partes[1]);
        String payload = new String(decodedBytes);

        // Parseamos el JSON del payload a un Map
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> claims = mapper.readValue(payload, Map.class);

        return claims;
    }
}