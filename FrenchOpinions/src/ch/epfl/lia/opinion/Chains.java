package ch.epfl.lia.opinion;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ch.epfl.lia.entity.Chain;
import ch.epfl.lia.entity.Dependency;

/**
 * Collection of utility methods on dependency chains
 * 
 * @see Chain
 * @author Cyriaque Brousse
 */
public final class Chains {
    
    private Chains() { }
    
    /**
     * @param first
     *            the dependency to start with. It will thus be the first of all
     *            chains
     * @param allDeps
     *            the collection of all dependencies. <em>Note:</em> it may or
     *            may not contain {@code dep}. If it does, a set exclusion will
     *            first be performed.
     * @return a possibly empty set of chains, all starting from the provided
     *         dependency
     * @implNote returned chains are all of length 2
     */
    public static Set<Chain> allChainsFromFirst(Dependency first, Collection<Dependency> allDeps) {
        /* Set exclusion */
        Set<Dependency> dependencies = new HashSet<>(allDeps);
        dependencies.remove(first);
        
        /* Look for the dependencies that match the provided one,
         * that is we have something if the form dep(a,b):::dep(b,c)
         */
        List<Dependency> chainDeps = new LinkedList<>();
        allDeps.stream().filter(second -> 
                       first.dep().equals(second.gov())
                   &&  first.depId() == second.govId()
                   && !first.equals(second)
                ).forEach(d -> chainDeps.add(d));
        
        /* Construct chains: dep(a,b)#dep(b,c) */
        Set<Chain> chains = new HashSet<>();
        chainDeps.stream().forEach(second -> chains.add(new Chain(first, second)));
        
        return chains;
    }
    
}
