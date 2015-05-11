package ch.epfl.lia.opinion;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import ch.epfl.lia.entity.Chain;
import ch.epfl.lia.entity.Dependency;
import ch.epfl.lia.entity.Opinion;
import ch.epfl.lia.entity.ParsedArticle;
import ch.epfl.lia.entity.ParsedSentence;
import ch.epfl.lia.entity.Topic;
import ch.epfl.lia.opinion.dictionary.FrenchSentimentDictionary;
import ch.epfl.lia.opinion.dictionary.Polarity;
import ch.epfl.lia.opinion.dictionary.SentimentDictionary;
import ch.epfl.lia.util.Preconditions;

/**
 * @author Cyriaque Brousse
 */
public class FrenchOpinionExtractor extends OpinionExtractor {
    
    private final SentimentDictionary dictionary = FrenchSentimentDictionary.getInstance();

    @Override
    public Set<Opinion> extractOpinions(ParsedArticle article,
            Collection<Topic> topics) throws OpinionExtractionException {
        Preconditions.throwIfNull("a parsed article is needed", article);
        Preconditions.throwIfNullOrEmpty("topics are needed", topics);
        
        Set<Opinion> opinions = new HashSet<>();
        article.parsedSentences().stream().forEach(s -> opinions.addAll(extractOpinions(s, topics)));

        return opinions;
    }
    
    /**
     * Extracts opinions from a sentence, basing the analysis on the provided
     * topics
     */
    private Set<Opinion> extractOpinions(ParsedSentence sentence, Collection<Topic> topics) {
        List<Dependency> allDeps = sentence.dependencies();
        Set<Opinion> opinions = new HashSet<>();
        
        allDeps.stream().forEach(d -> {
            topics.stream().forEach(t -> {
                opinions.addAll(analyzeDependency(d, allDeps, t));
            });
        });
        
        return opinions;
    }
    
    /**
     * Analyses a specific dependency, with respect to the collection of all
     * dependencies
     */
    private Set<Opinion> analyzeDependency(Dependency dependency,
            Collection<Dependency> allDeps, Topic topic) {
        /* Opinion collector */
        final Set<Opinion> opinions = new HashSet<>();
        
        /* Determining topic word */
        final String topicWord;
        final int topicWordId;
        final String otherWord;
        final int otherWordId;
        
        if (topic.keys().contains(dependency.gov())) {
            topicWord = dependency.gov();
            topicWordId = dependency.govId();
            otherWord = dependency.dep();
            otherWordId = dependency.depId();
        } else if (topic.keys().contains(dependency.dep())) {
            topicWord = dependency.dep();
            topicWordId = dependency.depId();
            otherWord = dependency.gov();
            otherWordId = dependency.govId();
        } else {
            return opinions;
        }
        
        /* If the second word is polar */
        final Optional<Polarity> polarityLookup = dictionary.lookup(otherWord);
        if (polarityLookup.isPresent()) {
            final String polarityWord = otherWord;
            final int polarityWordId = otherWordId;
            final Polarity polarity = polarityLookup.get();
            
            /* An opinion was found */
            opinions.add(new Opinion(topic, topicWord, topicWordId, polarityWord, polarityWordId, polarity));
            
            /* Try to find and analyze chains, starting with this dependency */
            final Collection<Opinion> chainOpinions = new HashSet<>();
            Chains.allChainsFromFirst(dependency, allDeps).stream().forEach(c -> {
                Optional<Opinion> opinion = analyzeChain(c, topic);
                if (opinion.isPresent()) {
                    chainOpinions.add(opinion.get());
                }
            });
            opinions.addAll(chainOpinions);
        }
        
        /* Return any found opinions */
        return opinions;
    }
    
    /**
     * @return an opinion, if the chain contains one, otherwise nothing
     */
    private Optional<Opinion> analyzeChain(Chain chain, Topic topic) {
        /* Consider the whole chain as a single dependency */
        final String topicWord = chain.first().gov();
        final int topicWordId = chain.first().govId();
        final String polarityWord = chain.last().dep();
        final int polarityWordId = chain.last().depId();
        
        final Optional<Polarity> lastDepPolarityLookup = dictionary.lookup(chain.last().dep());
        if (lastDepPolarityLookup.isPresent()) {
            final Polarity polarity = lastDepPolarityLookup.get();
            return Optional.of(new Opinion(topic, topicWord, topicWordId, polarityWord, polarityWordId, polarity));
        } else {
            return Optional.empty();
        }
    }
}
