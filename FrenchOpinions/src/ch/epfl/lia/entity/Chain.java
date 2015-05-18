package ch.epfl.lia.entity;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import ch.epfl.lia.nlp.Dependency;
import ch.epfl.lia.util.Preconditions;

/**
 * Represents a chain of grammatical dependencies.<br>
 * <p>
 * Invariants:<br>
 * (1) each dependent of the first dependency must be the governor of the second
 * one, and<br>
 * (2) no duplicate words are allowed (a chain may not go through a node more
 * than once).
 * </p>
 * 
 * @author Cyriaque Brousse
 */
public class Chain {

    private final List<Dependency> dependencies = new LinkedList<>();
    private final List<Integer> wordIds = new LinkedList<>();
    
    public Chain(Dependency... dependencies) {
        if (dependencies == null || dependencies.length == 0) {
            throw new NullPointerException("cannot create from null or empty array");
        }
        
        List<Dependency> depList = Arrays.asList(dependencies);
        if (!checkChainInvariants(depList)) {
            throw new IllegalArgumentException("chain invariants do not hold");
        }

        this.dependencies.addAll(depList);
        this.wordIds.addAll(wordIdListFromDependencies(depList));
    }
    
    public Chain(List<Dependency> dependencies) {
        Preconditions.throwIfNullOrEmpty("cannot create from null or empty list", dependencies);
        if (!checkChainInvariants(dependencies)) {
            throw new IllegalArgumentException("chain invariants do not hold");
        }
        
        this.dependencies.addAll(dependencies);
        this.wordIds.addAll(wordIdListFromDependencies(dependencies));
    }
    
    /**
     * Copy constructor
     * 
     * @param chain
     *            the chain to copy
     */
    public Chain(Chain chain) {
        Preconditions.throwIfNull("cannot create from null object", chain);
        
        this.dependencies.addAll(chain.dependencies);
        this.wordIds.addAll(chain.wordIds);
    }
    
    /**
     * Appends a dependency to this chain
     * 
     * @param dep
     *            dependency to append
     * @return the immutable new chain
     */
    public Chain append(Dependency dep) {
        Preconditions.throwIfNull("must append a non-null object", dep);
        final Dependency last = dependencies.get(dependencies.size() - 1);
        if (!checkChainInvariants(last, dep)) {
            throw new IllegalArgumentException("chain invariant violated for " + dep);
        }
        
        List<Dependency> dependencies = new LinkedList<>(this.dependencies);
        dependencies.add(dep);
        
        return new Chain(dependencies);
    }
    
    /**
     * @return the type of the dependency chain. The format is as follows:
     *         {@code typeDep1#typeDep2#...#typeDepN}.
     */
    public String type() {
        StringBuilder builder = new StringBuilder();
        
        dependencies.forEach(d -> builder.append(d.reln() + '#'));
        
        builder.deleteCharAt(builder.lastIndexOf("#"));
        return builder.toString();
    }
    
    public List<Dependency> dependencies() {
        return new LinkedList<>(dependencies);
    }
    
    /**
     * @return the first dependency of the chain
     */
    public Dependency first() {
        assert !dependencies.isEmpty();
        return dependencies.get(0);
    }
    
    /**
     * @return the last dependency of the chain
     */
    public Dependency last() {
        assert !dependencies.isEmpty();
        return dependencies.get(dependencies.size() - 1);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((dependencies == null) ? 0 : dependencies.hashCode());
        result = prime * result + ((wordIds == null) ? 0 : wordIds.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Chain other = (Chain) obj;
        if (dependencies == null) {
            if (other.dependencies != null) {
                return false;
            }
        } else if (!dependencies.equals(other.dependencies)) {
            return false;
        }
        if (wordIds == null) {
            if (other.wordIds != null) {
                return false;
            }
        } else if (!wordIds.equals(other.wordIds)) {
            return false;
        }
        return true;
    }

    /**
     * @param first
     *            first member of the couple (first,second)
     * @param second
     *            second member of the couple (first,second)
     * @return {@code true} if and only if the dependent word of the first
     *         dependency is the same as the governing word of the second, AND
     *         if the three words of the chain composed by the two dependencies
     *         are not already contained in the global chain. Otherwise, it
     *         returns {@false}
     */
    private boolean checkChainInvariants(Dependency first, Dependency second) {
        return first.dep().id() == second.gov().id()
                && !wordIds.contains(first.gov().id())
                && !wordIds.contains(first.dep().id())
                && !wordIds.contains(second.dep().id());
    }
    
    /**
     * @param dependencies
     *            list of dependencies to check
     * @see #checkChainInvariants(Dependency, Dependency)
     * @return {@code true} if the invariants do hold for all dependencies,
     *         {@code false} otherwise
     */
    private boolean checkChainInvariants(List<Dependency> dependencies) {
        /* Checking for each pair (first,second) */
        for (int i = 0; i < dependencies.size() - 1; ++i) {
            if (!checkChainInvariants(dependencies.get(i), dependencies.get(i + 1))) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * @param dependencies
     * @return the word id list constructed from the dependency list
     */
    private List<Integer> wordIdListFromDependencies(List<Dependency> dependencies) {
        List<Integer> wordIds = new LinkedList<>();
        Dependency last = dependencies.get(dependencies.size() - 1);
        
        for (Dependency dep : dependencies) {
            wordIds.add(dep.gov().id());
            if (dep == last) {
                wordIds.add(dep.dep().id());
            }
        }
        
        return wordIds;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        
        for (Dependency dep : dependencies) {
            builder.append(dep + "#");
        }
        
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }
}
