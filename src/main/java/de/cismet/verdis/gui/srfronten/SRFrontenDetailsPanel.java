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
package de.cismet.verdis.gui.srfronten;

import Sirius.server.middleware.types.MetaObject;

import com.vividsolutions.jts.geom.Geometry;

import edu.umd.cs.piccolo.PCanvas;

import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.Converter;

import org.openide.util.Exceptions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;

import de.cismet.cids.custom.util.BindingValidationSupport;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultBindableReferenceCombo;
import de.cismet.cids.editors.converters.SqlDateToStringConverter;

import de.cismet.cids.utils.multibean.EmbeddedMultiBeanDisplay;

import de.cismet.tools.CismetThreadPool;

import de.cismet.tools.gui.StaticSwingTools;

import de.cismet.validation.Validator;
import de.cismet.validation.ValidatorHelper;
import de.cismet.validation.ValidatorState;
import de.cismet.validation.ValidatorStateImpl;

import de.cismet.validation.display.EmbeddedValidatorDisplay;

import de.cismet.validation.validator.CidsBeanValidator;

import de.cismet.verdis.CidsAppBackend;
import de.cismet.verdis.CrossReference;

import de.cismet.verdis.commons.constants.FrontPropertyConstants;
import de.cismet.verdis.commons.constants.FrontinfoPropertyConstants;
import de.cismet.verdis.commons.constants.GeomPropertyConstants;
import de.cismet.verdis.commons.constants.StrassePropertyConstants;
import de.cismet.verdis.commons.constants.VerdisMetaClassConstants;

