package com.ems.device.dtos;

public class DeviceDetailsDTO {

    private Long id;
    private String name;
    private String description;
    private Double maxConsumptionW;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getMaxConsumptionW() { return maxConsumptionW; }
    public void setMaxConsumptionW(Double maxConsumptionW) { this.maxConsumptionW = maxConsumptionW; }
}
