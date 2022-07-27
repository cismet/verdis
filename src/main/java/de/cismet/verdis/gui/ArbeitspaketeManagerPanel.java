/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis.gui;

import Sirius.navigator.exception.ConnectionException;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;

import java.awt.Color;
import java.awt.Component;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.tools.gui.StaticSwingTools;

import de.cismet.verdis.CidsAppBackend;

import de.cismet.verdis.commons.constants.VerdisConstants;

import de.cismet.verdis.server.search.CsUsrSearchStatement;
import de.cismet.verdis.server.search.KassenzeichenlistSearchStatement;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class ArbeitspaketeManagerPanel extends javax.swing.JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(ArbeitspaketeManagerPanel.class);

    private static final String[] PREVIEW_COLUMN_NAMES = {
            "#",
            "Bezeichnung",
            "Benutzer",
            "∑",
            "✓"
        };

    private static ArbeitspaketeManagerPanel INSTANCE = null;

    private static final Class[] PREVIEW_COLUMN_CLASSES = {
            Integer.class,
            String.class,
            String.class,
            Integer.class,
            Integer.class,
        };

    //~ Instance fields --------------------------------------------------------

    protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    private Map<CidsBean, Integer> abgearbeitetMap = new HashMap<CidsBean, Integer>();
    private final MetaClass mcArbeitspaket;
    private final MetaClass mcArbeitspaketEintrag;
    private final ComboBoxModel<String> csUsrsModel;

    private CidsBean selectedPaket = null;

    private final PropertyChangeListener propListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            if (evt.getPropertyName().equals(VerdisConstants.PROP.ARBEITSPAKET.NAME)
                                        || evt.getPropertyName().equals(VerdisConstants.PROP.ARBEITSPAKET.LOGIN_NAME)) {
                                pakedDataChanged();
                            }
                        }
                    });
            }
        };

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JDialog jDialog2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JList jList2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form ArbeitspaketeManagerPanel.
     */
    private ArbeitspaketeManagerPanel() {
        MetaClass mcArbeitspaket;
        try {
            mcArbeitspaket = CidsAppBackend.getInstance().getVerdisMetaClass(VerdisConstants.MC.ARBEITSPAKET);
        } catch (final Exception ex) {
            LOG.warn(ex, ex);
            mcArbeitspaket = null;
        }
        this.mcArbeitspaket = mcArbeitspaket;

        MetaClass mcArbeitspaketEintrag;
        try {
            mcArbeitspaketEintrag = CidsAppBackend.getInstance()
                        .getVerdisMetaClass(VerdisConstants.MC.ARBEITSPAKET_EINTRAG);
        } catch (final Exception ex) {
            LOG.warn(ex, ex);
            mcArbeitspaketEintrag = null;
        }
        this.mcArbeitspaketEintrag = mcArbeitspaketEintrag;

        ComboBoxModel<String> csUsrsModel = null;
        try {
            final Collection<String> csUsrs = CidsAppBackend.getInstance()
                        .executeCustomServerSearch(new CsUsrSearchStatement());
            csUsrsModel = new DefaultComboBoxModel<>(csUsrs.toArray(new String[0]));
        } catch (final ConnectionException ex) {
            LOG.error(ex, ex);
        }
        this.csUsrsModel = csUsrsModel;
        initComponents();

        StaticSwingTools.decorateWithFixedAutoCompleteDecorator(jComboBox1);

        jList2.setCellRenderer(new DefaultListCellRenderer() {

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
                                    VerdisConstants.PROP.ARBEITSPAKET_EINTRAG.KASSENZEICHENNUMMER)));
                        final Boolean istAbgearbeitet = (Boolean)cidsBean.getProperty(
                                VerdisConstants.PROP.ARBEITSPAKET_EINTRAG.IST_ABGEARBEITET);
                        if ((istAbgearbeitet != null) && istAbgearbeitet) {
                            renderer.setEnabled(false);
                        }
                    }
                    return renderer;
                }
            });

        jTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                @Override
                public void valueChanged(final ListSelectionEvent e) {
                    if (!e.getValueIsAdjusting()) {
                        final int selectedRow = jTable1.getSelectedRow();
                        final CidsBean selectedBean = (CidsBean)((AbstractCidsBeanTableModel)jTable1.getModel())
                                    .getCidsBeanByIndex(jTable1.convertRowIndexToModel(selectedRow));
                        setSelectedPaket(selectedBean);
                    }
                }
            });

        jTable1.getColumnModel().getColumn(0).setMinWidth(40);
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(40);
        jTable1.getColumnModel().getColumn(0).setMaxWidth(40);

        jTable1.getColumnModel().getColumn(1).setPreferredWidth(200);

        jTable1.getColumnModel().getColumn(2).setPreferredWidth(200);

        jTable1.getColumnModel().getColumn(3).setMinWidth(20);
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(30);
        jTable1.getColumnModel().getColumn(3).setMaxWidth(40);

        jTable1.getColumnModel().getColumn(4).setMinWidth(20);
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(30);
        jTable1.getColumnModel().getColumn(4).setMaxWidth(40);

        final HighlightPredicate changedPredicate = new HighlightPredicate() {

                @Override
                public boolean isHighlighted(final Component renderer, final ComponentAdapter componentAdapter) {
                    final int displayedIndex = componentAdapter.row;
                    final int modelIndex = jTable1.convertRowIndexToModel(displayedIndex);
                    final CidsBean cidsBean = ((ArbeitspaketeTableModel)jTable1.getModel()).getCidsBeanByIndex(
                            modelIndex);
                    if (cidsBean != null) {
                        return cidsBean.getMetaObject().getStatus() == MetaObject.MODIFIED;
                    } else {
                        return false;
                    }
                }
            };

        final Highlighter changedHighlighter = new ColorHighlighter(changedPredicate, null, Color.RED);
        ((JXTable)jTable1).setHighlighters(changedHighlighter);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static ArbeitspaketeManagerPanel getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ArbeitspaketeManagerPanel();
        }
        return INSTANCE;
    }

