package com.ironyard.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jasonskipper on 10/20/16.
 */
public class Player {
    private List<Card> cardsToPlayWith;
    private List<Card> wonCards;
    private String name;

    public Player(String name) {
        this.name = name;
        cardsToPlayWith = new ArrayList<>();
        wonCards = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void acceptCardForPlay(Card c){
        c.setOwner(this);
        cardsToPlayWith.add(c);

    }

    public void acceptWonCards(List<Card> hurraa){
        System.out.printf("\n %s won %s cards.", name,hurraa.size());
        wonCards.addAll(hurraa);
        for(Card c:wonCards){
            c.setOwner(this);
        }
    }

    public Card playCard() {
        Card cardToPlay = null;
        // get card from cardToPlayWith from top
        if (!cardsToPlayWith.isEmpty()){
            cardToPlay = cardsToPlayWith.remove(0);
        }

        // if no cards in carsToPlayWith
        if(cardToPlay == null && !wonCards.isEmpty()) {

                // shuffle won cards
                Game.shuffle(wonCards);

                // move them to cardsToPlayWin
                cardsToPlayWith.addAll(wonCards);
                wonCards.clear();

                // ask yourself fot a card
                // recursive call
                return this.playCard();

        }
        return cardToPlay;
    }

    public boolean has52Cards() {
        return cardsToPlayWith.size() + wonCards.size() == 52;
    }
}
