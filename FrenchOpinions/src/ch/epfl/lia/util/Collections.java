package ch.epfl.lia.util;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Cyriaque Brousse
 */
public final class Collections {
    
    private Collections() { }
    
    /**
     * @param all
     *            the set to exclude from
     * @param excl
     *            set of elements to exclude from {@code all}
     * @return {@code all \ excl}, which is all elements in {@code all} that are
     *         not in {@code excl}
     */
    public static <A> Set<A> exclude(Set<? extends A> all, Set<? super A> excl) {
        Set<A> result = new HashSet<>(all);
        result.removeAll(excl);
        return result;
    }

}
