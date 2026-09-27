package org.example.cleandesk.ui;

import org.example.cleandesk.model.Card;
import org.example.cleandesk.model.CardType;
import org.example.cleandesk.model.Priority;

import javax.swing.JComboBox;
import javax.swing.JDialog;
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
        JOptionPane optionPane = new JOptionPane(form, JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION);
        JDialog dialog = new JDialog(JOptionPane.getFrameForComponent(parent), dialogTitle, true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setContentPane(optionPane);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        optionPane.addPropertyChangeListener(event -> {
            if (!dialog.isVisible() || !JOptionPane.VALUE_PROPERTY.equals(event.getPropertyName())) {
                return;
            }
            Object value = optionPane.getValue();
            if (JOptionPane.UNINITIALIZED_VALUE.equals(value)) {
                return;
            }
            if (Integer.valueOf(JOptionPane.OK_OPTION).equals(value) && titleField.getText().isBlank()) {
                optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                JOptionPane.showMessageDialog(dialog, "Bitte gib einen Titel ein.",
                        "Titel erforderlich", JOptionPane.WARNING_MESSAGE);
                titleField.requestFocusInWindow();
                return;
            }
            dialog.dispose();
        });
        dialog.setVisible(true);
        dialog.dispose();

        if (!Integer.valueOf(JOptionPane.OK_OPTION).equals(optionPane.getValue())) {
            return Optional.empty();
        }
        return Optional.of(new Input(
                (CardType) typeBox.getSelectedItem(),
                titleField.getText().strip(),
                descriptionField.getText().strip(),
                (Priority) priorityBox.getSelectedItem()));
    }
}
