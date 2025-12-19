package com.ems.monitoring.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "monitored_devices")
public class MonitoredDevice {
    @Id
    private Long id; // Same ID as in device_db
    private Double maxConsumption;
    private Long userId; // The user assigned to this device

    public MonitoredDevice() {}
    public MonitoredDevice(Long id, Double maxConsumption, Long userId) {
        this.id = id;
        this.maxConsumption = maxConsumption;
        this.userId = userId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Double getMaxConsumption() { return maxConsumption; }
    public void setMaxConsumption(Double maxConsumption) { this.maxConsumption = maxConsumption; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}