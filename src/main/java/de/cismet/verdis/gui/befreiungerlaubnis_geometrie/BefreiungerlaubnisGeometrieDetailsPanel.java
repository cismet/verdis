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
package de.cismet.verdis.gui.befreiungerlaubnis_geometrie;

import Sirius.server.middleware.types.DefaultMetaObject;
import Sirius.server.middleware.types.MetaObject;

import com.vividsolutions.jts.geom.Geometry;

import edu.umd.cs.piccolo.PCanvas;

import org.openide.util.Exceptions;

import java.awt.Color;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;

import de.cismet.cids.custom.util.BindingValidationSupport;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultBindableReferenceCombo;
import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.converters.SqlDateToStringConverter;

import de.cismet.cids.utils.multibean.EmbeddedMultiBeanDisplay;
import de.cismet.cids.utils.multibean.MultiBeanHelper;

import de.cismet.validation.*;

import de.cismet.validation.display.EmbeddedValidatorDisplay;

import de.cismet.validation.validator.CidsBeanValidator;

import de.cismet.verdis.CidsAppBackend;
import de.cismet.verdis.CrossReference;

import de.cismet.verdis.commons.constants.BefreiungerlaubnisGeometriePropertyConstants;
import de.cismet.verdis.commons.constants.BefreiungerlaubnisPropertyConstants;
import de.cismet.verdis.commons.constants.GeomPropertyConstants;
import de.cismet.verdis.commons.constants.VerdisMetaClassConstants;

