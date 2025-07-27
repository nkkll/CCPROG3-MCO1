public class CollectorBinder extends Binder {
    public CollectorBinder(String name) {
        super(name);
    }

    @Override
    public boolean canBeSold() {
        return false;
    }

    @Override
    public boolean canTrade() {
        return true;
    }

    @Override
    public boolean isCardAllowed(Card card) {
        boolean validRarity = card.getRarity().equalsIgnoreCase("rare") || card.getRarity().equalsIgnoreCase("legendary");
        boolean validVariant = !card.getVariant().equalsIgnoreCase("normal");
        return validRarity && validVariant;
    }

    @Override
    public String getRestrictionMessage(Card card) {
        if (!card.getRarity().equalsIgnoreCase("rare") && !card.getRarity().equalsIgnoreCase("legendary")) {
            return "Only cards with 'Rare' or 'Legendary' rarity are allowed in a Collector Binder.";
        }
        if (card.getVariant().equalsIgnoreCase("normal")) {
            return "Only cards with special variants (not 'normal') are allowed in a Collector Binder.";
        }
        return "Card not allowed in Collector Binder.";
    }

    @Override
    public double calculateSellPrice() {
        return 0; // Not sellable
    }
}
