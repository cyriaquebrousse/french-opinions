package ch.epfl.lia.opinion.dictionary;

import java.io.Serializable;
import java.util.Optional;

/**
 * Models a sentiment dictionary, which is a collection of words mapped to a
 * polarity
 * 
 * @author Cyriaque Brousse
 */
public interface SentimentDictionary extends Serializable {

    /**
     * @param word
     *            the word whose polarity is being looked up
     * @return the polarity, if the word was found in the dictionary
     */
    Optional<Polarity> lookup(String word);
    
}
