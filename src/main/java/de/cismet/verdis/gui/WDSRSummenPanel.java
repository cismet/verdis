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

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;

import de.cismet.verdis.commons.constants.FrontinfoPropertyConstants;
import de.cismet.verdis.commons.constants.KassenzeichenPropertyConstants;

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
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JTable tblSR;
    private javax.swing.JTable tblWD;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form ZusammenfassungWinterdienstPanel.
     */
    public WDSRSummenPanel() {
        initComponents();
        tblSR.setModel(new DefaultTableModel());
        tblWD.setModel(new DefaultTableModel());
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

        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        tblSR = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        tblWD = new javax.swing.JTable();

        setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel3.setLayout(new java.awt.GridBagLayout());

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setText(org.openide.util.NbBundle.getMessage(WDSRSummenPanel.class, "WDSRSummenPanel.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 0);
        jPanel3.add(jLabel1, gridBagConstraints);

        jPanel1.add(jPanel3, java.awt.BorderLayout.PAGE_START);

        tblSR.setBackground(javax.swing.UIManager.getDefaults().getColor("Label.background"));
        jPanel1.add(tblSR, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jPanel1, gridBagConstraints);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel4.setLayout(new java.awt.GridBagLayout());

        jLabel2.setText(org.openide.util.NbBundle.getMessage(WDSRSummenPanel.class, "WDSRSummenPanel.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 0);
        jPanel4.add(jLabel2, gridBagConstraints);

        jPanel2.add(jPanel4, java.awt.BorderLayout.PAGE_START);

        tblWD.setBackground(javax.swing.UIManager.getDefaults().getColor("Label.background"));
        jPanel2.add(tblWD, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jPanel2, gridBagConstraints);
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
                final List<CidsBean> fronten = kassenzeichenBean.getBeanCollectionProperty(
                        KassenzeichenPropertyConstants.PROP__FRONTEN);
                final SumHashMap srHash = new SumHashMap();
                final SumHashMap wdHash = new SumHashMap();

                for (final CidsBean front : fronten) {
                    final Integer laenge = (Integer)front.getProperty(
                            FrontinfoPropertyConstants.PROP__LAENGE_KORREKTUR);

                    final CidsBean satzung_strassenreinigung = (CidsBean)front.getProperty(
                            FrontinfoPropertyConstants.PROP__LAGE_SR);
                    final String srKey;
                    if (satzung_strassenreinigung == null) {
                        srKey = front.getProperty(FrontinfoPropertyConstants.PROP__SR_KLASSE_OR__KEY) + "-"
                                    + front.getProperty(FrontinfoPropertyConstants.PROP__SR_KLASSE_OR__SCHLUESSEL);
                    } else {
                        srKey = satzung_strassenreinigung.getProperty("sr_klasse.key") + "-"
                                    + satzung_strassenreinigung.getProperty("sr_klasse.schluessel");
                    }
                    srHash.add(srKey, laenge);

                    final CidsBean satzung_winterdienst = (CidsBean)front.getProperty(
                            FrontinfoPropertyConstants.PROP__LAGE_WD);
                    final String wdKey;
                    if (satzung_winterdienst == null) {
                        wdKey = front.getProperty(FrontinfoPropertyConstants.PROP__WD_PRIO_OR__KEY) + "-"
                                    + front.getProperty(FrontinfoPropertyConstants.PROP__WD_PRIO_OR__SCHLUESSEL);
                    } else {
                        wdKey = satzung_winterdienst.getProperty("wd_prio.key") + "-"
                                    + satzung_winterdienst.getProperty("wd_prio.schluessel");
                    }
                    wdHash.add(wdKey, laenge);
                }

                final Vector srData = new Vector();
                for (final String key : srHash.keySet()) {
                    final Vector row = new Vector();
                    row.add(key);
                    row.add(srHash.get(key) + " m");
                    srData.add(row);
                }
                final Vector wdData = new Vector();
                for (final String key : wdHash.keySet()) {
                    final Vector row = new Vector();
                    row.add(key);
                    row.add(wdHash.get(key) + " m");
                    wdData.add(row);
                }

                final Vector header = new Vector();

                header.add("A");
                header.add("B");

                ((DefaultTableModel)tblSR.getModel()).setDataVector(srData, header);
                ((DefaultTableModel)tblWD.getModel()).setDataVector(wdData, header);
            }
        } catch (Exception e) {
            log.error("error in setCidsBean", e);
        }
    }
}

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
class SumHashMap extends HashMap<String, Integer> {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  key     DOCUMENT ME!
     * @param  number  DOCUMENT ME!
     */
    public void add(final String key, final Integer number) {
        final Integer x = super.get(key);
        if (x == null) {
            super.put(key, number);
        } else {
            super.put(key, number.intValue() + x.intValue());
        }
    }
}
