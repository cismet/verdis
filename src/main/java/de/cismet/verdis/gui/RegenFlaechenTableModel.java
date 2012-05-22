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

import javax.swing.Icon;
import javax.swing.ImageIcon;

import de.cismet.cids.custom.util.CidsBeanSupport;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.verdis.commons.constants.RegenFlaechenPropertyConstants;

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
    private static ImageIcon MULT_IMAGE = new javax.swing.ImageIcon(RegenFlaechenPropertyConstants.class.getResource(
                "/de/cismet/verdis/res/images/table/mult.png"));
    private static ImageIcon EDITED_IMAGE = new javax.swing.ImageIcon(RegenFlaechenPropertyConstants.class.getResource(
                "/de/cismet/verdis/res/images/table/edited.png"));
    private static ImageIcon WARN_IMAGE = new javax.swing.ImageIcon(RegenFlaechenPropertyConstants.class.getResource(
                "/de/cismet/verdis/res/images/table/warn.png"));

    private static final String[] COLUMN_NAMES = {
            " ",
            "Bezeichnung",
            " ",
            "Gr\u00F6\u00DFe in m²",
            "Fl\u00E4chenart",
            "Anschlu\u00DFgrad"
        };

    private static final Class[] COLUMN_CLASSES = {
            Icon.class,
            String.class,
            javax.swing.Icon.class,
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
        if (columnIndex == 0) {
            if (cidsBean.getProperty(RegenFlaechenPropertyConstants.PROP__ANTEIL) != null) {
                return MULT_IMAGE;
            }
            if ((cidsBean.getProperty(RegenFlaechenPropertyConstants.PROP__SPERRE) instanceof Boolean)
                        && (Boolean)cidsBean.getProperty(RegenFlaechenPropertyConstants.PROP__SPERRE)) {
                return WARN_IMAGE;
            }
            return null;
        } // Bezeichnungsspalte
        else if (columnIndex == 1) {
            return cidsBean.getProperty(RegenFlaechenPropertyConstants.PROP__FLAECHENBEZEICHNUNG);
        } // Edit Icon Spalte
        // hier kommt ein Edit Icon rein wenn die Gr\u00F6\u00DFe von
        // Hand ge\u00E4ndert wurde
        else if (columnIndex == 2) {
            if (!((cidsBean.getProperty(RegenFlaechenPropertyConstants.PROP__FLAECHENINFO__GROESSE_KORREKTUR)
                                == null)
                            || cidsBean.getProperty(
                                RegenFlaechenPropertyConstants.PROP__FLAECHENINFO__GROESSE_KORREKTUR).equals(
                                cidsBean.getProperty(
                                    RegenFlaechenPropertyConstants.PROP__FLAECHENINFO__GROESSE_GRAFIK)))) {
                return EDITED_IMAGE;
            } else {
                return null;
            }
        } // Groesse
        // Wenn in flaecheninfo.groesse_korrektur was drinsteht
        // wird das genommen
        else if (columnIndex == 3) {
            if (cidsBean.getProperty(RegenFlaechenPropertyConstants.PROP__FLAECHENINFO__GROESSE_KORREKTUR) != null) {
                return cidsBean.getProperty(RegenFlaechenPropertyConstants.PROP__FLAECHENINFO__GROESSE_KORREKTUR);
            } else {
                return cidsBean.getProperty(RegenFlaechenPropertyConstants.PROP__FLAECHENINFO__GROESSE_GRAFIK);
            }
        } // Flaechenart
        else if (columnIndex == 4) {
            return cidsBean.getProperty(RegenFlaechenPropertyConstants.PROP__FLAECHENINFO__FLAECHENART__ABKUERZUNG);
        } // Anschlussgrad
        else if (columnIndex == 5) {
            return cidsBean.getProperty(RegenFlaechenPropertyConstants.PROP__FLAECHENINFO__ANSCHLUSSGRAD__ABKUERZUNG);
        } else {
            return null;
        }
    }

    @Override
    public CidsBean deepcloneBean(final CidsBean cidsBean) throws Exception {
        final CidsBean deepclone = super.deepcloneBean(cidsBean);
        final CidsBean origFlaecheninfo = (CidsBean)cidsBean.getProperty(
                RegenFlaechenPropertyConstants.PROP__FLAECHENINFO);
        if (origFlaecheninfo != null) {
            deepclone.setProperty(
                RegenFlaechenPropertyConstants.PROP__FLAECHENINFO__ANSCHLUSSGRAD,
                cidsBean.getProperty(RegenFlaechenPropertyConstants.PROP__FLAECHENINFO__ANSCHLUSSGRAD));
            deepclone.setProperty(
                RegenFlaechenPropertyConstants.PROP__FLAECHENINFO__FLAECHENART,
                cidsBean.getProperty(RegenFlaechenPropertyConstants.PROP__FLAECHENINFO__FLAECHENART));
        }
        return deepclone;
    }
}