import de.cismet.verdis.gui.AbstractCidsBeanDetailsPanel;
import de.cismet.verdis.gui.Main;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class SRFrontenDetailsPanel extends AbstractCidsBeanDetailsPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            SRFrontenDetailsPanel.class);

    //~ Instance fields --------------------------------------------------------

    private CidsBean frontBean = null;
    private final Validator bindingValidator;
    private CidsBean lastStrasseBean = null;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.cismet.cismap.commons.gui.SimpleBackgroundedJPanel bpanSrDetails;
    private javax.swing.JCheckBox cbAnteil;
    private javax.swing.JCheckBox cbBaulasten;
    private javax.swing.JCheckBox cbGarageStellplatz;
    private javax.swing.JCheckBox cbGrunddienstbarkeit;
    private javax.swing.JCheckBox cbQuadratwurzel;
    private javax.swing.JComboBox cboLageSR;
    private javax.swing.JComboBox cboSR;
    private javax.swing.JComboBox cboStrasse;
    private javax.swing.JEditorPane edtQuer;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JLabel labLageSR;
    private javax.swing.JLabel lblBemSR;
    private javax.swing.JLabel lblErfassungsdatumDesc;
    private javax.swing.JLabel lblLaengeGR;
    private javax.swing.JLabel lblLaengeKorr;
    private javax.swing.JLabel lblLastEditorDescr;
    private javax.swing.JLabel lblNummer;
    private javax.swing.JLabel lblQuerverweise;
    private javax.swing.JLabel lblStrasse;
    private javax.swing.JLabel lblStrassenreinigung;
    private javax.swing.JLabel lblVeranlagungSR;
    private javax.swing.JLabel lblWinkel;
    private javax.swing.JScrollPane scpBemSR;
    private javax.swing.JScrollPane scpQuer;
    private javax.swing.JTextField txtBearbeitetDurch;
    private javax.swing.JTextArea txtBemSR;
    private javax.swing.JTextField txtErfassungsdatum;
    private javax.swing.JTextField txtLaengeGrafik;
    private javax.swing.JTextField txtLaengeKorrektur;
    private javax.swing.JTextField txtNummer;
    private javax.swing.JTextField txtVeranlagungSR;
    private javax.swing.JFormattedTextField txtWinkel;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form DetailPanel.
     */
    public SRFrontenDetailsPanel() {
        initComponents();

        ((DefaultBindableReferenceCombo)cboStrasse).setMetaClass(CidsAppBackend.getInstance().getVerdisMetaClass(
                VerdisMetaClassConstants.MC_STRASSE));
        ((DefaultBindableReferenceCombo)cboSR).setMetaClass(CidsAppBackend.getInstance().getVerdisMetaClass(
                VerdisMetaClassConstants.MC_STRASSENREINIGUNG));

        cboLageSR.setRenderer(new DefaultListCellRenderer() {

                @Override
                public Component getListCellRendererComponent(final JList list,
                        final Object value,
                        final int index,
                        final boolean isSelected,
                        final boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value == null) {
                        setText("manuelle Auswahl der Straßenreinigung");
                    } else if (value instanceof CidsBean) {
                        final CidsBean bean = (CidsBean)value;
                        final String strasse = (String)bean.getProperty("strasse.name");
                        final String bem = (String)bean.getProperty("sr_bem");
                        final String key = (String)bean.getProperty("sr_klasse.key");
                        setText(((bem == null) ? strasse : bem) + " (" + key + ")");
                    }
                    return this;
                }
            });

        cboStrasse.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final CidsBean strasseBean = (CidsBean)cboStrasse.getSelectedItem();
                    if ((strasseBean != null) && (frontBean != null)) {
                        lastStrasseBean = strasseBean;
                    }
                }
            });

        StaticSwingTools.decorateWithFixedAutoCompleteDecorator(cboStrasse);
        final JTextField txt = (JTextField)cboStrasse.getEditor().getEditorComponent();
        txt.setOpaque(false);
        edtQuer.addHyperlinkListener(this);

        EmbeddedMultiBeanDisplay.registerComponentForProperty(
            txtNummer,
            FrontPropertyConstants.PROP__NUMMER,
            getMultiBeanHelper());
        EmbeddedMultiBeanDisplay.registerComponentForProperty(
            txtLaengeGrafik,
            FrontPropertyConstants.PROP__FRONTINFO
                    + "."
                    + FrontinfoPropertyConstants.PROP__LAENGE_GRAFIK,
            getMultiBeanHelper());
        EmbeddedMultiBeanDisplay.registerComponentForProperty(
            txtLaengeKorrektur,
            FrontPropertyConstants.PROP__FRONTINFO
                    + "."
                    + FrontinfoPropertyConstants.PROP__LAENGE_KORREKTUR,
            getMultiBeanHelper());
        EmbeddedMultiBeanDisplay.registerComponentForProperty(
            txtBearbeitetDurch,
            FrontPropertyConstants.PROP__BEARBEITET_DURCH,
            getMultiBeanHelper());
        EmbeddedMultiBeanDisplay.registerComponentForProperty(
            txtErfassungsdatum,
            FrontPropertyConstants.PROP__ERFASSUNGSDATUM,
            getMultiBeanHelper());
        EmbeddedMultiBeanDisplay.registerComponentForProperty(
            cboStrasse,
            FrontPropertyConstants.PROP__FRONTINFO
                    + "."
                    + FrontinfoPropertyConstants.PROP__STRASSE,
            getMultiBeanHelper());
        EmbeddedMultiBeanDisplay.registerComponentForProperty(
            cboLageSR,
            FrontPropertyConstants.PROP__FRONTINFO
                    + "."
                    + FrontinfoPropertyConstants.PROP__LAGE_SR,
            getMultiBeanHelper());
        EmbeddedMultiBeanDisplay.registerComponentForProperty(
            cboSR,
            FrontPropertyConstants.PROP__FRONTINFO
                    + "."
                    + FrontinfoPropertyConstants.PROP__SR_KLASSE_OR,
            getMultiBeanHelper());
        EmbeddedMultiBeanDisplay.registerComponentForProperty(
            txtBemSR,
            FrontPropertyConstants.PROP__FRONTINFO
                    + "."
                    + FrontinfoPropertyConstants.PROP__SR_BEM,
            getMultiBeanHelper());
        EmbeddedMultiBeanDisplay.registerComponentForProperty(
            txtVeranlagungSR,
            FrontPropertyConstants.PROP__FRONTINFO
                    + "."
                    + FrontinfoPropertyConstants.PROP__SR_VERANLAGUNG,
            getMultiBeanHelper());

        bindingValidator = BindingValidationSupport.attachBindingValidationToAllTargets(bindingGroup);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * bindingValidator = BindingValidationSupport.attachBindingValidationToAllTargets(bindingGroup); DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Validator getValidator() {
        // nur BindingValidator notwendig, der TabellenValidator validiert schon alle beans
        return bindingValidator;
    }

    /**
     * DOCUMENT ME!
     */
    private void attachBeanValidators() {
        ValidatorHelper.removeAllNoBindingValidatorFromDisplay(txtNummer);
        getValidatorNummer(frontBean).attachDisplay(EmbeddedValidatorDisplay.getEmbeddedDisplayFor(txtNummer));
        ValidatorHelper.removeAllNoBindingValidatorFromDisplay(txtLaengeGrafik);
        getValidatorLaengeGrafik(frontBean).attachDisplay(EmbeddedValidatorDisplay.getEmbeddedDisplayFor(
                txtLaengeGrafik));
        ValidatorHelper.removeAllNoBindingValidatorFromDisplay(txtLaengeKorrektur);
        getValidatorLaengeKorrektur(frontBean).attachDisplay(EmbeddedValidatorDisplay.getEmbeddedDisplayFor(
                txtLaengeKorrektur));
        ValidatorHelper.removeAllNoBindingValidatorFromDisplay(txtErfassungsdatum);
        getValidatorDatumErfassung(frontBean).attachDisplay(EmbeddedValidatorDisplay.getEmbeddedDisplayFor(
                txtErfassungsdatum));
        ValidatorHelper.removeAllNoBindingValidatorFromDisplay(txtVeranlagungSR);
        getValidatorVeranlagungSR(frontBean).attachDisplay(EmbeddedValidatorDisplay.getEmbeddedDisplayFor(
                txtVeranlagungSR));
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

        bpanSrDetails = new de.cismet.cismap.commons.gui.SimpleBackgroundedJPanel();
        lblNummer = new javax.swing.JLabel();
        lblLaengeGR = new javax.swing.JLabel();
        lblLaengeKorr = new javax.swing.JLabel();
        lblLastEditorDescr = new javax.swing.JLabel();
        lblErfassungsdatumDesc = new javax.swing.JLabel();
        lblStrasse = new javax.swing.JLabel();
        txtNummer = new javax.swing.JTextField();
        txtLaengeGrafik = new javax.swing.JTextField();
        txtLaengeKorrektur = new javax.swing.JTextField();
        txtBearbeitetDurch = new javax.swing.JTextField();
        txtErfassungsdatum = new javax.swing.JTextField();
        cboStrasse = new DefaultBindableReferenceCombo(true);
        jSeparator1 = new javax.swing.JSeparator();
        labLageSR = new javax.swing.JLabel();
        lblStrassenreinigung = new javax.swing.JLabel();
        lblBemSR = new javax.swing.JLabel();
        lblVeranlagungSR = new javax.swing.JLabel();
        cboLageSR = new javax.swing.JComboBox();
        cboSR = new DefaultBindableReferenceCombo();
        scpBemSR = new javax.swing.JScrollPane();
        txtBemSR = new javax.swing.JTextArea();
        txtVeranlagungSR = new javax.swing.JTextField();
        jSeparator3 = new javax.swing.JSeparator();
        jPanel2 = new javax.swing.JPanel();
        cbGarageStellplatz = new javax.swing.JCheckBox();
        cbBaulasten = new javax.swing.JCheckBox();
        cbGrunddienstbarkeit = new javax.swing.JCheckBox();
        cbAnteil = new javax.swing.JCheckBox();
        cbQuadratwurzel = new javax.swing.JCheckBox();
        lblWinkel = new javax.swing.JLabel();
        txtWinkel = new javax.swing.JFormattedTextField();
        jSeparator4 = new javax.swing.JSeparator();
        lblQuerverweise = new javax.swing.JLabel();
        scpQuer = new javax.swing.JScrollPane();
        edtQuer = new javax.swing.JEditorPane();
        jPanel1 = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        bpanSrDetails.setOpaque(false);
        bpanSrDetails.setLayout(new java.awt.GridBagLayout());

        lblNummer.setText(org.openide.util.NbBundle.getMessage(
                SRFrontenDetailsPanel.class,
                "SRFrontenDetailsPanel.lblNummer.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 7, 2, 2);
        bpanSrDetails.add(lblNummer, gridBagConstraints);

        lblLaengeGR.setText(org.openide.util.NbBundle.getMessage(
                SRFrontenDetailsPanel.class,
                "SRFrontenDetailsPanel.lblLaengeGR.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 7, 2, 2);
        bpanSrDetails.add(lblLaengeGR, gridBagConstraints);

        lblLaengeKorr.setText(org.openide.util.NbBundle.getMessage(
                SRFrontenDetailsPanel.class,
                "SRFrontenDetailsPanel.lblLaengeKorr.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 7, 2, 2);
        bpanSrDetails.add(lblLaengeKorr, gridBagConstraints);

        lblLastEditorDescr.setText(org.openide.util.NbBundle.getMessage(
                SRFrontenDetailsPanel.class,
                "SRFrontenDetailsPanel.lblLastEditorDescr.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 7, 2, 2);
        bpanSrDetails.add(lblLastEditorDescr, gridBagConstraints);

        lblErfassungsdatumDesc.setText(org.openide.util.NbBundle.getMessage(
                SRFrontenDetailsPanel.class,
                "SRFrontenDetailsPanel.lblErfassungsdatumDesc.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 7, 2, 2);
        bpanSrDetails.add(lblErfassungsdatumDesc, gridBagConstraints);

        lblStrasse.setText(org.openide.util.NbBundle.getMessage(
                SRFrontenDetailsPanel.class,
                "SRFrontenDetailsPanel.lblStrasse.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 7, 2, 2);
        bpanSrDetails.add(lblStrasse, gridBagConstraints);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.nummer}"),
                txtNummer,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                FrontPropertyConstants.PROP__NUMMER);
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        bpanSrDetails.add(txtNummer, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.frontinfo.laenge_grafik}"),
                txtLaengeGrafik,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                FrontPropertyConstants.PROP__FRONTINFO
                        + "."
                        + FrontinfoPropertyConstants.PROP__LAENGE_GRAFIK);
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        bpanSrDetails.add(txtLaengeGrafik, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.frontinfo.laenge_korrektur}"),
                txtLaengeKorrektur,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                FrontPropertyConstants.PROP__FRONTINFO
                        + "."
                        + FrontinfoPropertyConstants.PROP__LAENGE_KORREKTUR);
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        bpanSrDetails.add(txtLaengeKorrektur, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bearbeitet_durch}"),
                txtBearbeitetDurch,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                FrontPropertyConstants.PROP__BEARBEITET_DURCH);
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        bpanSrDetails.add(txtBearbeitetDurch, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.erfassungsdatum}"),
                txtErfassungsdatum,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                FrontPropertyConstants.PROP__ERFASSUNGSDATUM);
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        binding.setConverter(new SqlDateToStringConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        bpanSrDetails.add(txtErfassungsdatum, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.frontinfo.strasse}"),
                cboStrasse,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"),
                "frontinfo.strasse");
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        cboStrasse.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cboStrasseActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        bpanSrDetails.add(cboStrasse, gridBagConstraints);

        jSeparator1.setMinimumSize(new java.awt.Dimension(0, 10));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        bpanSrDetails.add(jSeparator1, gridBagConstraints);

        labLageSR.setText(org.openide.util.NbBundle.getMessage(
                SRFrontenDetailsPanel.class,
                "SRFrontenDetailsPanel.labLageSR.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 7, 2, 2);
        bpanSrDetails.add(labLageSR, gridBagConstraints);

        lblStrassenreinigung.setText(org.openide.util.NbBundle.getMessage(
                SRFrontenDetailsPanel.class,
                "SRFrontenDetailsPanel.lblStrassenreinigung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 7, 2, 2);
        bpanSrDetails.add(lblStrassenreinigung, gridBagConstraints);

        lblBemSR.setText(org.openide.util.NbBundle.getMessage(
                SRFrontenDetailsPanel.class,
                "SRFrontenDetailsPanel.lblBemSR.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 7, 2, 2);
        bpanSrDetails.add(lblBemSR, gridBagConstraints);

        lblVeranlagungSR.setText(org.openide.util.NbBundle.getMessage(
                SRFrontenDetailsPanel.class,
                "SRFrontenDetailsPanel.lblVeranlagungSR.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 7, 2, 2);
        bpanSrDetails.add(lblVeranlagungSR, gridBagConstraints);

        cboLageSR.setToolTipText(org.openide.util.NbBundle.getMessage(
                SRFrontenDetailsPanel.class,
                "SRFrontenDetailsPanel.cboLageSR.toolTipText")); // NOI18N
        cboLageSR.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cboLageSRActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        bpanSrDetails.add(cboLageSR, gridBagConstraints);

        cboSR.setToolTipText(org.openide.util.NbBundle.getMessage(
                SRFrontenDetailsPanel.class,
                "SRFrontenDetailsPanel.cboSR.toolTipText")); // NOI18N
        cboSR.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cboSRActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        bpanSrDetails.add(cboSR, gridBagConstraints);

        scpBemSR.setMinimumSize(new java.awt.Dimension(23, 75));
        scpBemSR.setPreferredSize(new java.awt.Dimension(279, 100));

        txtBemSR.setColumns(20);
        txtBemSR.setLineWrap(true);
        txtBemSR.setRows(2);
        txtBemSR.setMinimumSize(new java.awt.Dimension(240, 200));
        txtBemSR.setPreferredSize(new java.awt.Dimension(21, 756));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.frontinfo.sr_bem}"),
                txtBemSR,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "frontinfo.sr_bem");
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        scpBemSR.setViewportView(txtBemSR);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        bpanSrDetails.add(scpBemSR, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.frontinfo.sr_veranlagung}"),
                txtVeranlagungSR,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                FrontinfoPropertyConstants.PROP__SR_VERANLAGUNG);
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        bpanSrDetails.add(txtVeranlagungSR, gridBagConstraints);

        jSeparator3.setMinimumSize(new java.awt.Dimension(0, 10));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        bpanSrDetails.add(jSeparator3, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridLayout(0, 2));

        cbGarageStellplatz.setText(org.openide.util.NbBundle.getMessage(
                SRFrontenDetailsPanel.class,
                "SRFrontenDetailsPanel.cbGarageStellplatz.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.frontinfo.garage_stellplatz}"),
                cbGarageStellplatz,
                org.jdesktop.beansbinding.BeanProperty.create("selected"),
                FrontinfoPropertyConstants.PROP__GARAGE_STELLPLATZ);
        binding.setSourceNullValue(false);
        bindingGroup.addBinding(binding);

        jPanel2.add(cbGarageStellplatz);

        cbBaulasten.setText(org.openide.util.NbBundle.getMessage(
                SRFrontenDetailsPanel.class,
                "SRFrontenDetailsPanel.cbBaulasten.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.frontinfo.baulasten}"),
                cbBaulasten,
                org.jdesktop.beansbinding.BeanProperty.create("selected"),
                FrontinfoPropertyConstants.PROP__BAULASTEN);
        binding.setSourceNullValue(false);
        bindingGroup.addBinding(binding);

        jPanel2.add(cbBaulasten);

        cbGrunddienstbarkeit.setText(org.openide.util.NbBundle.getMessage(
                SRFrontenDetailsPanel.class,
                "SRFrontenDetailsPanel.cbGrunddienstbarkeit.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.frontinfo.grunddienstbarkeit}"),
                cbGrunddienstbarkeit,
                org.jdesktop.beansbinding.BeanProperty.create("selected"),
                FrontinfoPropertyConstants.PROP__GRUNDDIENSTBARKEIT);
        binding.setSourceNullValue(false);
        bindingGroup.addBinding(binding);

        jPanel2.add(cbGrunddienstbarkeit);

        cbAnteil.setText(org.openide.util.NbBundle.getMessage(
                SRFrontenDetailsPanel.class,
                "SRFrontenDetailsPanel.cbAnteil.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.frontinfo.anteil}"),
                cbAnteil,
                org.jdesktop.beansbinding.BeanProperty.create("selected"),
                FrontinfoPropertyConstants.PROP__ANTEIL);
        binding.setSourceNullValue(false);
        bindingGroup.addBinding(binding);

        jPanel2.add(cbAnteil);

        cbQuadratwurzel.setText(org.openide.util.NbBundle.getMessage(
                SRFrontenDetailsPanel.class,
                "SRFrontenDetailsPanel.cbQuadratwurzel.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.frontinfo.quadratwurzel}"),
                cbQuadratwurzel,
                org.jdesktop.beansbinding.BeanProperty.create("selected"),
                FrontinfoPropertyConstants.PROP__QUADRATWURZEL);
        binding.setSourceNullValue(false);
        bindingGroup.addBinding(binding);

        jPanel2.add(cbQuadratwurzel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 7, 2, 3);
        bpanSrDetails.add(jPanel2, gridBagConstraints);

        lblWinkel.setText(org.openide.util.NbBundle.getMessage(
                SRFrontenDetailsPanel.class,
                "SRFrontenDetailsPanel.lblWinkel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 7, 2, 2);
        bpanSrDetails.add(lblWinkel, gridBagConstraints);

        txtWinkel.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
                new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.0"))));
        txtWinkel.setText(org.openide.util.NbBundle.getMessage(
                SRFrontenDetailsPanel.class,
                "SRFrontenDetailsPanel.txtWinkel.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.frontinfo.winkel}"),
                txtWinkel,
                org.jdesktop.beansbinding.BeanProperty.create("value"),
                FrontinfoPropertyConstants.PROP__WINKEL);
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        binding.setConverter(new de.cismet.verdis.gui.converter.DoubleToNumberConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        bpanSrDetails.add(txtWinkel, gridBagConstraints);

        jSeparator4.setMinimumSize(new java.awt.Dimension(0, 10));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        bpanSrDetails.add(jSeparator4, gridBagConstraints);

        lblQuerverweise.setText(org.openide.util.NbBundle.getMessage(
                SRFrontenDetailsPanel.class,
                "SRFrontenDetailsPanel.lblQuerverweise.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        bpanSrDetails.add(lblQuerverweise, gridBagConstraints);

        scpQuer.setOpaque(false);

        edtQuer.setEditable(false);
        edtQuer.setContentType("text/html"); // NOI18N
        edtQuer.setOpaque(false);
        scpQuer.setViewportView(edtQuer);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 2);
        bpanSrDetails.add(scpQuer, gridBagConstraints);

        jPanel1.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 0.01;
        bpanSrDetails.add(jPanel1, gridBagConstraints);

        add(bpanSrDetails, java.awt.BorderLayout.CENTER);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cboStrasseActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cboStrasseActionPerformed
        CidsBean strasseBean = null;
        if (frontBean != null) {
            strasseBean = (CidsBean)frontBean.getProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                            + FrontinfoPropertyConstants.PROP__STRASSE);
        }
        if (strasseBean != null) {
            final int strasseId = (Integer)strasseBean.getProperty(StrassePropertyConstants.PROP__ID);

            final String tabSatzung = VerdisMetaClassConstants.MC_SATZUNG;
            final String tabStrassenreinigung = VerdisMetaClassConstants.MC_STRASSENREINIGUNG;
            final String tabStrasse = VerdisMetaClassConstants.MC_STRASSE;
            final String fldId = FrontinfoPropertyConstants.PROP__ID;
            final String fldStrasse = "strasse";
            final String fldSrKlasse = "sr_klasse";

            CismetThreadPool.execute(new SatzungsComboModelWorker(
                    cboLageSR,
                    "SELECT "
                            + CidsAppBackend.getInstance().getVerdisMetaClass(tabSatzung).getId()
                            + ", "
                            + tabSatzung
                            + ".id "
                            + "FROM "
                            + tabStrasse
                            + ", "
                            + tabSatzung
                            + ", "
                            + tabStrassenreinigung
                            + " "
                            + "WHERE "
                            + tabStrasse
                            + "."
                            + fldId
                            + " = "
                            + strasseId
                            + " AND "
                            + tabSatzung
                            + "."
                            + fldStrasse
                            + " = "
                            + tabStrasse
                            + "."
                            + fldId
                            + " AND "
                            + tabStrassenreinigung
                            + "."
                            + fldId
                            + " = "
                            + tabSatzung
                            + "."
                            + fldSrKlasse
                            + ";",
                    (CidsBean)getCidsBean().getProperty(
                        FrontPropertyConstants.PROP__FRONTINFO
                                + "."
                                + FrontinfoPropertyConstants.PROP__LAGE_SR)));
        } else {
            final DefaultComboBoxModel dcmSR = new DefaultComboBoxModel();
            dcmSR.addElement(null);
            cboLageSR.setModel(dcmSR);
        }
    } //GEN-LAST:event_cboStrasseActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cboLageSRActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cboLageSRActionPerformed
        try {
            final CidsBean oldLageSRBean = (CidsBean)getCidsBean().getProperty(FrontPropertyConstants.PROP__FRONTINFO
                            + "."
                            + FrontinfoPropertyConstants.PROP__LAGE_SR);
            final CidsBean newLageSRBean = (CidsBean)cboLageSR.getSelectedItem();

            if (((oldLageSRBean != null) && !oldLageSRBean.equals(newLageSRBean))
                        || ((newLageSRBean != null) && !newLageSRBean.equals(oldLageSRBean))) {
                getCidsBean().setProperty(
                    FrontPropertyConstants.PROP__FRONTINFO
                            + "."
                            + FrontinfoPropertyConstants.PROP__LAGE_SR,
                    cboLageSR.getSelectedItem());
            }
            if ((oldLageSRBean != null) && (newLageSRBean == null)) {
                for (int index = 0; index < cboSR.getItemCount(); index++) {
                    final CidsBean sr = (CidsBean)cboSR.getItemAt(index);
                    if ((sr != null) && (sr.getProperty("key") == null)) {
                        cboSR.setSelectedItem(sr);
                    }
                }
            }
        } catch (Exception ex) {
            LOG.warn(ex, ex);
        }
        updateLageSrCbo();
    } //GEN-LAST:event_cboLageSRActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cboSRActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cboSRActionPerformed
        if (getCidsBean() != null) {
            try {
                final CidsBean lageSRBean = (CidsBean)getCidsBean().getProperty(FrontPropertyConstants.PROP__FRONTINFO
                                + "."
                                + FrontinfoPropertyConstants.PROP__LAGE_SR);
                if (lageSRBean == null) {
                    getCidsBean().setProperty(
                        FrontPropertyConstants.PROP__FRONTINFO
                                + "."
                                + FrontinfoPropertyConstants.PROP__SR_KLASSE_OR,
                        cboSR.getSelectedItem());
                }
            } catch (Exception ex) {
                LOG.warn(ex, ex);
            }
            updateLageSrCbo();
        }
    }                                                                         //GEN-LAST:event_cboSRActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Geometry getGeometry() {
        return getGeometry(getCidsBean());
    }

    /**
     * DOCUMENT ME!
     *
     * @param   frontBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Geometry getGeometry(final CidsBean frontBean) {
        if ((frontBean != null)
                    && (frontBean.getProperty(
                            FrontPropertyConstants.PROP__FRONTINFO
                            + "."
                            + FrontinfoPropertyConstants.PROP__GEOMETRIE) != null)) {
            return (Geometry)frontBean.getProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                            + FrontinfoPropertyConstants.PROP__GEOMETRIE + "."
                            + GeomPropertyConstants.PROP__GEO_FIELD);
        } else {
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   geom      DOCUMENT ME!
     * @param   cidsBean  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void setGeometry(final Geometry geom, final CidsBean cidsBean) throws Exception {
        Main.transformToDefaultCrsNeeded(geom);
        if (
            cidsBean.getProperty(
                        FrontPropertyConstants.PROP__FRONTINFO
                        + "."
                        + FrontinfoPropertyConstants.PROP__GEOMETRIE)
                    == null) {
            final CidsBean emptyGeoBean = CidsAppBackend.getInstance()
                        .getVerdisMetaClass(VerdisMetaClassConstants.MC_GEOM)
                        .getEmptyInstance()
                        .getBean();
            cidsBean.setProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                        + FrontinfoPropertyConstants.PROP__GEOMETRIE,
                emptyGeoBean);
        }
        cidsBean.setProperty(FrontPropertyConstants.PROP__FRONTINFO + "." + FrontinfoPropertyConstants.PROP__GEOMETRIE
                    + "." + GeomPropertyConstants.PROP__GEO_FIELD,
            geom);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public CidsBean getCidsBean() {
        return frontBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getLastStrasseBean() {
        return lastStrasseBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        bindingGroup.unbind();
        frontBean = cidsBean;
        bindingGroup.bind();

        setEnabled(CidsAppBackend.getInstance().isEditable() && (cidsBean != null));

        try {
            if ((cidsBean != null)
                        && (cidsBean.getProperty(
                                FrontPropertyConstants.PROP__FRONTINFO
                                + "."
                                + FrontinfoPropertyConstants.PROP__GEOMETRIE) != null)) {
                bpanSrDetails.setBackgroundEnabled(true);
            } else {
                bpanSrDetails.setBackgroundEnabled(false);
            }
            if ((cidsBean == null)
                        || (getMultiBeanHelper().isValuesAllEquals(
                                FrontPropertyConstants.PROP__FRONTINFO
                                + "."
                                + FrontinfoPropertyConstants.PROP__STRASSE)
                            && (cidsBean.getProperty(
                                    FrontPropertyConstants.PROP__FRONTINFO
                                    + "."
                                    + FrontinfoPropertyConstants.PROP__STRASSE) == null))) {
                cboLageSR.setSelectedItem(null);
            }
        } catch (Exception e) {
            LOG.warn("problem when trying to set background enabled (or not). will turn the background off", e);
            bpanSrDetails.setBackgroundEnabled(false);
        }
        updateLageSrCbo();
        updateCrossReferences();
        attachBeanValidators();
    }

    /**
     * DOCUMENT ME!
     */
    public synchronized void updateCrossReferences() {
        if (frontBean != null) {
            final CidsBean finalFrontBean = frontBean;
            new SwingWorker<String, Void>() {

                    @Override
                    protected String doInBackground() throws Exception {
                        try {
                            final Collection<CrossReference> crossReference = (Collection)CidsAppBackend.getInstance()
                                        .getFrontenCrossReferencesForFrontid((Integer)finalFrontBean.getProperty("id"));

                            if (crossReference != null) {
                                String html = "<html><body><center>";
                                for (final CrossReference crossreference : crossReference) {
                                    final String link = crossreference.getEntityToKassenzeichen() + ":"
                                                + crossreference.getEntityToBezeichnung();
                                    html += "<a href=\"" + link + "\"><font size=\"-2\">" + link + "</font></a><br>";
                                }
                                html += "</center></body></html>";
                                return html;
                            }
                        } catch (final Exception ex) {
                            LOG.info(ex, ex);
                        }
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            final String html = get();
                            if (html != null) {
                                lblQuerverweise.setVisible(true);
                                edtQuer.setVisible(true);
                                scpQuer.setVisible(true);
                                edtQuer.setText(html);
                                edtQuer.setCaretPosition(0);
                            } else {
                                edtQuer.setText("");
                                lblQuerverweise.setVisible(false);
                                edtQuer.setVisible(false);
                                scpQuer.setVisible(false);
                            }
                        } catch (InterruptedException ex) {
                            Exceptions.printStackTrace(ex);
                        } catch (ExecutionException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                }.execute();
        } else {
            edtQuer.setText("");
            lblQuerverweise.setVisible(false);
            edtQuer.setVisible(false);
            scpQuer.setVisible(false);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void updateLageSrCbo() {
        if (getCidsBean() != null) {
            final CidsBean lageSRBean = (CidsBean)getCidsBean().getProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                            + FrontinfoPropertyConstants.PROP__LAGE_SR);
            if (lageSRBean != null) {
                cboSR.setSelectedItem(lageSRBean.getProperty("sr_klasse"));
            } else {
                cboSR.setSelectedItem((CidsBean)getCidsBean().getProperty(
                        FrontPropertyConstants.PROP__FRONTINFO
                                + "."
                                + FrontinfoPropertyConstants.PROP__SR_KLASSE_OR));
            }
            cboSR.setEnabled(isEnabled() && (lageSRBean == null)
                        && getMultiBeanHelper().isValuesAllEquals(
                            FrontPropertyConstants.PROP__FRONTINFO
                            + "."
                            + FrontinfoPropertyConstants.PROP__LAGE_SR));
        }
    }

    @Override
    public void editModeChanged() {
        setEnabled(CidsAppBackend.getInstance().isEditable() && (getCidsBean() != null));
    }

    @Override
    public void setEnabled(final boolean bln) {
        super.setEnabled(bln);
        txtBearbeitetDurch.setEditable(bln);
        txtBemSR.setEditable(bln);
        txtErfassungsdatum.setEditable(bln);
        txtLaengeGrafik.setEditable(bln);
        txtLaengeKorrektur.setEditable(bln);
        txtNummer.setEditable(bln);
        txtVeranlagungSR.setEditable(bln);
        cboSR.setEnabled(bln);
        cboStrasse.setEnabled(bln);
        cboLageSR.setEnabled(bln);
        cbAnteil.setEnabled(bln);
        cbBaulasten.setEnabled(bln);
        cbGarageStellplatz.setEnabled(bln);
        cbGrunddienstbarkeit.setEnabled(bln);
        cbQuadratwurzel.setEnabled(bln);
        txtWinkel.setEnabled(bln);

        txtBearbeitetDurch.setOpaque(bln);
        txtBemSR.setOpaque(bln);
        txtErfassungsdatum.setOpaque(bln);
        txtLaengeGrafik.setOpaque(bln);
        txtLaengeKorrektur.setOpaque(bln);
        txtNummer.setOpaque(bln);
        txtVeranlagungSR.setOpaque(bln);
        cboSR.setOpaque(bln);
        cboStrasse.setOpaque(bln);
        cboLageSR.setOpaque(bln);
        txtWinkel.setOpaque(bln);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  pCanvas  DOCUMENT ME!
     */
    public void setBackgroundPCanvas(final PCanvas pCanvas) {
        pCanvas.setBackground(getBackground());
        bpanSrDetails.setPCanvas(pCanvas);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   frontBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Validator getValidatorNummer(final CidsBean frontBean) {
        return new CidsBeanValidator(
                frontBean,
                FrontPropertyConstants.PROP__NUMMER) {

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
//                            final String newValue = Main.THIS.getRegenFlaechenTable().getValidFlaechenname(art);
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

    /**
     * DOCUMENT ME!
     *
     * @param   frontBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Validator getValidatorLaengeGrafik(final CidsBean frontBean) {
        return new CidsBeanValidator(
                frontBean,
                FrontPropertyConstants.PROP__FRONTINFO
                        + "."
                        + FrontinfoPropertyConstants.PROP__LAENGE_GRAFIK,
                FrontPropertyConstants.PROP__FRONTINFO
                        + "."
                        + FrontinfoPropertyConstants.PROP__GEOMETRIE) {

                @Override
                public ValidatorState performValidation() {
                    final CidsBean cidsBean = getCidsBean();
                    if (cidsBean == null) {
                        return null;
                    }

                    final Integer laenge_grafik = (Integer)cidsBean.getProperty(
                            FrontPropertyConstants.PROP__FRONTINFO
                                    + "."
                                    + FrontinfoPropertyConstants.PROP__LAENGE_GRAFIK);
                    final Geometry geom = SRFrontenDetailsPanel.getGeometry(cidsBean);
                    final Action action = new AbstractAction() {

                            @Override
                            public void actionPerformed(final ActionEvent event) {
                                final CidsBean cidsBean = getCidsBean();
                                final Geometry geom = SRFrontenDetailsPanel.getGeometry(cidsBean);

                                if (Main.getInstance().isInEditMode()) {
                                    if (geom != null) {
                                        final int answer = JOptionPane.showConfirmDialog(
                                                Main.getInstance(),
                                                "Soll die Länge aus der Grafik \u00FCbernommen werden?",
                                                "Länge automatisch setzen",
                                                JOptionPane.YES_NO_OPTION);
                                        if (answer == JOptionPane.YES_OPTION) {
                                            try {
                                                final int laenge_grafik = (int)Math.abs(geom.getLength());
                                                cidsBean.setProperty(
                                                    FrontPropertyConstants.PROP__FRONTINFO
                                                            + "."
                                                            + FrontinfoPropertyConstants.PROP__LAENGE_GRAFIK,
                                                    laenge_grafik);
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

                    if ((geom != null) && !geom.isValid()) {
                        return new ValidatorStateImpl(
                                ValidatorState.Type.ERROR,
                                "Die Geometrie ist ung\u00FCltig",
                                action);
                    } else if ((geom != null) && (laenge_grafik != (int)Math.abs(geom.getLength()))) {
                        return new ValidatorStateImpl(
                                ValidatorState.Type.WARNING,
                                "L\u00E4nge der Geometrie stimmt nicht \u00FCberein ("
                                        + ((int)(geom.getLength()))
                                        + ")",
                                action);
                    }
                    return new ValidatorStateImpl(ValidatorState.Type.VALID);
                }
            };
    }

    /**
     * DOCUMENT ME!
     *
     * @param   frontBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Validator getValidatorLaengeKorrektur(final CidsBean frontBean) {
        return new CidsBeanValidator(
                frontBean,
                FrontPropertyConstants.PROP__FRONTINFO
                        + "."
                        + FrontinfoPropertyConstants.PROP__LAENGE_KORREKTUR) {

                @Override
                public ValidatorState performValidation() {
                    final CidsBean cidsBean = getCidsBean();
                    if (cidsBean == null) {
                        return null;
                    }

                    Integer laenge_grafik = (Integer)cidsBean.getProperty(
                            FrontPropertyConstants.PROP__FRONTINFO
                                    + "."
                                    + FrontinfoPropertyConstants.PROP__LAENGE_GRAFIK);
                    final Integer laenge_korrektur = (Integer)cidsBean.getProperty(
                            FrontPropertyConstants.PROP__FRONTINFO
                                    + "."
                                    + FrontinfoPropertyConstants.PROP__LAENGE_KORREKTUR);
                    final Action action = new AbstractAction() {

                            @Override
                            public void actionPerformed(final ActionEvent event) {
                                final CidsBean cidsBean = getCidsBean();
                                final Geometry geom = SRFrontenDetailsPanel.getGeometry(cidsBean);

                                if (Main.getInstance().isInEditMode()) {
                                    if (geom != null) {
                                        final int answer = JOptionPane.showConfirmDialog(
                                                Main.getInstance(),
                                                "Soll die Länge aus der Grafik \u00FCbernommen werden?",
                                                "Länge automatisch setzen",
                                                JOptionPane.YES_NO_OPTION);
                                        if (answer == JOptionPane.YES_OPTION) {
                                            try {
                                                final Integer laenge_grafik = (Integer)cidsBean.getProperty(
                                                        FrontPropertyConstants.PROP__FRONTINFO
                                                                + "."
                                                                + FrontinfoPropertyConstants.PROP__LAENGE_GRAFIK);
                                                cidsBean.setProperty(
                                                    FrontPropertyConstants.PROP__FRONTINFO
                                                            + "."
                                                            + FrontinfoPropertyConstants.PROP__LAENGE_KORREKTUR,
                                                    laenge_grafik);
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
                            return new ValidatorStateImpl(
                                    ValidatorState.Type.WARNING,
                                    "Differenz zwischen Korrekturwert und Länge > 20m.",
                                    action);
                        }
                    }
                    return new ValidatorStateImpl(ValidatorState.Type.VALID);
                }
            };
    }

    /**
     * DOCUMENT ME!
     *
     * @param   frontBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Validator getValidatorDatumErfassung(final CidsBean frontBean) {
        return new CidsBeanValidator(
                frontBean,
                FrontPropertyConstants.PROP__ERFASSUNGSDATUM) {

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

    /**
     * DOCUMENT ME!
     *
     * @param   frontBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Validator getValidatorVeranlagungSR(final CidsBean frontBean) {
        return new CidsBeanValidator(
                frontBean,
                FrontPropertyConstants.PROP__FRONTINFO
                        + "."
                        + FrontinfoPropertyConstants.PROP__SR_VERANLAGUNG) {

                @Override
                public ValidatorState performValidation() {
                    final CidsBean cidsBean = getCidsBean();
                    if (cidsBean == null) {
                        return null;
                    }

                    final String veranlagungsdatum = (String)cidsBean.getProperty(
                            FrontPropertyConstants.PROP__FRONTINFO
                                    + "."
                                    + FrontinfoPropertyConstants.PROP__SR_VERANLAGUNG);

                    if (veranlagungsdatum != null) {
                        final boolean matches = Pattern.matches(
                                "\\d\\d/(01|02|03|04|05|06|07|08|09|10|11|12)",
                                veranlagungsdatum);
                        if (!matches) {
                            return new ValidatorStateImpl(
                                    ValidatorState.Type.ERROR,
                                    "Veranlagungsdatum muss im Format JJ/MM eingegeben werden.");
                        }
                    }
                    return new ValidatorStateImpl(ValidatorState.Type.VALID);
                }
            };
    }

    @Override
    public void hyperlinkUpdate(final HyperlinkEvent he) {
        final Thread t = new Thread() {

                @Override
                public void run() {
                    if (he.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                        try {
                            CidsAppBackend.getInstance().gotoKassenzeichen(he.getDescription());
                        } catch (Exception ex) {
                            LOG.error("Fehler im Hyperlinken", ex);
                        }
                    }
                }
            };
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();
    }

    @Override
    public CidsBean createDummyBean() {
        final CidsBean dummyBean = CidsAppBackend.getInstance()
                    .getVerdisMetaClass(VerdisMetaClassConstants.MC_FRONT)
                    .getEmptyInstance()
                    .getBean();
        final CidsBean frontinfoBean = CidsAppBackend.getInstance()
                    .getVerdisMetaClass(VerdisMetaClassConstants.MC_FRONTINFO)
                    .getEmptyInstance()
                    .getBean();
        final CidsBean geomBean = CidsAppBackend.getInstance()
                    .getVerdisMetaClass(VerdisMetaClassConstants.MC_GEOM)
                    .getEmptyInstance()
                    .getBean();
        try {
            dummyBean.setProperty(FrontPropertyConstants.PROP__FRONTINFO, frontinfoBean);
            dummyBean.setProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                        + FrontinfoPropertyConstants.PROP__GEOMETRIE,
                geomBean);
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }
        return dummyBean;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class SatzungsComboModelWorker extends SwingWorker<ComboBoxModel, Void> {

        //~ Instance fields ----------------------------------------------------

        private final JComboBox cb;
        private final String query;
        private final CidsBean selected;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new SatzungsComboModelWorker object.
         *
         * @param  cb        DOCUMENT ME!
         * @param  query     DOCUMENT ME!
         * @param  selected  DOCUMENT ME!
         */
        public SatzungsComboModelWorker(final JComboBox cb,
                final String query,
                final CidsBean selected) {
            this.cb = cb;
            this.query = query;
            this.selected = selected;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected ComboBoxModel doInBackground() throws Exception {
            cb.setEnabled(false);

            final Collection<CidsBean> cbs = CidsAppBackend.getInstance().getBeansByQuery(query);

            final DefaultComboBoxModel dcm = new DefaultComboBoxModel();
            dcm.addElement(null);

            for (final CidsBean cb : cbs) {
                dcm.addElement(cb);
            }
            return dcm;
        }

        @Override
        protected void done() {
            try {
                cb.setModel(get());
                cb.setSelectedItem(selected);
            } catch (Exception ex) {
                LOG.error(ex, ex);
            } finally {
                cb.setEnabled(isEnabled() && (getCidsBean() != null));
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class SatzungsConverter extends Converter<CidsBean, MetaObject> {

        //~ Methods ------------------------------------------------------------

        @Override
        public MetaObject convertForward(final CidsBean t) {
            if (t == null) {
                return null;
            }
            return t.getMetaObject();
        }

        @Override
        public CidsBean convertReverse(final MetaObject s) {
            if (s == null) {
                return null;
            }
            return s.getBean();
        }
    }
}
