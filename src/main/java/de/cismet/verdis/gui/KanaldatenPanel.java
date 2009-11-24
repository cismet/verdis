/*
 * KanaldatenPanel.java
 *
 * Created on 10. April 2006, 09:21
 */

package de.cismet.verdis.gui;

import de.cismet.validation.NotValidException;
import de.cismet.verdis.data.BefreiungErlaubnis;
import de.cismet.verdis.data.Kanalanschluss;
import de.cismet.verdis.interfaces.KassenzeichenChangedListener;
import de.cismet.verdis.interfaces.Storable;
import java.awt.Color;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author  thorsten.hell@cismet.de
 */
public class KanaldatenPanel extends javax.swing.JPanel implements Storable,KassenzeichenChangedListener {
    private boolean editmode=false;
    private Connection connection;
    private Color myBlue=new java.awt.Color(0, 51, 153);
    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private Kanalanschluss kanalanschlussdaten;
    private Main main;
    
    private boolean isClipboardBECutPasted  = true;

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

    /** Creates new form KanaldatenPanel */
    public KanaldatenPanel() {
        //UIManager.put( "ComboBox.disabledForeground", Color.black );
        initComponents();
        clear();
        setEditable(false);
    }
//         chkRKvorhanden
//         chkMKRvorhanden
//         chkMKSvorhanden
//         chkSKvorhanden
//         chkSGvorhanden
//         chkKKAvorhanden
//         chkSGentleerung
//         chkKKAentleerung
//         chkErlaubnisfreieVersickerung
//         cboRKangeschlossen
//         cboMKRangeschlossen
//         cboMKSangeschlossen
//         cboSKangeschlossen
    
    public void addStoreChangeStatements(Vector v) throws NotValidException {
        kanalanschlussdaten.addStatements(v);
    }
    
    public void setShrinked(boolean shrinked) {
        panMain.setVisible(shrinked);
    }
    
    public void enableEditing(boolean b) {
        editmode=b;
        setEditable(b);
    }
    
    public void unlockDataset() {
    }
    
    public boolean lockDataset() {
        return true;
    }
    
