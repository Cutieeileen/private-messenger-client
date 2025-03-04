package ru.anonymus.simplemessenger.adapters.models;

public class Role {
    private Long id;
    private String name;
    private boolean isSelected;
    private String colorCode;

    public Role() {
    }

    public Role(Long id, String name, boolean isSelected, String colorCode) {
        this.id = id;
        this.name = name;
        this.isSelected = isSelected;
        this.colorCode = colorCode;
    }

    public String getName() {
        return name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
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
