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

import de.cismet.verdis.constants.KassenzeichenPropertyConstants;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import edu.umd.cs.piccolox.event.PNotification;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;
import de.cismet.cismap.commons.CrsTransformer;

import de.cismet.cismap.commons.ServiceLayer;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.features.FeatureCollection;
import de.cismet.cismap.commons.features.FeatureCollectionEvent;
import de.cismet.cismap.commons.features.FeatureCollectionListener;
import de.cismet.cismap.commons.features.PostgisFeature;
import de.cismet.cismap.commons.features.PureNewFeature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.piccolo.PFeature;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.CreateGeometryListener;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.CreateSimpleGeometryListener;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.DeleteFeatureListener;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.FeatureMoveListener;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.JoinPolygonsListener;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.SelectionListener;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.SimpleMoveListener;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.SplitPolygonListener;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.actions.CustomAction;
import de.cismet.cismap.commons.interaction.CismapBroker;
import de.cismet.cismap.commons.interaction.memento.MementoInterface;
import de.cismet.cismap.commons.retrieval.RetrievalEvent;
import de.cismet.cismap.commons.retrieval.RetrievalListener;

import de.cismet.cismap.navigatorplugin.BeanUpdatingCidsFeature;
import de.cismet.cismap.navigatorplugin.CidsFeature;

import de.cismet.gui.tools.PureNewFeatureWithThickerLineString;
import de.cismet.tools.CurrentStackTrace;

import de.cismet.tools.StaticDecimalTools;

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.historybutton.JHistoryButton;

