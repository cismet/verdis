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

import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;

import java.sql.Timestamp;

import java.text.SimpleDateFormat;

import java.util.List;
import java.util.Objects;

import javax.swing.RowFilter;
import javax.swing.table.TableModel;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.verdis.CidsAppBackend;

import de.cismet.verdis.commons.constants.VerdisConstants;

import de.cismet.verdis.gui.AbstractCidsBeanTableModel;

import de.cismet.verdis.server.json.StacOptionsJson;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class AenderungsanfrageTable extends JXTable {

    //~ Static fields/initializers ---------------------------------------------

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    private static final transient Logger LOG = Logger.getLogger(AenderungsanfrageTable.class);

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

    private String filterUsername = null;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AenderungsanfrageTable object.
     */
    public AenderungsanfrageTable() {
        super(new AenderungsanfrageTableModel());

        final HighlightPredicate activeHighlightPredicate = new HighlightPredicate() {

                @Override
                public boolean isHighlighted(final Component renderer, final ComponentAdapter componentAdapter) {
                    final CidsBean aenderungsanfrageBean = getAenderungsanfrageBeanAtRow(componentAdapter.row);
                    final CidsBean activeAenderungsanfrageBean = AenderungsanfrageHandler.getInstance()
                                .getAenderungsanfrageBean();

                    return Objects.equals(aenderungsanfrageBean, activeAenderungsanfrageBean);
                }
            };

        final Highlighter activeHighlighter = new ColorHighlighter(
                activeHighlightPredicate,
                Color.ORANGE,
                Color.BLACK);

        setHighlighters(activeHighlighter);
        setRowFilter(new AenderungsanfrageRowFilter());
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private AenderungsanfrageTableModel getAenderungsanfrageTableModel() {
        return (AenderungsanfrageTableModel)super.getModel();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  filterUsername  DOCUMENT ME!
     */
    public void setFilterUsername(final String filterUsername) {
        this.filterUsername = filterUsername;
    }

    @Override
    public String getToolTipText(final MouseEvent e) {
        final Point p = e.getPoint();
        final int rowIndex = rowAtPoint(p);
        if (rowIndex >= 0) {
            final CidsBean aenderungsanfrageBean = getAenderungsanfrageTableModel().getCidsBeanByIndex(
                    convertRowIndexToModel(rowIndex));
            if (aenderungsanfrageBean != null) {
                return "Prüfkennzeichen: "
                            + AenderungsanfrageHandler.getInstance().getStacIdHash(aenderungsanfrageBean);
            }
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  aenderungsanfrageBeans  DOCUMENT ME!
     */
    public void setCidsBeans(final List<CidsBean> aenderungsanfrageBeans) {
        getAenderungsanfrageTableModel().setCidsBeans(aenderungsanfrageBeans);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   row  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getAenderungsanfrageBeanAtRow(final int row) {
        final int convertedRow = convertRowIndexToModel(row);
        final CidsBean aenderungsanfrageBean = getAenderungsanfrageTableModel().getCidsBeanByIndex(
                convertedRow);
        return aenderungsanfrageBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getSelectedAenderungsanfrageBean() {
        final int row = getSelectedRow();
        if (row < 0) {
            return null;
        } else {
            final CidsBean aenderungsanfrageBean = getAenderungsanfrageBeanAtRow(row);
            return aenderungsanfrageBean;
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void gotoSelectedKassenzeichen() {
        final CidsBean aenderungsanfrageBean = getSelectedAenderungsanfrageBean();
        if (aenderungsanfrageBean != null) {
            final String kassenzeichenPlusStacId = createKassenzeichenPlusStacId(aenderungsanfrageBean);
            CidsAppBackend.getInstance().gotoKassenzeichen(kassenzeichenPlusStacId);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   aenderungsanfrageBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String createKassenzeichenPlusStacId(final CidsBean aenderungsanfrageBean) {
        final Integer kassenzeichen = (Integer)aenderungsanfrageBean.getProperty(
                VerdisConstants.PROP.AENDERUNGSANFRAGE.KASSENZEICHEN_NUMMER);
        if (kassenzeichen != null) {
            final Integer stacId = (Integer)aenderungsanfrageBean.getProperty(
                    VerdisConstants.PROP.AENDERUNGSANFRAGE.STAC_ID);
            final String kassenzeichenPlusStacId = Integer.toString(kassenzeichen) + ";" + stacId;
            return kassenzeichenPlusStacId;
        } else {
            return null;
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class AenderungsanfrageRowFilter extends RowFilter<TableModel, Integer> {

        //~ Methods ------------------------------------------------------------

        @Override
        public boolean include(final RowFilter.Entry<? extends TableModel, ? extends Integer> entry) {
            if (filterUsername != null) {
                try {
                    final CidsBean aenderungsanfrageBean = getAenderungsanfrageTableModel().getCidsBeanByIndex(
                            entry.getIdentifier());
                    final StacOptionsJson stacOptions = CidsAppBackend.getInstance()
                                .getStacOptions((Integer)aenderungsanfrageBean.getProperty(
                                        VerdisConstants.PROP.AENDERUNGSANFRAGE.STAC_ID));
                    return (stacOptions != null) && filterUsername.equals(stacOptions.getCreatorUserName());
                } catch (final Exception ex) {
                    LOG.error(ex, ex);
                    return false;
                }
            } else {
                return true;
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static class AenderungsanfrageTableModel extends AbstractCidsBeanTableModel {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new AenderungsanfrageTableModel object.
         */
        public AenderungsanfrageTableModel() {
            super(COLUMN_NAMES, COLUMN_CLASSES);
        }

        //~ Methods ------------------------------------------------------------

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
                    return cidsBean.getProperty(VerdisConstants.PROP.AENDERUNGSANFRAGE.STATUS);
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
            return getCidsBeans().get(row);
        }

        @Override
        public boolean isCellEditable(final int row, final int column) {
            return false;
        }
    }
}
