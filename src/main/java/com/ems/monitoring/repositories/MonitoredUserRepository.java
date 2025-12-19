package com.ems.monitoring.repositories;

import com.ems.monitoring.entities.MonitoredUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonitoredUserRepository extends JpaRepository<MonitoredUser, Long> {
}