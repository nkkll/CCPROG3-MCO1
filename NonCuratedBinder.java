public class NonCuratedBinder extends Binder {
    public NonCuratedBinder(String name) {
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
        return true; // All cards allowed
    }

    @Override
    public String getRestrictionMessage(Card card) {
        return "This binder allows all cards."; 
    }

    @Override
    public double calculateSellPrice() {
        return 0; // Not sellable
    }
}
