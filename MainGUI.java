import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Graphical User Interface (GUI) entry point for the Trading Card Inventory System.
 * Migrated from Main.java console-based implementation.
 */
public class MainGUI extends JFrame {

    private Collector collector;
    private ArrayList<Deck> decks;
    private ArrayList<Binder> binders;

    public MainGUI() {
        collector = new Collector(); 
        decks = new ArrayList<>();
        binders = new ArrayList<>();

        setTitle("Trading Card Inventory System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(640, 360);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Left side panel with header
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(new Color(245, 245, 245));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 20));

        JLabel titleLabel = new JLabel("TRADING CARD INVENTORY SYSTEM");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel versionLabel = new JLabel("v1.0");
        versionLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        versionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        versionLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        leftPanel.add(titleLabel);
        leftPanel.add(versionLabel);
        add(leftPanel, BorderLayout.CENTER);

        // Right panel with buttons
        JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 40));

        addButton(buttonPanel, "Add a Card", () -> collector.getCollection().addCardToCollection(this));
        addButton(buttonPanel, "View Collection", () -> collector.getCollection().showCollection(this));
        addButton(buttonPanel, "Binders", () -> Binder.openBinderMenu(binders, collector.getCollection().getCards(), collector, null));
        addButton(buttonPanel, "Decks", this::handleDecksMenu);
        addButton(buttonPanel, "Sell Card", this::sellCardFromCollection);
        addButton(buttonPanel, "View Money", () -> {
            double money = collector.getMoney();
            JOptionPane.showMessageDialog(this, String.format("You have $%.2f", money), "Money", JOptionPane.INFORMATION_MESSAGE);
        });
        addButton(buttonPanel, "Exit", () -> System.exit(0));

        add(buttonPanel, BorderLayout.EAST);
        setVisible(true);
    }

    /**
     * A helper method that creates and adds a button to a given JPanel and
     * sets its behavior as Runnable action.
     * @param panel
     * @param label
     * @param action
     */
    private void addButton(JPanel panel, String label, Runnable action) {
        JButton button = new JButton(label);
        button.addActionListener(e -> action.run());
        panel.add(button);
    }

    /**
     * A method that displays the Decks Menu through a JOptionPane and organizes
     * the deck's functionalities.
     */
    private void handleDecksMenu() {
        String[] options = {"Create Deck", "Manage Deck", "Delete Deck", "Sell Deck", "Cancel"};
        int choice = JOptionPane.showOptionDialog(this, "Choose a Deck option:", "Deck Menu",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[0]);

        switch (choice) {
            case 0 -> createDeck();
            case 1 -> manageDeck();
            case 2 -> deleteDeck();
            case 3 -> sellDeck();
        }
    }

    /**
     * A method creating a Deck through JOptionPane
     */
    private void createDeck() {
        String deckName = JOptionPane.showInputDialog(this, "Enter deck name:");
        if (deckName == null || deckName.trim().isEmpty()) return;

        if (findDeckByName(deckName) != null) {
            JOptionPane.showMessageDialog(this, "Deck with that name already exists.");
            return;
        }

        String[] types = {"Normal Deck", "Sellable Deck"};
        int choice = JOptionPane.showOptionDialog(this, "Choose deck type:", "Deck Type",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, types, types[0]);

        Deck newDeck;
        if (choice == 0) {
            newDeck = new NormalDeck(deckName);
        } else if (choice == 1) {
            newDeck = new SellableDeck(deckName);
        } else {
            return;
        }

        decks.add(newDeck);
        JOptionPane.showMessageDialog(this, newDeck.getClass().getSimpleName() + " \"" + deckName + "\" created.");
    }

    /**
     * A method allowing the user to manage a deck through JOptionPane
     */
    private void manageDeck() {
        if (decks.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No decks available.");
            return;
        }

        Deck selected = selectDeck("Manage");
        if (selected == null) return;

        selected.viewDeckGUI(this, collector.getCollection().getCards());
    }

    /**
     * A method allowing the user to delete a deck through a JOptionPane
     */
    private void deleteDeck() {
        if (decks.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No decks to delete.");
            return;
        }

        Deck selected = selectDeck("Delete");
        if (selected == null) return;

        decks.remove(selected);
        selected.returnAllCardsToCollection(collector.getCollection().getCards());
        JOptionPane.showMessageDialog(this, "Deck \"" + selected.getName() + "\" deleted.");
    }

    /**
     * A method allowing the user to sell a SellableDeck through a JOptionPane
     */
    private void sellDeck() {
        ArrayList<Deck> sellableDecks = new ArrayList<>();
        for (Deck d : decks) {
            if (d instanceof SellableDeck) {
                sellableDecks.add(d);
            }
        }

        if (sellableDecks.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No sellable decks available.");
            return;
        }

        String[] names = sellableDecks.stream().map(Deck::getName).toArray(String[]::new);
        String selectedName = (String) JOptionPane.showInputDialog(this, "Select a deck to sell:",
                "Sell Deck", JOptionPane.PLAIN_MESSAGE, null, names, names[0]);

        if (selectedName == null) return;

        Deck selected = findDeckByName(selectedName);
        double totalValue = selected.getCards().stream().mapToDouble(Card::getActualValue).sum();
        collector.addMoney(totalValue);
        decks.remove(selected);
        JOptionPane.showMessageDialog(this, "Sold deck \"" + selected.getName() + "\" for $" + String.format("%.2f", totalValue));
    }

    /**
     * A method allowing the user to sell a card from their collection through a JOptionPane
     */
    private void sellCardFromCollection() {
        ArrayList<Card> sellableCards = new ArrayList<>();
        for (Card card : collector.getCollection().getCards()) {
            if (card.getCount() > 0) {
                sellableCards.add(card);
            }
        }

        if (sellableCards.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No cards available to sell.");
            return;
        }

        String[] cardNames = sellableCards.stream().map(Card::getName).toArray(String[]::new);
        String selectedCardName = (String) JOptionPane.showInputDialog(this, "Select a card to sell:",
                "Sell Card", JOptionPane.PLAIN_MESSAGE, null, cardNames, cardNames[0]);

        if (selectedCardName == null) return;
        collector.sellCard(selectedCardName);
    }

    /**
     * A helper method used to find a deck by its name
     * @param name the name of the deck to search for
     * @return
     */
    private Deck findDeckByName(String name) {
        for (Deck deck : decks) {
            if (deck.getName().equalsIgnoreCase(name)) return deck;
        }
        return null;
    }

    /**
     * Displays a dialog to select a deck by name.
     *
     * @param action The action label (e.g., "Manage", "Delete")
     * @return The selected Deck or null if cancelled or not found
     */
    private Deck selectDeck(String action) {
        if (decks.isEmpty()) return null;

        String[] deckNames = decks.stream()
                .map(Deck::getName)
                .sorted()
                .toArray(String[]::new);

        String selected = (String) JOptionPane.showInputDialog(
                this,
                "Select a deck to " + action + ":",
                action + " Deck",
                JOptionPane.PLAIN_MESSAGE,
                null,
                deckNames,
                deckNames[0]
        );

        if (selected == null) return null;

        return decks.stream()
                .filter(d -> d.getName().equalsIgnoreCase(selected))
                .findFirst()
                .orElse(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainGUI::new);
    }
}
