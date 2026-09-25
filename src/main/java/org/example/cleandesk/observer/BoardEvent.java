package org.example.cleandesk.observer;

import org.example.cleandesk.model.Card;
import org.example.cleandesk.model.Status;
import org.example.cleandesk.strategy.SortStrategy;

/** Alle Ereignisse, die auf dem Board passieren können. */
public sealed interface BoardEvent {
    record CardAdded(Card card) implements BoardEvent {}
    record CardUpdated(Card card) implements BoardEvent {}
    record CardMoved(Card card, Status from, Status to) implements BoardEvent {}
    record CardDeleted(Card card) implements BoardEvent {}
    record SortingChanged(SortStrategy strategy) implements BoardEvent {}
}
