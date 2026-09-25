package org.example.cleandesk.model;

import java.util.Optional;

/** Die Spalten des Kanban-Boards in ihrer natürlichen Reihenfolge. */
public enum Status {
    TODO("To Do"),
    IN_PROGRESS("In Progress"),
    DONE("Done");

    private final String displayName;

    Status(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }

    public Optional<Status> next() {
        return ordinal() < values().length - 1 ? Optional.of(values()[ordinal() + 1]) : Optional.empty();
    }

    public Optional<Status> previous() {
        return ordinal() > 0 ? Optional.of(values()[ordinal() - 1]) : Optional.empty();
    }

    @Override
    public String toString() {
        return displayName;
    }
}
