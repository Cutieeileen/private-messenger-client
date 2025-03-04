package ru.anonymus.simplemessenger.dto;


import java.time.LocalDateTime;
import java.util.List;


public class UserDetailsDto {

    private Long id;
    private String username;
    private String registeredAt;
    private String description;
    private List<String> authorities;
    private List<RoleDto> roles;

    public UserDetailsDto(Long id, String username, String registeredAt, String description, List<String> authorities, List<RoleDto> roles) {
        this.id = id;
        this.username = username;
        this.registeredAt = registeredAt;
        this.description = description;
        this.authorities = authorities;
        this.roles = roles;
    }

    public UserDetailsDto() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(String registeredAt) {
        this.registeredAt = registeredAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }

    public List<RoleDto> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleDto> roles) {
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
