package org.example.cleandesk.persistence;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;

import java.time.LocalDateTime;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.name;
import static org.jooq.impl.DSL.table;
import static org.jooq.impl.SQLDataType.LOCALDATETIME;

/** PostgreSQL table and column definitions used by the repositories. */
final class SqlTables {

    static final Table<Record> BOARD = table(name("board"));
    static final Field<Long> BOARD_ID = field(name("board", "id"), Long.class);
    static final Field<String> BOARD_NAME = field(name("board", "name"), String.class);

    static final Table<Record> CARD = table(name("card"));
    static final Field<Long> CARD_ID = field(name("card", "id"), Long.class);
    static final Field<Long> CARD_BOARD_ID = field(name("card", "board_id"), Long.class);
    static final Field<String> CARD_TYPE = field(name("card", "type"), String.class);
    static final Field<String> CARD_TITLE = field(name("card", "title"), String.class);
    static final Field<String> CARD_DESCRIPTION = field(name("card", "description"), String.class);
    static final Field<String> CARD_PRIORITY = field(name("card", "priority"), String.class);
    static final Field<String> CARD_STATUS = field(name("card", "status"), String.class);
    static final Field<LocalDateTime> CARD_CREATED_AT =
            field(name("card", "created_at"), LOCALDATETIME);

    private SqlTables() {
    }
}
