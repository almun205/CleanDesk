package org.example.cleandesk.ui;

import org.example.cleandesk.model.Card;
import org.example.cleandesk.model.Status;
import org.example.cleandesk.service.BoardService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/** Eine Spalte des Boards (To Do, In Progress oder Done). */
final class ColumnPanel extends JPanel {

    private final DefaultListModel<Card> model = new DefaultListModel<>();
    private final JList<Card> cardList = new JList<>(model);
    private final CardRenderer cardRenderer = new CardRenderer();
    private final JLabel countLabel = new JLabel("0", JLabel.CENTER);

    ColumnPanel(Status status, BoardService service, Runnable addCard) {
        super(new BorderLayout(0, 12));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(17, 14, 14, 14));
        add(createHeader(status), BorderLayout.NORTH);

        cardList.setCellRenderer(cardRenderer);
        cardList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cardList.setDragEnabled(true);
        cardList.setDropMode(DropMode.ON_OR_INSERT);
        cardList.setTransferHandler(new CardTransferHandler(status, service));
        cardList.setOpaque(false);
        cardList.setBackground(UiTheme.SURFACE);
        cardList.setFixedCellHeight(126);
        cardList.setSelectionBackground(UiTheme.SURFACE);
        cardList.setBorder(BorderFactory.createEmptyBorder());
        installCardActions(service);

        JScrollPane scrollPane = new JScrollPane(cardList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        add(scrollPane, BorderLayout.CENTER);
        add(createFooter(addCard), BorderLayout.SOUTH);
    }

    void showCards(List<Card> cards) {
        model.clear();
        cardRenderer.setHoveredIndex(-1);
        for (Card c : cards) {
            model.addElement(c);
        }
        countLabel.setText(String.valueOf(cards.size()));
    }

    private JPanel createHeader(Status status) {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));

        JLabel dot = new JLabel("●");
        dot.setForeground(statusColor(status));
        dot.setFont(UiTheme.font(Font.BOLD, 15));
        JLabel title = new JLabel(status.displayName().toUpperCase());
        title.setForeground(new Color(58, 69, 87));
        title.setFont(UiTheme.font(Font.BOLD, 13));
        countLabel.setOpaque(true);
        countLabel.setBackground(new Color(235, 238, 243));
        countLabel.setForeground(UiTheme.MUTED);
        countLabel.setFont(UiTheme.font(Font.BOLD, 11));
        countLabel.setBorder(BorderFactory.createEmptyBorder(2, 7, 2, 7));

        header.add(dot);
        header.add(Box.createHorizontalStrut(7));
        header.add(title);
        header.add(Box.createHorizontalStrut(8));
        header.add(countLabel);
        header.add(Box.createHorizontalGlue());
        return header;
    }

    private JPanel createFooter(Runnable addCard) {
        ModernButton addButton = new ModernButton(
                "+  Karte hinzufügen", UiTheme.SURFACE, new Color(242, 245, 249), UiTheme.MUTED);
        addButton.addActionListener(event -> addCard.run());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        buttons.setOpaque(false);
        buttons.add(addButton);
        return buttons;
    }

    private void installCardActions(BoardService service) {
        MouseAdapter actions = new MouseAdapter() {
            private int hoveredIndex = -1;

            @Override
            public void mouseMoved(MouseEvent event) {
                int newIndex = cardIndexAt(event.getPoint());
                if (newIndex != hoveredIndex) {
                    hoveredIndex = newIndex;
                    cardRenderer.setHoveredIndex(hoveredIndex);
                    cardList.repaint();
                }
                cardList.setCursor((isOverAction(event.getPoint(), newIndex)
                        || isOverMoveAction(event.getPoint(), newIndex))
                        ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                        : Cursor.getDefaultCursor());
            }

            @Override
            public void mouseExited(MouseEvent event) {
                hoveredIndex = -1;
                cardRenderer.setHoveredIndex(-1);
                cardList.setCursor(Cursor.getDefaultCursor());
                cardList.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent event) {
                if (!SwingUtilities.isLeftMouseButton(event)) {
                    return;
                }
                int index = cardIndexAt(event.getPoint());
                if (index < 0) {
                    return;
                }
                Card card = model.get(index);
                if (isOverAction(event.getPoint(), index)) {
                    int action = actionAt(event.getPoint());
                    if (action == 1) {
                        editCard(card, service);
                    } else if (action == 2) {
                        confirmDelete(card, service);
                    }
                    event.consume();
                    return;
                }

                int move = moveActionAt(event.getPoint(), index, card);
                if (move < 0) {
                    service.moveBackward(card.id());
                } else if (move > 0) {
                    service.moveForward(card.id());
                } else {
                    return;
                }
                event.consume();
            }
        };
        cardList.addMouseMotionListener(actions);
        cardList.addMouseListener(actions);
    }

    private void editCard(Card card, BoardService service) {
        NewCardDialog.showForEdit(this, card).ifPresent(input ->
                service.updateCard(card.id(), input.type(), input.title(), input.description(), input.priority()));
    }

    private void confirmDelete(Card card, BoardService service) {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Möchtest du die Karte „" + card.title() + "“ wirklich löschen?",
                "Karte löschen",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            service.deleteCard(card.id());
        }
    }

    private int cardIndexAt(Point point) {
        int index = cardList.locationToIndex(point);
        if (index < 0) {
            return -1;
        }
        Rectangle bounds = cardList.getCellBounds(index, index);
        return bounds != null && bounds.contains(point) ? index : -1;
    }

    private boolean isOverAction(Point point, int index) {
        if (index < 0) {
            return false;
        }
        Rectangle bounds = cardList.getCellBounds(index, index);
        return bounds != null
                && point.y - bounds.y >= 5
                && point.y - bounds.y <= 43
                && point.x >= cardList.getWidth() - 76;
    }

    /** 1 = edit, 2 = delete. */
    private int actionAt(Point point) {
        return point.x < cardList.getWidth() - 38 ? 1 : 2;
    }

    private boolean isOverMoveAction(Point point, int index) {
        return index >= 0 && moveActionAt(point, index, model.get(index)) != 0;
    }

    /** -1 = backward, 1 = forward, 0 = no movement action. */
    private int moveActionAt(Point point, int index, Card card) {
        Rectangle bounds = cardList.getCellBounds(index, index);
        if (bounds == null || point.y - bounds.y < bounds.height - 42 || point.x < cardList.getWidth() - 76) {
            return 0;
        }

        boolean inLastButton = point.x >= cardList.getWidth() - 38;
        if (card.status() == Status.TODO) {
            return inLastButton ? 1 : 0;
        }
        if (card.status() == Status.DONE) {
            return inLastButton ? -1 : 0;
        }
        return inLastButton ? 1 : -1;
    }

    private static Color statusColor(Status status) {
        return switch (status) {
            case TODO -> new Color(153, 164, 181);
            case IN_PROGRESS -> new Color(245, 158, 11);
            case DONE -> new Color(16, 185, 129);
        };
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(UiTheme.SURFACE);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
        g2.setColor(UiTheme.BORDER);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
        g2.dispose();
        super.paintComponent(graphics);
    }
}
