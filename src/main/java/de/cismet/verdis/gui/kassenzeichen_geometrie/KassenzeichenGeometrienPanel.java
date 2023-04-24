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
 * AllgemeineInfos.java
 *
 * Created on 04.12.2010, 10:33:57
 */
package de.cismet.verdis.gui.kassenzeichen_geometrie;

import Sirius.navigator.connection.SessionManager;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import com.vividsolutions.jts.geom.Geometry;

import edu.umd.cs.piccolox.event.PNotification;

import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.observablecollections.ObservableListListener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;

import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.features.FeatureCollection;
import de.cismet.cismap.commons.features.FeatureCollectionEvent;
import de.cismet.cismap.commons.features.FeatureCollectionListener;
import de.cismet.cismap.commons.features.PureNewFeature;
import de.cismet.cismap.commons.gui.piccolo.PFeature;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.AttachFeatureListener;

import de.cismet.cismap.navigatorplugin.CidsFeature;

import de.cismet.tools.gui.StaticSwingTools;

import de.cismet.verdis.CidsAppBackend;
import de.cismet.verdis.EditModeListener;

import de.cismet.verdis.commons.constants.VerdisConstants;

import de.cismet.verdis.gui.Main;

import de.cismet.verdis.server.search.AlkisLandparcelSearch;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class KassenzeichenGeometrienPanel extends javax.swing.JPanel implements CidsBeanStore,
    EditModeListener,
    FeatureCollectionListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            KassenzeichenGeometrienPanel.class);
    private static final Color[] COLORS = new Color[] {
            new Color(41, 86, 178),
            new Color(101, 156, 239),
            new Color(125, 189, 0),
            new Color(220, 246, 0),
            new Color(255, 91, 0)
        };
    public static final List<Color> LANDPARCEL_COLORS = Collections.unmodifiableList(Arrays.asList(COLORS));
    private static final double ALKIS_LANDPARCEL_GEOM_BUFFER = -0.05;
    private static final double FLAECHE_GEOM_BUFFER = -0.30;
    private static int NEW_KASSENZEICHEN_GEOMETRIE_ID = -1;
    private static AlkisLandparcelWorker WORKER;

    //~ Instance fields --------------------------------------------------------

    private KassenzeichenGeometrieListListener kassenzeichenGeometrieListListener =
        new KassenzeichenGeometrieListListener();
    private CidsBean kassenzeichenBean;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdAutoCreateGeometries;
    private javax.swing.JButton cmdRemoveKassenzeichenGeometrien;
    private javax.swing.JButton cmdShowAlkisRenderer;
    private javax.swing.JButton cmdShowAlkisRendererForAll;
    private javax.swing.JButton cmdShowAlkisRendererForSelected;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList lstAlkisLandparcels;
    private javax.swing.JList lstKassenzeichenGeometrien;
    private javax.swing.JToggleButton tglShowAlkisLandparcelGeoms;
    private javax.swing.JToggleButton tglShowKassenzeichenGeometrien;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form AllgemeineInfos.
     */
    public KassenzeichenGeometrienPanel() {
        initComponents();
        lstKassenzeichenGeometrien.setCellRenderer(new KassenzeichenGeometrieCellRenderer());
        lstAlkisLandparcels.setCellRenderer(new AlkisLandparcelListCellRenderer());
        lstAlkisLandparcels.getModel().addListDataListener(new ListDataListener() {

                @Override
                public void intervalAdded(final ListDataEvent e) {
                    refreshCmdShowAlkisRendererForAllVisibility();
                }

                @Override
                public void intervalRemoved(final ListDataEvent e) {
                    refreshCmdShowAlkisRendererForAllVisibility();
                }

                @Override
                public void contentsChanged(final ListDataEvent e) {
                }
            });
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
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstKassenzeichenGeometrien = new KassenzeichenGeometrienList();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstAlkisLandparcels = new javax.swing.JList();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cmdShowAlkisRendererForSelected = new javax.swing.JButton();
        tglShowAlkisLandparcelGeoms = new javax.swing.JToggleButton();
        jPanel4 = new javax.swing.JPanel();
        cmdShowAlkisRendererForAll = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        tglShowKassenzeichenGeometrien = new javax.swing.JToggleButton();
        jPanel5 = new javax.swing.JPanel();
        cmdRemoveKassenzeichenGeometrien = new javax.swing.JButton();
        cmdShowAlkisRenderer = new javax.swing.JButton();
        cmdAutoCreateGeometries = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jScrollPane1.setMinimumSize(new java.awt.Dimension(23, 100));
        jScrollPane1.setName(""); // NOI18N
        jScrollPane1.setOpaque(false);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(258, 150));

        lstKassenzeichenGeometrien.setBackground(new java.awt.Color(242, 241, 240));
        lstKassenzeichenGeometrien.setOpaque(false);

        final org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create(
                "${cidsBean.kassenzeichen_geometrien}");
        final org.jdesktop.swingbinding.JListBinding jListBinding = org.jdesktop.swingbinding.SwingBindings
                    .createJListBinding(
                        org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                        this,
                        eLProperty,
                        lstKassenzeichenGeometrien,
                        "");
        bindingGroup.addBinding(jListBinding);

        lstKassenzeichenGeometrien.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lstKassenzeichenGeometrienMouseClicked(evt);
                }
            });
        lstKassenzeichenGeometrien.addListSelectionListener(new javax.swing.event.ListSelectionListener() {

                @Override
                public void valueChanged(final javax.swing.event.ListSelectionEvent evt) {
                    lstKassenzeichenGeometrienValueChanged(evt);
                }
            });
        jScrollPane1.setViewportView(lstKassenzeichenGeometrien);
        ((KassenzeichenGeometrienList)lstKassenzeichenGeometrien).setPanel(this);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 3);
        jPanel1.add(jScrollPane1, gridBagConstraints);

        jScrollPane2.setOpaque(false);

        lstAlkisLandparcels.setBackground(new java.awt.Color(242, 241, 240));
        lstAlkisLandparcels.setModel(new DefaultListModel());
        lstAlkisLandparcels.setToolTipText(org.openide.util.NbBundle.getMessage(
                KassenzeichenGeometrienPanel.class,
                "KassenzeichenGeometrienPanel.lstAlkisLandparcels.toolTipText")); // NOI18N
        lstAlkisLandparcels.setOpaque(false);
        lstAlkisLandparcels.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lstAlkisLandparcelsMouseClicked(evt);
                }
            });
        lstAlkisLandparcels.addListSelectionListener(new javax.swing.event.ListSelectionListener() {

                @Override
                public void valueChanged(final javax.swing.event.ListSelectionEvent evt) {
                    lstAlkisLandparcelsValueChanged(evt);
                }
            });
        jScrollPane2.setViewportView(lstAlkisLandparcels);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 3);
        jPanel1.add(jScrollPane2, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText(org.openide.util.NbBundle.getMessage(
                KassenzeichenGeometrienPanel.class,
                "KassenzeichenGeometrienPanel.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel2.add(jLabel1, gridBagConstraints);

        cmdShowAlkisRendererForSelected.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/commons/gui/alk.png")));       // NOI18N
        cmdShowAlkisRendererForSelected.setText(org.openide.util.NbBundle.getMessage(
                KassenzeichenGeometrienPanel.class,
                "KassenzeichenGeometrienPanel.cmdShowAlkisRendererForSelected.text"));        // NOI18N
        cmdShowAlkisRendererForSelected.setToolTipText(org.openide.util.NbBundle.getMessage(
                KassenzeichenGeometrienPanel.class,
                "KassenzeichenGeometrienPanel.cmdShowAlkisRendererForSelected.toolTipText")); // NOI18N
        cmdShowAlkisRendererForSelected.setBorderPainted(false);
        cmdShowAlkisRendererForSelected.setContentAreaFilled(false);
        cmdShowAlkisRendererForSelected.setEnabled(false);
        cmdShowAlkisRendererForSelected.setFocusPainted(false);
        cmdShowAlkisRendererForSelected.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdShowAlkisRendererForSelectedActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        jPanel2.add(cmdShowAlkisRendererForSelected, gridBagConstraints);

        tglShowAlkisLandparcelGeoms.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/foreground.png")));    // NOI18N
        tglShowAlkisLandparcelGeoms.setText(org.openide.util.NbBundle.getMessage(
                KassenzeichenGeometrienPanel.class,
                "KassenzeichenGeometrienPanel.tglShowAlkisLandparcelGeoms.text"));                  // NOI18N
        tglShowAlkisLandparcelGeoms.setToolTipText(org.openide.util.NbBundle.getMessage(
                KassenzeichenGeometrienPanel.class,
                "KassenzeichenGeometrienPanel.tglShowAlkisLandparcelGeoms.toolTipText"));           // NOI18N
        tglShowAlkisLandparcelGeoms.setBorderPainted(false);
        tglShowAlkisLandparcelGeoms.setContentAreaFilled(false);
        tglShowAlkisLandparcelGeoms.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/foreground_on.png"))); // NOI18N
        tglShowAlkisLandparcelGeoms.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    tglShowAlkisLandparcelGeomsActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        jPanel2.add(tglShowAlkisLandparcelGeoms, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(jPanel4, gridBagConstraints);

        cmdShowAlkisRendererForAll.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/alks.png"))); // NOI18N
        cmdShowAlkisRendererForAll.setText(org.openide.util.NbBundle.getMessage(
                KassenzeichenGeometrienPanel.class,
                "KassenzeichenGeometrienPanel.cmdShowAlkisRendererForAll.text"));          // NOI18N
        cmdShowAlkisRendererForAll.setToolTipText(org.openide.util.NbBundle.getMessage(
                KassenzeichenGeometrienPanel.class,
                "KassenzeichenGeometrienPanel.cmdShowAlkisRendererForAll.toolTipText"));   // NOI18N
        cmdShowAlkisRendererForAll.setBorderPainted(false);
        cmdShowAlkisRendererForAll.setContentAreaFilled(false);
        cmdShowAlkisRendererForAll.setEnabled(false);
        cmdShowAlkisRendererForAll.setFocusPainted(false);
        cmdShowAlkisRendererForAll.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdShowAlkisRendererForAllActionPerformed(evt);
                }
            });
        jPanel2.add(cmdShowAlkisRendererForAll, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(13, 3, 2, 3);
        jPanel1.add(jPanel2, gridBagConstraints);

        jPanel3.setLayout(new java.awt.GridBagLayout());

        jLabel2.setText(org.openide.util.NbBundle.getMessage(
                KassenzeichenGeometrienPanel.class,
                "KassenzeichenGeometrienPanel.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel3.add(jLabel2, gridBagConstraints);

        tglShowKassenzeichenGeometrien.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/foreground.png")));    // NOI18N
        tglShowKassenzeichenGeometrien.setSelected(true);
        tglShowKassenzeichenGeometrien.setText(org.openide.util.NbBundle.getMessage(
                KassenzeichenGeometrienPanel.class,
                "KassenzeichenGeometrienPanel.tglShowKassenzeichenGeometrien.text"));               // NOI18N
        tglShowKassenzeichenGeometrien.setToolTipText(org.openide.util.NbBundle.getMessage(
                KassenzeichenGeometrienPanel.class,
                "KassenzeichenGeometrienPanel.tglShowKassenzeichenGeometrien.toolTipText"));        // NOI18N
        tglShowKassenzeichenGeometrien.setBorderPainted(false);
        tglShowKassenzeichenGeometrien.setContentAreaFilled(false);
        tglShowKassenzeichenGeometrien.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/foreground_on.png"))); // NOI18N
        tglShowKassenzeichenGeometrien.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    tglShowKassenzeichenGeometrienActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        jPanel3.add(tglShowKassenzeichenGeometrien, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(jPanel5, gridBagConstraints);

        cmdRemoveKassenzeichenGeometrien.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/removePoly.png"))); // NOI18N
        cmdRemoveKassenzeichenGeometrien.setText(org.openide.util.NbBundle.getMessage(
                KassenzeichenGeometrienPanel.class,
                "KassenzeichenGeometrienPanel.cmdRemoveKassenzeichenGeometrien.text"));          // NOI18N
        cmdRemoveKassenzeichenGeometrien.setToolTipText(org.openide.util.NbBundle.getMessage(
                KassenzeichenGeometrienPanel.class,
                "KassenzeichenGeometrienPanel.cmdRemoveKassenzeichenGeometrien.toolTipText"));   // NOI18N
        cmdRemoveKassenzeichenGeometrien.setBorderPainted(false);
        cmdRemoveKassenzeichenGeometrien.setContentAreaFilled(false);
        cmdRemoveKassenzeichenGeometrien.setEnabled(false);
        cmdRemoveKassenzeichenGeometrien.setFocusPainted(false);
        cmdRemoveKassenzeichenGeometrien.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdRemoveKassenzeichenGeometrienActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        jPanel3.add(cmdRemoveKassenzeichenGeometrien, gridBagConstraints);

        cmdShowAlkisRenderer.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/alks.png"))); // NOI18N
        cmdShowAlkisRenderer.setText(org.openide.util.NbBundle.getMessage(
                KassenzeichenGeometrienPanel.class,
                "KassenzeichenGeometrienPanel.cmdShowAlkisRenderer.text"));                // NOI18N
        cmdShowAlkisRenderer.setToolTipText(org.openide.util.NbBundle.getMessage(
                KassenzeichenGeometrienPanel.class,
                "KassenzeichenGeometrienPanel.cmdShowAlkisRenderer.toolTipText"));         // NOI18N
        cmdShowAlkisRenderer.setBorderPainted(false);
        cmdShowAlkisRenderer.setContentAreaFilled(false);
        cmdShowAlkisRenderer.setEnabled(false);
        cmdShowAlkisRenderer.setFocusPainted(false);
        cmdShowAlkisRenderer.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdShowAlkisRendererActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        jPanel3.add(cmdShowAlkisRenderer, gridBagConstraints);

        cmdAutoCreateGeometries.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/new_kassenzeichen_geometries.png"))); // NOI18N
        cmdAutoCreateGeometries.setText(org.openide.util.NbBundle.getMessage(
                KassenzeichenGeometrienPanel.class,
                "KassenzeichenGeometrienPanel.cmdAutoCreateGeometries.text"));                                     // NOI18N
        cmdAutoCreateGeometries.setToolTipText(org.openide.util.NbBundle.getMessage(
                KassenzeichenGeometrienPanel.class,
                "KassenzeichenGeometrienPanel.cmdAutoCreateGeometries.toolTipText"));                              // NOI18N
        cmdAutoCreateGeometries.setBorderPainted(false);
        cmdAutoCreateGeometries.setContentAreaFilled(false);
        cmdAutoCreateGeometries.setEnabled(false);
        cmdAutoCreateGeometries.setFocusPainted(false);
        cmdAutoCreateGeometries.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdAutoCreateGeometriesActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        jPanel3.add(cmdAutoCreateGeometries, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 3, 2, 3);
        jPanel1.add(jPanel3, gridBagConstraints);

        add(jPanel1, java.awt.BorderLayout.CENTER);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     */
    private void refreshLstAlkisLandparcels() {
        final boolean enabled = CidsAppBackend.getInstance().isEditable()
                    && (lstKassenzeichenGeometrien.getSelectedIndices().length > 0);
        cmdRemoveKassenzeichenGeometrien.setEnabled(enabled);

        final FeatureCollection featureCollection = CidsAppBackend.getInstance().getMainMap().getFeatureCollection();

        try {
//
//            final Collection<Feature> alkisLandparcelFeaturesToRemove = new ArrayList<Feature>();
//            for (final Feature feature : featureCollection.getAllFeatures()) {
//                if (feature instanceof CidsFeature) {
//                    final CidsFeature cidsFeature = (CidsFeature)feature;
//                    if (cidsFeature.getMetaClass().getTableName().equalsIgnoreCase(
//                                    "alkis_landparcel")) {
//                        alkisLandparcelFeaturesToRemove.add(cidsFeature);
//                    }
//                }
//            }
//            try {
//                featureCollection.removeFeatureCollectionListener(this);
//                featureCollection.removeFeatures(alkisLandparcelFeaturesToRemove);
//            } finally {
//                featureCollection.addFeatureCollectionListener(this);
//            }
//
            final DefaultListModel alkisLandparcelListModel = (DefaultListModel)lstAlkisLandparcels.getModel();
            alkisLandparcelListModel.clear();
            final int[] selectedIndices = lstKassenzeichenGeometrien.getSelectedIndices();
            if (selectedIndices.length == 0) {
                try {
                    featureCollection.removeFeatureCollectionListener(this);
                    featureCollection.select(new ArrayList<Feature>());
                } finally {
                    featureCollection.addFeatureCollectionListener(this);
                }
            } else {
                alkisLandparcelListModel.addElement("Alkis-Flurstücke werden geladen...");

                final Collection<CidsBean> selectedKassenzeichenGeometrieBeans = new ArrayList<CidsBean>();
                final Collection<Feature> featuresToSelect = new ArrayList<Feature>();
                for (int index = 0; index < selectedIndices.length; ++index) {
                    final int selectedIndex = selectedIndices[index];
                    final Object listObject = lstKassenzeichenGeometrien.getModel().getElementAt(selectedIndex);
                    if (listObject instanceof CidsBean) {
                        final CidsBean kassenzeichenGeometrieGeomBean = (CidsBean)listObject;
                        final CidsFeature kassenzeichenGeometrieFeature = new CidsFeature(
                                kassenzeichenGeometrieGeomBean.getMetaObject());

                        selectedKassenzeichenGeometrieBeans.add(kassenzeichenGeometrieGeomBean);
                        featuresToSelect.add(kassenzeichenGeometrieFeature);
                        kassenzeichenGeometrieFeature.setEditable(CidsAppBackend.getInstance().isEditable());
                    }
                }

                try {
                    featureCollection.removeFeatureCollectionListener(this);
                    featureCollection.select(featuresToSelect);
                } finally {
                    featureCollection.addFeatureCollectionListener(this);
                }

                WORKER = new AlkisLandparcelWorker(selectedKassenzeichenGeometrieBeans);
                WORKER.execute();
            }
        } catch (Exception ex) {
            LOG.error(ex, ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstKassenzeichenGeometrienValueChanged(final javax.swing.event.ListSelectionEvent evt) { //GEN-FIRST:event_lstKassenzeichenGeometrienValueChanged
        if (evt.getValueIsAdjusting()) {
            return;
        }
        refreshLstAlkisLandparcels();
        Main.getInstance().selectionChanged();
    }                                                                                                     //GEN-LAST:event_lstKassenzeichenGeometrienValueChanged

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstAlkisLandparcelsValueChanged(final javax.swing.event.ListSelectionEvent evt) { //GEN-FIRST:event_lstAlkisLandparcelsValueChanged
        final boolean enabled = lstAlkisLandparcels.getSelectedIndices().length > 0;
        cmdShowAlkisRendererForSelected.setEnabled(enabled);

        final Collection<Feature> selectedFeatures = new ArrayList<Feature>();
        final int[] selectionIndices = lstAlkisLandparcels.getSelectedIndices();
        for (int index = 0; index < selectionIndices.length; ++index) {
            final int selectedIndex = selectionIndices[index];
            final Object listObject = lstAlkisLandparcels.getModel().getElementAt(selectedIndex);
            if (listObject instanceof CidsBean) {
                final CidsBean alkisLandparcelBean = (CidsBean)listObject;
                final CidsFeature alkisLandparcelFeature = new CidsFeature(alkisLandparcelBean.getMetaObject());
                selectedFeatures.add(alkisLandparcelFeature);
            }
        }

        if (tglShowAlkisLandparcelGeoms.isSelected()) {
            showThisFeatures(selectedFeatures, "alkis_landparcel");
        }
    } //GEN-LAST:event_lstAlkisLandparcelsValueChanged

    /**
     * DOCUMENT ME!
     *
     * @param  featuresToShow  DOCUMENT ME!
     * @param  mcName          DOCUMENT ME!
     */
    private void showThisFeatures(final Collection<Feature> featuresToShow, final String mcName) {
        final FeatureCollection featureCollection = CidsAppBackend.getInstance().getMainMap().getFeatureCollection();

        final Collection<Feature> allFeatures = featureCollection.getAllFeatures();
        for (final Feature feature : allFeatures) {
            if (feature instanceof CidsFeature) {
                final CidsFeature cidsFeature = (CidsFeature)feature;
                if (cidsFeature.getMetaObject().getMetaClass().getTableName().equalsIgnoreCase(
                                mcName)) {
                    final boolean show = featuresToShow.contains(feature);
                    CidsAppBackend.getInstance().getMainMap().getPFeatureHM().get(feature).setVisible(show);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstAlkisLandparcelsMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lstAlkisLandparcelsMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON3) {
            lstAlkisLandparcels.clearSelection();
        } else if (evt.getClickCount() == 2) {
            final int selectedIndex = lstAlkisLandparcels.getSelectedIndex();
            final Object listObject = lstAlkisLandparcels.getModel().getElementAt(selectedIndex);
            if (listObject instanceof CidsBean) {
                final CidsBean alkisLandparcelBean = (CidsBean)listObject;
                Main.getInstance().showRenderer(alkisLandparcelBean.getMetaObject());
            }
        }
    }                                                                                   //GEN-LAST:event_lstAlkisLandparcelsMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdShowAlkisRendererForSelectedActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdShowAlkisRendererForSelectedActionPerformed
        final int[] selectedIndices = lstAlkisLandparcels.getSelectedIndices();
        final List<MetaObject> coll = new ArrayList<MetaObject>();
        for (int index = 0; index < selectedIndices.length; ++index) {
            final int selectedIndex = selectedIndices[index];
            final Object listObject = lstAlkisLandparcels.getModel().getElementAt(selectedIndex);
            if (listObject instanceof CidsBean) {
                final CidsBean kassenzeichenGeometrieBean = (CidsBean)listObject;
                coll.add(kassenzeichenGeometrieBean.getMetaObject());
            }
        }
        Main.getInstance().showRenderer(coll.toArray(new MetaObject[0]));
    }                                                                                                   //GEN-LAST:event_cmdShowAlkisRendererForSelectedActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void tglShowAlkisLandparcelGeomsActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_tglShowAlkisLandparcelGeomsActionPerformed
        final Collection<Feature> featuresToShow = new ArrayList<Feature>();
        if (tglShowAlkisLandparcelGeoms.isSelected()) {
            final int[] selectedIndices = lstAlkisLandparcels.getSelectedIndices();
            for (int index = 0; index < selectedIndices.length; ++index) {
                final int selectedIndex = selectedIndices[index];
                final Object listObject = lstAlkisLandparcels.getModel().getElementAt(selectedIndex);
                if (listObject instanceof CidsBean) {
                    final CidsBean cidsBean = (CidsBean)listObject;
                    featuresToShow.add(new CidsFeature(cidsBean.getMetaObject()));
                }
            }
        }
        showThisFeatures(featuresToShow, "alkis_landparcel");
    }                                                                                               //GEN-LAST:event_tglShowAlkisLandparcelGeomsActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstKassenzeichenGeometrienMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lstKassenzeichenGeometrienMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON3) {
            lstKassenzeichenGeometrien.clearSelection();
        }
    }                                                                                          //GEN-LAST:event_lstKassenzeichenGeometrienMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void tglShowKassenzeichenGeometrienActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_tglShowKassenzeichenGeometrienActionPerformed
        final Collection<Feature> featuresToShow = new ArrayList<Feature>();
        final CidsBean kassenzBean = getCidsBean();
        if (kassenzBean != null) {
            if (tglShowKassenzeichenGeometrien.isSelected()) {
                for (final CidsBean kassenzeichenGeometrieBean
                            : kassenzBean.getBeanCollectionProperty(
                                VerdisConstants.PROP.KASSENZEICHEN.KASSENZEICHEN_GEOMETRIEN)) {
                    final CidsFeature kassenzeichenGeometrieFeature = new CidsFeature(
                            kassenzeichenGeometrieBean.getMetaObject());
                    featuresToShow.add(kassenzeichenGeometrieFeature);
                }
            }
        }
        showThisFeatures(featuresToShow, VerdisConstants.MC.KASSENZEICHEN_GEOMETRIE);
    }                                                                                                  //GEN-LAST:event_tglShowKassenzeichenGeometrienActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdRemoveKassenzeichenGeometrienActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdRemoveKassenzeichenGeometrienActionPerformed
        ((KassenzeichenGeometrienList)lstKassenzeichenGeometrien).removeSelectedBeans();
        refreshAutoCreateGeometriesButton();
    }                                                                                                    //GEN-LAST:event_cmdRemoveKassenzeichenGeometrienActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdShowAlkisRendererForAllActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdShowAlkisRendererForAllActionPerformed
        final DefaultListModel alkisLandparcelListModel = (DefaultListModel)lstAlkisLandparcels.getModel();

        final int size = alkisLandparcelListModel.getSize();
        final List<MetaObject> coll = new ArrayList<MetaObject>();
        for (int index = 0; index < size; ++index) {
            final Object listObject = lstAlkisLandparcels.getModel().getElementAt(index);
            if (listObject instanceof CidsBean) {
                final CidsBean alkisLandparcelBean = (CidsBean)listObject;
                coll.add(alkisLandparcelBean.getMetaObject());
            }
        }
        Main.getInstance().showRenderer(coll.toArray(new MetaObject[0]));
    } //GEN-LAST:event_cmdShowAlkisRendererForAllActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdShowAlkisRendererActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdShowAlkisRendererActionPerformed
        cmdShowAlkisRenderer.setEnabled(false);
        new SwingWorker<Collection<CidsBean>, Void>() {

                @Override
                protected Collection<CidsBean> doInBackground() throws Exception {
                    final CidsBean kassenzBean = getCidsBean();
                    if (kassenzBean != null) {
                        final Geometry unionGeom = getKassenzeichenGeometrieUnionGeom(
                                kassenzBean.getBeanCollectionProperty(
                                    VerdisConstants.PROP.KASSENZEICHEN.KASSENZEICHEN_GEOMETRIEN));
                        final Collection<CidsBean> alkisLandparcelBeans = searchAlkisLandparcelBeans(unionGeom);
                        return alkisLandparcelBeans;
                    } else {
                        return null;
                    }
                }

                @Override
                protected void done() {
                    try {
                        final Collection<CidsBean> alkisLandparcelBeans = get();

                        final List<MetaObject> coll = new ArrayList<MetaObject>();
                        if (alkisLandparcelBeans != null) {
                            for (final CidsBean alkisLandparcelBean : alkisLandparcelBeans) {
                                coll.add(alkisLandparcelBean.getMetaObject());
                            }
                        }
                        Main.getInstance().showRenderer(coll.toArray(new MetaObject[0]));
                    } catch (final Exception ex) {
                        LOG.error(ex, ex);
                    }
                    cmdShowAlkisRenderer.setEnabled(true);
                }
            }.execute();
    } //GEN-LAST:event_cmdShowAlkisRendererActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdAutoCreateGeometriesActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdAutoCreateGeometriesActionPerformed
        final JDialog dialog = new JDialog((JFrame)null, "Bitte warten...", true) {

                @Override
                public void dispose() {
                    super.dispose();
                }
            };

        final JProgressBar bar = new JProgressBar();
        bar.setIndeterminate(true);
        bar.setString(" Kassenzeichen-Geometrien werden erstellt. ");
        bar.setStringPainted(true);
//        bar.setMinimumSize(new Dimension(200, 50));
//        bar.setPreferredSize(bar.getMinimumSize());
        dialog.add(BorderLayout.CENTER, bar);
        // dlg.add(BorderLayout.NORTH, new JLabel("Progress..."));
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);
        dialog.pack();

        new SwingWorker<Collection<CidsBean>, Object>() {

                @Override
                protected Collection<CidsBean> doInBackground() throws Exception {
                    SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                StaticSwingTools.showDialog(dialog);
                            }
                        });

                    Geometry unionGeom = null;
                    final CidsBean kassenzBean = getCidsBean();
                    if (kassenzBean != null) {
                        for (final CidsBean flaecheBean
                                    : (Collection<CidsBean>)kassenzBean.getBeanCollectionProperty(
                                        VerdisConstants.PROP.KASSENZEICHEN.FLAECHEN)) {
                            final Geometry flaecheGeom = (Geometry)flaecheBean.getProperty(
                                    VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                            + "."
                                            + VerdisConstants.PROP.FLAECHENINFO.GEOMETRIE
                                            + "."
                                            + VerdisConstants.PROP.GEOM.GEO_FIELD);
                            if (flaecheGeom != null) {
                                final Geometry bufferedGeom = flaecheGeom.buffer(FLAECHE_GEOM_BUFFER);
                                if (unionGeom == null) {
                                    unionGeom = (Geometry)bufferedGeom;
                                } else {
                                    unionGeom = unionGeom.union(bufferedGeom);
                                }
                            }
                        }
                    }

                    final Collection<CidsBean> alkisLandparcelBeans = searchAlkisLandparcelBeans(unionGeom);
                    return alkisLandparcelBeans;
                }

                @Override
                protected void done() {
                    try {
                        final Collection<CidsBean> alkisLandparcelBeans = get();

                        final Collection<CidsBean> kassenzeichenGeometrieBeansToAdd = new ArrayList<CidsBean>();
                        for (final CidsBean alkisLandparcelBean : alkisLandparcelBeans) {
                            final String bezeichnung = (String)alkisLandparcelBean.getProperty("alkis_id");
                            final Geometry alkisLandparcelGeom = (Geometry)alkisLandparcelBean.getProperty(
                                    "geometrie.geo_field");

                            final String currentCrs = CrsTransformer.createCrsFromSrid(CrsTransformer.getCurrentSrid());
                            final Geometry transformedAlkisLandparcelGeom = CrsTransformer.transformToGivenCrs(
                                    (Geometry)alkisLandparcelGeom.clone(),
                                    currentCrs);
                            transformedAlkisLandparcelGeom.setSRID(CrsTransformer.getCurrentSrid());

                            final CidsBean kassenzeichenGeometrieBean = createNewKassenzeichenGeometrieBean(
                                    transformedAlkisLandparcelGeom,
                                    bezeichnung,
                                    false);
                            kassenzeichenGeometrieBeansToAdd.add(kassenzeichenGeometrieBean);
                        }
                        ((KassenzeichenGeometrienList)lstKassenzeichenGeometrien).addKassenzeichenGeometrieBeans(
                            kassenzeichenGeometrieBeansToAdd,
                            false);
                    } catch (final Exception ex) {
                        LOG.error("error while creating kassenzeichenGeometrie beans", ex);
                    } finally {
                        dialog.dispose();
                        refreshAutoCreateGeometriesButton();
                    }
                }
            }.execute();
    } //GEN-LAST:event_cmdAutoCreateGeometriesActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param   geometry     DOCUMENT ME!
     * @param   bezeichnung  DOCUMENT ME!
     * @param   istFrei      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public CidsBean createNewKassenzeichenGeometrieBean(final Geometry geometry,
            final String bezeichnung,
            final boolean istFrei) throws Exception {
        final CidsBean geomBean = CidsBean.createNewCidsBeanFromTableName(
                VerdisConstants.DOMAIN,
                VerdisConstants.MC.GEOM);
        geomBean.setProperty(VerdisConstants.PROP.GEOM.GEO_FIELD, geometry);

        final CidsBean kassenzeichenGeometrieBean = CidsBean.createNewCidsBeanFromTableName(
                VerdisConstants.DOMAIN,
                VerdisConstants.MC.KASSENZEICHEN_GEOMETRIE);
        kassenzeichenGeometrieBean.setProperty(VerdisConstants.PROP.KASSENZEICHEN_GEOMETRIE.ISTFREI, istFrei);
        kassenzeichenGeometrieBean.setProperty(VerdisConstants.PROP.KASSENZEICHEN_GEOMETRIE.GEOMETRIE, geomBean);
        kassenzeichenGeometrieBean.setProperty(VerdisConstants.PROP.KASSENZEICHEN_GEOMETRIE.NAME, bezeichnung);

        kassenzeichenGeometrieBean.getMetaObject().setID(KassenzeichenGeometrienPanel.getNewKassenzeichenGeometrieId());

        return kassenzeichenGeometrieBean;
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
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        bindingGroup.unbind();
        if (kassenzeichenBean != null) {
            final ObservableList<CidsBean> kassenzeichenGeometrieList = (ObservableList<CidsBean>)
                kassenzeichenBean.getBeanCollectionProperty(
                    VerdisConstants.PROP.KASSENZEICHEN.KASSENZEICHEN_GEOMETRIEN);
            kassenzeichenGeometrieList.removeObservableListListener(kassenzeichenGeometrieListListener);
        }
        kassenzeichenBean = cidsBean;
        if (kassenzeichenBean != null) {
            final ObservableList<CidsBean> kassenzeichenGeometrieList = (ObservableList<CidsBean>)
                kassenzeichenBean.getBeanCollectionProperty(
                    VerdisConstants.PROP.KASSENZEICHEN.KASSENZEICHEN_GEOMETRIEN);
            kassenzeichenGeometrieList.addObservableListListener(kassenzeichenGeometrieListListener);
        }
        bindingGroup.bind();

        refreshCmdShowAlkisRendererVisibility();
    }

    /**
     * DOCUMENT ME!
     */
    @Override
    public void editModeChanged() {
        final boolean isEditable = CidsAppBackend.getInstance().isEditable();
        setEnabled(isEditable);
        cmdRemoveKassenzeichenGeometrien.setEnabled(isEditable
                    && (lstKassenzeichenGeometrien.getSelectedIndices().length > 0));
        refreshAutoCreateGeometriesButton();
    }

    /**
     * DOCUMENT ME!
     */
    public void refreshAutoCreateGeometriesButton() {
        final boolean isEditable = CidsAppBackend.getInstance().isEditable();
        boolean hasFlaecheGeoms = false;
        boolean hasAllgGeoms = false;
        final CidsBean kassenzBean = getCidsBean();
        if (kassenzBean != null) {
            for (final CidsBean flaecheBean
                        : (Collection<CidsBean>)kassenzBean.getBeanCollectionProperty(
                            VerdisConstants.PROP.KASSENZEICHEN.FLAECHEN)) {
                if (
                    flaecheBean.getProperty(
                                VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                + "."
                                + VerdisConstants.PROP.FLAECHENINFO.GEOMETRIE)
                            != null) {
                    hasFlaecheGeoms = true;
                    break;
                }
            }
            hasAllgGeoms =
                !((Collection<CidsBean>)kassenzBean.getBeanCollectionProperty(
                        VerdisConstants.PROP.KASSENZEICHEN.KASSENZEICHEN_GEOMETRIEN)).isEmpty();
        }

        cmdAutoCreateGeometries.setEnabled(isEditable && hasFlaecheGeoms && !hasAllgGeoms);
    }
    /**
     * DOCUMENT ME!
     *
     * @param  bln  DOCUMENT ME!
     */
    @Override
    public void setEnabled(final boolean bln) {
        super.setEnabled(bln);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  fce  DOCUMENT ME!
     */
    @Override
    public void featuresAdded(final FeatureCollectionEvent fce) {
        final Collection<Feature> addedFeatures = fce.getEventFeatures();
        for (final Feature addedFeature : addedFeatures) {
            if (addedFeature instanceof CidsFeature) {
                final CidsFeature addedCidsFeature = (CidsFeature)addedFeature;
                final String addedMCName = addedCidsFeature.getMetaClass().getTableName();
                if (addedMCName.equalsIgnoreCase(VerdisConstants.MC.KASSENZEICHEN_GEOMETRIE)) {
                    final PFeature pFeature = CidsAppBackend.getInstance()
                                .getMainMap()
                                .getPFeatureHM()
                                .get(addedCidsFeature);
                    if (pFeature != null) {
                        pFeature.setVisible(tglShowKassenzeichenGeometrien.isSelected());
                    }
                }
            }
        }
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
     *
     * @param  fce  DOCUMENT ME!
     */
    @Override
    public void featuresRemoved(final FeatureCollectionEvent fce) {
        final CidsBean kassenzBean = getCidsBean();
        if (kassenzBean != null) {
            final Collection<CidsBean> kassenzeichenGeometrieBeans = kassenzBean.getBeanCollectionProperty(
                    VerdisConstants.PROP.KASSENZEICHEN.KASSENZEICHEN_GEOMETRIEN);
            final Collection<Feature> removedFeatures = fce.getEventFeatures();
            for (final Feature feature : removedFeatures) {
                if (feature instanceof CidsFeature) {
                    final CidsFeature cidsFeature = (CidsFeature)feature;
                    final CidsBean toRemoveBean = cidsFeature.getMetaObject().getBean();
                    kassenzeichenGeometrieBeans.remove(toRemoveBean);
                }
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
        final Collection<Feature> changedFeatures = fce.getEventFeatures();
        if (changedFeatures != null) {
            boolean refreshNeeded = false;
            for (final Feature changedFeature : changedFeatures) {
                if (changedFeature instanceof CidsFeature) {
                    final CidsFeature changedCidsFeature = (CidsFeature)changedFeature;
                    if (changedCidsFeature.getMetaObject().getMetaClass().getTableName().equalsIgnoreCase(
                                    VerdisConstants.MC.KASSENZEICHEN_GEOMETRIE)) {
                        try {
                            final CidsBean kassenzeichenGeometrieBean = changedCidsFeature.getMetaObject().getBean();
                            final String alterText = (String)kassenzeichenGeometrieBean.getProperty(
                                    VerdisConstants.PROP.KASSENZEICHEN_GEOMETRIE.NAME);
                            final Boolean alterIstFrei = (Boolean)kassenzeichenGeometrieBean.getProperty(
                                    VerdisConstants.PROP.KASSENZEICHEN_GEOMETRIE.ISTFREI);
                            kassenzeichenGeometrieBean.setProperty(
                                VerdisConstants.PROP.KASSENZEICHEN_GEOMETRIE.ISTFREI,
                                true);
                            kassenzeichenGeometrieBean.setProperty(
                                VerdisConstants.PROP.KASSENZEICHEN_GEOMETRIE.NAME,
                                "freie Geometrie");

                            if (!refreshNeeded) {
                                final int[] selectedIndices = lstKassenzeichenGeometrien.getSelectedIndices();
                                for (int index = 0; index < selectedIndices.length; index++) {
                                    final int selectedIndex = selectedIndices[index];
                                    final Object lstItem = lstKassenzeichenGeometrien.getModel()
                                                .getElementAt(selectedIndex);
                                    if (lstItem instanceof CidsBean) {
                                        final CidsBean selectedKassenzeichenGeometrieBean = (CidsBean)lstItem;
                                        if (selectedKassenzeichenGeometrieBean.equals(kassenzeichenGeometrieBean)) {
                                            refreshNeeded = true;
                                        }
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            LOG.error("error while modifying landparcelgeom", ex);
                        }
                    }
                }
                if (refreshNeeded) {
                    refreshLstAlkisLandparcels();
                }
            }
        }
        lstKassenzeichenGeometrien.repaint();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  fce  DOCUMENT ME!
     */
    @Override
    public void featureSelectionChanged(final FeatureCollectionEvent fce) {
        SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    if (fce.getEventFeatures() == null) {
                        return;
                    }
                    final FeatureCollection featureCollection = CidsAppBackend.getInstance()
                                .getMainMap()
                                .getFeatureCollection();

                    final CidsBean kassenzBean = getCidsBean();
                    if (kassenzBean != null) {
                        final Collection<Feature> selectedFeatures = featureCollection.getSelectedFeatures();
                        final List<CidsBean> kassenzeichenGeometrieBeans = kassenzBean.getBeanCollectionProperty(
                                VerdisConstants.PROP.KASSENZEICHEN.KASSENZEICHEN_GEOMETRIEN);

                        final List<Integer> indicesToSelect = new ArrayList<Integer>();
                        if (selectedFeatures != null) {
                            for (final Feature selectedFeature : selectedFeatures) {
                                if (selectedFeature instanceof CidsFeature) {
                                    final CidsFeature selectedCidsFeature = (CidsFeature)selectedFeature;
                                    final int indexToSelect = kassenzeichenGeometrieBeans.indexOf(
                                            selectedCidsFeature.getMetaObject().getBean());
                                    if (indexToSelect >= 0) {
                                        indicesToSelect.add(indexToSelect);
                                    }
                                }
                            }
                        }
                        final int[] indicesArr = new int[indicesToSelect.size()];
                        for (int index = 0; index < indicesArr.length; index++) {
                            indicesArr[index] = indicesToSelect.get(index);
                        }

                        lstKassenzeichenGeometrien.setSelectedIndices(indicesArr);
                        Main.getInstance().selectionChanged();
                    }
                }
            });
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
     */
    @Override
    public void featureCollectionChanged() {
    }

    /**
     * DOCUMENT ME!
     *
     * @param  notification  DOCUMENT ME!
     */
    public void attachFeatureRequested(final PNotification notification) {
        final Object notificationObject = notification.getObject();
        if (notificationObject instanceof AttachFeatureListener) {
            final AttachFeatureListener afl = (AttachFeatureListener)notificationObject;
            final PFeature pFeatureToAttach = afl.getFeatureToAttach();
            if (pFeatureToAttach.getFeature() instanceof PureNewFeature) {
                try {
                    final Geometry geometry = pFeatureToAttach.getFeature().getGeometry();

                    final CidsBean kassenzeichenGeometrieBean = createNewKassenzeichenGeometrieBean(
                            geometry,
                            "freie Geometrie",
                            true);
                    ((KassenzeichenGeometrienList)lstKassenzeichenGeometrien).addKassenzeichenGeometrieBean(
                        kassenzeichenGeometrieBean,
                        true);
                    Main.getMappingComponent().getFeatureCollection().removeFeature(pFeatureToAttach.getFeature());
                } catch (Exception ex) {
                    LOG.error("error while attaching feature", ex);
                }
            } else if (pFeatureToAttach.getFeature() instanceof CidsFeature) {
                JOptionPane.showMessageDialog(
                    Main.getMappingComponent(),
                    "Es k\u00F6nnen nur nicht bereits zugeordnete Fl\u00E4chen zugeordnet werden.");
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static int getNewKassenzeichenGeometrieId() {
        return NEW_KASSENZEICHEN_GEOMETRIE_ID--;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   kassenzeichenGeometrieBeans  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Geometry getKassenzeichenGeometrieUnionGeom(final Collection<CidsBean> kassenzeichenGeometrieBeans) {
        Geometry unionGeom = null;
        for (final CidsBean kassenzeichenGeometrieBean : kassenzeichenGeometrieBeans) {
            final Geometry geom = (Geometry)kassenzeichenGeometrieBean.getProperty(
                    VerdisConstants.PROP.KASSENZEICHEN_GEOMETRIE.GEOMETRIE
                            + "."
                            + VerdisConstants.PROP.GEOM.GEO_FIELD);
            final Geometry bufferedGeom = geom.buffer(ALKIS_LANDPARCEL_GEOM_BUFFER);
            if (unionGeom == null) {
                unionGeom = (Geometry)bufferedGeom;
            } else {
                final int srid = unionGeom.getSRID();
                unionGeom = unionGeom.union(bufferedGeom);
                unionGeom.setSRID(srid);
            }
        }
        return unionGeom;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   geometry  geometry DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Collection<CidsBean> searchAlkisLandparcelBeans(final Geometry geometry) {
        try {
            final AlkisLandparcelSearch serverSearch = new AlkisLandparcelSearch();
            final String crs = serverSearch.getCrs();

            final Geometry transformedGeom = CrsTransformer.transformToGivenCrs(geometry, crs);

            transformedGeom.setSRID(CrsTransformer.extractSridFromCrs(crs));
            serverSearch.setGeometry(transformedGeom);

            final List<Integer> alkisLandparcelIds = (List<Integer>)SessionManager.getProxy()
                        .customServerSearch(SessionManager.getSession().getUser(), serverSearch);

            if (alkisLandparcelIds.isEmpty()) {
                return null;
            }

            final StringBuilder idStringBuilder = new StringBuilder();
            for (int index = 0; index < alkisLandparcelIds.size(); index++) {
                final Integer alkisLandparcel = alkisLandparcelIds.get(index);
                if (index > 0) {
                    idStringBuilder.append(", ");
                }
                idStringBuilder.append(Integer.toString(alkisLandparcel));
            }
            final MetaClass mc = CidsBean.getMetaClassFromTableName(
                    "WUNDA_BLAU",
                    "alkis_landparcel");
            final MetaObject[] mos = SessionManager.getProxy()
                        .getMetaObjectByQuery(SessionManager.getSession().getUser(),
                            "SELECT "
                            + mc.getId()
                            + ", id FROM alkis_landparcel WHERE id IN ("
                            + idStringBuilder.toString()
                            + ")",
                            "WUNDA_BLAU");

            final Collection<CidsBean> alkisLandparcelBeans = new ArrayList<CidsBean>();
            for (final MetaObject mo : mos) {
                alkisLandparcelBeans.add(mo.getBean());
            }
            return alkisLandparcelBeans;
        } catch (final Exception ex) {
            LOG.error("error while searching alkis landparcels", ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void refreshCmdShowAlkisRendererVisibility() {
        final int size = lstKassenzeichenGeometrien.getModel().getSize();
        cmdShowAlkisRenderer.setEnabled(size > 0);
    }

    /**
     * DOCUMENT ME!
     */
    private void refreshCmdShowAlkisRendererForAllVisibility() {
        final int size = lstAlkisLandparcels.getModel().getSize();
        cmdShowAlkisRendererForAll.setEnabled(size > 0);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public KassenzeichenGeometrienList getKassenzeichenGeometrienList() {
        return (KassenzeichenGeometrienList)lstKassenzeichenGeometrien;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class AlkisLandparcelListCellRenderer extends DefaultListCellRenderer {

        //~ Static fields/initializers -----------------------------------------

        private static final int SPACING = 5;
        private static final int MARKER_WIDTH = 4;

        //~ Instance fields ----------------------------------------------------

        private boolean selected = false;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new FancyListCellRenderer object.
         */
        public AlkisLandparcelListCellRenderer() {
            setOpaque(false);
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param   list          DOCUMENT ME!
         * @param   value         DOCUMENT ME!
         * @param   index         DOCUMENT ME!
         * @param   isSelected    DOCUMENT ME!
         * @param   cellHasFocus  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        @Override
        public Component getListCellRendererComponent(final JList list,
                final Object value,
                final int index,
                final boolean isSelected,
                final boolean cellHasFocus) {
            selected = isSelected;
            if (value instanceof CidsBean) {
                final CidsBean bean = (CidsBean)value;
                final Component comp = super.getListCellRendererComponent(
                        list,
                        bean.getProperty("alkis_id"),
                        index,
                        isSelected,
                        cellHasFocus);
                final List<CidsBean> kassenzeichenGeometrieBeans;
                if (WORKER == null) {
                    kassenzeichenGeometrieBeans = null;
                } else {
                    kassenzeichenGeometrieBeans = WORKER.getAlkisLandparcelToKassenzeichenGeometrieMap().get(bean);
                }
                final Color color;
                if ((kassenzeichenGeometrieBeans == null) || kassenzeichenGeometrieBeans.isEmpty()) {
                    color = null;
                } else if (kassenzeichenGeometrieBeans.size() > 1) {
                    color = Color.GRAY;
                } else {
                    int colorIndex = kassenzeichenGeometrieBeans.get(0).getMetaObject().getId();
                    if (colorIndex < 0) {
                        colorIndex = -colorIndex;
                    }
                    colorIndex %= KassenzeichenGeometrienPanel.LANDPARCEL_COLORS.size();

                    color = LANDPARCEL_COLORS.get(colorIndex);
                }
                setBackground(color);
                setBorder(BorderFactory.createEmptyBorder(1, (2 * SPACING) + MARKER_WIDTH, 1, 0));
                return comp;
            } else {
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @param  g  DOCUMENT ME!
         */
        @Override
        protected void paintComponent(final Graphics g) {
            if (getBackground() != null) {
                final Graphics2D g2d = (Graphics2D)g;
                final Paint backup = g2d.getPaint();
                if (selected) {
                    g2d.setColor(javax.swing.UIManager.getDefaults().getColor("List.selectionBackground"));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
                g2d.setColor(getBackground());
                g2d.fillRect(SPACING, 0, MARKER_WIDTH, getHeight());
                g2d.setPaint(backup);
            }
            super.paintComponent(g);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class KassenzeichenGeometrieCellRenderer extends DefaultListCellRenderer {

        //~ Static fields/initializers -----------------------------------------

        private static final int SPACING = 5;
        private static final int MARKER_WIDTH = 4;

        //~ Instance fields ----------------------------------------------------

        private boolean selected = false;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new FancyListCellRenderer object.
         */
        public KassenzeichenGeometrieCellRenderer() {
            setOpaque(false);
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param   list          DOCUMENT ME!
         * @param   value         DOCUMENT ME!
         * @param   index         DOCUMENT ME!
         * @param   isSelected    DOCUMENT ME!
         * @param   cellHasFocus  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        @Override
        public Component getListCellRendererComponent(final JList list,
                final Object value,
                final int index,
                final boolean isSelected,
                final boolean cellHasFocus) {
            final CidsBean bean = (CidsBean)value;
            final Component comp = super.getListCellRendererComponent(
                    list,
                    bean.getProperty(VerdisConstants.PROP.KASSENZEICHEN_GEOMETRIE.NAME),
                    index,
                    isSelected,
                    cellHasFocus);
            selected = isSelected;
            int colorIndex = bean.getMetaObject().getId();
            if (colorIndex < 0) {
                colorIndex = -colorIndex;
            }
            colorIndex %= KassenzeichenGeometrienPanel.LANDPARCEL_COLORS.size();

            setBackground(LANDPARCEL_COLORS.get(colorIndex));
            setBorder(BorderFactory.createEmptyBorder(1, (2 * SPACING) + MARKER_WIDTH, 1, 0));
            return comp;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  g  DOCUMENT ME!
         */
        @Override
        protected void paintComponent(final Graphics g) {
            final Graphics2D g2d = (Graphics2D)g;
            final Paint backup = g2d.getPaint();
            if (selected) {
                g2d.setColor(javax.swing.UIManager.getDefaults().getColor("List.selectionBackground"));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
            g2d.setColor(getBackground());
            g2d.fillRect(SPACING, 0, MARKER_WIDTH, getHeight());
            g2d.setPaint(backup);
            super.paintComponent(g);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class AlkisLandparcelWorker extends SwingWorker<Collection<CidsBean>, Void> {

        //~ Instance fields ----------------------------------------------------

        private final FeatureCollection featureCollection = CidsAppBackend.getInstance()
                    .getMainMap()
                    .getFeatureCollection();
        private final Collection<CidsBean> kassenzeichenGeometrieBeans;
        private final Map<CidsBean, List<CidsBean>> alkisLandparcelToKassenzeichenGeometrieMap =
            new HashMap<CidsBean, List<CidsBean>>();

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new AlkisLandparcelWorker object.
         *
         * @param  kassenzeichenGeometrieBeans  serverSearch DOCUMENT ME!
         */
        public AlkisLandparcelWorker(final Collection<CidsBean> kassenzeichenGeometrieBeans) {
            this.kassenzeichenGeometrieBeans = kassenzeichenGeometrieBeans;
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         *
         * @throws  Exception  DOCUMENT ME!
         */
        @Override
        protected Collection<CidsBean> doInBackground() throws Exception {
            final Geometry unionGeom = getKassenzeichenGeometrieUnionGeom(kassenzeichenGeometrieBeans);
            final Collection<CidsBean> alkisLandparcelBeans = searchAlkisLandparcelBeans(unionGeom);
            if (alkisLandparcelBeans != null) {
                for (final CidsBean alkisLandparcelBean : alkisLandparcelBeans) {
                    final List<CidsBean> assignedKassenzeichenGeometrieBeans;
                    if (alkisLandparcelToKassenzeichenGeometrieMap.get(alkisLandparcelBean)
                                == null) {
                        assignedKassenzeichenGeometrieBeans = new ArrayList<CidsBean>();
                        alkisLandparcelToKassenzeichenGeometrieMap.put(
                            alkisLandparcelBean,
                            assignedKassenzeichenGeometrieBeans);
                    } else {
                        assignedKassenzeichenGeometrieBeans = alkisLandparcelToKassenzeichenGeometrieMap.get(
                                alkisLandparcelBean);
                    }

                    final CidsBean kassenzBean = getCidsBean();
                    if (kassenzBean != null) {
                        final List<CidsBean> kassenzeichenGeometrieBeans = kassenzBean.getBeanCollectionProperty(
                                VerdisConstants.PROP.KASSENZEICHEN.KASSENZEICHEN_GEOMETRIEN);
                        for (final CidsBean kassenzeichenGeometrieBean : kassenzeichenGeometrieBeans) {
                            final Geometry kassenzeichenGeometrieGeom = (Geometry)
                                kassenzeichenGeometrieBean.getProperty(
                                    VerdisConstants.PROP.KASSENZEICHEN_GEOMETRIE.GEOMETRIE
                                            + "."
                                            + VerdisConstants.PROP.GEOM.GEO_FIELD);

                            final Geometry alkisLandparcelGeom = (Geometry)alkisLandparcelBean.getProperty(
                                    "geometrie.geo_field");

                            final String currentCrs = CrsTransformer.createCrsFromSrid(CrsTransformer.getCurrentSrid());
                            final Geometry transformedAlkisLandparcelGeom = CrsTransformer.transformToGivenCrs(
                                    (Geometry)alkisLandparcelGeom.clone(),
                                    currentCrs);
                            transformedAlkisLandparcelGeom.setSRID(CrsTransformer.getCurrentSrid());
                            alkisLandparcelBean.setProperty("geometrie.geo_field", transformedAlkisLandparcelGeom);

                            if (transformedAlkisLandparcelGeom.buffer(ALKIS_LANDPARCEL_GEOM_BUFFER).intersects(
                                            kassenzeichenGeometrieGeom)) {
                                assignedKassenzeichenGeometrieBeans.add(kassenzeichenGeometrieBean);
                            }
                        }
                    }
                }
            }
            return alkisLandparcelBeans;
        }

        /**
         * DOCUMENT ME!
         */
        @Override
        protected void done() {
            if (!isCancelled()) {
                try {
                    final DefaultListModel alkisLandparcelListModel = (DefaultListModel)lstAlkisLandparcels.getModel();
                    alkisLandparcelListModel.clear();

                    final Collection<CidsBean> alkisLandparcelBeans = get();

                    if (alkisLandparcelBeans != null) {
                        for (final CidsBean alkisLandparcelBean : alkisLandparcelBeans) {
                            final CidsFeature alkisLandparcelFeature = new CidsFeature(
                                    alkisLandparcelBean.getMetaObject());
                            alkisLandparcelListModel.addElement(alkisLandparcelBean);
                            try {
                                featureCollection.removeFeatureCollectionListener(
                                    KassenzeichenGeometrienPanel.this);
                                featureCollection.addFeature(alkisLandparcelFeature);
                            } finally {
                                featureCollection.addFeatureCollectionListener(KassenzeichenGeometrienPanel.this);
                            }
                            alkisLandparcelFeature.setEditable(false);
                            CidsAppBackend.getInstance()
                                    .getMainMap()
                                    .getPFeatureHM()
                                    .get(alkisLandparcelFeature)
                                    .setVisible(false);
                        }
                    }
                } catch (final Exception ex) {
                    LOG.error(ex, ex);
                }
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public Map<CidsBean, List<CidsBean>> getAlkisLandparcelToKassenzeichenGeometrieMap() {
            return alkisLandparcelToKassenzeichenGeometrieMap;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class KassenzeichenGeometrieListListener implements ObservableListListener {

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param  ol  DOCUMENT ME!
         * @param  i   DOCUMENT ME!
         * @param  i1  DOCUMENT ME!
         */
        @Override
        public void listElementsAdded(final ObservableList ol, final int i, final int i1) {
            refreshCmdShowAlkisRendererVisibility();
        }

        /**
         * DOCUMENT ME!
         *
         * @param  ol    DOCUMENT ME!
         * @param  i     DOCUMENT ME!
         * @param  list  DOCUMENT ME!
         */
        @Override
        public void listElementsRemoved(final ObservableList ol, final int i, final List list) {
            refreshCmdShowAlkisRendererVisibility();
        }

        /**
         * DOCUMENT ME!
         *
         * @param  ol  DOCUMENT ME!
         * @param  i   DOCUMENT ME!
         * @param  o   DOCUMENT ME!
         */
        @Override
        public void listElementReplaced(final ObservableList ol, final int i, final Object o) {
        }

        /**
         * DOCUMENT ME!
         *
         * @param  ol  DOCUMENT ME!
         * @param  i   DOCUMENT ME!
         */
        @Override
        public void listElementPropertyChanged(final ObservableList ol, final int i) {
        }
    }
}
