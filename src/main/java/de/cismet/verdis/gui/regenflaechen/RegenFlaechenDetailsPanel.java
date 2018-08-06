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
package de.cismet.verdis.gui.regenflaechen;

import com.vividsolutions.jts.geom.Geometry;

import edu.umd.cs.piccolo.PCanvas;

import org.openide.util.Exceptions;

import java.awt.Color;
import java.awt.event.ActionEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.JTextComponent;

import de.cismet.cids.custom.util.BindingValidationSupport;
import de.cismet.cids.custom.util.VerdisUtils;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultBindableReferenceCombo;
import de.cismet.cids.editors.converters.SqlDateToStringConverter;

import de.cismet.cids.utils.multibean.EmbeddedMultiBeanDisplay;
import de.cismet.cids.utils.multibean.MultiBeanHelper;

import de.cismet.validation.*;

import de.cismet.validation.display.EmbeddedValidatorDisplay;

import de.cismet.validation.validator.CidsBeanValidator;

import de.cismet.verdis.CidsAppBackend;
import de.cismet.verdis.CrossReference;

import de.cismet.verdis.commons.constants.FlaechePropertyConstants;
import de.cismet.verdis.commons.constants.FlaechenartPropertyConstants;
import de.cismet.verdis.commons.constants.FlaecheninfoPropertyConstants;
import de.cismet.verdis.commons.constants.GeomPropertyConstants;
import de.cismet.verdis.commons.constants.KassenzeichenPropertyConstants;
import de.cismet.verdis.commons.constants.VerdisMetaClassConstants;

