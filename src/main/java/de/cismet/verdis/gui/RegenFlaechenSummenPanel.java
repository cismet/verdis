/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * SummenPanel.java
 *
 * Created on 5. Januar 2005, 14:01
 */
package de.cismet.verdis.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.DefaultTableModel;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;

/**
 * DOCUMENT ME!
 *
 * @author   hell
 * @version  $Revision$, $Date$
 */
public class RegenFlaechenSummenPanel extends javax.swing.JPanel implements CidsBeanStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            RegenFlaechenSummenPanel.class);

    //~ Instance fields --------------------------------------------------------

    private CidsBean kassenzeichenBean;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblAnschlussgrad;
    private javax.swing.JLabel lblVeranlagung;
    private javax.swing.JPanel panAnschlussgrad;
    private javax.swing.JPanel panVeranlagung;
    private javax.swing.JTable tblSumAnschlussgrad;
    private javax.swing.JTable tblSumVeranlagung;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form SummenPanel.
     */
    public RegenFlaechenSummenPanel() {
        initComponents();
        tblSumVeranlagung.setDefaultRenderer(Object.class, new SummenTableCellRenderer());
        tblSumAnschlussgrad.setDefaultRenderer(Object.class, new SummenTableCellRenderer());
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panVeranlagung = new javax.swing.JPanel();
        lblVeranlagung = new javax.swing.JLabel();
        tblSumVeranlagung = new javax.swing.JTable();
        panAnschlussgrad = new javax.swing.JPanel();
        lblAnschlussgrad = new javax.swing.JLabel();
        tblSumAnschlussgrad = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        panVeranlagung.setLayout(new java.awt.GridBagLayout());

        lblVeranlagung.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblVeranlagung.setText("Veranlagung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 0);
        panVeranlagung.add(lblVeranlagung, gridBagConstraints);

        tblSumVeranlagung.setBackground(javax.swing.UIManager.getDefaults().getColor("Label.background"));
        tblSumVeranlagung.setModel(new DefaultTableModel());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        panVeranlagung.add(tblSumVeranlagung, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(panVeranlagung, gridBagConstraints);

        panAnschlussgrad.setLayout(new java.awt.GridBagLayout());

        lblAnschlussgrad.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblAnschlussgrad.setText("Anschlussgrad");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 0);
        panAnschlussgrad.add(lblAnschlussgrad, gridBagConstraints);

        tblSumAnschlussgrad.setBackground(javax.swing.UIManager.getDefaults().getColor("Label.background"));
        tblSumAnschlussgrad.setModel(new DefaultTableModel());
        tblSumAnschlussgrad.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblSumAnschlussgrad.setFocusable(false);
        tblSumAnschlussgrad.setMinimumSize(new java.awt.Dimension(60, 25));
        tblSumAnschlussgrad.setRowSelectionAllowed(false);
        tblSumAnschlussgrad.setShowHorizontalLines(false);
        tblSumAnschlussgrad.setShowVerticalLines(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        panAnschlussgrad.add(tblSumAnschlussgrad, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(panAnschlussgrad, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel1, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    @Override
    public CidsBean getCidsBean() {
        return kassenzeichenBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        try {
            kassenzeichenBean = cidsBean;
            if (cidsBean != null) {
                final Map<String, Double> veranlagungHash = new HashMap<String, Double>();
                final Map<String, Double> anschlussgradHash = new HashMap<String, Double>();

                Main.getCurrentInstance().fillFlaechenVeranlagungSummeMap(veranlagungHash);
                Main.getCurrentInstance().fillFlaechenAnschlussgradSummeMap(anschlussgradHash);

                final List<String> veranlagungKeys = Arrays.asList(veranlagungHash.keySet().toArray(new String[0]));
                final List<String> anschlussgradKeys = Arrays.asList(anschlussgradHash.keySet().toArray(new String[0]));

                Collections.sort(veranlagungKeys);
                final List<String[]> veranlagungData = new ArrayList<String[]>();
                for (final String key : veranlagungKeys) {
                    final double value = veranlagungHash.get(key);
                    if (value > 0) {
                        veranlagungData.add(new String[] { key, (int)value + " m²" });
                    }
                }

                Collections.sort(anschlussgradKeys);
                final List<String[]> anschlussgradData = new ArrayList<String[]>();
                for (final String key : anschlussgradKeys) {
                    final double value = anschlussgradHash.get(key);
                    if (value > 0) {
                        anschlussgradData.add(new String[] { key, (int)value + " m²" });
                    }
                }

                final String[] header = { "A", "B" };

                ((DefaultTableModel)tblSumVeranlagung.getModel()).setDataVector(veranlagungData.toArray(
                        new String[0][]),
                    header);
                ((DefaultTableModel)tblSumAnschlussgrad.getModel()).setDataVector(anschlussgradData.toArray(
                        new String[0][]),
                    header);
                tblSumVeranlagung.getColumnModel().getColumn(1).setPreferredWidth(0);
                tblSumAnschlussgrad.getColumnModel().getColumn(1).setPreferredWidth(0);
            }
        } catch (Exception e) {
            LOG.error("error in setCidsBean", e);
        }
    }
}