import de.cismet.verdis.gui.AbstractCidsBeanDetailsPanel;
import de.cismet.verdis.gui.Main;
import de.cismet.verdis.gui.converter.DateToStringConverter;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class BefreiungerlaubnisGeometrieDetailsPanel extends AbstractCidsBeanDetailsPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            BefreiungerlaubnisGeometrieDetailsPanel.class);
    private static BefreiungerlaubnisGeometrieDetailsPanel INSTANCE;

    //~ Instance fields --------------------------------------------------------

    private CidsBean cidsBean;
    private final Validator bindingValidator;
    private CidsBean parentBean;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.cismet.cismap.commons.gui.SimpleBackgroundedJPanel bpanRegenFlDetails;
    private javax.swing.JComboBox cboNutzung;
    private javax.swing.JComboBox cboTypEinleitung;
    private javax.swing.JComboBox cboTypVersickerung;
    private javax.swing.JCheckBox chkGutachtenVorhanden;
    private javax.swing.JEditorPane edtQuer;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblAktenzeichen;
    private javax.swing.JLabel lblAntragVom;
    private javax.swing.JLabel lblBemerkung;
    private javax.swing.JLabel lblDurchfluss;
    private javax.swing.JLabel lblFilterkonstante;
    private javax.swing.JLabel lblGewaessername;
    private javax.swing.JLabel lblGueltigBis;
    private javax.swing.JLabel lblGutachtenVorhanden;
    private javax.swing.JLabel lblNutzung;
    private javax.swing.JLabel lblQuerverweise;
    private javax.swing.JLabel lblTypEinleitung;
    private javax.swing.JLabel lblTypVersickerung;
    private javax.swing.JScrollPane scpBemerkung;
    private javax.swing.JScrollPane scpQuer;
    private javax.swing.JTextField txtAktenzeichen;
    private javax.swing.JTextField txtAntragVom;
    private javax.swing.JTextArea txtBemerkung;
    private javax.swing.JTextField txtDurchfluss;
    private javax.swing.JTextField txtFilterkonstante;
    private javax.swing.JTextField txtGewaessername;
    private javax.swing.JTextField txtGueltigBis;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form VersickerungDetailsPanel.
     */
    private BefreiungerlaubnisGeometrieDetailsPanel() {
        UIManager.put("ComboBox.disabledForeground", Color.black);
        initComponents();

        setEnabled(false);

        EmbeddedMultiBeanDisplay.registerComponentForProperty(
            txtDurchfluss,
            BefreiungerlaubnisGeometriePropertyConstants.PROP__DURCHFLUSS,
            getMultiBeanHelper());
        EmbeddedMultiBeanDisplay.registerComponentForProperty(
            txtFilterkonstante,
            BefreiungerlaubnisGeometriePropertyConstants.PROP__FILTERKONSTANTE,
            getMultiBeanHelper());
        EmbeddedMultiBeanDisplay.registerComponentForProperty(
            cboTypEinleitung,
            BefreiungerlaubnisGeometriePropertyConstants.PROP__TYP_EINLEITUNG,
            getMultiBeanHelper());
        EmbeddedMultiBeanDisplay.registerComponentForProperty(
            cboTypVersickerung,
            BefreiungerlaubnisGeometriePropertyConstants.PROP__TYP_VERSICKERUNG,
            getMultiBeanHelper());
        EmbeddedMultiBeanDisplay.registerComponentForProperty(
            txtGewaessername,
            BefreiungerlaubnisGeometriePropertyConstants.PROP__GEWAESSERNAME,
            getMultiBeanHelper());
        EmbeddedMultiBeanDisplay.registerComponentForProperty(
            txtBemerkung,
            BefreiungerlaubnisGeometriePropertyConstants.PROP__BEMERKUNG,
            getMultiBeanHelper());
        EmbeddedMultiBeanDisplay.registerComponentForProperty(
            chkGutachtenVorhanden,
            BefreiungerlaubnisGeometriePropertyConstants.PROP__GUTACHTEN_VORHANDEN,
            getMultiBeanHelper());

        bindingValidator = BindingValidationSupport.attachBindingValidationToAllTargets(bindingGroup);

        SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    setCidsBean(null);
                }
            });
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static BefreiungerlaubnisGeometrieDetailsPanel getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BefreiungerlaubnisGeometrieDetailsPanel();
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
     * @param   beferBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Geometry getGeometry(final CidsBean beferBean) {
        if ((beferBean != null)
                    && (beferBean.getProperty(BefreiungerlaubnisGeometriePropertyConstants.PROP__GEOMETRIE) != null)) {
            return (Geometry)beferBean.getProperty(BefreiungerlaubnisGeometriePropertyConstants.PROP__GEOMETRIE
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
        if (cidsBean.getProperty(BefreiungerlaubnisGeometriePropertyConstants.PROP__GEOMETRIE) == null) {
            final CidsBean emptyGeoBean = CidsAppBackend.getInstance()
                        .getVerdisMetaClass(VerdisMetaClassConstants.MC_GEOM)
                        .getEmptyInstance()
                        .getBean();
            cidsBean.setProperty(BefreiungerlaubnisGeometriePropertyConstants.PROP__GEOMETRIE, emptyGeoBean);
        }
        cidsBean.setProperty(BefreiungerlaubnisGeometriePropertyConstants.PROP__GEOMETRIE + "."
                    + GeomPropertyConstants.PROP__GEO_FIELD,
            geom);
    }

    /**
     * DOCUMENT ME!
     */
    private void attachBeanValidators() {
        ValidatorHelper.removeAllNoBindingValidatorFromDisplay(txtDurchfluss);
        getValidatorDurchfluss(cidsBean).attachDisplay(EmbeddedValidatorDisplay.getEmbeddedDisplayFor(
                txtDurchfluss));
        ValidatorHelper.removeAllNoBindingValidatorFromDisplay(txtFilterkonstante);
        getValidatorFilterkonstante(cidsBean).attachDisplay(EmbeddedValidatorDisplay.getEmbeddedDisplayFor(
                txtFilterkonstante));
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
        lblAktenzeichen = new javax.swing.JLabel();
        txtAktenzeichen = new javax.swing.JTextField();
        lblAntragVom = new javax.swing.JLabel();
        txtAntragVom = new javax.swing.JTextField();
        lblGueltigBis = new javax.swing.JLabel();
        txtGueltigBis = new javax.swing.JTextField();
        lblNutzung = new javax.swing.JLabel();
        cboNutzung = new DefaultBindableReferenceCombo();
        jSeparator2 = new javax.swing.JSeparator();
        lblTypEinleitung = new javax.swing.JLabel();
        cboTypEinleitung = new DefaultBindableReferenceCombo();
        lblTypVersickerung = new javax.swing.JLabel();
        cboTypVersickerung = new DefaultBindableReferenceCombo();
        lblDurchfluss = new javax.swing.JLabel();
        txtDurchfluss = new javax.swing.JTextField();
        lblGutachtenVorhanden = new javax.swing.JLabel();
        chkGutachtenVorhanden = new javax.swing.JCheckBox();
        lblBemerkung = new javax.swing.JLabel();
        scpBemerkung = new javax.swing.JScrollPane();
        txtBemerkung = new javax.swing.JTextArea();
        lblFilterkonstante = new javax.swing.JLabel();
        txtFilterkonstante = new javax.swing.JTextField();
        lblGewaessername = new javax.swing.JLabel();
        txtGewaessername = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        lblQuerverweise = new javax.swing.JLabel();
        scpQuer = new javax.swing.JScrollPane();
        edtQuer = new javax.swing.JEditorPane();
        jPanel1 = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.BorderLayout());

        bpanRegenFlDetails.setOpaque(false);
        bpanRegenFlDetails.setLayout(new java.awt.GridBagLayout());

        lblAktenzeichen.setText(org.openide.util.NbBundle.getMessage(
                BefreiungerlaubnisGeometrieDetailsPanel.class,
                "BefreiungerlaubnisGeometrieDetailsPanel.lblAktenzeichen.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        bpanRegenFlDetails.add(lblAktenzeichen, gridBagConstraints);

        txtAktenzeichen.setEditable(false);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${parentBean.aktenzeichen}"),
                txtAktenzeichen,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "aktenzeichen");
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 2);
        bpanRegenFlDetails.add(txtAktenzeichen, gridBagConstraints);

        lblAntragVom.setText(org.openide.util.NbBundle.getMessage(
                BefreiungerlaubnisGeometrieDetailsPanel.class,
                "BefreiungerlaubnisGeometrieDetailsPanel.lblAntragVom.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        bpanRegenFlDetails.add(lblAntragVom, gridBagConstraints);

        txtAntragVom.setEditable(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${parentBean.antrag_vom}"),
                txtAntragVom,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "\"antrag_vom\"");
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        binding.setConverter(new DateToStringConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 2);
        bpanRegenFlDetails.add(txtAntragVom, gridBagConstraints);

        lblGueltigBis.setText(org.openide.util.NbBundle.getMessage(
                BefreiungerlaubnisGeometrieDetailsPanel.class,
                "BefreiungerlaubnisGeometrieDetailsPanel.lblGueltigBis.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        bpanRegenFlDetails.add(lblGueltigBis, gridBagConstraints);

        txtGueltigBis.setEditable(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${parentBean.gueltig_bis}"),
                txtGueltigBis,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "gueltig_bis");
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        binding.setConverter(new DateToStringConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 2);
        bpanRegenFlDetails.add(txtGueltigBis, gridBagConstraints);

        lblNutzung.setText(org.openide.util.NbBundle.getMessage(
                BefreiungerlaubnisGeometrieDetailsPanel.class,
                "BefreiungerlaubnisGeometrieDetailsPanel.lblNutzung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        bpanRegenFlDetails.add(lblNutzung, gridBagConstraints);

        cboNutzung.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${parentBean.nutzung}"),
                cboNutzung,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 2);
        bpanRegenFlDetails.add(cboNutzung, gridBagConstraints);
        ((DefaultBindableReferenceCombo)cboNutzung).setMetaClass(CidsAppBackend.getInstance().getVerdisMetaClass(
                VerdisMetaClassConstants.MC_BEFREIUNGERLAUBNIS_NUTZUNG));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        bpanRegenFlDetails.add(jSeparator2, gridBagConstraints);

        lblTypEinleitung.setText(org.openide.util.NbBundle.getMessage(
                BefreiungerlaubnisGeometrieDetailsPanel.class,
                "BefreiungerlaubnisGeometrieDetailsPanel.lblTypEinleitung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        bpanRegenFlDetails.add(lblTypEinleitung, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.typ_einleitung}"),
                cboTypEinleitung,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"),
                "typ");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 2);
        bpanRegenFlDetails.add(cboTypEinleitung, gridBagConstraints);
        ((DefaultBindableReferenceCombo)cboTypEinleitung).setMetaClass(CidsAppBackend.getInstance().getVerdisMetaClass(
                VerdisMetaClassConstants.MC_BEFREIUNGERLAUBNIS_GEOMETRIE_TYP_EINLEITUNG));

        lblTypVersickerung.setText(org.openide.util.NbBundle.getMessage(
                BefreiungerlaubnisGeometrieDetailsPanel.class,
                "BefreiungerlaubnisGeometrieDetailsPanel.lblTypVersickerung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        bpanRegenFlDetails.add(lblTypVersickerung, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.typ_versickerung}"),
                cboTypVersickerung,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 2);
        bpanRegenFlDetails.add(cboTypVersickerung, gridBagConstraints);
        ((DefaultBindableReferenceCombo)cboTypEinleitung).setMetaClass(CidsAppBackend.getInstance().getVerdisMetaClass(
                VerdisMetaClassConstants.MC_BEFREIUNGERLAUBNIS_GEOMETRIE_TYP_VERSICKERUNG));

        lblDurchfluss.setText(org.openide.util.NbBundle.getMessage(
                BefreiungerlaubnisGeometrieDetailsPanel.class,
                "BefreiungerlaubnisGeometrieDetailsPanel.lblDurchfluss.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        bpanRegenFlDetails.add(lblDurchfluss, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.durchfluss}"),
                txtDurchfluss,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "durchfluss");
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 2);
        bpanRegenFlDetails.add(txtDurchfluss, gridBagConstraints);

        lblGutachtenVorhanden.setText(org.openide.util.NbBundle.getMessage(
                BefreiungerlaubnisGeometrieDetailsPanel.class,
                "BefreiungerlaubnisGeometrieDetailsPanel.lblGutachtenVorhanden.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        bpanRegenFlDetails.add(lblGutachtenVorhanden, gridBagConstraints);

        chkGutachtenVorhanden.setForeground(java.awt.Color.red);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.gutachten_vorhanden}"),
                chkGutachtenVorhanden,
                org.jdesktop.beansbinding.BeanProperty.create("selected"),
                "\"gutachten_vorhanden\"");
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 0);
        bpanRegenFlDetails.add(chkGutachtenVorhanden, gridBagConstraints);

        lblBemerkung.setText(org.openide.util.NbBundle.getMessage(
                BefreiungerlaubnisGeometrieDetailsPanel.class,
                "BefreiungerlaubnisGeometrieDetailsPanel.lblBemerkung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        bpanRegenFlDetails.add(lblBemerkung, gridBagConstraints);

        scpBemerkung.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scpBemerkung.setMinimumSize(new java.awt.Dimension(19, 80));
        scpBemerkung.setOpaque(false);
        scpBemerkung.setPreferredSize(new java.awt.Dimension(19, 80));

        txtBemerkung.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 11)); // NOI18N
        txtBemerkung.setLineWrap(true);
        txtBemerkung.setRows(2);
        txtBemerkung.setOpaque(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bemerkung}"),
                txtBemerkung,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "\"Bemerkung\"");
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        scpBemerkung.setViewportView(txtBemerkung);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 2);
        bpanRegenFlDetails.add(scpBemerkung, gridBagConstraints);

        lblFilterkonstante.setText(org.openide.util.NbBundle.getMessage(
                BefreiungerlaubnisGeometrieDetailsPanel.class,
                "BefreiungerlaubnisGeometrieDetailsPanel.lblFilterkonstante.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        bpanRegenFlDetails.add(lblFilterkonstante, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.filterkonstante}"),
                txtFilterkonstante,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "Filterkonstante");
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 2);
        bpanRegenFlDetails.add(txtFilterkonstante, gridBagConstraints);

        lblGewaessername.setText(org.openide.util.NbBundle.getMessage(
                BefreiungerlaubnisGeometrieDetailsPanel.class,
                "BefreiungerlaubnisGeometrieDetailsPanel.lblGewaessername.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        bpanRegenFlDetails.add(lblGewaessername, gridBagConstraints);

        txtGewaessername.setEditable(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.gewaessername}"),
                txtGewaessername,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "gewaessername");
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        binding.setConverter(new SqlDateToStringConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 2);
        bpanRegenFlDetails.add(txtGewaessername, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        bpanRegenFlDetails.add(jSeparator1, gridBagConstraints);

        lblQuerverweise.setText(org.openide.util.NbBundle.getMessage(
                BefreiungerlaubnisGeometrieDetailsPanel.class,
                "BefreiungerlaubnisGeometrieDetailsPanel.lblQuerverweise.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        bpanRegenFlDetails.add(lblQuerverweise, gridBagConstraints);

        scpQuer.setOpaque(false);

        edtQuer.setEditable(false);
        edtQuer.setContentType("text/html"); // NOI18N
        edtQuer.setOpaque(false);
        scpQuer.setViewportView(edtQuer);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
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

        jPanel2.add(bpanRegenFlDetails, java.awt.BorderLayout.CENTER);

        add(jPanel2, java.awt.BorderLayout.CENTER);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getParentBean() {
        return parentBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  parentBean  DOCUMENT ME!
     */
    public void setParentBean(final CidsBean parentBean) {
        this.parentBean = parentBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        if ((cidsBean != null) && cidsBean.equals(this.cidsBean)) {
            return;
        }

        setEnabled(CidsAppBackend.getInstance().isEditable() && (cidsBean != null));

        bindingGroup.unbind();
        this.cidsBean = cidsBean;
        setParentBean(
            ((this.cidsBean == null)
                        || (Main.getInstance().getBefreiungerlaubnisGeometrieTable().getSelectedBeans().size() != 1))
                ? null
                : ((MetaObject)Main.getInstance().getBefreiungerlaubnisGeometrieTable().getSelectedBeans().iterator()
                                .next().getMetaObject().getReferencingObjectAttribute().getParentObject()
                                .getReferencingObjectAttribute().getParentObject()).getBean());
        if (this.cidsBean != null) {
            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                this.cidsBean);
        }
        bindingGroup.bind();

        final boolean isVersickerung = (parentBean != null)
                    && (parentBean.getProperty(BefreiungerlaubnisPropertyConstants.PROP__AKTENZEICHEN) != null)
                    && ((String)parentBean.getProperty(BefreiungerlaubnisPropertyConstants.PROP__AKTENZEICHEN))
                    .startsWith("747-");
        lblTypVersickerung.setVisible(isVersickerung);
        cboTypVersickerung.setVisible(isVersickerung);
        lblFilterkonstante.setVisible(isVersickerung);
        txtFilterkonstante.setVisible(isVersickerung);

        final boolean isEinleitung = (parentBean != null)
                    && (parentBean.getProperty(BefreiungerlaubnisPropertyConstants.PROP__AKTENZEICHEN) != null)
                    && ((String)parentBean.getProperty(BefreiungerlaubnisPropertyConstants.PROP__AKTENZEICHEN))
                    .startsWith("748-");
        lblTypEinleitung.setVisible(isEinleitung);
        cboTypEinleitung.setVisible(isEinleitung);
        txtGewaessername.setVisible(isEinleitung);
        lblGewaessername.setVisible(isEinleitung);

        lblDurchfluss.setVisible(isVersickerung || isEinleitung);
        txtDurchfluss.setVisible(isVersickerung || isEinleitung);
        lblGutachtenVorhanden.setVisible(isVersickerung || isEinleitung);
        chkGutachtenVorhanden.setVisible(isVersickerung || isEinleitung);
        lblBemerkung.setVisible(isVersickerung || isEinleitung);
        scpBemerkung.setVisible(isVersickerung || isEinleitung);
        jSeparator2.setVisible(isVersickerung || isEinleitung);

        try {
            if ((this.cidsBean != null)
                        && (this.cidsBean.getProperty(BefreiungerlaubnisGeometriePropertyConstants.PROP__GEOMETRIE)
                            != null)) {
                bpanRegenFlDetails.setBackgroundEnabled(true);
            } else {
                bpanRegenFlDetails.setBackgroundEnabled(false);
            }
        } catch (Exception e) {
            LOG.warn("problem when trying to set background enabled (or not). will turn the background off", e);
            bpanRegenFlDetails.setBackgroundEnabled(false);
        }

        updateCrossReferences();

        attachBeanValidators();
    }

    /**
     * DOCUMENT ME!
     */
    public synchronized void updateCrossReferences() {
        if ((cidsBean != null) && (cidsBean.getProperty("id") != null)) {
            new SwingWorker<String, Void>() {

                    @Override
                    protected String doInBackground() throws Exception {
                        final Collection<CrossReference> crossReference = CidsAppBackend.getInstance()
                                    .getBefreiungerlaubnisCrossReferencesFor((Integer)parentBean.getProperty("id"));

                        if (crossReference != null) {
                            String html = "<html><body><center>";
                            for (final CrossReference crossreference : crossReference) {
                                final String link = Integer.toString(crossreference.getEntityToKassenzeichen());
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

    @Override
    public void editModeChanged() {
        setEnabled(CidsAppBackend.getInstance().isEditable() && (getCidsBean() != null));
    }

    @Override
    public final void setEnabled(final boolean b) {
        super.setEnabled(b);
        txtBemerkung.setEditable(b);
        txtBemerkung.setOpaque(b);
        txtGewaessername.setEditable(b);
        txtGewaessername.setOpaque(b);
        txtDurchfluss.setEditable(b);
        txtDurchfluss.setOpaque(b);
        txtFilterkonstante.setEditable(b);
        txtFilterkonstante.setOpaque(b);
        chkGutachtenVorhanden.setEnabled(b);
        chkGutachtenVorhanden.setOpaque(b);
        cboTypVersickerung.setEnabled(b);
        cboTypVersickerung.setOpaque(b);
        cboTypEinleitung.setEnabled(b);
        cboTypEinleitung.setOpaque(b);

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
     * @param   beferBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Validator getValidatorDurchfluss(final CidsBean beferBean) {
        final MultiBeanHelper mbh = BefreiungerlaubnisGeometrieDetailsPanel.getInstance().getMultiBeanHelper();
        return new CidsBeanValidator(beferBean, BefreiungerlaubnisGeometriePropertyConstants.PROP__DURCHFLUSS) {

                @Override
                public ValidatorState performValidation() {
                    return new ValidatorStateImpl(ValidatorState.Type.VALID);
                }
            };
    }

    /**
     * DOCUMENT ME!
     *
     * @param   beferBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Validator getValidatorFilterkonstante(final CidsBean beferBean) {
        final MultiBeanHelper mbh = BefreiungerlaubnisGeometrieDetailsPanel.getInstance().getMultiBeanHelper();
        return new CidsBeanValidator(beferBean, BefreiungerlaubnisGeometriePropertyConstants.PROP__FILTERKONSTANTE) {

                @Override
                public ValidatorState performValidation() {
                    return new ValidatorStateImpl(ValidatorState.Type.VALID);
                }
            };
    }

    @Override
    public CidsBean createDummyBean() {
        final CidsBean dummyBean = CidsAppBackend.getInstance()
                    .getVerdisMetaClass(VerdisMetaClassConstants.MC_BEFREIUNGERLAUBNIS_GEOMETRIE)
                    .getEmptyInstance()
                    .getBean();
        final CidsBean geomBean = CidsAppBackend.getInstance()
                    .getVerdisMetaClass(VerdisMetaClassConstants.MC_GEOM)
                    .getEmptyInstance()
                    .getBean();
        try {
            dummyBean.setProperty(BefreiungerlaubnisGeometriePropertyConstants.PROP__GEOMETRIE, geomBean);
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }
        return dummyBean;
    }
}
