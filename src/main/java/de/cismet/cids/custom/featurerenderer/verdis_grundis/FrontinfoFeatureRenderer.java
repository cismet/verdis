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

import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;

import de.cismet.cids.featurerenderer.CustomCidsFeatureRenderer;

import de.cismet.cismap.commons.gui.piccolo.OldFixedWidthStroke;
import de.cismet.verdis.constants.WDSRPropertyConstants;

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public class FrontinfoFeatureRenderer extends CustomCidsFeatureRenderer implements WDSRPropertyConstants {

    //~ Instance fields --------------------------------------------------------

    OldFixedWidthStroke stroke = new OldFixedWidthStroke();
    boolean wd = false;
    boolean sr = false;

    private final transient org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());

    //~ Methods ----------------------------------------------------------------

    @Override
    public void assign() {
        sr = cidsBean.getProperty(PROP__SR_KLASSE_OR) != null;
        wd = cidsBean.getProperty(PROP__WD_PRIO_OR) != null;
    }

    @Override
    public Paint getLinePaint() {
        if (sr && wd) {
            return Color.ORANGE;
        } else if (sr) {
            return Color.BLUE;
        } else if (wd) {
            return Color.YELLOW;
        } else {
            return Color.RED;
        }
    }

    @Override
    public Stroke getLineStyle() {
        stroke.setMultiplyer(10);
        return stroke;
    }
}
