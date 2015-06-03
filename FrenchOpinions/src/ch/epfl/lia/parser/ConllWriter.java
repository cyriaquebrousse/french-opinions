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
