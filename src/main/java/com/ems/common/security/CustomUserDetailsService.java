package com.ems.common.security;

import com.ems.auth.entities.Credential;
import com.ems.auth.repositories.CredentialRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final CredentialRepository credentialRepository;

    public CustomUserDetailsService(CredentialRepository credentialRepository) {
        this.credentialRepository = credentialRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Credential cred = credentialRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(cred.getUsername())
                .password(cred.getPassword())
                // IMPORTANT: authority is exactly "ADMIN" or "CLIENT"
                .authorities(cred.getRole())
                .build();
    }
}
