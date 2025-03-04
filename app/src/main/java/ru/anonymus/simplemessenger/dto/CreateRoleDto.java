package ru.anonymus.simplemessenger.dto;

public class CreateRoleDto {

    String name;
    String colorCode;

    public CreateRoleDto(String name, String colorCode) {
        this.name = name;
        this.colorCode = colorCode;
    }

    public CreateRoleDto() {
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
}
