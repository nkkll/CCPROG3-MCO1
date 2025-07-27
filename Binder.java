import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public abstract class Binder {
    protected String name;
    protected ArrayList<Card> cards;
    protected final int MAX_CARDS = 20;

    public Binder(String name) {
        this.name = name;
        this.cards = new ArrayList<>();
    }

    public abstract boolean canBeSold();
    public abstract boolean isCardAllowed(Card card);
    public abstract String getRestrictionMessage(Card card);
    public abstract double calculateSellPrice();
    public abstract boolean canTrade();

    public boolean addCard(Card newCard) {
        if (cards.size() >= MAX_CARDS) {
            System.out.println("Binder is full. Cannot add more cards.");
            return false;
        }
        if (!isCardAllowed(newCard)) {
            System.out.println(getRestrictionMessage(newCard));
            return false;
        }
        cards.add(newCard);
        return true;
    }

    public Card removeCardByName(String cardName) {
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).getName().equalsIgnoreCase(cardName)) {
                return cards.remove(i);
            }
        }
        return null;
    }

    public boolean removeCard(Card card) {
        return this.cards.remove(card);
    }

    public ArrayList<Card> returnAllCards() {
        ArrayList<Card> temp = new ArrayList<>(cards);
        cards.clear();
        return temp;
    }

    public ArrayList<Card> getCards() {
        return new ArrayList<>(cards);
    }

    public int getCardCount() {
        return cards.size();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void viewBinder() {
        System.out.println("---- Binder: " + name + " ----");
        ArrayList<Card> sorted = new ArrayList<>(cards);
        Collections.sort(sorted, Comparator.comparing(Card::getName, String.CASE_INSENSITIVE_ORDER));

        double totalValue = 0;
        for (int i = 0; i < sorted.size(); i++) {
            Card card = sorted.get(i);
            System.out.println("[" + (i + 1) + "] " + card.getName());
            System.out.println("Rarity:  " + card.getRarity());
            System.out.println("Variant: " + card.getVariant());
            System.out.println("Value:   $" + String.format("%.2f", card.getActualValue()));
            System.out.println();
            totalValue += card.getActualValue();
        }

        if (sorted.isEmpty()) {
            System.out.println("This binder is currently empty.");
        } else {
            System.out.println("Total binder value: $" + String.format("%.2f", totalValue));
        }
    }

    public static void openBinderMenu(ArrayList<Binder> binders, ArrayList<Card> collection, Collector collector, Scanner scanner) {
        boolean inBinders = true;

        while (inBinders) {
            System.out.println("\n--- Binders Menu ---");
            System.out.println("1. Create a binder");
            System.out.println("2. Manage a binder");
            System.out.println("3. Delete a binder");
            System.out.println("4. Trade a card");
            System.out.println("5. Sell a binder");
            System.out.println("6. Go back");
            System.out.print("Choose an option: ");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    createBinder(binders, scanner);
                    break;
                case "2":
                    manageBinder(binders, collection, scanner);
                    break;
                case "3":
                    deleteBinder(binders, collection, scanner);
                    break;
                case "4":
                    tradeCard(binders, collection, scanner);
                    break;
                case "5":
                    sellBinder(binders, collector, scanner);
                    break;
                case "6":
                    inBinders = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void createBinder(ArrayList<Binder> binders, Scanner scanner) {
        System.out.print("Enter binder name: ");
        String name = scanner.nextLine().trim();

        if (findBinder(binders, name) != null) {
            System.out.println("Binder with that name already exists.");
            return;
        }

        System.out.println("Choose binder type:");
        System.out.println("1. Non-Curated Binder");
        System.out.println("2. Pauper Binder");
        System.out.println("3. Rares Binder");
        System.out.println("4. Luxury Binder");
        System.out.println("5. Collector Binder");
        System.out.print("Enter option: ");
        String typeInput = scanner.nextLine().trim();

        Binder newBinder = null;
        switch (typeInput) {
            case "1": newBinder = new NonCuratedBinder(name); break;
            case "2": newBinder = new PauperBinder(name); break;
            case "3": newBinder = new RaresBinder(name); break;
            case "4": newBinder = new LuxuryBinder(name); break;
            case "5": newBinder = new CollectorBinder(name); break;
            default:
                System.out.println("Invalid binder type.");
                return;
        }

        binders.add(newBinder);
        System.out.println(newBinder.getClass().getSimpleName() + " created.");
    }

    private static void manageBinder(ArrayList<Binder> binders, ArrayList<Card> collection, Scanner scanner) {
        if (binders.isEmpty()) {
            System.out.println("No binders available.");
            return;
        }

        Binder binder = selectBinderFromList(binders, scanner);
        if (binder == null) return;

        boolean managing = true;
        while (managing) {
            System.out.println("\n--- Managing Binder: " + binder.getName() + " ---");
            System.out.println("1. Add card");
            System.out.println("2. Remove card");
            System.out.println("3. View binder");
            System.out.println("4. Go back");
            System.out.print("Choose an option: ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    ArrayList<Card> available = new ArrayList<>();
                    for (Card c : collection) {
                        if (c.getCount() > 0) available.add(c);
                    }

                    if (available.isEmpty()) {
                        System.out.println("No cards to add.");
                        break;
                    }

                    for (int i = 0; i < available.size(); i++) {
                        Card c = available.get(i);
                        System.out.println((i + 1) + ". " + c.getName() + " (Count: " + c.getCount() + ")");
                    }
                    System.out.print("Enter card number to add: ");
                    try {
                        int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
                        if (index >= 0 && index < available.size()) {
                            Card selected = available.get(index);
                            if (binder.addCard(selected)) {
                                selected.decreaseCount();
                                System.out.println("Card added.");
                            }
                        } else {
                            System.out.println("Invalid number.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input.");
                    }
                    break;

                case "2":
                    ArrayList<Card> cards = binder.getCards();
                    if (cards.isEmpty()) {
                        System.out.println("Binder is empty.");
                        break;
                    }

                    for (int i = 0; i < cards.size(); i++) {
                        System.out.println((i + 1) + ". " + cards.get(i).getName());
                    }
                    System.out.print("Enter number to remove: ");
                    try {
                        int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
                        if (index >= 0 && index < cards.size()) {
                            Card removed = cards.get(index);
                            binder.removeCard(removed);
                            Card existing = findCard(collection, removed.getName());
                            if (existing != null) existing.increaseCount();
                            else collection.add(removed);
                            System.out.println("Card returned to collection.");
                        } else {
                            System.out.println("Invalid number.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input.");
                    }
                    break;

                case "3":
                    binder.viewBinder();
                    break;

                case "4":
                    managing = false;
                    break;

                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void deleteBinder(ArrayList<Binder> binders, ArrayList<Card> collection, Scanner scanner) {
        Binder binder = selectBinderFromList(binders, scanner);
        if (binder == null) return;

        ArrayList<Card> returned = binder.returnAllCards();
        for (Card card : returned) {
            Card existing = findCard(collection, card.getName());
            if (existing != null) existing.increaseCount();
            else collection.add(card);
        }

        binders.remove(binder);
        System.out.println("Binder deleted and cards returned to collection.");
    }

    private static void tradeCard(ArrayList<Binder> binders, ArrayList<Card> collection, Scanner scanner) {
        ArrayList<Binder> tradeableBinders = new ArrayList<>();
        for (Binder b : binders) {
            if (b.canTrade()) {
                tradeableBinders.add(b);
            }
        }

        if (tradeableBinders.isEmpty()) {
            System.out.println("No binders support trading.");
            return;
        }

        System.out.println("Select a binder to trade from:");
        for (int i = 0; i < tradeableBinders.size(); i++) {
            System.out.println((i + 1) + ". " + tradeableBinders.get(i).getName());
        }

        System.out.print("Enter binder number: ");
        try {
            int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (index >= 0 && index < tradeableBinders.size()) {
                Binder selected = tradeableBinders.get(index);
                TradeManager.initiateTrade(selected, collection, scanner);
            } else {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    private static void sellBinder(ArrayList<Binder> binders, Collector collector, Scanner scanner) {
        Binder binder = selectBinderFromList(binders, scanner);
        if (binder == null) return;

        if (!binder.canBeSold()) {
            System.out.println("This binder cannot be sold.");
            return;
        }

        if (binder instanceof LuxuryBinder) {
            LuxuryBinder luxury = (LuxuryBinder) binder;
            System.out.print("Enter custom sell price (must be ≥ total value): ");
            try {
                double price = Double.parseDouble(scanner.nextLine().trim());
                boolean success = luxury.setCustomPrice(price);
                if (!success) {
                    System.out.println("Sale cancelled due to invalid custom price.");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid price. Sale cancelled.");
                return;
            }
        }

        double salePrice = binder.calculateSellPrice();
        collector.addMoney(salePrice);
        binders.remove(binder);
        System.out.println("Binder sold for $" + String.format("%.2f", salePrice));
    }

    private static Binder selectBinderFromList(ArrayList<Binder> binders, Scanner scanner) {
        if (binders.isEmpty()) {
            System.out.println("No binders available.");
            return null;
        }

        for (int i = 0; i < binders.size(); i++) {
            System.out.println((i + 1) + ". " + binders.get(i).getName());
        }

        System.out.print("Enter binder number: ");
        try {
            int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (index >= 0 && index < binders.size()) {
                return binders.get(index);
            } else {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
        return null;
    }

    private static Card findCard(ArrayList<Card> collection, String name) {
        for (Card c : collection) {
            if (c.getName().equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }

    private static Binder findBinder(ArrayList<Binder> binders, String name) {
        for (Binder b : binders) {
            if (b.getName().equalsIgnoreCase(name)) {
                return b;
            }
        }
        return null;
    }
}
