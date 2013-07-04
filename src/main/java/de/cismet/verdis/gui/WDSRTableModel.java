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

import Sirius.server.middleware.types.MetaObject;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.verdis.commons.constants.FrontPropertyConstants;
import de.cismet.verdis.commons.constants.FrontinfoPropertyConstants;
import de.cismet.verdis.commons.constants.StrassenreinigungPropertyConstants;
import de.cismet.verdis.commons.constants.WinterdienstPropertyConstants;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class WDSRTableModel extends CidsBeanTableModel {

    //~ Static fields/initializers ---------------------------------------------

    private static final String[] COLUMN_NAMES = {
            "Nummer",
            "Länge",
            "Straßenreinigung",
            "Winterdienst"
        };
    private static final Class[] COLUMN_CLASSES = {
            Integer.class,
            Float.class,
            String.class,
            String.class
        };

    //~ Instance fields --------------------------------------------------------

    private final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(WDSRTableModel.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new WDSRTableModel object.
     */
    public WDSRTableModel() {
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
        } else {
            try {
                final CidsBean satzung_winterdienst = (CidsBean)frontBean.getProperty(
                        FrontPropertyConstants.PROP__FRONTINFO
                                + "."
                                + FrontinfoPropertyConstants.PROP__LAGE_WD);
                final String wdKey;
                if (satzung_winterdienst == null) {
                    wdKey = (String)frontBean.getProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                                    + FrontinfoPropertyConstants.PROP__WD_PRIO_OR + "."
                                    + WinterdienstPropertyConstants.PROP__KEY);
                } else {
                    wdKey = (String)satzung_winterdienst.getProperty("wd_prio.key");
                }
                return (wdKey == null) ? "" : wdKey;
            } catch (Exception e) {
                LOG.warn("exception in tablemodel", e);
                return "";
            }
        }
    }

    @Override
    public CidsBean deepcloneBean(final CidsBean cidsBean) throws Exception {
        final CidsBean deepclone = super.deepcloneBean(cidsBean);
        deepclone.setProperty(
            FrontPropertyConstants.PROP__FRONTINFO
                    + "."
                    + FrontinfoPropertyConstants.PROP__SR_KLASSE_OR,
            cidsBean.getProperty(
                FrontPropertyConstants.PROP__FRONTINFO
                        + "."
                        + FrontinfoPropertyConstants.PROP__SR_KLASSE_OR));
        deepclone.setProperty(
            FrontPropertyConstants.PROP__FRONTINFO
                    + "."
                    + FrontinfoPropertyConstants.PROP__WD_PRIO_OR,
            cidsBean.getProperty(
                FrontPropertyConstants.PROP__FRONTINFO
                        + "."
                        + FrontinfoPropertyConstants.PROP__WD_PRIO_OR));
        deepclone.setProperty(
            FrontPropertyConstants.PROP__FRONTINFO
                    + "."
                    + FrontinfoPropertyConstants.PROP__STRASSE,
            cidsBean.getProperty(FrontPropertyConstants.PROP__FRONTINFO
                    + "."
                    + FrontinfoPropertyConstants.PROP__STRASSE));
        // deepclone.setProperty(FrontinfoPropertyConstants.PROP__SATZUNG,
        // cidsBean.getProperty(FrontinfoPropertyConstants.PROP__SATZUNG));

        final CidsBean sr_klasse = (CidsBean)deepclone.getProperty(FrontPropertyConstants.PROP__FRONTINFO
                    + "."
                    + FrontinfoPropertyConstants.PROP__SR_KLASSE_OR);
        if (sr_klasse != null) {
            sr_klasse.getMetaObject().forceStatus(MetaObject.NO_STATUS);
        }

        final CidsBean wd_prio = (CidsBean)deepclone.getProperty(FrontPropertyConstants.PROP__FRONTINFO
                    + "."
                    + FrontinfoPropertyConstants.PROP__WD_PRIO_OR);
        if (wd_prio != null) {
            wd_prio.getMetaObject().forceStatus(MetaObject.NO_STATUS);
        }

        final CidsBean strasse = (CidsBean)deepclone.getProperty(FrontPropertyConstants.PROP__FRONTINFO
                    + "."
                    + FrontinfoPropertyConstants.PROP__STRASSE);
        if (strasse != null) {
            strasse.getMetaObject().forceStatus(MetaObject.NO_STATUS);
        }
        return deepclone;
    }
}
