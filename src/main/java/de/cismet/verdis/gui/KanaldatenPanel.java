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

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import java.io.IOException;

import java.sql.Date;

import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import de.cismet.cids.custom.util.BindingValidationSupport;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;

import de.cismet.cids.editors.DefaultBindableReferenceCombo;

import de.cismet.tools.gui.StaticSwingTools;

import de.cismet.verdis.CidsAppBackend;
import de.cismet.verdis.EditModeListener;

import de.cismet.verdis.commons.constants.BefreiungerlaubnisPropertyConstants;
import de.cismet.verdis.commons.constants.KanalanschlussPropertyConstants;
import de.cismet.verdis.commons.constants.VerdisMetaClassConstants;

import de.cismet.verdis.gui.befreiungerlaubnis.BefreiungerlaubnisTableModel;
import de.cismet.verdis.gui.befreiungerlaubnis.BefreiungerlaubnisTablePanel;
import de.cismet.verdis.gui.befreiungerlaubnis.NewBefreiungerlaubnisDialog;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten.hell@cismet.de
 * @version  $Revision$, $Date$
 */
public class KanaldatenPanel extends javax.swing.JPanel implements CidsBeanStore, EditModeListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final String STRING_CUT = "cut";
    private static final String STRING_COPY = "copy";
    private static final String STRING_PASTE = "paste";
    private static final String STRING_DELETE = "delete";
    private static final String SEPARATOR_BE = "\n";
    private static final String SEPARATOR_BE_DATA = "\t";
    private static final KeyStroke KEYSTROKE_CUT = KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK);
    private static final KeyStroke KEYSTROKE_COPY = KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK);
    private static final KeyStroke KEYSTROKE_PASTE = KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK);
    private static final KeyStroke KEYSTROKE_DELETE = javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);

    //~ Instance fields --------------------------------------------------------

    private boolean editmode = false;
    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private boolean isClipboardBECutPasted = true;
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
    private javax.swing.JButton cmdAddBefreiungerlaubnis;
    private javax.swing.JButton cmdDeleteBefreiungerlaubnis;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JLabel lblAngeschlossen;
    private javax.swing.JLabel lblBE;
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
    private javax.swing.JMenuItem mniCopy;
    private javax.swing.JMenuItem mniCut;
    private javax.swing.JMenuItem mniDelete;
    private javax.swing.JMenuItem mniPaste;
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
                        .getVerdisMetaClass(VerdisMetaClassConstants.MC_ANSCHLUSSSTATUS));
            ((DefaultBindableReferenceCombo)cboMKSangeschlossen).setMetaClass(CidsAppBackend.getInstance()
                        .getVerdisMetaClass(VerdisMetaClassConstants.MC_ANSCHLUSSSTATUS));
            ((DefaultBindableReferenceCombo)cboRKangeschlossen).setMetaClass(CidsAppBackend.getInstance()
                        .getVerdisMetaClass(VerdisMetaClassConstants.MC_ANSCHLUSSSTATUS));
            ((DefaultBindableReferenceCombo)cboSKangeschlossen).setMetaClass(CidsAppBackend.getInstance()
                        .getVerdisMetaClass(VerdisMetaClassConstants.MC_ANSCHLUSSSTATUS));
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
//        if (tblBE.getModel() instanceof BefreiungerlaubnisTableModel) {
//            ((BefreiungerlaubnisTableModel) tblBE.getModel()).removeAll();
//        }
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
        cmdAddBefreiungerlaubnis.setEnabled(editable);
        chkSGentleerung.setEnabled(editable && chkSGvorhanden.isSelected());
        chkKKAentleerung.setEnabled(editable && chkKKAvorhanden.isSelected());
        cboRKangeschlossen.setEnabled(editable && chkRKvorhanden.isSelected());
        cboMKRangeschlossen.setEnabled(editable && chkMKRvorhanden.isSelected());
        cboMKSangeschlossen.setEnabled(editable && chkMKSvorhanden.isSelected());
        cboSKangeschlossen.setEnabled(editable && chkSKvorhanden.isSelected());
