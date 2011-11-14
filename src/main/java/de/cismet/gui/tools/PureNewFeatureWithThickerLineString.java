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
package de.cismet.gui.tools;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

import java.awt.Stroke;
import java.awt.geom.Point2D;

import de.cismet.cismap.commons.WorldToScreenTransform;
import de.cismet.cismap.commons.features.PureNewFeature;
import de.cismet.cismap.commons.gui.piccolo.OldFixedWidthStroke;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class PureNewFeatureWithThickerLineString extends PureNewFeature {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new PureNewFeatureWithThickerLineString object.
     *
     * @param  g  DOCUMENT ME!
     */
    public PureNewFeatureWithThickerLineString(final Geometry g) {
        super(g);
    }

    /**
     * Creates a new PureNewFeatureWithThickerLineString object.
     *
     * @param  coordArr  DOCUMENT ME!
     * @param  wtst      DOCUMENT ME!
     */
    public PureNewFeatureWithThickerLineString(final Coordinate[] coordArr, final WorldToScreenTransform wtst) {
        super(coordArr, wtst);
    }

    /**
     * Creates a new PureNewFeatureWithThickerLineString object.
     *
     * @param  canvasPoints  DOCUMENT ME!
     * @param  wtst          DOCUMENT ME!
     */
    public PureNewFeatureWithThickerLineString(final Point2D[] canvasPoints, final WorldToScreenTransform wtst) {
        super(canvasPoints, wtst);
    }

    /**
     * Creates a new PureNewFeatureWithThickerLineString object.
     *
     * @param  point  DOCUMENT ME!
     * @param  wtst   DOCUMENT ME!
     */
    public PureNewFeatureWithThickerLineString(final Point2D point, final WorldToScreenTransform wtst) {
        super(point, wtst);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Stroke getLineStyle() {
        final OldFixedWidthStroke s = new OldFixedWidthStroke();
        s.setMultiplyer(5);
        return s;
    }
}
