package ch.epfl.lia.opinion;

/**
 * @author Cyriaque Brousse
 */
public class OpinionExtractionException extends Exception {
    
    private static final long serialVersionUID = 1L;

    public OpinionExtractionException() {
        super();
    }

    public OpinionExtractionException(String msg) {
        super(msg);
    }

    public OpinionExtractionException(Throwable t) {
        super(t);
    }
    
    public OpinionExtractionException(String msg, Throwable t) {
        super(msg, t);
    }
    
}
