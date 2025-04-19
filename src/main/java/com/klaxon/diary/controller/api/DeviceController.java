package com.klaxon.diary.controller.api;

import com.klaxon.diary.dto.AuthUser;
import com.klaxon.diary.dto.RefreshToken.Device;
import com.klaxon.diary.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/device")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @GetMapping
    public ResponseEntity<List<Device>> getDevices(@AuthenticationPrincipal AuthUser userDetails) {
        var list = deviceService.getDevices(userDetails.id()).stream()
                .map(d -> new Device(d.device().id(), d.device().expiryDate()))
                .toList();
        return ResponseEntity.ok().body(list);
    }

    @DeleteMapping("/{deviceId}")
    public ResponseEntity<String> revokeDevice(@PathVariable UUID deviceId,
                                               @AuthenticationPrincipal AuthUser userDetails) {
        deviceService.revokeDevice(userDetails.id(), deviceId);
        return ResponseEntity.ok().body("Device revoked successfully");
    }
}
