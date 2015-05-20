package ch.epfl.lia.opinion.dictionary;

import static ch.epfl.lia.main.Config.SENTIMENT_DIC_LOCATION_FR;
import static ch.epfl.lia.util.FileUtils.foreachNonEmptyLine;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import ch.epfl.lia.entity.Language;
import ch.epfl.lia.nlp.Word;

/**
 * The dictionary for French, loaded from the 'lexicon_FR.txt' file. The file
 * has the following format:
 * <p>
 * <code>accessible  0  0  1<br> ... etc </code>
 * </p>
 * Note: there exists a single instance for this class, since the dictionary is
 * immutable.
 * 
 * @author Cyriaque Brousse
 */
public final class FrenchSentimentDictionary implements SentimentDictionary {

    private static final long serialVersionUID = 1L;
    
    private static FrenchSentimentDictionary instance = null;
    private final Map<String, Polarity> dictionary;
    
    private FrenchSentimentDictionary(String fileName) throws IOException {
        this.dictionary = new HashMap<>();
        
        foreachNonEmptyLine(fileName, line -> {
            final String[] entry = line.split("\\t");
            if (entry.length != 4) {
                System.err.println("malformed entry (" + line + ')');
                return;
            }
            
            final String word = entry[0];
            final Polarity polarity;
            
            if (Integer.parseInt(entry[1]) == 1) {
                polarity = Polarity.NEGATIVE;
            } else if (Integer.parseInt(entry[2]) == 1) {
                polarity = Polarity.NEUTRAL;
            } else if (Integer.parseInt(entry[3]) == 1) {
                polarity = Polarity.POSITIVE;
            } else {
                System.err.println("inconsistent entry (" + word + ')');
                return;
            }
            
            dictionary.put(word, polarity);
        });
    }
    
    /**
     * @return the unique instance of the dictionary
     */
    public static FrenchSentimentDictionary getInstance() {
        if (instance == null) {
            try {
                instance = new FrenchSentimentDictionary(SENTIMENT_DIC_LOCATION_FR);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return instance;
    }

    @Override
    public Optional<Polarity> lookup(Word word) {
        final Polarity polarity = dictionary.get(word.value());
        
        return polarity != null ? Optional.of(polarity) : Optional.empty();
    }

    @Override
    public Language getLanguage() {
        return Language.FRENCH;
    }
    
}
