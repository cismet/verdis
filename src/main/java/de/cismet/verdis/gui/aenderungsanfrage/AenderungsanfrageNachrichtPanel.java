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

import org.jdesktop.swingx.JXHyperlink;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.text.DateFormat;

import java.util.Date;

import javax.swing.ImageIcon;

import de.cismet.cids.custom.clientutils.ByteArrayActionDownload;

import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.layout.WrapLayout;

import de.cismet.tools.gui.downloadmanager.Download;
import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;

import de.cismet.verdis.commons.constants.VerdisConstants;

import de.cismet.verdis.server.action.DownloadChangeRequestAnhangServerAction;
import de.cismet.verdis.server.json.FlaecheAnschlussgradJson;
import de.cismet.verdis.server.json.FlaecheFlaechenartJson;
import de.cismet.verdis.server.json.NachrichtAnhangJson;
import de.cismet.verdis.server.json.NachrichtJson;
import de.cismet.verdis.server.json.NachrichtParameterJson;
import de.cismet.verdis.server.json.NachrichtSachberarbeiterJson;
import de.cismet.verdis.server.utils.AenderungsanfrageUtils;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class AenderungsanfrageNachrichtPanel extends javax.swing.JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            AenderungsanfrageNachrichtPanel.class);

    private static final Color COLOR_LEFT = new Color(110, 204, 214);
    private static final Color COLOR_LEFT_DRAFT = COLOR_LEFT.brighter();
    private static final Color COLOR_CENTER = new Color(238, 238, 238);
    private static final Color COLOR_CENTER_DRAFT = COLOR_CENTER.brighter();
    private static final Color COLOR_RIGHT = new Color(252, 221, 153);
    private static final Color COLOR_RIGHT_DRAFT = COLOR_RIGHT.brighter();
    private static final DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum Orientation {

        //~ Enum constants -----------------------------------------------------

        LEFT, CENTER, RIGHT
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel lblCenter;
    private javax.swing.JLabel lblLeft;
    private javax.swing.JLabel lblRight;
    private javax.swing.JPanel pnlAnhang;
    private de.cismet.tools.gui.RoundedPanel roundedPanel1;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form AenderungsanfrageNachrichtPanel.
     */
    public AenderungsanfrageNachrichtPanel() {
        this(new NachrichtSachberarbeiterJson(
                "test-test-test",
                new Date(),
                null,
                "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.",
                "Dirk Steinbacher",
                Boolean.FALSE));
    }

    /**
     * Creates a new AenderungsanfrageNachrichtPanel object.
     *
     * @param  nachrichtJson  orientation DOCUMENT ME!
     */
    public AenderungsanfrageNachrichtPanel(final NachrichtJson nachrichtJson) {
        final String absender = nachrichtJson.getAbsender();
        final Date timestamp = nachrichtJson.getTimestamp();

        final AenderungsanfrageNachrichtPanel.Orientation orientation;
        if (NachrichtJson.Typ.CLERK.equals(nachrichtJson.getTyp())) {
            orientation = AenderungsanfrageNachrichtPanel.Orientation.LEFT;
        } else if (NachrichtJson.Typ.CITIZEN.equals(nachrichtJson.getTyp())) {
            orientation = AenderungsanfrageNachrichtPanel.Orientation.RIGHT;
        } else {
            orientation = AenderungsanfrageNachrichtPanel.Orientation.CENTER;
        }

        final String text = createText(nachrichtJson);

        initComponents();
        jTextArea1.setText(text);

        if ((nachrichtJson.getAnhang() != null) && (nachrichtJson.getAnhang().size() > 0)) {
            pnlAnhang.setVisible(true);
            for (final NachrichtAnhangJson nachrichtAnhang : nachrichtJson.getAnhang()) {
                pnlAnhang.add(new AnhangLink(nachrichtAnhang));
            }
        } else {
            pnlAnhang.setVisible(false);
        }
        roundedPanel1.setAlpha(255);

        lblCenter.setText((timestamp != null) ? DATE_FORMAT.format(timestamp) : null);
        if (Orientation.LEFT.equals(orientation)) {
            jPanel4.remove(filler1);
            lblLeft.setText(absender);
            lblRight.setText("");
            roundedPanel1.setBackground(Boolean.TRUE.equals(nachrichtJson.getDraft()) ? COLOR_LEFT_DRAFT : COLOR_LEFT);
        } else if (Orientation.RIGHT.equals(orientation)) {
            jPanel4.remove(filler2);
            lblLeft.setText("");
            lblRight.setText(absender);
            roundedPanel1.setBackground(Boolean.TRUE.equals(nachrichtJson.getDraft()) ? COLOR_RIGHT_DRAFT
                                                                                      : COLOR_RIGHT);
        } else {
            lblLeft.setText("");
            lblRight.setText("");
            roundedPanel1.setBackground(Boolean.TRUE.equals(nachrichtJson.getDraft()) ? COLOR_CENTER_DRAFT
                                                                                      : COLOR_CENTER);
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   nachrichtJson  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String createText(final NachrichtJson nachrichtJson) {
        final String nachricht = nachrichtJson.getNachricht();
        final NachrichtParameterJson nachrichtenParameter = nachrichtJson.getNachrichtenParameter();

        final String text;
        if ((nachrichtenParameter != null) && (nachrichtenParameter.getType() != null)) {
            final Integer groesse = nachrichtenParameter.getGroesse();
            final FlaecheFlaechenartJson flaechenart = nachrichtenParameter.getFlaechenart();
            final FlaecheAnschlussgradJson anschlussgrad = nachrichtenParameter.getAnschlussgrad();
            final Boolean benachrichtigt = nachrichtenParameter.getBenachrichtigt();
            final boolean accepted = NachrichtParameterJson.Type.CHANGED.equals(nachrichtenParameter.getType());
            final AenderungsanfrageUtils.Status status = nachrichtenParameter.getStatus();
            if (status != null) {
                switch (status) {
                    case CLOSED: {
                        text = "Die Bearbeitung wurde durch '" + nachrichtJson.getAbsender() + "' gesperrt.";
                    }
                    break;
                    case NONE: {     // FINISHED
                        text = "Die Bearbeitung wurde von '" + nachrichtJson.getAbsender() + "' abgeschlossen.";
                    }
                    break;
                    case PROCESSING: {
                        text = "Die Bearbeitung wurde von '" + nachrichtJson.getAbsender() + "' aufgenommen.";
                    }
                    break;
                    case PENDING: {
                        text = "Es wurden neue Änderungen eingereicht.";
                    }
                    break;
                    default: {
                        text = null; // unreachable
                    }
                }
            } else if (groesse != null) {
                text = String.format(
                        "Die Änderung der Größe der Fläche '%s' auf %dm² wurde von '%s' %s.",
                        nachrichtenParameter.getFlaeche(),
                        groesse,
                        nachrichtJson.getAbsender(),
                        accepted ? "angenommen" : "abgelehnt");
            } else if (flaechenart != null) {
                text = String.format(
                        "Die Änderung der Flächenart der Fläche '%s' auf '%s' wurde von '%s' %s.",
                        nachrichtenParameter.getFlaeche(),
                        flaechenart.getArt(),
                        nachrichtJson.getAbsender(),
                        accepted ? "angenommen" : "abgelehnt");
            } else if (anschlussgrad != null) {
                text = String.format(
                        "Die Änderung des Anschlussgrads der Fläche '%s' auf '%s' wurde von '%s' %s.",
                        nachrichtenParameter.getFlaeche(),
                        anschlussgrad.getGrad(),
                        nachrichtJson.getAbsender(),
                        accepted ? "angenommen" : "abgelehnt");
            } else if (benachrichtigt != null) {
                text = String.format(
                        "Eine Änderungs-Benachrichtigung von '%s' wurde %s.",
                        nachrichtJson.getAbsender(),
                        benachrichtigt ? "versandt" : "angefordet");
            } else {
                text = null;
            }
        } else {
            text = nachricht;
        }
        return text;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel3 = new javax.swing.JPanel();
        lblLeft = new javax.swing.JLabel();
        lblCenter = new javax.swing.JLabel();
        lblRight = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(100, 0),
                new java.awt.Dimension(100, 0),
                new java.awt.Dimension(100, 32767));
        roundedPanel1 = new de.cismet.tools.gui.RoundedPanel();
        jPanel2 = new javax.swing.JPanel();
        jTextArea1 = new javax.swing.JTextArea();
        pnlAnhang = new javax.swing.JPanel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(100, 0),
                new java.awt.Dimension(100, 0),
                new java.awt.Dimension(50, 32767));

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        jPanel3.setOpaque(false);
        jPanel3.setLayout(new java.awt.GridLayout(1, 0));

        lblLeft.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        org.openide.awt.Mnemonics.setLocalizedText(
            lblLeft,
            org.openide.util.NbBundle.getMessage(
                AenderungsanfrageNachrichtPanel.class,
                "AenderungsanfrageNachrichtPanel.lblLeft.text")); // NOI18N
        jPanel3.add(lblLeft);

        lblCenter.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            lblCenter,
            org.openide.util.NbBundle.getMessage(
                AenderungsanfrageNachrichtPanel.class,
                "AenderungsanfrageNachrichtPanel.lblCenter.text")); // NOI18N
        jPanel3.add(lblCenter);

        lblRight.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(
            lblRight,
            org.openide.util.NbBundle.getMessage(
                AenderungsanfrageNachrichtPanel.class,
                "AenderungsanfrageNachrichtPanel.lblRight.text")); // NOI18N
        jPanel3.add(lblRight);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 2, 10);
        add(jPanel3, gridBagConstraints);

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel4.setOpaque(false);
        jPanel4.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel4.add(filler1, gridBagConstraints);

        roundedPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jTextArea1.setEditable(false);
        jTextArea1.setLineWrap(true);
        jTextArea1.setText(org.openide.util.NbBundle.getMessage(
                AenderungsanfrageNachrichtPanel.class,
                "AenderungsanfrageNachrichtPanel.jTextArea1.text")); // NOI18N
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(jTextArea1, gridBagConstraints);

        pnlAnhang.setOpaque(false);
        pnlAnhang.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        pnlAnhang.setLayout(new WrapLayout(WrapLayout.RIGHT));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel2.add(pnlAnhang, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        roundedPanel1.add(jPanel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel4.add(roundedPanel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel4.add(filler2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jPanel4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        add(jPanel1, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class AnhangLink extends JXHyperlink {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new AnhangLink object.
         *
         * @param  nachrichtAnhang  DOCUMENT ME!
         */
        public AnhangLink(final NachrichtAnhangJson nachrichtAnhang) {
            super();
            setContentAreaFilled(false);
            setFocusPainted(false);
            setIcon(new ImageIcon(getClass().getResource("/de/cismet/verdis/res/download.png")));
            setText(nachrichtAnhang.getName());

            addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(final ActionEvent evt) {
                        try {
                            if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(
                                            AenderungsanfrageNachrichtPanel.this)) {
                                final String jobname = DownloadManagerDialog.getInstance().getJobName();
                                final String filename = nachrichtAnhang.getName();
                                final Download download = new ByteArrayActionDownload(
                                        VerdisConstants.DOMAIN,
                                        DownloadChangeRequestAnhangServerAction.TASK_NAME,
                                        nachrichtAnhang.toJson(),
                                        null,
                                        "Anhang",
                                        jobname,
                                        filename,
                                        filename.substring(filename.lastIndexOf(".")),
                                        ConnectionContext.createDeprecated());

                                DownloadManager.instance().add(download);
                            }
                        } catch (final Exception ex) {
                            LOG.error(ex, ex);
                        }
                    }
                });
        }
    }
}
