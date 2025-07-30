import java.util.ArrayList;
import java.util.Scanner;

/**
 * The Collector class represents the player.
 * It manages their card collection, money, binders, and decks.
 * 
 * This version only includes card-related functionality for MCO2 Phase 1.
 */
public class Collector {

    private Collection collection;
    private double money;

    /**
     * Constructs a Collector with an empty collection and zero money.
     * 
     * @param scanner Scanner used to pass to Collection.
     */
    public Collector(Scanner scanner) {
        this.collection = new Collection(new ArrayList<>());
        this.money = 0.0;
    }

    /**
     * Default constructor for GUI usage (no Scanner input).
     */
    public Collector() {
        this.collection = new Collection(new ArrayList<>());
        this.money = 0.0;
    }

    /**
     * Gets the collector's current money.
     * 
     * @return money as double.
     */
    public double getMoney() {
        return money;
    }

    /**
     * Adds the given amount to the collector's money.
     * 
     * @param amount amount to add.
     */
    public void addMoney(double amount) {
        this.money += amount;
    }

    /**
     * Attempts to sell a card from the collection by name.
     * 
     * @param name name of the card.
     * @return true if sold, false otherwise.
     */
    public boolean sellCard(String name) {
        Card card = collection.findCardByName(name);
        if (card == null) {
            System.out.println("Card not found in collection.");
            return false;
        }

        if (card.getCount() <= 0) {
            System.out.println("Card count is already 0. Cannot sell.");
            return false;
        }

        double value = card.getRealValue();
        card.decreaseCount();
        addMoney(value);
        System.out.printf("Sold 1 '%s' for $%.2f. Total money: $%.2f\n", card.getName(), value, money);
        return true;
    }

    /**
     * Gets the collection owned by the collector.
     * 
     * @return collection object.
     */
    public Collection getCollection() {
        return collection;
    }
}
