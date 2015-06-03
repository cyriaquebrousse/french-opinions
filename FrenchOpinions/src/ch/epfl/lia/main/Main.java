package ch.epfl.lia.main;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import ch.epfl.lia.entity.Article;
import ch.epfl.lia.entity.Language;
import ch.epfl.lia.entity.Opinion;
import ch.epfl.lia.entity.ParsedArticle;
import ch.epfl.lia.entity.Topic;
import ch.epfl.lia.opinion.Evaluator;
import ch.epfl.lia.opinion.OpinionExtractionException;
import ch.epfl.lia.opinion.OpinionExtractor;
import ch.epfl.lia.parser.FrenchParser;
import ch.epfl.lia.parser.LanguageParser;
import ch.epfl.lia.parser.ParseException;
import ch.epfl.lia.topic.TopicExtractionException;
import ch.epfl.lia.topic.TopicManager;
import ch.epfl.lia.util.Articles;

/**
 * @author Cyriaque Brousse
 */
public class Main {

    private static final Language LANGUAGE = Language.FRENCH;
    
    public static void main(String[] args) throws IOException, ParseException,
            TopicExtractionException, OpinionExtractionException {
        
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
            System.err.println("Parsing " + article.id());
            article.parse(parser);
            article.saveToDisk(serialPathPrefix + article.id() + ".ser");
        }
            
        Collection<Topic> topics = TopicManager.extractTopics(articles);
        
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
            
            final int numberExtractedOpinions = evalBuilder.opinionCount(article);
            System.out.println("Extracted opinions:\t" + numberExtractedOpinions);
            
//            System.out.println("--- Assessment of opinions ---");
//            Assessment.assessOpinions(opinions, "results/expected_polarities.txt");
        }
        
        /* Statistics */
        System.out.println(evalBuilder.build());
    }

    private static void getMostProminentTopicsForArticles(Collection<Topic> topics) {
        System.out.println("\n>> Most prominent topics per article:");
        try {
            BufferedReader reader = new BufferedReader(new FileReader("topics/compo.txt"));
            String line;
            
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                
                String[] split = line.split("\\s+");
                final int articleId = Integer.parseInt(split[1].substring(
                        split[1].lastIndexOf('/') + 1, split[1].indexOf(".txt")));
                List<Integer> mostProminentTopics = new ArrayList<>();
                
                for (int i = 2; i < split.length - 1; i += 2) {
                    if (Double.parseDouble(split[i + 1]) >= 0.1) {
                        mostProminentTopics.add(Integer.parseInt(split[i]));
                    } else {
                        break;
                    }
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
