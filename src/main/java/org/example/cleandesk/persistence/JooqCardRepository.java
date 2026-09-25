package org.example.cleandesk.persistence;

import org.example.cleandesk.factory.CardFactory;
import org.example.cleandesk.model.*;
import org.jooq.DSLContext;
import org.jooq.Record;

import java.util.List;

import static org.example.cleandesk.persistence.SqlTables.*;

/** Typsichere SQL-Queries mit jOOQ. Tippfehler bei Spaltennamen fallen schon beim Kompilieren auf. */
public final class JooqCardRepository implements CardRepository {

    private final DSLContext dsl;

    public JooqCardRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public List<Card> findByBoard(long boardId) {
        return dsl.selectFrom(CARD)
                .where(CARD_BOARD_ID.eq(boardId))
                .fetch(this::toCard);
    }

    @Override
    public Card insert(long boardId, Card card) {
        Long generatedId = dsl.insertInto(CARD)
                .set(CARD_BOARD_ID, boardId)
                .set(CARD_TYPE, card.type().name())
                .set(CARD_TITLE, card.title())
                .set(CARD_DESCRIPTION, card.description())
                .set(CARD_PRIORITY, card.priority().name())
                .set(CARD_STATUS, card.status().name())
                .set(CARD_CREATED_AT, card.createdAt())
                .returning(CARD_ID)
                .fetchSingle()
                .get(CARD_ID);
        return card.withId(generatedId);
    }

    @Override
    public void update(Card card) {
        dsl.update(CARD)
                .set(CARD_TYPE, card.type().name())
                .set(CARD_TITLE, card.title())
                .set(CARD_DESCRIPTION, card.description())
                .set(CARD_PRIORITY, card.priority().name())
                .where(CARD_ID.eq(card.id()))
                .execute();
    }

    @Override
    public void updateStatus(long cardId, Status status) {
        dsl.update(CARD)
                .set(CARD_STATUS, status.name())
                .where(CARD_ID.eq(cardId))
                .execute();
    }

    @Override
    public void delete(long cardId) {
        dsl.deleteFrom(CARD).where(CARD_ID.eq(cardId)).execute();
    }

    private Card toCard(Record row) {
        return CardFactory.create(
                row.get(CARD_ID),
                CardType.valueOf(row.get(CARD_TYPE)),
                row.get(CARD_TITLE),
                row.get(CARD_DESCRIPTION),
                Priority.valueOf(row.get(CARD_PRIORITY)),
                Status.valueOf(row.get(CARD_STATUS)),
                row.get(CARD_CREATED_AT.getName(), java.time.LocalDateTime.class));
    }
}