    public boolean changesPending() {
        if (kanalanschlussdaten!=null) {
            return kanalanschlussdaten.hasChanged();
        } else {
            return false;
        }
    }
    
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
        if (tblBE.getModel() instanceof BefreiungenModel){
            ((BefreiungenModel)tblBE.getModel()).removeAll();
        }
        visualizeValidity();
        
    }
    
    public void setEditable(boolean editable) {
        editmode=editable;
        chkRKvorhanden.setEnabled(editable);
        chkMKRvorhanden.setEnabled(editable);
        chkMKSvorhanden.setEnabled(editable);
        chkSKvorhanden.setEnabled(editable);
        chkSGvorhanden.setEnabled(editable);
        chkKKAvorhanden.setEnabled(editable);
        chkSGentleerung.setEnabled(editable&&chkSGvorhanden.isSelected());
        chkKKAentleerung.setEnabled(editable&&chkKKAvorhanden.isSelected());
        chkErlaubnisfreieVersickerung.setEnabled(editable);
        cboRKangeschlossen.setEnabled(editable&&chkRKvorhanden.isSelected());
        cboMKRangeschlossen.setEnabled(editable&&chkMKRvorhanden.isSelected());
        cboMKSangeschlossen.setEnabled(editable&&chkMKSvorhanden.isSelected());
        cboSKangeschlossen.setEnabled(editable&&chkSKvorhanden.isSelected());
        tblBE.setEnabled(editable);
        cmdAddBefreiungErlaubnis.setEnabled(editable);
        updateClipboardMenu();
        visualizeValidity();
    }
    //Inserting Docking Window functionalty (Sebastian) 24.07.07
    //temporary disabled --> handled in Main.java
    public void setLeftTitlebarColor(Color c) {
        //panTitle.setLeftColor(c);
        //panTitle.repaint();
    }
    
    public Main getMain() {
        return main;
    }
    
    public void setMain(Main main) {
        this.main = main;
    }
    
    public void kassenzeichenChanged(final String kassenzeichen) {
        log.debug("Kanaldatenretrieval");
        clear();
        Thread t=new Thread(){
            public void run() {
                try {
                    Statement stmnt=connection.createStatement();
                    ResultSet rs=null;
                    if (kassenzeichen.length()==6) {
                        rs=stmnt.executeQuery("select kanalanschluss.id,rkvorhanden,	mkrvorhanden,	mksvorhanden,	skvorhanden,	rkangeschlossen,	mkrangeschlossen,	mksangeschlossen,	skangeschlossen,	sgvorhanden,	kkavorhanden,	sgentleerung,	kkaentleerung,	evg,	befreiungenunderlaubnisse from kassenzeichen,kanalanschluss where kassenzeichen.kanalanschluss=kanalanschluss.id and kassenzeichen.id/10 ="+kassenzeichen);
                    } else {
                        rs=stmnt.executeQuery("select kanalanschluss.id,rkvorhanden,	mkrvorhanden,	mksvorhanden,	skvorhanden,	rkangeschlossen,	mkrangeschlossen,	mksangeschlossen,	skangeschlossen,	sgvorhanden,	kkavorhanden,	sgentleerung,	kkaentleerung,	evg,	befreiungenunderlaubnisse from kassenzeichen,kanalanschluss where kassenzeichen.kanalanschluss=kanalanschluss.id and kassenzeichen.id ="+kassenzeichen);
                    }
                    
                    if (!rs.next()) {
                        
                        log.info("keine Kanaldaten gefunden");
                        kanalanschlussdaten=new Kanalanschluss();
                        clear();
                    } else {
//                        if (editmode && !isEmpty()) {
//                            unlockDataset();
//                        }
                        int cc=rs.getMetaData().getColumnCount();
                        Object[] rowdataKanal=new Object[cc];
                        for (int i=0; i<cc;++i) {
                            rowdataKanal[i]=rs.getObject(i+1);
                        }
                        kanalanschlussdaten=new Kanalanschluss();
                        if (kassenzeichen.length()==6) {
                            
                            rs=stmnt.executeQuery("select  befreiungerlaubnis.id,befreiungerlaubnis.aktenzeichen,befreiungerlaubnis.gueltig_bis,  from kassenzeichen,kanalanschluss,befreiungerlaubnisArray,befreiungerlaubnis where kassenzeichen.kanalanschluss=kanalanschluss.id and kanalanschluss.befreiungenunderlaubnisse=befreiungerlaubnisarray.kanalanschluss_reference and befreiungerlaubnisarray.befreiungerlaubnis=befreiungerlaubnis.id and  kassenzeichen.id/10 ="+kassenzeichen);
                        } else {
                            rs=stmnt.executeQuery("select  befreiungerlaubnis.id,befreiungerlaubnis.aktenzeichen,befreiungerlaubnis.gueltig_bis  from kassenzeichen,kanalanschluss,befreiungerlaubnisArray,befreiungerlaubnis where kassenzeichen.kanalanschluss=kanalanschluss.id and kanalanschluss.befreiungenunderlaubnisse=befreiungerlaubnisarray.kanalanschluss_reference and befreiungerlaubnisarray.befreiungerlaubnis=befreiungerlaubnis.id and  kassenzeichen.id ="+kassenzeichen);
                        }
                        Vector<BefreiungErlaubnis> befreiungen=new Vector<BefreiungErlaubnis>();
                        while (rs.next()) {
                            cc=rs.getMetaData().getColumnCount();
                            Object[] rowdataBefreiung=new Object[cc];
                            for (int i=0; i<cc;++i) {
                                rowdataBefreiung[i]=rs.getObject(i+1);
                                
                            }
                            BefreiungErlaubnis be=new BefreiungErlaubnis();
                            be.fillFromObjectArray(rowdataBefreiung);
                            befreiungen.add(be);
                        }
                        
                        kanalanschlussdaten.fillFromObjectArray(rowdataKanal,befreiungen);
                        
                        
                        
                    }
                    kanalanschlussdaten.backup();
                    kanalanschlussdaten.setKassenzeichen(new Integer(kassenzeichen).intValue());
                    //Models
                    chkRKvorhanden.setModel(kanalanschlussdaten.getRkVorhandenModel());
                    chkMKRvorhanden.setModel(kanalanschlussdaten.getMkrVorhandenModel());
                    chkMKSvorhanden.setModel(kanalanschlussdaten.getMksVorhandenModel());
                    chkSKvorhanden.setModel(kanalanschlussdaten.getSkVorhandenModel());
                    chkSGvorhanden.setModel(kanalanschlussdaten.getSgVorhandenModel());
                    chkKKAvorhanden.setModel(kanalanschlussdaten.getKkaVorhandenModel());
                    chkSGentleerung.setModel(kanalanschlussdaten.getSgEntleerungModel());
                    chkKKAentleerung.setModel(kanalanschlussdaten.getKkaEntleerungModel());
                    chkErlaubnisfreieVersickerung.setModel(kanalanschlussdaten.getEvgModel());
                    cboRKangeschlossen.setModel(kanalanschlussdaten.getRkAngeschlossenModel());
                    cboMKRangeschlossen.setModel(kanalanschlussdaten.getMkrAngeschlossenModel());
                    cboMKSangeschlossen.setModel(kanalanschlussdaten.getMksAngeschlossenModel());
                    cboSKangeschlossen.setModel(kanalanschlussdaten.getSkAngeschlossenModel());
                    tblBE.setModel(kanalanschlussdaten.getBefreiungenModel());
                    setEditable(editmode);
                    visualizeValidity();
                } catch (SQLException sqlEx) {
                    log.error("Fehler bei der Suche nach Kassenzeichen!",sqlEx);
                }
                
                
            }
        };
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();
    }
    
    //Inserting Docking Window functionalty (Sebastian) 24.07.07
    private void visualizeValidity() {
        //TODO UGLY
        if(main != null){
            if (kanalanschlussdaten==null||kanalanschlussdaten.isValid()) {
                //lblTitle.setForeground(Color.white);
                main.setKanalTitleForeground(Color.BLACK);
            } else {
                if (this.isEditmode()) {
                    //lblTitle.setForeground(Color.YELLOW);
                    main.setKanalTitleForeground(Color.YELLOW);
                } else {
                    //lblTitle.setForeground(Color.red);
                    main.setKanalTitleForeground(Color.RED);
                }
            }
        }
    }
    
    public void setConnectionInfo(de.cismet.tools.ConnectionInfo connectionInfo) {
        try {
            Class.forName(connectionInfo.getDriver());
            connection=DriverManager.getConnection(connectionInfo.getUrl(),connectionInfo.getUser(), connectionInfo.getPass());
        } catch (ClassNotFoundException cnfEx) {
            log.fatal("Datenbanktreiber nicht gefunden!",cnfEx);
        } catch (java.sql.SQLException sqlEx) {
            log.fatal("Fehler beim Aufbau der Datenbankverbindung!",sqlEx);
        }
    }
    
    public boolean isEditmode() {
        return editmode;
    }

    private void updateClipboardMenu() {
        log.debug("updateClipboardMenu()");

        boolean isClipboardEmpty = getClipboardBEs().isEmpty();
        boolean isSelectionEmpty = tblBE.getSelectionModel().isSelectionEmpty();

        // popupmenu aktualisieren
        mniPaste.setEnabled(!isClipboardEmpty);
        mniCut.setEnabled(!isSelectionEmpty);
        mniCopy.setEnabled(!isSelectionEmpty);
        mniDelete.setEnabled(!isSelectionEmpty);
        
        // deletebutton aktualisieren
        cmdDeleteBefreiungErlaubnis.setEnabled(!isSelectionEmpty);
    }

    private void deleteSelectedBE() {
        log.debug("deleteBE()");

        // nur um Editmodus reagieren
        if (isEditmode()) {
            Vector<BefreiungErlaubnis> selectedBefreiungen = getSelectedBE();
            for (BefreiungErlaubnis selectedBefreiung : selectedBefreiungen) {
                log.debug("selectedBefreiung: " + selectedBefreiung);
                kanalanschlussdaten.addBefreiungToDelete(selectedBefreiung);
                kanalanschlussdaten.getBefreiungen().remove(selectedBefreiung);
            }

            // Tabelle aktualisieren
            kanalanschlussdaten.updateModels();
            tblBE.setModel(kanalanschlussdaten.getBefreiungenModel());

            // Menus aktualisieren
            updateClipboardMenu();
        }
    }

    private boolean cutSelectedBE() {
        log.debug("cutBE()");

        // nur um Editmodus reagieren
        if (isEditmode()) {
            // ausgewählte BE in die Zwischenablage kopieren
            boolean copySuccessful = copySelectedBE();
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

    private boolean copySelectedBE() {
        log.debug("copyBE()");

        // nur um Editmodus reagieren
        if (isEditmode()) {
            // ausgeschnittene Daten noch nicht wieder eingefügt?
            if (!isClipboardBECutPasted && getClipboardBEs().size() > 0) {
                // Dialog zum Nachfragen ob ausgeschnittene Daten verworfen werden
                int answer = JOptionPane.showConfirmDialog(this, "Sie haben zuvor Daten in die Zwischenablage ausgeschnitten und nicht wieder eingefügt.\nWenn Sie jetzt fortfahren, werden diese Daten verworfen !", "Daten verwerfen?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                // Wenn nicht OK, abbrechen
                if (answer != JOptionPane.OK_OPTION) {
                    return false;
                }
            }

            // Zwischenspeicher vorbereiten
            StringBuffer clipboardString = new StringBuffer();

            // markierte BE in die Zwischenablage speichern
            Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            for (BefreiungErlaubnis befreiung : getSelectedBE()) {
                clipboardString.append(befreiung.getAktenzeichen() + SEPARATOR_BE_DATA + befreiung.getGueltigBis() + SEPARATOR_BE);
            }
            systemClipboard.setContents(new StringSelection(clipboardString.toString()), null);


            // Menus aktualisieren
            updateClipboardMenu();
            // Kopieren ging gut
            return true;
        }
        return false;
    }

    private Vector<BefreiungErlaubnis> getClipboardBEs() {
        Vector<BefreiungErlaubnis> befreiungen = new Vector<BefreiungErlaubnis>();
        String data = "";

        //
        Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable transfer = systemClipboard.getContents(null);
        try {
            data = (String)transfer.getTransferData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException ex) {
            log.debug("(String)transfer.getTransferData(DataFlavor.stringFlavor)", ex);
        } catch (IOException ex) {
            log.debug("(String)transfer.getTransferData(DataFlavor.stringFlavor)", ex);
        }

        //
        String[] befreiungStrings = data.split(SEPARATOR_BE);
        for (String befreiungString : befreiungStrings) {
            String[] befreiungData = befreiungString.split(SEPARATOR_BE_DATA);
            if (befreiungData.length == 2) {
                BefreiungErlaubnis befreiung = new BefreiungErlaubnis();
                befreiung.setAktenzeichen(befreiungData[0]);
                befreiung.setGueltigBis(befreiungData[1]);
                befreiungen.add(befreiung);
            }
        }

        return befreiungen;
    }

    private void pasteBE() {
        log.debug("pasteBE()");

        // nur um Editmodus reagieren
        if (isEditmode()) {
            for (BefreiungErlaubnis clipboardBefreiung : getClipboardBEs()) {
                log.debug("clipboardBefreiung: " + clipboardBefreiung);

                // neue BE erzeugen
                BefreiungErlaubnis newBefreiung = new BefreiungErlaubnis();
                // Daten der BE im Zwischenspeicher in neue BE kopieren
                newBefreiung.setAktenzeichen(clipboardBefreiung.getAktenzeichen());
                newBefreiung.setGueltigBis(clipboardBefreiung.getGueltigBis());

                // neue BE einfügen
                kanalanschlussdaten.getBefreiungen().add(newBefreiung);
            }

            // ausgeschnittene Daten wurden wieder eingefügt
            if (!isClipboardBECutPasted) {
                isClipboardBECutPasted = true;
            }

            // Tabelle aktualisieren
            kanalanschlussdaten.updateModels();
            tblBE.setModel(kanalanschlussdaten.getBefreiungenModel());

            // Menus aktualisieren
            updateClipboardMenu();
        }
    }

    private Vector<BefreiungErlaubnis> getSelectedBE() {
        Vector<BefreiungErlaubnis> befreiungen = new Vector<BefreiungErlaubnis>();
        int[] rows = tblBE.getSelectedRows();
        for (int row : rows) {
            befreiungen.add(kanalanschlussdaten.getBefreiungen().get(row));
        }
        return befreiungen;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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
        cboRKangeschlossen = new javax.swing.JComboBox();
        cboMKRangeschlossen = new javax.swing.JComboBox();
        cboMKSangeschlossen = new javax.swing.JComboBox();
        cboSKangeschlossen = new javax.swing.JComboBox();
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
        chkRKvorhanden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkRKvorhandenActionPerformed(evt);
            }
        });

        chkMKRvorhanden.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkMKRvorhanden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkMKRvorhandenActionPerformed(evt);
            }
        });

        chkMKSvorhanden.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkMKSvorhanden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkMKSvorhandenActionPerformed(evt);
            }
        });

        chkSKvorhanden.setToolTipText("");
        chkSKvorhanden.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkSKvorhanden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSKvorhandenActionPerformed(evt);
            }
        });

        lblAngeschlossen.setText("angeschlossen");

        cboRKangeschlossen.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ja", "nein", "fraglich" }));
        cboRKangeschlossen.setFocusable(false);
        cboRKangeschlossen.setMinimumSize(new java.awt.Dimension(55, 16));
        cboRKangeschlossen.setPreferredSize(new java.awt.Dimension(59, 16));
        cboRKangeschlossen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboRKangeschlossenActionPerformed(evt);
            }
        });

        cboMKRangeschlossen.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ja", "nein", "fraglich" }));
        cboMKRangeschlossen.setFocusable(false);
        cboMKRangeschlossen.setMinimumSize(new java.awt.Dimension(55, 16));
        cboMKRangeschlossen.setPreferredSize(new java.awt.Dimension(59, 16));
        cboMKRangeschlossen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboMKRangeschlossenActionPerformed(evt);
            }
        });

        cboMKSangeschlossen.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ja", "nein", "fraglich" }));
        cboMKSangeschlossen.setFocusable(false);
        cboMKSangeschlossen.setMinimumSize(new java.awt.Dimension(55, 16));
        cboMKSangeschlossen.setPreferredSize(new java.awt.Dimension(59, 16));
        cboMKSangeschlossen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboMKSangeschlossenActionPerformed(evt);
            }
        });

        cboSKangeschlossen.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ja", "nein", "fraglich" }));
        cboSKangeschlossen.setFocusable(false);
        cboSKangeschlossen.setMinimumSize(new java.awt.Dimension(55, 16));
        cboSKangeschlossen.setPreferredSize(new java.awt.Dimension(59, 16));

        lblSG.setText("SG");
        lblSG.setToolTipText("Sickergrube");

        lblVorhanden2.setText("vorh.");

        lblEntleerung.setText("Entleerung");

        lblKKA.setText("KKA");
        lblKKA.setToolTipText("Kleinkläranlage");

        chkSGvorhanden.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkSGvorhanden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSGvorhandenActionPerformed(evt);
            }
        });

        chkKKAvorhanden.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkKKAvorhanden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkKKAvorhandenActionPerformed(evt);
            }
        });

        chkSGentleerung.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        chkKKAentleerung.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        chkErlaubnisfreieVersickerung.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);

        scpBE.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        scpBE.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                scpBEMousePressed(evt);
            }
        });

        tblBE.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Aktenzeichen", "gültig bis"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblBE.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tblBEMousePressed(evt);
            }
        });
        scpBE.setViewportView(tblBE);
        // reagieren auf Verändern der Selektion
        tblBE.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                log.debug("selectionChanged");
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

        cmdAddBefreiungErlaubnis.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/titlebars/add.png"))); // NOI18N
        cmdAddBefreiungErlaubnis.setFocusPainted(false);
        cmdAddBefreiungErlaubnis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdAddBefreiungErlaubnisActionPerformed(evt);
            }
        });

        cmdDeleteBefreiungErlaubnis.setAction(new DeleteAction());
        cmdDeleteBefreiungErlaubnis.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/titlebars/remove.png"))); // NOI18N
        cmdDeleteBefreiungErlaubnis.setEnabled(false);
        cmdDeleteBefreiungErlaubnis.setFocusPainted(false);

        org.jdesktop.layout.GroupLayout panMainLayout = new org.jdesktop.layout.GroupLayout(panMain);
        panMain.setLayout(panMainLayout);
        panMainLayout.setHorizontalGroup(
            panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panMainLayout.createSequentialGroup()
                .addContainerGap()
                .add(panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(panMainLayout.createSequentialGroup()
                        .add(lblKKA)
                        .add(121, 121, 121))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, panMainLayout.createSequentialGroup()
                        .add(panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(panMainLayout.createSequentialGroup()
                                .add(panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(lblRK)
                                    .add(lblMKR)
                                    .add(lblMKS)
                                    .add(lblSK)
                                    .add(lblSG)
                                    .add(panMainLayout.createSequentialGroup()
                                        .add(lblEVG)
                                        .add(12, 12, 12)
                                        .add(panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                            .add(chkRKvorhanden)
                                            .add(chkMKRvorhanden)
                                            .add(chkMKSvorhanden)
                                            .add(chkSKvorhanden)
                                            .add(chkSGvorhanden)
                                            .add(chkKKAvorhanden)
                                            .add(chkErlaubnisfreieVersickerung))))
                                .add(13, 13, 13))
                            .add(panMainLayout.createSequentialGroup()
                                .add(lblVorhanden1)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                            .add(panMainLayout.createSequentialGroup()
                                .add(lblVorhanden2)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                        .add(panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblEntleerung)
                            .add(lblAngeschlossen)
                            .add(panMainLayout.createSequentialGroup()
                                .add(10, 10, 10)
                                .add(panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(cboMKRangeschlossen, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(cboRKangeschlossen, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(cboMKSangeschlossen, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(cboSKangeschlossen, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                            .add(panMainLayout.createSequentialGroup()
                                .add(10, 10, 10)
                                .add(panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(chkKKAentleerung)
                                    .add(chkSGentleerung))))
                        .add(13, 13, 13)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(panMainLayout.createSequentialGroup()
                        .add(lblBE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 28, Short.MAX_VALUE)
                        .add(cmdAddBefreiungErlaubnis)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cmdDeleteBefreiungErlaubnis))
                    .add(scpBE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE))
                .addContainerGap())
        );
        panMainLayout.setVerticalGroup(
            panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panMainLayout.createSequentialGroup()
                .addContainerGap()
                .add(panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblAngeschlossen)
                    .add(lblVorhanden1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblRK)
                    .add(chkRKvorhanden)
                    .add(cboRKangeschlossen, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblMKR)
                    .add(chkMKRvorhanden)
                    .add(cboMKRangeschlossen, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblMKS)
                    .add(chkMKSvorhanden)
                    .add(cboMKSangeschlossen, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblSK)
                    .add(chkSKvorhanden)
                    .add(cboSKangeschlossen, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(8, 8, 8)
                .add(panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(panMainLayout.createSequentialGroup()
                        .add(18, 18, 18)
                        .add(lblSG))
                    .add(panMainLayout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblEntleerung)
                            .add(lblVorhanden2))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(chkSGvorhanden)
                            .add(chkSGentleerung))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(lblKKA)
                        .add(chkKKAvorhanden))
                    .add(chkKKAentleerung))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(chkErlaubnisfreieVersickerung)
                    .add(lblEVG))
                .addContainerGap(21, Short.MAX_VALUE))
            .add(jSeparator4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
            .add(panMainLayout.createSequentialGroup()
                .addContainerGap()
                .add(panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblBE)
                    .add(cmdAddBefreiungErlaubnis)
                    .add(cmdDeleteBefreiungErlaubnis))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(scpBE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                .addContainerGap())
        );

        add(panMain, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    
    private void cboMKSangeschlossenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboMKSangeschlossenActionPerformed
        visualizeValidity();
    }//GEN-LAST:event_cboMKSangeschlossenActionPerformed
    
    private void cboMKRangeschlossenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboMKRangeschlossenActionPerformed
        visualizeValidity();
    }//GEN-LAST:event_cboMKRangeschlossenActionPerformed
    
    private void cboRKangeschlossenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboRKangeschlossenActionPerformed
        visualizeValidity();
    }//GEN-LAST:event_cboRKangeschlossenActionPerformed
    
    private void cmdAddBefreiungErlaubnisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdAddBefreiungErlaubnisActionPerformed
        kanalanschlussdaten.getBefreiungen().add(new BefreiungErlaubnis());
        kanalanschlussdaten.updateModels();
        tblBE.setModel(kanalanschlussdaten.getBefreiungenModel());
    }//GEN-LAST:event_cmdAddBefreiungErlaubnisActionPerformed
    
    private void chkKKAvorhandenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkKKAvorhandenActionPerformed
        if (chkKKAvorhanden.isSelected()&&editmode) {
            chkKKAentleerung.setEnabled(true);
        } else {
            chkKKAentleerung.setEnabled(false);
            chkKKAentleerung.setSelected(false);
        }
        visualizeValidity();
    }//GEN-LAST:event_chkKKAvorhandenActionPerformed
    
    private void chkSGvorhandenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSGvorhandenActionPerformed
        if (chkSGvorhanden.isSelected()&&editmode) {
            chkSGentleerung.setEnabled(true);
        } else {
            chkSGentleerung.setEnabled(false);
            chkSGentleerung.setSelected(false);
        }
        visualizeValidity();
    }//GEN-LAST:event_chkSGvorhandenActionPerformed
    
    private void chkSKvorhandenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSKvorhandenActionPerformed
        if (chkSKvorhanden.isSelected()&&editmode) {
            cboSKangeschlossen.setEnabled(true);
        } else {
            cboSKangeschlossen.setEnabled(false);
            cboSKangeschlossen.setSelectedIndex(-1);
        }
        visualizeValidity();
    }//GEN-LAST:event_chkSKvorhandenActionPerformed
    
    private void chkMKSvorhandenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkMKSvorhandenActionPerformed
        if (chkMKSvorhanden.isSelected()&&editmode) {
            cboMKSangeschlossen.setEnabled(true);
        } else {
            cboMKSangeschlossen.setEnabled(false);
            cboMKSangeschlossen.setSelectedIndex(-1);
        }
        visualizeValidity();
    }//GEN-LAST:event_chkMKSvorhandenActionPerformed
    
    private void chkMKRvorhandenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkMKRvorhandenActionPerformed
        if (chkMKRvorhanden.isSelected()&&editmode) {
            cboMKRangeschlossen.setEnabled(true);
        } else {
            cboMKRangeschlossen.setEnabled(false);
            cboMKRangeschlossen.setSelectedIndex(-1);
        }
        visualizeValidity();
    }//GEN-LAST:event_chkMKRvorhandenActionPerformed
    
    private void chkRKvorhandenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkRKvorhandenActionPerformed
        if (chkRKvorhanden.isSelected()&&editmode) {
            cboRKangeschlossen.setEnabled(true);
        } else {
            cboRKangeschlossen.setEnabled(false);
            cboRKangeschlossen.setSelectedIndex(-1);
        }
        visualizeValidity();
    }//GEN-LAST:event_chkRKvorhandenActionPerformed
            
    private void tblBEMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBEMousePressed
        if (SwingUtilities.isRightMouseButton(evt) && isEditmode()) {
            Point p = evt.getPoint();
            int rowNumber = tblBE.rowAtPoint(p);
            if (tblBE.getSelectionModel().isSelectionEmpty()) {
                tblBE.getSelectionModel().setSelectionInterval(rowNumber, rowNumber);
            }
            updateClipboardMenu();
            jPopupMenu1.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_tblBEMousePressed

    private void scpBEMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_scpBEMousePressed
        tblBEMousePressed(evt);
    }//GEN-LAST:event_scpBEMousePressed
        
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
    // End of variables declaration//GEN-END:variables

    class CutAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            cutSelectedBE();
        }
    }

    class CopyAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            copySelectedBE();
        }
    }

    class PasteAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            pasteBE();
        }
    }

    class DeleteAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            deleteSelectedBE();
        }
    }

}
