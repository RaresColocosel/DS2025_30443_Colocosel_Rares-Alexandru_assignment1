package com.ems.monitoring.repositories;

import com.ems.monitoring.entities.MonitoredDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MonitoredDeviceRepository extends JpaRepository<MonitoredDevice, Long> {
    List<MonitoredDevice> findByUserId(Long userId);
}