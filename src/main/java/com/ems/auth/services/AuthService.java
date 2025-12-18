package com.ems.auth.services;

import com.ems.auth.dtos.LoginRequest;
import com.ems.auth.dtos.LoginResponse;
import com.ems.auth.dtos.RegisterRequest;
import com.ems.auth.dtos.TokenResponse;
import com.ems.auth.entities.Credential;
import com.ems.auth.repositories.CredentialRepository;
import com.ems.common.jwt.JwtService;
import com.ems.user.entities.User;
import com.ems.user.repositories.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

  private final CredentialRepository credentialRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final UserRepository userRepository;

  public AuthService(CredentialRepository credentialRepository,
                     PasswordEncoder passwordEncoder,
                     JwtService jwtService,
                     UserRepository userRepository) {
    this.credentialRepository = credentialRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.userRepository = userRepository;
  }

  /**
   * ADMIN registration (used by admin / Postman).
   */
  @Transactional
  public void register(RegisterRequest req) {
    String role = normalizeRole(req.getRole());
    registerInternal(req, role);
  }

  /**
   * Public CLIENT self-registration. Always forces role = CLIENT and returns JWT.
   */
  @Transactional
  public TokenResponse registerClient(RegisterRequest req) {
    String role = "CLIENT";
    User user = registerInternal(req, role);

    String token = jwtService.generateToken(user.getUsername(), role);

    return new TokenResponse(
            token,
            user.getId(),
            user.getUsername(),
            role
    );
  }

  private User registerInternal(RegisterRequest req, String role) {
    if (credentialRepository.existsByUsername(req.getUsername())) {
      throw new IllegalArgumentException("Username already exists");
    }

    // auth_db: credentials
    Credential credential = new Credential();
    credential.setUsername(req.getUsername());
    credential.setPassword(passwordEncoder.encode(req.getPassword()));
    credential.setRole(role);      // "ADMIN" or "CLIENT"
    credentialRepository.save(credential);

    // user_db: user row
    User user = new User();
    user.setUsername(req.getUsername());
    user.setRole(role);
    user.setEmail(req.getEmail());
    user.setFullName(
            (req.getFullName() != null && !req.getFullName().isBlank())
                    ? req.getFullName()
                    : req.getUsername()
    );

    return userRepository.save(user);
  }

  private String normalizeRole(String role) {
    if (role == null || role.isBlank()) {
      return "CLIENT";
    }
    // just in case someone sends "ROLE_ADMIN"
    return role.startsWith("ROLE_") ? role.substring("ROLE_".length()) : role;
  }

  public LoginResponse login(LoginRequest req) {
    Credential credential = credentialRepository.findByUsername(req.getUsername())
            .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

    if (!passwordEncoder.matches(req.getPassword(), credential.getPassword())) {
      throw new BadCredentialsException("Invalid username or password");
    }

    String token = jwtService.generateToken(
            credential.getUsername(),
            credential.getRole()
    );
    return new LoginResponse(token, credential.getRole());
  }
}
