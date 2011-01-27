/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * DokumentenPanel.java
 *
 * Created on 5. Januar 2005, 14:01
 */
package de.cismet.verdis.gui;
import java.applet.AppletContext;

import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.sql.*;

import java.util.Iterator;
import java.util.Vector;

import javax.swing.*;

import de.cismet.gui.tools.*;

import de.cismet.tools.gui.StaticSwingTools;

import de.cismet.validation.NotValidException;

import de.cismet.verdis.interfaces.Storable;

/**
 * DOCUMENT ME!
 *
 * @author   hell
 * @version  $Revision$, $Date$
 */
public class DokumentenPanel extends javax.swing.JPanel
        implements de.cismet.verdis.interfaces.KassenzeichenChangedListener,
            DropTargetListener,
            Storable {

    //~ Instance fields --------------------------------------------------------

    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private Connection connection;
    private java.applet.AppletContext ac = null;
    private Thread lookupThread;
    private Color myBlue = new java.awt.Color(0, 51, 153);
    private boolean inEditMode = false;
    private Vector newLinks = new Vector();
    private Vector removedLinks = new Vector();
    private String momentanesKassenzeichen = "-1";

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel panDocs;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form DokumentenPanel.
     */
    public DokumentenPanel() {
        initComponents();
        final DropTarget dt = new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  ac  DOCUMENT ME!
     */
    public void setAppletContext(final java.applet.AppletContext ac) {
        this.ac = ac;
    }

    /**
     * DOCUMENT ME!
     */
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

    @Override
    public void kassenzeichenChanged(final String kz) {
        momentanesKassenzeichen = kz;
        final java.awt.event.ActionListener iconAnimator = new java.awt.event.ActionListener() {

                int counter = 0;

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent event) {
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

        final javax.swing.Timer animationTimer = new javax.swing.Timer(40, iconAnimator);
        animationTimer.setRepeats(true);
        animationTimer.start();

        if ((lookupThread != null) && lookupThread.isAlive()) {
            lookupThread.interrupt();
        }
        emptyPanel();

        // Verbindung zur Datenbank aufbauen und die Dokumente auslesen
        lookupThread = new Thread() {

                @Override
                public void run() {
                    try {
                        final Statement stmnt = connection.createStatement();
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
                        final ResultSet rs = stmnt.executeQuery(
                                "select name,typ,description,prot_prefix,server,path, object_name,dms_urls.id as dms_urls_id,dms_url.id as dms_url_id,url.id as url_id, url_base.id as url_base_id  from dms_urls, dms_url ,url, url_base where dms_url=dms_url.id and url_id=url.id and url.url_base_id=url_base.id and kassenzeichen_reference="
                                        + kz);
                        setCursor(java.awt.Cursor.getDefaultCursor());

                        while (rs.next() && !isInterrupted()) {
                            final String url = rs.getString(4) + rs.getString(5) + rs.getString(6) + rs.getString(7);
                            final int typ = rs.getInt(2);
                            addNewDocPanel(
                                ac,
                                rs.getString(1),
                                url,
                                typ,
                                kz,
                                rs.getInt("dms_urls_id"),
                                rs.getInt("dms_url_id"),
                                rs.getInt("url_id"),
                                rs.getInt("url_base_id"));
                        }
                    } catch (Exception e) {
                        log.error("Fehler beim Laden der Dokumente!", e);
                    }
                    if (animationTimer != null) {
                        animationTimer.setRepeats(false);
                        animationTimer.setDelay(1);
                    }
                }
            };
        lookupThread.setPriority(Thread.NORM_PRIORITY);
        lookupThread.start();
    }
    /**
     * DOCUMENT ME!
     *
     * @param   ac             DOCUMENT ME!
     * @param   description    DOCUMENT ME!
     * @param   u              DOCUMENT ME!
     * @param   kassenzeichen  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public DocPanel addNewDocPanel(final AppletContext ac,
            final String description,
            final String u,
            final String kassenzeichen) {
        return addNewDocPanel(ac, description, u, 1, momentanesKassenzeichen, -1, -1, -1, -1);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   ac           DOCUMENT ME!
     * @param   description  DOCUMENT ME!
     * @param   u            DOCUMENT ME!
     * @param   typ          DOCUMENT ME!
     * @param   kz           DOCUMENT ME!
     * @param   dms_urls_id  DOCUMENT ME!
     * @param   dms_url_id   DOCUMENT ME!
     * @param   url_id       DOCUMENT ME!
     * @param   url_base_id  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public DocPanel addNewDocPanel(final AppletContext ac,
            final String description,
            final String u,
            final int typ,
            final String kz,
            final int dms_urls_id,
            final int dms_url_id,
            final int url_id,
            final int url_base_id) {
        String url = u;
        log.info("AddNewDocPanel: " + url);
        ImageIcon ic = null;
        boolean deletable = true;
        if (typ == 0) {
            // Setze WMS Icon und h\u00E4nge Kassenzeichen an
            ic = new javax.swing.ImageIcon(getClass().getResource(
                        "/de/cismet/verdis/res/images/filetypes/dms_default.png"));
            url = url.trim() + kz;
            deletable = false;
        }
        if (typ == 1) {
            // Setze das Icon nach der Dateiendung
            final int pPos = url.lastIndexOf(".");
            final String type = url.substring(pPos + 1, url.length()).toLowerCase();
            final String filename = type + ".png";
            try {
                ic = new javax.swing.ImageIcon(getClass().getResource(
                            "/de/cismet/verdis/res/images/filetypes/"
                                    + filename));
            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.debug("Fehler beim Suchen des Icons:" + type);
                }
                ic = new javax.swing.ImageIcon(getClass().getResource(
                            "/de/cismet/verdis/res/images/filetypes/dms_default.png"));
            }
        }

        final DocPanel dp = new DocPanel();
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
        dp.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    if (e.getSource() instanceof DocPanel) {
                        if (e.getActionCommand().equals(DocPanel.DELETE_ACTION_COMMAND)) {
                            panDocs.remove((DocPanel)e.getSource());
                            if (!newLinks.contains((DocPanel)e.getSource())) {
                                removedLinks.add(e.getSource());
                            } else {
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
        } catch (ClassNotFoundException cnfEx) {
            log.fatal("Datenbanktreiber nicht gefunden!", cnfEx);
        } catch (java.sql.SQLException sqlEx) {
            log.fatal("Fehler beim Aufbau der Datenbankverbindung!", sqlEx);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        final java.awt.GridBagConstraints gridBagConstraints;

        panDocs = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        panDocs.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        add(panDocs, java.awt.BorderLayout.CENTER);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        final DokumentenPanel d = new DokumentenPanel();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void dragEnter(final DropTargetDragEvent dtde) {
        if (!inEditMode) {
            dtde.rejectDrag();
        }
    }

    @Override
    public void dragOver(final DropTargetDragEvent dtde) {
    }

    @Override
    public void dropActionChanged(final DropTargetDragEvent dtde) {
    }

    @Override
    public void dragExit(final DropTargetEvent dte) {
    }

    @Override
    public void drop(final DropTargetDropEvent dtde) {
        dtde.acceptDrop(DnDConstants.ACTION_COPY);
        final String link = StaticSwingTools.getLinkFromDropEvent(dtde);
        if (link != null) {
            final String description = JOptionPane.showInputDialog(
                    this,
                    "Welche Beschriftung soll der Link haben?",
                    link);
            if (description != null) {
                final DocPanel dp = addNewDocPanel(ac, description, link, momentanesKassenzeichen);
                newLinks.add(dp);
                this.repaint();
            }
        }
    }

    @Override
    public void addStoreChangeStatements(final Vector v) throws NotValidException {
        Iterator it = removedLinks.iterator();
        while (it.hasNext()) {
            final Object elem = (Object)it.next();
            if (elem instanceof DocPanel) {
                ((DocPanel)elem).addDeleteStatements(v);
            }
        }
        removedLinks.removeAllElements();

        it = newLinks.iterator();
        while (it.hasNext()) {
            final Object elem = (Object)it.next();
            if (elem instanceof DocPanel) {
                ((DocPanel)elem).addNewStatements(v);
            }
        }
        newLinks.removeAllElements();
    }

    @Override
    public void enableEditing(final boolean b) {
        inEditMode = b;
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
    public void unlockDataset() {
    }

    @Override
    public boolean lockDataset() {
        return true;
    }

    @Override
    public boolean changesPending() {
//        return false;
        if ((newLinks.size() > 0) || (removedLinks.size() > 0)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isInEditMode() {
        return inEditMode;
    }
}
