/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.verdis.gui.aenderungsanfrage;

import Sirius.navigator.connection.SessionManager;

import java.sql.Timestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.verdis.CidsAppBackend;

import de.cismet.verdis.commons.constants.VerdisConstants;

import de.cismet.verdis.gui.AbstractCidsBeanTableModel;

import de.cismet.verdis.server.utils.AenderungsanfrageUtils;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class AenderungsanfrageTableModel extends AbstractCidsBeanTableModel {

    //~ Static fields/initializers ---------------------------------------------

    private static final String[] COLUMN_NAMES = {
            "Kassenzeichen",
            "Bearbeiter",
            "Status",
            "Letzte Änderung",
            "gültig bis"
        };

    private static final Class[] COLUMN_CLASSES = {
            String.class,
            String.class,
            String.class,
            Timestamp.class,
            Timestamp.class
        };

    //~ Instance fields --------------------------------------------------------

    private final Map<CidsBean, CidsAppBackend.StacOptionsEntry> beanToStacEntryMap = new HashMap<>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AenderungsanfrageTableModel object.
     */
    public AenderungsanfrageTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Map<CidsBean, CidsAppBackend.StacOptionsEntry> getBeanToStacEntryMap() {
        return beanToStacEntryMap;
    }

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        final CidsBean aenderungsanfrageBean = getCidsBeanByIndex(rowIndex);
        if (aenderungsanfrageBean == null) {
            return null;
        }
        switch (columnIndex) {
            case 0: {
                return
                    (aenderungsanfrageBean.getProperty(VerdisConstants.PROP.AENDERUNGSANFRAGE.KASSENZEICHEN_NUMMER)
                                != null)
                    ? Integer.toString((Integer)aenderungsanfrageBean.getProperty(
                            VerdisConstants.PROP.AENDERUNGSANFRAGE.KASSENZEICHEN_NUMMER)) : null;
            }
            case 1: {
                final String clerkUsername = (String)aenderungsanfrageBean.getProperty(
                        VerdisConstants.PROP.AENDERUNGSANFRAGE.CLERK_USERNAME);
                return
                    (((clerkUsername != null)
                                    && (SessionManager.getSession().getUser().getName().equals(clerkUsername)))
                        ? "ich" : "*****");
            }
            case 2: {
                final CidsAppBackend.StacOptionsEntry stacEntry = beanToStacEntryMap.get(aenderungsanfrageBean);
                final Timestamp now = new Timestamp(new Date().getTime());
                final Timestamp timestamp = (stacEntry != null) ? stacEntry.getTimestamp() : null;
                if (
                    AenderungsanfrageUtils.Status.ARCHIVED.toString().equals(
                                aenderungsanfrageBean.getProperty(
                                    VerdisConstants.PROP.AENDERUNGSANFRAGE.STATUS
                                    + "."
                                    + VerdisConstants.PROP.AENDERUNGSANFRAGE_STATUS.SCHLUESSEL))
                            || ((timestamp != null) && timestamp.after(now))) {
                    return Objects.toString(aenderungsanfrageBean.getProperty(
                                VerdisConstants.PROP.AENDERUNGSANFRAGE.STATUS
                                        + "."
                                        + VerdisConstants.PROP.AENDERUNGSANFRAGE_STATUS.NAME));
                } else {
                    return "abgelaufen";
                }
            }
            case 3: {
                final Timestamp timestamp = (Timestamp)aenderungsanfrageBean.getProperty(
                        VerdisConstants.PROP.AENDERUNGSANFRAGE.TIMESTAMP);
                return timestamp;
            }
            case 4: {
                final CidsAppBackend.StacOptionsEntry stacEntry = beanToStacEntryMap.get(aenderungsanfrageBean);
                return (stacEntry != null) ? stacEntry.getTimestamp() : null;
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
        return getCidsBeans().get(row);
    }

    @Override
    public boolean isCellEditable(final int row, final int column) {
        return false;
    }

    @Override
    public Class getColumnClass(final int column) {
        return COLUMN_CLASSES[column];
    }
}
