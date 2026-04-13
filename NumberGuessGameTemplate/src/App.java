import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

// Task 0: Make the NumberGuessGame work in Game.java
// Optional: 
// Task 1: Get getNumGuesses() to work correctly for each Game, and call getBestGame() here in App.java
// Task 2: Be forgiving to players - tell them they already guessed a number if they have, and do not add it to numGuesses.

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Welcome to the Number Guess Game!");
        // See Game for pseudocode you need to fill out
        // Create a new Game that sets a number within the user's specified number range
        ArrayList<Game> games = new ArrayList<>();
        HashMap<Integer, Integer> gameStats = new HashMap<>();

        Scanner sc = new Scanner(System.in);
        System.out.println("To play a game, press 'y'. If you want to stop playing, type anything other than 'y'.");
        while (sc.hasNext() && sc.next().equals("y")) {
            Game g = new Game(1, 50);
            g.play();
            games.add(g);
            System.out.println("press 'y' to play another game.");
            int key = g.getNumGuesses();
            if(gameStats.containsKey(key)){
                gameStats.put(key, gameStats.get(key) + 1);
            } else {
                gameStats.put(key, 1);
            }
        }
        System.out.println(gameStats);
    }

    public static void getBestGame(ArrayList<Game> games) {
        // best game
        int minGame = 0;
        for (Game g : games) {
            if (g.getNumGuesses() < minGame) {
                minGame = g.getNumGuesses();
            }
        }
        System.out.println("min game is: " + minGame);
    }
}
