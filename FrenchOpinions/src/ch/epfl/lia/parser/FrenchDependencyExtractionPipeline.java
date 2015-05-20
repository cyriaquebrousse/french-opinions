package ch.epfl.lia.parser;
import static ch.epfl.lia.main.Config.MALT_CONFIG_LOCATION_FR;
import static ch.epfl.lia.main.Config.NOT_JUNK_DEP_REGEXP;
import static ch.epfl.lia.util.FileUtils.foreachNonEmptyLine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.maltparser.concurrent.ConcurrentMaltParserModel;
import org.maltparser.concurrent.ConcurrentMaltParserService;
import org.maltparser.core.exception.MaltChainedException;

import ch.epfl.lia.entity.Language;
import ch.epfl.lia.nlp.Dependency;
import ch.epfl.lia.util.Preconditions;

/**
 * Extracts the Stanford dependencies from a given CoNLL-format input file.<br>
 * Uses the MALT parser for dependency extraction.<br>
 * Operates in three steps: sanitizing, parsing, extraction.
 * 
 * @author Cyriaque Brousse
 */
public final class FrenchDependencyExtractionPipeline implements DependencyExtractor {
    
    private final TreeAnalyzer analyzer;
    private final String conllInputFile;
    
    public FrenchDependencyExtractionPipeline(String conllInputFilePath, TreeAnalyzer analyzer) {
        Preconditions.throwIfEmptyString("cannot open null path", conllInputFilePath);
        Preconditions.throwIfNull("cannot construct pipeline with null analyzer", analyzer);
        
        this.conllInputFile = conllInputFilePath;
        this.analyzer = analyzer;
    }
    
    /**
     * Runs the pipeline
     * 
     * @return the list of extracted dependencies
     * @throws DependencyExtractionException
     */
    @Override
    public List<Dependency> extract() throws DependencyExtractionException {
        List<Dependency> dependencies;
        
        try {
            List<String> sanitizedTokens = getSanitizedTokens();
            List<String> parsedTokens = maltParse(sanitizedTokens);
            dependencies = extractStanfordDependencies(parsedTokens);
            
        } catch (IOException|MaltChainedException e) {
            throw new DependencyExtractionException(e);
        }
        
        return dependencies;
    }
    
    @Override
    public Language getLanguage() {
        return Language.FRENCH;
    }

    private List<String> getSanitizedTokens() throws IOException {
        Map<String, String> fineToCoarseGrainMap = initFineToCoarseGrainMap();
        List<String> sanitizedTokens = new ArrayList<>();
        
        foreachNonEmptyLine(conllInputFile, line -> {
            /* Match line against sanitizing pattern */
            line = line.replaceFirst("^((\\d+)\t(\\S+)\t_\t(\\S+)\t(\\S+)\t_).+", "$1");
            
            int i = 0;
            for (String fineGrainPOS : fineToCoarseGrainMap.keySet()) {
                
                if (line.matches("(.+)\t" + fineGrainPOS + "\t" + fineGrainPOS + "\t(.+)")) {
                    line = line.replaceFirst("\t" + fineGrainPOS + "\t",
                            "\t" + fineToCoarseGrainMap.get(fineGrainPOS) + "\t");
                    sanitizedTokens.add(line);
                    break;
                }
                
                if (line.matches("(.+)\tPUNC\tPUNC\t(.+)")) {
                    line = line.replaceAll("\tPUNC", "\tPONCT");
                    sanitizedTokens.add(line);
                    break;
                }
                
                if (i == fineToCoarseGrainMap.keySet().size() - 1) {
                    sanitizedTokens.add(line);
                    break;
                }
                
                i++;
            }
        });
        
        return sanitizedTokens;
    }

    /**
     * @return the fine-grain => coarse-grain map needed for the French format
     *         of the MaltParser
     */
    private Map<String, String> initFineToCoarseGrainMap() {
        Map<String, String> map = new HashMap<>();
        // adjectives
        map.put("ADJ", "A");
        map.put("ADJWH", "A");
        
        // adverbs
        map.put("ADVWH", "ADV");
        
        // clitic pronouns
        map.put("CLS", "CL");
        map.put("CLO", "CL");
        map.put("CLR", "CL");
        
        // determiners
        map.put("DET", "D");
        map.put("DETWH", "D");
        
        // nouns
        map.put("NC", "N");
        map.put("NPP", "N");
        
        // conjunctions
        map.put("CC", "C");
        map.put("CS", "C");
        
        // verbs
        map.put("VINF", "V");
        map.put("VPR", "V");
        map.put("VPP", "V");
        map.put("VIMP", "V");
        map.put("VS", "V");
        
        // pronouns
        map.put("PROREL", "PRO");
        
        // prepositions
        map.put("P+D", "P");
        map.put("P+PRO", "P");
        map.put("PROWH", "P");
        
        return map;
    }

    private List<String> maltParse(List<String> inputTokens) throws MaltChainedException, IOException {
        Preconditions.throwIfNullOrEmpty("input tokens list may not be null or empty", inputTokens);
        
        // Convert token list to array
        String[] inTokens = new String[inputTokens.size()];
        for (int i = 0; i < inTokens.length; i++) {
            inTokens[i] = inputTokens.get(i);
        }
        
        // Parse
        ConcurrentMaltParserModel model = ConcurrentMaltParserService.initializeParserModel(
                new File(MALT_CONFIG_LOCATION_FR));
        String[] parsedTokens = model.parseTokens(inTokens);
        
        List<String> outputTokens = new ArrayList<>();
        for (String parsed : parsedTokens) {
            outputTokens.add(parsed);
        }
        
        return outputTokens;
    }
    
    private List<Dependency> extractStanfordDependencies(List<String> parsedTokens) throws IOException {
        Preconditions.throwIfNullOrEmpty("parsed tokens list may not be null or empty", parsedTokens);
        
        List<Dependency> dependencies = new ArrayList<>();
        
        Pattern pattern = Pattern.compile("^(\\d+)\t(\\S+)\t_\t(\\S+)\t(\\S+)\t_\t(\\d+)\t(\\S+)\t.+");
        for (String line : parsedTokens) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            
            Matcher matcher = pattern.matcher(line);
            matcher.matches();
            final String reln = matcher.group(6);
            
            final String gov;
            final String dep = matcher.group(2);
            final int govId = Integer.parseInt(matcher.group(5));
            final int depId = Integer.parseInt(matcher.group(1));
            final String govPos;
            final String depPos;
            
            if (reln.equals("ponct") || reln.equals("root")) {
                continue;
            } else {
                gov = analyzer.leaves().get(govId - 1).value();
                
                /* Skip junk tokens, as defined in the configuration */
                if (!gov.matches(NOT_JUNK_DEP_REGEXP) || !dep.matches(NOT_JUNK_DEP_REGEXP)) {
                    continue;
                }
                
                govPos = analyzer.posTagOfWord(gov, govId - 1);
                depPos = analyzer.posTagOfWord(dep, depId - 1);
                
                /* Skip residual junk tokens */
                if (govPos == null || depPos == null) {
                    System.err.println("gros caca pourri");
                    continue;
                }
            }
            
            
            dependencies.add(new Dependency(reln, gov, govId, govPos, dep, depId, depPos));
        }
        
        return dependencies;
    }
}
