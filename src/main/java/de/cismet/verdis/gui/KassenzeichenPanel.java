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

import java.awt.Color;

import java.sql.*;

import java.util.*;

import javax.swing.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import de.cismet.tools.gui.historybutton.DefaultHistoryModel;
import de.cismet.tools.gui.historybutton.HistoryModelListener;
import de.cismet.tools.gui.historybutton.JHistoryButton;

import de.cismet.validation.Validatable;
import de.cismet.validation.Validator;

import de.cismet.verdis.data.Kassenzeichen;

import de.cismet.verdis.interfaces.*;
/**
 * DOCUMENT ME!
 *
 * @author   hell
 * @version  $Revision$, $Date$
 */
public class KassenzeichenPanel extends javax.swing.JPanel implements Storable, HistoryModelListener {

    //~ Instance fields --------------------------------------------------------

    private boolean editmode = false;
    private java.sql.Connection connection;
    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private Vector kassenzeichenChangedListeners = new Vector();
    private Color myBlue = new java.awt.Color(0, 51, 153);
    private Kassenzeichen kassenzeichenData;
    private String userString = "???";
    private String lockNonce;
    // PERHAPS BETTER TO GIVE THE SUMMENPANEL THE VIEWOBJECT
    private Main mainApp;
    private DefaultHistoryModel historyModel = new DefaultHistoryModel();

    private Validator valTxtErfassungsdatum;
    private Validator valTxtVeranlagungsdatum;
    private Validator valTxtKassenzeichen;
    private JHistoryButton hbBack;
    private JHistoryButton hbFwd;
    private JLabel lblLastModification;

