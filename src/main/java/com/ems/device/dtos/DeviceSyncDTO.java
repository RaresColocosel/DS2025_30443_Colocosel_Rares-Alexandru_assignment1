package com.ems.device.dtos;

import java.io.Serializable;

public class DeviceSyncDTO implements Serializable {
    private Long id;
    private Double maxConsumption;
    private Long userId; // We only need the User ID for synchronization
    private String action; // "CREATE", "UPDATE", "DELETE"

    public DeviceSyncDTO() {}

    public DeviceSyncDTO(Long id, Double maxConsumption, Long userId, String action) {
        this.id = id;
        this.maxConsumption = maxConsumption;
        this.userId = userId;
        this.action = action;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Double getMaxConsumption() { return maxConsumption; }
    public void setMaxConsumption(Double maxConsumption) { this.maxConsumption = maxConsumption; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
}