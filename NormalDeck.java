public class NormalDeck extends Deck {

    public NormalDeck(String name) {
        super(name);
    }

    @Override
    public boolean canBeSold() {
        return false;
    }

    @Override
    public double calculateSellPrice() {
        return 0.0;
    }
}
