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
}
