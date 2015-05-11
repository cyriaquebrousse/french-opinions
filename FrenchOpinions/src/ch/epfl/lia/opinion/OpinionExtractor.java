package ch.epfl.lia.opinion;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ch.epfl.lia.entity.Language;
import ch.epfl.lia.entity.Opinion;
import ch.epfl.lia.entity.ParsedArticle;
import ch.epfl.lia.entity.Topic;

/**
 * @author Cyriaque Brousse
 */
public abstract class OpinionExtractor {
    
    private static final Map<Language, OpinionExtractor> LANG_EXTRACTOR_MAP = initLangToExtractorMap();

    /**
     * Extracts opinions from the given article and previously extracted topics
     * 
     * @param article
     *            parsed article to work on
     * @param topics
     *            non-empty collection of topics to base the extraction on
     * @return the set of extracted opinions
     */
    public abstract Set<Opinion> extractOpinions(ParsedArticle article,
            Collection<Topic> topics) throws OpinionExtractionException;
    
    /**
     * @param language
     *            the language to get an extractor for
     * @return the extractor for this language
     * @throws UnsupportedOperationException
     *             if the language is not supported
     */
    public static OpinionExtractor getForLanguage(Language language) {
        OpinionExtractor extractor = LANG_EXTRACTOR_MAP.get(language);
        
        if (extractor == null) {
            throw new UnsupportedOperationException("no extractor for language " + language);
        }
        
        return extractor;
    }
    
    private static Map<Language, OpinionExtractor> initLangToExtractorMap() {
        Map<Language, OpinionExtractor> map = new HashMap<>();
        
        map.put(Language.FRENCH, new FrenchOpinionExtractor());
        
        return map;
    }
    
}
