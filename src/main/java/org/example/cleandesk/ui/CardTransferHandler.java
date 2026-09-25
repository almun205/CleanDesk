package org.example.cleandesk.ui;

import org.example.cleandesk.model.Card;
import org.example.cleandesk.model.Status;
import org.example.cleandesk.service.BoardService;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/** Drag & Drop: Beim Ziehen wird die Karten-ID übertragen, beim Ablegen wird die Karte verschoben. */
final class CardTransferHandler extends TransferHandler {

    private final Status targetStatus;
    private final BoardService service;

    CardTransferHandler(Status targetStatus, BoardService service) {
        this.targetStatus = targetStatus;
        this.service = service;
    }

    @Override
    public int getSourceActions(JComponent component) {
        return MOVE;
    }

    @Override
    protected Transferable createTransferable(JComponent component) {
        if (component instanceof JList<?> list && list.getSelectedValue() instanceof Card card) {
            return new StringSelection(String.valueOf(card.id()));
        }
        return null;
    }

    @Override
    public boolean canImport(TransferSupport support) {
        return support.isDrop() && support.isDataFlavorSupported(DataFlavor.stringFlavor);
    }

    @Override
    public boolean importData(TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }
        try {
            String cardId = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
            SwingUtilities.invokeLater(() -> service.moveCard(Long.parseLong(cardId), targetStatus));
            return true;
        } catch (UnsupportedFlavorException | IOException | NumberFormatException e) {
            return false;
        }
    }
}
