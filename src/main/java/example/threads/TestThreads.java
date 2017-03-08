package example.threads;

import com.ironyard.data.Game;

import java.util.concurrent.TimeUnit;

/**
 * Created by jasonskipper on 3/7/17.
 */
public class TestThreads {

    public static void main(String[] stupidQuestion){

        Runnable runnable = () -> {
            Game aGame = new Game();
            aGame.initGame(5);
            aGame.dealOutAllCards();
            while(aGame.getWinner() == null){
                aGame.playRound();
            }
            String name = Thread.currentThread().getName();
            System.out.println("Name of Thread: " + name + " -- Winner of Game: " + aGame.getWinner().getName());

        };

        for(int i = 0; i < 10000; i++) {
            Thread thread = new Thread(runnable);
            thread.start();
        }
    }

    private static Runnable getRunnableWithSleep() {
        return () -> {
                try {
                    String name = Thread.currentThread().getName();
                    System.out.println("Foo " + name);
                    TimeUnit.SECONDS.sleep(50);
                    System.out.println("Bar " + name);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };
    }
}
