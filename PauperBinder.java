public class PauperBinder extends Binder {
    public PauperBinder(String name) {
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
        String rarity = card.getRarity().toLowerCase();
        return rarity.equals("common") || rarity.equals("uncommon");
    }

    @Override
    public String getRestrictionMessage(Card card) {
        return "Only common or uncommon cards are allowed in a Pauper Binder.";
    }

    @Override
    public double calculateSellPrice() {
        return cards.stream().mapToDouble(Card::getActualValue).sum(); // No handling fee
    }
}
