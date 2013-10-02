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
package de.cismet.verdis.gui;

import java.text.SimpleDateFormat;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.verdis.commons.constants.AnschlussgradPropertyConstants;
import de.cismet.verdis.commons.constants.FlaechePropertyConstants;
import de.cismet.verdis.commons.constants.FlaechenartPropertyConstants;
import de.cismet.verdis.commons.constants.FlaecheninfoPropertyConstants;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class RegenFlaechenTableModel extends CidsBeanTableModel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            RegenFlaechenTableModel.class);
    static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    private static ImageIcon MULT_IMAGE = new javax.swing.ImageIcon(FlaechePropertyConstants.class.getResource(
                "/de/cismet/verdis/res/images/table/mult.png"));
    private static ImageIcon EDITED_IMAGE = new javax.swing.ImageIcon(FlaechePropertyConstants.class.getResource(
                "/de/cismet/verdis/res/images/table/edited.png"));
    private static ImageIcon WARN_IMAGE = new javax.swing.ImageIcon(FlaechePropertyConstants.class.getResource(
                "/de/cismet/verdis/res/images/table/warn.png"));

    private static final String[] COLUMN_NAMES = {
            " ",
            "Bezeichnung",
            " ",
            "Gr\u00F6\u00DFe in m²",
            "Fl\u00E4chenart",
            "Anschlu\u00DFgrad",
            "Erfassungsdatum"
        };

    private static final Class[] COLUMN_CLASSES = {
            Icon.class,
            String.class,
            javax.swing.Icon.class,
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
                // Bezeichnungsspalte
                if (cidsBean.getProperty(FlaechePropertyConstants.PROP__ANTEIL) != null) {
                    return MULT_IMAGE;
                }
                if ((cidsBean.getProperty(FlaechePropertyConstants.PROP__SPERRE) instanceof Boolean)
                            && (Boolean)cidsBean.getProperty(FlaechePropertyConstants.PROP__SPERRE)) {
                    return WARN_IMAGE;
                }
                return null;
            }
            case 1: {
                // Edit Icon Spalte
                // hier kommt ein Edit Icon rein wenn die Gr\u00F6\u00DFe von
                // Hand ge\u00E4ndert wurde
                return cidsBean.getProperty(FlaechePropertyConstants.PROP__FLAECHENBEZEICHNUNG);
            }
            case 2: {
                // Groesse
                // Wenn in flaecheninfo.groesse_korrektur was drinsteht
                // wird das genommen
                if (!((cidsBean.getProperty(
                                        FlaechePropertyConstants.PROP__FLAECHENINFO
                                        + "."
                                        + FlaecheninfoPropertyConstants.PROP__GROESSE_KORREKTUR)
                                    == null)
                                || cidsBean.getProperty(
                                    FlaechePropertyConstants.PROP__FLAECHENINFO
                                    + "."
                                    + FlaecheninfoPropertyConstants.PROP__GROESSE_KORREKTUR).equals(
                                    cidsBean.getProperty(
                                        FlaechePropertyConstants.PROP__FLAECHENINFO
                                        + "."
                                        + FlaecheninfoPropertyConstants.PROP__GROESSE_GRAFIK)))) {
                    return EDITED_IMAGE;
                } else {
                    return null;
                }
            }
            case 3: {
                // Flaechenart
                if (
                    cidsBean.getProperty(
                                FlaechePropertyConstants.PROP__FLAECHENINFO
                                + "."
                                + FlaecheninfoPropertyConstants.PROP__GROESSE_KORREKTUR)
                            != null) {
                    return cidsBean.getProperty(FlaechePropertyConstants.PROP__FLAECHENINFO + "."
                                    + FlaecheninfoPropertyConstants.PROP__GROESSE_KORREKTUR);
                } else {
                    return cidsBean.getProperty(FlaechePropertyConstants.PROP__FLAECHENINFO + "."
                                    + FlaecheninfoPropertyConstants.PROP__GROESSE_GRAFIK);
                }
            }
            case 4: {
                // Anschlussgrad
                return cidsBean.getProperty(FlaechePropertyConstants.PROP__FLAECHENINFO + "."
                                + FlaecheninfoPropertyConstants.PROP__FLAECHENART + "."
                                + FlaechenartPropertyConstants.PROP__ART_ABKUERZUNG);
            }
            case 5: {
                // Anschlussgrad
                return cidsBean.getProperty(
                        FlaechePropertyConstants.PROP__FLAECHENINFO
                                + "."
                                + FlaecheninfoPropertyConstants.PROP__ANSCHLUSSGRAD
                                + "."
                                + AnschlussgradPropertyConstants.PROP__GRAD_ABKUERZUNG);
            }
            case 6: {
                // Änderungsdatum
                return DATE_FORMAT.format(cidsBean.getProperty(FlaechePropertyConstants.PROP__DATUM_AENDERUNG));
            }
            default: {
                return null;
            }
        }
    }

    @Override
    public CidsBean deepcloneBean(final CidsBean cidsBean) throws Exception {
        final CidsBean deepclone = super.deepcloneBean(cidsBean);
        final CidsBean origFlaecheninfo = (CidsBean)cidsBean.getProperty(
                FlaechePropertyConstants.PROP__FLAECHENINFO);
        if (origFlaecheninfo != null) {
            deepclone.setProperty(
                FlaechePropertyConstants.PROP__FLAECHENINFO
                        + "."
                        + FlaecheninfoPropertyConstants.PROP__ANSCHLUSSGRAD,
                cidsBean.getProperty(
                    FlaechePropertyConstants.PROP__FLAECHENINFO
                            + "."
                            + FlaecheninfoPropertyConstants.PROP__ANSCHLUSSGRAD));
            deepclone.setProperty(
                FlaechePropertyConstants.PROP__FLAECHENINFO
                        + "."
                        + FlaecheninfoPropertyConstants.PROP__FLAECHENART,
                cidsBean.getProperty(
                    FlaechePropertyConstants.PROP__FLAECHENINFO
                            + "."
                            + FlaecheninfoPropertyConstants.PROP__FLAECHENART));
        }
        return deepclone;
    }
}
