/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis.gui;

import Sirius.navigator.connection.SessionManager;

import Sirius.util.collections.MultiMap;

import org.apache.log4j.Logger;

import java.awt.Color;
import java.awt.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.util.VerdisUtils;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;

import de.cismet.tools.gui.StaticSwingTools;

import de.cismet.verdis.AbstractClipboard;
import de.cismet.verdis.CidsAppBackend;
import de.cismet.verdis.ClipboardListener;
import de.cismet.verdis.EditModeListener;

import de.cismet.verdis.commons.constants.ArbeitspaketEintragPropertyConstants;
import de.cismet.verdis.commons.constants.ArbeitspaketPropertyConstants;
import de.cismet.verdis.commons.constants.FlaechePropertyConstants;
import de.cismet.verdis.commons.constants.FrontPropertyConstants;
import de.cismet.verdis.commons.constants.KassenzeichenPropertyConstants;
import de.cismet.verdis.commons.constants.VerdisMetaClassConstants;

import static de.cismet.verdis.CidsAppBackend.Mode.ALLGEMEIN;
import static de.cismet.verdis.CidsAppBackend.Mode.ESW;
import static de.cismet.verdis.CidsAppBackend.Mode.REGEN;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class KassenzeichenListPanel extends javax.swing.JPanel implements CidsBeanStore,
    EditModeListener,
    ClipboardListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(KassenzeichenListPanel.class);
    private static KassenzeichenListPanel INSTANCE;

    //~ Instance fields --------------------------------------------------------

    private CidsBean cidsBean;
    private volatile MultiMap lockMap = new MultiMap();
    private final HashMap<CidsBean, Color> bgColorMap = new HashMap<CidsBean, Color>();
    private DefaultListModel nullListModel = new DefaultListModel();
    private boolean buttonsEnabled = true;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdPaste;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton8;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JDialog jDialog3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JList jList1;
    private javax.swing.JList jList2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JProgressBar jProgressBar2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JList lstKassenzeichen;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form KassenzeichenGeomSearchPanel.
     */
    private KassenzeichenListPanel() {
        initComponents();

        try {
            lstKassenzeichen.setCellRenderer(new DefaultListCellRenderer() {

                    @Override
                    public Component getListCellRendererComponent(final JList list,
                            final Object value,
                            final int index,
                            final boolean isSelected,
                            final boolean cellHasFocus) {
                        final JLabel renderer = (JLabel)super.getListCellRendererComponent(
                                list,
                                value,
                                index,
                                isSelected,
                                cellHasFocus);

                        if (value instanceof CidsBean) {
                            final CidsBean cidsBean = (CidsBean)value;
                            renderer.setText(
                                Integer.toString(
                                    (Integer)cidsBean.getProperty(
                                        ArbeitspaketEintragPropertyConstants.PROP__KASSENZEICHENNUMMER)));
                            final Boolean istAbgearbeitet = (Boolean)cidsBean.getProperty(
                                    ArbeitspaketEintragPropertyConstants.PROP__IST_ABGEARBEITET);
                            if ((istAbgearbeitet != null) && istAbgearbeitet) {
                                renderer.setEnabled(false);
                            }
                        }
                        return renderer;
                    }
                });

            jList1.setCellRenderer(new DefaultListCellRenderer() {

                    @Override
                    public Component getListCellRendererComponent(final JList<?> list,
                            final Object value,
                            final int index,
                            final boolean isSelected,
                            final boolean cellHasFocus) {
                        final Component comp = super.getListCellRendererComponent(
                                list,
                                value,
                                index,
                                false,
                                false);
                        if (comp instanceof JLabel) {
                            ((JLabel)comp).setText(
                                Main.getInstance().getCurrentClipboard().getFromKassenzeichenBean()
                                        + ":"
                                        + ((JLabel)comp).getText());
                        }
                        return comp;
                    }
                });
            jList2.setCellRenderer(new DefaultListCellRenderer() {

                    @Override
                    public Component getListCellRendererComponent(final JList<?> list,
                            final Object value,
                            final int index,
                            final boolean isSelected,
                            final boolean cellHasFocus) {
                        final Component comp = super.getListCellRendererComponent(
                                list,
                                value,
                                index,
                                false,
                                false);
                        final Color bgColor = bgColorMap.get((CidsBean)value);
                        if (bgColor != null) {
                            setBackground(bgColor);
                        }
                        return comp;
                    }
                });

            jComboBox1.setRenderer(new DefaultListCellRenderer() {

                    @Override
                    public Component getListCellRendererComponent(final JList list,
                            final Object value,
                            final int index,
                            final boolean isSelected,
                            final boolean cellHasFocus) {
                        final JLabel label = (JLabel)super.getListCellRendererComponent(
                                list,
                                value,
                                index,
                                isSelected,
                                cellHasFocus);
                        if (value == null) {
                            label.setText("temporäre Kassenzeichenliste");
                        }
                        return label;
                    }
                });
            searchFinished(null);
            reloadArbeitsPakete();
        } catch (final Exception ex) {
            LOG.warn(ex, ex);
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static KassenzeichenListPanel getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new KassenzeichenListPanel();
        }
        return INSTANCE;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<Integer> getKassenzeichen() {
        final Integer[] temp = new Integer[lstKassenzeichen.getModel().getSize()];
        for (int i = 0; i < lstKassenzeichen.getModel().getSize(); i++) {
            temp[i] = (Integer)lstKassenzeichen.getModel().getElementAt(i);
        }
        return Arrays.asList(temp);
    }

    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled); // To change body of generated methods, choose Tools | Templates.
        jButton1.setEnabled(enabled);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jDialog1 = new PasteDialog();
        jPanel3 = new javax.swing.JPanel();
        jCheckBox2 = new javax.swing.JCheckBox();
        jPanel4 = new javax.swing.JPanel();
        jProgressBar2 = new javax.swing.JProgressBar();
        jPanel5 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList();
        jLabel2 = new javax.swing.JLabel();
        jDialog3 = new javax.swing.JDialog();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstKassenzeichen = new javax.swing.JList();
        jProgressBar1 = new javax.swing.JProgressBar();
        jPanel1 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        cmdPaste = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();
        jToggleButton1 = new javax.swing.JToggleButton();
        jPanel9 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox();
        jButton8 = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();

        jDialog1.setTitle(org.openide.util.NbBundle.getMessage(
                KassenzeichenListPanel.class,
                "KassenzeichenListPanel.jDialog1.title")); // NOI18N
        jDialog1.setMinimumSize(new java.awt.Dimension(400, 300));
        jDialog1.setModal(true);
        jDialog1.setResizable(false);
        jDialog1.getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel3.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jCheckBox2,
            org.openide.util.NbBundle.getMessage(
                KassenzeichenListPanel.class,
                "KassenzeichenListPanel.jCheckBox2.text")); // NOI18N
        jCheckBox2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jCheckBox2ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(jCheckBox2, gridBagConstraints);

        jPanel4.setLayout(new java.awt.GridBagLayout());

        jProgressBar2.setString(org.openide.util.NbBundle.getMessage(
                KassenzeichenListPanel.class,
                "KassenzeichenListPanel.jProgressBar2.string")); // NOI18N
        jProgressBar2.setStringPainted(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 0, 5);
        jPanel4.add(jProgressBar2, gridBagConstraints);

        jPanel5.setLayout(new java.awt.GridLayout(1, 0, 5, 0));

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton5,
            org.openide.util.NbBundle.getMessage(KassenzeichenListPanel.class, "KassenzeichenListPanel.jButton5.text")); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton5ActionPerformed(evt);
                }
            });
        jPanel5.add(jButton5);

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton4,
            org.openide.util.NbBundle.getMessage(KassenzeichenListPanel.class, "KassenzeichenListPanel.jButton4.text")); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton4ActionPerformed(evt);
                }
            });
        jPanel5.add(jButton4);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel4.add(jPanel5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(jPanel4, gridBagConstraints);

        jPanel6.setLayout(new java.awt.GridLayout(1, 0));

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jList1.setModel(new DefaultListModel());
        jScrollPane1.setViewportView(jList1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel2.add(jScrollPane1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(KassenzeichenListPanel.class, "KassenzeichenListPanel.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel2.add(jLabel1, gridBagConstraints);

        jPanel6.add(jPanel2);

        jPanel7.setLayout(new java.awt.GridBagLayout());

        jList2.setModel(new DefaultListModel());
        jScrollPane3.setViewportView(jList2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel7.add(jScrollPane3, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel2,
            org.openide.util.NbBundle.getMessage(KassenzeichenListPanel.class, "KassenzeichenListPanel.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel7.add(jLabel2, gridBagConstraints);

        jPanel6.add(jPanel7);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel3.add(jPanel6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jDialog1.getContentPane().add(jPanel3, gridBagConstraints);

        setLayout(new java.awt.GridBagLayout());

        lstKassenzeichen.setModel(nullListModel);
        lstKassenzeichen.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lstKassenzeichenMouseClicked(evt);
                }
            });
        lstKassenzeichen.addListSelectionListener(new javax.swing.event.ListSelectionListener() {

                @Override
                public void valueChanged(final javax.swing.event.ListSelectionEvent evt) {
                    lstKassenzeichenValueChanged(evt);
                }
            });
        jScrollPane2.setViewportView(lstKassenzeichen);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane2, gridBagConstraints);

        jProgressBar1.setBorderPainted(false);
        jProgressBar1.setString(org.openide.util.NbBundle.getMessage(
                KassenzeichenListPanel.class,
                "KassenzeichenListPanel.jProgressBar1.string")); // NOI18N
        jProgressBar1.setStringPainted(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(jProgressBar1, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jButton2.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/add.png")));                              // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            jButton2,
            org.openide.util.NbBundle.getMessage(KassenzeichenListPanel.class, "KassenzeichenListPanel.jButton2.text")); // NOI18N
        jButton2.setToolTipText(org.openide.util.NbBundle.getMessage(
                KassenzeichenListPanel.class,
                "KassenzeichenListPanel.jButton2.toolTipText"));                                                         // NOI18N
        jButton2.setBorderPainted(false);
        jButton2.setContentAreaFilled(false);
        jButton2.setDisabledIcon(null);
        jButton2.setDisabledSelectedIcon(null);
        jButton2.setFocusPainted(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton2ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel1.add(jButton2, gridBagConstraints);

        jButton3.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/remove.png")));                           // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            jButton3,
            org.openide.util.NbBundle.getMessage(KassenzeichenListPanel.class, "KassenzeichenListPanel.jButton3.text")); // NOI18N
        jButton3.setToolTipText(org.openide.util.NbBundle.getMessage(
                KassenzeichenListPanel.class,
                "KassenzeichenListPanel.jButton3.toolTipText"));                                                         // NOI18N
        jButton3.setBorderPainted(false);
        jButton3.setContentAreaFilled(false);
        jButton3.setDisabledIcon(null);
        jButton3.setDisabledSelectedIcon(null);
        jButton3.setFocusPainted(false);
        jButton3.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton3ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel1.add(jButton3, gridBagConstraints);

        cmdPaste.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/pasteFl.png"))); // NOI18N
        cmdPaste.setToolTipText(org.openide.util.NbBundle.getMessage(
                KassenzeichenListPanel.class,
                "KassenzeichenListPanel.cmdPaste.toolTipText"));                              // NOI18N
        cmdPaste.setBorderPainted(false);
        cmdPaste.setContentAreaFilled(false);
        cmdPaste.setFocusPainted(false);
        cmdPaste.setMaximumSize(new java.awt.Dimension(28, 28));
        cmdPaste.setMinimumSize(new java.awt.Dimension(28, 28));
        cmdPaste.setPreferredSize(new java.awt.Dimension(28, 28));
        cmdPaste.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdPasteActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        jPanel1.add(cmdPaste, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jCheckBox1,
            org.openide.util.NbBundle.getMessage(
                KassenzeichenListPanel.class,
                "KassenzeichenListPanel.jCheckBox1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(jCheckBox1, gridBagConstraints);

        jToggleButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/workdone.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            jToggleButton1,
            org.openide.util.NbBundle.getMessage(
                KassenzeichenListPanel.class,
                "KassenzeichenListPanel.jToggleButton1.text"));                                                          // NOI18N
        jToggleButton1.setToolTipText(org.openide.util.NbBundle.getMessage(
                KassenzeichenListPanel.class,
                "KassenzeichenListPanel.jToggleButton1.toolTipText"));                                                   // NOI18N
        jToggleButton1.setBorderPainted(false);
        jToggleButton1.setContentAreaFilled(false);
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jToggleButton1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel1.add(jToggleButton1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        add(jPanel1, gridBagConstraints);

        jPanel9.setLayout(new java.awt.GridBagLayout());

        jComboBox1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jComboBox1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel9.add(jComboBox1, gridBagConstraints);

        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/postion.png")));        // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            jButton8,
            org.openide.util.NbBundle.getMessage(KassenzeichenListPanel.class, "KassenzeichenListPanel.jButton8.text")); // NOI18N
        jButton8.setToolTipText(org.openide.util.NbBundle.getMessage(
                KassenzeichenListPanel.class,
                "KassenzeichenListPanel.jButton8.toolTipText"));                                                         // NOI18N
        jButton8.setBorderPainted(false);
        jButton8.setContentAreaFilled(false);
        jButton8.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton8ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        jPanel9.add(jButton8, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(jPanel9, gridBagConstraints);

        jPanel10.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton1,
            org.openide.util.NbBundle.getMessage(KassenzeichenListPanel.class, "KassenzeichenListPanel.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel10.add(jButton1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(jPanel10, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstKassenzeichenMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lstKassenzeichenMouseClicked
        if (jButton1.isEnabled() && (evt.getClickCount() == 2) && !CidsAppBackend.getInstance().isEditable()) {
            if (lstKassenzeichen.getSelectedValue() != null) {
                gotoSelectedKassenzeichen();
            }
        }
    }                                                                                //GEN-LAST:event_lstKassenzeichenMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstKassenzeichenValueChanged(final javax.swing.event.ListSelectionEvent evt) { //GEN-FIRST:event_lstKassenzeichenValueChanged
        final int count = lstKassenzeichen.getSelectedIndices().length;
        jProgressBar1.setIndeterminate(false);
        jProgressBar1.setValue(0);
        jProgressBar1.setString(count + " Kassenzeichen ausgewählt");
        updateButtons();
    }                                                                                           //GEN-LAST:event_lstKassenzeichenValueChanged

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton1ActionPerformed
        gotoSelectedKassenzeichen();
    }                                                                            //GEN-LAST:event_jButton1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton3ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton3ActionPerformed
        for (final Object entry : lstKassenzeichen.getSelectedValuesList()) {
            ((DefaultListModel)lstKassenzeichen.getModel()).removeElement(entry);
        }
    }                                                                            //GEN-LAST:event_jButton3ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton2ActionPerformed
        StaticSwingTools.showDialog(new KassenzeichenAddDialog(new KassenzeichenAddDialogListener() {

                    @Override
                    public void kassenzeichennummerAdded(final Integer kassenzeichennummer) {
                        addOneKassenzeichenToList(kassenzeichennummer, false);
                    }
                }));
    } //GEN-LAST:event_jButton2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdPasteActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdPasteActionPerformed
        StaticSwingTools.showDialog(jDialog1);
    }                                                                            //GEN-LAST:event_cmdPasteActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  clipboardBeans      DOCUMENT ME!
     * @param  kassenzeichenBeans  DOCUMENT ME!
     */
    private void executeCopy(final Collection<CidsBean> clipboardBeans, final Collection<CidsBean> kassenzeichenBeans) {
        int progress = 0;
        for (final CidsBean kassenzeichen : kassenzeichenBeans) {
            try {
                SwingUtilities.invokeLater(new Thread() {

                        @Override
                        public void run() {
                            jProgressBar2.setString("kopiere nach " + kassenzeichen);
                        }
                    });

                for (final CidsBean beanToPaste : clipboardBeans) {
                    CidsBean newBean = null;
                    Collection<CidsBean> targetCollection = null;
                    switch (CidsAppBackend.getInstance().getMode()) {
                        case REGEN: {
                            targetCollection = kassenzeichen.getBeanCollectionProperty(
                                    KassenzeichenPropertyConstants.PROP__FLAECHEN);
                            newBean = VerdisUtils.createPastedFlaecheBean(
                                    beanToPaste,
                                    targetCollection,
                                    jCheckBox2.isSelected());
                        }
                        break;
                        case ESW: {
                            targetCollection = kassenzeichen.getBeanCollectionProperty(
                                    KassenzeichenPropertyConstants.PROP__FRONTEN);
                            newBean = VerdisUtils.createPastedFrontBean(
                                    beanToPaste,
                                    targetCollection,
                                    jCheckBox2.isSelected());
                        }
                        break;
                        case ALLGEMEIN: {
                            targetCollection = kassenzeichen.getBeanCollectionProperty(
                                    KassenzeichenPropertyConstants.PROP__KASSENZEICHEN_GEOMETRIEN);
                            newBean = VerdisUtils.createPastedInfoBean(beanToPaste);
                        }
                        break;
                    }
                    if ((targetCollection != null) && (newBean != null)) {
                        targetCollection.add(newBean);
                    }
                }
                kassenzeichen.persist();
                final int temp = progress++;
                SwingUtilities.invokeLater(new Thread() {

                        @Override
                        public void run() {
                            jProgressBar2.setValue(temp);
                        }
                    });
            } catch (final Exception ex) {
                LOG.fatal(ex, ex);
            }
        }
        final AbstractClipboard clipboard = Main.getInstance().getCurrentClipboard();
        if (clipboard != null) {
            clipboard.clear();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton4ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton4ActionPerformed
        jDialog1.setVisible(false);
    }                                                                            //GEN-LAST:event_jButton4ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton5ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton5ActionPerformed
        final Collection<CidsBean> clipboardBeans = new ArrayList<CidsBean>();
        for (int i = 0; i < jList1.getModel().getSize(); i++) {
            clipboardBeans.add((CidsBean)jList1.getModel().getElementAt(i));
        }
        final Collection<CidsBean> kassenzeichenBeans = new ArrayList<CidsBean>();
        for (int i = 0; i < jList2.getModel().getSize(); i++) {
            kassenzeichenBeans.add((CidsBean)jList2.getModel().getElementAt(i));
        }
        final int totalCopy = kassenzeichenBeans.size();
        jProgressBar2.setMaximum(totalCopy);
        new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    executeCopy(clipboardBeans, kassenzeichenBeans);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                    } catch (final Exception ex) {
                        LOG.fatal(ex, ex);
                    } finally {
                        jProgressBar2.setValue(0);
                        jProgressBar2.setString("");
                        jDialog1.setVisible(false);
                    }
                }
            }.execute();
    } //GEN-LAST:event_jButton5ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jCheckBox2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jCheckBox2ActionPerformed
        // TODO add your handling code here:
    } //GEN-LAST:event_jCheckBox2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jComboBox1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jComboBox1ActionPerformed
        updateButtons();

        final CidsBean selectedItem = (CidsBean)jComboBox1.getSelectedItem();

        if (selectedItem == null) {
            lstKassenzeichen.setModel(nullListModel);
        } else {
            final DefaultListModel tmpModel = new DefaultListModel();
            for (final CidsBean element
                        : selectedItem.getBeanCollectionProperty(
                            ArbeitspaketPropertyConstants.PROP__KASSENZEICHENNUMMERN)) {
                tmpModel.addElement(element);
                lstKassenzeichen.setModel(tmpModel);
            }
        }
    } //GEN-LAST:event_jComboBox1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton8ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton8ActionPerformed
        final CidsBean selectedItem = (CidsBean)jComboBox1.getSelectedItem();
        setButtonsEnabled(false);
        reloadArbeitsPakete();
        jComboBox1.setSelectedItem(selectedItem);
        setButtonsEnabled(true);
    }                                                                            //GEN-LAST:event_jButton8ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  buttonsEnabled  DOCUMENT ME!
     */
    private void setButtonsEnabled(final boolean buttonsEnabled) {
        this.buttonsEnabled = buttonsEnabled;
        updateButtons();
    }
    /**
     * DOCUMENT ME!
     */
    private void reloadArbeitsPakete() {
        final String query = "SELECT "
                    + CidsAppBackend.getInstance().getVerdisMetaClass(VerdisMetaClassConstants.MC_ARBEITSPAKET).getId()
                    + ", " + VerdisMetaClassConstants.MC_ARBEITSPAKET + ".id "
                    + "FROM " + VerdisMetaClassConstants.MC_ARBEITSPAKET + " "
                    + "WHERE fk_user = " + SessionManager.getSession().getUser().getId() + ";";
        ((DefaultComboBoxModel)jComboBox1.getModel()).removeAllElements();
        ((DefaultComboBoxModel)jComboBox1.getModel()).addElement(null);
        jComboBox1.getModel().setSelectedItem(null);
        new SwingWorker<Collection<CidsBean>, Void>() {

                @Override
                protected Collection<CidsBean> doInBackground() throws Exception {
                    final Collection<CidsBean> cbs = CidsAppBackend.getInstance().getBeansByQuery(query);
                    return cbs;
                }

                @Override
                protected void done() {
                    try {
                        final Collection<CidsBean> cbs = get();
                        for (final CidsBean cb : cbs) {
                            ((DefaultComboBoxModel)jComboBox1.getModel()).addElement(cb);
                        }
                    } catch (final Exception ex) {
                        LOG.warn(ex, ex);
                        CidsAppBackend.getInstance()
                                .showError("Fehler", "Beim Laden der Arbeitspakete kam es zu einem Fehler.", ex);
                    }
                }
            }.execute();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jToggleButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jToggleButton1ActionPerformed
        final int max = lstKassenzeichen.getSelectedIndices().length;
        final boolean checked = jToggleButton1.isSelected();
        setButtonsEnabled(false);
        jProgressBar1.setIndeterminate(false);
        jProgressBar1.setMaximum(max);

        final int[] selectedIndices = lstKassenzeichen.getSelectedIndices();
        new SwingWorker<Void, Object>() {

                @Override
                protected Void doInBackground() throws Exception {
                    int count = 0;
                    for (final int index : selectedIndices) {
                        final int tmpCount = ++count;
                        final CidsBean kassenzeichenNummer = (CidsBean)lstKassenzeichen.getModel().getElementAt(index);
                        kassenzeichenNummer.setProperty(
                            ArbeitspaketEintragPropertyConstants.PROP__IST_ABGEARBEITET,
                            checked);
                        final CidsBean persistedBean = kassenzeichenNummer.persist();

                        SwingUtilities.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    ((DefaultListModel)lstKassenzeichen.getModel()).setElementAt(persistedBean, index);
                                    jProgressBar1.setString((tmpCount) + "/" + max + " Kassenzeichen markiert");
                                    jProgressBar1.setValue(tmpCount);
                                }
                            });
                    }
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                    } catch (final Exception ex) {
                        LOG.warn(ex, ex);
                        CidsAppBackend.getInstance()
                                .showError(
                                    "Fehler",
                                    "Beim Speichern des Arbeitspakets-Status kam es zu einem Fehler",
                                    ex);
                    } finally {
                        setButtonsEnabled(true);
                    }
                }
            }.execute();
    } //GEN-LAST:event_jToggleButton1ActionPerformed

    /**
     * DOCUMENT ME!
     */
    public void searchStarted() {
        setButtonsEnabled(false);
        jComboBox1.setSelectedItem(null);
        setKassenzeichenList(null);
        jProgressBar1.setString("Kassenzeichen werden gesucht...");
        jProgressBar1.setIndeterminate(true);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JProgressBar getProgressBar() {
        return jProgressBar1;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kassenzeichenNummern  DOCUMENT ME!
     */
    public void searchFinished(final Collection<Integer> kassenzeichenNummern) {
        final List<Integer> kassenzeichenList = new ArrayList<Integer>();
        if (kassenzeichenNummern != null) {
            for (final Integer kassenzeichenNummer : kassenzeichenNummern) {
                kassenzeichenList.add(kassenzeichenNummer);
            }
        }
        addManyKassenzeichenToList(kassenzeichenList);
        setButtonsEnabled(true);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kassenzeichen  DOCUMENT ME!
     */
    private void addOneKassenzeichenToList(final Integer kassenzeichen) {
        addOneKassenzeichenToList(kassenzeichen, !jCheckBox1.isSelected());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kassenzeichen  DOCUMENT ME!
     * @param  removeOld      DOCUMENT ME!
     */
    private void addOneKassenzeichenToList(final Integer kassenzeichen, final boolean removeOld) {
        addManyKassenzeichenToList((List<Integer>)Arrays.asList(new Integer[] { kassenzeichen }), removeOld);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kassenzeichenList  DOCUMENT ME!
     */
    private void addManyKassenzeichenToList(final List<Integer> kassenzeichenList) {
        addManyKassenzeichenToList(kassenzeichenList, !jCheckBox1.isSelected());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kassenzeichenList  DOCUMENT ME!
     * @param  removeOld          DOCUMENT ME!
     */
    private void addManyKassenzeichenToList(final List<Integer> kassenzeichenList, final boolean removeOld) {
        final List<CidsBean> kassenzeichennummern = new ArrayList<CidsBean>();
        for (final Integer kassenzeichen : kassenzeichenList) {
            final CidsBean newBean = CidsAppBackend.getInstance()
                        .getVerdisMetaClass(
                            VerdisMetaClassConstants.MC_ARBEITSPAKET_EINTRAG)
                        .getEmptyInstance()
                        .getBean();
            try {
                newBean.setProperty(ArbeitspaketEintragPropertyConstants.PROP__KASSENZEICHENNUMMER, kassenzeichen);
                newBean.setProperty(ArbeitspaketEintragPropertyConstants.PROP__IST_ABGEARBEITET, false);
            } catch (Exception ex) {
                LOG.warn(ex, ex);
            }
            kassenzeichennummern.add(newBean);
        }

        final int count = setKassenzeichenList(kassenzeichennummern, removeOld);
        jProgressBar1.setIndeterminate(false);
        jProgressBar1.setValue(0);
        if (count > 0) {
            jProgressBar1.setString(count + " Kassenzeichen gefunden");
        } else {
            jProgressBar1.setString("keine neuen Kassenzeichen");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  message  DOCUMENT ME!
     */
    public void searchFailed(final String message) {
        setKassenzeichenList(null);
        jProgressBar1.setIndeterminate(false);
        jProgressBar1.setValue(0);
        jProgressBar1.setString("Fehler: " + message);
    }

    /**
     * DOCUMENT ME!
     */
    private void gotoSelectedKassenzeichen() {
        final CidsBean arbeitspaketEintrag = (CidsBean)lstKassenzeichen.getSelectedValue();
        if (arbeitspaketEintrag != null) {
            final Integer kassenzeichen = (Integer)arbeitspaketEintrag.getProperty(
                    ArbeitspaketEintragPropertyConstants.PROP__KASSENZEICHENNUMMER);
            if (kassenzeichen != null) {
                CidsAppBackend.getInstance().gotoKassenzeichen(Integer.toString(kassenzeichen));
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   kassenzeichenList  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private int setKassenzeichenList(final List<CidsBean> kassenzeichenList) {
        return setKassenzeichenList(kassenzeichenList, !jCheckBox1.isSelected());
    }

    /**
     * DOCUMENT ME!
     *
     * @param   kassenzeichennummern  DOCUMENT ME!
     * @param   removeOld             DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private int setKassenzeichenList(final List<CidsBean> kassenzeichennummern, final boolean removeOld) {
        final DefaultListModel model = (DefaultListModel)lstKassenzeichen.getModel();
        int counter = 0;
        if (removeOld) {
            model.removeAllElements();
        }
        final ArrayList<Integer> indices = new ArrayList<Integer>();
        if ((kassenzeichennummern != null) && !kassenzeichennummern.isEmpty()) {
            Collections.sort(kassenzeichennummern, new Comparator<CidsBean>() {

                    @Override
                    public int compare(final CidsBean o1, final CidsBean o2) {
                        final Integer i1 = (Integer)o1.getProperty(
                                ArbeitspaketEintragPropertyConstants.PROP__KASSENZEICHENNUMMER);
                        final Integer i2 = (Integer)o2.getProperty(
                                ArbeitspaketEintragPropertyConstants.PROP__KASSENZEICHENNUMMER);
                        return i1.compareTo(i2);
                    }
                });
            for (final CidsBean kassenzeichen : kassenzeichennummern) {
                if (!model.contains(kassenzeichen)) {
                    model.addElement(kassenzeichen);
                }
                indices.add(((DefaultListModel)lstKassenzeichen.getModel()).indexOf(kassenzeichen));
                counter++;
            }
            final int[] is = new int[indices.size()];
            for (int i = 0; i < indices.size(); i++) {
                is[i] = indices.get(i);
            }
            lstKassenzeichen.setSelectedIndices(is);
            lstKassenzeichenValueChanged(null);
        }
        return counter;
    }

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        final CidsBean previousBean = this.cidsBean;
        this.cidsBean = cidsBean;
        if (jCheckBox1.isSelected() && (previousBean != null) && !previousBean.equals(cidsBean)) {
            final Integer previousKassenzeichen = (Integer)previousBean.getProperty(
                    KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER);
            addOneKassenzeichenToList(previousKassenzeichen);
        }
    }

    @Override
    public void editModeChanged() {
        updateButtons();
    }

    /**
     * DOCUMENT ME!
     */
    public void updateButtons() {
        final int count = lstKassenzeichen.getSelectedIndices().length;
        jComboBox1.setSelectedItem(buttonsEnabled);

        jButton1.setEnabled(buttonsEnabled && !CidsAppBackend.getInstance().isEditable() && (count == 1));
        jButton2.setEnabled(buttonsEnabled && (jComboBox1.getSelectedItem() == null)
                    && !CidsAppBackend.getInstance().isEditable());
        jButton3.setEnabled(buttonsEnabled && (jComboBox1.getSelectedItem() == null) && (count > 0)
                    && !CidsAppBackend.getInstance().isEditable());
        cmdPaste.setEnabled(buttonsEnabled && !lstKassenzeichen.getSelectionModel().isSelectionEmpty()
                    && Main.getInstance().getCurrentClipboard().isPastable());

        final Object value = lstKassenzeichen.getSelectedValue();
        if (value instanceof CidsBean) {
            final CidsBean cidsBean = (CidsBean)value;

            final Boolean istAbgearbeitet = (Boolean)cidsBean.getProperty(
                    ArbeitspaketEintragPropertyConstants.PROP__IST_ABGEARBEITET);
            jToggleButton1.setSelected((istAbgearbeitet != null) && istAbgearbeitet);
        }

        jToggleButton1.setEnabled(buttonsEnabled && (value instanceof CidsBean)
                    && (jComboBox1.getSelectedItem() != null));

        jCheckBox1.setEnabled(buttonsEnabled && (jComboBox1.getSelectedItem() == null)
                    && !CidsAppBackend.getInstance().isEditable());
    }

    @Override
    public void clipboardChanged() {
        updateButtons();
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class PasteDialog extends JDialog {

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         */
        private void allLocked() {
            jProgressBar2.setString("bereit zum Kopieren");
            jProgressBar2.setValue(0);
            jButton4.setEnabled(true);
            jButton5.setEnabled(true);
        }

        /**
         * DOCUMENT ME!
         */
        private void allReleased() {
            jProgressBar2.setString("Sperren freigegeben");
            jProgressBar2.setValue(0);
            jButton4.setEnabled(true);
            jButton5.setEnabled(true);
            SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        PasteDialog.super.setVisible(false);
                    }
                });
        }

        /**
         * DOCUMENT ME!
         *
         * @param  lockOrRelease                 DOCUMENT ME!
         * @param  mainToLock                    DOCUMENT ME!
         * @param  kassenzeichenToLockOrRelease  DOCUMENT ME!
         */
        private void lock(final boolean lockOrRelease,
                final Integer mainToLock,
                final List<Integer> kassenzeichenToLockOrRelease) {
            Collections.sort(kassenzeichenToLockOrRelease);
            kassenzeichenToLockOrRelease.add(mainToLock);
            jProgressBar2.setMaximum(kassenzeichenToLockOrRelease.size());
            if (lockOrRelease) {
                jProgressBar2.setString("Kassenzeichen werden gesperrt...");
            } else {
                jProgressBar2.setString("Kassenzeichen werden freigegeben...");
            }
            jProgressBar2.setValue(0);
            jButton4.setEnabled(false);
            jButton5.setEnabled(false);
            bgColorMap.clear();

            final Collection<CidsBean> allExistingLocks = new ArrayList<CidsBean>();
            for (final Integer kassenzeichenNummer : kassenzeichenToLockOrRelease) {
                new SwingWorker<Collection, Void>() {

                        @Override
                        protected Collection doInBackground() throws Exception {
                            try {
                                final boolean addToList = !kassenzeichenNummer.equals(mainToLock);
                                if (lockOrRelease) {
                                    final CidsBean kassenzeichenBean = CidsAppBackend.getInstance()
                                                .loadKassenzeichenByNummer(kassenzeichenNummer);
                                    final Collection<CidsBean> infoBeans = new ArrayList<CidsBean>();

                                    if (addToList) {
                                        for (final CidsBean entity
                                                    : Main.getInstance().getCurrentClipboard().getClipboardBeans()) {
                                            switch (CidsAppBackend.getInstance().getMode()) {
                                                case REGEN: {
                                                    infoBeans.add((CidsBean)entity.getProperty(
                                                            FlaechePropertyConstants.PROP__FLAECHENINFO));
                                                }
                                                break;
                                                case ESW: {
                                                    infoBeans.add((CidsBean)entity.getProperty(
                                                            FrontPropertyConstants.PROP__FRONTINFO));
                                                }
                                                break;
                                            }
                                        }

                                        int count = 0;
                                        switch (CidsAppBackend.getInstance().getMode()) {
                                            case REGEN: {
                                                for (final CidsBean flaeche
                                                            : kassenzeichenBean.getBeanCollectionProperty(
                                                                KassenzeichenPropertyConstants.PROP__FLAECHEN)) {
                                                    if (infoBeans.contains(
                                                                    (CidsBean)flaeche.getProperty(
                                                                        FlaechePropertyConstants.PROP__FLAECHENINFO))) {
                                                        count++;
                                                    }
                                                }
                                            }
                                            break;
                                            case ESW: {
                                                for (final CidsBean front
                                                            : kassenzeichenBean.getBeanCollectionProperty(
                                                                KassenzeichenPropertyConstants.PROP__FLAECHEN)) {
                                                    if (infoBeans.contains(
                                                                    (CidsBean)front.getProperty(
                                                                        FrontPropertyConstants.PROP__FRONTINFO))) {
                                                        count++;
                                                    }
                                                }
                                            }
                                            break;
                                        }
                                        if (count
                                                    == Main.getInstance().getCurrentClipboard().getClipboardBeans()
                                                    .size()) {
                                            bgColorMap.put(kassenzeichenBean, Color.orange);
                                        } else if (count > 0) {
                                            bgColorMap.put(kassenzeichenBean, Color.yellow);
                                        }

                                        SwingUtilities.invokeLater(new Runnable() {

                                                @Override
                                                public void run() {
                                                    ((DefaultListModel)jList2.getModel()).addElement(kassenzeichenBean);
                                                }
                                            });
                                    }
                                    for (final CidsBean lock
                                                : CidsAppBackend.getInstance().acquireLock(
                                                    kassenzeichenBean,
                                                    !addToList)) {
                                        lockMap.put(
                                            kassenzeichenNummer,
                                            lock);
                                    }
                                } else {
                                    final List<CidsBean> locks = (List<CidsBean>)lockMap.get(
                                            kassenzeichenNummer);
                                    if (locks != null) {
                                        CidsAppBackend.getInstance().releaseLocks(locks);
                                        lockMap.remove(kassenzeichenNummer);
                                    }
                                }
                                return null;
                            } catch (final LockAlreadyExistsException ex) {
                                return ex.getAlreadyExisingLocks();
                            }
                        }

                        @Override
                        protected void done() {
                            try {
                                final Collection existingLocks = get();
                                if (existingLocks != null) {
                                    allExistingLocks.addAll(existingLocks);
                                }
                            } catch (final Exception ex) {
                                LOG.error(ex, ex);
                                CidsAppBackend.getInstance()
                                        .showError(
                                            "Fehler beim Sperren",
                                            "Beim Sperren des Kassenzeichens ist es zu einem Fehler gekommen.",
                                            ex);
                            } finally {
                                jProgressBar2.setValue(jProgressBar2.getValue() + 1);
                                if (jProgressBar2.getValue() == jProgressBar2.getMaximum()) {
                                    if (lockOrRelease) {
                                        if (allExistingLocks.isEmpty()) {
                                            allLocked();
                                        } else {
                                            CidsAppBackend.getInstance().showObjectsLockedDialog(allExistingLocks);
                                            lock(false, mainToLock, kassenzeichenToLockOrRelease);
                                            setVisible(false);
                                        }
                                    } else {
                                        allReleased();
                                    }
                                }
                            }
                        }
                    }.execute();
            }
        }

        @Override
        public void setVisible(final boolean visible) {
            if (visible == isVisible()) {
                return;
            }
            final AbstractClipboard clipboard = Main.getInstance().getCurrentClipboard();
            if (clipboard != null) {
                final List<Integer> kassenzeichenToLockOrRelease = new ArrayList<Integer>();
                final Integer mainToLock = (Integer)Main.getInstance().getCurrentClipboard().getFromKassenzeichenBean()
                            .getProperty(
                                    KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER);
                if (visible) {
                    SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                PasteDialog.super.setVisible(true);
                            }
                        });

                    final Collection<CidsBean> clipboardBeans = clipboard.getClipboardBeans();

                    ((DefaultListModel)jList1.getModel()).clear();
                    for (final CidsBean beanToPaste : clipboardBeans) {
                        ((DefaultListModel)jList1.getModel()).addElement(beanToPaste);
                    }

                    ((DefaultListModel)jList2.getModel()).clear();
                    for (final int index : lstKassenzeichen.getSelectedIndices()) {
                        final Integer kassenzeichenNummer = (Integer)lstKassenzeichen.getModel().getElementAt(index);
                        kassenzeichenToLockOrRelease.add(kassenzeichenNummer);
                    }

                    lock(true, mainToLock, kassenzeichenToLockOrRelease);
                } else {
                    for (int index = 0; index < jList2.getModel().getSize(); index++) {
                        final CidsBean kassenzeichenBean = ((CidsBean)jList2.getModel().getElementAt(index));
                        kassenzeichenToLockOrRelease.add((Integer)kassenzeichenBean.getProperty(
                                KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER));
                    }
                    lock(false, mainToLock, kassenzeichenToLockOrRelease);
                }
            }
        }
    }
}
