package ch.epfl.lia.parser;

/**
 * @author Cyriaque Brousse
 */
public class ParseException extends Exception {

    private static final long serialVersionUID = 1L;

    public ParseException() {
        super();
    }

    public ParseException(String msg) {
        super(msg);
    }

    public ParseException(Throwable t) {
        super(t);
    }
    
    public ParseException(String msg, Throwable t) {
        super(msg, t);
    }
}
