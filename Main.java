// File: Main.java
// Description: Entry point for the Trading Card Inventory System (TCIS) program.
// This class contains the main menu logic and manages interaction between the collection, deck, and binder system.
// Authors: Kathryn Pulido, Anika Lumbania
// Date Created: 06/24/2025
// Last Updated: 07/26/2025

import java.util.*;

/**
 * Main class to run the Trading Card Inventory System.
 * Handles user interface and navigates between features.
 */
public class Main {

    /** List of decks created by the user */
    private static ArrayList<Deck> decks = new ArrayList<>();

    /** List of binders created by the user */
    private static ArrayList<Binder> binders = new ArrayList<>();

    /** Scanner for user input */
    private static Scanner scanner = new Scanner(System.in);

    /** Collector manages collection and money */
    private static Collector collector = new Collector(scanner);

    /**
     * Main method. Runs the primary menu and handles top-level navigation.
     * 
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Trading Card Inventory System ---");
            System.out.println("1. Add a card");
            System.out.println("2. View collection");
            System.out.println("3. Binders");
            System.out.println("4. Decks");
            System.out.println("5. Sell a card from collection");
            System.out.println("6. View collector's money");
            System.out.println("7. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    collector.getCollection().addCardToCollection();
                    break;
                case "2":
                    collector.getCollection().viewCollectionMenu();
                    break;
                case "3":
                    Binder.openBinderMenu(binders, collector.getCollection().getCards(), collector, scanner);
                    break;
                case "4":
                    decksMenu();
                    break;
                case "5":
                    sellCardFromCollection();
                    break;
                case "6":
                    System.out.printf("Total Money: $%.2f\n", collector.getMoney());
                    break;
                case "7":
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
            System.out.println("4. Sell a deck");
            System.out.println("5. Go back");
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
                    sellDeck();
                    break;
                case "5":
                    inDecks = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    /**
     * Allows user to create a new deck by entering a unique deck name and selecting its type.
     */
    private static void createDeck() {
        System.out.print("Enter a name for the new deck: ");
        String deckName = scanner.nextLine().trim();

        if (findDeckByName(deckName) != null) {
            System.out.println("Deck with that name already exists.");
            return;
        }

        System.out.println("Select deck type:");
        System.out.println("1. Normal Deck");
        System.out.println("2. Sellable Deck");
        System.out.print("Enter option: ");
        String typeInput = scanner.nextLine().trim();

        Deck newDeck = null;

        switch (typeInput) {
            case "1":
                newDeck = new NormalDeck(deckName);
                break;
            case "2":
                newDeck = new SellableDeck(deckName);
                break;
            default:
                System.out.println("Invalid deck type.");
                return;
        }

        decks.add(newDeck);
        System.out.println(newDeck.getClass().getSimpleName() + " \"" + deckName + "\" created.");
    }

    /**
     * Allows user to manage a selected deck.
     * Enables viewing, adding cards, or exiting the deck menu.
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
                    selectedDeck.viewDeckInteractive(collector.getCollection().getCards(), scanner);
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
        for (Card card : collector.getCollection().getCards()) {
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
        selectedDeck.returnAllCardsToCollection(collector.getCollection().getCards());
        System.out.println("Deck \"" + selectedDeck.getName() + "\" deleted and its cards returned to the collection.");
    }

    /**
     * Allows the user to sell a SellableDeck.
     * The deck and its cards are permanently removed and the collector earns money.
     */
    private static void sellDeck() {
        ArrayList<Deck> sellableDecks = new ArrayList<>();
        for (Deck d : decks) {
            if (d instanceof SellableDeck) {
                sellableDecks.add(d);
            }
        }

        if (sellableDecks.isEmpty()) {
            System.out.println("No sellable decks available.");
            return;
        }

        System.out.println("\nSellable Decks:");
        for (int i = 0; i < sellableDecks.size(); i++) {
            System.out.println((i + 1) + ". " + sellableDecks.get(i).getName());
        }

        System.out.print("Enter the number of the deck to sell: ");
        String input = scanner.nextLine().trim();
        int index;

        try {
            index = Integer.parseInt(input);
            if (index < 1 || index > sellableDecks.size()) {
                System.out.println("Invalid number.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }

        Deck selected = sellableDecks.get(index - 1);
        double totalValue = 0;
        for (Card card : selected.getCards()) {
            totalValue += card.getActualValue();
        }

        collector.addMoney(totalValue);
        decks.remove(selected);
        System.out.printf("Sold deck \"%s\" for $%.2f.\n", selected.getName(), totalValue);
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
     * Displays a list of cards in the collection and allows the user to select one to sell.
     */
    private static void sellCardFromCollection() {
        ArrayList<Card> sellableCards = new ArrayList<>();
        for (Card card : collector.getCollection().getCards()) {
            if (card.getCount() > 0) {
                sellableCards.add(card);
            }
        }

        if (sellableCards.isEmpty()) {
            System.out.println("No cards available to sell.");
            return;
        }

        Collections.sort(sellableCards, Comparator.comparing(Card::getName));

        System.out.println("\nAvailable Cards to Sell:");
        for (int i = 0; i < sellableCards.size(); i++) {
            Card card = sellableCards.get(i);
            System.out.println((i + 1) + ". " + card.getName() + " (Count: " + card.getCount() + ")");
        }

        System.out.print("Enter the number of the card to sell, or 0 to go back: ");
        String input = scanner.nextLine().trim();

        try {
            int index = Integer.parseInt(input);
            if (index == 0) return;
            if (index < 1 || index > sellableCards.size()) {
                System.out.println("Invalid number.");
                return;
            }

            Card selected = sellableCards.get(index - 1);
            collector.sellCard(selected.getName());

        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    /**
     * Finds a deck by its name.
     * 
     * @param name The name of the deck.
     * @return The Deck object or null if not found.
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
