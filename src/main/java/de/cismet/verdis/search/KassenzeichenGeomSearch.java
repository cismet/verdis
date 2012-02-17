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
import com.vividsolutions.jts.geom.Geometry;
import de.cismet.verdis.CidsAppBackend;
import de.cismet.verdis.constants.KassenzeichenPropertyConstants;
import de.cismet.verdis.constants.VerdisMetaClassConstants;
import java.util.ArrayList;
import java.util.Collection;


/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class KassenzeichenGeomSearch extends GeomServerSearch {

    @Override
    public Collection performServerSearch() {
        final Geometry searchGeometry = getGeometry();
        if (searchGeometry != null) {
            try {            
                final String sql = "SELECT " +
                    "    DISTINCT " + VerdisMetaClassConstants.MC_KASSENZEICHEN + "." + KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER + " AS kassenzeichennumer " + 
                    "FROM " +
                    "    " + VerdisMetaClassConstants.MC_KASSENZEICHEN + " AS kassenzeichen, " +
                    "    flaechen AS flaechen, " +
                    "    " + VerdisMetaClassConstants.MC_FLAECHE + " AS flaeche, " +
                    "    " + VerdisMetaClassConstants.MC_FLAECHENINFO + " AS flaecheninfo, " +
                    "    fronten AS fronten, " +
                    "    " + VerdisMetaClassConstants.MC_FRONTINFO + " AS frontinfo, " +
                    "    " + VerdisMetaClassConstants.MC_GEOM + " AS geom_kz, " +
                    "    " + VerdisMetaClassConstants.MC_GEOM + " AS geom_fl, " +
                    "    " + VerdisMetaClassConstants.MC_GEOM + " AS geom_fr " +
                    "WHERE " +                        
                    "    kassenzeichen." + KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER + " IS NOT NULL AND " +
                    "    fronten.kassenzeichen_reference = kassenzeichen.id AND " +
                    "    fronten.frontinfo = frontinfo.id AND " +
                    "    flaechen.kassenzeichen_reference = kassenzeichen.id AND " +
                    "    flaechen.flaeche = flaeche.id AND " +
                    "    flaeche.flaecheninfo = flaecheninfo.id AND " +
                    "    geom_kz.id = kassenzeichen.geometrie AND " +
                    "    geom_fl.id = flaecheninfo.geometrie AND " +
                    "    geom_fr.id = frontinfo.geometrie AND (	 " +
                    "        ST_Intersects(GeomFromText('" + searchGeometry.toText() + "', " + searchGeometry.getSRID() + "), geom_kz.geo_field) OR " +            
                    "        ST_Intersects(GeomFromText('" + searchGeometry.toText() + "', " + searchGeometry.getSRID() + "), geom_fr.geo_field) OR " +            
                    "        ST_Intersects(GeomFromText('" + searchGeometry.toText() + "', " + searchGeometry.getSRID() + "), geom_fl.geo_field) " +            
                    "    ) " +            
                    "    ORDER BY kassenzeichennumer ASC;";

                getLog().debug(sql);
                final MetaService metaService = (MetaService) getActiveLoaclServers().get(CidsAppBackend.DOMAIN);
                final ArrayList<ArrayList> result = metaService.performCustomSearch(sql);

                final ArrayList<Integer> ids = new ArrayList<Integer>();
                for (final ArrayList fields : result) {
                    ids.add((Integer)fields.get(0));
                }
                return ids;
            } catch (Exception ex) {
                getLog().error("problem during kassenzeichen geom search", ex);
                return null;
            }
        } else {
            getLog().info("searchGeometry is null, geom search is not possible");
            return null;
        }
    }
}
