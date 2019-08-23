/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * KanaldatenPanel.java
 *
 * Created on 10. April 2006, 09:21 .
 */
package de.cismet.verdis.gui;

import de.cismet.cids.custom.util.BindingValidationSupport;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;

import de.cismet.cids.editors.DefaultBindableReferenceCombo;

import de.cismet.verdis.CidsAppBackend;
import de.cismet.verdis.EditModeListener;

import de.cismet.verdis.commons.constants.VerdisConstants;

import de.cismet.verdis.gui.befreiungerlaubnis.BefreiungerlaubnisTablePanel;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten.hell@cismet.de
 * @version  $Revision$, $Date$
 */
public class KanaldatenPanel extends javax.swing.JPanel implements CidsBeanStore, EditModeListener {

    //~ Instance fields --------------------------------------------------------

    private boolean editmode = false;
    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private CidsBean kassenzeichenBean;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.cismet.verdis.gui.befreiungerlaubnis.BefreiungerlaubnisTablePanel befreiungerlaubnisTablePanel1;
    private javax.swing.JComboBox cboMKRangeschlossen;
    private javax.swing.JComboBox cboMKSangeschlossen;
    private javax.swing.JComboBox cboRKangeschlossen;
    private javax.swing.JComboBox cboSKangeschlossen;
    private javax.swing.JCheckBox chkErlaubnisfreieVersickerung;
    private javax.swing.JCheckBox chkKKAentleerung;
    private javax.swing.JCheckBox chkKKAvorhanden;
    private javax.swing.JCheckBox chkMKRvorhanden;
    private javax.swing.JCheckBox chkMKSvorhanden;
    private javax.swing.JCheckBox chkRKvorhanden;
    private javax.swing.JCheckBox chkSGentleerung;
    private javax.swing.JCheckBox chkSGvorhanden;
    private javax.swing.JCheckBox chkSKvorhanden;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JLabel lblAngeschlossen;
    private javax.swing.JLabel lblEVG;
    private javax.swing.JLabel lblEntleerung;
    private javax.swing.JLabel lblKKA;
    private javax.swing.JLabel lblMKR;
    private javax.swing.JLabel lblMKS;
    private javax.swing.JLabel lblRK;
    private javax.swing.JLabel lblSG;
    private javax.swing.JLabel lblSK;
    private javax.swing.JLabel lblVorhanden1;
    private javax.swing.JLabel lblVorhanden2;
    private javax.swing.JPanel panMain;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form KanaldatenPanel.
     */
    public KanaldatenPanel() {
        // UIManager.put( "ComboBox.disabledForeground", Color.black );
        initComponents();
        clear();
        setEditable(false);
        try {
            ((DefaultBindableReferenceCombo)cboMKRangeschlossen).setMetaClass(CidsAppBackend.getInstance()
                        .getVerdisMetaClass(VerdisConstants.MC.ANSCHLUSSSTATUS));
            ((DefaultBindableReferenceCombo)cboMKSangeschlossen).setMetaClass(CidsAppBackend.getInstance()
                        .getVerdisMetaClass(VerdisConstants.MC.ANSCHLUSSSTATUS));
            ((DefaultBindableReferenceCombo)cboRKangeschlossen).setMetaClass(CidsAppBackend.getInstance()
                        .getVerdisMetaClass(VerdisConstants.MC.ANSCHLUSSSTATUS));
            ((DefaultBindableReferenceCombo)cboSKangeschlossen).setMetaClass(CidsAppBackend.getInstance()
                        .getVerdisMetaClass(VerdisConstants.MC.ANSCHLUSSSTATUS));
        } catch (Exception e) {
            log.error("Comboboxen sind ohne Funktion.", e);
        }

        BindingValidationSupport.attachBindingValidationToAllTargets(bindingGroup);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    public void clear() {
        chkRKvorhanden.setSelected(false);
        chkMKRvorhanden.setSelected(false);
        chkMKSvorhanden.setSelected(false);
        chkSKvorhanden.setSelected(false);
        chkSGvorhanden.setSelected(false);
        chkKKAvorhanden.setSelected(false);
        chkSGentleerung.setSelected(false);
        chkKKAentleerung.setSelected(false);
        chkErlaubnisfreieVersickerung.setSelected(false);
        cboRKangeschlossen.setSelectedIndex(-1);
        cboMKRangeschlossen.setSelectedIndex(-1);
        cboMKSangeschlossen.setSelectedIndex(-1);
        cboSKangeschlossen.setSelectedIndex(-1);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  editable  DOCUMENT ME!
     */
    public void setEditable(final boolean editable) {
        editmode = editable;
        chkRKvorhanden.setEnabled(editable);
        chkMKRvorhanden.setEnabled(editable);
        chkMKSvorhanden.setEnabled(editable);
        chkSKvorhanden.setEnabled(editable);
        chkSGvorhanden.setEnabled(editable);
        chkKKAvorhanden.setEnabled(editable);
        chkErlaubnisfreieVersickerung.setEnabled(editable);
        chkSGentleerung.setEnabled(editable && chkSGvorhanden.isSelected());
        chkKKAentleerung.setEnabled(editable && chkKKAvorhanden.isSelected());
        cboRKangeschlossen.setEnabled(editable && chkRKvorhanden.isSelected());
        cboMKRangeschlossen.setEnabled(editable && chkMKRvorhanden.isSelected());
        cboMKSangeschlossen.setEnabled(editable && chkMKSvorhanden.isSelected());
        cboSKangeschlossen.setEnabled(editable && chkSKvorhanden.isSelected());
        befreiungerlaubnisTablePanel1.setEditable(editable);
    }

    @Override
    public CidsBean getCidsBean() {
        return kassenzeichenBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        chkSGentleerung.setEnabled(false);
        chkKKAentleerung.setEnabled(false);
        cboRKangeschlossen.setEnabled(false);
        cboMKRangeschlossen.setEnabled(false);
        cboMKSangeschlossen.setEnabled(false);
        cboSKangeschlossen.setEnabled(false);
        cboRKangeschlossen.setSelectedIndex(-1);
        cboMKRangeschlossen.setSelectedIndex(-1);
        cboMKSangeschlossen.setSelectedIndex(-1);
        cboSKangeschlossen.setSelectedIndex(-1);

        kassenzeichenBean = cidsBean;
        bindingGroup.unbind();
        bindingGroup.bind();

        befreiungerlaubnisTablePanel1.getTable().setCidsBean(cidsBean);
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
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        panMain = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jSeparator4 = new javax.swing.JSeparator();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        lblRK = new javax.swing.JLabel();
        lblMKR = new javax.swing.JLabel();
        lblMKS = new javax.swing.JLabel();
        lblSK = new javax.swing.JLabel();
        lblVorhanden1 = new javax.swing.JLabel();
        chkRKvorhanden = new javax.swing.JCheckBox();
        chkMKRvorhanden = new javax.swing.JCheckBox();
        chkMKSvorhanden = new javax.swing.JCheckBox();
        chkSKvorhanden = new javax.swing.JCheckBox();
        lblAngeschlossen = new javax.swing.JLabel();
        cboRKangeschlossen = new DefaultBindableReferenceCombo();
        cboMKRangeschlossen = new DefaultBindableReferenceCombo();
        cboMKSangeschlossen = new DefaultBindableReferenceCombo();
        cboSKangeschlossen = new DefaultBindableReferenceCombo();
        lblEntleerung = new javax.swing.JLabel();
        lblKKA = new javax.swing.JLabel();
        lblVorhanden2 = new javax.swing.JLabel();
        chkSGentleerung = new javax.swing.JCheckBox();
        chkErlaubnisfreieVersickerung = new javax.swing.JCheckBox();
        chkKKAvorhanden = new javax.swing.JCheckBox();
        chkSGvorhanden = new javax.swing.JCheckBox();
        chkKKAentleerung = new javax.swing.JCheckBox();
        lblSG = new javax.swing.JLabel();
        lblEVG = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        befreiungerlaubnisTablePanel1 = new de.cismet.verdis.gui.befreiungerlaubnis.BefreiungerlaubnisTablePanel();

        setLayout(new java.awt.BorderLayout());

        panMain.setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 9, 0, 9);
        jPanel1.add(jSeparator4, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jPanel4.setLayout(new java.awt.GridBagLayout());

        lblRK.setText("RK");
        lblRK.setToolTipText("Regenwasserkanal");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel4.add(lblRK, gridBagConstraints);

        lblMKR.setText("MKR");
        lblMKR.setToolTipText("Mischwasserkanal Regen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel4.add(lblMKR, gridBagConstraints);

        lblMKS.setText("MKS");
        lblMKS.setToolTipText("Mischwasserkanal Schmutz");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel4.add(lblMKS, gridBagConstraints);

        lblSK.setText("SK");
        lblSK.setToolTipText("Schmutzwasserkanal");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel4.add(lblSK, gridBagConstraints);

        lblVorhanden1.setText("vorh.");
        lblVorhanden1.setToolTipText("vorhanden");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 5);
        jPanel4.add(lblVorhanden1, gridBagConstraints);

        chkRKvorhanden.setBorder(null);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kanalanschluss.rkvorhanden}"),
                chkRKvorhanden,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        chkRKvorhanden.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    chkRKvorhandenActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        jPanel4.add(chkRKvorhanden, gridBagConstraints);

        chkMKRvorhanden.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kanalanschluss.mkrvorhanden}"),
                chkMKRvorhanden,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        chkMKRvorhanden.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    chkMKRvorhandenActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        jPanel4.add(chkMKRvorhanden, gridBagConstraints);

        chkMKSvorhanden.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kanalanschluss.mksvorhanden}"),
                chkMKSvorhanden,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        chkMKSvorhanden.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    chkMKSvorhandenActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        jPanel4.add(chkMKSvorhanden, gridBagConstraints);

