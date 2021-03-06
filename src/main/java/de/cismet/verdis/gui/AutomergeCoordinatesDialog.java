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
package de.cismet.verdis.gui;

import lombok.Getter;

import java.awt.Component;
import java.awt.event.KeyEvent;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.interaction.CismapBroker;
import de.cismet.cismap.commons.tools.PFeatureTools;

import de.cismet.cismap.navigatorplugin.CidsFeature;

import de.cismet.tools.gui.StaticSwingTools;

import de.cismet.verdis.CidsAppBackend;

import de.cismet.verdis.commons.constants.VerdisConstants;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class AutomergeCoordinatesDialog extends javax.swing.JDialog {

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum Status {

        //~ Enum constants -----------------------------------------------------

        CANCEL, MERGE, IGNORE
    }

    //~ Instance fields --------------------------------------------------------

    @Getter private Status status = Status.CANCEL;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JList<CidsFeature> jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form AutomergeCoordinatesDialog.
     *
     * @param  parent  DOCUMENT ME!
     * @param  modal   DOCUMENT ME!
     */
    private AutomergeCoordinatesDialog(final java.awt.Frame parent, final boolean modal) {
        super(parent, modal);
        initComponents();
        getRootPane().setDefaultButton(jButton1);
        StaticSwingTools.doClickButtonOnKeyStroke(
            jButton1,
            KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
            getRootPane());
        StaticSwingTools.doClickButtonOnKeyStroke(
            jButton3,
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            getRootPane());
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   infos  numberOfCoordinates DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Status display(final Collection<PFeatureTools.PFeatureCoordinateInformation> infos) {
        int numberOfCoordinates = infos.size();
        ((DefaultListModel<CidsFeature>)jList1.getModel()).clear();
        final Set<Feature> features = new HashSet<>();
        for (final PFeatureTools.PFeatureCoordinateInformation info : infos) {
            numberOfCoordinates += info.getNeighbourInfos().size();
            features.add(info.getPFeature().getFeature());
            for (final PFeatureTools.PFeatureCoordinateInformation neighbourInfo : info.getNeighbourInfos()) {
                features.add(neighbourInfo.getPFeature().getFeature());
            }
        }
        for (final Feature feature : features) {
            if (feature instanceof CidsFeature) {
                ((DefaultListModel<CidsFeature>)jList1.getModel()).addElement((CidsFeature)feature);
            }
        }

        final String message = org.openide.util.NbBundle.getMessage(
                AutomergeCoordinatesDialog.class,
                "AutomergeCoordinatesDialog.jLabel1.text");
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            String.format(message, numberOfCoordinates, getCoordinateDuplicateThreshold())); // NOI18N

        StaticSwingTools.showDialog(getInstance());
        return getStatus();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static double getCoordinateDuplicateThreshold() {
        return CidsAppBackend.getInstance().getAppPreferences().getCoordinateDuplicateThreshold();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(
                AutomergeCoordinatesDialog.class,
                "AutomergeCoordinatesDialog.title")); // NOI18N
        setMaximumSize(new java.awt.Dimension(350, 350));
        setMinimumSize(new java.awt.Dimension(350, 350));
        setPreferredSize(new java.awt.Dimension(350, 350));
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel3.setLayout(new java.awt.GridBagLayout());

        jList1.setModel(new DefaultListModel<CidsFeature>());
        jList1.setCellRenderer(new ObjectListCellRenderer());
        jList1.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    jList1MouseClicked(evt);
                }
            });
        jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {

                @Override
                public void valueChanged(final javax.swing.event.ListSelectionEvent evt) {
                    jList1ValueChanged(evt);
                }
            });
        jScrollPane1.setViewportView(jList1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanel3.add(jScrollPane1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(
                AutomergeCoordinatesDialog.class,
                "AutomergeCoordinatesDialog.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel3.add(jLabel1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel2,
            org.openide.util.NbBundle.getMessage(
                AutomergeCoordinatesDialog.class,
                "AutomergeCoordinatesDialog.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanel3.add(jLabel2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel3,
            org.openide.util.NbBundle.getMessage(
                AutomergeCoordinatesDialog.class,
                "AutomergeCoordinatesDialog.jLabel3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanel3.add(jLabel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jPanel3, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton1,
            org.openide.util.NbBundle.getMessage(
                AutomergeCoordinatesDialog.class,
                "AutomergeCoordinatesDialog.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });
        jPanel2.add(jButton1);

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton2,
            org.openide.util.NbBundle.getMessage(
                AutomergeCoordinatesDialog.class,
                "AutomergeCoordinatesDialog.jButton2.text")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton2ActionPerformed(evt);
                }
            });
        jPanel2.add(jButton2);

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton3,
            org.openide.util.NbBundle.getMessage(
                AutomergeCoordinatesDialog.class,
                "AutomergeCoordinatesDialog.jButton3.text")); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton3ActionPerformed(evt);
                }
            });
        jPanel2.add(jButton3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_END;
        jPanel1.add(jPanel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jPanel1, gridBagConstraints);

        pack();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton1ActionPerformed
        status = Status.MERGE;
        setVisible(false);
    }                                                                            //GEN-LAST:event_jButton1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton2ActionPerformed
        status = Status.IGNORE;
        setVisible(false);
    }                                                                            //GEN-LAST:event_jButton2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jList1ValueChanged(final javax.swing.event.ListSelectionEvent evt) { //GEN-FIRST:event_jList1ValueChanged
        final Feature feature = jList1.getSelectedValue();
        if (feature != null) {
            SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        CismapBroker.getInstance().getMappingComponent().getFeatureCollection().select(feature);
                    }
                });
        }
    } //GEN-LAST:event_jList1ValueChanged

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jList1MouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_jList1MouseClicked
        if (evt.getClickCount() == 2) {
            final Feature feature = jList1.getSelectedValue();
            if (feature != null) {
//                CismapBroker.getInstance().getMappingComponent().zoom
            }
        }
    }                                                                      //GEN-LAST:event_jList1MouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton3ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton3ActionPerformed
        status = Status.CANCEL;
        setVisible(false);
    }                                                                            //GEN-LAST:event_jButton3ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static AutomergeCoordinatesDialog getInstance() {
        return LazyInitialiser.INSTANCE;
    }

    @Override
    public void setVisible(final boolean visible) {
        if (visible) {
            status = Status.CANCEL;
        }
        super.setVisible(visible);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class LazyInitialiser {

        //~ Static fields/initializers -----------------------------------------

        private static final AutomergeCoordinatesDialog INSTANCE = new AutomergeCoordinatesDialog(Main.getInstance(),
                true);

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LazyInitialiser object.
         */
        private LazyInitialiser() {
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class ObjectListCellRenderer extends DefaultListCellRenderer {

        //~ Methods ------------------------------------------------------------

        @Override
        public Component getListCellRendererComponent(final JList<?> list,
                final Object value,
                final int index,
                final boolean isSelected,
                final boolean cellHasFocus) {
            final CidsFeature feature = (CidsFeature)value;
            final CidsBean bean = feature.getMetaObject().getBean();
            final JLabel component = (JLabel)super.getListCellRendererComponent(
                    list,
                    value,
                    index,
                    isSelected,
                    cellHasFocus);
            final String tableName = feature.getMetaClass().getTableName();
            if (VerdisConstants.MC.FLAECHE.equalsIgnoreCase(tableName)) {
                component.setText("Fläche: " + bean.getProperty(VerdisConstants.PROP.FLAECHE.FLAECHENBEZEICHNUNG));
            } else if (VerdisConstants.MC.FRONT.equalsIgnoreCase(tableName)) {
                component.setText("Front: " + bean.getProperty(VerdisConstants.PROP.FRONT.NUMMER));
            } else if (VerdisConstants.MC.KASSENZEICHEN_GEOMETRIE.equalsIgnoreCase(tableName)) {
                component.setText("Allgemeine Geometrie: "
                            + bean.getProperty(VerdisConstants.PROP.KASSENZEICHEN_GEOMETRIE.NAME));
            }
            return component;
        }
    }
}
