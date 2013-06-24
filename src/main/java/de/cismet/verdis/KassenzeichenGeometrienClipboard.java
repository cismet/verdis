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

import de.cismet.verdis.commons.constants.KassenzeichenGeometriePropertyConstants;

import de.cismet.verdis.gui.KassenzeichenGeometrienList;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class KassenzeichenGeometrienClipboard extends AbstractClipboard {

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