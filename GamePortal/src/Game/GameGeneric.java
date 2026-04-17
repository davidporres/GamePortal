package Game;

// abstract classes are allowed to have fields, interfaces are not. 
// it's probably a good idea to not use interfaces over abstract classes 
// (just use the default method) unless you want to supply fields in a super class.  
public abstract class GameGeneric implements Game {
    // int score = 0; // example of a field in an abstract class

    public String getGameName() {
        return getClass().getSimpleName();
    }
}
