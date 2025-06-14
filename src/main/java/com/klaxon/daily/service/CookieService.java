package com.klaxon.daily.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

public class CookieService {

    public static void attachCookie(HttpServletResponse response, String name, String value, boolean httpOnly,
                                    boolean secure, String path, String sameSite, long maxAgeSeconds) {
        response.addHeader(HttpHeaders.SET_COOKIE,
                ResponseCookie.from(name, value)
                        .httpOnly(httpOnly)
                        .secure(secure)
                        .path(path)
                        .sameSite(sameSite)
                        .maxAge(maxAgeSeconds)
                        .build()
                        .toString()
        );
    }
}
