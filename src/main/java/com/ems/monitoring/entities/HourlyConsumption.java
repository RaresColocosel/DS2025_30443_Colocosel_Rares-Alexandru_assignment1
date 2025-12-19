package com.ems.monitoring.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "hourly_consumption")
public class HourlyConsumption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id", nullable = false)
    private Long deviceId;

    @Column(name = "hour_timestamp", nullable = false)
    private LocalDateTime hourTimestamp;

    @Column(name = "total_value")
    private Double totalValue;

    public HourlyConsumption() {}

    public HourlyConsumption(Long deviceId, LocalDateTime hourTimestamp, Double totalValue) {
        this.deviceId = deviceId;
        this.hourTimestamp = hourTimestamp;
        this.totalValue = totalValue;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDeviceId() { return deviceId; }
    public void setDeviceId(Long deviceId) { this.deviceId = deviceId; }
    public LocalDateTime getHourTimestamp() { return hourTimestamp; }
    public void setHourTimestamp(LocalDateTime hourTimestamp) { this.hourTimestamp = hourTimestamp; }
    public Double getTotalValue() { return totalValue; }
    public void setTotalValue(Double totalValue) { this.totalValue = totalValue; }
}