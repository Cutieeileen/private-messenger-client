package ru.anonymus.simplemessenger.adapters.models;

public class Right {
    private String name;
    private boolean isSelected;

    public Right(String name) {
        this.name = name;
        this.isSelected = false;
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
}
