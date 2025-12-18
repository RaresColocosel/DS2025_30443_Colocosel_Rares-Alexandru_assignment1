package com.ems.device.controllers;

import com.ems.device.dtos.AssignDeviceRequest;
import com.ems.device.dtos.DeviceCreateRequest;
import com.ems.device.dtos.DeviceDTO;
import com.ems.device.services.DeviceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/devices")
public class DeviceController {

  private final DeviceService deviceService;

  public DeviceController(DeviceService deviceService) {
    this.deviceService = deviceService;
  }

  // GET /devices
  @GetMapping
  public ResponseEntity<List<DeviceDTO>> getAllDevices() {
    return ResponseEntity.ok(deviceService.getAllDevices());
  }

  // GET /devices/{id}
  @GetMapping("/{id}")
  public ResponseEntity<DeviceDTO> getDevice(@PathVariable Long id) {
    return ResponseEntity.ok(deviceService.getDevice(id));
  }

  // POST /devices
  @PostMapping
  public ResponseEntity<DeviceDTO> createDevice(@Valid @RequestBody DeviceCreateRequest req) {
    return ResponseEntity.ok(deviceService.createDevice(req));
  }

  // PUT /devices/{id}
  @PutMapping("/{id}")
  public ResponseEntity<DeviceDTO> updateDevice(@PathVariable Long id,
                                                @Valid @RequestBody DeviceCreateRequest req) {
    return ResponseEntity.ok(deviceService.updateDevice(id, req));
  }

  // DELETE /devices/{id}
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteDevice(@PathVariable Long id) {
    deviceService.deleteDevice(id);
    return ResponseEntity.noContent().build();
  }

  // POST /devices/{id}/assign
  @PostMapping("/{id}/assign")
  public ResponseEntity<Void> assignDevice(@PathVariable("id") Long deviceId,
                                           @Valid @RequestBody AssignDeviceRequest req) {
    deviceService.assignDeviceToUser(deviceId, req.getUserId());
    return ResponseEntity.ok().build();
  }
}
