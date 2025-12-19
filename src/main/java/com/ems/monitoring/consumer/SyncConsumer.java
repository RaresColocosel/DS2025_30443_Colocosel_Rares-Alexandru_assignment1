package com.ems.monitoring.consumer;

import com.ems.monitoring.config.RabbitConfig;
import com.ems.monitoring.entities.MonitoredDevice;
import com.ems.monitoring.entities.MonitoredUser;
import com.ems.monitoring.repositories.MonitoredDeviceRepository;
import com.ems.monitoring.repositories.MonitoredUserRepository;
// You need to duplicate/share the DTOs in the monitoring package OR import them if they are in a common module.
// Assuming you copied them to a common package as requested earlier, or duplicate them here:
import com.ems.user.dtos.UserSyncDTO;
import com.ems.device.dtos.DeviceSyncDTO;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SyncConsumer {

    private final MonitoredUserRepository userRepository;
    private final MonitoredDeviceRepository deviceRepository;
    private final ObjectMapper mapper;

    public SyncConsumer(MonitoredUserRepository userRepository,
                        MonitoredDeviceRepository deviceRepository) {
        this.userRepository = userRepository;
        this.deviceRepository = deviceRepository;
        this.mapper = new ObjectMapper();
    }

    // =========================================================
    // USER SYNC
    // =========================================================
    @RabbitListener(queues = RabbitConfig.USER_SYNC_QUEUE)
    @Transactional
    public void consumeUserSync(String message) {
        try {
            UserSyncDTO dto = mapper.readValue(message, UserSyncDTO.class);
            System.out.println(" [Monitoring] User Sync received: " + dto.getAction() + " ID: " + dto.getId());

            if ("DELETE".equalsIgnoreCase(dto.getAction())) {
                userRepository.deleteById(dto.getId());
                // Optional: Nullify device ownership if user is deleted
                // deviceRepository.findByUserId(dto.getId()).forEach(d -> { ... });
            } else {
                // CREATE or UPDATE
                MonitoredUser user = new MonitoredUser(dto.getId(), dto.getUsername());
                userRepository.save(user);
            }
        } catch (Exception e) {
            System.err.println("Error processing user sync: " + e.getMessage());
        }
    }

    // =========================================================
    // DEVICE SYNC
    // =========================================================
    @RabbitListener(queues = RabbitConfig.DEVICE_SYNC_QUEUE)
    @Transactional
    public void consumeDeviceSync(String message) {
        try {
            DeviceSyncDTO dto = mapper.readValue(message, DeviceSyncDTO.class);
            System.out.println(" [Monitoring] Device Sync received: " + dto.getAction() + " ID: " + dto.getId());

            if ("DELETE".equalsIgnoreCase(dto.getAction())) {
                deviceRepository.deleteById(dto.getId());
            } else {
                // CREATE or UPDATE (Assignment is an update)
                // We fetch existing or create new
                MonitoredDevice device = deviceRepository.findById(dto.getId())
                        .orElse(new MonitoredDevice());

                device.setId(dto.getId());
                device.setMaxConsumption(dto.getMaxConsumption());

                // Only update userId if it's provided (not null)
                // Or if your logic dictates it can be set to null (unassigned)
                if (dto.getUserId() != null) {
                    device.setUserId(dto.getUserId());
                }

                deviceRepository.save(device);
            }
        } catch (Exception e) {
            System.err.println("Error processing device sync: " + e.getMessage());
        }
    }
}