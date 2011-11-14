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
package de.cismet.verdis.gui;

import Sirius.server.search.CidsServerSearch;
import Sirius.server.middleware.interfaces.domainserver.MetaService;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author thorsten
 */


public class RegenFlaechenSummenServerSearch extends CidsServerSearch {

    String searchQuery = "";

    public RegenFlaechenSummenServerSearch(int kz) {
        searchQuery = ""
                + "SELECT "
                + "    sub.bezeichner, "
                + "    sum(groesse) AS Groesse, "
                + "    round(sum(GroesseGewichtet) * 10000) / 10000 as GroesseGewichtet "
                + "FROM ( "
                + "    SELECT "
                + "        flaeche.id, "
                + "        bezeichner, "
                + "        flaecheninfo.groesse_korrektur AS Groesse, "
                + "        (flaecheninfo.groesse_korrektur * veranlagungsgrundlage.veranlagungsschluessel) AS GroesseGewichtet "
                + "    FROM "
                + "        flaechen, "
                + "        flaeche, "
                + "        flaecheninfo, "
                + "        veranlagungsgrundlage, "
                + "        kassenzeichen "
                + "    WHERE "
                + "        anteil IS null AND "
                + "        flaechen.kassenzeichen_reference = kassenzeichen.id AND "
                + "        kassenzeichen.kassenzeichennummer = " + Integer.toString(kz) + " AND "
                + "        flaechen.flaeche = flaeche.id AND "
                + "        flaeche.flaecheninfo = flaecheninfo.id AND "
                + "        flaecheninfo.flaechenart = veranlagungsgrundlage.flaechenart AND "
                + "        flaecheninfo.anschlussgrad = veranlagungsgrundlage.anschlussgrad "
                + "    UNION "
                + "    SELECT "
                + "        flaeche.id, "
                + "        bezeichner, "
                + "        flaeche.anteil AS Groesse, "
                + "        (flaeche.anteil * veranlagungsgrundlage.veranlagungsschluessel) AS GroesseGewichtet "
                + "    FROM "
                + "        flaechen, "
                + "        flaeche, "
                + "        flaecheninfo, "
                + "        veranlagungsgrundlage, "
                + "        kassenzeichen "
                + "    WHERE "
                + "        anteil IS NOT null AND "
                + "        flaechen.kassenzeichen_reference = kassenzeichen.id AND "
                + "        kassenzeichen.kassenzeichennummer = " + Integer.toString(kz) + " AND "
                + "        flaechen.flaeche = flaeche.id AND "
                + "        flaeche.flaecheninfo = flaecheninfo.id AND "
                + "        flaecheninfo.flaechenart = veranlagungsgrundlage.flaechenart AND "
                + "        flaecheninfo.anschlussgrad = veranlagungsgrundlage.anschlussgrad "
                + ") AS sub "
                + "GROUP BY bezeichner "
                + "HAVING bezeichner IS NOT null "
                + "ORDER BY 1";
    }

    @Override
    public Collection performServerSearch() {
        final MetaService ms = (MetaService) getActiveLoaclServers().get("VERDIS_GRUNDIS");
        if (ms != null) {
            try {
                final ArrayList<ArrayList> lists = ms.performCustomSearch(searchQuery);
                return lists;
            } catch (RemoteException ex) {
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return searchQuery;
    }
}
