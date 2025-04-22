package com.klaxon.diary.config.security;

import com.klaxon.diary.config.log.Log;
import com.klaxon.diary.config.log.hidden.Hidden;
import com.klaxon.diary.dto.AuthUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

import static com.klaxon.diary.util.BearerUtil.getBearer;
import static com.klaxon.diary.util.Headers.ACCESS_TOKEN_HEADER;

@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-expiration-ms}")
    private long jwtExpirationMs;


    public String generateAccessToken(AuthUser user) {
        return Jwts.builder()
                .setSubject(user.id().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public UUID getUserIdFromToken(String token) {
        return UUID.fromString(
                Jwts.parserBuilder()
                        .setSigningKey(jwtSecret.getBytes())
                        .build()
                        .parseClaimsJws(token)
                        .getBody()
                        .getSubject()
        );
    }

    @Log
    public boolean validateToken(@Hidden String token) {
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

    @Log(logArgs = false, logResult = false)
    public String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(ACCESS_TOKEN_HEADER);
        return getBearer(bearer);
    }
}
