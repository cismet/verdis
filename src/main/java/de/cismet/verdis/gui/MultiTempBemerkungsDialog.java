/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis.gui;

import Sirius.navigator.connection.SessionManager;

import org.apache.log4j.Logger;

import java.awt.Component;
import java.awt.event.KeyEvent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;

import de.cismet.verdis.CidsAppBackend;

/**
 * cismet GmbH, Saarbruecken, Germany.
 *
 * <p>... and it just works.**************************************************</p>
 *
 * @version  $Revision$, $Date$
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class MultiTempBemerkungsDialog extends javax.swing.JDialog {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(MultiTempBemerkungsDialog.class);

    private static MultiTempBemerkungsDialog INSTANCE;

    private static final String[] COLUMN_NAMES = new String[] {
            "Erstellt am",
            "Erstellt von",
            "Bemerkung",
            "Verfällt am"
        };

    private static final Class[] COLUMN_CLASSES = new Class[] {
            String.class,
            String.class,
            String.class,
            String.class
        };

    private static final String[] VERFAELLT_NAMES = { "nie", "in einem Monat", "in 3 Monaten", "in einem Jahr" };

    private static final Integer[] VERFAELLT_VALUES = {
            null,
            50,
            100,
            365
        };

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    //~ Instance fields --------------------------------------------------------

    private MultiBemerkung multiBemerkung;
    private boolean selectionEmpty;

    private boolean editable = false;
    private final DefaultTableCellRenderer dateRenderer = new DefaultTableCellRenderer();
    private final DefaultTableCellRenderer userRenderer = new DefaultTableCellRenderer();
    private final DefaultTableCellRenderer bemerkungRenderer = new TextAreaCellRenderer();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel lblBemerkung;
    private javax.swing.JLabel lblBemerkung1;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form NewJDialog.
     *
     * @param  parent  DOCUMENT ME!
     * @param  modal   DOCUMENT ME!
     */
    private MultiTempBemerkungsDialog(final java.awt.Frame parent, final boolean modal) {
        super(parent, modal);

        initComponents();

        StaticSwingTools.doClickButtonOnKeyStroke(
            jButton4,
            KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
            jDialog1.getRootPane());
        StaticSwingTools.doClickButtonOnKeyStroke(
            jButton1,
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            jDialog1.getRootPane());

        getRootPane().setDefaultButton(jButton2);
        StaticSwingTools.doClickButtonOnKeyStroke(
            jButton2,
            KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
            jDialog1.getRootPane());
        StaticSwingTools.doClickButtonOnKeyStroke(
            jButton2,
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            jDialog1.getRootPane());

        dateRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        userRenderer.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);
        bemerkungRenderer.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);

        jTable1.getColumnModel().getColumn(0).setPreferredWidth(90);
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(120);
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(300);
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(90);

        jTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                @Override
                public void valueChanged(final ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        setSelectionEmpty(jTable1.getSelectedRow() == -1);
                    }
                }
            });
        jDialog1.pack();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void updateButtons() {
        jButton3.setEnabled(isEditable() && !isSelectionEmpty());
        jButton5.setEnabled(isEditable());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  editable  DOCUMENT ME!
     */
    public void setEditable(final boolean editable) {
        this.editable = editable;
        updateButtons();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static MultiTempBemerkungsDialog getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MultiTempBemerkungsDialog(null, true);
        }
        return INSTANCE;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isSelectionEmpty() {
        return selectionEmpty;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  selectionEmpty  DOCUMENT ME!
     */
    public void setSelectionEmpty(final boolean selectionEmpty) {
        this.selectionEmpty = selectionEmpty;
        updateButtons();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  multiBemerkung  DOCUMENT ME!
     */
    public void setMultiBemerkung(final MultiBemerkung multiBemerkung) {
        this.multiBemerkung = multiBemerkung;
        ((AbstractTableModel)jTable1.getModel()).fireTableDataChanged();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public MultiBemerkung getMultiBemerkung() {
        return multiBemerkung;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jDialog1 = new javax.swing.JDialog();
        jPanel1 = new javax.swing.JPanel();
        lblBemerkung = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox();
        lblBemerkung1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        jDialog1.setTitle(org.openide.util.NbBundle.getMessage(
                MultiTempBemerkungsDialog.class,
                "MultiTempBemerkungsDialog.jDialog1.title_1")); // NOI18N
        jDialog1.setModal(true);
        jDialog1.setResizable(false);
        jDialog1.getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblBemerkung,
            org.openide.util.NbBundle.getMessage(
                MultiTempBemerkungsDialog.class,
                "MultiTempBemerkungsDialog.lblBemerkung.text_1")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
        jPanel1.add(lblBemerkung, gridBagConstraints);

        jPanel4.setLayout(new java.awt.GridLayout(1, 0, 5, 0));

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton1,
            org.openide.util.NbBundle.getMessage(
                MultiTempBemerkungsDialog.class,
                "MultiTempBemerkungsDialog.jButton1.text_1")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });
        jPanel4.add(jButton1);

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton4,
            org.openide.util.NbBundle.getMessage(
                MultiTempBemerkungsDialog.class,
                "MultiTempBemerkungsDialog.jButton4.text_1")); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton4ActionPerformed(evt);
                }
            });
        jPanel4.add(jButton4);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        jPanel1.add(jPanel4, gridBagConstraints);

        jComboBox1.setModel(new DefaultComboBoxModel(VERFAELLT_NAMES));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        jPanel1.add(jComboBox1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblBemerkung1,
            org.openide.util.NbBundle.getMessage(
                MultiTempBemerkungsDialog.class,
                "MultiTempBemerkungsDialog.lblBemerkung1.text_1")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
        jPanel1.add(lblBemerkung1, gridBagConstraints);

        jScrollPane2.setPreferredSize(new java.awt.Dimension(300, 100));

        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setWrapStyleWord(true);
        jScrollPane2.setViewportView(jTextArea1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        jPanel1.add(jScrollPane2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jDialog1.getContentPane().add(jPanel1, gridBagConstraints);

        jDialog1.getRootPane().setDefaultButton(jButton4);

        setTitle(org.openide.util.NbBundle.getMessage(
                MultiTempBemerkungsDialog.class,
                "MultiTempBemerkungsDialog.title")); // NOI18N
        setPreferredSize(new java.awt.Dimension(600, 300));
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jTable1.setModel(new MultiTableModel());
        jTable1.getTableHeader().setResizingAllowed(false);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable1);
        jTable1.getColumnModel().getColumn(0).setCellRenderer(dateRenderer);
        jTable1.getColumnModel().getColumn(1).setCellRenderer(userRenderer);
        jTable1.getColumnModel().getColumn(2).setCellRenderer(bemerkungRenderer);
        jTable1.getColumnModel().getColumn(3).setCellRenderer(dateRenderer);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(jScrollPane1, gridBagConstraints);

        jPanel3.setLayout(new java.awt.GridBagLayout());

        jButton5.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/add.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            jButton5,
            org.openide.util.NbBundle.getMessage(
                MultiTempBemerkungsDialog.class,
                "MultiTempBemerkungsDialog.jButton5.text_1"));                              // NOI18N
        jButton5.setToolTipText(org.openide.util.NbBundle.getMessage(
                MultiTempBemerkungsDialog.class,
                "MultiTempBemerkungsDialog.jButton5.toolTipText_1"));                       // NOI18N
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
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel3.add(jButton5, gridBagConstraints);

        jButton3.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/remove.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            jButton3,
            org.openide.util.NbBundle.getMessage(
                MultiTempBemerkungsDialog.class,
                "MultiTempBemerkungsDialog.jButton3.text_1"));                                 // NOI18N
        jButton3.setToolTipText(org.openide.util.NbBundle.getMessage(
                MultiTempBemerkungsDialog.class,
                "MultiTempBemerkungsDialog.jButton3.toolTipText_1"));                          // NOI18N
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
        jPanel3.add(jButton3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel2.add(jPanel3, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton2,
            org.openide.util.NbBundle.getMessage(
                MultiTempBemerkungsDialog.class,
                "MultiTempBemerkungsDialog.jButton2.text_1")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton2ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        jPanel2.add(jButton2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(jPanel2, gridBagConstraints);

        pack();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton5ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton5ActionPerformed
        jTextArea1.setText(null);
        jComboBox1.setSelectedIndex(0);
        jTextArea1.requestFocus();
        StaticSwingTools.showDialog(jDialog1);
    }                                                                            //GEN-LAST:event_jButton5ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton3ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton3ActionPerformed
        final int index = jTable1.getSelectedRow();
        if ((index >= 0) && (index < multiBemerkung.getBemerkungen().size())) {
            multiBemerkung.getBemerkungen().remove(index);
            ((AbstractTableModel)jTable1.getModel()).fireTableDataChanged();
        }
    }                                                                            //GEN-LAST:event_jButton3ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton2ActionPerformed
        setVisible(false);
    }                                                                            //GEN-LAST:event_jButton2ActionPerformed

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
    private void jButton4ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton4ActionPerformed
        jDialog1.setVisible(false);
        final Date date = new Date();
        final String bem = jTextArea1.getText();
        final Integer verfaellt = VERFAELLT_VALUES[jComboBox1.getSelectedIndex()];
        final String user;
        if (SessionManager.isInitialized()) {
            user = SessionManager.getSession().getUser().getName();
        } else {
            user = null;
        }
        multiBemerkung.getBemerkungen().add(new SingleBemerkung(date, user, bem, verfaellt));
        ((AbstractTableModel)jTable1.getModel()).fireTableDataChanged();
    }                                                                            //GEN-LAST:event_jButton4ActionPerformed

    @Override
    public void setEnabled(final boolean b) {
        super.setEnabled(b);
        setSelectionEmpty(b && (jTable1.getSelectedRow() != -1));
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  the command line arguments
     */
    public static void main(final String[] args) {
        final MultiBemerkung multiBemerkung = new MultiBemerkung(
                Arrays.asList(
                    new SingleBemerkung[] { new SingleBemerkung(new Date(), "jruiz", "Dies ist ein Test", 100) }));

        Log4JQuickConfig.configure4LumbermillOnLocalhost();
        final String bem = CidsAppBackend.transformMultiBemerkungToJson(multiBemerkung);
        LOG.fatal(bem);
        final MultiBemerkung multi = CidsAppBackend.transformMultiBemerkungFromJson(bem);
        MultiTempBemerkungsDialog.getInstance().setMultiBemerkung(multi);
        StaticSwingTools.showDialog(MultiTempBemerkungsDialog.getInstance());
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class MultiTableModel extends AbstractTableModel {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new GrafikPreviewTableModel object.
         */
        public MultiTableModel() {
            super();
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public String getColumnName(final int index) {
            return COLUMN_NAMES[index];
        }

        @Override
        public Class<?> getColumnClass(final int index) {
            return COLUMN_CLASSES[index];
        }

        @Override
        public int getColumnCount() {
            return COLUMN_NAMES.length;
        }

        @Override
        public int getRowCount() {
            if (multiBemerkung == null) {
                return 0;
            } else {
                return multiBemerkung.getBemerkungen().size();
            }
        }

        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            final SingleBemerkung single = multiBemerkung.getBemerkungen().get(rowIndex);

            switch (columnIndex) {
                case 0: {
                    if (single.getErstellt_am() == null) {
                        return "-";
                    } else {
                        return DATE_FORMAT.format(single.getErstellt_am());
                    }
                }
                case 1: {
                    return single.getErstellt_von();
                }
                case 2: {
                    return single.getBemerkung();
                }
                case 3: {
                    if (single.getVerfaellt_tage() == null) {
                        return "nie";
                    } else {
                        return DATE_FORMAT.format(single.getVerfallsDatum());
                    }
                }
                default: {
                    return null;
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class TextAreaCellRenderer extends DefaultTableCellRenderer implements TableCellRenderer {

        //~ Instance fields ----------------------------------------------------

        private final List<List<Integer>> rowAndCellHeightList = new ArrayList<>();

        //~ Methods ------------------------------------------------------------

        @Override
        public Component getTableCellRendererComponent(final JTable table,
                final Object value,
                final boolean isSelected,
                final boolean hasFocus,
                final int row,
                final int column) {
            final JTextArea textArea = new JTextArea(Objects.toString(value, ""));
            textArea.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setFont(table.getFont());
            textArea.setBackground(super.getTableCellRendererComponent(
                    table,
                    value,
                    isSelected,
                    hasFocus,
                    row,
                    column).getBackground());
            textArea.setBounds(table.getCellRect(row, column, false));

            final int preferredHeight = textArea.getPreferredSize().height;
            while (rowAndCellHeightList.size() <= row) {
                rowAndCellHeightList.add(new ArrayList<Integer>(column));
            }

            final List<Integer> cellHeightList = rowAndCellHeightList.get(row);
            while (cellHeightList.size() <= column) {
                cellHeightList.add(0);
            }
            cellHeightList.set(column, preferredHeight);

            Integer max = Integer.MIN_VALUE;
            for (final Integer i : cellHeightList) {
                if (max < i) {
                    max = i;
                }
            }
            if (table.getRowHeight(row) != max) {
                table.setRowHeight(row, max);
            }

            return textArea;
        }
    }
}
