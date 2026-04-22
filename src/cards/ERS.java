package cards;
import Game.*;
import java.util.Collections;
import java.util.ArrayList;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.concurrent.CountDownLatch;
import processing.core.PApplet;

public class ERS extends CardGame implements GameWriteable {
    private static final int cards_per_player = 26;
    // stack positions
    private static final int p1x = 80;
    private static final int p1y = 600;
    private static final int p2x = 80;
    private static final int p2y = 80;
    // delay before awarding pile so u can see the last card
    private boolean waitingForPileAward = false;
    private long pileAwardTime = 0;
    private static final int pile_delay = 800;
    private static final int cardW = 80;
    private static final int cardH = 120;
    // tiny offsets so the stack has thickness
    private static final int stackXOffset = 0;
    private static final int stackYOffset = 2;
    // face card challenges
    private boolean challengeActive = false;
    private int challengeRemaining = 0;
    private boolean challengerIsP1 = false;

    // Timer and latch for portal integration
    private long startTime = 0; // 0 means not started yet
    private long elapsedSeconds = 0;
    private CountDownLatch latch;

    @Override
    protected void createDeck() {
        deck.clear();
        String[] suits = { "Hearts", "Diamonds", "Clubs", "Spades" };
        String[] values = { "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace" };
        for (String suit : suits) {
            for (String value : values) {
                deck.add(new ERSCard(value, suit));
            }
        }
    }

    @Override
    protected void initializeGame() {
        super.initializeGame();
        discardPile.clear();
        lastPlayedCard = null;
        challengeActive = false;
        challengeRemaining = 0;
        challengerIsP1 = false;
    }

    @Override
    protected void dealCards(int ignored) {
        Collections.shuffle(deck);
        playerOneHand = new Hand();
        playerTwoHand = new Hand();
        for (int i = 0; i < cards_per_player; i++) {
            Card c1 = deck.remove(0);
            c1.setTurned(true);
            playerOneHand.addCard(c1);
            Card c2 = deck.remove(0);
            c2.setTurned(true);
            playerTwoHand.addCard(c2);
        }
        checkEndGame();
        restackBoth();
        playerOneTurn = true;
    }

    // slap stuff
    private void attemptSlap(boolean slapperIsP1) {
        if (isValidSlap()) {
            collectPile(slapperIsP1);
            playerOneTurn = slapperIsP1;
        } else {
            burnCard(slapperIsP1);
        }
    }

    private boolean isValidSlap() {
        int n = discardPile.size();
        if (n < 2) return false;
        Card top = discardPile.get(n - 1);
        Card second = discardPile.get(n - 2);
        if (sameValue(top, second)) return true;
        if (n >= 3) {
            Card third = discardPile.get(n - 3);
            if (sameValue(top, third)) return true;
        }
        if (n >= 3 && isStaircaseTop3(true)) return true;
        if (n >= 3 && isStaircaseTop3(false)) return true;
        return false;
    }

    private boolean isStaircaseTop3(boolean ascending) {
        int n = discardPile.size();
        Card a = discardPile.get(n - 3);
        Card b = discardPile.get(n - 2);
        Card c = discardPile.get(n - 1);
        int ra = rank(a), rb = rank(b), rc = rank(c);
        if (ra == -1 || rb == -1 || rc == -1) return false;
        if (ascending) return (rb == ra + 1) && (rc == rb + 1);
        else return (rb == ra - 1) && (rc == rb - 1);
    }

    private boolean sameValue(Card a, Card b) {
        return a != null && b != null && a.value.equals(b.value);
    }

    private int rank(Card c) {
        if (c == null) return -1;
        switch (c.value) {
            case "Ace":   return 14;
            case "King":  return 13;
            case "Queen": return 12;
            case "Jack":  return 11;
            default:
                try { return Integer.parseInt(c.value); }
                catch (Exception e) { return -1; }
        }
    }

    private void burnCard(boolean slapperIsP1) {
        Hand h = slapperIsP1 ? playerOneHand : playerTwoHand;
        if (h.getSize() == 0) return;
        Card burn = h.getCard(h.getSize() - 1);
        h.removeCard(burn);
        burn.setTurned(false);
        discardPile.add(0, burn);
    }

    private void collectPile(boolean winnerIsP1) {
        Hand winner = winnerIsP1 ? playerOneHand : playerTwoHand;
        for (Card c : discardPile) c.setTurned(true);
        winner.getCards().addAll(0, discardPile);
        discardPile.clear();
        lastPlayedCard = null;
        checkEndGame();
        restackBoth();
    }

