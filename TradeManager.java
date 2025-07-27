import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

/**
 * The TradeManager class handles trading cards between a binder and an external source.
 * It compares values and allows users to cancel trades when the difference is $1.00 or more.
 */
public class TradeManager {

    /**
     * Initiates the trade process with a given binder.
     *
     * @param binder     The binder from which a card will be traded.
     * @param collection The collection to which new cards are added.
     * @param scanner    Scanner for user input.
     */
    public static void initiateTrade(Binder binder, ArrayList<Card> collection, Scanner scanner) {
        ArrayList<Card> sorted = new ArrayList<>(binder.getCards());
        Collections.sort(sorted, Comparator.comparing(Card::getName, String.CASE_INSENSITIVE_ORDER));

        System.out.println("\n--- Select a Card to Trade Away ---");
        for (int i = 0; i < sorted.size(); i++) {
            System.out.println((i + 1) + ". " + sorted.get(i).getName());
        }

        System.out.print("Enter number of the card to trade: ");
        String input = scanner.nextLine().trim();

        int index = -1;
        try {
            index = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }

        if (index < 1 || index > sorted.size()) {
            System.out.println("Invalid card number.");
            return;
        }

        Card outgoing = sorted.get(index - 1);

        boolean removed = binder.removeCard(outgoing);
        if (!removed) {
            System.out.println("Error: Could not locate card in binder.");
            return;
        }

        System.out.println("\n--- Enter Incoming Card Details ---");
        System.out.print("Card Name: ");
        String name = scanner.nextLine().trim();

        String rarity = "";
        boolean validRarity = false;
        while (!validRarity) {
            System.out.print("Rarity (common, uncommon, rare, legendary): ");
            rarity = scanner.nextLine().trim().toLowerCase();
            validRarity = Card.isValidRarity(rarity);
            if (!validRarity) {
                System.out.println("Invalid rarity.");
            }
        }

        String variant = "normal";
        if (rarity.equals("rare") || rarity.equals("legendary")) {
            boolean validVariant = false;
            while (!validVariant) {
                System.out.print("Variant (normal, extended-art, full-art, alt-art): ");
                variant = scanner.nextLine().trim().toLowerCase();
                validVariant = Card.isValidVariant(variant);
                if (!validVariant) {
                    System.out.println("Invalid variant.");
                }
            }
        }

        System.out.print("Base value: ");
        double baseValue;
        try {
            baseValue = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid value. Trade cancelled.");
            binder.addCard(outgoing);
            return;
        }

        Card incoming = new Card(name, rarity, variant, baseValue);

        double diff = Math.abs(outgoing.getActualValue() - incoming.getActualValue());
        if (diff >= 1.0) {
            System.out.printf("Value difference is $%.2f. Proceed with trade? (yes/no): ", diff);
            String confirm = scanner.nextLine().trim().toLowerCase();
            if (!confirm.equals("yes") && !confirm.equals("y")) {
                System.out.println("Trade cancelled. Returning outgoing card to binder.");
                binder.addCard(outgoing);
                return;
            }
        }

        boolean addedToBinder = binder.addCard(incoming);
        if (addedToBinder) {
            System.out.println("Trade complete. Incoming card added to binder.");
        } else {
            System.out.println("Binder is full. Trade failed. Returning outgoing card.");
            binder.addCard(outgoing);
            return;
        }

        Card existing = findCard(collection, incoming.getName());
        if (existing == null) {
            collection.add(new Card(name, rarity, variant, baseValue));
            Card added = findCard(collection, name);
            if (added != null) {
                added.setCount(0);
            }
        }
    }

    /**
     * Searches for a card in the collection by name.
     *
     * @param collection The user's card collection.
     * @param name       The name of the card.
     * @return Card if found, else null.
     */
    private static Card findCard(ArrayList<Card> collection, String name) {
        for (Card c : collection) {
            if (c.getName().equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }
}
