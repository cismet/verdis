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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import java.text.DateFormat;

import java.util.Arrays;
import java.util.Date;

import de.cismet.verdis.server.json.aenderungsanfrage.NachrichtAnhangJson;
import de.cismet.verdis.server.json.aenderungsanfrage.NachrichtJson;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class AenderungsanfrageNachrichtPanel extends javax.swing.JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final Color COLOR_LEFT = new Color(110, 204, 214);
    private static final Color COLOR_CENTER = new Color(238, 238, 238);
    private static final Color COLOR_RIGHT = new Color(252, 221, 153);
    private static final double WIDTH_FACTOR = 0.7;
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
    private javax.swing.Box.Filler filler3;
    private org.jdesktop.swingx.JXHyperlink hlkAnhang;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel lblCenter;
    private javax.swing.JLabel lblLeft;
    private javax.swing.JLabel lblRight;
    private de.cismet.tools.gui.RoundedPanel roundedPanel1;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form AenderungsanfrageNachrichtPanel.
     */
    public AenderungsanfrageNachrichtPanel() {
        this(new NachrichtJson(
                NachrichtJson.Typ.CLERK,
                new Date(),
                "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.",
                "Dirk Steinbacher",
                Arrays.asList(new NachrichtAnhangJson("test.pdf", "xxx-xxx-xxx"))));
    }

    /**
     * Creates a new AenderungsanfrageNachrichtPanel object.
     *
     * @param  nachrichtJson  orientation DOCUMENT ME!
     */
    public AenderungsanfrageNachrichtPanel(final NachrichtJson nachrichtJson) {
        final AenderungsanfrageNachrichtPanel.Orientation orientation;
        if (NachrichtJson.Typ.CLERK.equals(nachrichtJson.getTyp())) {
            orientation = AenderungsanfrageNachrichtPanel.Orientation.LEFT;
        } else if (NachrichtJson.Typ.CITIZEN.equals(nachrichtJson.getTyp())) {
            orientation = AenderungsanfrageNachrichtPanel.Orientation.RIGHT;
        } else {
            orientation = AenderungsanfrageNachrichtPanel.Orientation.CENTER;
        }
        final String absender = nachrichtJson.getAbsender();
        final Date timestamp = nachrichtJson.getTimestamp();
        final String text = nachrichtJson.getNachricht();
        final String anhang = (nachrichtJson.getAnhang() != null) ? nachrichtJson.getAnhang().get(0).getName() : null;

        initComponents();
        jTextArea1.setText(text);
        if (anhang != null) {
            hlkAnhang.setText(anhang);
        } else {
            hlkAnhang.setVisible(false);
        }
        roundedPanel1.setAlpha(255);

        lblCenter.setText(DATE_FORMAT.format(timestamp));
        if (Orientation.LEFT.equals(orientation)) {
            jPanel1.remove(filler1);
            lblLeft.setText(absender);
            lblRight.setText("");
            roundedPanel1.setBackground(COLOR_LEFT);
        } else if (Orientation.RIGHT.equals(orientation)) {
            jPanel1.remove(filler3);
            lblLeft.setText("");
            lblRight.setText(absender);
            roundedPanel1.setBackground(COLOR_RIGHT);
        } else {
            lblLeft.setText("");
            lblRight.setText("");
            roundedPanel1.setBackground(COLOR_CENTER);
        }

        addComponentListener(new ComponentAdapter() {

                @Override
                public void componentResized(final ComponentEvent e) {
                    final Dimension dFull = new Dimension((int)(getWidth() * getWidthFactor()), 1);
                    filler2.setPreferredSize(dFull);
                    filler2.setMinimumSize(dFull);
                    filler2.setMaximumSize(dFull);
                    filler2.setSize(dFull);
                }
            });
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private double getWidthFactor() {
        return WIDTH_FACTOR;
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
        roundedPanel1 = new de.cismet.tools.gui.RoundedPanel();
        jPanel2 = new javax.swing.JPanel();
        hlkAnhang = new org.jdesktop.swingx.JXHyperlink();
        jTextArea1 = new javax.swing.JTextArea();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0));
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0));

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
        add(jPanel3, gridBagConstraints);

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        roundedPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.GridBagLayout());

        hlkAnhang.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/download.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            hlkAnhang,
            org.openide.util.NbBundle.getMessage(
                AenderungsanfrageNachrichtPanel.class,
                "AenderungsanfrageNachrichtPanel.hlkAnhang.text"));                                                 // NOI18N
        hlkAnhang.setContentAreaFilled(false);
        hlkAnhang.setFocusPainted(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel2.add(hlkAnhang, gridBagConstraints);

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
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel1.add(roundedPanel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(filler1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(filler2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(filler3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel1, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents
}
