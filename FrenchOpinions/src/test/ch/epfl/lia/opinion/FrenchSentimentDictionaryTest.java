package ch.epfl.lia.opinion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Test;

import ch.epfl.lia.nlp.Word;
import ch.epfl.lia.opinion.dictionary.FrenchSentimentDictionary;
import ch.epfl.lia.opinion.dictionary.Polarity;

/**
 * @author Cyriaque Brousse
 */
public class FrenchSentimentDictionaryTest {

    @Test
    public void testLookupFirstPresent() {
        Optional<Polarity> pol = FrenchSentimentDictionary.getInstance().lookup(new Word("accessible", 1, "ADJ"));
        
        if (!pol.isPresent()) {
            fail("accessible (first word) should be present but wasn't");
        }
        
        assertEquals(Polarity.POSITIVE, pol.get());
    }
    
    @Test
    public void testLookupMultiple() {
        List<Word> words = Arrays.asList("accessible", "bavard", "obésité", "exploser",
                "redondant", "cajoler", "lenteur").stream().map(s -> new Word(s, 1, "N/A")).collect(Collectors.toList());
        
        words.forEach(w -> {
            Optional<Polarity> pol = FrenchSentimentDictionary.getInstance().lookup(w);
            if (!pol.isPresent()) {
                fail(w + " should be present but wasn't");
            }
        });
    }

}
