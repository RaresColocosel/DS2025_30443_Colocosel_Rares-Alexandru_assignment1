package com.ems.user.controllers;

import com.ems.user.dtos.UpdateProfileRequest;
import com.ems.user.dtos.UserDetailsDTO;
import com.ems.user.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client")
public class MeController {

    private final UserService userService;

    public MeController(UserService userService) {
        this.userService = userService;
    }

    // GET /client/me  -> show own profile
    @GetMapping("/me")
    public ResponseEntity<UserDetailsDTO> getMyProfile(Authentication auth) {
        String username = auth.getName();
        return ResponseEntity.ok(userService.getDetailsForUsername(username));
    }

    // PUT /client/me  -> update own profile + optional password
    @PutMapping("/me")
    public ResponseEntity<UserDetailsDTO> updateMyProfile(Authentication auth,
                                                          @Valid @RequestBody UpdateProfileRequest req) {
        String username = auth.getName();
        return ResponseEntity.ok(userService.updateCurrentUser(username, req));
    }
}
