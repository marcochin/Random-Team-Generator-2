package com.marcochin.teamrandomizer.model;

public class Player {
    private boolean checkboxVisible;
    private boolean included;
    private String name;

    public Player(String name) {
        this.name = name;
        included = true; // Player is included in the random pool by default
    }

    public boolean isCheckboxVisible() {
        return checkboxVisible;
    }

    public void setCheckboxVisible(boolean checkboxVisible) {
        this.checkboxVisible = checkboxVisible;
    }

    public boolean isIncluded() {
        return included;
    }

    public void setIncluded(boolean included) {
        this.included = included;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
