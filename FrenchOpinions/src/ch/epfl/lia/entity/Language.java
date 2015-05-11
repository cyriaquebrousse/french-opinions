package ch.epfl.lia.entity;

import java.util.Locale;

import ch.epfl.lia.util.Preconditions;

/**
 * Uniquely represents a language
 * 
 * @author Cyriaque Brousse
 */
public enum Language {

    ENGLISH("English", "en"),
    FRENCH("French", "fr");

    private final String fullName;
    private final String shortName;

    /**
     * @param fullName
     * @param shortName
     *            two-letter short name (i.e. {@code "en"} for English)
     */
    private Language(String fullName, String shortName) {
        Preconditions.throwIfEmptyString("language names may not be empty",
                fullName, shortName);

        this.fullName = fullName;
        this.shortName = shortName.toLowerCase(Locale.ENGLISH).substring(0, 2);
    }

    @Override
    public String toString() {
        return fullName;
    }

    /**
     * @return the short name for this language, in lowercase two-letter form
     *         (i.e. {@code "en"} for English)
     */
    public String shortName() {
        return shortName.substring(0, 2).toLowerCase(Locale.ENGLISH);
    }
}
