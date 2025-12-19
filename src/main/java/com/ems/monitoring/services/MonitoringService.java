package com.ems.monitoring.services;

import com.ems.monitoring.entities.HourlyConsumption;
import com.ems.monitoring.repositories.HourlyConsumptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class MonitoringService {

    private final HourlyConsumptionRepository repository;

    public MonitoringService(HourlyConsumptionRepository repository) {
        this.repository = repository;
    }

    /**
     * RabbitMQ Consumer calls this to update the DB.
     */
    @Transactional
    public void processMeasurement(Long deviceId, Double value, Long timestampMillis) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestampMillis), ZoneId.systemDefault());
        // Truncate to the start of the hour (e.g., 14:23 -> 14:00)
        LocalDateTime hourStart = dateTime.withMinute(0).withSecond(0).withNano(0);

        HourlyConsumption consumption = repository.findByDeviceIdAndHourTimestamp(deviceId, hourStart)
                .orElse(new HourlyConsumption(deviceId, hourStart, 0.0));

        consumption.setTotalValue(consumption.getTotalValue() + value);
        repository.save(consumption);
    }

    /**
     * Get listings with optional filters.
     */
    public List<HourlyConsumption> getConsumption(Long deviceId, LocalDate date) {
        // 1. If Device ID and Date are provided -> Filter by both
        if (deviceId != null && date != null) {
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
            return repository.findByDeviceIdAndHourTimestampBetween(deviceId, startOfDay, endOfDay);
        }

        // 2. If only Device ID is provided -> Filter by device
        if (deviceId != null) {
            return repository.findByDeviceId(deviceId);
        }

        // 3. Otherwise -> Return everything (Admin view)
        return repository.findAll();
    }
}