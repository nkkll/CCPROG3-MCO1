import java.util.ArrayList;
import java.util.Comparator;
import javax.swing .*;

/**
 * The Collection class manages the user's card collection.
 * It allows adding new cards, viewing, and managing card details.
 * Originally written as CollectionManager.
 */
public class Collection {

    private ArrayList<Card> collection;

    /**
     * Constructs a Collection manager with the given list of cards and scanner input.
     *
     * @param collection The ArrayList of cards in the user's collection.
     */

    public Collection(ArrayList<Card> collection) {
        this.collection = collection;
    }

    /**
     * Adds a new card to the collection using GUI prompts.
     */
    public void addCardToCollection(MainGUI parentComponent) {
        String name = JOptionPane.showInputDialog(parentComponent, "Enter card name:");
        if (name == null || name.trim().isEmpty()) return;
        name = name.trim();

        Card existing = findCardByName(name);
        if (existing != null) {
            int option = JOptionPane.showConfirmDialog(
                    parentComponent,
                    "Card already exists. Increase count by 1?",
                    "Duplicate Card",
                    JOptionPane.YES_NO_OPTION
            );
            if (option == JOptionPane.YES_OPTION) {
                existing.increaseCount();
                JOptionPane.showMessageDialog(parentComponent, "Card count increased.");
            }
            return;
        }

        String[] rarities = {"common", "uncommon", "rare", "legendary"};
        String rarity = (String) JOptionPane.showInputDialog(
                parentComponent,
                "Select card rarity:",
                "Card Rarity",
                JOptionPane.QUESTION_MESSAGE,
                null,
                rarities,
                rarities[0]
        );

        if (rarity == null) return;

        String variant = "normal";
        if (rarity.equals("rare") || rarity.equals("legendary")) {
            String[] variants = {"normal", "extended-art", "full-art", "alt-art"};
            variant = (String) JOptionPane.showInputDialog(
                    parentComponent,
                    "Select card variant:",
                    "Card Variant",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    variants,
                    variants[0]
            );

            if (variant == null) return; // user cancelled
        }

        double baseValue = 0;
        boolean validValue = false;
        while (!validValue) {
            String valueStr = JOptionPane.showInputDialog(parentComponent, "Enter base value in dollars:");
            if (valueStr == null) return;
            try {
                baseValue = Double.parseDouble(valueStr.trim());
                validValue = true;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(parentComponent, "Invalid number. Try again.");
            }
        }

        // Create card
        try {
            Card newCard = new Card(name, rarity, variant, baseValue);
            collection.add(newCard);
            JOptionPane.showMessageDialog(parentComponent, "Card added successfully.");
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(parentComponent, "Failed to create card: " + e.getMessage());
        }
    }

     public void modifyCardCount(JFrame parent) {
        if (collection.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "No cards in collection.");
            return;
        }

        String[] cardNames = collection.stream().map(Card::getName).toArray(String[]::new);
        String selected = (String) JOptionPane.showInputDialog(parent, "Select card:",
                "Modify Card Count", JOptionPane.PLAIN_MESSAGE, null, cardNames, cardNames[0]);

        if (selected == null) return;

        Card card = collection.stream().filter(c -> c.getName().equals(selected)).findFirst().orElse(null);
        if (card == null) return;

        String countStr = JOptionPane.showInputDialog(parent, "Current count: " + card.getCount() + "\nEnter new count:");
        if (countStr == null) return;

        try {
            int newCount = Integer.parseInt(countStr);
            card.setCount(newCount);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parent, "Invalid number.");
        }
    }

        /**
         * Finds a card by its name.
         */
        public Card findCardByName(String name) {
            for (Card card : collection) {
                if (card.getName().equalsIgnoreCase(name)) {
                    return card;
                }
            }
            return null;
        }

        /**
         * Returns the internal list of cards in the collection.
         */
        public ArrayList<Card> getCards() {
            return collection;
        }

        /**
         * Displays all cards in the collection in a dialog.
         */
        public void showCollection(MainGUI parentComponent) {
            if (collection.isEmpty()) {
                JOptionPane.showMessageDialog(parentComponent, "Collection is empty.");
                return;
            }

            ArrayList<Card> sorted = new ArrayList<>(collection);
            sorted.sort(Comparator.comparing(Card::getName, String.CASE_INSENSITIVE_ORDER));

            StringBuilder message = new StringBuilder("Your Collection:\n");
            for (int i = 0; i < sorted.size(); i++) {
                Card card = sorted.get(i);
                message.append((i + 1)).append(". ")
                        .append(card.getName()).append(" (Count: ")
                        .append(card.getCount()).append(")\n");
            }
            JOptionPane.showMessageDialog(parentComponent, message.toString());
        }
}
