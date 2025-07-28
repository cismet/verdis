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
import java.awt.event.MouseEvent;

import java.sql.Timestamp;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.verdis.CidsAppBackend;

import de.cismet.verdis.commons.constants.VerdisConstants;

import de.cismet.verdis.server.json.AenderungsanfrageJson;
import de.cismet.verdis.server.json.NachrichtJson;
import de.cismet.verdis.server.utils.AenderungsanfrageUtils;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class AenderungsanfrageTable extends JXTable {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(AenderungsanfrageTable.class);

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
                    final CidsBean activeAenderungsanfrageBean = AenderungsanfrageHandler.getInstance().getCidsBean();

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
        SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    try {
                        ((AenderungsanfrageTableModel)getModel()).fireTableDataChanged();
                    } catch (final Exception ex) {
                        LOG.warn("could not update AenderungsanfrageTableModel", ex);
                    }
                }
            });
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
     * @param  beanToStacEntryMap  DOCUMENT ME!
     */
    public void setCidsBeans(final Map<CidsBean, CidsAppBackend.StacOptionsEntry> beanToStacEntryMap) {
        final CidsBean selectedBean = getSelectedAenderungsanfrageBean();

        getAenderungsanfrageTableModel().getBeanToStacEntryMap().clear();
        getAenderungsanfrageTableModel().getBeanToStacEntryMap().putAll(beanToStacEntryMap);
        getAenderungsanfrageTableModel().setCidsBeans(Arrays.asList(
                beanToStacEntryMap.keySet().toArray(new CidsBean[0])));

        setSelectedAenderungsanfrageBean(selectedBean);
        update();
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
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    public void setSelectedAenderungsanfrageBean(final CidsBean cidsBean) {
        getSelectionModel().clearSelection();
        final int index = getAenderungsanfrageTableModel().getIndexByCidsBean(cidsBean);
        getSelectionModel().addSelectionInterval(index, index);
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
            boolean show = true;

            try {
                final AenderungsanfrageJson aenderungsanfrage = AenderungsanfrageUtils.stripAenderungsanfrageFor(
                        getAenderungsanfrageTableModel().getAenderungsanfrage(aenderungsanfrageBean),
                        NachrichtJson.Typ.CLERK);
                show &= (aenderungsanfrage != null)
                            && (((aenderungsanfrage.getNachrichten() != null)
                                    && !aenderungsanfrage.getNachrichten().isEmpty())
                                || ((aenderungsanfrage.getFlaechen() != null)
                                    && !aenderungsanfrage.getFlaechen().isEmpty()));
            } catch (final Exception ex) {
                LOG.error(ex, ex);
                show = false;
            }

            if (filterUsername) {
                final String clerkUsername = (String)aenderungsanfrageBean.getProperty(
                        VerdisConstants.PROP.AENDERUNGSANFRAGE.CLERK_USERNAME);
                show &= (clerkUsername != null) && Objects.equals(username, clerkUsername);
            }
            if (filterKassenzeichen) {
                show &= Objects.equals(
                        kassenzeichenNummer,
                        aenderungsanfrageBean.getProperty(VerdisConstants.PROP.AENDERUNGSANFRAGE.KASSENZEICHEN_NUMMER));
            }
            if (filterActive) {
                final String status = (String)aenderungsanfrageBean.getProperty(
                        VerdisConstants.PROP.AENDERUNGSANFRAGE.STATUS
                                + "."
                                + VerdisConstants.PROP.AENDERUNGSANFRAGE_STATUS.SCHLUESSEL);
                show &= !Objects.equals(
                        status,
                        AenderungsanfrageUtils.Status.ARCHIVED.toString());
            }

            return show;
        }
    }
}
