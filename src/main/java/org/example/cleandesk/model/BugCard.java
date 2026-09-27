package org.example.cleandesk.model;

import com.google.common.base.Strings;

import java.time.LocalDateTime;


/**
 * Ein Bug-Karte mit einer eindeutigen ID, einem Titel, einer Beschreibung, einer Priorität, einem Status und einem Erstellungsdatum.
 * @param id
 * @param title
 * @param description
 * @param priority
 * @param status
 * @param createdAt
 */
public record BugCard(long id, String title, String description,
                      Priority priority, Status status, LocalDateTime createdAt) implements Card {

    public BugCard {
        Card.requireValid(title, priority, status, createdAt);
        description = Strings.nullToEmpty(description);
    }

    @Override
    public BugCard withStatus(Status newStatus) {
        return new BugCard(id, title, description, priority, newStatus, createdAt);
    }

    @Override
    public BugCard withId(long newId) {
        return new BugCard(newId, title, description, priority, status, createdAt);
    }
}
