package ch.epfl.lia.opinion.dictionary;

import java.io.Serializable;
import java.util.Optional;

import ch.epfl.lia.nlp.Word;
import ch.epfl.lia.util.LanguageDependent;

/**
 * Models a sentiment dictionary, which is a collection of words mapped to a
 * polarity
 * 
 * @author Cyriaque Brousse
 */
public interface SentimentDictionary extends Serializable, LanguageDependent {

    /**
     * @param word
     *            the word whose polarity is being looked up
     * @return the polarity, if the word was found in the dictionary (as is,
     *         i.e. no stemming will be performed)
     */
    Optional<Polarity> lookup(Word word);
    
    /**
     * @param word
     *            the word whose polarity is being looked up
     * @return the polarity, if the word was found in the dictionary (as is, or
     *         a stemmed version of it)
     */
    Optional<Polarity> stemAndlookup(Word word);
    
}
