package cards;
import Game.*;
import java.util.Collections;
import java.util.ArrayList;
import java.awt.event.KeyEvent;
import java.io.File;
//i had to delete almost all of the spaces to make it only 300 lines :(
//so now it's kinda chopped but whatever it works
public class ERS extends CardGame implements Game{
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
    private boolean challengerIsP1 = false; // who played the face card that started the current challenge
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
        // reset hands
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
        playerOneTurn = true; // p1 starts
    }
//slap stuff
private void attemptSlap(boolean slapperIsP1) {
    if (isValidSlap()) {
        // slap wins the whole pile
        collectPile(slapperIsP1);
        // slapper plays next
        playerOneTurn = slapperIsP1;

    } else {
        // bad slap: burn one card to the pile
        burnCard(slapperIsP1);
    }
}
private boolean isValidSlap() {
    int n = discardPile.size();
    if (n < 2) return false;
    Card top = discardPile.get(n - 1);
    Card second = discardPile.get(n - 2);
    // double
    if (sameValue(top, second)) return true;
    // sandwich
    if (n >= 3) {
        Card third = discardPile.get(n - 3);
        if (sameValue(top, third)) return true;
    }
    // staircase
    if (n >= 3 && isStaircaseTop3(true)) return true;
    // r staircase
    if (n >= 3 && isStaircaseTop3(false)) return true;
    return false;
}
private boolean isStaircaseTop3(boolean ascending) {
    int n = discardPile.size();
    Card a = discardPile.get(n - 3);
    Card b = discardPile.get(n - 2);
    Card c = discardPile.get(n - 1);
    int ra = rank(a);
    int rb = rank(b);
    int rc = rank(c);
    if (ra == -1 || rb == -1 || rc == -1) return false;
    if (ascending) {
        return (rb == ra + 1) && (rc == rb + 1);
    } else {
        return (rb == ra - 1) && (rc == rb - 1);
    }
}

private boolean sameValue(Card a, Card b) {
    return a != null && b != null && a.value.equals(b.value);
}
// map the cards in order -- 2-10, j, q,k, a
private int rank(Card c) {
    if (c == null) return -1;
    String v = c.value;
    switch (v) {
        case "Ace":   return 14;
        case "King":  return 13;
        case "Queen": return 12;
        case "Jack":  return 11;
        default:
            try {
                return Integer.parseInt(v);
            } catch (Exception e) {
                return -1;
            }
    }
}
// burn card for bad slap
private void burnCard(boolean slapperIsP1) {
    Hand h = slapperIsP1 ? playerOneHand : playerTwoHand;
    if (h.getSize() == 0) return;
    Card burn = h.getCard(h.getSize() - 1); // top of their stack
    h.removeCard(burn);
    burn.setTurned(false);
    discardPile.add(0, burn);

}
// pile goes to bottom of winner's stack
private void collectPile(boolean winnerIsP1) {
    Hand winner = winnerIsP1 ? playerOneHand : playerTwoHand;
    // turn pile face-down when collected
    for (Card c : discardPile) c.setTurned(true);
    // add to bottom of 
    winner.getCards().addAll(0, discardPile);

    discardPile.clear();
    lastPlayedCard = null;
    checkEndGame();
    restackBoth();
}
    // keyboard entry point
    public void handleKey(int keyCode) {
        // handle delayed pile award
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
    return; // ignore input while waiting
}
        if (!gameActive) return;
        switch (keyCode) {
            // P1
            case KeyEvent.VK_Q:
                if (playerOneTurn) playTopCard(true);
                break;
           case KeyEvent.VK_A: // P1 slap
                attemptSlap(true);
                break;
            // P2
            case KeyEvent.VK_P:
                if (!playerOneTurn) playTopCard(false);
                break;
            case KeyEvent.VK_L: // P2 slap
                attemptSlap(false);
                break;
        }
    }
    // plays the top card of the current player into the center pile
    private void playTopCard(boolean isP1) {
        Hand hand = isP1 ? playerOneHand : playerTwoHand;
        if (hand.getSize() == 0) return;
        Card c = hand.getCard(hand.getSize() - 1); // top of stack = last card
        hand.removeCard(c);
        c.setTurned(false);
        discardPile.add(c);
        lastPlayedCard = c;
        int face = faceValue(c);
        if (face > 0) {
            // face card starts / resets challenge
            challengeActive = true;
            challengeRemaining = face;
            challengerIsP1 = isP1;
            // now opponent must respond
            playerOneTurn = !isP1;
        } else {
            if (challengeActive) {
                // defender is burning chances (defender keeps playing)
                challengeRemaining--;
                if (challengeRemaining <= 0) {
            // defender failed but wait a little before awarding pile
                waitingForPileAward = true;
                 pileAwardTime = System.currentTimeMillis();
            } else {
                    // still in challenge so same player again
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
        if (c instanceof ERSCard) {
            return ((ERSCard) c).getFaceValue();
        }
        return 0;
    }
    private void restackBoth() {
        stackHand(playerOneHand, p1x, p1y);
        stackHand(playerTwoHand, p2x, p2y);
    }
    private void stackHand(Hand hand, int x, int y) {
        for (int i = 0; i < hand.getSize(); i++) {
            Card c = hand.getCard(i);
            c.setPosition(
                x + i * stackXOffset,
                y + i * stackYOffset,
                cardW,
                cardH
            );
        }
    }
// end of game here
    private boolean gameOver = false;
    private String winnerText = ""; 
    public boolean isGameOver() {
        return gameOver;
    }
    public String getWinnerText() {
    return winnerText;
}
// call this after things that change card states
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
    // only end if someone is out and the pile is empty
    if (!discardPile.isEmpty()) {
        if (playerOneTurn && p1 == 0) playerOneTurn = false;
        if (!playerOneTurn && p2 == 0) playerOneTurn = true;
        return;
    }
    // now pile empty + someone has no cards
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
    System.out.println(msg);
    }
@Override
public String getGameName() {
    return "ERS";
}
@Override
public void play() {
    createDeck();
    initializeGame();
}
@Override
public String getScore() {
    // TODO Auto-generated method stub
    return "Player 1: " + playerOneHand.getSize() + " | Player 2: " + playerTwoHand.getSize();
}
@Override
public void writeHighScore(File f) {
    // TODO Auto-generated method stub
    return ;
    // throw new UnsupportedOperationException("Unimplemented method 'writeHighScore'");
}
}