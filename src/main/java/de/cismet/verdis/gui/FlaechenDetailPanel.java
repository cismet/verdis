/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * FlaechenDetailPanel.java
 *
 * Created on 5. Januar 2005, 14:02
 */
package de.cismet.verdis.gui;
import edu.umd.cs.piccolo.PCanvas;

import java.awt.*;

import java.util.Collection;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.*;

import de.cismet.validation.Validatable;
import de.cismet.validation.Validator;

import de.cismet.verdis.data.*;

/**
 * DOCUMENT ME!
 *
 * @author   hell
 * @version  $Revision$, $Date$
 */
public class FlaechenDetailPanel extends javax.swing.JPanel implements HyperlinkListener {

    //~ Instance fields --------------------------------------------------------

    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private Color defaultDisabledForeground;
    private Flaeche model;

    private Validator valTxtBezeichnung;
    private Validator valTxtGroesseGrafik;
    private Validator valTxtGroesseKorrektur;
    private Validator valTxtAnteil;
    private Validator valTxtErfassungsdatum;
    private Validator valTxtVeranlagungsdatum;
    private Validator valTxtFEB_ID;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.cismet.cismap.commons.gui.SimpleBackgroundedJPanel bpanFlDetails;
    private javax.swing.JComboBox cboAnschlussgrad;
    private javax.swing.JComboBox cboBeschreibung;
    private javax.swing.JComboBox cboFlaechenart;
    private javax.swing.JCheckBox chkSperre;
    private javax.swing.JEditorPane edtQuer;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JLabel lblAnschlussgrad;
    private javax.swing.JLabel lblAnteil;
    private javax.swing.JLabel lblBemerkung;
    private javax.swing.JLabel lblBeschreibung;
    private javax.swing.JLabel lblBezeichnung;
    private javax.swing.JLabel lblErfassungsadtum;
    private javax.swing.JLabel lblFEB_ID;
    private javax.swing.JLabel lblFlaechenart;
    private javax.swing.JLabel lblGroesseGrafik;
    private javax.swing.JLabel lblGroesseKorrektur;
    private javax.swing.JLabel lblSperre;
    private javax.swing.JLabel lblTeileigentumQuerverweise;
    private javax.swing.JLabel lblVeranlagungsdatum;
    private javax.swing.JPanel panFlDetails;
    private javax.swing.JScrollPane scpBemerkung;
    private javax.swing.JScrollPane scpQuer;
    private javax.swing.JTabbedPane tbpDetails;
    private javax.swing.JTextField txtAnteil;
    private javax.swing.JTextArea txtBemerkung;
    private javax.swing.JTextField txtBezeichnung;
    private javax.swing.JTextField txtErfassungsdatum;
    private javax.swing.JTextField txtFEB_ID;
    private javax.swing.JTextField txtGroesseGrafik;
    private javax.swing.JTextField txtGroesseKorrektur;
    private javax.swing.JTextField txtSperreBemerkung;
    private javax.swing.JTextField txtVeranlagungsdatum;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form FlaechenDetailPanel.
     */
    public FlaechenDetailPanel() {
        initComponents();

        // txtBezeichnung.setDocument(model.getBezeichnungsModel());
        defaultDisabledForeground = (Color)(UIManager.getDefaults().get("ComboBox.disabledForeground"));
        valTxtBezeichnung = new Validator(txtBezeichnung);

        valTxtGroesseGrafik = new Validator(txtGroesseGrafik);
        valTxtGroesseKorrektur = new Validator(txtGroesseKorrektur);
        valTxtAnteil = new Validator(txtAnteil);
        valTxtErfassungsdatum = new Validator(txtErfassungsdatum);
        valTxtVeranlagungsdatum = new Validator(txtVeranlagungsdatum);
        valTxtFEB_ID = new Validator(txtFEB_ID);
        setEnabled(false);

        edtQuer.addHyperlinkListener(this);
        scpQuer.getViewport().setOpaque(false);
        clearDetails();
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void hyperlinkUpdate(final HyperlinkEvent e) {
        final Thread t = new Thread() {

                @Override
                public void run() {
                    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                        try {
                            Main.THIS.getKzPanel().gotoKassenzeichen(e.getDescription());
                        } catch (Exception ex) {
                            log.error("Fehler im Hyperlinken", ex);
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
     * @param  pc  DOCUMENT ME!
     */
    public void setPCanvas(final PCanvas pc) {
        this.bpanFlDetails.setPCanvas(pc);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  flaeche  DOCUMENT ME!
     */
    public void setDetails(final de.cismet.verdis.data.Flaeche flaeche) {
        if (flaeche != null) {
            model = flaeche;

            // Bezeichnung
            txtBezeichnung.setDocument((PlainDocument)model.getBezeichnungsModel());
            valTxtBezeichnung.reSetValidator((Validatable)model.getBezeichnungsModel());

            // Gr��e Grafik
            txtGroesseGrafik.setDocument(model.getGr_GrafikModel());
            valTxtGroesseGrafik.reSetValidator((Validatable)model.getGr_GrafikModel());
            // log.debug("setDetails");

            // Gr��e Korrektur
            txtGroesseKorrektur.setDocument(model.getGr_KorrekturModel());
            valTxtGroesseKorrektur.reSetValidator((Validatable)model.getGr_KorrekturModel());

            // Fl�chenart
            // Anschlu�grad
            UIManager.put("ComboBox.disabledForeground", Color.black);
            cboAnschlussgrad.repaint();
            cboFlaechenart.repaint();
            cboBeschreibung.repaint();

            cboAnschlussgrad.setModel(model.getGradModel());
            cboFlaechenart.setModel(model.getArtModel());
            cboBeschreibung.setModel(model.getBeschreibungsmodel());

            // Anteil
            txtAnteil.setDocument(model.getAnteilModel());
            valTxtAnteil.reSetValidator((Validatable)model.getAnteilModel());

            // Erfassungsadtum
            txtErfassungsdatum.setDocument(model.getErfassungsdatumModel());
            valTxtErfassungsdatum.reSetValidator((Validatable)model.getErfassungsdatumModel());

            // Veranlagungsdatum
            txtVeranlagungsdatum.setDocument(model.getVeranlagungsdatumModel());
            valTxtVeranlagungsdatum.reSetValidator((Validatable)model.getVeranlagungsdatumModel());

            // Bemerkung
            txtBemerkung.setDocument(model.getBemerkungsModel());

            // Sperre
            // log.debug("Sperre isEnabled()="+chkSperre.isEnabled());
            final boolean b = chkSperre.isEnabled();
            this.chkSperre.setModel(model.getSperrenModel());
            chkSperre.setEnabled(b);

            // FEB ID
            txtFEB_ID.setDocument(model.getFeb_IdModel());
            valTxtFEB_ID.reSetValidator((Validatable)model.getFeb_IdModel());

            // TODO
            // Sperrenbemerkung
            txtSperreBemerkung.setDocument(model.getBem_sperreModel());

            if (flaeche.isSperre()) {
            } else {
//                this.lblSperreBemerkung.setText("");
            }

            if (flaeche.getGeometry() != null) {
                bpanFlDetails.setBackgroundEnabled(true);
            } else {
                bpanFlDetails.setBackgroundEnabled(false);
            }

            final Thread t = new Thread() {

                    @Override
                    public void run() {
                        // TeileigentumCrossReferences
                        if (flaeche.getTeileigentumCrossReferences() != null) {
                            String html = "<html><body><center>";
                            final Collection c = flaeche.getTeileigentumCrossReferences();
                            for (final Object o : c) {
                                final String link = o.toString();
                                html += "<a href=\"" + link + "\"><font size=\"-2\">" + link + "</font></a><br>";
                            }
                            html += "</center></body></html>";
                            final String finalHtml = html;
                            EventQueue.invokeLater(new Runnable() {

                                    @Override
                                    public void run() {
                                        lblTeileigentumQuerverweise.setVisible(true);
                                        edtQuer.setVisible(true);
                                        scpQuer.setVisible(true);
                                        edtQuer.setText(finalHtml);
                                        edtQuer.setCaretPosition(0);
                                    }
                                });
                        } else {
                            EventQueue.invokeLater(new Runnable() {

                                    @Override
                                    public void run() {
                                        edtQuer.setText("");
                                        lblTeileigentumQuerverweise.setVisible(false);
                                        edtQuer.setVisible(false);
                                        scpQuer.setVisible(false);
                                    }
                                });
                        }
                    }
                };
            t.setPriority(Thread.NORM_PRIORITY);
            t.start();
        } else {
            bpanFlDetails.setBackgroundEnabled(false);
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void clearDetails() {
        bpanFlDetails.setBackgroundEnabled(false);

        // leeres Document das keine �nderungen annimmt selbst wenn die Textfelder mal
        // nicht disabled sind
        final PlainDocument empty = new PlainDocument() {

                @Override
                public void insertString(final int offset, final String string, final AttributeSet attributes)
                        throws BadLocationException {
                }
            };

        final DefaultComboBoxModel comboEmpty = new DefaultComboBoxModel();
        UIManager.put("ComboBox.disabledForeground", defaultDisabledForeground);
        txtBezeichnung.setDocument(empty);

        txtBemerkung.setDocument(empty);
        txtAnteil.setDocument(empty);
        txtErfassungsdatum.setDocument(empty);
        txtVeranlagungsdatum.setDocument(empty);
        txtGroesseGrafik.setDocument(empty);
        txtGroesseKorrektur.setDocument(empty);
        txtSperreBemerkung.setDocument(empty);
        chkSperre.setModel(new DefaultButtonModel());
        chkSperre.setEnabled(cboAnschlussgrad.isEnabled());

        cboAnschlussgrad.setModel(comboEmpty);
        cboFlaechenart.setModel(comboEmpty);
        cboBeschreibung.setModel(comboEmpty);

        txtFEB_ID.setDocument(empty);
        cboAnschlussgrad.repaint();
        cboFlaechenart.repaint();
        cboBeschreibung.repaint();
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                repaint();
//            }
//        });
        repaint();
        edtQuer.setText("");
        lblTeileigentumQuerverweise.setVisible(false);
        edtQuer.setVisible(false);
        scpQuer.setVisible(false);

//        revalidate();
    }
    @Override
    public void setEnabled(final boolean b) {
        txtAnteil.setEditable(b);
        txtBemerkung.setEditable(b);
        txtBezeichnung.setEditable(b);
        txtErfassungsdatum.setEditable(b);
        txtFEB_ID.setEditable(b);
        txtGroesseGrafik.setEditable(b);
        txtGroesseKorrektur.setEditable(b);
        txtVeranlagungsdatum.setEditable(b);
        chkSperre.setEnabled(b);
        cboAnschlussgrad.setEnabled(b);
        cboFlaechenart.setEnabled(b);
        cboBeschreibung.setEnabled(b);
        // Opacity
        txtAnteil.setOpaque(b);
        txtBemerkung.setOpaque(b);
        txtBezeichnung.setOpaque(b);
        txtErfassungsdatum.setOpaque(b);
        txtFEB_ID.setOpaque(b);
        txtGroesseGrafik.setOpaque(b);
        txtGroesseKorrektur.setOpaque(b);
        txtVeranlagungsdatum.setOpaque(b);
        chkSperre.setOpaque(b);

        cboAnschlussgrad.setOpaque(b);
        cboFlaechenart.setOpaque(b);
        cboBeschreibung.setOpaque(b);

        this.scpBemerkung.setOpaque(b);
        scpBemerkung.getViewport().setOpaque(b);
        if (b) {
            txtBemerkung.setBackground(java.awt.Color.white);
        } else {
            txtBemerkung.setBackground(this.getBackground());
        }
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                repaint();
//            }
//        });
        repaint();
    }
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        tbpDetails = new javax.swing.JTabbedPane();
        bpanFlDetails = new de.cismet.cismap.commons.gui.SimpleBackgroundedJPanel();
        panFlDetails = new javax.swing.JPanel();
        lblBezeichnung = new javax.swing.JLabel();
        lblGroesseGrafik = new javax.swing.JLabel();
        lblGroesseKorrektur = new javax.swing.JLabel();
        lblFlaechenart = new javax.swing.JLabel();
        lblAnschlussgrad = new javax.swing.JLabel();
        txtBezeichnung = new javax.swing.JTextField();
        txtGroesseGrafik = new javax.swing.JTextField();
        txtGroesseKorrektur = new javax.swing.JTextField();
        lblAnteil = new javax.swing.JLabel();
        lblErfassungsadtum = new javax.swing.JLabel();
        lblVeranlagungsdatum = new javax.swing.JLabel();
        lblBemerkung = new javax.swing.JLabel();
        txtAnteil = new javax.swing.JTextField();
        txtErfassungsdatum = new javax.swing.JTextField();
        txtVeranlagungsdatum = new javax.swing.JTextField();
        cboFlaechenart = new javax.swing.JComboBox();
        cboAnschlussgrad = new javax.swing.JComboBox();
        scpBemerkung = new javax.swing.JScrollPane();
        txtBemerkung = new javax.swing.JTextArea();
        lblSperre = new javax.swing.JLabel();
        chkSperre = new javax.swing.JCheckBox();
        txtFEB_ID = new javax.swing.JTextField();
        lblFEB_ID = new javax.swing.JLabel();
        txtSperreBemerkung = new javax.swing.JTextField();
        lblTeileigentumQuerverweise = new javax.swing.JLabel();
        scpQuer = new javax.swing.JScrollPane();
        edtQuer = new javax.swing.JEditorPane();
        jPanel1 = new javax.swing.JPanel();
        lblBeschreibung = new javax.swing.JLabel();
        cboBeschreibung = new javax.swing.JComboBox();

        jScrollPane1.setViewportView(jTextPane1);

        setLayout(new java.awt.BorderLayout());

        tbpDetails.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));

        bpanFlDetails.setLayout(new java.awt.BorderLayout());

        panFlDetails.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panFlDetails.setMinimumSize(new java.awt.Dimension(300, 300));
        panFlDetails.setOpaque(false);
        panFlDetails.setPreferredSize(new java.awt.Dimension(300, 300));
        panFlDetails.setLayout(new java.awt.GridBagLayout());

        lblBezeichnung.setText("Fl\u00E4chenbezeichnung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        panFlDetails.add(lblBezeichnung, gridBagConstraints);

        lblGroesseGrafik.setText("Gr\u00F6\u00DFe (Grafik)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        panFlDetails.add(lblGroesseGrafik, gridBagConstraints);

        lblGroesseKorrektur.setText("Gr\u00F6\u00DFe (Korrektur)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        panFlDetails.add(lblGroesseKorrektur, gridBagConstraints);

        lblFlaechenart.setText("Fl\u00E4chenart");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        panFlDetails.add(lblFlaechenart, gridBagConstraints);

        lblAnschlussgrad.setText("Anschlussgrad");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        panFlDetails.add(lblAnschlussgrad, gridBagConstraints);

        txtBezeichnung.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 2);
        panFlDetails.add(txtBezeichnung, gridBagConstraints);

        txtGroesseGrafik.setOpaque(false);
        txtGroesseGrafik.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    txtGroesseGrafikActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 2);
        panFlDetails.add(txtGroesseGrafik, gridBagConstraints);

        txtGroesseKorrektur.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 2);
        panFlDetails.add(txtGroesseKorrektur, gridBagConstraints);

        lblAnteil.setText("Anteil");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        panFlDetails.add(lblAnteil, gridBagConstraints);

        lblErfassungsadtum.setText("Erfassungsdatum");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        panFlDetails.add(lblErfassungsadtum, gridBagConstraints);

        lblVeranlagungsdatum.setText("Veranlagungsdatum");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        panFlDetails.add(lblVeranlagungsdatum, gridBagConstraints);

        lblBemerkung.setText("Bemerkung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        panFlDetails.add(lblBemerkung, gridBagConstraints);

        txtAnteil.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 2);
        panFlDetails.add(txtAnteil, gridBagConstraints);

        txtErfassungsdatum.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 2);
        panFlDetails.add(txtErfassungsdatum, gridBagConstraints);

        txtVeranlagungsdatum.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 2);
        panFlDetails.add(txtVeranlagungsdatum, gridBagConstraints);

        cboFlaechenart.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 2);
        panFlDetails.add(cboFlaechenart, gridBagConstraints);

        cboAnschlussgrad.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 2);
        panFlDetails.add(cboAnschlussgrad, gridBagConstraints);

        scpBemerkung.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scpBemerkung.setMinimumSize(new java.awt.Dimension(103, 40));
        scpBemerkung.setOpaque(false);
        scpBemerkung.setPreferredSize(new java.awt.Dimension(40, 40));

        txtBemerkung.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 11));
        txtBemerkung.setLineWrap(true);
        txtBemerkung.setRows(2);
        txtBemerkung.setMinimumSize(new java.awt.Dimension(73, 38));
        txtBemerkung.setOpaque(false);
        txtBemerkung.setPreferredSize(new java.awt.Dimension(21, 756));
        scpBemerkung.setViewportView(txtBemerkung);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 2);
        panFlDetails.add(scpBemerkung, gridBagConstraints);

        lblSperre.setText("Sperre");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        panFlDetails.add(lblSperre, gridBagConstraints);

        chkSperre.setForeground(java.awt.Color.red);
        chkSperre.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 0);
        panFlDetails.add(chkSperre, gridBagConstraints);

        txtFEB_ID.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 2);
        panFlDetails.add(txtFEB_ID, gridBagConstraints);

        lblFEB_ID.setText("FEB Id");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        panFlDetails.add(lblFEB_ID, gridBagConstraints);

        txtSperreBemerkung.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.background"));
        txtSperreBemerkung.setEditable(false);
        txtSperreBemerkung.setForeground(java.awt.Color.red);
        txtSperreBemerkung.setBorder(null);
        txtSperreBemerkung.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 2);
        panFlDetails.add(txtSperreBemerkung, gridBagConstraints);

        lblTeileigentumQuerverweise.setText("Querverweise");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        panFlDetails.add(lblTeileigentumQuerverweise, gridBagConstraints);

        scpQuer.setOpaque(false);

        edtQuer.setContentType("text/html");
        edtQuer.setEditable(false);
        edtQuer.setOpaque(false);
        scpQuer.setViewportView(edtQuer);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 2);
        panFlDetails.add(scpQuer, gridBagConstraints);

        jPanel1.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 0.01;
        panFlDetails.add(jPanel1, gridBagConstraints);

        lblBeschreibung.setText("Beschreibung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 5);
        panFlDetails.add(lblBeschreibung, gridBagConstraints);

        cboBeschreibung.setModel(new javax.swing.DefaultComboBoxModel(
                new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 2, 2);
        panFlDetails.add(cboBeschreibung, gridBagConstraints);

        bpanFlDetails.add(panFlDetails, java.awt.BorderLayout.CENTER);

        tbpDetails.addTab("Details", bpanFlDetails);

        add(tbpDetails, java.awt.BorderLayout.CENTER);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void txtGroesseGrafikActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_txtGroesseGrafikActionPerformed
        // TODO add your handling code here:
    } //GEN-LAST:event_txtGroesseGrafikActionPerformed
}
