package ch.epfl.lia.util;

import java.io.Serializable;

/**
 * Models a triplet (three elements together)
 * @author Cyriaque Brousse
 */
public final class Triplet<X, Y, Z> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private final X _1;
    private final Y _2;
    private final Z _3;
    
    public Triplet(X _1, Y _2, Z _3) {
        this._1 = _1;
        this._2 = _2;
        this._3 = _3;
    }
    
    /**
     * @return the first member of the triplet
     */
    public X _1() {
        return _1;
    }
    
    /**
     * @return the second member of the triplet
     */
    public Y _2() {
        return _2;
    }
    
    /**
     * @return the third member of the triplet
     */
    public Z _3() {
        return _3;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_1 == null) ? 0 : _1.hashCode());
        result = prime * result + ((_2 == null) ? 0 : _2.hashCode());
        result = prime * result + ((_3 == null) ? 0 : _3.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Triplet<?, ?, ?> other = (Triplet<?, ?, ?>) obj;
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
        if (_3 == null) {
            if (other._3 != null)
                return false;
        } else if (!_3.equals(other._3))
            return false;
        return true;
    }
    
}
