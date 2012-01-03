/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 *  Copyright (C) 2010 thorsten
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
package de.cismet.verdis.search;

import Sirius.server.middleware.interfaces.domainserver.MetaService;
import Sirius.server.search.CidsServerSearch;
import de.cismet.verdis.CidsAppBackend;
import de.cismet.verdis.constants.KassenzeichenPropertyConstants;

import java.util.ArrayList;
import java.util.Collection;
import org.apache.xml.dtm.ref.dom2dtm.DOM2DTM;


/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class KassenzeichenSearchStatement extends CidsServerSearch {

    //~ Instance fields --------------------------------------------------------

    private String searchString;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new KassenzeichenSearchStatement object.
     *
     * @param  searchString  DOCUMENT ME!
     */
    public KassenzeichenSearchStatement(String searchString) {
        if (searchString != null) {
            this.searchString = searchString;
        } else {
            searchString = "-1";
        }
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection performServerSearch() {
        try {
            String sql = "";
            if (searchString.length() == 6) {
                sql = "SELECT id FROM kassenzeichen WHERE " + KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER + "/10 = " + searchString;
            } else {
                sql = "SELECT id FROM kassenzeichen WHERE " + KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER + " = " + searchString;
            }

            final MetaService ms = (MetaService)getActiveLoaclServers().get(CidsAppBackend.DOMAIN);

            final ArrayList<ArrayList> result = ms.performCustomSearch(sql);

            final ArrayList aln = new ArrayList();
            for (final ArrayList al : result) {
                final int oid = (Integer)al.get(1);
                aln.add(oid);
            }
            return aln;
        } catch (Exception e) {
            getLog().error("problem during kassenzeichen search", e);
            return null;
        }
    }
}
