/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis.gui;

import org.apache.log4j.Logger;

import java.util.Collection;

import javax.swing.DefaultListModel;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class KassenzeichenListPanel extends javax.swing.JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(KassenzeichenListPanel.class);

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList lstKassenzeichen;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form KassenzeichenGeomSearchPanel.
     */
    public KassenzeichenListPanel() {
        initComponents();
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled); // To change body of generated methods, choose Tools | Templates.
        jButton1.setEnabled(enabled);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane2 = new javax.swing.JScrollPane();
        lstKassenzeichen = new javax.swing.JList();
        jButton1 = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();

        setLayout(new java.awt.GridBagLayout());

        lstKassenzeichen.setModel(new DefaultListModel());
        lstKassenzeichen.setEnabled(false);
        lstKassenzeichen.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lstKassenzeichenMouseClicked(evt);
                }
            });
        lstKassenzeichen.addListSelectionListener(new javax.swing.event.ListSelectionListener() {

                @Override
                public void valueChanged(final javax.swing.event.ListSelectionEvent evt) {
                    lstKassenzeichenValueChanged(evt);
                }
            });
        jScrollPane2.setViewportView(lstKassenzeichen);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        add(jScrollPane2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton1,
            org.openide.util.NbBundle.getMessage(KassenzeichenListPanel.class, "KassenzeichenListPanel.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(jButton1, gridBagConstraints);

        jProgressBar1.setIndeterminate(true);
        jProgressBar1.setString(org.openide.util.NbBundle.getMessage(
                KassenzeichenListPanel.class,
                "KassenzeichenListPanel.jProgressBar1.string")); // NOI18N
        jProgressBar1.setStringPainted(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(jProgressBar1, gridBagConstraints);
    }                                                            // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstKassenzeichenMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lstKassenzeichenMouseClicked
        if (jButton1.isEnabled() && (evt.getClickCount() == 2)) {
            if (lstKassenzeichen.getSelectedValue() != null) {
                gotoSelectedKassenzeichen();
            }
        }
    }                                                                                //GEN-LAST:event_lstKassenzeichenMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstKassenzeichenValueChanged(final javax.swing.event.ListSelectionEvent evt) { //GEN-FIRST:event_lstKassenzeichenValueChanged
        jButton1.setEnabled(!lstKassenzeichen.getSelectionModel().isSelectionEmpty());
    }                                                                                           //GEN-LAST:event_lstKassenzeichenValueChanged

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton1ActionPerformed
        gotoSelectedKassenzeichen();
    }                                                                            //GEN-LAST:event_jButton1ActionPerformed

    /**
     * DOCUMENT ME!
     */
    public void searchStarted() {
        setKassenzeichenList(null);
        jProgressBar1.setString("Kassenzeichen werden gesucht...");
        jProgressBar1.setIndeterminate(true);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kassenzeichenNummern  DOCUMENT ME!
     */
    public void searchFinished(final Collection<Integer> kassenzeichenNummern) {
        setKassenzeichenList(kassenzeichenNummern);
        jProgressBar1.setIndeterminate(false);
        jProgressBar1.setValue(jProgressBar1.getMaximum());
        if ((kassenzeichenNummern != null) && (kassenzeichenNummern.size() > 0)) {
            jProgressBar1.setString(kassenzeichenNummern.size() + " Kassenzeichen gefunden");
        } else {
            jProgressBar1.setString("keine Kassenzeichen gefunden");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  message  DOCUMENT ME!
     */
    public void searchFailed(final String message) {
        setKassenzeichenList(null);
        jProgressBar1.setIndeterminate(false);
        jProgressBar1.setValue(0);
        jProgressBar1.setString("Fehler: " + message);
    }

    /**
     * DOCUMENT ME!
     */
    private void gotoSelectedKassenzeichen() {
        final int kassenzeichennummer = (Integer)lstKassenzeichen.getSelectedValue();
        Main.getCurrentInstance().getKzPanel().gotoKassenzeichen(Integer.toString(kassenzeichennummer));
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kassenzeichenList  DOCUMENT ME!
     */
    private void setKassenzeichenList(final Collection<Integer> kassenzeichenList) {
        final DefaultListModel model = (DefaultListModel)lstKassenzeichen.getModel();
        model.removeAllElements();
        if ((kassenzeichenList != null) && !kassenzeichenList.isEmpty()) {
            for (final int id : kassenzeichenList) {
                model.addElement(id);
            }
            lstKassenzeichen.setEnabled(true);
            jButton1.setEnabled(true);
        } else {
            jButton1.setEnabled(false);
            lstKassenzeichen.setEnabled(false);
        }
    }
}