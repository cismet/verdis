/***************************************************
 *
 * cismet GmbH, Saarbruecken, Germany
 *
 *              ... and it just works.
 *
 ****************************************************/
/*
 *  Copyright (C) 2010 thorsten
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * DetailPanel.java
 *
 * Created on 24.11.2010, 20:42:43
 */
package de.cismet.verdis.gui;

import de.cismet.verdis.constants.WDSRPropertyConstants;
import com.vividsolutions.jts.geom.Geometry;
import de.cismet.cids.custom.util.BindingValidationSupport;

import edu.umd.cs.piccolo.PCanvas;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;

import de.cismet.cids.editors.DefaultBindableReferenceCombo;
import de.cismet.cids.editors.converters.SqlDateToStringConverter;
import de.cismet.validation.Validator;
import de.cismet.validation.ValidatorHelper;
import de.cismet.validation.ValidatorState;
import de.cismet.validation.ValidatorStateImpl;
import de.cismet.validation.display.EmbeddedValidatorDisplay;
import de.cismet.validation.validator.CidsBeanValidator;

import de.cismet.verdis.CidsAppBackend;
import de.cismet.verdis.EditModeListener;
import de.cismet.verdis.constants.VerdisMetaClassConstants;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class WDSRDetailsPanel extends javax.swing.JPanel implements CidsBeanStore, EditModeListener, WDSRPropertyConstants {

    //~ Instance fields --------------------------------------------------------
    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(WDSRDetailsPanel.class);
    private CidsBean frontBean = null;
    private final Validator bindingValidator;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.cismet.cismap.commons.gui.SimpleBackgroundedJPanel bpanWdsrDetails;
    private javax.swing.JComboBox cboReinigungsklasse;
    private javax.swing.JComboBox cboStrasse;
    private javax.swing.JComboBox cboWinterdienstPrio;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblBemSR;
    private javax.swing.JLabel lblBemWD;
    private javax.swing.JLabel lblErfassungsdatumDesc;
    private javax.swing.JLabel lblErfassungsdatumDesc1;
    private javax.swing.JLabel lblLaengeGR;
    private javax.swing.JLabel lblLaengeKorr;
    private javax.swing.JLabel lblLastEditorDescr;
    private javax.swing.JLabel lblNummer;
    private javax.swing.JLabel lblVeranlagungWD;
    private javax.swing.JScrollPane scpBemSR;
    private javax.swing.JScrollPane scpBemWD;
    private javax.swing.JTextField txtBearbeitetDurch;
    private javax.swing.JTextArea txtBemSR;
    private javax.swing.JTextArea txtBemWD;
    private javax.swing.JTextField txtErfassungsdatum;
    private javax.swing.JTextField txtLaengeGrafik;
    private javax.swing.JTextField txtLaengeKorrektur;
    private javax.swing.JTextField txtNummer;
    private javax.swing.JTextField txtVeranlagungSR;
    private javax.swing.JTextField txtVeranlagungWD;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------
    /**
     * Creates new form DetailPanel.
     */
    public WDSRDetailsPanel() {
        initComponents();

        bindingValidator = BindingValidationSupport.attachBindingValidationToAllTargets(bindingGroup);

        ((DefaultBindableReferenceCombo) cboStrasse).setMetaClass(CidsAppBackend.getInstance().getVerdisMetaClass(VerdisMetaClassConstants.MC_STRASSE));
        ((DefaultBindableReferenceCombo) cboReinigungsklasse).setMetaClass(CidsAppBackend.getInstance().getVerdisMetaClass(VerdisMetaClassConstants.MC_STRASSENREINIGUNG));
        ((DefaultBindableReferenceCombo) cboWinterdienstPrio).setMetaClass(CidsAppBackend.getInstance().getVerdisMetaClass(VerdisMetaClassConstants.MC_WINTERDIENST));
    }

    //~ Methods ----------------------------------------------------------------
    public Validator getValidator() {
        // nur BindingValidator notwendig, der TabellenValidator validiert schon alle beans
        return bindingValidator;
    }

    private void attachBeanValidators() {
        ValidatorHelper.removeAllNoBindingValidatorFromDisplay(txtNummer);
        getValidatorNummer(frontBean).attachDisplay(EmbeddedValidatorDisplay.getEmbeddedDisplayFor(txtNummer));
        ValidatorHelper.removeAllNoBindingValidatorFromDisplay(txtLaengeGrafik);
        getValidatorLaengeGrafik(frontBean).attachDisplay(EmbeddedValidatorDisplay.getEmbeddedDisplayFor(txtLaengeGrafik));
        ValidatorHelper.removeAllNoBindingValidatorFromDisplay(txtLaengeKorrektur);
        getValidatorLaengeKorrektur(frontBean).attachDisplay(EmbeddedValidatorDisplay.getEmbeddedDisplayFor(txtLaengeKorrektur));
        ValidatorHelper.removeAllNoBindingValidatorFromDisplay(txtErfassungsdatum);
        getValidatorDatumErfassung(frontBean).attachDisplay(EmbeddedValidatorDisplay.getEmbeddedDisplayFor(txtErfassungsdatum));
        ValidatorHelper.removeAllNoBindingValidatorFromDisplay(txtVeranlagungWD);
        getValidatorVeranlagungWD(frontBean).attachDisplay(EmbeddedValidatorDisplay.getEmbeddedDisplayFor(txtVeranlagungWD));
        ValidatorHelper.removeAllNoBindingValidatorFromDisplay(txtVeranlagungSR);
        getValidatorVeranlagungSR(frontBean).attachDisplay(EmbeddedValidatorDisplay.getEmbeddedDisplayFor(txtVeranlagungSR));
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

        bpanWdsrDetails = new de.cismet.cismap.commons.gui.SimpleBackgroundedJPanel();
        lblNummer = new javax.swing.JLabel();
        txtNummer = new javax.swing.JTextField();
        lblLaengeGR = new javax.swing.JLabel();
        txtLaengeGrafik = new javax.swing.JTextField();
        lblLaengeKorr = new javax.swing.JLabel();
        txtLaengeKorrektur = new javax.swing.JTextField();
        lblLastEditorDescr = new javax.swing.JLabel();
        cboStrasse = new DefaultBindableReferenceCombo();
        cboReinigungsklasse = new DefaultBindableReferenceCombo();
        lblErfassungsdatumDesc = new javax.swing.JLabel();
        scpBemSR = new javax.swing.JScrollPane();
        txtBemSR = new javax.swing.JTextArea();
        lblBemSR = new javax.swing.JLabel();
        lblBemWD = new javax.swing.JLabel();
        scpBemWD = new javax.swing.JScrollPane();
        txtBemWD = new javax.swing.JTextArea();
        cboWinterdienstPrio = new DefaultBindableReferenceCombo();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        txtBearbeitetDurch = new javax.swing.JTextField();
        txtErfassungsdatum = new javax.swing.JTextField();
        lblErfassungsdatumDesc1 = new javax.swing.JLabel();
        txtVeranlagungSR = new javax.swing.JTextField();
        lblVeranlagungWD = new javax.swing.JLabel();
        txtVeranlagungWD = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();

        setLayout(new java.awt.BorderLayout());

        bpanWdsrDetails.setOpaque(false);
        bpanWdsrDetails.setLayout(new java.awt.GridBagLayout());

        lblNummer.setText(org.openide.util.NbBundle.getMessage(WDSRDetailsPanel.class, "WDSRDetailsPanel.lblNummer.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 7, 2, 2);
        bpanWdsrDetails.add(lblNummer, gridBagConstraints);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.nummer}"), txtNummer, org.jdesktop.beansbinding.BeanProperty.create("text"), PROP__NUMMER);
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        bpanWdsrDetails.add(txtNummer, gridBagConstraints);

        lblLaengeGR.setText(org.openide.util.NbBundle.getMessage(WDSRDetailsPanel.class, "WDSRDetailsPanel.lblLaengeGR.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 7, 2, 2);
        bpanWdsrDetails.add(lblLaengeGR, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.laenge_grafik}"), txtLaengeGrafik, org.jdesktop.beansbinding.BeanProperty.create("text"), PROP__LAENGE_GRAFIK);
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        bpanWdsrDetails.add(txtLaengeGrafik, gridBagConstraints);

        lblLaengeKorr.setText(org.openide.util.NbBundle.getMessage(WDSRDetailsPanel.class, "WDSRDetailsPanel.lblLaengeKorr.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 7, 2, 2);
        bpanWdsrDetails.add(lblLaengeKorr, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.laenge_korrektur}"), txtLaengeKorrektur, org.jdesktop.beansbinding.BeanProperty.create("text"), PROP__LAENGE_KORREKTUR);
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        bpanWdsrDetails.add(txtLaengeKorrektur, gridBagConstraints);

        lblLastEditorDescr.setText(org.openide.util.NbBundle.getMessage(WDSRDetailsPanel.class, "WDSRDetailsPanel.lblLastEditorDescr.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 7, 2, 2);
        bpanWdsrDetails.add(lblLastEditorDescr, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.strasse}"), cboStrasse, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        bpanWdsrDetails.add(cboStrasse, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.sr_klasse_or}"), cboReinigungsklasse, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        bpanWdsrDetails.add(cboReinigungsklasse, gridBagConstraints);

        lblErfassungsdatumDesc.setText(org.openide.util.NbBundle.getMessage(WDSRDetailsPanel.class, "WDSRDetailsPanel.lblErfassungsdatumDesc.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 7, 2, 2);
        bpanWdsrDetails.add(lblErfassungsdatumDesc, gridBagConstraints);

        txtBemSR.setColumns(20);
        txtBemSR.setEditable(false);
        txtBemSR.setLineWrap(true);
        txtBemSR.setRows(2);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.satzungseintrag.sr_bem}"), txtBemSR, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        scpBemSR.setViewportView(txtBemSR);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        bpanWdsrDetails.add(scpBemSR, gridBagConstraints);

        lblBemSR.setText(org.openide.util.NbBundle.getMessage(WDSRDetailsPanel.class, "WDSRDetailsPanel.lblBemSR.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 7, 2, 2);
        bpanWdsrDetails.add(lblBemSR, gridBagConstraints);

        lblBemWD.setText(org.openide.util.NbBundle.getMessage(WDSRDetailsPanel.class, "WDSRDetailsPanel.lblBemWD.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 7, 2, 2);
        bpanWdsrDetails.add(lblBemWD, gridBagConstraints);

        txtBemWD.setColumns(20);
        txtBemWD.setEditable(false);
        txtBemWD.setLineWrap(true);
        txtBemWD.setRows(2);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.satzungseintrag.wd_bem}"), txtBemWD, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        scpBemWD.setViewportView(txtBemWD);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        bpanWdsrDetails.add(scpBemWD, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.wd_prio_or}"), cboWinterdienstPrio, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"), "veranlagung_wd");
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        bpanWdsrDetails.add(cboWinterdienstPrio, gridBagConstraints);

        jSeparator1.setMinimumSize(new java.awt.Dimension(0, 10));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        bpanWdsrDetails.add(jSeparator1, gridBagConstraints);

        jSeparator2.setMinimumSize(new java.awt.Dimension(0, 10));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        bpanWdsrDetails.add(jSeparator2, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bearbeitet_durch}"), txtBearbeitetDurch, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        bpanWdsrDetails.add(txtBearbeitetDurch, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.erfassungsdatum}"), txtErfassungsdatum, org.jdesktop.beansbinding.BeanProperty.create("text"), PROP__ERFASSUNGSDATUM);
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        binding.setConverter(new SqlDateToStringConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        bpanWdsrDetails.add(txtErfassungsdatum, gridBagConstraints);

        lblErfassungsdatumDesc1.setText(org.openide.util.NbBundle.getMessage(WDSRDetailsPanel.class, "WDSRDetailsPanel.lblErfassungsdatumDesc1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 7, 2, 2);
        bpanWdsrDetails.add(lblErfassungsdatumDesc1, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.sr_veranlagung}"), txtVeranlagungSR, org.jdesktop.beansbinding.BeanProperty.create("text"), PROP__SR_VERANLAGUNG);
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        bpanWdsrDetails.add(txtVeranlagungSR, gridBagConstraints);

        lblVeranlagungWD.setText(org.openide.util.NbBundle.getMessage(WDSRDetailsPanel.class, "WDSRDetailsPanel.lblVeranlagungWD.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 7, 2, 2);
        bpanWdsrDetails.add(lblVeranlagungWD, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.wd_veranlagung}"), txtVeranlagungWD, org.jdesktop.beansbinding.BeanProperty.create("text"), PROP__WD_VERANLAGUNG);
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        bpanWdsrDetails.add(txtVeranlagungWD, gridBagConstraints);

        jLabel1.setText(org.openide.util.NbBundle.getMessage(WDSRDetailsPanel.class, "WDSRDetailsPanel.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 7, 2, 2);
        bpanWdsrDetails.add(jLabel1, gridBagConstraints);

        jLabel2.setText(org.openide.util.NbBundle.getMessage(WDSRDetailsPanel.class, "WDSRDetailsPanel.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 7, 2, 2);
        bpanWdsrDetails.add(jLabel2, gridBagConstraints);

        jLabel3.setText(org.openide.util.NbBundle.getMessage(WDSRDetailsPanel.class, "WDSRDetailsPanel.jLabel3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 7, 2, 2);
        bpanWdsrDetails.add(jLabel3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        bpanWdsrDetails.add(jComboBox1, gridBagConstraints);

        add(bpanWdsrDetails, java.awt.BorderLayout.CENTER);

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    public Geometry getGeometry() {
        return getGeometry(getCidsBean());
    }

    public static Geometry getGeometry(final CidsBean frontBean) {
        if (frontBean != null && frontBean.getProperty(PROP__GEOMETRIE) != null) {
            return (Geometry) frontBean.getProperty(PROP__GEOMETRIE__GEO_FIELD);
        } else {
            return null;
        }
    }

    public static void setGeometry(final Geometry geom, final CidsBean cidsBean) throws Exception {
        Main.transformToDefaultCrsNeeded(geom);
        if (cidsBean.getProperty(PROP__GEOMETRIE) == null) {
            final CidsBean emptyGeoBean = CidsAppBackend.getInstance().getVerdisMetaClass(VerdisMetaClassConstants.MC_GEOM).getEmptyInstance().getBean();
            cidsBean.setProperty(PROP__GEOMETRIE, emptyGeoBean);
        }
        cidsBean.setProperty(PROP__GEOMETRIE__GEO_FIELD, geom);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    @Override
    public CidsBean getCidsBean() {
        return frontBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        frontBean = cidsBean;
//        DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(bindingGroup, cidsBean);
        bindingGroup.unbind();
        bindingGroup.bind();

        try {
            if ((cidsBean != null) && (cidsBean.getProperty(PROP__GEOMETRIE) != null)) {
                bpanWdsrDetails.setBackgroundEnabled(true);
            } else {
                bpanWdsrDetails.setBackgroundEnabled(false);
            }
        } catch (Exception e) {
            LOG.warn("problem when trying to set background enabled (or not). will turn the background off", e);
            bpanWdsrDetails.setBackgroundEnabled(false);
        }

        attachBeanValidators();
    }

    @Override
    public void editModeChanged() {
        setEnabled(CidsAppBackend.getInstance().isEditable());
    }

    @Override
    public void setEnabled(final boolean bln) {
        super.setEnabled(bln);
        txtBearbeitetDurch.setEditable(bln);
        txtBemSR.setEditable(bln);
        txtBemWD.setEditable(bln);
        txtErfassungsdatum.setEditable(bln);
        txtLaengeGrafik.setEditable(bln);
        txtLaengeKorrektur.setEditable(bln);
        txtNummer.setEditable(bln);
        txtVeranlagungSR.setEditable(bln);
        txtVeranlagungWD.setEditable(bln);
        cboReinigungsklasse.setEnabled(bln);
        cboStrasse.setEnabled(bln);
        cboWinterdienstPrio.setEnabled(bln);

        txtBearbeitetDurch.setOpaque(bln);
        txtBemSR.setOpaque(bln);
        txtBemWD.setOpaque(bln);
        txtErfassungsdatum.setOpaque(bln);
        txtLaengeGrafik.setOpaque(bln);
        txtLaengeKorrektur.setOpaque(bln);
        txtNummer.setOpaque(bln);
        txtVeranlagungSR.setOpaque(bln);
        txtVeranlagungWD.setOpaque(bln);
        cboReinigungsklasse.setOpaque(bln);
        cboStrasse.setOpaque(bln);
        cboWinterdienstPrio.setOpaque(bln);

//        this.scpBemerkung.setOpaque(b);
//        scpBemerkung.getViewport().setOpaque(b);
//        if (b) {
//            txtBemerkung.setBackground(java.awt.Color.white);
//        } else {
//            txtBemerkung.setBackground(this.getBackground());
//        }

    }

    /**
     * DOCUMENT ME!
     *
     * @param  pCanvas  DOCUMENT ME!
     */
    public void setBackgroundPCanvas(final PCanvas pCanvas) {
        pCanvas.setBackground(getBackground());
        bpanWdsrDetails.setPCanvas(pCanvas);
    }

    public static Validator getValidatorNummer(final CidsBean frontBean) {
        return new CidsBeanValidator(frontBean, WDSRPropertyConstants.PROP__NUMMER) {

            @Override
            public ValidatorState performValidation() {
                final CidsBean cidsBean = getCidsBean();
                if (cidsBean == null) {
                    return null;
                }

//                final String bezeichnung = (String) cidsBean.getProperty(PROP__"flaechenbezeichnung");
//                final int art = (cidsBean.getProperty(PROP__"flaecheninfo.flaechenart.id") == null) ? 0 : (Integer) cidsBean.getProperty(PROP__"flaecheninfo.flaechenart.id");
//
//                final Action action = new AbstractAction() {
//
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
//                        final int answer = JOptionPane.showConfirmDialog(
//                                Main.THIS,
//                                "Soll die n\u00E4chste freie Bezeichnung gew\u00E4hlt werden?",
//                                "Bezeichnung automatisch setzen",
//                                JOptionPane.YES_NO_OPTION);
//                        if (answer == JOptionPane.YES_OPTION) {
//                            final CidsBean cidsBean = getCidsBean();
//                            int art;
//                            try {
//                                art = (Integer) cidsBean.getProperty(PROP__"flaecheninfo.flaechenart.id");
//                            } catch (final NumberFormatException ex) {
//                                art = 0;
//                            }
//                            final String newValue = Main.THIS.getRegenFlaechenTabellenPanel().getValidFlaechenname(art);
//                            try {
//                                cidsBean.setProperty(PROP__"flaechenbezeichnung", newValue);
//                            } catch (Exception ex) {
//                                if (log.isDebugEnabled()) {
//                                    log.debug("error while setting flaechenbezeichnung", ex);
//                                }
//                            }
//                        }
//                    }
//                };
//                boolean numerisch = false;
//                Integer tester = null;
//                try {
//                    tester = Integer.parseInt(bezeichnung);
//                    numerisch = true;
//                } catch (final Exception ex) {
//                    numerisch = false;
//                }
//
//                if ((art == Main.PROPVAL_ART_DACH) || (art == Main.PROPVAL_ART_GRUENDACH)) {
//                    if (!numerisch) {
//                        return new ValidatorStateImpl(ValidatorState.ERROR, "Fl\u00E4chenbezeichnung muss eine Zahl sein.", action);
//                    } else {
//                        if ((tester.intValue() > 1000) || (tester.intValue() < 0)) {
//                            return new ValidatorStateImpl(ValidatorState.ERROR, "Fl\u00E4chenbezeichnung muss zwischen 0 und 1000 liegen.", action);
//                        }
//                    }
//                } else {
//                    if (bezeichnung != null) {
//                        final int len = bezeichnung.length();
//                        if (numerisch || ((len > 3) || ((len == 3) && (bezeichnung.compareTo("BBB") > 0)))) {
//                            return new ValidatorStateImpl(ValidatorState.ERROR, "Fl\u00E4chenbezeichnung muss zwischen A und BBB liegen.", action);
//                        }
//                    }
//                }
                return new ValidatorStateImpl(ValidatorState.Type.VALID);
            }
        };
    }

    public static Validator getValidatorLaengeGrafik(final CidsBean frontBean) {
        return new CidsBeanValidator(frontBean, WDSRPropertyConstants.PROP__LAENGE_GRAFIK) {

            @Override
            public ValidatorState performValidation() {
                final CidsBean cidsBean = getCidsBean();
                if (cidsBean == null) {
                    return null;
                }

                final Integer laenge_grafik = (Integer) cidsBean.getProperty(WDSRPropertyConstants.PROP__LAENGE_GRAFIK);
                final Geometry geom = WDSRDetailsPanel.getGeometry(cidsBean);
                final Action action = new AbstractAction() {

                    @Override
                    public void actionPerformed(final ActionEvent event) {
                        final CidsBean cidsBean = getCidsBean();
                        final Geometry geom = WDSRDetailsPanel.getGeometry(cidsBean);

                        if (Main.THIS.isInEditMode()) {
                            if (geom != null) {
                                final int answer = JOptionPane.showConfirmDialog(
                                        Main.THIS,
                                        "Soll die Länge aus der Grafik \u00FCbernommen werden?",
                                        "Länge automatisch setzen",
                                        JOptionPane.YES_NO_OPTION);
                                if (answer == JOptionPane.YES_OPTION) {
                                    try {
                                        final int laenge_grafik = (int) Math.abs(geom.getLength());
                                        cidsBean.setProperty(WDSRPropertyConstants.PROP__LAENGE_GRAFIK, laenge_grafik);
                                    } catch (final Exception ex) {
                                        if (LOG.isDebugEnabled()) {
                                            LOG.debug("error while setting laenge_grafik", ex);
                                        }
                                    }
                                }
                            }
                        }
                    }
                };

                if (laenge_grafik == null) {
                    return new ValidatorStateImpl(ValidatorState.Type.ERROR, "Wert ist leer", action);
                }

                if (geom != null && laenge_grafik != (int) Math.abs(geom.getLength())) {
                    return new ValidatorStateImpl(ValidatorState.Type.WARNING, "L\u00E4nge der Geometrie stimmt nicht \u00FCberein (" + ((int) (geom.getLength())) + ")", action);
                }
                return new ValidatorStateImpl(ValidatorState.Type.VALID);
            }
        };
    }

    public static Validator getValidatorLaengeKorrektur(final CidsBean frontBean) {
        return new CidsBeanValidator(frontBean, WDSRPropertyConstants.PROP__LAENGE_KORREKTUR) {

            @Override
            public ValidatorState performValidation() {
                final CidsBean cidsBean = getCidsBean();
                if (cidsBean == null) {
                    return null;
                }

                Integer laenge_grafik = (Integer) cidsBean.getProperty(WDSRPropertyConstants.PROP__LAENGE_GRAFIK);
                final Integer laenge_korrektur = (Integer) cidsBean.getProperty(WDSRPropertyConstants.PROP__LAENGE_KORREKTUR);
                final Action action = new AbstractAction() {

                    @Override
                    public void actionPerformed(final ActionEvent event) {
                        final CidsBean cidsBean = getCidsBean();
                        final Geometry geom = WDSRDetailsPanel.getGeometry(cidsBean);

                        if (Main.THIS.isInEditMode()) {
                            if (geom != null) {
                                final int answer = JOptionPane.showConfirmDialog(
                                        Main.THIS,
                                        "Soll die Länge aus der Grafik \u00FCbernommen werden?",
                                        "Länge automatisch setzen",
                                        JOptionPane.YES_NO_OPTION);
                                if (answer == JOptionPane.YES_OPTION) {
                                    try {
                                        final Integer laenge_grafik = (Integer) cidsBean.getProperty(WDSRPropertyConstants.PROP__LAENGE_GRAFIK);
                                        cidsBean.setProperty(WDSRPropertyConstants.PROP__LAENGE_KORREKTUR, laenge_grafik);
                                    } catch (final Exception ex) {
                                        if (LOG.isDebugEnabled()) {
                                            LOG.debug("error while setting laenge_korrektur", ex);
                                        }
                                    }
                                }
                            }
                        }
                    }
                };

                if (laenge_korrektur != null) {
                    if (laenge_grafik == null) {
                        laenge_grafik = 0;
                    }
                    final int diff = laenge_korrektur - laenge_grafik;
                    if (Math.abs(diff) > 20) {
                        return new ValidatorStateImpl(ValidatorState.Type.WARNING, "Differenz zwischen Korrekturwert und Länge > 20m.", action);
                    }
                }
                return new ValidatorStateImpl(ValidatorState.Type.VALID);
            }
        };
    }

    public static Validator getValidatorDatumErfassung(final CidsBean frontBean) {
        return new CidsBeanValidator(frontBean, WDSRPropertyConstants.PROP__ERFASSUNGSDATUM) {

            @Override
            public ValidatorState performValidation() {
                final CidsBean cidsBean = getCidsBean();
                if (cidsBean == null) {
                    return null;
                }

                // jedes gültige Datum ist valide
                return new ValidatorStateImpl(ValidatorState.Type.VALID);
            }
        };
    }

    public static Validator getValidatorVeranlagungSR(final CidsBean frontBean) {
        return new CidsBeanValidator(frontBean, WDSRPropertyConstants.PROP__SR_VERANLAGUNG) {

            @Override
            public ValidatorState performValidation() {
                final CidsBean cidsBean = getCidsBean();
                if (cidsBean == null) {
                    return null;
                }

                final String veranlagungsdatum = (String) cidsBean.getProperty(WDSRPropertyConstants.PROP__SR_VERANLAGUNG);

                if (veranlagungsdatum != null) {
                    final boolean matches = Pattern.matches(
                            "\\d\\d/(01|02|03|04|05|06|07|08|09|10|11|12)",
                            veranlagungsdatum);
                    if (!matches) {
                        return new ValidatorStateImpl(ValidatorState.Type.ERROR, "Veranlagungsdatum muss im Format JJ/MM eingegeben werden.");
                    }
                }
                return new ValidatorStateImpl(ValidatorState.Type.VALID);
            }
        };
    }

    public static Validator getValidatorVeranlagungWD(final CidsBean frontBean) {
        return new CidsBeanValidator(frontBean, WDSRPropertyConstants.PROP__WD_VERANLAGUNG) {

            @Override
            public ValidatorState performValidation() {
                final CidsBean cidsBean = getCidsBean();
                if (cidsBean == null) {
                    return null;
                }

                final String veranlagungsdatum = (String) cidsBean.getProperty(WDSRPropertyConstants.PROP__WD_VERANLAGUNG);

                if (veranlagungsdatum != null) {
                    final boolean matches = Pattern.matches(
                            "\\d\\d/(01|02|03|04|05|06|07|08|09|10|11|12)",
                            veranlagungsdatum);
                    if (!matches) {
                        return new ValidatorStateImpl(ValidatorState.Type.ERROR, "Veranlagungsdatum muss im Format JJ/MM eingegeben werden.");
                    }
                }
                return new ValidatorStateImpl(ValidatorState.Type.VALID);
            }
        };
    }
}