    private String requestForSelectionFlaeche;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkSperre;
    private javax.swing.JLabel lblBemerkung;
    private javax.swing.JLabel lblErfassungsdatum;
    private javax.swing.JLabel lblKassenzeichen;
    private javax.swing.JLabel lblSperre;
    private javax.swing.JLabel lblSuche;
    private javax.swing.JLabel lblVeranlagungsdatum;
    private javax.swing.JPanel panFill;
    private javax.swing.JPanel panKZValues;
    private javax.swing.JPanel panSearch;
    private javax.swing.JScrollPane scpBemerkung;
    private javax.swing.JSeparator sepTitle1;
    private javax.swing.JTextArea txtBemerkung;
    private javax.swing.JTextField txtErfassungsdatum;
    private javax.swing.JTextField txtKassenzeichen;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtSperreBemerkung;
    private javax.swing.JTextField txtVeranlagungsdatum;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form KassenzeichenPanel.
     */
    public KassenzeichenPanel() {
        initComponents();
        configureButtons();
        // de.cismet.gui.tools.DullPane dp=new de.cismet.gui.tools.DullPane();
        // this.panKZValues.add(dp);
        setEnabled(false);
        valTxtKassenzeichen = new Validator(txtKassenzeichen);
        valTxtErfassungsdatum = new Validator(txtErfassungsdatum);
        valTxtVeranlagungsdatum = new Validator(txtVeranlagungsdatum);
        historyModel.addHistoryModelListener(this);
        lblLastModification = new JLabel();
        lblLastModification.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/goto.png")));
        lblLastModification.setOpaque(false);
        // lblLastModification.setBorderPainted(false);
        // lblLastModification.setFocusPainted(false);
        hbBack.revalidate();
        hbFwd.revalidate();
        setShrinked(false);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  b  DOCUMENT ME!
     */
    private void THistoryEnabled(final boolean b) {
        hbBack.setEnabled(b);
        hbFwd.setEnabled(b);
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
        txtVeranlagungsdatum.setEditable(b);
        txtBemerkung.setEditable(b);
        chkSperre.setEnabled(b);
        if (b) {
            txtBemerkung.setBackground(java.awt.Color.white);
        } else {
            txtBemerkung.setBackground(this.getBackground());
        }
    }
    /**
     * Inserting Docking Window functionalty (Sebastian) 24.07.07 temporary disabled --> handled in Main.java.
     *
     * @param  c  DOCUMENT ME!
     */
    public void setLeftTitlebarColor(final Color c) {
        // panTitle.setLeftColor(c);
        // panTitle.repaint();
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
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public java.sql.Connection getConnection() {
        return connection;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  connectionInfo  DOCUMENT ME!
     */
    public void setConnectionInfo(final de.cismet.tools.ConnectionInfo connectionInfo) {
        try {
            Class.forName(connectionInfo.getDriver());
            connection = DriverManager.getConnection(connectionInfo.getUrl(),
                    connectionInfo.getUser(),
                    connectionInfo.getPass());
            // connection.setAutoCommit(false);
        } catch (ClassNotFoundException cnfEx) {
            log.fatal("Datenbanktreiber nicht gefunden!", cnfEx);
        } catch (java.sql.SQLException sqlEx) {
            log.fatal("Fehler beim Aufbau der Datenbankverbindung!", sqlEx);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Vector<JComponent> getCustomButtons() {
        final Vector<JComponent> tmp = new Vector<JComponent>();
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

        panSearch = new javax.swing.JPanel();
        sepTitle1 = new javax.swing.JSeparator();
        txtSearch = new javax.swing.JTextField();
        lblSuche = new javax.swing.JLabel();
        panKZValues = new javax.swing.JPanel();
        lblKassenzeichen = new javax.swing.JLabel();
        lblErfassungsdatum = new javax.swing.JLabel();
        lblVeranlagungsdatum = new javax.swing.JLabel();
        lblBemerkung = new javax.swing.JLabel();
        lblSperre = new javax.swing.JLabel();
        txtErfassungsdatum = new javax.swing.JTextField();
        txtVeranlagungsdatum = new javax.swing.JTextField();
        chkSperre = new javax.swing.JCheckBox();
        scpBemerkung = new javax.swing.JScrollPane();
        txtBemerkung = new javax.swing.JTextArea();
        txtKassenzeichen = new javax.swing.JTextField();
        txtSperreBemerkung = new javax.swing.JTextField();
        panFill = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        panSearch.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(13, 0, 0, 0);
        panSearch.add(sepTitle1, gridBagConstraints);

        txtSearch.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    txtSearchActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(13, 13, 3, 8);
        panSearch.add(txtSearch, gridBagConstraints);

        lblSuche.setText("Suche: Kassenzeichen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 8, 3, 3);
        panSearch.add(lblSuche, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        add(panSearch, gridBagConstraints);

        panKZValues.setLayout(new java.awt.GridBagLayout());

        lblKassenzeichen.setText("Kassenzeichen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 8, 3, 3);
        panKZValues.add(lblKassenzeichen, gridBagConstraints);

        lblErfassungsdatum.setText("Datum der Erfassung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 8, 3, 3);
        panKZValues.add(lblErfassungsdatum, gridBagConstraints);

        lblVeranlagungsdatum.setText("Datum der Veranlagung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 8, 3, 3);
        panKZValues.add(lblVeranlagungsdatum, gridBagConstraints);

        lblBemerkung.setText("Bemerkung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 8, 3, 3);
        panKZValues.add(lblBemerkung, gridBagConstraints);

        lblSperre.setText("Veranlagung gesperrt");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 8, 13, 3);
        panKZValues.add(lblSperre, gridBagConstraints);

        txtErfassungsdatum.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    txtErfassungsdatumActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 8);
        panKZValues.add(txtErfassungsdatum, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 8);
        panKZValues.add(txtVeranlagungsdatum, gridBagConstraints);

        chkSperre.setForeground(java.awt.Color.red);
        chkSperre.setEnabled(false);
        chkSperre.setFocusPainted(false);
        chkSperre.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    chkSperreActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 13, 2);
        panKZValues.add(chkSperre, gridBagConstraints);

        scpBemerkung.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scpBemerkung.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scpBemerkung.setMinimumSize(new java.awt.Dimension(6, 36));

        txtBemerkung.setColumns(3);
        txtBemerkung.setLineWrap(true);
        txtBemerkung.setRows(3);
        txtBemerkung.setMinimumSize(new java.awt.Dimension(0, 36));
        scpBemerkung.setViewportView(txtBemerkung);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 8);
        panKZValues.add(scpBemerkung, gridBagConstraints);

        txtKassenzeichen.setEditable(false);
        txtKassenzeichen.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    txtKassenzeichenActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(13, 3, 3, 8);
        panKZValues.add(txtKassenzeichen, gridBagConstraints);

        txtSperreBemerkung.setBackground(getBackground());
        txtSperreBemerkung.setEditable(false);
        txtSperreBemerkung.setForeground(java.awt.Color.red);
        txtSperreBemerkung.setBorder(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 13, 8);
        panKZValues.add(txtSperreBemerkung, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(panKZValues, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(panFill, gridBagConstraints);
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
    }                                                                             //GEN-LAST:event_chkSperreActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void txtSearchActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_txtSearchActionPerformed
        this.gotoKassenzeichen(txtSearch.getText());
    }                                                                             //GEN-LAST:event_txtSearchActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void txtErfassungsdatumActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_txtErfassungsdatumActionPerformed
    }                                                                                      //GEN-LAST:event_txtErfassungsdatumActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void txtKassenzeichenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_txtKassenzeichenActionPerformed
    }                                                                                    //GEN-LAST:event_txtKassenzeichenActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        final KassenzeichenPanel p = new KassenzeichenPanel();
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
    public void clear() {
        this.kassenzeichenData = null;
        this.txtSearch.setText("");
        this.txtErfassungsdatum.setText("");
        this.txtKassenzeichen.setText("");
        this.txtVeranlagungsdatum.setText("");
        this.txtSperreBemerkung.setText("");
        this.chkSperre.setSelected(false);
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
    public void gotoKassenzeichen(String kz, final boolean historyEnabled) {
        boolean refreshFlag = false;
        final String[] test = kz.split(":");
        requestForSelectionFlaeche = "";
        if (test.length > 1) {
            kz = test[0];
            requestForSelectionFlaeche = test[1];
        }

        final String kassenzeichen = kz;
        if (kassenzeichen.trim().equals(txtKassenzeichen.getText().trim())) {
            refreshFlag = true;
        }
        if (log.isDebugEnabled()) {
            log.debug("gotoKassenzeichen(" + kassenzeichen + ")");
        }
        if ((mainApp.changesPending() == false) || (refreshFlag == true)) {
//            TODO Abfragen ob er sich sicher ist und so weiter und das alle ge\u00E4nderten daten futsch sind bla bla
//            if (refreshFlag) {
//                JOptionPane.sh
//            }
//

            final Thread t = new Thread() {

                    @Override
                    public void run() {
                        try {
                            if (!txtSearch.getText().equals(kassenzeichen)) {
                                txtSearch.setText(kassenzeichen);
                            }
                            setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
                            final Statement stmnt = connection.createStatement();
                            ResultSet rs = null;
                            if (kassenzeichen.length() == 6) {
                                rs = stmnt.executeQuery(
                                        "select id,datum_erfassung,datum_veranlagung,bemerkung,sperre,bemerkung_sperre,letzte_aenderung_von,letzte_aenderung_ts from kassenzeichen where id/10 ="
                                                + kassenzeichen);
                            } else {
                                rs = stmnt.executeQuery(
                                        "select id,datum_erfassung,datum_veranlagung,bemerkung,sperre,bemerkung_sperre,letzte_aenderung_von,letzte_aenderung_ts from kassenzeichen where id="
                                                + kassenzeichen);
                            }
                            setCursor(java.awt.Cursor.getDefaultCursor());
                            if (!rs.next()) {
                                log.info("nix gefunden");
                                flashSearchField(java.awt.Color.RED);
                                // Inserting Docking Window functionalty (Sebastian) 24.07.07
                                lblLastModification.setToolTipText(null);
                            } else {
                                if (isEditmode() && !isEmpty()) {
                                    unlockDataset();
                                }
                                final int cc = rs.getMetaData().getColumnCount();
                                final Object[] rowdata = new Object[cc];
                                for (int i = 0; i < cc; ++i) {
                                    rowdata[i] = rs.getObject(i + 1);
                                }
                                kassenzeichenData = new Kassenzeichen();
                                kassenzeichenData.fillFromObjectArray(rowdata);
                                kassenzeichenData.backup();
                                txtKassenzeichen.setDocument(kassenzeichenData.getKassenzeichenModel());
                                valTxtKassenzeichen.reSetValidator((Validatable)
                                    kassenzeichenData.getKassenzeichenModel());
                                txtErfassungsdatum.setDocument(kassenzeichenData.getErfassungsdatumModel());
                                valTxtErfassungsdatum.reSetValidator((Validatable)
                                    kassenzeichenData.getErfassungsdatumModel());

                                txtVeranlagungsdatum.setDocument(kassenzeichenData.getVeranlagungsdatumModel());
                                valTxtVeranlagungsdatum.reSetValidator((Validatable)
                                    kassenzeichenData.getVeranlagungsdatumModel());

                                txtBemerkung.setDocument(kassenzeichenData.getBemerkungsModel());
                                final boolean chkSperreIsEnabled = chkSperre.isEnabled();
                                chkSperre.setModel(kassenzeichenData.getSperrenModel());
                                chkSperre.setEnabled(chkSperreIsEnabled);
                                txtSperreBemerkung.setDocument(kassenzeichenData.getBemerkungSperreModel());
                                // Inserting Docking Window functionalty (Sebastian) 24.07.07
                                lblLastModification.setToolTipText(kassenzeichenData.getLetzteAenderung());

                                flashSearchField(java.awt.Color.GREEN);
                                if (historyEnabled) {
                                    historyModel.addToHistory(kassenzeichen);
                                }
                                if (isEditmode()) {
                                    final boolean worked = lockDataset();
                                    if (!worked) {
                                        mainApp.enableEditing(false);
                                    }
                                }

                                // Andere Komponenten vom Wechsel der ObjektID in Kenntniss setzen
                                fireKassenzeichenChanged(kassenzeichenData.getKassenzeichen());

                                // Farbe der Applikation entsprechend der Sperre setzen
                                mainApp.refreshLeftTitleBarColor();
                            }
                        } catch (SQLException sqlEx) {
                            log.error("Fehler bei der Suche nach Kassenzeichen!", sqlEx);
                            flashSearchField(java.awt.Color.RED);
                        }
                    }
                };
            t.setPriority(Thread.NORM_PRIORITY);
            t.start();
        } else {
            JOptionPane.showMessageDialog(
                mainApp,
                "Das Kassenzeichen kann nur gewechselt werden wenn alle \u00C4nderungen gespeichert oder verworfen worden sind.",
                "Wechseln nicht m\u00F6glich",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    public boolean changesPending() {
        if (kassenzeichenData == null) {
            return false;
        } else {
            return kassenzeichenData.hasChanged();
        }
    }
    @Override
    public void enableEditing(final boolean b) {
        if (log.isDebugEnabled()) {
            log.debug("enableEditing(" + b + ")");
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
        lockNonce = "VERDIS:" + System.currentTimeMillis();
        if (log.isDebugEnabled()) {
            log.debug("lockNonce:" + lockNonce);
        }
        try {
            Statement stmnt = connection.createStatement();
            ResultSet rs = stmnt.executeQuery(
                    "select class_id,object_id,user_string,additional_info from cs_locks where class_id="
                            + Main.KASSENZEICHEN_CLASS_ID
                            + " and object_id="
                            + object_id);
            if (!rs.next()) {
                rs.close();
                // Kein Eintragvorhanden. Eintrag schreiben
                stmnt = connection.createStatement();
                final String locker = "insert into cs_locks (class_id,object_id,user_string,additional_info) values ("
                            + Main.KASSENZEICHEN_CLASS_ID + "," + object_id + ",'" + userString + "','" + lockNonce
                            + "')";
                if (log.isDebugEnabled()) {
                    log.debug("lockDataset: " + locker);
                }
                stmnt.executeUpdate(locker);
                stmnt.close();
                // Sperreintrag geschrieben. Jetzt wird noch \u00FCberpr\u00FCft ob in der zwischenzeit noch jemnad
                // einen Sperreintrag geschrieben hat
                stmnt = connection.createStatement();
                rs = stmnt.executeQuery("select count(*) from cs_locks where class_id=" + Main.KASSENZEICHEN_CLASS_ID
                                + " and object_id=" + object_id);
                if (!rs.next()) {
                    log.fatal("select count(*) hat nichts zur\u00FCckgeliefert.");
                    return false;
                } else {
                    final int count = rs.getInt(1);
                    if (count > 1) {
                        final JFrame t = mainApp;
                        new Thread() {

                            {
                                start();
                            }

                            @Override
                            public void run() {
                                JOptionPane.showMessageDialog(
                                    t,
                                    "Es wurde gleichzeitig versucht einen Datensatz zu sperren. Der kl\u00FCgere gibt nach ;-)",
                                    "Sperren fehlgeschlagen",
                                    JOptionPane.WARNING_MESSAGE);
                            }
                        };
                        stmnt = connection.createStatement();
                        final int ret = stmnt.executeUpdate("delete from cs_locks where class_id="
                                        + Main.KASSENZEICHEN_CLASS_ID + " and object_id=" + object_id
                                        + " and additional_info='" + lockNonce + "'");
                        stmnt.close();
                        if (ret != 1) {
                            log.warn("Kassenzeichen " + object_id
                                        + " konnte nicht entsperrt werden. R\u00FCckgabewert des DeleteStmnts:" + ret);
                        }

                        return false;
                    } else {
                        return true;
                    }
                }
            } else {
                final JFrame t = mainApp;
                final String user = rs.getString(3);
                rs.close();
                new Thread() {

                    {
                        start();
                    }

                    @Override
                    public void run() {
                        JOptionPane.showMessageDialog(
                            t,
                            "Der Datensatz wird schon vom Benutzer "
                                    + user
                                    + " zum Ver\u00E4ndern gesperrt",
                            "Kein Editieren m\u00F6glich",
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                };
                return false;
            }
        } catch (Exception e) {
            log.error("SQL Fehler beim Sperren", e);
            return false;
        }
    }
    /**
     * DOCUMENT ME!
     *
     * @param  object_id  DOCUMENT ME!
     */
    public void unlockDataset(final String object_id) {
        final String sql = "delete from cs_locks where class_id=" + Main.KASSENZEICHEN_CLASS_ID + " and object_id="
                    + object_id + " and additional_info='" + lockNonce + "'";
        log.info("unlockDataset: " + sql);
        try {
            final Statement stmnt = connection.createStatement();
            final int ret = stmnt.executeUpdate(sql);
            stmnt.close();
            if (ret != 1) {
                log.fatal("Kassenzeichen " + object_id
                            + " konnte nicht entsperrt werden. R\u00FCckgabewert des DeleteStmnts:" + ret + "(" + sql
                            + ")");
            }
        } catch (Exception e) {
            log.fatal("SQL Fehler beim Entsperren (Statement=" + sql + ")", e);
        }
    }
    @Override
    public boolean lockDataset() {
        final String object_id = txtKassenzeichen.getText().trim();
        return lockDataset(object_id);
    }
    @Override
    public void unlockDataset() {
        final String object_id = txtKassenzeichen.getText().trim();
        if ((object_id != null) && (object_id.length() > 0)) {
            unlockDataset(object_id);
        }
    }

    @Override
    public void addStoreChangeStatements(final Vector v) {
        v.add(kassenzeichenData.createUpdateAction());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  s  DOCUMENT ME!
     */
    public void setUserString(final String s) {
        userString = s;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  l  DOCUMENT ME!
     */
    public void addKassenzeichenChangedListener(final KassenzeichenChangedListener l) {
        kassenzeichenChangedListeners.add(l);
    }
    /**
     * DOCUMENT ME!
     *
     * @param  l  DOCUMENT ME!
     */
    public void removeKassenzeichenChangedListener(final KassenzeichenChangedListener l) {
        kassenzeichenChangedListeners.remove(l);
    }
    /**
     * DOCUMENT ME!
     *
     * @param  kz  DOCUMENT ME!
     */
    public void fireKassenzeichenChanged(final String kz) {
        final java.util.Iterator it = kassenzeichenChangedListeners.iterator();
        while (it.hasNext()) {
            try {
                final KassenzeichenChangedListener kcl = (KassenzeichenChangedListener)it.next();
                kcl.kassenzeichenChanged(kz);
            } catch (java.lang.ClassCastException cce) {
                log.error("KassenzeichenChangedListener nicht vom richtigen Typ.", cce);
            }
        }
    }

    @Override
    public void historyChanged() {
        if ((historyModel != null) && (historyModel.getCurrentElement() != null)) {
            if (log.isDebugEnabled()) {
                log.debug("historyChanged:" + historyModel.getCurrentElement().toString());
            }
            if ((historyModel.getCurrentElement() != null)
                        && (!(historyModel.getCurrentElement().equals(txtSearch.getText())))) {
                txtSearch.setText(historyModel.getCurrentElement().toString());
//            new Thread(new Runnable() {
//                public void run() {
                gotoKassenzeichen(txtSearch.getText(), false);
//                }
//            }).start();
            }
        }
    }

    @Override
    public void forwardStatusChanged() {
    }

    @Override
    public void backStatusChanged() {
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isShrinked() {
        return !(panKZValues.isVisible());
    }
    /**
     * DOCUMENT ME!
     *
     * @param  shrink  DOCUMENT ME!
     */
    public void setShrinked(final boolean shrink) {
        if (log.isDebugEnabled()) {
            log.debug("setShrinked " + shrink);
        }
        panKZValues.setVisible(!shrink);
        sepTitle1.setVisible(!shrink);
        panFill.setVisible(shrink);
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
    public String getRequestForSelectionFlaeche() {
        return requestForSelectionFlaeche;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  requestForSelectionFlaeche  DOCUMENT ME!
     */
    public void setRequestForSelectionFlaeche(final String requestForSelectionFlaeche) {
        this.requestForSelectionFlaeche = requestForSelectionFlaeche;
    }
}
