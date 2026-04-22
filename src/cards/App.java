package cards;
import processing.core.PApplet;
import processing.core.PFont;

public class App extends PApplet {

    CardGame cardGame; // set via setGame() when launched from portal, or defaulted in main()
    int gameWidth = 1000;
    private int timer;

    public static void main(String[] args) {
        App appSketch = new App();
        appSketch.cardGame = new ERS();
        PApplet.runSketch(new String[] { "cards.App" }, appSketch);
    }

    public void setGame(CardGame game) {
        this.cardGame = game;
    }

    @Override
    public void settings() {
        size(gameWidth, 800);
    }

    // let the suits symbols work and not just result in an X
    PFont font;

    @Override
    public void setup() {
        font = createFont("Arial Unicode MS", 32);
        textFont(font);
    }

    @Override
    public void draw() {
        background(255);
        // Draw player hands
        cardGame.playerOneHand.draw(this);
        // Draw computer hand
        cardGame.playerTwoHand.draw(this);
        // Stack counts (ERS / in general)
        fill(0);
        textSize(18);
        text("P1 Stack: " + cardGame.playerOneHand.getSize(), 80, 580);
        text("P2 Stack: " + cardGame.playerTwoHand.getSize(), 80, 60);

        // Draw draw button if not ERS
        if (!(cardGame instanceof ERS)) {
            cardGame.drawButton.draw(this);
        }

        // Display current player + restart button
        fill(0);
        textSize(24);
        textAlign(CENTER);
        text("Press 'R' to Restart", width / 2, 150);
        text("Current Player: " + cardGame.getCurrentPlayer(), width / 2, 100);
        textSize(16);
        // Display deck size
        text("Deck Size: " + cardGame.getDeckSize(), width / 2, height - 20);

        // show last 3 played cards and make corners readable
        int baseX = width / 2 - 40;
        int baseY = height / 2 - 60;
        int offsetX = 14;
        int offsetY = 18;

        int start = Math.max(0, cardGame.discardPile.size() - 3);
        for (int i = start; i < cardGame.discardPile.size(); i++) {
            Card c = cardGame.discardPile.get(i);
            int layer = i - start;
            c.setPosition(baseX + layer * offsetX, baseY + layer * offsetY, 80, 120);
            c.draw(this);
        }

        // show winner text if game is over
        if (cardGame instanceof ERS && ((ERS) cardGame).isGameOver()) {
            textSize(36);
            fill(0);
            text(((ERS) cardGame).getWinnerText(), width / 2, height / 2);
        }

        // don't do computer stuff if ERS
        if (cardGame.getCurrentPlayer() == "Player Two" && !(cardGame instanceof ERS)) {
            fill(0);
            textSize(16);
            text("Computer is thinking...", width / 2, height / 2 + 80);
            timer++;
            if (timer == 100) {
                cardGame.handleComputerTurn();
                timer = 0;
            }
        }

        cardGame.drawChoices(this);
    }

    @Override
    public void mousePressed() {
        // don't do draw button stuff if ERS
        if (!(cardGame instanceof ERS)) {
            cardGame.handleDrawButtonClick(mouseX, mouseY);
        }
        cardGame.handleCardClick(mouseX, mouseY);
    }

    @Override
    public void keyPressed() {
        // press R to restart
        if (key == 'r' || key == 'R') {
            timer = 0;
            if (cardGame instanceof ERS) {
                ERS newERS = new ERS();
                cardGame = newERS;
            } else if (cardGame instanceof Uno) {
                cardGame = new Uno();
            } else if (cardGame instanceof MonopolyDeal) {
                cardGame = new MonopolyDeal();
            }
            return;
        }
        if (cardGame instanceof ERS) {
            ((ERS) cardGame).handleKey(keyCode);
        }
    }

    @Override
    public void exitActual() {
        // prevents closing the Processing window from killing the whole portal
        if (cardGame instanceof ERS) {
            ((ERS) cardGame).countDownLatch();
        }
    }
}
