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

import java.awt.Paint;

import de.cismet.cids.featurerenderer.CustomCidsFeatureRenderer;

import de.cismet.verdis.commons.constants.FlaechePropertyConstants;
import de.cismet.verdis.commons.constants.FlaechenartPropertyConstants;
import de.cismet.verdis.commons.constants.FlaecheninfoPropertyConstants;

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public class FlaecheFeatureRenderer extends CustomCidsFeatureRenderer {

    //~ Static fields/initializers ---------------------------------------------

    public static final int PROPVAL_ART_DACH = 1;
    public static final int PROPVAL_ART_GRUENDACH = 2;
    public static final int PROPVAL_ART_VERSIEGELTEFLAECHE = 3;
    public static final int PROPVAL_ART_OEKOPFLASTER = 4;
    public static final int PROPVAL_ART_STAEDTISCHESTRASSENFLAECHE = 5;
    public static final int PROPVAL_ART_STAEDTISCHESTRASSENFLAECHEOEKOPLFASTER = 6;
    public static final int PROPVAL_ART_VORLAEUFIGEVERANLASSUNG = 7;

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            FlaecheFeatureRenderer.class);

    //~ Methods ----------------------------------------------------------------

    @Override
    public void assign() {
    }

    @Override
    public Paint getFillingStyle() {
        int art = -1;
        try {
            art = (Integer)super.cidsBean.getProperty(
                    FlaechePropertyConstants.PROP__FLAECHENINFO
                            + "."
                            + FlaecheninfoPropertyConstants.PROP__FLAECHENART
                            + "."
                            + FlaechenartPropertyConstants.PROP__ID);
        } catch (Exception e) {
            LOG.error("error during getting the flaechenart", e);
        }
        final int alpha = 255;
        switch (art) {
            case PROPVAL_ART_DACH: {
                return new java.awt.Color(162, 76, 41, alpha);
            }
            case PROPVAL_ART_GRUENDACH: {
                return new java.awt.Color(106, 122, 23, alpha);
            }
            case PROPVAL_ART_VERSIEGELTEFLAECHE: {
                return new java.awt.Color(120, 129, 128, alpha);
            }
            case PROPVAL_ART_OEKOPFLASTER: {
                return new java.awt.Color(159, 155, 108, alpha);
            }
            case PROPVAL_ART_STAEDTISCHESTRASSENFLAECHE: {
                return new java.awt.Color(138, 134, 132, alpha);
            }
            case PROPVAL_ART_STAEDTISCHESTRASSENFLAECHEOEKOPLFASTER: {
                return new java.awt.Color(126, 91, 71, alpha);
            }
            default: {
                return null;
            }
        }
    }
}
