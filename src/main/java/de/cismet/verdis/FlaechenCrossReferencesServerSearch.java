/*
 *  Copyright (C) 2011 thorsten
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

import Sirius.server.search.CidsServerSearch;
import Sirius.server.middleware.interfaces.domainserver.MetaService;
import de.cismet.verdis.constants.KassenzeichenPropertyConstants;
import de.cismet.verdis.constants.RegenFlaechenPropertyConstants;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.log4j.Logger;

/**
 *
 * @author thorsten
 */


public class FlaechenCrossReferencesServerSearch extends CidsServerSearch {

    private static final transient Logger LOG = Logger.getLogger(FlaechenCrossReferencesServerSearch.class);

    String searchQuery = "";

    public FlaechenCrossReferencesServerSearch(int kzNummer) {
        searchQuery = "SELECT " +
                "    kassenzeichen1." + KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER + " AS kz1, " +
                "    flaeche1." + RegenFlaechenPropertyConstants.PROP__ID + " AS fid, " +
                "    flaeche1." + RegenFlaechenPropertyConstants.PROP__FLAECHENBEZEICHNUNG + " AS f1, " +
                "    kassenzeichen2." + KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER + " AS kz2, " +
                "    flaeche2." + RegenFlaechenPropertyConstants.PROP__FLAECHENBEZEICHNUNG + " AS f2 " +
                "FROM " +
                "    kassenzeichen AS kassenzeichen1, " +
                "    kassenzeichen AS kassenzeichen2, " +
                "    flaechen AS flaechen1, " +
                "    flaechen AS flaechen2, " +
                "    flaeche AS flaeche1, " +
                "    flaeche AS flaeche2, " +
                "    flaecheninfo AS flaecheninfo1, " +
                "    flaecheninfo AS flaecheninfo2 " +
                "WHERE " +
                "    kassenzeichen1.id = flaechen1.kassenzeichen_reference AND " +
                "    kassenzeichen2.id = flaechen2.kassenzeichen_reference AND " +
                "    flaechen1.flaeche = flaeche1.id AND " +
                "    flaechen2.flaeche = flaeche2.id AND " +
                "    flaeche1.flaecheninfo = flaecheninfo1.id AND " +
                "    flaeche2.flaecheninfo = flaecheninfo2.id AND " +
                "    flaecheninfo2.id = flaecheninfo1.id AND " +
                "    NOT flaechen2.kassenzeichen_reference = flaechen1.kassenzeichen_reference AND " +
                "    kassenzeichen1." + KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER + " = " + kzNummer;
    }

    @Override
    public Collection performServerSearch() {
        final MetaService ms = (MetaService) getActiveLoaclServers().get(CidsAppBackend.DOMAIN);
        if (ms != null) {
            try {
                final ArrayList<ArrayList> lists = ms.performCustomSearch(searchQuery);
                return lists;
            } catch (RemoteException ex) {
                LOG.fatal("error while performing custom server search", ex);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return searchQuery;
    }
}
