package com.CardSystem;

import java.util.Scanner;

public class TradeManager {
    private Scanner sc;

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
}
