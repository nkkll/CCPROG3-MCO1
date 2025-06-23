// File: Card.java
// Description: Defines the Card class used in the Trading Card Inventory System.
// Each card has a name, rarity, variant, base value, and count.
// This class provides methods for validation, value computation, and count management.
// Authors: Kathryn Pulido
// Date Created: 06/24/2025
// Last Updated: 06/24/2025
/**
 * The Card class represents a collectible trading card with relevant properties
 * and behaviors such as count tracking, value calculation, and detail display.
 */
public class Card {

    // Name of the card (unique identifier)
    private String name;

    // Rarity of the card (e.g., common, rare)
    private String rarity;

    // Variant type of the card (e.g., full-art)
    private String variant;

    // Base dollar value of the card
    private double baseValue;

    // Number of copies of the card in the collection
    private int count;

    // Allowed rarities
    private static final String[] VALID_RARITIES = {
        "common", "uncommon", "rare", "legendary"
    };

    // Allowed variants
    private static final String[] VALID_VARIANTS = {
        "normal", "extended-art", "full-art", "alt-art"
    };

    /**
     * Constructs a Card object with given parameters after validation.
     * 
     * @param name       the name of the card
     * @param rarity     the rarity of the card
     * @param variant    the visual variant of the card
     * @param baseValue  the base dollar value
     * @throws IllegalArgumentException if rarity or variant is invalid
     */
    public Card(String name, String rarity, String variant, double baseValue) {
        rarity = rarity.toLowerCase();
        variant = variant.toLowerCase();

        if (!isValidRarity(rarity)) {
            throw new IllegalArgumentException("Invalid rarity: " + rarity);
        }

        if (!isValidVariant(variant)) {
            throw new IllegalArgumentException("Invalid variant: " + variant);
        }

        this.name = name;
        this.rarity = rarity;
        this.variant = validateVariant(rarity, variant);
        this.baseValue = baseValue;
        this.count = 1;
    }

    /**
     * Ensures variant is "normal" if the card is not rare or legendary.
     * 
     * @param rarity  the rarity of the card
     * @param variant the input variant
     * @return a valid variant string
     */
    private String validateVariant(String rarity, String variant) {
        if (rarity.equals("common") || rarity.equals("uncommon")) {
            return "normal";
        }
        return variant;
    }

    /**
     * Checks if the input rarity is among allowed rarities.
     * 
     * @param rarity the rarity string to check
     * @return true if valid, false otherwise
     */
    public static boolean isValidRarity(String rarity) {
        for (int i = 0; i < VALID_RARITIES.length; i++) {
            if (VALID_RARITIES[i].equals(rarity)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the input variant is among allowed variants.
     * 
     * @param variant the variant string to check
     * @return true if valid, false otherwise
     */
    public static boolean isValidVariant(String variant) {
        for (int i = 0; i < VALID_VARIANTS.length; i++) {
            if (VALID_VARIANTS[i].equals(variant)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Calculates the card’s actual value based on its variant.
     * 
     * @return actual dollar value of the card
     */
    public double getActualValue() {
        if (variant.equals("extended-art")) {
            return baseValue * 1.5;
        } else if (variant.equals("full-art")) {
            return baseValue * 2.0;
        } else if (variant.equals("alt-art")) {
            return baseValue * 3.0;
        }
        return baseValue;
    }

    /**
     * Increases the count of this card by 1.
     */
    public void increaseCount() {
        count++;
    }

    /**
     * Decreases the count of this card by 1 (not below 0).
     */
    public void decreaseCount() {
        if (count > 0) {
            count--;
        }
    }

    /**
     * Gets the current count of this card.
     * 
     * @return number of this card in collection
     */
    public int getCount() {
        return count;
    }

    /**
     * Gets the name of the card.
     * 
     * @return name string
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the rarity of the card.
     * 
     * @return rarity string
     */
    public String getRarity() {
        return rarity;
    }

    /**
     * Gets the variant of the card.
     * 
     * @return variant string
     */
    public String getVariant() {
        return variant;
    }

    /**
     * Gets the base value of the card.
     * 
     * @return base dollar value
     */
    public double getBaseValue() {
        return baseValue;
    }

    /**
     * Displays the card's information in a formatted "card-style" table.
     */
    public void displayCardDetails() {
        String border = "+---------------------------+";
        System.out.println(border);
        System.out.printf("| %-25s |\n", name.toUpperCase());
        System.out.println(border);
        System.out.printf("| Rarity  : %-14s |\n", capitalize(rarity));
        System.out.printf("| Variant : %-14s |\n", capitalize(variant));
        System.out.printf("| Base $  : $%-13.2f |\n", baseValue);
        System.out.printf("| Value   : $%-13.2f |\n", getActualValue());
        System.out.printf("| Count   : %-14d |\n", count);
        System.out.println(border);
    }

    /**
     * Capitalizes the first letter of a string (helper method).
     * 
     * @param str the string to capitalize
     * @return capitalized version
     */
    private String capitalize(String str) {
        if (str == null || str.length() == 0) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    /**
     * Returns a string summary of the card for quick display.
     */
    public String toString() {
        return name + " [" + rarity + ", " + variant + "] | Base: $" + String.format("%.2f", baseValue)
                + " | Value: $" + String.format("%.2f", getActualValue()) + " | Count: " + count;
    }
}
