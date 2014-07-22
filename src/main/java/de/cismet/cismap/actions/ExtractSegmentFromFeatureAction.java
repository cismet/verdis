/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 *  Copyright (C) 2011 thorsten
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
package de.cismet.cismap.actions;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import org.openide.util.lookup.ServiceProvider;

import java.awt.event.ActionEvent;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.AbstractAction;

import de.cismet.cismap.commons.features.CommonFeatureAction;
import de.cismet.cismap.commons.features.CommonFeaturePreciseAction;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.features.InheritsLayerProperties;
import de.cismet.cismap.commons.features.PureNewFeature;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.gui.tools.PureNewFeatureWithThickerLineString;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = CommonFeatureAction.class)
public class ExtractSegmentFromFeatureAction extends AbstractAction implements CommonFeaturePreciseAction {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            ExtractSegmentFromFeatureAction.class);

    //~ Instance fields --------------------------------------------------------

    private Feature feature = null;
    private Collection<Feature> allFeatures = null;
    private Coordinate coordinate = null;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new DuplicateGeometryFeatureAction object.
     */
    public ExtractSegmentFromFeatureAction() {
        super("Segment extrahieren");
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public int getSorter() {
        return 1;
    }

    @Override
    public Feature getSourceFeature() {
        return feature;
    }

    @Override
    public boolean isActive() {
        return !(feature instanceof PureNewFeature);
    }

    @Override
    public void setSourceFeature(final Feature source) {
        feature = source;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        Geometry nearestSegment = null;
        double distance = Double.MAX_VALUE;
        final Feature nearestFeature = null;
        for (final Feature feature : allFeatures) {
            final Point actionPoint = new Point(
                    coordinate,
                    feature.getGeometry().getPrecisionModel(),
                    feature.getGeometry().getSRID());
            distance = feature.getGeometry().getBoundary().distance(actionPoint);

            final Collection<LineString> lineStrings = new ArrayList<LineString>();

            if (feature.getGeometry() instanceof LineString) {
                lineStrings.add((LineString)feature.getGeometry());
            } else if (feature.getGeometry() instanceof MultiLineString) {
                final MultiLineString mls = (MultiLineString)feature.getGeometry();
                for (int i = 0; i < mls.getNumGeometries(); i++) {
                    lineStrings.add((LineString)mls.getGeometryN(i));
                }
            } else if (feature.getGeometry() instanceof Polygon) {
                lineStrings.add((LinearRing)feature.getGeometry().getBoundary());
            } else if (feature.getGeometry() instanceof MultiPolygon) {
                final MultiLineString mls = (MultiLineString)feature.getGeometry().getBoundary();
                for (int i = 0; i < mls.getNumGeometries(); i++) {
                    lineStrings.add((LineString)mls.getGeometryN(i));
                }
            }
            for (final LineString ls : lineStrings) {
                for (int j = 0; j < ls.getCoordinates().length; j++) {
                    final Coordinate a = ls.getCoordinateN(j);
                    final Coordinate b = ((j + 1) < ls.getCoordinates().length) ? ls.getCoordinateN(j + 1)
                                                                                : ls.getCoordinateN(0);

                    final LineSegment segment = new LineSegment(a, b);
                    final double segmentDistance = segment.distance(coordinate);
                    if (segmentDistance <= distance) {
                        nearestSegment = new LineString(
                                new Coordinate[] { a, b },
                                feature.getGeometry().getPrecisionModel(),
                                feature.getGeometry().getSRID());
                        distance = segmentDistance;
                    }
                }
            }
        }

        final PureNewFeatureWithThickerLineString feat = new PureNewFeatureWithThickerLineString(nearestSegment);
        if (feature instanceof InheritsLayerProperties) {
            final String name = "aus \""
                        + ((InheritsLayerProperties)feature).getLayerProperties().getFeatureService().getName() + "\"";
            feat.setName(name);
        }

        CismapBroker.getInstance().getMappingComponent().getFeatureCollection().addFeature(feat);
    }

    @Override
    public void setActionCoordinate(final Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    @Override
    public void setAllSourceFeatures(final Collection<Feature> allFeatures) {
        this.allFeatures = allFeatures;
    }
}
