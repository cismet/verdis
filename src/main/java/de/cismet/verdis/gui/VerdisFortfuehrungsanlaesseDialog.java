/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.verdis.gui;

import Sirius.navigator.connection.SessionManager;

import Sirius.server.middleware.types.MetaClass;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import java.sql.Timestamp;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.alkisfortfuehrung.FortfuehrungItem;
import de.cismet.cids.custom.alkisfortfuehrung.FortfuehrungsanlaesseDialog;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.search.CidsServerSearch;

import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.tools.gui.StaticSwingTools;

import de.cismet.verdis.CidsAppBackend;

import de.cismet.verdis.commons.constants.FortfuehrungPropertyConstants;
import de.cismet.verdis.commons.constants.VerdisConstants;
import de.cismet.verdis.commons.constants.VerdisMetaClassConstants;

import de.cismet.verdis.server.search.KassenzeichenGeomSearch;
import de.cismet.verdis.server.search.VerdisFortfuehrungItemSearch;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class VerdisFortfuehrungsanlaesseDialog extends FortfuehrungsanlaesseDialog {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(VerdisFortfuehrungsanlaesseDialog.class);

    private static VerdisFortfuehrungsanlaesseDialog INSTANCE = null;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox cbxAbgearbeitet;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList lstKassenzeichen;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form VerdisFortfuehrungsanlaesseDialog.
     *
     * @param  parent  DOCUMENT ME!
     * @param  modal   DOCUMENT ME!
     */
    public VerdisFortfuehrungsanlaesseDialog(final java.awt.Frame parent, final boolean modal) {
        super(parent, modal, ConnectionContext.createDeprecated());
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected JPanel getObjectsPanel() {
        if (jPanel1 == null) {
            initComponents();
        }
        return jPanel1;
    }

    @Override
    protected String getLinkFormat() {
        return CidsAppBackend.getInstance().getAppPreferences().getFortfuehrungLinkFormat();
    }

    /**
     * DOCUMENT ME!
     */
    private void gotoSelectedKassenzeichen() {
        final int kassenzeichennummer = (Integer)lstKassenzeichen.getSelectedValue();
        CidsAppBackend.getInstance().gotoKassenzeichen(Integer.toString(kassenzeichennummer));
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kassenzeichennummern  DOCUMENT ME!
     */
    @Override
    protected void setObjects(final Collection kassenzeichennummern) {
        if (kassenzeichennummern == null) {
            cbxAbgearbeitet.setSelected(false);
        } else {
            final DefaultListModel kassenzeichenListModel = (DefaultListModel)lstKassenzeichen.getModel();
            kassenzeichenListModel.removeAllElements();

            if (kassenzeichennummern != null) {
                for (final Integer kassenzeichennummer : (Collection<Integer>)kassenzeichennummern) {
                    kassenzeichenListModel.addElement(kassenzeichennummer);
                }
            }
        }
    }

    @Override
    protected void setDetailEnabled(final boolean enabled) {
        lstKassenzeichen.setEnabled(enabled);
        cbxAbgearbeitet.setEnabled(enabled);
    }

    @Override
    protected FortfuehrungItem createFortfuehrungItem(final Object[] rawItem) {
        return new FortfuehrungItem((Integer)rawItem[VerdisFortfuehrungItemSearch.FIELD_ID],
                (String)rawItem[VerdisFortfuehrungItemSearch.FIELD_FFN],
                (String)rawItem[VerdisFortfuehrungItemSearch.FIELD_ANLASSNAME],
                (Date)rawItem[VerdisFortfuehrungItemSearch.FIELD_BEGINN],
                (String)rawItem[VerdisFortfuehrungItemSearch.FIELD_FS_ALT],
                (String)rawItem[VerdisFortfuehrungItemSearch.FIELD_FS_NEU],
                (Integer)rawItem[VerdisFortfuehrungItemSearch.FIELD_FORTFUEHRUNG_ID]);
    }

    @Override
    protected void searchObjects(final Geometry geom) {
        if (geom == null) {
            lstKassenzeichen.setEnabled(false);
            cbxAbgearbeitet.setEnabled(false);
        } else {
            new SwingWorker<Collection<Integer>, Void>() {

                    @Override
                    protected Collection<Integer> doInBackground() throws Exception {
                        final KassenzeichenGeomSearch geomSearch = new KassenzeichenGeomSearch();
                        final Geometry searchGeom = geom.buffer(0);
                        ;
                        final int currentSrid = CrsTransformer.getCurrentSrid();
                        searchGeom.setSRID(currentSrid);
                        geomSearch.setGeometry(searchGeom);
                        final Collection<Integer> result = (Collection<Integer>)SessionManager.getProxy()
                                    .customServerSearch(SessionManager.getSession().getUser(), geomSearch);
                        return result;
                    }

                    @Override
                    protected void done() {
                        try {
                            final FortfuehrungItem selectedFortfuehrungItem = getSelectedFortfuehrungItem();

                            final Collection<Integer> kassenzeichennummern = get();
                            setDetailEnabled(true);
                            setObjects(kassenzeichennummern);

                            cbxAbgearbeitet.setSelected(selectedFortfuehrungItem.isIst_abgearbeitet());

                            final String ffn = selectedFortfuehrungItem.getFfn();
                            final Calendar cal = new GregorianCalendar();
                            cal.setTime(selectedFortfuehrungItem.getBeginn());
                            final int year = cal.get(Calendar.YEAR);

                            final String urlFormat = getLinkFormat();
                            final String urlString = String.format(
                                    urlFormat,
                                    year,
                                    ffn.substring(2, 6),
                                    ffn.substring(6, 11));
                            setDokumentLink(urlString);
                        } catch (final Exception ex) {
                            setObjects(null);
                            cbxAbgearbeitet.setSelected(false);
                            LOG.fatal("", ex);
                        }
                        lstKassenzeichen.setEnabled(true);
                        cbxAbgearbeitet.setEnabled(true);
                        searchDone();
                    }
                }.execute();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static VerdisFortfuehrungsanlaesseDialog getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new VerdisFortfuehrungsanlaesseDialog(StaticSwingTools.getParentFrame(
                        CismapBroker.getInstance().getMappingComponent()),
                    false);
        }
        return INSTANCE;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstKassenzeichen = new javax.swing.JList();
        jButton1 = new javax.swing.JButton();
        cbxAbgearbeitet = new javax.swing.JCheckBox();

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    VerdisFortfuehrungsanlaesseDialog.class,
                    "VerdisFortfuehrungsanlaesseDialog.jPanel4.border.title"))); // NOI18N
        jPanel4.setLayout(new java.awt.GridBagLayout());

        lstKassenzeichen.setModel(new DefaultListModel());
        lstKassenzeichen.setEnabled(false);
        lstKassenzeichen.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lstKassenzeichenMouseClicked(evt);
                }
            });
        lstKassenzeichen.addListSelectionListener(new javax.swing.event.ListSelectionListener() {

                @Override
                public void valueChanged(final javax.swing.event.ListSelectionEvent evt) {
                    lstKassenzeichenValueChanged(evt);
                }
            });
        jScrollPane2.setViewportView(lstKassenzeichen);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel4.add(jScrollPane2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton1,
            org.openide.util.NbBundle.getMessage(
                VerdisFortfuehrungsanlaesseDialog.class,
                "VerdisFortfuehrungsanlaesseDialog.jButton1.text")); // NOI18N
        jButton1.setEnabled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        jPanel4.add(jButton1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jPanel4, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            cbxAbgearbeitet,
            org.openide.util.NbBundle.getMessage(
                VerdisFortfuehrungsanlaesseDialog.class,
                "VerdisFortfuehrungsanlaesseDialog.cbxAbgearbeitet.text")); // NOI18N
        cbxAbgearbeitet.setEnabled(false);
        cbxAbgearbeitet.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cbxAbgearbeitetActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel1.add(cbxAbgearbeitet, gridBagConstraints);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        final javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 400, Short.MAX_VALUE));
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 300, Short.MAX_VALUE));

        pack();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstKassenzeichenMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lstKassenzeichenMouseClicked
        if (evt.getClickCount() == 2) {
            if (lstKassenzeichen.getSelectedValue() != null) {
                gotoSelectedKassenzeichen();
            }
        }
    }                                                                                //GEN-LAST:event_lstKassenzeichenMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstKassenzeichenValueChanged(final javax.swing.event.ListSelectionEvent evt) { //GEN-FIRST:event_lstKassenzeichenValueChanged
        jButton1.setEnabled(!lstKassenzeichen.getSelectionModel().isSelectionEmpty());
    }                                                                                           //GEN-LAST:event_lstKassenzeichenValueChanged

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton1ActionPerformed
        gotoSelectedKassenzeichen();
    }                                                                            //GEN-LAST:event_jButton1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbxAbgearbeitetActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cbxAbgearbeitetActionPerformed
        final FortfuehrungItem selectedFortfuehrungItem = getSelectedFortfuehrungItem();

        final boolean istAbgearbeitet = cbxAbgearbeitet.isSelected();
        new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    try {
                        if (istAbgearbeitet) {
                            final CidsBean fortfuehrungBean = CidsBean.createNewCidsBeanFromTableName(
                                    VerdisConstants.DOMAIN,
                                    VerdisMetaClassConstants.MC_FORTFUEHRUNG);
                            fortfuehrungBean.setProperty(
                                FortfuehrungPropertyConstants.PROP__ALKIS_FFN_ID,
                                selectedFortfuehrungItem.getAnlassId());
                            fortfuehrungBean.setProperty(
                                FortfuehrungPropertyConstants.PROP__ALKIS_FFN,
                                selectedFortfuehrungItem.getFfn());
                            fortfuehrungBean.setProperty(
                                FortfuehrungPropertyConstants.PROP__ABGEARBEITET_AM,
                                new Timestamp(new Date().getTime()));
                            fortfuehrungBean.setProperty(
                                FortfuehrungPropertyConstants.PROP__ABGEARBEITET_VON,
                                SessionManager.getSession().getUser().getName());
                            final CidsBean persisted = fortfuehrungBean.persist();
                            selectedFortfuehrungItem.setFortfuehrungId(persisted.getMetaObject().getId());
                        } else {
                            final MetaClass mc = CidsAppBackend.getInstance()
                                        .getVerdisMetaClass(VerdisMetaClassConstants.MC_FORTFUEHRUNG);
                            final CidsBean fortfuehrungBean = CidsAppBackend.getInstance()
                                        .getVerdisMetaObject(selectedFortfuehrungItem.getFortfuehrungId(), mc.getId())
                                        .getBean();
                            fortfuehrungBean.delete();
                            fortfuehrungBean.persist();
                            selectedFortfuehrungItem.setFortfuehrungId(null);
                        }
                    } catch (Exception ex) {
                        LOG.error("fehler beim setzen von ist_abgearbeitet", ex);
                    }
                    return null;
                }

                @Override
                protected void done() {
                    refreshFortfuehrungsList();
                }
            }.execute();
    } //GEN-LAST:event_cbxAbgearbeitetActionPerformed

    @Override
    protected CidsServerSearch createFortfuehrungItemSearch(final Date fromDate, final Date toDate) {
        return new VerdisFortfuehrungItemSearch(fromDate, toDate);
    }
}
