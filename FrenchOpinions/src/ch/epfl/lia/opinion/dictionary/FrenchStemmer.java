package ch.epfl.lia.opinion.dictionary;

import java.util.Optional;

import org.tartarus.snowball.ext.frenchStemmer;

import ch.epfl.lia.entity.Language;
import ch.epfl.lia.nlp.Word;

/**
 * Wraps a Snowball stemmer for French.<br>
 * Note: there exists a single instance for this class.
 * 
 * @author Cyriaque Brousse
 */
public final class FrenchStemmer implements Stemmer {

    private static final long serialVersionUID = 1L;
    
    private static FrenchStemmer instance = null;
    private final frenchStemmer stemmer;
    
    private FrenchStemmer() {
        this.stemmer = new frenchStemmer();
    }
    
    public static FrenchStemmer getInstance() {
        if (instance == null) {
            instance = new FrenchStemmer();
        }
        
        return instance;
    }

    @Override
    public Language getLanguage() {
        return Language.FRENCH;
    }

    @Override
    public synchronized Optional<String> stem(Word word) {
        stemmer.setCurrent(word.value());
        stemmer.stem();
        return Optional.of(stemmer.getCurrent());
    }

}
