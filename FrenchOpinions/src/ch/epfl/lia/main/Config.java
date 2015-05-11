package ch.epfl.lia.main;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration class
 * 
 * @author Cyriaque Brousse
 */
public final class Config {
    
    /** Part of speech tags corresponding to nouns in French */
    public static final List<String> POS_NOUNS_FR = Arrays.asList("N", "NC", "NPP");

    /** Part of speech tags corresponding to verbs in French */
    public static final List<String> POS_VERBS_FR = Arrays.asList("V", "VINF", "VIMP", "VPP", "VPR", "VS");

    /** Location of mallet-extracted topic keys (all languages) */
    public static final String MALLET_TOPIC_KEYS_LOCATION = "topics/keys.txt";

    /** Location of CoNLL-parsed files */
    public static final String PARSED_CONLL_FILES_LOCATION = "parsed/conll/";

    /** Location of raw (original, unparsed) articles (all languages) */
    public static final String RAW_ARTICLES_LOCATION = "raw/";

    /** Location of the MALT parser configuration file for French */
    public static final String MALT_CONFIG_LOCATION_FR = "lib/fremalt-1.7.mco";

    /** Location of the sentiment dictionary for French */
    public static final String SENTIMENT_DIC_LOCATION_FR = "lib/sentiment_dic_fr.txt";

    /** Location of the Stanford parser for French */
    public static final String STANFORD_PARSER_LOCATION_FR = "edu/stanford/nlp/models/lexparser/frenchFactored.ser.gz";

    /** Type of shell to be used when running shell scripts */
    public static final String SHELL = "sh";

    /** French run script for MALLET tool */
    public static final String MALLET_RUN_SCRIPT_LOCATION = "lib/run_mallet.sh";

    /** Location of extracted nouns (all languages) */
    public static final String NOUNS_LOCATION = "parsed/nouns/";

    private Config() { }

}
