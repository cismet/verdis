/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * SummenTableCellRenderer.java
 *
 * Created on 7. Januar 2005, 10:32
 */
package de.cismet.verdis.gui;
import java.awt.Component;

import javax.swing.*;
import javax.swing.table.*;
/**
 * DOCUMENT ME!
 *
 * @author   hell
 * @version  $Revision$, $Date$
 */
public class RegenFlaechenSummenTableCellRenderer extends DefaultTableCellRenderer {

    //~ Instance fields --------------------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RegenFlaechenSummenTableCellRenderer.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of SummenTableCellRenderer.
     */
    public RegenFlaechenSummenTableCellRenderer() {
        super();
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Component getTableCellRendererComponent(final JTable table,
            final Object value,
            final boolean isSelected,
            final boolean hasFocus,
            final int row,
            final int column) {
        final JLabel label = new JLabel(value.toString());
        if (column == 1) {
            label.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        } else {
            label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        }
        return label;
    }
}
