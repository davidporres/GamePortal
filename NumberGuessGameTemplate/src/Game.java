import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Game {
    private int guesses;
    private int numToGuess;
    private final int low;
    private final int high;

    private static final Scanner sc = new Scanner(System.in);
    private final ArrayList<Integer> guessesList = new ArrayList<>();
    private final Random rand = new Random();

    public Game(int low, int high) {
        // Fix swapped bounds
        if (low > high) {
            int temp = low;
            low = high;
            high = temp;
        }

        this.low = low;
        this.high = high;

        guesses = 0;

        // Random number in [low, high]
        numToGuess = rand.nextInt(high - low + 1) + low;

        System.out.println("I'm thinking of a number from " + low + " to " + high + ".");
    }

    public void play() {
        System.out.println("Make a guess!");
        int guess = getGuess();

        while (guess != numToGuess) {
            if (guess < numToGuess) {
                System.out.println("Higher!");
            } else {
                System.out.println("Lower!");
            }
            System.out.println("Make a guess!");
            guess = getGuess();
        }

        System.out.println("You got it! The number was " + numToGuess + ".");
        System.out.println("Guesses this game: " + guesses);
    }

    private int getGuess() {
        if (!sc.hasNextInt()) {
            System.out.println("Invalid input, please enter an integer.");
            sc.next(); // throw away bad token
            return getGuess();
        }

        int guess = sc.nextInt();

        // Optional: enforce range
        if (guess < low || guess > high) {
            System.out.println("Out of range! Enter a number from " + low + " to " + high + ".");
            return getGuess();
        }

        // Task 2: forgiving (don't count duplicates)
        if (guessesList.contains(guess)) {
            System.out.println("You already guessed that number! Try again.");
            return getGuess();
        }

        guessesList.add(guess);
        guesses++;
        return guess;
    }

    public int getNumGuesses() {
        return guesses;
    }
}
