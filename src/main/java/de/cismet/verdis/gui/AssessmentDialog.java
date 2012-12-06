/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.verdis.gui;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.verdis.CidsAppBackend;

import de.cismet.verdis.commons.constants.KassenzeichenPropertyConstants;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class AssessmentDialog extends javax.swing.JDialog {

    //~ Static fields/initializers ---------------------------------------------

    public static int RETURN_CANCEL = 1;
    public static int RETURN_WITHOUT_ASSESSEMENT = 2;
    public static int RETURN_WITH_ASSESSEMENT = 3;
    private static SimpleDateFormat DATEFORMAT_FULL = new SimpleDateFormat("dd.MM.yyyy");
    private static SimpleDateFormat DATEFORMAT_MONTH = new SimpleDateFormat("MM");
    private static SimpleDateFormat DATEFORMAT_YEAR = new SimpleDateFormat("yyyy");

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssessmentDialog.class);

    //~ Instance fields --------------------------------------------------------

    private int returnType = 0;
    private String zettelHtml = "";
    private Map<String, Double> oldSchluesselSummeMap;
    private Map<String, Double> newSchluesselSummeMap;
    private Collection<String> bezeichners;
    private Date datum = null;
    private Date veranlagungsdatum = null;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdCancel;
    private javax.swing.JButton cmdWithAssessement;
    private javax.swing.JButton cmdWithoutAssessement;
    private de.cismet.cids.editors.DefaultBindableDateChooser defaultBindableDateChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblVeranlagungszettel;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form AssessmentDialog.
     *
     * @param  parent  DOCUMENT ME!
     * @param  modal   DOCUMENT ME!
     */
    public AssessmentDialog(final java.awt.Frame parent, final boolean modal) {
        super(parent, modal);
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

        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel6 = new javax.swing.JPanel();
        lblVeranlagungszettel = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        cmdWithAssessement = new javax.swing.JButton();
        cmdWithoutAssessement = new javax.swing.JButton();
        cmdCancel = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        defaultBindableDateChooser1 = new de.cismet.cids.editors.DefaultBindableDateChooser();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(AssessmentDialog.class, "AssessmentDialog.title")); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel5.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(AssessmentDialog.class, "AssessmentDialog.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        jPanel5.add(jLabel1, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jScrollPane2.setMinimumSize(new java.awt.Dimension(200, 400));
        jScrollPane2.setPreferredSize(new java.awt.Dimension(200, 400));

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setLayout(new java.awt.GridBagLayout());

        lblVeranlagungszettel.setBackground(new java.awt.Color(255, 255, 255));
        lblVeranlagungszettel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            lblVeranlagungszettel,
            org.openide.util.NbBundle.getMessage(
                AssessmentDialog.class,
                "AssessmentDialog.lblVeranlagungszettel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel6.add(lblVeranlagungszettel, gridBagConstraints);

        jScrollPane2.setViewportView(jPanel6);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(jScrollPane2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        jPanel5.add(jPanel2, gridBagConstraints);

        jPanel3.setLayout(new java.awt.GridBagLayout());

        cmdWithAssessement.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/ok.png")));                               // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            cmdWithAssessement,
            org.openide.util.NbBundle.getMessage(AssessmentDialog.class, "AssessmentDialog.cmdWithAssessement.text")); // NOI18N
        cmdWithAssessement.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdWithAssessementActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        jPanel3.add(cmdWithAssessement, gridBagConstraints);

        cmdWithoutAssessement.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/ok_without_assessment.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            cmdWithoutAssessement,
            org.openide.util.NbBundle.getMessage(
                AssessmentDialog.class,
                "AssessmentDialog.cmdWithoutAssessement.text"));                                            // NOI18N
        cmdWithoutAssessement.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdWithoutAssessementActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        jPanel3.add(cmdWithoutAssessement, gridBagConstraints);

        cmdCancel.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/cancel.png")));                  // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            cmdCancel,
            org.openide.util.NbBundle.getMessage(AssessmentDialog.class, "AssessmentDialog.cmdCancel.text")); // NOI18N
        cmdCancel.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdCancelActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        jPanel3.add(cmdCancel, gridBagConstraints);

        final javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                21,
                Short.MAX_VALUE));
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                34,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(jPanel4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        jPanel5.add(jPanel3, gridBagConstraints);

        defaultBindableDateChooser1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    defaultBindableDateChooser1ActionPerformed(evt);
                }
            });
        defaultBindableDateChooser1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

                @Override
                public void propertyChange(final java.beans.PropertyChangeEvent evt) {
                    defaultBindableDateChooser1PropertyChange(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        jPanel5.add(defaultBindableDateChooser1, gridBagConstraints);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel2,
            org.openide.util.NbBundle.getMessage(AssessmentDialog.class, "AssessmentDialog.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
        jPanel5.add(jLabel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(jPanel5, gridBagConstraints);

        pack();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Date getVeranlagungsdatum() {
        return veranlagungsdatum;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  veranlagungsdatum  DOCUMENT ME!
     */
    public void setVeranlagungsdatum(final Date veranlagungsdatum) {
        this.veranlagungsdatum = veranlagungsdatum;
        defaultBindableDateChooser1.setDate(veranlagungsdatum);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Date getDatum() {
        return datum;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  datum  DOCUMENT ME!
     */
    public void setDatum(final Date datum) {
        this.datum = datum;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Map<String, Double> getOldSchluesselSummeMap() {
        return oldSchluesselSummeMap;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  oldSchluesselSummeMap  DOCUMENT ME!
     */
    public void setOldSchluesselSummeMap(final Map<String, Double> oldSchluesselSummeMap) {
        this.oldSchluesselSummeMap = oldSchluesselSummeMap;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Map<String, Double> getNewSchluesselSummeMap() {
        return newSchluesselSummeMap;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  newSchluesselSummeMap  DOCUMENT ME!
     */
    public void setNewSchluesselSummeMap(final Map<String, Double> newSchluesselSummeMap) {
        this.newSchluesselSummeMap = newSchluesselSummeMap;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<String> getBezeichners() {
        return bezeichners;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  bezeichners  DOCUMENT ME!
     */
    public void setBezeichners(final Collection<String> bezeichners) {
        this.bezeichners = bezeichners;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   kassenzeichenBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String createZettelHtml(final CidsBean kassenzeichenBean) {
        if (kassenzeichenBean != null) {
            final String kassenzeichennummer = Integer.toString((Integer)kassenzeichenBean.getProperty(
                        KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER));
            final String veranlagunsZettel = (String)kassenzeichenBean.getProperty(
                    KassenzeichenPropertyConstants.PROP__VERANLAGUNGSZETTEL);
            final String alterZettel = (veranlagunsZettel != null) ? veranlagunsZettel : "";

            final StringBuilder zettelSB = new StringBuilder();
            final StringBuilder teilZettelSB = new StringBuilder();
            zettelSB.append("<html>")
                    .append("   <basefont face=\"Courier New\">")
                    .append("   <center><h1> Änderungsnotiz</h1></center>")
                    .append("   <h2>Kassenzeichen: ")
                    .append(kassenzeichennummer)
                    .append("<h2>");
            teilZettelSB.append("   <hr>")
                    .append("   <h3>Datum: ")
                    .append(DATEFORMAT_FULL.format(datum))
                    .append("</h3>")
                    .append("   <center>")
                    .append("      <table cellspacing=\"10\">")
                    .append("         <thead>")
                    .append("            <tr>")
                    .append("               <th align=\"left\">ABS</th>")
                    .append("               <th align=\"right\">alt</th>")
                    .append("               <th align=\"right\">neu</th>")
                    .append("               <th align=\"center\">Monat VA</th>")
                    .append("               <th align=\"center\">Jahr VA</th>")
                    .append("            </tr>")
                    .append("         </thead>")
                    .append("         <tbody>");
            for (final String bezeichner : bezeichners) {
                final double oldSumme = oldSchluesselSummeMap.get(bezeichner);
                final double newSumme = newSchluesselSummeMap.get(bezeichner);
                if ((oldSumme > 0) || (newSumme > 0)) {
                    teilZettelSB.append("            <tr>")
                            .append("               <td align=\"left\">")
                            .append(bezeichner)
                            .append("</td>")
                            .append("               <td align=\"right\">")
                            .append(Double.toString(oldSumme))
                            .append("</td>")
                            .append("               <td align=\"right\">")
                            .append(Double.toString(newSumme))
                            .append("</td>")
                            .append("               <td align=\"center\">")
                            .append(DATEFORMAT_MONTH.format(veranlagungsdatum))
                            .append("</td>")
                            .append("               <td align=\"center\">")
                            .append(DATEFORMAT_YEAR.format(veranlagungsdatum))
                            .append("</td>")
                            .append("            </tr>");
                }
            }
            teilZettelSB.append("         </tbody>").append("      </table>");
            teilZettelSB.append(alterZettel);
            zettelSB.append(teilZettelSB.toString()).append("   </center>").append("</html>");

            this.zettelHtml = teilZettelSB.toString();

            return zettelSB.toString();
        } else {
            return null;
        }
    }
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getReturnType() {
        return returnType;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getZettelHtml() {
        return zettelHtml;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdCancelActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdCancelActionPerformed
        returnType = RETURN_CANCEL;
        dispose();
    }                                                                             //GEN-LAST:event_cmdCancelActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdWithoutAssessementActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdWithoutAssessementActionPerformed
        returnType = RETURN_WITHOUT_ASSESSEMENT;
        dispose();
    }                                                                                         //GEN-LAST:event_cmdWithoutAssessementActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdWithAssessementActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdWithAssessementActionPerformed
        returnType = RETURN_WITH_ASSESSEMENT;
        dispose();
    }                                                                                      //GEN-LAST:event_cmdWithAssessementActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void defaultBindableDateChooser1PropertyChange(final java.beans.PropertyChangeEvent evt) { //GEN-FIRST:event_defaultBindableDateChooser1PropertyChange
    }                                                                                                  //GEN-LAST:event_defaultBindableDateChooser1PropertyChange

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void defaultBindableDateChooser1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_defaultBindableDateChooser1ActionPerformed
        veranlagungsdatum = defaultBindableDateChooser1.getDate();
        refreshZettel();
    }                                                                                               //GEN-LAST:event_defaultBindableDateChooser1ActionPerformed

    @Override
    public void setVisible(final boolean b) {
        refreshZettel();
        super.setVisible(b);
    }

    /**
     * DOCUMENT ME!
     */
    private void refreshZettel() {
        lblVeranlagungszettel.setText(createZettelHtml(CidsAppBackend.getInstance().getCidsBean()));
        jScrollPane2.revalidate();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  the command line arguments
     */
    public static void main(final String[] args) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (final javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AssessmentDialog.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AssessmentDialog.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AssessmentDialog.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AssessmentDialog.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    final AssessmentDialog dialog = new AssessmentDialog(new javax.swing.JFrame(), true);
                    dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                            @Override
                            public void windowClosing(final java.awt.event.WindowEvent e) {
                                System.exit(0);
                            }
                        });
                    dialog.setVisible(true);
                }
            });
    }
}