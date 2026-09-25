package org.example.cleandesk.model;

import com.google.common.base.Strings;

import java.time.LocalDateTime;

public record FeatureCard(long id, String title, String description,
                          Priority priority, Status status, LocalDateTime createdAt) implements Card {

    public FeatureCard {
        Card.requireValid(title, priority, status, createdAt);
        description = Strings.nullToEmpty(description);
    }

    @Override
    public FeatureCard withStatus(Status newStatus) {
        return new FeatureCard(id, title, description, priority, newStatus, createdAt);
    }

    @Override
    public FeatureCard withId(long newId) {
        return new FeatureCard(newId, title, description, priority, status, createdAt);
    }
}
