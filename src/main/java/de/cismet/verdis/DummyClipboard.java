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

import java.util.Arrays;
import java.util.Collection;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class DummyClipboard extends AbstractClipboard {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new DummyClipboard object.
     *
     * @param  fromKassenzeichen  DOCUMENT ME!
     * @param  isCutted           DOCUMENT ME!
     * @param  clipboardBeans     DOCUMENT ME!
     */
    public DummyClipboard(final Integer fromKassenzeichen,
            final boolean isCutted,
            final Collection<CidsBean> clipboardBeans) {
        super(null, null);
        setFromKassenzeichen(fromKassenzeichen);
        setIsCutted(isCutted);
        getClipboardBeans().addAll(clipboardBeans);
    }

    /**
     * Creates a new DummyClipboard object.
     *
     * @param  fromKassenzeichen  DOCUMENT ME!
     * @param  isCutted           DOCUMENT ME!
     * @param  clipboardBeans     DOCUMENT ME!
     */
    public DummyClipboard(final Integer fromKassenzeichen,
            final boolean isCutted,
            final CidsBean[] clipboardBeans) {
        super(null, null);
        setFromKassenzeichen(fromKassenzeichen);
        setIsCutted(isCutted);
        getClipboardBeans().addAll(Arrays.asList(clipboardBeans));
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public CidsBean createPastedBean(final CidsBean clipboardBean) throws Exception {
        return null;
    }

    @Override
    public boolean isPastable(final CidsBean clipboardFlaecheBean) {
        return false;
    }
}
