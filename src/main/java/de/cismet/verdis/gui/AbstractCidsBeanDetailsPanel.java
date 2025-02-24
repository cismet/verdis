/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis.gui;

import org.openide.util.Exceptions;

import java.util.Collection;

import javax.swing.SwingWorker;
import javax.swing.event.HyperlinkListener;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;

import de.cismet.cids.utils.multibean.MultiBeanHelper;

import de.cismet.validation.Validatable;

import de.cismet.verdis.EditModeListener;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public abstract class AbstractCidsBeanDetailsPanel extends javax.swing.JPanel implements CidsBeanStore,
    EditModeListener,
    HyperlinkListener,
    Validatable {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            AbstractCidsBeanDetailsPanel.class);

    //~ Instance fields --------------------------------------------------------

    private final MultiBeanHelper multiBeanHelper = new MultiBeanHelper();
    private SwingWorker previousSwingworker = null;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public abstract CidsBean createDummyBean();

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBeans  DOCUMENT ME!
     */
    public void setCidsBeans(final Collection<CidsBean> cidsBeans) {
        if ((cidsBeans == null) || cidsBeans.isEmpty()) {
            setCidsBean(null);
            return;
        }
        while ((previousSwingworker != null) && !previousSwingworker.isDone()) {
            try {
                // do not cancel the previousSwingWorker. This can lead to a race condition with and the cancelled
                // worker ends with setCidsBean(null); after the new worker.
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                // nothing to do
            }
        }
        previousSwingworker = new SwingWorker<CidsBean, Void>() {

                @Override
                protected CidsBean doInBackground() throws Exception {
                    final CidsBean dummyBean = ((cidsBeans != null) && !cidsBeans.isEmpty()) ? createDummyBean() : null;
                    multiBeanHelper.setDummyBean(dummyBean);
                    multiBeanHelper.setBeans(cidsBeans);
                    return dummyBean;
                }

                @Override
                protected void done() {
                    CidsBean dummyBean = null;
                    try {
                        dummyBean = get();
                        setCidsBean(dummyBean);
                    } catch (Exception ex) {
                        setCidsBean(null);
                        LOG.warn(ex, ex);
                    }
                }
            };
        previousSwingworker.execute();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  geoJsonFeature  DOCUMENT ME!
     */
    protected void setAnnotationGeoJsonFeature(final org.geojson.Feature geoJsonFeature) {
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected MultiBeanHelper getMultiBeanHelper() {
        return multiBeanHelper;
    }
}