import de.cismet.verdis.gui.AbstractCidsBeanDetailsPanel;
import de.cismet.verdis.gui.Main;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class RegenFlaechenDetailsPanel extends AbstractCidsBeanDetailsPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            RegenFlaechenDetailsPanel.class);
    private static RegenFlaechenDetailsPanel INSTANCE;

    //~ Instance fields --------------------------------------------------------

    private CidsBean anschlussgradBean;

    private CidsBean flaecheBean;
    private final Validator bindingValidator;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.cismet.cismap.commons.gui.SimpleBackgroundedJPanel bpanRegenFlDetails;
    private javax.swing.JComboBox cboAnschlussgrad;
    private javax.swing.JComboBox cboBeschreibung;
    private javax.swing.JComboBox cboFlaechenart;
    private javax.swing.JCheckBox chkSperre;
    private javax.swing.JEditorPane edtQuer;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblAenderungsdatum;
    private javax.swing.JLabel lblAnschlussgrad;
    private javax.swing.JLabel lblAnteil;
    private javax.swing.JLabel lblBemerkung;
    private javax.swing.JLabel lblBeschreibung;
    private javax.swing.JLabel lblBezeichnung;
    private javax.swing.JLabel lblFEB_ID;
    private javax.swing.JLabel lblFlaechenart;
    private javax.swing.JLabel lblGroesseGrafik;
    private javax.swing.JLabel lblGroesseKorrektur;
    private javax.swing.JLabel lblSperre;
    private javax.swing.JLabel lblTeileigentumQuerverweise;
    private javax.swing.JLabel lblVeranlagungsdatum;
    private javax.swing.JScrollPane scpBemerkung;
    private javax.swing.JScrollPane scpQuer;
    private javax.swing.JTextField txtAenderungsdatum;
    private javax.swing.JTextField txtAnteil;
    private javax.swing.JTextArea txtBemerkung;
    private javax.swing.JTextField txtBezeichnung;
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
    private RegenFlaechenDetailsPanel() {
        UIManager.put("ComboBox.disabledForeground", Color.black);
        initComponents();

        ((DefaultBindableReferenceCombo)cboBeschreibung).setMetaClass(CidsAppBackend.getInstance().getVerdisMetaClass(
                VerdisMetaClassConstants.MC_FLAECHENBESCHREIBUNG));
        ((DefaultBindableReferenceCombo)cboAnschlussgrad).setMetaClass(CidsAppBackend.getInstance().getVerdisMetaClass(
                VerdisMetaClassConstants.MC_ANSCHLUSSGRAD));
        ((DefaultBindableReferenceCombo)cboFlaechenart).setMetaClass(CidsAppBackend.getInstance().getVerdisMetaClass(
                VerdisMetaClassConstants.MC_FLAECHENART));
        setEnabled(false);

        anschlussgradBean = CidsAppBackend.getInstance()
                    .getVerdisMetaObject(
                            1,
                            CidsAppBackend.getInstance().getVerdisMetaClass(VerdisMetaClassConstants.MC_ANSCHLUSSGRAD)
                                .getId())
                    .getBean();

        EmbeddedMultiBeanDisplay.registerComponentForProperty(
            txtBezeichnung,
            FlaechePropertyConstants.PROP__FLAECHENBEZEICHNUNG,
            getMultiBeanHelper());
        EmbeddedMultiBeanDisplay.registerComponentForProperty(
            txtGroesseGrafik,
            FlaechePropertyConstants.PROP__FLAECHENINFO
                    + "."
                    + FlaecheninfoPropertyConstants.PROP__GROESSE_GRAFIK,
            getMultiBeanHelper());
        EmbeddedMultiBeanDisplay.registerComponentForProperty(
            txtGroesseKorrektur,
            FlaechePropertyConstants.PROP__FLAECHENINFO
                    + "."
                    + FlaecheninfoPropertyConstants.PROP__GROESSE_KORREKTUR,
            getMultiBeanHelper());
        EmbeddedMultiBeanDisplay.registerComponentForProperty(
            cboFlaechenart,
            FlaechePropertyConstants.PROP__FLAECHENINFO
                    + "."
                    + FlaecheninfoPropertyConstants.PROP__FLAECHENART,
            getMultiBeanHelper());
        EmbeddedMultiBeanDisplay.registerComponentForProperty(
            cboAnschlussgrad,
            FlaechePropertyConstants.PROP__FLAECHENINFO
                    + "."
                    + FlaecheninfoPropertyConstants.PROP__ANSCHLUSSGRAD,
            getMultiBeanHelper());
        EmbeddedMultiBeanDisplay.registerComponentForProperty(
            cboBeschreibung,
            FlaechePropertyConstants.PROP__FLAECHENINFO
                    + "."
                    + FlaecheninfoPropertyConstants.PROP__BESCHREIBUNG,
            getMultiBeanHelper());
        EmbeddedMultiBeanDisplay.registerComponentForProperty(
            txtAnteil,
            FlaechePropertyConstants.PROP__ANTEIL,
            getMultiBeanHelper());
        EmbeddedMultiBeanDisplay.registerComponentForProperty(
            txtAenderungsdatum,
            FlaechePropertyConstants.PROP__DATUM_AENDERUNG,
            getMultiBeanHelper());
        EmbeddedMultiBeanDisplay.registerComponentForProperty(
            txtVeranlagungsdatum,
            FlaechePropertyConstants.PROP__DATUM_VERANLAGUNG,
            getMultiBeanHelper());
        EmbeddedMultiBeanDisplay.registerComponentForProperty(
            txtBemerkung,
            FlaechePropertyConstants.PROP__BEMERKUNG,
            getMultiBeanHelper());
        EmbeddedMultiBeanDisplay.registerComponentForProperty(
            chkSperre,
            FlaechePropertyConstants.PROP__SPERRE,
            getMultiBeanHelper());
        EmbeddedMultiBeanDisplay.registerComponentForProperty(
            txtSperreBemerkung,
            FlaechePropertyConstants.PROP__BEMERKUNG_SPERRE,
            getMultiBeanHelper());
        EmbeddedMultiBeanDisplay.registerComponentForProperty(
            txtFEB_ID,
            FlaechePropertyConstants.PROP__FEB_ID,
            getMultiBeanHelper());

        bindingValidator = BindingValidationSupport.attachBindingValidationToAllTargets(bindingGroup);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static RegenFlaechenDetailsPanel getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RegenFlaechenDetailsPanel();
            INSTANCE.edtQuer.addHyperlinkListener(INSTANCE);
        }
        return INSTANCE;
    }

    @Override
    public Validator getValidator() {
        // nur BindingValidator notwendig, der TabellenValidator validiert schon alle beans
        return bindingValidator;
    }

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
     * @param   flaecheBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Geometry getGeometry(final CidsBean flaecheBean) {
        if ((flaecheBean != null)
                    && (flaecheBean.getProperty(FlaechePropertyConstants.PROP__FLAECHENINFO) != null)
                    && (flaecheBean.getProperty(
                            FlaechePropertyConstants.PROP__FLAECHENINFO
                            + "."
                            + FlaecheninfoPropertyConstants.PROP__GEOMETRIE) != null)) {
            return (Geometry)flaecheBean.getProperty(
                    FlaechePropertyConstants.PROP__FLAECHENINFO
                            + "."
                            + FlaecheninfoPropertyConstants.PROP__GEOMETRIE
                            + "."
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
                        FlaechePropertyConstants.PROP__FLAECHENINFO
                        + "."
                        + FlaecheninfoPropertyConstants.PROP__GEOMETRIE)
                    == null) {
            final CidsBean emptyGeoBean = CidsAppBackend.getInstance()
                        .getVerdisMetaClass(VerdisMetaClassConstants.MC_GEOM)
                        .getEmptyInstance()
                        .getBean();
            cidsBean.setProperty(FlaechePropertyConstants.PROP__FLAECHENINFO + "."
                        + FlaecheninfoPropertyConstants.PROP__GEOMETRIE,
                emptyGeoBean);
        }
        cidsBean.setProperty(FlaechePropertyConstants.PROP__FLAECHENINFO + "."
                    + FlaecheninfoPropertyConstants.PROP__GEOMETRIE + "." + GeomPropertyConstants.PROP__GEO_FIELD,
            geom);
    }

    /**
     * DOCUMENT ME!
     */
    private void attachBeanValidators() {
        ValidatorHelper.removeAllNoBindingValidatorFromDisplay(txtBezeichnung);
        getValidatorFlaechenBezeichnung(flaecheBean).attachDisplay(EmbeddedValidatorDisplay.getEmbeddedDisplayFor(
                txtBezeichnung));
        ValidatorHelper.removeAllNoBindingValidatorFromDisplay(txtGroesseGrafik);
        getValidatorGroesseGrafik(flaecheBean).attachDisplay(EmbeddedValidatorDisplay.getEmbeddedDisplayFor(
                txtGroesseGrafik));
        ValidatorHelper.removeAllNoBindingValidatorFromDisplay(txtGroesseKorrektur);
        getValidatorGroesseKorrektur(flaecheBean).attachDisplay(EmbeddedValidatorDisplay.getEmbeddedDisplayFor(
                txtGroesseKorrektur));
        ValidatorHelper.removeAllNoBindingValidatorFromDisplay(txtAnteil);
        getValidatorAnteil(flaecheBean).attachDisplay(EmbeddedValidatorDisplay.getEmbeddedDisplayFor(txtAnteil));
        ValidatorHelper.removeAllNoBindingValidatorFromDisplay(txtAenderungsdatum);
        getValidatorDatumErfassung(flaecheBean).attachDisplay(EmbeddedValidatorDisplay.getEmbeddedDisplayFor(
                txtAenderungsdatum));
        ValidatorHelper.removeAllNoBindingValidatorFromDisplay(txtVeranlagungsdatum);
        getValidatorDatumVeranlagung(flaecheBean).attachDisplay(EmbeddedValidatorDisplay.getEmbeddedDisplayFor(
                txtVeranlagungsdatum));
        ValidatorHelper.removeAllNoBindingValidatorFromDisplay(txtFEB_ID);
        getValidatorFebId(flaecheBean).attachDisplay(EmbeddedValidatorDisplay.getEmbeddedDisplayFor(txtFEB_ID));

        ValidatorHelper.removeAllNoBindingValidatorFromDisplay(cboFlaechenart);
        getValidatorFlaechenart(flaecheBean).attachDisplay(EmbeddedValidatorDisplay.getEmbeddedDisplayFor(
                cboFlaechenart));
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
        lblAenderungsdatum = new javax.swing.JLabel();
        lblVeranlagungsdatum = new javax.swing.JLabel();
        lblBemerkung = new javax.swing.JLabel();
        txtAnteil = new javax.swing.JTextField();
        txtAenderungsdatum = new javax.swing.JTextField();
        txtVeranlagungsdatum = new javax.swing.JTextField();
        cboFlaechenart = createComboArtForEdit();
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
        jCheckBox1 = new javax.swing.JCheckBox();

        setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.BorderLayout());

        bpanRegenFlDetails.setOpaque(false);
        bpanRegenFlDetails.setLayout(new java.awt.GridBagLayout());

        lblBezeichnung.setText(org.openide.util.NbBundle.getMessage(
                RegenFlaechenDetailsPanel.class,
                "RegenFlaechenDetailsPanel.lblBezeichnung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        bpanRegenFlDetails.add(lblBezeichnung, gridBagConstraints);

        lblGroesseGrafik.setText(org.openide.util.NbBundle.getMessage(
                RegenFlaechenDetailsPanel.class,
                "RegenFlaechenDetailsPanel.lblGroesseGrafik.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        bpanRegenFlDetails.add(lblGroesseGrafik, gridBagConstraints);

        lblGroesseKorrektur.setText(org.openide.util.NbBundle.getMessage(
                RegenFlaechenDetailsPanel.class,
                "RegenFlaechenDetailsPanel.lblGroesseKorrektur.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        bpanRegenFlDetails.add(lblGroesseKorrektur, gridBagConstraints);

        lblFlaechenart.setText(org.openide.util.NbBundle.getMessage(
                RegenFlaechenDetailsPanel.class,
                "RegenFlaechenDetailsPanel.lblFlaechenart.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        bpanRegenFlDetails.add(lblFlaechenart, gridBagConstraints);

        lblAnschlussgrad.setText(org.openide.util.NbBundle.getMessage(
                RegenFlaechenDetailsPanel.class,
                "RegenFlaechenDetailsPanel.lblAnschlussgrad.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        bpanRegenFlDetails.add(lblAnschlussgrad, gridBagConstraints);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.flaechenbezeichnung}"),
                txtBezeichnung,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                de.cismet.verdis.commons.constants.FlaechePropertyConstants.PROP__FLAECHENBEZEICHNUNG);
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

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.flaecheninfo.groesse_aus_grafik}"),
                txtGroesseGrafik,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                FlaechePropertyConstants.PROP__FLAECHENINFO
                        + "."
                        + FlaecheninfoPropertyConstants.PROP__GROESSE_GRAFIK);
        bindingGroup.addBinding(binding);

        txtGroesseGrafik.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    txtGroesseGrafikActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 2);
        bpanRegenFlDetails.add(txtGroesseGrafik, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.flaecheninfo.groesse_korrektur}"),
                txtGroesseKorrektur,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                de.cismet.verdis.commons.constants.FlaechePropertyConstants.PROP__FLAECHENINFO
                        + "."
                        + FlaecheninfoPropertyConstants.PROP__GROESSE_KORREKTUR);
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 2);
        bpanRegenFlDetails.add(txtGroesseKorrektur, gridBagConstraints);

        lblAnteil.setText(org.openide.util.NbBundle.getMessage(
                RegenFlaechenDetailsPanel.class,
                "RegenFlaechenDetailsPanel.lblAnteil.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        bpanRegenFlDetails.add(lblAnteil, gridBagConstraints);

        lblAenderungsdatum.setText(org.openide.util.NbBundle.getMessage(
                RegenFlaechenDetailsPanel.class,
                "RegenFlaechenDetailsPanel.lblAenderungsdatum.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        bpanRegenFlDetails.add(lblAenderungsdatum, gridBagConstraints);

        lblVeranlagungsdatum.setText(org.openide.util.NbBundle.getMessage(
                RegenFlaechenDetailsPanel.class,
                "RegenFlaechenDetailsPanel.lblVeranlagungsdatum.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        bpanRegenFlDetails.add(lblVeranlagungsdatum, gridBagConstraints);

        lblBemerkung.setText(org.openide.util.NbBundle.getMessage(
                RegenFlaechenDetailsPanel.class,
                "RegenFlaechenDetailsPanel.lblBemerkung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        bpanRegenFlDetails.add(lblBemerkung, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.anteil}"),
                txtAnteil,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                de.cismet.verdis.commons.constants.FlaechePropertyConstants.PROP__ANTEIL);
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        binding.setConverter(new de.cismet.verdis.gui.converter.EmptyFloatToStringConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 2);
        bpanRegenFlDetails.add(txtAnteil, gridBagConstraints);

        txtAenderungsdatum.setEditable(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.datum_erfassung}"),
                txtAenderungsdatum,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                KassenzeichenPropertyConstants.PROP__DATUM_ERFASSUNG);
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
        bpanRegenFlDetails.add(txtAenderungsdatum, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.datum_veranlagung}"),
                txtVeranlagungsdatum,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                KassenzeichenPropertyConstants.PROP__DATUM_VERANLAGUNG);
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

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.flaecheninfo.flaechenart}"),
                cboFlaechenart,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
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

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.flaecheninfo.anschlussgrad}"),
                cboAnschlussgrad,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
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

        txtBemerkung.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 11)); // NOI18N
        txtBemerkung.setLineWrap(true);
        txtBemerkung.setRows(2);
        txtBemerkung.setMinimumSize(new java.awt.Dimension(73, 38));
        txtBemerkung.setOpaque(false);
        txtBemerkung.setPreferredSize(new java.awt.Dimension(21, 756));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bemerkung}"),
                txtBemerkung,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
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

        lblSperre.setText(org.openide.util.NbBundle.getMessage(
                RegenFlaechenDetailsPanel.class,
                "RegenFlaechenDetailsPanel.lblSperre.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        bpanRegenFlDetails.add(lblSperre, gridBagConstraints);

        chkSperre.setForeground(java.awt.Color.red);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.sperre}"),
                chkSperre,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        chkSperre.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    chkSperreActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 0);
        bpanRegenFlDetails.add(chkSperre, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.feb_id}"),
                txtFEB_ID,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                de.cismet.verdis.commons.constants.FlaechePropertyConstants.PROP__FEB_ID);
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

        lblFEB_ID.setText(org.openide.util.NbBundle.getMessage(
                RegenFlaechenDetailsPanel.class,
                "RegenFlaechenDetailsPanel.lblFEB_ID.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        bpanRegenFlDetails.add(lblFEB_ID, gridBagConstraints);

        txtSperreBemerkung.setEditable(false);
        txtSperreBemerkung.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.background"));
        txtSperreBemerkung.setForeground(java.awt.Color.red);
        txtSperreBemerkung.setBorder(null);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bemerkung_sperre}"),
                txtSperreBemerkung,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
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

        lblTeileigentumQuerverweise.setText(org.openide.util.NbBundle.getMessage(
                RegenFlaechenDetailsPanel.class,
                "RegenFlaechenDetailsPanel.lblTeileigentumQuerverweise.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        bpanRegenFlDetails.add(lblTeileigentumQuerverweise, gridBagConstraints);

        scpQuer.setOpaque(false);

        edtQuer.setEditable(false);
        edtQuer.setContentType("text/html"); // NOI18N
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

        lblBeschreibung.setText(org.openide.util.NbBundle.getMessage(
                RegenFlaechenDetailsPanel.class,
                "RegenFlaechenDetailsPanel.lblBeschreibung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        bpanRegenFlDetails.add(lblBeschreibung, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.flaecheninfo.beschreibung}"),
                cboBeschreibung,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
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

        jCheckBox1.setText(org.openide.util.NbBundle.getMessage(
                RegenFlaechenDetailsPanel.class,
                "RegenFlaechenDetailsPanel.jCheckBox1.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.flaecheninfo.nachgewiesen}"),
                jCheckBox1,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 2);
        bpanRegenFlDetails.add(jCheckBox1, gridBagConstraints);

        jPanel2.add(bpanRegenFlDetails, java.awt.BorderLayout.CENTER);

        add(jPanel2, java.awt.BorderLayout.CENTER);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void txtGroesseGrafikActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_txtGroesseGrafikActionPerformed
        // TODO add your handling code here:
    } //GEN-LAST:event_txtGroesseGrafikActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void chkSperreActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_chkSperreActionPerformed
        final boolean sperre = chkSperre.isSelected();
        if (sperre) {
            final String answer = JOptionPane.showInputDialog(Main.getInstance().getRootPane(),
                    "Bitte eine Bemerkung zur Sperre angeben.",
                    txtSperreBemerkung.getText());
            if (answer == null) {
                chkSperre.setSelected(false);
            }
            txtSperreBemerkung.setText(answer);
        } else {
            txtSperreBemerkung.setText(null);
        }
    }                                                                             //GEN-LAST:event_chkSperreActionPerformed

    @Override
    public CidsBean getCidsBean() {
        return flaecheBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        if ((cidsBean != null) && cidsBean.equals(flaecheBean)) {
            return;
        }

        flaecheBean = cidsBean;
        setEnabled(CidsAppBackend.getInstance().isEditable() && (cidsBean != null));
//        DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(bindingGroup, cidsBean);
        if (cidsBean != null) {
            bindingGroup.unbind();
            ((DefaultBindableReferenceCombo)cboFlaechenart).reload(false);
            bindingGroup.bind();
        } else {
            bindingGroup.unbind();
            hideContent(true);
        }
        try {
            if ((cidsBean != null)
                        && (cidsBean.getProperty(
                                FlaechePropertyConstants.PROP__FLAECHENINFO
                                + "."
                                + FlaecheninfoPropertyConstants.PROP__GEOMETRIE) != null)) {
                bpanRegenFlDetails.setBackgroundEnabled(true);
            } else {
                bpanRegenFlDetails.setBackgroundEnabled(false);
            }
        } catch (Exception e) {
            LOG.warn("problem when trying to set background enabled (or not). will turn the background off", e);
            bpanRegenFlDetails.setBackgroundEnabled(false);
        }
        if (cidsBean != null) {
            updateCrossReferences();

            attachBeanValidators();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  hide  DOCUMENT ME!
     */
    private void hideContent(final boolean hide) {
        final JTextComponent[] txts = new JTextComponent[] {
                txtBezeichnung,
                txtGroesseGrafik,
                txtGroesseKorrektur,
                txtAnteil,
                txtAenderungsdatum,
                txtVeranlagungsdatum,
                txtBemerkung,
                txtFEB_ID,
                edtQuer
            };

        final JComboBox[] combos = new JComboBox[] {
                cboFlaechenart, cboAnschlussgrad,
                cboBeschreibung
            };

        if (hide) {
            for (final JTextComponent c : txts) {
                c.setText("");
            }
            for (final JComboBox c : combos) {
                c.getModel().setSelectedItem(null);
            }
            chkSperre.setSelected(false);
        }
    }

    /**
     * DOCUMENT ME!
     */
    public synchronized void updateCrossReferences() {
        if ((flaecheBean != null) && (flaecheBean.getProperty("id") != null)) {
            new SwingWorker<String, Void>() {

                    @Override
                    protected String doInBackground() throws Exception {
                        final Collection<CrossReference> crossReference = CidsAppBackend.getInstance()
                                    .getFlaechenCrossReferencesForFlaecheid((Integer)flaecheBean.getProperty("id"));

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

                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            final String html = get();
                            if (html != null) {
                                lblTeileigentumQuerverweise.setVisible(true);
                                edtQuer.setVisible(true);
                                scpQuer.setVisible(true);
                                edtQuer.setText(html);
                                edtQuer.setCaretPosition(0);
                            } else {
                                edtQuer.setText("");
                                lblTeileigentumQuerverweise.setVisible(false);
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
            lblTeileigentumQuerverweise.setVisible(false);
            edtQuer.setVisible(false);
            scpQuer.setVisible(false);
        }
    }

    @Override
    public void editModeChanged() {
        setEnabled(CidsAppBackend.getInstance().isEditable() && (getCidsBean() != null));
    }

    @Override
    public final void setEnabled(final boolean b) {
        super.setEnabled(b);
        txtAnteil.setEnabled(true);
        txtAnteil.setEditable(b);
        txtBemerkung.setEnabled(true);
        txtBemerkung.setEditable(b);
        txtBezeichnung.setEnabled(true);
        txtBezeichnung.setEditable(b);
        txtFEB_ID.setEnabled(true);
        txtFEB_ID.setEditable(b);
        try {
            txtGroesseGrafik.setEditable(b
                        && (VerdisUtils.PROPVAL_ART_VORLAEUFIGEVERANLASSUNG
                            != (Integer)getCidsBean().getProperty(
                                FlaechePropertyConstants.PROP__FLAECHENINFO
                                + "."
                                + FlaecheninfoPropertyConstants.PROP__FLAECHENART
                                + "."
                                + FlaechenartPropertyConstants.PROP__ID)));
        } catch (final Exception ex) {
            txtGroesseGrafik.setEditable(b);
        }
        txtGroesseGrafik.setEnabled(true);
        txtGroesseKorrektur.setEditable(b);
        txtGroesseKorrektur.setEnabled(true);
        txtVeranlagungsdatum.setEditable(b);
        txtVeranlagungsdatum.setEnabled(true);
        chkSperre.setEnabled(b);
        try {
            cboAnschlussgrad.setEnabled(b
                        && (VerdisUtils.PROPVAL_ART_VORLAEUFIGEVERANLASSUNG
                            != (Integer)getCidsBean().getProperty(
                                FlaechePropertyConstants.PROP__FLAECHENINFO
                                + "."
                                + FlaecheninfoPropertyConstants.PROP__FLAECHENART
                                + "."
                                + FlaechenartPropertyConstants.PROP__ID)));
        } catch (final Exception ex) {
            cboAnschlussgrad.setEnabled(b);
        }
        cboFlaechenart.setEnabled(b);
        cboBeschreibung.setEnabled(b);
        // Opacity
        txtAnteil.setOpaque(b);
        txtBemerkung.setOpaque(b);
        txtBezeichnung.setOpaque(b);
        txtAenderungsdatum.setOpaque(b);
        txtFEB_ID.setOpaque(b);
        txtGroesseGrafik.setOpaque(b);
        txtGroesseKorrektur.setOpaque(b);
        txtVeranlagungsdatum.setOpaque(b);
        chkSperre.setOpaque(b);

        cboAnschlussgrad.setOpaque(b);
        cboFlaechenart.setOpaque(b);
        cboBeschreibung.setOpaque(b);

        scpBemerkung.setOpaque(b);
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

    /**
     * DOCUMENT ME!
     *
     * @param   flaecheBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Validator getValidatorFlaechenBezeichnung(final CidsBean flaecheBean) {
        final MultiBeanHelper mbh = RegenFlaechenDetailsPanel.getInstance().getMultiBeanHelper();
        return new CidsBeanValidator(
                flaecheBean,
                FlaechePropertyConstants.PROP__FLAECHENBEZEICHNUNG,
                FlaechePropertyConstants.PROP__FLAECHENINFO
                        + "."
                        + FlaecheninfoPropertyConstants.PROP__FLAECHENART) {

                @Override
                public ValidatorState performValidation() {
                    final boolean doNotValidate = (flaecheBean == null)
                                || ((getTriggerdByProperty() != null)
                                    && mbh.getAttachedProperties().contains(getTriggerdByProperty())
                                    && !mbh.isValuesAllEquals(getTriggerdByProperty()));
                    if (doNotValidate && ((flaecheBean == null) || flaecheBean.equals(mbh.getDummyBean()))) {
                        return null;
                    } else {
                        final String bezeichnung = (String)flaecheBean.getProperty(
                                FlaechePropertyConstants.PROP__FLAECHENBEZEICHNUNG);
                        final int art =
                            (flaecheBean.getProperty(
                                    FlaechePropertyConstants.PROP__FLAECHENINFO
                                            + "."
                                            + FlaecheninfoPropertyConstants.PROP__FLAECHENART
                                            + "."
                                            + FlaechenartPropertyConstants.PROP__ID)
                                        == null)
                            ? 0
                            : (Integer)flaecheBean.getProperty(
                                FlaechePropertyConstants.PROP__FLAECHENINFO
                                        + "."
                                        + FlaecheninfoPropertyConstants.PROP__FLAECHENART
                                        + "."
                                        + FlaechenartPropertyConstants.PROP__ID);
                        final Action action = new AbstractAction() {

                                @Override
                                public void actionPerformed(final ActionEvent e) {
                                    final int answer = JOptionPane.showConfirmDialog(
                                            Main.getInstance(),
                                            "Soll die n\u00E4chste freie Bezeichnung gew\u00E4hlt werden?",
                                            "Bezeichnung automatisch setzen",
                                            JOptionPane.YES_NO_OPTION);
                                    if (answer == JOptionPane.YES_OPTION) {
                                        int art;
                                        try {
                                            art = (Integer)flaecheBean.getProperty(
                                                    FlaechePropertyConstants.PROP__FLAECHENINFO
                                                            + "."
                                                            + FlaecheninfoPropertyConstants.PROP__FLAECHENART
                                                            + "."
                                                            + FlaechenartPropertyConstants.PROP__ID);
                                        } catch (final NumberFormatException ex) {
                                            art = 0;
                                        }
                                        final String newValue = Main.getInstance()
                                                    .getRegenFlaechenTable()
                                                    .getValidFlaechenname(art);
                                        try {
                                            flaecheBean.setProperty(
                                                FlaechePropertyConstants.PROP__FLAECHENBEZEICHNUNG,
                                                newValue);
                                        } catch (Exception ex) {
                                            if (LOG.isDebugEnabled()) {
                                                LOG.debug("error while setting flaechenbezeichnung", ex);
                                            }
                                        }
                                    }
                                }
                            };

                        boolean numerisch = false;
                        Integer tester = null;
                        try {
                            tester = Integer.parseInt(bezeichnung);
                            numerisch = true;
                        } catch (final Exception ex) {
                            numerisch = false;
                        }

                        if (art == VerdisUtils.PROPVAL_ART_VORLAEUFIGEVERANLASSUNG) {
                            if (!"A".equals(bezeichnung)) {
                                return new ValidatorStateImpl(
                                        ValidatorState.Type.ERROR,
                                        "Fl\u00E4chenbezeichnung muss \"A\" sein.",
                                        action);
                            }
                        } else if ((art == VerdisUtils.PROPVAL_ART_DACH)
                                    || (art == VerdisUtils.PROPVAL_ART_GRUENDACH)) {
                            if (!numerisch) {
                                return new ValidatorStateImpl(
                                        ValidatorState.Type.ERROR,
                                        "Fl\u00E4chenbezeichnung muss eine Zahl sein.",
                                        action);
                            } else {
                                if ((tester.intValue() > 1000) || (tester.intValue() < 0)) {
                                    return new ValidatorStateImpl(
                                            ValidatorState.Type.ERROR,
                                            "Fl\u00E4chenbezeichnung muss zwischen 0 und 1000 liegen.",
                                            action);
                                }
                            }
                        } else {
                            if (bezeichnung != null) {
                                final int len = bezeichnung.length();
                                if (numerisch || ((len > 3) || ((len == 3) && (bezeichnung.compareTo("BBB") > 0)))) {
                                    return new ValidatorStateImpl(
                                            ValidatorState.Type.ERROR,
                                            "Fl\u00E4chenbezeichnung muss zwischen A und BBB liegen.",
                                            action);
                                }
                            }
                        }
                        return new ValidatorStateImpl(ValidatorState.Type.VALID);
                    }
                }
            };
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flaecheBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Validator getValidatorGroesseGrafik(final CidsBean flaecheBean) {
        final MultiBeanHelper mbh = RegenFlaechenDetailsPanel.getInstance().getMultiBeanHelper();
        return new CidsBeanValidator(
                flaecheBean,
                FlaechePropertyConstants.PROP__FLAECHENINFO
                        + "."
                        + FlaecheninfoPropertyConstants.PROP__GROESSE_GRAFIK,
                FlaechePropertyConstants.PROP__FLAECHENINFO
                        + "."
                        + FlaecheninfoPropertyConstants.PROP__FLAECHENART,
                FlaechePropertyConstants.PROP__FLAECHENINFO
                        + "."
                        + FlaecheninfoPropertyConstants.PROP__GEOMETRIE) {

                @Override
                public ValidatorState performValidation() {
                    final boolean doNotValidate = (flaecheBean == null)
                                || (mbh.getAttachedProperties().contains(getTriggerdByProperty())
                                    && !mbh.isValuesAllEquals(getTriggerdByProperty()));
                    if (doNotValidate && ((flaecheBean == null) || flaecheBean.equals(mbh.getDummyBean()))) {
                        return null;
                    } else {
                        final CidsBean backupBean = Main.getInstance()
                                    .getRegenFlaechenTable()
                                    .getBeanBackup(flaecheBean);
                        final Integer groesseGrafik = (Integer)flaecheBean.getProperty(
                                FlaechePropertyConstants.PROP__FLAECHENINFO
                                        + "."
                                        + FlaecheninfoPropertyConstants.PROP__GROESSE_GRAFIK);
                        if (backupBean != null) {
                            final Integer backupGroesseGrafik = (Integer)backupBean.getProperty(
                                    FlaechePropertyConstants.PROP__FLAECHENINFO
                                            + "."
                                            + FlaecheninfoPropertyConstants.PROP__GROESSE_GRAFIK);
                            if ((backupGroesseGrafik != null) && (groesseGrafik != null)
                                        && (Math.abs(backupGroesseGrafik - groesseGrafik)
                                            > CidsAppBackend.getInstance().getAppPreferences()
                                            .getNachgewiesenFalseThreshold())) {
                                try {
                                    flaecheBean.setProperty(FlaechePropertyConstants.PROP__FLAECHENINFO
                                                + "."
                                                + FlaecheninfoPropertyConstants.PROP__NACHGEWIESEN,
                                        false);
                                } catch (final Exception ex) {
                                    if (LOG.isDebugEnabled()) {
                                        LOG.debug("error while setting nachgewiesen", ex);
                                    }
                                }
                            }
                        }

                        final Geometry geom = RegenFlaechenDetailsPanel.getGeometry(flaecheBean);
                        final Action action = new AbstractAction() {

                                @Override
                                public void actionPerformed(final ActionEvent event) {
                                    if (Main.getInstance().isInEditMode()) {
                                        if (VerdisUtils.PROPVAL_ART_VORLAEUFIGEVERANLASSUNG
                                                    == (Integer)flaecheBean.getProperty(
                                                        FlaechePropertyConstants.PROP__FLAECHENINFO
                                                        + "."
                                                        + FlaecheninfoPropertyConstants.PROP__FLAECHENART
                                                        + "."
                                                        + FlaechenartPropertyConstants.PROP__ID)) {
                                            try {
                                                flaecheBean.setProperty(
                                                    FlaechePropertyConstants.PROP__FLAECHENINFO
                                                            + "."
                                                            + FlaecheninfoPropertyConstants.PROP__GROESSE_GRAFIK,
                                                    null);
                                            } catch (final Exception ex) {
                                                if (LOG.isDebugEnabled()) {
                                                    LOG.debug("error while setting groesse_aus_grafik", ex);
                                                }
                                            }
                                        } else if (geom != null) {
                                            final int answer = JOptionPane.showConfirmDialog(
                                                    Main.getInstance(),
                                                    "Soll die Gr\u00F6\u00DFe aus der Grafik \u00FCbernommen werden?",
                                                    "Gr\u00F6\u00DFe automatisch setzen",
                                                    JOptionPane.YES_NO_OPTION);
                                            if (answer == JOptionPane.YES_OPTION) {
                                                try {
                                                    final Integer gr_grafik = new Integer((int)(geom.getArea()));
                                                    flaecheBean.setProperty(
                                                        FlaechePropertyConstants.PROP__FLAECHENINFO
                                                                + "."
                                                                + FlaecheninfoPropertyConstants.PROP__GROESSE_GRAFIK,
                                                        gr_grafik);
                                                } catch (final Exception ex) {
                                                    if (LOG.isDebugEnabled()) {
                                                        LOG.debug("error while setting groesse_aus_grafik", ex);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            };

                        final Integer artId = (Integer)flaecheBean.getProperty(
                                FlaechePropertyConstants.PROP__FLAECHENINFO
                                        + "."
                                        + FlaecheninfoPropertyConstants.PROP__FLAECHENART
                                        + "."
                                        + FlaechenartPropertyConstants.PROP__ID);
                        if ((geom != null) && !geom.isValid()) {
                            return new ValidatorStateImpl(
                                    ValidatorState.Type.ERROR,
                                    "Die Geometrie ist ung\u00FCltig",
                                    action);
                        } else if (artId == null) {
                            return new ValidatorStateImpl(
                                    ValidatorState.Type.WARNING,
                                    "kann nicht validiert werden, Flächenart ist nicht gesetzt.");
                        } else if (groesseGrafik == null) {
                            if (VerdisUtils.PROPVAL_ART_VORLAEUFIGEVERANLASSUNG != artId) {
                                return new ValidatorStateImpl(ValidatorState.Type.ERROR, "Wert ist leer", action);
                            } else {
                                return new ValidatorStateImpl(ValidatorState.Type.VALID);
                            }
                        } else if (VerdisUtils.PROPVAL_ART_VORLAEUFIGEVERANLASSUNG == artId) {
                            return new ValidatorStateImpl(ValidatorState.Type.ERROR, "Wert muss leer sein", action);
                        } else if ((geom != null) && !groesseGrafik.equals(new Integer((int)(geom.getArea())))) {
                            return new ValidatorStateImpl(
                                    ValidatorState.Type.WARNING,
                                    "Fl\u00E4che der Geometrie stimmt nicht \u00FCberein ("
                                            + ((int)(geom.getArea()))
                                            + ")",
                                    action);
                        }
                        return new ValidatorStateImpl(ValidatorState.Type.VALID);
                    }
                }
            };
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flaecheBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Validator getValidatorGroesseKorrektur(final CidsBean flaecheBean) {
        final MultiBeanHelper mbh = RegenFlaechenDetailsPanel.getInstance().getMultiBeanHelper();
        return new CidsBeanValidator(
                flaecheBean,
                FlaechePropertyConstants.PROP__FLAECHENINFO
                        + "."
                        + FlaecheninfoPropertyConstants.PROP__GROESSE_KORREKTUR,
                FlaechePropertyConstants.PROP__FLAECHENINFO
                        + "."
                        + FlaecheninfoPropertyConstants.PROP__FLAECHENART,
                FlaechePropertyConstants.PROP__FLAECHENINFO
                        + "."
                        + FlaecheninfoPropertyConstants.PROP__GROESSE_GRAFIK) {

                @Override
                public ValidatorState performValidation() {
                    final boolean doNotValidate = (flaecheBean == null)
                                || ((getTriggerdByProperty() != null)
                                    && mbh.getAttachedProperties().contains(getTriggerdByProperty())
                                    && !mbh.isValuesAllEquals(getTriggerdByProperty()));
                    if (doNotValidate && ((flaecheBean == null) || flaecheBean.equals(mbh.getDummyBean()))) {
                        return null;
                    } else {
                        final Integer groesseGrafik = (Integer)flaecheBean.getProperty(
                                FlaechePropertyConstants.PROP__FLAECHENINFO
                                        + "."
                                        + FlaecheninfoPropertyConstants.PROP__GROESSE_GRAFIK);
                        final Integer groesseKorrektur = (Integer)flaecheBean.getProperty(
                                FlaechePropertyConstants.PROP__FLAECHENINFO
                                        + "."
                                        + FlaecheninfoPropertyConstants.PROP__GROESSE_KORREKTUR);

                        final CidsBean backupBean = Main.getInstance()
                                    .getRegenFlaechenTable()
                                    .getBeanBackup(flaecheBean);
                        if (backupBean != null) {
                            final Integer backupGroesseKorrektur = (Integer)backupBean.getProperty(
                                    FlaechePropertyConstants.PROP__FLAECHENINFO
                                            + "."
                                            + FlaecheninfoPropertyConstants.PROP__GROESSE_KORREKTUR);

                            if ((backupGroesseKorrektur != null) && (groesseKorrektur != null)
                                        && (Math.abs(backupGroesseKorrektur - groesseKorrektur)
                                            > CidsAppBackend.getInstance().getAppPreferences()
                                            .getNachgewiesenFalseThreshold())) {
                                try {
                                    flaecheBean.setProperty(FlaechePropertyConstants.PROP__FLAECHENINFO
                                                + "."
                                                + FlaecheninfoPropertyConstants.PROP__NACHGEWIESEN,
                                        false);
                                } catch (final Exception ex) {
                                    if (LOG.isDebugEnabled()) {
                                        LOG.debug("error while setting nachgewiesen", ex);
                                    }
                                }
                            }
                        }

                        final Action action = new AbstractAction() {

                                @Override
                                public void actionPerformed(final ActionEvent event) {
                                    final Geometry geom = RegenFlaechenDetailsPanel.getGeometry(flaecheBean);

                                    if (Main.getInstance().isInEditMode()) {
                                        if (geom != null) {
                                            final int answer = JOptionPane.showConfirmDialog(
                                                    Main.getInstance(),
                                                    "Soll die Gr\u00F6\u00DFe aus dem Feld \"Gr\u00F6\u00DFe (Grafik)\" \u00FCbernommen werden?",
                                                    "Gr\u00F6\u00DFe automatisch setzen",
                                                    JOptionPane.YES_NO_OPTION);
                                            if (answer == JOptionPane.YES_OPTION) {
                                                try {
                                                    final Integer gr_grafik = (Integer)flaecheBean.getProperty(
                                                            FlaechePropertyConstants.PROP__FLAECHENINFO
                                                                    + "."
                                                                    + FlaecheninfoPropertyConstants.PROP__GROESSE_GRAFIK);
                                                    flaecheBean.setProperty(
                                                        FlaechePropertyConstants.PROP__FLAECHENINFO
                                                                + "."
                                                                + FlaecheninfoPropertyConstants.PROP__GROESSE_KORREKTUR,
                                                        gr_grafik);
                                                } catch (final Exception ex) {
                                                    if (LOG.isDebugEnabled()) {
                                                        LOG.debug("error while setting groesse_korrektur", ex);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            };
                        if (groesseGrafik == null) {
                            return new ValidatorStateImpl(ValidatorState.Type.WARNING, "Wert ist leer");
                        } else if (groesseKorrektur != null) {
                            final int diff = groesseKorrektur.intValue() - groesseGrafik.intValue();
                            if (Math.abs(diff) > 20) {
                                return new ValidatorStateImpl(
                                        ValidatorState.Type.WARNING,
                                        "Differenz zwischen Korrekturwert und Gr\u00F6\u00DFe > 20m.",
                                        action);
                            }
                        }
                        return new ValidatorStateImpl(ValidatorState.Type.VALID);
                    }
                }
            };
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flaecheBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Validator getValidatorAnteil(final CidsBean flaecheBean) {
        final MultiBeanHelper mbh = RegenFlaechenDetailsPanel.getInstance().getMultiBeanHelper();
        return new CidsBeanValidator(flaecheBean, FlaechePropertyConstants.PROP__ANTEIL) {

                @Override
                public ValidatorState performValidation() {
                    final boolean doNotValidate = (flaecheBean == null)
                                || ((getTriggerdByProperty() != null)
                                    && mbh.getAttachedProperties().contains(getTriggerdByProperty())
                                    && !mbh.isValuesAllEquals(getTriggerdByProperty()));
                    if (doNotValidate && ((flaecheBean == null) || flaecheBean.equals(mbh.getDummyBean()))) {
                        return null;
                    } else {
                        final Float anteil = (Float)flaecheBean.getProperty(FlaechePropertyConstants.PROP__ANTEIL);
                        final Integer gr_grafik = (Integer)flaecheBean.getProperty(
                                FlaechePropertyConstants.PROP__FLAECHENINFO
                                        + "."
                                        + FlaecheninfoPropertyConstants.PROP__GROESSE_GRAFIK);
                        final Integer gr_korrektur = (Integer)flaecheBean.getProperty(
                                FlaechePropertyConstants.PROP__FLAECHENINFO
                                        + "."
                                        + FlaecheninfoPropertyConstants.PROP__GROESSE_KORREKTUR);

                        if (anteil != null) {
                            if ((gr_korrektur != null) && (anteil.intValue() > gr_korrektur.intValue())) {
                                return new ValidatorStateImpl(
                                        ValidatorState.Type.ERROR,
                                        "Anteil ist h\u00F6her als Gr\u00F6\u00DFe.");
                            } else if ((gr_grafik != null) && (anteil.intValue() > gr_grafik.intValue())) {
                                return new ValidatorStateImpl(
                                        ValidatorState.Type.ERROR,
                                        "Anteil ist h\u00F6her als Gr\u00F6\u00DFe.");
                            }
                        }
                        return new ValidatorStateImpl(ValidatorState.Type.VALID);
                    }
                }
            };
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flaecheBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Validator getValidatorDatumErfassung(final CidsBean flaecheBean) {
        final MultiBeanHelper mbh = RegenFlaechenDetailsPanel.getInstance().getMultiBeanHelper();
        return new CidsBeanValidator(flaecheBean, FlaechePropertyConstants.PROP__DATUM_AENDERUNG) {

                @Override
                public ValidatorState performValidation() {
                    final boolean doNotValidate = (flaecheBean == null)
                                || ((getTriggerdByProperty() != null)
                                    && mbh.getAttachedProperties().contains(getTriggerdByProperty())
                                    && !mbh.isValuesAllEquals(getTriggerdByProperty()));
                    if (doNotValidate && ((flaecheBean == null) || flaecheBean.equals(mbh.getDummyBean()))) {
                        return null;
                    } else {
                        // jedes gültige Datum ist valide
                        return new ValidatorStateImpl(ValidatorState.Type.VALID);
                    }
                }
            };
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flaecheBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Validator getValidatorDatumVeranlagung(final CidsBean flaecheBean) {
        final MultiBeanHelper mbh = RegenFlaechenDetailsPanel.getInstance().getMultiBeanHelper();
        return new CidsBeanValidator(flaecheBean, FlaechePropertyConstants.PROP__DATUM_VERANLAGUNG) {

                @Override
                public ValidatorState performValidation() {
                    final boolean doNotValidate = (flaecheBean == null)
                                || ((getTriggerdByProperty() != null)
                                    && mbh.getAttachedProperties().contains(getTriggerdByProperty())
                                    && !mbh.isValuesAllEquals(getTriggerdByProperty()));
                    if (doNotValidate && ((flaecheBean == null) || flaecheBean.equals(mbh.getDummyBean()))) {
                        return null;
                    } else {
                        final String veranlagungsdatum = (String)flaecheBean.getProperty(
                                FlaechePropertyConstants.PROP__DATUM_VERANLAGUNG);

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
                }
            };
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flaecheBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Validator getValidatorFebId(final CidsBean flaecheBean) {
        final MultiBeanHelper mbh = RegenFlaechenDetailsPanel.getInstance().getMultiBeanHelper();
        return new CidsBeanValidator(flaecheBean, FlaechePropertyConstants.PROP__FEB_ID) {

                @Override
                public ValidatorState performValidation() {
                    final boolean doNotValidate = (flaecheBean == null)
                                || ((getTriggerdByProperty() != null)
                                    && mbh.getAttachedProperties().contains(getTriggerdByProperty())
                                    && !mbh.isValuesAllEquals(getTriggerdByProperty()));
                    if (doNotValidate && ((flaecheBean == null) || flaecheBean.equals(mbh.getDummyBean()))) {
                        return null;
                    } else {
                        final String febId = (String)flaecheBean.getProperty(FlaechePropertyConstants.PROP__FEB_ID);
                        if ((febId == null) || febId.trim().isEmpty()) {
                            return new ValidatorStateImpl(ValidatorState.Type.VALID);
                        }
                        Integer febIdInt = null;
                        try {
                            febIdInt = Integer.parseInt(febId);
                        } catch (final Exception ex) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("error while parsing febId to integer", ex);
                            }
                            return new ValidatorStateImpl(ValidatorState.Type.ERROR, "FEB muss eine Zahl sein.");
                        }

                        if ((febIdInt.intValue() < 20000001) || (febIdInt.intValue() > 20200000)) {
                            return new ValidatorStateImpl(
                                    ValidatorState.Type.ERROR,
                                    "FEB muss zwischen 20.000.000 und 20.200.000 liegen.");
                        }
                        return new ValidatorStateImpl(ValidatorState.Type.VALID);
                    }
                }
            };
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flaecheBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Validator getValidatorFlaechenart(final CidsBean flaecheBean) {
        final MultiBeanHelper mbh = RegenFlaechenDetailsPanel.getInstance().getMultiBeanHelper();
        return new CidsBeanValidator(
                flaecheBean,
                FlaechePropertyConstants.PROP__FLAECHENINFO
                        + "."
                        + FlaecheninfoPropertyConstants.PROP__FLAECHENART,
                FlaechePropertyConstants.PROP__FLAECHENINFO
                        + "."
                        + FlaecheninfoPropertyConstants.PROP__GEOMETRIE
                        + "."
                        + GeomPropertyConstants.PROP__GEO_FIELD) {

                @Override
                public ValidatorState performValidation() {
                    final boolean doNotValidate = (flaecheBean == null)
                                || ((getTriggerdByProperty() != null)
                                    && mbh.getAttachedProperties().contains(getTriggerdByProperty())
                                    && !mbh.isValuesAllEquals(getTriggerdByProperty()));
                    if (doNotValidate && ((flaecheBean == null) || flaecheBean.equals(mbh.getDummyBean()))) {
                        return null;
                    } else {
                        SwingUtilities.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    ((DefaultBindableReferenceCombo)cboFlaechenart).reload(true);
                                }
                            });

                        final CidsBean flaechenart = (CidsBean)flaecheBean.getProperty(
                                FlaechePropertyConstants.PROP__FLAECHENINFO
                                        + "."
                                        + FlaecheninfoPropertyConstants.PROP__FLAECHENART);

                        if ((flaechenart != null)
                                    && (VerdisUtils.PROPVAL_ART_VORLAEUFIGEVERANLASSUNG
                                        == (Integer)flaechenart.getProperty(FlaechenartPropertyConstants.PROP__ID))) {
                            try {
                                flaecheBean.setProperty(FlaechePropertyConstants.PROP__FLAECHENINFO + "."
                                            + FlaecheninfoPropertyConstants.PROP__ANSCHLUSSGRAD,
                                    anschlussgradBean);
                                flaecheBean.setProperty(
                                    FlaechePropertyConstants.PROP__FLAECHENBEZEICHNUNG,
                                    Main.getInstance().getRegenFlaechenTable().getValidFlaechenname(
                                        (Integer)flaechenart.getProperty("id")));
                                flaecheBean.setProperty(FlaechePropertyConstants.PROP__FLAECHENINFO + "."
                                            + FlaecheninfoPropertyConstants.PROP__GROESSE_GRAFIK,
                                    null);
                            } catch (final Exception ex) {
                                LOG.error(ex, ex);
                            }
                            cboAnschlussgrad.setEnabled(false);
                            txtGroesseGrafik.setEditable(false);
                        } else {
                            cboAnschlussgrad.setEnabled(txtBezeichnung.isEditable());
                            txtGroesseGrafik.setEditable(txtBezeichnung.isEditable());
                        }
                        Main.getInstance().refreshItemButtons();

                        final Geometry geom = RegenFlaechenDetailsPanel.getGeometry(flaecheBean);
                        if ((flaechenart != null)
                                    && (VerdisUtils.PROPVAL_ART_VORLAEUFIGEVERANLASSUNG
                                        == (Integer)flaechenart.getProperty(FlaechenartPropertyConstants.PROP__ID))
                                    && (geom != null)) {
                            return new ValidatorStateImpl(
                                    ValidatorState.Type.ERROR,
                                    "Geometrie darf nicht gesetzt sein.");
                        }

                        return new ValidatorStateImpl(ValidatorState.Type.VALID);
                    }
                }
            };
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public DefaultBindableReferenceCombo createComboArtForEdit() {
        final DefaultBindableReferenceCombo combo = new DefaultBindableReferenceCombo() {

                @Override
                public void setModel(final ComboBoxModel aModel) {
                    bindingGroup.unbind();
                    super.setModel(aModel);
                    bindingGroup.bind();
                }
            };

        combo.addPropertyChangeListener(new PropertyChangeListener() {

                @Override
                public void propertyChange(final PropertyChangeEvent evt) {
                    if (evt.getPropertyName().equals("model") && (evt.getNewValue() instanceof DefaultComboBoxModel)) {
                        final CidsBean kassenzeichenBean = CidsAppBackend.getInstance().getCidsBean();
                        if ((kassenzeichenBean != null)
                                    && ((kassenzeichenBean.getBeanCollectionProperty(
                                                KassenzeichenPropertyConstants.PROP__FLAECHEN).size() > 1)
                                        || ((kassenzeichenBean.getBeanCollectionProperty(
                                                    KassenzeichenPropertyConstants.PROP__FLAECHEN).size() == 1)
                                            && (kassenzeichenBean.getBeanCollectionProperty(
                                                    KassenzeichenPropertyConstants.PROP__FLAECHEN).iterator().next()
                                                .getProperty(
                                                    FlaechePropertyConstants.PROP__FLAECHENINFO
                                                    + "."
                                                    + FlaecheninfoPropertyConstants.PROP__GEOMETRIE
                                                    + "."
                                                    + GeomPropertyConstants.PROP__GEO_FIELD) != null)))) {
                            final DefaultComboBoxModel aModel = (DefaultComboBoxModel)evt.getNewValue();
                            Object vvobject = null;
                            for (int index = 0; (index < aModel.getSize()) && (vvobject == null); index++) {
                                final Object object = aModel.getElementAt(index);
                                if ((object instanceof CidsBean)
                                            && ((Integer)((CidsBean)object).getProperty(
                                                    FlaechenartPropertyConstants.PROP__ID)
                                                == VerdisUtils.PROPVAL_ART_VORLAEUFIGEVERANLASSUNG)) {
                                    vvobject = object;
                                }
                            }
                            aModel.removeElement(vvobject);
                        }
                    }
                }
            });
        return combo;
    }

    @Override
    public CidsBean createDummyBean() {
        final CidsBean dummyBean = CidsAppBackend.getInstance()
                    .getVerdisMetaClass(VerdisMetaClassConstants.MC_FLAECHE)
                    .getEmptyInstance()
                    .getBean();
        final CidsBean flaecheninfoBean = CidsAppBackend.getInstance()
                    .getVerdisMetaClass(VerdisMetaClassConstants.MC_FLAECHENINFO)
                    .getEmptyInstance()
                    .getBean();
        final CidsBean geomBean = CidsAppBackend.getInstance()
                    .getVerdisMetaClass(VerdisMetaClassConstants.MC_GEOM)
                    .getEmptyInstance()
                    .getBean();
        try {
            dummyBean.setProperty(FlaechePropertyConstants.PROP__FLAECHENINFO, flaecheninfoBean);
            dummyBean.setProperty(FlaechePropertyConstants.PROP__FLAECHENINFO + "."
                        + FlaecheninfoPropertyConstants.PROP__GEOMETRIE,
                geomBean);
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }
        return dummyBean;
    }
}
