package ch.epfl.lia.main;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import ch.epfl.lia.entity.Article;
import ch.epfl.lia.entity.Dependency;
import ch.epfl.lia.entity.Language;
import ch.epfl.lia.entity.Opinion;
import ch.epfl.lia.entity.ParsedArticle;
import ch.epfl.lia.entity.Topic;
import ch.epfl.lia.opinion.Evaluator;
import ch.epfl.lia.opinion.OpinionExtractionException;
import ch.epfl.lia.opinion.OpinionExtractor;
import ch.epfl.lia.parser.DependencyExtractionException;
import ch.epfl.lia.parser.DependencyExtractionPipeline;
import ch.epfl.lia.parser.FrenchParser;
import ch.epfl.lia.parser.LanguageParser;
import ch.epfl.lia.parser.ParseException;
import ch.epfl.lia.parser.TreeAnalyzer;
import ch.epfl.lia.topic.TopicExtractionException;
import ch.epfl.lia.topic.TopicManager;
import ch.epfl.lia.util.Articles;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreePrint;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.international.french.FrenchTreebankLanguagePack;

/**
 * @author Cyriaque Brousse
 */
public class Main {

    private static final List<Character> SPECIAL_CHARS = Arrays.asList('\'', '\"', '«', '»', '%', '$', '£', '€', '`');
    private static final Language LANGUAGE = Language.FRENCH;
    
    public static void main(String[] args) throws IOException, ParseException, TopicExtractionException, OpinionExtractionException {
        
        final LanguageParser parser = new FrenchParser();
        final List<Article> articles = new ArrayList<>();
        final String serialPathPrefix = "parsed/ser/";
        
        for (String arg : args) {
            final int articleId = Integer.parseInt(arg);
            
            Article article = Articles.getFromDisk(serialPathPrefix + articleId + ".ser");
            if (article == null) {
                article = Articles.constructArticleFromRaw(articleId, LANGUAGE);
            }
            
            articles.add(article);
        }

        for (Article article : articles) {
            article.parse(parser);
            article.saveToDisk(serialPathPrefix + article.id() + ".ser");
        }
            
        Collection<Topic> topics = TopicManager.extractTopics(articles);
        getMostProminentTopicsForArticles(topics);
        
        OpinionExtractor extractor = OpinionExtractor.getForLanguage(LANGUAGE);
        Evaluator.Builder evalBuilder = new Evaluator.Builder();
        
        for (Article article : articles) {
            System.out.println("\n####### " + article.id() + " #######");
            
            ParsedArticle parsed = article.parse(); // no parsing effect
            
            Set<Opinion> opinions = extractor.extractOpinions(parsed, topics);
            System.out.println("Extracted opinions:\t" + opinions);
            
            evalBuilder.addOpinionsToArticle(article, opinions);
            
            final int score = opinions.stream().mapToInt(o -> o.polarity().score()).sum();
            System.out.println("Score:\t" + score);
        }
        
        /* Statistics */
        System.out.println(evalBuilder.build());
    }

