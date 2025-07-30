import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

public class ViewBinderGUI extends JDialog {
    public ViewBinderGUI(JFrame parent, Binder binder) {
        super(parent, "View Binder: " + binder.getName(), true);
        setSize(500, 400);
        setLayout(new BorderLayout());

        ArrayList<Card> sorted = new ArrayList<>(binder.getCards());
        sorted.sort(Comparator.comparing(Card::getName, String.CASE_INSENSITIVE_ORDER));

        DefaultListModel<String> model = new DefaultListModel<>();
        double totalValue = 0.0;
        for (Card card : sorted) {
            model.addElement(card.getName() + " | Rarity: " + card.getRarity()
                    + " | Variant: " + card.getVariant()
                    + " | Value: $" + String.format("%.2f", card.getActualValue()));
            totalValue += card.getActualValue();
        }

        JList<String> list = new JList<>(model);
        JScrollPane scroll = new JScrollPane(list);
        add(scroll, BorderLayout.CENTER);

        JLabel valueLabel = new JLabel("Total Binder Value: $" + String.format("%.2f", totalValue));
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(valueLabel, BorderLayout.SOUTH);

        setLocationRelativeTo(parent);
        setVisible(true);
    }
}
