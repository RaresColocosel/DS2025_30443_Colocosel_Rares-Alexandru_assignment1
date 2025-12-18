package com.ems.user.controllers;

import com.ems.user.dtos.UserDTO;
import com.ems.user.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  // GET /users
  @GetMapping
  public ResponseEntity<List<UserDTO>> getAllUsers() {
    return ResponseEntity.ok(userService.getAllUsers());
  }

  // GET /users/{id}
  @GetMapping("/{id}")
  public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
    return ResponseEntity.ok(userService.getUserById(id));
  }

  // POST /users  (admin creates account + credentials)
  @PostMapping
  public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO dto) {
    return ResponseEntity.ok(userService.createUser(dto));
  }

  // PUT /users/{id}
  @PutMapping("/{id}")
  public ResponseEntity<UserDTO> updateUser(@PathVariable Long id,
                                            @Valid @RequestBody UserDTO dto) {
    return ResponseEntity.ok(userService.updateUser(id, dto));
  }

  // DELETE /users/{id}
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }
}
