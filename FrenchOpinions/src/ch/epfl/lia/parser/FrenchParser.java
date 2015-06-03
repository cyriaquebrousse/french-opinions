package ch.epfl.lia.parser;

import static ch.epfl.lia.main.Config.PARSED_CONLL_FILES_LOCATION;
import static ch.epfl.lia.main.Config.RAW_ARTICLES_LOCATION;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ch.epfl.lia.entity.Article;
import ch.epfl.lia.entity.Language;
import ch.epfl.lia.entity.ParsedArticle;
import ch.epfl.lia.entity.ParsedSentence;
import ch.epfl.lia.main.Config;
import ch.epfl.lia.nlp.Dependency;
import ch.epfl.lia.nlp.Word;
import ch.epfl.lia.util.Preconditions;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

/**
 * @see LanguageParser
 * @author Cyriaque Brousse
 */
public class FrenchParser extends LanguageParser {
    
    private static final MaxentTagger TAGGER = new MaxentTagger(Config.STANFORD_TAGGER_LOCATION_FR);

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
                List<Word> words = tagWords(TAGGER, sentence);
                ConllWriter.writeWordsAsConll(words, conllOutputLocation);
                
                final ParsingAnalyzer analyzer = new ParsingAnalyzer(words);
                
                /* Dependencies extraction */
                FrenchDependencyExtractionPipeline pipeline = new FrenchDependencyExtractionPipeline(
                        conllOutputLocation, analyzer);
                List<Dependency> dependencies = pipeline.extract();
                
                /* Part of speech tags and nouns extraction */
                nouns.addAll(analyzer.nounsAsStrings());
                
                /* Saving the extracted features */
                parsedSentences.add(new ParsedSentence(words, dependencies));
            }
            
        } catch (IOException | DependencyExtractionException e) {
            throw new ParseException(e);
        }
        
        return new ParsedArticle(article.id(), parsedSentences, nouns);
    }
    
    /**
     * @param tagger
     *            the tagger to use
     * @param sentence
     *            the sentence to tag
     * @return the list of words extracted from the sentence
     * @see Word
     */
    private List<Word> tagWords(MaxentTagger tagger, List<HasWord> sentence) {
        List<TaggedWord> taggedWords = tagger.tagSentence(sentence);
        List<Word> words = new ArrayList<>();
        
        int id = 1;
        for (TaggedWord w : taggedWords) {
            words.add(new Word(w.value(), id, w.tag()));
            id++;
        }
        
        return words;
    }

}
