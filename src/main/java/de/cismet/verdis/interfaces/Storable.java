/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * Storable.java
 *
 * Created on 21. Januar 2005, 12:46la
 */
package de.cismet.verdis.interfaces;

import de.cismet.validation.NotValidException;

/**
 * DOCUMENT ME!
 *
 * @author   hell
 * @version  $Revision$, $Date$
 */
public interface Storable {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean changesPending();
    /**
     * DOCUMENT ME!
     *
     * @param  b  DOCUMENT ME!
     */
    void enableEditing(boolean b);
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean lockDataset();
    /**
     * DOCUMENT ME!
     */
    void unlockDataset();
    /**
     * DOCUMENT ME!
     *
     * @param   v  DOCUMENT ME!
     *
     * @throws  NotValidException  DOCUMENT ME!
     */
    void addStoreChangeStatements(java.util.Vector v) throws NotValidException;
}
