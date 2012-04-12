/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis.gui;

import java.text.SimpleDateFormat;

import java.util.Date;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.verdis.constants.FortfuehrungPropertyConstants;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class FortfuehrungenTableModel extends CidsBeanTableModel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            FortfuehrungenTableModel.class);

    private static final String[] COLUMN_NAMES = {
            "Datum",
            "Art",
            ""
        };

    private static final Class[] COLUMN_CLASSES = {
            String.class,
            String.class,
            String.class
        };

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FortfuehrungenTableModel object.
     */
    public FortfuehrungenTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        final CidsBean fortfuehrungBean = getCidsBeanByIndex(rowIndex);
        if (fortfuehrungBean == null) {
            return null;
        }
        if (columnIndex == 0) {
            try {
                final Date date = (Date)fortfuehrungBean.getProperty(FortfuehrungPropertyConstants.PROP__BEGINN);
                if (date != null) {
                    return new SimpleDateFormat("dd.MM.yyyy").format(date);
                } else {
                    return "";
                }
            } catch (Exception e) {
                LOG.warn("exception in tablemodel", e);
                return "";
            }
        } else if (columnIndex == 1) {
            try {
                return (String)fortfuehrungBean.getProperty(FortfuehrungPropertyConstants.PROP__ANLASS_NAME);
            } catch (Exception e) {
                LOG.warn("exception in tablemodel", e);
                return "";
            }
        } else if (columnIndex == 2) {
            try {
                final String flurstueck_alt = (String)fortfuehrungBean.getProperty(
                        FortfuehrungPropertyConstants.PROP__FLURSTUECK_ALT);
                final String flurstueck_neu = (String)fortfuehrungBean.getProperty(
                        FortfuehrungPropertyConstants.PROP__FLURSTUECK_NEU);
                if (flurstueck_alt.equals(flurstueck_neu)) {
                    return flurstueck_alt;
                } else {
                    return flurstueck_alt + " => " + flurstueck_neu;
                }
            } catch (Exception e) {
                LOG.warn("exception in tablemodel", e);
                return "";
            }
        }
        return null;
    }
}
