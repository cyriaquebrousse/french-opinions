package ch.epfl.lia.opinion.dictionary;

import java.io.Serializable;
import java.util.Optional;

import ch.epfl.lia.nlp.Word;
import ch.epfl.lia.util.LanguageDependent;

/**
 * Models a stemmer, which is a component that takes a word as input, and output
 * the grammatical root of the word as output.<br>
 * For example, stemming "views" in English would possibly yield "view".<br>
 * The implementation is language-dependent.
 * 
 * @author Cyriaque Brousse
 */
public interface Stemmer extends Serializable, LanguageDependent {

    /**
     * @param word
     *            the word to stem
     * @return the stemmed version of the word, if any. In case of failure, an
     *         empty {@link Optional} is returned.
     */
    Optional<String> stem(Word word);
    
}
