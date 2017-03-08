package com.ironyard;

import com.ironyard.data.Game;
import com.ironyard.data.Player;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@SpringBootApplication
@ComponentScan(basePackages = "com.ironyard")
public class Application
{
    private static final int NUMBER_OF_GAMES = 10000;
    private int gameNumber;
    private int gamesEnded;


    private String doGame(Game aGame, int numOfPlayers)
    {
        int localGameNumber;
        synchronized (this) {
            gameNumber++;
            localGameNumber = gameNumber;
        }

        System.out.println("Starting game #" + localGameNumber);

        aGame.initGame(numOfPlayers);
        aGame.dealOutAllCards();

        Player winner = null;
        while (winner == null)
        {
            aGame.playRound();
            winner = aGame.getWinner();
        }

        synchronized (this) {
            gamesEnded++;
        }
        String declareWinner = "Winner of game#" + String.valueOf(localGameNumber) + " is: " + winner.getName() +
                "  (Games started = " + gameNumber + ", Games ended = " + gamesEnded + ").";
        System.out.println(declareWinner);
        return declareWinner;
    }


    private void doGameThreads_Method1() throws ExecutionException, InterruptedException
    {
        System.out.println("STARTING ALL GAMES\n-----------------------");

        ExecutorService executor = Executors.newWorkStealingPool();
        gameNumber = 0;
        gamesEnded = 0;

        List<Callable<String>> callables = new ArrayList<>();
        for (int i=0; i < NUMBER_OF_GAMES; i++)
        {
            callables.add(() -> doGame(new Game(), 5));
        }

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
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e)
        {
            System.err.println("doGameThreads_Method1: tasks interrupted " + e.getMessage());
        }
        finally
        {
            if (!executor.isTerminated()) {
                System.err.println("doGameThreads_Method1: cancel non-finished tasks to shutdown");
            }
            executor.shutdownNow();

            System.out.println("ALL GAMES ENDED!\n-----------------------");
        }
    }


    public static void main(String[] args) throws ExecutionException, InterruptedException {

        SpringApplication.run(Application.class, args);

        (new Application()).doGameThreads_Method1();

        //doGameThreads_Method2(5);
    }


    /*
    private void doGameThreads_Method2(Integer numberOfGames) throws ExecutionException, InterruptedException
    {
        Callable<Integer> task = () -> {
            try {
                TimeUnit.SECONDS.sleep(1);
                return 123;
            }
            catch (InterruptedException e) {
                throw new IllegalStateException("task interrupted", e);
            }
        };

        ExecutorService executor = Executors.newFixedThreadPool(numberOfGames);

        Future<Integer>[] futures = new Future[numberOfGames];

        List<Future> activeGames = new ArrayList<>();

        for (int i=0; i < numberOfGames; i++)
        {
            futures[i] = executor.submit(task);
            activeGames.add(futures[i]);
        }


        while (activeGames.size() > 0)
        {
            boolean gameDone = false;
            int i=0;

            for (i=0; i < activeGames.size(); i++)
            {
                if (futures[i].isDone())
                {
                    gameDone = true;
                    break;
                }
            }

            if (gameDone)
            {
                System.out.println(futures[i].get());
                activeGames.remove(i);
            }
        }

    }
     */

}