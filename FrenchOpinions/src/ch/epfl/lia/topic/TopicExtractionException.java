package ch.epfl.lia.topic;

/**
 * @author Cyriaque Brousse
 */
public class TopicExtractionException extends Exception {

    private static final long serialVersionUID = 1L;

    public TopicExtractionException() {
        super();
    }

    public TopicExtractionException(String msg) {
        super(msg);
    }

    public TopicExtractionException(Throwable t) {
        super(t);
    }

    public TopicExtractionException(String msg, Throwable t) {
        super(msg, t);
    }

}
