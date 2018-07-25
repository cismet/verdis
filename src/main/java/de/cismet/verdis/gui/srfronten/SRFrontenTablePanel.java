/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis.gui.srfronten;

import com.vividsolutions.jts.geom.Geometry;

import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;

import java.util.ArrayList;
import java.util.List;

import javax.swing.KeyStroke;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.tools.NumberStringComparator;

import de.cismet.tools.gui.StaticSwingTools;

import de.cismet.verdis.commons.constants.FrontPropertyConstants;
import de.cismet.verdis.commons.constants.FrontinfoPropertyConstants;

import de.cismet.verdis.gui.AbstractCidsBeanTable;
import de.cismet.verdis.gui.AbstractCidsBeanTableModel;
import de.cismet.verdis.gui.AbstractCidsBeanTablePanel;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class SRFrontenTablePanel extends AbstractCidsBeanTablePanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SRFrontenTablePanel.class);
    private static final String[] PREVIEW_COLUMN_NAMES = {
            "Nummer",
            "Grafik (alt)",
            "Grafik (neu)",
            "Korrektur (alt)",
            "Korrektur (neu)"
        };

    private static final Class[] PREVIEW_COLUMN_CLASSES = {
            Integer.class,
            Integer.class,
            Integer.class,
            Integer.class,
            Integer.class
        };
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
    private org.jdesktop.swingx.JXTable jxtOverview1;
    private de.cismet.verdis.gui.srfronten.SRFrontenTable sRFrontenTable1;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form RegenflaechenTabellenPanel.
     */
    public SRFrontenTablePanel() {
        initComponents();

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

        jDialog1.getRootPane().setDefaultButton(jButton2);
        StaticSwingTools.doClickButtonOnKeyStroke(
            jButton2,
            KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
            jDialog1.getRootPane());
        StaticSwingTools.doClickButtonOnKeyStroke(
            jButton1,
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            jDialog1.getRootPane());
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
        jxtOverview1 = new org.jdesktop.swingx.JXTable();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        sRFrontenTable1 = new de.cismet.verdis.gui.srfronten.SRFrontenTable();

        jDialog1.setTitle(org.openide.util.NbBundle.getMessage(
                SRFrontenTablePanel.class,
                "SRFrontenTablePanel.jDialog1.title")); // NOI18N
        jDialog1.setModal(true);
        jDialog1.setResizable(false);
        jDialog1.getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jScrollPane2.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));

        jxtOverview1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jxtOverview1.setModel(new GrafikPreviewTableModel());
        jxtOverview1.setRowSelectionAllowed(false);
        jScrollPane2.setViewportView(jxtOverview1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel2.add(jScrollPane2, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridLayout(1, 0, 10, 0));

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton1,
            org.openide.util.NbBundle.getMessage(SRFrontenTablePanel.class, "SRFrontenTablePanel.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });
        jPanel1.add(jButton1);

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton2,
            org.openide.util.NbBundle.getMessage(SRFrontenTablePanel.class, "SRFrontenTablePanel.jButton2.text")); // NOI18N
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
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPanel2.add(jPanel1, gridBagConstraints);

        jPanel3.setLayout(new java.awt.GridLayout(1, 0, 10, 0));

        jLabel1.setBackground(java.awt.Color.yellow);
        jLabel1.setForeground(java.awt.Color.black);
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(SRFrontenTablePanel.class, "SRFrontenTablePanel.jLabel1.text")); // NOI18N
        jLabel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel1.setOpaque(true);
        jPanel3.add(jLabel1);

        jLabel3.setBackground(new java.awt.Color(255, 31, 31));
        jLabel3.setForeground(java.awt.Color.white);
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel3,
            org.openide.util.NbBundle.getMessage(SRFrontenTablePanel.class, "SRFrontenTablePanel.jLabel3.text")); // NOI18N
        jLabel3.setToolTipText(org.openide.util.NbBundle.getMessage(
                SRFrontenTablePanel.class,
                "SRFrontenTablePanel.jLabel3.toolTipText"));                                                      // NOI18N
        jLabel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel3.setOpaque(true);
        jPanel3.add(jLabel3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        jPanel2.add(jPanel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jDialog1.getContentPane().add(jPanel2, gridBagConstraints);

        setLayout(new java.awt.BorderLayout());

        jScrollPane1.setViewportView(sRFrontenTable1);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton2ActionPerformed
        for (int displayedIndex = 0; displayedIndex < jxtOverview1.getRowCount(); ++displayedIndex) {
            final int modelIndex = jxtOverview1.convertRowIndexToModel(displayedIndex);

            final CidsBean frontBean = ((GrafikPreviewTableModel)jxtOverview1.getModel()).getCidsBeanByIndex(
                    modelIndex);

            final int oldGrafik = (Integer)jxtOverview1.getValueAt(displayedIndex, 1);
            final int newGrafik = (Integer)jxtOverview1.getValueAt(displayedIndex, 2);
            final int oldKorrektur = (Integer)jxtOverview1.getValueAt(displayedIndex, 3);
            final int newKorrektur = (Integer)jxtOverview1.getValueAt(displayedIndex, 4);

            if (oldGrafik != newGrafik) {
                try {
                    frontBean.setProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                                + FrontinfoPropertyConstants.PROP__LAENGE_GRAFIK,
                        newGrafik);
                } catch (final Exception ex) {
                    LOG.warn(ex, ex);
                }
            }
            if (oldKorrektur != newKorrektur) {
                try {
                    frontBean.setProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                                + FrontinfoPropertyConstants.PROP__LAENGE_KORREKTUR,
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
     * @param  evt  DOCUMENT ME!
     */
    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton1ActionPerformed
        jDialog1.setVisible(false);
    }                                                                            //GEN-LAST:event_jButton1ActionPerformed

    /**
     * DOCUMENT ME!
     */
    public void recalculateLengthOfFronten() {
        final List<CidsBean> toCorrectFrontenBeans = new ArrayList<>();

        for (int index = 0; index < sRFrontenTable1.getRowCount(); ++index) {
            final CidsBean flaecheBean = sRFrontenTable1.getModel()
                        .getCidsBeanByIndex(sRFrontenTable1.convertRowIndexToModel(index));
            toCorrectFrontenBeans.add(flaecheBean);
        }

        ((GrafikPreviewTableModel)jxtOverview1.getModel()).setCidsBeans(toCorrectFrontenBeans);
        jDialog1.pack();
        StaticSwingTools.showDialog(jDialog1);

        sRFrontenTable1.getModel().fireTableDataChanged();
        repaint();
    }

    @Override
    public AbstractCidsBeanTable getTable() {
        return sRFrontenTable1;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class GrafikPreviewTableModel extends AbstractCidsBeanTableModel {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new GrafikPreviewTableModel object.
         */
        public GrafikPreviewTableModel() {
            super(PREVIEW_COLUMN_NAMES, PREVIEW_COLUMN_CLASSES);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            final CidsBean cidsBean = getCidsBeanByIndex(rowIndex);
            if (cidsBean == null) {
                return null;
            }

            final Geometry geom = sRFrontenTable1.getGeometry(cidsBean);

            final int oldGrafik = (Integer)cidsBean.getProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                            + FrontinfoPropertyConstants.PROP__LAENGE_GRAFIK);
            final int oldKorrektur = (Integer)cidsBean.getProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                            + FrontinfoPropertyConstants.PROP__LAENGE_KORREKTUR);
            final boolean korrekturEqualsGrafik = oldKorrektur == oldGrafik;
            final int newGrafik = (geom != null) ? (int)geom.getLength() : 0;
            final int newKorrektur = (korrekturEqualsGrafik) ? newGrafik : oldKorrektur;

            switch (columnIndex) {
                case 0: {
                    return (Integer)cidsBean.getProperty(FrontPropertyConstants.PROP__NUMMER);
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
