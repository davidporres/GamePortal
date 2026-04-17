package cards;
import processing.core.PImage;
import processing.core.PApplet;

public class Card extends ClickableRectangle {
    String value;
    String suit;
    PImage img;
    boolean turned = false;
    private int clickableWidth = 30; // Width of the left sliver that is clickable
    private boolean selected = false;
    private int baseY;
    private boolean hasBaseY = false;

    Card(String value, String suit) {
        this.value = value;
        this.suit = suit;
    }

    Card(String value, String suit, PImage img) {
        this.value = value;
        this.suit = suit;
        this.img = img;
    }

    public void setTurned(boolean turned) {
        this.turned = turned;
    }

    public void setClickableWidth(int width) {
        this.clickableWidth = width;
    }

    public void setSelected(boolean selected, int raiseAmount) {
        if (selected && !this.selected) {
            baseY = y;
            hasBaseY = true;
            y = baseY - raiseAmount;
        } else if (!selected && this.selected && hasBaseY) {
            y = baseY;
        }
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    @Override
    public boolean isClicked(int mouseX, int mouseY) {
        // Only the left sliver of the card is clickable
        return mouseX >= x && mouseX <= x + clickableWidth &&
                mouseY >= y && mouseY <= y + height;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setPosition(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public void drawFront(PApplet sketch) {
    if (img != null) {
        sketch.image(img, x, y, width, height);
        return;
    }

    sketch.push();

    // Card face
    sketch.stroke(0);
    sketch.strokeWeight(2);
    sketch.fill(255);
    sketch.rect(x, y, width, height, 10);

    // Suit symbol + color
    String suitSym = getSuitSymbol(suit);
    boolean red = "Hearts".equals(suit) || "Diamonds".equals(suit);

    // Corner text
    sketch.textAlign(PApplet.LEFT, PApplet.TOP);
    sketch.textSize(18);
    sketch.fill(red ? sketch.color(200, 0, 0) : sketch.color(0));

    String corner = getShortValue(value) + suitSym;
    sketch.text(corner, x + 8, y + 6);

    // Big center suit
    sketch.textAlign(PApplet.CENTER, PApplet.CENTER);
    sketch.textSize(56);
    sketch.text(suitSym, x + width / 2, y + height / 2 + 6);

    // Bottom-right corner (mirrors top-left)
    sketch.textAlign(PApplet.RIGHT, PApplet.BOTTOM);
    sketch.textSize(18);
    sketch.text(corner, x + width - 8, y + height - 6);

    sketch.pop();
}

private String getSuitSymbol(String suit) {
    switch (suit) {
        case "Hearts":   return "♥";
        case "Diamonds": return "♦";
        case "Clubs":    return "♣";
        case "Spades":   return "♠";
        default:         return "?";
    }
}

private String getShortValue(String v) {
    switch (v) {
        case "Jack":  return "J";
        case "Queen": return "Q";
        case "King":  return "K";
        case "Ace":   return "A";
        default:      return v; // "2".."10"
    }
}
    

    public void draw(PApplet sketch) {
        if (turned) {
            sketch.fill(150);
            sketch.rect(x, y, width, height);
            return;
        }
        if (isSelected()) {
            sketch.stroke(0);
            sketch.strokeWeight(4);
        } else {
            sketch.stroke(0);
        }
        drawFront(sketch);
        sketch.strokeWeight(1);
    }
}
