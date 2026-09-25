package org.example.cleandesk.ui;

import com.google.common.collect.ImmutableListMultimap;
import org.example.cleandesk.model.Card;
import org.example.cleandesk.model.Status;
import org.example.cleandesk.observer.BoardEvent;
import org.example.cleandesk.observer.BoardObserver;
import org.example.cleandesk.service.BoardService;
import org.example.cleandesk.strategy.DateStrategy;
import org.example.cleandesk.strategy.PriorityStrategy;
import org.example.cleandesk.strategy.SortStrategy;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

/** Hauptfenster. Als Observer baut es sich bei jedem BoardEvent neu auf. */
public final class MainFrame extends JFrame implements BoardObserver {

    private final BoardService service;
    private final Consumer<String> boardNameUpdater;
    private final Map<Status, ColumnPanel> columns = new EnumMap<>(Status.class);
    private final JLabel statusBar = new JLabel(" ");
    private final JLabel totalCardsLabel = new JLabel("0 Karten insgesamt");
    private final JLabel boardTitleLabel = new JLabel();

    public MainFrame(BoardService service, String boardName, Consumer<String> boardNameUpdater) {
        super("CleanDesk – " + boardName);
        this.service = service;
        this.boardNameUpdater = boardNameUpdater;
        boardTitleLabel.setText(boardName);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UiTheme.CANVAS);
        add(createHeader(), BorderLayout.NORTH);
        add(createColumns(), BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.SOUTH);

        service.addObserver(this);
        refreshColumns();
        setMinimumSize(new Dimension(920, 600));
        setSize(1180, 720);
        setLocationRelativeTo(null);
    }

    @Override
    public void onBoardChanged(BoardEvent event) {
        refreshColumns();
        statusBar.setText(describe(event));
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(24, 0));
        header.setBackground(UiTheme.SURFACE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UiTheme.BORDER),
                BorderFactory.createEmptyBorder(14, 24, 14, 24)));

        JPanel identity = new JPanel();
        identity.setOpaque(false);
        identity.setLayout(new BoxLayout(identity, BoxLayout.X_AXIS));
        identity.add(new Logo());
        identity.add(Box.createHorizontalStrut(12));

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        boardTitleLabel.setForeground(UiTheme.TEXT);
        boardTitleLabel.setFont(UiTheme.font(Font.BOLD, 19));
        ModernButton renameButton = new ModernButton(
                "✎", UiTheme.SURFACE, new Color(242, 245, 249), UiTheme.MUTED);
        renameButton.setToolTipText("Board umbenennen");
        renameButton.addActionListener(event -> renameBoard());

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        titleRow.setOpaque(false);
        titleRow.add(boardTitleLabel);
        titleRow.add(renameButton);
        totalCardsLabel.setForeground(UiTheme.MUTED);
        totalCardsLabel.setFont(UiTheme.font(Font.PLAIN, 12));
        titleBlock.add(titleRow);
        titleBlock.add(Box.createVerticalStrut(3));
        titleBlock.add(totalCardsLabel);
        identity.add(titleBlock);
        header.add(identity, BorderLayout.WEST);

        JComboBox<SortStrategy> sortBox =
                new JComboBox<>(new SortStrategy[]{new PriorityStrategy(), new DateStrategy()});
        sortBox.addActionListener(e -> service.setSortStrategy((SortStrategy) sortBox.getSelectedItem()));
        sortBox.setFont(UiTheme.font(Font.PLAIN, 13));
        sortBox.setBackground(UiTheme.SURFACE);
        sortBox.setPreferredSize(new Dimension(145, 36));

        JLabel sortLabel = new JLabel("↕  Sortieren:");
        sortLabel.setFont(UiTheme.font(Font.BOLD, 13));
        sortLabel.setForeground(new Color(95, 105, 121));

        ModernButton addButton = new ModernButton(
                "+  Neue Karte", UiTheme.BLUE, UiTheme.BLUE_HOVER, Color.WHITE);
        addButton.addActionListener(event -> showNewCardDialog());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        actions.add(sortLabel);
        actions.add(sortBox);
        actions.add(addButton);
        header.add(actions, BorderLayout.EAST);
        return header;
    }

    private JPanel createColumns() {
        JPanel panel = new JPanel(new GridLayout(1, Status.values().length, 16, 0));
        panel.setBackground(UiTheme.CANVAS);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 18, 24));
        for (Status status : Status.values()) {
            ColumnPanel column = new ColumnPanel(status, service, this::showNewCardDialog);
            columns.put(status, column);
            panel.add(column);
        }
        return panel;
    }

    private JPanel createStatusBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UiTheme.SURFACE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UiTheme.BORDER),
                BorderFactory.createEmptyBorder(7, 24, 7, 24)));
        statusBar.setForeground(UiTheme.MUTED);
        statusBar.setFont(UiTheme.font(Font.PLAIN, 12));
        panel.add(statusBar);
        return panel;
    }

    private void showNewCardDialog() {
        NewCardDialog.show(this).ifPresent(input ->
                service.addCard(input.type(), input.title(), input.description(), input.priority()));
    }

    private void renameBoard() {
        JTextField nameField = new JTextField(boardTitleLabel.getText(), 24);
        int result = JOptionPane.showConfirmDialog(
                this,
                nameField,
                "Board umbenennen",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String newName = nameField.getText().strip();
        if (newName.isBlank()) {
            JOptionPane.showMessageDialog(
                    this, "Der Board-Name darf nicht leer sein.", "Ungültiger Name", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boardNameUpdater.accept(newName);
        boardTitleLabel.setText(newName);
        setTitle("CleanDesk – " + newName);
        statusBar.setText("Board umbenannt: " + newName);
    }

    private void refreshColumns() {
        ImmutableListMultimap<Status, Card> cardsByStatus = service.cardsByStatus();
        columns.forEach((status, column) -> column.showCards(cardsByStatus.get(status)));
        int count = cardsByStatus.size();
        totalCardsLabel.setText(count + (count == 1 ? " Karte insgesamt" : " Karten insgesamt"));
    }

    /** Record-Patterns: Die Felder des Events werden direkt im case zerlegt. */
    private static String describe(BoardEvent event) {
        return switch (event) {
            case BoardEvent.CardAdded(Card card) -> "Neue Karte: " + card.title();
            case BoardEvent.CardUpdated(Card card) -> "Bearbeitet: " + card.title();
            case BoardEvent.CardMoved(Card card, Status from, Status to) ->
                    "„%s“: %s → %s".formatted(card.title(), from.displayName(), to.displayName());
            case BoardEvent.CardDeleted(Card card) -> "Gelöscht: " + card.title();
            case BoardEvent.SortingChanged(SortStrategy strategy) -> "Sortiert nach " + strategy.displayName();
        };
    }

    private static final class Logo extends JPanel {

        private Logo() {
            setOpaque(false);
            setPreferredSize(new Dimension(40, 40));
            setMinimumSize(getPreferredSize());
            setMaximumSize(getPreferredSize());
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(UiTheme.BLUE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            g2.setColor(Color.WHITE);
            for (int row = 0; row < 2; row++) {
                for (int column = 0; column < 2; column++) {
                    g2.drawRoundRect(11 + column * 10, 10 + row * 10, 7, 7, 2, 2);
                }
            }
            g2.dispose();
        }
    }
}
