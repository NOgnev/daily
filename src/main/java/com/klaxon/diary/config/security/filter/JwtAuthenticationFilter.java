package com.klaxon.diary.config.security.filter;

import com.klaxon.diary.config.security.JwtProvider;
import com.klaxon.diary.error.AppException;
import com.klaxon.diary.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

import static com.klaxon.diary.error.ErrorRegistry.USER_NOT_FOUND;
import static com.klaxon.diary.util.MdcKey.USER_ID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String token = jwtProvider.getTokenFromRequest(request);

        if (token != null && jwtProvider.validateToken(token)) {
            var userId = jwtProvider.getUserIdFromToken(token);
            MDC.put(USER_ID, userId.toString());
            var userDetails = userRepository.findById(userId)
                    .orElseThrow(() -> AppException.builder()
                            .httpStatus(HttpStatus.UNPROCESSABLE_ENTITY)
                            .error(USER_NOT_FOUND)
                            .args(Map.of("id", userId.toString()))
                            .build());
            var authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        chain.doFilter(request, response);
    }
}
