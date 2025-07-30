import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

public class ManageBinderGUI extends JDialog {

    public ManageBinderGUI(BinderGUI parent, Binder binder, ArrayList<Card> collection) {
        super(parent, "Manage Binder: " + binder.getName(), true);
        setSize(800, 500);
        setLayout(new BorderLayout());

        ArrayList<Card> sortedBinder = new ArrayList<>(binder.getCards());
        sortedBinder.sort(Comparator.comparing(Card::getName, String.CASE_INSENSITIVE_ORDER));

        DefaultListModel<Card> binderModel = new DefaultListModel<>();
        for (Card c : sortedBinder) binderModel.addElement(c);

        DefaultListModel<Card> collectionModel = new DefaultListModel<>();
        for (Card c : collection) {
            if (c.getCount() > 0) collectionModel.addElement(c);
        }

        JList<Card> binderList = new JList<>(binderModel);
        binderList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JList<Card> collectionList = new JList<>(collectionModel);
        collectionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton addButton = new JButton("Add →");
        JButton removeButton = new JButton("← Remove");
        JButton closeButton = new JButton("Close");

        addButton.addActionListener(e -> {
            Card selected = collectionList.getSelectedValue();
            if (selected != null && selected.getCount() > 0) {
                if (binder.addCard(selected)) {
                    selected.decreaseCount();
                    binderModel.addElement(selected);
                    collectionModel.removeElement(selected);
                    if (selected.getCount() > 0) collectionModel.addElement(selected);
                } else {
                    JOptionPane.showMessageDialog(this, binder.getRestrictionMessage(selected));
                }
            }
        });

        removeButton.addActionListener(e -> {
            Card selected = binderList.getSelectedValue();
            if (selected != null) {
                binder.removeCard(selected);
                Card existing = collection.stream()
                        .filter(c -> c.getName().equalsIgnoreCase(selected.getName()))
                        .findFirst().orElse(null);
                if (existing != null) existing.increaseCount();
                else {
                    selected.setCount(1);
                    collection.add(selected);
                }

                binderModel.removeElement(selected);
                collectionModel.removeAllElements();
                for (Card c : collection) {
                    if (c.getCount() > 0) collectionModel.addElement(c);
                }
            }
        });

        closeButton.addActionListener(e -> dispose());

        JPanel centerPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        centerPanel.add(new JScrollPane(collectionList));
        JPanel middleButtons = new JPanel(new GridLayout(2, 1, 5, 5));
        middleButtons.add(addButton);
        middleButtons.add(removeButton);
        centerPanel.add(middleButtons);
        centerPanel.add(new JScrollPane(binderList));

        add(centerPanel, BorderLayout.CENTER);
        add(closeButton, BorderLayout.SOUTH);

        setLocationRelativeTo(parent);
        setVisible(true);
    }
}
