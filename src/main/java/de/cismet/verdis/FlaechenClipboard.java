/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 *  Copyright (C) 2011 jruiz
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.cismet.verdis;

import java.io.File;

import de.cismet.cids.custom.util.VerdisUtils;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.verdis.commons.constants.VerdisConstants;

import de.cismet.verdis.gui.regenflaechen.RegenFlaechenTable;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class FlaechenClipboard extends AbstractClipboard {

    //~ Static fields/initializers ---------------------------------------------

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FlaechenClipboard.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FlaechenClipboard object.
     *
     * @param  table  DOCUMENT ME!
     * @param  file   DOCUMENT ME!
     */
    public FlaechenClipboard(final RegenFlaechenTable table, final File file) {
        super(table, file);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   clipboardBean  is an instance of Flaeche
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @Override
    public CidsBean createPastedBean(final CidsBean clipboardBean) throws Exception {
        final RegenFlaechenTable table = (RegenFlaechenTable)getComponent();

        final CidsBean pasteBean = VerdisUtils.createPastedFlaecheBean(
                clipboardBean,
                table.getAllBeans(),
                true);

        return pasteBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   clipboardFlaecheBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public boolean isPastable(final CidsBean clipboardFlaecheBean) {
        if (clipboardFlaecheBean == null) {
            return false;
        }

        for (final CidsBean flaecheBean : getComponent().getAllBeans()) {
            final int id = (Integer)flaecheBean.getProperty(VerdisConstants.PROP.FLAECHE.FLAECHENINFO + "."
                            + VerdisConstants.PROP.FLAECHENINFO.ID);
            final int ownId = (Integer)clipboardFlaecheBean.getProperty(
                    VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                            + "."
                            + VerdisConstants.PROP.FLAECHENINFO.ID);
            if (id == ownId) {
                return false;
            }
        }

        return true;
    }
}
