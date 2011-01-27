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
public class SummenTableCellRenderer extends DefaultTableCellRenderer {

    //~ Instance fields --------------------------------------------------------

    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of SummenTableCellRenderer.
     */
    public SummenTableCellRenderer() {
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
