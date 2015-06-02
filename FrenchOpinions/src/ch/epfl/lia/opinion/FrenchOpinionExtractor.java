package ch.epfl.lia.opinion;

import static ch.epfl.lia.opinion.dictionary.Polarity.NEGATIVE;
import static ch.epfl.lia.opinion.dictionary.Polarity.NEUTRAL;
import static ch.epfl.lia.opinion.dictionary.Polarity.POSITIVE;
import static ch.epfl.lia.util.NLPUtils.isVerb;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import ch.epfl.lia.entity.Language;
import ch.epfl.lia.entity.Opinion;
import ch.epfl.lia.entity.ParsedArticle;
import ch.epfl.lia.entity.ParsedSentence;
import ch.epfl.lia.entity.Topic;
import ch.epfl.lia.nlp.Chain;
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
    public Language getLanguage() {
        return Language.FRENCH;
    }

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
        final Optional<Polarity> polarityLookup = dictionary.stemAndlookup(otherWord);
        if (polarityLookup.isPresent()) {
            final Word polarWord = otherWord;
            final Polarity polarWordPolarity = polarityLookup.get();
            
            final Polarity globalPolarity = determineGlobalPolarity(polarWord, polarWordPolarity, topicWord);
            
            /* An opinion was found */
            if (!topicWord.equals(polarWord)) {
                opinions.add(new Opinion(topic, topicWord, polarWord, globalPolarity));
            }
            
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
        final Word polarWord = chain.last().dep();
        
        if (topicWord.equals(polarWord)) {
            return Optional.empty();
        }
        
        final Optional<Polarity> lastDepPolarityLookup = dictionary.stemAndlookup(chain.last().dep());
        if (lastDepPolarityLookup.isPresent()) {
            final Polarity polarity = lastDepPolarityLookup.get();
            return Optional.of(new Opinion(topic, topicWord, polarWord, polarity));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Determines the global polarity for the provided couple of words. It
     * clears up inconsistencies such as a negative polarity for couples as
     * (kill,cancer). It generally does not alter the predicted polarity for
     * couples where there is no inconsistency, such as (bad,cancer).
     * 
     * @param polarWord
     *            the polar word, i.e. the word that is not a topic key and has
     *            a polarity
     * @param polarWordPolarity
     *            polarity extracted from the dictionary
     * @param topicWord
     *            the other word, that is a topic key
     * @return the computed polarity for the couple (polarWord,topicWord)
     */
    private Polarity determineGlobalPolarity(Word polarWord, Polarity polarWordPolarity, Word topicWord) {
        /* Check whether the topic word is also polar. If not, there is no further investigation to make */
        Optional<Polarity> topicWordPolarityLookup = dictionary.stemAndlookup(topicWord);
        
        if (topicWordPolarityLookup.isPresent() && topicWordPolarityLookup.get() != NEUTRAL) {
            
            final Polarity topicWordPolarity = topicWordPolarityLookup.get();
            
            if (polarWordPolarity == NEGATIVE && isVerb(polarWord, getLanguage())) {
                /* The polar word is a verb, and its polarity is negative */
                System.err.println(polarWord + "-" + topicWord);
                return topicWordPolarity == NEGATIVE ? POSITIVE : NEGATIVE;
                
            } else if (topicWordPolarity == NEGATIVE && isVerb(topicWord, getLanguage())) {
                /* Same for the topic word */
                System.err.println(polarWord + "+" + topicWord);
                return polarWordPolarity == NEGATIVE ? POSITIVE : NEGATIVE;
            }
            
            /* None of the words is a verb, but we have a +/- case: we need to
               return negative (e.g. "rapid cancer" is +/-, but it is clearly
               negative) */
            if (polarWordPolarity != topicWordPolarity
                    && polarWordPolarity != NEUTRAL
                    && topicWordPolarity != NEUTRAL) {
                System.err.println(polarWord + "*" + topicWord);
                return NEGATIVE;
            }
            
        }
            
        return polarWordPolarity;
    }
}
