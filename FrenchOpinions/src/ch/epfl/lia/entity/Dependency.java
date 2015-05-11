package ch.epfl.lia.entity;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.epfl.lia.util.Preconditions;

/**
 * @author Cyriaque Brousse
 */
public class Dependency implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private static final Pattern PATTERN = Pattern.compile("^(.+)\\((.+)-(\\d+)'*,(.+)-(\\d+)'*\\)$");

    private final String reln;
    private final String gov;
    private final int govId;
    private final String dep;
    private final int depId;

    public Dependency(String raw) {
        Preconditions.throwIfEmptyString("raw string may not be empty", raw);
        
        Matcher matcher = PATTERN.matcher(raw);
        matcher.matches();
        reln = matcher.group(1);
        gov = matcher.group(2);
        govId = Integer.parseInt(matcher.group(3));
        dep = matcher.group(4);
        depId = Integer.parseInt(matcher.group(5));
    }

    public Dependency(String reln, String gov, int govId, String dep, int depId) {
        Preconditions.throwIfEmptyString("dependency members may not be empty", reln, gov, dep);
        if (govId < 0 || depId < 0) throw new IllegalArgumentException("govId and depId must be positive");
        
        this.reln = reln;
        this.gov = gov;
        this.dep = dep;
        this.govId = govId;
        this.depId = depId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Dependency other = (Dependency) obj;
        if (dep == null) {
            if (other.dep != null)
                return false;
        } else if (!dep.equals(other.dep))
            return false;
        if (depId != other.depId)
            return false;
        if (gov == null) {
            if (other.gov != null)
                return false;
        } else if (!gov.equals(other.gov))
            return false;
        if (govId != other.govId)
            return false;
        if (reln == null) {
            if (other.reln != null)
                return false;
        } else if (!reln.equals(other.reln))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dep == null) ? 0 : dep.hashCode());
        result = prime * result + depId;
        result = prime * result + ((gov == null) ? 0 : gov.hashCode());
        result = prime * result + govId;
        result = prime * result + ((reln == null) ? 0 : reln.hashCode());
        return result;
    }

    public String toString() {
        return reln + '(' + gov + '-' + govId + ',' + dep + '-' + depId + ')';
    }

    public String reln() {
        return reln;
    }

    public String gov() {
        return gov;
    }

    public String dep() {
        return dep;
    }

    public int govId() {
        return govId;
    }

    public int depId() {
        return depId;
    }
}
