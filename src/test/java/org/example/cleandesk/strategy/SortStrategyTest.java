package org.example.cleandesk.strategy;

import org.example.cleandesk.factory.CardFactory;
import org.example.cleandesk.model.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SortStrategyTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 9, 22, 10, 0);

    private final Card newLowCard =
            CardFactory.create(1, CardType.TASK, "Neu", "", Priority.LOW, Status.TODO, NOW);
    private final Card oldHighCard =
        CardFactory.create(2, CardType.BUG, "Alt", "", Priority.HIGH, Status.TODO, NOW.minusDays(1));

    @Test
    void priorityStrategyPutsHighestPriorityFirst() {
        assertEquals(List.of(oldHighCard, newLowCard),
                new PriorityStrategy().sort(List.of(newLowCard, oldHighCard)));
    }

    @Test
    void dateStrategyPutsNewestFirst() {
        assertEquals(List.of(newLowCard, oldHighCard),
                new DateStrategy().sort(List.of(oldHighCard, newLowCard)));
    }
}
