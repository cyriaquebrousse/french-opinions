package ch.epfl.lia.opinion;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;

import ch.epfl.lia.entity.Article;
import ch.epfl.lia.entity.Opinion;
import ch.epfl.lia.util.Preconditions;

/**
 * Statistical evaluator of extracted opinions from articles
 * 
 * @author Cyriaque Brousse
 */
public final class Evaluator {
    
    private final Comparator<Article> articleScoreComparator = initArticleScoreComparator();
    
    private final Map<Article, Set<Opinion>> opsetMap;
    private final Map<Article, Integer> scoreMap;
    
    /* Some statistics */
    private Article mostPositiveArticle = null;
    private Article leastPositiveArticle = null;
    private Article biggestOpinionCount = null;
    private double averageOpinionCount = Float.NaN;
    
    private Evaluator(Map<Article, Set<Opinion>> opsetMap) {
        this.opsetMap = new HashMap<>(opsetMap);
        this.scoreMap = new HashMap<>();
        
        opsetMap.forEach((art, opset) -> {
            int score = computeOpsetScore(opset);
            scoreMap.put(art, score);
        });
    }
    
    /**
     * @param article
     * @return the score of the article (i.e. the sum of the scores of all its
     *         extracted opinions)
     */
    public int articleScore(Article article) {
        Preconditions.throwIfNull("article was null", article);
        
        return scoreMap.get(article);
    }
    
    /**
     * @return the article with the lowest score, or {@code null} if no opinions
     *         were extracted
     */
    public Article leastPositiveArticle() {
        if (leastPositiveArticle == null) {
            Optional<Article> someArticle = scoreMap.keySet().stream().min(articleScoreComparator);
            if (someArticle.isPresent()) {
                this.leastPositiveArticle = someArticle.get();
            }
        }
        
        return leastPositiveArticle;
    }
    
    /**
     * @return the article with the highest score, or {@code null} if no opinions
     *         were extracted
     */
    public Article mostPositiveArticle() {
        if (mostPositiveArticle == null) {
            Optional<Article> someArticle = scoreMap.keySet().stream().max(articleScoreComparator);
            if (someArticle.isPresent()) {
                this.mostPositiveArticle = someArticle.get();
            }
        }
        
        return mostPositiveArticle;
    }
    
    /**
     * @return the article with the highest opinion cardinality, or {@code null}
     *         if no opinions were extracted
     */
    public Article biggestOpinionCount() {
        if (biggestOpinionCount == null) {
            Optional<Article> someArticle = opsetMap.keySet().stream().max((_1, _2) -> {
                int count1 = opsetMap.get(_1).size();
                int count2 = opsetMap.get(_2).size();
                
                if (count1 < count2) {
                    return -1;
                } else if (count1 == count2) {
                    return 0;
                } else {
                    return 1;
                }
            });
            
            if (someArticle.isPresent()) {
                this.biggestOpinionCount = someArticle.get();
            }
        }
        
        return biggestOpinionCount;
    }
    
    /**
     * @param article
     * @return the number of opinions registered for this article
     */
    public int opinionCount(Article article) {
        Preconditions.throwIfNull("article was null", article);
        
        return opsetMap.get(article).size();
    }
    
    /**
     * @return the average number of extracted opinions, or {@link Double#NaN}
     *         if no opinions were extracted
     */
    public double averageOpinionCount() {
        if (Double.compare(averageOpinionCount, Double.NaN) == 0) {
            OptionalDouble optresult = opsetMap.keySet().stream().mapToInt(a -> opinionCount(a)).average();
            if (optresult.isPresent()) {
                this.averageOpinionCount = optresult.getAsDouble();
            }
        }
        
        return averageOpinionCount;
    }
    
    /**
     * @return a string representation of all available statistics
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("\n\n========= Statistics for this batch =========\n");
        
        // Least positive article
        final Article leastPositive = leastPositiveArticle();
        builder.append("Least positive article:\t" + leastPositive.id()
                + " (" + articleScore(leastPositive) + ")\n");
        
        // Most positive article
        final Article mostPositive = mostPositiveArticle();
        builder.append("Most positive article:\t" + mostPositive.id()
                + " (" + articleScore(mostPositive) + ")\n");
        
        // Article with the biggest amount of extracted opinions
        final Article biggestAmtOpinions = biggestOpinionCount();
        builder.append("Biggest amount of opinions:\t" + biggestAmtOpinions.id()
                + " (" + opinionCount(biggestAmtOpinions) + ")\n");
        
        // Average number of extracted opinions
        final double avg = averageOpinionCount();
        builder.append("Average number of extracted opinions:\t" + avg + "\n");
        
        return builder.toString();
    }
    
    /**
     * @return the score of this opinion set (i.e. the sum of the scores of all
     *         extracted opinions)
     */
    private int computeOpsetScore(Set<Opinion> set) {
        return set.stream().mapToInt(o -> o.polarity().score()).sum();
    }

    private Comparator<Article> initArticleScoreComparator() {
        return (_1, _2) -> {
            int score1 = scoreMap.get(_1);
            int score2 = scoreMap.get(_2);
            
            if (score1 < score2) {
                return -1;
            } else if (score1 == score2) {
                return 0;
            } else {
                return 1;
            }
        };
    }

    /**
     * Builder class for {@link Evaluator}. When finished adding articles and
     * their opinions to it via {@link #addOpinionsToArticle(Article, Set)}, get
     * the evaluator using {@link #build()}.
     * 
     * @author Cyriaque Brousse
     */
    public static class Builder {
        
        private final Map<Article, Set<Opinion>> map = new HashMap<>();
        
        /**
         * Register a possibly empty opinion set for the specified article
         * 
         * @param article
         * @param opset
         */
        public void addOpinionsToArticle(Article article, Set<Opinion> opset) {
            Preconditions.throwIfNull("article and opset must be provided", article, opset);
            
            map.put(article, new HashSet<>(opset));
        }
        
        /**
         * @return the built evaluator
         */
        public Evaluator build() {
            if (map.isEmpty()) {
                throw new NoSuchElementException("you must add articles and opsets before proceeding");
            }
            
            return new Evaluator(map);
        }
    }
}
