package com.ems.auth.repositories;

import com.ems.auth.entities.Credential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CredentialRepository extends JpaRepository<Credential, Long> {

  Optional<Credential> findByUsername(String username);

  boolean existsByUsername(String username);
}
