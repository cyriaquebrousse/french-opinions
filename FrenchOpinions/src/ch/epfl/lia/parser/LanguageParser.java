package ch.epfl.lia.parser;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.lia.entity.Article;
import ch.epfl.lia.entity.Language;
import ch.epfl.lia.entity.ParsedArticle;

/**
 * Grammatical parser abstraction.<br>
 * The underlying machinery must be able to produce the three following outputs:<br>
 * . part-of-speech tags,<br>
 * . parse trees,<br>
 * . grammatical dependencies between words.
 * 
 * @author Cyriaque Brousse
 */
public abstract class LanguageParser {
    
    private static final Map<Language, LanguageParser> LANG_PARSER_MAP = initLangToParserMap();
    
    /**
     * @return the language in which this parser operates
     */
    public abstract Language getLanguage();
    
    /**
     * Parses the provided Article into a ParsedArticle
     * 
     * @param article
     *            the article to parse
     * @return the article parsed with the language parser
     */
    public abstract ParsedArticle parse(Article article) throws ParseException;
    
    /**
     * @param language
     *            the language to get a parser for
     * @return the parser for this language
     * @throws UnsupportedOperationException
     *             if the language is not supported
     */
    public static LanguageParser getForLanguage(Language language) {
        LanguageParser parser = LANG_PARSER_MAP.get(language);

        if (parser == null) {
            throw new UnsupportedOperationException("no parser for language " + language);
        }

        return parser;
    }
    
    private static Map<Language, LanguageParser> initLangToParserMap() {
        Map<Language, LanguageParser> map = new HashMap<>();
        
        map.put(Language.FRENCH, new FrenchParser());
        
        return map;
    }
}
