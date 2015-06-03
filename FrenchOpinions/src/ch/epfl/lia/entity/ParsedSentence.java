package ch.epfl.lia.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ch.epfl.lia.nlp.Dependency;
import ch.epfl.lia.nlp.Word;
import ch.epfl.lia.util.Preconditions;
import edu.stanford.nlp.trees.Tree;

/** Represents a parsed sentence, which is a list of words and a list of dependencies. This implementation
 * may also contain a parse tree, if it is provided.
 * @author Cyriaque Brousse
 */
public class ParsedSentence implements Serializable {

    private static final long serialVersionUID = 1L;

    /** List of words contained in the sentence. See what a {@link Word} is. */
    private final List<Word> words;
    /** List of dependencies contained in the sentence */
    private final List<Dependency> dependencies;
    /** Parse tree for this sentence. This element is nullable. */
    private final Tree parseTree;
    
    public ParsedSentence(List<Word> words, List<Dependency> dependencies, Tree parseTree) {
        Preconditions.throwIfNull("Cannot construct a parsed sentence with null argument",
                words, dependencies);
        Preconditions.throwIfNull("Please provide a parse tree for this parse sentence,"
                        + " or use the appropriate constructor", parseTree);
        
        this.words = new ArrayList<>(words);
        this.dependencies = new ArrayList<>(dependencies);
        this.parseTree = parseTree;
    }
    
    public ParsedSentence(List<Word> words, List<Dependency> dependencies) {
        Preconditions.throwIfNull("Cannot construct a parsed sentence with null argument",
                words, dependencies);
        
        this.words = new ArrayList<>(words);
        this.dependencies = new ArrayList<>(dependencies);
        this.parseTree = null;
    }
    
    public List<Word> words() {
        return new ArrayList<>(words);
    }

    public List<Dependency> dependencies() {
        return new ArrayList<>(dependencies);
    }
    
    /**
     * @return the parse tree if it exists, or an empty {@link Optional}
     */
    public Optional<Tree> parseTree() {
        if (parseTree == null) {
            return Optional.empty();
        } else {
            return Optional.of(parseTree);
        }
    }
}
