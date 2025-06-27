package com.CardSystem;

import java.util.Scanner;

public class TradeManager {
    private Scanner sc;

    public boolean trade(Card outgoingCard, Card incomingCard) {
        if(outgoingCard.getActualValue() - incomingCard.getActualValue() >= 1.0) {
            System.out.println("The difference in value is greater than 1.00$.");
            System.out.println("Continue with trade? [Yes/No]");
            String choice = sc.nextLine();
        }
    }

}
