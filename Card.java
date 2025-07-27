/**
 * Represents a trading card with a name, rarity, variant, base value, and count.
 * Includes methods for value calculation, validation, and display formatting.
 */
public class Card {

    private String name;
    private String rarity;
    private String variant;
    private double baseValue;
    private int count;

    private static final String[] VALID_RARITIES = {
        "common", "uncommon", "rare", "legendary"
    };

    private static final String[] VALID_VARIANTS = {
        "normal", "extended-art", "full-art", "alt-art"
    };

    public Card(String name, String rarity, String variant, double baseValue) {
        rarity = rarity.toLowerCase();
        variant = variant.toLowerCase();

        if (!isValidRarity(rarity)) {
            throw new IllegalArgumentException("Invalid rarity: " + rarity);
        }

        if (!isValidVariant(variant)) {
            throw new IllegalArgumentException("Invalid variant: " + variant);
        }

        if (baseValue <= 0) {
            throw new IllegalArgumentException("Base value must be a positive number.");
        }

        this.name = name;
        this.rarity = rarity;
        this.variant = validateVariant(rarity, variant);
        this.baseValue = baseValue;
        this.count = 1;
    }

    private String validateVariant(String rarity, String variant) {
        if (rarity.equals("common") || rarity.equals("uncommon")) {
            return "normal";
        }
        return variant;
    }

    public static boolean isValidRarity(String rarity) {
        for (String r : VALID_RARITIES) {
            if (r.equals(rarity)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidVariant(String variant) {
        for (String v : VALID_VARIANTS) {
            if (v.equals(variant)) {
                return true;
            }
        }
        return false;
    }

    public double getActualValue() {
        switch (variant) {
            case "extended-art":
                return baseValue * 1.5;
            case "full-art":
                return baseValue * 2.0;
            case "alt-art":
                return baseValue * 3.0;
            default:
                return baseValue;
        }
    }

    public void increaseCount() {
        count++;
    }

    public void decreaseCount() {
        if (count > 0) {
            count--;
        }
    }

    public void setCount(int newCount) {
        this.count = Math.max(0, newCount);
    }

    public int getCount() {
        return count;
    }

    public String getName() {
        return name;
    }

    public String getRarity() {
        return rarity;
    }

    public String getVariant() {
        return variant;
    }

    public double getBaseValue() {
        return baseValue;
    }

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

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    @Override
    public String toString() {
        return name + " [" + rarity + ", " + variant + "] | Base: $" + String.format("%.2f", baseValue)
            + " | Value: $" + String.format("%.2f", getActualValue()) + " | Count: " + count;
    }

    /**
     * Returns the real value of the card, as defined in MCO2 specs.
     * This is identical to getActualValue().
     *
     * @return real monetary value of the card.
     */
    public double getRealValue() {
        return getActualValue();
    }
}
