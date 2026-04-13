import java.util.Scanner;

public class Quiz {
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        // Create Categories
        Category baguette = new Category("Baguette",
                "You are steady, confident, and effortlessly put-together.\n"
              + "People rely on you because you actually get things done.\n"
              + "You like order, quality, and being the one calling the shots.");

        Category milkroll = new Category("Milk Roll",
                "You bring softness into a world that definitely needs it.\n"
              + "Warm, gentle, and genuinely kind, you make people feel calmer just by being around.\n"
              + "You're not trying to impress anyone — you just like comfort, closeness, and keeping life simple.");

        Category bagel = new Category("Bagel",
                "You've got real New Yorker energy — direct, quick-thinking, and always moving toward something.\n"
              + "You navigate life with a kind of effortless street smarts.\n"
              + "People appreciate how real you are: no fluff, no nonsense, just you.");

        Category focaccia = new Category("Focaccia",
                "You have a naturally creative, wandering spirit.\n"
              + "Calm but curious, peaceful but never boring.\n"
              + "You notice details others miss and drift toward anything you find interesting.");


        // Create Questions
        Question q1 = new Question("It's Saturday morning. What are you doing?");
        q1.possibleAnswers[0] = new Answer("Meeting some friends for brunch, what else?", bagel);
        q1.possibleAnswers[1] = new Answer("Getting ahead on some work for the weekend.", baguette);
        q1.possibleAnswers[2] = new Answer("Sleeping in and having a cozy breakfast at home, also probably watching tv or something.", milkroll);
        q1.possibleAnswers[3] = new Answer("Doing something fun and artsy in the city.", focaccia);

        Question q2 = new Question("Choose a drink.");
        q2.possibleAnswers[0] = new Answer("Tea", baguette);
        q2.possibleAnswers[1] = new Answer("Coffee", bagel);
        q2.possibleAnswers[2] = new Answer("Hot chocolate", milkroll);
        q2.possibleAnswers[3] = new Answer("Iced latte", focaccia);

        Question q3 = new Question("Pick a vibe for your ideal weekend hangout.");
        q3.possibleAnswers[0] = new Answer("A wild, crowded spot where everyone's talking at once.", bagel);
        q3.possibleAnswers[1] = new Answer("A cozy living room with blankets and a movie on.", milkroll);
        q3.possibleAnswers[2] = new Answer("An outdoor market or fair with lots of cool little stalls.", focaccia);
        q3.possibleAnswers[3] = new Answer("A quiet cafe with good pastries and better people-watching.", baguette);

        Question q4 = new Question("You just lost the championship of whatever sport/activity you do. What are you doing now?");
        q4.possibleAnswers[0] = new Answer("Congratulating the winner.", milkroll);
        q4.possibleAnswers[1] = new Answer("Analyzing your mistakes and noting how to improve.", baguette);
        q4.possibleAnswers[2] = new Answer("Packing up and going home.", bagel);
        q4.possibleAnswers[3] = new Answer("Celebrating that you got this far.", focaccia);

        Question q5 = new Question("Choose a vacation destination.");
        q5.possibleAnswers[0] = new Answer("A colorful Mediterranean town with art, music, and amazing food.", focaccia);
        q5.possibleAnswers[1] = new Answer("A historic European city with museums and bakeries on every corner.", baguette);
        q5.possibleAnswers[2] = new Answer("A cozy cabin in the mountains with hot drinks and board games.", milkroll);
        q5.possibleAnswers[3] = new Answer("A busy beach town with friends and boardwalk snacks.", bagel);

        Question q6 = new Question("How do you most often talk to your friends/colleagues online?");
        q6.possibleAnswers[0] = new Answer("Text", bagel);
        q6.possibleAnswers[1] = new Answer("FaceTime", milkroll);
        q6.possibleAnswers[2] = new Answer("Email", baguette);
        q6.possibleAnswers[3] = new Answer("Snapchat", focaccia);

        Question q7 = new Question("Pick a motto for how you live your life.");
        q7.possibleAnswers[0] = new Answer("If you want it done, do it yourself.", baguette);
        q7.possibleAnswers[1] = new Answer("Take it slow. Good things don't need to be rushed.", milkroll);
        q7.possibleAnswers[2] = new Answer("Move with purpose. Life never stops, so why would I?", bagel);
        q7.possibleAnswers[3] = new Answer("There's always something to find if you look hard enough.", focaccia);

