package ch.epfl.lia.parser;

import java.util.List;

import ch.epfl.lia.nlp.Dependency;
import ch.epfl.lia.util.LanguageDependent;

/**
 * @author Cyriaque Brousse
 */
public interface DependencyExtractor extends LanguageDependent {
    /**
     * Extracts the dependencies
     * 
     * @return the list of extracted dependencies
     * @throws DependencyExtractionException
     */
    List<Dependency> extract() throws DependencyExtractionException;
}
