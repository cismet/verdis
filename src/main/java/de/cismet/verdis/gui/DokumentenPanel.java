/*
 * DokumentenPanel.java
 *
 * Created on 5. Januar 2005, 14:01
 */

package de.cismet.verdis.gui;
import de.cismet.gui.tools.*;
import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.validation.NotValidException;
import de.cismet.verdis.interfaces.Storable;
import java.applet.AppletContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.*;

/**
 *
 * @author  hell
 */
public class DokumentenPanel extends javax.swing.JPanel implements de.cismet.verdis.interfaces.KassenzeichenChangedListener, DropTargetListener,Storable{
    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private Connection connection;
    private java.applet.AppletContext ac=null;
    private Thread lookupThread;
    private Color myBlue=new java.awt.Color(0, 51, 153);    
    private boolean inEditMode=false;
    private Vector newLinks=new Vector();
    private Vector removedLinks=new Vector();
    private String momentanesKassenzeichen="-1";
    
    /** Creates new form DokumentenPanel */
    public DokumentenPanel() {
        initComponents();
        DropTarget dt=new DropTarget(this,DnDConstants.ACTION_COPY_OR_MOVE,this);
    }
    public void setAppletContext(java.applet.AppletContext ac) {
        this.ac=ac;
    }
    
    private void emptyPanel() {
//        EventQueue.invokeLater(new Runnable(){
//            public void run() {
//                panDocs.removeAll();
//                panDocs.repaint();
//            }
//        });
        panDocs.removeAll();
        panDocs.repaint();

    }
    
    public void kassenzeichenChanged(final String kz) {
        momentanesKassenzeichen=kz;
        java.awt.event.ActionListener iconAnimator = new java.awt.event.ActionListener() {
          int counter=0;
            public void actionPerformed( java.awt.event.ActionEvent event ) {
//                javax.swing.Timer tt=(javax.swing.Timer)event.getSource();
//                if (tt.isRepeats()==false) {
//                    lblTitle.setIcon(ic_default);
//                }
//                else {
//                    if (counter==0) {
//                        lblTitle.setIcon(ic_0);
//                        counter++;
//                    }
//                    else if (counter==1) {
//                        lblTitle.setIcon(ic_1);
//                        counter++;
//                    }
//                      
//                    else if (counter==16) {
//                        lblTitle.setIcon(ic_0);
//                        counter=0;
//                    }
//                }
          }
        };
        final javax.swing.Timer animationTimer = new javax.swing.Timer(40, iconAnimator );
        animationTimer.setRepeats(true);
        animationTimer.start();

        if (lookupThread!=null&&lookupThread.isAlive()) {
            lookupThread.interrupt();
        }
        emptyPanel();
        
        //Verbindung zur Datenbank aufbauen und die Dokumente auslesen
        lookupThread=new Thread() {
            public void run() {
                try { 
                    Statement stmnt=connection.createStatement();
//                    select 
//                    name,typ,description,prot_prefix,server,path, object_name,
//                    dms_urls.id as dms_urls.id,dms_url.id as dms_url_id,url.id as url_id, url_base.id as url_base_id
//                     from 
//                    dms_urls, dms_url ,url, url_base 
//                    where 
//                    dms_url=dms_url.id and 
//                    url_id=url.id and 
//                    url.url_base_id=url_base.id and 
//                    kassenzeichen_reference=6000467
                    ResultSet rs=stmnt.executeQuery("select name,typ,description,prot_prefix,server,path, object_name,dms_urls.id as dms_urls_id,dms_url.id as dms_url_id,url.id as url_id, url_base.id as url_base_id  from dms_urls, dms_url ,url, url_base where dms_url=dms_url.id and url_id=url.id and url.url_base_id=url_base.id and kassenzeichen_reference="+kz);
                    setCursor(java.awt.Cursor.getDefaultCursor());
                    
                    while (rs.next()&&!isInterrupted()) {
                        String url=rs.getString(4)+rs.getString(5)+rs.getString(6)+rs.getString(7);
                        int typ=rs.getInt(2);
                        addNewDocPanel(ac,rs.getString(1),url,typ,kz,rs.getInt("dms_urls_id"),rs.getInt("dms_url_id"),rs.getInt("url_id"),rs.getInt("url_base_id"));                        
                    }
                }
                catch ( Exception  e ) { 
                    log.error("Fehler beim Laden der Dokumente!",e); 
                }
                if (animationTimer!=null) {
                    animationTimer.setRepeats(false);
                    animationTimer.setDelay(1);
                }
        } };
        lookupThread.setPriority(Thread.NORM_PRIORITY);
        lookupThread.start();
    }
    public DocPanel addNewDocPanel(AppletContext ac,String description, String u,String kassenzeichen){
        return addNewDocPanel(ac,description,u,1,momentanesKassenzeichen,-1,-1,-1,-1);
    }   
    
