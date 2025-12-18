package com.ems.device.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DeviceCreateRequest {

  @NotBlank
  private String name;

  private String description;

  @NotNull
  private Double maxConsumptionW;

  public DeviceCreateRequest() {
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Double getMaxConsumptionW() {
    return maxConsumptionW;
  }

  public void setMaxConsumptionW(Double maxConsumptionW) {
    this.maxConsumptionW = maxConsumptionW;
  }
}
