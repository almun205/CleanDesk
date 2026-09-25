package org.example.cleandesk.observer;

@FunctionalInterface
public interface BoardObserver {
    void onBoardChanged(BoardEvent event);
}
