/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis.gui.aenderungsanfrage;

import java.sql.Timestamp;

import java.text.SimpleDateFormat;

import java.util.List;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.verdis.commons.constants.VerdisConstants;

import de.cismet.verdis.gui.AbstractCidsBeanTableModel;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class AenderungsanfrageTableModel extends AbstractCidsBeanTableModel {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            AenderungsanfrageTableModel.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    private static final String[] COLUMN_NAMES = {
            "Kassenzeichen",
            "Status",
            "Letzte Änderung"
        };

    private static final Class[] COLUMN_CLASSES = {
            String.class,
            String.class,
            java.util.Date.class
        };

    //~ Instance fields --------------------------------------------------------

    private List<CidsBean> aenderungsanfragen;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AenderungsanfrageTableModel object.
     */
    public AenderungsanfrageTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        final CidsBean cidsBean = getCidsBeanByIndex(rowIndex);
        if (cidsBean == null) {
            return null;
        }
        switch (columnIndex) {
            case 0: {
                return (cidsBean.getProperty(VerdisConstants.PROP.AENDERUNGSANFRAGE.KASSENZEICHEN_NUMMER) != null)
                    ? Integer.toString((Integer)cidsBean.getProperty(
                            VerdisConstants.PROP.AENDERUNGSANFRAGE.KASSENZEICHEN_NUMMER)) : null;
            }
            case 1: {
                return (String)cidsBean.getProperty(VerdisConstants.PROP.AENDERUNGSANFRAGE.STATUS);
            }
            case 2: {
                final Timestamp timestamp = (Timestamp)cidsBean.getProperty(
                        VerdisConstants.PROP.AENDERUNGSANFRAGE.TIMESTAMP);
                return (timestamp != null) ? DATE_FORMAT.format(timestamp) : timestamp;
            }
            default: {
                return null;
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   row  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getAenderungsanfrageAt(final int row) {
        return aenderungsanfragen.get(row);
    }

    @Override
    public boolean isCellEditable(final int row, final int column) {
        return false;
    }
}
