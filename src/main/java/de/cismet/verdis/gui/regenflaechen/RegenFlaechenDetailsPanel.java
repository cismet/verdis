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

import Sirius.navigator.connection.SessionManager;

import com.vividsolutions.jts.geom.Geometry;

import edu.umd.cs.piccolo.PCanvas;

import org.openide.util.Exceptions;

import java.awt.Color;
import java.awt.event.ActionEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.text.NumberFormat;

import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
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

import de.cismet.verdis.commons.constants.VerdisConstants;

import de.cismet.verdis.gui.AbstractCidsBeanDetailsPanel;
import de.cismet.verdis.gui.Main;

import de.cismet.verdis.server.utils.aenderungsanfrage.AnfrageJson;
import de.cismet.verdis.server.utils.aenderungsanfrage.FlaecheJson;
import de.cismet.verdis.server.utils.aenderungsanfrage.FlaechePruefungAnschlussgradJson;
import de.cismet.verdis.server.utils.aenderungsanfrage.FlaechePruefungFlaechenartJson;
import de.cismet.verdis.server.utils.aenderungsanfrage.FlaechePruefungGroesseJson;
import de.cismet.verdis.server.utils.aenderungsanfrage.PruefungJson;

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
    private static final Icon AENDERUNG_ICON = new javax.swing.ImageIcon(RegenFlaechenDetailsPanel.class.getResource(
                "/de/cismet/validation/info.png"));

    //~ Instance fields --------------------------------------------------------

    private final CidsBean anschlussgradBean;

    private CidsBean flaecheBean;
    private FlaecheJson flaecheJson;
    private final Validator bindingValidator;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.cismet.cismap.commons.gui.SimpleBackgroundedJPanel bpanRegenFlDetails;
    private javax.swing.JButton btnAnschlussgradAenderungAccept;
    private javax.swing.JButton btnAnschlussgradAenderungReject;
    private javax.swing.JButton btnFlaechenartAenderungAccept;
    private javax.swing.JButton btnFlaechenartAenderungReject;
    private javax.swing.JButton btnGroesseAenderungAccept;
    private javax.swing.JButton btnGroesseAenderungReject;
    private javax.swing.JComboBox cboAnschlussgrad;
    private javax.swing.JComboBox cboBeschreibung;
    private javax.swing.JComboBox cboFlaechenart;
    private javax.swing.JEditorPane edtQuer;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblAenderungsdatum;
    private javax.swing.JLabel lblAnschlussgrad;
    private javax.swing.JLabel lblAnschlussgradAenderung;
    private javax.swing.JLabel lblAnteil;
    private javax.swing.JLabel lblBemerkung;
    private javax.swing.JLabel lblBeschreibung;
    private javax.swing.JLabel lblBezeichnung;
    private javax.swing.JLabel lblFlaechenart;
    private javax.swing.JLabel lblFlaechenartAenderung;
    private javax.swing.JLabel lblGroesseAenderung;
    private javax.swing.JLabel lblGroesseGrafik;
    private javax.swing.JLabel lblGroesseKorrektur;
    private javax.swing.JLabel lblTeileigentumQuerverweise;
    private javax.swing.JLabel lblVeranlagungsdatum;
    private javax.swing.JPanel pnlAnschlussgrad;
    private javax.swing.JPanel pnlFlaechenArt;
    private javax.swing.JPanel pnlGroesseKorrektur;
    private javax.swing.JScrollPane scpBemerkung;
    private javax.swing.JScrollPane scpQuer;
    private javax.swing.JTextField txtAenderungsdatum;
    private javax.swing.JTextField txtAnteil;
    private javax.swing.JTextArea txtBemerkung;
    private javax.swing.JTextField txtBezeichnung;
    private javax.swing.JTextField txtGroesseGrafik;
    private javax.swing.JTextField txtGroesseKorrektur;
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
                VerdisConstants.MC.FLAECHENBESCHREIBUNG));
        ((DefaultBindableReferenceCombo)cboAnschlussgrad).setMetaClass(CidsAppBackend.getInstance().getVerdisMetaClass(
                VerdisConstants.MC.ANSCHLUSSGRAD));
        ((DefaultBindableReferenceCombo)cboFlaechenart).setMetaClass(CidsAppBackend.getInstance().getVerdisMetaClass(
                VerdisConstants.MC.FLAECHENART));
        setEnabled(false);

        anschlussgradBean = CidsAppBackend.getInstance()
                    .getVerdisMetaObject(
                            1,
                            CidsAppBackend.getInstance().getVerdisMetaClass(VerdisConstants.MC.ANSCHLUSSGRAD).getId())
                    .getBean();

        EmbeddedMultiBeanDisplay.registerComponentForProperty(
            txtBezeichnung,
            VerdisConstants.PROP.FLAECHE.FLAECHENBEZEICHNUNG,
            getMultiBeanHelper());
        EmbeddedMultiBeanDisplay.registerComponentForProperty(
            txtGroesseGrafik,
            VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                    + "."
                    + VerdisConstants.PROP.FLAECHENINFO.GROESSE_GRAFIK,
            getMultiBeanHelper());
        EmbeddedMultiBeanDisplay.registerComponentForProperty(
            txtGroesseKorrektur,
            VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                    + "."
                    + VerdisConstants.PROP.FLAECHENINFO.GROESSE_KORREKTUR,
            getMultiBeanHelper());
        EmbeddedMultiBeanDisplay.registerComponentForProperty(
            cboFlaechenart,
            VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                    + "."
                    + VerdisConstants.PROP.FLAECHENINFO.FLAECHENART,
            getMultiBeanHelper());
        EmbeddedMultiBeanDisplay.registerComponentForProperty(
            cboAnschlussgrad,
            VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                    + "."
                    + VerdisConstants.PROP.FLAECHENINFO.ANSCHLUSSGRAD,
            getMultiBeanHelper());
        EmbeddedMultiBeanDisplay.registerComponentForProperty(
            cboBeschreibung,
            VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                    + "."
                    + VerdisConstants.PROP.FLAECHENINFO.BESCHREIBUNG,
            getMultiBeanHelper());
        EmbeddedMultiBeanDisplay.registerComponentForProperty(
            txtAnteil,
            VerdisConstants.PROP.FLAECHE.ANTEIL,
            getMultiBeanHelper());
        EmbeddedMultiBeanDisplay.registerComponentForProperty(
            txtAenderungsdatum,
            VerdisConstants.PROP.FLAECHE.DATUM_AENDERUNG,
            getMultiBeanHelper());
        EmbeddedMultiBeanDisplay.registerComponentForProperty(
            txtVeranlagungsdatum,
            VerdisConstants.PROP.FLAECHE.DATUM_VERANLAGUNG,
            getMultiBeanHelper());
        EmbeddedMultiBeanDisplay.registerComponentForProperty(
            txtBemerkung,
            VerdisConstants.PROP.FLAECHE.BEMERKUNG,
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
                    && (flaecheBean.getProperty(VerdisConstants.PROP.FLAECHE.FLAECHENINFO) != null)
                    && (flaecheBean.getProperty(
                            VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                            + "."
                            + VerdisConstants.PROP.FLAECHENINFO.GEOMETRIE) != null)) {
            return (Geometry)flaecheBean.getProperty(
                    VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                            + "."
                            + VerdisConstants.PROP.FLAECHENINFO.GEOMETRIE
                            + "."
                            + VerdisConstants.PROP.GEOM.GEO_FIELD);
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
                        VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                        + "."
                        + VerdisConstants.PROP.FLAECHENINFO.GEOMETRIE)
                    == null) {
            final CidsBean emptyGeoBean = CidsAppBackend.getInstance()
                        .getVerdisMetaClass(VerdisConstants.MC.GEOM)
                        .getEmptyInstance()
                        .getBean();
            cidsBean.setProperty(VerdisConstants.PROP.FLAECHE.FLAECHENINFO + "."
                        + VerdisConstants.PROP.FLAECHENINFO.GEOMETRIE,
                emptyGeoBean);
        }
        cidsBean.setProperty(VerdisConstants.PROP.FLAECHE.FLAECHENINFO + "."
                    + VerdisConstants.PROP.FLAECHENINFO.GEOMETRIE + "." + VerdisConstants.PROP.GEOM.GEO_FIELD,
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
        lblAnteil = new javax.swing.JLabel();
        lblAenderungsdatum = new javax.swing.JLabel();
        lblVeranlagungsdatum = new javax.swing.JLabel();
        lblBemerkung = new javax.swing.JLabel();
        txtAnteil = new javax.swing.JTextField();
        txtAenderungsdatum = new javax.swing.JTextField();
        txtVeranlagungsdatum = new javax.swing.JTextField();
        scpBemerkung = new javax.swing.JScrollPane();
        txtBemerkung = new javax.swing.JTextArea();
        lblTeileigentumQuerverweise = new javax.swing.JLabel();
        scpQuer = new javax.swing.JScrollPane();
        edtQuer = new javax.swing.JEditorPane();
        lblBeschreibung = new javax.swing.JLabel();
        cboBeschreibung = new DefaultBindableReferenceCombo();
        jCheckBox1 = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        pnlGroesseKorrektur = new javax.swing.JPanel();
        lblGroesseAenderung = new javax.swing.JLabel();
        btnGroesseAenderungAccept = new javax.swing.JButton();
        btnGroesseAenderungReject = new javax.swing.JButton();
        pnlFlaechenArt = new javax.swing.JPanel();
        lblFlaechenartAenderung = new javax.swing.JLabel();
        cboFlaechenart = createComboArtForEdit();
        btnFlaechenartAenderungAccept = new javax.swing.JButton();
        btnFlaechenartAenderungReject = new javax.swing.JButton();
        pnlAnschlussgrad = new javax.swing.JPanel();
        lblAnschlussgradAenderung = new javax.swing.JLabel();
        cboAnschlussgrad = new DefaultBindableReferenceCombo();
        btnAnschlussgradAenderungAccept = new javax.swing.JButton();
        btnAnschlussgradAenderungReject = new javax.swing.JButton();
        txtGroesseKorrektur = new javax.swing.JTextField();

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
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 0);
        bpanRegenFlDetails.add(lblBezeichnung, gridBagConstraints);

        lblGroesseGrafik.setText(org.openide.util.NbBundle.getMessage(
                RegenFlaechenDetailsPanel.class,
                "RegenFlaechenDetailsPanel.lblGroesseGrafik.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 0);
        bpanRegenFlDetails.add(lblGroesseGrafik, gridBagConstraints);

        lblGroesseKorrektur.setText(org.openide.util.NbBundle.getMessage(
                RegenFlaechenDetailsPanel.class,
                "RegenFlaechenDetailsPanel.lblGroesseKorrektur.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 0);
        bpanRegenFlDetails.add(lblGroesseKorrektur, gridBagConstraints);

        lblFlaechenart.setText(org.openide.util.NbBundle.getMessage(
                RegenFlaechenDetailsPanel.class,
                "RegenFlaechenDetailsPanel.lblFlaechenart.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 0);
        bpanRegenFlDetails.add(lblFlaechenart, gridBagConstraints);

        lblAnschlussgrad.setText(org.openide.util.NbBundle.getMessage(
                RegenFlaechenDetailsPanel.class,
                "RegenFlaechenDetailsPanel.lblAnschlussgrad.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 0);
        bpanRegenFlDetails.add(lblAnschlussgrad, gridBagConstraints);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.flaechenbezeichnung}"),
                txtBezeichnung,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                VerdisConstants.PROP.FLAECHE.FLAECHENBEZEICHNUNG);
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 2);
        bpanRegenFlDetails.add(txtBezeichnung, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.flaecheninfo.groesse_aus_grafik}"),
                txtGroesseGrafik,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                        + "."
                        + VerdisConstants.PROP.FLAECHENINFO.GROESSE_GRAFIK);
        bindingGroup.addBinding(binding);

        txtGroesseGrafik.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    txtGroesseGrafikActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
        bpanRegenFlDetails.add(txtGroesseGrafik, gridBagConstraints);

        lblAnteil.setText(org.openide.util.NbBundle.getMessage(
                RegenFlaechenDetailsPanel.class,
                "RegenFlaechenDetailsPanel.lblAnteil.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 0);
        bpanRegenFlDetails.add(lblAnteil, gridBagConstraints);

        lblAenderungsdatum.setText(org.openide.util.NbBundle.getMessage(
                RegenFlaechenDetailsPanel.class,
                "RegenFlaechenDetailsPanel.lblAenderungsdatum.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 0);
        bpanRegenFlDetails.add(lblAenderungsdatum, gridBagConstraints);

        lblVeranlagungsdatum.setText(org.openide.util.NbBundle.getMessage(
                RegenFlaechenDetailsPanel.class,
                "RegenFlaechenDetailsPanel.lblVeranlagungsdatum.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 0);
        bpanRegenFlDetails.add(lblVeranlagungsdatum, gridBagConstraints);

        lblBemerkung.setText(org.openide.util.NbBundle.getMessage(
                RegenFlaechenDetailsPanel.class,
                "RegenFlaechenDetailsPanel.lblBemerkung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 0);
        bpanRegenFlDetails.add(lblBemerkung, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.anteil}"),
                txtAnteil,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                VerdisConstants.PROP.FLAECHE.ANTEIL);
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        binding.setConverter(new de.cismet.verdis.gui.converter.EmptyFloatToStringConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 2);
        bpanRegenFlDetails.add(txtAnteil, gridBagConstraints);

        txtAenderungsdatum.setEditable(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.datum_erfassung}"),
                txtAenderungsdatum,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                VerdisConstants.PROP.KASSENZEICHEN.DATUM_ERFASSUNG);
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        binding.setConverter(new SqlDateToStringConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 2);
        bpanRegenFlDetails.add(txtAenderungsdatum, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.datum_veranlagung}"),
                txtVeranlagungsdatum,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                VerdisConstants.PROP.KASSENZEICHEN.DATUM_VERANLAGUNG);
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 2);
        bpanRegenFlDetails.add(txtVeranlagungsdatum, gridBagConstraints);

        scpBemerkung.setMinimumSize(new java.awt.Dimension(23, 70));
        scpBemerkung.setOpaque(false);
        scpBemerkung.setPreferredSize(new java.awt.Dimension(19, 70));

        txtBemerkung.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 11)); // NOI18N
        txtBemerkung.setLineWrap(true);
        txtBemerkung.setRows(4);
        txtBemerkung.setOpaque(false);

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
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 2);
        bpanRegenFlDetails.add(scpBemerkung, gridBagConstraints);

        lblTeileigentumQuerverweise.setText(org.openide.util.NbBundle.getMessage(
                RegenFlaechenDetailsPanel.class,
                "RegenFlaechenDetailsPanel.lblTeileigentumQuerverweise.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 0);
        bpanRegenFlDetails.add(lblTeileigentumQuerverweise, gridBagConstraints);

        scpQuer.setOpaque(false);

        edtQuer.setEditable(false);
        edtQuer.setContentType("text/html"); // NOI18N
        edtQuer.setOpaque(false);
        scpQuer.setViewportView(edtQuer);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 2);
        bpanRegenFlDetails.add(scpQuer, gridBagConstraints);

        lblBeschreibung.setText(org.openide.util.NbBundle.getMessage(
                RegenFlaechenDetailsPanel.class,
                "RegenFlaechenDetailsPanel.lblBeschreibung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 0);
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
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 2);
        bpanRegenFlDetails.add(cboBeschreibung, gridBagConstraints);

        jCheckBox1.setText(org.openide.util.NbBundle.getMessage(
                RegenFlaechenDetailsPanel.class,
                "RegenFlaechenDetailsPanel.jCheckBox1.text")); // NOI18N
        jCheckBox1.setContentAreaFilled(false);

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
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 2);
        bpanRegenFlDetails.add(jCheckBox1, gridBagConstraints);

        jPanel1.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.01;
        bpanRegenFlDetails.add(jPanel1, gridBagConstraints);

        pnlGroesseKorrektur.setOpaque(false);
        pnlGroesseKorrektur.setLayout(new java.awt.GridBagLayout());

        lblGroesseAenderung.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/validation/info.png"))); // NOI18N
        lblGroesseAenderung.setText(org.openide.util.NbBundle.getMessage(
                RegenFlaechenDetailsPanel.class,
                "RegenFlaechenDetailsPanel.lblGroesseAenderung.text"));     // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        pnlGroesseKorrektur.add(lblGroesseAenderung, gridBagConstraints);
        lblGroesseAenderung.setVisible(false);

        btnGroesseAenderungAccept.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/redo.png"))); // NOI18N
        btnGroesseAenderungAccept.setText(org.openide.util.NbBundle.getMessage(
                RegenFlaechenDetailsPanel.class,
                "RegenFlaechenDetailsPanel.btnGroesseAenderungAccept.text"));              // NOI18N
        btnGroesseAenderungAccept.setToolTipText(org.openide.util.NbBundle.getMessage(
                RegenFlaechenDetailsPanel.class,
                "RegenFlaechenDetailsPanel.btnGroesseAenderungAccept.toolTipText"));       // NOI18N
        btnGroesseAenderungAccept.setFocusPainted(false);
        btnGroesseAenderungAccept.setMaximumSize(new java.awt.Dimension(24, 24));
        btnGroesseAenderungAccept.setMinimumSize(new java.awt.Dimension(24, 24));
        btnGroesseAenderungAccept.setPreferredSize(new java.awt.Dimension(24, 24));
        btnGroesseAenderungAccept.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnGroesseAenderungAcceptActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        pnlGroesseKorrektur.add(btnGroesseAenderungAccept, gridBagConstraints);
        btnGroesseAenderungAccept.setVisible(false);

        btnGroesseAenderungReject.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/cancel.png"))); // NOI18N
        btnGroesseAenderungReject.setText(org.openide.util.NbBundle.getMessage(
                RegenFlaechenDetailsPanel.class,
                "RegenFlaechenDetailsPanel.btnGroesseAenderungReject.text"));                  // NOI18N
        btnGroesseAenderungReject.setToolTipText("Änderung ablehnen");
        btnGroesseAenderungReject.setFocusPainted(false);
        btnGroesseAenderungReject.setMaximumSize(new java.awt.Dimension(24, 24));
        btnGroesseAenderungReject.setMinimumSize(new java.awt.Dimension(24, 24));
        btnGroesseAenderungReject.setPreferredSize(new java.awt.Dimension(24, 24));
        btnGroesseAenderungReject.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnGroesseAenderungRejectActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        pnlGroesseKorrektur.add(btnGroesseAenderungReject, gridBagConstraints);
        btnGroesseAenderungReject.setVisible(false);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 0);
        bpanRegenFlDetails.add(pnlGroesseKorrektur, gridBagConstraints);

        pnlFlaechenArt.setOpaque(false);
        pnlFlaechenArt.setLayout(new java.awt.GridBagLayout());

        lblFlaechenartAenderung.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/validation/info.png"))); // NOI18N
        lblFlaechenartAenderung.setText(org.openide.util.NbBundle.getMessage(
                RegenFlaechenDetailsPanel.class,
                "RegenFlaechenDetailsPanel.lblFlaechenartAenderung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        pnlFlaechenArt.add(lblFlaechenartAenderung, gridBagConstraints);
        lblFlaechenartAenderung.setVisible(false);

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
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        pnlFlaechenArt.add(cboFlaechenart, gridBagConstraints);

        btnFlaechenartAenderungAccept.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/redo.png"))); // NOI18N
        btnFlaechenartAenderungAccept.setText(org.openide.util.NbBundle.getMessage(
                RegenFlaechenDetailsPanel.class,
                "RegenFlaechenDetailsPanel.btnFlaechenartAenderungAccept.text"));          // NOI18N
        btnFlaechenartAenderungAccept.setToolTipText(org.openide.util.NbBundle.getMessage(
                RegenFlaechenDetailsPanel.class,
                "RegenFlaechenDetailsPanel.btnFlaechenartAenderungAccept.toolTipText"));   // NOI18N
        btnFlaechenartAenderungAccept.setFocusPainted(false);
        btnFlaechenartAenderungAccept.setMaximumSize(new java.awt.Dimension(24, 24));
        btnFlaechenartAenderungAccept.setMinimumSize(new java.awt.Dimension(24, 24));
        btnFlaechenartAenderungAccept.setPreferredSize(new java.awt.Dimension(24, 24));
        btnFlaechenartAenderungAccept.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnFlaechenartAenderungAcceptActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        pnlFlaechenArt.add(btnFlaechenartAenderungAccept, gridBagConstraints);
        btnFlaechenartAenderungAccept.setVisible(false);

        btnFlaechenartAenderungReject.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/cancel.png"))); // NOI18N
        btnFlaechenartAenderungReject.setText(org.openide.util.NbBundle.getMessage(
                RegenFlaechenDetailsPanel.class,
                "RegenFlaechenDetailsPanel.btnFlaechenartAenderungReject.text"));              // NOI18N
        btnFlaechenartAenderungReject.setToolTipText("Änderung ablehnen");
        btnFlaechenartAenderungReject.setFocusPainted(false);
        btnFlaechenartAenderungReject.setMaximumSize(new java.awt.Dimension(24, 24));
        btnFlaechenartAenderungReject.setMinimumSize(new java.awt.Dimension(24, 24));
        btnFlaechenartAenderungReject.setPreferredSize(new java.awt.Dimension(24, 24));
        btnFlaechenartAenderungReject.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnFlaechenartAenderungRejectActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        pnlFlaechenArt.add(btnFlaechenartAenderungReject, gridBagConstraints);
        btnFlaechenartAenderungReject.setVisible(false);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 2);
        bpanRegenFlDetails.add(pnlFlaechenArt, gridBagConstraints);

        pnlAnschlussgrad.setOpaque(false);
        pnlAnschlussgrad.setLayout(new java.awt.GridBagLayout());

        lblAnschlussgradAenderung.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/validation/info.png")));   // NOI18N
        lblAnschlussgradAenderung.setText(org.openide.util.NbBundle.getMessage(
                RegenFlaechenDetailsPanel.class,
                "RegenFlaechenDetailsPanel.lblAnschlussgradAenderung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        pnlAnschlussgrad.add(lblAnschlussgradAenderung, gridBagConstraints);
        lblAnschlussgradAenderung.setVisible(false);

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
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        pnlAnschlussgrad.add(cboAnschlussgrad, gridBagConstraints);

        btnAnschlussgradAenderungAccept.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/redo.png"))); // NOI18N
        btnAnschlussgradAenderungAccept.setText(org.openide.util.NbBundle.getMessage(
                RegenFlaechenDetailsPanel.class,
                "RegenFlaechenDetailsPanel.btnAnschlussgradAenderungAccept.text"));        // NOI18N
        btnAnschlussgradAenderungAccept.setToolTipText(org.openide.util.NbBundle.getMessage(
                RegenFlaechenDetailsPanel.class,
                "RegenFlaechenDetailsPanel.btnAnschlussgradAenderungAccept.toolTipText")); // NOI18N
        btnAnschlussgradAenderungAccept.setFocusPainted(false);
        btnAnschlussgradAenderungAccept.setMaximumSize(new java.awt.Dimension(24, 24));
        btnAnschlussgradAenderungAccept.setMinimumSize(new java.awt.Dimension(24, 24));
        btnAnschlussgradAenderungAccept.setPreferredSize(new java.awt.Dimension(24, 24));
        btnAnschlussgradAenderungAccept.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAnschlussgradAenderungAcceptActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        pnlAnschlussgrad.add(btnAnschlussgradAenderungAccept, gridBagConstraints);
        btnAnschlussgradAenderungAccept.setVisible(false);

        btnAnschlussgradAenderungReject.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/cancel.png"))); // NOI18N
        btnAnschlussgradAenderungReject.setText(org.openide.util.NbBundle.getMessage(
                RegenFlaechenDetailsPanel.class,
                "RegenFlaechenDetailsPanel.btnAnschlussgradAenderungReject.text"));            // NOI18N
        btnAnschlussgradAenderungReject.setToolTipText("Änderung ablehnen");
        btnAnschlussgradAenderungReject.setFocusPainted(false);
        btnAnschlussgradAenderungReject.setMaximumSize(new java.awt.Dimension(24, 24));
        btnAnschlussgradAenderungReject.setMinimumSize(new java.awt.Dimension(24, 24));
        btnAnschlussgradAenderungReject.setPreferredSize(new java.awt.Dimension(24, 24));
        btnAnschlussgradAenderungReject.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAnschlussgradAenderungRejectActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        pnlAnschlussgrad.add(btnAnschlussgradAenderungReject, gridBagConstraints);
        btnAnschlussgradAenderungReject.setVisible(false);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 2);
        bpanRegenFlDetails.add(pnlAnschlussgrad, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.flaecheninfo.groesse_korrektur}"),
                txtGroesseKorrektur,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                        + "."
                        + VerdisConstants.PROP.FLAECHENINFO.GROESSE_KORREKTUR);
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
        bpanRegenFlDetails.add(txtGroesseKorrektur, gridBagConstraints);

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
    private void btnGroesseAenderungAcceptActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnGroesseAenderungAcceptActionPerformed
        try {
            flaecheBean.setProperty(VerdisConstants.PROP.FLAECHE.FLAECHENINFO + "."
                        + VerdisConstants.PROP.FLAECHENINFO.GROESSE_KORREKTUR,
                flaecheJson.getGroesse());
            acceptAenderungGroesse();
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }
        refreshAenderungButtons(isEnabled());
    }                                                                                             //GEN-LAST:event_btnGroesseAenderungAcceptActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnGroesseAenderungRejectActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnGroesseAenderungRejectActionPerformed
        rejectAenderungGroesse();
        refreshAenderungButtons(isEnabled());
    }                                                                                             //GEN-LAST:event_btnGroesseAenderungRejectActionPerformed

    /**
     * DOCUMENT ME!
     */
    private void acceptAenderungGroesse() {
        final PruefungJson pruefungJson = new PruefungJson(
                PruefungJson.Status.ACCEPTED,
                SessionManager.getSession().getUser().getName(),
                new Date());
        if (flaecheJson.getPruefung() == null) {
            flaecheJson.setPruefung(new FlaechePruefungGroesseJson(pruefungJson));
        } else {
            flaecheJson.getPruefung().setGroesse(pruefungJson);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void rejectAenderungGroesse() {
        final PruefungJson pruefungJson = new PruefungJson(
                PruefungJson.Status.REJECTED,
                SessionManager.getSession().getUser().getName(),
                new Date());
        if (flaecheJson.getPruefung() == null) {
            flaecheJson.setPruefung(new FlaechePruefungGroesseJson(pruefungJson));
        } else {
            flaecheJson.getPruefung().setGroesse(pruefungJson);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void acceptAenderungFlaechenart() {
        final PruefungJson pruefungJson = new PruefungJson(
                PruefungJson.Status.ACCEPTED,
                SessionManager.getSession().getUser().getName(),
                new Date());
        if (flaecheJson.getPruefung() == null) {
            flaecheJson.setPruefung(new FlaechePruefungFlaechenartJson(pruefungJson));
        } else {
            flaecheJson.getPruefung().setFlaechenart(pruefungJson);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void rejectAenderungFlaechenart() {
        final PruefungJson pruefungJson = new PruefungJson(
                PruefungJson.Status.REJECTED,
                SessionManager.getSession().getUser().getName(),
                new Date());
        if (flaecheJson.getPruefung() == null) {
            flaecheJson.setPruefung(new FlaechePruefungFlaechenartJson(pruefungJson));
        } else {
            flaecheJson.getPruefung().setFlaechenart(pruefungJson);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void acceptAenderungAnschlussgrad() {
        final PruefungJson pruefungJson = new PruefungJson(
                PruefungJson.Status.ACCEPTED,
                SessionManager.getSession().getUser().getName(),
                new Date());
        if (flaecheJson.getPruefung() == null) {
            flaecheJson.setPruefung(new FlaechePruefungAnschlussgradJson(pruefungJson));
        } else {
            flaecheJson.getPruefung().setAnschlussgrad(pruefungJson);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void rejectAenderungAnschlussgrad() {
        final PruefungJson pruefungJson = new PruefungJson(
                PruefungJson.Status.REJECTED,
                SessionManager.getSession().getUser().getName(),
                new Date());
        if (flaecheJson.getPruefung() == null) {
            flaecheJson.setPruefung(new FlaechePruefungAnschlussgradJson(pruefungJson));
        } else {
            flaecheJson.getPruefung().setAnschlussgrad(pruefungJson);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnFlaechenartAenderungAcceptActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnFlaechenartAenderungAcceptActionPerformed
        try {
            for (int i = 0; i < cboAnschlussgrad.getModel().getSize(); i++) {
                final CidsBean flaechenartBean = (CidsBean)cboFlaechenart.getModel().getElementAt(i);
                if (flaecheJson.getFlaechenart().getArt().equals(
                                (String)flaechenartBean.getProperty(VerdisConstants.PROP.FLAECHENART.ART))) {
                    flaecheBean.setProperty(VerdisConstants.PROP.FLAECHE.FLAECHENINFO + "."
                                + VerdisConstants.PROP.FLAECHENINFO.FLAECHENART,
                        flaechenartBean);

                    break;
                }
            }
            acceptAenderungFlaechenart();
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }
        refreshAenderungButtons(isEnabled());
    } //GEN-LAST:event_btnFlaechenartAenderungAcceptActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnFlaechenartAenderungRejectActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnFlaechenartAenderungRejectActionPerformed
        rejectAenderungFlaechenart();
        refreshAenderungButtons(isEnabled());
    }                                                                                                 //GEN-LAST:event_btnFlaechenartAenderungRejectActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAnschlussgradAenderungRejectActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAnschlussgradAenderungRejectActionPerformed
        rejectAenderungAnschlussgrad();
        refreshAenderungButtons(isEnabled());
    }                                                                                                   //GEN-LAST:event_btnAnschlussgradAenderungRejectActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAnschlussgradAenderungAcceptActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAnschlussgradAenderungAcceptActionPerformed
        try {
            for (int i = 0; i < cboAnschlussgrad.getModel().getSize(); i++) {
                final CidsBean anschlussgradBean = (CidsBean)cboAnschlussgrad.getModel().getElementAt(i);
                if (flaecheJson.getAnschlussgrad().getGrad().equals(
                                (String)anschlussgradBean.getProperty(VerdisConstants.PROP.ANSCHLUSSGRAD.GRAD))) {
                    flaecheBean.setProperty(VerdisConstants.PROP.FLAECHE.FLAECHENINFO + "."
                                + VerdisConstants.PROP.FLAECHENINFO.ANSCHLUSSGRAD,
                        anschlussgradBean);

                    break;
                }
            }
            acceptAenderungAnschlussgrad();
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }
        refreshAenderungButtons(isEnabled());
    } //GEN-LAST:event_btnAnschlussgradAenderungAcceptActionPerformed

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
                                VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                + "."
                                + VerdisConstants.PROP.FLAECHENINFO.GEOMETRIE) != null)) {
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
            jCheckBox1.setSelected(false);
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

    /**
     * DOCUMENT ME!
     *
     * @param  b  DOCUMENT ME!
     */
    private void refreshFieldsEnabled(final boolean b) {
        txtAnteil.setEnabled(true);
        txtAnteil.setEditable(b);
        txtBemerkung.setEnabled(true);
        txtBemerkung.setEditable(b);
        txtBezeichnung.setEnabled(true);
        txtBezeichnung.setEditable(b);
        try {
            txtGroesseGrafik.setEditable(b
                        && (VerdisUtils.PROPVAL_ART_VORLAEUFIGEVERANLASSUNG
                            != (Integer)getCidsBean().getProperty(
                                VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                + "."
                                + VerdisConstants.PROP.FLAECHENINFO.FLAECHENART
                                + "."
                                + VerdisConstants.PROP.FLAECHENART.ID)));
        } catch (final Exception ex) {
            txtGroesseGrafik.setEditable(b);
        }
        txtGroesseGrafik.setEnabled(true);
        txtGroesseKorrektur.setEditable(b);
        txtGroesseKorrektur.setEnabled(true);
        txtVeranlagungsdatum.setEditable(b);
        txtVeranlagungsdatum.setEnabled(true);
        jCheckBox1.setEnabled(b);
        try {
            cboAnschlussgrad.setEnabled(b
                        && (VerdisUtils.PROPVAL_ART_VORLAEUFIGEVERANLASSUNG
                            != (Integer)getCidsBean().getProperty(
                                VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                + "."
                                + VerdisConstants.PROP.FLAECHENINFO.FLAECHENART
                                + "."
                                + VerdisConstants.PROP.FLAECHENART.ID)));
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
        txtGroesseGrafik.setOpaque(b);
        txtGroesseKorrektur.setOpaque(b);
        txtVeranlagungsdatum.setOpaque(b);

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

        refreshAenderungButtons(b);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  b  DOCUMENT ME!
     */
    private void refreshAenderungButtons(final boolean b) {
        try {
            final AnfrageJson aenderungsanfrageJson = CidsAppBackend.getInstance().getAenderungsanfrageJson();
            final String flaechebezeichnung = (flaecheBean != null)
                ? (String)flaecheBean.getProperty(VerdisConstants.PROP.FLAECHE.FLAECHENBEZEICHNUNG) : null;
            flaecheJson = (aenderungsanfrageJson != null) ? aenderungsanfrageJson.getFlaechen().get(flaechebezeichnung)
                                                          : null;

            final PruefungJson.Status pruefungGroesseStatus =
                ((flaecheJson != null) && (flaecheJson.getPruefung() != null)
                            && (flaecheJson.getPruefung().getGroesse() != null))
                ? flaecheJson.getPruefung().getGroesse().getStatus() : null;
            final PruefungJson.Status pruefungFlaechenartStatus =
                ((flaecheJson != null) && (flaecheJson.getPruefung() != null)
                            && (flaecheJson.getPruefung().getFlaechenart() != null))
                ? flaecheJson.getPruefung().getFlaechenart().getStatus() : null;
            final PruefungJson.Status pruefungAnschlussgradStatus =
                ((flaecheJson != null) && (flaecheJson.getPruefung() != null)
                            && (flaecheJson.getPruefung().getAnschlussgrad() != null))
                ? flaecheJson.getPruefung().getAnschlussgrad().getStatus() : null;

            final boolean isGroesseAccepted = PruefungJson.Status.ACCEPTED.equals(pruefungGroesseStatus);
            final boolean isGroesseRejected = PruefungJson.Status.REJECTED.equals(pruefungGroesseStatus);
            final boolean isFlaechenartAccepted = PruefungJson.Status.ACCEPTED.equals(pruefungFlaechenartStatus);
            final boolean isFlaechenartRejected = PruefungJson.Status.REJECTED.equals(pruefungFlaechenartStatus);
            final boolean isAnschlussgradAccepted = PruefungJson.Status.ACCEPTED.equals(pruefungAnschlussgradStatus);
            final boolean isAnschlussgradRejected = PruefungJson.Status.REJECTED.equals(pruefungAnschlussgradStatus);

            lblGroesseAenderung.setVisible((flaecheJson != null) && (flaecheJson.getGroesse() != null));
            lblFlaechenartAenderung.setVisible((flaecheJson != null) && (flaecheJson.getFlaechenart() != null));
            lblAnschlussgradAenderung.setVisible((flaecheJson != null) && (flaecheJson.getAnschlussgrad() != null));

            btnGroesseAenderungAccept.setVisible(!isGroesseAccepted && (flaecheJson != null)
                        && (flaecheJson.getGroesse() != null) && b);
            btnFlaechenartAenderungAccept.setVisible(!isFlaechenartAccepted && (flaecheJson != null)
                        && (flaecheJson.getFlaechenart() != null)
                        && b);
            btnAnschlussgradAenderungAccept.setVisible(!isAnschlussgradAccepted && (flaecheJson != null)
                        && (flaecheJson.getAnschlussgrad() != null)
                        && b);
            btnGroesseAenderungReject.setVisible(!isGroesseRejected && (flaecheJson != null)
                        && (flaecheJson.getGroesse() != null) && b);
            btnFlaechenartAenderungReject.setVisible(!isFlaechenartRejected && (flaecheJson != null)
                        && (flaecheJson.getFlaechenart() != null)
                        && b);
            btnAnschlussgradAenderungReject.setVisible(!isAnschlussgradRejected && (flaecheJson != null)
                        && (flaecheJson.getAnschlussgrad() != null)
                        && b);

            lblGroesseAenderung.setText((flaecheJson != null)
                    ? NumberFormat.getIntegerInstance().format(flaecheJson.getGroesse()) : null);
            lblFlaechenartAenderung.setText((flaecheJson != null) ? flaecheJson.getFlaechenart().getArtAbkuerzung()
                                                                  : null);
            lblAnschlussgradAenderung.setText((flaecheJson != null) ? flaecheJson.getAnschlussgrad()
                            .getGradAbkuerzung() : null);

            if (flaecheJson != null) {
                if (PruefungJson.Status.ACCEPTED.equals(pruefungGroesseStatus)) {
                    lblGroesseAenderung.setIcon(btnGroesseAenderungAccept.getIcon());
                } else if (PruefungJson.Status.REJECTED.equals(pruefungGroesseStatus)) {
                    lblGroesseAenderung.setIcon(btnGroesseAenderungReject.getIcon());
                } else {
                    lblGroesseAenderung.setIcon(AENDERUNG_ICON);
                }
                if (PruefungJson.Status.ACCEPTED.equals(pruefungFlaechenartStatus)) {
                    lblFlaechenartAenderung.setIcon(btnFlaechenartAenderungAccept.getIcon());
                } else if (PruefungJson.Status.REJECTED.equals(pruefungFlaechenartStatus)) {
                    lblFlaechenartAenderung.setIcon(btnFlaechenartAenderungReject.getIcon());
                } else {
                    lblFlaechenartAenderung.setIcon(AENDERUNG_ICON);
                }
                if (PruefungJson.Status.ACCEPTED.equals(pruefungAnschlussgradStatus)) {
                    lblAnschlussgradAenderung.setIcon(btnAnschlussgradAenderungAccept.getIcon());
                } else if (PruefungJson.Status.REJECTED.equals(pruefungAnschlussgradStatus)) {
                    lblAnschlussgradAenderung.setIcon(btnAnschlussgradAenderungReject.getIcon());
                } else {
                    lblAnschlussgradAenderung.setIcon(AENDERUNG_ICON);
                }
            }
        } catch (final Exception ex) {
            LOG.error(ex, ex);
            lblGroesseAenderung.setVisible(false);
            lblFlaechenartAenderung.setVisible(false);
            lblAnschlussgradAenderung.setVisible(false);
        }
    }

    @Override
    public final void setEnabled(final boolean b) {
        super.setEnabled(b);
        refreshFieldsEnabled(b);
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
                VerdisConstants.PROP.FLAECHE.FLAECHENBEZEICHNUNG,
                VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                        + "."
                        + VerdisConstants.PROP.FLAECHENINFO.FLAECHENART) {

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
                                VerdisConstants.PROP.FLAECHE.FLAECHENBEZEICHNUNG);
                        final int art =
                            (flaecheBean.getProperty(
                                    VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                            + "."
                                            + VerdisConstants.PROP.FLAECHENINFO.FLAECHENART
                                            + "."
                                            + VerdisConstants.PROP.FLAECHENART.ID)
                                        == null)
                            ? 0
                            : (Integer)flaecheBean.getProperty(
                                VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                        + "."
                                        + VerdisConstants.PROP.FLAECHENINFO.FLAECHENART
                                        + "."
                                        + VerdisConstants.PROP.FLAECHENART.ID);
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
                                                    VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                                            + "."
                                                            + VerdisConstants.PROP.FLAECHENINFO.FLAECHENART
                                                            + "."
                                                            + VerdisConstants.PROP.FLAECHENART.ID);
                                        } catch (final NumberFormatException ex) {
                                            art = 0;
                                        }
                                        final String newValue = Main.getInstance()
                                                    .getRegenFlaechenTable()
                                                    .getValidFlaechenname(art);
                                        try {
                                            flaecheBean.setProperty(
                                                VerdisConstants.PROP.FLAECHE.FLAECHENBEZEICHNUNG,
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
                VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                        + "."
                        + VerdisConstants.PROP.FLAECHENINFO.GROESSE_GRAFIK,
                VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                        + "."
                        + VerdisConstants.PROP.FLAECHENINFO.FLAECHENART,
                VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                        + "."
                        + VerdisConstants.PROP.FLAECHENINFO.GEOMETRIE
                        + "."
                        + VerdisConstants.PROP.GEOM.GEO_FIELD) {

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
                                VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                        + "."
                                        + VerdisConstants.PROP.FLAECHENINFO.GROESSE_GRAFIK);
                        if (backupBean != null) {
                            final Integer backupGroesseGrafik = (Integer)backupBean.getProperty(
                                    VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                            + "."
                                            + VerdisConstants.PROP.FLAECHENINFO.GROESSE_GRAFIK);
                            if ((backupGroesseGrafik != null) && (groesseGrafik != null)
                                        && (Math.abs(backupGroesseGrafik - groesseGrafik)
                                            > CidsAppBackend.getInstance().getAppPreferences()
                                            .getNachgewiesenFalseThreshold())) {
                                try {
                                    flaecheBean.setProperty(VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                                + "."
                                                + VerdisConstants.PROP.FLAECHENINFO.NACHGEWIESEN,
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
                                                        VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                                        + "."
                                                        + VerdisConstants.PROP.FLAECHENINFO.FLAECHENART
                                                        + "."
                                                        + VerdisConstants.PROP.FLAECHENART.ID)) {
                                            try {
                                                flaecheBean.setProperty(
                                                    VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                                            + "."
                                                            + VerdisConstants.PROP.FLAECHENINFO.GROESSE_GRAFIK,
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
                                                        VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                                                + "."
                                                                + VerdisConstants.PROP.FLAECHENINFO.GROESSE_GRAFIK,
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
                                VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                        + "."
                                        + VerdisConstants.PROP.FLAECHENINFO.FLAECHENART
                                        + "."
                                        + VerdisConstants.PROP.FLAECHENART.ID);
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
                VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                        + "."
                        + VerdisConstants.PROP.FLAECHENINFO.GROESSE_KORREKTUR,
                VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                        + "."
                        + VerdisConstants.PROP.FLAECHENINFO.FLAECHENART,
                VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                        + "."
                        + VerdisConstants.PROP.FLAECHENINFO.GROESSE_GRAFIK) {

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
                                VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                        + "."
                                        + VerdisConstants.PROP.FLAECHENINFO.GROESSE_GRAFIK);
                        final Integer groesseKorrektur = (Integer)flaecheBean.getProperty(
                                VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                        + "."
                                        + VerdisConstants.PROP.FLAECHENINFO.GROESSE_KORREKTUR);

                        final CidsBean backupBean = Main.getInstance()
                                    .getRegenFlaechenTable()
                                    .getBeanBackup(flaecheBean);
                        if (backupBean != null) {
                            final Integer backupGroesseKorrektur = (Integer)backupBean.getProperty(
                                    VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                            + "."
                                            + VerdisConstants.PROP.FLAECHENINFO.GROESSE_KORREKTUR);

                            if ((backupGroesseKorrektur != null) && (groesseKorrektur != null)
                                        && (Math.abs(backupGroesseKorrektur - groesseKorrektur)
                                            > CidsAppBackend.getInstance().getAppPreferences()
                                            .getNachgewiesenFalseThreshold())) {
                                try {
                                    flaecheBean.setProperty(VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                                + "."
                                                + VerdisConstants.PROP.FLAECHENINFO.NACHGEWIESEN,
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
                                                            VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                                                    + "."
                                                                    + VerdisConstants.PROP.FLAECHENINFO.GROESSE_GRAFIK);
                                                    flaecheBean.setProperty(
                                                        VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                                                + "."
                                                                + VerdisConstants.PROP.FLAECHENINFO.GROESSE_KORREKTUR,
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
        return new CidsBeanValidator(flaecheBean, VerdisConstants.PROP.FLAECHE.ANTEIL) {

                @Override
                public ValidatorState performValidation() {
                    final boolean doNotValidate = (flaecheBean == null)
                                || ((getTriggerdByProperty() != null)
                                    && mbh.getAttachedProperties().contains(getTriggerdByProperty())
                                    && !mbh.isValuesAllEquals(getTriggerdByProperty()));
                    if (doNotValidate && ((flaecheBean == null) || flaecheBean.equals(mbh.getDummyBean()))) {
                        return null;
                    } else {
                        final Float anteil = (Float)flaecheBean.getProperty(VerdisConstants.PROP.FLAECHE.ANTEIL);
                        final Integer gr_grafik = (Integer)flaecheBean.getProperty(
                                VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                        + "."
                                        + VerdisConstants.PROP.FLAECHENINFO.GROESSE_GRAFIK);
                        final Integer gr_korrektur = (Integer)flaecheBean.getProperty(
                                VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                        + "."
                                        + VerdisConstants.PROP.FLAECHENINFO.GROESSE_KORREKTUR);

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
        return new CidsBeanValidator(flaecheBean, VerdisConstants.PROP.FLAECHE.DATUM_AENDERUNG) {

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
        return new CidsBeanValidator(flaecheBean, VerdisConstants.PROP.FLAECHE.DATUM_VERANLAGUNG) {

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
                                VerdisConstants.PROP.FLAECHE.DATUM_VERANLAGUNG);

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
    private Validator getValidatorFlaechenart(final CidsBean flaecheBean) {
        final MultiBeanHelper mbh = RegenFlaechenDetailsPanel.getInstance().getMultiBeanHelper();
        return new CidsBeanValidator(
                flaecheBean,
                VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                        + "."
                        + VerdisConstants.PROP.FLAECHENINFO.FLAECHENART,
                VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                        + "."
                        + VerdisConstants.PROP.FLAECHENINFO.GEOMETRIE
                        + "."
                        + VerdisConstants.PROP.GEOM.GEO_FIELD) {

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
                                VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                        + "."
                                        + VerdisConstants.PROP.FLAECHENINFO.FLAECHENART);

                        if ((flaechenart != null)
                                    && (VerdisUtils.PROPVAL_ART_VORLAEUFIGEVERANLASSUNG
                                        == (Integer)flaechenart.getProperty(VerdisConstants.PROP.FLAECHENART.ID))) {
                            try {
                                flaecheBean.setProperty(VerdisConstants.PROP.FLAECHE.FLAECHENINFO + "."
                                            + VerdisConstants.PROP.FLAECHENINFO.ANSCHLUSSGRAD,
                                    anschlussgradBean);
                                flaecheBean.setProperty(
                                    VerdisConstants.PROP.FLAECHE.FLAECHENBEZEICHNUNG,
                                    Main.getInstance().getRegenFlaechenTable().getValidFlaechenname(
                                        (Integer)flaechenart.getProperty("id")));
                                flaecheBean.setProperty(VerdisConstants.PROP.FLAECHE.FLAECHENINFO + "."
                                            + VerdisConstants.PROP.FLAECHENINFO.GROESSE_GRAFIK,
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
                                        == (Integer)flaechenart.getProperty(VerdisConstants.PROP.FLAECHENART.ID))
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
                                                VerdisConstants.PROP.KASSENZEICHEN.FLAECHEN).size() > 1)
                                        || ((kassenzeichenBean.getBeanCollectionProperty(
                                                    VerdisConstants.PROP.KASSENZEICHEN.FLAECHEN).size() == 1)
                                            && (kassenzeichenBean.getBeanCollectionProperty(
                                                    VerdisConstants.PROP.KASSENZEICHEN.FLAECHEN).iterator().next()
                                                .getProperty(
                                                    VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                                    + "."
                                                    + VerdisConstants.PROP.FLAECHENINFO.GEOMETRIE
                                                    + "."
                                                    + VerdisConstants.PROP.GEOM.GEO_FIELD) != null)))) {
                            final DefaultComboBoxModel aModel = (DefaultComboBoxModel)evt.getNewValue();
                            Object vvobject = null;
                            for (int index = 0; (index < aModel.getSize()) && (vvobject == null); index++) {
                                final Object object = aModel.getElementAt(index);
                                if ((object instanceof CidsBean)
                                            && ((Integer)((CidsBean)object).getProperty(
                                                    VerdisConstants.PROP.FLAECHENART.ID)
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
                    .getVerdisMetaClass(VerdisConstants.MC.FLAECHE)
                    .getEmptyInstance()
                    .getBean();
        final CidsBean flaecheninfoBean = CidsAppBackend.getInstance()
                    .getVerdisMetaClass(VerdisConstants.MC.FLAECHENINFO)
                    .getEmptyInstance()
                    .getBean();
        final CidsBean geomBean = CidsAppBackend.getInstance()
                    .getVerdisMetaClass(VerdisConstants.MC.GEOM)
                    .getEmptyInstance()
                    .getBean();
        try {
            dummyBean.setProperty(VerdisConstants.PROP.FLAECHE.FLAECHENINFO, flaecheninfoBean);
            dummyBean.setProperty(VerdisConstants.PROP.FLAECHE.FLAECHENINFO + "."
                        + VerdisConstants.PROP.FLAECHENINFO.GEOMETRIE,
                geomBean);
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }
        return dummyBean;
    }
}
