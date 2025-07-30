import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class TradeGUI extends JDialog {
    public TradeGUI(JFrame parent, Binder binder, ArrayList<Card> collection) {
        super(parent, "Trade Card - " + binder.getName(), true);
        setSize(600, 400);
        setLayout(new BorderLayout());

        java.util.List<Card> binderCards = binder.getCards();
        if (binderCards.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Binder has no cards to trade.");
            dispose();
            return;
        }

        String[] cardNames = binderCards.stream().map(Card::getName).toArray(String[]::new);
        JComboBox<String> cardDropdown = new JComboBox<>(cardNames);
        JTextField nameField = new JTextField();
        JComboBox<String> rarityBox = new JComboBox<>(new String[]{"common", "uncommon", "rare", "legendary"});
        JComboBox<String> variantBox = new JComboBox<>(new String[]{"normal", "extended-art", "full-art", "alt-art"});
        JTextField baseValueField = new JTextField();

        JPanel form = new JPanel(new GridLayout(6, 2, 10, 10));
        form.add(new JLabel("Select card to trade away:"));
        form.add(cardDropdown);
        form.add(new JLabel("Incoming Card Name:"));
        form.add(nameField);
        form.add(new JLabel("Rarity:"));
        form.add(rarityBox);
        form.add(new JLabel("Variant:"));
        form.add(variantBox);
        form.add(new JLabel("Base Value:"));
        form.add(baseValueField);

        JButton tradeButton = new JButton("Trade");
        tradeButton.addActionListener(e -> {
            String outgoingName = (String) cardDropdown.getSelectedItem();
            Card outgoing = binderCards.stream().filter(c -> c.getName().equalsIgnoreCase(outgoingName)).findFirst().orElse(null);

            if (outgoing == null) {
                JOptionPane.showMessageDialog(this, "Card not found in binder.");
                return;
            }

            String name = nameField.getText().trim();
            String rarity = (String) rarityBox.getSelectedItem();
            String variant = (String) variantBox.getSelectedItem();
            double baseValue;

            try {
                baseValue = Double.parseDouble(baseValueField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid base value.");
                return;
            }

            Card incoming;
            try {
                incoming = new Card(name, rarity, variant, baseValue);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "" + ex.getMessage());
                return;
            }

            double diff = Math.abs(outgoing.getActualValue() - incoming.getActualValue());
            if (diff >= 1.0) {
                int confirm = JOptionPane.showConfirmDialog(this, String.format(
                        "The value difference is $%.2f. Proceed with trade?", diff),
                        "Confirm Trade", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            binder.removeCard(outgoing);
            boolean added = binder.addCard(incoming);

            if (!added) {
                JOptionPane.showMessageDialog(this, "Binder is full or incoming card is not allowed. Trade cancelled.");
                binder.addCard(outgoing);
                return;
            }

            Card existing = collection.stream()
                    .filter(c -> c.getName().equalsIgnoreCase(incoming.getName()))
                    .findFirst().orElse(null);
            if (existing == null) {
                collection.add(new Card(name, rarity, variant, baseValue));
                collection.stream()
                        .filter(c -> c.getName().equalsIgnoreCase(name))
                        .findFirst().ifPresent(c -> c.setCount(0));
            }

            JOptionPane.showMessageDialog(this, "Trade successful!");
            dispose();
        });

        add(form, BorderLayout.CENTER);
        add(tradeButton, BorderLayout.SOUTH);
        setLocationRelativeTo(parent);
        setVisible(true);
    }
}
