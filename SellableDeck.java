public class SellableDeck extends Deck {

    public SellableDeck(String name) {
        super(name);
    }

    @Override
    public boolean canBeSold() {
        return true;
    }

    @Override
    public double calculateSellPrice() {
        double total = 0.0;
        for (Card card : cards) {
            total += card.getActualValue();
        }
        return total;
    }
}
