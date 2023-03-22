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
package de.cismet.verdis.gui.aenderungsanfrage;

import Sirius.navigator.connection.SessionManager;

import Sirius.server.newuser.User;

import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.application.Platform;

import javafx.concurrent.Worker;

import netscape.javascript.JSObject;

import org.apache.commons.collections.map.MultiValueMap;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.clientutils.ByteArrayActionDownload;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.actions.GetServerResourceServerAction;

import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.tools.BrowserLauncher;

import de.cismet.tools.gui.FXWebViewPanel;
import de.cismet.tools.gui.JPopupMenuButton;
import de.cismet.tools.gui.downloadmanager.Download;
import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;

import de.cismet.verdis.CidsAppBackend;
import de.cismet.verdis.EditModeListener;

import de.cismet.verdis.commons.constants.VerdisConstants;

import de.cismet.verdis.server.action.DownloadChangeRequestAnhangServerAction;
import de.cismet.verdis.server.json.AenderungsanfrageJson;
import de.cismet.verdis.server.json.NachrichtJson;
import de.cismet.verdis.server.json.NachrichtParameterJson;
import de.cismet.verdis.server.json.NachrichtParameterNotifyJson;
import de.cismet.verdis.server.json.NachrichtParameterSeenJson;
import de.cismet.verdis.server.json.NachrichtSachberarbeiterJson;
import de.cismet.verdis.server.json.NachrichtSystemJson;
import de.cismet.verdis.server.json.TextbausteinJson;
import de.cismet.verdis.server.utils.AenderungsanfrageUtils;
import de.cismet.verdis.server.utils.VerdisServerResources;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class AenderungsanfrageNachrichtenPanel extends javax.swing.JPanel implements EditModeListener,
    AenderungsanfrageHandler.ChangeListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            AenderungsanfrageNachrichtenPanel.class);

    //~ Instance fields --------------------------------------------------------

    final FXWebViewPanel fxWebView = new FXWebViewPanel();

    private String email;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JToggleButton jToggleButton1;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AenderungsanfrageNachrichtenPanel object.
     */
    public AenderungsanfrageNachrichtenPanel() {
        initComponents();
        refreshTextbausteine();
        jPanel1.add(fxWebView, BorderLayout.CENTER);

        AenderungsanfrageHandler.getInstance().addChangeListener(this);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void refreshTextbausteine() {
        try {
            final TextbausteinJson[] textbausteine = loadTextBausteine();
            final MultiValueMap textBausteineMultiMap = new MultiValueMap();
            final Set<String> categories = new LinkedHashSet<>();
            for (final TextbausteinJson textbaustein : textbausteine) {
                final String category = textbaustein.getKategorie();
                textBausteineMultiMap.put(category, textbaustein);
                categories.add(category);
            }
            final Map<String, TextbausteinJson[]> textBausteineCategorised = new HashMap<>();
            for (final String category : categories) {
                final Collection<TextbausteinJson> catTextbausteine = ((Collection<TextbausteinJson>)
                        textBausteineMultiMap.getCollection(category));
                textBausteineCategorised.put((String)category, catTextbausteine.toArray(new TextbausteinJson[0]));
            }
            ((TextbausteinePopupButton)jButton7).setData(categories.toArray(new String[0]), textBausteineCategorised);
        } catch (final Exception ex) {
            CidsAppBackend.getInstance()
                    .showError(
                        "Fehler beim Laden der Text-Bausteine",
                        "Die Text-Bausteine konnten nicht geladen werden.",
                        ex);
            jButton7.setVisible(false);
        }
    }

    /**
     * DOCUMENT ME!
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jButton6 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        jToggleButton1 = new javax.swing.JToggleButton();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        jButton3 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        jButton7 = new TextbausteinePopupButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton6,
            org.openide.util.NbBundle.getMessage(
                AenderungsanfrageNachrichtenPanel.class,
                "AenderungsanfrageNachrichtenPanel.jButton6.text")); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton6ActionPerformed(evt);
                }
            });

        setLayout(new java.awt.CardLayout());
        add(jPanel5, "null");

        jPanel4.setLayout(new java.awt.GridBagLayout());

        jPanel3.setLayout(new java.awt.GridBagLayout());

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/reload.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            jButton4,
            org.openide.util.NbBundle.getMessage(
                AenderungsanfrageNachrichtenPanel.class,
                "AenderungsanfrageNachrichtenPanel.jButton4.text"));                                             // NOI18N
        jButton4.setToolTipText(org.openide.util.NbBundle.getMessage(
                AenderungsanfrageNachrichtenPanel.class,
                "AenderungsanfrageNachrichtenPanel.jButton4.toolTipText"));                                      // NOI18N
        jButton4.setBorderPainted(false);
        jButton4.setContentAreaFilled(false);
        jButton4.setFocusPainted(false);
        jButton4.setMaximumSize(new java.awt.Dimension(24, 24));
        jButton4.setMinimumSize(new java.awt.Dimension(24, 24));
        jButton4.setPreferredSize(new java.awt.Dimension(24, 24));
        jButton4.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton4ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
        jPanel3.add(jButton4, gridBagConstraints);

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/read.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            jButton5,
            org.openide.util.NbBundle.getMessage(
                AenderungsanfrageNachrichtenPanel.class,
                "AenderungsanfrageNachrichtenPanel.jButton5.text"));                                           // NOI18N
        jButton5.setToolTipText(org.openide.util.NbBundle.getMessage(
                AenderungsanfrageNachrichtenPanel.class,
                "AenderungsanfrageNachrichtenPanel.jButton5.toolTipText"));                                    // NOI18N
        jButton5.setBorderPainted(false);
        jButton5.setContentAreaFilled(false);
        jButton5.setEnabled(false);
        jButton5.setFocusPainted(false);
        jButton5.setMaximumSize(new java.awt.Dimension(24, 24));
        jButton5.setMinimumSize(new java.awt.Dimension(24, 24));
        jButton5.setPreferredSize(new java.awt.Dimension(24, 24));
        jButton5.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton5ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
        jPanel3.add(jButton5, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(filler3, gridBagConstraints);

        jToggleButton1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/hide_system_messages.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            jToggleButton1,
            org.openide.util.NbBundle.getMessage(
                AenderungsanfrageNachrichtenPanel.class,
                "AenderungsanfrageNachrichtenPanel.jToggleButton1.text"));                  // NOI18N
        jToggleButton1.setToolTipText(org.openide.util.NbBundle.getMessage(
                AenderungsanfrageNachrichtenPanel.class,
                "AenderungsanfrageNachrichtenPanel.jToggleButton1.toolTipText"));           // NOI18N
        jToggleButton1.setBorderPainted(false);
        jToggleButton1.setContentAreaFilled(false);
        jToggleButton1.setFocusPainted(false);
        jToggleButton1.setMaximumSize(new java.awt.Dimension(24, 24));
        jToggleButton1.setMinimumSize(new java.awt.Dimension(24, 24));
        jToggleButton1.setPreferredSize(new java.awt.Dimension(24, 24));
        jToggleButton1.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/show_system_messages.png"))); // NOI18N
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jToggleButton1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPanel3.add(jToggleButton1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(filler4, gridBagConstraints);

        jButton3.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/download_comments.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            jButton3,
            org.openide.util.NbBundle.getMessage(
                AenderungsanfrageNachrichtenPanel.class,
                "AenderungsanfrageNachrichtenPanel.jButton3.text"));                     // NOI18N
        jButton3.setToolTipText(org.openide.util.NbBundle.getMessage(
                AenderungsanfrageNachrichtenPanel.class,
                "AenderungsanfrageNachrichtenPanel.jButton3.toolTipText"));              // NOI18N
        jButton3.setBorderPainted(false);
        jButton3.setFocusPainted(false);
        jButton3.setMaximumSize(new java.awt.Dimension(24, 24));
        jButton3.setMinimumSize(new java.awt.Dimension(24, 24));
        jButton3.setPreferredSize(new java.awt.Dimension(24, 24));
        jButton3.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton3ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        jPanel3.add(jButton3, gridBagConstraints);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/send_mail.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            jButton2,
            org.openide.util.NbBundle.getMessage(
                AenderungsanfrageNachrichtenPanel.class,
                "AenderungsanfrageNachrichtenPanel.jButton2.text"));                                                // NOI18N
        jButton2.setBorderPainted(false);
        jButton2.setFocusPainted(false);
        jButton2.setMaximumSize(new java.awt.Dimension(24, 24));
        jButton2.setMinimumSize(new java.awt.Dimension(24, 24));
        jButton2.setPreferredSize(new java.awt.Dimension(24, 24));

        final org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${email}"),
                jButton2,
                org.jdesktop.beansbinding.BeanProperty.create("toolTipText"));
        bindingGroup.addBinding(binding);

        jButton2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton2ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        jPanel3.add(jButton2, gridBagConstraints);
        jButton2.setVisible(false);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(
                AenderungsanfrageNachrichtenPanel.class,
                "AenderungsanfrageNachrichtenPanel.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        jPanel3.add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel4.add(jPanel3, gridBagConstraints);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel4.add(jPanel1, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jScrollPane2.setMinimumSize(new java.awt.Dimension(19, 82));

        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(4);
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.addKeyListener(new java.awt.event.KeyAdapter() {

                @Override
                public void keyPressed(final java.awt.event.KeyEvent evt) {
                    jTextArea1KeyPressed(evt);
                }
            });
        jScrollPane2.setViewportView(jTextArea1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(jScrollPane2, gridBagConstraints);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/send_message.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            jButton1,
            org.openide.util.NbBundle.getMessage(
                AenderungsanfrageNachrichtenPanel.class,
                "AenderungsanfrageNachrichtenPanel.jButton1.text"));                                                   // NOI18N
        jButton1.setToolTipText(org.openide.util.NbBundle.getMessage(
                AenderungsanfrageNachrichtenPanel.class,
                "AenderungsanfrageNachrichtenPanel.jButton1.toolTipText"));                                            // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel2.add(jButton1, gridBagConstraints);

        jButton7.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/gui/aenderungsanfrage/pencil--plus.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            jButton7,
            org.openide.util.NbBundle.getMessage(
                AenderungsanfrageNachrichtenPanel.class,
                "AenderungsanfrageNachrichtenPanel.jButton7.text"));                                  // NOI18N
        jButton7.setToolTipText(org.openide.util.NbBundle.getMessage(
                AenderungsanfrageNachrichtenPanel.class,
                "AenderungsanfrageNachrichtenPanel.jButton7.toolTipText"));                           // NOI18N
        jButton7.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton7ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel2.add(jButton7, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(filler1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel4.add(jPanel2, gridBagConstraints);

        add(jPanel4, "chat");

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     */
    private void sendMessagePressed() {
        final String text = jTextArea1.getText().trim();
        if ((jTextArea1.getText() != null) && !jTextArea1.getText().trim().isEmpty()) {
            final AenderungsanfrageJson aenderungsanfrage = getAenderungsanfrage();

            // Nachricht erzeugen und lokal hinzufügen
            final NachrichtJson nachrichtJson = new NachrichtSachberarbeiterJson(
                    null,
                    null,
                    null,
                    text,
                    SessionManager.getSession().getUser().getName(),
                    true);
            aenderungsanfrage.getNachrichten().add(nachrichtJson);

            // GUI vorab schonmal anpassen
            refresh(aenderungsanfrage);
            jTextArea1.setText("");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton1ActionPerformed
        sendMessagePressed();
    }                                                                            //GEN-LAST:event_jButton1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */

    public String getEmail() {
        return email;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jToggleButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jToggleButton1ActionPerformed
        refresh();
        jToggleButton1.setToolTipText(jToggleButton1.isSelected() ? "Systemnachrichten verbergen"
                                                                  : "Systemnachrichten anzeigen");
    }                                                                                  //GEN-LAST:event_jToggleButton1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton2ActionPerformed
        try {
            BrowserLauncher.openURL("mailto:" + email);
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }
    }                                                                            //GEN-LAST:event_jButton2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton3ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton3ActionPerformed
        final AenderungsanfrageJson aenderungsanfrage = getAenderungsanfrage();

        if ((aenderungsanfrage != null) && (aenderungsanfrage.getNachrichten() != null)) {
            final String jobname;
            if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(this)) {
                jobname = DownloadManagerDialog.getInstance().getJobName();
            } else {
                jobname = null;
            }

            DownloadManager.instance()
                    .add(new AenderungsanfrageDownload(
                            jobname,
                            aenderungsanfrage,
                            ConnectionContext.createDeprecated()));
        }
    } //GEN-LAST:event_jButton3ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jTextArea1KeyPressed(final java.awt.event.KeyEvent evt) { //GEN-FIRST:event_jTextArea1KeyPressed
        if (KeyEvent.VK_UP == evt.getKeyCode()) {
            redoLastMessage();
        } else if ((KeyEvent.VK_ENTER == evt.getKeyCode()) && (evt.isControlDown() || evt.isAltDown())) {
            sendMessagePressed();
        }
    }                                                                      //GEN-LAST:event_jTextArea1KeyPressed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton4ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton4ActionPerformed
        jButton1.setEnabled(false);
        new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    AenderungsanfrageHandler.getInstance().reloadCurrentAnfrage();
                    return null;
                }

                @Override
                protected void done() {
                    jButton1.setEnabled(true);
                }
            }.execute();
    } //GEN-LAST:event_jButton4ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton5ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton5ActionPerformed
        final AenderungsanfrageJson aenderungsanfrage = AenderungsanfrageHandler.getInstance().getAenderungsanfrage();
        final NachrichtJson nachrichtJson = new NachrichtSystemJson(new NachrichtParameterSeenJson(),
                SessionManager.getSession().getUser().getName());
        aenderungsanfrage.getNachrichten().add(nachrichtJson);
        refresh(aenderungsanfrage);
        jButton5.setEnabled(false);

        new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    AenderungsanfrageHandler.getInstance()
                            .sendAenderungsanfrage(new AenderungsanfrageJson(
                                    aenderungsanfrage.getKassenzeichen(),
                                    aenderungsanfrage.getEmailAdresse(),
                                    aenderungsanfrage.getEmailVerifiziert(),
                                    null,
                                    null,
                                    aenderungsanfrage.getNachrichten()));
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        AenderungsanfrageHandler.getInstance().reloadAenderungsanfrageBeans();
                    } catch (final Exception ex) {
                        LOG.error(ex, ex);
                    }
                }
            }.execute();
    } //GEN-LAST:event_jButton5ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton6ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton6ActionPerformed
        final AenderungsanfrageJson aenderungsanfrage = getAenderungsanfrage();
        final NachrichtJson nachrichtJson = new NachrichtSystemJson(new NachrichtParameterNotifyJson(false),
                SessionManager.getSession().getUser().getName());
        aenderungsanfrage.getNachrichten().add(nachrichtJson);
        refresh(aenderungsanfrage);
        jPanel1.remove(jButton6);

        // nachricht tatsächlich senden
        new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    try {
                        AenderungsanfrageHandler.getInstance()
                                .sendAenderungsanfrage(new AenderungsanfrageJson(
                                        aenderungsanfrage.getKassenzeichen(),
                                        aenderungsanfrage.getEmailAdresse(),
                                        aenderungsanfrage.getEmailVerifiziert(),
                                        null,
                                        null,
                                        aenderungsanfrage.getNachrichten()));
                    } catch (final Exception ex) {
                        LOG.error(ex, ex);
                    }
                    return null;
                }
            }.execute();
    } //GEN-LAST:event_jButton6ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton7ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton7ActionPerformed
        refreshTextbausteine();
    }                                                                            //GEN-LAST:event_jButton7ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private TextbausteinJson[] loadTextBausteine() throws Exception {
        final User user = SessionManager.getSession().getUser();
        final String textBausteineString = (String)SessionManager.getProxy()
                    .executeTask(
                            user,
                            GetServerResourceServerAction.TASK_NAME,
                            VerdisConstants.DOMAIN,
                            VerdisServerResources.AENDERUNTSANFRAGE_TEXTBAUSTEINE.getValue(),
                            ConnectionContext.createDummy());
        return new ObjectMapper().readValue(textBausteineString, TextbausteinJson[].class);
    }

    /**
     * DOCUMENT ME!
     */
    private void redoLastMessage() {
        final AenderungsanfrageJson aenderungsanfrage = getAenderungsanfrage();
        if ((aenderungsanfrage != null)
                    && (aenderungsanfrage.getNachrichten() != null)
                    && !aenderungsanfrage.getNachrichten().isEmpty()) {
            final List<NachrichtJson> reversedNachrichten = new ArrayList<>(aenderungsanfrage.getNachrichten());
            Collections.reverse(reversedNachrichten);

            for (final NachrichtJson lastNachricht : reversedNachrichten) {
                if (NachrichtJson.Typ.CLERK.equals(lastNachricht.getTyp())
                            && Boolean.TRUE.equals(lastNachricht.getDraft())) {
                    aenderungsanfrage.getNachrichten().remove(lastNachricht);
                    refresh(aenderungsanfrage);
                    jTextArea1.setText(lastNachricht.getNachricht() + "\n");
                    break;
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void refresh() {
        refresh(getAenderungsanfrage());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  aenderungsanfrage  DOCUMENT ME!
     */
    private void refresh(final AenderungsanfrageJson aenderungsanfrage) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        refresh(aenderungsanfrage);
                    }
                });
        } else {
            final List<NachrichtJson> nachrichten = (aenderungsanfrage != null) ? aenderungsanfrage.getNachrichten()
                                                                                : null;

            jPanel1.remove(jButton6);
            try {
                fxWebView.loadContent(AenderungsanfrageUtils.createChatHtmlFromAenderungsanfrage(
                        aenderungsanfrage,
                        12,
                        jToggleButton1.isSelected(),
                        false,
                        false));

                new SwingWorker<Void, Void>() {

                        @Override
                        protected Void doInBackground() throws Exception {
                            Worker.State state = null;
                            while ((state = fxWebView.getWebEngine().getLoadWorker().getState())
                                        != Worker.State.SUCCEEDED) {
                                if ((state == Worker.State.FAILED) || (state == Worker.State.CANCELLED)) {
                                    throw new Exception("worker failed or cancelled");
                                }
                                Thread.sleep(10);
                            }
                            return null;
                        }

                        @Override
                        protected void done() {
                            Platform.runLater(new Runnable() {

                                    @Override
                                    public void run() {
                                        ((JSObject)fxWebView.getWebEngine().executeScript("window")).setMember(
                                            "java",
                                            AenderungsanfrageNachrichtenPanel.this);
                                        fxWebView.getWebEngine()
                                                .executeScript("window.scrollTo(0, document.body.scrollHeight);");
                                    }
                                });
                        }
                    }.execute();
            } catch (final Exception ex) {
                CidsAppBackend.getInstance()
                        .showError(
                            "Fehler beim Laden des Chats",
                            "Beim Laden des Chats kam es zu einem unerwarteten Fehler.",
                            ex);
            }

            int nonSystemCount = 0;
            if (nachrichten != null) {
                NachrichtJson lastNachrichtJson = null;
                for (final NachrichtJson nachrichtJson : nachrichten) {
                    if (NachrichtJson.Typ.CITIZEN.equals(nachrichtJson.getTyp())
                                && Boolean.TRUE.equals(nachrichtJson.getDraft())) {
                        continue;
                    }
                    if (NachrichtJson.Typ.SYSTEM.equals(nachrichtJson.getTyp())
                                && !jToggleButton1.isSelected()) {
                        continue;
                    }
                    if (!NachrichtJson.Typ.SYSTEM.equals(nachrichtJson.getTyp())) {
                        nonSystemCount++;
                    }
                    lastNachrichtJson = nachrichtJson;
                }
                if ((lastNachrichtJson != null) && !CidsAppBackend.getInstance().isEditable()) {    // to ensure that all we don't send notifications before changes are undrafted
                    final boolean lastMessageIsNotification =
                        NachrichtJson.Typ.SYSTEM.equals(lastNachrichtJson.getTyp())
                                && (lastNachrichtJson.getNachrichtenParameter() != null)
                                && NachrichtParameterJson.Type.NOTIFY.equals(
                                    lastNachrichtJson.getNachrichtenParameter().getType());
                    final boolean lastMessageIsStatusDone = (lastNachrichtJson.getNachrichtenParameter() != null)
                                && AenderungsanfrageUtils.Status.NONE.equals(lastNachrichtJson.getNachrichtenParameter()
                                    .getStatus());
                    final boolean lastMessageWasFromMe = SessionManager.getSession()
                                .getUser()
                                .getName()
                                .equals(lastNachrichtJson.getAbsender());
                    final boolean hasVerifiedEmail = (aenderungsanfrage.getEmailAdresse() != null)
                                && Boolean.TRUE.equals(aenderungsanfrage.getEmailVerifiziert());
                    final boolean showButton = hasVerifiedEmail
                                && lastMessageWasFromMe
                                && !lastMessageIsNotification
                                && !lastMessageIsStatusDone;
                    if (showButton) {
                        final java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
                        gridBagConstraints.gridx = 0;
                        gridBagConstraints.gridy = GridBagConstraints.RELATIVE;
                        gridBagConstraints.fill = java.awt.GridBagConstraints.NONE;
                        gridBagConstraints.weightx = 1.0;
                        gridBagConstraints.insets = new java.awt.Insets(0, 20, 20, 20);
                        jPanel1.add(jButton6, BorderLayout.SOUTH);
                    }
                }
            }
            jLabel1.setText(String.format(
                    "<html><i>%d %s",
                    nonSystemCount,
                    (nonSystemCount == 1) ? "Nachricht" : "Nachrichten"));

            jPanel1.doLayout();
            revalidate();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  name  DOCUMENT ME!
     * @param  json  DOCUMENT ME!
     */
    public void linkClicked(final String name, final String json) {
        SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    try {
                        if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(
                                        AenderungsanfrageNachrichtenPanel.this)) {
                            final String jobname = DownloadManagerDialog.getInstance().getJobName();
                            final String filename = name;
                            final Download download = new ByteArrayActionDownload(
                                    VerdisConstants.DOMAIN,
                                    DownloadChangeRequestAnhangServerAction.TASK_NAME,
                                    json,
                                    null,
                                    filename,
                                    jobname,
                                    filename.substring(0, filename.lastIndexOf(".")),
                                    filename.substring(filename.lastIndexOf(".")),
                                    ConnectionContext.createDeprecated());

                            DownloadManager.instance().add(download);
                        }
                    } catch (final Exception ex) {
                        CidsAppBackend.getInstance()
                                .showError(
                                    "Fehler beim Download",
                                    "Der Anhang konnte nicht heruntergeladen werden.",
                                    ex);
                    }
                }
            });
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public AenderungsanfrageJson getAenderungsanfrage() {
        return AenderungsanfrageHandler.getInstance().getAenderungsanfrage();
    }

    @Override
    public void editModeChanged() {
        setEnabled(CidsAppBackend.getInstance().isEditable()
                    && (AenderungsanfrageHandler.getInstance().getAenderungsanfrage() != null));
    }

    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
        jPanel2.setVisible(enabled);
        refresh();
    }

    @Override
    public void aenderungsanfrageChanged(final AenderungsanfrageJson aenderungsanfrage) {
        refresh(aenderungsanfrage);
        jButton5.setEnabled(!isEnabled() && AenderungsanfrageUtils.isNewCitizenMessage(aenderungsanfrage));
        ((CardLayout)getLayout()).show(this, (aenderungsanfrage == null) ? "null" : "chat");
    }

    @Override
    public void aenderungsanfrageBeansChanged(final List<CidsBean> aenderungsanfrageBeans) {
    }

    @Override
    public void loadingStarted() {
    }

    @Override
    public void loadingFinished() {
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static class myPanel extends JPanel {

        //~ Methods ------------------------------------------------------------

        @Override
        public void setPreferredSize(final Dimension preferredSize) {
            super.setPreferredSize(preferredSize); // To change body of generated methods, choose Tools | Templates.
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class TextbausteinePopupButton extends JPopupMenuButton {

        //~ Instance fields ----------------------------------------------------

        private final JPopupMenu popupMenu = new JPopupMenu();

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new TextbausteinePopupButton object.
         */
        public TextbausteinePopupButton() {
            super(true);
            setPopupMenu(popupMenu);
            addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        refreshTextbausteine();
                        popupMenu.show(TextbausteinePopupButton.this, 0, getHeight());
                    }
                });
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public JPopupMenu getPopupMenu() {
            return popupMenu;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  orderedKeys  DOCUMENT ME!
         * @param  data         DOCUMENT ME!
         */
        public void setData(final String[] orderedKeys, final Map<String, TextbausteinJson[]> data) {
            getPopupMenu().removeAll();
            for (final String key : orderedKeys) {
                final TextbausteinJson[] values = data.get(key);
                if ((values != null) && (values.length > 0)) {
                    final JMenu catMenu = new JMenu(key);

                    for (final TextbausteinJson value : values) {
                        catMenu.add(new MyMenu(value));
                    }
                    getPopupMenu().add(catMenu);
                }
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @param  text  DOCUMENT ME!
         */
        public void addText(final String text) {
            jTextArea1.replaceSelection("");
            jTextArea1.insert(text, jTextArea1.getCaretPosition());
        }

        //~ Inner Classes ------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @version  $Revision$, $Date$
         */
        class MyMenu extends JMenuItem {

            //~ Constructors ---------------------------------------------------

            /**
             * Creates a new MyMenu object.
             *
             * @param  textbaustein  DOCUMENT ME!
             */
            public MyMenu(final TextbausteinJson textbaustein) {
                super(textbaustein.getTitel());
                setToolTipText(textbaustein.getText());
                addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(final ActionEvent e) {
                            addText(textbaustein.getText());
                        }
                    });
            }
        }
    }
}
