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

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;

import java.util.Objects;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.verdis.CidsAppBackend;

import de.cismet.verdis.commons.constants.VerdisConstants;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class AenderungsanfrageTable extends JXTable {

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
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public AenderungsanfrageTableModel getModel() {
        return (AenderungsanfrageTableModel)super.getModel();
    }

    @Override
    public String getToolTipText(final MouseEvent e) {
        final Point p = e.getPoint();
        final int rowIndex = rowAtPoint(p);
        if (rowIndex >= 0) {
            final CidsBean aenderungsanfrageBean = getModel().getCidsBeanByIndex(convertRowIndexToModel(rowIndex));
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
     * @param   row  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getAenderungsanfrageBeanAtRow(final int row) {
        final int convertedRow = convertRowIndexToModel(row);
        final CidsBean aenderungsanfrageBean = getModel().getCidsBeanByIndex(
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
}
