package com.ems.user.dtos;

import java.io.Serializable;

public class UserSyncDTO implements Serializable {
    private Long id;
    private String username;
    private String action; // "CREATE", "UPDATE", "DELETE"

    public UserSyncDTO() {}

    public UserSyncDTO(Long id, String username, String action) {
        this.id = id;
        this.username = username;
        this.action = action;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
}