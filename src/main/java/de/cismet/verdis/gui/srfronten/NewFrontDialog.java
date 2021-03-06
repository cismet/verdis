/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis.gui.srfronten;

import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class NewFrontDialog extends javax.swing.JDialog {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(NewFrontDialog.class);

    public static final int RET_OK = 1;
    public static final int RET_CANCEL = 0;

    //~ Instance fields --------------------------------------------------------

    private int returnStatus = RET_CANCEL;

    private boolean querverweiseChecked = false;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdCancel;
    private javax.swing.JButton cmdOK;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form NewFlaecheDialog.
     */
    public NewFrontDialog() {
        super((JFrame)null, true);

        initComponents();

        getRootPane().setDefaultButton(cmdOK);
        StaticSwingTools.doClickButtonOnKeyStroke(cmdOK, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), getRootPane());
        StaticSwingTools.doClickButtonOnKeyStroke(
            cmdCancel,
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            getRootPane());
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
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        cmdOK = new javax.swing.JButton();
        cmdCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(NewFrontDialog.class, "NewFrontDialog.title")); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel3.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jCheckBox1,
            org.openide.util.NbBundle.getMessage(NewFrontDialog.class, "NewFrontDialog.jCheckBox1.text")); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${querverweiseChecked}"),
                jCheckBox1,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${querverweiseExists}"),
                jCheckBox1,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 10, 0);
        jPanel3.add(jCheckBox1, gridBagConstraints);

        jLabel2.setIcon(UIManager.getIcon("OptionPane.questionIcon"));
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel2,
            org.openide.util.NbBundle.getMessage(NewFrontDialog.class, "NewFrontDialog.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 15);
        jPanel3.add(jLabel2, gridBagConstraints);

        jPanel1.add(jPanel3, java.awt.BorderLayout.CENTER);

        jPanel2.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(jPanel4, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            cmdOK,
            org.openide.util.NbBundle.getMessage(NewFrontDialog.class, "NewFrontDialog.cmdOK.text")); // NOI18N
        cmdOK.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdOKActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel2.add(cmdOK, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            cmdCancel,
            org.openide.util.NbBundle.getMessage(NewFrontDialog.class, "NewFrontDialog.cmdCancel.text")); // NOI18N
        cmdCancel.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdCancelActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel2.add(cmdCancel, gridBagConstraints);

        jPanel1.add(jPanel2, java.awt.BorderLayout.SOUTH);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 15, 15, 15);
        getContentPane().add(jPanel1, gridBagConstraints);

        bindingGroup.bind();

        pack();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdOKActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdOKActionPerformed
        doClose(RET_OK);
    }                                                                         //GEN-LAST:event_cmdOKActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdCancelActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdCancelActionPerformed
        doClose(RET_CANCEL);
    }                                                                             //GEN-LAST:event_cmdCancelActionPerformed

    @Override
    public void setVisible(final boolean b) {
        super.setVisible(b);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  retStatus  DOCUMENT ME!
     */
    private void doClose(final int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isQuerverweiseChecked() {
        return querverweiseChecked;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getReturnStatus() {
        return returnStatus;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  querverweiseChecked  DOCUMENT ME!
     */
    public void setQuerverweiseChecked(final boolean querverweiseChecked) {
        this.querverweiseChecked = querverweiseChecked;
    }
}
