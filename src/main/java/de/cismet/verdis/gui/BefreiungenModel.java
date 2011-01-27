/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis.gui;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import de.cismet.verdis.data.BefreiungErlaubnis;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten.hell@cismet.de
 * @version  $Revision$, $Date$
 */
public class BefreiungenModel extends DefaultTableModel {

    //~ Instance fields --------------------------------------------------------

    Vector<BefreiungErlaubnis> data = new Vector<BefreiungErlaubnis>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BefreiungenModel object.
     */
    public BefreiungenModel() {
    }
    /**
     * Creates a new BefreiungenModel object.
     *
     * @param  data  DOCUMENT ME!
     */
    public BefreiungenModel(final Vector<BefreiungErlaubnis> data) {
        this.data = data;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public int getColumnCount() {
        return 2;
    }
    @Override
    public int getRowCount() {
        if (data != null) {
            return data.size();
        } else {
            return 0;
        }
    }
    @Override
    public String getColumnName(final int column) {
        if (column == 0) {
            return "Aktenzeichen";
        } else {
            return "g\u00FCltig bis";
        }
    }
    @Override
    public Object getValueAt(final int row, final int column) {
        final BefreiungErlaubnis be = data.get(row);
        if (column == 0) {
            return be.getAktenzeichen();
        } else {
            return be.getGueltigBis();
        }
    }
    @Override
    public void setValueAt(final Object value, final int row, final int column) {
        final BefreiungErlaubnis be = data.get(row);
        if (column == 0) {
            be.setAktenzeichen(value.toString());
        } else {
            be.setGueltigBis(value.toString());
        }
    }
    /**
     * DOCUMENT ME!
     */
    public void addRow() {
        data.add(new BefreiungErlaubnis());
        fireTableDataChanged();
    }
    /**
     * DOCUMENT ME!
     */
    public void removeAll() {
        data = new Vector<BefreiungErlaubnis>();
        fireTableDataChanged();
    }
}
