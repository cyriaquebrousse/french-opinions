package ch.epfl.lia.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ch.epfl.lia.nlp.Dependency;
import ch.epfl.lia.util.Preconditions;
import ch.epfl.lia.util.Tuple;
import edu.stanford.nlp.trees.Tree;

/**
 * @author Cyriaque Brousse
 */
public class ParsedSentence implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<Tuple<String, String>> wordsAndTags;
    private final List<Dependency> dependencies;
    private final Tree parseTree;
    
    public ParsedSentence(List<Tuple<String, String>> wordsAndTags, List<Dependency> dependencies, Tree parseTree) {
        Preconditions.throwIfNull("Cannot construct a parsed sentence with null argument",
                wordsAndTags, dependencies, parseTree);
        
        this.wordsAndTags = new ArrayList<>(wordsAndTags);
        this.dependencies = new ArrayList<>(dependencies);
        this.parseTree = parseTree;
    }
    
    public List<Dependency> dependencies() {
        return new ArrayList<>(dependencies);
    }
    
    public Tree parseTree() {
        return parseTree;
    }

    public List<Tuple<String, String>> wordsAndTags() {
        return new ArrayList<>(wordsAndTags);
    }
}
