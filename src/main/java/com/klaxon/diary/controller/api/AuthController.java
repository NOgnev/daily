package com.klaxon.diary.controller.api;

import com.klaxon.diary.dto.AuthRequest;
import com.klaxon.diary.dto.Device;
import com.klaxon.diary.dto.TokensHolder;
import com.klaxon.diary.service.AuthService;
import com.klaxon.diary.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest request) {
        authService.register(request.nickname(), request.password());
        return ResponseEntity.ok().body("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<TokensHolder> login(@RequestHeader("X_DEVICE_ID") String deviceId,
                                              @RequestBody AuthRequest request) {
        return ResponseEntity.ok().body(authService.login(request, deviceId));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokensHolder> refresh(@RequestHeader("X_DEVICE_ID") String deviceId,
                                                @RequestBody TokensHolder request) {
        return ResponseEntity.ok().body(authService.refresh(request.refreshToken(), deviceId));
    }

    @GetMapping("/devices")
    public ResponseEntity<List<Device>> getDevices(@AuthenticationPrincipal UserDetails userDetails) {
        var list = refreshTokenService.getDevices(userDetails.getUsername()).stream()
                .map(d -> new Device(d.deviceId(), d.expiryDate().toString()))
                .toList();
        return ResponseEntity.ok().body(list);
    }

    @DeleteMapping("/devices/{deviceId}")
    public ResponseEntity<String> revokeDevice(@PathVariable String deviceId,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        refreshTokenService.revokeDevice(userDetails.getUsername(), deviceId);
        return ResponseEntity.ok().body("Device revoked successfully");
    }
}
