package org.example.cleandesk.persistence;

import org.example.cleandesk.model.Board;
import org.jooq.DSLContext;

import static org.example.cleandesk.persistence.SqlTables.BOARD;
import static org.example.cleandesk.persistence.SqlTables.BOARD_ID;
import static org.example.cleandesk.persistence.SqlTables.BOARD_NAME;

public final class JooqBoardRepository {

    private final DSLContext dsl;

    public JooqBoardRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Board findOrCreate(String name) {
        return dsl.selectFrom(BOARD)
                .where(BOARD_NAME.eq(name))
                .fetchOptional()
                .map(row -> new Board(row.get(BOARD_ID), row.get(BOARD_NAME)))
                .orElseGet(() -> create(name));
    }

    public Board findFirstOrCreate(String defaultName) {
        return dsl.selectFrom(BOARD)
                .orderBy(BOARD_ID)
                .limit(1)
                .fetchOptional()
                .map(row -> new Board(row.get(BOARD_ID), row.get(BOARD_NAME)))
                .orElseGet(() -> create(defaultName));
    }

    public Board rename(long boardId, String name) {
        dsl.update(BOARD)
                .set(BOARD_NAME, name)
                .where(BOARD_ID.eq(boardId))
                .execute();
        return new Board(boardId, name);
    }

    private Board create(String name) {
        Long id = dsl.insertInto(BOARD)
                .set(BOARD_NAME, name)
                .returning(BOARD_ID)
                .fetchSingle()
                .get(BOARD_ID);
        return new Board(id, name);
    }
}
