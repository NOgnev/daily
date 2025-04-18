package com.klaxon.diary.config.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-expiration-ms}")
    private long jwtExpirationMs;

    @Value("${jwt.refresh-expiration-ms}")
    private long jwtRefreshExpirationMs;

    public String generateAccessToken(Authentication auth) {
        return Jwts.builder()
                .setSubject(auth.getName())
                .claim("token_type", "access")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(Authentication auth) {
        return Jwts.builder()
                .setSubject(auth.getName())
                .claim("token_type", "refresh")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtRefreshExpirationMs))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(jwtSecret.getBytes())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isAccessToken(String token) {
        return "access".equals(getTokenType(token));
    }

    public boolean isRefreshToken(String token) {
        return true;
    }

    private String getTokenType(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("token_type", String.class);

    }
}