        Question q8 = new Question("School just got canceled for the day. What do you do first?");
        q8.possibleAnswers[0] = new Answer("Hit up the group chat to make some plans.", bagel);
        q8.possibleAnswers[1] = new Answer("Use the time to catch up or get ahead on work.", baguette);
        q8.possibleAnswers[2] = new Answer("Go back to sleep, obviously.", milkroll);
        q8.possibleAnswers[3] = new Answer("Start a hobby project you've been thinking about.", focaccia);

        // Intro + play
        gameIntro();

        // Ask all questions
        Question[] qList = { q1, q2, q3, q4, q5, q6, q7, q8 };
        for (Question q : qList) {
            Category c = q.ask(sc);
            while (c == null) {
                System.out.println("Unidentifiable input. Please enter a number 1-4.");
                c = q.ask(sc);
            }
            c.points++;
        }

        Category[] cList = { baguette, milkroll, bagel, focaccia };

        int winnerIndex = getWinnerIndexWithTiebreak(cList, sc);

        System.out.println();
        System.out.println("If you were a bread, you would be " + cList[winnerIndex].label + "!");
        System.out.println(cList[winnerIndex].description);

        sc.close();
    }

    public static void gameIntro() {
        System.out.println("Which Bread Variant Are You?");
        System.out.println("You get to choose numbers 1-4 for every question.");
        System.out.println("Enter '1' to play!");

        while (true) {
            if (!sc.hasNextInt()) {
                System.out.println("Unidentifiable input. Please enter '1' to play.");
                sc.next(); // throw away bad input
                continue;
            }
            int play = sc.nextInt();
            if (play == 1) {
                System.out.println();
                return; // valid, continue
            } else {
                System.out.println("Unidentifiable input. Please enter '1' to play.");
            }
        }
    }
    
   //either says the winner or says there's a tie and does a tiebreaker
    public static int getWinnerIndexWithTiebreak(Category[] cats, Scanner sc) {
        // 1. find max points
        int max = 0;
        for (Category c : cats) {
            if (c.points > max) {
                max = c.points;
            }
        }
        // 2. count how many are tied for max
        int tieCount = 0;
        for (Category c : cats) {
            if (c.points == max) {
                tieCount++;
            }
        }
        // 3. if no tie, return the single max
        if (tieCount <= 1) {
            for (int i = 0; i < cats.length; i++) {
                if (cats[i].points == max) {
                    return i;
                }
            }
        }
        // 4. if tie: collect tied indices
        int[] tiedIndices = new int[tieCount];
        int idx = 0;
        for (int i = 0; i < cats.length; i++) {
            if (cats[i].points == max) {
                tiedIndices[idx] = i;
                idx++;
            }
        }
        // 5. qnnounce tie
        System.out.println();
        System.out.print("You're tied between: ");
        for (int i = 0; i < tiedIndices.length; i++) {
            System.out.print(cats[tiedIndices[i]].label);
            if (i < tiedIndices.length - 1) {
                System.out.print(", ");
            }
        }
        System.out.println(".");
        System.out.println("Time for a tiebreaker question!");

        // 6. tiebreaker question (only among tied categories)
        while (true) {
            System.out.println();
            System.out.println("TIEBREAKER: Which of these feels most like you right now?");
            for (int i = 0; i < tiedIndices.length; i++) {
                System.out.println("[" + (i + 1) + "]: " + cats[tiedIndices[i]].label);
            }
            System.out.println("Enter a number between 1 and " + tiedIndices.length + ":");

            if (!sc.hasNextInt()) {
                System.out.println("Unidentifiable input. Please enter a number between 1 and " + tiedIndices.length + ".");
                sc.next(); // discard if bad
                continue;
            }
            int choice = sc.nextInt();
            if (choice < 1 || choice > tiedIndices.length) {
                System.out.println("Unidentifiable input. Please enter a number between 1 and " + tiedIndices.length + ".");
                continue;
            }

            // valid
            return tiedIndices[choice - 1];
        }
    }
}