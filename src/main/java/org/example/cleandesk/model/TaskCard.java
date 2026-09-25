package org.example.cleandesk.model;

import com.google.common.base.Strings;

import java.time.LocalDateTime;

public record TaskCard(long id, String title, String description,
                       Priority priority, Status status, LocalDateTime createdAt) implements Card {

    public TaskCard {
        Card.requireValid(title, priority, status, createdAt);
        description = Strings.nullToEmpty(description);
    }

    @Override
    public TaskCard withStatus(Status newStatus) {
        return new TaskCard(id, title, description, priority, newStatus, createdAt);
    }

    @Override
    public TaskCard withId(long newId) {
        return new TaskCard(newId, title, description, priority, status, createdAt);
    }
}
