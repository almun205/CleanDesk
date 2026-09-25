package org.example.cleandesk.model;

public enum CardType {
    BUG("Bug"),
    FEATURE("Feature"),
    TASK("Task");

    private final String displayName;

    CardType(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
