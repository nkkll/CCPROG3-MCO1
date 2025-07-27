public class RaresBinder extends Binder {
    public RaresBinder(String name) {
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
        return rarity.equals("rare") || rarity.equals("legendary");
    }

    @Override
    public String getRestrictionMessage(Card card) {
        return "Only rare or legendary cards are allowed in a Rares Binder.";
    }

    @Override
    public double calculateSellPrice() {
        double total = cards.stream().mapToDouble(Card::getActualValue).sum();
        return total * 1.10; // 10% handling fee
    }
}
