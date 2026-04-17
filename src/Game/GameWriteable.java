package Game;
/*
 * gets best score from the File based on a csv.
 * CSV is written using "GameName, score" as a heading.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public interface GameWriteable extends Game {

    // compares the score of this game to current highscore.
    // returns true if this score is "higher" than the current highscore
    // the current high score might be NULL, so be careful.
    public boolean isHighScore(String score, String currentHighScore);

    public default void writeHighScore(File f) {
        // get the current score of this game, compare it with the current highScore
        // (getBestScore) using isHighScore
        // and if it is a high score, replace that line of the file with the new high
        // score.
        String score = getScore();
        String highScore = getBestScore(f);
        System.out.println("Thanks for playing! Your score was " + score);

        if (isHighScore(score, highScore)) {
            System.out.println("You got a new high score, which beats the previous high score of " + highScore);
            try {

                Scanner myReader = new Scanner(f);
                String newFile = "";
                while (myReader.hasNextLine()) {
                    // rewrite all lines except the line with the game name
                    String line = myReader.nextLine();
                    String[] data = line.split(",");
                    if (!data[0].equals(getGameName())) {
                        newFile += line + "\n";
                    }
                }
                // replace the Game's line with the new high score
                // puts it at the end of the file
                newFile += getGameName() + "," + score + "\n";
                // write the new file
                FileWriter myWriter = new FileWriter(f);
                myWriter.write(newFile);
                myWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // get the best score for a specific game, from a file. returns null if no score. 

    public default String getBestScore(File highscoreFile) {
        String highScore = null;
        try {
            Scanner myReader = new Scanner(highscoreFile);
            while (myReader.hasNextLine()) {
                String[] data = myReader.nextLine().split(",");
                if (data.length != 2 || !data[0].equals(getGameName())) {
                    // bad line or not this game, skip
                    continue;
                }
                highScore = data[1];
                break;
            }
        } catch (FileNotFoundException e) {
            try {
                highscoreFile.createNewFile();
            } catch (IOException ioException) {
                System.err
                        .println("Could not create file " + highscoreFile.getName() + " in " + highscoreFile.getPath());
            }
        }
        return highScore;
    }
}