package org.example.cleandesk.strategy;

import org.example.cleandesk.model.Card;

import java.util.Comparator;

/** Neueste Karten zuerst. */
public final class DateStrategy implements SortStrategy {

    @Override
    public String displayName() {
        return "Datum";
    }

    @Override
    public Comparator<Card> comparator() {
        return Comparator.comparing(Card::createdAt, Comparator.reverseOrder());
    }

    @Override
    public String toString() {
        return displayName();
    }
}
