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

import org.apache.commons.codec.digest.DigestUtils;

import org.openide.util.Exceptions;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import java.util.Date;

import javax.swing.Box;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;

import de.cismet.tools.BrowserLauncher;

import de.cismet.verdis.CidsAppBackend;
import de.cismet.verdis.EditModeListener;

import de.cismet.verdis.commons.constants.VerdisConstants;

import de.cismet.verdis.server.json.aenderungsanfrage.AenderungsanfrageJson;
import de.cismet.verdis.server.json.aenderungsanfrage.NachrichtJson;

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

    //~ Instance fields --------------------------------------------------------

    private AenderungsanfrageJson aenderungsanfrage;
    private String email;
    private String username;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JToggleButton jToggleButton1;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink1;
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
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        jXHyperlink1 = new org.jdesktop.swingx.JXHyperlink();
        jLabel1 = new javax.swing.JLabel();
        jToggleButton1 = new javax.swing.JToggleButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
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
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(filler2, gridBagConstraints);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${email}"),
                jXHyperlink1,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jXHyperlink1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jXHyperlink1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 5, 0);
        jPanel3.add(jXHyperlink1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(
                AenderungsanfrageNachrichtenPanel.class,
                "AenderungsanfrageNachrichtenPanel.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 5, 5);
        jPanel3.add(jLabel1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jToggleButton1,
            org.openide.util.NbBundle.getMessage(
                AenderungsanfrageNachrichtenPanel.class,
                "AenderungsanfrageNachrichtenPanel.jToggleButton1.text")); // NOI18N
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jToggleButton1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 5, 0);
        jPanel3.add(jToggleButton1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel2,
            org.openide.util.NbBundle.getMessage(
                AenderungsanfrageNachrichtenPanel.class,
                "AenderungsanfrageNachrichtenPanel.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 5, 5);
        jPanel3.add(jLabel2, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${stacId}"),
                jLabel3,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 5, 5);
        jPanel3.add(jLabel3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(filler3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
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
        add(jScrollPane1, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jScrollPane2.setMinimumSize(new java.awt.Dimension(19, 82));

        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setMinimumSize(new java.awt.Dimension(220, 80));
        jScrollPane2.setViewportView(jTextArea1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(jScrollPane2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton1,
            org.openide.util.NbBundle.getMessage(
                AenderungsanfrageNachrichtenPanel.class,
                "AenderungsanfrageNachrichtenPanel.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_END;
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
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton1ActionPerformed
        if ((jTextArea1.getText() != null) && !jTextArea1.getText().trim().isEmpty()) {
            final NachrichtJson nachrichtJson = new NachrichtJson.Sachberarbeiter(
                    true,
                    new Date(),
                    jTextArea1.getText().trim(),
                    username);
            aenderungsanfrage.getNachrichten().add(nachrichtJson);
            addNachricht(nachrichtJson);
            jTextArea1.setText("");
            refresh();
        }
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
    private void jXHyperlink1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jXHyperlink1ActionPerformed
        try {
            BrowserLauncher.openURL("mailto:" + email);
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }
    }                                                                                //GEN-LAST:event_jXHyperlink1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jToggleButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jToggleButton1ActionPerformed
        refresh();
    }                                                                                  //GEN-LAST:event_jToggleButton1ActionPerformed

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
     * @return  DOCUMENT ME!
     */
    public String getStacId() {
        final CidsBean aenderungsanfrageBean = AenderungsanfrageHandler.getInstance().getAenderungsanfrageBean();
        if (aenderungsanfrageBean != null) {
            final String md5 = DigestUtils.md5Hex(
                    Integer.toString(
                        (Integer)aenderungsanfrageBean.getProperty(
                            VerdisConstants.PROP.AENDERUNGSANFRAGE.KASSENZEICHEN_NUMMER))
                            + ";"
                            + Integer.toString(
                                (Integer)aenderungsanfrageBean.getProperty(
                                    VerdisConstants.PROP.AENDERUNGSANFRAGE.STAC_ID)));
            return md5.substring(0, 6);
        } else {
            return null;
        }
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
}
