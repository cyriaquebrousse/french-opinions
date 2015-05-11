package ch.epfl.lia.entity;

import ch.epfl.lia.opinion.dictionary.Polarity;
import ch.epfl.lia.util.Preconditions;

/**
 * Represents an opinion.<br>
 * It is characterized by two words:<br>
 * <p>
 * - a topic word ({@link #topicWord}), linked to a {@link #topic},<br>
 * - a polar word ({@link #polarWord}), along with its {@link #polarity}.
 * </p>
 * 
 * @author Cyriaque Brousse
 */
public class Opinion implements Entity {

    private static final long serialVersionUID = 1L;

    /** The topic this opinion is related to */
    private final Topic topic;

    /** The word corresponding to the {@link #topic} */
    private final String topicWord;
    private final int topicWordId;

    /** The word that is polar, i.e. contained in the sentiment dictionary */
    private final String polarWord;
    private final int polarWordId;

    /** The polarity of the opinion, i.e. the polarity of the polar word of the opinion */
    private final Polarity polarity;

    public Opinion(Topic topic, String topicWord, int topicWordId,
            String polarityWord, int polarityWordId, Polarity polarity) {
        Preconditions.throwIfEmptyString("polarity and topic words may not be empty", polarityWord, topicWord);
        Preconditions.throwIfNull("topic may not be null", topic);
        Preconditions.throwIfNull("polarity may not be null", polarity);
        if (polarityWordId < 0 || topicWordId < 0) {
            throw new IllegalArgumentException("word ids must be positive");
        }
        
        this.topic = topic;
        this.topicWord = topicWord;
        this.topicWordId = topicWordId;
        this.polarWord = polarityWord;
        this.polarWordId = polarityWordId;
        this.polarity = polarity;
    }
    
    public String topicWord() {
        return topicWord;
    }
    
    public int topicWordId() {
        return topicWordId;
    }

    public Topic topic() {
        return topic;
    }
    
    public String polarWord() {
        return polarWord;
    }

    public int polarWordId() {
        return polarWordId;
    }
    
    public Polarity polarity() {
        return polarity;
    }

    @Override
    public int save() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void delete() {
        // TODO Auto-generated method stub
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
    }
    
    @Override
    public String toString() {
        return polarity.toString() + '(' + topicWord + '-' + topicWordId + ','
                + polarWord + '-' + polarWordId + ')';
    }
    
    /**
     * <em>Note:</em><br>
     * The hashcode calculation will ignore the {@link #topic} contained in this
     * opinion, since the same opinion can be extracted with two distinct
     * topics.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((polarWord == null) ? 0 : polarWord.hashCode());
        result = prime * result + polarWordId;
        result = prime * result
                + ((polarity == null) ? 0 : polarity.hashCode());
        result = prime * result
                + ((topicWord == null) ? 0 : topicWord.hashCode());
        result = prime * result + topicWordId;
        return result;
    }

    /**
     * <em>Note:</em><br>
     * The equality check will ignore the {@link #topic} contained in this
     * opinion, since the same opinion can be extracted with two distinct
     * topics.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Opinion other = (Opinion) obj;
        if (polarWord == null) {
            if (other.polarWord != null) {
                return false;
            }
        } else if (!polarWord.equals(other.polarWord)) {
            return false;
        }
        if (polarWordId != other.polarWordId) {
            return false;
        }
        if (polarity != other.polarity) {
            return false;
        }
        if (topicWord == null) {
            if (other.topicWord != null) {
                return false;
            }
        } else if (!topicWord.equals(other.topicWord)) {
            return false;
        }
        if (topicWordId != other.topicWordId) {
            return false;
        }
        return true;
    }
    
}