//        cmdOk.setEnabled(b && !aggValidator.getState().isError());

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JDialog getDialog() {
        return jDialog1;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getSelectedPaket() {
        return selectedPaket;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  selectedPaket  DOCUMENT ME!
     */
    public void setSelectedPaket(final CidsBean selectedPaket) {
        if (selectedPaket != null) {
            selectedPaket.removePropertyChangeListener(propListener);
        }

        final Object old = this.selectedPaket;
        this.selectedPaket = selectedPaket;
        propertyChangeSupport.firePropertyChange("selectedPaket", old, selectedPaket);
        bindingGroup.unbind();
        bindingGroup.bind();

        if (selectedPaket != null) {
            selectedPaket.addPropertyChangeListener(propListener);
        }

        final boolean detailsEnabled = selectedPaket != null;
        jTextField1.setEnabled(detailsEnabled);
        jComboBox1.setEnabled(detailsEnabled);
        jList2.setEnabled(detailsEnabled);
        jButton4.setEnabled(detailsEnabled);
        jButton5.setEnabled(detailsEnabled);
        jButton7.setEnabled(detailsEnabled);
    }

    /**
     * DOCUMENT ME!
     */
    public void loadArbeitspakete() {
        abgearbeitetMap.clear();
        setSelectedPaket(null);

        jProgressBar1.setIndeterminate(true);
        new SwingWorker<List<CidsBean>, Void>() {

                @Override
                protected List<CidsBean> doInBackground() throws Exception {
                    final List<CidsBean> coll = CidsAppBackend.getInstance().getArbeitspakete();
                    for (final CidsBean arbeitspaket : coll) {
                        final List<CidsBean> nummern = arbeitspaket.getBeanCollectionProperty(
                                VerdisConstants.PROP.ARBEITSPAKET.KASSENZEICHENNUMMERN);
                        int countAbgearbeitet = 0;
                        for (final CidsBean nummer : nummern) {
                            final Boolean isAbgearbeitet = (Boolean)nummer.getProperty(
                                    VerdisConstants.PROP.ARBEITSPAKET_EINTRAG.IST_ABGEARBEITET);
                            if ((isAbgearbeitet != null) && isAbgearbeitet.equals(true)) {
                                countAbgearbeitet++;
                            }
                        }
                        abgearbeitetMap.put(arbeitspaket, countAbgearbeitet);
                    }
                    return coll;
                }

                @Override
                protected void done() {
                    try {
                        final List<CidsBean> coll = get();
                        ((ArbeitspaketeTableModel)jTable1.getModel()).setCidsBeans(coll);
                    } catch (final Exception ex) {
                        LOG.warn(ex, ex);
                    } finally {
                        jProgressBar1.setIndeterminate(false);
                    }
                }
            }.execute();
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jDialog1 = new javax.swing.JDialog() {

                @Override
                public void setVisible(final boolean visible) {
                    if (!visible) {
                        boolean changed = false;
                        for (final CidsBean arbeitspaket : ((ArbeitspaketeTableModel)jTable1.getModel()).getCidsBeans()) {
                            if (arbeitspaket.getMetaObject().getStatus() != MetaObject.NO_STATUS) {
                                changed = true;
                                break;
                            }
                        }

                        if (changed) {
                            final int answer = JOptionPane.showConfirmDialog(
                                    this,
                                    "Wollen Sie die gemachten \u00C4nderungen speichern?",
                                    "Änderungen speichern ?",
                                    JOptionPane.YES_NO_CANCEL_OPTION);
                            if (answer == JOptionPane.YES_OPTION) {
                                jButton6ActionPerformed(null);
                                super.setVisible(visible);
                            } else if (answer == JOptionPane.NO_OPTION) {
                                super.setVisible(visible);
                            } else {
                            }
                        } else {
                            super.setVisible(visible);
                        }
                    } else {
                        super.setVisible(visible);
                    }
                }
            };
        jDialog2 = new javax.swing.JDialog();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jButton9 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new JXTable();
        jPanel3 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox(csUsrsModel);
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList();
        jPanel8 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();
        jButton6 = new javax.swing.JButton();

        jDialog1.setTitle(org.openide.util.NbBundle.getMessage(
                ArbeitspaketeManagerPanel.class,
                "ArbeitspaketeManagerPanel.jDialog1.title")); // NOI18N
        jDialog1.setMinimumSize(new java.awt.Dimension(800, 400));

        jDialog1.setContentPane(this);

        jDialog2.setTitle(org.openide.util.NbBundle.getMessage(
                ArbeitspaketeManagerPanel.class,
                "ArbeitspaketeManagerPanel.jDialog2.title")); // NOI18N
        jDialog2.setMinimumSize(new java.awt.Dimension(500, 300));
        jDialog2.setModal(true);
        jDialog2.getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel6.setLayout(new java.awt.GridBagLayout());

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane2.setViewportView(jTextArea1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanel6.add(jScrollPane2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel3,
            org.openide.util.NbBundle.getMessage(
                ArbeitspaketeManagerPanel.class,
                "ArbeitspaketeManagerPanel.jLabel3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel6.add(jLabel3, gridBagConstraints);

        jPanel7.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton9,
            org.openide.util.NbBundle.getMessage(
                ArbeitspaketeManagerPanel.class,
                "ArbeitspaketeManagerPanel.jButton9.text")); // NOI18N
        jButton9.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton9ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        jPanel7.add(jButton9, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton8,
            org.openide.util.NbBundle.getMessage(
                ArbeitspaketeManagerPanel.class,
                "ArbeitspaketeManagerPanel.jButton8.text")); // NOI18N
        jButton8.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton8ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel7.add(jButton8, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_END;
        jPanel6.add(jPanel7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jDialog2.getContentPane().add(jPanel6, gridBagConstraints);

        setLayout(new java.awt.GridBagLayout());

        jPanel4.setLayout(new java.awt.GridBagLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    ArbeitspaketeManagerPanel.class,
                    "ArbeitspaketeManagerPanel.jPanel1.border.title"))); // NOI18N
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel9.setLayout(new java.awt.GridBagLayout());

        jTable1.setModel(new ArbeitspaketeTableModel());
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable1);
        jTable1.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel9.add(jScrollPane1, gridBagConstraints);

        jPanel3.setLayout(new java.awt.GridBagLayout());

        jButton2.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/add.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            jButton2,
            org.openide.util.NbBundle.getMessage(
                ArbeitspaketeManagerPanel.class,
                "ArbeitspaketeManagerPanel.jButton2.text"));                                // NOI18N
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
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel3.add(jButton2, gridBagConstraints);

        jButton3.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/remove.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            jButton3,
            org.openide.util.NbBundle.getMessage(
                ArbeitspaketeManagerPanel.class,
                "ArbeitspaketeManagerPanel.jButton3.text"));                                   // NOI18N
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
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel3.add(jButton3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        jPanel9.add(jPanel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jPanel9, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel4.add(jPanel1, gridBagConstraints);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    ArbeitspaketeManagerPanel.class,
                    "ArbeitspaketeManagerPanel.jPanel2.border.title"))); // NOI18N
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jPanel10.setLayout(new java.awt.GridBagLayout());

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${selectedPaket.login_name}"),
                jComboBox1,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel10.add(jComboBox1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(
                ArbeitspaketeManagerPanel.class,
                "ArbeitspaketeManagerPanel.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 15);
        jPanel10.add(jLabel1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel2,
            org.openide.util.NbBundle.getMessage(
                ArbeitspaketeManagerPanel.class,
                "ArbeitspaketeManagerPanel.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 15);
        jPanel10.add(jLabel2, gridBagConstraints);

        jTextField1.setMinimumSize(new java.awt.Dimension(200, 27));
        jTextField1.setPreferredSize(new java.awt.Dimension(250, 27));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${selectedPaket.name}"),
                jTextField1,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel10.add(jTextField1, gridBagConstraints);

        jScrollPane3.setMinimumSize(new java.awt.Dimension(150, 19));
        jScrollPane3.setPreferredSize(new java.awt.Dimension(150, 180));

        final org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create(
                "${selectedPaket.kassenzeichennummern}");
        final org.jdesktop.swingbinding.JListBinding jListBinding = org.jdesktop.swingbinding.SwingBindings
                    .createJListBinding(
                        org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                        this,
                        eLProperty,
                        jList2);
        jListBinding.setSourceNullValue(null);
        jListBinding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(jListBinding);

        jList2.addListSelectionListener(new javax.swing.event.ListSelectionListener() {

                @Override
                public void valueChanged(final javax.swing.event.ListSelectionEvent evt) {
                    jList2ValueChanged(evt);
                }
            });
        jScrollPane3.setViewportView(jList2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel10.add(jScrollPane3, gridBagConstraints);

        jPanel8.setLayout(new java.awt.GridBagLayout());

        jButton4.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/add.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            jButton4,
            org.openide.util.NbBundle.getMessage(
                ArbeitspaketeManagerPanel.class,
                "ArbeitspaketeManagerPanel.jButton4.text"));                                // NOI18N
        jButton4.setBorderPainted(false);
        jButton4.setContentAreaFilled(false);
        jButton4.setDisabledIcon(null);
        jButton4.setDisabledSelectedIcon(null);
        jButton4.setFocusPainted(false);
        jButton4.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton4ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel8.add(jButton4, gridBagConstraints);

        jButton5.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/remove.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            jButton5,
            org.openide.util.NbBundle.getMessage(
                ArbeitspaketeManagerPanel.class,
                "ArbeitspaketeManagerPanel.jButton5.text"));                                   // NOI18N
        jButton5.setBorderPainted(false);
        jButton5.setContentAreaFilled(false);
        jButton5.setDisabledIcon(null);
        jButton5.setDisabledSelectedIcon(null);
        jButton5.setFocusPainted(false);
        jButton5.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton5ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel8.add(jButton5, gridBagConstraints);

        jButton7.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/multiAddSql.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            jButton7,
            org.openide.util.NbBundle.getMessage(
                ArbeitspaketeManagerPanel.class,
                "ArbeitspaketeManagerPanel.jButton7.text"));                                        // NOI18N
        jButton7.setBorderPainted(false);
        jButton7.setContentAreaFilled(false);
        jButton7.setFocusPainted(false);
        jButton7.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton7ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel8.add(jButton7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 15);
        jPanel10.add(jPanel8, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(jPanel10, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel4.add(jPanel2, gridBagConstraints);

        jPanel5.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton1,
            org.openide.util.NbBundle.getMessage(
                ArbeitspaketeManagerPanel.class,
                "ArbeitspaketeManagerPanel.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel5.add(jButton1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 5);
        jPanel5.add(jProgressBar1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton6,
            org.openide.util.NbBundle.getMessage(
                ArbeitspaketeManagerPanel.class,
                "ArbeitspaketeManagerPanel.jButton6.text")); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton6ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel5.add(jButton6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel4.add(jPanel5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jPanel4, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton2ActionPerformed
        final CidsBean newBean = mcArbeitspaket.getEmptyInstance().getBean();
        ((ArbeitspaketeTableModel)jTable1.getModel()).addCidsBean(newBean);
        final int index = jTable1.convertRowIndexToView(((ArbeitspaketeTableModel)jTable1.getModel())
                        .getIndexByCidsBean(newBean));
        jTable1.getSelectionModel().setSelectionInterval(index, index);
    }                                                                            //GEN-LAST:event_jButton2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton3ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton3ActionPerformed
        ((ArbeitspaketeTableModel)jTable1.getModel()).removeCidsBean(selectedPaket);
        setSelectedPaket(null);
    }                                                                            //GEN-LAST:event_jButton3ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton4ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton4ActionPerformed
        StaticSwingTools.showDialog(new KassenzeichenAddDialog(new KassenzeichenAddDialogListener() {

                    @Override
                    public void kassenzeichennummerAdded(final Integer kassenzeichennummer) {
                        addOneKassenzeichenToList(kassenzeichennummer);
                    }
                }));
    } //GEN-LAST:event_jButton4ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton5ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton5ActionPerformed
        final List<CidsBean> eintraege = selectedPaket.getBeanCollectionProperty(
                VerdisConstants.PROP.ARBEITSPAKET.KASSENZEICHENNUMMERN);

        for (final Object entry : jList2.getSelectedValuesList()) {
            eintraege.remove((CidsBean)entry);
        }

        pakedDataChanged();
    } //GEN-LAST:event_jButton5ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton7ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton7ActionPerformed
        StaticSwingTools.showDialog(jDialog2);
    }                                                                            //GEN-LAST:event_jButton7ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jList2ValueChanged(final javax.swing.event.ListSelectionEvent evt) { //GEN-FIRST:event_jList2ValueChanged
        // TODO add your handling code here:
    } //GEN-LAST:event_jList2ValueChanged

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton1ActionPerformed
        jDialog1.setVisible(false);
    }                                                                            //GEN-LAST:event_jButton1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton6ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton6ActionPerformed
        final int selRow = jTable1.getSelectedRow();

        jProgressBar1.setIndeterminate(true);
        new SwingWorker<List<CidsBean>, Void>() {

                @Override
                protected List<CidsBean> doInBackground() throws Exception {
                    for (final CidsBean arbeitspaket : ((ArbeitspaketeTableModel)jTable1.getModel()).getCidsBeans()) {
                        arbeitspaket.persist();
                    }
                    for (final CidsBean removedArbeitsPaket
                                : ((ArbeitspaketeTableModel)jTable1.getModel()).getRemovedCidsBeans()) {
                        removedArbeitsPaket.persist();
                    }

                    return CidsAppBackend.getInstance().getArbeitspakete();
                }

                @Override
                protected void done() {
                    try {
                        final List<CidsBean> coll = get();
                        ((ArbeitspaketeTableModel)jTable1.getModel()).setCidsBeans(coll);
                        jTable1.setRowSelectionInterval(selRow, selRow);
                        pakedDataChanged();
                    } catch (final Exception ex) {
                        LOG.fatal(ex, ex);
                    } finally {
                        jProgressBar1.setIndeterminate(false);
                    }
                }
            }.execute();
    } //GEN-LAST:event_jButton6ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton8ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton8ActionPerformed
        jDialog2.setVisible(false);
    }                                                                            //GEN-LAST:event_jButton8ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton9ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton9ActionPerformed
        final KassenzeichenlistSearchStatement search = new KassenzeichenlistSearchStatement(jTextArea1.getText());

        jButton9.setEnabled(false);
        jProgressBar1.setIndeterminate(true);
        new SwingWorker<List<Integer>, Void>() {

                @Override
                protected List<Integer> doInBackground() throws Exception {
                    final List<Integer> tmpList = new ArrayList<Integer>();
                    final Collection coll = CidsAppBackend.getInstance().executeCustomServerSearch(search);
                    tmpList.addAll(coll);
                    return tmpList;
                }

                @Override
                protected void done() {
                    try {
                        final List<Integer> list = get();

                        addManyKassenzeichenToList(list);
                        jDialog2.setVisible(false);
                    } catch (final Exception ex) {
                        CidsAppBackend.getInstance()
                                .showError("SQL-Fehler", "Fehler bei der Ausführung der SQL-Query.", ex);
                        LOG.warn(ex, ex);
                    } finally {
                        jButton9.setEnabled(true);
                        jProgressBar1.setIndeterminate(false);
                    }
                }
            }.execute();
    } //GEN-LAST:event_jButton9ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  kassenzeichen  DOCUMENT ME!
     */
    private void addOneKassenzeichenToList(final Integer kassenzeichen) {
        addManyKassenzeichenToList((List<Integer>)Arrays.asList(new Integer[] { kassenzeichen }));
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kassenzeichenList  DOCUMENT ME!
     */
    private void addManyKassenzeichenToList(final List<Integer> kassenzeichenList) {
        final List<CidsBean> kassenzeichennummern = new ArrayList<CidsBean>();
        for (final Integer kassenzeichen : kassenzeichenList) {
            final CidsBean newBean = mcArbeitspaketEintrag.getEmptyInstance().getBean();
            try {
                newBean.setProperty(VerdisConstants.PROP.ARBEITSPAKET_EINTRAG.KASSENZEICHENNUMMER, kassenzeichen);
                newBean.setProperty(VerdisConstants.PROP.ARBEITSPAKET_EINTRAG.IST_ABGEARBEITET, false);
            } catch (Exception ex) {
                LOG.warn(ex, ex);
            }
            kassenzeichennummern.add(newBean);
        }

        setKassenzeichenList(kassenzeichennummern, false);
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
        int counter = 0;
        final List<CidsBean> eintraege = selectedPaket.getBeanCollectionProperty(
                VerdisConstants.PROP.ARBEITSPAKET.KASSENZEICHENNUMMERN);
        if (removeOld) {
            eintraege.clear();
        }
        final ArrayList<Integer> indices = new ArrayList<Integer>();
        if ((kassenzeichennummern != null) && !kassenzeichennummern.isEmpty()) {
            Collections.sort(kassenzeichennummern, new Comparator<CidsBean>() {

                    @Override
                    public int compare(final CidsBean o1, final CidsBean o2) {
                        final Integer i1 = (Integer)o1.getProperty(
                                VerdisConstants.PROP.ARBEITSPAKET_EINTRAG.KASSENZEICHENNUMMER);
                        final Integer i2 = (Integer)o2.getProperty(
                                VerdisConstants.PROP.ARBEITSPAKET_EINTRAG.KASSENZEICHENNUMMER);
                        return i1.compareTo(i2);
                    }
                });
            for (final CidsBean kassenzeichen : kassenzeichennummern) {
                if (!eintraege.contains(kassenzeichen)) {
                    eintraege.add(kassenzeichen);
                }
                eintraege.indexOf(kassenzeichen);
                counter++;
            }
            final int[] is = new int[indices.size()];
            for (int i = 0; i < indices.size(); i++) {
                is[i] = indices.get(i);
            }
            jList2.setSelectedIndices(is);
            jList2ValueChanged(null);

            pakedDataChanged();
        }
        return counter;
    }

    /**
     * DOCUMENT ME!
     */
    private void pakedDataChanged() {
        final int selRow = jTable1.getSelectedRow();
        if (selRow >= 0) {
            final int row = jTable1.convertRowIndexToModel(selRow);
            ((ArbeitspaketeTableModel)jTable1.getModel()).fireTableRowsUpdated(row, row);
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class ArbeitspaketeTableModel extends AbstractCidsBeanTableModel {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new GrafikPreviewTableModel object.
         */
        public ArbeitspaketeTableModel() {
            super(PREVIEW_COLUMN_NAMES, PREVIEW_COLUMN_CLASSES);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void removeCidsBean(final CidsBean cidsBean) {
            super.removeCidsBean(cidsBean);
            try {
                cidsBean.delete();
            } catch (final Exception ex) {
                CidsAppBackend.getInstance().showError("Fehler", "Fehler beim entfernen des Arbeitsauftrages.", ex);
            }
        }

        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            final CidsBean cidsBean = getCidsBeanByIndex(rowIndex);
            if (cidsBean == null) {
                return null;
            }
            final Integer id = (Integer)cidsBean.getProperty(VerdisConstants.PROP.ARBEITSPAKET.ID);
            final String bezeichnung = (String)cidsBean.getProperty(VerdisConstants.PROP.ARBEITSPAKET.NAME);
            final String benutzer = (String)cidsBean.getProperty(VerdisConstants.PROP.ARBEITSPAKET.LOGIN_NAME);
            final Integer countAbgearbeitet = abgearbeitetMap.get(cidsBean);
            final int countGesamt = cidsBean.getBeanCollectionProperty(
                    VerdisConstants.PROP.ARBEITSPAKET.KASSENZEICHENNUMMERN)
                        .size();
            switch (columnIndex) {
                case 0: {
                    return (id < 0) ? null : id;
                }
                case 1: {
                    return bezeichnung;
                }
                case 2: {
                    return benutzer;
                }
                case 3: {
                    return countGesamt;
                }
                case 4: {
                    return (countAbgearbeitet == null) ? null : countAbgearbeitet;
                }
                default: {
                    return null;
                }
            }
        }
    }
}
