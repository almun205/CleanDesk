package org.example.cleandesk.ui;

import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

final class UiIcons {

    private UiIcons() {
    }

    static Icon trash(Color color) {
        return new DrawnIcon(17, 17, color, (graphics, size) -> {
            graphics.drawLine(4, 5, 13, 5);
            graphics.drawLine(7, 3, 10, 3);
            graphics.drawLine(5, 7, 6, 14);
            graphics.drawLine(12, 7, 11, 14);
            graphics.drawLine(6, 14, 11, 14);
            graphics.drawLine(8, 7, 8, 12);
            graphics.drawLine(10, 7, 10, 12);
        });
    }

    static Icon bug(Color color) {
        return new DrawnIcon(18, 18, color, (graphics, size) -> {
            graphics.drawOval(6, 5, 7, 10);
            graphics.drawArc(7, 2, 5, 6, 0, 180);
            graphics.drawLine(9, 6, 9, 15);
            graphics.drawLine(4, 6, 7, 8);
            graphics.drawLine(14, 6, 12, 8);
            graphics.drawLine(4, 10, 6, 10);
            graphics.drawLine(13, 10, 15, 10);
            graphics.drawLine(4, 14, 7, 12);
            graphics.drawLine(14, 14, 12, 12);
        });
    }

    @FunctionalInterface
    private interface Painter {
        void paint(Graphics2D graphics, int size);
    }

    private record DrawnIcon(int width, int height, Color color, Painter painter) implements Icon {

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.translate(x, y);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(color);
            painter.paint(g2, Math.min(width, height));
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return width;
        }

        @Override
        public int getIconHeight() {
            return height;
        }
    }
}
