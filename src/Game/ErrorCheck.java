package Game;

import java.util.Scanner;

public class ErrorCheck {

    public static int getInt(Scanner sc) {
        while (!sc.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            sc.next(); // discard bad token
        }
        return sc.nextInt();
    }
}
