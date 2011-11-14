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

import de.cismet.verdis.constants.WDSRPropertyConstants;
import de.cismet.verdis.constants.KassenzeichenPropertyConstants;
import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class WDSRTableModel extends CidsBeanTableModel implements WDSRPropertyConstants {

    private final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(WDSRTableModel.class);

    //~ Methods ----------------------------------------------------------------

    @Override
    public String getBeansProperty() {
        return KassenzeichenPropertyConstants.PROP__FRONTEN;
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public String getColumnName(final int i) {
        if (i == 0) {
            return "Nummer";
        } else if (i == 1) {
            return "Länge";
        } else if (i == 2) {
            return "Straßenreinigung";
        } else {
            return "Winterdienst";
        }
    }

    @Override
    public Class<?> getColumnClass(final int i) {
        if (i == 0) {
            return Integer.class;
        } else if (i == 1) {
            return Float.class;
        } else if (i == 2) {
            return String.class;
        } else {
            return String.class;
        }
    }

    @Override
    public boolean isCellEditable(final int i, final int i1) {
        return false;
    }

    @Override
    public Object getValueAt(final int row, final int column) {
        final CidsBean cidsBean = getCidsBeans().get(row);
        if (column == 0) {
            try {
                return (Integer)cidsBean.getProperty(PROP__NUMMER);
            } catch (Exception e) {
                LOG.warn("exception in tablemodel", e);
                return -1;
            }
        } else if (column == 1) {
            try {
                return (Integer)cidsBean.getProperty(PROP__LAENGE_KORREKTUR);
            } catch (Exception e) {
                LOG.warn("exception in tablemodel", e);
                return -1f;
            }
        } else if (column == 2) {
            try {
                final Object o = cidsBean.getProperty(PROP__SR_KLASSE_OR__KEY);
                return (o == null) ? "" : o.toString();
            } catch (Exception e) {
                LOG.warn("exception in tablemodel", e);
                return "";
            }
        } else {
            try {
                final Object o = cidsBean.getProperty(PROP__WD_PRIO_OR__KEY);
                return (o == null) ? "" : o.toString();
            } catch (Exception e) {
                LOG.warn("exception in tablemodel", e);
                return "";
            }
        }
    }  
}
