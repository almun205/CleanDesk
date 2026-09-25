package org.example.cleandesk.service;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimaps;
import org.example.cleandesk.factory.CardFactory;
import org.example.cleandesk.model.*;
import org.example.cleandesk.observer.BoardEvent;
import org.example.cleandesk.observer.BoardObserver;
import org.example.cleandesk.persistence.CardRepository;
import org.example.cleandesk.strategy.PriorityStrategy;
import org.example.cleandesk.strategy.SortStrategy;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/** Zentrale Geschäftslogik. Informiert alle Observer über jede Änderung. */
public final class BoardService {

    private final CardRepository repository;
    private final long boardId;
    private final Map<Long, Card> cardsById = new LinkedHashMap<>();
    private final List<BoardObserver> observers = new CopyOnWriteArrayList<>();
    private SortStrategy sortStrategy = new PriorityStrategy();

    public BoardService(CardRepository repository, long boardId) {
        this.repository = checkNotNull(repository);
        this.boardId = boardId;
        repository.findByBoard(boardId).forEach(card -> cardsById.put(card.id(), card));
    }

    public void addObserver(BoardObserver observer) {
        observers.add(checkNotNull(observer));
    }

    public void removeObserver(BoardObserver observer) {
        observers.remove(observer);
    }

    public Card addCard(CardType type, String title, String description, Priority priority) {
        Card saved = repository.insert(boardId, CardFactory.createNew(type, title, description, priority));
        cardsById.put(saved.id(), saved);
        notifyObservers(new BoardEvent.CardAdded(saved));
        return saved;
    }

    public void moveCard(long cardId, Status target) {
        Card card = findCard(cardId);
        if (card.status() == target) {
            return;
        }
        Card moved = card.withStatus(target);
        repository.updateStatus(cardId, target);
        cardsById.put(cardId, moved);
        notifyObservers(new BoardEvent.CardMoved(moved, card.status(), target));
    }

    public Card updateCard(long cardId, CardType type, String title, String description, Priority priority) {
        Card existing = findCard(cardId);
        Card updated = CardFactory.create(
                existing.id(), type, title, description, priority, existing.status(), existing.createdAt());
        repository.update(updated);
        cardsById.put(cardId, updated);
        notifyObservers(new BoardEvent.CardUpdated(updated));
        return updated;
    }

    public void moveForward(long cardId) {
        findCard(cardId).status().next().ifPresent(next -> moveCard(cardId, next));
    }

    public void moveBackward(long cardId) {
        findCard(cardId).status().previous().ifPresent(previous -> moveCard(cardId, previous));
    }

    public void deleteCard(long cardId) {
        Card removed = findCard(cardId);
        repository.delete(cardId);
        cardsById.remove(cardId);
        notifyObservers(new BoardEvent.CardDeleted(removed));
    }

    public void setSortStrategy(SortStrategy strategy) {
        this.sortStrategy = checkNotNull(strategy);
        notifyObservers(new BoardEvent.SortingChanged(strategy));
    }

    /** Karten nach Spalte gruppiert, in jeder Spalte nach der aktuellen Strategie sortiert (Guava). */
    public ImmutableListMultimap<Status, Card> cardsByStatus() {
        return Multimaps.index(sortStrategy.sort(cardsById.values()), Card::status);
    }

    public List<Card> cardsIn(Status status) {
        return cardsByStatus().get(status);
    }

    private Card findCard(long cardId) {
        Card card = cardsById.get(cardId);
        checkArgument(card != null, "Karte %s existiert nicht", cardId);
        return card;
    }

    private void notifyObservers(BoardEvent event) {
        observers.forEach(observer -> observer.onBoardChanged(event));
    }
}
