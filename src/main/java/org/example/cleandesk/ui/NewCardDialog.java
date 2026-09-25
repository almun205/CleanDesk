package org.example.cleandesk.ui;

import org.example.cleandesk.model.Card;
import org.example.cleandesk.model.CardType;
import org.example.cleandesk.model.Priority;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.Optional;

final class NewCardDialog {

    record Input(CardType type, String title, String description, Priority priority) {}

    private NewCardDialog() {
    }

    static Optional<Input> show(Component parent) {
        return show(parent, null);
    }

    static Optional<Input> showForEdit(Component parent, Card card) {
        return show(parent, card);
    }

    private static Optional<Input> show(Component parent, Card existing) {
        JComboBox<CardType> typeBox = new JComboBox<>(CardType.values());
        JTextField titleField = new JTextField(20);
        JTextField descriptionField = new JTextField(20);
        JComboBox<Priority> priorityBox = new JComboBox<>(Priority.values());
        priorityBox.setSelectedItem(Priority.MEDIUM);

        if (existing != null) {
            typeBox.setSelectedItem(existing.type());
            titleField.setText(existing.title());
            descriptionField.setText(existing.description());
            priorityBox.setSelectedItem(existing.priority());
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 4, 4));
        form.add(new JLabel("Typ:"));
        form.add(typeBox);
        form.add(new JLabel("Titel:"));
        form.add(titleField);
        form.add(new JLabel("Beschreibung:"));
        form.add(descriptionField);
        form.add(new JLabel("Priorität:"));
        form.add(priorityBox);

        String dialogTitle = existing == null ? "Neue Karte" : "Karte bearbeiten";
        int result = JOptionPane.showConfirmDialog(parent, form, dialogTitle,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION || titleField.getText().isBlank()) {
            return Optional.empty();
        }
        return Optional.of(new Input(
                (CardType) typeBox.getSelectedItem(),
                titleField.getText().strip(),
                descriptionField.getText().strip(),
                (Priority) priorityBox.getSelectedItem()));
    }
}
