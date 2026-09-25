package org.example.cleandesk.factory;

import org.example.cleandesk.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

class CardFactoryTest {

    @Test
    void createsBugCardForBugType() {
        Card card = CardFactory.createNew(CardType.BUG, "Login kaputt", "", Priority.HIGH);

        assertInstanceOf(BugCard.class, card);
        assertEquals(Status.TODO, card.status());
    }

    @ParameterizedTest
    @EnumSource(CardType.class)
    void createdCardHasRequestedType(CardType type) {
        assertEquals(type, CardFactory.createNew(type, "Titel", "", Priority.LOW).type());
    }

    @Test
    void rejectsBlankTitle() {
        assertThrows(IllegalArgumentException.class,
                () -> CardFactory.createNew(CardType.TASK, "   ", "", Priority.LOW));
    }
}
