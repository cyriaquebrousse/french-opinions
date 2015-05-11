package ch.epfl.lia.entity;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import ch.epfl.lia.util.Preconditions;

/**
 * @author Cyriaque Brousse
 */
public class ParsedArticle implements Entity {

    private static final long serialVersionUID = 1L;
    
    private final int id;
    private final List<ParsedSentence> parsedSentences;
    private final List<String> nouns;
    
    public ParsedArticle(int id, List<ParsedSentence> parsedSentences, List<String> nouns) {
        Preconditions.throwIfNullOrEmpty("Cannot construct a parsed article with no sentences", parsedSentences);
        Preconditions.throwIfNullOrEmpty("Cannot construct a parsed article with no nouns", nouns);
        if (id < 0) {
            throw new IllegalArgumentException("article id must be positive");
        }
        
        this.id = id;
        this.parsedSentences = new ArrayList<>(parsedSentences);
        this.nouns = new ArrayList<>(nouns);
    }
    
    public int id() {
        return id;
    }
    
    public List<ParsedSentence> parsedSentences() {
        return new ArrayList<>(parsedSentences);
    }
    
    public List<String> nouns() {
        return new ArrayList<>(nouns);
    }
    
    /**
     * Writes all the nouns from the article (one per line), to the specified
     * file
     * 
     * @param fileName
     *            file to write to
     * @throws IOException
     */
    public void writeNouns(String fileName) throws IOException {
        PrintWriter writer = new PrintWriter(fileName);
        for (String noun : nouns) {
            writer.write(noun + "\n");
        }
        writer.close();
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ParsedArticle other = (ParsedArticle) obj;
        if (id != other.id) {
            return false;
        }
        return true;
    }
    
    

}
