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
package de.cismet.verdis.gui.regenflaechen;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.verdis.commons.constants.VerdisConstants;

import de.cismet.verdis.gui.AbstractCidsBeanTableModel;

import de.cismet.verdis.server.json.FlaecheAenderungJson;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class RegenFlaechenTableModel extends AbstractCidsBeanTableModel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            RegenFlaechenTableModel.class);
    static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    private static final ImageIcon MULT_IMAGE = new javax.swing.ImageIcon(RegenFlaechenTableModel.class.getResource(
                "/de/cismet/verdis/res/images/table/mult.png"));
    private static final ImageIcon EDITED_IMAGE = new javax.swing.ImageIcon(RegenFlaechenTableModel.class.getResource(
                "/de/cismet/verdis/res/images/table/edited.png"));
    private static final ImageIcon WARN_IMAGE = new javax.swing.ImageIcon(RegenFlaechenTableModel.class.getResource(
                "/de/cismet/verdis/res/images/table/warn.png"));

    private static final String[] COLUMN_NAMES = {
            " ",
            "Bezeichnung",
            " ",
            "Gr\u00F6\u00DFe in m²",
            "Fl\u00E4chenart",
            "Anschlu\u00DFgrad",
            "Beschreibung",
            "Erfassungsdatum"
        };

    private static final Class[] COLUMN_CLASSES = {
            Icon.class,
            String.class,
            Icon.class,
            Integer.class,
            String.class,
            String.class,
            String.class,
            String.class
        };

    //~ Instance fields --------------------------------------------------------

    private Map<String, FlaecheAenderungJson> aenderungsanfrageFlaechen = null;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RegenFlaechenTableModel object.
     */
    public RegenFlaechenTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        final CidsBean cidsBean = getCidsBeanByIndex(rowIndex);
        if (cidsBean == null) {
            return null;
        }

        final String bezeichnung = (String)cidsBean.getProperty(VerdisConstants.PROP.FLAECHE.FLAECHENBEZEICHNUNG);
        final FlaecheAenderungJson aenderungsanfrageFlaeche =
            ((aenderungsanfrageFlaechen != null) && aenderungsanfrageFlaechen.containsKey(bezeichnung))
            ? aenderungsanfrageFlaechen.get(bezeichnung) : null;

        switch (columnIndex) {
            case 0: {
                if (cidsBean.getProperty(VerdisConstants.PROP.FLAECHE.ANTEIL) != null) {
                    return MULT_IMAGE;
                }
                return null;
            }
            case 1: {
                // Bezeichnungsspalte
                return bezeichnung;
            }
            case 2: {
                // Edit Icon Spalte
                // hier kommt ein Edit Icon rein wenn die Gr\u00F6\u00DFe von
                // Hand ge\u00E4ndert wurde
                if (!((cidsBean.getProperty(
                                        VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                        + "."
                                        + VerdisConstants.PROP.FLAECHENINFO.GROESSE_KORREKTUR)
                                    == null)
                                || cidsBean.getProperty(
                                    VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                    + "."
                                    + VerdisConstants.PROP.FLAECHENINFO.GROESSE_KORREKTUR).equals(
                                    cidsBean.getProperty(
                                        VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                        + "."
                                        + VerdisConstants.PROP.FLAECHENINFO.GROESSE_GRAFIK)))) {
                    return EDITED_IMAGE;
                } else {
                    return null;
                }
            }
            case 3: {
                // Größe
                final Integer groesse;
                if (
                    cidsBean.getProperty(
                                VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                + "."
                                + VerdisConstants.PROP.FLAECHENINFO.GROESSE_KORREKTUR)
                            != null) {
                    groesse = (Integer)cidsBean.getProperty(VerdisConstants.PROP.FLAECHE.FLAECHENINFO + "."
                                    + VerdisConstants.PROP.FLAECHENINFO.GROESSE_KORREKTUR);
                } else {
                    groesse = (Integer)cidsBean.getProperty(VerdisConstants.PROP.FLAECHE.FLAECHENINFO + "."
                                    + VerdisConstants.PROP.FLAECHENINFO.GROESSE_GRAFIK);
                }
                final Integer groesseAenderung =
                    ((aenderungsanfrageFlaeche != null) && !Boolean.TRUE.equals(aenderungsanfrageFlaeche.getDraft()))
                    ? aenderungsanfrageFlaeche.getGroesse() : null;
                return ((groesseAenderung != null) ? (groesse + " (" + groesseAenderung + ")") : groesse);
            }
            case 4: {
                // Flaechenart
                final String flaechenart = (String)cidsBean.getProperty(VerdisConstants.PROP.FLAECHE.FLAECHENINFO + "."
                                + VerdisConstants.PROP.FLAECHENINFO.FLAECHENART + "."
                                + VerdisConstants.PROP.FLAECHENART.ART_ABKUERZUNG);
                final String flaechenartAenderung =
                    (((aenderungsanfrageFlaeche != null) && !Boolean.TRUE.equals(aenderungsanfrageFlaeche.getDraft()))
                                && (aenderungsanfrageFlaeche.getFlaechenart() != null))
                    ? aenderungsanfrageFlaeche.getFlaechenart().getArtAbkuerzung() : null;
                return flaechenart + ((flaechenartAenderung != null) ? (" (" + flaechenartAenderung + ")") : "");
            }
            case 5: {
                // Anschlussgrad
                final String anschlussgrad = (String)cidsBean.getProperty(
                        VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                + "."
                                + VerdisConstants.PROP.FLAECHENINFO.ANSCHLUSSGRAD
                                + "."
                                + VerdisConstants.PROP.ANSCHLUSSGRAD.GRAD_ABKUERZUNG);
                final String anschlussgradAenderung =
                    (((aenderungsanfrageFlaeche != null) && !Boolean.TRUE.equals(aenderungsanfrageFlaeche.getDraft()))
                                && (aenderungsanfrageFlaeche.getAnschlussgrad() != null))
                    ? aenderungsanfrageFlaeche.getAnschlussgrad().getGradAbkuerzung() : null;
                return anschlussgrad + ((anschlussgradAenderung != null) ? (" (" + anschlussgradAenderung + ")") : "");
            }
            case 6: {
                // Beschreibung
                return (String)cidsBean.getProperty(
                        VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                + "."
                                + VerdisConstants.PROP.FLAECHENINFO.BESCHREIBUNG
                                + ".beschreibung");
            }
            case 7: {
                // Änderungsdatum
                final Date datum_erfassung = (Date)cidsBean.getProperty(VerdisConstants.PROP.FLAECHE.DATUM_AENDERUNG);
                if (datum_erfassung == null) {
                    return null;
                } else {
                    return (String)DATE_FORMAT.format(datum_erfassung);
                }
            }
            default: {
                return null;
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  aenderungsanfrageFlaechen  DOCUMENT ME!
     */
    public void setAenderungsanfrageFlaechen(final Map<String, FlaecheAenderungJson> aenderungsanfrageFlaechen) {
        this.aenderungsanfrageFlaechen = aenderungsanfrageFlaechen;
    }
}
