package NumberGuessGameTemplate;

import Game.*;
import java.io.File;
import java.util.Scanner;

public class App implements GameWriteable {

    private int lastScore = Integer.MAX_VALUE; // fewest guesses this session
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        App app = new App();
        app.play();
    }

    @Override
    public String getGameName() {
        return "Number Guess Game";
    }

    @Override
    public void play() {
        System.out.println("Welcome to the Number Guess Game!");
        lastScore = Integer.MAX_VALUE;

        System.out.println("Press 'y' to play. Anything else to stop.");
        while (sc.hasNext() && sc.next().equals("y")) {
            Gamed g = new Gamed(1, 50);
            g.play();

            int guesses = g.getNumGuesses();
            System.out.println("You finished in " + guesses + " guesses.");

            if (guesses < lastScore) {
                lastScore = guesses;
                System.out.println("New best score: " + lastScore + " guesses!");
            }

            System.out.println("Press 'y' to play again. Anything else to stop.");
        }

        System.out.println("Your best this session: " + lastScore + " guesses.");
    }

    @Override
    public String getScore() {
        if (lastScore == Integer.MAX_VALUE) {
            return "N/A";
        }
        return String.valueOf(lastScore);
    }

    @Override
    public boolean isHighScore(String score, String currentHighScore) {
        if (currentHighScore == null) return true;
        try {
            return Integer.parseInt(score) < Integer.parseInt(currentHighScore);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
