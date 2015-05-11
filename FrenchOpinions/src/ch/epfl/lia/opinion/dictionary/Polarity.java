package ch.epfl.lia.opinion.dictionary;

/**
 * Represents the polarity of a word. A score is associated to each possibility.
 * 
 * @author Cyriaque Brousse
 */
public enum Polarity {
    
    POSITIVE(1), NEUTRAL(0), NEGATIVE(-1);
    
    private final int score;
    
    private Polarity(int score) {
        this.score = score;
    }
    
    public int score() {
        return score;
    }
    
}
