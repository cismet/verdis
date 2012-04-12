/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis.gui;
import java.util.Collection;

import javax.swing.table.DefaultTableModel;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;

import de.cismet.verdis.CidsAppBackend;

import de.cismet.verdis.constants.KassenzeichenPropertyConstants;

import de.cismet.verdis.server.search.RegenFlaechenSummenServerSearch;

/**
 * DOCUMENT ME!
 *
 * @author   hell
 * @version  $Revision$, $Date$
 */

public class RegenFlaechenSummenTableModel extends DefaultTableModel implements CidsBeanStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            RegenFlaechenSummenTableModel.class);

    //~ Instance fields --------------------------------------------------------

    private CidsBean kassenzeichenBean;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of RegenFlaechenSummenTableModel.
     */
    public RegenFlaechenSummenTableModel() {
        super(0, 2);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void emptyModel() {
        this.dataVector.removeAllElements();
        fireTableDataChanged();
    }

    @Override
    public boolean isCellEditable(final int row, final int column) {
        return false;
    }

    @Override
    public CidsBean getCidsBean() {
        return kassenzeichenBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        emptyModel();
        kassenzeichenBean = cidsBean;
        if (cidsBean != null) {
            final int kz = (Integer)kassenzeichenBean.getProperty(
                    KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER);
            final RegenFlaechenSummenServerSearch search = new RegenFlaechenSummenServerSearch(kz);

            de.cismet.tools.CismetThreadPool.execute(new javax.swing.SwingWorker<Collection, Void>() {

                    @Override
                    protected Collection doInBackground() throws Exception {
                        return CidsAppBackend.getInstance()
                                    .getProxy()
                                    .customServerSearch(CidsAppBackend.getInstance().getSession().getUser(), search);
                    }

                    @Override
                    protected void done() {
                        try {
                            final Collection result = get();
                            for (final Object row : result) {
                                final Object[] r = ((Collection)row).toArray();

                                final String bezeichner = (String)r[0];
                                final int summeNormal = ((Float)r[1]).intValue();
                                final int summeVeranlagt = ((Double)r[2]).intValue();

                                final String summe;
                                if (summeVeranlagt == 0.0) {
                                    summe = summeNormal + " m\u00B2";
                                } else {
                                    summe = summeVeranlagt + " m\u00B2";
                                }

                                addRow(
                                    new Object[] {
                                        bezeichner,
                                        summe
                                    });
                            }
                        } catch (Exception e) {
                            LOG.error("Exception in Background Thread", e);
                        }
                    }
                });
        }
    }
}
