package com.ems.device.services;

import com.ems.device.dtos.DeviceCreateRequest;
import com.ems.device.dtos.DeviceDTO;
import com.ems.device.dtos.DeviceSyncDTO; // Ensure this DTO exists in com.ems.device.dtos
import com.ems.device.dtos.builders.DeviceBuilder;
import com.ems.device.entities.Device;
import com.ems.device.entities.DeviceAssignment;
import com.ems.device.repositories.DeviceAssignmentRepository;
import com.ems.device.repositories.DeviceRepository;
import com.ems.user.entities.User;
import com.ems.user.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DeviceService {

  private final DeviceRepository deviceRepository;
  private final DeviceAssignmentRepository assignmentRepository;
  private final UserRepository userRepository;
  private final RabbitTemplate rabbitTemplate; // Inject RabbitTemplate
  private final ObjectMapper objectMapper;     // Inject ObjectMapper for JSON

  public DeviceService(DeviceRepository deviceRepository,
                       DeviceAssignmentRepository assignmentRepository,
                       UserRepository userRepository,
                       RabbitTemplate rabbitTemplate) {
    this.deviceRepository = deviceRepository;
    this.assignmentRepository = assignmentRepository;
    this.userRepository = userRepository;
    this.rabbitTemplate = rabbitTemplate;
    this.objectMapper = new ObjectMapper();
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

    // --- SYNC: Publish CREATE Event ---
    // New device has no user assigned yet
    syncDevice(d, null, "CREATE");

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

    // Fetch current owner to keep monitoring DB in sync
    Long currentUserId = findUserIdForDevice(id);

    // --- SYNC: Publish UPDATE Event ---
    syncDevice(d, currentUserId, "UPDATE");

    return DeviceBuilder.toDTO(d);
  }

  @Transactional
  public void deleteDevice(Long id) {
    Device d = deviceRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Device not found: " + id));

    // delete assignments first
    assignmentRepository.findByUserId(null); // force class load

    assignmentRepository.findAll().stream()
            .filter(a -> a.getDevice().getId().equals(id))
            .forEach(assignmentRepository::delete);

    deviceRepository.delete(d);

    // --- SYNC: Publish DELETE Event ---
    // (User ID is irrelevant for deletion)
    syncDevice(d, null, "DELETE");
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

    // idempotent check
    if (assignmentRepository.existsByUserIdAndDevice_Id(userId, deviceId)) {
      return;
    }

    DeviceAssignment assignment = new DeviceAssignment();
    assignment.setDevice(device);
    assignment.setUserId(user.getId());
    assignmentRepository.save(assignment);

    // --- SYNC: Publish UPDATE Event (Assignment Changed) ---
    // This informs Monitoring Service that 'deviceId' now belongs to 'userId'
    syncDevice(device, userId, "UPDATE");
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

  // ===================================
  // SYNCHRONIZATION HELPERS
  // ===================================

  /**
   * Helper to find the User ID associated with a device (if any).
   * Needed because assignment is in a separate table/repository.
   */
  private Long findUserIdForDevice(Long deviceId) {
    return assignmentRepository.findAll().stream()
            .filter(a -> a.getDevice().getId().equals(deviceId))
            .map(DeviceAssignment::getUserId)
            .findFirst()
            .orElse(null);
  }

  /**
   * Publishes a sync message to RabbitMQ.
   */
  private void syncDevice(Device device, Long userId, String action) {
    try {
      DeviceSyncDTO dto = new DeviceSyncDTO(
              device.getId(),
              device.getMaxConsumptionW(),
              userId,
              action
      );

      String jsonMessage = objectMapper.writeValueAsString(dto);
      String routingKey = "device." + action.toLowerCase();

      rabbitTemplate.convertAndSend("device_sync_exchange", routingKey, jsonMessage);

      System.out.println(" [Device Service] Sent Sync: " + routingKey + " -> " + jsonMessage);
    } catch (Exception e) {
      System.err.println(" [Device Service] Failed to sync device: " + e.getMessage());
    }
  }
}