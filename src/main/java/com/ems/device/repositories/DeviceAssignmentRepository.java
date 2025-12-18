package com.ems.device.repositories;

import com.ems.device.entities.DeviceAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceAssignmentRepository extends JpaRepository<DeviceAssignment, Long> {

  List<DeviceAssignment> findByUserId(Long userId);

  boolean existsByUserIdAndDevice_Id(Long userId, Long deviceId);

  void deleteByUserIdAndDevice_Id(Long userId, Long deviceId);
}
