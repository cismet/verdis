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

import javax.swing.Icon;
import javax.swing.ImageIcon;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.verdis.commons.constants.VerdisConstants;

import de.cismet.verdis.gui.AbstractCidsBeanTableModel;

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
        switch (columnIndex) {
            case 0: {
                if (cidsBean.getProperty(VerdisConstants.PROP.FLAECHE.ANTEIL) != null) {
                    return MULT_IMAGE;
                }
                return null;
            }
            case 1: {
                // Bezeichnungsspalte
                return (String)cidsBean.getProperty(VerdisConstants.PROP.FLAECHE.FLAECHENBEZEICHNUNG);
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
                if (
                    cidsBean.getProperty(
                                VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                + "."
                                + VerdisConstants.PROP.FLAECHENINFO.GROESSE_KORREKTUR)
                            != null) {
                    return (Integer)cidsBean.getProperty(VerdisConstants.PROP.FLAECHE.FLAECHENINFO + "."
                                    + VerdisConstants.PROP.FLAECHENINFO.GROESSE_KORREKTUR);
                } else {
                    return (Integer)cidsBean.getProperty(VerdisConstants.PROP.FLAECHE.FLAECHENINFO + "."
                                    + VerdisConstants.PROP.FLAECHENINFO.GROESSE_GRAFIK);
                }
            }
            case 4: {
                // Flaechenart
                return (String)cidsBean.getProperty(VerdisConstants.PROP.FLAECHE.FLAECHENINFO + "."
                                + VerdisConstants.PROP.FLAECHENINFO.FLAECHENART + "."
                                + VerdisConstants.PROP.FLAECHENART.ART_ABKUERZUNG);
            }
            case 5: {
                // Anschlussgrad
                return (String)cidsBean.getProperty(
                        VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                + "."
                                + VerdisConstants.PROP.FLAECHENINFO.ANSCHLUSSGRAD
                                + "."
                                + VerdisConstants.PROP.ANSCHLUSSGRAD.GRAD_ABKUERZUNG);
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
}
