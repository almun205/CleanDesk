package org.example.cleandesk.model;

import java.time.LocalDateTime;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Eine Karte auf dem Board. Das Interface ist "sealed": Der Compiler kennt alle
 * Kartentypen, deshalb braucht ein switch mit Pattern-Matching keinen default-Zweig.
 */
public sealed interface Card permits BugCard, FeatureCard, TaskCard {

    long id();
    String title();
    String description();
    Priority priority();
    Status status();
    LocalDateTime createdAt();

    Card withStatus(Status newStatus);

    Card withId(long newId);

    default CardType type() {
        return switch (this) {
            case BugCard _ -> CardType.BUG;
            case FeatureCard _ -> CardType.FEATURE;
            case TaskCard _ -> CardType.TASK;
        };
    }

    /** Gemeinsame Validierung für alle Karten-Records. */
    static void requireValid(String title, Priority priority, Status status, LocalDateTime createdAt) {
        checkArgument(title != null && !title.isBlank(), "Titel darf nicht leer sein");
        checkNotNull(priority, "priority");
        checkNotNull(status, "status");
        checkNotNull(createdAt, "createdAt");
    }
}
