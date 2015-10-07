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

import de.cismet.cids.custom.util.VerdisUtils;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.verdis.commons.constants.FrontPropertyConstants;
import de.cismet.verdis.commons.constants.FrontinfoPropertyConstants;
import de.cismet.verdis.commons.constants.StrassenreinigungPropertyConstants;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class SRFrontenTableModel extends CidsBeanTableModel {

    //~ Static fields/initializers ---------------------------------------------

    private static final String[] COLUMN_NAMES = {
            "Nummer",
            "Länge",
            "Straßenreinigung"
        };
    private static final Class[] COLUMN_CLASSES = {
            Integer.class,
            Float.class,
            String.class
        };

    //~ Instance fields --------------------------------------------------------

    private final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SRFrontenTableModel.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SRFrontenTableModel object.
     */
    public SRFrontenTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public boolean isCellEditable(final int i, final int i1) {
        return false;
    }

    @Override
    public Object getValueAt(final int row, final int column) {
        final CidsBean frontBean = getCidsBeanByIndex(row);
        if (frontBean == null) {
            return null;
        }
        if (column == 0) {
            try {
                return (Integer)frontBean.getProperty(FrontPropertyConstants.PROP__NUMMER);
            } catch (Exception e) {
                LOG.warn("exception in tablemodel", e);
                return -1;
            }
        } else if (column == 1) {
            try {
                return (Integer)frontBean.getProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                                + FrontinfoPropertyConstants.PROP__LAENGE_KORREKTUR);
            } catch (Exception e) {
                LOG.warn("exception in tablemodel", e);
                return -1f;
            }
        } else if (column == 2) {
            try {
                final CidsBean satzung_strassenreinigung = (CidsBean)frontBean.getProperty(
                        FrontPropertyConstants.PROP__FRONTINFO
                                + "."
                                + FrontinfoPropertyConstants.PROP__LAGE_SR);
                final String srKey;
                if (satzung_strassenreinigung == null) {
                    srKey = (String)frontBean.getProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                                    + FrontinfoPropertyConstants.PROP__SR_KLASSE_OR + "."
                                    + StrassenreinigungPropertyConstants.PROP__KEY);
                } else {
                    srKey = (String)satzung_strassenreinigung.getProperty("sr_klasse.key");
                }
                return (srKey == null) ? "" : srKey;
            } catch (Exception e) {
                LOG.warn("exception in tablemodel", e);
                return "";
            }
        }
        return null;
    }

    @Override
    public CidsBean deepcloneBean(final CidsBean cidsBean) throws Exception {
        return VerdisUtils.deepcloneFrontBean(cidsBean);
    }
}
