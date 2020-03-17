/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * Copyright (c) 2016 Vivid Solutions.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */
package com.vividsolutions.jts.io.geojson;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.io.ParseException;

import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Reads a GeoJson Geometry from a JSON fragment into a {@link Geometry}.
 *
 * <p>A specification of the GeoJson format can be found at the GeoJson web site: <a
 * href='http://geojson.org/geojson-spec.html'>http://geojson.org/geojson-spec.html</a>.</p>
 *
 * <p>It is the caller's responsibility to ensure that the supplied {@link PrecisionModel} matches the precision of the
 * incoming data. If a lower precision for the data is required, a subsequent process must be run on the data to reduce
 * its precision.</p>
 *
 * @author   Martin Davis
 * @author   Paul Howells, Vivid Solutions.
 * @version  $Revision$, $Date$
 */
public class GeoJsonReader {

    //~ Instance fields --------------------------------------------------------

    private GeometryFactory gf;

    //~ Constructors -----------------------------------------------------------

    /**
     * The default constructor uses the SRID from the Geojson CRS and the default <code>PrecisionModel</code> to create
     * a <code>GeometryFactory</code>. If there is no CRS specified then the default CRS is a geographic coordinate
     * reference system, using the WGS84 datum, and with longitude and latitude units of decimal degrees (SRID = 4326)
     */
    public GeoJsonReader() {
        // do nothing
    }

