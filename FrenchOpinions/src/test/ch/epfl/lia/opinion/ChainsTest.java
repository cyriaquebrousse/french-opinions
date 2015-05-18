package ch.epfl.lia.opinion;

import static ch.epfl.lia.opinion.Chains.allChainsFromFirst;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import ch.epfl.lia.entity.Chain;
import ch.epfl.lia.nlp.Dependency;

/**
 * @author Cyriaque Brousse
 */
public class ChainsTest {
    
    private static final Dependency DEP_1 = new Dependency("mod", "obésité", 5, "NC", "hypertension", 9, "NC");
    private static final Dependency DEP_2 = new Dependency("mod", "hypertension", 9, "NC", "arthrose", 13, "NC");
    private static final Dependency DEP_3 = new Dependency("mod", "arthrose", 13, "NC", "mauvaise", 12, "ADJ");
    private static final Dependency DEP_4 = new Dependency("dep", "hypertension", 9, "NC", "morbide", 17, "ADJ");

    @Test
    public void testGetTwoDepsChain() {
        Set<Chain> actual = allChainsFromFirst(DEP_1, Arrays.asList(DEP_1, DEP_2, DEP_4));
        Set<Chain> expected = new HashSet<>(Arrays.asList(new Chain(DEP_1, DEP_2), new Chain(DEP_1, DEP_4)));
        
        assertEquals(expected, actual);
    }
    
    @Test
    public void testNoMoreThanTwoDeps() {
        Set<Chain> actual = allChainsFromFirst(DEP_1, Arrays.asList(DEP_1, DEP_2, DEP_3, DEP_4));
        Set<Chain> expected = new HashSet<>(Arrays.asList(new Chain(DEP_1, DEP_2), new Chain(DEP_1, DEP_4)));
        
        assertEquals(expected, actual);
    }

}