    @Deprecated
    private static List<List<Dependency>> parseArticle(final LexicalizedParser parser, final int articleId) {
        System.out.println(">> Parsing article " + articleId + ":");
        
        /* Init file locations */
        final String rawArticleLocation = "raw/" + articleId + ".txt";
        final String conllOutputLocation = "parsed/conll/" + articleId + ".txt";
        final String dependenciesLocation = "parsed/dependencies/" + articleId + ".txt";
        final String nounsLocation = "parsed/nouns/" + articleId + ".txt";
        
        /* Return value: outer list is per sentence */
        final List<List<Dependency>> articleDeps = new ArrayList<>();

        try {
            PrintWriter conllWriter = new PrintWriter(conllOutputLocation);
            BufferedWriter depWriter = new BufferedWriter(new FileWriter(dependenciesLocation));
            BufferedWriter nounsWriter = new BufferedWriter(new FileWriter(nounsLocation));
            
            int sentenceId = 0;
            for (List<HasWord> sentence : new DocumentPreprocessor(rawArticleLocation)) {
                Tree tree = parser.parse(sentence);
                TreeAnalyzer analyzer = new TreeAnalyzer(tree);

                printTree(tree, 0);
                saveOutput(conllOutputLocation, "conll2007", tree);

                /* Saving dependencies */
                DependencyExtractionPipeline pipeline = new DependencyExtractionPipeline(conllOutputLocation, analyzer);
                List<Dependency> deps = pipeline.process();
                articleDeps.add(new ArrayList<>());
                for (Dependency dep : deps) {
                    depWriter.write(dep + "\n");
                    articleDeps.get(sentenceId).add(dep);
                }
                depWriter.write("\n");
                
                /* Saving nouns */
                List<String> nouns = analyzer.nounsAsStrings();
                for (String noun : nouns) {
                    if (noun.isEmpty() || noun.length() == 1 && SPECIAL_CHARS.contains(noun.charAt(0))) {
                        continue;
                    }
                    nounsWriter.write(noun + "\n");
                }
                nounsWriter.write("\n");
                
                ++sentenceId;
            }
            
            conllWriter.close();
            depWriter.close();
            nounsWriter.close();

        } catch (IOException | DependencyExtractionException e) {
            e.printStackTrace();
        }
        
        System.out.println(">> Done parsing article " + articleId + ".");
        
        return articleDeps;
    }
    
    @Deprecated
    private static void printTree(Tree tree, int level) {
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
    
    @Deprecated
    private static void saveOutput(String path, String format, Tree tree) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(path);
        
        TreebankLanguagePack langPack = new FrenchTreebankLanguagePack();
        TreePrint treePrint = new TreePrint(format, langPack);
        treePrint.printTree(tree, writer);
        
        writer.close();
    }
    
    @Deprecated
    private static void runMallet(final int articleId) {
        System.out.println(">> Running topic extraction on available articles:");
        
        ProcessBuilder builder = new ProcessBuilder("sh", "run_mallet.sh",
                "parsed/nouns/", Integer.toString(1));
        builder.redirectErrorStream(true);
        
        try {
            Process p = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while (reader.readLine() != null) {}
            if (p.waitFor() != 0) throw new IllegalStateException();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println(">> Done extracting topics.");
    }
    
    @Deprecated
    private static List<Topic> extractTopics() {
        System.out.println("\n>> Extracted topics:");
        List<Topic> topics = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("topics/keys.txt"));
            String line;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                
                String[] split = line.split("\\s+");
                List<String> keys = new ArrayList<>();
                for (int i = 2; i < split.length; i++) {
                    keys.add(split[i]);
                }
                
                Topic topic = new Topic(Integer.parseInt(split[0]),
                        Double.parseDouble(split[1]), keys);
                topics.add(topic);
            }
            reader.close();
            
            /* Printing the topic list */
            for (Topic topic : topics) {
                System.out.println(topic.lineId() + "\t[" + topic.polarity() + "]\t" + topic.keys());
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return topics;
    }
    
    private static void getMostProminentTopicsForArticles(Collection<Topic> topics) {
        System.out.println("\n>> Most prominent topics per article:");
        try {
            BufferedReader reader = new BufferedReader(new FileReader("topics/compo.txt"));
            String line;
            
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                
                String[] split = line.split("\\s+");
                final int articleId = Integer.parseInt(split[1].substring(
                        split[1].lastIndexOf('/') + 1, split[1].indexOf(".txt")));
                List<Integer> mostProminentTopics = new ArrayList<>();
                
                for (int i = 2; i < split.length - 1; i += 2) {
                    if (Double.parseDouble(split[i + 1]) >= 0.1) {
                        mostProminentTopics.add(Integer.parseInt(split[i]));
                    } else break;
                }
                
                // FIXME incorrect
                if (mostProminentTopics.isEmpty()) {
                    mostProminentTopics.add(Integer.parseInt(split[2]));
                }
                
                System.out.print("Article " + articleId + ": \t");
                System.out.println(mostProminentTopics);
            }
            reader.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
