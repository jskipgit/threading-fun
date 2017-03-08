package example.threads;

import com.ironyard.data.Game;
import com.ironyard.data.Player;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * Created by osmanidris on 3/7/17.
 */
public class TestThreadsGame {
    public static void main(String[] args){
        Runnable task = () -> {
            String threadName = Thread.currentThread().getName();
            Game aGame = new Game();
            aGame.initGame(5);
            aGame.dealOutAllCards();
            Player winner = aGame.startGame();
            System.out.printf("\n%s: %s is the Winner! In %s turns\n",threadName, winner.getName(),aGame.getTurns());
        };
        for(int i = 0; i< 1000; i++) {
            Thread thread = new Thread(task);
            thread.start();
        }
    }

    synchronized void startGame(){
        Game aGame = new Game();
        aGame.initGame(5);
        aGame.dealOutAllCards();
        Player winner = aGame.startGame();
        System.out.printf("\n%s is the Winner! In %s turns\n", winner.getName(),aGame.getTurns());
    }
}


