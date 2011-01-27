/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * Validatable.java
 *
 * Created on 23. Januar 2005, 14:09
 */
package de.cismet.validation;

import java.awt.Component;

/**
 * DOCUMENT ME!
 *
 * @author   HP
 * @version  $Revision$, $Date$
 */
public interface Validatable {

    //~ Static fields/initializers ---------------------------------------------

    int VALID = 0;
    int WARNING = 1;
    int ERROR = 2;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  l  DOCUMENT ME!
     */
    void addValidationStateChangedListener(ValidationStateChangedListener l);
    /**
     * DOCUMENT ME!
     *
     * @param  l  DOCUMENT ME!
     */
    void removeValidationStateChangedListener(ValidationStateChangedListener l);
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    int getStatus();
    /**
     * DOCUMENT ME!
     *
     * @param  parent  DOCUMENT ME!
     */
    void showAssistent(Component parent);
    /**
     * public String getFormatExample(); public String getDescription();
     *
     * @return  DOCUMENT ME!
     */
    String getValidationMessage();
}
