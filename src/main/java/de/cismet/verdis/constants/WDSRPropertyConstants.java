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
 *
 * @author jruiz
 */
public interface WDSRPropertyConstants {

    public static final String PROP__ID = "id";
    public static final String PROP__LAENGE_GRAFIK = "laenge_grafik";
    public static final String PROP__LAENGE_KORREKTUR = "laenge_korrektur";
    public static final String PROP__NUMMER = "nummer";
    public static final String PROP__ERFASSUNGSDATUM = "erfassungsdatum";
    public static final String PROP__SR_VERANLAGUNG = "sr_veranlagung";
    public static final String PROP__WD_VERANLAGUNG = "wd_veranlagung";
    public static final String PROP__GEOMETRIE = "geometrie";
    // +
    public static final String PROP__GEOMETRIE__GEO_FIELD = PROP__GEOMETRIE + ".geo_field";
    // +
    public static final String PROP__SR_KLASSE_OR = "sr_klasse_or";
    // ++
    public static final String PROP__SR_KLASSE_OR__KEY = PROP__SR_KLASSE_OR + ".key";
    public static final String PROP__SR_KLASSE_OR__SCHLUESSEL = PROP__SR_KLASSE_OR + ".schluessel";
    // +
    public static final String PROP__WD_PRIO_OR = "wd_prio_or";
    // ++
    public static final String PROP__WD_PRIO_OR__KEY = PROP__WD_PRIO_OR + ".key";
    public static final String PROP__WD_PRIO_OR__SCHLUESSEL = PROP__WD_PRIO_OR + ".schluessel";

}
