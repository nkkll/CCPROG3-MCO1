// File: CollectionManager.java
// Description: Manages the user's card collection, allowing them to add, view,
// and update cards through a text-based interface.
// Authors: Kathryn Pulido
// Date Created: 06/24/2025
// Last Updated: 06/24/2025
/**
 * The CollectionManager class handles user interactions related to managing
 * the trading card collection, including adding cards, viewing details,
 * and adjusting quantities.
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class CollectionManager {

    // The list of cards in the collection
    private Collection collection;

    // Scanner for user input
    private Scanner scanner;

    /**
     * Constructs a CollectionManager with a given card list and input scanner.
     *
     * @param collection the list of cards representing the collection
     * @param scanner    the Scanner used for user input
     */
    public CollectionManager(Collection collection, Scanner scanner) {
        this.collection = collection;
        this.scanner = scanner;
    }

    /**
     * Adds a new card to the collection. If a card with the same name already exists,
     * the user is asked whether to increase its count instead.
     */
    public void addCardToCollection() {
        System.out.print("Enter card name: ");
        String name = scanner.nextLine().trim();

        Card existing = collection.searchCard(name);
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

        String rarity;
        while (true) {
            System.out.print("Enter rarity (common, uncommon, rare, legendary): ");
            rarity = scanner.nextLine().trim().toLowerCase();
            if (Card.isValidRarity(rarity)) break;
            System.out.println("Invalid rarity. Try again.");
        }

        String variant = "normal";
        if (rarity.equals("rare") || rarity.equals("legendary")) {
            while (true) {
                System.out.print("Enter variant (normal, extended-art, full-art, alt-art): ");
                variant = scanner.nextLine().trim().toLowerCase();
                if (Card.isValidVariant(variant)) break;
                System.out.println("Invalid variant. Try again.");
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
     * Displays the collection and allows the user to view or manage individual cards.
     * The list is shown in alphabetical order.
     */
    public void viewCollectionMenu() {
        if (collection.isEmpty()) {
            System.out.println("Collection is empty.");
            return;
        }

        ArrayList<Card> sortedCards = new ArrayList<>(collection);
        Collections.sort(sortedCards, new Comparator<Card>() {
            public int compare(Card c1, Card c2) {
                return c1.getName().compareToIgnoreCase(c2.getName());
            }
        });

        boolean viewing = true;
        while (viewing) {
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
     * Displays a menu for a specific card, allowing the user to view details
     * or decrease the card's count.
     *
     * @param cardName the name of the card to manage
     */
    private void manageCardMenu(String cardName) {
        Card card = collection.searchCard(cardName);
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
                        System.out.println("Card count is already 0. Cannot decrease further.");
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
}
