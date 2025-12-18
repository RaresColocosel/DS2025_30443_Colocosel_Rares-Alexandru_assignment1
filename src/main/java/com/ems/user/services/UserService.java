package com.ems.user.services;

import com.ems.auth.entities.Credential;
import com.ems.auth.repositories.CredentialRepository;
import com.ems.user.dtos.UpdateProfileRequest;
import com.ems.user.dtos.UserDTO;
import com.ems.user.dtos.UserDetailsDTO;
import com.ems.user.dtos.builders.UserBuilder;
import com.ems.user.entities.User;
import com.ems.user.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final CredentialRepository credentialRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository,
                     CredentialRepository credentialRepository,
                     PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.credentialRepository = credentialRepository;
    this.passwordEncoder = passwordEncoder;
  }

  // ========================
  // ADMIN OPERATIONS (/users)
  // ========================

  public List<UserDTO> getAllUsers() {
    return userRepository.findAll()
            .stream()
            .map(UserBuilder::toDTO)
            .toList();
  }

  public UserDTO getUserById(Long id) {
    User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    return UserBuilder.toDTO(user);
  }

  @Transactional
  public UserDTO createUser(UserDTO dto) {
    if (userRepository.existsByUsername(dto.getUsername()) ||
            credentialRepository.existsByUsername(dto.getUsername())) {
      throw new IllegalArgumentException("Username already exists");
    }
    if (dto.getPassword() == null || dto.getPassword().isBlank()) {
      throw new IllegalArgumentException("Password is required for new user");
    }

    String role = normalizeRole(dto.getRole());

    // user_db
    User user = new User();
    user.setUsername(dto.getUsername());
    user.setFullName(dto.getFullName());
    user.setEmail(dto.getEmail());
    user.setRole(role);
    user = userRepository.save(user);

    // auth_db
    Credential credential = new Credential();
    credential.setUsername(dto.getUsername());
    credential.setPassword(passwordEncoder.encode(dto.getPassword()));
    credential.setRole(role);
    credentialRepository.save(credential);

    return UserBuilder.toDTO(user);
  }

  @Transactional
  public UserDTO updateUser(Long id, UserDTO dto) {
    User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

    String oldUsername = user.getUsername();

    String newUsername = dto.getUsername();
    if (!oldUsername.equals(newUsername)) {
      // ensure new username not already taken
      if (userRepository.existsByUsername(newUsername) ||
              credentialRepository.existsByUsername(newUsername)) {
        throw new IllegalArgumentException("Username already exists");
      }
    }

    String role = normalizeRole(dto.getRole());

    // user_db
    user.setUsername(newUsername);
    user.setFullName(dto.getFullName());
    user.setEmail(dto.getEmail());
    user.setRole(role);
    userRepository.save(user);

    // auth_db
    Credential credential = credentialRepository.findByUsername(oldUsername)
            .orElseThrow(() -> new IllegalStateException("Credential missing for user " + oldUsername));

    credential.setUsername(newUsername);
    credential.setRole(role);

    if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
      credential.setPassword(passwordEncoder.encode(dto.getPassword()));
    }

    credentialRepository.save(credential);

    return UserBuilder.toDTO(user);
  }

  @Transactional
  public void deleteUser(Long id) {
    User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

    String username = user.getUsername();

    // delete credentials first
    credentialRepository.findByUsername(username)
            .ifPresent(credentialRepository::delete);

    userRepository.delete(user);
  }

  // ===================================
  // SELF SERVICE OPERATIONS (/client/me)
  // ===================================

  public UserDetailsDTO getDetailsForUsername(String username) {
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
    return UserBuilder.toDetails(user);
  }

  @Transactional
  public UserDetailsDTO updateCurrentUser(String username, UpdateProfileRequest req) {
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

    user.setFullName(req.getFullName());
    user.setEmail(req.getEmail());
    userRepository.save(user);

    if (req.getPassword() != null && !req.getPassword().isBlank()) {
      Credential credential = credentialRepository.findByUsername(username)
              .orElseThrow(() -> new IllegalStateException("Credential missing for user " + username));
      credential.setPassword(passwordEncoder.encode(req.getPassword()));
      credentialRepository.save(credential);
    }

    return UserBuilder.toDetails(user);
  }

  private String normalizeRole(String role) {
    if (role == null || role.isBlank()) {
      return "CLIENT";
    }
    return role.toUpperCase();
  }
}
