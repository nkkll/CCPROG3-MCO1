import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Scanner;

public class BinderGUI extends JDialog {
    private final ArrayList<Binder> binders;
    private final Collector collector;
    private final Runnable onBinderChanged;

    public BinderGUI(JFrame parent, ArrayList<Binder> binders, Collector collector, Runnable onBinderChanged) {
        super(parent, "Manage Binders", true);
        this.binders = binders;
        this.collector = collector;
        this.onBinderChanged = onBinderChanged;

        setSize(500, 400);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(6, 1, 10, 10));

        addButton("Create Binder", () -> showCreateBinderDialog(this, binders, onBinderChanged));
        addButton("Manage Binder", this::manageBinder);
        addButton("Delete Binder", this::deleteBinder);
        addButton("Trade Card", this::tradeCard);
        addButton("Sell Binder", this::sellBinder);
        addButton("Close", this::dispose);
    }

    private void addButton(String label, Runnable action) {
        JButton button = new JButton(label);
        button.addActionListener(e -> action.run());
        add(button);
    }

    public static void showBinderMenu(JFrame parent, ArrayList<Binder> binders, Collector collector, Runnable onBinderChanged) {
        BinderGUI dialog = new BinderGUI(parent, binders, collector, onBinderChanged);
        dialog.setVisible(true);
    }

    public static void showCreateBinderDialog(Component parent, ArrayList<Binder> binders, Runnable onBinderChanged) {
        String name = JOptionPane.showInputDialog(parent, "Enter binder name:");
        if (name == null || name.trim().isEmpty()) return;

        for (Binder b : binders) {
            if (b.getName().equalsIgnoreCase(name)) {
                JOptionPane.showMessageDialog(parent, "Binder with that name already exists.");
                return;
            }
        }

        String[] types = {"Non-Curated Binder", "Pauper Binder", "Rares Binder", "Luxury Binder", "Collector Binder"};
        int choice = JOptionPane.showOptionDialog(parent, "Select binder type:", "Binder Type",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, types, types[0]);

        Binder newBinder = switch (choice) {
            case 0 -> new NonCuratedBinder(name);
            case 1 -> new PauperBinder(name);
            case 2 -> new RaresBinder(name);
            case 3 -> new LuxuryBinder(name);
            case 4 -> new CollectorBinder(name);
            default -> null;
        };

        if (newBinder != null) {
            binders.add(newBinder);
            JOptionPane.showMessageDialog(parent, newBinder.getClass().getSimpleName() + " created.");
            if (onBinderChanged != null) {
                onBinderChanged.run(); 
            }
        }
    }

    private Binder selectBinder() {
        if (binders.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No binders available.");
            return null;
        }

        String[] binderNames = binders.stream().map(Binder::getName).toArray(String[]::new);
        String selected = (String) JOptionPane.showInputDialog(this, "Select a binder:",
                "Select Binder", JOptionPane.PLAIN_MESSAGE, null, binderNames, binderNames[0]);

        if (selected == null) return null;

        return binders.stream().filter(b -> b.getName().equalsIgnoreCase(selected)).findFirst().orElse(null);
    }

    private void manageBinder() {
        Binder binder = selectBinder();
        if (binder == null) return;

        new ManageBinderGUI(this, binder, collector.getCollection().getCards());
    }

    private void deleteBinder() {
        Binder binder = selectBinder();
        if (binder == null) return;

        ArrayList<Card> returned = binder.returnAllCards();
        for (Card card : returned) {
            Card existing = collector.getCollection().findCardByName(card.getName());
            if (existing != null) existing.increaseCount();
            else collector.getCollection().getCards().add(card);
        }

        binders.remove(binder);
        JOptionPane.showMessageDialog(this, "Binder deleted and cards returned to collection.");
    }

    private void sellBinder() {
        Binder binder = selectBinder();
        if (binder == null) return;

        if (!binder.canBeSold()) {
            JOptionPane.showMessageDialog(this, "This binder cannot be sold.");
            return;
        }

        if (binder instanceof LuxuryBinder luxuryBinder) {
            double baseValue = luxuryBinder.getCards().stream().mapToDouble(Card::getActualValue).sum();
            String priceStr = JOptionPane.showInputDialog(this,
                    "Enter custom sell price (≥ $" + String.format("%.2f", baseValue) + "):");
            try {
                double customPrice = Double.parseDouble(priceStr);
                if (!luxuryBinder.setCustomPrice(customPrice)) {
                    JOptionPane.showMessageDialog(this, "Invalid custom price.");
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid input.");
                return;
            }
        }

        double price = binder.calculateSellPrice();
        collector.addMoney(price);
        binders.remove(binder);
        JOptionPane.showMessageDialog(this, "Binder sold for $" + String.format("%.2f", price));
    }

    private void tradeCard() {
        ArrayList<Binder> tradeables = new ArrayList<>();
        for (Binder b : binders) {
            if (b.canTrade()) tradeables.add(b);
        }

        if (tradeables.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No binders allow trading.");
            return;
        }

        String[] names = tradeables.stream().map(Binder::getName).toArray(String[]::new);
        String selected = (String) JOptionPane.showInputDialog(this, "Select a binder:",
                "Trade From", JOptionPane.PLAIN_MESSAGE, null, names, names[0]);

        if (selected == null) return;

        Binder chosen = tradeables.stream()
                .filter(b -> b.getName().equalsIgnoreCase(selected))
                .findFirst().orElse(null);

        if (chosen != null) {
            // Reuse console trade method temporarily
            TradeManager.initiateTrade(chosen, collector.getCollection().getCards(), new Scanner(System.in));
        }
    }
}
