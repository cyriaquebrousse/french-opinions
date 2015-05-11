package ch.epfl.lia.util;

import static org.junit.Assert.*;

import org.junit.Test;

import ch.epfl.lia.util.Tuple;

/**
 * @author Cyriaque Brousse
 */
public class TupleTest {
    
    @Test
    public void testEqualsNull() {
        Tuple<String, String> t1 = new Tuple<>("bonjour", "quelqu'un");
        Tuple<String, String> t2 = null;
        
        assertFalse(t1.equals(t2));
    }

    @Test
    public void testEqualsCorrect() {
        Tuple<String, Boolean> t1 = new Tuple<>("bonjour", true);
        Tuple<String, Boolean> t2 = new Tuple<>("bonjour", true);
        
        assertEquals(t1, t2);
        assertEquals(t1, t1);
    }
    
    @Test
    public void testEqualsIncorrect() {
        Tuple<String, Boolean> t1 = new Tuple<>("bonjour", true);
        Tuple<String, Boolean> t2 = new Tuple<>("bonjour", false);
        Tuple<String, Boolean> t3 = new Tuple<>("bonsoir", true);
        
        assertFalse(t1.equals(t2));
        assertFalse(t1.equals(t3));
    }

}
