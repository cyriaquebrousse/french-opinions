package ch.epfl.lia.entity;

import java.io.Serializable;

/**
 * Models an entity
 * 
 * @author Cyriaque Brousse
 */
public interface Entity extends Serializable {
    /**
     * Saves the entity (i.e. to a filesystem or a database)
     * 
     * @return the id the entity was saved with
     */
    int save();
    
    /**
     * Deletes the entity (i.e. from a filesystem or a database)
     */
    void delete();
    
    /**
     * Optional, local operation.<br>
     * Clears the fields of the entity in order to free local space
     */
    void clear();
}
