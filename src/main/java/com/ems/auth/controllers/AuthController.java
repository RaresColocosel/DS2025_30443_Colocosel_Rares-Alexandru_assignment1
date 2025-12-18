package com.ems.auth.controllers;

import com.ems.auth.dtos.LoginRequest;
import com.ems.auth.dtos.LoginResponse;
import com.ems.auth.dtos.RegisterRequest;
import com.ems.auth.dtos.TokenResponse;
import com.ems.auth.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService service;

  public AuthController(AuthService service) {
    this.service = service;
  }

  // Admin / Postman registration – no token returned
  @PostMapping("/register")
  public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequest req) {
    service.register(req);
    return ResponseEntity.ok().build();
  }

  // Classic login
  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest req) {
    return ResponseEntity.ok(service.login(req));
  }

  // Public client self-registration – returns token
  @PostMapping("/register-client")
  public ResponseEntity<TokenResponse> registerClient(
          @Valid @RequestBody RegisterRequest request) {

    TokenResponse token = service.registerClient(request);
    return ResponseEntity.ok(token);
  }
}
