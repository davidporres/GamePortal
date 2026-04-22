import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import Game.Game;
import Game.ErrorCheck;
import NumberGuessGameTemplate.App;
import BuzzFeedQuiz.Quiz;
import cards.ERS;

public class GamePortal {
    static Scanner sc = new Scanner(System.in);
    static ArrayList<Game> games = new ArrayList<Game>();

    public static void main(String[] args) {
        HashMap<String, Integer> gameCounts = new HashMap<String, Integer>();
        File f = new File("Highscore.csv");

        while (true) {
            loadGames();

            System.out.println("\nWhich game would you like to play?");
            printGameChoices();
            Game g = getGameChoice();
            System.out.println("You're playing " + g.getGameName() + "!");

            g.play();
            g.writeHighScore(f);

            // track how many times each game has been played
            String gameKey = g.getGameName();
            gameCounts.put(gameKey, gameCounts.getOrDefault(gameKey, 0) + 1);

            System.out.println("\nReturning to the Game Portal...");
        }
    }

    public static void loadGames() {
        games.clear();
        games.add(new App()); // number guess game
        games.add(new Quiz()); // buzz quiz
        games.add(new ERS()); // ers
    }

    public static void printGameChoices() {
        int n = 1;
        for (Game g : games) {
            System.out.println("[" + (n++) + "]: " + g.getGameName());
        }
    }

    public static Game getGameChoice() {
        int choice = ErrorCheck.getInt(sc);
        while (choice < 1 || choice > games.size()) {
            System.out.println("Invalid choice. Please enter a number between 1 and " + games.size() + ".");
            choice = ErrorCheck.getInt(sc);
        }
        return games.get(choice - 1);
    }
}
