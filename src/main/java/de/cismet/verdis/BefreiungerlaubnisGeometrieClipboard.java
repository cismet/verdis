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

import de.cismet.verdis.commons.constants.BefreiungerlaubnisPropertyConstants;

import de.cismet.verdis.gui.befreiungerlaubnis.BefreiungerlaubnisTable;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class BefreiungerlaubnisGeometrieClipboard extends AbstractClipboard {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FrontenClipboard object.
     *
     * @param  table  DOCUMENT ME!
     */
    public BefreiungerlaubnisGeometrieClipboard(final BefreiungerlaubnisTable table) {
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
        final BefreiungerlaubnisTable table = (BefreiungerlaubnisTable)getComponent();

        final CidsBean pasteBean = VerdisUtils.createPastedBefreiungerlaubnisGeometrieBean(
                clipboardBean,
                table.getAllBeans(),
                true);

        return pasteBean;
    }

    @Override
    public boolean isPastable(final CidsBean befreiungerlaubnisBean) {
        if (befreiungerlaubnisBean == null) {
            return false;
        }

        return !getComponent().getAllBeans().contains(befreiungerlaubnisBean);
    }
}
