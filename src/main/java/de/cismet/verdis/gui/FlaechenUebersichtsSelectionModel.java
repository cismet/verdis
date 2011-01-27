/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * FlaechenUebersichtsSelectionModel.java
 *
 * Created on 14. Januar 2005, 15:05
 */
package de.cismet.verdis.gui;
import javax.swing.*;

/**
 * DOCUMENT ME!
 *
 * @author   hell
 * @version  $Revision$, $Date$
 */
public class FlaechenUebersichtsSelectionModel extends DefaultListSelectionModel {

    //~ Instance fields --------------------------------------------------------

    private int oldSelection;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of FlaechenUebersichtsSelectionModel.
     */
    public FlaechenUebersichtsSelectionModel() {
        super();
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void setSelectionInterval(final int i0, final int i1) {
        super.setSelectionInterval(i0, i1);
        System.out.println(i0);
        oldSelection = i0;
    }
}
