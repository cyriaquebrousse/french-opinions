package ch.epfl.lia.util;

import java.util.Arrays;
import java.util.List;

import ch.epfl.lia.entity.Language;
import ch.epfl.lia.nlp.Word;

/**
 * @author Cyriaque Brousse
 */
public final class NLPUtils {
    
    private NLPUtils() { }

    /* French POS families */
    public static final List<String> POS_NOUNS_FR = Arrays.asList("N", "NC", "NPP");
    public static final List<String> POS_VERBS_FR = Arrays.asList("V", "VINF", "VIMP", "VPP", "VPR", "VS");
    public static final List<String> POS_DETERMINERS_FR = Arrays.asList("D", "DET", "DETWH");
    public static final List<String> POS_AVERBS_FR = Arrays.asList("ADV", "ADVWH");
    public static final List<String> POS_CLPRO_FR = Arrays.asList("CL", "CLS", "CLO", "CLR");
    public static final List<String> POS_PRO_FR = Arrays.asList("PRO", "PROREL");
    public static final List<String> POS_PREP_FR = Arrays.asList("P", "P+D", "P+PRO", "PROWH");
    public static final List<String> POS_CONJ_FR = Arrays.asList("C", "CC", "CS");
    public static final List<String> POS_ADJ_FR = Arrays.asList("A", "ADJ", "ADJWH");
    
    public static boolean isVerb(Word w, Language lang) {
        switch (lang) {
        case FRENCH:
            return POS_VERBS_FR.contains(w.posTag());
        default:
            return false;
        }
    }
}
