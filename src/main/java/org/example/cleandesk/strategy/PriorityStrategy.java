package org.example.cleandesk.strategy;

import org.example.cleandesk.model.Card;

import java.util.Comparator;

/** Höchste Priorität zuerst, bei Gleichstand die ältere Karte zuerst. */
public final class PriorityStrategy implements SortStrategy {

    @Override
    public String displayName() {
        return "Priorität";
    }

    @Override
    public Comparator<Card> comparator() {
        return Comparator.comparing(Card::priority, Comparator.reverseOrder())
                .thenComparing(Card::createdAt);
    }

    @Override
    public String toString() {
        return displayName();
    }
}
