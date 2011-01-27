/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * DocPanel.java
 *
 * Created on 16. Dezember 2004, 15:39
 */
package de.cismet.gui.tools;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Iterator;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JOptionPane;

import de.cismet.tools.URLSplitter;

import de.cismet.tools.gui.dbwriter.SimpleDbAction;

import de.cismet.verdis.gui.DokumentenPanel;
/**
 * Klasse zum Anzeigen von Links und zugeh�rigen Icons in einer Anwendung.<br>
 * Bei Klick wird die URL im Webbrowser ge�ffnet.
 *
 * @author   hell
 * @version  $Revision$, $Date$
 */
public class DocPanel extends javax.swing.JPanel {

    //~ Static fields/initializers ---------------------------------------------

    public static final int MAX_DESCRIPTION_LENGTH = 12;
    public static final String DELETE_ACTION_COMMAND = "DELETE_ACTION";

    //~ Instance fields --------------------------------------------------------

    Vector actionListeners = new Vector();
    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private Icon icon;
    private String desc;
    private String gotoUrl;
    private java.applet.AppletContext appletContext = null;
    private boolean deletable = false;
    private int dms_urls_id = -1;
    private int dms_url_id = -1;
    private int url_id = -1;
    private int url_base_id = -1;
    private String kassenzeichen = "";
//    public String getToolTipText(MouseEvent e) {
//
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblDescr;
    private javax.swing.JLabel lblIcon;
    private javax.swing.JMenuItem mniDelete;
    private javax.swing.JPopupMenu pmnLink;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form DocPanel.
     */
    public DocPanel() {
        initComponents();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Setzt den Appletkontext.<br>
     * Wird dann ben�tigt falls DocPanel in einem Applett benutzt wird
     *
     * @param  appletContext  Appletkontext
     */
    public void setAplettContext(final java.applet.AppletContext appletContext) {
        this.appletContext = appletContext;
    }
    /**
     * Liefert das dargestellte Symbol zur�ck.
     *
     * @return  Icon
     */
    public Icon getIcon() {
        return lblIcon.getIcon();
    }

    /**
     * Setzt das dargestellte Symbol.
     *
     * @param  icon  Dargestelltes Symbol
     */
    public void setIcon(final Icon icon) {
        lblIcon.setIcon(icon);
    }

    /**
     * Liefert die Beschreibung.
     *
     * @return  Beschreibung
     */
    public String getDesc() {
        return this.desc;
    }

    /**
     * Setzt die Beschreibung.
     *
     * @param  desc  Beschreibung
     */
    public void setDesc(final String desc) {
        this.desc = desc;
        if (desc.length() > MAX_DESCRIPTION_LENGTH) {
            this.lblDescr.setText(desc.substring(0, MAX_DESCRIPTION_LENGTH) + "...");
            this.lblDescr.setToolTipText(desc);
        } else {
            this.lblDescr.setText(desc);
        }
    }

    /**
     * Liefert die verkn�pfte URL.
     *
     * @return  URL
     */
    public String getGotoUrl() {
        return gotoUrl;
    }

    /**
     * setzt die verkn�pfte URL.
     *
     * @param  gotoUrl  URL
     */
    public void setGotoUrl(final String gotoUrl) {
        this.gotoUrl = gotoUrl;
        lblIcon.setToolTipText(gotoUrl);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        pmnLink = new javax.swing.JPopupMenu();
        mniDelete = new javax.swing.JMenuItem();
        lblIcon = new javax.swing.JLabel();
        lblDescr = new javax.swing.JLabel();

        mniDelete.setText("Link entfernen");
        mniDelete.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniDeleteActionPerformed(evt);
                }
            });

        pmnLink.add(mniDelete);

        setLayout(new java.awt.BorderLayout());

        setMaximumSize(new java.awt.Dimension(100, 100));
        lblIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblIcon.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/filetypes/dms_default.png")));
        lblIcon.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mousePressed(final java.awt.event.MouseEvent evt) {
                    lblIconMousePressed(evt);
                }
            });

        add(lblIcon, java.awt.BorderLayout.CENTER);

        lblDescr.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDescr.setText("Beschreibung");
        lblDescr.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {

                @Override
                public void mouseMoved(final java.awt.event.MouseEvent evt) {
                    lblDescrMouseMoved(evt);
                }
            });
        lblDescr.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lblDescrMouseClicked(evt);
                }
                @Override
                public void mouseEntered(final java.awt.event.MouseEvent evt) {
                    lblDescrMouseEntered(evt);
                }
                @Override
                public void mouseExited(final java.awt.event.MouseEvent evt) {
                    lblDescrMouseExited(evt);
                }
            });

        add(lblDescr, java.awt.BorderLayout.SOUTH);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblIconMousePressed(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblIconMousePressed
        if ((evt.getButton() == evt.BUTTON3) && isDeletable()) {
            if (this.getParent().getParent() instanceof DokumentenPanel) {
                if (((DokumentenPanel)(getParent().getParent())).isInEditMode()) {
                    pmnLink.show(evt.getComponent(), evt.getX(), evt.getY());
                }
            }
        }
    }                                                                       //GEN-LAST:event_lblIconMousePressed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniDeleteActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniDeleteActionPerformed
        fireDeleteActionPerformed();
    }                                                                             //GEN-LAST:event_mniDeleteActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblDescrMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblDescrMouseClicked

        if (gotoUrl == null) {
            JOptionPane.showMessageDialog(this, "Es wurde keine Url hinterlegt!", "Fehler", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (appletContext == null) {
                de.cismet.tools.BrowserLauncher.openURL(gotoUrl);
            } else {
                final java.net.URL u = new java.net.URL(gotoUrl);
                appletContext.showDocument(u, "cismetDocPanelFrame");
            }
        } catch (Exception e) {
            log.warn("Fehler beim Oeffnen von:" + gotoUrl + "\nNeuer Versuch", e);
            // Nochmal zur Sicherheit mit dem BrowserLauncher probieren
            try {
                de.cismet.tools.BrowserLauncher.openURL(gotoUrl);
            } catch (Exception e2) {
                log.warn("Auch das 2te Mal ging schief.Fehler beim Oeffnen von:" + gotoUrl + "\nLetzter Versuch", e2);
                try {
                    gotoUrl = gotoUrl.replaceAll("\\\\", "/");
                    gotoUrl = gotoUrl.replaceAll(" ", "%20");
                    de.cismet.tools.BrowserLauncher.openURL("file:///" + gotoUrl);
                } catch (Exception e3) {
                    log.error("Auch das 3te Mal ging schief.Fehler beim �ffnen von:file://" + gotoUrl, e3);
                }
            }
        }
    } //GEN-LAST:event_lblDescrMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblDescrMouseExited(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblDescrMouseExited
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        lblDescr.setForeground(java.awt.Color.BLACK);
    }                                                                       //GEN-LAST:event_lblDescrMouseExited

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblDescrMouseEntered(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblDescrMouseEntered
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblDescr.setForeground(java.awt.Color.BLUE);
    }                                                                        //GEN-LAST:event_lblDescrMouseEntered

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblDescrMouseMoved(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblDescrMouseMoved
    }                                                                      //GEN-LAST:event_lblDescrMouseMoved

    /**
     * DOCUMENT ME!
     *
     * @param  al  DOCUMENT ME!
     */
    public void addActionListener(final ActionListener al) {
        actionListeners.add(al);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  al  DOCUMENT ME!
     */
    public void removeActionListener(final ActionListener al) {
        actionListeners.remove(al);
    }
    /**
     * DOCUMENT ME!
     */
    public void fireDeleteActionPerformed() {
        final Iterator it = actionListeners.iterator();
        final ActionEvent event = new ActionEvent(this, 0, DELETE_ACTION_COMMAND);
        while (it.hasNext()) {
            final Object elem = (Object)it.next();
            if (elem instanceof ActionListener) {
                ((ActionListener)elem).actionPerformed(event);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isDeletable() {
        return deletable;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  deletable  DOCUMENT ME!
     */
    public void setDeletable(final boolean deletable) {
        this.deletable = deletable;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  container  DOCUMENT ME!
     */
    public void addDeleteStatements(final Vector container) {
        SimpleDbAction sdba = new SimpleDbAction();
        sdba.setDescription("Link in >>DMS_URLS<< l�schen");
        sdba.setType(sdba.DELETE);
        sdba.setStatement("delete from dms_urls where id=" + dms_urls_id);
        container.add(sdba);
        sdba = new SimpleDbAction();
        sdba.setDescription("Link in >>DMS_URL<< l�schen");
        sdba.setType(sdba.DELETE);
        sdba.setStatement("delete from dms_url where id=" + dms_url_id);
        container.add(sdba);
        sdba = new SimpleDbAction() {

                @Override
                public void executeAction(final Connection conn) throws SQLException {
                    final Statement checker = conn.createStatement();
                    final ResultSet check = checker.executeQuery("SELECT count(*) FROM dms_url where url_id=" + url_id);
                    check.next();
                    final int counter = check.getInt(1);
                    if (counter == 0) {
                        super.executeAction(conn);
                    }
                }
            };
        sdba.setDescription("Link in >>URL_BASE<< l�schen");
        sdba.setType(sdba.DELETE);
        sdba.setStatement("delete from url_base where id=" + url_base_id);
        container.add(sdba);
        sdba = new SimpleDbAction() {

                @Override
                public void executeAction(final Connection conn) throws SQLException {
                    final Statement checker = conn.createStatement();
                    final ResultSet check = checker.executeQuery("SELECT count(*) FROM url where url_base_id="
                                    + url_base_id);
                    check.next();
                    final int counter = check.getInt(1);
                    if (counter == 0) {
                        super.executeAction(conn);
                    }
                }
            };
        sdba.setDescription("Link in >>URL<< l�schen");
        sdba.setType(sdba.DELETE);
        sdba.setStatement("delete from url where id=" + url_id);
        container.add(sdba);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  container  DOCUMENT ME!
     */
    public void addNewStatements(final Vector container) {
        final URLSplitter splitter = new URLSplitter(gotoUrl);
        SimpleDbAction sdba = new SimpleDbAction();
        sdba.setDescription("LINK in DMS_URLS eintragen");
        sdba.setType(sdba.INSERT);
        sdba.setStatement("INSERT INTO dms_urls "
                    + "(id,dms_url,kassenzeichen_reference)"
                    + "VALUES("
                    + "nextval('DMS_URLS_SEQ')"
                    + "," + "nextval('DMS_URL_SEQ')"
                    + "," + kassenzeichen
                    + ")");
        container.add(sdba);

        sdba = new SimpleDbAction();
        sdba.setDescription("LINK in DMS_URL eintragen");
        sdba.setType(sdba.INSERT);
        sdba.setStatement("INSERT INTO dms_url "
                    + "(id,typ,name,url_id)"
                    + "VALUES("
                    + "currval('DMS_URL_SEQ')"
                    + ",1"
                    + ",'" + this.getDesc() + "'"
                    + "," + "nextval('URL_SEQ')"
                    + ")");
        container.add(sdba);

        sdba = new SimpleDbAction();
        sdba.setDescription("LINK in URL eintragen");
        sdba.setType(sdba.INSERT);
        sdba.setStatement("INSERT INTO url "
                    + "(id,url_base_id,object_name)"
                    + "VALUES("
                    + "currval('URL_SEQ')"
                    + ",nextval('URL_BASE_SEQ')"
                    + ",'" + splitter.getObject_name().replaceAll("\\\\", "\\\\\\\\") + "'"
                    + ")");
        container.add(sdba);
        sdba = new SimpleDbAction();
        sdba.setDescription("LINK in URL eintragen");
        sdba.setType(sdba.INSERT);
        sdba.setStatement("INSERT INTO url_base "
                    + "(id,prot_prefix,server,path)"
                    + "VALUES("
                    + "currval('URL_BASE_SEQ')"
                    + ",'" + splitter.getProt_prefix().replaceAll("\\\\", "\\\\\\\\") + "'"
                    + ",'" + splitter.getServer().replaceAll("\\\\", "\\\\\\\\") + "'"
                    + ",'" + splitter.getPath().replaceAll("\\\\", "\\\\\\\\") + "'"
                    + ")");
        container.add(sdba);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  container  DOCUMENT ME!
     * @param  sdba       DOCUMENT ME!
     */
    private void add2Container(final Vector container, final SimpleDbAction sdba) {
        if (sdba != null) {
            container.add(sdba);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getDms_urls_id() {
        return dms_urls_id;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  dms_urls_id  DOCUMENT ME!
     */
    public void setDms_urls_id(final int dms_urls_id) {
        this.dms_urls_id = dms_urls_id;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getDms_url_id() {
        return dms_url_id;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  dms_url_id  DOCUMENT ME!
     */
    public void setDms_url_id(final int dms_url_id) {
        this.dms_url_id = dms_url_id;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getUrl_id() {
        return url_id;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  url_id  DOCUMENT ME!
     */
    public void setUrl_id(final int url_id) {
        this.url_id = url_id;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getUrl_base_id() {
        return url_base_id;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  url_base_id  DOCUMENT ME!
     */
    public void setUrl_base_id(final int url_base_id) {
        this.url_base_id = url_base_id;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kassenzeichen  DOCUMENT ME!
     */
    public void setKassenzeichen(final String kassenzeichen) {
        this.kassenzeichen = kassenzeichen;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getKassenzeichen() {
        return kassenzeichen;
    }
}
