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
package de.cismet.verdis.gui.befreiungerlaubnis_geometrie;

import Sirius.server.middleware.types.MetaObject;

import java.text.SimpleDateFormat;

import javax.swing.Icon;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.verdis.commons.constants.BefreiungerlaubnisGeometriePropertyConstants;
import de.cismet.verdis.commons.constants.BefreiungerlaubnisGeometrieTypEinleitungPropertyConstants;
import de.cismet.verdis.commons.constants.BefreiungerlaubnisGeometrieTypVersickerungPropertyConstants;
import de.cismet.verdis.commons.constants.BefreiungerlaubnisPropertyConstants;

import de.cismet.verdis.gui.AbstractCidsBeanTableModel;
import de.cismet.verdis.gui.Main;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class BefreiungerlaubnisGeometrieTableModel extends AbstractCidsBeanTableModel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            BefreiungerlaubnisGeometrieTableModel.class);
    static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    private static final String[] COLUMN_NAMES = {
            " ",
            "Aktenzeichen",
            "Typ",
            "Q [l/s]",
            "G-Verh."
        };

    private static final Class[] COLUMN_CLASSES = {
            Icon.class,
            String.class,
            String.class,
            Double.class,
            Boolean.class
        };

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BefreiungerlaubnisGeometrieTableModel object.
     */
    public BefreiungerlaubnisGeometrieTableModel() {
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
                return null;
            }
            case 1: {
                return (String)
                    (((MetaObject)cidsBean.getMetaObject().getReferencingObjectAttribute().getParentObject()
                                    .getReferencingObjectAttribute().getParentObject()).getBean()).getProperty(
                        BefreiungerlaubnisPropertyConstants.PROP__AKTENZEICHEN);
            }
            case 2: {
                final CidsBean parentBean =
                    ((MetaObject)cidsBean.getMetaObject().getReferencingObjectAttribute().getParentObject()
                                .getReferencingObjectAttribute().getParentObject()).getBean();
                final boolean isVersickerung = (parentBean != null)
                            && (parentBean.getProperty(BefreiungerlaubnisPropertyConstants.PROP__AKTENZEICHEN) != null)
                            && ((String)parentBean.getProperty(BefreiungerlaubnisPropertyConstants.PROP__AKTENZEICHEN))
                            .startsWith("747-");
                final boolean isEinleitung = (parentBean != null)
                            && (parentBean.getProperty(BefreiungerlaubnisPropertyConstants.PROP__AKTENZEICHEN) != null)
                            && ((String)parentBean.getProperty(BefreiungerlaubnisPropertyConstants.PROP__AKTENZEICHEN))
                            .startsWith("748-");

                if (isVersickerung) {
                    return (String)cidsBean.getProperty(
                            BefreiungerlaubnisGeometriePropertyConstants.PROP__TYP_VERSICKERUNG
                                    + "."
                                    + BefreiungerlaubnisGeometrieTypVersickerungPropertyConstants.PROP__NAME);
                } else if (isEinleitung) {
                    return (String)cidsBean.getProperty(
                            BefreiungerlaubnisGeometriePropertyConstants.PROP__TYP_EINLEITUNG
                                    + "."
                                    + BefreiungerlaubnisGeometrieTypEinleitungPropertyConstants.PROP__NAME);
                } else {
                    return "";
                }
            }
            case 3: {
                return (Double)cidsBean.getProperty(BefreiungerlaubnisGeometriePropertyConstants.PROP__DURCHFLUSS);
            }
            case 4: {
                return (Boolean)cidsBean.getProperty(
                        BefreiungerlaubnisGeometriePropertyConstants.PROP__GUTACHTEN_VORHANDEN);
            }
            default: {
                return null;
            }
        }
    }
}
