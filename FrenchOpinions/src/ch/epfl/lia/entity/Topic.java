package ch.epfl.lia.entity;

import java.util.LinkedList;
import java.util.List;

import ch.epfl.lia.util.Preconditions;

/**
 * @author Cyriaque Brousse
 */
public class Topic implements Entity {

    private static final long serialVersionUID = 1L;
    
    private final int lineId;
    private final double polarity;
    private final List<String> keys;
    
    public Topic(int lineId, double polarity, List<String> keys) {
        Preconditions.throwIfNullOrEmpty("key list may not be null or empty", keys);
        if (lineId < 0) throw new IllegalArgumentException("line id must be positive");
        
        this.lineId = lineId;
        this.polarity = polarity;
        this.keys = new LinkedList<>(keys);
    }
    
    /**
     * Copy constructor
     * 
     * @param topic
     *            the topic to copy
     */
    public Topic(Topic topic) {
        this.lineId = topic.lineId;
        this.polarity = topic.polarity;
        this.keys = new LinkedList<>(topic.keys);
    }
    
    public int lineId() {
        return lineId;
    }
    
    public double polarity() {
        return polarity;
    }
    
    public List<String> keys() {
        return new LinkedList<>(keys);
    }

    @Override
    public int save() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void delete() {
        // TODO Auto-generated method stub
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
    }
    
    @Override
    public String toString() {
        return "{" + lineId + "}" + keys.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((keys == null) ? 0 : keys.hashCode());
        result = prime * result + lineId;
        long temp;
        temp = Double.doubleToLongBits(polarity);
        result = prime * result + (int) (temp ^ (temp >>> 32));
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
        Topic other = (Topic) obj;
        if (keys == null) {
            if (other.keys != null)
                return false;
        } else if (!keys.equals(other.keys))
            return false;
        if (lineId != other.lineId)
            return false;
        if (Double.doubleToLongBits(polarity) != Double
                .doubleToLongBits(other.polarity))
            return false;
        return true;
    }
    
}