import de.cismet.verdis.AppModeListener;
import de.cismet.verdis.CidsAppBackend;
import de.cismet.verdis.EditModeListener;
import de.cismet.verdis.constants.RegenFlaechenPropertyConstants;
import de.cismet.verdis.constants.VerdisMetaClassConstants;
import de.cismet.verdis.constants.WDSRPropertyConstants;
import edu.umd.cs.piccolo.PCamera;
import java.awt.Event;
import java.sql.Date;
import java.util.ArrayList;

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
        AppModeListener {

    //~ Instance fields --------------------------------------------------------
    final private HashSet activeRetrievalServices = new HashSet();
    private CidsBean kassenzeichenBean = null;
    private final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KartenPanel.class);
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdALB;
    private javax.swing.JButton cmdAdd;
    private javax.swing.JButton cmdAddHandle;
    private javax.swing.JButton cmdAttachPolyToAlphadata;
    private javax.swing.JButton cmdBack;
    private javax.swing.JButton cmdForeground;
    private javax.swing.JButton cmdForward;
    private javax.swing.JButton cmdFullPoly;
    private javax.swing.JButton cmdFullPoly1;
    private javax.swing.JButton cmdJoinPoly;
    private javax.swing.JButton cmdMoveHandle;
    private javax.swing.JButton cmdMovePolygon;
    private javax.swing.JButton cmdNewLinestring;
    private javax.swing.JButton cmdNewPoint;
    private javax.swing.JButton cmdNewPolygon;
    private javax.swing.JButton cmdOrthogonalRectangle;
    private javax.swing.JButton cmdPan;
    private javax.swing.JButton cmdRaisePolygon;
    private javax.swing.JButton cmdRedo;
    private javax.swing.JButton cmdRemoveHandle;
    private javax.swing.JButton cmdRemovePolygon;
    private javax.swing.JButton cmdRotatePolygon;
    private javax.swing.JButton cmdSearchFlurstueck;
    private javax.swing.JButton cmdSelect;
    private javax.swing.JButton cmdSnap;
    private javax.swing.JButton cmdSplitPoly;
    private javax.swing.JButton cmdUndo;
    private javax.swing.JButton cmdWmsBackground;
    private javax.swing.JButton cmdZoom;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JLabel lblCoord;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JLabel lblMeasurement;
    private javax.swing.JLabel lblScale;
    private javax.swing.JLabel lblWaiting;
    private de.cismet.cismap.commons.gui.MappingComponent mappingComp;
    private javax.swing.JPanel panMap;
    private javax.swing.JPanel panStatus;
    private javax.swing.JPopupMenu pomScale;
    private javax.swing.JSeparator sep2;
    private javax.swing.JSeparator sep3;
    private javax.swing.JSeparator sep4;
    private javax.swing.JToolBar tobVerdis;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------
    /**
     * Creates new form KartenPanel.
     */
    public KartenPanel() {
        initComponents();

        CidsAppBackend.getInstance().setMainMap(mappingComp);
        CismapBroker.getInstance().setMappingComponent(mappingComp);

        mappingComp.getFeatureCollection().addFeatureCollectionListener(this);
        // mappingComp.putInputListener(MappingComponent.ATTACH_POLYGON_TO_ALPHADATA, new AttachFeatureListener());

        mappingComp.setBackgroundEnabled(true);

        cmdSelect.setSelected(true);

//        CreateGeometryListener g=new CreateGeometryListener(mappingComp,JLabel.class) {
//
//
//        };
        // mappingComp.addInputListener("TIM_EASY_CREATOR",)

        // TIM Easy
        cmdNewPoint.setVisible(true);

        ((JHistoryButton) cmdForward).setDirection(JHistoryButton.DIRECTION_FORWARD);
        ((JHistoryButton) cmdBack).setDirection(JHistoryButton.DIRECTION_BACKWARD);
        ((JHistoryButton) cmdForward).setHistoryModel(mappingComp);
        ((JHistoryButton) cmdBack).setHistoryModel(mappingComp);

        cmdWmsBackground.setSelected(mappingComp.isBackgroundEnabled());

        mappingComp.getCamera().addPropertyChangeListener(
                PCamera.PROPERTY_VIEW_TRANSFORM,
                new PropertyChangeListener() {

                    @Override
                    public void propertyChange(final PropertyChangeEvent evt) {
                        final int sd = (int) (mappingComp.getScaleDenominator() + 0.5);
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
            LOG.debug("Fl\u00E4chen\u00DCbersichtsTabellenPanel als Observer anmelden");
        }
        ((Observable) mappingComp.getMemUndo()).addObserver(this);
        ((Observable) mappingComp.getMemRedo()).addObserver(this);

//        if (mappingComp.getFeatureCollection() instanceof DefaultFeatureCollection) {
//            ((DefaultFeatureCollection) mappingComp.getFeatureCollection()).setSingleSelection(true);
//        }
    }

    //~ Methods ----------------------------------------------------------------
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pomScale = new javax.swing.JPopupMenu();
        panMap = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        mappingComp = new de.cismet.cismap.commons.gui.MappingComponent();
        panStatus = new javax.swing.JPanel();
        cmdAdd = new javax.swing.JButton();
        lblInfo = new javax.swing.JLabel();
        lblCoord = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        lblWaiting = new javax.swing.JLabel();
        lblMeasurement = new javax.swing.JLabel();
        lblScale = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        tobVerdis = new javax.swing.JToolBar();
        cmdFullPoly = new javax.swing.JButton();
        cmdFullPoly1 = new javax.swing.JButton();
        cmdBack = new JHistoryButton();
        cmdForward = new JHistoryButton();
        jSeparator5 = new javax.swing.JSeparator();
        cmdWmsBackground = new javax.swing.JButton();
        cmdForeground = new javax.swing.JButton();
        cmdSnap = new javax.swing.JButton();
        sep2 = new javax.swing.JSeparator();
        cmdZoom = new javax.swing.JButton();
        cmdPan = new javax.swing.JButton();
        cmdSelect = new javax.swing.JButton();
        cmdALB = new javax.swing.JButton();
        cmdMovePolygon = new javax.swing.JButton();
        cmdNewPolygon = new javax.swing.JButton();
        cmdNewLinestring = new javax.swing.JButton();
        cmdNewPoint = new javax.swing.JButton();
        cmdOrthogonalRectangle = new javax.swing.JButton();
        cmdSearchFlurstueck = new javax.swing.JButton();
        cmdRaisePolygon = new javax.swing.JButton();
        cmdRemovePolygon = new javax.swing.JButton();
        cmdAttachPolyToAlphadata = new javax.swing.JButton();
        cmdJoinPoly = new javax.swing.JButton();
        cmdSplitPoly = new javax.swing.JButton();
        sep3 = new javax.swing.JSeparator();
        cmdMoveHandle = new javax.swing.JButton();
        cmdAddHandle = new javax.swing.JButton();
        cmdRemoveHandle = new javax.swing.JButton();
        cmdRotatePolygon = new javax.swing.JButton();
        sep4 = new javax.swing.JSeparator();
        cmdUndo = new javax.swing.JButton();
        cmdRedo = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        panMap.setLayout(new java.awt.BorderLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2), javax.swing.BorderFactory.createEtchedBorder()), javax.swing.BorderFactory.createEmptyBorder(4, 4, 1, 4)));
        jPanel2.setLayout(new java.awt.BorderLayout());

        mappingComp.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        mappingComp.setInternalLayerWidgetAvailable(true);
        jPanel2.add(mappingComp, java.awt.BorderLayout.CENTER);

        panStatus.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 1, 1, 1));
        panStatus.setLayout(new java.awt.GridBagLayout());

        cmdAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/statusbar/layersman.png"))); // NOI18N
        cmdAdd.setBorderPainted(false);
        cmdAdd.setFocusPainted(false);
        cmdAdd.setMinimumSize(new java.awt.Dimension(25, 25));
        cmdAdd.setPreferredSize(new java.awt.Dimension(25, 25));
        cmdAdd.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/statusbar/layersman.png"))); // NOI18N
        cmdAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
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

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panStatus.add(jSeparator1, gridBagConstraints);

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
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblScaleMousePressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panStatus.add(lblScale, gridBagConstraints);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panStatus.add(jSeparator2, gridBagConstraints);

        jPanel2.add(panStatus, java.awt.BorderLayout.SOUTH);

        panMap.add(jPanel2, java.awt.BorderLayout.CENTER);

        tobVerdis.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        tobVerdis.setFloatable(false);
        tobVerdis.setRollover(true);

        cmdFullPoly.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/fullPoly.png"))); // NOI18N
        cmdFullPoly.setToolTipText("Zeige alle Flächen");
        cmdFullPoly.setFocusPainted(false);
        cmdFullPoly.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdFullPolyActionPerformed(evt);
            }
        });
        tobVerdis.add(cmdFullPoly);

        cmdFullPoly1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/fullSelPoly.png"))); // NOI18N
        cmdFullPoly1.setToolTipText("Zoom zur ausgewählten Fläche");
        cmdFullPoly1.setFocusPainted(false);
        cmdFullPoly1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdFullPoly1ActionPerformed(evt);
            }
        });
        tobVerdis.add(cmdFullPoly1);

        cmdBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/back.png"))); // NOI18N
        cmdBack.setToolTipText("Zurück");
        tobVerdis.add(cmdBack);

        cmdForward.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/fwd.png"))); // NOI18N
        cmdForward.setToolTipText("Vor");
        tobVerdis.add(cmdForward);

        jSeparator5.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator5.setMaximumSize(new java.awt.Dimension(2, 32767));
        tobVerdis.add(jSeparator5);

        cmdWmsBackground.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/map.png"))); // NOI18N
        cmdWmsBackground.setToolTipText("Hintergrund an/aus");
        cmdWmsBackground.setFocusPainted(false);
        cmdWmsBackground.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/map_on.png"))); // NOI18N
        cmdWmsBackground.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/map_on.png"))); // NOI18N
        cmdWmsBackground.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdWmsBackgroundActionPerformed(evt);
            }
        });
        tobVerdis.add(cmdWmsBackground);

        cmdForeground.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/foreground.png"))); // NOI18N
        cmdForeground.setToolTipText("Vordergrund an/aus");
        cmdForeground.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdForeground.setFocusable(false);
        cmdForeground.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdForeground.setSelected(true);
        cmdForeground.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/foreground_on.png"))); // NOI18N
        cmdForeground.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdForeground.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdForegroundActionPerformed(evt);
            }
        });
        tobVerdis.add(cmdForeground);

        cmdSnap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/snap.png"))); // NOI18N
        cmdSnap.setToolTipText("Snapping an/aus");
        cmdSnap.setFocusPainted(false);
        cmdSnap.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/snap_selected.png"))); // NOI18N
        cmdSnap.setSelected(true);
        cmdSnap.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/snap_selected.png"))); // NOI18N
        cmdSnap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSnapActionPerformed(evt);
            }
        });
        tobVerdis.add(cmdSnap);

        sep2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        sep2.setMaximumSize(new java.awt.Dimension(2, 32767));
        tobVerdis.add(sep2);

        cmdZoom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/zoom.png"))); // NOI18N
        cmdZoom.setToolTipText("Zoomen");
        cmdZoom.setFocusPainted(false);
        cmdZoom.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/zoom_selected.png"))); // NOI18N
        cmdZoom.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/zoom_selected.png"))); // NOI18N
        cmdZoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdZoomActionPerformed(evt);
            }
        });
        tobVerdis.add(cmdZoom);

        cmdPan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/move2.png"))); // NOI18N
        cmdPan.setToolTipText("Verschieben");
        cmdPan.setFocusPainted(false);
        cmdPan.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/move2_selected.png"))); // NOI18N
        cmdPan.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/move2_selected.png"))); // NOI18N
        cmdPan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdPanActionPerformed(evt);
            }
        });
        tobVerdis.add(cmdPan);

        cmdSelect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/select.png"))); // NOI18N
        cmdSelect.setToolTipText("Auswählen");
        cmdSelect.setFocusPainted(false);
        cmdSelect.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/select_selected.png"))); // NOI18N
        cmdSelect.setSelected(true);
        cmdSelect.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/select_selected.png"))); // NOI18N
        cmdSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelectActionPerformed(evt);
            }
        });
        tobVerdis.add(cmdSelect);

        cmdALB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/alb.png"))); // NOI18N
        cmdALB.setToolTipText("ALB");
        cmdALB.setFocusPainted(false);
        cmdALB.setFocusable(false);
        cmdALB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdALB.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/alb_selected.png"))); // NOI18N
        cmdALB.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/alb_selected.png"))); // NOI18N
        cmdALB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdALB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdALBActionPerformed(evt);
            }
        });
        tobVerdis.add(cmdALB);

        cmdMovePolygon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/movePoly.png"))); // NOI18N
        cmdMovePolygon.setToolTipText("Polygon verschieben");
        cmdMovePolygon.setFocusPainted(false);
        cmdMovePolygon.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/movePoly_selected.png"))); // NOI18N
        cmdMovePolygon.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/movePoly_selected.png"))); // NOI18N
        cmdMovePolygon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdMovePolygonActionPerformed(evt);
            }
        });
        tobVerdis.add(cmdMovePolygon);

        cmdNewPolygon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/newPoly.png"))); // NOI18N
        cmdNewPolygon.setToolTipText("neues Polygon");
        cmdNewPolygon.setFocusPainted(false);
        cmdNewPolygon.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/newPoly_selected.png"))); // NOI18N
        cmdNewPolygon.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/newPoly_selected.png"))); // NOI18N
        cmdNewPolygon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdNewPolygonActionPerformed(evt);
            }
        });
        tobVerdis.add(cmdNewPolygon);

        cmdNewLinestring.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/newLine.png"))); // NOI18N
        cmdNewLinestring.setToolTipText("neue Linie");
        cmdNewLinestring.setFocusPainted(false);
        cmdNewLinestring.setFocusable(false);
        cmdNewLinestring.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdNewLinestring.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/newLine_selected.png"))); // NOI18N
        cmdNewLinestring.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/newLine_selected.png"))); // NOI18N
        cmdNewLinestring.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdNewLinestring.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdNewLinestringActionPerformed(evt);
            }
        });
        tobVerdis.add(cmdNewLinestring);

        cmdNewPoint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/newPoint.png"))); // NOI18N
        cmdNewPoint.setToolTipText("neuer Punkt");
        cmdNewPoint.setFocusPainted(false);
        cmdNewPoint.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/newPoint_selected.png"))); // NOI18N
        cmdNewPoint.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/newPoint_selected.png"))); // NOI18N
        cmdNewPoint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdNewPointActionPerformed(evt);
            }
        });
        tobVerdis.add(cmdNewPoint);

        cmdOrthogonalRectangle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/rechtwDreieck.png"))); // NOI18N
        cmdOrthogonalRectangle.setToolTipText("Rechteckige Fläche");
        cmdOrthogonalRectangle.setFocusPainted(false);
        cmdOrthogonalRectangle.setFocusable(false);
        cmdOrthogonalRectangle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdOrthogonalRectangle.setMaximumSize(new java.awt.Dimension(28, 20));
        cmdOrthogonalRectangle.setPreferredSize(new java.awt.Dimension(28, 20));
        cmdOrthogonalRectangle.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/rechtwDreieck_selected.png"))); // NOI18N
        cmdOrthogonalRectangle.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/rechtwDreieck_selected.png"))); // NOI18N
        cmdOrthogonalRectangle.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdOrthogonalRectangle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdOrthogonalRectangleActionPerformed(evt);
            }
        });
        tobVerdis.add(cmdOrthogonalRectangle);

        cmdSearchFlurstueck.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/alk.png"))); // NOI18N
        cmdSearchFlurstueck.setToolTipText("Alkis Renderer");
        cmdSearchFlurstueck.setFocusPainted(false);
        cmdSearchFlurstueck.setFocusable(false);
        cmdSearchFlurstueck.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdSearchFlurstueck.setMaximumSize(new java.awt.Dimension(28, 20));
        cmdSearchFlurstueck.setPreferredSize(new java.awt.Dimension(28, 20));
        cmdSearchFlurstueck.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/alk_selected.png"))); // NOI18N
        cmdSearchFlurstueck.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/alk_selected.png"))); // NOI18N
        cmdSearchFlurstueck.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdSearchFlurstueck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSearchFlurstueckActionPerformed(evt);
            }
        });
        tobVerdis.add(cmdSearchFlurstueck);

        cmdRaisePolygon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/raisePoly.png"))); // NOI18N
        cmdRaisePolygon.setToolTipText("Polygon hochholen");
        cmdRaisePolygon.setFocusPainted(false);
        cmdRaisePolygon.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/raisePoly_selected.png"))); // NOI18N
        cmdRaisePolygon.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/raisePoly_selected.png"))); // NOI18N
        cmdRaisePolygon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdRaisePolygonActionPerformed(evt);
            }
        });
        tobVerdis.add(cmdRaisePolygon);

        cmdRemovePolygon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/removePoly.png"))); // NOI18N
        cmdRemovePolygon.setToolTipText("Polygon entfernen");
        cmdRemovePolygon.setFocusPainted(false);
        cmdRemovePolygon.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/removePoly_selected.png"))); // NOI18N
        cmdRemovePolygon.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/removePoly_selected.png"))); // NOI18N
        cmdRemovePolygon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdRemovePolygonActionPerformed(evt);
            }
        });
        tobVerdis.add(cmdRemovePolygon);

        cmdAttachPolyToAlphadata.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/polygonAttachment.png"))); // NOI18N
        cmdAttachPolyToAlphadata.setToolTipText("Polygon zuordnen");
        cmdAttachPolyToAlphadata.setFocusPainted(false);
        cmdAttachPolyToAlphadata.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/polygonAttachment_selected.png"))); // NOI18N
        cmdAttachPolyToAlphadata.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/polygonAttachment_selected.png"))); // NOI18N
        cmdAttachPolyToAlphadata.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdAttachPolyToAlphadataActionPerformed(evt);
            }
        });
        tobVerdis.add(cmdAttachPolyToAlphadata);

        cmdJoinPoly.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/joinPoly.png"))); // NOI18N
        cmdJoinPoly.setToolTipText("Polygone zusammenfassen");
        cmdJoinPoly.setFocusPainted(false);
        cmdJoinPoly.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/joinPoly_selected.png"))); // NOI18N
        cmdJoinPoly.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/joinPoly_selected.png"))); // NOI18N
        cmdJoinPoly.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdJoinPolyActionPerformed(evt);
            }
        });
        tobVerdis.add(cmdJoinPoly);

        cmdSplitPoly.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/splitPoly.png"))); // NOI18N
        cmdSplitPoly.setToolTipText("Polygon splitten");
        cmdSplitPoly.setFocusPainted(false);
        cmdSplitPoly.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/splitPoly_selected.png"))); // NOI18N
        cmdSplitPoly.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/splitPoly_selected.png"))); // NOI18N
        cmdSplitPoly.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSplitPolyActionPerformed(evt);
            }
        });
        tobVerdis.add(cmdSplitPoly);

        sep3.setOrientation(javax.swing.SwingConstants.VERTICAL);
        sep3.setMaximumSize(new java.awt.Dimension(2, 32767));
        tobVerdis.add(sep3);

        cmdMoveHandle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/moveHandle.png"))); // NOI18N
        cmdMoveHandle.setToolTipText("Handle verschieben");
        cmdMoveHandle.setFocusPainted(false);
        cmdMoveHandle.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/moveHandle_selected.png"))); // NOI18N
        cmdMoveHandle.setSelected(true);
        cmdMoveHandle.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/moveHandle_selected.png"))); // NOI18N
        cmdMoveHandle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdMoveHandleActionPerformed(evt);
            }
        });
        tobVerdis.add(cmdMoveHandle);

        cmdAddHandle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/addHandle.png"))); // NOI18N
        cmdAddHandle.setToolTipText("Handle hinzufügen");
        cmdAddHandle.setFocusPainted(false);
        cmdAddHandle.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/addHandle_selected.png"))); // NOI18N
        cmdAddHandle.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/addHandle_selected.png"))); // NOI18N
        cmdAddHandle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdAddHandleActionPerformed(evt);
            }
        });
        tobVerdis.add(cmdAddHandle);

        cmdRemoveHandle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/removeHandle.png"))); // NOI18N
        cmdRemoveHandle.setToolTipText("Handle entfernen");
        cmdRemoveHandle.setFocusPainted(false);
        cmdRemoveHandle.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/removeHandle_selected.png"))); // NOI18N
        cmdRemoveHandle.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/removeHandle_selected.png"))); // NOI18N
        cmdRemoveHandle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdRemoveHandleActionPerformed(evt);
            }
        });
        tobVerdis.add(cmdRemoveHandle);

        cmdRotatePolygon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/rotate16.png"))); // NOI18N
        cmdRotatePolygon.setToolTipText("Rotiere Polygon");
        cmdRotatePolygon.setFocusPainted(false);
        cmdRotatePolygon.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/rotate16.png"))); // NOI18N
        cmdRotatePolygon.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/rotate16_selected.png"))); // NOI18N
        cmdRotatePolygon.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/rotate16_selected.png"))); // NOI18N
        cmdRotatePolygon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdRotatePolygonActionPerformed(evt);
            }
        });
        tobVerdis.add(cmdRotatePolygon);

        sep4.setOrientation(javax.swing.SwingConstants.VERTICAL);
        sep4.setMaximumSize(new java.awt.Dimension(2, 32767));
        tobVerdis.add(sep4);

        cmdUndo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/undo.png"))); // NOI18N
        cmdUndo.setToolTipText("Undo");
        cmdUndo.setEnabled(false);
        cmdUndo.setFocusPainted(false);
        cmdUndo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdUndo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdUndoActionPerformed(evt);
            }
        });
        tobVerdis.add(cmdUndo);

        cmdRedo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/redo.png"))); // NOI18N
        cmdRedo.setEnabled(false);
        cmdRedo.setFocusable(false);
        cmdRedo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdRedo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdRedo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdRedoActionPerformed(evt);
            }
        });
        tobVerdis.add(cmdRedo);

        panMap.add(tobVerdis, java.awt.BorderLayout.NORTH);

        add(panMap, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdAddActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdAddActionPerformed
        try {
            mappingComp.showInternalLayerWidget(!mappingComp.isInternalLayerWidgetVisible(), 500);
        } catch (Throwable t) {
            LOG.error("Fehler beim Anzeigen des Layersteuerelements", t);
        }
    }//GEN-LAST:event_cmdAddActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblScaleMousePressed(final java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblScaleMousePressed
        if (evt.isPopupTrigger()) {
            pomScale.setVisible(true);
        }
    }//GEN-LAST:event_lblScaleMousePressed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdFullPolyActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdFullPolyActionPerformed
        mappingComp.zoomToFullFeatureCollectionBounds();
    }//GEN-LAST:event_cmdFullPolyActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdFullPoly1ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdFullPoly1ActionPerformed
        mappingComp.zoomToSelectedNode();
    }//GEN-LAST:event_cmdFullPoly1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdWmsBackgroundActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdWmsBackgroundActionPerformed
        if (mappingComp.isBackgroundEnabled()) {
            mappingComp.setBackgroundEnabled(false);
            cmdWmsBackground.setSelected(false);
        } else {
            mappingComp.setBackgroundEnabled(true);
            cmdWmsBackground.setSelected(true);
            mappingComp.queryServices();
        }
    }//GEN-LAST:event_cmdWmsBackgroundActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdForegroundActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdForegroundActionPerformed
        if (mappingComp.isFeatureCollectionVisible()) {
            mappingComp.setFeatureCollectionVisibility(false);
            cmdForeground.setSelected(false);
        } else {
            mappingComp.setFeatureCollectionVisibility(true);
            cmdForeground.setSelected(true);
        }
    }//GEN-LAST:event_cmdForegroundActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdSnapActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSnapActionPerformed
        cmdSnap.setSelected(!cmdSnap.isSelected());
        // cismapPrefs.getGlobalPrefs().setSnappingEnabled(cmdSnap.isSelected());
        // cismapPrefs.getGlobalPrefs().setSnappingPreviewEnabled(cmdSnap.isSelected());
        mappingComp.setSnappingEnabled(cmdSnap.isSelected());
        mappingComp.setVisualizeSnappingEnabled(cmdSnap.isSelected());
        mappingComp.setInGlueIdenticalPointsMode(cmdSnap.isSelected());
    }//GEN-LAST:event_cmdSnapActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdZoomActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdZoomActionPerformed
        removeMainGroupSelection();
        cmdZoom.setSelected(true);
        mappingComp.setInteractionMode(MappingComponent.ZOOM);
    }//GEN-LAST:event_cmdZoomActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdPanActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdPanActionPerformed
        removeMainGroupSelection();
        cmdPan.setSelected(true);
        mappingComp.setInteractionMode(MappingComponent.PAN);
    }//GEN-LAST:event_cmdPanActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdSelectActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSelectActionPerformed
        removeMainGroupSelection();
        cmdSelect.setSelected(true);
        mappingComp.setInteractionMode(MappingComponent.SELECT);
        cmdMoveHandleActionPerformed(null);
    }//GEN-LAST:event_cmdSelectActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdALBActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdALBActionPerformed
        LOG.info("ALB");
        removeMainGroupSelection();
        cmdALB.setSelected(true);
        mappingComp.setInteractionMode(MappingComponent.CUSTOM_FEATUREINFO);
    }//GEN-LAST:event_cmdALBActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdMovePolygonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdMovePolygonActionPerformed
        removeMainGroupSelection();
        cmdMovePolygon.setSelected(true);
        mappingComp.setInteractionMode(MappingComponent.MOVE_POLYGON);
    }//GEN-LAST:event_cmdMovePolygonActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdNewPolygonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdNewPolygonActionPerformed
        removeMainGroupSelection();
        // final boolean snapEnab = cismapPrefs.getGlobalPrefs().isSnappingEnabled();
        // final boolean snapVizEnab = cismapPrefs.getGlobalPrefs().isSnappingPreviewEnabled();
        // mappingComp.setSnappingEnabled(snapEnab);
        // mappingComp.setVisualizeSnappingEnabled(snapVizEnab);
        cmdNewPolygon.setSelected(true);
        mappingComp.setInteractionMode(MappingComponent.NEW_POLYGON);
        ((CreateGeometryListener) mappingComp.getInputListener(MappingComponent.NEW_POLYGON)).setMode(
                CreateGeometryListener.POLYGON);
        ((CreateGeometryListener) mappingComp.getInputListener(MappingComponent.NEW_POLYGON)).setGeometryFeatureClass(PureNewFeature.class);
    }//GEN-LAST:event_cmdNewPolygonActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdNewPointActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdNewPointActionPerformed
        removeMainGroupSelection();
        // final boolean snapEnab = cismapPrefs.getGlobalPrefs().isSnappingEnabled();
        // final boolean snapVizEnab = cismapPrefs.getGlobalPrefs().isSnappingPreviewEnabled();
        // mappingComp.setSnappingEnabled(snapEnab);
        // mappingComp.setVisualizeSnappingEnabled(snapVizEnab);
        cmdNewPoint.setSelected(true);
        mappingComp.setInteractionMode(MappingComponent.NEW_POLYGON);
        ((CreateGeometryListener) mappingComp.getInputListener(MappingComponent.NEW_POLYGON)).setMode(
                CreateGeometryListener.POINT);
    }//GEN-LAST:event_cmdNewPointActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdOrthogonalRectangleActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdOrthogonalRectangleActionPerformed
        removeMainGroupSelection();
        cmdOrthogonalRectangle.setSelected(true);
        mappingComp.setInteractionMode(MappingComponent.NEW_POLYGON);
        ((CreateGeometryListener) mappingComp.getInputListener(MappingComponent.NEW_POLYGON)).setMode(
                CreateGeometryListener.RECTANGLE_FROM_LINE);
    }//GEN-LAST:event_cmdOrthogonalRectangleActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdRaisePolygonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdRaisePolygonActionPerformed
        removeMainGroupSelection();
        cmdRaisePolygon.setSelected(true);
        mappingComp.setInteractionMode(MappingComponent.RAISE_POLYGON);
    }//GEN-LAST:event_cmdRaisePolygonActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdRemovePolygonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdRemovePolygonActionPerformed
        removeMainGroupSelection();
        cmdRemovePolygon.setSelected(true);
        mappingComp.setInteractionMode(MappingComponent.REMOVE_POLYGON);
    }//GEN-LAST:event_cmdRemovePolygonActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdAttachPolyToAlphadataActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdAttachPolyToAlphadataActionPerformed
        removeMainGroupSelection();
        cmdAttachPolyToAlphadata.setSelected(true);
        mappingComp.setInteractionMode(MappingComponent.ATTACH_POLYGON_TO_ALPHADATA);
    }//GEN-LAST:event_cmdAttachPolyToAlphadataActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdJoinPolyActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdJoinPolyActionPerformed
        removeMainGroupSelection();
        cmdJoinPoly.setSelected(true);
        mappingComp.setInteractionMode(MappingComponent.JOIN_POLYGONS);
    }//GEN-LAST:event_cmdJoinPolyActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdSplitPolyActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSplitPolyActionPerformed
        removeMainGroupSelection();
        cmdSplitPoly.setSelected(true);
        mappingComp.setInteractionMode(MappingComponent.SPLIT_POLYGON);
        cmdMoveHandleActionPerformed(null);
    }//GEN-LAST:event_cmdSplitPolyActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdMoveHandleActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdMoveHandleActionPerformed
        removeHandleGroupSelection();
        cmdMoveHandle.setSelected(true);
        mappingComp.setHandleInteractionMode(MappingComponent.MOVE_HANDLE);
    }//GEN-LAST:event_cmdMoveHandleActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdAddHandleActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdAddHandleActionPerformed
        removeHandleGroupSelection();
        cmdAddHandle.setSelected(true);
        mappingComp.setHandleInteractionMode(MappingComponent.ADD_HANDLE);
    }//GEN-LAST:event_cmdAddHandleActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdRemoveHandleActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdRemoveHandleActionPerformed
        removeHandleGroupSelection();
        cmdRemoveHandle.setSelected(true);
        mappingComp.setHandleInteractionMode(MappingComponent.REMOVE_HANDLE);
    }//GEN-LAST:event_cmdRemoveHandleActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdRotatePolygonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdRotatePolygonActionPerformed
        removeHandleGroupSelection();
        cmdRotatePolygon.setSelected(true);
        mappingComp.setHandleInteractionMode(MappingComponent.ROTATE_POLYGON);
    }//GEN-LAST:event_cmdRotatePolygonActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdUndoActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdUndoActionPerformed
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
    }//GEN-LAST:event_cmdUndoActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdRedoActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdRedoActionPerformed
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
    }//GEN-LAST:event_cmdRedoActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdNewLinestringActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdNewLinestringActionPerformed
        removeMainGroupSelection();
        // final boolean snapEnab = cismapPrefs.getGlobalPrefs().isSnappingEnabled();
        // final boolean snapVizEnab = cismapPrefs.getGlobalPrefs().isSnappingPreviewEnabled();
        // mappingComp.setSnappingEnabled(snapEnab);
        // mappingComp.setVisualizeSnappingEnabled(snapVizEnab);
        cmdNewLinestring.setSelected(true);
        mappingComp.setInteractionMode(MappingComponent.NEW_POLYGON);
        ((CreateGeometryListener) mappingComp.getInputListener(MappingComponent.NEW_POLYGON)).setMode(
                CreateGeometryListener.LINESTRING);
        ((CreateGeometryListener) mappingComp.getInputListener(MappingComponent.NEW_POLYGON)).setGeometryFeatureClass(
                PureNewFeatureWithThickerLineString.class);
    }//GEN-LAST:event_cmdNewLinestringActionPerformed

    private void cmdSearchFlurstueckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSearchFlurstueckActionPerformed
        removeMainGroupSelection();
        cmdSearchFlurstueck.setSelected(true);
        mappingComp.setInteractionMode(MappingComponent.CREATE_SIMPLE_GEOMETRY);
}//GEN-LAST:event_cmdSearchFlurstueckActionPerformed

    public void landparcelSearchGeometryCreated(final PNotification notification) {
        final PureNewFeature newFeature = ((CreateSimpleGeometryListener)mappingComp.getInputListener(MappingComponent.CREATE_SIMPLE_GEOMETRY)).getNewFeature();

        final Point point = newFeature.getGeometry().getInteriorPoint();
        Main.THIS.loadAlkisFlurstueck(point);
    }

    /**
     * DOCUMENT ME!
     */
    private void removeMainGroupSelection() {
        cmdSelect.setSelected(false);
        cmdPan.setSelected(false);
        cmdZoom.setSelected(false);
        cmdMovePolygon.setSelected(false);
        cmdNewPolygon.setSelected(false);
        cmdNewLinestring.setSelected(false);
        cmdNewPoint.setSelected(false);
        cmdOrthogonalRectangle.setSelected(false);
        cmdRemovePolygon.setSelected(false);
        cmdAttachPolyToAlphadata.setSelected(false);
        cmdOrthogonalRectangle.setSelected(false);
        cmdSplitPoly.setSelected(false);
        cmdJoinPoly.setSelected(false);
        cmdRaisePolygon.setSelected(false);
        cmdALB.setSelected(false);
    }

    /**
     * DOCUMENT ME!
     */
    private void removeHandleGroupSelection() {
        cmdRemoveHandle.setSelected(false);
        cmdAddHandle.setSelected(false);
        cmdMoveHandle.setSelected(false);
        cmdRotatePolygon.setSelected(false);
    }

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

    @Override
    public void allFeaturesRemoved(final FeatureCollectionEvent fce) {
    }

    @Override
    public void featureCollectionChanged() {
    }

    @Override
    public void featureReconsiderationRequested(final FeatureCollectionEvent fce) {
    }

    @Override
    public void featureSelectionChanged(final FeatureCollectionEvent fce) {
        refreshMeasurementsInStatus();
    }

    @Override
    public void featuresAdded(final FeatureCollectionEvent fce) {
    }

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

    @Override
    public void featuresRemoved(final FeatureCollectionEvent fce) {
    }

    /**
     * DOCUMENT ME!
     *
     * @param  e  DOCUMENT ME!
     */
    @Override
    public void retrievalAborted(final RetrievalEvent e) {
        activeRetrievalServices.remove(((ServiceLayer) e.getRetrievalService()).getName());
        checkProgress();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  e  DOCUMENT ME!
     */
    @Override
    public void retrievalComplete(final RetrievalEvent e) {
        activeRetrievalServices.remove(((ServiceLayer) e.getRetrievalService()).getName());
        checkProgress();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  e  DOCUMENT ME!
     */
    @Override
    public void retrievalError(final RetrievalEvent e) {
        activeRetrievalServices.remove(((ServiceLayer) e.getRetrievalService()).getName());
        checkProgress();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  e  DOCUMENT ME!
     */
    @Override
    public void retrievalProgress(final RetrievalEvent e) {
        activeRetrievalServices.remove(((ServiceLayer) e.getRetrievalService()).getName());
        checkProgress();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  e  DOCUMENT ME!
     */
    @Override
    public void retrievalStarted(final RetrievalEvent e) {
        activeRetrievalServices.add(((ServiceLayer) e.getRetrievalService()).getName());
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
            if (feature instanceof CidsFeature || feature instanceof PureNewFeature) {
                cidsFeatures.add(feature);
            }
        }
        refreshMeasurementsInStatus(cidsFeatures);
    }

    @Override
    public void setEnabled(final boolean b) {
        this.cmdMovePolygon.setVisible(b);
//        this.cmdNewPolygon.setVisible(b);
//        this.cmdNewLinestring.setVisible(b);
//        this.cmdNewPoint.setVisible(b);
        this.cmdRemovePolygon.setVisible(b);
        this.cmdAttachPolyToAlphadata.setVisible(b);
        this.cmdJoinPoly.setVisible(b);
        this.sep3.setVisible(b);
        this.cmdMoveHandle.setVisible(b);
        this.cmdAddHandle.setVisible(b);
        this.cmdRemoveHandle.setVisible(b);
        this.cmdSplitPoly.setVisible(b);
        this.cmdRaisePolygon.setVisible(b);
        this.sep4.setVisible(b);
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
            if ((area == 0.0 && umfang == 0.0) || cf.isEmpty()) {
                lblMeasurement.setText("");
            } else {
                lblMeasurement.setText("Fl\u00E4che: " + StaticDecimalTools.round(area) + "  Umfang: "
                        + StaticDecimalTools.round(umfang));
            }
        } else if (CidsAppBackend.getInstance().getMode().equals(CidsAppBackend.Mode.ESW)) {
            double length = 0.0;
            for (final Feature f : cf) {
                if ((f != null) && (f.getGeometry() != null)) {
                    length += f.getGeometry().getLength();
                }
            }
            lblMeasurement.setText("L\u00E4nge: " + StaticDecimalTools.round(length));
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
            final double x = ((SimpleMoveListener) o).getXCoord();
            final double y = ((SimpleMoveListener) o).getYCoord();

            lblCoord.setText(MappingComponent.getCoordinateString(x, y)); // + "... " +test);
            final PFeature pf = ((SimpleMoveListener) o).getUnderlyingPFeature();
            if ((pf != null) && (pf.getFeature() instanceof PostgisFeature) && (pf.getVisible() == true)
                    && (pf.getParent() != null)
                    && (pf.getParent().getVisible() == true)) {
                lblInfo.setText(((PostgisFeature) pf.getFeature()).getObjectName());
            } else if ((pf != null) && (pf.getFeature() instanceof CidsFeature)) {
                final CidsFeature cf = (CidsFeature) pf.getFeature();
                if (cf.getMetaClass().getName().toLowerCase().equals(VerdisMetaClassConstants.MC_FLAECHE.toLowerCase())) {
                    final CidsBean cb = (CidsBean) cf.getMetaObject().getBean();

                    final int kassenzeichenNummer = (Integer) getCidsBean().getProperty(KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER);
                    final String bezeichnung = (String) cb.getProperty(RegenFlaechenPropertyConstants.PROP__FLAECHENBEZEICHNUNG);

                    lblInfo.setText("Kassenzeichen: " + Integer.toString(kassenzeichenNummer) + "::" + bezeichnung);
                }
            } else {
                lblInfo.setText("");
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  notification  DOCUMENT ME!
     */
    public void joinPolygons(final PNotification notification) {
        if (CidsAppBackend.getInstance().getMode().equals(CidsAppBackend.Mode.REGEN)) {
            PFeature one;
            PFeature two;
            one = mappingComp.getSelectedNode();
            two = null;

            final Object o = notification.getObject();

            if (o instanceof JoinPolygonsListener) {
                final JoinPolygonsListener listener = ((JoinPolygonsListener) o);
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
                                final Geometry newGeom = one.getFeature().getGeometry().union(two.getFeature().getGeometry());
                                if (newGeom.getGeometryType().equalsIgnoreCase("Multipolygon")) {
                                    JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                                            "Es k\u00F6nnen nur Polygone zusammengefasst werden, die aneinander angrenzen oder sich \u00FCberlappen.",
                                            "Zusammenfassung nicht m\u00F6glich",
                                            JOptionPane.WARNING_MESSAGE,
                                            null);
                                    return;
                                }
                                if (newGeom.getGeometryType().equalsIgnoreCase("Polygon")
                                        && (((Polygon) newGeom).getNumInteriorRing() > 0)) {
                                    JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                                            "Polygone k\u00F6nnen nur dann zusammengefasst werden, wenn dadurch kein Loch entsteht.",
                                            "Zusammenfassung nicht m\u00F6glich",
                                            JOptionPane.WARNING_MESSAGE,
                                            null);
                                    return;
                                }
                                if ((one != null) && (two != null) && (one.getFeature() instanceof CidsFeature)
                                        && (two.getFeature() instanceof CidsFeature)) {

                                    final CidsFeature cfOne = (CidsFeature) one.getFeature();
                                    final CidsFeature cfTwo = (CidsFeature) two.getFeature();
                                    final CidsBean cbOne = cfOne.getMetaObject().getBean();
                                    final CidsBean cbTwo = cfTwo.getMetaObject().getBean();

                                    final int artOne = (Integer) cbOne.getProperty(RegenFlaechenPropertyConstants.PROP__FLAECHENINFO__FLAECHENART__ID);
                                    final int artTwo = (Integer) cbTwo.getProperty(RegenFlaechenPropertyConstants.PROP__FLAECHENINFO__FLAECHENART__ID);

                                    final int gradOne = (Integer) cbOne.getProperty(RegenFlaechenPropertyConstants.PROP__FLAECHENINFO__ANSCHLUSSGRAD__ID);
                                    final int gradTwo = (Integer) cbTwo.getProperty(RegenFlaechenPropertyConstants.PROP__FLAECHENINFO__ANSCHLUSSGRAD__ID);

                                    if (artOne != artTwo || gradOne != gradTwo) {
                                        JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                                                "Fl\u00E4chen k\u00F6nnen nur zusammengefasst werden, wenn Fl\u00E4chenart und Anschlussgrad gleich sind.",
                                                "Zusammenfassung nicht m\u00F6glich",
                                                JOptionPane.WARNING_MESSAGE,
                                                null);
                                        return;
                                    }

                                    final Integer anteilOne = (Integer) cbOne.getProperty(RegenFlaechenPropertyConstants.PROP__ANTEIL);
                                    final Integer anteilTwo = (Integer) cbTwo.getProperty(RegenFlaechenPropertyConstants.PROP__ANTEIL);

                                    // Check machen ob eine Fl\u00E4che eine Teilfl\u00E4che ist
                                    if (anteilOne != null || anteilTwo != null) {
                                        JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                                                "Fl\u00E4chen die von Teileigentum betroffen sind k\u00F6nnen nicht zusammengefasst werden.",
                                                "Zusammenfassung nicht m\u00F6glich",
                                                JOptionPane.WARNING_MESSAGE,
                                                null);
                                        return;
                                    }

                                    Main.THIS.getRegenFlaechenTabellenPanel().removeBean(cbTwo);

                                    cbOne.setProperty(RegenFlaechenPropertyConstants.PROP__FLAECHENINFO__GROESSE_GRAFIK, new Integer((int) (newGeom.getArea())));

                                    final String bemerkungOne = (String) cbOne.getProperty(RegenFlaechenPropertyConstants.PROP__BEMERKUNG);

                                    if (bemerkungOne != null && bemerkungOne.trim().length() > 0) {
                                        cbOne.setProperty(RegenFlaechenPropertyConstants.PROP__BEMERKUNG, bemerkungOne + "\n");
                                    }
                                    cbOne.setProperty(RegenFlaechenPropertyConstants.PROP__BEMERKUNG, getJoinBackupString(cbTwo));

                                    final boolean sperreOne = cbOne.getProperty(RegenFlaechenPropertyConstants.PROP__SPERRE) != null && (Boolean) cbOne.getProperty(RegenFlaechenPropertyConstants.PROP__SPERRE);
                                    final boolean sperreTwo = cbTwo.getProperty(RegenFlaechenPropertyConstants.PROP__SPERRE) != null && (Boolean) cbTwo.getProperty(RegenFlaechenPropertyConstants.PROP__SPERRE);

                                    if (!sperreOne && sperreTwo) {
                                        cbOne.setProperty(RegenFlaechenPropertyConstants.PROP__SPERRE, true);
                                        cbOne.setProperty(RegenFlaechenPropertyConstants.PROP__BEMERKUNG_SPERRE, "JOIN::" + cbTwo.getProperty(RegenFlaechenPropertyConstants.PROP__BEMERKUNG_SPERRE));
                                    }
                                }
                                if (one.getFeature() instanceof CidsFeature) {
                                    final CidsFeature cfOne = (CidsFeature) one.getFeature();
                                    final CidsBean cbOne = cfOne.getMetaObject().getBean();

                                    // Eine vorhandene Fl\u00E4che und eine neuangelegt wurden gejoint
                                    RegenFlaechenDetailsPanel.setGeometry(newGeom, cbOne);
                                    cbOne.setProperty(RegenFlaechenPropertyConstants.PROP__FLAECHENINFO__GROESSE_GRAFIK, (int) newGeom.getArea());
                                }
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("newGeom ist vom Typ:" + newGeom.getGeometryType());
                                }
                                one.getFeature().setGeometry(newGeom);
                                if (!(one.getFeature().getGeometry().equals(backup))) {
                                    two.removeFromParent();
                                    two = null;
                                }
                                one.visualize();
                            } catch (Exception e) {
                                LOG.error("one: " + one + "\n two: " + two, e);
                            }
                            return;
                        }
                    } else {
                        final PFeature pf = joinCandidate;
                        if (one != null) {
                            one.setSelected(false);
                        }
                        one = pf;
                        if (one.getFeature() instanceof CidsFeature) {
                            final CidsFeature flaecheFeature = (CidsFeature) one.getFeature();
                            mappingComp.getFeatureCollection().select(flaecheFeature);
                        } else {
                            mappingComp.getFeatureCollection().unselectAll();
                        }
                    }
                }
            }
        }
    }

    private static String getJoinBackupString(CidsBean flaecheBean) {
        final String bezeichnung = (String) flaecheBean.getProperty(RegenFlaechenPropertyConstants.PROP__FLAECHENBEZEICHNUNG);
        final String gr_grafik = Integer.toString((Integer) flaecheBean.getProperty(RegenFlaechenPropertyConstants.PROP__FLAECHENINFO__GROESSE_GRAFIK));
        final String gr_korrektur = Integer.toString((Integer) flaecheBean.getProperty(RegenFlaechenPropertyConstants.PROP__FLAECHENINFO__GROESSE_KORREKTUR));
        final String erfassungsdatum = ((Date) flaecheBean.getProperty(KassenzeichenPropertyConstants.PROP__DATUM_ERFASSUNG)).toString();
        final String veranlagungsdatum = (String) flaecheBean.getProperty(RegenFlaechenPropertyConstants.PROP__DATUM_VERANLAGUNG);
        final String sperre = Boolean.toString(flaecheBean.getProperty(RegenFlaechenPropertyConstants.PROP__SPERRE) != null && (Boolean) flaecheBean.getProperty(RegenFlaechenPropertyConstants.PROP__SPERRE));
        final String bem_sperre = (String) flaecheBean.getProperty(RegenFlaechenPropertyConstants.PROP__BEMERKUNG_SPERRE);
        final String feb_id = (String) flaecheBean.getProperty(RegenFlaechenPropertyConstants.PROP__FEB_ID);
        final String bemerkung = (String) flaecheBean.getProperty(RegenFlaechenPropertyConstants.PROP__BEMERKUNG);

        String ret = "<JOIN ";
        ret += "bez=\"" + bezeichnung
                + "\" gr=\"" + gr_grafik
                + "\" grk=\"" + gr_korrektur
                + "\" edat=\"" + erfassungsdatum
                + "\" vdat=\"" + veranlagungsdatum
                + "\" sp=\"" + sperre
                + "\" spbem=\"" + bem_sperre
                + "\" febid=\"" + feb_id + "  >\n";
        ret += bemerkung;
        if ((bemerkung != null) && (bemerkung.trim().length() > 0) && !bemerkung.endsWith("\n")) {
            ret += "\n";
        }
        ret += "</JOIN>";
        return ret;
    }

    @Override
    public CidsBean getCidsBean() {
        return kassenzeichenBean;
    }

    private void refreshInMap(final boolean withZoom) {
        final FeatureCollection featureCollection = mappingComp.getFeatureCollection();

        featureCollection.removeAllFeatures();

        final CidsBean cidsBean = getCidsBean();
        if (cidsBean != null) {
            switch (CidsAppBackend.getInstance().getMode()) {
                case ALLGEMEIN: {
                    final Feature add = new BeanUpdatingCidsFeature(cidsBean, KassenzeichenPropertyConstants.PROP__GEOMETRIE__GEO_FIELD);
                    featureCollection.addFeature(add);
                } break;
                case REGEN: {
                    final List<CidsBean> flaechen = (List<CidsBean>) cidsBean.getProperty(KassenzeichenPropertyConstants.PROP__FLAECHEN);
                    for (final CidsBean flaeche : flaechen) {
                        final Feature add = new BeanUpdatingCidsFeature(flaeche, RegenFlaechenPropertyConstants.PROP__FLAECHENINFO__GEOMETRIE__GEO_FIELD);
                        featureCollection.addFeature(add);
                    }
                } break;
                case ESW: {
                    final List<CidsBean> fronten = (List<CidsBean>) cidsBean.getProperty(KassenzeichenPropertyConstants.PROP__FRONTEN);
                    for (final CidsBean front : fronten) {
                        final Feature add = new BeanUpdatingCidsFeature(front, WDSRPropertyConstants.PROP__GEOMETRIE__GEO_FIELD);
                        featureCollection.addFeature(add);
                    }
                } break;
            }
            if (withZoom) {
                mappingComp.zoomToAFeatureCollection(featureCollection.getAllFeatures(), true, mappingComp.isFixedMapScale());
            }
        }
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        final CidsBean oldCidsBean = kassenzeichenBean;

        kassenzeichenBean = cidsBean;

        refreshInMap(cidsBean != null && !cidsBean.equals(oldCidsBean));
    }

    @Override
    public void editModeChanged() {
        final boolean b = CidsAppBackend.getInstance().isEditable();
        final List<Feature> all = mappingComp.getFeatureCollection().getAllFeatures();
        for (final Feature f : all) {
            f.setEditable(b);
        }
        CidsAppBackend.getInstance().getMainMap().setReadOnly(!b);
    }

    @Override
    public void appModeChanged() {
        refreshInMap(true);
        lblMeasurement.setText("");
    }

    public void changeSelectedButtonAccordingToInteractionMode() {
        removeMainGroupSelection();
        final String im = mappingComp.getInteractionMode();
        if (LOG.isDebugEnabled()) {
            LOG.debug("changeSelectedButtonAccordingToInteractionMode: " + mappingComp.getInteractionMode(),
                    new CurrentStackTrace());
        }
        if (im.equals(MappingComponent.ZOOM)) {
            cmdZoom.setSelected(true);
        }
        if (im.equals(MappingComponent.CUSTOM_FEATUREINFO)) {
            cmdALB.setSelected(true);
        } else if (im.equals(MappingComponent.PAN)) {
            cmdPan.setSelected(true);
        } else if (im.equals(MappingComponent.SELECT)) {
            cmdSelect.setSelected(true);
        } else if (im.equals(MappingComponent.NEW_POLYGON)) {
            if (((CreateGeometryListener) mappingComp.getInputListener(MappingComponent.NEW_POLYGON)).getMode().equals(
                    CreateGeometryListener.POINT)) {
                cmdNewPoint.setSelected(true);
            } else if (((CreateGeometryListener) mappingComp.getInputListener(MappingComponent.NEW_POLYGON)).getMode().equals(CreateGeometryListener.POLYGON)) {
                cmdNewPolygon.setSelected(true);
            } else {
                cmdSelect.setSelected(true);
            }
        } else {
            cmdSelect.setSelected(true);
        }

        if (mappingComp.isSnappingEnabled()) {
            cmdSnap.setSelected(true);
        } else {
            cmdSnap.setSelected(false);
        }
    }

    public void selectionChanged(final PNotification notfication) {
        final Object o = notfication.getObject();
        if (o instanceof SelectionListener || o instanceof FeatureMoveListener || o instanceof SplitPolygonListener) {
            PFeature pf = null;
            if (o instanceof SelectionListener) {
                pf = ((SelectionListener) o).getSelectedPFeature();
                if (((SelectionListener) o).getClickCount() > 1 && pf.getFeature() instanceof PostgisFeature) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("SelectionchangedListener: clickCOunt:" + ((SelectionListener) o).getClickCount());
                    }
                    final PostgisFeature postgisFeature = ((PostgisFeature) pf.getFeature());
                    try {
                        if (pf.getVisible() == true && pf.getParent().getVisible() == true
                                && postgisFeature.getFeatureType().equalsIgnoreCase("KASSENZEICHEN")) {
                            Main.THIS.getKzPanel().gotoKassenzeichen(postgisFeature.getGroupingKey());
                        }
                    } catch (Exception e) {
                        LOG.info("Fehler beim gotoKassenzeichen", e);
                    }
                }
            }
        }
    }

    public void featureDeleteRequested(final PNotification notfication) {
        final Object o = notfication.getObject();
        if (o instanceof DeleteFeatureListener) {
            final DeleteFeatureListener dfl = (DeleteFeatureListener) o;
            final PFeature pf = dfl.getFeatureRequestedForDeletion();
            if (pf.getFeature() instanceof CidsFeature) {
                try {
                    final CidsFeature cf = (CidsFeature) pf.getFeature();
                    final CidsBean cb = cf.getMetaObject().getBean();
                    cb.setProperty(RegenFlaechenPropertyConstants.PROP__FLAECHENINFO__GEOMETRIE, null);
                } catch (Exception ex) {
                    LOG.error("error while removing feature", ex);
                }
            }
        }
    }

    public void splitPolygon(final PNotification notification) {
        if (CidsAppBackend.getInstance().getMode().equals(CidsAppBackend.Mode.REGEN)) {
            final Object o = notification.getObject();
            if (o instanceof SplitPolygonListener) {
                final SplitPolygonListener l = (SplitPolygonListener) o;
                final PFeature pf = l.getFeatureClickedOn();
                if (pf.getFeature() instanceof CidsFeature) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Split");
                    }
                    final CidsFeature cf = (CidsFeature) pf.getFeature();
                    final CidsBean cb = cf.getMetaObject().getBean();
                    try {
                        cb.setProperty(RegenFlaechenPropertyConstants.PROP__FLAECHENINFO__GEOMETRIE, null);
                    } catch (Exception ex) {
                        LOG.error("error while removing geometry", ex);
                    }
                }
                final Feature[] f_arr = pf.split();
                if (f_arr != null) {
                    mappingComp.getFeatureCollection().removeFeature(pf.getFeature());
                    f_arr[0].setEditable(true);
                    f_arr[1].setEditable(true);
                    mappingComp.getFeatureCollection().addFeature(f_arr[0]);
                    mappingComp.getFeatureCollection().addFeature(f_arr[1]);
                    cmdAttachPolyToAlphadataActionPerformed(null);
                }
            }
        }
    }
}
