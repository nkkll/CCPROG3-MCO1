import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

/**
 * Abstract class representing a card deck.
 * Supports adding/removing cards, viewing, and returning cards to a collection.
 * Now supports two types: NormalDeck and SellableDeck.
 */
public abstract class Deck {
    protected String name;
    protected ArrayList<Card> cards;

    /**
     * Constructs a deck given a name
     * @param name
     */
    public Deck(String name) {
        this.name = name;
        this.cards = new ArrayList<>();
    }

    /**
     * methods that must be implemented by subclasses
     * @return
     */
    public abstract boolean canBeSold();
    public abstract double calculateSellPrice();

    /**
     * A method for adding a Card to a deck.
     * @param card the new card to be added to deck
     * @return true if added, otherwise false
     */
    public boolean addCard(Card card) {
        if (cards.size() >= 10) {
            System.out.println("Deck is full. Maximum of 10 unique cards allowed.");
            return false;
        }

        for (Card existing : cards) {
            if (existing.getName().equalsIgnoreCase(card.getName())) {
                System.out.println("Card \"" + card.getName() + "\" already exists in this deck.");
                return false;
            }
        }

        cards.add(card);
        System.out.println("Added \"" + card.getName() + "\" to deck \"" + name + "\".");
        return true;
    }

    /**
     * A method for removing a card from a deck given its index
     * @param index the index of the card to be removed
     * @return true if removed, otherwise false
     */
    public boolean removeCardByIndex(int index) {
        if (index >= 0 && index < cards.size()) {
            Card removed = cards.remove(index);
            System.out.println("Removed \"" + removed.getName() + "\" from deck \"" + name + "\".");
            return true;
        }
        System.out.println("Invalid selection.");
        return false;
    }

    /**
     * A method returning all cards in a deck back to the collection
     * @param collection the card collection a user has
     */
    public void returnAllCardsToCollection(ArrayList<Card> collection) {
        for (Card card : cards) {
            int foundIndex = getCardIndexByName(collection, card.getName());
            if (foundIndex != -1) {
                collection.get(foundIndex).increaseCount();
            } else {
                collection.add(new Card(card.getName(), card.getRarity(), card.getVariant(), card.getBaseValue()));
            }
        }
        cards.clear();
    }

    /**
     * GUI for Managing Deck including addition and removal of cards.
     * @param parent the JFrame MainGUI
     * @param collection the collection of the user
     */
    public void viewDeckGUI(JFrame parent, ArrayList<Card> collection) {
        JDialog dialog = new JDialog(parent, "Manage Deck: " + name, true);
        dialog.setSize(800, 500);
        dialog.setLayout(new BorderLayout());

        // Sort deck cards
        ArrayList<Card> sortedDeck = new ArrayList<>(cards);
        sortedDeck.sort(Comparator.comparing(Card::getName, String.CASE_INSENSITIVE_ORDER));

        // Deck card list
        DefaultListModel<Card> deckModel = new DefaultListModel<>();
        for (Card c : sortedDeck) deckModel.addElement(c);
        JList<Card> deckList = new JList<>(deckModel);
        deckList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        deckList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(value.getName());
            if (isSelected) label.setBackground(Color.LIGHT_GRAY);
            label.setOpaque(true);
            return label;
        });

        // Collection card list
        DefaultListModel<Card> collectionModel = new DefaultListModel<>();
        for (Card c : collection) {
            if (c.getCount() > 0) collectionModel.addElement(c);
        }
        JList<Card> collectionList = new JList<>(collectionModel);
        collectionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        collectionList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(value.getName() + " (x" + value.getCount() + ")");
            if (isSelected) label.setBackground(Color.CYAN);
            label.setOpaque(true);
            return label;
        });

        // Buttons
        JButton addBtn = new JButton("Add to Deck →");
        JButton removeBtn = new JButton("← Remove from Deck");
        JButton closeBtn = new JButton("Close");

        addBtn.addActionListener(e -> {
            Card selected = collectionList.getSelectedValue();
            if (selected != null && selected.getCount() > 0) {
                Card toAdd = new Card(selected.getName(), selected.getRarity(), selected.getVariant(), selected.getBaseValue());
                addCard(toAdd);
                selected.decreaseCount();
                deckModel.addElement(toAdd);

                collectionModel.removeElement(selected);
                if (selected.getCount() > 0) {
                    collectionModel.addElement(selected);
                }
            }
        });

        removeBtn.addActionListener(e -> {
            Card selected = deckList.getSelectedValue();
            if (selected != null) {
                int indexInDeck = cards.indexOf(selected);
                if (removeCardByIndex(indexInDeck)) {
                    deckModel.removeElement(selected);

                    // Restore count to collection
                    boolean found = false;
                    for (Card c : collection) {
                        if (c.getName().equalsIgnoreCase(selected.getName())) {
                            c.increaseCount();
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        collection.add(selected);
                        selected.setCount(1);
                    }
                    collectionModel.removeAllElements();
                    for (Card c : collection) {
                        if (c.getCount() > 0) collectionModel.addElement(c);
                    }
                }
            }
        });

        closeBtn.addActionListener(e -> dialog.dispose());

        // Layout
        JPanel centerPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        centerPanel.add(new JScrollPane(collectionList));
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        buttonPanel.add(addBtn);
        buttonPanel.add(removeBtn);
        centerPanel.add(buttonPanel);
        centerPanel.add(new JScrollPane(deckList));

        dialog.add(centerPanel, BorderLayout.CENTER);
        dialog.add(closeBtn, BorderLayout.SOUTH);

        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    /**
     * Helper method that gets a card's index using its name
     * @param list the list of cards in a deck
     * @param name the name of the card
     * @return
     */
    private int getCardIndexByName(ArrayList<Card> list, String name) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getName().equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * A method displaying the card in table format
     * @param card
     */
    private void printCardAsTable(Card card) {
        String border = "+---------------------------+";
        System.out.println(border);
        System.out.printf("| %-25s |\n", card.getName().toUpperCase());
        System.out.println(border);
        System.out.printf("| Rarity  : %-14s |\n", capitalize(card.getRarity()));
        System.out.printf("| Variant : %-14s |\n", capitalize(card.getVariant()));
        System.out.printf("| Base $  : $%-13.2f |\n", card.getBaseValue());
        System.out.printf("| Value   : $%-13.2f |\n", card.getActualValue());
        System.out.printf("| Count   : %-14d |\n", card.getCount());
        System.out.println(border);
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    public boolean containsCard(String cardName) {
        for (Card card : cards) {
            if (card.getName().equalsIgnoreCase(cardName)) return true;
        }
        return false;
    }

    public int size() {
        return cards.size();
    }

    public String getName() {
        return name;
    }

    public ArrayList<Card> getCards() {
        return cards;
    }
}

