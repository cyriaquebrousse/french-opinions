package ch.epfl.lia.topic;

import static ch.epfl.lia.main.Config.MALLET_RUN_SCRIPT_LOCATION;
import static ch.epfl.lia.main.Config.MALLET_TOPIC_KEYS_LOCATION;
import static ch.epfl.lia.main.Config.NOUNS_LOCATION;
import static ch.epfl.lia.main.Config.SHELL;
import static ch.epfl.lia.util.FileUtils.foreachNonEmptyLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.epfl.lia.entity.Article;
import ch.epfl.lia.entity.Language;
import ch.epfl.lia.entity.ParsedArticle;
import ch.epfl.lia.entity.Topic;
import ch.epfl.lia.parser.ParseException;
import ch.epfl.lia.util.FileUtils;
import ch.epfl.lia.util.Preconditions;

/**
 * Collection of methods related to topic extraction
 * 
 * @author Cyriaque Brousse
 */
public final class TopicManager {
    
    private TopicManager() { }
    
    /**
     * Extracts topics from the provided list of articles. It will parse them if
     * not already done, then extract the nouns to a directory that will
     * previously have been cleared, then run MALLET to extract the topics.
     * 
     * @param articles
     *            articles to extract topics from.<br>
     *            <b style="color:red">Warning:</b> All articles must be in the
     *            same language.
     * @return the list of extracted topics
     * @throws TopicExtractionException
     */
    public static Collection<Topic> extractTopics(List<Article> articles) throws TopicExtractionException {
        Preconditions.throwIfNullOrEmpty("article list was empty", articles);
        final Language language = articles.get(0).language();
        if (!articles.stream().allMatch(a -> a.language() == language)) {
            throw new IllegalArgumentException("Cannot extract topics on articles of different languages");
        }
        
        /* Remove old nouns before proceeding */
        FileUtils.clearDirectory(NOUNS_LOCATION);
        
        List<Topic> topics = new ArrayList<>();
        try {
            /* Extract nouns */
            for (Article article : articles) {
                ParsedArticle parsed = article.parse();
                parsed.writeNouns(NOUNS_LOCATION + parsed.id() + ".txt");
            }
            
            /* Run MALLET (topic extractor) */
            runMallet(NOUNS_LOCATION, language);
            
            /* Reading lines from the keys file */
            foreachNonEmptyLine(MALLET_TOPIC_KEYS_LOCATION, line -> {
                String[] split = line.split("\\s+");
                List<String> keys = new ArrayList<>();
                for (int i = 2; i < split.length; i++) {
                    keys.add(split[i]);
                }
                
                topics.add(new Topic(Integer.parseInt(split[0]), Double.parseDouble(split[1]), keys));
            });
            
        } catch (IOException | ParseException e) {
            throw new TopicExtractionException(e);
        }
        
        return topics;
    }
    
    /**
     * Run MALLET tool for topic extraction on the specified directory in the
     * specified language
     * 
     * @param language
     *            language to operate in (needed for the stopword list)
     * @param nounsDir
     *            directory in which the nouns are located
     * @throws TopicExtractionException
     */
    private static void runMallet(String nounsDir, Language language) throws TopicExtractionException {
        ProcessBuilder builder = new ProcessBuilder(SHELL, MALLET_RUN_SCRIPT_LOCATION,
                language.shortName(), nounsDir, Integer.toString(16));
        builder.redirectErrorStream(true);

        try {
            Process p = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            
            while (reader.readLine() != null) {
                /* consume the line */
            }
            
            if (p.waitFor() != 0) throw new IllegalStateException();

        } catch (IOException | InterruptedException | IllegalStateException e) {
            throw new TopicExtractionException(e);
        }
    }
}
