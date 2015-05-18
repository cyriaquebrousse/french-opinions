package ch.epfl.lia.nlp;

import java.io.Serializable;

import ch.epfl.lia.util.Preconditions;

/**
 * Represents a word, which is essentially a string, an id (per sentence) and a
 * part of speech tag (grammatical nature).
 * 
 * @author Cyriaque Brousse
 */
public class Word implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private final String value;
    private final int id;
    private final String posTag;
    
    public Word(String value, int id, String posTag) {
        Preconditions.throwIfEmptyString("word value and POS tag may not be empty", value, posTag);
        if (id < 0) {
            throw new IllegalArgumentException("word id must be positive");
        }
        
        this.value = value;
        this.id = id;
        this.posTag = posTag;
    }
    
    public String value() {
        return value;
    }
    
    public int id() {
        return id;
    }
    
    public String posTag() {
        return posTag;
    }
    
    @Override
    public String toString() {
        return value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + ((posTag == null) ? 0 : posTag.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
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
        Word other = (Word) obj;
        if (id != other.id) {
            return false;
        }
        if (posTag == null) {
            if (other.posTag != null) {
                return false;
            }
        } else if (!posTag.equals(other.posTag)) {
            return false;
        }
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        return true;
    }
    
}
