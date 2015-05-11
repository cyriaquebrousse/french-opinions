package ch.epfl.lia.opinion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import ch.epfl.lia.opinion.dictionary.FrenchSentimentDictionary;
import ch.epfl.lia.opinion.dictionary.Polarity;

/**
 * @author Cyriaque Brousse
 */
public class FrenchSentimentDictionaryTest {

    @Test
    public void testLookupFirstPresent() {
        Optional<Polarity> pol = FrenchSentimentDictionary.getInstance().lookup("accessible");
        
        if (!pol.isPresent()) {
            fail("accessible (first word) should be present but wasn't");
        }
        
        assertEquals(Polarity.POSITIVE, pol.get());
    }
    
    @Test
    public void testLookupMultiple() {
        List<String> words = Arrays.asList("accessible", "bavard", "obésité", "exploser",
                "redondant", "cajoler", "lenteur");
        
        words.forEach(w -> {
            Optional<Polarity> pol = FrenchSentimentDictionary.getInstance().lookup(w);
            if (!pol.isPresent()) {
                fail(w + " should be present but wasn't");
            }
        });
    }

}
