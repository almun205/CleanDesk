package org.example.cleandesk.persistence;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Threadsicheres, lazy initialisiertes Singleton für die Anwendungsdatenbank. */
public final class DatabaseManager {

    private final DSLContext dsl;

    private DatabaseManager() {
        dsl = createApplicationContext();
    }

    public static DatabaseManager instance() {
        return Holder.INSTANCE;
    }

    public DSLContext dsl() {
        return dsl;
    }

    /** Auch von Tests mit einem expliziten SQL-Dialekt nutzbar. */
    static DSLContext connect(String jdbcUrl, String user, String password, SQLDialect dialect) {
        try {
            return DSL.using(DriverManager.getConnection(jdbcUrl, user, password), dialect);
        } catch (SQLException e) {
            throw new IllegalStateException("Datenbank nicht erreichbar: " + jdbcUrl, e);
        }
    }

    static DSLContext initializeSchema(DSLContext dsl) {
        try (InputStream stream = DatabaseManager.class.getResourceAsStream("/db/schema.sql")) {
            if (stream == null) {
                throw new IllegalStateException("Datenbankschema /db/schema.sql fehlt");
            }

            String schema = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            for (String statement : schema.split(";")) {
                if (!statement.isBlank()) {
                    dsl.execute(statement);
                }
            }
            return dsl;
        } catch (IOException e) {
            throw new IllegalStateException("Datenbankschema konnte nicht gelesen werden", e);
        }
    }

    private static DSLContext createApplicationContext() {
        String url = setting("CLEANDESK_DB_URL", "jdbc:postgresql://localhost:5432/cleandesk");
        String user = setting("CLEANDESK_DB_USER", "cleandesk");
        String password = setting("CLEANDESK_DB_PASSWORD", "");
        return initializeSchema(connect(url, user, password, SQLDialect.POSTGRES));
    }

    private static String setting(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }

    private static final class Holder {
        private static final DatabaseManager INSTANCE = new DatabaseManager();
    }
}
