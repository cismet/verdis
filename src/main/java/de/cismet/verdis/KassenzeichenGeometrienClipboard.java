/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis;

import de.cismet.cids.custom.util.CidsBeanSupport;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.verdis.commons.constants.GeomPropertyConstants;
import de.cismet.verdis.commons.constants.KassenzeichenGeometriePropertyConstants;
import de.cismet.verdis.commons.constants.KassenzeichenPropertyConstants;

import de.cismet.verdis.gui.KassenzeichenGeometrienList;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class KassenzeichenGeometrienClipboard extends AbstractClipboard {

    //~ Static fields/initializers ---------------------------------------------

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            KassenzeichenGeometrienClipboard.class);

    //~ Instance fields --------------------------------------------------------

    private int newBeanId = 0;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new KassenzeichenGeometrienClipboard object.
     *
     * @param  component  DOCUMENT ME!
     */
    public KassenzeichenGeometrienClipboard(final KassenzeichenGeometrienList component) {
        super(component);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public CidsBean createPastedBean(final CidsBean clipboardBean) throws Exception {
        final CidsBean pasteBean = CidsBeanSupport.deepcloneCidsBean(clipboardBean);
        final int id = getNextNewBeanId();
        pasteBean.setProperty(KassenzeichenGeometriePropertyConstants.PROP__ID, id);
        pasteBean.getMetaObject().setID(id);

        final int geoId = getNextNewBeanId();
        final CidsBean geometrie = (CidsBean)pasteBean.getProperty(
                KassenzeichenGeometriePropertyConstants.PROP__GEOMETRIE);
        geometrie.getMetaObject().setID(geoId);
        geometrie.setProperty(GeomPropertyConstants.PROP__ID, geoId);

        return pasteBean;
    }

    @Override
    public boolean isPastable(final CidsBean clipboardBean) {
        if (clipboardBean == null) {
            return false;
        }

        for (final CidsBean geometrieBean : getComponent().getAllBeans()) {
            final int id = (Integer)geometrieBean.getProperty(KassenzeichenGeometriePropertyConstants.PROP__ID);
            final int ownId = (Integer)clipboardBean.getProperty(KassenzeichenGeometriePropertyConstants.PROP__ID);
            if (id == ownId) {
                return false;
            }
        }

        return true;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getNextNewBeanId() {
        return --newBeanId;
    }
}