    public void handleKey(int keyCode) {
        if (waitingForPileAward) {
            if (System.currentTimeMillis() - pileAwardTime > pile_delay) {
                awardPileToChallenger();
                challengeActive = false;
                challengeRemaining = 0;
                waitingForPileAward = false;
                playerOneTurn = challengerIsP1;
                checkEndGame();
                restackBoth();
            }
            return;
        }
        if (!gameActive) return;
        switch (keyCode) {
            case KeyEvent.VK_Q:
                if (playerOneTurn) playTopCard(true);
                break;
            case KeyEvent.VK_A:
                attemptSlap(true);
                break;
            case KeyEvent.VK_P:
                if (!playerOneTurn) playTopCard(false);
                break;
            case KeyEvent.VK_L:
                attemptSlap(false);
                break;
        }
    }

    private void playTopCard(boolean isP1) {
        // start timer on first card played
        if (startTime == 0) startTime = System.currentTimeMillis();

        Hand hand = isP1 ? playerOneHand : playerTwoHand;
        if (hand.getSize() == 0) return;
        Card c = hand.getCard(hand.getSize() - 1);
        hand.removeCard(c);
        c.setTurned(false);
        discardPile.add(c);
        lastPlayedCard = c;
        int face = faceValue(c);
        if (face > 0) {
            challengeActive = true;
            challengeRemaining = face;
            challengerIsP1 = isP1;
            playerOneTurn = !isP1;
        } else {
            if (challengeActive) {
                challengeRemaining--;
                if (challengeRemaining <= 0) {
                    waitingForPileAward = true;
                    pileAwardTime = System.currentTimeMillis();
                } else {
                    playerOneTurn = isP1;
                }
            } else {
                playerOneTurn = !isP1;
            }
        }
        checkEndGame();
        restackBoth();
    }

    private void awardPileToChallenger() {
        Hand winner = challengerIsP1 ? playerOneHand : playerTwoHand;
        ArrayList<Card> pileCopy = new ArrayList<>(discardPile);
        discardPile.clear();
        for (Card c : pileCopy) c.setTurned(true);
        winner.getCards().addAll(0, pileCopy);
        lastPlayedCard = null;
    }

    private int faceValue(Card c) {
        if (c instanceof ERSCard) return ((ERSCard) c).getFaceValue();
        return 0;
    }

    private void restackBoth() {
        stackHand(playerOneHand, p1x, p1y);
        stackHand(playerTwoHand, p2x, p2y);
    }

    private void stackHand(Hand hand, int x, int y) {
        for (int i = 0; i < hand.getSize(); i++) {
            Card c = hand.getCard(i);
            c.setPosition(x + i * stackXOffset, y + i * stackYOffset, cardW, cardH);
        }
    }

    // end of game
    private boolean gameOver = false;
    private String winnerText = "";

    public boolean isGameOver() { return gameOver; }
    public String getWinnerText() { return winnerText; }

    private void checkEndGame() {
        if (waitingForPileAward) return;
        if (gameOver) return;
        int p1 = playerOneHand.getSize();
        int p2 = playerTwoHand.getSize();
        if (challengeActive) {
            boolean defenderIsP1 = !challengerIsP1;
            if ((defenderIsP1 && p1 == 0) || (!defenderIsP1 && p2 == 0)) {
                challengeActive = false;
                challengeRemaining = 0;
                waitingForPileAward = false;
                awardPileToChallenger();
                playerOneTurn = challengerIsP1;
                restackBoth();
                checkEndGame();
                return;
            }
        }
        if (!discardPile.isEmpty()) {
            if (playerOneTurn && p1 == 0) playerOneTurn = false;
            if (!playerOneTurn && p2 == 0) playerOneTurn = true;
            return;
        }
        if (p1 == 0) {
            endGame("Player 2 wins!");
        } else if (p2 == 0) {
            endGame("Player 1 wins!");
        }
    }

    private void endGame(String msg) {
        gameOver = true;
        gameActive = false;
        winnerText = msg;
        elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000;
        System.out.println(msg);
        System.out.println("Time: " + elapsedSeconds + " seconds.");
        if (latch != null) latch.countDown();
    }

    @Override
    public String getGameName() {
        return "ERS";
    }

    @Override
    public void play() {
        createDeck();
        initializeGame();
        startTime = 0; // will be set on first card played
        elapsedSeconds = 0;
        latch = new CountDownLatch(1);

        App appSketch = new App();
        appSketch.setGame(this);
        PApplet.runSketch(new String[] { "ERS" }, appSketch);

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void countDownLatch() {
        if (latch != null) latch.countDown();
    }

    @Override
    public String getScore() {
        if (elapsedSeconds == 0) return "N/A";
        return String.valueOf(elapsedSeconds);
    }

    @Override
    public boolean isHighScore(String score, String currentHighScore) {
        if (currentHighScore == null) return true;
        try {
            return Long.parseLong(score) < Long.parseLong(currentHighScore);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
