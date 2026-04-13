import java.util.Scanner;

public class Question {
    // Fields
    String label;
    Answer[] possibleAnswers = new Answer[4];

    Question(String label) {
        this.label = label;
    }

    // ask a question, and return the category that corresponds to the answer
    Category ask(Scanner sc) {
        System.out.println(this.label);

        // print answer choices
        for (int i = 0; i < this.possibleAnswers.length; i++) {
            int choiceNumber = i + 1;
            System.out.println("[" + choiceNumber + "]: " + this.possibleAnswers[i].label);
        }

        // validate input
        if (!sc.hasNextInt()) {
            sc.next();
            return null;
        }

        int ans = sc.nextInt();
        if (ans < 1 || ans > 4) {
            return null; // out of range
        }
        return possibleAnswers[ans - 1].cat;
    }
}
