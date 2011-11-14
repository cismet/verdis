/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 *  Copyright (C) 2010 srichter
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
package de.cismet.cids.custom.featurerenderer.verdis_grundis;

import Sirius.server.middleware.types.MetaObject;

import java.awt.Paint;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.featurerenderer.CustomCidsFeatureRenderer;
import de.cismet.verdis.constants.RegenFlaechenPropertyConstants;

import de.cismet.verdis.gui.Main;

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public class FlaecheFeatureRenderer extends CustomCidsFeatureRenderer {

    //~ Instance fields --------------------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FlaecheFeatureRenderer.class);

    //~ Methods ----------------------------------------------------------------

    @Override
    public void assign() {
    }

    @Override
    public Paint getFillingStyle() {
        int art = -1;
        try {
            art = (Integer)super.cidsBean.getProperty(RegenFlaechenPropertyConstants.PROP__FLAECHENINFO__FLAECHENART__ID);
        } catch (Exception e) {
            LOG.error("error during getting the flaechenart", e);
        }
        boolean markedForDeletion = false;
        try {
            final CidsBean geom = (CidsBean)cidsBean.getProperty(RegenFlaechenPropertyConstants.PROP__FLAECHENINFO__GEOMETRIE);
            markedForDeletion = geom.getMetaObject().getStatus() == MetaObject.TO_DELETE;
        } catch (Exception e) {
            LOG.error("error during markedForDeletionCheck", e);
        }
        int alpha = 0;
        if (markedForDeletion) {
            alpha = 100;
        } else {
            alpha = 255;
        }
        switch (art) {
            case Main.PROPVAL_ART_DACH: {
                return new java.awt.Color(162, 76, 41, alpha);
            }
            case Main.PROPVAL_ART_GRUENDACH: {
                return new java.awt.Color(106, 122, 23, alpha);
            }
            case Main.PROPVAL_ART_VERSIEGELTEFLAECHE: {
                return new java.awt.Color(120, 129, 128, alpha);
            }
            case Main.PROPVAL_ART_OEKOPFLASTER: {
                return new java.awt.Color(159, 155, 108, alpha);
            }
            case Main.PROPVAL_ART_STAEDTISCHESTRASSENFLAECHE: {
                return new java.awt.Color(138, 134, 132, alpha);
            }
            case Main.PROPVAL_ART_STAEDTISCHESTRASSENFLAECHEOEKOPLFASTER: {
                return new java.awt.Color(126, 91, 71, alpha);
            }
            default: {
                return null;
            }
        }
    }
}
