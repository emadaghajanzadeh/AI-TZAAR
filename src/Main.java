import models.*;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

public class Main {
    public static ArrayList branchfactor = new ArrayList<>()  ;

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
            System.out.println("Game Started");
            RandomPlayer whitePlayer1 = new RandomPlayer(PlayerType.white);
            Ai blackPlayer1 = new Ai(PlayerType.black);
            Game game1 = new Game(whitePlayer1, blackPlayer1);

            long startTime = System.currentTimeMillis();
            Player winner = game1.play(0, 0);
            long duration = System.currentTimeMillis() - startTime;
            System.out.println(winner.getType());
            System.out.println("Spent time is: " + duration);
    }

}
