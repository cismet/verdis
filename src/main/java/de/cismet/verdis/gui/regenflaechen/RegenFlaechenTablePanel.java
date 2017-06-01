/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis.gui.regenflaechen;

import com.vividsolutions.jts.geom.Geometry;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;

import java.awt.Color;
import java.awt.Component;

import java.util.ArrayList;
import java.util.List;

import de.cismet.cids.custom.util.VerdisUtils;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.tools.NumberStringComparator;

import de.cismet.tools.gui.StaticSwingTools;

import de.cismet.verdis.commons.constants.FlaechePropertyConstants;
import de.cismet.verdis.commons.constants.FlaechenartPropertyConstants;
import de.cismet.verdis.commons.constants.FlaecheninfoPropertyConstants;

import de.cismet.verdis.gui.AbstractCidsBeanTable;
import de.cismet.verdis.gui.AbstractCidsBeanTableModel;
import de.cismet.verdis.gui.AbstractCidsBeanTablePanel;
import de.cismet.verdis.gui.TableSorter;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class RegenFlaechenTablePanel extends AbstractCidsBeanTablePanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RegenFlaechenTable.class);

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

    // Variables declaration - do not modify
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane2;
    private org.jdesktop.swingx.JXTable jxtOverview1;
    // End of variables declaration

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private de.cismet.verdis.gui.regenflaechen.RegenFlaechenTable regenFlaechenTable1;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form RegenflaechenTabellenPanel.
     */
    public RegenFlaechenTablePanel() {
        initComponents();

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

        jDialog1.setTitle(org.openide.util.NbBundle.getMessage(
                RegenFlaechenTable.class,
                "RegenFlaechenTable.jDialog1.title_1")); // NOI18N
        jDialog1.setModal(true);
        jDialog1.setResizable(false);
        jDialog1.getContentPane().setLayout(new java.awt.GridBagLayout());
        jDialog1.getRootPane().setDefaultButton(jButton2);

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
                RegenFlaechenTable.class,
                "RegenFlaechenTable.jButton1.text_1")); // NOI18N
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
                RegenFlaechenTable.class,
                "RegenFlaechenTable.jButton2.text_1")); // NOI18N
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
                RegenFlaechenTable.class,
                "RegenFlaechenTable.jLabel1.text_1")); // NOI18N
        jLabel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel1.setOpaque(true);
        jPanel3.add(jLabel1);

        jLabel3.setBackground(Color.RED.brighter().brighter().brighter());
        jLabel3.setForeground(Color.WHITE);
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel3,
            org.openide.util.NbBundle.getMessage(
                RegenFlaechenTable.class,
                "RegenFlaechenTable.jLabel3.text_1")); // NOI18N
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
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) {
        jDialog1.setVisible(false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton2ActionPerformed(final java.awt.event.ActionEvent evt) {
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
    }

    /**
     * DOCUMENT ME!
     */
    public void recalculateAreaOfFlaechen() {
        final List<CidsBean> toCorrectFlaecheBeans = new ArrayList<CidsBean>();

        for (int index = 0; index < regenFlaechenTable1.getRowCount(); ++index) {
            final CidsBean flaecheBean = regenFlaechenTable1.getModel()
                        .getCidsBeanByIndex(regenFlaechenTable1.convertRowIndexToModel(index));
            toCorrectFlaecheBeans.add(flaecheBean);
        }

        ((GrafikPreviewTableModel)jxtOverview1.getModel()).setCidsBeans(toCorrectFlaecheBeans);
        jDialog1.pack();
        StaticSwingTools.showDialog(jDialog1);

        regenFlaechenTable1.getModel().fireTableDataChanged();
        repaint();
    }

    /**
     * DOCUMENT ME!
     */
    public void reEnumerateFlaechen() {
        final TableSorter sort = new TableSorter(regenFlaechenTable1.getModel());
        sort.setSortingStatus(3, TableSorter.DESCENDING);
        int counterInt = 0;
        String counterString = null;
        for (int i = 0; i < regenFlaechenTable1.getModel().getRowCount(); ++i) {
            final CidsBean flaecheBean = regenFlaechenTable1.getModel().getCidsBeanByIndex(sort.getSortedIndex(i));
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
                regenFlaechenTable1.getModel().fireTableDataChanged();
                repaint();
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
        jScrollPane1 = new javax.swing.JScrollPane();
        regenFlaechenTable1 = new de.cismet.verdis.gui.regenflaechen.RegenFlaechenTable();

        setLayout(new java.awt.BorderLayout());

        jScrollPane1.setViewportView(regenFlaechenTable1);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    } // </editor-fold>//GEN-END:initComponents

    @Override
    public AbstractCidsBeanTable getTable() {
        return regenFlaechenTable1;
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

            final Geometry geom = regenFlaechenTable1.getGeometry(cidsBean);

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
