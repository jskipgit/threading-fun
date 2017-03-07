package com.ironyard.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jasonskipper on 10/20/16.
 */
public class Game {
    private List<Player> players;
    private List<Card> cardsInPlay;
    private List<Card> startingDeck;
    int turns = 0;

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public List<Card> getCardsInPlay() {
        return cardsInPlay;
    }

    public void setCardsInPlay(List<Card> cardsInPlay) {
        this.cardsInPlay = cardsInPlay;
    }

    public List<Card> getStartingDeck() {
        return startingDeck;
    }

    public void setStartingDeck(List<Card> startingDeck) {
        this.startingDeck = startingDeck;
    }

    public int getTurns() {
        return turns;
    }

    public void setTurns(int turns) {
        this.turns = turns;
    }

    public static void main(String[] args){
        Game g = new Game();
        g.initGame(4);
        g.dealOutAllCards();
        Player winner = g.startGame();

        System.out.printf("\n\n%s is the Winner! In %s turns\n", winner.getName(),g.turns);
    }

    public void initGame(int numberOfPlayers){
        // set all lists to new empty lists
        turns = 0;
        players = new ArrayList<>();
        cardsInPlay = new ArrayList<>();
        startingDeck = new ArrayList<>();

        // create x players add to list of players
        for(int i=0; i<numberOfPlayers; i++){
            players.add(new Player("Player #"+i));
        }

        // init deck of cards
        startingDeck = Card.createDeck();

        // shuffle deck of cards
        shuffle(startingDeck);

    }

    public void dealOutAllCards(){
        // go through staringDeck
        int playerTurn = 0;
        for(Card c: startingDeck) {
            // give each player 1 card (repeat)
            int pos = playerTurn % players.size();
            players.get(pos).acceptCardForPlay(c);
            playerTurn++;
        }

    }
    public void playRound(){
            // each player puts down top card
            for(Player p:players){
                Card t = p.playCard();
                if(t !=null) {
                    cardsInPlay.add(t);
                }
            }
            turns++;

            // see who won
            Card highest = null;
            for(Card c:cardsInPlay){
                if(highest == null){
                    highest = c;
                }else if(highest.getValue() < c.getValue()){
                    highest = c;
                }
            }

            // winner gets all played cards
            highest.getOwner().acceptWonCards(cardsInPlay);
            cardsInPlay.clear();

    }
    public Player getWinner(){
        Player winner = null;
        for(Player p:players){
            if(p.has52Cards()){
                winner = p;
            }
        }
        return winner;
    }

    public Player startGame(){
        Player winner = null;
        while(winner == null){
            playRound();
            winner = getWinner();
        }
        return winner;
    }

    public static void shuffle(List<Card> shuffleMePlease){
        // pick random number
        Collections.shuffle(shuffleMePlease);
    }


}
