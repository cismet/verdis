/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis;

import java.io.File;

import de.cismet.cids.custom.util.VerdisUtils;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.verdis.commons.constants.VerdisConstants;

import de.cismet.verdis.gui.kassenzeichen_geometrie.KassenzeichenGeometrienList;

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
     * @param  file       DOCUMENT ME!
     */
    public KassenzeichenGeometrienClipboard(final KassenzeichenGeometrienList component, final File file) {
        super(component, file);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public CidsBean createPastedBean(final CidsBean clipboardBean) throws Exception {
        final CidsBean pasteBean = VerdisUtils.createPastedInfoBean(
                clipboardBean);
        return pasteBean;
    }

    @Override
    public boolean isPastable(final CidsBean clipboardBean) {
        if (clipboardBean == null) {
            return false;
        }

        for (final CidsBean geometrieBean : getComponent().getAllBeans()) {
            final int id = (Integer)geometrieBean.getProperty(VerdisConstants.PROP.KASSENZEICHEN_GEOMETRIE.ID);
            final int ownId = (Integer)clipboardBean.getProperty(VerdisConstants.PROP.KASSENZEICHEN_GEOMETRIE.ID);
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
