/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis;

import de.cismet.cids.custom.util.VerdisUtils;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.verdis.commons.constants.FrontPropertyConstants;
import de.cismet.verdis.commons.constants.FrontinfoPropertyConstants;

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
     */
    public FrontenClipboard(final SRFrontenTable table) {
        super(table);
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
            final int id = (Integer)frontBean.getProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                            + FrontinfoPropertyConstants.PROP__ID);
            final int ownId = (Integer)clipboardFrontBean.getProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                            + FrontinfoPropertyConstants.PROP__ID);
            if (id == ownId) {
                return false;
            }
        }

        return true;
    }
}
