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
package de.cismet.cids.custom.tostringconverter.verdis_grundis;

import de.cismet.cids.tools.CustomToStringConverter;
import de.cismet.cids.tools.tostring.CidsLayerFeatureToStringConverter;

import de.cismet.cismap.cidslayer.CidsLayerFeature;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class LayerViewKassenzeichenGeometrienToStringConverter extends CustomToStringConverter
        implements CidsLayerFeatureToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        final String kassenzeichennummer = String.valueOf(cidsBean.getProperty("kassenzeichennummer"));
        final String bezeichnung = String.valueOf(cidsBean.getProperty("name"));
        return String.format("Kassenzeichen: %s::%s", kassenzeichennummer, bezeichnung);
    }

    @Override
    public String featureToString(final Object feature) {
        if (feature instanceof CidsLayerFeature) {
            final CidsLayerFeature cidsFeature = (CidsLayerFeature)feature;
            final String kassenzeichennummer = String.valueOf(cidsFeature.getProperty("kassenzeichennummer"));
            final String name = String.valueOf(cidsFeature.getProperty("name"));
            return String.format("Kassenzeichen: %s::%s", kassenzeichennummer, name);
        } else {
            return null;
        }
    }
}
