package ch.epfl.lia.opinion;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import ch.epfl.lia.entity.Chain;
import ch.epfl.lia.entity.Opinion;
import ch.epfl.lia.entity.ParsedArticle;
import ch.epfl.lia.entity.ParsedSentence;
import ch.epfl.lia.entity.Topic;
import ch.epfl.lia.nlp.Dependency;
import ch.epfl.lia.nlp.Word;
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
        final Word topicWord;
        final Word otherWord;
        
        /* The first word of the dependency is the topical word */
        if (topic.keys().contains(dependency.gov().value())) {
            topicWord = dependency.gov();
            otherWord = dependency.dep();
        /* The second word is */
        } else if (topic.keys().contains(dependency.dep().value())) {
            topicWord = dependency.dep();
            otherWord = dependency.gov();
        } else {
            return opinions;
        }
        
        /* If the second word is polar */
        final Optional<Polarity> polarityLookup = dictionary.lookup(otherWord.value());
        if (polarityLookup.isPresent()) {
            final Word polarityWord = otherWord;
            final Polarity polarity = polarityLookup.get();
            
            /* Determine whether the topical word is polar as well */
            final Optional<Polarity> topicalWordPolarityLookup = dictionary.lookup(topicWord.value());
            if (topicalWordPolarityLookup.isPresent()) {
                System.err.println(topicWord);
            }
            
            /* An opinion was found */
            opinions.add(new Opinion(topic, topicWord, polarityWord, polarity));
            
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
        final Word topicWord = chain.first().gov();
        final Word polarityWord = chain.last().dep();
        
        final Optional<Polarity> lastDepPolarityLookup = dictionary.lookup(chain.last().dep().value());
        if (lastDepPolarityLookup.isPresent()) {
            final Polarity polarity = lastDepPolarityLookup.get();
            return Optional.of(new Opinion(topic, topicWord, polarityWord, polarity));
        } else {
            return Optional.empty();
        }
    }
}
