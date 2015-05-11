package ch.lia.parser;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import ch.epfl.lia.parser.TreeAnalyzer;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.Tree;

/**
 * @author Cyriaque Brousse
 */
public class TreeAnalyzerTest {
    
    private static final LexicalizedParser PARSER = LexicalizedParser
            .loadModel("edu/stanford/nlp/models/lexparser/frenchFactored.ser.gz");
    
    private static final String SENTENCE_1 = "Le chien brun sauta au-dessus de la rivière.";
    private static final List<String> EXP_LEAVES_SENT_1 = Arrays.asList("Le",
            "chien", "brun", "sauta", "au", "-", "dessus", "de", "la", "rivière", ".");
    private static final List<String> EXP_NOUNS_SENT_1 = Arrays.asList("chien", "rivière");
    
    private static final String SENTENCE_2 = "Le sucre participe à près de 35 millions de morts "
            + "chaque année et sa commercialisation doit par conséquent être strictement"
            + "encadrée: c'est en substance la position très tranchée défendue par trois "
            + "chercheurs américains de l'Université de Californie (San Francisco) dans un "
            + "article publié par la prestigieuse revue Nature, «La toxique vérité sur le sucre».";
    private static final List<String> EXP_NOUNS_SENT_2 = Arrays.asList("sucre",
            "millions", "morts", "année", "commercialisation", "conséquent",
            "substance", "position", "chercheurs", "Université", "Californie",
            "article", "revue", "Nature", "vérité", "sucre");
    
    @Test
    public void testLeavesSimpleSentence() {
        Tree tree = PARSER.parse(SENTENCE_1);
        TreeAnalyzer analyzer = new TreeAnalyzer(tree);
        List<String> actualLeaves = treeListAsStringList(analyzer.leaves());
        
        assertEquals(EXP_LEAVES_SENT_1, actualLeaves);
    }
    
    @Test
    public void testNounsSimpleSentence() {
        Tree tree = PARSER.parse(SENTENCE_1);
        TreeAnalyzer analyzer = new TreeAnalyzer(tree);
        List<String> actualNouns = treeListAsStringList(analyzer.nouns());
        
        assertEquals(EXP_NOUNS_SENT_1, actualNouns);
    }
    
    @Test
    public void testNounsLongSentence() {
        Tree tree = PARSER.parse(SENTENCE_2);
        TreeAnalyzer analyzer = new TreeAnalyzer(tree);
        List<String> actualNouns = treeListAsStringList(analyzer.nouns());
        
        assertEquals(EXP_NOUNS_SENT_2, actualNouns);
    }
    
    private List<String> treeListAsStringList(List<Tree> trees) {
        List<String> strings = new ArrayList<>();
        for (Tree tree : trees) {
            strings.add(tree.value());
        }
        return strings;
    }

}
