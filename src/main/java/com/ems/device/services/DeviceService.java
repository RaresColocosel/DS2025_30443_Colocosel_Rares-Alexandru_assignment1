package com.ems.device.services;

import com.ems.device.dtos.DeviceCreateRequest;
import com.ems.device.dtos.DeviceDTO;
import com.ems.device.dtos.builders.DeviceBuilder;
import com.ems.device.entities.Device;
import com.ems.device.entities.DeviceAssignment;
import com.ems.device.repositories.DeviceAssignmentRepository;
import com.ems.device.repositories.DeviceRepository;
import com.ems.user.entities.User;
import com.ems.user.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DeviceService {

  private final DeviceRepository deviceRepository;
  private final DeviceAssignmentRepository assignmentRepository;
  private final UserRepository userRepository;

  public DeviceService(DeviceRepository deviceRepository,
                       DeviceAssignmentRepository assignmentRepository,
                       UserRepository userRepository) {
    this.deviceRepository = deviceRepository;
    this.assignmentRepository = assignmentRepository;
    this.userRepository = userRepository;
  }

  // =========
  // DEVICES
  // =========

  @Transactional(readOnly = true)
  public List<DeviceDTO> getAllDevices() {
    return deviceRepository.findAll()
            .stream()
            .map(DeviceBuilder::toDTO)
            .toList();
  }

  @Transactional(readOnly = true)
  public DeviceDTO getDevice(Long id) {
    Device device = deviceRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Device not found: " + id));
    return DeviceBuilder.toDTO(device);
  }

  @Transactional
  public DeviceDTO createDevice(DeviceCreateRequest req) {
    Device d = new Device();
    d.setName(req.getName());
    d.setDescription(req.getDescription());
    d.setMaxConsumptionW(req.getMaxConsumptionW());
    d = deviceRepository.save(d);
    return DeviceBuilder.toDTO(d);
  }

  @Transactional
  public DeviceDTO updateDevice(Long id, DeviceCreateRequest req) {
    Device d = deviceRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Device not found: " + id));

    d.setName(req.getName());
    d.setDescription(req.getDescription());
    d.setMaxConsumptionW(req.getMaxConsumptionW());
    d = deviceRepository.save(d);

    return DeviceBuilder.toDTO(d);
  }

  @Transactional
  public void deleteDevice(Long id) {
    Device d = deviceRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Device not found: " + id));

    // delete assignments first
    assignmentRepository.findByUserId(null); // just to force class loading, no-op

    // cheaper: let DB cascade if you set FK, otherwise delete manually
    assignmentRepository.findAll().stream()
            .filter(a -> a.getDevice().getId().equals(id))
            .forEach(assignmentRepository::delete);

    deviceRepository.delete(d);
  }

  // ===========
  // ASSIGNMENT
  // ===========

  @Transactional
  public void assignDeviceToUser(Long deviceId, Long userId) {
    Device device = deviceRepository.findById(deviceId)
            .orElseThrow(() -> new IllegalArgumentException("Device not found: " + deviceId));

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

    // idempotent: if already assigned, do nothing (avoids DB unique-key error)
    if (assignmentRepository.existsByUserIdAndDevice_Id(userId, deviceId)) {
      return;
    }

    DeviceAssignment assignment = new DeviceAssignment();
    assignment.setDevice(device);
    assignment.setUserId(user.getId());
    assignmentRepository.save(assignment);
  }

  @Transactional(readOnly = true)
  public List<DeviceDTO> getDevicesForUserId(Long userId) {
    return assignmentRepository.findByUserId(userId)
            .stream()
            .map(DeviceAssignment::getDevice)
            .map(DeviceBuilder::toDTO)
            .toList();
  }

  @Transactional(readOnly = true)
  public List<DeviceDTO> getDevicesForUsername(String username) {
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
    return getDevicesForUserId(user.getId());
  }
}
