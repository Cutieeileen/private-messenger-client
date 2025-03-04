package ru.anonymus.simplemessenger.dto;

public class RoleDto {

    private String name;
    private String colorCode;
    private Long id;

    public RoleDto() {
    }

    public RoleDto(String name, String colorCode, Long id) {
        this.name = name;
        this.colorCode = colorCode;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
