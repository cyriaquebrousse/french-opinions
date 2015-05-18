package ch.epfl.lia.entity;

import ch.epfl.lia.nlp.Word;
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
    private final Word topicWord;

    /** The word that is polar, i.e. contained in the sentiment dictionary */
    private final Word polarWord;

    /** The polarity of the opinion, i.e. the polarity of the polar word of the opinion */
    private final Polarity polarity;

    public Opinion(Topic topic, Word topicWord, Word polarityWord, Polarity polarity) {
        Preconditions.throwIfNull("polarity and topic words may not be null", polarityWord, topicWord);
        Preconditions.throwIfNull("topic may not be null", topic);
        Preconditions.throwIfNull("polarity may not be null", polarity);
        
        this.topic = topic;
        this.topicWord = topicWord;
        this.polarWord = polarityWord;
        this.polarity = polarity;
    }
    
    public Word topicWord() {
        return topicWord;
    }
    
    public Topic topic() {
        return topic;
    }
    
    public Word polarWord() {
        return polarWord;
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
        return polarity.toString() + '(' + topicWord + '-' + topicWord.id() + ','
                + polarWord + '-' + polarWord.id() + ')';
    }
    
    /**
     * <em>Note:</em><br>
     * The equality check will ignore the {@link #topic} contained in this
     * opinion, since the same opinion can be extracted with two distinct
     * topics.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((polarWord == null) ? 0 : polarWord.hashCode());
        result = prime * result
                + ((polarity == null) ? 0 : polarity.hashCode());
        result = prime * result
                + ((topicWord == null) ? 0 : topicWord.hashCode());
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
        return true;
    }
}
