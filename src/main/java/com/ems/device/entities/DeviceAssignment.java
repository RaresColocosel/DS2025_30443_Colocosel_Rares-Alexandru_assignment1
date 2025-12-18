package com.ems.device.entities;

import jakarta.persistence.*;

@Entity
@Table(
        name = "device_assignment",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_device",
                        columnNames = {"user_id", "device_id"}
                )
        }
)
public class DeviceAssignment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // user is stored only by id (user_db is a different datasource)
  @Column(name = "user_id", nullable = false)
  private Long userId;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "device_id", nullable = false)
  private Device device;

  public DeviceAssignment() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Device getDevice() {
    return device;
  }

  public void setDevice(Device device) {
    this.device = device;
  }
}
