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

    public Deck(String name) {
        this.name = name;
        this.cards = new ArrayList<>();
    }

    // Subclasses must implement these
    public abstract boolean canBeSold();
    public abstract double calculateSellPrice();

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

    public boolean removeCardByIndex(int index) {
        if (index >= 0 && index < cards.size()) {
            Card removed = cards.remove(index);
            System.out.println("Removed \"" + removed.getName() + "\" from deck \"" + name + "\".");
            return true;
        }
        System.out.println("Invalid selection.");
        return false;
    }

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

    public void viewDeckInteractive(ArrayList<Card> collection, Scanner scanner) {
        if (cards.isEmpty()) {
            System.out.println("Deck \"" + name + "\" is currently empty.");
            return;
        }

        ArrayList<Card> sorted = new ArrayList<>(cards);
        Collections.sort(sorted, Comparator.comparing(Card::getName, String.CASE_INSENSITIVE_ORDER));

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
                System.out.print("Enter number of the card to remove: ");
                try {
                    int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
                    if (idx >= 0 && idx < sorted.size()) {
                        Card removed = sorted.get(idx);
                        if (removeCardByIndex(cards.indexOf(removed))) {
                            int foundIndex = getCardIndexByName(collection, removed.getName());
                            if (foundIndex != -1) {
                                collection.get(foundIndex).increaseCount();
                            } else {
                                collection.add(new Card(removed.getName(), removed.getRarity(), removed.getVariant(), removed.getBaseValue()));
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

    private int getCardIndexByName(ArrayList<Card> list, String name) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getName().equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

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
