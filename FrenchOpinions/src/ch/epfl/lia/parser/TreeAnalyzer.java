package ch.epfl.lia.parser;
import static ch.epfl.lia.main.Config.POS_NOUNS_FR;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.lia.util.Tuple;
import edu.stanford.nlp.trees.Tree;

/**
 * @author Cyriaque Brousse
 */
public class TreeAnalyzer {
    
    private final Tree tree;
    private final List<Tree> leaves;
    
    private final List<Tree> nouns;
    private final List<Tuple<String, String>> wordsAndTags;
    
    /**
     * Construct a new analyzer
     * 
     * @param tree
     *            the parse tree to construct on
     */
    public TreeAnalyzer(final Tree tree) {
        this.tree = tree;
        this.leaves = objectArrayToTreeList();
        this.nouns = initNouns();
        this.wordsAndTags = initPosTags();
    }
    
    /**
     * @return the leaves contained in the underlying parse tree
     */
    public List<Tree> leaves() {
        return new ArrayList<>(leaves);
    }

    /**
     * @return the subset of leaves that are nouns
     * @see #leaves()
     */
    public List<Tree> nouns() {
        return new ArrayList<>(nouns);
    }
    
    /**
     * @return the list of words associated with their tag
     */
    public List<Tuple<String, String>> wordsAndTags() {
        return new ArrayList<>(wordsAndTags);
    }
    
    /**
     * @param word
     *            the string value of the word
     * @param id
     *            the word id in the sentence, where the word indices start at 0
     * @return the part of speech tag of the provided word, or {@code null} if
     *         the word wasn't found in the sentence. This should not happen in
     *         general, but it might in case of junk tokens such as non-word
     *         single-character tokens (e.g. "`" or "''")
     */
    public String posTagOfWord(String word, int id) {
        return getGrammaticalNatureOfLeaf(findLeafForToken(word, id));
    }
    
    /**
     * @return the nouns as a string list
     * @see #nouns()
     */
    public List<String> nounsAsStrings() {
        List<String> strings = new ArrayList<>();
        for (Tree noun : nouns) {
            strings.add(noun.value());
        }
        return strings;
    }
    
    /**
     * @param token
     *            the token (~ string) contained in the leaf we are looking for
     * @param index
     *            the index of the token in the sentence, where the indices
     *            start at 0. If the index is {@code -1}, we will return the
     *            first leaf that matches the token, starting at the beginning
     *            of the sentence.
     * @return the {@code index}-th {@code Tree} that contains the token, or
     *         {@code null} if it wasn't found
     */
    private Tree findLeafForToken(String token, int index) {
        final boolean considerIndices = index != -1;
        if (considerIndices && (index < 0 || index >= leaves.size())) {
            throw new ArrayIndexOutOfBoundsException(index + " not a valid leaf index");
        }
        
        for (int i = 0; i < leaves.size(); i++) {
            Tree leaf = leaves.get(i);
            if (leaf.label().value().equals(token)) {
                if (considerIndices && i != index) {
                    continue;
                }
                
                return leaf;
            }
        }
        
        /* No match was found */
        String msg = considerIndices ? token + " not present at index " + index
                : token + " does not match any leaf in the tree";
        System.err.println(msg);
        return null;
    }
    
    /**
     * @param leaf
     *            if {@code null}, the return value will be null
     * @return grammatical nature (POS tag) of the token, or {@code null} if the
     *         argument was {@code null} as well
     */
    private String getGrammaticalNatureOfLeaf(Tree leaf) {
        if (leaf == null) {
            return null;
        }
        
        if (!leaf.isLeaf()) {
            throw new IllegalArgumentException("token was not a leaf");
        }
        
        Tree parent = leaf.parent(this.tree);
        return parent.label().value();
    }
    
    private List<Tree> objectArrayToTreeList() {
        Object[] all = tree.toArray();
        List<Tree> asList = new ArrayList<>();
        for (Object t : all) {
            if (((Tree) t).isLeaf()) {
                asList.add((Tree) t);
            }
        }
        return asList;
    }

    private List<Tree> initNouns() {
        List<Tree> nouns = new ArrayList<>();
//        for (Tree leaf : leaves) {
//            if (Config.POS_NOUNS_FR.contains(getGrammaticalNatureOfLeaf(leaf))) {
//                nouns.add(leaf);
//            }
//        }
        leaves.stream().filter(l -> POS_NOUNS_FR.contains(getGrammaticalNatureOfLeaf(l)))
                .forEach(l -> nouns.add(l));
        
        return nouns;
    }
    
    private List<Tuple<String, String>> initPosTags() {
        List<Tuple<String, String>> posTags = new ArrayList<>();
//        for (Tree leaf : leaves) {
//            Tuple<String, String> wordAndTag = new Tuple<String, String>(
//                    leaf.value(), getGrammaticalNatureOfLeaf(leaf));
//            
//            posTags.add(wordAndTag);
//        }
        
        /* add the couple (word,tag) */
        leaves.stream().forEach(l -> posTags.add(new Tuple<>(l.value(), getGrammaticalNatureOfLeaf(l))));
        return posTags;
    }
    
}
