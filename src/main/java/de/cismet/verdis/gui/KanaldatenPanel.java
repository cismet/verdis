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
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import java.io.IOException;

import java.sql.Connection;
import java.sql.Date;

import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.cismet.cids.custom.util.BindingValidationSupport;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;

import de.cismet.cids.editors.DefaultBindableReferenceCombo;

import de.cismet.tools.gui.StaticSwingTools;

import de.cismet.verdis.CidsAppBackend;
import de.cismet.verdis.EditModeListener;

import de.cismet.verdis.commons.constants.VerdisMetaClassConstants;

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
    private Connection connection;
    private Color myBlue = new java.awt.Color(0, 51, 153);
    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private Main main;
    private boolean isClipboardBECutPasted = true;
    private CidsBean kassenzeichenBean;

    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    private javax.swing.JButton cmdAddBefreiungErlaubnis;
    private javax.swing.JButton cmdDeleteBefreiungErlaubnis;
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
    private javax.swing.JScrollPane scpBE;
    private javax.swing.JTable tblBE;
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
//        if (tblBE.getModel() instanceof BefreiungenModel) {
//            ((BefreiungenModel) tblBE.getModel()).removeAll();
//        }
        visualizeValidity();
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
        cmdAddBefreiungErlaubnis.setEnabled(editable);
        chkSGentleerung.setEnabled(editable && chkSGvorhanden.isSelected());
        chkKKAentleerung.setEnabled(editable && chkKKAvorhanden.isSelected());
        cboRKangeschlossen.setEnabled(editable && chkRKvorhanden.isSelected());
        cboMKRangeschlossen.setEnabled(editable && chkMKRvorhanden.isSelected());
        cboMKSangeschlossen.setEnabled(editable && chkMKSvorhanden.isSelected());
        cboSKangeschlossen.setEnabled(editable && chkSKvorhanden.isSelected());
        tblBE.setEnabled(editable);
        updateClipboardMenu();
        visualizeValidity();
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
    public Main getMain() {
        return main;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  main  DOCUMENT ME!
     */
    public void setMain(final Main main) {
        this.main = main;
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

        final BefreiungenModel m = new BefreiungenModel();
        tblBE.setModel(m);
        m.setCidsBean(cidsBean);
        kassenzeichenBean = cidsBean;
        bindingGroup.unbind();
        bindingGroup.bind();
    }

    /**
     * Inserting Docking Window functionalty (Sebastian) 24.07.07.
     */
    private void visualizeValidity() {
        // TODO UGLY
// if (main != null) {
// if ((kanalanschlussdaten == null) || kanalanschlussdaten.isValid()) {
// // lblTitle.setForeground(Color.white);
// main.setKanalTitleForeground(Color.BLACK);
// } else {
// if (this.isEditmode()) {
// // lblTitle.setForeground(Color.YELLOW);
// main.setKanalTitleForeground(Color.YELLOW);
// } else {
// // lblTitle.setForeground(Color.red);
// main.setKanalTitleForeground(Color.RED);
// }
// }
// }
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
        final boolean isSelectionEmpty = tblBE.getSelectionModel().isSelectionEmpty();

        // popupmenu aktualisieren
        mniPaste.setEnabled(!isClipboardEmpty);
        mniCut.setEnabled(!isSelectionEmpty);
        mniCopy.setEnabled(!isSelectionEmpty);
        mniDelete.setEnabled(!isSelectionEmpty);

        // deletebutton aktualisieren
        cmdDeleteBefreiungErlaubnis.setEnabled(!isSelectionEmpty);
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
                    selectedBefreiung.delete();
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
                clipboardString.append(befreiung.getProperty("aktenzeichen") + SEPARATOR_BE_DATA
                            + befreiung.getProperty("gueltig_bis")
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
                            "befreiungerlaubnis");
                    befreiung.setProperty("aktenzeichen", befreiungData[0]);
                    try {
                        befreiung.setProperty("gueltig_bis", Date.valueOf(befreiungData[1]));
                    } catch (final Exception ex) {
                        befreiung.setProperty("gueltig_bis", null);
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
                            "kanalanschluss.befreiungenunderlaubnisse");
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
        final List<CidsBean> befreiungen = new ArrayList<CidsBean>();
        final List<CidsBean> allBefreiungen = kassenzeichenBean.getBeanCollectionProperty(
                "kanalanschluss.befreiungenunderlaubnisse");
        final int[] rows = tblBE.getSelectedRows();
        for (final int row : rows) {
            befreiungen.add(allBefreiungen.get(row));
        }
        return befreiungen;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jPopupMenu1 = new javax.swing.JPopupMenu();
        mniCut = new javax.swing.JMenuItem();
        mniCopy = new javax.swing.JMenuItem();
        mniPaste = new javax.swing.JMenuItem();
        mniDelete = new javax.swing.JMenuItem();
        panMain = new javax.swing.JPanel();
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
        lblSG = new javax.swing.JLabel();
        lblVorhanden2 = new javax.swing.JLabel();
        lblEntleerung = new javax.swing.JLabel();
        lblKKA = new javax.swing.JLabel();
        chkSGvorhanden = new javax.swing.JCheckBox();
        chkKKAvorhanden = new javax.swing.JCheckBox();
        chkSGentleerung = new javax.swing.JCheckBox();
        chkKKAentleerung = new javax.swing.JCheckBox();
        chkErlaubnisfreieVersickerung = new javax.swing.JCheckBox();
        jSeparator4 = new javax.swing.JSeparator();
        scpBE = new javax.swing.JScrollPane();
        tblBE = new javax.swing.JTable();
        lblBE = new javax.swing.JLabel();
        lblEVG = new javax.swing.JLabel();
        cmdAddBefreiungErlaubnis = new javax.swing.JButton();
        cmdDeleteBefreiungErlaubnis = new javax.swing.JButton();

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

        lblRK.setText("RK");
        lblRK.setToolTipText("Regenwasserkanal");

        lblMKR.setText("MKR");
        lblMKR.setToolTipText("Mischwasserkanal Regen");

        lblMKS.setText("MKS");
        lblMKS.setToolTipText("Mischwasserkanal Schmutz");

        lblSK.setText("SK");
        lblSK.setToolTipText("Schmutzwasserkanal");

        lblVorhanden1.setText("vorh.");
        lblVorhanden1.setToolTipText("vorhanden");

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

        lblAngeschlossen.setText("angeschlossen");

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

        lblSG.setText("SG");
        lblSG.setToolTipText("Sickergrube");

        lblVorhanden2.setText("vorh.");

        lblEntleerung.setText("Entleerung");

        lblKKA.setText("KKA");
        lblKKA.setToolTipText("Kleinkläranlage");

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

        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);

        scpBE.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        scpBE.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mousePressed(final java.awt.event.MouseEvent evt) {
                    scpBEMousePressed(evt);
                }
            });

        tblBE.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {},
                new String[] { "Aktenzeichen", "gültig bis" }) {

                Class[] types = new Class[] { java.lang.String.class, java.lang.String.class };
                boolean[] canEdit = new boolean[] { false, false };

                @Override
                public Class getColumnClass(final int columnIndex) {
                    return types[columnIndex];
                }

                @Override
                public boolean isCellEditable(final int rowIndex, final int columnIndex) {
                    return canEdit[columnIndex];
                }
            });
        tblBE.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mousePressed(final java.awt.event.MouseEvent evt) {
                    tblBEMousePressed(evt);
                }
            });
        scpBE.setViewportView(tblBE);
        // reagieren auf Verändern der Selektion
        tblBE.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                @Override
                public void valueChanged(final ListSelectionEvent e) {
                    if (log.isDebugEnabled()) {
                        log.debug("selectionChanged");
                    }
                    updateClipboardMenu();
                }
            });

        // registrieren von STRG+X für Ausschneiden
        tblBE.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KEYSTROKE_CUT, STRING_CUT);
        tblBE.getActionMap().put(STRING_CUT, new CutAction());

        // registrieren von STRG+C für Kopieren
        tblBE.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KEYSTROKE_COPY, STRING_COPY);
        tblBE.getActionMap().put(STRING_COPY, new CopyAction());

        // registrieren von STRG+V für Einfügen
        tblBE.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KEYSTROKE_PASTE, STRING_PASTE);
        tblBE.getActionMap().put(STRING_PASTE, new PasteAction());

        // registrieren von ENTF für Löschen
        tblBE.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KEYSTROKE_DELETE, STRING_DELETE);
        tblBE.getActionMap().put(STRING_DELETE, new DeleteAction());

        lblBE.setText("Befreiung / Erlaubnis");

        lblEVG.setText("EVG");
        lblEVG.setToolTipText("Erlaubnisfreie Versickerung");

        cmdAddBefreiungErlaubnis.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/add.png"))); // NOI18N
        cmdAddBefreiungErlaubnis.setFocusPainted(false);
        cmdAddBefreiungErlaubnis.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdAddBefreiungErlaubnisActionPerformed(evt);
                }
            });

        cmdDeleteBefreiungErlaubnis.setAction(new DeleteAction());
        cmdDeleteBefreiungErlaubnis.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/remove.png"))); // NOI18N
        cmdDeleteBefreiungErlaubnis.setEnabled(false);
        cmdDeleteBefreiungErlaubnis.setFocusPainted(false);

        final org.jdesktop.layout.GroupLayout panMainLayout = new org.jdesktop.layout.GroupLayout(panMain);
        panMain.setLayout(panMainLayout);
        panMainLayout.setHorizontalGroup(
            panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                panMainLayout.createSequentialGroup().addContainerGap().add(
                    panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                        panMainLayout.createSequentialGroup().add(lblKKA).add(121, 121, 121)).add(
                        org.jdesktop.layout.GroupLayout.TRAILING,
                        panMainLayout.createSequentialGroup().add(
                            panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING).add(
                                panMainLayout.createSequentialGroup().add(
                                    panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                                        lblRK).add(lblMKR).add(lblMKS).add(lblSK).add(lblSG).add(
                                        panMainLayout.createSequentialGroup().add(lblEVG).add(12, 12, 12).add(
                                            panMainLayout.createParallelGroup(
                                                org.jdesktop.layout.GroupLayout.LEADING).add(chkRKvorhanden).add(
                                                chkMKRvorhanden).add(chkMKSvorhanden).add(chkSKvorhanden).add(
                                                chkSGvorhanden).add(chkKKAvorhanden).add(
                                                chkErlaubnisfreieVersickerung)))).add(13, 13, 13)).add(
                                panMainLayout.createSequentialGroup().add(lblVorhanden1).addPreferredGap(
                                    org.jdesktop.layout.LayoutStyle.RELATED)).add(
                                panMainLayout.createSequentialGroup().add(lblVorhanden2).addPreferredGap(
                                    org.jdesktop.layout.LayoutStyle.RELATED))).add(
                            panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                                lblEntleerung).add(lblAngeschlossen).add(
                                panMainLayout.createSequentialGroup().add(10, 10, 10).add(
                                    panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                                        cboMKRangeschlossen,
                                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(
                                        cboRKangeschlossen,
                                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(
                                        cboMKSangeschlossen,
                                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(
                                        cboSKangeschlossen,
                                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(chkKKAentleerung).add(
                                        chkSGentleerung)))).add(13, 13, 13))).add(
                    jSeparator4,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    org.jdesktop.layout.LayoutStyle.RELATED).add(
                    panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                        panMainLayout.createSequentialGroup().add(lblBE).addPreferredGap(
                            org.jdesktop.layout.LayoutStyle.RELATED,
                            org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                            Short.MAX_VALUE).add(cmdAddBefreiungErlaubnis).addPreferredGap(
                            org.jdesktop.layout.LayoutStyle.RELATED).add(cmdDeleteBefreiungErlaubnis)).add(
                        scpBE,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                        0,
                        Short.MAX_VALUE)).addContainerGap()));
        panMainLayout.setVerticalGroup(
            panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                jSeparator4,
                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                254,
                Short.MAX_VALUE).add(
                panMainLayout.createSequentialGroup().addContainerGap().add(
                    panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                        panMainLayout.createSequentialGroup().add(
                            panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(
                                lblAngeschlossen).add(lblVorhanden1)).addPreferredGap(
                            org.jdesktop.layout.LayoutStyle.RELATED).add(
                            panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(lblRK).add(
                                chkRKvorhanden).add(
                                cboRKangeschlossen,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
                            org.jdesktop.layout.LayoutStyle.RELATED).add(
                            panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(
                                lblMKR).add(chkMKRvorhanden).add(
                                cboMKRangeschlossen,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
                            org.jdesktop.layout.LayoutStyle.RELATED).add(
                            panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(
                                lblMKS).add(chkMKSvorhanden).add(
                                cboMKSangeschlossen,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
                            org.jdesktop.layout.LayoutStyle.RELATED).add(
                            panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(lblSK).add(
                                chkSKvorhanden).add(
                                cboSKangeschlossen,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).add(8, 8, 8).add(
                            panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                                panMainLayout.createSequentialGroup().add(18, 18, 18).add(lblSG)).add(
                                panMainLayout.createSequentialGroup().add(
                                    panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(
                                        lblEntleerung).add(lblVorhanden2)).addPreferredGap(
                                    org.jdesktop.layout.LayoutStyle.RELATED).add(
                                    panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(
                                        chkSGvorhanden).add(chkSGentleerung)))).addPreferredGap(
                            org.jdesktop.layout.LayoutStyle.RELATED).add(
                            panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                                panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(
                                    lblKKA).add(chkKKAvorhanden)).add(chkKKAentleerung)).addPreferredGap(
                            org.jdesktop.layout.LayoutStyle.RELATED).add(
                            panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(
                                chkErlaubnisfreieVersickerung).add(lblEVG)).add(0, 8, Short.MAX_VALUE)).add(
                        panMainLayout.createSequentialGroup().add(
                            panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(lblBE).add(
                                cmdAddBefreiungErlaubnis).add(cmdDeleteBefreiungErlaubnis)).addPreferredGap(
                            org.jdesktop.layout.LayoutStyle.RELATED).add(
                            scpBE,
                            org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                            196,
                            Short.MAX_VALUE))).addContainerGap()));

        add(panMain, java.awt.BorderLayout.CENTER);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cboMKSangeschlossenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cboMKSangeschlossenActionPerformed
        visualizeValidity();
    }                                                                                       //GEN-LAST:event_cboMKSangeschlossenActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cboMKRangeschlossenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cboMKRangeschlossenActionPerformed
        visualizeValidity();
    }                                                                                       //GEN-LAST:event_cboMKRangeschlossenActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cboRKangeschlossenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cboRKangeschlossenActionPerformed
        visualizeValidity();
    }                                                                                      //GEN-LAST:event_cboRKangeschlossenActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdAddBefreiungErlaubnisActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdAddBefreiungErlaubnisActionPerformed
        try {
            final List<CidsBean> list = kassenzeichenBean.getBeanCollectionProperty(
                    "kanalanschluss.befreiungenunderlaubnisse");

            final CidsBean newBefreiungBean = CidsBean.createNewCidsBeanFromTableName(CidsAppBackend.getInstance()
                            .getDomain(),
                    "befreiungerlaubnis");

            final JDialog dialog = new BEDialog(StaticSwingTools.getParentFrame(this), newBefreiungBean, list);
            dialog.addWindowListener(new WindowAdapter() {

                    @Override
                    public void windowClosed(final WindowEvent e) {
                        ((BefreiungenModel)tblBE.getModel()).fireTableDataChanged();
                    }
                });
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        } catch (Exception e) {
            log.error("Fehler beim Hinzufügen einer neuen Befreiung", e);
        }
    } //GEN-LAST:event_cmdAddBefreiungErlaubnisActionPerformed

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
        visualizeValidity();
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
        visualizeValidity();
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
        visualizeValidity();
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
        visualizeValidity();
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
        visualizeValidity();
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
        visualizeValidity();
    }                                                                                  //GEN-LAST:event_chkRKvorhandenActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void tblBEMousePressed(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_tblBEMousePressed
        if (SwingUtilities.isRightMouseButton(evt) && isEditmode()) {
            final Point p = evt.getPoint();
            final int rowNumber = tblBE.rowAtPoint(p);
            if (tblBE.getSelectionModel().isSelectionEmpty()) {
                tblBE.getSelectionModel().setSelectionInterval(rowNumber, rowNumber);
            }
            updateClipboardMenu();
            jPopupMenu1.show(evt.getComponent(), evt.getX(), evt.getY());
        } else if (SwingUtilities.isLeftMouseButton(evt) && (evt.getClickCount() == 2) && isEditmode()) {
            final Point p = evt.getPoint();
            final int rowNumber = tblBE.rowAtPoint(p);
            final CidsBean newBefreiungBean = ((BefreiungenModel)tblBE.getModel()).getBefreiungAt(rowNumber);
            final JDialog dialog = new BEDialog(StaticSwingTools.getParentFrame(this), newBefreiungBean);
            dialog.addWindowListener(new WindowAdapter() {

                    @Override
                    public void windowClosed(final WindowEvent e) {
                        ((BefreiungenModel)tblBE.getModel()).fireTableDataChanged();
                    }
                });
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        }
    } //GEN-LAST:event_tblBEMousePressed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void scpBEMousePressed(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_scpBEMousePressed
        tblBEMousePressed(evt);
    }                                                                     //GEN-LAST:event_scpBEMousePressed

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
