package ch.epfl.lia.nlp;

import java.io.Serializable;

import ch.epfl.lia.util.Preconditions;

/**
 * @author Cyriaque Brousse
 */
public class Dependency implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private final String reln;
    private final Word gov;
    private final Word dep;

    public Dependency(String reln, String gov, int govId, String govPos, String dep, int depId, String depPos) {
        Preconditions.throwIfEmptyString("dependency members may not be empty", reln, gov, dep, govPos, depPos);
        if (govId < 0 || depId < 0) {
            throw new IllegalArgumentException("govId and depId must be positive");
        }
        
        this.reln = reln;
        this.gov = new Word(gov, govId, govPos);
        this.dep = new Word(dep, depId, depPos);
    }
    
    public Dependency(String reln, Word gov, Word dep) {
        Preconditions.throwIfEmptyString("reln may not be empty", reln);
        Preconditions.throwIfNull("dependency members may not be null", gov, dep);
        
        this.reln = reln;
        this.gov = gov;
        this.dep = dep;
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
        Dependency other = (Dependency) obj;
        if (dep == null) {
            if (other.dep != null) {
                return false;
            }
        } else if (!dep.equals(other.dep)) {
            return false;
        }
        if (gov == null) {
            if (other.gov != null) {
                return false;
            }
        } else if (!gov.equals(other.gov)) {
            return false;
        }
        if (reln == null) {
            if (other.reln != null) {
                return false;
            }
        } else if (!reln.equals(other.reln)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dep == null) ? 0 : dep.hashCode());
        result = prime * result + ((gov == null) ? 0 : gov.hashCode());
        result = prime * result + ((reln == null) ? 0 : reln.hashCode());
        return result;
    }
    

    public String toString() {
        return reln + '(' + gov + '-' + gov.id() + '-' + gov.posTag() + ','
                + dep + '-' + dep.id() + '-' + dep.posTag() + ')';
    }

    public String reln() {
        return reln;
    }

    public Word gov() {
        return gov;
    }

    public Word dep() {
        return dep;
    }

}
