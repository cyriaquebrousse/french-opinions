package ch.epfl.lia.main;
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
        }
        
        /* Statistics */
        System.out.println(evalBuilder.build());
    }
    
}
