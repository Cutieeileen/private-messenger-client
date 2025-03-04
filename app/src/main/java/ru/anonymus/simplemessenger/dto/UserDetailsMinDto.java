package ru.anonymus.simplemessenger.dto;

import java.time.LocalDateTime;
import java.util.List;

public class UserDetailsMinDto {

    private String username;
    private String description;
    private String registeredAt;
    private List<RoleDto> roles;

    public UserDetailsMinDto() {

    }

    public UserDetailsMinDto(String username, String description, String registeredAt, List<RoleDto> roles) {
        this.username = username;
        this.description = description;
        this.registeredAt = registeredAt;
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(String registeredAt) {
        this.registeredAt = registeredAt;
    }

    public List<RoleDto> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleDto> roles) {
        this.roles = roles;
    }
}
