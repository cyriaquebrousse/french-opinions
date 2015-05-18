package ch.lia.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import ch.epfl.lia.entity.Chain;
import ch.epfl.lia.nlp.Dependency;

/**
 * @author Cyriaque Brousse
 */
public class ChainTest {
    
    private static final Dependency DEP_1 = new Dependency("mod", "obésité", 5, "NC", "hypertension", 9, "NC");
    private static final Dependency DEP_2 = new Dependency("mod", "hypertension", 9, "NC", "arthrose", 13, "NC");
    private static final Dependency DEP_3 = new Dependency("mod", "arthrose", 13, "NC", "mauvaise", 12, "ADJ");

    private static final Dependency DEP_BAD_1 = new Dependency("mod", "obésité",5,"NC","diabète",7,"NC");

    @Test
    public void testTwoGoodDeps() {
        try {
            new Chain(DEP_1, DEP_2);
        } catch (IllegalArgumentException e) {
            fail("should not throw");
        }
    }
    
    @Test
    public void testTwoDepsBadOrder() {
        try {
            new Chain(DEP_2, DEP_1);
            fail("should throw");
        } catch (IllegalArgumentException e) {
            /* good */
        }
    }
    
    @Test
    public void testThreeGoodDeps() {
        try {
            new Chain(DEP_1, DEP_2, DEP_3);
        } catch (IllegalArgumentException e) {
            fail("should not throw");
        }
    }
    
    @Test
    public void testBrokenChainConstructor() {
        try {
            new Chain(DEP_1, DEP_2, DEP_3, DEP_BAD_1);
            fail("should throw");
        } catch (IllegalArgumentException e) {
            /* good */
        }
    }
    
    @Test
    public void testBrokenChainAppend() {
        try {
            Chain c = new Chain(DEP_1, DEP_2, DEP_3);
            c.append(DEP_BAD_1);
            fail("should throw");
        } catch (IllegalArgumentException e) {
            /* good */
        }
    }
    
    @Test
    public void testAppendFailsOnNull() {
        try {
            Chain c = new Chain(DEP_1, DEP_2, DEP_3);
            c.append(null);
            fail("should throw");
        } catch (NullPointerException e) {
            /* good */
        } catch (Exception e) {
            fail("wrong type of exception");
        }
    }

    @Test
    public void testStringRepresentation() {
        Chain c = new Chain(DEP_1, DEP_2, DEP_3);
        String expected = "mod(obésité-5,hypertension-9)#mod(hypertension-9,arthrose-13)#mod(arthrose-13,mauvaise-12)";
        
        assertEquals(c.toString(), expected);
    }
    
    @Test
    public void testType() {
        Chain c = new Chain(DEP_1, DEP_2, DEP_3);
        String expected = "mod#mod#mod";
        
        assertEquals(c.type(), expected);
    }

}
