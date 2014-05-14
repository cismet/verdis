/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis.gui;

import java.util.Collection;

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
public abstract class AbstractDetailsPanel extends javax.swing.JPanel implements CidsBeanStore,
    EditModeListener,
    HyperlinkListener,
    Validatable {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AbstractDetailsPanel.class);

    //~ Instance fields --------------------------------------------------------

    private final MultiBeanHelper multiBeanHelper = new MultiBeanHelper();

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
        final CidsBean dummyBean = createDummyBean();
        multiBeanHelper.setDummyBean(dummyBean);
        multiBeanHelper.setBeans(cidsBeans);

        setCidsBean(dummyBean);
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
