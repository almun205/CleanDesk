package org.example.cleandesk.model;

/** Reihenfolge ist relevant: LOW < MEDIUM < HIGH. */
public enum Priority {
    LOW("Niedrig"),
    MEDIUM("Mittel"),
    HIGH("Hoch");

    private final String displayName;

    Priority(String displayName) {
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
