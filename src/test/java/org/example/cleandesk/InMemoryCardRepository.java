package org.example.cleandesk;

import org.example.cleandesk.model.Card;
import org.example.cleandesk.model.Status;
import org.example.cleandesk.persistence.CardRepository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Test-Double: So lässt sich der Service ohne Datenbank testen. */
public class InMemoryCardRepository implements CardRepository {

    private final Map<Long, Card> cards = new LinkedHashMap<>();
    private long nextId = 1;

    @Override
    public List<Card> findByBoard(long boardId) {
        return List.copyOf(cards.values());
    }

    @Override
    public Card insert(long boardId, Card card) {
        Card saved = card.withId(nextId++);
        cards.put(saved.id(), saved);
        return saved;
    }

    @Override
    public void update(Card card) {
        cards.replace(card.id(), card);
    }

    @Override
    public void updateStatus(long cardId, Status status) {
        cards.computeIfPresent(cardId, (id, card) -> card.withStatus(status));
    }

    @Override
    public void delete(long cardId) {
        cards.remove(cardId);
    }
}
