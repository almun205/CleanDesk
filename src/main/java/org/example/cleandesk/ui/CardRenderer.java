package org.example.cleandesk.ui;

import org.example.cleandesk.model.*;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.BorderFactory;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.time.format.DateTimeFormatter;

/** Zeichnet eine Karte, jeder Kartentyp hat eine eigene Farbe. */
final class CardRenderer extends JPanel implements ListCellRenderer<Card> {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
    private final JLabel icon = new JLabel("", JLabel.CENTER);
    private final JLabel title = new JLabel();
    private final JLabel description = new JLabel();
    private final JLabel type = new JLabel();
    private final JLabel priority = new JLabel();
    private final JLabel date = new JLabel();
    private final JLabel backAction = new JLabel("←", JLabel.CENTER);
    private final JLabel forwardAction = new JLabel("→", JLabel.CENTER);
    private final JLabel editAction = new JLabel("✎");
    private final JLabel deleteAction = new JLabel(UiIcons.trash(new Color(239, 68, 68)));
    private Color cardBackground;
    private Color cardBorder;
    private boolean selected;
    private int hoveredIndex = -1;

    CardRenderer() {
        super(new BorderLayout(0, 8));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(11, 13, 14, 13));

        JPanel heading = transparentPanel();
        heading.setLayout(new BoxLayout(heading, BoxLayout.X_AXIS));
        icon.setFont(UiTheme.font(Font.BOLD, 15));
        title.setFont(UiTheme.font(Font.BOLD, 14));
        title.setForeground(UiTheme.TEXT);
        heading.add(icon);
        heading.add(Box.createHorizontalStrut(9));
        heading.add(title);
        heading.add(Box.createHorizontalGlue());
        editAction.setFont(UiTheme.font(Font.BOLD, 18));
        editAction.setForeground(new Color(86, 101, 122));
        editAction.setToolTipText("Karte bearbeiten");
        deleteAction.setToolTipText("Karte löschen");
        heading.add(editAction);
        heading.add(Box.createHorizontalStrut(13));
        heading.add(deleteAction);
        add(heading, BorderLayout.NORTH);

        description.setFont(UiTheme.font(Font.PLAIN, 12));
        description.setForeground(new Color(91, 104, 123));
        add(description, BorderLayout.CENTER);

        JPanel metadata = transparentPanel();
        metadata.setLayout(new BoxLayout(metadata, BoxLayout.X_AXIS));
        type.setFont(UiTheme.font(Font.BOLD, 11));
        type.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        priority.setFont(UiTheme.font(Font.PLAIN, 11));
        priority.setForeground(new Color(99, 112, 130));
        date.setFont(UiTheme.font(Font.PLAIN, 10));
        date.setForeground(UiTheme.MUTED);
        metadata.add(type);
        metadata.add(Box.createHorizontalStrut(8));
        metadata.add(priority);
        metadata.add(Box.createHorizontalGlue());
        metadata.add(date);
        metadata.add(Box.createHorizontalStrut(8));
        styleMoveAction(backAction);
        styleMoveAction(forwardAction);
        metadata.add(backAction);
        metadata.add(Box.createHorizontalStrut(4));
        metadata.add(forwardAction);
        add(metadata, BorderLayout.SOUTH);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Card> list, Card card, int index,
                                                  boolean isSelected, boolean hasFocus) {
        selected = isSelected;
        Palette palette = paletteFor(card);
        cardBackground = palette.background();
        cardBorder = palette.border();
        boolean hovered = index == hoveredIndex;
        editAction.setVisible(hovered);
        deleteAction.setVisible(hovered);
        icon.setIcon(card instanceof BugCard ? UiIcons.bug(palette.accent()) : null);
        icon.setText(card instanceof BugCard ? "" : palette.icon());
        icon.setForeground(palette.accent());
        title.setText(card.title());
        String text = card.description().isBlank() ? "Keine Beschreibung" : card.description();
        description.setText("<html>" + escape(text) + "</html>");
        type.setText(card.type().displayName());
        type.setOpaque(true);
        type.setBackground(palette.chip());
        type.setForeground(palette.accent());
        priority.setText(priorityDot(card.priority()) + "  " + card.priority().displayName());
        date.setText(card.createdAt().format(DATE_FORMAT));
        backAction.setVisible(card.status().previous().isPresent());
        forwardAction.setVisible(card.status().next().isPresent());
        return this;
    }

    void setHoveredIndex(int hoveredIndex) {
        this.hoveredIndex = hoveredIndex;
    }

    private static Palette paletteFor(Card card) {
        return switch (card) {
            case BugCard _ -> new Palette(
                    new Color(255, 244, 244), new Color(252, 180, 180),
                    new Color(220, 55, 55), new Color(255, 226, 226), "⚠");
            case FeatureCard _ -> new Palette(
                    new Color(239, 253, 248), new Color(151, 232, 202),
                    new Color(5, 150, 105), new Color(209, 250, 229), "✦");
            case TaskCard _ -> new Palette(
                    new Color(239, 246, 255), new Color(177, 210, 255),
                    new Color(37, 99, 235), new Color(219, 234, 254), "✓");
        };
    }

    private static String priorityDot(Priority value) {
        return switch (value) {
            case LOW -> "○";
            case MEDIUM -> "●";
            case HIGH -> "◆";
        };
    }

    private static JPanel transparentPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        return panel;
    }

    private static void styleMoveAction(JLabel label) {
        label.setOpaque(true);
        label.setBackground(Color.WHITE);
        label.setForeground(new Color(93, 107, 127));
        label.setFont(UiTheme.font(Font.BOLD, 15));
        label.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
    }

    private static String escape(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(cardBackground == null ? UiTheme.SURFACE : cardBackground);
        g2.fillRoundRect(3, 3, getWidth() - 7, getHeight() - 9, 15, 15);
        g2.setColor(selected ? UiTheme.BLUE : cardBorder);
        g2.drawRoundRect(3, 3, getWidth() - 7, getHeight() - 9, 15, 15);
        if (selected) {
            g2.setColor(new Color(43, 91, 220, 28));
            g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 7, 16, 16);
        }
        g2.dispose();
        super.paintComponent(graphics);
    }

    private record Palette(Color background, Color border, Color accent, Color chip, String icon) {
    }
}
