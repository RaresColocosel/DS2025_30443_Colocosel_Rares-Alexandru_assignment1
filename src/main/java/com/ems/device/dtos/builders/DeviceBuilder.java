package com.ems.device.dtos.builders;

import com.ems.device.dtos.DeviceDTO;
import com.ems.device.entities.Device;

public final class DeviceBuilder {

  private DeviceBuilder() {
  }

  public static DeviceDTO toDTO(Device d) {
    if (d == null) return null;

    DeviceDTO dto = new DeviceDTO();
    dto.setId(d.getId());
    dto.setName(d.getName());
    dto.setDescription(d.getDescription());
    dto.setMaxConsumptionW(d.getMaxConsumptionW());
    return dto;
  }
}