        chkSKvorhanden.setToolTipText("");
        chkSKvorhanden.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kanalanschluss.skvorhanden}"),
                chkSKvorhanden,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        chkSKvorhanden.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    chkSKvorhandenActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        jPanel4.add(chkSKvorhanden, gridBagConstraints);

        lblAngeschlossen.setText("angeschlossen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        jPanel4.add(lblAngeschlossen, gridBagConstraints);

        cboRKangeschlossen.setFocusable(false);
        cboRKangeschlossen.setMinimumSize(new java.awt.Dimension(55, 16));
        cboRKangeschlossen.setPreferredSize(new java.awt.Dimension(59, 16));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kanalanschluss.rkangeschlossen}"),
                cboRKangeschlossen,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 9, 0, 0);
        jPanel4.add(cboRKangeschlossen, gridBagConstraints);

        cboMKRangeschlossen.setFocusable(false);
        cboMKRangeschlossen.setMinimumSize(new java.awt.Dimension(55, 16));
        cboMKRangeschlossen.setPreferredSize(new java.awt.Dimension(59, 16));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kanalanschluss.mkrangeschlossen}"),
                cboMKRangeschlossen,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 9, 0, 0);
        jPanel4.add(cboMKRangeschlossen, gridBagConstraints);

        cboMKSangeschlossen.setFocusable(false);
        cboMKSangeschlossen.setMinimumSize(new java.awt.Dimension(55, 16));
        cboMKSangeschlossen.setPreferredSize(new java.awt.Dimension(59, 16));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kanalanschluss.mksangeschlossen}"),
                cboMKSangeschlossen,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 9, 0, 0);
        jPanel4.add(cboMKSangeschlossen, gridBagConstraints);

        cboSKangeschlossen.setFocusable(false);
        cboSKangeschlossen.setMinimumSize(new java.awt.Dimension(55, 16));
        cboSKangeschlossen.setPreferredSize(new java.awt.Dimension(59, 16));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kanalanschluss.skangeschlossen}"),
                cboSKangeschlossen,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 9, 0, 0);
        jPanel4.add(cboSKangeschlossen, gridBagConstraints);

        lblEntleerung.setText("Entleerung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        jPanel4.add(lblEntleerung, gridBagConstraints);

        lblKKA.setText("KKA");
        lblKKA.setToolTipText("Kleinkläranlage");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel4.add(lblKKA, gridBagConstraints);

        lblVorhanden2.setText("vorh.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 5);
        jPanel4.add(lblVorhanden2, gridBagConstraints);

        chkSGentleerung.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kanalanschluss.sgentleerung}"),
                chkSGentleerung,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        jPanel4.add(chkSGentleerung, gridBagConstraints);

        chkErlaubnisfreieVersickerung.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kanalanschluss.evg}"),
                chkErlaubnisfreieVersickerung,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        jPanel4.add(chkErlaubnisfreieVersickerung, gridBagConstraints);

        chkKKAvorhanden.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kanalanschluss.kkavorhanden}"),
                chkKKAvorhanden,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        chkKKAvorhanden.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    chkKKAvorhandenActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        jPanel4.add(chkKKAvorhanden, gridBagConstraints);

        chkSGvorhanden.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kanalanschluss.sgvorhanden}"),
                chkSGvorhanden,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        chkSGvorhanden.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    chkSGvorhandenActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        jPanel4.add(chkSGvorhanden, gridBagConstraints);

        chkKKAentleerung.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kanalanschluss.kkaentleerung}"),
                chkKKAentleerung,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        jPanel4.add(chkKKAentleerung, gridBagConstraints);

        lblSG.setText("SG");
        lblSG.setToolTipText("Sickergrube");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel4.add(lblSG, gridBagConstraints);

        lblEVG.setText("EVG");
        lblEVG.setToolTipText("Erlaubnisfreie Versickerung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel4.add(lblEVG, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel2.add(jPanel4, gridBagConstraints);

        jPanel5.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(jPanel5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 0, 0);
        jPanel1.add(jPanel2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(befreiungerlaubnisTablePanel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMain.add(jPanel1, gridBagConstraints);

        add(panMain, java.awt.BorderLayout.CENTER);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public BefreiungerlaubnisTablePanel getBefreiungerlaubnisTablePanel() {
        return befreiungerlaubnisTablePanel1;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void chkKKAvorhandenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_chkKKAvorhandenActionPerformed
        if (chkKKAvorhanden.isSelected() && editmode) {
            chkKKAentleerung.setEnabled(true);
        } else {
            chkKKAentleerung.setEnabled(false);
            chkKKAentleerung.setSelected(false);
        }
    }                                                                                   //GEN-LAST:event_chkKKAvorhandenActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void chkSGvorhandenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_chkSGvorhandenActionPerformed
        if (chkSGvorhanden.isSelected() && editmode) {
            chkSGentleerung.setEnabled(true);
        } else {
            chkSGentleerung.setEnabled(false);
            chkSGentleerung.setSelected(false);
        }
    }                                                                                  //GEN-LAST:event_chkSGvorhandenActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void chkSKvorhandenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_chkSKvorhandenActionPerformed
        if (chkSKvorhanden.isSelected() && editmode) {
            cboSKangeschlossen.setEnabled(true);
        } else {
            cboSKangeschlossen.setEnabled(false);
            cboSKangeschlossen.setSelectedIndex(-1);
        }
    }                                                                                  //GEN-LAST:event_chkSKvorhandenActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void chkMKSvorhandenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_chkMKSvorhandenActionPerformed
        if (chkMKSvorhanden.isSelected() && editmode) {
            cboMKSangeschlossen.setEnabled(true);
        } else {
            cboMKSangeschlossen.setEnabled(false);
            cboMKSangeschlossen.setSelectedIndex(-1);
        }
    }                                                                                   //GEN-LAST:event_chkMKSvorhandenActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void chkMKRvorhandenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_chkMKRvorhandenActionPerformed
        if (chkMKRvorhanden.isSelected() && editmode) {
            cboMKRangeschlossen.setEnabled(true);
        } else {
            cboMKRangeschlossen.setEnabled(false);
            cboMKRangeschlossen.setSelectedIndex(-1);
        }
    }                                                                                   //GEN-LAST:event_chkMKRvorhandenActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void chkRKvorhandenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_chkRKvorhandenActionPerformed
        if (chkRKvorhanden.isSelected() && editmode) {
            cboRKangeschlossen.setEnabled(true);
        } else {
            cboRKangeschlossen.setEnabled(false);
            cboRKangeschlossen.setSelectedIndex(-1);
        }
    }                                                                                  //GEN-LAST:event_chkRKvorhandenActionPerformed

    @Override
    public void editModeChanged() {
        setEditable(CidsAppBackend.getInstance().isEditable());
    }
}
