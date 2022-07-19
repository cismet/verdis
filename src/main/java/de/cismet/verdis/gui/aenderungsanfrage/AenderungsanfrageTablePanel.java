/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis.gui.aenderungsanfrage;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import org.jdesktop.swingx.JXTable;

import org.openide.util.Exceptions;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;

import de.cismet.cids.servermessage.CidsServerMessageNotifier;
import de.cismet.cids.servermessage.CidsServerMessageNotifierListener;
import de.cismet.cids.servermessage.CidsServerMessageNotifierListenerEvent;

import de.cismet.verdis.CidsAppBackend;
import de.cismet.verdis.EditModeListener;

import de.cismet.verdis.commons.constants.VerdisConstants;

import de.cismet.verdis.server.action.KassenzeichenChangeRequestServerAction;
import de.cismet.verdis.server.json.AenderungsanfrageJson;
import de.cismet.verdis.server.json.NachrichtParameterProlongJson;
import de.cismet.verdis.server.json.NachrichtSystemJson;
import de.cismet.verdis.server.utils.AenderungsanfrageUtils;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class AenderungsanfrageTablePanel extends JPanel implements CidsBeanStore,
    EditModeListener,
    AenderungsanfrageHandler.ChangeListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            AenderungsanfrageTablePanel.class);

    //~ Instance fields --------------------------------------------------------

    private CidsBean cidsBean;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.cismet.verdis.gui.aenderungsanfrage.AenderungsanfrageTable aenderungsanfrageTable1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JToggleButton jToggleButton2;
    private javax.swing.JToggleButton jToggleButton3;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form RegenflaechenTabellenPanel.
     */
    public AenderungsanfrageTablePanel() {
        initComponents();

        jButton7.setVisible(false);
        aenderungsanfrageTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                @Override
                public void valueChanged(final ListSelectionEvent e) {
                    jButton2.setEnabled(aenderungsanfrageTable1.getSelectedAenderungsanfrageBean() != null);

                    final CidsBean aenderungsanfrageBean = aenderungsanfrageTable1.getSelectedAenderungsanfrageBean();
                    final String status = (aenderungsanfrageBean != null)
                        ? (String)aenderungsanfrageBean.getProperty(
                            VerdisConstants.PROP.AENDERUNGSANFRAGE.STATUS
                                    + "."
                                    + VerdisConstants.PROP.AENDERUNGSANFRAGE_STATUS.SCHLUESSEL) : null;

                    jButton7.setVisible(AenderungsanfrageUtils.Status.ARCHIVED.toString().equals(status));
                }
            });

        try {
            if (SessionManager.getConnection().hasConfigAttr(
                            SessionManager.getSession().getUser(),
                            "csm://"
                            + KassenzeichenChangeRequestServerAction.CSM_NEWREQUEST)) {
                CidsServerMessageNotifier.getInstance()
                        .subscribe(new CidsServerMessageNotifierListener() {

                                @Override
                                public void messageRetrieved(final CidsServerMessageNotifierListenerEvent event) {
                                    try {
                                        final KassenzeichenChangeRequestServerAction.ServerMessage serverMessage =
                                            (KassenzeichenChangeRequestServerAction.ServerMessage)event
                                            .getMessage().getContent();
                                        AenderungsanfrageHandler.getInstance().reloadAenderungsanfrageBeans();
                                    } catch (final Exception ex) {
                                        LOG.warn(ex, ex);
                                    }
                                }
                            },
                            KassenzeichenChangeRequestServerAction.CSM_NEWREQUEST);
            }
        } catch (final ConnectionException ex) {
            LOG.warn("Konnte Rechte an csm://" + KassenzeichenChangeRequestServerAction.CSM_NEWREQUEST
                        + " nicht abfragen.",
                ex);
        }
        AenderungsanfrageHandler.getInstance().addChangeListener(this);

        try {
            AenderungsanfrageHandler.getInstance().reloadAenderungsanfrageBeans();
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }
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
        jButton1 = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        jToggleButton1 = new javax.swing.JToggleButton();
        jToggleButton2 = new javax.swing.JToggleButton();
        jToggleButton3 = new javax.swing.JToggleButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        jButton7 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        jScrollPane1 = new javax.swing.JScrollPane();
        aenderungsanfrageTable1 = new de.cismet.verdis.gui.aenderungsanfrage.AenderungsanfrageTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/reload.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            jButton1,
            org.openide.util.NbBundle.getMessage(
                AenderungsanfrageTablePanel.class,
                "AenderungsanfrageTablePanel.jButton1.text"));                                                   // NOI18N
        jButton1.setToolTipText(org.openide.util.NbBundle.getMessage(
                AenderungsanfrageTablePanel.class,
                "AenderungsanfrageTablePanel.jButton1.toolTipText"));                                            // NOI18N
        jButton1.setBorderPainted(false);
        jButton1.setContentAreaFilled(false);
        jButton1.setFocusPainted(false);
        jButton1.setMaximumSize(new java.awt.Dimension(24, 24));
        jButton1.setMinimumSize(new java.awt.Dimension(24, 24));
        jButton1.setPreferredSize(new java.awt.Dimension(24, 24));
        jButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
        jPanel1.add(jButton1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(filler3, gridBagConstraints);

        jToggleButton1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/filter_user_disabled.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            jToggleButton1,
            org.openide.util.NbBundle.getMessage(
                AenderungsanfrageTablePanel.class,
                "AenderungsanfrageTablePanel.jToggleButton1.text"));                        // NOI18N
        jToggleButton1.setToolTipText(org.openide.util.NbBundle.getMessage(
                AenderungsanfrageTablePanel.class,
                "AenderungsanfrageTablePanel.jToggleButton1.toolTipText"));                 // NOI18N
        jToggleButton1.setBorderPainted(false);
        jToggleButton1.setContentAreaFilled(false);
        jToggleButton1.setFocusPainted(false);
        jToggleButton1.setMaximumSize(new java.awt.Dimension(24, 24));
        jToggleButton1.setMinimumSize(new java.awt.Dimension(24, 24));
        jToggleButton1.setPreferredSize(new java.awt.Dimension(24, 24));
        jToggleButton1.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/filter_user_enabled.png")));  // NOI18N
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jToggleButton1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        jPanel1.add(jToggleButton1, gridBagConstraints);
        // jToggleButton1.setVisible(false);

        jToggleButton2.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/filter_kassenzeichen_disabled.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            jToggleButton2,
            org.openide.util.NbBundle.getMessage(
                AenderungsanfrageTablePanel.class,
                "AenderungsanfrageTablePanel.jToggleButton2.text"));                                 // NOI18N
        jToggleButton2.setToolTipText(org.openide.util.NbBundle.getMessage(
                AenderungsanfrageTablePanel.class,
                "AenderungsanfrageTablePanel.jToggleButton2.toolTipText"));                          // NOI18N
        jToggleButton2.setBorderPainted(false);
        jToggleButton2.setContentAreaFilled(false);
        jToggleButton2.setFocusPainted(false);
        jToggleButton2.setMaximumSize(new java.awt.Dimension(24, 24));
        jToggleButton2.setMinimumSize(new java.awt.Dimension(24, 24));
        jToggleButton2.setPreferredSize(new java.awt.Dimension(24, 24));
        jToggleButton2.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/filter_kassenzeichen_enabled.png")));  // NOI18N
        jToggleButton2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jToggleButton2ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        jPanel1.add(jToggleButton2, gridBagConstraints);
        // jToggleButton1.setVisible(false);

        jToggleButton3.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/filter_active_disabled.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            jToggleButton3,
            org.openide.util.NbBundle.getMessage(
                AenderungsanfrageTablePanel.class,
                "AenderungsanfrageTablePanel.jToggleButton3.text"));                          // NOI18N
        jToggleButton3.setToolTipText(org.openide.util.NbBundle.getMessage(
                AenderungsanfrageTablePanel.class,
                "AenderungsanfrageTablePanel.jToggleButton3.toolTipText"));                   // NOI18N
        jToggleButton3.setBorderPainted(false);
        jToggleButton3.setContentAreaFilled(false);
        jToggleButton3.setFocusPainted(false);
        jToggleButton3.setMaximumSize(new java.awt.Dimension(24, 24));
        jToggleButton3.setMinimumSize(new java.awt.Dimension(24, 24));
        jToggleButton3.setPreferredSize(new java.awt.Dimension(24, 24));
        jToggleButton3.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/filter_active_enabled.png")));  // NOI18N
        jToggleButton3.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jToggleButton3ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        jPanel1.add(jToggleButton3, gridBagConstraints);
        // jToggleButton1.setVisible(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(filler2, gridBagConstraints);

        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/prolongation.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            jButton7,
            org.openide.util.NbBundle.getMessage(
                AenderungsanfrageTablePanel.class,
                "AenderungsanfrageTablePanel.jButton7.text"));                                                         // NOI18N
        jButton7.setToolTipText(org.openide.util.NbBundle.getMessage(
                AenderungsanfrageTablePanel.class,
                "AenderungsanfrageTablePanel.jButton7.toolTipText"));                                                  // NOI18N
        jButton7.setBorderPainted(false);
        jButton7.setContentAreaFilled(false);
        jButton7.setFocusPainted(false);
        jButton7.setMaximumSize(new java.awt.Dimension(24, 24));
        jButton7.setMinimumSize(new java.awt.Dimension(24, 24));
        jButton7.setPreferredSize(new java.awt.Dimension(24, 24));
        jButton7.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton7ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        jPanel1.add(jButton7, gridBagConstraints);

        jButton2.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/kassenzeichen22.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            jButton2,
            org.openide.util.NbBundle.getMessage(
                AenderungsanfrageTablePanel.class,
                "AenderungsanfrageTablePanel.jButton2.text"));                         // NOI18N
        jButton2.setToolTipText(org.openide.util.NbBundle.getMessage(
                AenderungsanfrageTablePanel.class,
                "AenderungsanfrageTablePanel.jButton2.toolTipText"));                  // NOI18N
        jButton2.setBorderPainted(false);
        jButton2.setContentAreaFilled(false);
        jButton2.setEnabled(false);
        jButton2.setMaximumSize(new java.awt.Dimension(24, 24));
        jButton2.setMinimumSize(new java.awt.Dimension(24, 24));
        jButton2.setPreferredSize(new java.awt.Dimension(24, 24));
        jButton2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton2ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPanel1.add(jButton2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(filler4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(filler5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(jPanel1, gridBagConstraints);

        aenderungsanfrageTable1.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    aenderungsanfrageTable1MouseClicked(evt);
                }
            });
        jScrollPane1.setViewportView(aenderungsanfrageTable1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(jScrollPane1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(
                AenderungsanfrageTablePanel.class,
                "AenderungsanfrageTablePanel.jLabel1.text")); // NOI18N
        jPanel2.add(jLabel1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(jPanel2, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton1ActionPerformed
        try {
            AenderungsanfrageHandler.getInstance().reloadAenderungsanfrageBeans();
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }
    }                                                                            //GEN-LAST:event_jButton1ActionPerformed

    /**
     * DOCUMENT ME!
     */

    @Override
    public void loadingStarted() {
        jButton1.setEnabled(false);
        jPanel2.setVisible(true);
        jLabel1.setText("Änderungsanfragen werden neu geladen ...");
        refreshFilterButtons();
    }

    @Override
    public void loadingFinished() {
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton2ActionPerformed
        ((AenderungsanfrageTable)aenderungsanfrageTable1).gotoSelectedKassenzeichen();
    }                                                                            //GEN-LAST:event_jButton2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void aenderungsanfrageTable1MouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_aenderungsanfrageTable1MouseClicked
        if (jButton1.isEnabled() && (evt.getClickCount() == 2) && !CidsAppBackend.getInstance().isEditable()) {
            if (aenderungsanfrageTable1.getSelectedRow() >= 0) {
                ((AenderungsanfrageTable)aenderungsanfrageTable1).gotoSelectedKassenzeichen();
            }
        }
    }                                                                                       //GEN-LAST:event_aenderungsanfrageTable1MouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jToggleButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jToggleButton1ActionPerformed
        aenderungsanfrageTable1.setFilterUsername(jToggleButton1.isSelected());
        AenderungsanfrageHandler.getInstance().setFilterOwn(jToggleButton1.isSelected());
    }                                                                                  //GEN-LAST:event_jToggleButton1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jToggleButton2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jToggleButton2ActionPerformed
        aenderungsanfrageTable1.setFilterKassenzeichen(jToggleButton2.isSelected());
    }                                                                                  //GEN-LAST:event_jToggleButton2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jToggleButton3ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jToggleButton3ActionPerformed
        aenderungsanfrageTable1.setFilterActive(jToggleButton3.isSelected());
        AenderungsanfrageHandler.getInstance().setFilterActive(jToggleButton3.isSelected());
    }                                                                                  //GEN-LAST:event_jToggleButton3ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton7ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton7ActionPerformed
        jButton7.setEnabled(false);
        new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    final CidsBean aenderungsanfrageBean = aenderungsanfrageTable1.getSelectedAenderungsanfrageBean();
                    final String aenderungsanfrageJson = (aenderungsanfrageBean != null)
                        ? (String)aenderungsanfrageBean.getProperty(VerdisConstants.PROP.AENDERUNGSANFRAGE.CHANGES_JSON)
                        : null;
                    final AenderungsanfrageJson aenderungsanfrage = (aenderungsanfrageJson != null)
                        ? AenderungsanfrageUtils.getInstance().createAenderungsanfrageJson(aenderungsanfrageJson)
                        : null;
                    final Integer stacId = (Integer)aenderungsanfrageBean.getProperty(
                            VerdisConstants.PROP.AENDERUNGSANFRAGE.STAC_ID);
                    aenderungsanfrage.getNachrichten()
                            .add(new NachrichtSystemJson(
                                    null,
                                    null,
                                    null,
                                    new NachrichtParameterProlongJson(Boolean.FALSE),
                                    SessionManager.getSession().getUser().getName()));
                    AenderungsanfrageHandler.getInstance().sendAenderungsanfrage(aenderungsanfrage, stacId);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        AenderungsanfrageHandler.getInstance().reloadAenderungsanfrageBeans();
                    } catch (final Exception ex) {
                        LOG.error(ex, ex);
                    } finally {
                        jButton7.setEnabled(true);
                    }
                }
            }.execute();
    } //GEN-LAST:event_jButton7ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JXTable getTable() {
        return aenderungsanfrageTable1;
    }

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        this.cidsBean = cidsBean;
        aenderungsanfrageTable1.update();
    }

    @Override
    public void editModeChanged() {
//        setEnabled(CidsAppBackend.getInstance().isEditable()
//                    && (AenderungsanfrageHandler.getInstance().getAenderungsanfrageBean() != null));
        aenderungsanfrageTable1.update();
    }

    @Override
    public void aenderungsanfrageChanged(final AenderungsanfrageJson aenderungsanfrageJson) {
        aenderungsanfrageTable1.update();
    }

    @Override
    public void aenderungsanfrageBeansChanged(final List<CidsBean> aenderungsanfrageBeans) {
        new SwingWorker<Map<CidsBean, CidsAppBackend.StacOptionsEntry>, Void>() {

                @Override
                protected Map<CidsBean, CidsAppBackend.StacOptionsEntry> doInBackground() throws Exception {
                    final Map<CidsBean, CidsAppBackend.StacOptionsEntry> beanToStacEntryMap = new LinkedHashMap<>();

                    for (final CidsBean aenderungsanfrageBean : aenderungsanfrageBeans) {
                        final CidsAppBackend.StacOptionsEntry entry = CidsAppBackend.getInstance()
                                    .getStacOptionsEntry((Integer)aenderungsanfrageBean.getProperty(
                                            VerdisConstants.PROP.AENDERUNGSANFRAGE.STAC_ID));
                        beanToStacEntryMap.put(aenderungsanfrageBean, entry);
                    }
                    return beanToStacEntryMap;
                }

                @Override
                protected void done() {
                    try {
                        aenderungsanfrageTable1.setCidsBeans(get());
                    } catch (final Exception ex) {
                        LOG.error(ex, ex);
                    } finally {
                        jButton1.setEnabled(true);
                        jPanel2.setVisible(false);
                        refreshFilterButtons();
                    }
                }
            }.execute();
    }

    /**
     * DOCUMENT ME!
     */
    public void refreshFilterButtons() {
        jToggleButton3.setSelected(AenderungsanfrageHandler.getInstance().isFilterActive());
        jToggleButton1.setSelected(AenderungsanfrageHandler.getInstance().isFilterOwn());
    }
}
