/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis.gui;

import Sirius.navigator.connection.SessionManager;

import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.WKTReader;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.Highlighter;

import org.openide.util.Exceptions;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Frame;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.cismet.cismap.commons.CrsTransformer;

import de.cismet.tools.BrowserLauncher;

import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;

import de.cismet.verdis.CidsAppBackend;

import de.cismet.verdis.server.search.FortfuehrungItemSearch;
import de.cismet.verdis.server.search.KassenzeichenGeomSearch;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class FortfuehrungsanlaesseDialog extends javax.swing.JDialog {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(FortfuehrungsanlaesseDialog.class);
    private static final float FLURSTUECKBUFFER_FOR_KASSENZEICHEN_GEOMSEARCH = -0.1f;

    private static FortfuehrungsanlaesseDialog INSTANCE = null;

    //~ Instance fields --------------------------------------------------------

    private boolean lockDateButtons = false;
    private final WKTReader wktreader = new WKTReader();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCloseDialog;
    private javax.swing.JToggleButton btnLastMonth;
    private javax.swing.JToggleButton btnLastWeek;
    private javax.swing.JButton btnRefreshAnlaesse;
    private javax.swing.JToggleButton btnThisMonth;
    private javax.swing.JToggleButton btnThisWeek;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox cbxAbgearbeitet;
    private org.jdesktop.swingx.JXDatePicker dpiFrom;
    private org.jdesktop.swingx.JXDatePicker dpiTo;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
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
    private org.jdesktop.swingx.JXTable jXTable1;
    private javax.swing.JLabel lblDokumentLink;
    private javax.swing.JList lstKassenzeichen;
    private javax.swing.JPanel panDetail;
    private javax.swing.JPanel panMaster;
    private javax.swing.JPanel panMasterDetail;
    private javax.swing.JPanel panPeriod;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form FortfuehrungsanlaesseDialog.
     *
     * @param  parent  DOCUMENT ME!
     * @param  modal   DOCUMENT ME!
     */
    private FortfuehrungsanlaesseDialog(final Frame parent, final boolean modal) {
        super(parent, modal);

        initComponents();

        final Highlighter istAbgearbeitetHighlighter = new IstAbgearbeitetHighlighter();
        jXTable1.setHighlighters(istAbgearbeitetHighlighter);

        jXTable1.setModel(new FortfuehrungenTableModel());

        jXTable1.getColumnModel().getColumn(0).setCellRenderer(jXTable1.getDefaultRenderer(String.class));
        jXTable1.getColumnModel().getColumn(1).setCellRenderer(jXTable1.getDefaultRenderer(String.class));
        jXTable1.getColumnModel().getColumn(2).setCellRenderer(jXTable1.getDefaultRenderer(String.class));

        jXTable1.getColumnModel().getColumn(0).setPreferredWidth(100);
        jXTable1.getColumnModel().getColumn(1).setPreferredWidth(150);
        jXTable1.getColumnModel().getColumn(2).setPreferredWidth(200);

        jXTable1.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jXTable1.setDragEnabled(false);

        jXTable1.getTableHeader().setResizingAllowed(true);
        jXTable1.getTableHeader().setReorderingAllowed(false);
        // jXTable1.setSortOrder(1, SortOrder.ASCENDING);

        jXTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                @Override
                public void valueChanged(final ListSelectionEvent e) {
                    // If cell selection is enabled, both row and column change events are fired
                    if ((e.getSource() == jXTable1.getSelectionModel()) && jXTable1.getRowSelectionAllowed()) {
                        fortfuehrungsTableListSelectionChanged(e);
                    }
                }
            });

        jProgressBar1.setVisible(false);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        panPeriod = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        dpiFrom = new org.jdesktop.swingx.JXDatePicker();
        dpiTo = new org.jdesktop.swingx.JXDatePicker();
        jPanel2 = new javax.swing.JPanel();
        btnThisWeek = new javax.swing.JToggleButton();
        btnLastWeek = new javax.swing.JToggleButton();
        btnThisMonth = new javax.swing.JToggleButton();
        btnLastMonth = new javax.swing.JToggleButton();
        jPanel7 = new javax.swing.JPanel();
        btnRefreshAnlaesse = new javax.swing.JButton();
        panMasterDetail = new javax.swing.JPanel();
        panMaster = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jXTable1 = new org.jdesktop.swingx.JXTable();
        panDetail = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        lblDokumentLink = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstKassenzeichen = new javax.swing.JList();
        jButton1 = new javax.swing.JButton();
        cbxAbgearbeitet = new javax.swing.JCheckBox();
        jPanel6 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        btnCloseDialog = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();
        jPanel9 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel1.setPreferredSize(new java.awt.Dimension(800, 643));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        panPeriod.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    FortfuehrungsanlaesseDialog.class,
                    "FortfuehrungsanlaesseDialog.panPeriod.border.title"))); // NOI18N
        panPeriod.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(
                FortfuehrungsanlaesseDialog.class,
                "FortfuehrungsanlaesseDialog.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 5);
        panPeriod.add(jLabel1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel2,
            org.openide.util.NbBundle.getMessage(
                FortfuehrungsanlaesseDialog.class,
                "FortfuehrungsanlaesseDialog.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 5);
        panPeriod.add(jLabel2, gridBagConstraints);

        dpiFrom.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

                @Override
                public void propertyChange(final java.beans.PropertyChangeEvent evt) {
                    dpiFromPropertyChange(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 5, 10);
        panPeriod.add(dpiFrom, gridBagConstraints);

        dpiTo.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

                @Override
                public void propertyChange(final java.beans.PropertyChangeEvent evt) {
                    dpiToPropertyChange(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 5, 10);
        panPeriod.add(dpiTo, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridLayout(2, 3, 5, 5));

        buttonGroup1.add(btnThisWeek);
        org.openide.awt.Mnemonics.setLocalizedText(
            btnThisWeek,
            org.openide.util.NbBundle.getMessage(
                FortfuehrungsanlaesseDialog.class,
                "FortfuehrungsanlaesseDialog.btnThisWeek.text")); // NOI18N
        btnThisWeek.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnThisWeekActionPerformed(evt);
                }
            });
        jPanel2.add(btnThisWeek);

        buttonGroup1.add(btnLastWeek);
        org.openide.awt.Mnemonics.setLocalizedText(
            btnLastWeek,
            org.openide.util.NbBundle.getMessage(
                FortfuehrungsanlaesseDialog.class,
                "FortfuehrungsanlaesseDialog.btnLastWeek.text")); // NOI18N
        btnLastWeek.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnLastWeekActionPerformed(evt);
                }
            });
        jPanel2.add(btnLastWeek);

        buttonGroup1.add(btnThisMonth);
        org.openide.awt.Mnemonics.setLocalizedText(
            btnThisMonth,
            org.openide.util.NbBundle.getMessage(
                FortfuehrungsanlaesseDialog.class,
                "FortfuehrungsanlaesseDialog.btnThisMonth.text")); // NOI18N
        btnThisMonth.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnThisMonthActionPerformed(evt);
                }
            });
        jPanel2.add(btnThisMonth);

        buttonGroup1.add(btnLastMonth);
        org.openide.awt.Mnemonics.setLocalizedText(
            btnLastMonth,
            org.openide.util.NbBundle.getMessage(
                FortfuehrungsanlaesseDialog.class,
                "FortfuehrungsanlaesseDialog.btnLastMonth.text")); // NOI18N
        btnLastMonth.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnLastMonthActionPerformed(evt);
                }
            });
        jPanel2.add(btnLastMonth);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        panPeriod.add(jPanel2, gridBagConstraints);

        jPanel7.setPreferredSize(new java.awt.Dimension(50, 10));

        final javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                50,
                Short.MAX_VALUE));
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                86,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weightx = 1.0;
        panPeriod.add(jPanel7, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            btnRefreshAnlaesse,
            org.openide.util.NbBundle.getMessage(
                FortfuehrungsanlaesseDialog.class,
                "FortfuehrungsanlaesseDialog.btnRefreshAnlaesse.text")); // NOI18N
        btnRefreshAnlaesse.setEnabled(false);
        btnRefreshAnlaesse.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRefreshAnlaesseActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 10, 10);
        panPeriod.add(btnRefreshAnlaesse, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(panPeriod, gridBagConstraints);

        panMasterDetail.setLayout(new java.awt.GridBagLayout());

        panMaster.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    FortfuehrungsanlaesseDialog.class,
                    "FortfuehrungsanlaesseDialog.panMaster.border.title"))); // NOI18N
        panMaster.setLayout(new java.awt.GridBagLayout());

        jXTable1.setEnabled(false);
        jScrollPane1.setViewportView(jXTable1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 10, 10);
        panMaster.add(jScrollPane1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        panMasterDetail.add(panMaster, gridBagConstraints);

        panDetail.setBorder(null);
        panDetail.setVerifyInputWhenFocusTarget(false);
        panDetail.setLayout(new java.awt.GridBagLayout());

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    FortfuehrungsanlaesseDialog.class,
                    "FortfuehrungsanlaesseDialog.jPanel3.border.title"))); // NOI18N
        jPanel3.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblDokumentLink,
            org.openide.util.NbBundle.getMessage(
                FortfuehrungsanlaesseDialog.class,
                "FortfuehrungsanlaesseDialog.lblDokumentLink.text")); // NOI18N
        lblDokumentLink.setEnabled(false);
        lblDokumentLink.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lblDokumentLinkMouseClicked(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel3.add(lblDokumentLink, gridBagConstraints);

        final javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(jPanel5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panDetail.add(jPanel3, gridBagConstraints);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    FortfuehrungsanlaesseDialog.class,
                    "FortfuehrungsanlaesseDialog.jPanel4.border.title"))); // NOI18N
        jPanel4.setLayout(new java.awt.GridBagLayout());

        lstKassenzeichen.setModel(new DefaultListModel());
        lstKassenzeichen.setEnabled(false);
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
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel4.add(jScrollPane2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton1,
            org.openide.util.NbBundle.getMessage(
                FortfuehrungsanlaesseDialog.class,
                "FortfuehrungsanlaesseDialog.jButton1.text")); // NOI18N
        jButton1.setEnabled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        jPanel4.add(jButton1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        panDetail.add(jPanel4, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            cbxAbgearbeitet,
            org.openide.util.NbBundle.getMessage(
                FortfuehrungsanlaesseDialog.class,
                "FortfuehrungsanlaesseDialog.cbxAbgearbeitet.text")); // NOI18N
        cbxAbgearbeitet.setEnabled(false);
        cbxAbgearbeitet.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cbxAbgearbeitetActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        panDetail.add(cbxAbgearbeitet, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        panMasterDetail.add(panDetail, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(panMasterDetail, gridBagConstraints);

        final javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        jPanel1.add(jPanel6, gridBagConstraints);

        jPanel8.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            btnCloseDialog,
            org.openide.util.NbBundle.getMessage(
                FortfuehrungsanlaesseDialog.class,
                "FortfuehrungsanlaesseDialog.btnCloseDialog.text")); // NOI18N
        btnCloseDialog.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnCloseDialogActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel8.add(btnCloseDialog, gridBagConstraints);

        jProgressBar1.setIndeterminate(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel8.add(jProgressBar1, gridBagConstraints);

        jPanel9.setOpaque(false);

        final javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                549,
                Short.MAX_VALUE));
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                29,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel8.add(jPanel9, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jPanel8, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jPanel1, gridBagConstraints);

        pack();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRefreshAnlaesseActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRefreshAnlaesseActionPerformed
        refreshFortfuehrungsList();
    }                                                                                      //GEN-LAST:event_btnRefreshAnlaesseActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnThisWeekActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnThisWeekActionPerformed
        final Calendar calendar = Calendar.getInstance();

        final Date toDate = calendar.getTime();

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        final Date fromDate = calendar.getTime();

        try {
            lockDateButtons = true;
            dpiFrom.setDate(fromDate);
            dpiTo.setDate(toDate);
        } finally {
            lockDateButtons = false;
        }

        periodChanged();
        refreshFortfuehrungsList();
    } //GEN-LAST:event_btnThisWeekActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnLastWeekActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnLastWeekActionPerformed
        final Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        final Date toDate = calendar.getTime();

        calendar.add(Calendar.DATE, -7);
        final Date fromDate = calendar.getTime();

        try {
            lockDateButtons = true;
            dpiFrom.setDate(fromDate);
            dpiTo.setDate(toDate);
        } finally {
            lockDateButtons = false;
        }

        periodChanged();
        refreshFortfuehrungsList();
    } //GEN-LAST:event_btnLastWeekActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnThisMonthActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnThisMonthActionPerformed
        final Calendar calendar = Calendar.getInstance();

        final Date toDate = calendar.getTime();

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        final Date fromDate = calendar.getTime();

        try {
            lockDateButtons = true;
            dpiFrom.setDate(fromDate);
            dpiTo.setDate(toDate);
        } finally {
            lockDateButtons = false;
        }

        periodChanged();
        refreshFortfuehrungsList();
    } //GEN-LAST:event_btnThisMonthActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnLastMonthActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnLastMonthActionPerformed
        final Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        final Date toDate = calendar.getTime();

        calendar.add(Calendar.MONTH, -1);
        final Date fromDate = calendar.getTime();

        try {
            lockDateButtons = true;
            dpiFrom.setDate(fromDate);
            dpiTo.setDate(toDate);
        } finally {
            lockDateButtons = false;
        }

        periodChanged();
        refreshFortfuehrungsList();
    } //GEN-LAST:event_btnLastMonthActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void dpiFromPropertyChange(final java.beans.PropertyChangeEvent evt) { //GEN-FIRST:event_dpiFromPropertyChange
        if (!lockDateButtons) {
            manualPeriodChangePerformed();
        }
    }                                                                              //GEN-LAST:event_dpiFromPropertyChange

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void dpiToPropertyChange(final java.beans.PropertyChangeEvent evt) { //GEN-FIRST:event_dpiToPropertyChange
        if (!lockDateButtons) {
            manualPeriodChangePerformed();
        }
    }                                                                            //GEN-LAST:event_dpiToPropertyChange

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnCloseDialogActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnCloseDialogActionPerformed
        dispose();
    }                                                                                  //GEN-LAST:event_btnCloseDialogActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstKassenzeichenValueChanged(final javax.swing.event.ListSelectionEvent evt) { //GEN-FIRST:event_lstKassenzeichenValueChanged
        jButton1.setEnabled(!lstKassenzeichen.getSelectionModel().isSelectionEmpty());
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
    private void lstKassenzeichenMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lstKassenzeichenMouseClicked
        if (evt.getClickCount() == 2) {
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
    private void lblDokumentLinkMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblDokumentLinkMouseClicked
        final String url = lblDokumentLink.getToolTipText();
        try {
            BrowserLauncher.openURL(url);
        } catch (Exception ex) {
            LOG.error("fehler beim öffnen der url", ex);
        }
    }                                                                               //GEN-LAST:event_lblDokumentLinkMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbxAbgearbeitetActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cbxAbgearbeitetActionPerformed
        try {
            final int displayedIndex = jXTable1.getSelectedRow();
            final int modelIndex = jXTable1.convertRowIndexToModel(displayedIndex);
            // final CidsBean selectedFortfuehrungBean = ((FortfuehrungenTableModel)jXTable1.getModel()).getItem(
            // modelIndex).getBean();
            // selectedFortfuehrungBean.setProperty(
            // FortfuehrungPropertyConstants.PROP__IST_ABGEARBEITET,
            // cbxAbgearbeitet.isSelected());
            // selectedFortfuehrungBean.persist();
            jXTable1.repaint();
        } catch (Exception ex) {
            LOG.error("fehler beim setzen von ist_abgearbeitet", ex);
        }
    } //GEN-LAST:event_cbxAbgearbeitetActionPerformed

    /**
     * DOCUMENT ME!
     */
    private void gotoSelectedKassenzeichen() {
        final int kassenzeichennummer = (Integer)lstKassenzeichen.getSelectedValue();
        CidsAppBackend.getInstance().gotoKassenzeichen(Integer.toString(kassenzeichennummer));
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kassenzeichennummern  DOCUMENT ME!
     */
    private void setKassenzeichenNummern(final Collection<Integer> kassenzeichennummern) {
        final DefaultListModel kassenzeichenListModel = (DefaultListModel)lstKassenzeichen.getModel();
        kassenzeichenListModel.removeAllElements();

        if (kassenzeichennummern != null) {
            for (final Integer kassenzeichennummer : kassenzeichennummern) {
                kassenzeichenListModel.addElement(kassenzeichennummer);
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void manualPeriodChangePerformed() {
        buttonGroup1.clearSelection();
        periodChanged();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  e  DOCUMENT ME!
     */
    private void fortfuehrungsTableListSelectionChanged(final ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }
        final int selectedIndex = jXTable1.getSelectedRow();
        final FortfuehrungItem selectedFortfuehrungItem;
        if (selectedIndex >= 0) {
            final int rowIndex = jXTable1.convertRowIndexToModel(selectedIndex);
            selectedFortfuehrungItem = ((FortfuehrungenTableModel)jXTable1.getModel()).getItem(rowIndex);
        } else {
            selectedFortfuehrungItem = null;
        }
        if (selectedFortfuehrungItem != null) {
            new SwingWorker<Collection<Integer>, Void>() {

                    @Override
                    protected Collection<Integer> doInBackground() throws Exception {
                        lstKassenzeichen.setEnabled(false);
                        cbxAbgearbeitet.setEnabled(false);
                        jProgressBar1.setVisible(true);
                        final List<Geometry> geoms = new ArrayList<Geometry>(selectedFortfuehrungItem.getGeoms()
                                        .size());
                        for (final Geometry geom : selectedFortfuehrungItem.getGeoms()) {
                            geoms.add(geom.buffer(FLURSTUECKBUFFER_FOR_KASSENZEICHEN_GEOMSEARCH));
                        }
                        final Geometry geomColl = new GeometryCollection(GeometryFactory.toGeometryArray(geoms),
                                geoms.get(0).getFactory());

                        final KassenzeichenGeomSearch geomSearch = new KassenzeichenGeomSearch();
                        final Geometry searchGeom = geomColl.buffer(0);
                        final int currentSrid = CrsTransformer.getCurrentSrid();
                        searchGeom.setSRID(currentSrid);
                        geomSearch.setGeometry(searchGeom);
                        final Collection<Integer> result = (Collection<Integer>)SessionManager.getProxy()
                                    .customServerSearch(SessionManager.getSession().getUser(), geomSearch);
                        return result;
                    }

                    @Override
                    protected void done() {
                        try {
                            final Collection<Integer> kassenzeichennummern = get();
                            setDetailEnabled(true);
                            setKassenzeichenNummern(kassenzeichennummern);
                            cbxAbgearbeitet.setSelected(selectedFortfuehrungItem.isIst_abgearbeitet());

                            final String ffn = selectedFortfuehrungItem.getFfn();
                            final Calendar cal = new GregorianCalendar();
                            cal.setTime(selectedFortfuehrungItem.getBeginn());
                            final int year = cal.get(Calendar.YEAR);

                            final String urlString = "\\\\stadt\\102\\102\\ALKIS-Dokumente\\Fortführungsnachweise\\"
                                        + year
                                        + "\\FN_"
                                        + year + "_" + ffn.substring(2, 6) + "_" + ffn.substring(6, 11) + ".pdf";
                            setDokumentLink(urlString);
                        } catch (final Exception ex) {
                            setKassenzeichenNummern(null);
                            cbxAbgearbeitet.setSelected(false);
                            LOG.fatal("", ex);
                        }
                        lstKassenzeichen.setEnabled(true);
//                        cbxAbgearbeitet.setEnabled(true);
                        jProgressBar1.setVisible(false);
                    }
                }.execute();
        } else {
            setDetailEnabled(false);
            setKassenzeichenNummern(null);
            cbxAbgearbeitet.setSelected(false);
            setDokumentLink(null);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  dokumentUrl  DOCUMENT ME!
     */
    private void setDokumentLink(final String dokumentUrl) {
        if (dokumentUrl != null) {
            lblDokumentLink.setText("<html><a href=\"" + dokumentUrl + "\">Dokument im Browser anzeigen</a>");
            lblDokumentLink.setToolTipText(dokumentUrl);
            lblDokumentLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            lblDokumentLink.setText("");
            lblDokumentLink.setToolTipText(null);
            lblDokumentLink.setCursor(Cursor.getDefaultCursor());
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void periodChanged() {
        final boolean anlaesseEnabled = (dpiFrom.getDate() != null) && (dpiTo.getDate() != null);
        btnRefreshAnlaesse.setEnabled(anlaesseEnabled);
        jXTable1.setEnabled(anlaesseEnabled);
    }

    /**
     * DOCUMENT ME!
     */
    private void refreshFortfuehrungsList() {
        new SwingWorker<Collection<FortfuehrungItem>, Void>() {

                @Override
                protected Collection<FortfuehrungItem> doInBackground() throws Exception {
                    btnRefreshAnlaesse.setEnabled(false);
                    jProgressBar1.setVisible(true);
                    jXTable1.setEnabled(false);
                    final CidsAppBackend be = CidsAppBackend.getInstance();

                    final FortfuehrungItemSearch itemSearch = new FortfuehrungItemSearch(dpiFrom.getDate(),
                            dpiTo.getDate());
                    final Collection<Object[]> rawItems = (Collection<Object[]>)SessionManager.getProxy()
                                .customServerSearch(SessionManager.getSession().getUser(), itemSearch);
                    final Map<Integer, FortfuehrungItem> ffnMap = new HashMap<Integer, FortfuehrungItem>();
                    for (final Object[] rawItem : rawItems) {
                        final int id = (Integer)rawItem[0];
                        if (!ffnMap.containsKey(id)) {
                            ffnMap.put(
                                id,
                                new FortfuehrungItem(
                                    id,
                                    (String)rawItem[1],
                                    (String)rawItem[2],
                                    (Date)rawItem[3],
                                    (String)rawItem[4],
                                    (String)rawItem[5]));
                        }
                        final Geometry geom = wktreader.read((String)rawItem[6]);
                        geom.setSRID(25832);

                        final int currentSrid = CrsTransformer.getCurrentSrid();
                        final String currentCrs = CrsTransformer.createCrsFromSrid(currentSrid);
                        final Geometry transformedAlkisLandparcelGeom = CrsTransformer.transformToGivenCrs((Geometry)
                                geom.clone(),
                                currentCrs);
                        transformedAlkisLandparcelGeom.setSRID(currentSrid);

                        ffnMap.get(id).getGeoms().add(transformedAlkisLandparcelGeom);
                    }
                    final List<FortfuehrungItem> items = new ArrayList<FortfuehrungItem>(ffnMap.values());
                    Collections.sort(items);
                    return items;
                }

                @Override
                protected void done() {
                    Collection<FortfuehrungItem> items = null;
                    try {
                        items = get();

                        jXTable1.getSelectionModel().clearSelection();
                        ((FortfuehrungenTableModel)jXTable1.getModel()).setItems(items.toArray(
                                new FortfuehrungItem[0]));
                    } catch (Exception ex) {
                        LOG.error("error while loading fortfuehrung items", ex);
                    }
                    btnRefreshAnlaesse.setEnabled(true);
                    jXTable1.setEnabled(true);
                    jProgressBar1.setVisible(false);

                    if ((items == null) || (items.isEmpty())) {
                        JOptionPane.showMessageDialog(
                            rootPane,
                            "<html>Es konnten keine Fortführungsfälle<br/>für den gewählten Zeitraum gefunden werden.",
                            "keine Fortführungsfälle gefunden",
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }.execute();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  enabled  DOCUMENT ME!
     */
    private void setDetailEnabled(final boolean enabled) {
        lstKassenzeichen.setEnabled(enabled);
//        cbxAbgearbeitet.setEnabled(enabled);
        lblDokumentLink.setEnabled(enabled);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static FortfuehrungsanlaesseDialog getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FortfuehrungsanlaesseDialog(Main.getInstance(), false);
        }
        return INSTANCE;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  the command line arguments
     */
    public static void main(final String[] args) {
        /*
         * Create and display the dialog
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    try {
                        javax.swing.UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
                        Log4JQuickConfig.configure4LumbermillOnLocalhost();
                    } catch (UnsupportedLookAndFeelException ex) {
                        Exceptions.printStackTrace(ex);
                    }

                    final FortfuehrungsanlaesseDialog dialog = new FortfuehrungsanlaesseDialog(
                            new javax.swing.JFrame(),
                            true);
                    dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                            @Override
                            public void windowClosing(final java.awt.event.WindowEvent e) {
                                System.exit(0);
                            }
                        });
                    dialog.setVisible(true);
                }
            });
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class IstAbgearbeitetHighlighter implements Highlighter {

        //~ Methods ------------------------------------------------------------

        @Override
        public Component highlight(final Component renderer, final ComponentAdapter adapter) {
            final int displayedIndex = adapter.row;
            final int modelIndex = jXTable1.convertRowIndexToModel(displayedIndex);
            final FortfuehrungItem item = ((FortfuehrungenTableModel)jXTable1.getModel()).getItem(modelIndex);
            final boolean istAbgearbeitet = item.isIst_abgearbeitet();
            renderer.setEnabled(!istAbgearbeitet);
            return renderer;
        }

        @Override
        public void addChangeListener(final ChangeListener l) {
        }

        @Override
        public void removeChangeListener(final ChangeListener l) {
        }

        @Override
        public ChangeListener[] getChangeListeners() {
            return new ChangeListener[0];
        }
    }
}
