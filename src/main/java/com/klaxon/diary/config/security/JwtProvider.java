package com.klaxon.diary.config.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.klaxon.diary.util.Constants.ACCESS;
import static com.klaxon.diary.util.Constants.REFRESH;
import static com.klaxon.diary.util.Constants.TOKEN_TYPE;

@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-expiration-ms}")
    private long jwtExpirationMs;

    @Value("${jwt.refresh-expiration-ms}")
    private long jwtRefreshExpirationMs;


    public String generateAccessToken(Authentication auth) {
        return generateToken(auth, ACCESS, jwtExpirationMs);
    }

    public String generateRefreshToken(Authentication auth) {
        return generateToken(auth, REFRESH, jwtRefreshExpirationMs);
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
        return ACCESS.equals(getTokenType(token));
    }

    public boolean isRefreshToken(String token) {
        return REFRESH.equals(getTokenType(token));
    }

    private String generateToken(Authentication auth, String tokenType, long expirationMs) {
        return Jwts.builder()
                .setSubject(auth.getName())
                .claim(TOKEN_TYPE, tokenType)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    private String getTokenType(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get(TOKEN_TYPE, String.class);
    }
}
