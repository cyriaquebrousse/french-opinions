package ch.epfl.lia.util;

import java.io.Serializable;

/**
 * Models a tuple (pair of values)
 * @author Cyriaque Brousse
 */
public final class Tuple<X, Y> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final X _1;
    private final Y _2;
    
    public Tuple(X _1, Y _2) {
        this._1 = _1;
        this._2 = _2;
    }
    
    /**
     * @return the first member of the tuple
     */
    public X _1() {
        return _1;
    }
    
    /**
     * @return the second member of the tuple
     */
    public Y _2() {
        return _2;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Tuple<?, ?> other = (Tuple<?, ?>) obj;
        if (_1 == null) {
            if (other._1 != null)
                return false;
        } else if (!_1.equals(other._1))
            return false;
        if (_2 == null) {
            if (other._2 != null)
                return false;
        } else if (!_2.equals(other._2))
            return false;
        return true;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_1 == null) ? 0 : _1.hashCode());
        result = prime * result + ((_2 == null) ? 0 : _2.hashCode());
        return result;
    }
}
