// File: Main.java
// Description: Entry point for the Trading Card Inventory System (TCIS) program.
// This class contains the main menu logic and manages interaction between the collection and deck system.
// Authors: Kathryn Pulido, Anika Lumbania
// Date Created: 06/24/2025
// Last Updated: 06/24/2025

import java.util.*;

/**
 * Main class to run the Trading Card Inventory System.
 * Handles user interface and navigates between features.
 */
public class Main {

    // Collection of cards the user owns
    private static ArrayList<Card> collection = new ArrayList<>();

    // List of decks created by the user
    private static ArrayList<Deck> decks = new ArrayList<>();

    // Scanner for user input
    private static Scanner scanner = new Scanner(System.in);

    // Collection manager handles card-specific features
    private static CollectionManager collectionManager = new CollectionManager(collection, scanner);

    /**
     * Main method. Runs the primary menu and handles top-level navigation.
     */
    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            System.out.println("\n--- Trading Card Inventory System ---");
            System.out.println("1. Add a card");
            System.out.println("2. View collection");
            System.out.println("3. Decks");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    collectionManager.addCardToCollection();
                    break;
                case "2":
                    collectionManager.viewCollectionMenu();
                    break;
                case "3":
                    decksMenu();
                    break;
                case "4":
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }

        scanner.close();
    }

    /**
     * Displays the Decks menu and routes to appropriate deck management features.
     */
    private static void decksMenu() {
        boolean inDecks = true;

        while (inDecks) {
            System.out.println("\n--- Decks Menu ---");
            System.out.println("1. Create a deck");
            System.out.println("2. Manage a deck");
            System.out.println("3. Delete a deck");
            System.out.println("4. Go back");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    createDeck();
                    break;
                case "2":
                    manageDeck();
                    break;
                case "3":
                    deleteDeck();
                    break;
                case "4":
                    inDecks = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    /**
     * Allows user to create a new deck by entering a unique deck name.
     */
    private static void createDeck() {
        System.out.print("Enter a name for the new deck: ");
        String deckName = scanner.nextLine().trim();

        if (findDeckByName(deckName) != null) {
            System.out.println("Deck with that name already exists.");
            return;
        }

        decks.add(new Deck(deckName));
        System.out.println("Deck \"" + deckName + "\" created.");
    }

    /**
     * Allows user to manage a selected deck.
     */
    private static void manageDeck() {
        if (decks.isEmpty()) {
            System.out.println("No decks available to manage.");
            return;
        }

        Deck selectedDeck = selectDeck("manage");
        if (selectedDeck == null) return;

        boolean managing = true;
        while (managing) {
            System.out.println("\n--- Managing Deck: " + selectedDeck.getName() + " ---");
            System.out.println("1. View cards in deck");
            System.out.println("2. Add card from collection to deck");
            System.out.println("3. Go back");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    selectedDeck.viewDeckInteractive(collection, scanner);
                    break;
                case "2":
                    addCardToDeckMenu(selectedDeck);
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
     * Displays available cards from collection and allows user to add one to a deck.
     * 
     * @param deck The deck to which a card will be added.
     */
    private static void addCardToDeckMenu(Deck deck) {
        ArrayList<Card> availableCards = new ArrayList<>();
        for (Card card : collection) {
            if (card.getCount() > 0) {
                availableCards.add(card);
            }
        }

        if (availableCards.isEmpty()) {
            System.out.println("No cards available in collection to add.");
            return;
        }

        Collections.sort(availableCards, Comparator.comparing(Card::getName));

        System.out.println("\nAvailable Cards in Collection:");
        for (int i = 0; i < availableCards.size(); i++) {
            Card card = availableCards.get(i);
            System.out.println((i + 1) + ". " + card.getName() + " (Count: " + card.getCount() + ")");
        }

        System.out.print("Enter the number of the card to add to the deck, or 0 to go back: ");
        String input = scanner.nextLine().trim();

        try {
            int index = Integer.parseInt(input);
            if (index == 0) return;
            if (index < 1 || index > availableCards.size()) {
                System.out.println("Invalid number.");
                return;
            }

            Card selected = availableCards.get(index - 1);
            if (deck.addCard(selected)) {
                selected.decreaseCount();
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    /**
     * Deletes a selected deck and returns its cards back to the collection.
     */
    private static void deleteDeck() {
        if (decks.isEmpty()) {
            System.out.println("No decks to delete.");
            return;
        }

        Deck selectedDeck = selectDeck("delete");
        if (selectedDeck == null) return;

        decks.remove(selectedDeck);
        selectedDeck.returnAllCardsToCollection(collection);
        System.out.println("Deck \"" + selectedDeck.getName() + "\" deleted and its cards returned to the collection.");
    }

    /**
     * Allows user to select a deck from the list based on action context.
     * 
     * @param action The action being performed (e.g., "manage", "delete").
     * @return The selected Deck object or null if invalid.
     */
    private static Deck selectDeck(String action) {
        Collections.sort(decks, Comparator.comparing(Deck::getName));

        System.out.println("Available decks:");
        for (int i = 0; i < decks.size(); i++) {
            System.out.println((i + 1) + ". " + decks.get(i).getName());
        }

        System.out.print("Enter the number of the deck to " + action + ": ");
        String input = scanner.nextLine().trim();
        int index;

        try {
            index = Integer.parseInt(input);
            if (index < 1 || index > decks.size()) {
                System.out.println("Invalid number.");
                return null;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return null;
        }

        return decks.get(index - 1);
    }

    /**
     * Helper method to find a deck by name (case-insensitive).
     * 
     * @param name The name of the deck.
     * @return The Deck object if found, otherwise null.
     */
    private static Deck findDeckByName(String name) {
        for (Deck deck : decks) {
            if (deck.getName().equalsIgnoreCase(name)) {
                return deck;
            }
        }
        return null;
    }
}
