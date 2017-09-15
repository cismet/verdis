/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 *  Copyright (C) 2010 thorsten
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * KartenPanel.java
 *
 * Created on 24.11.2010, 20:42:05
 */
package de.cismet.verdis.gui;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.linemerge.LineMerger;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolox.event.PNotification;

import org.openide.util.Lookup;

import java.awt.Component;
import java.awt.Event;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.*;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.commons.searchgeometrylistener.BaulastblattNodesSearchCreateSearchGeometryListener;
import de.cismet.cids.custom.commons.searchgeometrylistener.FlurstueckNodesSearchCreateSearchGeometryListener;
import de.cismet.cids.custom.commons.searchgeometrylistener.RissNodesSearchCreateSearchGeometryListener;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;

import de.cismet.cismap.commons.RetrievalServiceLayer;
import de.cismet.cismap.commons.ServiceLayer;
import de.cismet.cismap.commons.features.*;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.ToolbarComponentDescription;
import de.cismet.cismap.commons.gui.ToolbarComponentsProvider;
import de.cismet.cismap.commons.gui.piccolo.AngleMeasurementDialog;
import de.cismet.cismap.commons.gui.piccolo.PFeature;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.*;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.actions.CustomAction;
import de.cismet.cismap.commons.interaction.CismapBroker;
import de.cismet.cismap.commons.interaction.memento.MementoInterface;
import de.cismet.cismap.commons.rasterservice.MapService;
import de.cismet.cismap.commons.retrieval.RetrievalEvent;
import de.cismet.cismap.commons.retrieval.RetrievalListener;

import de.cismet.cismap.navigatorplugin.BeanUpdatingCidsFeature;
import de.cismet.cismap.navigatorplugin.CidsFeature;

import de.cismet.cismap.tools.gui.CidsBeanDropJPopupMenuButton;

import de.cismet.gui.tools.PureNewFeatureWithThickerLineString;

import de.cismet.tools.CurrentStackTrace;
import de.cismet.tools.StaticDecimalTools;

import de.cismet.tools.gui.BasicGuiComponentProvider;
import de.cismet.tools.gui.JPopupMenuButton;
import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.historybutton.JHistoryButton;

import de.cismet.verdis.AppModeListener;
import de.cismet.verdis.CidsAppBackend;
import de.cismet.verdis.CrossReference;
import de.cismet.verdis.EditModeListener;

import de.cismet.verdis.commons.constants.*;

import de.cismet.verdis.gui.regenflaechen.RegenFlaechenDetailsPanel;
import de.cismet.verdis.gui.srfronten.SRFrontenDetailsPanel;

import de.cismet.verdis.search.ServerSearchCreateSearchGeometryListener;

import de.cismet.verdis.server.search.KassenzeichenGeomSearch;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class KartenPanel extends javax.swing.JPanel implements FeatureCollectionListener,
    RetrievalListener,
    Observer,
    CidsBeanStore,
    EditModeListener,
    AppModeListener,
    PropertyChangeListener {

    //~ Instance fields --------------------------------------------------------

    private final HashSet activeRetrievalServices = new HashSet();
    private CidsBean kassenzeichenBean = null;
    private final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KartenPanel.class);
    private boolean isAssignLandparcel = false;
    private Action searchAction = new AbstractAction() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                java.awt.EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("searchAction"); // NOI18N
                            }
                            EventQueue.invokeLater(new Runnable() {

                                    @Override
                                    public void run() {
                                        mappingComp.setInteractionMode(Main.KASSENZEICHEN_SEARCH_GEOMETRY_LISTENER);

                                        if (mniSearchRectangle1.isSelected()) {
                                            mainGroup.clearSelection();
                                            cmdSearchKassenzeichen.setSelected(true);
                                            ((ServerSearchCreateSearchGeometryListener)mappingComp.getInputListener(
                                                    Main.KASSENZEICHEN_SEARCH_GEOMETRY_LISTENER)).setMode(
                                                CreateGeometryListener.RECTANGLE);
                                        } else if (mniSearchPolygon1.isSelected()) {
                                            mainGroup.clearSelection();
                                            cmdSearchKassenzeichen.setSelected(true);
                                            ((ServerSearchCreateSearchGeometryListener)mappingComp.getInputListener(
                                                    Main.KASSENZEICHEN_SEARCH_GEOMETRY_LISTENER)).setMode(
                                                CreateGeometryListener.POLYGON);
                                        } else if (mniSearchEllipse1.isSelected()) {
                                            mainGroup.clearSelection();
                                            cmdSearchKassenzeichen.setSelected(true);
                                            ((ServerSearchCreateSearchGeometryListener)mappingComp.getInputListener(
                                                    Main.KASSENZEICHEN_SEARCH_GEOMETRY_LISTENER)).setMode(
                                                CreateGeometryListener.ELLIPSE);
                                        }
                                    }
                                });
                        }
                    });
            }
        };

    private Action searchRectangleAction = new AbstractAction() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                java.awt.EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("searchRectangleAction");                                                          // NOI18N
                            }
                            cmdSearchKassenzeichen.setIcon(
                                new javax.swing.ImageIcon(
                                    getClass().getResource(
                                        "/de/cismet/verdis/res/images/toolbar/kassenzeichenSearchRectangle.png")));          // NOI18N
                            cmdSearchKassenzeichen.setSelectedIcon(
                                new javax.swing.ImageIcon(
                                    getClass().getResource(
                                        "/de/cismet/verdis/res/images/toolbar/kassenzeichenSearchRectangle_selected.png"))); // NOI18N

                            mainGroup.clearSelection();
                            cmdSearchKassenzeichen.setSelected(true);

                            EventQueue.invokeLater(new Runnable() {

                                    @Override
                                    public void run() {
                                        mappingComp.setInteractionMode(Main.KASSENZEICHEN_SEARCH_GEOMETRY_LISTENER);
                                        ((ServerSearchCreateSearchGeometryListener)mappingComp.getInputListener(
                                                Main.KASSENZEICHEN_SEARCH_GEOMETRY_LISTENER)).setMode(
                                            ServerSearchCreateSearchGeometryListener.RECTANGLE);
                                    }
                                });
                        }
                    });
            }
        };

    private Action searchPolygonAction = new AbstractAction() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                java.awt.EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("searchPolygonAction"); // NOI18N
                            }
                            mainGroup.clearSelection();
                            cmdSearchKassenzeichen.setSelected(true);

                            cmdSearchKassenzeichen.setIcon(
                                new javax.swing.ImageIcon(
                                    getClass().getResource(
                                        "/de/cismet/verdis/res/images/toolbar/kassenzeichenSearchPolygon.png")));          // NOI18N
                            cmdSearchKassenzeichen.setSelectedIcon(
                                new javax.swing.ImageIcon(
                                    getClass().getResource(
                                        "/de/cismet/verdis/res/images/toolbar/kassenzeichenSearchPolygon_selected.png"))); // NOI18N

                            EventQueue.invokeLater(new Runnable() {

                                    @Override
                                    public void run() {
                                        mappingComp.setInteractionMode(Main.KASSENZEICHEN_SEARCH_GEOMETRY_LISTENER);
                                        ((ServerSearchCreateSearchGeometryListener)mappingComp.getInputListener(
                                                Main.KASSENZEICHEN_SEARCH_GEOMETRY_LISTENER)).setMode(
                                            ServerSearchCreateSearchGeometryListener.POLYGON);
                                    }
                                });
                        }
                    });
            }
        };

    private Action searchEllipseAction = new AbstractAction() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                java.awt.EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("searchEllipseAction"); // NOI18N
                            }

                            mainGroup.clearSelection();
                            cmdSearchKassenzeichen.setSelected(true);

                            cmdSearchKassenzeichen.setIcon(
                                new javax.swing.ImageIcon(
                                    getClass().getResource(
                                        "/de/cismet/verdis/res/images/toolbar/kassenzeichenSearchEllipse.png")));          // NOI18N
                            cmdSearchKassenzeichen.setSelectedIcon(
                                new javax.swing.ImageIcon(
                                    getClass().getResource(
                                        "/de/cismet/verdis/res/images/toolbar/kassenzeichenSearchEllipse_selected.png"))); // NOI18N

                            EventQueue.invokeLater(new Runnable() {

                                    @Override
                                    public void run() {
                                        mappingComp.setInteractionMode(Main.KASSENZEICHEN_SEARCH_GEOMETRY_LISTENER);
                                        ((ServerSearchCreateSearchGeometryListener)mappingComp.getInputListener(
                                                Main.KASSENZEICHEN_SEARCH_GEOMETRY_LISTENER)).setMode(
                                            ServerSearchCreateSearchGeometryListener.ELLIPSE);
                                    }
                                });
                        }
                    });
            }
        };

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdAdd;
    private javax.swing.JToggleButton cmdAddHandle;
    private javax.swing.JButton cmdAngleMeasurement;
    private javax.swing.JToggleButton cmdAttachPolyToAlphadata;
    private javax.swing.JButton cmdBack;
    private javax.swing.JToggleButton cmdCreateLandparcelGeom;
    private javax.swing.JButton cmdForeground;
    private javax.swing.JButton cmdForward;
    private javax.swing.JButton cmdFullPoly;
    private javax.swing.JButton cmdFullPoly1;
    private javax.swing.JToggleButton cmdJoinPoly;
    private javax.swing.JToggleButton cmdMoveHandle;
    private javax.swing.JToggleButton cmdMovePolygon;
    private javax.swing.JToggleButton cmdNewLinestring;
    private javax.swing.JToggleButton cmdNewPoint;
    private javax.swing.JToggleButton cmdNewPolygon;
    private javax.swing.JToggleButton cmdOrthogonalRectangle;
    private javax.swing.JToggleButton cmdPan;
    private javax.swing.JButton cmdPrint;
    private javax.swing.JToggleButton cmdRaisePolygon;
    private javax.swing.JButton cmdRedo;
    private javax.swing.JButton cmdRefreshSingleLayer;
    private javax.swing.JToggleButton cmdRemoveHandle;
    private javax.swing.JToggleButton cmdRemovePolygon;
    private javax.swing.JToggleButton cmdRotatePolygon;
    private javax.swing.JToggleButton cmdSearchAlkisLandparcel;
    private javax.swing.JToggleButton cmdSearchBaulasten;
    private javax.swing.JButton cmdSearchKassenzeichen;
    private javax.swing.JToggleButton cmdSearchVermessungRiss;
    private javax.swing.JToggleButton cmdSelect;
    private javax.swing.JToggleButton cmdSelect1;
    private javax.swing.JToggleButton cmdSnap;
    private javax.swing.JToggleButton cmdSplitPoly;
    private javax.swing.JButton cmdUndo;
    private javax.swing.JToggleButton cmdWmsBackground;
    private javax.swing.JToggleButton cmdZoom;
    private javax.swing.ButtonGroup handleGroup;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator11;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JSeparator jSeparator13;
    private javax.swing.JSeparator jSeparator14;
    private javax.swing.JSeparator jSeparator15;
    private javax.swing.JSeparator jSeparator16;
    private javax.swing.JSeparator jSeparator17;
    private javax.swing.JSeparator jSeparator18;
    private javax.swing.JSeparator jSeparator19;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JLabel lblCoord;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JLabel lblMeasurement;
    private javax.swing.JLabel lblScale;
    private javax.swing.JLabel lblWaiting;
    private javax.swing.ButtonGroup mainGroup;
    private de.cismet.cismap.commons.gui.MappingComponent mappingComp;
    private javax.swing.JRadioButtonMenuItem mniSearchEllipse1;
    private javax.swing.JRadioButtonMenuItem mniSearchPolygon1;
    private javax.swing.JRadioButtonMenuItem mniSearchRectangle1;
    private javax.swing.JPanel panMap;
    private javax.swing.JPanel panStatus;
    private javax.swing.JPopupMenu pomScale;
    private javax.swing.JPopupMenu popMenSearch;
    private javax.swing.ButtonGroup searchGroup;
    private javax.swing.JToolBar tobVerdis;
    private javax.swing.JToggleButton togFixMapExtent;
    private javax.swing.JToggleButton togFixMapExtent1;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form KartenPanel.
     */
    public KartenPanel() {
        initComponents();

        ((JPopupMenuButton)cmdSearchKassenzeichen).setPopupMenu(popMenSearch);
        CidsAppBackend.getInstance().setMainMap(mappingComp);
        CismapBroker.getInstance().setMappingComponent(mappingComp);

        mappingComp.getFeatureCollection().addFeatureCollectionListener(this);
        // mappingComp.putInputListener(MappingComponent.ATTACH_POLYGON_TO_ALPHADATA, new AttachFeatureListener());

        mappingComp.setBackgroundEnabled(true);

//        CreateGeometryListener g=new CreateGeometryListener(mappingComp,JLabel.class) {
//
//
//        };
        // mappingComp.addInputListener("TIM_EASY_CREATOR",)
        // TIM Easy
        cmdNewPoint.setVisible(true);

        ((JHistoryButton)cmdForward).setDirection(JHistoryButton.DIRECTION_FORWARD);
        ((JHistoryButton)cmdBack).setDirection(JHistoryButton.DIRECTION_BACKWARD);
        ((JHistoryButton)cmdForward).setHistoryModel(mappingComp);
        ((JHistoryButton)cmdBack).setHistoryModel(mappingComp);

        cmdWmsBackground.setSelected(mappingComp.isBackgroundEnabled());

        mappingComp.getCamera()
                .addPropertyChangeListener(
                    PCamera.PROPERTY_VIEW_TRANSFORM,
                    new PropertyChangeListener() {

                        @Override
                        public void propertyChange(final PropertyChangeEvent evt) {
                            final int sd = (int)(mappingComp.getScaleDenominator() + 0.5);
                            lblScale.setText("1:" + sd);
                        }
                    });

        addScalePopupMenu("1:500", 500);
        addScalePopupMenu("1:750", 750);
        addScalePopupMenu("1:1000", 1000);
        addScalePopupMenu("1:1500", 1500);
        addScalePopupMenu("1:2000", 2000);
        addScalePopupMenu("1:2500", 2500);
        addScalePopupMenu("1:5000", 5000);
        addScalePopupMenu("1:7500", 7500);
        addScalePopupMenu("1:10000", 10000);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Fl\u00E4chen\u00DCbersichtsTable als Observer anmelden");
        }
        ((Observable)mappingComp.getMemUndo()).addObserver(this);
        ((Observable)mappingComp.getMemRedo()).addObserver(this);

