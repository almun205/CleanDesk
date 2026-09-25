package org.example.cleandesk.persistence;

import org.example.cleandesk.model.Card;
import org.example.cleandesk.model.Status;

import java.util.List;

public interface CardRepository {
    List<Card> findByBoard(long boardId);
    Card insert(long boardId, Card card);
    void update(Card card);
    void updateStatus(long cardId, Status status);
    void delete(long cardId);
}
