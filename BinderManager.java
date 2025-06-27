package com.CardSystem;

import java.util.ArrayList;
import java.util.Scanner;

public class BinderManager {
    private ArrayList<Binder> binders;
    private Scanner sc;

    public void start() {
        boolean running = true;

        while(running) {

            if(binders.isEmpty()){
                System.out.println("Select a name for a new binder");
                String binderName = sc.nextLine();

                binders.add(new Binder(binderName));
            } else {
                for (int i = 0; i < binders.size(); i++) {
                    System.out.println("----Manage Binders----");
                    System.out.println(binders.get(i).getName());
                }

                System.out.println("1. Create a New Binder");
                System.out.println("2. Select a Binder");

                int choice = sc.nextInt();
                sc.nextLine();

                if (choice == 1) {
                    System.out.println("Select a name for a new binder.");

                    sc.nextLine();
                    String newBinderName = sc.nextLine();

                    if (addBinder(newBinderName)) {
                        System.out.println("Binder created.");
                    } else
                        System.out.println("Binder creation failed.");
                } else if (choice == 2) {
                    System.out.println("Enter binder's name");

                    String binderName = sc.nextLine();

                    int index = searchBinder(binderName);
                    if (index != -1) {
                        binderMenu(binders.get(index));
                    } else {
                        System.out.println("Binder not found.");
                    }
                }
            }
        }
    }
    public BinderManager() {
        binders = new ArrayList<>();
        sc = new Scanner(System.in);
    }

    public boolean addBinder(String name) {
        if (searchBinder(name) == -1) {
            binders.add(new Binder(name));
            return true;
        }
        return false;
    }

    public void binderMenu(Binder binder) {
        System.out.println("----Manage Binder: " + binder.getName() + " ----");
        System.out.println("1. Add a Card");
        System.out.println("2. Remove a Card");
        System.out.println("3. Trade Card");
        System.out.println("4. View Binder");
        System.out.println("5. Delete Binder");

        int choice = sc.nextInt();

        switch (choice) {
            case 1:
                System.out.println("Enter Card's information");
                System.out.println("Name: ");
                String newCardName = sc.nextLine();
                System.out.println("Rarity: ");
                String newCardRarity = sc.nextLine();
                System.out.println("Variant: ");
                String newCardVariant = sc.nextLine();
                System.out.println("Base Value: ");
                double newCardBValue = sc.nextDouble();
                sc.nextLine();
                
                binder.addCard(new Card(newCardName, newCardRarity, newCardVariant, newCardBValue));
                break;
            case 2:
                boolean found = false;
                System.out.println("Enter the name of the card to be removed: ");
                String removedCardName = sc.nextLine();
                for(int i = 0; i < binder.getCards().size(); i++) {
                    if(removedCardName.equalsIgnoreCase(binder.getCards().get(i).getName())) {
                        Card returnCard = binder.returnCard(i);
                        found = true
                    } 
                }
                if(!found) {
                    System.out.println("Card " + removedCardName + " not found.");
                }
                break;
            case 3:
                System.out.println("Enter card to be traded: ");
                String outgoingCardName = sc.nextLine();
                System.out.println("Enter card to be traded for: ");
                String incomingCardName = sc.nextLine();

                Card outgoing = binder.findCardByName(outgoingCardName);
                Card incoming = new Card(incomingCardName, .., .., ..);
                
                if (trade(outgoing, incoming)) {
                    System.out.println("Trade successful.");
                } else {
                    System.out.println("Trade cancelled.");
                }
                break;
            case 4: 
                binder.viewBinder();
                break;
            case 5:
                break;
        }
    }

   
    public boolean trade(Card outgoingCard, Card incomingCard) {
        if(outgoingCard.getActualValue() - incomingCard.getActualValue() >= 1.0) {
            System.out.println("The difference in value is greater than 1.00$.");
            System.out.println("Continue with trade? [Yes/No]");
            String choice = sc.nextLine();

            if(choice.equalsIgnoreCase("Yes")) {
                return true;      // returns true to indicate the trade was successful and the card will remain in collection.
            } else if(choice.equalsIgnoreCase("No")) {
                return false;     // returns false; this will trigger removing the card
            }
        }
        return true;
    }


    public int searchBinder(String binderName) {
        for(int i = 0; i < binders.size(); i++) {
            if(binders.get(i).getName().equalsIgnoreCase(binderName)) {
                return i;
            }
        }
        return -1;
    }

}
