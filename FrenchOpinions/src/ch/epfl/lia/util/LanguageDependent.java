package ch.epfl.lia.util;

import ch.epfl.lia.entity.Language;

/**
 * Represents a language-dependent abstraction. Used to discriminate, for example,
 * between a French and an English parser.
 * 
 * @author Cyriaque Brousse
 */
public interface LanguageDependent {
    
    /**
     * @return the language in which the process operates
     */
    Language getLanguage();
    
}
