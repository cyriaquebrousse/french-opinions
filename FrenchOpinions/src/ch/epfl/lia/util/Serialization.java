package ch.epfl.lia.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author Cyriaque Brousse
 */
public final class Serialization {
    
    private Serialization() { }
    
    public static void serialize(Object obj, String path) {
        Preconditions.throwIfNull("no object was provided", obj);
        Preconditions.throwIfEmptyString("no path was provided", path);
        
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
            oos.writeObject(obj);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static Object deserialize(String path) {
        Preconditions.throwIfEmptyString("no path was provided", path);
        
        Object obj = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
            obj = ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