    /**
     * This constructor accepts a <code>GeometryFactory</code> that is used to create the output geometries and to
     * override the GeoJson CRS.
     *
     * @param  geometryFactory  a GeometryFactory
     */
    public GeoJsonReader(final GeometryFactory geometryFactory) {
        this.gf = geometryFactory;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Reads a GeoJson Geometry from a <tt>String</tt> into a single {@link Geometry}.
     *
     * @param   json  The GeoJson String to parse
     *
     * @return  the resulting JTS Geometry
     *
     * @throws  ParseException  throws a ParseException if the JSON string cannot be parsed
     */
    public Geometry read(final String json) throws ParseException {
        final Geometry result = read(new StringReader(json));
        return result;
    }

    /**
     * Reads a GeoJson Geometry from a {@link Reader} into a single {@link Geometry}.
     *
     * @param   reader  The input source
     *
     * @return  The resulting JTS Geometry
     *
     * @throws  ParseException  throws a ParseException if the JSON string cannot be parsed
     */
    public Geometry read(final Reader reader) throws ParseException {
        Geometry result = null;

        final JSONParser parser = new JSONParser();
        try {
            @SuppressWarnings("unchecked")
            final Map<String, Object> geometryMap = (Map<String, Object>)parser.parse(reader);

            GeometryFactory geometryFactory = null;
            if (this.gf == null) {
                geometryFactory = this.getGeometryFactory(geometryMap);
            } else {
                geometryFactory = this.gf;
            }

            result = create(geometryMap, geometryFactory);
        } catch (org.json.simple.parser.ParseException e) {
            throw new ParseException(e);
        } catch (IOException e) {
            throw new ParseException(e);
        }

        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   geometryMap      DOCUMENT ME!
     * @param   geometryFactory  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ParseException  DOCUMENT ME!
     */
    private Geometry create(final Map<String, Object> geometryMap, final GeometryFactory geometryFactory)
            throws ParseException {
        Geometry result = null;

        final String type = (String)geometryMap.get(GeoJsonConstants.NAME_TYPE);

        if (type == null) {
            throw new ParseException(
                "Could not parse Geometry from Json string.  No 'type' property found.");
        } else {
            if (GeoJsonConstants.NAME_POINT.equals(type)) {
                result = createPoint(geometryMap, geometryFactory);
            } else if (GeoJsonConstants.NAME_LINESTRING.equals(type)) {
                result = createLineString(geometryMap, geometryFactory);
            } else if (GeoJsonConstants.NAME_POLYGON.equals(type)) {
                result = createPolygon(geometryMap, geometryFactory);
            } else if (GeoJsonConstants.NAME_MULTIPOINT.equals(type)) {
                result = createMultiPoint(geometryMap, geometryFactory);
            } else if (GeoJsonConstants.NAME_MULTILINESTRING.equals(type)) {
                result = createMultiLineString(geometryMap, geometryFactory);
            } else if (GeoJsonConstants.NAME_MULTIPOLYGON.equals(type)) {
                result = createMultiPolygon(geometryMap, geometryFactory);
            } else if (GeoJsonConstants.NAME_GEOMETRYCOLLECTION.equals(type)) {
                result = createGeometryCollection(geometryMap, geometryFactory);
            } else {
                throw new ParseException(
                    "Could not parse Geometry from GeoJson string.  Unsupported 'type':"
                            + type);
            }
        }

        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   geometryMap      DOCUMENT ME!
     * @param   geometryFactory  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ParseException  DOCUMENT ME!
     */
    private Geometry createGeometryCollection(final Map<String, Object> geometryMap,
            final GeometryFactory geometryFactory) throws ParseException {
        Geometry result = null;

        try {
            @SuppressWarnings("unchecked")
            final List<Map<String, Object>> geometriesList = (List<Map<String, Object>>)geometryMap.get(
                    GeoJsonConstants.NAME_GEOMETRIES);

            final Geometry[] geometries = new Geometry[geometriesList.size()];

            int i = 0;
            for (final Map<String, Object> map : geometriesList) {
                geometries[i] = this.create(map, geometryFactory);

                ++i;
            }

            result = geometryFactory.createGeometryCollection(geometries);
        } catch (RuntimeException e) {
            throw new ParseException(
                "Could not parse GeometryCollection from GeoJson string.",
                e);
        }

        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   geometryMap      DOCUMENT ME!
     * @param   geometryFactory  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ParseException  DOCUMENT ME!
     */
    private Geometry createMultiPolygon(final Map<String, Object> geometryMap, final GeometryFactory geometryFactory)
            throws ParseException {
        Geometry result = null;

        try {
            @SuppressWarnings("unchecked")
            final List<List<List<List<Number>>>> polygonsList = (List<List<List<List<Number>>>>)geometryMap.get(
                    GeoJsonConstants.NAME_COORDINATES);

            final Polygon[] polygons = new Polygon[polygonsList.size()];

            int p = 0;
            for (final List<List<List<Number>>> ringsList : polygonsList) {
                final List<CoordinateSequence> rings = new ArrayList<CoordinateSequence>();

                for (final List<List<Number>> coordinates : ringsList) {
                    rings.add(createCoordinateSequence(coordinates));
                }

                if (rings.isEmpty()) {
                    continue;
                }

                final LinearRing outer = geometryFactory.createLinearRing(rings.get(0));
                LinearRing[] inner = null;
                if (rings.size() > 1) {
                    inner = new LinearRing[rings.size() - 1];
                    for (int i = 1; i < rings.size(); i++) {
                        inner[i - 1] = geometryFactory.createLinearRing(rings.get(i));
                    }
                }

                polygons[p] = geometryFactory.createPolygon(outer, inner);

                ++p;
            }

            result = geometryFactory.createMultiPolygon(polygons);
        } catch (RuntimeException e) {
            throw new ParseException(
                "Could not parse MultiPolygon from GeoJson string.",
                e);
        }

        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   geometryMap      DOCUMENT ME!
     * @param   geometryFactory  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ParseException  DOCUMENT ME!
     */
    private Geometry createMultiLineString(final Map<String, Object> geometryMap, final GeometryFactory geometryFactory)
            throws ParseException {
        Geometry result = null;

        try {
            @SuppressWarnings("unchecked")
            final List<List<List<Number>>> linesList = (List<List<List<Number>>>)geometryMap.get(
                    GeoJsonConstants.NAME_COORDINATES);

            final LineString[] lineStrings = new LineString[linesList.size()];

            int i = 0;
            for (final List<List<Number>> coordinates : linesList) {
                lineStrings[i] = geometryFactory.createLineString(createCoordinateSequence(coordinates));

                ++i;
            }

            result = geometryFactory.createMultiLineString(lineStrings);
        } catch (RuntimeException e) {
            throw new ParseException(
                "Could not parse MultiLineString from GeoJson string.",
                e);
        }

        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   geometryMap      DOCUMENT ME!
     * @param   geometryFactory  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ParseException  DOCUMENT ME!
     */
    private Geometry createMultiPoint(final Map<String, Object> geometryMap, final GeometryFactory geometryFactory)
            throws ParseException {
        Geometry result = null;

        try {
            @SuppressWarnings("unchecked")
            final List<List<Number>> coordinatesList = (List<List<Number>>)geometryMap.get(
                    GeoJsonConstants.NAME_COORDINATES);

            final CoordinateSequence coordinates = this.createCoordinateSequence(coordinatesList);

            result = geometryFactory.createMultiPoint(coordinates);
        } catch (RuntimeException e) {
            throw new ParseException(
                "Could not parse MultiPoint from GeoJson string.",
                e);
        }

        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   geometryMap      DOCUMENT ME!
     * @param   geometryFactory  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ParseException  DOCUMENT ME!
     */
    private Geometry createPolygon(final Map<String, Object> geometryMap, final GeometryFactory geometryFactory)
            throws ParseException {
        Geometry result = null;

        try {
            @SuppressWarnings("unchecked")
            final List<List<List<Number>>> ringsList = (List<List<List<Number>>>)geometryMap.get(
                    GeoJsonConstants.NAME_COORDINATES);

            final List<CoordinateSequence> rings = new ArrayList<CoordinateSequence>();

            for (final List<List<Number>> coordinates : ringsList) {
                rings.add(createCoordinateSequence(coordinates));
            }

            if (rings.isEmpty()) {
                throw new IllegalArgumentException("Polygon specified with no rings.");
            }

            final LinearRing outer = geometryFactory.createLinearRing(rings.get(0));
            LinearRing[] inner = null;
            if (rings.size() > 1) {
                inner = new LinearRing[rings.size() - 1];
                for (int i = 1; i < rings.size(); i++) {
                    inner[i - 1] = geometryFactory.createLinearRing(rings.get(i));
                }
            }

            result = geometryFactory.createPolygon(outer, inner);
        } catch (RuntimeException e) {
            throw new ParseException("Could not parse Polygon from GeoJson string.",
                e);
        }

        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   geometryMap      DOCUMENT ME!
     * @param   geometryFactory  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ParseException  DOCUMENT ME!
     */
    private Geometry createLineString(final Map<String, Object> geometryMap, final GeometryFactory geometryFactory)
            throws ParseException {
        Geometry result = null;

        try {
            @SuppressWarnings("unchecked")
            final List<List<Number>> coordinatesList = (List<List<Number>>)geometryMap.get(
                    GeoJsonConstants.NAME_COORDINATES);

            final CoordinateSequence coordinates = createCoordinateSequence(coordinatesList);

            result = geometryFactory.createLineString(coordinates);
        } catch (RuntimeException e) {
            throw new ParseException(
                "Could not parse LineString from GeoJson string.",
                e);
        }

        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   geometryMap      DOCUMENT ME!
     * @param   geometryFactory  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ParseException  DOCUMENT ME!
     */
    private Geometry createPoint(final Map<String, Object> geometryMap, final GeometryFactory geometryFactory)
            throws ParseException {
        Geometry result = null;

        try {
            @SuppressWarnings("unchecked")
            final List<Number> coordinateList = (List<Number>)geometryMap.get(GeoJsonConstants.NAME_COORDINATES);

            final CoordinateSequence coordinate = this.createCoordinate(coordinateList);

            result = geometryFactory.createPoint(coordinate);
        } catch (RuntimeException e) {
            throw new ParseException("Could not parse Point from GeoJson string.", e);
        }

        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   geometryMap  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ParseException  DOCUMENT ME!
     */
    private GeometryFactory getGeometryFactory(final Map<String, Object> geometryMap) throws ParseException {
        GeometryFactory result = null;
        @SuppressWarnings("unchecked")
        final Map<String, Object> crsMap = (Map<String, Object>)geometryMap.get(GeoJsonConstants.NAME_CRS);
        Integer srid = null;

        if (crsMap != null) {
            try {
                @SuppressWarnings("unchecked")
                final Map<String, Object> propertiesMap = (Map<String, Object>)crsMap.get(
                        GeoJsonConstants.NAME_PROPERTIES);
                final String name = (String)propertiesMap.get(GeoJsonConstants.NAME_NAME);
                final String[] split = name.split(":");
                final String epsg = split[1];
                srid = Integer.valueOf(epsg);
            } catch (RuntimeException e) {
                throw new ParseException(
                    "Could not parse SRID from Geojson 'crs' object.",
                    e);
            }
        }

        if (srid == null) {
            // The default CRS is a geographic coordinate reference
            // system, using the WGS84 datum, and with longitude and
            // latitude units of decimal degrees. SRID 4326
            srid = Integer.valueOf(4326);
        }

        result = new GeometryFactory(new PrecisionModel(), srid.intValue());
        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   coordinates  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private CoordinateSequence createCoordinateSequence(final List<List<Number>> coordinates) {
        CoordinateSequence result = null;

        result = new CoordinateArraySequence(coordinates.size());

        for (int i = 0; i < coordinates.size(); ++i) {
            final List<Number> ordinates = coordinates.get(i);

            if (ordinates.size() > 0) {
                result.setOrdinate(i, 0, ordinates.get(0).doubleValue());
            }
            if (ordinates.size() > 1) {
                result.setOrdinate(i, 1, ordinates.get(1).doubleValue());
            }
            if (ordinates.size() > 2) {
                result.setOrdinate(i, 2, ordinates.get(2).doubleValue());
            }
        }

        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   ordinates  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private CoordinateSequence createCoordinate(final List<Number> ordinates) {
        final CoordinateSequence result = new CoordinateArraySequence(1);

        if (ordinates.size() > 0) {
            result.setOrdinate(0, 0, ordinates.get(0).doubleValue());
        }
        if (ordinates.size() > 1) {
            result.setOrdinate(0, 1, ordinates.get(1).doubleValue());
        }
        if (ordinates.size() > 2) {
            result.setOrdinate(0, 2, ordinates.get(2).doubleValue());
        }

        return result;
    }
}
