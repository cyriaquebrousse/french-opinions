package ch.epfl.lia.parser;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import ch.epfl.lia.entity.Dependency;
import ch.epfl.lia.main.Config;
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
        return new ArrayList<Tree>(leaves);
    }

    /**
     * @return the subset of leaves that are nouns
     * @see #leaves()
     */
    public List<Tree> nouns() {
        return new ArrayList<Tree>(nouns);
    }
    
    /**
     * @return the list of words associated with their tag
     */
    public List<Tuple<String, String>> wordsAndTags() {
        return new ArrayList<>(wordsAndTags);
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
    
    public List<Dependency> getVerbalDependencies(List<Dependency> list) {
        List<Dependency> filtered = new ArrayList<>();
        
        for (Dependency dependency : list) {
            if (dependency.reln().equals("root")) continue;
            
            Tree gov = findLeafForToken(dependency.gov(), dependency.govId() - 1);
            String govNature = getGrammaticalNatureOfLeaf(gov);
            Tree dep = findLeafForToken(dependency.dep(), dependency.depId() - 1);
            String depNature = getGrammaticalNatureOfLeaf(dep);
            
            if (Config.POS_VERBS_FR.contains(govNature) || Config.POS_VERBS_FR.contains(depNature)) {
                filtered.add(dependency);
            }
        }
        
        return filtered;
    }
    
    /** Finds a tree path from a leaf to another leaf (a word to another word in the sentence).
     * @param startLeaf
     * @param endLeaf
     */
    public void printPathLeafToLeaf(String startLeaf, String endLeaf) {
        Tree start = findLeafForToken(startLeaf);
        Tree end = findLeafForToken(endLeaf);
        
        List<Tree> path = tree.pathNodeToNode(start, end);
        printPath(path);
    }

    /**
     * @return the first leaf that contains the token
     * @throws NoSuchElementException
     *             if no such {@code Tree} was found
     *             {@link #findLeafForToken(String, int)}
     */
    public Tree findLeafForToken(String token) {
        return findLeafForToken(token, -1);
    }

    /**
     * @param token
     *            the token (~ string) contained in the leaf we are looking for
     * @param index
     *            the index of the token in the sentence, where the indices
     *            start at 0. If the index is -1, we will return the first leaf
     *            that matches the token, starting at the beginning of the
     *            sentence.
     * @return the {@code index}-th {@code Tree} that contains the token. It is
     *         never {@code null}
     * @throws NoSuchElementException
     *             if no such {@code Tree} was found
     */
    public Tree findLeafForToken(String token, int index) {
        final boolean considerIndices = index != -1;
        if (considerIndices && (index < 0 || index >= leaves.size())) {
            throw new ArrayIndexOutOfBoundsException(index + " not a valid leaf index");
        }
        
        for (int i = 0; i < leaves.size(); i++) {
            Tree leaf = leaves.get(i);
            if (leaf.label().value().equals(token)) {
                if (considerIndices && i != index) continue;
                return leaf;
            }
        }
        
        /* No match was found */
        String msg = considerIndices ? token + " not present at index " + index :
            token + " does not match any leaf in the tree";
        throw new NoSuchElementException(msg);
    }
    
    /**
     * @param token
     * @return grammatical nature (POS) of the token
     */
    public String getGrammaticalNatureOfLeaf(Tree token) {
        if (!token.isLeaf()) {
            throw new IllegalArgumentException("token was not a leaf");
        }
        
        Tree parent = token.parent(this.tree);
        return parent.label().value();
    }
    
    /**
     * Print the path between the governor and the dependent words of the
     * dependency
     * 
     * @param dependency
     */
    public void printDependencyPath(Dependency dependency) {
        Tree gov = findLeafForToken(dependency.gov(), dependency.govId() - 1);
        Tree dep = findLeafForToken(dependency.dep(), dependency.depId() - 1);
        
        printPath(tree.pathNodeToNode(gov, dep));
    }

    private void printPath(List<Tree> path) {
        if (path != null && !path.isEmpty()) {
            System.out.print("[");
            for (Tree node : path) {
                System.out.print(" -> " + node.label());
            }
            System.out.println("]");
        } else {
            System.out.println("The path is empty");
        }
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
        for (Tree leaf : leaves) {
            if (Config.POS_NOUNS_FR.contains(getGrammaticalNatureOfLeaf(leaf))) {
                nouns.add(leaf);
            }
        }
        return nouns;
    }
    
    private List<Tuple<String, String>> initPosTags() {
        List<Tuple<String, String>> posTags = new ArrayList<>();
        for (Tree leaf : leaves) {
            Tuple<String, String> wordAndTag = new Tuple<String,String>
                    (leaf.value(), getGrammaticalNatureOfLeaf(leaf));
            
            posTags.add(wordAndTag);
        }
        return posTags;
    }
    
}
