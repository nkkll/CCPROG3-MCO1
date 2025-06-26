package com.CardSystem;
import java.util.ArrayList;

/**
 * The Binder class manages a List with a maximum of 20 cards that will be used for trading.
 * This class handles creating the Binder, adding a Card to a Binder, returning and removing card/s
 * and displaying the Binder's content. it is implemented with a few helper functions.
 * Author: Anika Lumbania
 * Date created: June 23, 2025
 * Date last updated: June 25, 2025
 */

public class Binder {
    private String name;
    private ArrayList<Card> cards;
    private final int MAX_CARDS = 20;

    /**
     * Constructs a Binder with param name and
     * initializes cards.
     *
     * @param name
     */
    public Binder(String name){
        this.name = name;
        cards = new ArrayList<>();
    }

    /**
     * addCard method adds a new card passed as
     * the parameter to the binder
     *
     * @param newCard
     * @return true if card is added, false if not
     */
    public boolean addCard(Card newCard) {
        if(cards.size() < MAX_CARDS) {
            cards.add(newCard);
            return true;
        }
        return false;
    }

    /**
     * returnCard method returns a card matching the
     * name given in the parameter
     *
     * @param cardName
     * @return a Card if found, null if not
     */
    public Card returnCard(String cardName) {
        for(int i = 0; i < cards.size(); i++) {
            if(cards.get(i).getName().equalsIgnoreCase(cardName)) {
                return cards.remove(i);
            }
        }
        return null;
    }

    // returns the whole List to be added back to the collection probably thru looping addCard()

    /**
     * returnCards returns the whole List of cards in
     * a binder.
     *
     * @return the copy of the binder's contents
     */
    public ArrayList<Card> returnCards() {
        ArrayList<Card> temp = new ArrayList<>(cards);
        cards.clear();
        return temp;
    }

    /**
     * getCards copies the binder's list of cards to a
     * new List used for trading purposes
     *
     * @return a new List with the binder's contents
     */
    public ArrayList<Card> getCards() {
        return new ArrayList<>(cards);
    }

    /**
     * viewBinder displays the binder's cards and
     * its details alphabetically
     */
    public void viewBinder() {
        System.out.println("---- Binder: " + name + " ----");
        ArrayList<Card> sorted = new ArrayList<>(cards);
        sorted.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        for (Card card : sorted) {
            System.out.println("Name: " + card.getName());
            System.out.println("Rarity: " + card.getRarity());
            System.out.println("Variant: " + card.getVariant());
            System.out.println("Value: " + card.getActualValue());
            System.out.println();
        }
    }

    /**
     * getName is a getter for name
     *
     * @return
     */
    public String getName(){ return this.name; }

    /**
     * setName sets the name given a parameter name
     * 
     * @param name
     */
    public void setName(String name){ this.name = name; }

}
