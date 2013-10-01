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
 * ZusammenfassungWinterdienstPanel.java
 *
 * Created on 04.12.2010, 10:30:26
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
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class WDSRSummenPanel extends javax.swing.JPanel implements CidsBeanStore {

    //~ Instance fields --------------------------------------------------------

    private final transient org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private CidsBean kassenzeichenBean;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JLabel lblStrasse;
    private javax.swing.JPanel panStrasse;
    private javax.swing.JTable tblSR;
    private javax.swing.JTable tblSumStrasse;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form ZusammenfassungWinterdienstPanel.
     */
    public WDSRSummenPanel() {
        initComponents();
        tblSR.setDefaultRenderer(Object.class, new SummenTableCellRenderer());
        tblSumStrasse.setDefaultRenderer(Object.class, new SummenTableCellRenderer());
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

        jPanel5 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        tblSR = new javax.swing.JTable();
        panStrasse = new javax.swing.JPanel();
        lblStrasse = new javax.swing.JLabel();
        tblSumStrasse = new javax.swing.JTable();

        setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel5, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setText(org.openide.util.NbBundle.getMessage(WDSRSummenPanel.class, "WDSRSummenPanel.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 0);
        jPanel1.add(jLabel1, gridBagConstraints);

        tblSR.setBackground(javax.swing.UIManager.getDefaults().getColor("Label.background"));
        tblSR.setModel(new DefaultTableModel());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPanel1.add(tblSR, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jPanel1, gridBagConstraints);

        panStrasse.setLayout(new java.awt.GridBagLayout());

        lblStrasse.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblStrasse.setText(org.openide.util.NbBundle.getMessage(
                WDSRSummenPanel.class,
                "WDSRSummenPanel.lblStrasse.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 0);
        panStrasse.add(lblStrasse, gridBagConstraints);

        tblSumStrasse.setBackground(javax.swing.UIManager.getDefaults().getColor("Label.background"));
        tblSumStrasse.setModel(new DefaultTableModel());
        tblSumStrasse.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblSumStrasse.setFocusable(false);
        tblSumStrasse.setMinimumSize(new java.awt.Dimension(60, 25));
        tblSumStrasse.setRowSelectionAllowed(false);
        tblSumStrasse.setShowHorizontalLines(false);
        tblSumStrasse.setShowVerticalLines(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        panStrasse.add(tblSumStrasse, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(panStrasse, gridBagConstraints);
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
                final Map<String, Double> srHash = new HashMap<String, Double>();
                final Map<String, Double> strasseHash = new HashMap<String, Double>();

                Main.getCurrentInstance().fillStrassenreinigungSummeMap(srHash);
                Main.getCurrentInstance().fillStrasseSummeMap(strasseHash);

                final List<String> srKeys = Arrays.asList(srHash.keySet().toArray(new String[0]));
                final List<String> strasseKeys = Arrays.asList(strasseHash.keySet().toArray(new String[0]));

                Collections.sort(srKeys);
                final List<String[]> srData = new ArrayList<String[]>();
                for (final String key : srKeys) {
                    final double value = srHash.get(key);
                    if (value > 0) {
                        srData.add(new String[] { key, value + " m" });
                    }
                }

                Collections.sort(strasseKeys);
                final List<String[]> strasseData = new ArrayList<String[]>();
                for (final String key : strasseKeys) {
                    final double value = strasseHash.get(key);
                    if (value > 0) {
                        strasseData.add(new String[] { key, value + " m" });
                    }
                }

                final String[] header = { "A", "B" };

                ((DefaultTableModel)tblSR.getModel()).setDataVector(srData.toArray(new String[0][]), header);
                ((DefaultTableModel)tblSumStrasse.getModel()).setDataVector(strasseData.toArray(new String[0][]),
                    header);

                tblSR.getColumnModel().getColumn(1).setPreferredWidth(0);
                tblSumStrasse.getColumnModel().getColumn(1).setPreferredWidth(0);
            }
        } catch (Exception e) {
            log.error("error in setCidsBean", e);
        }
    }
}
