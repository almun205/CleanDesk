package org.example;

import org.example.cleandesk.model.Board;
import org.example.cleandesk.persistence.DatabaseManager;
import org.example.cleandesk.persistence.JooqBoardRepository;
import org.example.cleandesk.persistence.JooqCardRepository;
import org.example.cleandesk.service.BoardService;
import org.example.cleandesk.ui.MainFrame;
import org.jooq.DSLContext;

import javax.swing.SwingUtilities;

/** Einstiegspunkt: verbindet Datenbank, Service und GUI. */
public final class Main {

    private static final String BOARD_NAME = "CleanDesk";

    public static void main(String[] args) {
        DSLContext dsl = DatabaseManager.instance().dsl();
        JooqBoardRepository boardRepository = new JooqBoardRepository(dsl);
        Board board = boardRepository.findFirstOrCreate(BOARD_NAME);
        BoardService service = new BoardService(new JooqCardRepository(dsl), board.id());

        SwingUtilities.invokeLater(() -> new MainFrame(
                service,
                board.name(),
                name -> boardRepository.rename(board.id(), name)).setVisible(true));
    }
}
