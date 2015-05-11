package ch.epfl.lia.util;

import static ch.epfl.lia.util.Collections.exclude;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Cyriaque Brousse
 */
public class CollectionsTest {
    
    private static final Set<Number> DUMMY_SET = new HashSet<>();
    
    @Before
    public void setUp() {
        DUMMY_SET.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7.5));
    }

    @Test
    public void testExclusionSimple() {
        Set<Number> actual = exclude(DUMMY_SET, new HashSet<>(Arrays.asList(1, 2, 3)));
        Set<Number> expected = new HashSet<>(Arrays.asList(4, 5, 6, 7.5));
        
        assertEquals(expected, actual);
    }
    
    @Test
    public void testExclusionSubtype() {
        Set<Number> actual = exclude(DUMMY_SET, new HashSet<>(Arrays.asList(7.5, 10.0f, 11L, 0xBB)));
        Set<Integer> expected = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6));
        
        assertEquals(expected, actual);
    }

}
