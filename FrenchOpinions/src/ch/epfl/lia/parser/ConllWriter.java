package ch.epfl.lia.parser;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import ch.epfl.lia.nlp.Word;

/**
 * @author Cyriaque Brousse
 */
public final class ConllWriter {
    
    private ConllWriter() { }
    
    /**
     * Saves the provided list of words to the specified file in a variant of
     * the CoNLL 2007 format, specified as follows:<br>
     * {@code ID WORDSTRING _ POSTAG POSTAG _}<br>
     * The values are tab-separated.
     * 
     * @param words
     *            list of words to save as CoNLL-2007
     * @param path
     *            the file to save to
     * @throws IOException
     *             if the I/O operations on the specified path returned an error
     */
    public static void writeWordsAsConll(List<Word> words, String path) throws IOException {
        List<String> adaptedTokens = new ArrayList<>();
        words.stream().forEach(w -> {
            /* Eliminate undetermination on certain nouns */
            String posTag = w.posTag();
            if (posTag.equals("N")) {
                if (Character.isUpperCase(w.value().charAt(0))) {
                    posTag = "NPP";
                } else {
                    posTag = "NC";
                }
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append(w.id())
                .append("\t")
                .append(w.value())
                .append("\t_\t")
                .append(posTag)
                .append("\t")
                .append(posTag)
                .append("\t_");
            
            adaptedTokens.add(sb.toString());
        });
        
        PrintWriter writer = new PrintWriter(path);
        adaptedTokens.stream().forEach(t -> writer.println(t));
        writer.close();
    }
    
}
