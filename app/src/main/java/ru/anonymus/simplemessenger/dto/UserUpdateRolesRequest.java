package ru.anonymus.simplemessenger.dto;

import java.util.List;

public class UserUpdateRolesRequest {

    private Long userId;
    private List<Long> roleIds;

    public UserUpdateRolesRequest() {
    }

    public UserUpdateRolesRequest(Long userId, List<Long> roleIds) {
        this.userId = userId;
        this.roleIds = roleIds;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }
}
