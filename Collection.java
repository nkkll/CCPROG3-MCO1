package com.CardSystem;
import java.util.*;

/**
 * The Collection class manages a List of cards, adding a card to the collection,
 * removing cards, and displaying the collection.
 * Author: Anika Lumbania
 * Date created: June 23, 2025
 * Date last updated: June 25, 2025
 */

public class Collection {
    private ArrayList<Card> cards;
    private Scanner sc;

    /**
     * Constructor for Collection that inititalizes
     * cards and Scanner sc
     */
    public Collection() {
        cards = new ArrayList<>();
        sc = new Scanner(System.in);
    }

    /**
     * A method for adding a card to the Collection.
     * This method calls searchCard, a helper function,
     * to check if a card is already existing. If yes, it notifies
     * the user and gives the option to increase its count instead.
     *
     * @param card
     */
    public void add(Card card) {
        if(searchCard(card.getName()) == null) {
            card.setCount(1);
            cards.add(card);
        } else {
            System.out.println("A similar card already exists in the collection.");
            System.out.println("Increase the existing card's count instead? [Yes/No]");

            String response = sc.nextLine();

            if(response.equals("Yes") || response.equals("yes")) {
                searchCard(card.getName()).increaseCount();
            }
        }
    }

    /**
     * the removeCard method removes a card given by the user.
     *
     * @param card
     */
    public void removeCard(Card card) {
        cards.remove(card);
    }

    // this returns a value to be passed to methods adding a card in a deck or binder

    /**
     * the isZero method checks if a card's count is 0. this serves
     * as a helper function for checking whether a card can be added
     * to a binder or deck.
     *
     * @param card
     * @return boolean
     */
    public boolean isZero(Card card) {
        return card != null && card.getCount() == 0;
    }

    /**
     * the isEmpty method checks whether a collection
     * is empty.
     *
     * @return boolean
     */
    public boolean isEmpty(){ return cards.isEmpty(); }

    /**
     * the searchCard method searches for a card in the
     * collection matching the parameter name.
     * @param name
     * @return Card
     */
    public Card searchCard(String name) {
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).getName().equalsIgnoreCase(name)) {
                return cards.get(i);
            }
        }
        return null;
    }

    /**
     * the viewCollection method allows users to see the cards and
     * their details, in a collection.
     */
    public void viewCollection() {
        System.out.println("----Card Collection----");
        for(int i = 0; i < cards.size(); i++) {
            System.out.println("Name: " + cards.get(i).getName());
            System.out.println("Rarity: " + cards.get(i).getRarity());
            System.out.println("Variant: " + cards.get(i).getVariant());
            System.out.println("Value: : " + cards.get(i).getValue());
            System.out.println("-");
        }
    }
}
