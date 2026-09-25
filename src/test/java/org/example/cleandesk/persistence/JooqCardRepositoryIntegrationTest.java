package org.example.cleandesk.persistence;

import org.example.cleandesk.factory.CardFactory;
import org.example.cleandesk.model.*;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.example.cleandesk.persistence.SqlTables.*;

/** Integrationstest gegen eine explizit konfigurierte PostgreSQL-Testdatenbank. */
class JooqCardRepositoryIntegrationTest {

    private DSLContext dsl;
    private JooqCardRepository repository;
    private long boardId;

    @BeforeEach
    void setUp() {
        String url = System.getenv("CLEANDESK_TEST_DB_URL");
        assumeTrue(url != null && !url.isBlank(),
                "CLEANDESK_TEST_DB_URL is not set; PostgreSQL integration test skipped");

        String user = setting("CLEANDESK_TEST_DB_USER", "cleandesk");
        String password = setting("CLEANDESK_TEST_DB_PASSWORD", "");
        dsl = DatabaseManager.initializeSchema(DatabaseManager.connect(url, user, password, SQLDialect.POSTGRES));
        repository = new JooqCardRepository(dsl);
        boardId = new JooqBoardRepository(dsl).findOrCreate("Test-Board-" + System.nanoTime()).id();
    }

    @AfterEach
    void cleanUp() {
        if (dsl != null && boardId > 0) {
            dsl.deleteFrom(CARD)
                    .where(CARD_BOARD_ID.eq(boardId))
                    .execute();
            dsl.deleteFrom(BOARD)
                    .where(BOARD_ID.eq(boardId))
                    .execute();
        }
    }

    @Test
    void insertedCardCanBeLoaded() {
        Card saved = repository.insert(boardId, CardFactory.createNew(CardType.BUG, "NPE", "", Priority.HIGH));

        assertTrue(saved.id() > 0);
        assertEquals(1, repository.findByBoard(boardId).size());
        assertInstanceOf(BugCard.class, repository.findByBoard(boardId).getFirst());
    }

    @Test
    void statusUpdateIsPersisted() {
        Card saved = repository.insert(boardId, CardFactory.createNew(CardType.TASK, "Deploy", "", Priority.LOW));

        repository.updateStatus(saved.id(), Status.DONE);

        assertEquals(Status.DONE, repository.findByBoard(boardId).getFirst().status());
    }

    @Test
    void editedCardIsPersisted() {
        Card saved = repository.insert(boardId, CardFactory.createNew(CardType.TASK, "Alt", "Vorher", Priority.LOW));
        Card updated = CardFactory.create(
                saved.id(), CardType.FEATURE, "Neu", "Nachher", Priority.HIGH, saved.status(), saved.createdAt());

        repository.update(updated);

        Card loaded = repository.findByBoard(boardId).getFirst();
        assertInstanceOf(FeatureCard.class, loaded);
        assertEquals("Neu", loaded.title());
        assertEquals("Nachher", loaded.description());
        assertEquals(Priority.HIGH, loaded.priority());
    }

    @Test
    void boardCanBeRenamed() {
        JooqBoardRepository boards = new JooqBoardRepository(dsl);

        org.example.cleandesk.model.Board renamed = boards.rename(boardId, "Umbenannt-" + System.nanoTime());

        assertEquals(boardId, renamed.id());
        assertEquals(renamed, boards.findOrCreate(renamed.name()));
    }

    @Test
    void deleteRemovesCard() {
        Card saved = repository.insert(boardId, CardFactory.createNew(CardType.TASK, "Weg", "", Priority.LOW));

        repository.delete(saved.id());

        assertTrue(repository.findByBoard(boardId).isEmpty());
    }

    private static String setting(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }
}
