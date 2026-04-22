import processing.core.PApplet;
import processing.core.PFont;

public class App extends PApplet {

    CardGame cardGame = new ERS();
    int gameWidth = 1000;
    private int timer;

    public static void main(String[] args) {
        PApplet.main("App");
    }
    @Override
    public void settings() {
        size(gameWidth, 800);   
    }
    //let the suits symbols work and not just result in an X
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

    // Draw draw button if not ers (idk abt the other games but mine doesn't want a draw pile)
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
        text("Deck Size: " + cardGame.getDeckSize(), width / 2,
                height - 20);
        // show last 3 played cards and make corners readable

        int baseX = width / 2 - 40;
        int baseY = height / 2 - 60;

        int offsetX = 14; // shift right each layer
        int offsetY = 18; // shift down each layer

        int start = Math.max(0, cardGame.discardPile.size() - 3);
        for (int i = start; i < cardGame.discardPile.size(); i++) {
            Card c = cardGame.discardPile.get(i);
            int layer = i - start; // 0,1,2 (2 is top)

            c.setPosition(baseX + layer * offsetX, baseY + layer * offsetY, 80, 120);
            c.draw(this);
        }

        //don't do computer stuff if its ers
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
        //don't do draw button stuff if ers
        if (!(cardGame instanceof ERS)) {
    cardGame.handleDrawButtonClick(mouseX, mouseY);
    }
    cardGame.handleCardClick(mouseX, mouseY);
    }

    //let keys get pressed and do stuff in ers

    public void keyPressed() {
    // press r to restart game
    if (key == 'r' || key == 'R') {
        timer = 0;
        // restart the current game type
        if (cardGame instanceof ERS) {
            cardGame = new ERS();
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
    
}
