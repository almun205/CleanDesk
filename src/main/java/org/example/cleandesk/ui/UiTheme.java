package org.example.cleandesk.ui;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Font;

final class UiTheme {

    static final Color CANVAS = new Color(245, 247, 250);
    static final Color SURFACE = Color.WHITE;
    static final Color TEXT = new Color(31, 41, 55);
    static final Color MUTED = new Color(139, 151, 169);
    static final Color BORDER = new Color(225, 230, 237);
    static final Color BLUE = new Color(43, 91, 220);
    static final Color BLUE_HOVER = new Color(35, 76, 190);

    private UiTheme() {
    }

    static Font font(int style, int size) {
        return new Font(Font.SANS_SERIF, style, size);
    }

    static <T extends JComponent> T padded(T component, int top, int left, int bottom, int right) {
        component.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
        return component;
    }
}
