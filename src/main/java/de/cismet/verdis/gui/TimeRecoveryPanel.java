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
 * HistoryPanel.java
 *
 * Created on 06.12.2010, 10:22:48
 */
package de.cismet.verdis.gui;

import Sirius.server.middleware.types.HistoryObject;
import Sirius.server.middleware.types.MetaObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.util.CidsBeanSupport;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;

import de.cismet.verdis.CidsAppBackend;
import de.cismet.verdis.EditModeListener;

import de.cismet.verdis.commons.constants.VerdisConstants;

import de.cismet.verdis.gui.history.HistoryComboBox;
import de.cismet.verdis.gui.history.HistoryComboBoxModel;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class TimeRecoveryPanel extends javax.swing.JPanel implements CidsBeanStore, EditModeListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TimeRecoveryPanel.class);

    private static TimeRecoveryPanel INSTANCE;

    //~ Instance fields --------------------------------------------------------

    private CidsBean kassenzeichenBean;
    private HistoryObject left;
    private boolean loading;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cbLeft;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form HistoryPanel.
     */
    private TimeRecoveryPanel() {
        initComponents();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static TimeRecoveryPanel getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TimeRecoveryPanel();
            INSTANCE.jDialog1.pack();
        }
        return INSTANCE;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isLoading() {
        return loading;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JDialog getDialog() {
        return jDialog1;
    }

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
        jPanel3 = this;
        jButton2 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        cbLeft = new HistoryComboBox();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        jDialog1.setTitle(org.openide.util.NbBundle.getMessage(
                TimeRecoveryPanel.class,
                "TimeRecoveryPanel.jDialog1.title")); // NOI18N
        jDialog1.setResizable(false);
        jDialog1.getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel2.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(jPanel3, gridBagConstraints);

        jButton2.setText(org.openide.util.NbBundle.getMessage(
                TimeRecoveryPanel.class,
                "TimeRecoveryPanel.jButton2.text")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton2ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        jPanel2.add(jButton2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jDialog1.getContentPane().add(jPanel2, gridBagConstraints);

        setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        cbLeft.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cbLeftActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel1.add(cbLeft, gridBagConstraints);

        jButton1.setText(org.openide.util.NbBundle.getMessage(
                TimeRecoveryPanel.class,
                "TimeRecoveryPanel.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel1.add(jButton1, gridBagConstraints);

        jLabel1.setText(org.openide.util.NbBundle.getMessage(
                TimeRecoveryPanel.class,
                "TimeRecoveryPanel.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        jPanel1.add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel1, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbLeftActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cbLeftActionPerformed
        // TODO add your handling code here:
    } //GEN-LAST:event_cbLeftActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton1ActionPerformed
        new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    if (cbLeft.getModel().getSelectedItem() instanceof HistoryObject) {
                        left = (HistoryObject)cbLeft.getModel().getSelectedItem();
                        try {
                            loading = true;
                            final CidsBean kassenzeichenFromHistory = CidsBean.createNewCidsBeanFromJSON(
                                    true,
                                    left.getJsonData());
                            final Collection<String> arrayPropertiesColl = Arrays.asList(
                                    VerdisConstants.PROP.KASSENZEICHEN.FLAECHEN,
                                    VerdisConstants.PROP.KASSENZEICHEN.FRONTEN,
                                    VerdisConstants.PROP.KASSENZEICHEN.KASSENZEICHEN_GEOMETRIEN,
                                    VerdisConstants.PROP.KASSENZEICHEN.KANALANSCHLUSS
                                            + "."
                                            + VerdisConstants.PROP.KANALANSCHLUSS.BEFREIUNGENUNDERLAUBNISSE);

                            final Map<String, Map> propertyToMapMap = new HashMap<String, Map>();
                            for (final String arrayProperty : arrayPropertiesColl) {
                                final Map<Integer, CidsBean> idToBeanMap = new HashMap<Integer, CidsBean>();
                                final Collection<CidsBean> cidsBeans = kassenzeichenBean.getBeanCollectionProperty(
                                        arrayProperty);
                                if (cidsBeans != null) {
                                    for (final CidsBean cidsBean : cidsBeans) {
                                        idToBeanMap.put(cidsBean.getMetaObject().getId(), cidsBean);
                                    }
                                }
                                propertyToMapMap.put(arrayProperty, idToBeanMap);
                            }

                            final int moStatus = kassenzeichenBean.getMetaObject().getStatus();
                            CidsBeanSupport.deepcopyAllProperties(kassenzeichenFromHistory, kassenzeichenBean);
                            kassenzeichenBean.getMetaObject().forceStatus(moStatus);

                            for (final String arrayProperty : arrayPropertiesColl) {
                                final Collection<CidsBean> cidsBeans = kassenzeichenBean.getBeanCollectionProperty(
                                        arrayProperty);
                                final Map<Integer, CidsBean> idToBeanMap = new HashMap<Integer, CidsBean>();

                                if (cidsBeans != null) {
                                    for (final CidsBean cidsBean : cidsBeans) {
                                        idToBeanMap.put(cidsBean.getMetaObject().getId(), cidsBean);
                                    }
                                }

                                // only new ones
                                final Collection<Integer> ids = idToBeanMap.keySet();
                                ids.removeAll(propertyToMapMap.get(arrayProperty).keySet());

                                for (final Integer id : ids) {
                                    idToBeanMap.get(id).getMetaObject().forceStatus(MetaObject.NEW);
                                }
                            }
                            CidsAppBackend.getInstance().setCidsBean(kassenzeichenBean);
                        } catch (Exception ex) {
                            LOG.fatal(ex, ex);
                        } finally {
                            loading = false;
                        }
                    } else {
                        left = null;
                    }

                    return null;
                }
            }.execute();
    } //GEN-LAST:event_jButton1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton2ActionPerformed
        jDialog1.setVisible(false);
    }                                                                            //GEN-LAST:event_jButton2ActionPerformed

    @Override
    public CidsBean getCidsBean() {
        return kassenzeichenBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        kassenzeichenBean = cidsBean;

        if (cidsBean != null) {
            final int kassenzeichenId = (Integer)kassenzeichenBean.getProperty("id");

            new SwingWorker<HistoryObject[], Void>() {

                    @Override
                    protected HistoryObject[] doInBackground() {
                        try {
                            final HistoryObject[] historyObjs = CidsAppBackend.getInstance()
                                        .getHistory(kassenzeichenId, 99);

                            final SortedMap<Date, HistoryObject> sMap = new TreeMap<Date, HistoryObject>();
                            for (final HistoryObject historyObj : historyObjs) {
                                if (!"{ DELETED }".equalsIgnoreCase(historyObj.getJsonData())) {
                                    sMap.put(historyObj.getValidFrom(), historyObj);
                                }
                            }

                            Date lastDate = null;
                            for (final Date date : new ArrayList<Date>(sMap.keySet())) {
                                if (lastDate != null) {
                                    final long diff = Math.abs(lastDate.getTime() - date.getTime());
                                    if (diff < 5000) {
                                        sMap.remove(date);
                                    }
                                }
                                lastDate = date;
                            }

                            // reverse order
                            final List<HistoryObject> sorted = new ArrayList<HistoryObject>(sMap.values());
                            Collections.reverse(sorted);
                            return sorted.toArray(new HistoryObject[0]);
                        } catch (Exception e) {
                            LOG.error("Error occurred while fetching history of kassenzeichen '" + kassenzeichenId
                                        + "'.",
                                e);
                            return new HistoryObject[] {};
                        }
                    }

                    @Override
                    protected void done() {
                        try {
                            final HistoryObject[] historyObjects = get();
                            if ((historyObjects != null) && (historyObjects.length > 0)) {
                                cbLeft.setModel(new HistoryComboBoxModel(historyObjects));
                                cbLeft.setEnabled(true);
                            } else {
                                cbLeft.setEnabled(false);
                                JOptionPane.showMessageDialog(
                                    TimeRecoveryPanel.this,
                                    "Der Verlauf des Kassenzeichens '"
                                            + kassenzeichenId
                                            + "' konnte nicht erstellt werden.",
                                    "Fehler",
                                    JOptionPane.ERROR_MESSAGE);
                            }
                            jDialog1.pack();
                        } catch (final Exception e) {
                            LOG.error("Exception in Background Thread", e);
                        }
                    }
                }.execute();
        }
    }

    @Override
    public void editModeChanged() {
        jButton1.setEnabled(CidsAppBackend.getInstance().isEditable());
    }
}
