package ch.epfl.lia.entity;

import java.sql.Timestamp;

import ch.epfl.lia.parser.LanguageParser;
import ch.epfl.lia.parser.ParseException;
import ch.epfl.lia.util.Preconditions;
import ch.epfl.lia.util.Serialization;

/**
 * @author Cyriaque Brousse
 */
public class Article implements Entity {

    private static final long serialVersionUID = 1L;
    
    private final int id;
    private final Language language;
    private final String location;
    private final String imageLocation;
    private final String title;
    private final String contents;
    private final Timestamp creationDate;
    private ParsedArticle parsed;
    
    public Article(int id, Language language, String location, String imageLocation,
            String title, String contents, Timestamp creationDate, ParsedArticle parsed) {
        Preconditions.throwIfEmptyString("article title and content must not be empty", title, contents);
        Preconditions.throwIfNull("article construction requires non-null values", language, creationDate);
        if (id < 0) throw new IllegalArgumentException("article id must be positive");
        if (parsed != null && parsed.id() != id)
            throw new IllegalArgumentException("parsed article id does not match");
        
        this.id = id;
        this.language = language;
        this.location = location;
        this.imageLocation = imageLocation;
        this.title = title;
        this.contents = contents;
        this.creationDate = new Timestamp(creationDate.getTime());
        this.parsed = parsed;
    }
    
    public int id() {
        return id;
    }
    
    public Language language() {
        return language;
    }
    
    public String location() {
        return location;
    }
    
    public String imageLocation() {
        return imageLocation;
    }
    
    public String title() {
        return title;
    }
    
    public String contents() {
        return contents;
    }
    
    public Timestamp creationDate() {
        return new Timestamp(creationDate.getTime());
    }
    
    public boolean isParsed() {
        return parsed != null;
    }
    
    /**
     * Returns the parsed article from the current article.<br>
     * The parsing is done with the provided parser, if any.<br>
     * Will NOT reparse the article if it has already been parsed.
     * 
     * @return the parsed article
     * @param parser
     *            the parser to parse the article with. If {@code null}, will
     *            automatically determine the appropriate parser for the
     *            language of the article
     * @throws ParseException
     */
    public ParsedArticle parse(LanguageParser parser) throws ParseException {
        if (!isParsed()) {
            parser = parser == null ? LanguageParser.getForLanguage(language) : parser;
            parsed = parser.parse(this);
        }
        return parsed;
    }
    
    /**
     * Shorthand for {@code parse(null)}.
     * 
     * @return the parsed article
     * @throws ParseException
     * @see {@link #parse(LanguageParser)}
     */
    public ParsedArticle parse() throws ParseException {
        LanguageParser parser = LanguageParser.getForLanguage(language);
        return parse(parser);
    }
    
    /**
     * Saves the current state of the article to disk
     * 
     * @param fileName
     *            path to save to
     */
    public void saveToDisk(String fileName) {
        Serialization.serialize(this, fileName);
    }

    @Override
    public int save() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void delete() {
        // TODO Auto-generated method stub
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
    }

}
