package com.ems.monitoring.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "monitored_users")
public class MonitoredUser {
    @Id
    private Long id; // Same ID as in user_db
    private String username;

    public MonitoredUser() {}
    public MonitoredUser(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}