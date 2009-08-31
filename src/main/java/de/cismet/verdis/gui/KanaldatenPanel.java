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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

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
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
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

        jLabel3.setText("jLabel3");
        jLabel6.setText("jLabel6");

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
        chkRKvorhanden.setMargin(new java.awt.Insets(0, 0, 0, 0));
        chkRKvorhanden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkRKvorhandenActionPerformed(evt);
            }
        });

        chkMKRvorhanden.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkMKRvorhanden.setMargin(new java.awt.Insets(0, 0, 0, 0));
        chkMKRvorhanden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkMKRvorhandenActionPerformed(evt);
            }
        });

        chkMKSvorhanden.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkMKSvorhanden.setMargin(new java.awt.Insets(0, 0, 0, 0));
        chkMKSvorhanden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkMKSvorhandenActionPerformed(evt);
            }
        });

        chkSKvorhanden.setToolTipText("");
        chkSKvorhanden.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkSKvorhanden.setMargin(new java.awt.Insets(0, 0, 0, 0));
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
        lblKKA.setToolTipText("Kleinkl\u00e4ranlage");

        chkSGvorhanden.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkSGvorhanden.setMargin(new java.awt.Insets(0, 0, 0, 0));
        chkSGvorhanden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSGvorhandenActionPerformed(evt);
            }
        });

        chkKKAvorhanden.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkKKAvorhanden.setMargin(new java.awt.Insets(0, 0, 0, 0));
        chkKKAvorhanden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkKKAvorhandenActionPerformed(evt);
            }
        });

        chkSGentleerung.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkSGentleerung.setMargin(new java.awt.Insets(0, 0, 0, 0));

        chkKKAentleerung.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkKKAentleerung.setMargin(new java.awt.Insets(0, 0, 0, 0));

        chkErlaubnisfreieVersickerung.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkErlaubnisfreieVersickerung.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);

        scpBE.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        tblBE.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Aktenzeichen", "g\u00FCltig bis"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        scpBE.setViewportView(tblBE);

        lblBE.setText("Befreiung / Erlaubnis");

        lblEVG.setText("EVG");
        lblEVG.setToolTipText("Erlaubnisfreie Versickerung");

        cmdAddBefreiungErlaubnis.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/titlebars/add.png")));
        cmdAddBefreiungErlaubnis.setFocusPainted(false);
        cmdAddBefreiungErlaubnis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdAddBefreiungErlaubnisActionPerformed(evt);
            }
        });

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
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 49, Short.MAX_VALUE)
                        .add(cmdAddBefreiungErlaubnis))
                    .add(scpBE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE))
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
                .addContainerGap(49, Short.MAX_VALUE))
            .add(jSeparator4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
            .add(panMainLayout.createSequentialGroup()
                .addContainerGap()
                .add(panMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblBE)
                    .add(cmdAddBefreiungErlaubnis))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(scpBE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
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
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
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
    private javax.swing.JPanel panMain;
    private javax.swing.JScrollPane scpBE;
    private javax.swing.JTable tblBE;
    // End of variables declaration//GEN-END:variables
    
}
