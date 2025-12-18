package com.ems.user.dtos.builders;

import com.ems.user.dtos.UserDTO;
import com.ems.user.dtos.UserDetailsDTO;
import com.ems.user.entities.User;

public final class UserBuilder {

  private UserBuilder() {
  }

  public static UserDTO toDTO(User user) {
    if (user == null) {
      return null;
    }
    UserDTO dto = new UserDTO();
    dto.setId(user.getId());
    dto.setUsername(user.getUsername());
    dto.setFullName(user.getFullName());
    dto.setEmail(user.getEmail());
    dto.setRole(user.getRole());
    return dto;
  }

  public static UserDetailsDTO toDetails(User user) {
    if (user == null) {
      return null;
    }
    UserDetailsDTO dto = new UserDetailsDTO();
    dto.setId(user.getId());
    dto.setUsername(user.getUsername());
    dto.setFullName(user.getFullName());
    dto.setEmail(user.getEmail());
    dto.setRole(user.getRole());
    return dto;
  }
}
