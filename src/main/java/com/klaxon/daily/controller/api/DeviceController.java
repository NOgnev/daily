package com.klaxon.daily.controller.api;

import com.klaxon.daily.config.log.Log;
import com.klaxon.daily.dto.AuthUser;
import com.klaxon.daily.dto.RefreshToken.Device;
import com.klaxon.daily.service.DeviceService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/device")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @Log(logResult = false)
    @GetMapping
    public ResponseEntity<List<Device>> getDevices(@AuthenticationPrincipal AuthUser userDetails) {
        var list = deviceService.getDevices(userDetails.id()).stream()
                .map(d -> new Device(d.device().id(), d.device().expiryDate()))
                .sorted(Comparator.comparing(Device::expiryDate))
                .toList();
        return ResponseEntity.ok().body(list);
    }

    @Log
    @DeleteMapping("/{deviceId}")
    public ResponseEntity<Void> revokeDevice(@PathVariable @NotBlank UUID deviceId,
                                             @AuthenticationPrincipal AuthUser userDetails) {
        deviceService.revokeDevice(userDetails.id(), deviceId);
        return ResponseEntity.ok().build();
    }
}