//        if (mappingComp.getFeatureCollection() instanceof DefaultFeatureCollection) {
//            ((DefaultFeatureCollection) mappingComp.getFeatureCollection()).setSingleSelection(true);
//        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        final Object source = evt.getSource();
        final String propName = evt.getPropertyName();
        final Object newValue = evt.getNewValue();

        if (source == null) {
            return;
        }
        if (source.equals(mappingComp.getInputListener(Main.KASSENZEICHEN_SEARCH_GEOMETRY_LISTENER))) {
            if (AbstractCreateSearchGeometryListener.PROPERTY_MODE.equals(propName)) {
                mniSearchEllipse1.setSelected(CreateGeometryListener.ELLIPSE.equals(newValue));
                mniSearchPolygon1.setSelected(CreateGeometryListener.POLYGON.equals(newValue));
                mniSearchRectangle1.setSelected(CreateGeometryListener.RECTANGLE.equals(newValue));
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        pomScale = new javax.swing.JPopupMenu();
        popMenSearch = new javax.swing.JPopupMenu();
        mniSearchRectangle1 = new javax.swing.JRadioButtonMenuItem();
        mniSearchPolygon1 = new javax.swing.JRadioButtonMenuItem();
        mniSearchEllipse1 = new javax.swing.JRadioButtonMenuItem();
        mainGroup = new javax.swing.ButtonGroup();
        handleGroup = new javax.swing.ButtonGroup();
        searchGroup = new javax.swing.ButtonGroup();
        panMap = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        mappingComp = new de.cismet.cismap.commons.gui.MappingComponent();
        panStatus = new javax.swing.JPanel();
        cmdAdd = new javax.swing.JButton();
        lblInfo = new javax.swing.JLabel();
        lblCoord = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        lblWaiting = new javax.swing.JLabel();
        lblMeasurement = new javax.swing.JLabel();
        lblScale = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        tobVerdis = new javax.swing.JToolBar();
        cmdFullPoly = new javax.swing.JButton();
        cmdFullPoly1 = new javax.swing.JButton();
        togFixMapExtent = new javax.swing.JToggleButton();
        togFixMapExtent1 = new javax.swing.JToggleButton();
        cmdBack = new JHistoryButton();
        cmdForward = new JHistoryButton();
        cmdRefreshSingleLayer = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jSeparator9 = new javax.swing.JSeparator();
        cmdWmsBackground = new javax.swing.JToggleButton();
        cmdForeground = new javax.swing.JButton();
        cmdSnap = new javax.swing.JToggleButton();
        jPanel3 = new javax.swing.JPanel();
        jSeparator10 = new javax.swing.JSeparator();
        cmdPrint = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        jSeparator19 = new javax.swing.JSeparator();
        cmdZoom = new javax.swing.JToggleButton();
        cmdPan = new javax.swing.JToggleButton();
        cmdSelect = new javax.swing.JToggleButton();
        cmdMovePolygon = new javax.swing.JToggleButton();
        jPanel9 = new javax.swing.JPanel();
        jSeparator16 = new javax.swing.JSeparator();
        cmdNewPolygon = new javax.swing.JToggleButton();
        cmdNewLinestring = new javax.swing.JToggleButton();
        cmdNewPoint = new javax.swing.JToggleButton();
        jPanel4 = new javax.swing.JPanel();
        jSeparator11 = new javax.swing.JSeparator();
        cmdAngleMeasurement = new javax.swing.JButton();
        cmdOrthogonalRectangle = new javax.swing.JToggleButton();
        cmdSelect1 = new javax.swing.JToggleButton();
        jPanel11 = new javax.swing.JPanel();
        jSeparator18 = new javax.swing.JSeparator();
        cmdCreateLandparcelGeom = new javax.swing.JToggleButton();
        jPanel5 = new javax.swing.JPanel();
        jSeparator12 = new javax.swing.JSeparator();
        cmdSearchKassenzeichen = new CidsBeanDropJPopupMenuButton(
                Main.KASSENZEICHEN_SEARCH_GEOMETRY_LISTENER,
                mappingComp,
                null);
        jPanel10 = new javax.swing.JPanel();
        jSeparator17 = new javax.swing.JSeparator();
        cmdSearchAlkisLandparcel = new javax.swing.JToggleButton();
        cmdSearchVermessungRiss = new javax.swing.JToggleButton();
        cmdSearchBaulasten = new javax.swing.JToggleButton();
        jPanel6 = new javax.swing.JPanel();
        jSeparator13 = new javax.swing.JSeparator();
        cmdRaisePolygon = new javax.swing.JToggleButton();
        cmdRemovePolygon = new javax.swing.JToggleButton();
        cmdAttachPolyToAlphadata = new javax.swing.JToggleButton();
        cmdJoinPoly = new javax.swing.JToggleButton();
        cmdSplitPoly = new javax.swing.JToggleButton();
        jPanel7 = new javax.swing.JPanel();
        jSeparator14 = new javax.swing.JSeparator();
        cmdMoveHandle = new javax.swing.JToggleButton();
        cmdAddHandle = new javax.swing.JToggleButton();
        cmdRemoveHandle = new javax.swing.JToggleButton();
        cmdRotatePolygon = new javax.swing.JToggleButton();
        jPanel8 = new javax.swing.JPanel();
        jSeparator15 = new javax.swing.JSeparator();
        cmdUndo = new javax.swing.JButton();
        cmdRedo = new javax.swing.JButton();

        popMenSearch.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {

                @Override
                public void popupMenuWillBecomeVisible(final javax.swing.event.PopupMenuEvent evt) {
                    popMenSearchPopupMenuWillBecomeVisible(evt);
                }
                @Override
                public void popupMenuWillBecomeInvisible(final javax.swing.event.PopupMenuEvent evt) {
                }
                @Override
                public void popupMenuCanceled(final javax.swing.event.PopupMenuEvent evt) {
                }
            });

        mniSearchRectangle1.setAction(searchRectangleAction);
        searchGroup.add(mniSearchRectangle1);
        mniSearchRectangle1.setSelected(true);
        mniSearchRectangle1.setText("Rechteck");
        mniSearchRectangle1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/rectangleSearch.png"))); // NOI18N
        popMenSearch.add(mniSearchRectangle1);

        mniSearchPolygon1.setAction(searchPolygonAction);
        searchGroup.add(mniSearchPolygon1);
        mniSearchPolygon1.setText("Polygon");
        mniSearchPolygon1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/polygonSearch.png"))); // NOI18N
        popMenSearch.add(mniSearchPolygon1);

        mniSearchEllipse1.setAction(searchEllipseAction);
        searchGroup.add(mniSearchEllipse1);
        mniSearchEllipse1.setText("Ellipse / Kreis");
        mniSearchEllipse1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/ellipseSearch.png"))); // NOI18N
        popMenSearch.add(mniSearchEllipse1);

        setLayout(new java.awt.BorderLayout());

        panMap.setLayout(new java.awt.BorderLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createCompoundBorder(
                    javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2),
                    javax.swing.BorderFactory.createEtchedBorder()),
                javax.swing.BorderFactory.createEmptyBorder(4, 4, 1, 4)));
        jPanel2.setLayout(new java.awt.BorderLayout());

        mappingComp.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        mappingComp.setInternalLayerWidgetAvailable(true);
        jPanel2.add(mappingComp, java.awt.BorderLayout.CENTER);

        panStatus.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 1, 1, 1));
        panStatus.setLayout(new java.awt.GridBagLayout());

        cmdAdd.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/statusbar/layersman.png"))); // NOI18N
        cmdAdd.setBorderPainted(false);
        cmdAdd.setFocusPainted(false);
        cmdAdd.setMinimumSize(new java.awt.Dimension(25, 25));
        cmdAdd.setPreferredSize(new java.awt.Dimension(25, 25));
        cmdAdd.setRolloverIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/statusbar/layersman.png"))); // NOI18N
        cmdAdd.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdAddActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        panStatus.add(cmdAdd, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panStatus.add(lblInfo, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        panStatus.add(lblCoord, gridBagConstraints);
        panStatus.add(jSeparator1);

        lblWaiting.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/exec.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        panStatus.add(lblWaiting, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        panStatus.add(lblMeasurement, gridBagConstraints);

        lblScale.setText("1:???");
        lblScale.setComponentPopupMenu(pomScale);
        lblScale.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mousePressed(final java.awt.event.MouseEvent evt) {
                    lblScaleMousePressed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panStatus.add(lblScale, gridBagConstraints);
        panStatus.add(jSeparator2);

        jPanel2.add(panStatus, java.awt.BorderLayout.SOUTH);

        panMap.add(jPanel2, java.awt.BorderLayout.CENTER);

        tobVerdis.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        tobVerdis.setFloatable(false);
        tobVerdis.setMinimumSize(new java.awt.Dimension(1005, 30));
        tobVerdis.setName("tobVerdis"); // NOI18N
        tobVerdis.setPreferredSize(new java.awt.Dimension(1005, 30));

        cmdFullPoly.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/fullPoly.png"))); // NOI18N
        cmdFullPoly.setToolTipText("Zeige alle Flächen");
        cmdFullPoly.setBorderPainted(false);
        cmdFullPoly.setContentAreaFilled(false);
        cmdFullPoly.setFocusPainted(false);
        cmdFullPoly.setFocusable(false);
        cmdFullPoly.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdFullPolyActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdFullPoly);

        cmdFullPoly1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/fullSelPoly.png"))); // NOI18N
        cmdFullPoly1.setToolTipText("Zoom zur ausgewählten Fläche");
        cmdFullPoly1.setBorderPainted(false);
        cmdFullPoly1.setContentAreaFilled(false);
        cmdFullPoly1.setFocusPainted(false);
        cmdFullPoly1.setFocusable(false);
        cmdFullPoly1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdFullPoly1ActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdFullPoly1);

        togFixMapExtent.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/fixMapExtentMode_disabled.png"))); // NOI18N
        togFixMapExtent.setToolTipText("Kartenausschnitt für dieses Kassenzeichen beibehalten");
        togFixMapExtent.setBorderPainted(false);
        togFixMapExtent.setContentAreaFilled(false);
        togFixMapExtent.setFocusable(false);
        togFixMapExtent.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        togFixMapExtent.setRolloverIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/fixMapExtentMode_disabled.png"))); // NOI18N
        togFixMapExtent.setRolloverSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/fixMapExtentMode.png")));          // NOI18N
        togFixMapExtent.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/fixMapExtentMode.png")));          // NOI18N
        togFixMapExtent.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        final org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                togFixMapExtent1,
                org.jdesktop.beansbinding.ELProperty.create("${!selected}"),
                togFixMapExtent,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        togFixMapExtent.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    togFixMapExtentActionPerformed(evt);
                }
            });
        tobVerdis.add(togFixMapExtent);

        togFixMapExtent1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cismap/commons/gui/res/fixMapExtent_disabled.png"))); // NOI18N
        togFixMapExtent1.setToolTipText("Kartenausschnitt beibehalten");
        togFixMapExtent1.setBorderPainted(false);
        togFixMapExtent1.setContentAreaFilled(false);
        togFixMapExtent1.setFocusable(false);
        togFixMapExtent1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        togFixMapExtent1.setRolloverIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cismap/commons/gui/res/fixMapExtent_disabled.png"))); // NOI18N
        togFixMapExtent1.setRolloverSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cismap/commons/gui/res/fixMapExtent.png")));          // NOI18N
        togFixMapExtent1.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cismap/commons/gui/res/fixMapExtent.png")));          // NOI18N
        togFixMapExtent1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        togFixMapExtent1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    togFixMapExtent1ActionPerformed(evt);
                }
            });
        tobVerdis.add(togFixMapExtent1);

        cmdBack.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/back.png"))); // NOI18N
        cmdBack.setToolTipText("Zurück");
        cmdBack.setBorderPainted(false);
        cmdBack.setContentAreaFilled(false);
        cmdBack.setFocusPainted(false);
        cmdBack.setFocusable(false);
        tobVerdis.add(cmdBack);

        cmdForward.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/fwd.png"))); // NOI18N
        cmdForward.setToolTipText("Vor");
        cmdForward.setBorderPainted(false);
        cmdForward.setContentAreaFilled(false);
        cmdForward.setFocusPainted(false);
        cmdForward.setFocusable(false);
        tobVerdis.add(cmdForward);

        cmdRefreshSingleLayer.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cismap/commons/gui/layerwidget/res/refresh.png"))); // NOI18N
        cmdRefreshSingleLayer.setToolTipText("Kartenhintergründe neuladen");
        cmdRefreshSingleLayer.setBorderPainted(false);
        cmdRefreshSingleLayer.setFocusable(false);
        cmdRefreshSingleLayer.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdRefreshSingleLayer.setMargin(new java.awt.Insets(2, 1, 2, 1));
        cmdRefreshSingleLayer.setMaximumSize(new java.awt.Dimension(28, 28));
        cmdRefreshSingleLayer.setMinimumSize(new java.awt.Dimension(28, 28));
        cmdRefreshSingleLayer.setPreferredSize(new java.awt.Dimension(28, 28));
        cmdRefreshSingleLayer.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdRefreshSingleLayer.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdRefreshSingleLayerActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdRefreshSingleLayer);

        jPanel1.setMaximumSize(new java.awt.Dimension(2, 28));
        jPanel1.setMinimumSize(new java.awt.Dimension(2, 28));
        jPanel1.setPreferredSize(new java.awt.Dimension(2, 28));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jSeparator9.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel1.add(jSeparator9, java.awt.BorderLayout.CENTER);

        tobVerdis.add(jPanel1);

        cmdWmsBackground.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/map.png")));    // NOI18N
        cmdWmsBackground.setToolTipText("Hintergrund an/aus");
        cmdWmsBackground.setBorderPainted(false);
        cmdWmsBackground.setContentAreaFilled(false);
        cmdWmsBackground.setFocusPainted(false);
        cmdWmsBackground.setFocusable(false);
        cmdWmsBackground.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/map_on.png"))); // NOI18N
        cmdWmsBackground.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdWmsBackgroundActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdWmsBackground);

        cmdForeground.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/foreground.png")));    // NOI18N
        cmdForeground.setToolTipText("Vordergrund an/aus");
        cmdForeground.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdForeground.setBorderPainted(false);
        cmdForeground.setContentAreaFilled(false);
        cmdForeground.setFocusPainted(false);
        cmdForeground.setFocusable(false);
        cmdForeground.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdForeground.setMaximumSize(new java.awt.Dimension(28, 28));
        cmdForeground.setMinimumSize(new java.awt.Dimension(28, 28));
        cmdForeground.setPreferredSize(new java.awt.Dimension(28, 28));
        cmdForeground.setSelected(true);
        cmdForeground.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/foreground_on.png"))); // NOI18N
        cmdForeground.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdForeground.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdForegroundActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdForeground);

        cmdSnap.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/snap.png")));          // NOI18N
        cmdSnap.setSelected(true);
        cmdSnap.setToolTipText("Snapping an/aus");
        cmdSnap.setBorderPainted(false);
        cmdSnap.setContentAreaFilled(false);
        cmdSnap.setFocusPainted(false);
        cmdSnap.setFocusable(false);
        cmdSnap.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/snap_selected.png"))); // NOI18N
        cmdSnap.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdSnapActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdSnap);

        jPanel3.setMaximumSize(new java.awt.Dimension(2, 28));
        jPanel3.setMinimumSize(new java.awt.Dimension(2, 28));
        jPanel3.setPreferredSize(new java.awt.Dimension(2, 28));
        jPanel3.setLayout(new java.awt.BorderLayout());

        jSeparator10.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel3.add(jSeparator10, java.awt.BorderLayout.CENTER);

        tobVerdis.add(jPanel3);

        cmdPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/frameprint.png"))); // NOI18N
        cmdPrint.setToolTipText("Drucken");
        cmdPrint.setBorderPainted(false);
        cmdPrint.setFocusPainted(false);
        cmdPrint.setFocusable(false);
        cmdPrint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdPrint.setName("cmdPrint");                                                                  // NOI18N
        cmdPrint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdPrint.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdPrintActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdPrint);

        jPanel12.setMaximumSize(new java.awt.Dimension(2, 28));
        jPanel12.setMinimumSize(new java.awt.Dimension(2, 28));
        jPanel12.setPreferredSize(new java.awt.Dimension(2, 28));
        jPanel12.setLayout(new java.awt.BorderLayout());

        jSeparator19.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel12.add(jSeparator19, java.awt.BorderLayout.CENTER);

        tobVerdis.add(jPanel12);

        mainGroup.add(cmdZoom);
        cmdZoom.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/zoom.png")));          // NOI18N
        cmdZoom.setToolTipText("Zoomen");
        cmdZoom.setBorderPainted(false);
        cmdZoom.setContentAreaFilled(false);
        cmdZoom.setFocusPainted(false);
        cmdZoom.setFocusable(false);
        cmdZoom.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/zoom_selected.png"))); // NOI18N
        cmdZoom.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdZoomActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdZoom);

        mainGroup.add(cmdPan);
        cmdPan.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/move2.png")));          // NOI18N
        cmdPan.setToolTipText("Verschieben");
        cmdPan.setBorderPainted(false);
        cmdPan.setContentAreaFilled(false);
        cmdPan.setFocusPainted(false);
        cmdPan.setFocusable(false);
        cmdPan.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/move2_selected.png"))); // NOI18N
        cmdPan.setVerifyInputWhenFocusTarget(false);
        cmdPan.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdPanActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdPan);

        mainGroup.add(cmdSelect);
        cmdSelect.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/select.png")));          // NOI18N
        cmdSelect.setSelected(true);
        cmdSelect.setToolTipText("Auswählen");
        cmdSelect.setBorderPainted(false);
        cmdSelect.setContentAreaFilled(false);
        cmdSelect.setFocusPainted(false);
        cmdSelect.setFocusable(false);
        cmdSelect.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/select_selected.png"))); // NOI18N
        cmdSelect.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdSelectActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdSelect);

        mainGroup.add(cmdMovePolygon);
        cmdMovePolygon.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/movePoly.png")));          // NOI18N
        cmdMovePolygon.setToolTipText("Polygon verschieben");
        cmdMovePolygon.setBorderPainted(false);
        cmdMovePolygon.setContentAreaFilled(false);
        cmdMovePolygon.setFocusPainted(false);
        cmdMovePolygon.setFocusable(false);
        cmdMovePolygon.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/movePoly_selected.png"))); // NOI18N
        cmdMovePolygon.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdMovePolygonActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdMovePolygon);

        jPanel9.setMaximumSize(new java.awt.Dimension(2, 28));
        jPanel9.setMinimumSize(new java.awt.Dimension(2, 28));
        jPanel9.setPreferredSize(new java.awt.Dimension(2, 28));
        jPanel9.setLayout(new java.awt.BorderLayout());

        jSeparator16.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel9.add(jSeparator16, java.awt.BorderLayout.CENTER);

        tobVerdis.add(jPanel9);

        mainGroup.add(cmdNewPolygon);
        cmdNewPolygon.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/newPoly.png")));          // NOI18N
        cmdNewPolygon.setToolTipText("neues Polygon");
        cmdNewPolygon.setBorderPainted(false);
        cmdNewPolygon.setContentAreaFilled(false);
        cmdNewPolygon.setFocusPainted(false);
        cmdNewPolygon.setFocusable(false);
        cmdNewPolygon.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/newPoly_selected.png"))); // NOI18N
        cmdNewPolygon.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdNewPolygonActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdNewPolygon);

        mainGroup.add(cmdNewLinestring);
        cmdNewLinestring.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/newLine.png")));          // NOI18N
        cmdNewLinestring.setToolTipText("neue Linie");
        cmdNewLinestring.setBorderPainted(false);
        cmdNewLinestring.setContentAreaFilled(false);
        cmdNewLinestring.setFocusPainted(false);
        cmdNewLinestring.setFocusable(false);
        cmdNewLinestring.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdNewLinestring.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/newLine_selected.png"))); // NOI18N
        cmdNewLinestring.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdNewLinestring.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdNewLinestringActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdNewLinestring);

        mainGroup.add(cmdNewPoint);
        cmdNewPoint.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/newPoint.png")));          // NOI18N
        cmdNewPoint.setToolTipText("neuer Punkt");
        cmdNewPoint.setBorderPainted(false);
        cmdNewPoint.setContentAreaFilled(false);
        cmdNewPoint.setFocusPainted(false);
        cmdNewPoint.setFocusable(false);
        cmdNewPoint.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/newPoint_selected.png"))); // NOI18N
        cmdNewPoint.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdNewPointActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdNewPoint);

        jPanel4.setMaximumSize(new java.awt.Dimension(2, 28));
        jPanel4.setMinimumSize(new java.awt.Dimension(2, 28));
        jPanel4.setPreferredSize(new java.awt.Dimension(2, 28));
        jPanel4.setLayout(new java.awt.BorderLayout());

        jSeparator11.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel4.add(jSeparator11, java.awt.BorderLayout.CENTER);

        tobVerdis.add(jPanel4);

        cmdAngleMeasurement.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cismap/commons/gui/piccolo/Angle-Thingy-icon.png")));
        cmdAngleMeasurement.setBorderPainted(false);
        cmdAngleMeasurement.setFocusable(false);
        cmdAngleMeasurement.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdAngleMeasurement.setMaximumSize(new java.awt.Dimension(28, 28));
        cmdAngleMeasurement.setMinimumSize(new java.awt.Dimension(28, 28));
        cmdAngleMeasurement.setPreferredSize(new java.awt.Dimension(28, 28));
        cmdAngleMeasurement.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdAngleMeasurement.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdAngleMeasurementActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdAngleMeasurement);

        mainGroup.add(cmdOrthogonalRectangle);
        cmdOrthogonalRectangle.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/rechtwDreieck.png")));          // NOI18N
        cmdOrthogonalRectangle.setToolTipText("Rechteckige Fläche");
        cmdOrthogonalRectangle.setBorderPainted(false);
        cmdOrthogonalRectangle.setContentAreaFilled(false);
        cmdOrthogonalRectangle.setFocusPainted(false);
        cmdOrthogonalRectangle.setFocusable(false);
        cmdOrthogonalRectangle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdOrthogonalRectangle.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/rechtwDreieck_selected.png"))); // NOI18N
        cmdOrthogonalRectangle.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdOrthogonalRectangle.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdOrthogonalRectangleActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdOrthogonalRectangle);

        mainGroup.add(cmdSelect1);
        cmdSelect1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/ruler--plus.png")));          // NOI18N
        cmdSelect1.setToolTipText("Lot fällen");
        cmdSelect1.setBorderPainted(false);
        cmdSelect1.setContentAreaFilled(false);
        cmdSelect1.setFocusPainted(false);
        cmdSelect1.setFocusable(false);
        cmdSelect1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdSelect1.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/ruler--plus_selected.png"))); // NOI18N
        cmdSelect1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdSelect1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdSelect1ActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdSelect1);

        jPanel11.setMaximumSize(new java.awt.Dimension(2, 28));
        jPanel11.setMinimumSize(new java.awt.Dimension(2, 28));
        jPanel11.setPreferredSize(new java.awt.Dimension(2, 28));
        jPanel11.setLayout(new java.awt.BorderLayout());

        jSeparator18.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel11.add(jSeparator18, java.awt.BorderLayout.CENTER);

        tobVerdis.add(jPanel11);

        mainGroup.add(cmdCreateLandparcelGeom);
        cmdCreateLandparcelGeom.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/new_kassenzeichen_geometry.png"))); // NOI18N
        cmdCreateLandparcelGeom.setToolTipText("Kassenzeichen-Geometrie aus Flurstück erzeugen");
        cmdCreateLandparcelGeom.setBorderPainted(false);
        cmdCreateLandparcelGeom.setFocusable(false);
        cmdCreateLandparcelGeom.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdCreateLandparcelGeom.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdCreateLandparcelGeom.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdCreateLandparcelGeomActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdCreateLandparcelGeom);

        jPanel5.setMaximumSize(new java.awt.Dimension(2, 28));
        jPanel5.setMinimumSize(new java.awt.Dimension(2, 28));
        jPanel5.setPreferredSize(new java.awt.Dimension(2, 28));
        jPanel5.setLayout(new java.awt.BorderLayout());

        jSeparator12.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel5.add(jSeparator12, java.awt.BorderLayout.CENTER);

        tobVerdis.add(jPanel5);

        cmdSearchKassenzeichen.setAction(searchAction);
        cmdSearchKassenzeichen.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/kassenzeichenSearchRectangle.png"))); // NOI18N
        cmdSearchKassenzeichen.setToolTipText("Kassenzeichen-Suche");
        cmdSearchKassenzeichen.setBorderPainted(false);
        mainGroup.add(cmdSearchKassenzeichen);
        cmdSearchKassenzeichen.setContentAreaFilled(false);
        cmdSearchKassenzeichen.setFocusPainted(false);
        cmdSearchKassenzeichen.setFocusable(false);
        cmdSearchKassenzeichen.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdSearchKassenzeichen.setMaximumSize(new java.awt.Dimension(38, 28));
        cmdSearchKassenzeichen.setMinimumSize(new java.awt.Dimension(38, 28));
        cmdSearchKassenzeichen.setPreferredSize(new java.awt.Dimension(38, 28));
        cmdSearchKassenzeichen.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/de/cismet/verdis/res/images/toolbar/kassenzeichenSearchRectangle_selected.png")));           // NOI18N
        cmdSearchKassenzeichen.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdSearchKassenzeichen.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdSearchKassenzeichenActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdSearchKassenzeichen);

        jPanel10.setMaximumSize(new java.awt.Dimension(2, 28));
        jPanel10.setMinimumSize(new java.awt.Dimension(2, 28));
        jPanel10.setPreferredSize(new java.awt.Dimension(2, 28));
        jPanel10.setLayout(new java.awt.BorderLayout());

        jSeparator17.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel10.add(jSeparator17, java.awt.BorderLayout.CENTER);

        tobVerdis.add(jPanel10);

        mainGroup.add(cmdSearchAlkisLandparcel);
        cmdSearchAlkisLandparcel.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/commons/gui/alk.png")));          // NOI18N
        cmdSearchAlkisLandparcel.setToolTipText("Flurstücke suchen");
        cmdSearchAlkisLandparcel.setBorderPainted(false);
        cmdSearchAlkisLandparcel.setContentAreaFilled(false);
        cmdSearchAlkisLandparcel.setFocusPainted(false);
        cmdSearchAlkisLandparcel.setFocusable(false);
        cmdSearchAlkisLandparcel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdSearchAlkisLandparcel.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/commons/gui/alk_selected.png"))); // NOI18N
        cmdSearchAlkisLandparcel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdSearchAlkisLandparcel.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdSearchAlkisLandparcelActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdSearchAlkisLandparcel);

        mainGroup.add(cmdSearchVermessungRiss);
        cmdSearchVermessungRiss.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/commons/gui/vermessungsriss.png")));          // NOI18N
        cmdSearchVermessungRiss.setToolTipText("Vermessungsrisse suchen");
        cmdSearchVermessungRiss.setBorderPainted(false);
        cmdSearchVermessungRiss.setContentAreaFilled(false);
        cmdSearchVermessungRiss.setFocusPainted(false);
        cmdSearchVermessungRiss.setFocusable(false);
        cmdSearchVermessungRiss.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdSearchVermessungRiss.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/commons/gui/vermessungsriss_selected.png"))); // NOI18N
        cmdSearchVermessungRiss.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdSearchVermessungRiss.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdSearchVermessungRissActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdSearchVermessungRiss);
        cmdSearchVermessungRiss.setVisible(CidsAppBackend.getInstance().checkPermissionRisse());

        mainGroup.add(cmdSearchBaulasten);
        cmdSearchBaulasten.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/commons/gui/Baulast.png")));          // NOI18N
        cmdSearchBaulasten.setToolTipText("Baulasten suchen");
        cmdSearchBaulasten.setBorderPainted(false);
        cmdSearchBaulasten.setContentAreaFilled(false);
        cmdSearchBaulasten.setFocusPainted(false);
        cmdSearchBaulasten.setFocusable(false);
        cmdSearchBaulasten.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdSearchBaulasten.setName("cmdBaulastSearch");                                              // NOI18N
        cmdSearchBaulasten.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/commons/gui/Baulast_selected.png"))); // NOI18N
        cmdSearchBaulasten.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdSearchBaulasten.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdSearchBaulastenActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdSearchBaulasten);
        cmdSearchBaulasten.setVisible(CidsAppBackend.getInstance().checkPermissionBaulasten());

        jPanel6.setMaximumSize(new java.awt.Dimension(2, 28));
        jPanel6.setMinimumSize(new java.awt.Dimension(2, 28));
        jPanel6.setPreferredSize(new java.awt.Dimension(2, 28));
        jPanel6.setLayout(new java.awt.BorderLayout());

        jSeparator13.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel6.add(jSeparator13, java.awt.BorderLayout.CENTER);

        tobVerdis.add(jPanel6);

        mainGroup.add(cmdRaisePolygon);
        cmdRaisePolygon.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/raisePoly.png")));          // NOI18N
        cmdRaisePolygon.setToolTipText("Polygon hochholen");
        cmdRaisePolygon.setBorderPainted(false);
        cmdRaisePolygon.setContentAreaFilled(false);
        cmdRaisePolygon.setFocusPainted(false);
        cmdRaisePolygon.setFocusable(false);
        cmdRaisePolygon.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/raisePoly_selected.png"))); // NOI18N
        cmdRaisePolygon.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdRaisePolygonActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdRaisePolygon);

        mainGroup.add(cmdRemovePolygon);
        cmdRemovePolygon.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/removePoly.png")));          // NOI18N
        cmdRemovePolygon.setToolTipText("Polygon entfernen");
        cmdRemovePolygon.setBorderPainted(false);
        cmdRemovePolygon.setContentAreaFilled(false);
        cmdRemovePolygon.setFocusPainted(false);
        cmdRemovePolygon.setFocusable(false);
        cmdRemovePolygon.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/removePoly_selected.png"))); // NOI18N
        cmdRemovePolygon.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdRemovePolygonActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdRemovePolygon);

        mainGroup.add(cmdAttachPolyToAlphadata);
        cmdAttachPolyToAlphadata.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/polygonAttachment.png")));          // NOI18N
        cmdAttachPolyToAlphadata.setToolTipText("Polygon zuordnen");
        cmdAttachPolyToAlphadata.setBorderPainted(false);
        cmdAttachPolyToAlphadata.setContentAreaFilled(false);
        cmdAttachPolyToAlphadata.setFocusPainted(false);
        cmdAttachPolyToAlphadata.setFocusable(false);
        cmdAttachPolyToAlphadata.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/polygonAttachment_selected.png"))); // NOI18N
        cmdAttachPolyToAlphadata.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdAttachPolyToAlphadataActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdAttachPolyToAlphadata);

        mainGroup.add(cmdJoinPoly);
        cmdJoinPoly.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/joinPoly.png")));          // NOI18N
        cmdJoinPoly.setToolTipText("Polygone zusammenfassen");
        cmdJoinPoly.setBorderPainted(false);
        cmdJoinPoly.setContentAreaFilled(false);
        cmdJoinPoly.setFocusPainted(false);
        cmdJoinPoly.setFocusable(false);
        cmdJoinPoly.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/joinPoly_selected.png"))); // NOI18N
        cmdJoinPoly.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdJoinPolyActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdJoinPoly);

        mainGroup.add(cmdSplitPoly);
        cmdSplitPoly.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/splitPoly.png")));          // NOI18N
        cmdSplitPoly.setToolTipText("Polygon splitten");
        cmdSplitPoly.setBorderPainted(false);
        cmdSplitPoly.setContentAreaFilled(false);
        cmdSplitPoly.setFocusPainted(false);
        cmdSplitPoly.setFocusable(false);
        cmdSplitPoly.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/splitPoly_selected.png"))); // NOI18N
        cmdSplitPoly.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdSplitPolyActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdSplitPoly);

        jPanel7.setMaximumSize(new java.awt.Dimension(2, 28));
        jPanel7.setMinimumSize(new java.awt.Dimension(2, 28));
        jPanel7.setPreferredSize(new java.awt.Dimension(2, 28));
        jPanel7.setLayout(new java.awt.BorderLayout());

        jSeparator14.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel7.add(jSeparator14, java.awt.BorderLayout.CENTER);

        tobVerdis.add(jPanel7);

        handleGroup.add(cmdMoveHandle);
        cmdMoveHandle.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/moveHandle.png")));          // NOI18N
        cmdMoveHandle.setSelected(true);
        cmdMoveHandle.setToolTipText("Handle verschieben");
        cmdMoveHandle.setBorderPainted(false);
        cmdMoveHandle.setContentAreaFilled(false);
        cmdMoveHandle.setFocusPainted(false);
        cmdMoveHandle.setFocusable(false);
        cmdMoveHandle.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/moveHandle_selected.png"))); // NOI18N
        cmdMoveHandle.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdMoveHandleActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdMoveHandle);

        handleGroup.add(cmdAddHandle);
        cmdAddHandle.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/addHandle.png")));          // NOI18N
        cmdAddHandle.setToolTipText("Handle hinzufügen");
        cmdAddHandle.setBorderPainted(false);
        cmdAddHandle.setContentAreaFilled(false);
        cmdAddHandle.setFocusPainted(false);
        cmdAddHandle.setFocusable(false);
        cmdAddHandle.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/addHandle_selected.png"))); // NOI18N
        cmdAddHandle.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdAddHandleActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdAddHandle);

        handleGroup.add(cmdRemoveHandle);
        cmdRemoveHandle.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/removeHandle.png")));          // NOI18N
        cmdRemoveHandle.setToolTipText("Handle entfernen");
        cmdRemoveHandle.setBorderPainted(false);
        cmdRemoveHandle.setContentAreaFilled(false);
        cmdRemoveHandle.setFocusPainted(false);
        cmdRemoveHandle.setFocusable(false);
        cmdRemoveHandle.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/removeHandle_selected.png"))); // NOI18N
        cmdRemoveHandle.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdRemoveHandleActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdRemoveHandle);

        handleGroup.add(cmdRotatePolygon);
        cmdRotatePolygon.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/rotate16.png")));          // NOI18N
        cmdRotatePolygon.setToolTipText("Rotiere Polygon");
        cmdRotatePolygon.setBorderPainted(false);
        cmdRotatePolygon.setContentAreaFilled(false);
        cmdRotatePolygon.setFocusPainted(false);
        cmdRotatePolygon.setFocusable(false);
        cmdRotatePolygon.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/rotate16_selected.png"))); // NOI18N
        cmdRotatePolygon.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdRotatePolygonActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdRotatePolygon);

        jPanel8.setMaximumSize(new java.awt.Dimension(2, 28));
        jPanel8.setMinimumSize(new java.awt.Dimension(2, 28));
        jPanel8.setPreferredSize(new java.awt.Dimension(2, 28));
        jPanel8.setLayout(new java.awt.BorderLayout());

        jSeparator15.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel8.add(jSeparator15, java.awt.BorderLayout.CENTER);

        tobVerdis.add(jPanel8);

        cmdUndo.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/undo.png"))); // NOI18N
        cmdUndo.setToolTipText("Undo");
        cmdUndo.setBorderPainted(false);
        cmdUndo.setContentAreaFilled(false);
        cmdUndo.setEnabled(false);
        cmdUndo.setFocusPainted(false);
        cmdUndo.setFocusable(false);
        cmdUndo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdUndo.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdUndoActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdUndo);

        cmdRedo.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/redo.png"))); // NOI18N
        cmdRedo.setBorderPainted(false);
        cmdRedo.setContentAreaFilled(false);
        cmdRedo.setEnabled(false);
        cmdRedo.setFocusPainted(false);
        cmdRedo.setFocusable(false);
        cmdRedo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdRedo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdRedo.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdRedoActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdRedo);

        panMap.add(tobVerdis, java.awt.BorderLayout.NORTH);

        add(panMap, java.awt.BorderLayout.CENTER);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdAddActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdAddActionPerformed
        try {
            mappingComp.showInternalLayerWidget(!mappingComp.isInternalLayerWidgetVisible(), 500);
        } catch (Throwable t) {
            LOG.error("Fehler beim Anzeigen des Layersteuerelements", t);
        }
    }                                                                          //GEN-LAST:event_cmdAddActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblScaleMousePressed(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblScaleMousePressed
        if (evt.isPopupTrigger()) {
            pomScale.setVisible(true);
        }
    }                                                                        //GEN-LAST:event_lblScaleMousePressed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdFullPolyActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdFullPolyActionPerformed
        mappingComp.zoomToFullFeatureCollectionBounds();
    }                                                                               //GEN-LAST:event_cmdFullPolyActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdFullPoly1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdFullPoly1ActionPerformed
        final Collection<Feature> coll = new ArrayList<Feature>();
        for (final Object f : mappingComp.getFeatureCollection().getSelectedFeatures()) {
            if ((f instanceof CidsFeature) || (f instanceof PureNewFeature)) {
                coll.add((Feature)f);
            }
        }
        mappingComp.zoomToAFeatureCollection(coll, true, false);
    }                                                                                //GEN-LAST:event_cmdFullPoly1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdWmsBackgroundActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdWmsBackgroundActionPerformed
        if (mappingComp.isBackgroundEnabled()) {
            mappingComp.setBackgroundEnabled(false);
            cmdWmsBackground.setSelected(false);
        } else {
            mappingComp.setBackgroundEnabled(true);
            cmdWmsBackground.setSelected(true);
            mappingComp.queryServices();
        }
    }                                                                                    //GEN-LAST:event_cmdWmsBackgroundActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdForegroundActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdForegroundActionPerformed
        if (mappingComp.isFeatureCollectionVisible()) {
            mappingComp.setFeatureCollectionVisibility(false);
            cmdForeground.setSelected(false);
        } else {
            mappingComp.setFeatureCollectionVisibility(true);
            cmdForeground.setSelected(true);
        }
    }                                                                                 //GEN-LAST:event_cmdForegroundActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdSnapActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdSnapActionPerformed
        mappingComp.setSnappingEnabled(cmdSnap.isSelected());
        mappingComp.setVisualizeSnappingEnabled(cmdSnap.isSelected());
        mappingComp.setInGlueIdenticalPointsMode(cmdSnap.isSelected());
    }                                                                           //GEN-LAST:event_cmdSnapActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdMoveHandleActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdMoveHandleActionPerformed
        mappingComp.setInteractionMode(MappingComponent.SELECT);
        mappingComp.setHandleInteractionMode(MappingComponent.MOVE_HANDLE);
    }                                                                                 //GEN-LAST:event_cmdMoveHandleActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdAddHandleActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdAddHandleActionPerformed
        mappingComp.setInteractionMode(MappingComponent.SELECT);
        mappingComp.setHandleInteractionMode(MappingComponent.ADD_HANDLE);
    }                                                                                //GEN-LAST:event_cmdAddHandleActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdRemoveHandleActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdRemoveHandleActionPerformed
        mappingComp.setInteractionMode(MappingComponent.SELECT);
        mappingComp.setHandleInteractionMode(MappingComponent.REMOVE_HANDLE);
    }                                                                                   //GEN-LAST:event_cmdRemoveHandleActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdRotatePolygonActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdRotatePolygonActionPerformed
        mappingComp.setInteractionMode(MappingComponent.SELECT);
        mappingComp.setHandleInteractionMode(MappingComponent.ROTATE_POLYGON);
    }                                                                                    //GEN-LAST:event_cmdRotatePolygonActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdUndoActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdUndoActionPerformed
        LOG.info("UNDO");
        final CustomAction a = mappingComp.getMemUndo().getLastAction();
        if (LOG.isDebugEnabled()) {
            LOG.debug("... Aktion ausf\u00FChren: " + a.info());
        }
        try {
            a.doAction();
        } catch (Exception e) {
            LOG.error("Error beim Ausf\u00FChren der Aktion", e);
        }
        final CustomAction inverse = a.getInverse();
        mappingComp.getMemRedo().addAction(inverse);
        if (LOG.isDebugEnabled()) {
            LOG.debug("... neue Aktion auf REDO-Stack: " + inverse);
            LOG.debug("... fertig");
        }
    }                                                                           //GEN-LAST:event_cmdUndoActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdRedoActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdRedoActionPerformed
        LOG.info("REDO");
        final CustomAction a = mappingComp.getMemRedo().getLastAction();
        if (LOG.isDebugEnabled()) {
            LOG.debug("... Aktion ausf\u00FChren: " + a.info());
        }
        try {
            a.doAction();
        } catch (Exception e) {
            LOG.error("Error beim Ausf\u00FChren der Aktion", e);
        }
        final CustomAction inverse = a.getInverse();
        mappingComp.getMemUndo().addAction(inverse);
        if (LOG.isDebugEnabled()) {
            LOG.debug("... neue Aktion auf UNDO-Stack: " + inverse);
            LOG.debug("... fertig");
        }
    }                                                                           //GEN-LAST:event_cmdRedoActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void popMenSearchPopupMenuWillBecomeVisible(final javax.swing.event.PopupMenuEvent evt) { //GEN-FIRST:event_popMenSearchPopupMenuWillBecomeVisible
    }                                                                                                 //GEN-LAST:event_popMenSearchPopupMenuWillBecomeVisible

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdSplitPolyActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdSplitPolyActionPerformed
        cmdMoveHandleActionPerformed(null);
        mappingComp.setInteractionMode(MappingComponent.SPLIT_POLYGON);
    }                                                                                //GEN-LAST:event_cmdSplitPolyActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdJoinPolyActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdJoinPolyActionPerformed
        mappingComp.setInteractionMode(MappingComponent.JOIN_POLYGONS);
    }                                                                               //GEN-LAST:event_cmdJoinPolyActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdAttachPolyToAlphadataActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdAttachPolyToAlphadataActionPerformed
        mappingComp.setInteractionMode(MappingComponent.ATTACH_POLYGON_TO_ALPHADATA);
    }                                                                                            //GEN-LAST:event_cmdAttachPolyToAlphadataActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdRemovePolygonActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdRemovePolygonActionPerformed
        mappingComp.setInteractionMode(MappingComponent.REMOVE_POLYGON);
    }                                                                                    //GEN-LAST:event_cmdRemovePolygonActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdRaisePolygonActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdRaisePolygonActionPerformed
        mappingComp.setInteractionMode(MappingComponent.RAISE_POLYGON);
    }                                                                                   //GEN-LAST:event_cmdRaisePolygonActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdNewPointActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdNewPointActionPerformed
        mappingComp.setInteractionMode(MappingComponent.NEW_POLYGON);
        ((CreateGeometryListener)mappingComp.getInputListener(MappingComponent.NEW_POLYGON)).setMode(
            CreateGeometryListener.POINT);
    }                                                                               //GEN-LAST:event_cmdNewPointActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdPanActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdPanActionPerformed
        mappingComp.setInteractionMode(MappingComponent.PAN);
    }                                                                          //GEN-LAST:event_cmdPanActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdSelectActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdSelectActionPerformed
        mappingComp.setInteractionMode(MappingComponent.SELECT);
        cmdMoveHandle.setSelected(true);
        cmdMoveHandleActionPerformed(null);
    }                                                                             //GEN-LAST:event_cmdSelectActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdMovePolygonActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdMovePolygonActionPerformed
        mappingComp.setInteractionMode(MappingComponent.MOVE_POLYGON);
    }                                                                                  //GEN-LAST:event_cmdMovePolygonActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdNewPolygonActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdNewPolygonActionPerformed
        mappingComp.setInteractionMode(MappingComponent.NEW_POLYGON);
        ((CreateGeometryListener)mappingComp.getInputListener(MappingComponent.NEW_POLYGON)).setMode(
            CreateGeometryListener.POLYGON);
        ((CreateGeometryListener)mappingComp.getInputListener(MappingComponent.NEW_POLYGON)).setGeometryFeatureClass(
            PureNewFeature.class);
    }                                                                                 //GEN-LAST:event_cmdNewPolygonActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdNewLinestringActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdNewLinestringActionPerformed
        mappingComp.setInteractionMode(MappingComponent.NEW_POLYGON);
        ((CreateGeometryListener)mappingComp.getInputListener(MappingComponent.NEW_POLYGON)).setMode(
            CreateGeometryListener.LINESTRING);
        ((CreateGeometryListener)mappingComp.getInputListener(MappingComponent.NEW_POLYGON)).setGeometryFeatureClass(
            PureNewFeatureWithThickerLineString.class);
    }                                                                                    //GEN-LAST:event_cmdNewLinestringActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdOrthogonalRectangleActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdOrthogonalRectangleActionPerformed
        mappingComp.setInteractionMode(MappingComponent.NEW_POLYGON);
        ((CreateGeometryListener)mappingComp.getInputListener(MappingComponent.NEW_POLYGON)).setMode(
            CreateGeometryListener.RECTANGLE_FROM_LINE);
    }                                                                                          //GEN-LAST:event_cmdOrthogonalRectangleActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdSearchAlkisLandparcelActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdSearchAlkisLandparcelActionPerformed
        mappingComp.setInteractionMode(FlurstueckNodesSearchCreateSearchGeometryListener.NAME);
    }                                                                                            //GEN-LAST:event_cmdSearchAlkisLandparcelActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdSearchKassenzeichenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdSearchKassenzeichenActionPerformed
    }                                                                                          //GEN-LAST:event_cmdSearchKassenzeichenActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdZoomActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdZoomActionPerformed
        mappingComp.setInteractionMode(MappingComponent.ZOOM);
    }                                                                           //GEN-LAST:event_cmdZoomActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdCreateLandparcelGeomActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdCreateLandparcelGeomActionPerformed
        isAssignLandparcel = cmdCreateLandparcelGeom.isSelected();
        if (isAssignLandparcel) {
            mappingComp.setInteractionMode(Main.KASSENZEICHEN_GEOMETRIE_ASSIGN_GEOMETRY_LISTENER);
        }
    }                                                                                           //GEN-LAST:event_cmdCreateLandparcelGeomActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void togFixMapExtentActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_togFixMapExtentActionPerformed
        Main.getInstance().setFixMapExtentMode(togFixMapExtent.isSelected());
    }                                                                                   //GEN-LAST:event_togFixMapExtentActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdAngleMeasurementActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdAngleMeasurementActionPerformed
        StaticSwingTools.showDialog(AngleMeasurementDialog.getInstance());
    }                                                                                       //GEN-LAST:event_cmdAngleMeasurementActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdSelect1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdSelect1ActionPerformed
        EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    mappingComp.setHandleInteractionMode(MappingComponent.ADD_HANDLE);
                    mappingComp.setInteractionMode(MappingComponent.PERPENDICULAR_INTERSECTION);
                    ((PerpendicularIntersectionListener)mappingComp.getInputListener(
                            MappingComponent.PERPENDICULAR_INTERSECTION)).init();
                }
            });
    } //GEN-LAST:event_cmdSelect1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdRefreshSingleLayerActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdRefreshSingleLayerActionPerformed
        final SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    final TreeMap<Integer, MapService> rs = mappingComp.getMappingModel().getRasterServices();
                    for (final Integer key : rs.keySet()) {
                        final MapService value = rs.get(key);
                        if ((value instanceof RetrievalServiceLayer) && ((RetrievalServiceLayer)value).isEnabled()) {
//                            value.setBoundingBox(mappingComp.getCurrentBoundingBoxFromCamera());
                            ((RetrievalServiceLayer)value).retrieve(true);
                        }
                    }
                    return null;
                }
            };

        worker.execute();
    } //GEN-LAST:event_cmdRefreshSingleLayerActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdSearchVermessungRissActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdSearchVermessungRissActionPerformed
        mappingComp.setInteractionMode(RissNodesSearchCreateSearchGeometryListener.NAME);
    }                                                                                           //GEN-LAST:event_cmdSearchVermessungRissActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdSearchBaulastenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdSearchBaulastenActionPerformed
        mappingComp.setInteractionMode(BaulastblattNodesSearchCreateSearchGeometryListener.NAME);
    }                                                                                      //GEN-LAST:event_cmdSearchBaulastenActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdPrintActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdPrintActionPerformed
        mappingComp.showPrintingSettingsDialog();
        changeSelectedButtonAccordingToInteractionMode();
    }                                                                            //GEN-LAST:event_cmdPrintActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void togFixMapExtent1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_togFixMapExtent1ActionPerformed
        Main.getInstance().setFixMapExtent(togFixMapExtent1.isSelected());
    }                                                                                    //GEN-LAST:event_togFixMapExtent1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  text              DOCUMENT ME!
     * @param  scaleDenominator  DOCUMENT ME!
     */
    private void addScalePopupMenu(final String text, final double scaleDenominator) {
        final JMenuItem jmi = new JMenuItem(text);
        jmi.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    mappingComp.gotoBoundingBoxWithHistory(mappingComp.getBoundingBoxFromScale(scaleDenominator));
                }
            });
        pomScale.add(jmi);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  fce  DOCUMENT ME!
     */
    @Override
    public void allFeaturesRemoved(final FeatureCollectionEvent fce) {
    }

    /**
     * DOCUMENT ME!
     */
    @Override
    public void featureCollectionChanged() {
    }

    /**
     * DOCUMENT ME!
     *
     * @param  fce  DOCUMENT ME!
     */
    @Override
    public void featureReconsiderationRequested(final FeatureCollectionEvent fce) {
    }

    /**
     * DOCUMENT ME!
     *
     * @param  fce  DOCUMENT ME!
     */
    @Override
    public void featureSelectionChanged(final FeatureCollectionEvent fce) {
        refreshMeasurementsInStatus();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  fce  DOCUMENT ME!
     */
    @Override
    public void featuresAdded(final FeatureCollectionEvent fce) {
        for (final Feature feature : fce.getEventFeatures()) {
            if (feature instanceof BeanUpdatingCidsFeature) {
                final BeanUpdatingCidsFeature buFeature = (BeanUpdatingCidsFeature)feature;
                buFeature.updateBeanGeometry();
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  fce  DOCUMENT ME!
     */
    @Override
    public void featuresChanged(final FeatureCollectionEvent fce) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("FeatureChanged");
        }
        if (mappingComp.getInteractionMode().equals(MappingComponent.NEW_POLYGON)) {
            refreshMeasurementsInStatus(fce.getEventFeatures());
        } else {
            refreshMeasurementsInStatus();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  fce  DOCUMENT ME!
     */
    @Override
    public void featuresRemoved(final FeatureCollectionEvent fce) {
        for (final Feature feature : fce.getEventFeatures()) {
            if (feature instanceof BeanUpdatingCidsFeature) {
                final BeanUpdatingCidsFeature buFeature = (BeanUpdatingCidsFeature)feature;
                try {
                    buFeature.getCidsBean().setProperty(buFeature.getGeoPropertyName(), null);
                } catch (final Exception ex) {
                    LOG.warn(ex, ex);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  e  DOCUMENT ME!
     */
    @Override
    public void retrievalAborted(final RetrievalEvent e) {
        activeRetrievalServices.remove(((ServiceLayer)e.getRetrievalService()).getName());
        checkProgress();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  e  DOCUMENT ME!
     */
    @Override
    public void retrievalComplete(final RetrievalEvent e) {
        activeRetrievalServices.remove(((ServiceLayer)e.getRetrievalService()).getName());
        checkProgress();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  e  DOCUMENT ME!
     */
    @Override
    public void retrievalError(final RetrievalEvent e) {
        activeRetrievalServices.remove(((ServiceLayer)e.getRetrievalService()).getName());
        checkProgress();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  e  DOCUMENT ME!
     */
    @Override
    public void retrievalProgress(final RetrievalEvent e) {
        activeRetrievalServices.remove(((ServiceLayer)e.getRetrievalService()).getName());
        checkProgress();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  e  DOCUMENT ME!
     */
    @Override
    public void retrievalStarted(final RetrievalEvent e) {
        activeRetrievalServices.add(((ServiceLayer)e.getRetrievalService()).getName());
        checkProgress();
    }

    /**
     * DOCUMENT ME!
     */
    private void checkProgress() {
        // log.debug(activeRetrievalServices);
        if (activeRetrievalServices.size() > 0) {
            setWaiting(true);
        } else {
            setWaiting(false);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  o    DOCUMENT ME!
     * @param  arg  DOCUMENT ME!
     */
    @Override
    public void update(final Observable o, final Object arg) {
        if (o.equals(mappingComp.getMemUndo())) {
            if (arg.equals(MementoInterface.ACTIVATE) && !cmdUndo.isEnabled()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("UNDO-Button aktivieren");
                }
                EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            cmdUndo.setEnabled(true);
                        }
                    });
            } else if (arg.equals(MementoInterface.DEACTIVATE) && cmdUndo.isEnabled()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("UNDO-Button deaktivieren");
                }
                EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            cmdUndo.setEnabled(false);
                        }
                    });
            }
        } else if (o.equals(mappingComp.getMemRedo())) {
            if (arg.equals(MementoInterface.ACTIVATE) && !cmdRedo.isEnabled()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("REDO-Button aktivieren");
                }
                EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            cmdRedo.setEnabled(true);
                        }
                    });
            } else if (arg.equals(MementoInterface.DEACTIVATE) && cmdRedo.isEnabled()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("REDO-Button deaktivieren");
                }
                EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            cmdRedo.setEnabled(false);
                        }
                    });
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  wait  DOCUMENT ME!
     */
    private void setWaiting(final boolean wait) {
        lblWaiting.setVisible(wait);
    }

    /**
     * DOCUMENT ME!
     */
    public void refreshMeasurementsInStatus() {
        final Collection<Feature> selectedFeatures = mappingComp.getFeatureCollection().getSelectedFeatures();
        final Collection<Feature> cidsFeatures = new ArrayList<Feature>();
        for (final Feature feature : selectedFeatures) {
            if ((feature instanceof CidsFeature) || (feature instanceof PureNewFeature)) {
                cidsFeatures.add(feature);
            }
        }
        refreshMeasurementsInStatus(cidsFeatures);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  b  DOCUMENT ME!
     */
    @Override
    public void setEnabled(final boolean b) {
        cmdMovePolygon.setVisible(b);
        cmdRemovePolygon.setVisible(b);
        cmdAttachPolyToAlphadata.setVisible(b);
        cmdCreateLandparcelGeom.setVisible(b);
        jPanel5.setVisible(b);
        cmdJoinPoly.setVisible(b);
        jPanel6.setVisible(b);
        cmdMoveHandle.setVisible(b);
        cmdAddHandle.setVisible(b);
        cmdRemoveHandle.setVisible(b);
        cmdRotatePolygon.setVisible(b);
        cmdSplitPoly.setVisible(b);
        cmdRaisePolygon.setVisible(b);
        jPanel7.setVisible(b);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cf  DOCUMENT ME!
     */
    public void refreshMeasurementsInStatus(final Collection<Feature> cf) {
        if (CidsAppBackend.getInstance().getMode().equals(CidsAppBackend.Mode.REGEN)) {
            double umfang = 0.0;
            double area = 0.0;
            for (final Feature f : cf) {
                if ((f != null) && (f.getGeometry() != null)) {
                    area += f.getGeometry().getArea();
                    umfang += f.getGeometry().getLength();
                }
            }
            if (((area == 0.0) && (umfang == 0.0)) || cf.isEmpty()) {
                lblMeasurement.setText("");
            } else {
                // vor dem runden erst zweistellig abschneiden (damit nicht aufgerundet wird)
                final double areadd = ((double)(Math.ceil(area * 100))) / 100;
                final double umfangdd = ((double)(Math.ceil(umfang * 100))) / 100;
                lblMeasurement.setText("Fl\u00E4che: " + StaticDecimalTools.round(areadd) + "  Umfang: "
                            + StaticDecimalTools.round(umfangdd));
            }
        } else if (CidsAppBackend.getInstance().getMode().equals(CidsAppBackend.Mode.SR)) {
            double length = 0.0;
            for (final Feature f : cf) {
                if ((f != null) && (f.getGeometry() != null)) {
                    length += f.getGeometry().getLength();
                }
            }
            // vor dem runden erst zweistellig abschneiden (damit nicht aufgerundet wird)
            final double lengthdd = ((double)(Math.ceil(length * 100))) / 100;
            lblMeasurement.setText("L\u00E4nge: " + StaticDecimalTools.round(lengthdd));
        }
    }

    /**
     * piccolo reflection methods.
     *
     * @param  notification  DOCUMENT ME!
     */
    public void coordinatesChanged(final PNotification notification) {
        final Object o = notification.getObject();
        if (o instanceof SimpleMoveListener) {
            final double x = ((SimpleMoveListener)o).getXCoord();
            final double y = ((SimpleMoveListener)o).getYCoord();

            lblCoord.setText(MappingComponent.getCoordinateString(x, y)); // + "... " +test);
            final PFeature pf = ((SimpleMoveListener)o).getUnderlyingPFeature();
            if ((pf != null) && (pf.getFeature() instanceof PostgisFeature) && (pf.getVisible() == true)
                        && (pf.getParent() != null)
                        && (pf.getParent().getVisible() == true)) {
                lblInfo.setText(((PostgisFeature)pf.getFeature()).getObjectName());
            } else if ((pf != null) && (pf.getFeature() instanceof CidsFeature)) {
                final CidsFeature cf = (CidsFeature)pf.getFeature();
                final CidsBean cb = (CidsBean)cf.getMetaObject().getBean();
                if (cf.getMetaClass().getName().toLowerCase().equals(
                                VerdisMetaClassConstants.MC_FLAECHE.toLowerCase())) {
                    final int kassenzeichenNummer = (Integer)getCidsBean().getProperty(
                            KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER);
                    final String bezeichnung = (String)cb.getProperty(
                            FlaechePropertyConstants.PROP__FLAECHENBEZEICHNUNG);

                    final String anschlussGrad = (String)cb.getProperty(
                            FlaechePropertyConstants.PROP__FLAECHENINFO
                                    + "."
                                    + FlaecheninfoPropertyConstants.PROP__ANSCHLUSSGRAD
                                    + "."
                                    + AnschlussgradPropertyConstants.PROP__GRAD);

                    lblInfo.setText("Kassenzeichen: " + Integer.toString(kassenzeichenNummer) + "::" + bezeichnung
                                + " - " + anschlussGrad);
                } else if (cf.getMetaClass().getName().toLowerCase().equals(
                                VerdisMetaClassConstants.MC_FRONT.toLowerCase())) {
                    final int kassenzeichenNummer = (Integer)getCidsBean().getProperty(
                            KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER);
                    final Integer nummer = (Integer)cb.getProperty(
                            FrontPropertyConstants.PROP__NUMMER);

                    lblInfo.setText("Kassenzeichen: " + Integer.toString(kassenzeichenNummer) + "::" + nummer);
                }
            } else {
                lblInfo.setText("");
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void initPluginToolbarComponents() {
        final Collection<? extends ToolbarComponentsProvider> toolbarCompProviders = Lookup.getDefault()
                    .lookupAll(
                        ToolbarComponentsProvider.class);

        if (toolbarCompProviders != null) {
            for (final ToolbarComponentsProvider toolbarCompProvider : toolbarCompProviders) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Registering Toolbar Components for Plugin: " + toolbarCompProvider.getPluginName()); // NOI18N
                }

                final Collection<ToolbarComponentDescription> componentDescriptions =
                    toolbarCompProvider.getToolbarComponents();

                if (componentDescriptions != null) {
                    for (final ToolbarComponentDescription componentDescription : componentDescriptions) {
                        int insertionIndex = tobVerdis.getComponentCount();
                        final String anchor = componentDescription.getAnchorComponentName();

                        if (anchor != null) {
                            for (int i = tobVerdis.getComponentCount(); --i >= 0;) {
                                final Component currentAnchorCandidate = tobVerdis.getComponent(i);

                                if (anchor.equals(currentAnchorCandidate.getName())) {
                                    if (ToolbarComponentsProvider.ToolbarPositionHint.BEFORE.equals(
                                                    componentDescription.getPositionHint())) {
                                        insertionIndex = i;
                                    } else {
                                        insertionIndex = i + 1;
                                    }

                                    break;
                                }
                            }
                        }

                        tobVerdis.add(componentDescription.getComponent(), insertionIndex);
                    }
                }
            }
        }
        final Collection<? extends BasicGuiComponentProvider> toolbarguiCompProviders = Lookup.getDefault()
                    .lookupAll(BasicGuiComponentProvider.class);
        if (toolbarguiCompProviders != null) {
            for (final BasicGuiComponentProvider gui : toolbarguiCompProviders) {
                if (gui.getType() == BasicGuiComponentProvider.GuiType.TOOLBARCOMPONENT) {
                    final int insertionIndex = tobVerdis.getComponentCount();
                    tobVerdis.add(gui.getComponent(), insertionIndex);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  notification  DOCUMENT ME!
     */
    public void joinPolygons(final PNotification notification) {
        if (CidsAppBackend.getInstance().getMode().equals(CidsAppBackend.Mode.REGEN)
                    || CidsAppBackend.getInstance().getMode().equals(CidsAppBackend.Mode.SR)) {
            PFeature one;
            PFeature two;
            one = mappingComp.getSelectedNode();

            final Object o = notification.getObject();

            if (o instanceof JoinPolygonsListener) {
                final JoinPolygonsListener listener = ((JoinPolygonsListener)o);
                final PFeature joinCandidate = listener.getFeatureRequestedForJoin();
                if ((joinCandidate.getFeature() instanceof CidsFeature)
                            || (joinCandidate.getFeature() instanceof PureNewFeature)) {
                    if ((listener.getModifier() & Event.CTRL_MASK) != 0) {
                        if ((one != null) && (joinCandidate != one)) {
                            if ((one.getFeature() instanceof PureNewFeature)
                                        && (joinCandidate.getFeature() instanceof CidsFeature)) {
                                two = one;

                                one = joinCandidate;
                                one.setSelected(true);
                                two.setSelected(false);
                                mappingComp.getFeatureCollection().select(one.getFeature());
                            } else {
                                two = joinCandidate;
                            }
                            try {
                                final Geometry backup = one.getFeature().getGeometry();
                                final Geometry newGeom = one.getFeature()
                                            .getGeometry()
                                            .union(two.getFeature().getGeometry());

                                if ((one.getFeature() instanceof CidsFeature)
                                            && (two.getFeature() instanceof CidsFeature)) {
                                    final CidsFeature cfOne = (CidsFeature)one.getFeature();
                                    final CidsFeature cfTwo = (CidsFeature)two.getFeature();
                                    final CidsBean cbOne = cfOne.getMetaObject().getBean();
                                    final CidsBean cbTwo = cfTwo.getMetaObject().getBean();

                                    if (CidsAppBackend.getInstance().getMode().equals(CidsAppBackend.Mode.REGEN)) {
                                        if (newGeom.getGeometryType().equalsIgnoreCase("Multipolygon")) {
                                            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                                                "Es k\u00F6nnen nur Polygone zusammengefasst werden, die aneinander angrenzen oder sich \u00FCberlappen.",
                                                "Zusammenfassung nicht m\u00F6glich",
                                                JOptionPane.WARNING_MESSAGE,
                                                null);
                                            return;
                                        }
                                        if (newGeom.getGeometryType().equalsIgnoreCase("Polygon")
                                                    && (((Polygon)newGeom).getNumInteriorRing() > 0)) {
                                            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                                                "Polygone k\u00F6nnen nur dann zusammengefasst werden, wenn dadurch kein Loch entsteht.",
                                                "Zusammenfassung nicht m\u00F6glich",
                                                JOptionPane.WARNING_MESSAGE,
                                                null);
                                            return;
                                        }
                                        final int artOne = (Integer)cbOne.getProperty(
                                                FlaechePropertyConstants.PROP__FLAECHENINFO
                                                        + "."
                                                        + FlaecheninfoPropertyConstants.PROP__FLAECHENART
                                                        + "."
                                                        + FlaechenartPropertyConstants.PROP__ID);
                                        final int artTwo = (Integer)cbTwo.getProperty(
                                                FlaechePropertyConstants.PROP__FLAECHENINFO
                                                        + "."
                                                        + FlaecheninfoPropertyConstants.PROP__FLAECHENART
                                                        + "."
                                                        + FlaechenartPropertyConstants.PROP__ID);

                                        final int gradOne = (Integer)cbOne.getProperty(
                                                FlaechePropertyConstants.PROP__FLAECHENINFO
                                                        + "."
                                                        + FlaecheninfoPropertyConstants.PROP__ANSCHLUSSGRAD
                                                        + "."
                                                        + AnschlussgradPropertyConstants.PROP__ID);
                                        final int gradTwo = (Integer)cbTwo.getProperty(
                                                FlaechePropertyConstants.PROP__FLAECHENINFO
                                                        + "."
                                                        + FlaecheninfoPropertyConstants.PROP__ANSCHLUSSGRAD
                                                        + "."
                                                        + AnschlussgradPropertyConstants.PROP__ID);

                                        if ((artOne != artTwo) || (gradOne != gradTwo)) {
                                            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                                                "Fl\u00E4chen k\u00F6nnen nur zusammengefasst werden, wenn Fl\u00E4chenart und Anschlussgrad gleich sind.",
                                                "Zusammenfassung nicht m\u00F6glich",
                                                JOptionPane.WARNING_MESSAGE,
                                                null);
                                            return;
                                        }

                                        final Integer anteilOne = (Integer)cbOne.getProperty(
                                                FlaechePropertyConstants.PROP__ANTEIL);
                                        final Integer anteilTwo = (Integer)cbTwo.getProperty(
                                                FlaechePropertyConstants.PROP__ANTEIL);

                                        // Check machen ob eine Fl\u00E4che eine Teilfl\u00E4che ist
                                        if ((anteilOne != null) || (anteilTwo != null)) {
                                            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                                                "Fl\u00E4chen die von Teileigentum betroffen sind k\u00F6nnen nicht zusammengefasst werden.",
                                                "Zusammenfassung nicht m\u00F6glich",
                                                JOptionPane.WARNING_MESSAGE,
                                                null);
                                            return;
                                        }

                                        Main.getInstance().getRegenFlaechenTable().removeBean(cbTwo);

                                        final boolean sperreOne = (cbOne.getProperty(
                                                    FlaechePropertyConstants.PROP__SPERRE) != null)
                                                    && (Boolean)cbOne.getProperty(
                                                        FlaechePropertyConstants.PROP__SPERRE);
                                        final boolean sperreTwo = (cbTwo.getProperty(
                                                    FlaechePropertyConstants.PROP__SPERRE) != null)
                                                    && (Boolean)cbTwo.getProperty(
                                                        FlaechePropertyConstants.PROP__SPERRE);

                                        if (!sperreOne && sperreTwo) {
                                            cbOne.setProperty(FlaechePropertyConstants.PROP__SPERRE, true);
                                            cbOne.setProperty(
                                                FlaechePropertyConstants.PROP__BEMERKUNG_SPERRE,
                                                "JOIN::"
                                                        + cbTwo.getProperty(
                                                            FlaechePropertyConstants.PROP__BEMERKUNG_SPERRE));
                                        }
                                        // Eine vorhandene Fl\u00E4che und eine neuangelegt wurden gejoint
                                        RegenFlaechenDetailsPanel.setGeometry(newGeom, cbOne);
                                        final int groesse = (int)newGeom.getArea();
                                        cbOne.setProperty(FlaechePropertyConstants.PROP__FLAECHENINFO + "."
                                                    + FlaecheninfoPropertyConstants.PROP__GROESSE_GRAFIK,
                                            groesse);
                                        cbOne.setProperty(FlaechePropertyConstants.PROP__FLAECHENINFO + "."
                                                    + FlaecheninfoPropertyConstants.PROP__GROESSE_KORREKTUR,
                                            groesse);
                                        if (LOG.isDebugEnabled()) {
                                            LOG.debug("newGeom ist vom Typ:" + newGeom.getGeometryType());
                                        }
                                    } else if (CidsAppBackend.getInstance().getMode().equals(CidsAppBackend.Mode.SR)) {
                                        final LineMerger lm = new LineMerger();
                                        lm.add(one.getFeature().getGeometry());
                                        lm.add(two.getFeature().getGeometry());

                                        final Collection<Geometry> resLs = lm.getMergedLineStrings();
                                        if (resLs.size() > 1) {
                                            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                                                "Es können nur Fronten zusammengefasst werden,\n"
                                                        + "die sich berühren oder sich überlappen.",
                                                "Zusammenfassung nicht m\u00F6glich",
                                                JOptionPane.WARNING_MESSAGE,
                                                null);
                                            return;
                                        }

                                        final Collection<CrossReference> oneCrossRefs = CidsAppBackend.getInstance()
                                                    .getFrontenCrossReferencesForFrontid((Integer)cbOne.getProperty(
                                                            FrontPropertyConstants.PROP__ID));
                                        final Collection<CrossReference> twoCrossRefs = CidsAppBackend.getInstance()
                                                    .getFrontenCrossReferencesForFrontid((Integer)cbTwo.getProperty(
                                                            FrontPropertyConstants.PROP__ID));

                                        final Collection<Integer> oneCrossRefkassenzeichenList =
                                            new ArrayList<Integer>();
                                        if (oneCrossRefs != null) {
                                            for (final CrossReference crossRef : oneCrossRefs) {
                                                oneCrossRefkassenzeichenList.add(crossRef.getEntityToKassenzeichen());
                                            }
                                        }
                                        final Collection<Integer> twoCrossRefkassenzeichenList =
                                            new ArrayList<Integer>();
                                        if (twoCrossRefs != null) {
                                            for (final CrossReference crossRef : twoCrossRefs) {
                                                twoCrossRefkassenzeichenList.add(crossRef.getEntityToKassenzeichen());
                                            }
                                        }
                                        final boolean sameCrossRefs = (oneCrossRefkassenzeichenList.size()
                                                        == twoCrossRefkassenzeichenList.size())
                                                    && oneCrossRefkassenzeichenList.containsAll(
                                                        twoCrossRefkassenzeichenList);
                                        if (!sameCrossRefs) {
                                            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                                                "Fronten können nur zusammengefasst werden, wenn die\n"
                                                        + "Querverweise auf das selbe Kassenzeichen verweisen.",
                                                "Zusammenfassung nicht möglich",
                                                JOptionPane.WARNING_MESSAGE,
                                                null);
                                            return;
                                        }

                                        final Integer lageOne = (Integer)cbOne.getProperty(
                                                FrontPropertyConstants.PROP__FRONTINFO
                                                        + "."
                                                        + FrontinfoPropertyConstants.PROP__LAGE_SR
                                                        + "."
                                                        + FlaechenartPropertyConstants.PROP__ID);
                                        final Integer lageTwo = (Integer)cbTwo.getProperty(
                                                FrontPropertyConstants.PROP__FRONTINFO
                                                        + "."
                                                        + FrontinfoPropertyConstants.PROP__LAGE_SR
                                                        + "."
                                                        + FlaechenartPropertyConstants.PROP__ID);

                                        final Integer strasseOne = (Integer)cbOne.getProperty(
                                                FrontPropertyConstants.PROP__FRONTINFO
                                                        + "."
                                                        + FrontinfoPropertyConstants.PROP__STRASSE
                                                        + "."
                                                        + AnschlussgradPropertyConstants.PROP__ID);
                                        final Integer strasseTwo = (Integer)cbTwo.getProperty(
                                                FrontPropertyConstants.PROP__FRONTINFO
                                                        + "."
                                                        + FrontinfoPropertyConstants.PROP__STRASSE
                                                        + "."
                                                        + AnschlussgradPropertyConstants.PROP__ID);
                                        final Integer reinigungOne = (Integer)cbOne.getProperty(
                                                FrontPropertyConstants.PROP__FRONTINFO
                                                        + "."
                                                        + FrontinfoPropertyConstants.PROP__SR_KLASSE_OR
                                                        + "."
                                                        + AnschlussgradPropertyConstants.PROP__ID);
                                        final Integer reinigungTwo = (Integer)cbTwo.getProperty(
                                                FrontPropertyConstants.PROP__FRONTINFO
                                                        + "."
                                                        + FrontinfoPropertyConstants.PROP__SR_KLASSE_OR
                                                        + "."
                                                        + AnschlussgradPropertyConstants.PROP__ID);

                                        final boolean sameLage = Integer.compare((lageOne != null) ? lageOne : -1,
                                                (lageTwo != null) ? lageTwo : -1) == 0;
                                        final boolean sameStrasse = Integer.compare((strasseOne != null) ? strasseOne
                                                                                                         : -1,
                                                (strasseTwo != null) ? strasseTwo : -1) == 0;
                                        final boolean sameReinigung = Integer.compare((reinigungOne != null)
                                                    ? reinigungOne : -1,
                                                (reinigungTwo != null) ? reinigungTwo : -1) == 0;
                                        if (!sameLage || !sameStrasse || !sameReinigung) {
                                            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                                                "Fronten können nur zusammengefasst werden, wenn Straße, Lage und Straßenreinigung gleich sind.",
                                                "Zusammenfassung nicht möglich",
                                                JOptionPane.WARNING_MESSAGE,
                                                null);
                                            return;
                                        }

                                        Main.getInstance().getSRFrontenTable().removeBean(cbTwo);

                                        // Eine vorhandene Front und eine neuangelegt wurden gejoint
                                        SRFrontenDetailsPanel.setGeometry(newGeom, cbOne);
                                        final int length = (int)newGeom.getLength();
                                        cbOne.setProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                                                    + FrontinfoPropertyConstants.PROP__LAENGE_GRAFIK,
                                            length);
                                        cbOne.setProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                                                    + FrontinfoPropertyConstants.PROP__LAENGE_KORREKTUR,
                                            length);
                                    }
                                    one.getFeature().setGeometry(newGeom);
                                    if (!(one.getFeature().getGeometry().equals(backup))) {
                                        two.removeFromParent();
                                        two = null;
                                    }
                                    one.visualize();
                                }
                            } catch (Exception e) {
                                LOG.error("one: " + one + "\n two: " + two, e);
                            }
                            mappingComp.getFeatureCollection().select(one.getFeature());
                        }
                    } else {
                        final PFeature pf = joinCandidate;
                        if (one != null) {
                            one.setSelected(false);
                        }
                        one = pf;
                        if (one.getFeature() instanceof CidsFeature) {
                            final CidsFeature flaecheFeature = (CidsFeature)one.getFeature();
                            mappingComp.getFeatureCollection().select(flaecheFeature);
                        } else {
                            mappingComp.getFeatureCollection().unselectAll();
                        }
                    }
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public CidsBean getCidsBean() {
        return kassenzeichenBean;
    }

    /**
     * Draws the features of the <Code>kassenzeichenBean</Code> for the selected mode (Allgemein, Regen, ESW) in the
     * map. If withZoom is true, then zooms to the position of the Kassenzeichen in the map (if possible).
     *
     * @param  withZoom  true, zooms to the position of the Kassenzeichen
     */
    private void refreshInMap(final boolean withZoom) {
        final FeatureCollection featureCollection = mappingComp.getFeatureCollection();
        final boolean editable = CidsAppBackend.getInstance().isEditable();

        featureCollection.removeAllFeatures();

        final CidsBean cidsBean = getCidsBean();
        if (cidsBean != null) {
            switch (CidsAppBackend.getInstance().getMode()) {
                case ALLGEMEIN: {
                    featureCollection.addFeatures(fetchFeaturesAllgemein(cidsBean, editable));
                }
                break;
                case REGEN: {
                    featureCollection.addFeatures(fetchFeaturesRegen(cidsBean, editable));
                }
                break;
                case SR: {
                    featureCollection.addFeatures(fetchFeaturesESW(cidsBean, editable));
                }
                break;
                case KANALDATEN: {
                    featureCollection.addFeatures(fetchFeaturesBefreiungerlaubnisGeometrie(cidsBean, editable));
                }
                break;
            }
            if (withZoom) {
                final CidsAppBackend.Mode mode = CidsAppBackend.getInstance().getMode();
                if ((featureCollection.getFeatureCount() != 0)) {
                    mappingComp.zoomToAFeatureCollection(featureCollection.getAllFeatures(),
                        true,
                        mappingComp.isFixedMapScale());
                } else {
                    /* No features found for the currently selected mode, try to fetch features for the Kassenzeichen
                     * but from other modes.
                     * To not draw the features in the map, a new FeatureCollection must be used.*/
                    final FeatureCollection zoomingFeatureCollection = new DefaultFeatureCollection();
                    if (CidsAppBackend.Mode.ALLGEMEIN.equals(mode)) {
                        zoomingFeatureCollection.addFeatures(fetchFeaturesRegen(cidsBean, editable));
                        if (zoomingFeatureCollection.getFeatureCount() == 0) {
                            zoomingFeatureCollection.addFeatures(fetchFeaturesESW(cidsBean, editable));
                        }
                    }
                    if (CidsAppBackend.Mode.REGEN.equals(mode)) {
                        zoomingFeatureCollection.addFeatures(fetchFeaturesAllgemein(cidsBean, editable));
                        if (zoomingFeatureCollection.getFeatureCount() == 0) {
                            zoomingFeatureCollection.addFeatures(fetchFeaturesESW(cidsBean, editable));
                        }
                    }
                    if (CidsAppBackend.Mode.SR.equals(mode)) {
                        zoomingFeatureCollection.addFeatures(fetchFeaturesRegen(cidsBean, editable));
                        if (zoomingFeatureCollection.getFeatureCount() == 0) {
                            zoomingFeatureCollection.addFeatures(fetchFeaturesAllgemein(cidsBean, editable));
                        }
                    }
                    mappingComp.zoomToAFeatureCollection(zoomingFeatureCollection.getAllFeatures(),
                        true,
                        mappingComp.isFixedMapScale());
                }
            }
        }
    }

    /**
     * Fetches and returns the features from a Kassenzeichen CidsBean for the mode Allgemein (also called Info).
     *
     * @param   kassenzeichen  a Kassenzeichen CidsBean
     * @param   editable       should the features be editable or not
     *
     * @return  DOCUMENT ME!
     */
    private Collection<Feature> fetchFeaturesAllgemein(final CidsBean kassenzeichen, final boolean editable) {
        final ArrayList<Feature> featureCollection = new ArrayList<Feature>();

        final Collection<CidsBean> kassenzeichenGeometrieBeans = kassenzeichen.getBeanCollectionProperty(
                KassenzeichenPropertyConstants.PROP__KASSENZEICHEN_GEOMETRIEN);
        for (final CidsBean kassenzeichenGeometrieBean : kassenzeichenGeometrieBeans) {
            final Geometry geom = (Geometry)kassenzeichenGeometrieBean.getProperty(
                    KassenzeichenGeometriePropertyConstants.PROP__GEOMETRIE
                            + "."
                            + GeomPropertyConstants.PROP__GEO_FIELD);
            if (geom != null) {
                final Feature add = new BeanUpdatingCidsFeature(
                        kassenzeichenGeometrieBean,
                        KassenzeichenGeometriePropertyConstants.PROP__GEOMETRIE
                                + "."
                                + GeomPropertyConstants.PROP__GEO_FIELD);
                add.setEditable(editable);
                featureCollection.add(add);
            }
        }

        return featureCollection;
    }

    /**
     * Fetches and returns the features from a Kassenzeichen CidsBean for the mode Regen (also called versiegelte
     * Flächen).
     *
     * @param   kassenzeichen  a Kassenzeichen CidsBean
     * @param   editable       should the features be editable or not
     *
     * @return  DOCUMENT ME!
     */
    private Collection<Feature> fetchFeaturesRegen(final CidsBean kassenzeichen, final boolean editable) {
        final ArrayList<Feature> featureCollection = new ArrayList<Feature>();
        final List<CidsBean> flaechen = (List<CidsBean>)kassenzeichen.getProperty(
                KassenzeichenPropertyConstants.PROP__FLAECHEN);
        for (final CidsBean flaeche : flaechen) {
            final Geometry geom = (Geometry)flaeche.getProperty(FlaechePropertyConstants.PROP__FLAECHENINFO
                            + "."
                            + FlaecheninfoPropertyConstants.PROP__GEOMETRIE
                            + "."
                            + GeomPropertyConstants.PROP__GEO_FIELD);
            if (geom != null) {
                final Feature add = new BeanUpdatingCidsFeature(
                        flaeche,
                        FlaechePropertyConstants.PROP__FLAECHENINFO
                                + "."
                                + FlaecheninfoPropertyConstants.PROP__GEOMETRIE
                                + "."
                                + GeomPropertyConstants.PROP__GEO_FIELD);
                add.setEditable(editable);
                featureCollection.add(add);
            }
        }
        return featureCollection;
    }

    /**
     * Fetches and returns the features from a Kassenzeichen CidsBean for the mode ESW.
     *
     * @param   cidsBean  a Kassenzeichen CidsBean
     * @param   editable  should the features be editable or not
     *
     * @return  DOCUMENT ME!
     */
    private Collection<Feature> fetchFeaturesESW(final CidsBean cidsBean, final boolean editable) {
        final ArrayList<Feature> featureCollection = new ArrayList<Feature>();
        final List<CidsBean> fronten = (List<CidsBean>)cidsBean.getProperty(
                KassenzeichenPropertyConstants.PROP__FRONTEN);
        for (final CidsBean front : fronten) {
            final Geometry geom = (Geometry)front.getProperty(FrontPropertyConstants.PROP__FRONTINFO
                            + "."
                            + FrontinfoPropertyConstants.PROP__GEOMETRIE
                            + "."
                            + GeomPropertyConstants.PROP__GEO_FIELD);
            if (geom != null) {
                final Feature add = new BeanUpdatingCidsFeature(
                        front,
                        FrontPropertyConstants.PROP__FRONTINFO
                                + "."
                                + FrontinfoPropertyConstants.PROP__GEOMETRIE
                                + "."
                                + GeomPropertyConstants.PROP__GEO_FIELD);
                add.setEditable(editable);
                featureCollection.add(add);
            }
        }
        return featureCollection;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   cidsBean  DOCUMENT ME!
     * @param   editable  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Collection<Feature> fetchFeaturesBefreiungerlaubnisGeometrie(final CidsBean cidsBean,
            final boolean editable) {
        final ArrayList<Feature> featureCollection = new ArrayList<Feature>();
        final List<CidsBean> befers = cidsBean.getBeanCollectionProperty(VerdisMetaClassConstants.MC_KANALANSCHLUSS
                        + "." + KanalanschlussPropertyConstants.PROP__BEFREIUNGENUNDERLAUBNISSE);
        for (final CidsBean befer : befers) {
            for (final CidsBean beferGeom
                        : befer.getBeanCollectionProperty(BefreiungerlaubnisPropertyConstants.PROP__GEOMETRIEN)) {
                final Geometry geom = (Geometry)beferGeom.getProperty(
                        BefreiungerlaubnisGeometriePropertyConstants.PROP__GEOMETRIE
                                + "."
                                + GeomPropertyConstants.PROP__GEO_FIELD);
                if (geom != null) {
                    final Feature add = new BeanUpdatingCidsFeature(
                            beferGeom,
                            BefreiungerlaubnisGeometriePropertyConstants.PROP__GEOMETRIE
                                    + "."
                                    + GeomPropertyConstants.PROP__GEO_FIELD);
                    add.setEditable(editable);
                    featureCollection.add(add);
                }
            }
        }
        return featureCollection;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        final CidsBean oldCidsBean = kassenzeichenBean;

        kassenzeichenBean = cidsBean;

        refreshInMap(!Main.getInstance().isFixMapExtent() && (cidsBean != null)
                    && (!cidsBean.equals(oldCidsBean)
                        || (TimeRecoveryPanel.getInstance().isLoading())));
    }

    /**
     * DOCUMENT ME!
     */
    @Override
    public void editModeChanged() {
        final boolean b = CidsAppBackend.getInstance().isEditable();
        final List<Feature> all = mappingComp.getFeatureCollection().getAllFeatures();
        for (final Feature f : all) {
            f.setEditable(b);
        }
        CidsAppBackend.getInstance().getMainMap().setReadOnly(!b);
    }

    /**
     * DOCUMENT ME!
     */
    @Override
    public void appModeChanged() {
        refreshInMap(!Main.getInstance().isFixMapExtent() && !Main.getInstance().isFixMapExtentMode());
        lblMeasurement.setText("");
        final CidsAppBackend.Mode mode = CidsAppBackend.getInstance().getMode();
        final SimpleMoveListener simpleMoveListener = (SimpleMoveListener)mappingComp.getInputListener(
                MappingComponent.MOTION);
        if (mode.equals(mode.SR)) {
            simpleMoveListener.setUnderlyingObjectHalo(0.0010d);
        } else {
            simpleMoveListener.setUnderlyingObjectHalo(0.0d);
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void changeSelectedButtonAccordingToInteractionMode() {
        final String im = mappingComp.getInteractionMode();
        final String him = mappingComp.getHandleInteractionMode();

        if (LOG.isDebugEnabled()) {
            LOG.debug("changeSelectedButtonAccordingToInteractionMode: " + mappingComp.getInteractionMode(),
                new CurrentStackTrace());
        }
        if (MappingComponent.ZOOM.equals(im)) {
            cmdZoom.setSelected(true);
        }
        if (MappingComponent.PAN.equals(im)) {
            cmdPan.setSelected(true);
        } else if (MappingComponent.SELECT.equals(im)) {
            cmdSelect.setSelected(true);
        } else if (MappingComponent.NEW_POLYGON.equals(im)) {
            final String npm = ((CreateGeometryListener)mappingComp.getInputListener(MappingComponent.NEW_POLYGON))
                        .getMode();
            if (CreateGeometryListener.POLYGON.equals(npm)) {
                cmdNewPolygon.setSelected(true);
            } else if (CreateGeometryListener.LINESTRING.equals(npm)) {
                cmdNewLinestring.setSelected(true);
            } else if (CreateGeometryListener.POINT.equals(npm)) {
                cmdNewPoint.setSelected(true);
            }
        } else {
            cmdSelect.setSelected(true);
        }

        if (MappingComponent.MOVE_HANDLE.equals(him)) {
            if (!cmdMoveHandle.isSelected()) {
                cmdMoveHandle.setSelected(true);
            }
        } else if (MappingComponent.ADD_HANDLE.equals(him)) {
            if (!cmdAddHandle.isSelected()) {
                cmdAddHandle.setSelected(true);
            }
        } else if (MappingComponent.REMOVE_HANDLE.equals(him)) {
            if (!cmdRemoveHandle.isSelected()) {
                cmdRemoveHandle.setSelected(true);
            }
        } else if (MappingComponent.ROTATE_POLYGON.equals(him)) {
            if (!cmdRotatePolygon.isSelected()) {
                cmdRotatePolygon.setSelected(true);
            }
        }

        if (mappingComp.isSnappingEnabled()) {
            cmdSnap.setSelected(true);
        } else {
            cmdSnap.setSelected(false);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  notfication  DOCUMENT ME!
     */
    public void doubleClickPerformed(final PNotification notfication) {
        final Object o = notfication.getObject();
        if ((o instanceof SelectionListener)
                    && !mappingComp.getHandleInteractionMode().equals(MappingComponent.ADD_HANDLE)) {
            final SelectionListener selectionListener = (SelectionListener)o;
            final Geometry searchGeom = selectionListener.getDoubleclickPoint();

            if (searchGeom != null) {
                final KassenzeichenGeomSearch geomSearch = new KassenzeichenGeomSearch();
                geomSearch.setScaleDenominator(CismapBroker.getInstance().getMappingComponent().getScaleDenominator());
                geomSearch.setFlaecheFilter(CidsAppBackend.getInstance().getMode().equals(
                        CidsAppBackend.Mode.REGEN));
                geomSearch.setFrontFilter(CidsAppBackend.getInstance().getMode().equals(CidsAppBackend.Mode.SR));
                geomSearch.setAllgemeinFilter(CidsAppBackend.getInstance().getMode().equals(
                        CidsAppBackend.Mode.ALLGEMEIN));

                final ServerSearchCreateSearchGeometryListener serverSearchCreateSearchGeometryListener =
                    new ServerSearchCreateSearchGeometryListener(
                        mappingComp,
                        geomSearch);
                serverSearchCreateSearchGeometryListener.setMode(CreateGeometryListener.POINT);
                serverSearchCreateSearchGeometryListener.addPropertyChangeListener(new PropertyChangeListener() {

                        @Override
                        public void propertyChange(final PropertyChangeEvent evt) {
                            if (ServerSearchCreateSearchGeometryListener.ACTION_SEARCH_DONE.equals(
                                            evt.getPropertyName())) {
                                final Collection<Integer> result = (Collection<Integer>)evt.getNewValue();
                                if (!result.isEmpty()) {
                                    final Integer kassenzeichennummer = result.iterator().next();
                                    final CidsBean kassenzeichenBean = CidsAppBackend.getInstance().getCidsBean();
                                    if ((kassenzeichennummer != null)
                                                && ((kassenzeichenBean == null)
                                                    || !kassenzeichennummer.equals(
                                                        (Integer)kassenzeichenBean.getProperty(
                                                            KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER)))) {
                                        CidsAppBackend.getInstance().gotoKassenzeichen(kassenzeichennummer.toString());
                                    }
                                }
                            }
                        }
                    });

                final SearchFeature newFeature = new SearchFeature(
                        searchGeom,
                        ServerSearchCreateSearchGeometryListener.INPUT_LISTENER_NAME);
                newFeature.setEditable(false);
                newFeature.setGeometryType(AbstractNewFeature.geomTypes.POINT);
                newFeature.setCanBeSelected(false);
                // serverSearchCreateSearchGeometryListener.sh
                serverSearchCreateSearchGeometryListener.search(newFeature);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  notfication  DOCUMENT ME!
     */
    public void selectionChanged(final PNotification notfication) {
    }

    /**
     * DOCUMENT ME!
     *
     * @param  notification  DOCUMENT ME!
     */
    public void splitPolygon(final PNotification notification) {
        if (true) {
            final Object o = notification.getObject();
            if (o instanceof SplitPolygonListener) {
                final SplitPolygonListener l = (SplitPolygonListener)o;
                final PFeature pf = l.getFeatureClickedOn();
                if (pf.getFeature() instanceof CidsFeature) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Split");
                    }
                    final CidsFeature cf = (CidsFeature)pf.getFeature();
                    final CidsBean cb = cf.getMetaObject().getBean();
                    if (VerdisMetaClassConstants.MC_FLAECHE.equalsIgnoreCase(
                                    cb.getMetaObject().getMetaClass().getTableName())) {
                        try {
                            cb.setProperty(FlaechePropertyConstants.PROP__FLAECHENINFO + "."
                                        + FlaecheninfoPropertyConstants.PROP__GEOMETRIE,
                                null);
                        } catch (Exception ex) {
                            LOG.error("error while removing geometry", ex);
                        }

                        CidsAppBackend.getInstance().setLastSplitFlaecheId(cb.getMetaObject().getId());
                    } else if (VerdisMetaClassConstants.MC_FRONT.equalsIgnoreCase(
                                    cb.getMetaObject().getMetaClass().getTableName())) {
                        try {
                            cb.setProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                                        + FrontinfoPropertyConstants.PROP__GEOMETRIE,
                                null);
                        } catch (Exception ex) {
                            LOG.error("error while removing geometry", ex);
                        }

                        CidsAppBackend.getInstance().setLastSplitFrontId(cb.getMetaObject().getId());
                    }
                }
                final Feature[] f_arr = pf.split();
                if (f_arr != null) {
                    mappingComp.getFeatureCollection().removeFeature(pf.getFeature());
                    final boolean editable = CidsAppBackend.getInstance().isEditable();
                    f_arr[0].setEditable(editable);
                    f_arr[1].setEditable(editable);
                    mappingComp.getFeatureCollection().addFeature(f_arr[0]);
                    mappingComp.getFeatureCollection().addFeature(f_arr[1]);
                    cmdAttachPolyToAlphadataActionPerformed(null);
                }
            }
        }
    }
}
