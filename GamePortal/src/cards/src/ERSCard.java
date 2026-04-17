public class ERSCard extends Card {

    public ERSCard(String value, String suit) {
        super(value, suit);
    }

    // check face cards
    public boolean isFaceCard() {
        return value.equals("Jack")
            || value.equals("Queen")
            || value.equals("King")
            || value.equals("Ace");
    }


    // chances for slaps for every face card
    public int getFaceValue() {
        switch (value) {
            case "Jack":  return 1;
            case "Queen": return 2;
            case "King":  return 3;
            case "Ace":   return 4;
            default:      return 0;
        }
    }

    // numeric rank will use when coding slaps
    
    public int getRank() {
        switch (value) {
            case "Ace":   return 14;
            case "King":  return 13;
            case "Queen": return 12;
            case "Jack":  return 11;
            default:
                return Integer.parseInt(value);
        }
    }
}