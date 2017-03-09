package com.ironyard.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

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

    ExecutorService executor;

    /*
    public static void main(String[] args){
        Game g = new Game();
        g.initGame(4);
        g.dealOutAllCards();
        Player winner = g.startGame();

        System.out.printf("\n\n%s is the Winner! In %s turns\n", winner.getName(),g.turns);
    }
    */

    public void initGame(int numberOfPlayers){

        //executor = Executors.newFixedThreadPool(numberOfPlayers);
        //executor = Executors.newWorkStealingPool(numberOfPlayers);
        executor = Executors.newWorkStealingPool();

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

    ReentrantLock lock = new ReentrantLock();

    private void playCardTest(Player p)
    {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Void> future = executor.submit(new Callable<Void>() {
            public Void call() {
                Card t = p.playCard();
                if(t != null) {
                    lock.lock();
                    cardsInPlay.add(t);
                    lock.unlock();
                }
                return null;
            }
        });

        try
        {
            future.get();
        }
        catch (Exception ex)
        {
            System.out.println("Exception=" + ex.getMessage());
        }


        /*
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            Card t = p.playCard();
            if(t != null) {
                lock.lock();
                cardsInPlay.add(t);
                lock.unlock();
            }
        });
        */

        /*
        Runnable runnable = () -> {
            Card t = p.playCard();
            if(t != null) {
                lock.lock();
                cardsInPlay.add(t);
                lock.unlock();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        */
    }


    private Integer onePlayerPlayCard(Player p)
    {
        Card t = p.playCard();
        if(t !=null) {
            synchronized (this) {
                cardsInPlay.add(t);
            }

        }
        return 0;
    }


    private void playCardsOfOneTurn()
    {
        ExecutorService localExecutor = null;

        try
        {
            //localExecutor = Executors.newFixedThreadPool(players.size());
            localExecutor = Executors.newWorkStealingPool();

            List<Callable<Integer>> callables = new ArrayList<>();
            for(Player p:players)
            {
                callables.add(() -> onePlayerPlayCard(p));
            }

            /*
            List<Future<Integer>> futures = localExecutor.invokeAll(callables);
            for (Future f: futures)
            {
                f.get();
            }
            */

            executor.invokeAll(callables)
                    .stream()
                    .map(future -> {
                        try {
                            return future.get();
                        }
                        catch (Exception e) {
                            throw new IllegalStateException(e);
                        }
                    });
                    //.forEach(System.out::println);


            try
            {
                localExecutor.shutdown();
                localExecutor.awaitTermination(5, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException e)
            {
                System.err.println("playCardsOfOneTurn: tasks interrupted: " + e.getMessage());
            }
            finally
            {
                if (!localExecutor.isTerminated()) {
                    System.err.println("playCardsOfOneTurn: cancel non-finished tasks to shutdown");
                }
                localExecutor.shutdownNow();
            }
        }
        catch (Exception ex)
        {
            System.out.println("playCardsOfOneTurn: exception " + ex.getMessage());
        }
        finally {
            if (localExecutor != null)
            {
                //((ThreadPoolExecutor)localExecutor).purge();
            }
        }
    }




    private void Test2()
    {
        CompletionService<Integer> completionService = new ExecutorCompletionService<Integer>(executor);
        for(Player p:players)
        {
            completionService.submit(new Callable<Integer>() {
                public Integer call() {
                    Card t = p.playCard();
                    if(t !=null) {
                        //synchronized (this) {
                            cardsInPlay.add(t);
                        //}
                    }
                    return 0;
                }
            });
        }

        int done = 0;
        boolean exceptionOccurred = false;
        while (done < players.size() && !exceptionOccurred)
        {
            try
            {
                Future<Integer> future = completionService.take();
                Integer returnedValue = future.get();
                done++;
            }
            catch (Exception ex)
            {
                exceptionOccurred = true;
                System.out.println("playCardsOfOneTurn2: exception " + ex.getMessage());
            }
        }

        try
        {
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e)
        {
            System.err.println("playCardsOfOneTurn: tasks interrupted");
        }
        finally
        {
            if (!executor.isTerminated()) {
                System.err.println("playCardsOfOneTurn: cancel non-finished tasks to shutdown");
            }
            executor.shutdownNow();
        }
    }




    public void playRound(){
            // each player puts down top card

        playCardsOfOneTurn();
/*
            for(Player p:players){
                Card t = p.playCard();
                if(t !=null) {
                    cardsInPlay.add(t);
                }

            }
*/
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
