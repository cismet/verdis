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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;

import java.io.BufferedWriter;
import java.io.FileWriter;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;

import de.cismet.cids.custom.utils.ByteArrayActionDownload;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;

import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.tools.BrowserLauncher;

import de.cismet.tools.gui.downloadmanager.AbstractDownload;
import de.cismet.tools.gui.downloadmanager.BackgroundTaskMultipleDownload;
import de.cismet.tools.gui.downloadmanager.Download;
import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;

import de.cismet.verdis.CidsAppBackend;
import de.cismet.verdis.EditModeListener;

import de.cismet.verdis.commons.constants.VerdisConstants;

import de.cismet.verdis.server.action.DownloadChangeRequestAnhangServerAction;
import de.cismet.verdis.server.json.AenderungsanfrageJson;
import de.cismet.verdis.server.json.NachrichtAnhangJson;
import de.cismet.verdis.server.json.NachrichtJson;
import de.cismet.verdis.server.json.NachrichtSachberarbeiterJson;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class AenderungsanfrageNachrichtenPanel extends javax.swing.JPanel implements CidsBeanStore, EditModeListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            AenderungsanfrageNachrichtenPanel.class);

    private static final DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

    //~ Instance fields --------------------------------------------------------

    private AenderungsanfrageJson aenderungsanfrage;
    private String email;
    private String username;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
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
        this(null, "Testnutzer");
    }

    /**
     * Creates new form AenderungsanfrageDiskussionPanel.
     *
     * @param  aenderungsanfrage  nachrichtJsons DOCUMENT ME!
     * @param  username           DOCUMENT ME!
     */
    public AenderungsanfrageNachrichtenPanel(final AenderungsanfrageJson aenderungsanfrage, final String username) {
        this.aenderungsanfrage = aenderungsanfrage;
        this.username = username;

        initComponents();

        jScrollPane1.addComponentListener(new ComponentAdapter() {

                @Override
                public void componentResized(final ComponentEvent e) {
                    resize();
                }
            });
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void resize() {
        int height = 20;
        Component filler = null;
        for (final Component component : jPanel1.getComponents()) {
            if (!(component instanceof Box.Filler)) {
                height += 20 + component.getHeight();
//                LOG.error(height);
            } else {
                filler = component;
            }
        }
        final int width = jScrollPane1.getWidth() - 20;
        if ((filler != null) && (height < jScrollPane1.getHeight())) {
            filler.setPreferredSize(new Dimension(width, jScrollPane1.getHeight() - height));
            height = jScrollPane1.getHeight() - 5;
        }
//        LOG.fatal(height);
        jPanel1.setPreferredSize(new Dimension(width, height));
    }

    /**
     * DOCUMENT ME!
     *
     * @param  username  DOCUMENT ME!
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jPanel3 = new javax.swing.JPanel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        jToggleButton1 = new javax.swing.JToggleButton();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        jButton3 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        jPanel3.setLayout(new java.awt.GridBagLayout());
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

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(jPanel3, gridBagConstraints);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(filler1, gridBagConstraints);

        jScrollPane1.setViewportView(jPanel1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(jScrollPane1, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jScrollPane2.setMinimumSize(new java.awt.Dimension(19, 82));

        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(3);
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.addKeyListener(new java.awt.event.KeyAdapter() {

                @Override
                public void keyPressed(final java.awt.event.KeyEvent evt) {
                    jTextArea1KeyPressed(evt);
                }
            });
        jScrollPane2.setViewportView(jTextArea1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
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
        jButton1.setMaximumSize(new java.awt.Dimension(48, 48));
        jButton1.setMinimumSize(new java.awt.Dimension(48, 48));
        jButton1.setPreferredSize(new java.awt.Dimension(48, 48));
        jButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel2.add(jButton1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(jPanel2, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     */
    private void sendMessage() {
        String text = jTextArea1.getText().trim();
        if ((jTextArea1.getText() != null) && !jTextArea1.getText().trim().isEmpty()) {
            final int size = aenderungsanfrage.getNachrichten().size();
            if (size > 0) {
                final int index = size - 1;
                final NachrichtJson lastNachricht = aenderungsanfrage.getNachrichten().get(index);
                if ((lastNachricht.getNachricht() != null) && NachrichtJson.Typ.CLERK.equals(lastNachricht.getTyp())
                            && Boolean.TRUE.equals(lastNachricht.getDraft()) && lastNachricht.getAnhang().isEmpty()) {
                    aenderungsanfrage.getNachrichten().remove(index);
                    refresh();
                    text = lastNachricht.getNachricht() + "\n" + text;
                }
            }
            final NachrichtJson nachrichtJson = new NachrichtSachberarbeiterJson(
                    true,
                    new Date(),
                    text,
                    username);
            aenderungsanfrage.getNachrichten().add(nachrichtJson);

            addNachricht(nachrichtJson);
            jTextArea1.setText("");
            refresh();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton1ActionPerformed
        sendMessage();
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
        final StringBuffer sb = new StringBuffer();

        if ((aenderungsanfrage != null) && (aenderungsanfrage.getNachrichten() != null)) {
            final Set<NachrichtAnhangJson> nachrichtAnhaenge = new HashSet<>();
            for (final NachrichtJson nachrichtJson : aenderungsanfrage.getNachrichten()) {
                if (!Boolean.TRUE.equals(nachrichtJson.getDraft())) {
                    final String anhangString;
                    if (nachrichtJson.getAnhang() != null) {
                        final List<String> anhaenge = new ArrayList<>();
                        for (final NachrichtAnhangJson nachrichtAnhang : nachrichtJson.getAnhang()) {
                            anhaenge.add(nachrichtAnhang.getName());
                            nachrichtAnhaenge.add(nachrichtAnhang);
                        }
                        if (anhaenge.isEmpty()) {
                            anhangString = null;
                        } else {
                            anhangString = String.join(", ", anhaenge);
                        }
                    } else {
                        anhangString = null;
                    }

                    final String typ;
                    if (nachrichtJson.getTyp() != null) {
                        switch (nachrichtJson.getTyp()) {
                            case CITIZEN: {
                                typ = "Bürger";
                            }
                            break;
                            case CLERK: {
                                typ = "Bearbeiter";
                            }
                            break;
                            case SYSTEM: {
                                typ = "System";
                            }
                            break;
                            default: {
                                typ = null;
                            }
                        }
                    } else {
                        typ = null;
                    }

                    final String text = AenderungsanfrageNachrichtPanel.createText(nachrichtJson);
                    sb.append(DATE_FORMAT.format(nachrichtJson.getTimestamp()))
                            .append(" - ")
                            .append(typ)
                            .append(":")
                            .append((text != null) ? (" " + text) : "")
                            .append((anhangString != null) ? (" [" + anhangString + "]") : "")
                            .append(System.lineSeparator());
                }
            }

            if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(this)) {
                final String jobname = DownloadManagerDialog.getInstance().getJobName();

                final BackgroundTaskMultipleDownload.FetchDownloadsTask fetchDownloadsTask =
                    new BackgroundTaskMultipleDownload.FetchDownloadsTask() {

                        @Override
                        public Collection<? extends Download> fetchDownloads() throws Exception {
                            final String directory = ((jobname != null)
                                    ? (jobname + System.getProperty("file.separator")) : "")
                                        + aenderungsanfrage.getKassenzeichen()
                                        + "_"
                                        + Math.abs(aenderungsanfrage.hashCode());
                            final Collection<Download> downloads = new ArrayList<>();
                            downloads.add(new TxtDownload(
                                    sb.toString(),
                                    directory,
                                    "Nachrichten",
                                    "nachrichten",
                                    ".txt"));
                            for (final NachrichtAnhangJson nachrichtAnhang : nachrichtAnhaenge) {
                                final Download download = new ByteArrayActionDownload(
                                        VerdisConstants.DOMAIN,
                                        DownloadChangeRequestAnhangServerAction.TASK_NAME,
                                        nachrichtAnhang.toJson(),
                                        null,
                                        "Anhang",
                                        directory,
                                        nachrichtAnhang.getName().substring(
                                            0,
                                            nachrichtAnhang.getName().lastIndexOf(".")),
                                        nachrichtAnhang.getName().substring(nachrichtAnhang.getName().lastIndexOf(".")),
                                        ConnectionContext.createDeprecated());
                                downloads.add(download);
                            }
                            return downloads;
                        }
                    };
                DownloadManager.instance()
                        .add(new BackgroundTaskMultipleDownload(null, "Gesprächsprotokoll", fetchDownloadsTask));
            }
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
            sendMessage();
        }
    }                                                                      //GEN-LAST:event_jTextArea1KeyPressed

    /**
     * DOCUMENT ME!
     */
    private void redoLastMessage() {
        final int size = aenderungsanfrage.getNachrichten().size();
        if (size > 0) {
            final int index = size - 1;
            final NachrichtJson lastNachricht = aenderungsanfrage.getNachrichten().get(index);
            if ((lastNachricht.getNachricht() != null) && NachrichtJson.Typ.CLERK.equals(lastNachricht.getTyp())
                        && Boolean.TRUE.equals(lastNachricht.getDraft()) && lastNachricht.getAnhang().isEmpty()) {
                aenderungsanfrage.getNachrichten().remove(index);
                refresh();
                jTextArea1.setText(lastNachricht.getNachricht() + "\n");
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    public final void refresh() {
        jPanel1.invalidate();
        clear();
        if ((aenderungsanfrage != null) && (aenderungsanfrage.getNachrichten() != null)) {
            for (final NachrichtJson nachrichtJson : aenderungsanfrage.getNachrichten()) {
                if (NachrichtJson.Typ.CITIZEN.equals(nachrichtJson.getTyp())
                            && Boolean.TRUE.equals(nachrichtJson.getDraft())) {
                    continue;
                }
                if (NachrichtJson.Typ.SYSTEM.equals(nachrichtJson.getTyp())
                            && !jToggleButton1.isSelected()) {
                    continue;
                }
                addNachricht(nachrichtJson);
            }
        }
        jPanel1.revalidate();
        try {
            Thread.sleep(100);
        } catch (final InterruptedException ex) {
        }
        SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    resize();
                    SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                scrollToBottom();
                            }
                        });
                }
            });
    }

    /**
     * DOCUMENT ME!
     *
     * @param  aenderungsanfrage  nachrichtJsons DOCUMENT ME!
     */
    public void setAenderungsanfrage(final AenderungsanfrageJson aenderungsanfrage) {
        this.aenderungsanfrage = aenderungsanfrage;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  nachrichtJson  DOCUMENT ME!
     */
    private void addNachricht(final NachrichtJson nachrichtJson) {
        final java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 20, 20);
        jPanel1.add(new AenderungsanfrageNachrichtPanel(nachrichtJson), gridBagConstraints);
    }

    /**
     * DOCUMENT ME!
     */
    private void scrollToBottom() {
        final JScrollBar verticalScrollbar = jScrollPane1.getVerticalScrollBar();
        verticalScrollbar.setValue(verticalScrollbar.getMaximum());
    }

    /**
     * DOCUMENT ME!
     */
    private void clear() {
        final java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;

        jPanel1.removeAll();
        jPanel1.add(new javax.swing.Box.Filler(
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767)),
            gridBagConstraints);
    }

    @Override
    public CidsBean getCidsBean() {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  email  DOCUMENT ME!
     */
    public void setEmail(final String email) {
        this.email = email;
    }

    @Override
    public void setCidsBean(final CidsBean cb) {
        bindingGroup.unbind();
        final CidsBean aenderungsanfrageBean = AenderungsanfrageHandler.getInstance().getAenderungsanfrageBean();
        setEmail((aenderungsanfrageBean != null)
                ? (String)aenderungsanfrageBean.getProperty(VerdisConstants.PROP.AENDERUNGSANFRAGE.EMAIL) : null);
        setAenderungsanfrage(AenderungsanfrageHandler.getInstance().getAenderungsanfrage());
        bindingGroup.bind();

        refresh();
    }

    @Override
    public void editModeChanged() {
        setEnabled(CidsAppBackend.getInstance().isEditable()
                    && (AenderungsanfrageHandler.getInstance().getAenderungsanfrageBean() != null));
    }

    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
        jPanel2.setVisible(enabled);
        refresh();
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static class TxtDownload extends AbstractDownload {

        //~ Instance fields ----------------------------------------------------

        private final String content;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new TxtDownload object.
         *
         * @param  content    DOCUMENT ME!
         * @param  directory  DOCUMENT ME!
         * @param  title      DOCUMENT ME!
         * @param  filename   DOCUMENT ME!
         * @param  extension  DOCUMENT ME!
         */
        public TxtDownload(
                final String content,
                final String directory,
                final String title,
                final String filename,
                final String extension) {
            this.content = content;
            this.directory = directory;
            this.title = title;

            status = Download.State.WAITING;

            determineDestinationFile(filename, extension);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void run() {
            if (status != Download.State.WAITING) {
                return;
            }

            status = Download.State.RUNNING;

            stateChanged();

            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(fileToSaveTo, false));
                writer.write(content);
            } catch (final Exception ex) {
                error(ex);
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (final Exception e) {
                        log.warn("Exception occured while closing file.", e);
                    }
                }
            }

            if (status == Download.State.RUNNING) {
                status = Download.State.COMPLETED;
                stateChanged();
            }
        }
    }
}
