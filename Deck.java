// File: Deck.java
// Description: Represents a deck of cards and manages operations like
// adding/removing cards, viewing deck contents, and restoring cards back to the collection.
// Authors: Kathryn Pulido
// Date Created: 06/24/2025
// Last Updated: 06/24/2025
/**
 * The Deck class represents a collection of up to 10 unique trading cards.
 * It provides methods for adding/removing cards, viewing card details, and
 * returning all cards to a user's collection.
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class Deck {
    private String name;               // Name of the deck
    private ArrayList<Card> cards;     // Cards currently in the deck

    /**
     * Constructs a deck with a specified name.
     * 
     * @param name the name of the deck
     */
    public Deck(String name) {
        this.name = name;
        this.cards = new ArrayList<>();
    }

    /**
     * Adds a card to the deck if it's not already included and if the deck has space.
     * 
     * @param card the card to add
     * @return true if the card was successfully added, false otherwise
     */
    public boolean addCard(Card card) {
        if (cards.size() >= 10) {
            System.out.println("Deck is full. Maximum of 10 unique cards allowed.");
            return false;
        }

        for (Card c : cards) {
            if (c.getName().equalsIgnoreCase(card.getName())) {
                System.out.println("Card \"" + card.getName() + "\" already exists in this deck.");
                return false;
            }
        }

        cards.add(card);
        System.out.println("Added \"" + card.getName() + "\" to deck \"" + name + "\".");
        return true;
    }

    /**
     * Removes a card from the deck based on its index.
     * 
     * @param index the position of the card in the list
     * @return true if the card was removed, false if invalid index
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
     * Returns all cards in the deck to the main collection, restoring their count
     * or adding them back if missing.
     * 
     * @param collection the main card collection to return cards to
     */
    public void returnAllCardsToCollection(ArrayList<Card> collection) {
        for (Card card : cards) {
            Card found = findCardByName(collection, card.getName());
            if (found != null) {
                found.increaseCount();
            } else {
                Card restored = new Card(card.getName(), card.getRarity(), card.getVariant(), card.getBaseValue());
                collection.add(restored);
            }
        }
        cards.clear();
    }

    /**
     * Allows users to interactively view cards in the deck, inspect details, or remove cards.
     * 
     * @param collection the main collection (used for returning removed cards)
     * @param scanner    the Scanner for user input
     */
    public void viewDeckInteractive(ArrayList<Card> collection, Scanner scanner) {
        if (cards.isEmpty()) {
            System.out.println("Deck \"" + name + "\" is currently empty.");
            return;
        }

        ArrayList<Card> sorted = new ArrayList<>(cards);
        Collections.sort(sorted, new Comparator<Card>() {
            public int compare(Card a, Card b) {
                return a.getName().compareToIgnoreCase(b.getName());
            }
        });

        boolean viewing = true;
        while (viewing) {
            System.out.println("\nCards in Deck \"" + name + "\":");
            for (int i = 0; i < sorted.size(); i++) {
                Card card = sorted.get(i);
                System.out.println((i + 1) + ". " + card.getName());
            }

            System.out.print("Enter number to view card details, R to remove a card, or 0 to go back: ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("0")) {
                viewing = false;
            } else if (input.equalsIgnoreCase("R")) {
                // Handle card removal
                System.out.print("Enter number of the card to remove: ");
                try {
                    int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
                    if (idx >= 0 && idx < sorted.size()) {
                        Card removed = sorted.get(idx);
                        if (removeCardByIndex(cards.indexOf(removed))) {
                            Card collectionCard = findCardByName(collection, removed.getName());
                            if (collectionCard != null) {
                                collectionCard.increaseCount();
                            } else {
                                Card newCopy = new Card(removed.getName(), removed.getRarity(), removed.getVariant(), removed.getBaseValue());
                                collection.add(newCopy);
                            }
                            sorted.remove(idx);
                        }
                    } else {
                        System.out.println("Invalid number.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input.");
                }
            } else {
                // Handle card detail viewing
                try {
                    int index = Integer.parseInt(input);
                    if (index >= 1 && index <= sorted.size()) {
                        printCardAsTable(sorted.get(index - 1));
                    } else {
                        System.out.println("Invalid number.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input.");
                }
            }
        }
    }

    /**
     * Helper method to find a card by name from a list.
     * 
     * @param list the list of cards to search in
     * @param name the name of the card to find
     * @return the Card object if found, null otherwise
     */
    private Card findCardByName(ArrayList<Card> list, String name) {
        for (Card card : list) {
            if (card.getName().equalsIgnoreCase(name)) {
                return card;
            }
        }
        return null;
    }

    /**
     * Displays the details of a card in a formatted table layout.
     * 
     * @param card the card to display
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

    /**
     * Capitalizes the first letter of a string.
     * 
     * @param str the string to capitalize
     * @return capitalized version of the string
     */
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    /**
     * Checks if a card with a given name exists in the deck.
     * 
     * @param cardName the name of the card to check
     * @return true if the card is found, false otherwise
     */
    public boolean containsCard(String cardName) {
        for (Card c : cards) {
            if (c.getName().equalsIgnoreCase(cardName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the current number of cards in the deck.
     * 
     * @return the deck size
     */
    public int size() {
        return cards.size();
    }

    /**
     * Gets the name of the deck.
     * 
     * @return the deck's name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the list of cards in the deck.
     * 
     * @return the list of cards
     */
    public ArrayList<Card> getCards() {
        return cards;
    }
}
