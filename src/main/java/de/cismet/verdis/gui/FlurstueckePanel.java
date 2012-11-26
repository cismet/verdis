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
package de.cismet.verdis.gui;

import Sirius.navigator.connection.SessionManager;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import com.vividsolutions.jts.geom.Geometry;

import edu.umd.cs.piccolox.event.PNotification;

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
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionListener;

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

import de.cismet.verdis.CidsAppBackend;
import de.cismet.verdis.EditModeListener;

import de.cismet.verdis.commons.constants.VerdisConstants;

import de.cismet.verdis.server.search.AlkisLandparcelSearch;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class FlurstueckePanel extends javax.swing.JPanel implements CidsBeanStore,
    EditModeListener,
    FeatureCollectionListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            FlurstueckePanel.class);
    private static final Color[] COLORS = new Color[] {
            new Color(41, 86, 178),
            new Color(101, 156, 239),
            new Color(125, 189, 0),
            new Color(220, 246, 0),
            new Color(255, 91, 0)
        };
    public static final List<Color> LANDPARCEL_COLORS = Collections.unmodifiableList(Arrays.asList(COLORS));
    private static final double LANDPARCEL_GEOM_BUFFER = -0.05;
    private static int NEW_FLURSTUECK_GEOM_ID = -1;

    private static final Map<CidsBean, List<CidsBean>> ALKIS_LANDPARCEL_TO_LANDPARCEL_GEOM_MAP =
        new HashMap<CidsBean, List<CidsBean>>();

    //~ Instance fields --------------------------------------------------------

    private CidsBean kassenzeichenBean;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdRemoveFlurstueckGeom;
    private javax.swing.JButton cmdShowAlkisRenderer;
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
    private javax.swing.JList lstFlurstueckGeoms;
    private javax.swing.JToggleButton tglShowAlkisLandparcelGeoms;
    private javax.swing.JToggleButton tglShowFlurstueckGeoms;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form AllgemeineInfos.
     */
    public FlurstueckePanel() {
        initComponents();
        lstFlurstueckGeoms.setCellRenderer(new FlurstueckGeomCellRenderer());
        lstAlkisLandparcels.setCellRenderer(new AlkisLandparcelListCellRenderer());
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
        lstFlurstueckGeoms = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstAlkisLandparcels = new javax.swing.JList();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cmdShowAlkisRenderer = new javax.swing.JButton();
        tglShowAlkisLandparcelGeoms = new javax.swing.JToggleButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        tglShowFlurstueckGeoms = new javax.swing.JToggleButton();
        jPanel5 = new javax.swing.JPanel();
        cmdRemoveFlurstueckGeom = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jScrollPane1.setMinimumSize(new java.awt.Dimension(23, 100));
        jScrollPane1.setName(""); // NOI18N
        jScrollPane1.setOpaque(false);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(258, 150));

        lstFlurstueckGeoms.setBackground(new java.awt.Color(242, 241, 240));
        lstFlurstueckGeoms.setOpaque(false);

        final org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create(
                "${cidsBean.flurstuecke}");
        final org.jdesktop.swingbinding.JListBinding jListBinding = org.jdesktop.swingbinding.SwingBindings
                    .createJListBinding(
                        org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                        this,
                        eLProperty,
                        lstFlurstueckGeoms,
                        "");
        bindingGroup.addBinding(jListBinding);

        lstFlurstueckGeoms.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lstFlurstueckGeomsMouseClicked(evt);
                }
            });
        lstFlurstueckGeoms.addListSelectionListener(new javax.swing.event.ListSelectionListener() {

                @Override
                public void valueChanged(final javax.swing.event.ListSelectionEvent evt) {
                    lstFlurstueckGeomsValueChanged(evt);
                }
            });
        jScrollPane1.setViewportView(lstFlurstueckGeoms);

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
                FlurstueckePanel.class,
                "FlurstueckePanel.lstAlkisLandparcels.toolTipText")); // NOI18N
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

        jLabel1.setText(org.openide.util.NbBundle.getMessage(FlurstueckePanel.class, "FlurstueckePanel.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel2.add(jLabel1, gridBagConstraints);

        cmdShowAlkisRenderer.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/alk.png"))); // NOI18N
        cmdShowAlkisRenderer.setText(org.openide.util.NbBundle.getMessage(
                FlurstueckePanel.class,
                "FlurstueckePanel.cmdShowAlkisRenderer.text"));                           // NOI18N
        cmdShowAlkisRenderer.setToolTipText(org.openide.util.NbBundle.getMessage(
                FlurstueckePanel.class,
                "FlurstueckePanel.cmdShowAlkisRenderer.toolTipText"));                    // NOI18N
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
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        jPanel2.add(cmdShowAlkisRenderer, gridBagConstraints);

        tglShowAlkisLandparcelGeoms.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/foreground.png")));    // NOI18N
        tglShowAlkisLandparcelGeoms.setText(org.openide.util.NbBundle.getMessage(
                FlurstueckePanel.class,
                "FlurstueckePanel.tglShowAlkisLandparcelGeoms.text"));                              // NOI18N
        tglShowAlkisLandparcelGeoms.setBorderPainted(false);
        tglShowAlkisLandparcelGeoms.setContentAreaFilled(false);
        tglShowAlkisLandparcelGeoms.setRolloverEnabled(false);
        tglShowAlkisLandparcelGeoms.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/foreground_on.png"))); // NOI18N
        tglShowAlkisLandparcelGeoms.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    tglShowAlkisLandparcelGeomsActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        jPanel2.add(tglShowAlkisLandparcelGeoms, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(jPanel4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(13, 3, 2, 3);
        jPanel1.add(jPanel2, gridBagConstraints);

        jPanel3.setLayout(new java.awt.GridBagLayout());

        jLabel2.setText(org.openide.util.NbBundle.getMessage(FlurstueckePanel.class, "FlurstueckePanel.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel3.add(jLabel2, gridBagConstraints);

        tglShowFlurstueckGeoms.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/foreground.png")));    // NOI18N
        tglShowFlurstueckGeoms.setSelected(true);
        tglShowFlurstueckGeoms.setText(org.openide.util.NbBundle.getMessage(
                FlurstueckePanel.class,
                "FlurstueckePanel.tglShowFlurstueckGeoms.text"));                                   // NOI18N
        tglShowFlurstueckGeoms.setBorderPainted(false);
        tglShowFlurstueckGeoms.setContentAreaFilled(false);
        tglShowFlurstueckGeoms.setRolloverEnabled(false);
        tglShowFlurstueckGeoms.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/foreground_on.png"))); // NOI18N
        tglShowFlurstueckGeoms.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    tglShowFlurstueckGeomsActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        jPanel3.add(tglShowFlurstueckGeoms, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(jPanel5, gridBagConstraints);

        cmdRemoveFlurstueckGeom.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/removePoly.png"))); // NOI18N
        cmdRemoveFlurstueckGeom.setText(org.openide.util.NbBundle.getMessage(
                FlurstueckePanel.class,
                "FlurstueckePanel.cmdRemoveFlurstueckGeom.text"));                               // NOI18N
        cmdRemoveFlurstueckGeom.setToolTipText(org.openide.util.NbBundle.getMessage(
                FlurstueckePanel.class,
                "FlurstueckePanel.cmdRemoveFlurstueckGeom.toolTipText"));                        // NOI18N
        cmdRemoveFlurstueckGeom.setBorderPainted(false);
        cmdRemoveFlurstueckGeom.setContentAreaFilled(false);
        cmdRemoveFlurstueckGeom.setEnabled(false);
        cmdRemoveFlurstueckGeom.setFocusPainted(false);
        cmdRemoveFlurstueckGeom.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdRemoveFlurstueckGeomActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        jPanel3.add(cmdRemoveFlurstueckGeom, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(13, 3, 2, 3);
        jPanel1.add(jPanel3, gridBagConstraints);

        add(jPanel1, java.awt.BorderLayout.CENTER);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstFlurstueckGeomsValueChanged(final javax.swing.event.ListSelectionEvent evt) { //GEN-FIRST:event_lstFlurstueckGeomsValueChanged
        if (evt.getValueIsAdjusting()) {
            return;
        }
        final boolean enabled = CidsAppBackend.getInstance().isEditable()
                    && (lstFlurstueckGeoms.getSelectedIndices().length > 0);
        cmdRemoveFlurstueckGeom.setEnabled(enabled);

        final FeatureCollection featureCollection = CidsAppBackend.getInstance().getMainMap().getFeatureCollection();

        try {
            final Collection<Feature> alkisLandparcelFeaturesToRemove = new ArrayList<Feature>();
            for (final Feature feature : featureCollection.getAllFeatures()) {
                if (feature instanceof CidsFeature) {
                    final CidsFeature cidsFeature = (CidsFeature)feature;
                    if (cidsFeature.getMetaClass().getTableName().equalsIgnoreCase(
                                    "alkis_landparcel")) {
                        alkisLandparcelFeaturesToRemove.add(cidsFeature);
                    }
                }
            }
            featureCollection.removeFeatureCollectionListener(this);
            featureCollection.removeFeatures(alkisLandparcelFeaturesToRemove);
            featureCollection.addFeatureCollectionListener(this);

            ALKIS_LANDPARCEL_TO_LANDPARCEL_GEOM_MAP.clear();

            final DefaultListModel alkisLandparcelListModel = (DefaultListModel)lstAlkisLandparcels.getModel();
            alkisLandparcelListModel.clear();
            final int[] selectedIndices = lstFlurstueckGeoms.getSelectedIndices();
            if (selectedIndices.length > 0) {
                alkisLandparcelListModel.addElement("Flurstücke werden geladen...");

                final AlkisLandparcelSearch serverSearch = new AlkisLandparcelSearch();
                final String crs = serverSearch.getCrs();

                final Collection<Feature> featuresToSelect = new ArrayList<Feature>();
                Geometry unionGeom = null;
                for (int index = 0; index < selectedIndices.length; ++index) {
                    final int selectedIndex = selectedIndices[index];
                    final Object listObject = lstFlurstueckGeoms.getModel().getElementAt(selectedIndex);
                    if (listObject instanceof CidsBean) {
                        final CidsBean flurstueckGeomBean = (CidsBean)listObject;
                        final CidsFeature flurstueckGeomFeature = new CidsFeature(
                                flurstueckGeomBean.getMetaObject());

                        featuresToSelect.add(flurstueckGeomFeature);
                        flurstueckGeomFeature.setEditable(CidsAppBackend.getInstance().isEditable());

                        final Geometry geom = (Geometry)((CidsBean)flurstueckGeomBean.getProperty("geom")).getProperty(
                                "geo_field");
                        final Geometry transformedGeom = CrsTransformer.transformToGivenCrs((Geometry)geom.clone(),
                                    crs).buffer(LANDPARCEL_GEOM_BUFFER);
                        transformedGeom.setSRID(CrsTransformer.extractSridFromCrs(crs));
                        if (unionGeom == null) {
                            unionGeom = transformedGeom;
                        } else {
                            final int srid = unionGeom.getSRID();
                            unionGeom = unionGeom.union(transformedGeom);
                            unionGeom.setSRID(srid);
                        }
                    }
                }

                featureCollection.removeFeatureCollectionListener(this);
                featureCollection.select(featuresToSelect);
                featureCollection.addFeatureCollectionListener(this);

                final Geometry searchGeom = (Geometry)unionGeom.clone();

                new SwingWorker<MetaObject[], Void>() {

                        @Override
                        protected MetaObject[] doInBackground() throws Exception {
                            serverSearch.setGeometry(searchGeom);
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

                            if (mos != null) {
                                for (final MetaObject mo : mos) {
                                    final CidsBean alkisLandparcelBean = mo.getBean();

                                    final List<CidsBean> assignedFlurstueckGeomBeans;
                                    if (ALKIS_LANDPARCEL_TO_LANDPARCEL_GEOM_MAP.get(alkisLandparcelBean)
                                                == null) {
                                        assignedFlurstueckGeomBeans = new ArrayList<CidsBean>();
                                        ALKIS_LANDPARCEL_TO_LANDPARCEL_GEOM_MAP.put(
                                            alkisLandparcelBean,
                                            assignedFlurstueckGeomBeans);
                                    } else {
                                        assignedFlurstueckGeomBeans = ALKIS_LANDPARCEL_TO_LANDPARCEL_GEOM_MAP.get(
                                                alkisLandparcelBean);
                                    }

                                    final List<CidsBean> flurstueckGeomBeans = getCidsBean().getBeanCollectionProperty(
                                            "flurstuecke");
                                    for (final CidsBean flurstueckGeomBean : flurstueckGeomBeans) {
                                        final Geometry flurstueckGeom = (Geometry)flurstueckGeomBean.getProperty(
                                                "geom.geo_field");

                                        final Geometry alkisLandparcelGeom = (Geometry)alkisLandparcelBean.getProperty(
                                                "geometrie.geo_field");

                                        final String crs = CrsTransformer.createCrsFromSrid(CrsTransformer
                                                        .getCurrentSrid());
                                        final Geometry transformedAlkisLandparcelGeom = CrsTransformer
                                                    .transformToGivenCrs((Geometry)alkisLandparcelGeom.clone(),
                                                        crs);
                                        transformedAlkisLandparcelGeom.setSRID(CrsTransformer.getCurrentSrid());

                                        if (transformedAlkisLandparcelGeom.buffer(LANDPARCEL_GEOM_BUFFER).intersects(
                                                        flurstueckGeom)) {
                                            assignedFlurstueckGeomBeans.add(flurstueckGeomBean);
                                        }
                                    }
                                }
                            }
                            return mos;
                        }

                        @Override
                        protected void done() {
                            try {
                                final MetaObject[] mos = get();

                                final DefaultListModel alkisLandparcelListModel = (DefaultListModel)
                                    lstAlkisLandparcels.getModel();
                                alkisLandparcelListModel.clear();

                                if (mos != null) {
                                    for (final MetaObject mo : mos) {
                                        final CidsBean alkisLandparcelBean = mo.getBean();
                                        final CidsFeature alkisLandparcelFeature = new CidsFeature(mo);
                                        featureCollection.removeFeatureCollectionListener(FlurstueckePanel.this);
                                        alkisLandparcelListModel.addElement(alkisLandparcelBean);
                                        featureCollection.addFeature(alkisLandparcelFeature);
                                        featureCollection.addFeatureCollectionListener(FlurstueckePanel.this);
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
                    }.execute();
            }
        } catch (Exception ex) {
            LOG.error(ex, ex);
        }
    } //GEN-LAST:event_lstFlurstueckGeomsValueChanged

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstAlkisLandparcelsValueChanged(final javax.swing.event.ListSelectionEvent evt) { //GEN-FIRST:event_lstAlkisLandparcelsValueChanged
        final boolean enabled = lstAlkisLandparcels.getSelectedIndices().length > 0;
        cmdShowAlkisRenderer.setEnabled(enabled);

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
                Main.getCurrentInstance().showRenderer(alkisLandparcelBean.getMetaObject());
            }
        }
    }                                                                                   //GEN-LAST:event_lstAlkisLandparcelsMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdShowAlkisRendererActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdShowAlkisRendererActionPerformed
        final int[] selectedIndices = lstAlkisLandparcels.getSelectedIndices();
        final List<MetaObject> coll = new ArrayList<MetaObject>();
        for (int index = 0; index < selectedIndices.length; ++index) {
            final int selectedIndex = selectedIndices[index];
            final Object listObject = lstAlkisLandparcels.getModel().getElementAt(selectedIndex);
            if (listObject instanceof CidsBean) {
                final CidsBean flurstueckGeomBean = (CidsBean)listObject;
                coll.add(flurstueckGeomBean.getMetaObject());
            }
        }
        Main.getCurrentInstance().showRenderer(coll.toArray(new MetaObject[0]));
    }                                                                                        //GEN-LAST:event_cmdShowAlkisRendererActionPerformed

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
    private void lstFlurstueckGeomsMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lstFlurstueckGeomsMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON3) {
            lstFlurstueckGeoms.clearSelection();
        }
    }                                                                                  //GEN-LAST:event_lstFlurstueckGeomsMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void tglShowFlurstueckGeomsActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_tglShowFlurstueckGeomsActionPerformed
        final Collection<Feature> featuresToShow = new ArrayList<Feature>();
        if (tglShowFlurstueckGeoms.isSelected()) {
            for (final CidsBean flurstueckGeomBean : getCidsBean().getBeanCollectionProperty("flurstuecke")) {
                final CidsFeature flurstueckGeomFeature = new CidsFeature(flurstueckGeomBean.getMetaObject());
                featuresToShow.add(flurstueckGeomFeature);
            }
        }
        showThisFeatures(featuresToShow, "flurstuecke");
    }                                                                                          //GEN-LAST:event_tglShowFlurstueckGeomsActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdRemoveFlurstueckGeomActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdRemoveFlurstueckGeomActionPerformed
        final int[] selectedIndices = lstFlurstueckGeoms.getSelectedIndices();
        lstFlurstueckGeoms.clearSelection();
        final FeatureCollection featureCollection = CidsAppBackend.getInstance().getMainMap().getFeatureCollection();

        final Collection<CidsBean> beansToRemove = new ArrayList<CidsBean>();
        for (int index = 0; index < selectedIndices.length; ++index) {
            final int selectedIndex = selectedIndices[index];
            final Object listObject = lstFlurstueckGeoms.getModel().getElementAt(selectedIndex);
            if (listObject instanceof CidsBean) {
                final CidsBean flurstueckGeomBean = (CidsBean)listObject;
                beansToRemove.add(flurstueckGeomBean);
            }
        }

        for (final CidsBean beanToRemove : beansToRemove) {
            final CidsFeature flurstueckGeomFeature = new CidsFeature(beanToRemove.getMetaObject());
            getCidsBean().getBeanCollectionProperty("flurstuecke").remove(beanToRemove);
            featureCollection.removeFeature(flurstueckGeomFeature);
        }
    } //GEN-LAST:event_cmdRemoveFlurstueckGeomActionPerformed

    @Override
    public CidsBean getCidsBean() {
        return kassenzeichenBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        bindingGroup.unbind();
        kassenzeichenBean = cidsBean;
        bindingGroup.bind();
    }

    @Override
    public void editModeChanged() {
        final boolean isEditable = CidsAppBackend.getInstance().isEditable();
        setEnabled(isEditable);
        cmdRemoveFlurstueckGeom.setEnabled(isEditable && (lstFlurstueckGeoms.getSelectedIndices().length > 0));
    }

    @Override
    public void setEnabled(final boolean bln) {
        super.setEnabled(bln);
    }

    @Override
    public void featuresAdded(final FeatureCollectionEvent fce) {
        final Collection<Feature> addedFeatures = fce.getEventFeatures();
        for (final Feature addedFeature : addedFeatures) {
            if (addedFeature instanceof CidsFeature) {
                final CidsFeature addedCidsFeature = (CidsFeature)addedFeature;
                final String addedMCName = addedCidsFeature.getMetaClass().getTableName();
                if (addedMCName.equalsIgnoreCase("flurstuecke")) {
                    final boolean show = tglShowFlurstueckGeoms.isSelected();
                    CidsAppBackend.getInstance().getMainMap().getPFeatureHM().get(addedCidsFeature).setVisible(show);
                }
            }
        }
    }

    @Override
    public void allFeaturesRemoved(final FeatureCollectionEvent fce) {
    }

    @Override
    public void featuresRemoved(final FeatureCollectionEvent fce) {
        if (getCidsBean() != null) {
            final Collection<CidsBean> flurstueckGeomBeans = getCidsBean().getBeanCollectionProperty("flurstuecke");
            final Collection<Feature> removedFeatures = fce.getEventFeatures();
            for (final Feature feature : removedFeatures) {
                if (feature instanceof CidsFeature) {
                    final CidsFeature cidsFeature = (CidsFeature)feature;
                    final CidsBean toRemoveBean = cidsFeature.getMetaObject().getBean();
                    flurstueckGeomBeans.remove(toRemoveBean);
                }
            }
        }
    }

    @Override
    public void featuresChanged(final FeatureCollectionEvent fce) {
        final Collection<Feature> changedFeatures = fce.getEventFeatures();
        if (changedFeatures != null) {
            for (final Feature changedFeature : changedFeatures) {
                if (changedFeature instanceof CidsFeature) {
                    final CidsFeature changedCidsFeature = (CidsFeature)changedFeature;
                    if (changedCidsFeature.getMetaObject().getMetaClass().getTableName().equalsIgnoreCase(
                                    "flurstuecke")) {
                        try {
                            final CidsBean flurstueckGeomBean = changedCidsFeature.getMetaObject().getBean();
                            flurstueckGeomBean.setProperty("istfrei", false);
                            flurstueckGeomBean.setProperty("text", "freie Geometrie");
                        } catch (Exception ex) {
                            LOG.error("error while modifying landparcelgeom", ex);
                        }
                    }
                }
            }
        }
        lstFlurstueckGeoms.repaint();
    }

    @Override
    public void featureSelectionChanged(final FeatureCollectionEvent fce) {
        if (fce.getEventFeatures() == null) {
            return;
        }
        final FeatureCollection featureCollection = CidsAppBackend.getInstance().getMainMap().getFeatureCollection();

        final Collection<Feature> selectedFeatures = featureCollection.getSelectedFeatures();
        final List<CidsBean> flurstueckGeomBeans = getCidsBean().getBeanCollectionProperty("flurstuecke");

        final List<Integer> indicesToSelect = new ArrayList<Integer>();
        if (selectedFeatures != null) {
            for (final Feature selectedFeature : selectedFeatures) {
                if (selectedFeature instanceof CidsFeature) {
                    final CidsFeature selectedCidsFeature = (CidsFeature)selectedFeature;
                    final int indexToSelect = flurstueckGeomBeans.indexOf(selectedCidsFeature.getMetaObject()
                                    .getBean());
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

        lstFlurstueckGeoms.setSelectedIndices(indicesArr);
    }

    @Override
    public void featureReconsiderationRequested(final FeatureCollectionEvent fce) {
    }

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
                    final Geometry geom = pFeatureToAttach.getFeature().getGeometry();

                    final CidsBean geomBean = CidsBean.createNewCidsBeanFromTableName(
                            VerdisConstants.DOMAIN,
                            "geom");
                    geomBean.setProperty("geo_field", geom);

                    final CidsBean flurstueckGeomBean = CidsBean.createNewCidsBeanFromTableName(
                            VerdisConstants.DOMAIN,
                            "flurstuecke");
                    flurstueckGeomBean.setProperty("istfrei", true);
                    flurstueckGeomBean.setProperty("geom", geomBean);
                    flurstueckGeomBean.setProperty("text", "freie Geometrie");

                    final CidsFeature flurstueckGeomFeature = new CidsFeature(
                            flurstueckGeomBean.getMetaObject());
                    flurstueckGeomFeature.getMetaObject().setID(getNewFlurstueckGeomId());
                    flurstueckGeomFeature.setEditable(CidsAppBackend.getInstance().isEditable());
                    final FeatureCollection featureCollection = CidsAppBackend.getInstance()
                                .getMainMap()
                                .getFeatureCollection();
                    featureCollection.removeFeature(pFeatureToAttach.getFeature());
                    featureCollection.addFeature(flurstueckGeomFeature);
                    featureCollection.select(flurstueckGeomFeature);

                    getCidsBean().getBeanCollectionProperty("flurstuecke").add(flurstueckGeomBean);
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
    public static int getNewFlurstueckGeomId() {
        return NEW_FLURSTUECK_GEOM_ID--;
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
                        bean.getProperty("bezeichnung"),
                        index,
                        isSelected,
                        cellHasFocus);
                final List<CidsBean> flurstueckGeomBeans = ALKIS_LANDPARCEL_TO_LANDPARCEL_GEOM_MAP.get(bean);
                final Color color;
                if ((flurstueckGeomBeans == null) || flurstueckGeomBeans.isEmpty()) {
                    color = null;
                } else if (flurstueckGeomBeans.size() > 1) {
                    color = Color.GRAY;
                } else {
                    int colorIndex = flurstueckGeomBeans.get(0).getMetaObject().getId();
                    if (colorIndex < 0) {
                        colorIndex = -colorIndex;
                    }
                    colorIndex %= FlurstueckePanel.LANDPARCEL_COLORS.size();

                    color = LANDPARCEL_COLORS.get(colorIndex);
                }
                setBackground(color);
                setBorder(BorderFactory.createEmptyBorder(1, (2 * SPACING) + MARKER_WIDTH, 1, 0));
                return comp;
            } else {
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        }

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
    private static final class FlurstueckGeomCellRenderer extends DefaultListCellRenderer {

        //~ Static fields/initializers -----------------------------------------

        private static final int SPACING = 5;
        private static final int MARKER_WIDTH = 4;

        //~ Instance fields ----------------------------------------------------

        private boolean selected = false;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new FancyListCellRenderer object.
         */
        public FlurstueckGeomCellRenderer() {
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
                    bean.getProperty("text"),
                    index,
                    isSelected,
                    cellHasFocus);
            selected = isSelected;
            int colorIndex = bean.getMetaObject().getId();
            if (colorIndex < 0) {
                colorIndex = -colorIndex;
            }
            colorIndex %= FlurstueckePanel.LANDPARCEL_COLORS.size();

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
}
