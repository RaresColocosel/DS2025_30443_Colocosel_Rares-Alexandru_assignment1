package com.ems.device.controllers;

import com.ems.device.dtos.DeviceDTO;
import com.ems.device.services.DeviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/client/devices")
public class ClientDeviceController {

    private final DeviceService deviceService;

    public ClientDeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    // GET /client/devices  -> devices assigned to currently logged-in user
    @GetMapping
    public ResponseEntity<List<DeviceDTO>> getMyDevices(Authentication auth) {
        String username = auth.getName();
        return ResponseEntity.ok(deviceService.getDevicesForUsername(username));
    }
}
