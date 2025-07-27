import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

/**
 * The Collection class manages the user's card collection.
 * It allows adding new cards, viewing, and managing card details.
 * Originally written as CollectionManager.
 */
public class Collection {

    private ArrayList<Card> collection;
    private Scanner scanner;

    /**
     * Constructs a Collection manager with the given list of cards and scanner input.
     * 
     * @param collection The ArrayList of cards in the user's collection.
     * @param scanner    The Scanner object for reading user input.
     */
    public Collection(ArrayList<Card> collection, Scanner scanner) {
        this.collection = collection;
        this.scanner = scanner;
    }

    /**
     * Adds a new card to the collection.
     */
    public void addCardToCollection() {
        System.out.print("Enter card name: ");
        String name = scanner.nextLine().trim();

        Card existing = findCardByName(name);
        if (existing != null) {
            System.out.println("Card already exists in the collection.");
            System.out.print("Would you like to increase the count by 1? (yes/no): ");
            String response = scanner.nextLine().trim().toLowerCase();
            if (response.equals("yes") || response.equals("y")) {
                existing.increaseCount();
                System.out.println("Card count increased.");
            } else {
                System.out.println("Card not modified.");
            }
            return;
        }

        String rarity = "";
        boolean validRarity = false;
        while (!validRarity) {
            System.out.print("Enter rarity (common, uncommon, rare, legendary): ");
            rarity = scanner.nextLine().trim().toLowerCase();
            validRarity = Card.isValidRarity(rarity);
            if (!validRarity) {
                System.out.println("Invalid rarity. Try again.");
            }
        }

        String variant = "normal";
        if (rarity.equals("rare") || rarity.equals("legendary")) {
            boolean validVariant = false;
            while (!validVariant) {
                System.out.print("Enter variant (normal, extended-art, full-art, alt-art): ");
                variant = scanner.nextLine().trim().toLowerCase();
                validVariant = Card.isValidVariant(variant);
                if (!validVariant) {
                    System.out.println("Invalid variant. Try again.");
                }
            }
        }

        System.out.print("Enter base value in dollars: ");
        double baseValue = 0;
        try {
            baseValue = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number. Card not added.");
            return;
        }

        try {
            Card newCard = new Card(name, rarity, variant, baseValue);
            collection.add(newCard);
            System.out.println("Card added successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Failed to create card: " + e.getMessage());
        }
    }

    /**
     * Displays the main collection menu.
     */
    public void viewCollectionMenu() {
        if (collection.isEmpty()) {
            System.out.println("Collection is empty.");
            return;
        }

        boolean viewing = true;
        while (viewing) {
            ArrayList<Card> sortedCards = new ArrayList<>(collection);
            Collections.sort(sortedCards, Comparator.comparing(Card::getName, String.CASE_INSENSITIVE_ORDER));

            System.out.println("\nYour Collection:");
            for (int i = 0; i < sortedCards.size(); i++) {
                Card card = sortedCards.get(i);
                System.out.println((i + 1) + ". " + card.getName() + " (Count: " + card.getCount() + ")");
            }

            System.out.print("Enter the number of a card to manage it, or 0 to go back: ");
            String input = scanner.nextLine().trim();
            int index;

            try {
                index = Integer.parseInt(input);
                if (index == 0) {
                    viewing = false;
                } else if (index >= 1 && index <= sortedCards.size()) {
                    Card selected = sortedCards.get(index - 1);
                    manageCardMenu(selected.getName());
                } else {
                    System.out.println("Invalid number.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }
    }

    /**
     * Allows user to manage a specific card in the collection.
     */
    private void manageCardMenu(String cardName) {
        Card card = findCardByName(cardName);
        if (card == null) {
            System.out.println("Card not found.");
            return;
        }

        boolean managing = true;
        while (managing) {
            System.out.println("\n--- Managing: " + card.getName() + " ---");
            System.out.println("1. View details");
            System.out.println("2. Decrease count");
            System.out.println("3. Go back");
            System.out.print("Choose an option: ");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    card.displayCardDetails();
                    break;
                case "2":
                    if (card.getCount() > 0) {
                        card.decreaseCount();
                        System.out.println("Count decreased. New count: " + card.getCount());
                    } else {
                        System.out.println("Card count is already 0.");
                    }
                    break;
                case "3":
                    managing = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    /**
     * Finds a card by its name.
     * 
     * @param name The name of the card to search for.
     * @return The Card object if found; otherwise null.
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
     * 
     * @return The list of cards.
     */
    public ArrayList<Card> getCards() {
        return collection;
    }
}
