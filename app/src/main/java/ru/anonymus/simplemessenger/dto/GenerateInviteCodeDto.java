package ru.anonymus.simplemessenger.dto;

import java.util.List;

public class GenerateInviteCodeDto {
    private List<String> authorityNames;
    private List<Long> rolesIds;

    public GenerateInviteCodeDto() {
    }

    public GenerateInviteCodeDto(List<String> authorityNames, List<Long> rolesIds) {
        this.authorityNames = authorityNames;
        this.rolesIds = rolesIds;
    }

    public List<String> getAuthorityNames() {
        return authorityNames;
    }

    public void setAuthorityNames(List<String> authorityNames) {
        this.authorityNames = authorityNames;
    }

    public List<Long> getRolesIds() {
        return rolesIds;
    }

    public void setRolesIds(List<Long> rolesIds) {
        this.rolesIds = rolesIds;
    }
}
