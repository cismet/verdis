/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * LagisCrossoverPanel.java
 *
 * Created on 03.09.2009, 09:44:54
 */
package de.cismet.verdis.gui;

import Sirius.server.middleware.types.MetaObject;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXTable;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JDialog;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.layout.FadingCardLayout;

import de.cismet.verdis.CidsAppBackend;

import de.cismet.verdis.commons.constants.VerdisConstants;

/**
 * DOCUMENT ME!
 *
 * @author   spuhl
 * @version  $Revision$, $Date$
 */
public class PopupLagisCrossoverPanel extends javax.swing.JPanel implements MouseListener, ListSelectionListener {

    //~ Static fields/initializers ---------------------------------------------

    // ToDo defaults für Panel ?
    private static final Logger log = org.apache.log4j.Logger.getLogger(PopupLagisCrossoverPanel.class);
    private static final String server = "http://localhost:";
    private static final String request = "/loadFlurstueck?";
    public static final NameValuePair PARAMETER_GEMARKUNG = new NameValuePair("gemarkung", "");
    public static final NameValuePair PARAMETER_FLUR = new NameValuePair("flur", "");
    public static final NameValuePair PARAMETER_FLURSTUECK_ZAEHLER = new NameValuePair("zaehler", "");
    public static final NameValuePair PARAMETER_FLURSTUECK_NENNER = new NameValuePair("nenner", "");
    private static final String PROGRESS_CARD_NAME = "progress";
    private static final String CONTENT_CARD_NAME = "content";
    private static final String MESSAGE_CARD_NAME = "message";

    private static final int ALKIS_LANDPARCEL_META_CLASS_ID = 169;
    private static final int ALKIS_LANDPARCEL_GEOM_SRID = 25832;

    //~ Instance fields --------------------------------------------------------

    private final FlurstueckTableModel tableModel = new FlurstueckTableModel();
    private int lagisCrossoverPort = -1;
    private final ExecutorService execService = Executors.newCachedThreadPool();
    private FadingCardLayout layout = new FadingCardLayout();
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnLoadSelectedFlurstueck;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblMessage;
    private javax.swing.JPanel panAll;
    private javax.swing.JPanel panContent;
    private javax.swing.JPanel panContentMessage;
    private javax.swing.JPanel panContentProgress;
    private javax.swing.JPanel panControl;
    private javax.swing.JProgressBar pgbProgress;
    private javax.swing.JTable tblFlurstuecke;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form LagisCrossoverPanel.
     *
     * @param  lagisCrossoverPort  DOCUMENT ME!
     */
    public PopupLagisCrossoverPanel(final int lagisCrossoverPort) {
        initComponents();
        panAll.setLayout(layout);
        panAll.removeAll();
        panAll.add(panContentProgress, PROGRESS_CARD_NAME);
        panAll.add(panContent, CONTENT_CARD_NAME);
        panAll.add(panContentMessage, MESSAGE_CARD_NAME);
        tblFlurstuecke.setModel(tableModel);
        tblFlurstuecke.addMouseListener(this);
        tblFlurstuecke.getSelectionModel().addListSelectionListener(this);
        this.lagisCrossoverPort = lagisCrossoverPort;
        pgbProgress.setIndeterminate(true);
        layout.show(panAll, PROGRESS_CARD_NAME);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    public void startSearch() {
        try {
            execService.execute(new FlurstueckRetriever());
        } catch (Exception ex) {
            log.error("Fehler während dem suchen der Flurstücke: ", ex);
            // ToDo Nachricht an benutzer
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        panControl = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();
        btnLoadSelectedFlurstueck = new javax.swing.JButton();
        panAll = new javax.swing.JPanel();
        panContentProgress = new javax.swing.JPanel();
        pgbProgress = new javax.swing.JProgressBar();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        panContentMessage = new javax.swing.JPanel();
        lblMessage = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        panContent = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblFlurstuecke = new JXTable();

        panControl.setMinimumSize(new java.awt.Dimension(50, 50));
        panControl.setPreferredSize(new java.awt.Dimension(500, 200));

        btnClose.setText(org.openide.util.NbBundle.getMessage(
                PopupLagisCrossoverPanel.class,
                "PopupLagisCrossoverPanel.btnClose.text")); // NOI18N
        btnClose.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnCloseActionPerformed(evt);
                }
            });

