package ch.epfl.lia.parser;

import static ch.epfl.lia.main.Config.PARSED_CONLL_FILES_LOCATION;
import static ch.epfl.lia.main.Config.RAW_ARTICLES_LOCATION;
import static ch.epfl.lia.main.Config.STANFORD_PARSER_LOCATION_FR;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import ch.epfl.lia.entity.Article;
import ch.epfl.lia.entity.Language;
import ch.epfl.lia.entity.ParsedArticle;
import ch.epfl.lia.entity.ParsedSentence;
import ch.epfl.lia.nlp.Dependency;
import ch.epfl.lia.util.Preconditions;
import ch.epfl.lia.util.Tuple;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreePrint;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.international.french.FrenchTreebankLanguagePack;

/**
 * @see LanguageParser
 * @author Cyriaque Brousse
 */
public class FrenchParser extends LanguageParser {
    
    private static final LexicalizedParser PARSER = LexicalizedParser.loadModel(STANFORD_PARSER_LOCATION_FR);

    @Override
    public Language getLanguage() {
        return Language.FRENCH;
    }

    @Override
    public ParsedArticle parse(Article article) throws ParseException {
        Preconditions.throwIfNull("article may not be null", article);
        
        /* Files location */
        final String rawFileLocation = RAW_ARTICLES_LOCATION + article.id() + ".txt";
        final String conllOutputLocation = PARSED_CONLL_FILES_LOCATION + article.id() + ".txt";
        
        List<ParsedSentence> parsedSentences = new ArrayList<>();
        List<String> nouns = new ArrayList<>();
        
        try {
            for (List<HasWord> sentence : new DocumentPreprocessor(rawFileLocation)) {
                final Tree tree = PARSER.parse(sentence);
                final TreeAnalyzer analyzer = new TreeAnalyzer(tree);
                
                printTree(tree, 0);
                saveConll(conllOutputLocation, tree);
                
                /* Dependencies extraction */
                DependencyExtractionPipeline pipeline = new DependencyExtractionPipeline(conllOutputLocation, analyzer);
                List<Dependency> dependencies = pipeline.process();
                
                /* Part of speech tags and nouns extraction */
                List<Tuple<String, String>> wordsAndTags = analyzer.wordsAndTags();
                nouns.addAll(analyzer.nounsAsStrings());
                
                /* Saving the extracted features */
                parsedSentences.add(new ParsedSentence(wordsAndTags, dependencies, tree));
            }
            
        } catch (IOException | DependencyExtractionException e) {
            throw new ParseException(e);
        }
        
        return new ParsedArticle(article.id(), parsedSentences, nouns);
    }
    
    /**
     * Saves the tree in CoNLL-2007 format to disk
     */
    private void saveConll(String path, Tree tree) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(path);
        
        TreebankLanguagePack langPack = new FrenchTreebankLanguagePack();
        TreePrint treePrint = new TreePrint("conll2007", langPack);
        treePrint.printTree(tree, writer);
        
        writer.close();
    }
    
    private void printTree(Tree tree, int level) {
        String indent = "";
        for (int i = 0; i <= level; i++) {
            indent += "  ";
        }
        
        System.out.println(tree.label());
        for (Tree ch : tree.children()) {
            System.out.print(indent);
            printTree(ch, level + 1);
        }
    }

}
