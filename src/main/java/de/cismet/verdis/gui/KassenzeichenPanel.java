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

import org.jdesktop.swingx.JXBusyLabel;

import java.awt.Color;
import java.awt.Dimension;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.util.BindingValidationSupport;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;

import de.cismet.cids.editors.converters.SqlDateToStringConverter;

import de.cismet.tools.gui.historybutton.DefaultHistoryModel;
import de.cismet.tools.gui.historybutton.HistoryModelListener;
import de.cismet.tools.gui.historybutton.JHistoryButton;

import de.cismet.validation.*;

import de.cismet.validation.display.EmbeddedValidatorDisplay;

import de.cismet.validation.validator.AggregatedValidator;
import de.cismet.validation.validator.CidsBeanValidator;

import de.cismet.verdis.AppModeListener;
import de.cismet.verdis.CidsAppBackend;
import de.cismet.verdis.EditModeListener;

import de.cismet.verdis.commons.constants.KassenzeichenPropertyConstants;
import de.cismet.verdis.commons.constants.RegenFlaechenPropertyConstants;

/**
 * DOCUMENT ME!
 *
 * @author   hell
 * @version  $Revision$, $Date$
 */
public class KassenzeichenPanel extends javax.swing.JPanel implements HistoryModelListener,
    CidsBeanStore,
    AppModeListener,
    EditModeListener,
    Validatable {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(KassenzeichenPanel.class);

    //~ Instance fields --------------------------------------------------------

    private boolean editmode = false;
    private Main mainApp;
    private DefaultHistoryModel historyModel = new DefaultHistoryModel();
    private JHistoryButton hbBack;
    private JHistoryButton hbFwd;
    private CidsBean kassenzeichenBean;
    private final Validator bindingValidator;
    private final AggregatedValidator aggVal = new AggregatedValidator();
    private Integer nextKassenzeichen = null;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup btgMode;
    private javax.swing.JButton btnSearch;
    private javax.swing.JCheckBox chkSperre;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblBemerkung;
    private javax.swing.JLabel lblErfassungsdatum;
    private javax.swing.JLabel lblKassenzeichen;
    private javax.swing.JLabel lblLastModification;
    private javax.swing.JLabel lblSperre;
    private javax.swing.JLabel lblSuche;
    private javax.swing.JPanel panKZValues;
    private javax.swing.JPanel panSearch;
    private javax.swing.JScrollPane scpBemerkung;
    private javax.swing.JSeparator sepTitle1;
    private javax.swing.JSeparator sepTitle2;
    private javax.swing.JToggleButton togInfoMode;
    private javax.swing.JToggleButton togRegenMode;
    private javax.swing.JToggleButton togWDSRMode;
    private javax.swing.JTextArea txtBemerkung;
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

        bindingValidator = BindingValidationSupport.attachBindingValidationToAllTargets(bindingGroup);

        configureButtons();
        // de.cismet.gui.tools.DullPane dp=new de.cismet.gui.tools.DullPane();
        // this.panKZValues.add(dp);
        setEnabled(false);
        historyModel.addHistoryModelListener(this);

        lblLastModification.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/goto.png")));
        lblLastModification.setOpaque(false);
        // lblLastModification.setBorderPainted(false);
        // lblLastModification.setFocusPainted(false);
        hbBack.revalidate();
        hbFwd.revalidate();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void attachBeanValidators() {
        ValidatorHelper.removeAllNoBindingValidatorFromDisplay(txtKassenzeichen);
        getValidatorKassenzeichenNummer(kassenzeichenBean).attachDisplay(EmbeddedValidatorDisplay.getEmbeddedDisplayFor(
                txtKassenzeichen));
    }

    @Override
    public CidsBean getCidsBean() {
        return kassenzeichenBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        if (cidsBean == null) {
            txtErfassungsdatum.setText("");
            txtKassenzeichen.setText("");
            txtBemerkung.setText("");
            txtSperreBemerkung.setText("");
            chkSperre.setSelected(false);
        }

        aggVal.clear();
        aggVal.add(bindingValidator);

        bindingGroup.unbind();
        kassenzeichenBean = cidsBean;
        bindingGroup.bind();

        attachBeanValidators();

        aggVal.add(getValidatorKassenzeichenNummer(kassenzeichenBean));
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
    public void setEnabled(final boolean b) {
        txtErfassungsdatum.setEditable(b);
        txtBemerkung.setEditable(b);
        chkSperre.setEnabled(b);
        txtSearch.setEnabled(!b);
        btnSearch.setEnabled(!b);
        if (b) {
            txtBemerkung.setBackground(java.awt.Color.white);
        } else {
            txtBemerkung.setBackground(this.getBackground());
        }
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
                historyModel);
        hbFwd = JHistoryButton.getDefaultJHistoryButton(
                JHistoryButton.DIRECTION_FORWARD,
                JHistoryButton.ICON_SIZE_16,
                historyModel);

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
        lblErfassungsdatum = new javax.swing.JLabel();
        lblBemerkung = new javax.swing.JLabel();
        lblSperre = new javax.swing.JLabel();
        txtErfassungsdatum = new javax.swing.JTextField();
        chkSperre = new javax.swing.JCheckBox();
        scpBemerkung = new javax.swing.JScrollPane();
        txtBemerkung = new javax.swing.JTextArea();
        txtKassenzeichen = new javax.swing.JTextField();
        txtSperreBemerkung = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        togRegenMode = new javax.swing.JToggleButton();
        togWDSRMode = new javax.swing.JToggleButton();
        togInfoMode = new javax.swing.JToggleButton();
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

        lblSuche.setText("Kassenzeichen");
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

        lblKassenzeichen.setText("Kassenzeichen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panKZValues.add(lblKassenzeichen, gridBagConstraints);

        lblErfassungsdatum.setText("Datum der Erfassung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panKZValues.add(lblErfassungsdatum, gridBagConstraints);

        lblBemerkung.setText("Bemerkung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        panKZValues.add(lblBemerkung, gridBagConstraints);

        lblSperre.setText("Veranlagung gesperrt");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panKZValues.add(lblSperre, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.datum_erfassung}"),
                txtErfassungsdatum,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                KassenzeichenPropertyConstants.PROP__DATUM_ERFASSUNG);
        binding.setConverter(new SqlDateToStringConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 6, 3, 0);
        panKZValues.add(txtErfassungsdatum, gridBagConstraints);

        chkSperre.setForeground(java.awt.Color.red);
        chkSperre.setEnabled(false);
        chkSperre.setFocusPainted(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.sperre}"),
                chkSperre,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        chkSperre.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    chkSperreActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 6, 3, 0);
        panKZValues.add(chkSperre, gridBagConstraints);

        scpBemerkung.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scpBemerkung.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scpBemerkung.setMinimumSize(new java.awt.Dimension(6, 36));

        txtBemerkung.setColumns(3);
        txtBemerkung.setLineWrap(true);
        txtBemerkung.setRows(3);
        txtBemerkung.setMinimumSize(new java.awt.Dimension(0, 36));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bemerkung}"),
                txtBemerkung,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                KassenzeichenPropertyConstants.PROP__BEMERKUNG);
        bindingGroup.addBinding(binding);

        scpBemerkung.setViewportView(txtBemerkung);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 6, 3, 0);
        panKZValues.add(scpBemerkung, gridBagConstraints);

        txtKassenzeichen.setEditable(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kassenzeichennummer8}"),
                txtKassenzeichen,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 3, 0);
        panKZValues.add(txtKassenzeichen, gridBagConstraints);

        txtSperreBemerkung.setBackground(getBackground());
        txtSperreBemerkung.setEditable(false);
        txtSperreBemerkung.setForeground(java.awt.Color.red);
        txtSperreBemerkung.setBorder(null);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bemerkung_sperre}"),
                txtSperreBemerkung,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                KassenzeichenPropertyConstants.PROP__BEMERKUNG_SPERRE);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bemerkung_sperre}"),
                txtSperreBemerkung,
                org.jdesktop.beansbinding.BeanProperty.create("toolTipText"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        panKZValues.add(txtSperreBemerkung, gridBagConstraints);

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

        jLabel2.setText("Modus");
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

        btgMode.add(togWDSRMode);
        togWDSRMode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/esw_gr.png"))); // NOI18N
        togWDSRMode.setToolTipText("ESW");
        togWDSRMode.setFocusable(false);
        togWDSRMode.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        togWDSRMode.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        togWDSRMode.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    togWDSRModeActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanel2.add(togWDSRMode, gridBagConstraints);

        btgMode.add(togInfoMode);
        togInfoMode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/info_gr.png"))); // NOI18N
        togInfoMode.setSelected(true);
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
    private void hbBackActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hbBackActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_hbBackActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void chkSperreActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSperreActionPerformed
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
    }//GEN-LAST:event_chkSperreActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void txtSearchActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        gotoTxtKassenzeichen();
    }//GEN-LAST:event_txtSearchActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void togInfoModeActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_togInfoModeActionPerformed
        CidsAppBackend.getInstance().setMode(CidsAppBackend.Mode.ALLGEMEIN);
        mainApp.refreshLeftTitleBarColor();
    }//GEN-LAST:event_togInfoModeActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void togRegenModeActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_togRegenModeActionPerformed
        CidsAppBackend.getInstance().setMode(CidsAppBackend.Mode.REGEN);
        mainApp.refreshLeftTitleBarColor();
    }//GEN-LAST:event_togRegenModeActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void togWDSRModeActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_togWDSRModeActionPerformed
        CidsAppBackend.getInstance().setMode(CidsAppBackend.Mode.ESW);
        mainApp.refreshLeftTitleBarColor();
    }//GEN-LAST:event_togWDSRModeActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnSearchActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        gotoTxtKassenzeichen();
    }//GEN-LAST:event_btnSearchActionPerformed

    @Override
    public void appModeChanged() {
        final CidsAppBackend.Mode mode = CidsAppBackend.getInstance().getMode();
        if (mode.equals(mode.ALLGEMEIN)) {
            if (!togInfoMode.isSelected()) {
                togInfoMode.setSelected(true);
            }
        } else if (mode.equals(mode.ESW)) {
            if (!togWDSRMode.isSelected()) {
                togWDSRMode.setSelected(true);
            }
        } else {
            if (!togRegenMode.isSelected()) {
                togRegenMode.setSelected(true);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kz  DOCUMENT ME!
     */
    public void setKZSearchField(final String kz) {
        this.txtSearch.setText(kz);
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
            gotoKassenzeichen(txtKassenzeichen.getText().trim());
        } else {
            gotoKassenzeichen(txtSearch.getText().trim());
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
     * DOCUMENT ME!
     */
    private void gotoTxtKassenzeichen() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("gotoTxtKassenzeichen");
        }
        this.gotoKassenzeichen(txtSearch.getText());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kassenzeichen  DOCUMENT ME!
     */
    public void gotoKassenzeichen(final String kassenzeichen) {
        // Mit History
        gotoKassenzeichen(kassenzeichen, true);
    }

    /**
     * former synchronized method.
     *
     * @param  kz              DOCUMENT ME!
     * @param  historyEnabled  DOCUMENT ME!
     */
    public void gotoKassenzeichen(final String kz, final boolean historyEnabled) {
        boolean refreshFlag = false;
        final String[] test = kz.split(":");

        final String kassenzeichenNummer;
        final String flaechenBez;
        if (test.length > 1) {
            kassenzeichenNummer = test[0];
            flaechenBez = test[1];
        } else {
            kassenzeichenNummer = kz;
            flaechenBez = "";
        }

        if (kassenzeichenNummer.trim().equals(txtKassenzeichen.getText().trim())) {
            refreshFlag = true;
        }

        if ((mainApp.changesPending() == false) || (refreshFlag == true)) {
            mainApp.disableKassenzeichenCmds();
            txtSearch.setEnabled(false);
            btnSearch.setEnabled(false);
            setKZSearchField(kz);

            new SwingWorker<CidsBean, Void>() {

                    @Override
                    protected CidsBean doInBackground() throws Exception {
                        return CidsAppBackend.getInstance()
                                    .loadKassenzeichenByNummer(Integer.parseInt(kassenzeichenNummer));
                    }

                    @Override
                    protected void done() {
                        try {
                            final CidsBean cidsBean = get();

                            if (cidsBean != null) {
                                CidsAppBackend.getInstance().setCidsBean(cidsBean);
                                selectFlaecheByBez(flaechenBez);
                                flashSearchField(Color.GREEN);
                                if (historyEnabled) {
                                    historyModel.addToHistory(kz);
                                }
                            } else {
                                flashSearchField(Color.RED);
                            }
                        } catch (Exception e) {
                            LOG.error("Exception in Background Thread", e);
                            flashSearchField(Color.RED);
                            mainApp.enableEditing(false);
                        }
                        txtSearch.setEnabled(true);
                        btnSearch.setEnabled(true);
                        mainApp.refreshLeftTitleBarColor();
                        mainApp.refreshKassenzeichenButtons();
                        mainApp.refreshClipboardButtons();
                        mainApp.refreshItemButtons();
                    }
                }.execute();
        } else {
            JOptionPane.showMessageDialog(
                mainApp,
                "Das Kassenzeichen kann nur gewechselt werden wenn alle \u00C4nderungen gespeichert oder verworfen worden sind.",
                "Wechseln nicht m\u00F6glich",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  bez  DOCUMENT ME!
     */
    private void selectFlaecheByBez(final String bez) {
        for (final CidsBean flaeche
                    : (Collection<CidsBean>)kassenzeichenBean.getProperty(KassenzeichenPropertyConstants.PROP__FLAECHEN)) {
            if (((String)flaeche.getProperty(RegenFlaechenPropertyConstants.PROP__FLAECHENBEZEICHNUNG)).equals(bez)) {
                mainApp.getRegenFlaechenTabellenPanel().selectCidsBean(flaeche);
                return;
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  b  DOCUMENT ME!
     */
    public void enableEditing(final boolean b) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("enableEditing(" + b + ")");
        }
        this.setEnabled(b);
        editmode = b;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object_id  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean lockDataset(final String object_id) {
        return CidsAppBackend.getInstance().lockDataset(object_id);

//        try {
//            Statement stmnt = connection.createStatement();
//            ResultSet rs = stmnt.executeQuery("select class_id,object_id,user_string,additional_info from cs_locks where class_id=" + Main.KASSENZEICHEN_CLASS_ID + " and object_id=" + object_id);
//            if (!rs.next()) {
//                rs.close();
//                //Kein Eintragvorhanden. Eintrag schreiben
//                stmnt = connection.createStatement();
//                String locker = "insert into cs_locks (class_id,object_id,user_string,additional_info) values (" + Main.KASSENZEICHEN_CLASS_ID + "," + object_id + ",'" + userString + "','" + lockNonce + "')";
//                log.debug("lockDataset: " + locker);
//                stmnt.executeUpdate(locker);
//                stmnt.close();
//                //Sperreintrag geschrieben. Jetzt wird noch \u00FCberpr\u00FCft ob in der zwischenzeit noch jemnad einen Sperreintrag geschrieben hat
//                stmnt = connection.createStatement();
//                rs = stmnt.executeQuery("select count(*) from cs_locks where class_id=" + Main.KASSENZEICHEN_CLASS_ID + " and object_id=" + object_id);
//                if (!rs.next()) {
//                    log.fatal("select count(*) hat nichts zur\u00FCckgeliefert.");
//                    return false;
//                } else {
//                    int count = rs.getInt(1);
//                    if (count > 1) {
//                        final JFrame t = mainApp;
//                        new Thread() {
//
//                            {
//                                start();
//                            }
//
//                            public void run() {
//                                JOptionPane.showMessageDialog(t, "Es wurde gleichzeitig versucht einen Datensatz zu sperren. Der kl\u00FCgere gibt nach ;-)", "Sperren fehlgeschlagen", JOptionPane.WARNING_MESSAGE);
//                            }
//                        };
//                        stmnt = connection.createStatement();
//                        int ret = stmnt.executeUpdate("delete from cs_locks where class_id=" + Main.KASSENZEICHEN_CLASS_ID + " and object_id=" + object_id + " and additional_info='" + lockNonce + "'");
//                        stmnt.close();
//                        if (ret != 1) {
//                            log.warn("Kassenzeichen " + object_id + " konnte nicht entsperrt werden. R\u00FCckgabewert des DeleteStmnts:" + ret);
//                        }
//
//                        return false;
//                    } else {
//                        return true;
//                    }
//                }
//            } else {
//                final JFrame t = mainApp;
//                final String user = rs.getString(3);
//                rs.close();
//                new Thread() {
//
//                    {
//                        start();
//                    }
//
//                    public void run() {
//                        JOptionPane.showMessageDialog(t, "Der Datensatz wird schon vom Benutzer " + user + " zum Ver\u00E4ndern gesperrt", "Kein Editieren m\u00F6glich", JOptionPane.INFORMATION_MESSAGE);
//                    }
//                };
//                return false;
//            }
//
//        } catch (Exception e) {
//            log.error("SQL Fehler beim Sperren", e);
//            return false;
//        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  object_id  DOCUMENT ME!
     */
    public void unlockDataset(final String object_id) {
        CidsAppBackend.getInstance().unlockDataset(object_id);
//        String sql = "delete from cs_locks where class_id=" + Main.KASSENZEICHEN_CLASS_ID + " and object_id=" + object_id + " and additional_info='" + lockNonce + "'";
//        log.info("unlockDataset: " + sql);
//        try {
//            Statement stmnt = connection.createStatement();
//            int ret = stmnt.executeUpdate(sql);
//            stmnt.close();
//            if (ret != 1) {
//                log.fatal("Kassenzeichen " + object_id + " konnte nicht entsperrt werden. R\u00FCckgabewert des DeleteStmnts:" + ret + "(" + sql + ")");
//            }
//
//        } catch (Exception e) {
//            log.fatal("SQL Fehler beim Entsperren (Statement=" + sql + ")", e);
//        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean lockDataset() {
        final String object_id = txtKassenzeichen.getText().trim();
        return lockDataset(object_id);
    }

    /**
     * DOCUMENT ME!
     */
    public void unlockDataset() {
        final String object_id = txtKassenzeichen.getText().trim();
        if ((object_id != null) && (object_id.length() > 0)) {
            unlockDataset(object_id);
        }
    }

    @Override
    public void historyChanged() {
        if ((historyModel != null) && (historyModel.getCurrentElement() != null)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("historyChanged:" + historyModel.getCurrentElement().toString());
            }
            if ((historyModel.getCurrentElement() != null)
                        && (!(historyModel.getCurrentElement().equals(txtSearch.getText())))) {
                txtSearch.setText(historyModel.getCurrentElement().toString());
                gotoKassenzeichen(txtSearch.getText(), false);
            }
        }
    }

    @Override
    public void forwardStatusChanged() {
    }

    @Override
    public void backStatusChanged() {
    }

    @Override
    public void historyActionPerformed() {
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isEditmode() {
        return editmode;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  editmode  DOCUMENT ME!
     */
    public void setEditmode(final boolean editmode) {
        this.editmode = editmode;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getKassenzeichenObject() {
        return kassenzeichenBean;
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
        return new CidsBeanValidator(kassenzeichenBean, KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER) {

                @Override
                public ValidatorState performValidation() {
                    final CidsBean cidsBean = getCidsBean();
                    if (cidsBean == null) {
                        return null;
                    }
                    final Integer kassenzeichennummer = (Integer)cidsBean.getProperty(
                            KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER);

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
                        } catch (Exception ex) {
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
}
