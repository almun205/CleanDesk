package org.example.cleandesk.strategy;

import org.example.cleandesk.model.Card;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/** Strategy Pattern: Die Sortierung lässt sich zur Laufzeit austauschen. */
public interface SortStrategy {

    String displayName();

    Comparator<Card> comparator();

    default List<Card> sort(Collection<Card> cards) {
        return cards.stream().sorted(comparator()).toList();
    }
}
