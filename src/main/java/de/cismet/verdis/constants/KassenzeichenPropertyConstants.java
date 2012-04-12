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
package de.cismet.verdis.constants;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public interface KassenzeichenPropertyConstants extends PropertyConstants {

    //~ Static fields/initializers ---------------------------------------------

    String PROP__FLAECHEN = "flaechen";
    String PROP__FRONTEN = "fronten";
    String PROP__GEOMETRIE = "geometrie";
    String PROP__GEOMETRIE__GEO_FIELD = PROP__GEOMETRIE + DOT + GeomPropertyConstants.PROP__GEO_FIELD;
    String PROP__KASSENZEICHENNUMMER = "kassenzeichennummer8";
    String PROP__KASSENZEICHENNUMMER_OLD = "kassenzeichennummer";
    String PROP__KANALANSCHLUSS = "kanalanschluss";
    String PROP__DATUM_VERANLAGUNG = "datum_veranlagung";
    String PROP__DATUM_ERFASSUNG = "datum_erfassung";
    String PROP__BEMERKUNG = "bemerkung";
    String PROP__SPERRE = "sperre";
    String PROP__BEMERKUNG_SPERRE = "bemerkung_sperre";
    String PROP__LETZTE_AENDERUNG_TIMESTAMP = "letzte_aenderung_ts";
    String PROP__LETZTE_AENDERUNG_USER = "letzte_aenderung_von";
}
