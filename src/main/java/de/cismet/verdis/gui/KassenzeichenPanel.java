/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * KassenzeichenPanel.java
 *
 * Created on 5. Januar 2005, 14:01
 */
package de.cismet.verdis.gui;

import Sirius.navigator.connection.SessionManager;

import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.Converter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.util.BindingValidationSupport;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;

import de.cismet.cids.editors.converters.SqlDateToStringConverter;

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.historybutton.JHistoryButton;

import de.cismet.validation.*;

import de.cismet.validation.display.EmbeddedValidatorDisplay;

import de.cismet.validation.validator.AggregatedValidator;
import de.cismet.validation.validator.CidsBeanValidator;

import de.cismet.verdis.AppModeListener;
import de.cismet.verdis.CidsAppBackend;
import de.cismet.verdis.EditModeListener;

import de.cismet.verdis.commons.constants.VerdisConstants;

/**
 * DOCUMENT ME!
 *
 * @author   hell
 * @version  $Revision$, $Date$
 */
public class KassenzeichenPanel extends javax.swing.JPanel implements CidsBeanStore,
    AppModeListener,
    EditModeListener,
    Validatable {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(KassenzeichenPanel.class);

    //~ Instance fields --------------------------------------------------------

    private Main mainApp;
    private JHistoryButton hbBack;
    private JHistoryButton hbFwd;
    private CidsBean kassenzeichenBean;
    private final Validator bindingValidator;
    private final AggregatedValidator aggVal = new AggregatedValidator();
    private MultiBemerkung multi;

    private final PropertyChangeListener luftbildmerkerPropChangeListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                if ((evt.getSource() != null) && evt.getSource().equals(getCidsBean())
                            && VerdisConstants.PROP.KASSENZEICHEN.LUFTBILDMERKER.equals(evt.getPropertyName())) {
                    try {
                        getCidsBean().setProperty(
                            "luftbildmerker_von",
                            SessionManager.getSession().getUser().getName());
                        getCidsBean().setProperty(
                            "luftbildmerker_am",
                            new Timestamp(Calendar.getInstance().getTime().getTime()));
                    } catch (final Exception ex) {
                        LOG.error(ex, ex);
                    }
                }
            }
        };

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup btgMode;
    private javax.swing.JButton btnSearch;
    private javax.swing.JCheckBox chkSperre;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JFormattedTextField jFormattedTextField1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JLabel lblBemerkung;
    private javax.swing.JLabel lblErfassungsdatum;
    private javax.swing.JLabel lblKassenzeichen;
    private javax.swing.JLabel lblLastModification;
    private javax.swing.JLabel lblLuftbildmerker;
    private javax.swing.JLabel lblSperre;
    private javax.swing.JLabel lblSuche;
    private javax.swing.JPanel panKZValues;
    private javax.swing.JPanel panSearch;
    private javax.swing.JScrollPane scpBemerkung;
    private javax.swing.JSeparator sepTitle1;
    private javax.swing.JSeparator sepTitle2;
    private javax.swing.JToggleButton togInfoMode;
    private javax.swing.JToggleButton togRegenMode;
    private javax.swing.JToggleButton togSRMode;
    private javax.swing.JToggleButton togVersickerungMode;
    private javax.swing.JTextField txtErfassungsdatum;
    private javax.swing.JTextField txtKassenzeichen;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtSperreBemerkung;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form KassenzeichenPanel.
     */
    public KassenzeichenPanel() {
        initComponents();

        jTextPane1.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);

        bindingValidator = BindingValidationSupport.attachBindingValidationToAllTargets(bindingGroup);

        setEnabled(false);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void attachBeanValidators() {
        ValidatorHelper.removeAllNoBindingValidatorFromDisplay(txtKassenzeichen);
        getValidatorKassenzeichenNummer(kassenzeichenBean).attachDisplay(EmbeddedValidatorDisplay.getEmbeddedDisplayFor(
                txtKassenzeichen));

        ValidatorHelper.removeAllNoBindingValidatorFromDisplay(jFormattedTextField1);
        getValidatorLuftbildmerker(kassenzeichenBean).attachDisplay(EmbeddedValidatorDisplay.getEmbeddedDisplayFor(
                jFormattedTextField1));
    }

    @Override
    public CidsBean getCidsBean() {
        return kassenzeichenBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        final CidsBean oldCidsBean = getCidsBean();
        if (oldCidsBean != null) {
            oldCidsBean.removePropertyChangeListener(luftbildmerkerPropChangeListener);
        }
        if (cidsBean != null) {
            cidsBean.addPropertyChangeListener(luftbildmerkerPropChangeListener);
            final String asJson = (String)cidsBean.getProperty(VerdisConstants.PROP.KASSENZEICHEN.BEMERKUNG);
            multi = CidsAppBackend.transformMultiBemerkungFromJson(asJson);
            CidsAppBackend.cleanupMultiBemerkung(multi);
        } else {
            txtErfassungsdatum.setText("");
            txtKassenzeichen.setText("");
            txtSperreBemerkung.setText("");
            chkSperre.setSelected(false);
            multi = null;
        }
        jTextPane1.setText(CidsAppBackend.transformMultiBemerkungToHtml(multi));

        aggVal.clear();
        aggVal.add(bindingValidator);

        try {
            bindingGroup.unbind();
        } catch (final Exception ex) {
            LOG.warn("error while unbinding", ex);
        }
        kassenzeichenBean = cidsBean;
        bindingGroup.bind();

        updateLuftbildmerkerGUI(isEnabled(), cidsBean);
        attachBeanValidators();

        aggVal.add(getValidatorKassenzeichenNummer(kassenzeichenBean));
        aggVal.add(getValidatorLuftbildmerker(kassenzeichenBean));
        aggVal.validate();

        mainApp.refreshLeftTitleBarColor();
        Main.getInstance().refreshTitle();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  enabled   DOCUMENT ME!
     * @param  cidsBean  DOCUMENT ME!
     */
    private void updateLuftbildmerkerGUI(final boolean enabled, final CidsBean cidsBean) {
        jCheckBox1.setEnabled(enabled);
        jFormattedTextField1.setEnabled(enabled);
        final Integer luftbildmerker = (cidsBean != null) ? (Integer)cidsBean.getProperty("luftbildmerker") : null;
        jLabel1.setVisible(!enabled && (luftbildmerker != null));
        lblLuftbildmerker.setVisible(enabled || (luftbildmerker != null));
        jCheckBox1.setVisible(enabled);
        jFormattedTextField1.setVisible(enabled);
        jPanel3.setVisible(enabled || (luftbildmerker != null));
    }

    /**
     * DOCUMENT ME!
     *
     * @param  ma  DOCUMENT ME!
     */
    public void setMainApp(final Main ma) {
        mainApp = ma;
    }

    @Override
    public final void setEnabled(final boolean b) {
        super.setEnabled(b);
        txtErfassungsdatum.setEditable(b);
        chkSperre.setEnabled(b);
        txtSearch.setEnabled(!b);
        btnSearch.setEnabled(!b);
        if (b) {
            jTextPane1.setBackground(java.awt.Color.white);
        } else {
            jTextPane1.setBackground(this.getBackground());
        }
        updateLuftbildmerkerGUI(b, getCidsBean());
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isLocked() {
        return chkSperre.isSelected();
    }

    /**
     * DOCUMENT ME! ^
     *
     * @return  DOCUMENT ME!
     */
    public Collection<JComponent> getCustomButtons() {
        configureButtons();
        lblLastModification.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/goto.png")));
        lblLastModification.setOpaque(false);
        hbBack.revalidate();
        hbFwd.revalidate();

        final Collection<JComponent> tmp = new ArrayList<JComponent>();
        tmp.add(lblLastModification);
        tmp.add(hbBack);
        tmp.add(hbFwd);
        return tmp;
    }

    /**
     * Inserting Docking Window functionalty (Sebastian) 24.07.07.
     */
    private void configureButtons() {
        hbBack = JHistoryButton.getDefaultJHistoryButton(
                JHistoryButton.DIRECTION_BACKWARD,
                JHistoryButton.ICON_SIZE_16,
                CidsAppBackend.getInstance().getHistoryModel());
        hbFwd = JHistoryButton.getDefaultJHistoryButton(
                JHistoryButton.DIRECTION_FORWARD,
                JHistoryButton.ICON_SIZE_16,
                CidsAppBackend.getInstance().getHistoryModel());

        hbBack.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        hbBack.setOpaque(false);

        hbFwd.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        hbFwd.setOpaque(false);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        btgMode = new javax.swing.ButtonGroup();
        lblLastModification = new javax.swing.JLabel();
        panSearch = new javax.swing.JPanel();
        txtSearch = new javax.swing.JTextField();
        lblSuche = new javax.swing.JLabel();
        btnSearch = new javax.swing.JButton();
        panKZValues = new javax.swing.JPanel();
        lblKassenzeichen = new javax.swing.JLabel();
        txtKassenzeichen = new javax.swing.JTextField();
        lblErfassungsdatum = new javax.swing.JLabel();
        txtErfassungsdatum = new javax.swing.JTextField();
        lblLuftbildmerker = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jFormattedTextField1 = new javax.swing.JFormattedTextField();
        jCheckBox1 = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        lblBemerkung = new javax.swing.JLabel();
        scpBemerkung = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        lblSperre = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        chkSperre = new javax.swing.JCheckBox();
        txtSperreBemerkung = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        togRegenMode = new javax.swing.JToggleButton();
        togSRMode = new javax.swing.JToggleButton();
        togInfoMode = new javax.swing.JToggleButton();
        togVersickerungMode = new javax.swing.JToggleButton();
        sepTitle1 = new javax.swing.JSeparator();
        sepTitle2 = new javax.swing.JSeparator();

        lblLastModification.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/goto.png"))); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.letzte_aenderung_von}"),
                lblLastModification,
                org.jdesktop.beansbinding.BeanProperty.create("toolTipText"));
        bindingGroup.addBinding(binding);

        setLayout(new java.awt.GridBagLayout());

        panSearch.setLayout(new java.awt.GridBagLayout());

        txtSearch.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    txtSearchActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        panSearch.add(txtSearch, gridBagConstraints);

        lblSuche.setText("Kassenzeichen:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        panSearch.add(lblSuche, gridBagConstraints);

        btnSearch.setMnemonic('s');
        btnSearch.setText("suchen");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnSearchActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        panSearch.add(btnSearch, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        add(panSearch, gridBagConstraints);

        panKZValues.setLayout(new java.awt.GridBagLayout());

        lblKassenzeichen.setText("Kassenzeichen:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panKZValues.add(lblKassenzeichen, gridBagConstraints);

        txtKassenzeichen.setEditable(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kassenzeichennummer8}"),
                txtKassenzeichen,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                VerdisConstants.PROP.KASSENZEICHEN.KASSENZEICHENNUMMER);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 3, 0);
        panKZValues.add(txtKassenzeichen, gridBagConstraints);

        lblErfassungsdatum.setText("Datum der Erfassung:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panKZValues.add(lblErfassungsdatum, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.datum_erfassung}"),
                txtErfassungsdatum,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                VerdisConstants.PROP.KASSENZEICHEN.DATUM_ERFASSUNG);
        binding.setConverter(new SqlDateToStringConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 6, 3, 0);
        panKZValues.add(txtErfassungsdatum, gridBagConstraints);

        lblLuftbildmerker.setText("Luftbildmerker:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panKZValues.add(lblLuftbildmerker, gridBagConstraints);

        jPanel3.setOpaque(false);
        jPanel3.setLayout(new java.awt.GridBagLayout());

        jFormattedTextField1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
                new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("####"))));
        jFormattedTextField1.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.luftbildmerker}"),
                jFormattedTextField1,
                org.jdesktop.beansbinding.BeanProperty.create("value"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        binding.setConverter(new IntegerToLongConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        jPanel3.add(jFormattedTextField1, gridBagConstraints);

        jCheckBox1.setText("Jahr:");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.luftbildmerker != null}"),
                jCheckBox1,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jCheckBox1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        jPanel3.add(jCheckBox1, gridBagConstraints);

        jLabel1.setBackground(new java.awt.Color(178, 34, 34));
        jLabel1.setForeground(java.awt.Color.white);
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setOpaque(true);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.luftbildmerker}"),
                jLabel1,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("-");
        binding.setSourceUnreadableValue("-");
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create(
                    "gesetzt von ${cidsBean.luftbildmerker_von} am ${cidsBean.luftbildmerker_am}"),
                jLabel1,
                org.jdesktop.beansbinding.BeanProperty.create("toolTipText"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(3, 6, 3, 0);
        panKZValues.add(jPanel3, gridBagConstraints);

        lblBemerkung.setText("Bemerkung:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        panKZValues.add(lblBemerkung, gridBagConstraints);

        scpBemerkung.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scpBemerkung.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scpBemerkung.setMinimumSize(new java.awt.Dimension(6, 36));

        jTextPane1.setEditable(false);
        jTextPane1.setContentType("text/html"); // NOI18N
        jTextPane1.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    jTextPane1MouseClicked(evt);
                }
            });
        scpBemerkung.setViewportView(jTextPane1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 6, 3, 0);
        panKZValues.add(scpBemerkung, gridBagConstraints);

        lblSperre.setText("Veranlagung gesperrt:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panKZValues.add(lblSperre, gridBagConstraints);

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        chkSperre.setForeground(java.awt.Color.red);
        chkSperre.setEnabled(false);
        chkSperre.setFocusPainted(false);

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
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(chkSperre, gridBagConstraints);

        txtSperreBemerkung.setEditable(false);
        txtSperreBemerkung.setBackground(getBackground());
        txtSperreBemerkung.setForeground(java.awt.Color.red);
        txtSperreBemerkung.setBorder(null);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bemerkung_sperre}"),
                txtSperreBemerkung,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                VerdisConstants.PROP.KASSENZEICHEN.BEMERKUNG_SPERRE);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bemerkung_sperre}"),
                txtSperreBemerkung,
                org.jdesktop.beansbinding.BeanProperty.create("toolTipText"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        jPanel1.add(txtSperreBemerkung, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 6, 3, 0);
        panKZValues.add(jPanel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        add(panKZValues, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel2.setText("Modus:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(jLabel2, gridBagConstraints);

        btgMode.add(togRegenMode);
        togRegenMode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/regen_gr.png"))); // NOI18N
        togRegenMode.setToolTipText("Versiegelte Flächen");
        togRegenMode.setFocusable(false);
        togRegenMode.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        togRegenMode.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        togRegenMode.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    togRegenModeActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        jPanel2.add(togRegenMode, gridBagConstraints);

        btgMode.add(togSRMode);
        togSRMode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/esw_gr.png"))); // NOI18N
        togSRMode.setToolTipText("ESW");
        togSRMode.setFocusable(false);
        togSRMode.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        togSRMode.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        togSRMode.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    togSRModeActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanel2.add(togSRMode, gridBagConstraints);

        btgMode.add(togInfoMode);
        togInfoMode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/info_gr.png"))); // NOI18N
        togInfoMode.setToolTipText("Info");
        togInfoMode.setFocusable(false);
        togInfoMode.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        togInfoMode.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        togInfoMode.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    togInfoModeActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        jPanel2.add(togInfoMode, gridBagConstraints);

        btgMode.add(togVersickerungMode);
        togVersickerungMode.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/kanal_gr.png"))); // NOI18N
        togVersickerungMode.setSelected(true);
        togVersickerungMode.setToolTipText("Versickerung");
        togVersickerungMode.setFocusable(false);
        togVersickerungMode.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        togVersickerungMode.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        togVersickerungMode.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    togVersickerungModeActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        jPanel2.add(togVersickerungMode, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 7, 3, 7);
        add(jPanel2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        add(sepTitle1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        add(sepTitle2, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void hbBackActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_hbBackActionPerformed
// TODO add your handling code here:
    } //GEN-LAST:event_hbBackActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void chkSperreActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_chkSperreActionPerformed
        mainApp.refreshLeftTitleBarColor();

        final boolean sperre = chkSperre.isSelected();
        if (sperre) {
            final String answer = JOptionPane.showInputDialog(mainApp.getRootPane(),
                    "Bitte eine Bemerkung zur Sperre angeben.",
                    txtSperreBemerkung.getText());
            if (answer == null) {
                chkSperre.setSelected(false);
            }
            txtSperreBemerkung.setText(answer);
        } else {
            txtSperreBemerkung.setText(null);
        }
    } //GEN-LAST:event_chkSperreActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void txtSearchActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_txtSearchActionPerformed
        gotoTxtKassenzeichen();
    }                                                                             //GEN-LAST:event_txtSearchActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void togInfoModeActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_togInfoModeActionPerformed
        new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        CidsAppBackend.getInstance().setMode(CidsAppBackend.Mode.ALLGEMEIN);
                    } catch (Exception e) {
                        LOG.error("Exception in Background Thread", e);
                    }
                }
            }.execute();
    } //GEN-LAST:event_togInfoModeActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void togRegenModeActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_togRegenModeActionPerformed
        new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        CidsAppBackend.getInstance().setMode(CidsAppBackend.Mode.REGEN);
                    } catch (Exception e) {
                        LOG.error("Exception in Background Thread", e);
                    }
                }
            }.execute();
    } //GEN-LAST:event_togRegenModeActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void togSRModeActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_togSRModeActionPerformed
        new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        CidsAppBackend.getInstance().setMode(CidsAppBackend.Mode.SR);
                    } catch (Exception e) {
                        LOG.error("Exception in Background Thread", e);
                    }
                }
            }.execute();
    } //GEN-LAST:event_togSRModeActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnSearchActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnSearchActionPerformed
        gotoTxtKassenzeichen();
    }                                                                             //GEN-LAST:event_btnSearchActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jTextPane1MouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_jTextPane1MouseClicked
        final MultiTempBemerkungsDialog dialog = MultiTempBemerkungsDialog.getInstance();
        dialog.setMultiBemerkung(multi);
        dialog.setEditable(isEnabled());
        StaticSwingTools.showDialog(dialog);
        try {
            getCidsBean().setProperty(
                VerdisConstants.PROP.KASSENZEICHEN.BEMERKUNG,
                CidsAppBackend.transformMultiBemerkungToJson(multi));
            jTextPane1.setText(CidsAppBackend.transformMultiBemerkungToHtml(multi));
        } catch (final Exception ex) {
            LOG.error(ex, ex);
            jTextPane1.setText(ex.getLocalizedMessage());
        }
    }                                                                          //GEN-LAST:event_jTextPane1MouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void togVersickerungModeActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_togVersickerungModeActionPerformed
        new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        CidsAppBackend.getInstance().setMode(CidsAppBackend.Mode.KANALDATEN);
                    } catch (Exception e) {
                        LOG.error("Exception in Background Thread", e);
                    }
                }
            }.execute();
    } //GEN-LAST:event_togVersickerungModeActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jCheckBox1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jCheckBox1ActionPerformed
        jFormattedTextField1.setEnabled(jCheckBox1.isSelected() && CidsAppBackend.getInstance().isEditable());
        try {
            getCidsBean().setProperty(
                "luftbildmerker",
                jCheckBox1.isSelected() ? Calendar.getInstance().get(Calendar.YEAR) : null);
        } catch (final Exception ex) {
            LOG.error(ex, ex);
            jFormattedTextField1.setEnabled(false);
        }
    }                                                                              //GEN-LAST:event_jCheckBox1ActionPerformed

    @Override
    public void appModeChanged() {
        final CidsAppBackend.Mode mode = CidsAppBackend.getInstance().getMode();
        switch (mode) {
            case ALLGEMEIN: {
                if (!togInfoMode.isSelected()) {
                    togInfoMode.setSelected(true);
                }
            }
            break;
            case SR: {
                if (!togSRMode.isSelected()) {
                    togSRMode.setSelected(true);
                }
            }
            break;
            case REGEN: {
                if (!togRegenMode.isSelected()) {
                    togRegenMode.setSelected(true);
                }
            }
            break;
            case KANALDATEN: {
                if (!togVersickerungMode.isSelected()) {
                    togVersickerungMode.setSelected(true);
                }
            }
            break;
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void setSearchStarted() {
        txtSearch.setEnabled(false);
        btnSearch.setEnabled(false);
    }

    /**
     * DOCUMENT ME!
     */
    public void setSearchFinished() {
        txtSearch.setEnabled(true);
        btnSearch.setEnabled(true);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kassenzeichen  DOCUMENT ME!
     */
    public void setSearchField(final String kassenzeichen) {
        this.txtSearch.setText(kassenzeichen);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getSearchField() {
        return this.txtSearch.getText();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  c  DOCUMENT ME!
     */
    public void flashSearchField(final java.awt.Color c) {
        txtSearch.setBackground(c);

        final java.awt.event.ActionListener timerAction = new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent event) {
                    txtSearch.setBackground(javax.swing.UIManager.getDefaults().getColor("TextField.background"));
                }
            };

        final javax.swing.Timer timer = new javax.swing.Timer(250, timerAction);
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * DOCUMENT ME!
     */
    public void refresh() {
        if (txtKassenzeichen.getText().trim().length() > 0) {
            CidsAppBackend.getInstance().gotoKassenzeichen(txtKassenzeichen.getText().trim());
        } else {
            CidsAppBackend.getInstance().gotoKassenzeichen(txtSearch.getText().trim());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isEmpty() {
        return (txtKassenzeichen.getText().trim().equals(""));
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getShownKassenzeichen() {
        return txtKassenzeichen.getText();
    }

    /**
     * DOCUMENT ME!setKZSearchField.
     *
     * @return  DOCUMENT ME!
     */
    public String getShownBemerkung() {
        return jTextPane1.getText();
    }

    /**
     * DOCUMENT ME!
     */
    private void gotoTxtKassenzeichen() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("gotoTxtKassenzeichen");
        }
        CidsAppBackend.getInstance().gotoKassenzeichen(txtSearch.getText());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kassenzeichenObject  DOCUMENT ME!
     */
    public void setKassenzeichenObject(final CidsBean kassenzeichenObject) {
        this.kassenzeichenBean = kassenzeichenObject;
    }

    @Override
    public void editModeChanged() {
        setEnabled(CidsAppBackend.getInstance().isEditable());
    }

    @Override
    public Validator getValidator() {
        return aggVal;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   kassenzeichenBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static Validator getValidatorKassenzeichenNummer(final CidsBean kassenzeichenBean) {
        return new CidsBeanValidator(kassenzeichenBean, VerdisConstants.PROP.KASSENZEICHEN.KASSENZEICHENNUMMER) {

                @Override
                public ValidatorState performValidation() {
                    final CidsBean cidsBean = getCidsBean();
                    if (cidsBean == null) {
                        return null;
                    }
                    final Integer kassenzeichennummer = (Integer)cidsBean.getProperty(
                            VerdisConstants.PROP.KASSENZEICHEN.KASSENZEICHENNUMMER);

                    if (kassenzeichennummer == null) {
                        return new ValidatorStateImpl(ValidatorState.Type.ERROR, "Kassenzeichen leer.");
                    } else {
                        try {
                            if ((kassenzeichennummer >= 10000000) && (kassenzeichennummer < 100000000)) {
                                return new ValidatorStateImpl(ValidatorState.Type.VALID);
                            } else {
                                return new ValidatorStateImpl(
                                        ValidatorState.Type.ERROR,
                                        "Kassenzeichen nicht im g\u00FCltigen Bereich, muss 8-stellig sein.");
                            }
                        } catch (final Exception ex) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("KassenzeichenNummer not valid", ex);
                            }
                            return new ValidatorStateImpl(
                                    ValidatorState.Type.ERROR,
                                    "Kassenzeichen muss eine g\u00FCltige Zahl sein.");
                        }
                    }
                }
            };
    }

    /**
     * DOCUMENT ME!
     *
     * @param   kassenzeichenBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static Validator getValidatorLuftbildmerker(final CidsBean kassenzeichenBean) {
        return new CidsBeanValidator(kassenzeichenBean, VerdisConstants.PROP.KASSENZEICHEN.LUFTBILDMERKER) {

                @Override
                public ValidatorState performValidation() {
                    final CidsBean cidsBean = getCidsBean();
                    if (cidsBean == null) {
                        return null;
                    }
                    final Integer luftbildmerker = (Integer)cidsBean.getProperty(
                            VerdisConstants.PROP.KASSENZEICHEN.LUFTBILDMERKER);
                    if (luftbildmerker != null) {
                        final Timestamp luftbildmerkerAm = (Timestamp)cidsBean.getProperty(
                                VerdisConstants.PROP.KASSENZEICHEN.LUFTBILDMERKER_AM);
                        final Calendar cal = Calendar.getInstance();
                        cal.setTime(luftbildmerkerAm);
                        final int year = cal.get(Calendar.YEAR);
                        if (Math.abs(year - luftbildmerker) > 4) {
                            return new ValidatorStateImpl(
                                    ValidatorState.Type.ERROR,
                                    "Luftbildmerker muss zwischen jetzt +- 4 Jahre liegen.");
                        }
                    }
                    return new ValidatorStateImpl(ValidatorState.Type.VALID);
                }
            };
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static class IntegerToLongConverter extends Converter<Integer, Long> {

        //~ Methods ------------------------------------------------------------

        @Override
        public Long convertForward(final Integer i) {
            if (i == null) {
                return null;
            }
            return i.longValue();
        }

        @Override
        public Integer convertReverse(final Long l) {
            if (l == null) {
                return null;
            }
            return l.intValue();
        }
    }
}
