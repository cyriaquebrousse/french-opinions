package ch.epfl.lia.parser;
/**
 * @author Cyriaque Brousse
 */
public class DependencyExtractionException extends Exception {
    
    private static final long serialVersionUID = 1L;

    public DependencyExtractionException() {
        super();
    }

    public DependencyExtractionException(String msg) {
        super(msg);
    }

    public DependencyExtractionException(Throwable t) {
        super(t);
    }
    
    public DependencyExtractionException(String msg, Throwable t) {
        super(msg, t);
    }

}
