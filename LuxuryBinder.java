public class LuxuryBinder extends Binder {
    private double customPrice = -1;

    public LuxuryBinder(String name) {
        super(name);
    }

    @Override
    public boolean canBeSold() {
        return true;
    }

    @Override
    public boolean canTrade() {
        return false;
    }

    @Override
    public boolean isCardAllowed(Card card) {
        // Only allow cards with variant not "normal"
        return !card.getVariant().equalsIgnoreCase("normal");
    }

    @Override
    public String getRestrictionMessage(Card card) {
        return "Only cards with special variants (not 'normal') are allowed in a Luxury Binder.";
    }

    @Override
    public double calculateSellPrice() {
        double baseValue = cards.stream().mapToDouble(Card::getActualValue).sum();
        if (customPrice >= baseValue) {
            return customPrice * 1.10; // Add 10% handling fee
        } else {
            return baseValue * 1.10; // Fallback if no valid custom price set
        }
    }

    public boolean setCustomPrice(double price) {
        double baseValue = cards.stream().mapToDouble(Card::getActualValue).sum();
        if (price >= baseValue) {
            this.customPrice = price;
            return true;
        } else {
            System.out.println("Price is below total card value. Custom price not set.");
            return false;
        }
    }
}