        btnLoadSelectedFlurstueck.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/postion.png")));      // NOI18N
        btnLoadSelectedFlurstueck.setText(org.openide.util.NbBundle.getMessage(
                PopupLagisCrossoverPanel.class,
                "PopupLagisCrossoverPanel.btnLoadSelectedFlurstueck.text"));        // NOI18N
        btnLoadSelectedFlurstueck.setToolTipText(org.openide.util.NbBundle.getMessage(
                PopupLagisCrossoverPanel.class,
                "PopupLagisCrossoverPanel.btnLoadSelectedFlurstueck.toolTipText")); // NOI18N
        btnLoadSelectedFlurstueck.setEnabled(false);
        btnLoadSelectedFlurstueck.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnLoadSelectedFlurstueckActionPerformed(evt);
                }
            });

        final javax.swing.GroupLayout panControlLayout = new javax.swing.GroupLayout(panControl);
        panControl.setLayout(panControlLayout);
        panControlLayout.setHorizontalGroup(
            panControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                javax.swing.GroupLayout.Alignment.TRAILING,
                panControlLayout.createSequentialGroup().addContainerGap(337, Short.MAX_VALUE).addComponent(
                    btnLoadSelectedFlurstueck).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnClose).addContainerGap()));
        panControlLayout.setVerticalGroup(
            panControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                panControlLayout.createSequentialGroup().addContainerGap().addGroup(
                    panControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false).addComponent(
                        btnClose,
                        javax.swing.GroupLayout.Alignment.TRAILING,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE).addComponent(
                        btnLoadSelectedFlurstueck,
                        javax.swing.GroupLayout.Alignment.TRAILING,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)).addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        panAll.setLayout(new java.awt.CardLayout());

        panContentProgress.setPreferredSize(new java.awt.Dimension(250, 140));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/searching.png"))); // NOI18N

        jLabel2.setText(org.openide.util.NbBundle.getMessage(
                PopupLagisCrossoverPanel.class,
                "PopupLagisCrossoverPanel.jLabel2.text")); // NOI18N

        final javax.swing.GroupLayout panContentProgressLayout = new javax.swing.GroupLayout(panContentProgress);
        panContentProgress.setLayout(panContentProgressLayout);
        panContentProgressLayout.setHorizontalGroup(
            panContentProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                panContentProgressLayout.createSequentialGroup().addContainerGap().addComponent(jLabel1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                    panContentProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(
                                    pgbProgress,
                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                    388,
                                    Short.MAX_VALUE).addComponent(jLabel2)).addContainerGap()));
        panContentProgressLayout.setVerticalGroup(
            panContentProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                javax.swing.GroupLayout.Alignment.TRAILING,
                panContentProgressLayout.createSequentialGroup().addContainerGap(36, Short.MAX_VALUE).addGroup(
                    panContentProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING).addGroup(
                        panContentProgressLayout.createSequentialGroup().addComponent(jLabel2).addPreferredGap(
                            javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                            pgbProgress,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE).addGap(12, 12, 12)).addComponent(
                        jLabel1,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        67,
                        javax.swing.GroupLayout.PREFERRED_SIZE)).addContainerGap()));

        panAll.add(panContentProgress, "card3");

        panContentMessage.setPreferredSize(new java.awt.Dimension(250, 140));

        lblMessage.setText(org.openide.util.NbBundle.getMessage(
                PopupLagisCrossoverPanel.class,
                "PopupLagisCrossoverPanel.lblMessage.text")); // NOI18N

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/warn.png"))); // NOI18N
        jLabel3.setText(org.openide.util.NbBundle.getMessage(
                PopupLagisCrossoverPanel.class,
                "PopupLagisCrossoverPanel.jLabel3.text"));                                                    // NOI18N

        final javax.swing.GroupLayout panContentMessageLayout = new javax.swing.GroupLayout(panContentMessage);
        panContentMessage.setLayout(panContentMessageLayout);
        panContentMessageLayout.setHorizontalGroup(
            panContentMessageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                javax.swing.GroupLayout.Alignment.TRAILING,
                panContentMessageLayout.createSequentialGroup().addContainerGap(23, Short.MAX_VALUE).addComponent(
                    jLabel3,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    61,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addGap(18, 18, 18).addComponent(
                    lblMessage,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    378,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap()));
        panContentMessageLayout.setVerticalGroup(
            panContentMessageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                panContentMessageLayout.createSequentialGroup().addGap(52, 52, 52).addGroup(
                    panContentMessageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                        jLabel3,
                        javax.swing.GroupLayout.Alignment.TRAILING,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        51,
                        Short.MAX_VALUE).addComponent(
                        lblMessage,
                        javax.swing.GroupLayout.Alignment.TRAILING,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        51,
                        Short.MAX_VALUE)).addContainerGap()));

        panAll.add(panContentMessage, "card2");

        tblFlurstuecke.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                    { null, null, null, null },
                    { null, null, null, null },
                    { null, null, null, null },
                    { null, null, null, null }
                },
                new String[] { "Title 1", "Title 2", "Title 3", "Title 4" }));
        jScrollPane1.setViewportView(tblFlurstuecke);

        final javax.swing.GroupLayout panContentLayout = new javax.swing.GroupLayout(panContent);
        panContent.setLayout(panContentLayout);
        panContentLayout.setHorizontalGroup(
            panContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                panContentLayout.createSequentialGroup().addContainerGap().addComponent(
                    jScrollPane1,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    460,
                    Short.MAX_VALUE).addContainerGap()));
        panContentLayout.setVerticalGroup(
            panContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                panContentLayout.createSequentialGroup().addContainerGap().addComponent(
                    jScrollPane1,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    83,
                    Short.MAX_VALUE).addContainerGap()));

        panAll.add(panContent, "card4");

        final javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                panAll,
                javax.swing.GroupLayout.DEFAULT_SIZE,
                500,
                Short.MAX_VALUE).addComponent(
                panControl,
                javax.swing.GroupLayout.DEFAULT_SIZE,
                javax.swing.GroupLayout.DEFAULT_SIZE,
                Short.MAX_VALUE));
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addComponent(
                    panAll,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    123,
                    Short.MAX_VALUE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                    panControl,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    51,
                    javax.swing.GroupLayout.PREFERRED_SIZE)));
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnCloseActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnCloseActionPerformed
        closeDialog();
    }                                                                            //GEN-LAST:event_btnCloseActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnLoadSelectedFlurstueckActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnLoadSelectedFlurstueckActionPerformed
        loadSelectedFlurstueck();
    }                                                                                             //GEN-LAST:event_btnLoadSelectedFlurstueckActionPerformed
    /**
     * ToDo ugly.
     */
    private void closeDialog() {
        ((JDialog)getParent().getParent().getParent().getParent()).dispose();
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
        if (log.isDebugEnabled()) {
            log.debug("Crossover: mouse clicked");
        }
        final Object source = e.getSource();
        if (source instanceof JXTable) {
            if (e.getClickCount() > 1) {
                loadSelectedFlurstueck();
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Crossover: Kein Multiclick");
                }
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Crossover: Mouselistner nicht für JXTable");
            }
        }
    }
    /**
     * ToDo place query generation in LagisCrossover. Give key get Query. ToDo maybe thread ??
     *
     * @param  bean  key DOCUMENT ME!
     */
    private void openFlurstueckInLagis(final CidsBean bean) {
        if (bean != null) {
            if ((lagisCrossoverPort < 0) || (lagisCrossoverPort > 65535)) {
                log.warn("Crossover: lagisCrossoverPort ist ungültig: " + lagisCrossoverPort);
            } else {
                // ToDo Thread
                final URL lagisQuery = createQuery(lagisCrossoverPort, bean);
                if (lagisQuery != null) {
                    final SwingWorker<Void, Void> openKassenzeichen = new SwingWorker<Void, Void>() {

                            @Override
                            protected Void doInBackground() throws Exception {
                                lagisQuery.openStream();
                                return null;
                            }

                            @Override
                            protected void done() {
                                try {
                                    get();
                                } catch (Exception ex) {
                                    log.error("Fehler beim öffnen des Kassenzeichens", ex);
                                    // ToDo message to user;
                                }
                            }
                        };
                    execService.execute(openKassenzeichen);
                } else {
                    log.warn("Crossover: konnte keine Query anlegen. Kein Abruf der Flurstücke möglich.");
                }
            }
        } else {
            log.warn("Crossover: Kann angebenes Flurstück nicht öffnwen");
        }
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
    }

    @Override
    public void mouseExited(final MouseEvent e) {
    }

    @Override
    public void mousePressed(final MouseEvent e) {
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
    }

    /**
     * DOCUMENT ME!
     */
    private void loadSelectedFlurstueck() {
        try {
            final int selectedRow = tblFlurstuecke.getSelectedRow();
            if (selectedRow != -1) {
                final int modelIndex = ((JXTable)tblFlurstuecke).convertRowIndexToModel(selectedRow);
                if (modelIndex != -1) {
                    final CidsBean bean = tableModel.getFlurstueckSchluesselAtIndex(modelIndex);
                    if (bean != null) {
                        openFlurstueckInLagis(bean);
                    } else {
                        log.warn("Crossover: Kein FlurstueckSchluessel zu angebenen Index.");
                    }
                } else {
                    log.warn("Crossover: Kein ModelIndex zu angebenen ViewIndex.");
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Crossover: Keine Tabellen zeile selektiert.");
                }
            }
        } catch (Exception ex) {
            log.error("Fehler beim laden des selektierten Flurstücks", ex);
        }
    }
    // End of variables declaration

    /**
     * DOCUMENT ME!
     *
     * @param   o  str bean DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static String convertFlurstueckNumbersToString(final Object o) {
        if (o == null) {
            return "0";
        }

        final String str = (String)o;
        return str.replaceFirst("^0+", "");
    }

    /**
     * DOCUMENT ME!
     *
     * @param   port  DOCUMENT ME!
     * @param   bean  key DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static URL createQuery(final int port, final CidsBean bean) {
        if ((port < 0) || (port > 65535)) {
            log.warn("Crossover: lagisCrossoverPort ist ungültig: " + port);
        } else {
            try {
                // ToDo ugly because is static
                PARAMETER_GEMARKUNG.setValue(String.valueOf(bean.getProperty("gemarkung")));
                PARAMETER_FLUR.setValue(convertFlurstueckNumbersToString(bean.getProperty("flur")));
                PARAMETER_FLURSTUECK_ZAEHLER.setValue(convertFlurstueckNumbersToString(
                        bean.getProperty("fstck_zaehler")));
                PARAMETER_FLURSTUECK_NENNER.setValue(convertFlurstueckNumbersToString(
                        bean.getProperty("fstck_nenner")));

                final GetMethod tmp = new GetMethod(server + port + request);
                tmp.setQueryString(
                    new NameValuePair[] {
                        PARAMETER_GEMARKUNG,
                        PARAMETER_FLUR,
                        PARAMETER_FLURSTUECK_ZAEHLER,
                        PARAMETER_FLURSTUECK_NENNER
                    });
                if (log.isDebugEnabled()) {
                    log.debug("Crossover: lagisCrossOverQuery: " + tmp.getURI().toString());
                }
                return new URL(tmp.getURI().toString());
                    // WebAccessManager.getInstance(lagisCrossoverQuery.toString());
            } catch (Exception ex) {
                log.error("Crossover: Fehler beim fernsteuern von LagIS.", ex);
            }
        }
        return null;
    }

    @Override
    public void valueChanged(final ListSelectionEvent e) {
        if (tblFlurstuecke.getSelectedRowCount() > 0) {
            btnLoadSelectedFlurstueck.setEnabled(true);
        } else {
            btnLoadSelectedFlurstueck.setEnabled(false);
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public class FlurstueckTableModel extends AbstractTableModel {

        //~ Instance fields ----------------------------------------------------

        private final String[] COLUMN_HEADER = { "Flurstücke" };
        private final ArrayList<CidsBean> data = new ArrayList<CidsBean>();

        //~ Methods ------------------------------------------------------------

        @Override
        public int getColumnCount() {
            return 1;
        }

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            final CidsBean bean = data.get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    return bean.getProperty("gemarkung")
                                + " "
                                + convertFlurstueckNumbersToString(bean.getProperty("flur"))
                                + " "
                                + convertFlurstueckNumbersToString(bean.getProperty("fstck_zaehler"))
                                + "/"
                                + convertFlurstueckNumbersToString(bean.getProperty("fstck_nenner"));
                }
                default: {
                    return "Spalte ist nicht definiert";
                }
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @param  newData  DOCUMENT ME!
         */
        public void updateTableModel(final List newData) {
            data.clear();
            if (newData != null) {
                data.addAll(newData);
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @param   index  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public CidsBean getFlurstueckSchluesselAtIndex(final int index) {
            return data.get(index);
        }

        @Override
        public String getColumnName(final int column) {
            return COLUMN_HEADER[column];
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class FlurstueckRetriever extends SwingWorker<List<CidsBean>, Void> {

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param   geom    DOCUMENT ME!
         * @param   buffer  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        private List<CidsBean> getIntersectingFlurstuecke(final Geometry geom, final double buffer) {
            final String sql = "SELECT " + ALKIS_LANDPARCEL_META_CLASS_ID + ", alkis_landparcel.id "
                        + "FROM "
                        + "   alkis_landparcel, "
                        + "   geom "
                        + "WHERE "
                        + "   geom.id = alkis_landparcel.geometrie "
                        + "   AND not ST_IsEmpty(geom.geo_field)"
                        + "   AND intersects(geom.geo_field, "
                        + "                    st_buffer(st_buffer("
                        + "st_transform( "
                        + "   geomFromText('" + geom.toText() + "'," + geom.getSRID() + ")"
                        + "," + ALKIS_LANDPARCEL_GEOM_SRID + "), "
                        + buffer + "), 0))";

            if (log.isDebugEnabled()) {
                log.debug(sql);
            }

            final MetaObject[] result = CidsAppBackend.getInstance().getMetaObject(sql, "WUNDA_BLAU");
            final ArrayList<CidsBean> beans = new ArrayList<CidsBean>((result == null) ? 0 : result.length);

            if (result != null) {
                for (int i = 0; i < result.length; i++) {
                    beans.add(result[i].getBean());
                }
            }

            return beans;
        }

        @Override
        protected List<CidsBean> doInBackground() throws Exception {
            final CidsBean currentKassenzeichen = CidsAppBackend.getInstance().getCidsBean();
            if (currentKassenzeichen != null) {
                final List<CidsBean> kgeoms = (List<CidsBean>)currentKassenzeichen.getProperty(
                        VerdisConstants.PROP.KASSENZEICHEN.KASSENZEICHEN_GEOMETRIEN);

                final List<Geometry> geoms = new ArrayList<Geometry>();
                for (final CidsBean kgeom : kgeoms) {
                    geoms.add((Geometry)kgeom.getProperty(
                            VerdisConstants.PROP.KASSENZEICHEN_GEOMETRIE.GEOMETRIE
                                    + "."
                                    + VerdisConstants.PROP.GEOM.GEO_FIELD));
                }

                if (!geoms.isEmpty()) {
                    final Geometry kassenzeichenGeom = new GeometryCollection(GeometryFactory.toGeometryArray(geoms),
                            geoms.get(0).getFactory());
                    log.info("Crossover: Geometrie zum bestimmen der Flurstücke: " + kassenzeichenGeom);
                    if (log.isDebugEnabled()) {
                        log.debug("buffer: " + CidsAppBackend.getInstance().getAppPreferences().getFlurstueckBuffer());
                    }

                    final List<CidsBean> wfsFlurstuecke = this.getIntersectingFlurstuecke(
                            kassenzeichenGeom,
                            CidsAppBackend.getInstance().getAppPreferences().getFlurstueckBuffer());

                    if ((wfsFlurstuecke.size() > 0)) {
                        if (log.isDebugEnabled()) {
                            log.debug("Crossover: Anzahl WFS Flurstücke: " + wfsFlurstuecke.size());
                        }
                        return wfsFlurstuecke;
                    } else {
                        log.info("Crossover: Keine geschnittenen Flurstücke gefunden(WFS).");
                        // ToDo Meldung an benutzer
                        lblMessage.setText(
                            "<html>Keine geschnittenen Flurstücke gefunden.</html>");
                    }
                } else {
                    // ToDo user message !
                    lblMessage.setText(
                        "<html>Keine Kassenzeichengeometrie vorhanden,<br/>bestimmen der Flurstücke nicht möglich.</html>");
                    log.warn("Crossover: Keine Geometrie vorhanden zum bestimmen der Flurstücke");
                }
            } else {
                // ToDo user message !
                lblMessage.setText(
                    "<html>Bitte wählen Sie ein Kassenzeichen aus,<br/>damit Flurstücke bestimmt werden können.</html>");
                log.warn("Crossover: Kein Kassenzeichen ausgewählt kann Lagis Flurstück nicht bestimmen");
            }
            return null;
        }

        @Override
        protected void done() {
            if (log.isDebugEnabled()) {
                log.debug("FlurstueckRetriever done.");
            }
            super.done();
            if (isCancelled()) {
                if (log.isDebugEnabled()) {
                    log.debug("FlurstueckRetriever canceled. Nothing to do");
                }
            }
            try {
                final List<CidsBean> results = get();
                if (results == null) {
                    tableModel.updateTableModel(new ArrayList(0));
                    layout.show(panAll, MESSAGE_CARD_NAME);
                } else {
                    tableModel.updateTableModel(results);
                    layout.show(panAll, CONTENT_CARD_NAME);
                }
            } catch (Exception ex) {
                log.error("Fehler beim verarbeiten der Ergebnisse: ", ex);
                tableModel.updateTableModel(new ArrayList<>());
                lblMessage.setText("<html>Fehler beim abfragen<br/>der Flurstücke.</html>");
                layout.show(panAll, MESSAGE_CARD_NAME);
            }
        }
    }
}
