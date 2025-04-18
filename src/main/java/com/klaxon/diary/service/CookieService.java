package com.klaxon.diary.service;

import com.klaxon.diary.config.properties.CookieProperties;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CookieService {

    private final CookieProperties cookieProperties;

    public void attachRefreshToken(HttpServletResponse response, String token) {
        response.addHeader(HttpHeaders.SET_COOKIE,
                ResponseCookie.from(cookieProperties.getName(), token)
                        .httpOnly(cookieProperties.isHttpOnly())
                        .secure(cookieProperties.isSecure())
                        .path(cookieProperties.getPath())
                        .sameSite(cookieProperties.getSameSite())
                        .maxAge(cookieProperties.getMaxAge())
                        .build()
                        .toString()
        );
    }

    public void clearRefreshToken(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE,
                ResponseCookie.from(cookieProperties.getName(), "")
                        .httpOnly(cookieProperties.isHttpOnly())
                        .secure(cookieProperties.isSecure())
                        .path(cookieProperties.getPath())
                        .sameSite(cookieProperties.getSameSite())
                        .maxAge(0)
                        .build()
                        .toString()
        );
    }
}
