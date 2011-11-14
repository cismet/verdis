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

import de.cismet.verdis.constants.KassenzeichenPropertyConstants;
import javax.swing.ImageIcon;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.verdis.constants.RegenFlaechenPropertyConstants;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class RegenFlaechenTableModel extends CidsBeanTableModel implements RegenFlaechenPropertyConstants {

    //~ Instance fields --------------------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RegenFlaechenTableModel.class);
    
    private String[] columnNames = {
            " ",
            "Bezeichnung",
            " ",
            "Gr\u00F6\u00DFe in m²",
            "Fl\u00E4chenart",
            "Anschlu\u00DFgrad"
        };
    private ImageIcon mult = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/verdis/res/images/table/mult.png"));
    private ImageIcon edited = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/verdis/res/images/table/edited.png"));
    private ImageIcon warn = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/verdis/res/images/table/warn.png"));

    //~ Methods ----------------------------------------------------------------

    @Override
    public String getBeansProperty() {
        return KassenzeichenPropertyConstants.PROP__FLAECHEN;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(final int column) {
        return columnNames[column];
    }

    @Override
    public Class getColumnClass(final int column) {
        if ((column == 0) || (column == 2)) {
            return javax.swing.Icon.class;
        } else {
            return String.class;
        }
    }

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        final CidsBean cidsBean = (CidsBean) getCidsBeans().get(rowIndex);
        if (columnIndex == 0) {
            if (cidsBean.getProperty(PROP__ANTEIL) != null) {
                return mult;
            }
            if ((cidsBean.getProperty(PROP__SPERRE) instanceof Boolean) && (Boolean)cidsBean.getProperty(PROP__SPERRE)) {
                return warn;
            }
            return null;
        } // Bezeichnungsspalte
        else if (columnIndex == 1) {
            return cidsBean.getProperty(PROP__FLAECHENBEZEICHNUNG);
        } // Edit Icon Spalte
        // hier kommt ein Edit Icon rein wenn die Gr\u00F6\u00DFe von
        // Hand ge\u00E4ndert wurde
        else if (columnIndex == 2) {
            if (!((cidsBean.getProperty(PROP__FLAECHENINFO__GROESSE_KORREKTUR) == null)
                            || cidsBean.getProperty(PROP__FLAECHENINFO__GROESSE_KORREKTUR).equals(
                                cidsBean.getProperty(PROP__FLAECHENINFO__GROESSE_GRAFIK)))) {
                return edited;
            } else {
                return null;
            }
        } // Groesse
        // Wenn in flaecheninfo.groesse_korrektur was drinsteht
        // wird das genommen
        else if (columnIndex == 3) {
            if (cidsBean.getProperty(PROP__FLAECHENINFO__GROESSE_KORREKTUR) != null) {
                return cidsBean.getProperty(PROP__FLAECHENINFO__GROESSE_KORREKTUR);
            } else {
                return cidsBean.getProperty(PROP__FLAECHENINFO__GROESSE_GRAFIK);
            }
        } // Flaechenart
        else if (columnIndex == 4) {
            return cidsBean.getProperty(PROP__FLAECHENINFO__FLAECHENART__ABKUERZUNG);
        } // Anschlussgrad
        else if (columnIndex == 5) {
            return cidsBean.getProperty(PROP__FLAECHENINFO__ANSCHLUSSGRAD__ABKUERZUNG);
        } else {
            return null;
        }
    }
}
