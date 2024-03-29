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

import de.cismet.verdis.gui.srfronten.SRFrontenTable;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class FrontenClipboard extends AbstractClipboard {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FrontenClipboard object.
     *
     * @param  table  DOCUMENT ME!
     * @param  file   DOCUMENT ME!
     */
    public FrontenClipboard(final SRFrontenTable table, final File file) {
        super(table, file);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   clipboardBean  is an instance of Front
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @Override
    public CidsBean createPastedBean(final CidsBean clipboardBean) throws Exception {
        final SRFrontenTable table = (SRFrontenTable)getComponent();

        final CidsBean pasteBean = VerdisUtils.createPastedFrontBean(
                clipboardBean,
                table.getAllBeans(),
                true);

        return pasteBean;
    }

    @Override
    public boolean isPastable(final CidsBean clipboardFrontBean) {
        if (clipboardFrontBean == null) {
            return false;
        }

        for (final CidsBean frontBean : getComponent().getAllBeans()) {
            final int id = (Integer)frontBean.getProperty(VerdisConstants.PROP.FRONT.FRONTINFO + "."
                            + VerdisConstants.PROP.FRONTINFO.ID);
            final int ownId = (Integer)clipboardFrontBean.getProperty(VerdisConstants.PROP.FRONT.FRONTINFO + "."
                            + VerdisConstants.PROP.FRONTINFO.ID);
            if (id == ownId) {
                return false;
            }
        }

        return true;
    }
}
