package com.ems.monitoring.repositories;

import com.ems.monitoring.entities.HourlyConsumption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HourlyConsumptionRepository extends JpaRepository<HourlyConsumption, Long> {

    // Used by RabbitMQ Consumer to aggregate data
    Optional<HourlyConsumption> findByDeviceIdAndHourTimestamp(Long deviceId, LocalDateTime hourTimestamp);

    // Used by Controller to filter by Device
    List<HourlyConsumption> findByDeviceId(Long deviceId);

    // NEW: Used by Controller to filter by Device AND Date
    List<HourlyConsumption> findByDeviceIdAndHourTimestampBetween(Long deviceId, LocalDateTime start, LocalDateTime end);
}