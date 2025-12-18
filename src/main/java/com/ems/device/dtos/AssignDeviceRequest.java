package com.ems.device.dtos;

import jakarta.validation.constraints.NotNull;

public class AssignDeviceRequest {

  @NotNull
  private Long userId;

  public AssignDeviceRequest() {
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }
}
