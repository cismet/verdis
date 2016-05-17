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
package de.cismet.verdis.gui.befreiungerlaubnis;

import java.awt.Frame;

import java.sql.Date;

import java.util.List;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;

import de.cismet.cids.editors.DefaultBindableReferenceCombo;

import de.cismet.validation.*;

import de.cismet.validation.display.EmbeddedValidatorDisplay;

import de.cismet.validation.validator.AggregatedValidator;
import de.cismet.validation.validator.BindingValidator;

import de.cismet.verdis.CidsAppBackend;

import de.cismet.verdis.commons.constants.BefreiungerlaubnisPropertyConstants;
import de.cismet.verdis.commons.constants.VerdisMetaClassConstants;

import de.cismet.verdis.gui.converter.DateToStringConverter;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class NewBefreiungerlaubnisDialog extends javax.swing.JDialog implements CidsBeanStore {

    //~ Instance fields --------------------------------------------------------

    private CidsBean cidsBean;
    private final List<CidsBean> cidsBeanList;
    private final String backupAktenzeichen;
    private final Date backupGueltigBis;
    private final Date backupAntragVom;
    private final CidsBean backupNutzung;
    private boolean go = false;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnGo;
    private javax.swing.JComboBox<String> cboNutzung;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField txtAktenzeichen;
    private javax.swing.JTextField txtAntragVom;
    private javax.swing.JTextField txtGueltigBis;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BEDialog object.
     *
     * @param  owner   DOCUMENT ME!
     * @param  beBean  DOCUMENT ME!
     */
    public NewBefreiungerlaubnisDialog(final Frame owner, final CidsBean beBean) {
        this(owner, beBean, null);
    }

    /**
     * Creates a new BEDialog object.
     *
     * @param  owner     DOCUMENT ME!
     * @param  cidsBean  DOCUMENT ME!
     * @param  cidsList  DOCUMENT ME!
     */
    public NewBefreiungerlaubnisDialog(final Frame owner, final CidsBean cidsBean, final List<CidsBean> cidsList) {
        super(owner);

        this.cidsBeanList = cidsList;

        if (cidsBean != null) {
            backupAktenzeichen = (String)cidsBean.getProperty(BefreiungerlaubnisPropertyConstants.PROP__AKTENZEICHEN);
            backupGueltigBis = (Date)cidsBean.getProperty(BefreiungerlaubnisPropertyConstants.PROP__GUELTIG_BIS);
            backupAntragVom = (Date)cidsBean.getProperty(BefreiungerlaubnisPropertyConstants.PROP__ANTRAG_VOM);
            backupNutzung = (CidsBean)cidsBean.getProperty(BefreiungerlaubnisPropertyConstants.PROP__NUTZUNG);
        } else {
            backupAktenzeichen = null;
            backupGueltigBis = null;
            backupAntragVom = null;
            backupNutzung = null;
        }

        initComponents();
        getRootPane().setDefaultButton(btnGo);

        setCidsBean(cidsBean);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void initValidators() {
        ValidatorHelper.removeAllNoBindingValidatorFromDisplay(txtAktenzeichen);
        ValidatorHelper.removeAllNoBindingValidatorFromDisplay(txtGueltigBis);
        ValidatorHelper.removeAllNoBindingValidatorFromDisplay(txtAntragVom);

        final AggregatedValidator aggVal = new AggregatedValidator();
        aggVal.add(new BindingValidator(
                bindingGroup.getBinding(BefreiungerlaubnisPropertyConstants.PROP__AKTENZEICHEN)).attachDisplay(
                EmbeddedValidatorDisplay.getEmbeddedDisplayFor(txtAktenzeichen)));
        aggVal.add(new BindingValidator(
                bindingGroup.getBinding(BefreiungerlaubnisPropertyConstants.PROP__GUELTIG_BIS)).attachDisplay(
                EmbeddedValidatorDisplay.getEmbeddedDisplayFor(txtGueltigBis)));
        aggVal.add(new BindingValidator(bindingGroup.getBinding(BefreiungerlaubnisPropertyConstants.PROP__ANTRAG_VOM))
                    .attachDisplay(
                        EmbeddedValidatorDisplay.getEmbeddedDisplayFor(txtAntragVom)));
        aggVal.add(BefreiungerlaubnisTable.createValidatorAktenzeichen(cidsBean).attachDisplay(
                EmbeddedValidatorDisplay.getEmbeddedDisplayFor(txtAktenzeichen)));
        aggVal.add(BefreiungerlaubnisTable.createValidatorGueltigbis(cidsBean).attachDisplay(
                EmbeddedValidatorDisplay.getEmbeddedDisplayFor(txtGueltigBis)));
        aggVal.add(BefreiungerlaubnisTable.createValidatorAntragVom(cidsBean).attachDisplay(
                EmbeddedValidatorDisplay.getEmbeddedDisplayFor(txtAntragVom)));
        aggVal.addListener(new ValidatorListener() {

                @Override
                public void stateChanged(final Validator source, final ValidatorState state) {
                    btnGo.setEnabled(state.isValid());
                }
            });
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtAktenzeichen = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtAntragVom = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtGueltigBis = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        cboNutzung = new DefaultBindableReferenceCombo();
        jPanel2 = new javax.swing.JPanel();
        btnCancel = new javax.swing.JButton();
        btnGo = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(
                NewBefreiungerlaubnisDialog.class,
                "NewBefreiungerlaubnisDialog.title")); // NOI18N
        setModal(true);
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText(org.openide.util.NbBundle.getMessage(
                NewBefreiungerlaubnisDialog.class,
                "NewBefreiungerlaubnisDialog.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jLabel1, gridBagConstraints);

        txtAktenzeichen.setMinimumSize(new java.awt.Dimension(120, 28));
        txtAktenzeichen.setPreferredSize(new java.awt.Dimension(120, 28));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.aktenzeichen}"),
                txtAktenzeichen,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                de.cismet.verdis.commons.constants.BefreiungerlaubnisPropertyConstants.PROP__AKTENZEICHEN);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(txtAktenzeichen, gridBagConstraints);

        jLabel3.setText(org.openide.util.NbBundle.getMessage(
                NewBefreiungerlaubnisDialog.class,
                "NewBefreiungerlaubnisDialog.jLabel3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jLabel3, gridBagConstraints);

        txtAntragVom.setMinimumSize(new java.awt.Dimension(100, 28));
        txtAntragVom.setPreferredSize(new java.awt.Dimension(100, 28));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.antrag_vom}"),
                txtAntragVom,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                de.cismet.verdis.commons.constants.BefreiungerlaubnisPropertyConstants.PROP__ANTRAG_VOM);
        binding.setConverter(new DateToStringConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(txtAntragVom, gridBagConstraints);

        jLabel2.setText(org.openide.util.NbBundle.getMessage(
                NewBefreiungerlaubnisDialog.class,
                "NewBefreiungerlaubnisDialog.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jLabel2, gridBagConstraints);

        txtGueltigBis.setMinimumSize(new java.awt.Dimension(100, 28));
        txtGueltigBis.setPreferredSize(new java.awt.Dimension(100, 28));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.gueltig_bis}"),
                txtGueltigBis,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                de.cismet.verdis.commons.constants.BefreiungerlaubnisPropertyConstants.PROP__GUELTIG_BIS);
        binding.setConverter(new DateToStringConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(txtGueltigBis, gridBagConstraints);

        jLabel4.setText(org.openide.util.NbBundle.getMessage(
                NewBefreiungerlaubnisDialog.class,
                "NewBefreiungerlaubnisDialog.jLabel4.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jLabel4, gridBagConstraints);

        cboNutzung.setMinimumSize(new java.awt.Dimension(100, 28));
        cboNutzung.setPreferredSize(new java.awt.Dimension(100, 28));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.nutzung}"),
                cboNutzung,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(cboNutzung, gridBagConstraints);
        ((DefaultBindableReferenceCombo)cboNutzung).setMetaClass(CidsAppBackend.getInstance().getVerdisMetaClass(
                VerdisMetaClassConstants.MC_BEFREIUNGERLAUBNIS_NUTZUNG));

        jPanel2.setLayout(new java.awt.GridLayout(1, 0, 10, 0));

        btnCancel.setText(org.openide.util.NbBundle.getMessage(
                NewBefreiungerlaubnisDialog.class,
                "NewBefreiungerlaubnisDialog.btnCancel.text")); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnCancelActionPerformed(evt);
                }
            });
        jPanel2.add(btnCancel);

        btnGo.setText((cidsBeanList == null) ? "übernehmen" : "hinzufügen");
        btnGo.setEnabled(false);
        btnGo.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnGoActionPerformed(evt);
                }
            });
        jPanel2.add(btnGo);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        jPanel1.add(jPanel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(jPanel1, gridBagConstraints);

        bindingGroup.bind();

        pack();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnGoActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnGoActionPerformed
        go = true;
        dispose();
    }                                                                         //GEN-LAST:event_btnGoActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnCancelActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnCancelActionPerformed
        dispose();
    }                                                                             //GEN-LAST:event_btnCancelActionPerformed

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public final void setCidsBean(final CidsBean cidsBean) {
        bindingGroup.unbind();
        this.cidsBean = cidsBean;
        initValidators();
        bindingGroup.bind();
    }

    @Override
    public void dispose() {
        if (go) {
            if (cidsBeanList != null) {
                cidsBeanList.add(cidsBean);
            }
        } else {
            try {
                cidsBean.setProperty(BefreiungerlaubnisPropertyConstants.PROP__AKTENZEICHEN, backupAktenzeichen);
                cidsBean.setProperty(BefreiungerlaubnisPropertyConstants.PROP__ANTRAG_VOM, backupAntragVom);
                cidsBean.setProperty(BefreiungerlaubnisPropertyConstants.PROP__GUELTIG_BIS, backupGueltigBis);
                cidsBean.setProperty(BefreiungerlaubnisPropertyConstants.PROP__NUTZUNG, backupNutzung);
            } catch (Exception ex) {
                // LOG.error("error setting backup properties", ex);
            }
        }
        super.dispose();
    }
}
