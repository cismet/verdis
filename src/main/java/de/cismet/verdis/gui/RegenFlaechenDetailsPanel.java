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
 * RegenFlaechenDetailsPanel.java
 *
 * Created on 03.12.2010, 21:50:45
 */
package de.cismet.verdis.gui;

import de.cismet.verdis.constants.RegenFlaechenPropertyConstants;
import com.vividsolutions.jts.geom.Geometry;
import de.cismet.cids.custom.util.BindingValidationSupport;

import edu.umd.cs.piccolo.PCanvas;

import java.awt.Color;

import javax.swing.UIManager;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;

import de.cismet.cids.editors.DefaultBindableReferenceCombo;
import de.cismet.cids.editors.converters.SqlDateToStringConverter;
import de.cismet.validation.Validatable;
import de.cismet.validation.Validator;
import de.cismet.validation.ValidatorHelper;
import de.cismet.validation.display.EmbeddedValidatorDisplay;

import de.cismet.verdis.CidsAppBackend;
import de.cismet.verdis.EditModeListener;
import de.cismet.verdis.constants.KassenzeichenPropertyConstants;
import de.cismet.verdis.constants.VerdisMetaClassConstants;
import java.util.Collection;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class RegenFlaechenDetailsPanel extends javax.swing.JPanel implements CidsBeanStore, EditModeListener, HyperlinkListener, RegenFlaechenPropertyConstants, Validatable {

    //~ Instance fields --------------------------------------------------------

    private final transient org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private CidsBean flaecheBean;
    private final Validator bindingValidator;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.cismet.cismap.commons.gui.SimpleBackgroundedJPanel bpanRegenFlDetails;
    private javax.swing.JComboBox cboAnschlussgrad;
    private javax.swing.JComboBox cboBeschreibung;
    private javax.swing.JComboBox cboFlaechenart;
    private javax.swing.JCheckBox chkSperre;
    private javax.swing.JEditorPane edtQuer;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblAnschlussgrad;
    private javax.swing.JLabel lblAnteil;
    private javax.swing.JLabel lblBemerkung;
    private javax.swing.JLabel lblBeschreibung;
    private javax.swing.JLabel lblBezeichnung;
    private javax.swing.JLabel lblErfassungsadtum;
    private javax.swing.JLabel lblFEB_ID;
    private javax.swing.JLabel lblFlaechenart;
    private javax.swing.JLabel lblGroesseGrafik;
    private javax.swing.JLabel lblGroesseKorrektur;
    private javax.swing.JLabel lblSperre;
    private javax.swing.JLabel lblTeileigentumQuerverweise;
    private javax.swing.JLabel lblVeranlagungsdatum;
    private javax.swing.JScrollPane scpBemerkung;
    private javax.swing.JScrollPane scpQuer;
    private javax.swing.JTextField txtAnteil;
    private javax.swing.JTextArea txtBemerkung;
    private javax.swing.JTextField txtBezeichnung;
    private javax.swing.JTextField txtErfassungsdatum;
    private javax.swing.JTextField txtFEB_ID;
    private javax.swing.JTextField txtGroesseGrafik;
    private javax.swing.JTextField txtGroesseKorrektur;
    private javax.swing.JTextField txtSperreBemerkung;
    private javax.swing.JTextField txtVeranlagungsdatum;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form RegenFlaechenDetailsPanel.
     */
    public RegenFlaechenDetailsPanel() {
        UIManager.put("ComboBox.disabledForeground", Color.black);
        initComponents();

        bindingValidator = BindingValidationSupport.attachBindingValidationToAllTargets(bindingGroup);

        ((DefaultBindableReferenceCombo)cboBeschreibung).setMetaClass(CidsAppBackend.getInstance().getVerdisMetaClass(VerdisMetaClassConstants.MC_FLAECHENBESCHREIBUNG));
        ((DefaultBindableReferenceCombo)cboAnschlussgrad).setMetaClass(CidsAppBackend.getInstance().getVerdisMetaClass(VerdisMetaClassConstants.MC_ANSCHLUSSGRAD));
        ((DefaultBindableReferenceCombo)cboFlaechenart).setMetaClass(CidsAppBackend.getInstance().getVerdisMetaClass(VerdisMetaClassConstants.MC_FLAECHENART));
        setEnabled(false);

        edtQuer.addHyperlinkListener(this);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Validator getValidator() {
        // nur BindingValidator notwendig, der TabellenValidator validiert schon alle beans
        return bindingValidator;
    }

    public Geometry getGeometry() {
        return getGeometry(getCidsBean());
    }

    public static Geometry getGeometry(final CidsBean flaecheBean) {
        if (flaecheBean != null && flaecheBean.getProperty(PROP__FLAECHENINFO) != null && flaecheBean.getProperty(PROP__FLAECHENINFO__GEOMETRIE) != null) {
            return (Geometry) flaecheBean.getProperty(PROP__FLAECHENINFO__GEOMETRIE__GEO_FIELD);
        } else {
            return null;
        }
    }

    public static void setGeometry(final Geometry geom, final CidsBean cidsBean) throws Exception {
         Main.transformToDefaultCrsNeeded(geom);
         if (cidsBean.getProperty(PROP__FLAECHENINFO__GEOMETRIE) == null) {
            final CidsBean emptyGeoBean = CidsAppBackend.getInstance().getVerdisMetaClass(VerdisMetaClassConstants.MC_GEOM).getEmptyInstance().getBean();
            cidsBean.setProperty(PROP__FLAECHENINFO__GEOMETRIE, emptyGeoBean);
        }
        cidsBean.setProperty(PROP__FLAECHENINFO__GEOMETRIE__GEO_FIELD, geom);
    }

    private void attachBeanValidators() {
        ValidatorHelper.removeAllNoBindingValidatorFromDisplay(txtBezeichnung);
        RegenFlaechenTabellenPanel.getValidatorFlaechenBezeichnung(flaecheBean).attachDisplay(EmbeddedValidatorDisplay.getEmbeddedDisplayFor(txtBezeichnung));
        ValidatorHelper.removeAllNoBindingValidatorFromDisplay(txtGroesseGrafik);
        RegenFlaechenTabellenPanel.getValidatorGroesseGrafik(flaecheBean).attachDisplay(EmbeddedValidatorDisplay.getEmbeddedDisplayFor(txtGroesseGrafik));
        ValidatorHelper.removeAllNoBindingValidatorFromDisplay(txtGroesseKorrektur);
        RegenFlaechenTabellenPanel.getValidatorGroesseKorrektur(flaecheBean).attachDisplay(EmbeddedValidatorDisplay.getEmbeddedDisplayFor(txtGroesseKorrektur));
        ValidatorHelper.removeAllNoBindingValidatorFromDisplay(txtAnteil);
        RegenFlaechenTabellenPanel.getValidatorAnteil(flaecheBean).attachDisplay(EmbeddedValidatorDisplay.getEmbeddedDisplayFor(txtAnteil));
        ValidatorHelper.removeAllNoBindingValidatorFromDisplay(txtErfassungsdatum);
        RegenFlaechenTabellenPanel.getValidatorDatumErfassung(flaecheBean).attachDisplay(EmbeddedValidatorDisplay.getEmbeddedDisplayFor(txtErfassungsdatum));
        ValidatorHelper.removeAllNoBindingValidatorFromDisplay(txtVeranlagungsdatum);
        RegenFlaechenTabellenPanel.getValidatorDatumVeranlagung(flaecheBean).attachDisplay(EmbeddedValidatorDisplay.getEmbeddedDisplayFor(txtVeranlagungsdatum));
        ValidatorHelper.removeAllNoBindingValidatorFromDisplay(txtFEB_ID);
        RegenFlaechenTabellenPanel.getValidatorFebId(flaecheBean).attachDisplay(EmbeddedValidatorDisplay.getEmbeddedDisplayFor(txtFEB_ID));
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

        jPanel2 = new javax.swing.JPanel();
        bpanRegenFlDetails = new de.cismet.cismap.commons.gui.SimpleBackgroundedJPanel();
        lblBezeichnung = new javax.swing.JLabel();
        lblGroesseGrafik = new javax.swing.JLabel();
        lblGroesseKorrektur = new javax.swing.JLabel();
        lblFlaechenart = new javax.swing.JLabel();
        lblAnschlussgrad = new javax.swing.JLabel();
        txtBezeichnung = new javax.swing.JTextField();
        txtGroesseGrafik = new javax.swing.JTextField();
        txtGroesseKorrektur = new javax.swing.JTextField();
        lblAnteil = new javax.swing.JLabel();
        lblErfassungsadtum = new javax.swing.JLabel();
        lblVeranlagungsdatum = new javax.swing.JLabel();
        lblBemerkung = new javax.swing.JLabel();
        txtAnteil = new javax.swing.JTextField();
        txtErfassungsdatum = new javax.swing.JTextField();
        txtVeranlagungsdatum = new javax.swing.JTextField();
        cboFlaechenart = new DefaultBindableReferenceCombo();
        cboAnschlussgrad = new DefaultBindableReferenceCombo();
        scpBemerkung = new javax.swing.JScrollPane();
        txtBemerkung = new javax.swing.JTextArea();
        lblSperre = new javax.swing.JLabel();
        chkSperre = new javax.swing.JCheckBox();
        txtFEB_ID = new javax.swing.JTextField();
        lblFEB_ID = new javax.swing.JLabel();
        txtSperreBemerkung = new javax.swing.JTextField();
        lblTeileigentumQuerverweise = new javax.swing.JLabel();
        scpQuer = new javax.swing.JScrollPane();
        edtQuer = new javax.swing.JEditorPane();
        jPanel1 = new javax.swing.JPanel();
        lblBeschreibung = new javax.swing.JLabel();
        cboBeschreibung = new DefaultBindableReferenceCombo();

        setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.BorderLayout());

        bpanRegenFlDetails.setOpaque(false);
        bpanRegenFlDetails.setLayout(new java.awt.GridBagLayout());

        lblBezeichnung.setText(org.openide.util.NbBundle.getMessage(RegenFlaechenDetailsPanel.class, "RegenFlaechenDetailsPanel.lblBezeichnung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        bpanRegenFlDetails.add(lblBezeichnung, gridBagConstraints);

        lblGroesseGrafik.setText(org.openide.util.NbBundle.getMessage(RegenFlaechenDetailsPanel.class, "RegenFlaechenDetailsPanel.lblGroesseGrafik.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        bpanRegenFlDetails.add(lblGroesseGrafik, gridBagConstraints);

        lblGroesseKorrektur.setText(org.openide.util.NbBundle.getMessage(RegenFlaechenDetailsPanel.class, "RegenFlaechenDetailsPanel.lblGroesseKorrektur.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        bpanRegenFlDetails.add(lblGroesseKorrektur, gridBagConstraints);

        lblFlaechenart.setText(org.openide.util.NbBundle.getMessage(RegenFlaechenDetailsPanel.class, "RegenFlaechenDetailsPanel.lblFlaechenart.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        bpanRegenFlDetails.add(lblFlaechenart, gridBagConstraints);

        lblAnschlussgrad.setText(org.openide.util.NbBundle.getMessage(RegenFlaechenDetailsPanel.class, "RegenFlaechenDetailsPanel.lblAnschlussgrad.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        bpanRegenFlDetails.add(lblAnschlussgrad, gridBagConstraints);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.flaechenbezeichnung}"), txtBezeichnung, org.jdesktop.beansbinding.BeanProperty.create("text"), PROP__FLAECHENBEZEICHNUNG);
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 2);
        bpanRegenFlDetails.add(txtBezeichnung, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.flaecheninfo.groesse_aus_grafik}"), txtGroesseGrafik, org.jdesktop.beansbinding.BeanProperty.create("text"), PROP__FLAECHENINFO__GROESSE_GRAFIK);
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        txtGroesseGrafik.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtGroesseGrafikActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 2);
        bpanRegenFlDetails.add(txtGroesseGrafik, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.flaecheninfo.groesse_korrektur}"), txtGroesseKorrektur, org.jdesktop.beansbinding.BeanProperty.create("text"), PROP__FLAECHENINFO__GROESSE_KORREKTUR);
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 2);
        bpanRegenFlDetails.add(txtGroesseKorrektur, gridBagConstraints);

        lblAnteil.setText(org.openide.util.NbBundle.getMessage(RegenFlaechenDetailsPanel.class, "RegenFlaechenDetailsPanel.lblAnteil.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        bpanRegenFlDetails.add(lblAnteil, gridBagConstraints);

        lblErfassungsadtum.setText(org.openide.util.NbBundle.getMessage(RegenFlaechenDetailsPanel.class, "RegenFlaechenDetailsPanel.lblErfassungsadtum.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        bpanRegenFlDetails.add(lblErfassungsadtum, gridBagConstraints);

        lblVeranlagungsdatum.setText(org.openide.util.NbBundle.getMessage(RegenFlaechenDetailsPanel.class, "RegenFlaechenDetailsPanel.lblVeranlagungsdatum.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        bpanRegenFlDetails.add(lblVeranlagungsdatum, gridBagConstraints);

        lblBemerkung.setText(org.openide.util.NbBundle.getMessage(RegenFlaechenDetailsPanel.class, "RegenFlaechenDetailsPanel.lblBemerkung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        bpanRegenFlDetails.add(lblBemerkung, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.anteil}"), txtAnteil, org.jdesktop.beansbinding.BeanProperty.create("text"), PROP__ANTEIL);
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 2);
        bpanRegenFlDetails.add(txtAnteil, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.datum_erfassung}"), txtErfassungsdatum, org.jdesktop.beansbinding.BeanProperty.create("text"), KassenzeichenPropertyConstants.PROP__DATUM_ERFASSUNG);
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        binding.setConverter(new SqlDateToStringConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 2);
        bpanRegenFlDetails.add(txtErfassungsdatum, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.datum_veranlagung}"), txtVeranlagungsdatum, org.jdesktop.beansbinding.BeanProperty.create("text"), KassenzeichenPropertyConstants.PROP__DATUM_VERANLAGUNG);
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 2);
        bpanRegenFlDetails.add(txtVeranlagungsdatum, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.flaecheninfo.flaechenart}"), cboFlaechenart, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 2);
        bpanRegenFlDetails.add(cboFlaechenart, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.flaecheninfo.anschlussgrad}"), cboAnschlussgrad, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 2);
        bpanRegenFlDetails.add(cboAnschlussgrad, gridBagConstraints);

        scpBemerkung.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scpBemerkung.setMinimumSize(new java.awt.Dimension(103, 40));
        scpBemerkung.setOpaque(false);
        scpBemerkung.setPreferredSize(new java.awt.Dimension(40, 40));

        txtBemerkung.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 11));
        txtBemerkung.setLineWrap(true);
        txtBemerkung.setRows(2);
        txtBemerkung.setMinimumSize(new java.awt.Dimension(73, 38));
        txtBemerkung.setOpaque(false);
        txtBemerkung.setPreferredSize(new java.awt.Dimension(21, 756));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bemerkung}"), txtBemerkung, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        scpBemerkung.setViewportView(txtBemerkung);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 2);
        bpanRegenFlDetails.add(scpBemerkung, gridBagConstraints);

        lblSperre.setText(org.openide.util.NbBundle.getMessage(RegenFlaechenDetailsPanel.class, "RegenFlaechenDetailsPanel.lblSperre.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        bpanRegenFlDetails.add(lblSperre, gridBagConstraints);

        chkSperre.setForeground(java.awt.Color.red);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.sperre}"), chkSperre, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        chkSperre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSperreActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 0);
        bpanRegenFlDetails.add(chkSperre, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.feb_id}"), txtFEB_ID, org.jdesktop.beansbinding.BeanProperty.create("text"), PROP__FEB_ID);
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 2);
        bpanRegenFlDetails.add(txtFEB_ID, gridBagConstraints);

        lblFEB_ID.setText(org.openide.util.NbBundle.getMessage(RegenFlaechenDetailsPanel.class, "RegenFlaechenDetailsPanel.lblFEB_ID.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        bpanRegenFlDetails.add(lblFEB_ID, gridBagConstraints);

        txtSperreBemerkung.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.background"));
        txtSperreBemerkung.setEditable(false);
        txtSperreBemerkung.setForeground(java.awt.Color.red);
        txtSperreBemerkung.setBorder(null);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bemerkung_sperre}"), txtSperreBemerkung, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 2);
        bpanRegenFlDetails.add(txtSperreBemerkung, gridBagConstraints);

        lblTeileigentumQuerverweise.setText(org.openide.util.NbBundle.getMessage(RegenFlaechenDetailsPanel.class, "RegenFlaechenDetailsPanel.lblTeileigentumQuerverweise.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        bpanRegenFlDetails.add(lblTeileigentumQuerverweise, gridBagConstraints);

        scpQuer.setOpaque(false);

        edtQuer.setContentType(org.openide.util.NbBundle.getMessage(RegenFlaechenDetailsPanel.class, "RegenFlaechenDetailsPanel.edtQuer.contentType")); // NOI18N
        edtQuer.setEditable(false);
        edtQuer.setOpaque(false);
        scpQuer.setViewportView(edtQuer);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 2);
        bpanRegenFlDetails.add(scpQuer, gridBagConstraints);

        jPanel1.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 0.01;
        bpanRegenFlDetails.add(jPanel1, gridBagConstraints);

        lblBeschreibung.setText(org.openide.util.NbBundle.getMessage(RegenFlaechenDetailsPanel.class, "RegenFlaechenDetailsPanel.lblBeschreibung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        bpanRegenFlDetails.add(lblBeschreibung, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.flaecheninfo.beschreibung}"), cboBeschreibung, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 2);
        bpanRegenFlDetails.add(cboBeschreibung, gridBagConstraints);

        jPanel2.add(bpanRegenFlDetails, java.awt.BorderLayout.CENTER);

        add(jPanel2, java.awt.BorderLayout.CENTER);

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void txtGroesseGrafikActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtGroesseGrafikActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtGroesseGrafikActionPerformed

    private void chkSperreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSperreActionPerformed
        final boolean sperre = chkSperre.isSelected();
        if (sperre) {
            String answer = JOptionPane.showInputDialog(Main.THIS.getRootPane(),
                        "Bitte eine Bemerkung zur Sperre angeben.",
                        txtSperreBemerkung.getText());
            if (answer == null) {
                chkSperre.setSelected(false);
            }
            txtSperreBemerkung.setText(answer);
        } else {
            txtSperreBemerkung.setText(null);
        }
    }//GEN-LAST:event_chkSperreActionPerformed

    @Override
    public CidsBean getCidsBean() {
        return flaecheBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        if (cidsBean != null && cidsBean.equals(flaecheBean)) {
            return;
        }
        bindingGroup.unbind();
        flaecheBean = cidsBean;
//        DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(bindingGroup, cidsBean);
        bindingGroup.bind();

        try {
            if ((cidsBean != null) && (cidsBean.getProperty(PROP__FLAECHENINFO__GEOMETRIE) != null)) {
                bpanRegenFlDetails.setBackgroundEnabled(true);
            } else {
                bpanRegenFlDetails.setBackgroundEnabled(false);
            }
        } catch (Exception e) {
            log.warn("problem when trying to set background enabled (or not). will turn the background off", e);
            bpanRegenFlDetails.setBackgroundEnabled(false);
        }

        if (cidsBean != null) {
            final Thread t = new Thread() {

                @Override
                public void run() {
                    // TeileigentumCrossReferences
                    Collection crossReference = (Collection)CidsAppBackend.getInstance().getCrossReferencesFor(cidsBean.getMetaObject().getID());
                    if (crossReference != null) {
                        String html = "<html><body><center>";
                        for (final Object o : crossReference) {
                            final String link = o.toString();
                            html += "<a href=\"" + link + "\"><font size=\"-2\">" + link + "</font></a><br>";
                        }
                        html += "</center></body></html>";
                        final String finalHtml = html;
                        lblTeileigentumQuerverweise.setVisible(true);
                        edtQuer.setVisible(true);
                        scpQuer.setVisible(true);
                        edtQuer.setText(finalHtml);
                        edtQuer.setCaretPosition(0);
                    } else {
                        edtQuer.setText("");
                        lblTeileigentumQuerverweise.setVisible(false);
                        edtQuer.setVisible(false);
                        scpQuer.setVisible(false);
                    }
                }
            };
            t.setPriority(Thread.NORM_PRIORITY);
            t.start();
        } else {
            edtQuer.setText("");
            lblTeileigentumQuerverweise.setVisible(false);
            edtQuer.setVisible(false);
            scpQuer.setVisible(false);
        }

        attachBeanValidators();
    }

    @Override
    public void editModeChanged() {
        setEnabled(CidsAppBackend.getInstance().isEditable());
    }

    @Override
    public final void setEnabled(final boolean b) {
        txtAnteil.setEditable(b);
        txtBemerkung.setEditable(b);
        txtBezeichnung.setEditable(b);
        txtErfassungsdatum.setEditable(b);
        txtFEB_ID.setEditable(b);
        txtGroesseGrafik.setEditable(b);
        txtGroesseKorrektur.setEditable(b);
        txtVeranlagungsdatum.setEditable(b);
        chkSperre.setEnabled(b);
        cboAnschlussgrad.setEnabled(b);
        cboFlaechenart.setEnabled(b);
        cboBeschreibung.setEnabled(b);
        // Opacity
        txtAnteil.setOpaque(b);
        txtBemerkung.setOpaque(b);
        txtBezeichnung.setOpaque(b);
        txtErfassungsdatum.setOpaque(b);
        txtFEB_ID.setOpaque(b);
        txtGroesseGrafik.setOpaque(b);
        txtGroesseKorrektur.setOpaque(b);
        txtVeranlagungsdatum.setOpaque(b);
        chkSperre.setOpaque(b);

        cboAnschlussgrad.setOpaque(b);
        cboFlaechenart.setOpaque(b);
        cboBeschreibung.setOpaque(b);

        this.scpBemerkung.setOpaque(b);
        scpBemerkung.getViewport().setOpaque(b);
        if (b) {
            txtBemerkung.setBackground(java.awt.Color.white);
        } else {
            txtBemerkung.setBackground(this.getBackground());
        }
        repaint();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  pCanvas  DOCUMENT ME!
     */
    public void setBackgroundPCanvas(final PCanvas pCanvas) {
        pCanvas.setBackground(getBackground());
        bpanRegenFlDetails.setPCanvas(pCanvas);
    }

    @Override
    public void hyperlinkUpdate(final HyperlinkEvent he) {
        final Thread t = new Thread() {

                @Override
                public void run() {
                    if (he.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                        try {
                            Main.THIS.getKzPanel().gotoKassenzeichen(he.getDescription());
                        } catch (Exception ex) {
                            log.error("Fehler im Hyperlinken", ex);
                        }
                    }
                }
            };
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();
    }
}
