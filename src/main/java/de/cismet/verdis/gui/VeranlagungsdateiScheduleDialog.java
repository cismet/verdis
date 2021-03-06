/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis.gui;

import java.awt.Frame;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class VeranlagungsdateiScheduleDialog extends javax.swing.JDialog {

    //~ Static fields/initializers ---------------------------------------------

    private static VeranlagungsdateiScheduleDialog INSTANCE;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private de.cismet.verdis.gui.VeranlagungsdateiSchedulePanel veranlagungsdateiSchedulePanel1;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form AbgabedateiScheduleDownload.
     *
     * @param  frame  DOCUMENT ME!
     */
    private VeranlagungsdateiScheduleDialog(final Frame frame) {
        super(frame, false);
        initComponents();
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

        jButton1 = new javax.swing.JButton();
        veranlagungsdateiSchedulePanel1 = new de.cismet.verdis.gui.VeranlagungsdateiSchedulePanel();

        setTitle(org.openide.util.NbBundle.getMessage(
                VeranlagungsdateiScheduleDialog.class,
                "VeranlagungsdateiScheduleDialog.title")); // NOI18N
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton1,
            org.openide.util.NbBundle.getMessage(
                VeranlagungsdateiScheduleDialog.class,
                "VeranlagungsdateiScheduleDialog.jButton1.text")); // NOI18N
        jButton1.setMaximumSize(new java.awt.Dimension(98, 28));
        jButton1.setMinimumSize(new java.awt.Dimension(98, 28));
        jButton1.setPreferredSize(new java.awt.Dimension(98, 28));
        jButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jButton1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(veranlagungsdateiSchedulePanel1, gridBagConstraints);

        pack();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static VeranlagungsdateiScheduleDialog getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new VeranlagungsdateiScheduleDialog(StaticSwingTools.getParentFrame(Main.getInstance()));
            INSTANCE.veranlagungsdateiSchedulePanel1.init();
        }
        return INSTANCE;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton1ActionPerformed
        setVisible(false);
    }                                                                            //GEN-LAST:event_jButton1ActionPerformed
}