//        befreiungenTable1.setEnabled(editable);
        updateClipboardMenu();
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
     * DOCUMENT ME!
     */
    private void updateClipboardMenu() {
        if (log.isDebugEnabled()) {
            log.debug("updateClipboardMenu()");
        }

        final boolean isClipboardEmpty = getClipboardBEs().isEmpty();
        final boolean isSelectionEmpty = befreiungerlaubnisTablePanel1.getTable()
                    .getSelectionModel()
                    .isSelectionEmpty();

        // popupmenu aktualisieren
        mniPaste.setEnabled(!isClipboardEmpty);
        mniCut.setEnabled(!isSelectionEmpty);
        mniCopy.setEnabled(!isSelectionEmpty);
        mniDelete.setEnabled(!isSelectionEmpty);

        // deletebutton aktualisieren
        cmdDeleteBefreiungerlaubnis.setEnabled(!isSelectionEmpty);
    }

    /**
     * DOCUMENT ME!
     */
    private void deleteSelectedBE() {
        if (log.isDebugEnabled()) {
            log.debug("deleteBE()");
        }

        // nur um Editmodus reagieren
        if (isEditmode()) {
            final List<CidsBean> selectedBefreiungen = getSelectedBE();
            for (final CidsBean selectedBefreiung : selectedBefreiungen) {
                try {
                    if (log.isDebugEnabled()) {
                        log.debug("selectedBefreiung: " + selectedBefreiung);
                    }
                    getBefreiungerlaubnisTablePanel().getTable().removeBean(selectedBefreiung);
                } catch (Exception ex) {
                    log.error("Fehler beim Löschen einer Befreiung", ex);
                }
            }

            // Menus aktualisieren
            updateClipboardMenu();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean cutSelectedBE() {
        if (log.isDebugEnabled()) {
            log.debug("cutBE()");
        }

        // nur um Editmodus reagieren
        if (isEditmode()) {
            // ausgewählte BE in die Zwischenablage kopieren
            final boolean copySuccessful = copySelectedBE();
            // ging das Kopieren gut?
            if (copySuccessful) {
                // ausgewählte BE löschen
                deleteSelectedBE();
                // festhalten dass ausgeschnittene Daten noch nicht wieder eingefügt wurden
                isClipboardBECutPasted = false;
                // ausscheiden ging gut
                return true;
            } else { // Kopieren ging schief
                return false;
            }
        }
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean copySelectedBE() {
        if (log.isDebugEnabled()) {
            log.debug("copyBE()");
        }

//        // nur um Editmodus reagieren
        if (isEditmode()) {
            // ausgeschnittene Daten noch nicht wieder eingefügt?
            if (!isClipboardBECutPasted && (getClipboardBEs().size() > 0)) {
                // Dialog zum Nachfragen ob ausgeschnittene Daten verworfen werden
                final int answer = JOptionPane.showConfirmDialog(
                        this,
                        "Sie haben zuvor Daten in die Zwischenablage ausgeschnitten und nicht wieder eingefügt.\nWenn Sie jetzt fortfahren, werden diese Daten verworfen !",
                        "Daten verwerfen?",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                // Wenn nicht OK, abbrechen
                if (answer != JOptionPane.OK_OPTION) {
                    return false;
                }
            }

            // Zwischenspeicher vorbereiten
            final StringBuffer clipboardString = new StringBuffer();

            // markierte BE in die Zwischenablage speichern
            final Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            for (final CidsBean befreiung : getSelectedBE()) {
                clipboardString.append(befreiung.getProperty(BefreiungerlaubnisPropertyConstants.PROP__AKTENZEICHEN)
                            + SEPARATOR_BE_DATA
                            + befreiung.getProperty(BefreiungerlaubnisPropertyConstants.PROP__GUELTIG_BIS)
                            + SEPARATOR_BE);
            }
            systemClipboard.setContents(new StringSelection(clipboardString.toString()), null);

            // Menus aktualisieren
            updateClipboardMenu();
            // Kopieren ging gut
            return true;
        }
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private List<CidsBean> getClipboardBEs() {
        final List<CidsBean> befreiungen = new ArrayList<CidsBean>();

        try {
            String data = "";

            final Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            final Transferable transfer = systemClipboard.getContents(null);
            try {
                data = (String)transfer.getTransferData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException ex) {
                if (log.isDebugEnabled()) {
                    log.debug("(String)transfer.getTransferData(DataFlavor.stringFlavor)", ex);
                }
            } catch (IOException ex) {
                if (log.isDebugEnabled()) {
                    log.debug("(String)transfer.getTransferData(DataFlavor.stringFlavor)", ex);
                }
            }

            //
            final String[] befreiungStrings = data.split(SEPARATOR_BE);
            for (final String befreiungString : befreiungStrings) {
                final String[] befreiungData = befreiungString.split(SEPARATOR_BE_DATA);
                if (befreiungData.length == 2) {
                    final CidsBean befreiung = CidsBean.createNewCidsBeanFromTableName(CidsAppBackend.getInstance()
                                    .getDomain(),
                            VerdisMetaClassConstants.MC_BEFREIUNGERLAUBNIS);
                    befreiung.setProperty(BefreiungerlaubnisPropertyConstants.PROP__AKTENZEICHEN, befreiungData[0]);
                    try {
                        befreiung.setProperty(
                            BefreiungerlaubnisPropertyConstants.PROP__GUELTIG_BIS,
                            Date.valueOf(befreiungData[1]));
                    } catch (final Exception ex) {
                        befreiung.setProperty(BefreiungerlaubnisPropertyConstants.PROP__GUELTIG_BIS, null);
                    }
                    befreiungen.add(befreiung);
                }
            }
        } catch (Exception e) {
            log.error("Fehler beim lesen der Befreiungen aus der zwischenablage", e);
        }

        return befreiungen;
    }

    /**
     * DOCUMENT ME!
     */
    private void pasteBE() {
        if (log.isDebugEnabled()) {
            log.debug("pasteBE()");
        }

        // nur um Editmodus reagieren
        if (isEditmode()) {
            try {
                for (final CidsBean clipboardBefreiung : getClipboardBEs()) {
                    if (log.isDebugEnabled()) {
                        log.debug("clipboardBefreiung: " + clipboardBefreiung);
                    }

                    final List<CidsBean> list = kassenzeichenBean.getBeanCollectionProperty(
                            VerdisMetaClassConstants.MC_KANALANSCHLUSS
                                    + "."
                                    + KanalanschlussPropertyConstants.PROP__BEFREIUNGENUNDERLAUBNISSE);
                    list.add(clipboardBefreiung);
                }

                // ausgeschnittene Daten wurden wieder eingefügt
                if (!isClipboardBECutPasted) {
                    isClipboardBECutPasted = true;
                }

                // Menus aktualisieren
                updateClipboardMenu();
            } catch (Exception e) {
                log.error("Fehler beim Einfügen von Befreiungen aus der Zwischenablage", e);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private List<CidsBean> getSelectedBE() {
        return new ArrayList<CidsBean>(befreiungerlaubnisTablePanel1.getTable().getSelectedBeans());
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jPopupMenu1 = new javax.swing.JPopupMenu();
        mniCut = new javax.swing.JMenuItem();
        mniCopy = new javax.swing.JMenuItem();
        mniPaste = new javax.swing.JMenuItem();
        mniDelete = new javax.swing.JMenuItem();
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
        jPanel3 = new javax.swing.JPanel();
        lblBE = new javax.swing.JLabel();
        cmdAddBefreiungerlaubnis = new javax.swing.JButton();
        cmdDeleteBefreiungerlaubnis = new javax.swing.JButton();
        befreiungerlaubnisTablePanel1 = new de.cismet.verdis.gui.befreiungerlaubnis.BefreiungerlaubnisTablePanel();

        mniCut.setAction(new CutAction());
        mniCut.setAccelerator(KEYSTROKE_CUT);
        mniCut.setMnemonic('A');
        mniCut.setText("Ausschneiden");
        mniCut.setEnabled(false);
        jPopupMenu1.add(mniCut);

        mniCopy.setAction(new CopyAction());
        mniCopy.setAccelerator(KEYSTROKE_COPY);
        mniCopy.setMnemonic('K');
        mniCopy.setText("Kopieren");
        mniCopy.setEnabled(false);
        jPopupMenu1.add(mniCopy);

        mniPaste.setAction(new PasteAction());
        mniPaste.setAccelerator(KEYSTROKE_PASTE);
        mniPaste.setMnemonic('E');
        mniPaste.setText("Einfügen");
        mniPaste.setEnabled(false);
        jPopupMenu1.add(mniPaste);

        mniDelete.setAction(new DeleteAction());
        mniDelete.setAccelerator(KEYSTROKE_DELETE);
        mniDelete.setMnemonic('L');
        mniDelete.setText("Löschen");
        mniDelete.setEnabled(false);
        jPopupMenu1.add(mniDelete);

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

        cboRKangeschlossen.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cboRKangeschlossenActionPerformed(evt);
                }
            });
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

        cboMKRangeschlossen.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cboMKRangeschlossenActionPerformed(evt);
                }
            });
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

        cboMKSangeschlossen.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cboMKSangeschlossenActionPerformed(evt);
                }
            });
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

        jPanel3.setLayout(new java.awt.GridBagLayout());

        lblBE.setText("Befreiung / Erlaubnis");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(lblBE, gridBagConstraints);

        cmdAddBefreiungerlaubnis.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/add.png"))); // NOI18N
        cmdAddBefreiungerlaubnis.setFocusPainted(false);
        cmdAddBefreiungerlaubnis.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdAddBefreiungerlaubnisActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        jPanel3.add(cmdAddBefreiungerlaubnis, gridBagConstraints);

        cmdDeleteBefreiungerlaubnis.setAction(new DeleteAction());
        cmdDeleteBefreiungerlaubnis.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/remove.png"))); // NOI18N
        cmdDeleteBefreiungerlaubnis.setEnabled(false);
        cmdDeleteBefreiungerlaubnis.setFocusPainted(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel3.add(cmdDeleteBefreiungerlaubnis, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel3.add(befreiungerlaubnisTablePanel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 0, 0);
        jPanel1.add(jPanel3, gridBagConstraints);

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
     * @param  evt  DOCUMENT ME!
     */
    private void cboMKSangeschlossenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cboMKSangeschlossenActionPerformed
    }                                                                                       //GEN-LAST:event_cboMKSangeschlossenActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cboMKRangeschlossenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cboMKRangeschlossenActionPerformed
    }                                                                                       //GEN-LAST:event_cboMKRangeschlossenActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cboRKangeschlossenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cboRKangeschlossenActionPerformed
    }                                                                                      //GEN-LAST:event_cboRKangeschlossenActionPerformed

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
    private void cmdAddBefreiungerlaubnisActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdAddBefreiungerlaubnisActionPerformed
        try {
            final List<CidsBean> list = kassenzeichenBean.getBeanCollectionProperty(
                    VerdisMetaClassConstants.MC_KANALANSCHLUSS
                            + "."
                            + KanalanschlussPropertyConstants.PROP__BEFREIUNGENUNDERLAUBNISSE);

            final CidsBean newBefreiungBean = CidsBean.createNewCidsBeanFromTableName(CidsAppBackend.getInstance()
                            .getDomain(),
                    VerdisMetaClassConstants.MC_BEFREIUNGERLAUBNIS);

            final JDialog dialog = new NewBefreiungerlaubnisDialog(StaticSwingTools.getParentFrame(this),
                    newBefreiungBean,
                    list);
            dialog.addWindowListener(new WindowAdapter() {

                    @Override
                    public void windowClosed(final WindowEvent e) {
                        ((BefreiungerlaubnisTableModel)befreiungerlaubnisTablePanel1.getTable().getModel())
                                .fireTableDataChanged();
                    }
                });
            StaticSwingTools.showDialog(dialog);
        } catch (Exception e) {
            log.error("Fehler beim Hinzufügen einer neuen Befreiung", e);
        }
    } //GEN-LAST:event_cmdAddBefreiungerlaubnisActionPerformed

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

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class CutAction extends AbstractAction {

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param  e  DOCUMENT ME!
         */
        @Override
        public void actionPerformed(final ActionEvent e) {
            cutSelectedBE();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class CopyAction extends AbstractAction {

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param  e  DOCUMENT ME!
         */
        @Override
        public void actionPerformed(final ActionEvent e) {
            copySelectedBE();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class PasteAction extends AbstractAction {

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param  e  DOCUMENT ME!
         */
        @Override
        public void actionPerformed(final ActionEvent e) {
            pasteBE();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class DeleteAction extends AbstractAction {

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param  e  DOCUMENT ME!
         */
        @Override
        public void actionPerformed(final ActionEvent e) {
            deleteSelectedBE();
        }
    }
}
