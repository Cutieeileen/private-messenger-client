package ru.anonymus.simplemessenger.dto;


import java.util.List;

public class UserDto {
    private Long id;
    private String username;
    private List<String> authorities;
    private List<RoleDto> roles;

    public UserDto(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    public UserDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