    public DocPanel addNewDocPanel(AppletContext ac,String description, String u,int typ,String kz,int dms_urls_id,int dms_url_id,int url_id,int url_base_id){
        String url=u;
        log.info("AddNewDocPanel: "+url);
        ImageIcon ic=null;
        boolean deletable=true;
        if (typ==0) {
            //Setze WMS Icon und h\u00E4nge Kassenzeichen an
            ic=new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/filetypes/dms_default.png"));
            url=url.trim()+kz;
            deletable=false;
        }
        if (typ==1) {
            //Setze das Icon nach der Dateiendung
            int pPos=url.lastIndexOf(".");
            String type=url.substring(pPos+1, url.length()).toLowerCase();
            String filename=type+".png";
            try {
                ic=new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/filetypes/"+filename));
            }
            catch (Exception e){
                log.debug("Fehler beim Suchen des Icons:"+type);
                ic=new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/filetypes/dms_default.png"));
            }
        }
        
        final DocPanel dp=new DocPanel();
        dp.setAplettContext(ac);
        dp.setDesc(description);
        dp.setGotoUrl(url);
        dp.setIcon(ic);
        dp.setKassenzeichen(kz);
        dp.setDeletable(deletable);
        dp.setDms_url_id(dms_url_id);
        dp.setDms_urls_id(dms_urls_id);
        dp.setUrl_id(url_id);
        dp.setUrl_base_id(url_base_id);
        dp.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() instanceof DocPanel ) {
                    if (e.getActionCommand().equals(DocPanel.DELETE_ACTION_COMMAND)) {
                        panDocs.remove((DocPanel)e.getSource());
                        if (!newLinks.contains((DocPanel)e.getSource())) {
                            removedLinks.add(e.getSource());
                        }
                        else {
                            newLinks.remove((DocPanel)e.getSource());
                        }
                        panDocs.revalidate();
                        repaint();
                    }
                }
            }
        });
//        EventQueue.invokeLater(new Runnable(){
//            public void run() {
//                panDocs.add(dp);
//                revalidate();
//            }
//        });
        panDocs.add(dp);
        revalidate();

        return dp;
    }
 
    public void setConnectionInfo(de.cismet.tools.ConnectionInfo connectionInfo) {
        try {
            Class.forName(connectionInfo.getDriver());
            connection=DriverManager.getConnection(connectionInfo.getUrl(),connectionInfo.getUser(), connectionInfo.getPass());
        }
        catch (ClassNotFoundException cnfEx) {
            log.fatal("Datenbanktreiber nicht gefunden!",cnfEx);
        }
        catch (java.sql.SQLException sqlEx) {
            log.fatal("Fehler beim Aufbau der Datenbankverbindung!",sqlEx);
        }
    }

    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panDocs = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        panDocs.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        add(panDocs, java.awt.BorderLayout.CENTER);

    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel panDocs;
    // End of variables declaration//GEN-END:variables
    
    
    
    public static void main(String[] args) {
        DokumentenPanel d=new DokumentenPanel();
    }
    
    public Connection getConnection() {
        return connection;
    }

    public void dragEnter(DropTargetDragEvent dtde) {
        if (!inEditMode) {
            dtde.rejectDrag();
        }
    }

    public void dragOver(DropTargetDragEvent dtde) {
    }

    public void dropActionChanged(DropTargetDragEvent dtde) {
    }

    public void dragExit(DropTargetEvent dte) {
    }

    public void drop(DropTargetDropEvent dtde) {
        dtde.acceptDrop(DnDConstants.ACTION_COPY);
        String link=StaticSwingTools.getLinkFromDropEvent(dtde);
        if (link!=null){
            String description = JOptionPane.showInputDialog(this,"Welche Beschriftung soll der Link haben?",link);
            if (description !=null) {
                DocPanel dp=addNewDocPanel(ac,description,link,momentanesKassenzeichen);
                newLinks.add(dp);
                this.repaint();
            }
        }
        
    }

    public void addStoreChangeStatements(Vector v) throws NotValidException {
        Iterator it=removedLinks.iterator();
        while (it.hasNext()) {
            Object elem = (Object) it.next();
            if (elem instanceof DocPanel) {
                ((DocPanel)elem).addDeleteStatements(v);
            }
        }
        removedLinks.removeAllElements();
        
        it=newLinks.iterator();
        while (it.hasNext()) {
            Object elem = (Object) it.next();
            if (elem instanceof DocPanel) {
                ((DocPanel)elem).addNewStatements(v);
            }
            
        }
        newLinks.removeAllElements();
        
    }

    public void enableEditing(boolean b) {
        inEditMode=b;
    }
    
//Inserting Docking Window functionalty (Sebastian) 24.07.07
    //temporary disabled --> handled in Main.java 
    public void setLeftTitlebarColor(Color c) {                
        //panTitle.setLeftColor(c);
        //panTitle.repaint();
    }    

    public void unlockDataset() {
    }

    public boolean lockDataset() {
        return true;
    }

    public boolean changesPending() {
//        return false;
        if (newLinks.size()>0 || removedLinks.size()>0) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean isInEditMode() {
        return inEditMode;
    }

    

}
