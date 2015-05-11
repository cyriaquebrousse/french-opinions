package ch.epfl.lia.util;

import java.util.Collection;

/**
 * @author Cyriaque Brousse
 */
public final class Preconditions {
    
    private Preconditions() { }
    
    /**
     * Throws a {@link NullPointerException} with {@code message} if any of the
     * provided {@code objects} is null
     * 
     * @param message
     *            message to throw
     * @param objects
     *            objects to check
     */
    public static void throwIfNull(String message, Object... objects) {
        int index = 0;
        for (Object o : objects) {
            if (o == null) {
                throw new NullPointerException(message + "; Argument #" + index);
            }
            ++index;
        }
    }
    
    /**
     * Throws a {@link NullPointerException} with {@code message} if the
     * provided {@code collection} is null or empty
     * 
     * @param message
     *            message to throw
     * @param collection
     *            collection to check
     */
    public static <E> void throwIfNullOrEmpty(String message, Collection<E> collection) {
        if (collection == null || collection.isEmpty()) {
            throw new NullPointerException(message);
        }
    }
        
    /**
     * Throws an {@link IllegalArgumentException} with {@code message} if any of
     * the {@code strings} is empty
     * 
     * @param message
     *            message to throw
     * @param strings
     *            strings to check
     */
    public static void throwIfEmptyString(String message, String... strings) {
        for (String string : strings) {
            throwIfNull("Nothing to check", string);
            if (string.length() == 0) {
                throw new IllegalArgumentException(message);
            }
        }
    }
}
