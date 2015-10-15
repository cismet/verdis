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
 * RegenFlaechenTabellenPanel.java
 *
 * Created on 03.12.2010, 21:50:28
 */
package de.cismet.verdis.gui;

import Sirius.server.middleware.types.MetaObject;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

import edu.umd.cs.piccolox.event.PNotification;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.*;

import java.awt.Color;
import java.awt.Component;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SortOrder;

import de.cismet.cids.custom.util.VerdisUtils;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;

import de.cismet.cismap.commons.features.PureNewFeature;
import de.cismet.cismap.commons.features.SplittedNewFeature;
import de.cismet.cismap.commons.gui.piccolo.PFeature;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.AttachFeatureListener;

import de.cismet.cismap.navigatorplugin.CidsFeature;

import de.cismet.tools.NumberStringComparator;

import de.cismet.tools.gui.StaticSwingTools;

import de.cismet.validation.Validator;

import de.cismet.validation.validator.AggregatedValidator;

import de.cismet.verdis.CidsAppBackend;
import de.cismet.verdis.CrossReference;

import de.cismet.verdis.commons.constants.*;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class RegenFlaechenTabellenPanel extends AbstractCidsBeanTable implements CidsBeanStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            RegenFlaechenTabellenPanel.class);

    private static final String[] PREVIEW_COLUMN_NAMES = {
            "Bezeichnung",
            "Grafik (alt)",
            "Grafik (neu)",
            "Korrektur (alt)",
            "Korrektur (neu)"
        };

    private static final Class[] PREVIEW_COLUMN_CLASSES = {
            String.class,
            Integer.class,
            Integer.class,
            Integer.class,
            Integer.class
        };

    //~ Instance fields --------------------------------------------------------

    private CidsBean cidsBean;

    private Float lastSplitAnteil = null;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private org.jdesktop.swingx.JXTable jxtOverview;
    private org.jdesktop.swingx.JXTable jxtOverview1;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form RegenFlaechenTabellenPanel.
     */
    public RegenFlaechenTabellenPanel() {
        super(CidsAppBackend.Mode.REGEN, new RegenFlaechenTableModel());

        initComponents();
        jxtOverview.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        jxtOverview.setModel(getModel());
        final HighlightPredicate errorPredicate = new HighlightPredicate() {

                @Override
                public boolean isHighlighted(final Component renderer, final ComponentAdapter componentAdapter) {
                    final int displayedIndex = componentAdapter.row;
                    final int modelIndex = jxtOverview.convertRowIndexToModel(displayedIndex);
                    final CidsBean cidsBean = getModel().getCidsBeanByIndex(modelIndex);
                    return getItemValidator(cidsBean).getState().isError();
                }
            };

        final Highlighter errorHighlighter = new ColorHighlighter(
                errorPredicate,
                Color.RED.brighter().brighter().brighter(),
                Color.WHITE);

        final HighlightPredicate warningPredicate = new HighlightPredicate() {

                @Override
                public boolean isHighlighted(final Component renderer, final ComponentAdapter componentAdapter) {
                    final int displayedRowIndex = componentAdapter.row;
                    final int modelRowIndex = jxtOverview.convertRowIndexToModel(displayedRowIndex);
                    final int displayedColumnIndex = componentAdapter.column;
                    final int modelColumnIndex = jxtOverview.convertColumnIndexToModel(displayedColumnIndex);

                    final CidsBean flaecheBean = getModel().getCidsBeanByIndex(modelRowIndex);
                    final Validator validator;

                    if (modelColumnIndex == 1) {
                        validator = RegenFlaechenDetailsPanel.getValidatorFlaechenBezeichnung(flaecheBean);
                    } else if (modelColumnIndex == 3) {
                        final AggregatedValidator aggVal = new AggregatedValidator();
                        aggVal.add(RegenFlaechenDetailsPanel.getValidatorGroesseGrafik(flaecheBean));
                        aggVal.add(RegenFlaechenDetailsPanel.getValidatorGroesseKorrektur(flaecheBean));
                        aggVal.validate();
                        validator = aggVal;
                    } else if (modelColumnIndex == 6) {
                        final AggregatedValidator aggVal = new AggregatedValidator();
                        aggVal.add(RegenFlaechenDetailsPanel.getValidatorDatumErfassung(flaecheBean));
                        aggVal.add(RegenFlaechenDetailsPanel.getValidatorDatumVeranlagung(flaecheBean));
                        aggVal.validate();
                        validator = aggVal;
                    } else {
                        final AggregatedValidator aggVal = new AggregatedValidator();
                        aggVal.add(RegenFlaechenDetailsPanel.getValidatorAnteil(flaecheBean));
                        aggVal.add(RegenFlaechenDetailsPanel.getValidatorFebId(flaecheBean));
                        aggVal.validate();
                        validator = aggVal;
                    }

                    return validator.getState().isWarning();
                }
            };

        final Highlighter warningHighlighter = new ColorHighlighter(
                warningPredicate,
                Color.ORANGE,
                Color.BLACK);

        final HighlightPredicate changedPredicate = new HighlightPredicate() {

                @Override
                public boolean isHighlighted(final Component renderer, final ComponentAdapter componentAdapter) {
                    final int displayedIndex = componentAdapter.row;
                    final int modelIndex = jxtOverview.convertRowIndexToModel(displayedIndex);
                    final CidsBean cidsBean = getModel().getCidsBeanByIndex(modelIndex);
                    if (cidsBean != null) {
                        return CidsAppBackend.getInstance().isEditable()
                                    && (cidsBean.getMetaObject().getStatus() == MetaObject.MODIFIED);
                    } else {
                        return false;
                    }
                }
            };

        final Highlighter changedHighlighter = new ColorHighlighter(changedPredicate, null, Color.RED);

        final HighlightPredicate noGeometryPredicate = new HighlightPredicate() {

                @Override
                public boolean isHighlighted(final Component renderer, final ComponentAdapter componentAdapter) {
                    final int displayedIndex = componentAdapter.row;
                    final int modelIndex = jxtOverview.convertRowIndexToModel(displayedIndex);
                    final CidsBean cidsBean = getModel().getCidsBeanByIndex(modelIndex);
                    return ((cidsBean.getProperty(
                                    FlaechePropertyConstants.PROP__FLAECHENINFO
                                            + "."
                                            + FlaecheninfoPropertyConstants.PROP__FLAECHENART) != null)
                                    && (VerdisUtils.PROPVAL_ART_VORLAEUFIGEVERANLASSUNG
                                        != (Integer)cidsBean.getProperty(
                                            FlaechePropertyConstants.PROP__FLAECHENINFO
                                            + "."
                                            + FlaecheninfoPropertyConstants.PROP__FLAECHENART
                                            + "."
                                            + FlaechenartPropertyConstants.PROP__ID)))
                                && (getGeometry(cidsBean) == null);
                }
            };

        final Highlighter noGeometryHighlighter = new ColorHighlighter(noGeometryPredicate, Color.lightGray, null);

        jxtOverview.setHighlighters(changedHighlighter, warningHighlighter, noGeometryHighlighter, errorHighlighter);

        jxtOverview.getColumnModel().getColumn(0).setCellRenderer(jxtOverview.getDefaultRenderer(Icon.class));
        jxtOverview.getColumnModel().getColumn(2).setCellRenderer(jxtOverview.getDefaultRenderer(Icon.class));
        jxtOverview.getColumnModel().getColumn(3).setCellRenderer(jxtOverview.getDefaultRenderer(Number.class));

        jxtOverview.getColumnExt(1).setComparator(new NumberStringComparator());

        jxtOverview.getColumnModel().getColumn(0).setPreferredWidth(24);
        jxtOverview.getColumnModel().getColumn(1).setPreferredWidth(80);
        jxtOverview.getColumnModel().getColumn(2).setPreferredWidth(24);
        jxtOverview.getColumnModel().getColumn(3).setPreferredWidth(70);
        jxtOverview.getColumnModel().getColumn(4).setPreferredWidth(70);
        jxtOverview.getColumnModel().getColumn(5).setPreferredWidth(80);

        jxtOverview.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jxtOverview.setDragEnabled(false);

        jxtOverview.getTableHeader().setResizingAllowed(false);
        jxtOverview.getTableHeader().setReorderingAllowed(false);
        jxtOverview.setSortOrder(1, SortOrder.ASCENDING);

        final HighlightPredicate equalsPredicate = new HighlightPredicate() {

                @Override
                public boolean isHighlighted(final Component renderer, final ComponentAdapter componentAdapter) {
                    if ((componentAdapter.column != 2) && (componentAdapter.column != 4)) {
                        return false;
                    }

                    final int displayedIndex = componentAdapter.row;
                    final int oldGrafik = (Integer)jxtOverview1.getValueAt(displayedIndex, 1);
                    final int newGrafik = (Integer)jxtOverview1.getValueAt(displayedIndex, 2);
                    final int oldKorrektur = (Integer)jxtOverview1.getValueAt(displayedIndex, 3);
                    final int newKorrektur = (Integer)jxtOverview1.getValueAt(displayedIndex, 4);

                    return ((componentAdapter.column == 2) && (oldGrafik != newGrafik))
                                || ((componentAdapter.column == 4) && (oldKorrektur != newKorrektur));
                }
            };

        final HighlightPredicate differsPredicate = new HighlightPredicate() {

                @Override
                public boolean isHighlighted(final Component renderer, final ComponentAdapter componentAdapter) {
                    if ((componentAdapter.column != 3)) {
                        return false;
                    }

                    final int displayedIndex = componentAdapter.row;
                    final int oldGrafik = (Integer)jxtOverview1.getValueAt(displayedIndex, 1);
                    final int oldKorrektur = (Integer)jxtOverview1.getValueAt(displayedIndex, 3);

                    return oldGrafik != oldKorrektur;
                }
            };

        final Highlighter equalsHighlighter = new ColorHighlighter(
                equalsPredicate,
                Color.YELLOW,
                Color.BLACK);
        final Highlighter differsHighlighter = new ColorHighlighter(
                differsPredicate,
                Color.RED.brighter().brighter().brighter(),
                Color.WHITE);

        jxtOverview1.setHighlighters(equalsHighlighter, differsHighlighter);
        jxtOverview1.getColumnModel().getColumn(0).setPreferredWidth(50);
        jxtOverview1.getColumnModel().getColumn(1).setPreferredWidth(50);
        jxtOverview1.getColumnModel().getColumn(2).setPreferredWidth(50);
        jxtOverview1.getColumnModel().getColumn(3).setPreferredWidth(50);
        jxtOverview1.getColumnModel().getColumn(4).setPreferredWidth(50);

        jxtOverview1.getColumnExt(0).setComparator(new NumberStringComparator());
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

        jDialog1 = new javax.swing.JDialog();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jxtOverview1 = new JXTable(new GrafikPreviewTableModel());
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jxtOverview = getJXTable();

        jDialog1.setTitle(org.openide.util.NbBundle.getMessage(
                RegenFlaechenTabellenPanel.class,
                "RegenFlaechenTabellenPanel.jDialog1.title")); // NOI18N
        jDialog1.setModal(true);
        jDialog1.setResizable(false);
        jDialog1.getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jScrollPane2.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));

        jxtOverview1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jxtOverview1.setRowSelectionAllowed(false);
        jScrollPane2.setViewportView(jxtOverview1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel2.add(jScrollPane2, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridLayout(1, 0, 10, 0));

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton1,
            org.openide.util.NbBundle.getMessage(
                RegenFlaechenTabellenPanel.class,
                "RegenFlaechenTabellenPanel.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });
        jPanel1.add(jButton1);

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton2,
            org.openide.util.NbBundle.getMessage(
                RegenFlaechenTabellenPanel.class,
                "RegenFlaechenTabellenPanel.jButton2.text")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton2ActionPerformed(evt);
                }
            });
        jPanel1.add(jButton2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel2.add(jPanel1, gridBagConstraints);

        jPanel3.setLayout(new java.awt.GridLayout(1, 0, 10, 0));

        jLabel1.setBackground(Color.YELLOW);
        jLabel1.setForeground(Color.BLACK);
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(
                RegenFlaechenTabellenPanel.class,
                "RegenFlaechenTabellenPanel.jLabel1.text")); // NOI18N
        jLabel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel1.setOpaque(true);
        jPanel3.add(jLabel1);

        jLabel3.setBackground(Color.RED.brighter().brighter().brighter());
        jLabel3.setForeground(Color.WHITE);
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel3,
            org.openide.util.NbBundle.getMessage(
                RegenFlaechenTabellenPanel.class,
                "RegenFlaechenTabellenPanel.jLabel3.text")); // NOI18N
        jLabel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel3.setOpaque(true);
        jPanel3.add(jLabel3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel2.add(jPanel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jDialog1.getContentPane().add(jPanel2, gridBagConstraints);

        setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));

        jxtOverview.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jScrollPane1.setViewportView(jxtOverview);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton1ActionPerformed
        jDialog1.setVisible(false);
    }                                                                            //GEN-LAST:event_jButton1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton2ActionPerformed
        for (int displayedIndex = 0; displayedIndex < jxtOverview1.getRowCount(); ++displayedIndex) {
            final int modelIndex = jxtOverview1.convertRowIndexToModel(displayedIndex);

            final CidsBean flaecheBean = ((GrafikPreviewTableModel)jxtOverview1.getModel()).getCidsBeanByIndex(
                    modelIndex);

            final int oldGrafik = (Integer)jxtOverview1.getValueAt(displayedIndex, 1);
            final int newGrafik = (Integer)jxtOverview1.getValueAt(displayedIndex, 2);
            final int oldKorrektur = (Integer)jxtOverview1.getValueAt(displayedIndex, 3);
            final int newKorrektur = (Integer)jxtOverview1.getValueAt(displayedIndex, 4);

            if (oldGrafik != newGrafik) {
                try {
                    flaecheBean.setProperty(FlaechePropertyConstants.PROP__FLAECHENINFO + "."
                                + FlaecheninfoPropertyConstants.PROP__GROESSE_GRAFIK,
                        newGrafik);
                } catch (final Exception ex) {
                    LOG.warn(ex, ex);
                }
            }
            if (oldKorrektur != newKorrektur) {
                try {
                    flaecheBean.setProperty(FlaechePropertyConstants.PROP__FLAECHENINFO + "."
                                + FlaecheninfoPropertyConstants.PROP__GROESSE_KORREKTUR,
                        newKorrektur);
                } catch (final Exception ex) {
                    LOG.warn(ex, ex);
                }
            }
        }
        jDialog1.setVisible(false);
    } //GEN-LAST:event_jButton2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  notification  DOCUMENT ME!
     */
    public void attachFeatureRequested(final PNotification notification) {
        final Object o = notification.getObject();
        if (o instanceof AttachFeatureListener) {
            final AttachFeatureListener afl = (AttachFeatureListener)o;
            final PFeature pf = afl.getFeatureToAttach();
            if ((pf.getFeature() instanceof PureNewFeature) && (pf.getFeature().getGeometry() instanceof Polygon)) {
                final List<CidsBean> selectedBeans = getSelectedBeans();
                final CidsBean selectedBean = (!selectedBeans.isEmpty()) ? selectedBeans.get(0) : null;
                if ((selectedBean != null)
                            && (VerdisUtils.PROPVAL_ART_VORLAEUFIGEVERANLASSUNG
                                != (Integer)selectedBean.getProperty(
                                    FlaechePropertyConstants.PROP__FLAECHENINFO
                                    + "."
                                    + FlaecheninfoPropertyConstants.PROP__FLAECHENART
                                    + "."
                                    + FlaechenartPropertyConstants.PROP__ID))) {
                    Float oldAnteil = null;
                    double oldArea = 0;
                    double ratio = 0;
                    if (pf.getFeature() instanceof SplittedNewFeature) {
                        final SplittedNewFeature splittedFeature = (SplittedNewFeature)pf.getFeature();
                        if (splittedFeature.getSplittedFromPFeature().getFeature() instanceof CidsFeature) {
                            final CidsBean sourceFlaecheBean = ((CidsFeature)splittedFeature.getSplittedFromPFeature()
                                            .getFeature()).getMetaObject().getBean();
                            oldAnteil = (Float)sourceFlaecheBean.getProperty(FlaechePropertyConstants.PROP__ANTEIL);
                            oldArea = (int)splittedFeature.getSplittedFromPFeature().getFeature().getGeometry()
                                        .getArea();

                            final double area = (int)pf.getFeature().getGeometry().getArea();
                            ratio = (oldArea != 0) ? (area / oldArea) : 0;

                            lastSplitAnteil = oldAnteil;
                        }
                    }

                    final boolean hasGeometrie = getGeometry(selectedBean) != null;
                    final boolean isMarkedForDeletion = selectedBean.getMetaObject().getStatus()
                                == MetaObject.TO_DELETE;
                    if (!hasGeometrie) {
                        if (isMarkedForDeletion) {
                            JOptionPane.showMessageDialog(
                                Main.getMappingComponent(),
                                "Dieser Fl\u00E4che kann im Moment keine Geometrie zugewiesen werden. Bitte zuerst speichern.");
                        } else {
                            try {
                                final Geometry geom = pf.getFeature().getGeometry();
                                final int groesse = (int)geom.getArea();
                                Main.getMappingComponent().getFeatureCollection().removeFeature(pf.getFeature());
                                setGeometry(geom, selectedBean);
                                selectedBean.setProperty(
                                    FlaechePropertyConstants.PROP__FLAECHENINFO
                                            + "."
                                            + FlaecheninfoPropertyConstants.PROP__GROESSE_GRAFIK,
                                    groesse);
                                selectedBean.setProperty(
                                    FlaechePropertyConstants.PROP__FLAECHENINFO
                                            + "."
                                            + FlaecheninfoPropertyConstants.PROP__GROESSE_KORREKTUR,
                                    groesse);
                                if (oldAnteil != null) {
                                    selectedBean.setProperty(
                                        FlaechePropertyConstants.PROP__ANTEIL,
                                        (float)(int)(oldAnteil * ratio));
                                }
                                final CidsFeature cidsFeature = createCidsFeature(selectedBean);
                                final boolean editable = CidsAppBackend.getInstance().isEditable();
                                cidsFeature.setEditable(editable);
                                Main.getMappingComponent().getFeatureCollection().removeFeature(cidsFeature);
                                Main.getMappingComponent().getFeatureCollection().addFeature(cidsFeature);
                                Main.getMappingComponent().getFeatureCollection().select(cidsFeature);
                            } catch (Exception ex) {
                                LOG.error("error while attaching feature", ex);
                            }
                        }
                    }
                }
            } else if (pf.getFeature() instanceof CidsFeature) {
                JOptionPane.showMessageDialog(
                    Main.getMappingComponent(),
                    "Es k\u00F6nnen nur nicht bereits zugeordnete Fl\u00E4chen zugeordnet werden.");
            }
        }
    }

    @Override
    public Validator getItemValidator(final CidsBean flaecheBean) {
        final AggregatedValidator aggVal = new AggregatedValidator();
        aggVal.add(RegenFlaechenDetailsPanel.getValidatorFlaechenBezeichnung(flaecheBean));
        aggVal.add(RegenFlaechenDetailsPanel.getValidatorGroesseGrafik(flaecheBean));
        aggVal.add(RegenFlaechenDetailsPanel.getValidatorGroesseKorrektur(flaecheBean));
        aggVal.add(RegenFlaechenDetailsPanel.getValidatorAnteil(flaecheBean));
        aggVal.add(RegenFlaechenDetailsPanel.getValidatorDatumErfassung(flaecheBean));
        aggVal.add(RegenFlaechenDetailsPanel.getValidatorDatumVeranlagung(flaecheBean));
        aggVal.add(RegenFlaechenDetailsPanel.getValidatorFebId(flaecheBean));
        aggVal.validate();
        return aggVal;
    }

    /**
     * DOCUMENT ME!
     */
    public void recalculateAreaOfFlaechen() {
        final List<CidsBean> toCorrectFlaecheBeans = new ArrayList<CidsBean>();

        for (int index = 0; index < jxtOverview.getRowCount(); ++index) {
            final CidsBean flaecheBean = getModel().getCidsBeanByIndex(jxtOverview.convertRowIndexToModel(index));
            toCorrectFlaecheBeans.add(flaecheBean);
        }

        ((GrafikPreviewTableModel)jxtOverview1.getModel()).setCidsBeans(toCorrectFlaecheBeans);
        jDialog1.pack();
        StaticSwingTools.showDialog(jDialog1);

        getModel().fireTableDataChanged();
        repaint();
    }

    /**
     * DOCUMENT ME!
     */
    public void reEnumerateFlaechen() {
        final TableSorter sort = new TableSorter(getModel());
        sort.setSortingStatus(3, TableSorter.DESCENDING);
        int counterInt = 0;
        String counterString = null;
        for (int i = 0; i < getModel().getRowCount(); ++i) {
            final CidsBean flaecheBean = getModel().getCidsBeanByIndex(sort.getSortedIndex(i));
            if (flaecheBean != null) {
                final int art = (Integer)flaecheBean.getProperty(
                        FlaechePropertyConstants.PROP__FLAECHENINFO
                                + "."
                                + FlaecheninfoPropertyConstants.PROP__FLAECHENART
                                + "."
                                + FlaechenartPropertyConstants.PROP__ID);
                switch (art) {
                    case 1:
                    case 2: {
                        counterInt++;
                        try {
                            flaecheBean.setProperty(
                                FlaechePropertyConstants.PROP__FLAECHENBEZEICHNUNG,
                                new Integer(counterInt).toString());
                        } catch (Exception ex) {
                            LOG.error("error while setting flaechenbezeichnung", ex);
                        }
                        break;
                    }
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    default: {
                        counterString = VerdisUtils.nextFlBez(counterString);
                        try {
                            flaecheBean.setProperty(
                                FlaechePropertyConstants.PROP__FLAECHENBEZEICHNUNG,
                                counterString);
                        } catch (Exception ex) {
                            LOG.error("error while setting flaechenbezeichnung", ex);
                        }
                    }
                }
                getModel().fireTableDataChanged();
                repaint();
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flaechenartBean     DOCUMENT ME!
     * @param   otherFlaechenBeans  flaechenBezeichnung DOCUMENT ME!
     * @param   geom                DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static CidsBean createNewFlaecheBean(final CidsBean flaechenartBean,
            final Collection<CidsBean> otherFlaechenBeans,
            final Geometry geom) throws Exception {
        final CidsBean flaecheBean = CidsAppBackend.getInstance()
                    .getVerdisMetaClass(VerdisMetaClassConstants.MC_FLAECHE)
                    .getEmptyInstance()
                    .getBean();
        final CidsBean anschlussgradBean = CidsAppBackend.getInstance()
                    .getVerdisMetaObject(
                            1,
                            CidsAppBackend.getInstance().getVerdisMetaClass(VerdisMetaClassConstants.MC_ANSCHLUSSGRAD)
                                .getId())
                    .getBean();
        final CidsBean flaecheninfoBean = CidsAppBackend.getInstance()
                    .getVerdisMetaClass(VerdisMetaClassConstants.MC_FLAECHENINFO)
                    .getEmptyInstance()
                    .getBean();
        final CidsBean geomBean = CidsAppBackend.getInstance()
                    .getVerdisMetaClass(VerdisMetaClassConstants.MC_GEOM)
                    .getEmptyInstance()
                    .getBean();

        final int newId = getNextNewBeanId();
        final String bezeichnung;
        if (flaechenartBean != null) {
            bezeichnung = VerdisUtils.getValidFlaechenname(flaechenartBean.getMetaObject().getId(), otherFlaechenBeans);
        } else {
            bezeichnung = null;
        }

        flaecheBean.setProperty(FlaechePropertyConstants.PROP__ID, newId);
        flaecheBean.getMetaObject().setID(newId);

        flaecheBean.setProperty(
            FlaechePropertyConstants.PROP__DATUM_AENDERUNG,
            new java.sql.Date(Calendar.getInstance().getTime().getTime()));

        flaecheBean.setProperty(FlaechePropertyConstants.PROP__FLAECHENINFO, flaecheninfoBean);
        flaecheBean.setProperty(FlaechePropertyConstants.PROP__FLAECHENINFO + "."
                    + FlaecheninfoPropertyConstants.PROP__GEOMETRIE,
            geomBean);
        flaecheBean.setProperty(FlaechePropertyConstants.PROP__FLAECHENINFO + "."
                    + FlaecheninfoPropertyConstants.PROP__ANSCHLUSSGRAD,
            anschlussgradBean);
        flaecheBean.setProperty(FlaechePropertyConstants.PROP__FLAECHENINFO + "."
                    + FlaecheninfoPropertyConstants.PROP__FLAECHENART,
            flaechenartBean);
        flaecheBean.setProperty(FlaechePropertyConstants.PROP__FLAECHENBEZEICHNUNG, bezeichnung);
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);
        flaecheBean.setProperty(
            FlaechePropertyConstants.PROP__DATUM_VERANLAGUNG,
            new SimpleDateFormat("yy/MM").format(cal.getTime()));

        if (geom != null) {
            try {
                final int groesse = new Integer((int)(geom.getArea()));
                flaecheBean.setProperty(FlaechePropertyConstants.PROP__FLAECHENINFO + "."
                            + FlaecheninfoPropertyConstants.PROP__GROESSE_GRAFIK,
                    groesse);
                flaecheBean.setProperty(
                    FlaechePropertyConstants.PROP__FLAECHENINFO
                            + "."
                            + FlaecheninfoPropertyConstants.PROP__GROESSE_KORREKTUR,
                    groesse);
                RegenFlaechenDetailsPanel.setGeometry(geom, flaecheBean);
            } catch (Exception ex) {
                LOG.error("error while assigning feature to new flaeche", ex);
            }
        }

        return flaecheBean;
    }

    @Override
    public CidsBean createNewBean() throws Exception {
        final PFeature sole = Main.getMappingComponent().getSolePureNewFeature();

        final Integer lastSplitFlaecheId = CidsAppBackend.getInstance().getLastSplitFlaecheId();
        final Collection<CrossReference> crossreferences = CidsAppBackend.getInstance()
                    .getFlaechenCrossReferencesForFlaecheid(lastSplitFlaecheId);

        final NewFlaecheDialog dialog = new NewFlaecheDialog();
        dialog.setSoleNewExists((sole != null) && (sole.getFeature().getGeometry() instanceof Polygon));
        dialog.setQuerverweiseExists((crossreferences != null) && !crossreferences.isEmpty());

        StaticSwingTools.showDialog(dialog);

        if (dialog.getReturnStatus() == NewFlaecheDialog.RET_OK) {
            final CidsBean flaechenartBean = dialog.getSelectedArt();
            Geometry geom = null;
            boolean flaecheSplitted = false;
            CidsBean anschlussgradBean = null;
            Float oldAnteil = null;
            double oldArea = 0;
            double ratio = 0;
            if ((VerdisUtils.PROPVAL_ART_VORLAEUFIGEVERANLASSUNG
                            != (Integer)flaechenartBean.getProperty(FlaechenartPropertyConstants.PROP__ID))
                        && dialog.isSoleNewChecked()) {
                if (sole != null) {
                    geom = sole.getFeature().getGeometry();
                    // unzugeordnete Geometrie aus Karte entfernen
                    Main.getMappingComponent().getFeatureCollection().removeFeature(sole.getFeature());

                    if (sole.getFeature() instanceof SplittedNewFeature) {
                        flaecheSplitted = true;
                        final SplittedNewFeature splittedFeature = (SplittedNewFeature)sole.getFeature();
                        if (splittedFeature.getSplittedFromPFeature().getFeature() instanceof CidsFeature) {
                            final CidsBean sourceFlaecheBean = ((CidsFeature)splittedFeature.getSplittedFromPFeature()
                                            .getFeature()).getMetaObject().getBean();
                            anschlussgradBean = (CidsBean)sourceFlaecheBean.getProperty(
                                    FlaechePropertyConstants.PROP__FLAECHENINFO
                                            + "."
                                            + FlaecheninfoPropertyConstants.PROP__ANSCHLUSSGRAD);
                            oldAnteil = lastSplitAnteil;
                            oldArea = (int)splittedFeature.getSplittedFromPFeature().getFeature().getGeometry()
                                        .getArea();
                        }
                    }
                    final double area = (int)geom.getArea();
                    ratio = (oldArea != 0) ? (area / oldArea) : 0;
                }
            }

            final CidsBean flaecheBean = createNewFlaecheBean(
                    flaechenartBean,
                    getAllBeans(),
                    geom);
            if (flaecheSplitted) {
                flaecheBean.setProperty(FlaechePropertyConstants.PROP__FLAECHENINFO + "."
                            + FlaecheninfoPropertyConstants.PROP__ANSCHLUSSGRAD,
                    anschlussgradBean);
                if (oldAnteil != null) {
                    flaecheBean.setProperty(
                        FlaechePropertyConstants.PROP__ANTEIL,
                        (float)(int)(oldAnteil * ratio));
                }
            }

            if (dialog.isQuerverweiseChecked()) {
                if (crossreferences != null) {
                    for (final CrossReference crossreference : crossreferences) {
                        final int kassenzeichenNummer = crossreference.getEntityToKassenzeichen();
                        CidsAppBackend.getInstance()
                                .getFlaecheToKassenzeichenQuerverweisMap()
                                .put(flaecheBean, kassenzeichenNummer);
                    }
                }
            }

            return flaecheBean;
        } else {
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   art  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getValidFlaechenname(final int art) {
        return VerdisUtils.getValidFlaechenname(art, getAllBeans());
    }

    @Override
    public void removeBean(final CidsBean cidsBean) {
        if (cidsBean != null) {
            try {
                cidsBean.delete();
            } catch (final Exception ex) {
                LOG.error("error while removing flaechebean", ex);
            }
        }
        super.removeBean(cidsBean);
    }

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        this.cidsBean = cidsBean;

        final String prop = KassenzeichenPropertyConstants.PROP__FLAECHEN;
        if ((cidsBean != null) && (cidsBean.getProperty(prop) instanceof List)) {
            setCidsBeans((List<CidsBean>)cidsBean.getProperty(prop));
        } else {
            setCidsBeans(new ArrayList<CidsBean>());
        }
    }

    @Override
    public void setGeometry(final Geometry geometry, final CidsBean cidsBean) throws Exception {
        RegenFlaechenDetailsPanel.setGeometry(geometry, cidsBean);
    }

    @Override
    public Geometry getGeometry(final CidsBean cidsBean) {
        return RegenFlaechenDetailsPanel.getGeometry(cidsBean);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class GrafikPreviewTableModel extends CidsBeanTableModel {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new GrafikPreviewTableModel object.
         */
        public GrafikPreviewTableModel() {
            super(PREVIEW_COLUMN_NAMES, PREVIEW_COLUMN_CLASSES);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public CidsBean deepcloneBean(final CidsBean cidsBean) throws Exception {
            throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods,
                                                                           // choose Tools | Templates.
        }

        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            final CidsBean cidsBean = getCidsBeanByIndex(rowIndex);
            if (cidsBean == null) {
                return null;
            }

            final Geometry geom = getGeometry(cidsBean);

            final int oldGrafik = (Integer)cidsBean.getProperty(FlaechePropertyConstants.PROP__FLAECHENINFO + "."
                            + FlaecheninfoPropertyConstants.PROP__GROESSE_GRAFIK);
            final int oldKorrektur = (Integer)cidsBean.getProperty(FlaechePropertyConstants.PROP__FLAECHENINFO + "."
                            + FlaecheninfoPropertyConstants.PROP__GROESSE_KORREKTUR);
            final boolean korrekturEqualsGrafik = oldKorrektur == oldGrafik;
            final int newGrafik = (geom != null) ? (int)geom.getArea() : 0;
            final int newKorrektur = (korrekturEqualsGrafik) ? newGrafik : oldKorrektur;

            switch (columnIndex) {
                case 0: {
                    return (String)cidsBean.getProperty(FlaechePropertyConstants.PROP__FLAECHENBEZEICHNUNG);
                }
                case 1: {
                    return oldGrafik;
                }
                case 2: {
                    return newGrafik;
                }
                case 3: {
                    return oldKorrektur;
                }
                case 4: {
                    return newKorrektur;
                }
                default: {
                    return null;
                }
            }
        }
    }
}
