package org.example.cleandesk.service;

import org.example.cleandesk.InMemoryCardRepository;
import org.example.cleandesk.model.*;
import org.example.cleandesk.observer.BoardEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardServiceTest {

    private BoardService service;
    private final List<BoardEvent> receivedEvents = new ArrayList<>();

    @BeforeEach
    void setUp() {
        service = new BoardService(new InMemoryCardRepository(), 1);
        service.addObserver(receivedEvents::add);
    }

    @Test
    void addedCardAppearsInTodoColumn() {
        Card card = service.addCard(CardType.FEATURE, "Dark Mode", "", Priority.MEDIUM);

        assertEquals(List.of(card), service.cardsIn(Status.TODO));
        assertInstanceOf(BoardEvent.CardAdded.class, receivedEvents.getLast());
    }

    @Test
    void movingCardForwardNotifiesObservers() {
        Card card = service.addCard(CardType.TASK, "Doku schreiben", "", Priority.LOW);

        service.moveForward(card.id());

        assertEquals(new BoardEvent.CardMoved(card.withStatus(Status.IN_PROGRESS), Status.TODO, Status.IN_PROGRESS),
                receivedEvents.getLast());
    }

    @Test
    void editingCardUpdatesItsContentAndNotifiesObservers() {
        Card original = service.addCard(CardType.TASK, "Alte Aufgabe", "Alt", Priority.LOW);

        Card updated = service.updateCard(
                original.id(), CardType.FEATURE, "Neue Aufgabe", "Neu", Priority.HIGH);

        assertEquals("Neue Aufgabe", service.cardsIn(Status.TODO).getFirst().title());
        assertEquals("Neu", updated.description());
        assertEquals(Priority.HIGH, updated.priority());
        assertInstanceOf(FeatureCard.class, updated);
        assertEquals(new BoardEvent.CardUpdated(updated), receivedEvents.getLast());
    }

    @Test
    void cardInDoneCannotMoveFurther() {
        Card card = service.addCard(CardType.TASK, "Fertig", "", Priority.LOW);
        service.moveCard(card.id(), Status.DONE);
        int eventsBefore = receivedEvents.size();

        service.moveForward(card.id());

        assertEquals(eventsBefore, receivedEvents.size());
        assertEquals(1, service.cardsIn(Status.DONE).size());
    }

    @Test
    void deletingUnknownCardThrows() {
        assertThrows(IllegalArgumentException.class, () -> service.deleteCard(42));
    }
}
