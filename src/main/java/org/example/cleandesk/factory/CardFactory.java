package org.example.cleandesk.factory;

import org.example.cleandesk.model.*;

import java.time.LocalDateTime;

/** Factory Pattern: Die Aufrufer müssen die konkreten Karten-Klassen nicht kennen. */
public final class CardFactory {

    private CardFactory() {
    }

    /** Neue Karte: noch ohne ID, Status "To Do", Zeitstempel "jetzt". */
    public static Card createNew(CardType type, String title, String description, Priority priority) {
        return create(0, type, title, description, priority, Status.TODO, LocalDateTime.now());
    }

    public static Card create(long id, CardType type, String title, String description,
                              Priority priority, Status status, LocalDateTime createdAt) {
        return switch (type) {
            case BUG -> new BugCard(id, title, description, priority, status, createdAt);
            case FEATURE -> new FeatureCard(id, title, description, priority, status, createdAt);
            case TASK -> new TaskCard(id, title, description, priority, status, createdAt);
        };
    }
}
