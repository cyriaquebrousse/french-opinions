package ch.epfl.lia.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ch.epfl.lia.nlp.Word;
import static ch.epfl.lia.util.NLPUtils.POS_NOUNS_FR;

/**
 * @author Cyriaque Brousse
 */
public class ParsingAnalyzer {
    
    private final List<Word> words;
    private final List<Word> nouns;

    public ParsingAnalyzer(List<Word> words) {
        this.words = new ArrayList<>(words);
        this.nouns = initNouns();
    }
    
    public List<Word> words() {
        return new ArrayList<>(words);
    }
    
    public List<Word> nouns() {
        return new ArrayList<>(nouns);
    }
    
    public List<String> nounsAsStrings() {
        return nouns.stream().map(n -> n.value()).collect(Collectors.toList());
    }
    
    private List<Word> initNouns() {
        List<Word> nouns = new ArrayList<>();
        words.stream().filter(w -> POS_NOUNS_FR.contains(w.posTag()))
                .forEach(w -> nouns.add(w));
        return nouns;
    }
    
}
