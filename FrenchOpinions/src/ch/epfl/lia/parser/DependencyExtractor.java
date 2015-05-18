package ch.epfl.lia.parser;

import java.util.List;

import ch.epfl.lia.nlp.Dependency;

/**
 * @author Cyriaque Brousse
 */
public interface DependencyExtractor {
    /**
     * Extracts the dependencies
     * 
     * @return the list of extracted dependencies
     * @throws DependencyExtractionException
     */
    List<Dependency> extract() throws DependencyExtractionException;
}
