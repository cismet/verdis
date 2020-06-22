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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.swing.RowFilter;
import javax.swing.SwingWorker;
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
    private static final Map<CidsBean, CidsAppBackend.StacOptionsEntry> beanToStacEntryMap = new HashMap<>();

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

    private boolean filterUsername = false;
    private boolean filterKassenzeichen = false;
    private boolean filterActive = false;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AenderungsanfrageTable object.
     */
    public AenderungsanfrageTable() {
        super(new AenderungsanfrageTableModel());

        final HighlightPredicate selectedHighlightPredicate = new HighlightPredicate() {

                @Override
                public boolean isHighlighted(final Component renderer, final ComponentAdapter componentAdapter) {
                    return componentAdapter.isSelected();
                }
            };

        final HighlightPredicate activeHighlightPredicate = new HighlightPredicate() {

                @Override
                public boolean isHighlighted(final Component renderer, final ComponentAdapter componentAdapter) {
                    final CidsBean aenderungsanfrageBean = getAenderungsanfrageBeanAtRow(componentAdapter.row);
                    final CidsBean activeAenderungsanfrageBean = AenderungsanfrageHandler.getInstance()
                                .getAenderungsanfrageBean();

                    return Objects.equals(aenderungsanfrageBean, activeAenderungsanfrageBean);
                }
            };

        final Highlighter selectedHighlighter = new ColorHighlighter(
                selectedHighlightPredicate,
                new Color(207, 210, 221),
                Color.BLACK);

        final Highlighter activeHighlighter = new ColorHighlighter(
                activeHighlightPredicate,
                new Color(132, 162, 217),
                Color.WHITE);

        setHighlighters(selectedHighlighter, activeHighlighter);
        setRowFilter(new AenderungsanfrageRowFilter());
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    public void update() {
        ((AenderungsanfrageTableModel)getModel()).fireTableDataChanged();
    }

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
    public void setFilterUsername(final boolean filterUsername) {
        this.filterUsername = filterUsername;
        update();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  filterKassenzeichen  DOCUMENT ME!
     */
    public void setFilterKassenzeichen(final boolean filterKassenzeichen) {
        this.filterKassenzeichen = filterKassenzeichen;
        update();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  filterActive  DOCUMENT ME!
     */
    public void setFilterActive(final boolean filterActive) {
        this.filterActive = filterActive;
        update();
    }

    @Override
    public String getToolTipText(final MouseEvent e) {
//        final Point p = e.getPoint();
//        final int rowIndex = rowAtPoint(p);
//        if (rowIndex >= 0) {
//            final CidsBean aenderungsanfrageBean = getAenderungsanfrageTableModel().getCidsBeanByIndex(
//                    convertRowIndexToModel(rowIndex));
//            if (aenderungsanfrageBean != null) {
//                return "Prüfkennzeichen: "
//                            + AenderungsanfrageHandler.getInstance().getStacIdHash(aenderungsanfrageBean);
//            }
//        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  aenderungsanfrageBeans  DOCUMENT ME!
     */
    public void setCidsBeans(final List<CidsBean> aenderungsanfrageBeans) {
        getAenderungsanfrageTableModel().setCidsBeans(aenderungsanfrageBeans);
        new SwingWorker<Map<CidsBean, CidsAppBackend.StacOptionsEntry>, Void>() {

                @Override
                protected Map<CidsBean, CidsAppBackend.StacOptionsEntry> doInBackground() throws Exception {
                    final Map<CidsBean, CidsAppBackend.StacOptionsEntry> beanToStacEntryMap = new HashMap<>();

                    for (final CidsBean aenderungsanfrageBean : aenderungsanfrageBeans) {
                        final CidsAppBackend.StacOptionsEntry entry = CidsAppBackend.getInstance()
                                    .getStacOptionsEntry((Integer)aenderungsanfrageBean.getProperty(
                                            VerdisConstants.PROP.AENDERUNGSANFRAGE.STAC_ID));
                        beanToStacEntryMap.put(aenderungsanfrageBean, entry);
                    }
                    return beanToStacEntryMap;
                }

                @Override
                protected void done() {
                    try {
                        AenderungsanfrageTable.this.beanToStacEntryMap.clear();
                        AenderungsanfrageTable.this.beanToStacEntryMap.putAll(get());
                    } catch (final Exception ex) {
                    }
                    update();
                }
            }.execute();
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
            final String username = SessionManager.getSession().getUser().getName();
            final Integer kassenzeichenNummer = (CidsAppBackend.getInstance().getCidsBean() != null)
                ? (Integer)CidsAppBackend.getInstance().getCidsBean()
                        .getProperty(VerdisConstants.PROP.KASSENZEICHEN.KASSENZEICHENNUMMER) : null;
            final Timestamp now = new Timestamp(new Date().getTime());

            final CidsBean aenderungsanfrageBean = getAenderungsanfrageTableModel().getCidsBeanByIndex(
                    entry.getIdentifier());
            final CidsAppBackend.StacOptionsEntry stacEntry = beanToStacEntryMap.get(aenderungsanfrageBean);
            boolean show = true;

            if (filterUsername) {
                final StacOptionsJson stacOptions = (stacEntry != null) ? stacEntry.getStacOptionsJson() : null;
                show &= (stacOptions != null) && Objects.equals(username, stacOptions.getCreatorUserName());
            }
            if (filterKassenzeichen) {
                show &= Objects.equals(
                        kassenzeichenNummer,
                        aenderungsanfrageBean.getProperty(VerdisConstants.PROP.AENDERUNGSANFRAGE.KASSENZEICHEN_NUMMER));
            }
            if (filterActive) {
                final Timestamp timestamp = (stacEntry != null) ? stacEntry.getTimestamp() : null;
                show &= (timestamp != null) && timestamp.after(now);
            }

            return show;
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
                    final CidsAppBackend.StacOptionsEntry stacEntry = beanToStacEntryMap.get(aenderungsanfrageBean);
                    final StacOptionsJson stacOptions = (stacEntry != null) ? stacEntry.getStacOptionsJson() : null;
                    return (stacOptions != null)
                        ? ((!SessionManager.getSession().getUser().getName().equals(stacOptions.getCreatorUserName()))
                            ? stacOptions.getCreatorUserName() : "ich") : null;
                }
                case 2: {
                    final CidsAppBackend.StacOptionsEntry stacEntry = beanToStacEntryMap.get(aenderungsanfrageBean);
                    final Timestamp now = new Timestamp(new Date().getTime());
                    final Timestamp timestamp = (stacEntry != null) ? stacEntry.getTimestamp() : null;
                    if ((timestamp != null) && timestamp.after(now)) {
                        return Objects.toString(aenderungsanfrageBean.getProperty(
                                    VerdisConstants.PROP.AENDERUNGSANFRAGE.STATUS));
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
}
