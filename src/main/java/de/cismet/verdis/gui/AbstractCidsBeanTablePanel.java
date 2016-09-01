/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis.gui;

import javax.swing.JPanel;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public abstract class AbstractCidsBeanTablePanel extends JPanel {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AbstractCidsBeanTablePanel object.
     */
    public AbstractCidsBeanTablePanel() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public abstract AbstractCidsBeanTable getTable();
}
