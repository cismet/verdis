/*
 * Storable.java
 *
 * Created on 21. Januar 2005, 12:46la
 */

package de.cismet.verdis.interfaces;

import de.cismet.validation.NotValidException;

/**
 *
 * @author hell
 */
public interface Storable {
   public boolean changesPending();
    public void enableEditing(boolean b);
    public boolean lockDataset();
    public void unlockDataset();
    public void addStoreChangeStatements(java.util.Vector v) throws NotValidException;
}
