/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.reports.verdis;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

import org.apache.log4j.Logger;

import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

import java.awt.Image;
import java.awt.image.BufferedImage;

import java.io.IOException;

import java.text.NumberFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.Crs;
import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.HeadlessMapProvider;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;

import de.cismet.verdis.commons.constants.KassenzeichenPropertyConstants;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public abstract class EBReportBean {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(EBReportBean.class);
    private static final double PPI = 72.156d;
    private static final double METERS_TO_INCH_FACTOR = 0.0254d;

    //~ Instance fields --------------------------------------------------------

    private CidsBean kassenzeichen;
    private String kznr;
    private String scale;
    private Image mapImage = null;
    private Image barcodeImage = null;
    private final int mapWidth;
    private final int mapHeight;
    private final Double scaleDenominator;
    private boolean fillAbflusswirksamkeit = false;
    private final Properties properties;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FebBean object.
     *
     * @param  properties        DOCUMENT ME!
     * @param  kassenzeichen     DOCUMENT ME!
     * @param  mapHeight         DOCUMENT ME!
     * @param  mapWidth          DOCUMENT ME!
     * @param  scaleDenominator  DOCUMENT ME!
     * @param  fillAbfluss       DOCUMENT ME!
     */
    public EBReportBean(final Properties properties,
            final CidsBean kassenzeichen,
            final int mapHeight,
            final int mapWidth,
            final Double scaleDenominator,
            final boolean fillAbfluss) {
        this.properties = properties;
        this.kassenzeichen = kassenzeichen;
        this.mapHeight = mapHeight;
        this.mapWidth = mapWidth;
        this.scaleDenominator = scaleDenominator;
        kznr = "" + (Integer)this.kassenzeichen.getProperty(
                        KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER);
        this.fillAbflusswirksamkeit = fillAbfluss;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isFillAbflusswirksamkeit() {
        return fillAbflusswirksamkeit;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  fillAbflusswirksamkeit  DOCUMENT ME!
     */
    public void setFillAbflusswirksamkeit(final boolean fillAbflusswirksamkeit) {
        this.fillAbflusswirksamkeit = fillAbflusswirksamkeit;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getKassenzeichenBean() {
        return kassenzeichen;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  feb  DOCUMENT ME!
     */
    public void setKassenzeichenBean(final CidsBean feb) {
        this.kassenzeichen = feb;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getKznr() {
        return kznr;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kassenzeichennummer  DOCUMENT ME!
     */
    public void setKznr(final String kassenzeichennummer) {
        this.kznr = kassenzeichennummer;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getScale() {
        return scale;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  scale  DOCUMENT ME!
     */
    public void setScale(final String scale) {
        this.scale = scale;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Image getMapImage() {
        return mapImage;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  mapImage  DOCUMENT ME!
     */
    public void setMapImage(final Image mapImage) {
        this.mapImage = mapImage;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isReadyToProceed() {
        return (mapImage != null) && (kassenzeichen != null);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected abstract Collection<Feature> createFeatures();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * DOCUMENT ME!
     */
    protected void loadMap() {
        final SimpleWMS simpleWms = new SimpleWMS(new SimpleWmsGetMapUrl(EBGenerator.WMS_CALL));
        final HeadlessMapProvider mapProvider = new HeadlessMapProvider();
        mapProvider.setCenterMapOnResize(true);
        final Crs crs = new Crs(EBGenerator.SRS, "", "", true, true);
        mapProvider.setCrs(crs);
        mapProvider.addLayer(simpleWms);

        final Collection<Feature> features = createFeatures();
        int srid = CrsTransformer.extractSridFromCrs(EBGenerator.SRS);
        boolean first = true;
        final List<Geometry> geomList = new ArrayList<Geometry>(features.size());
        for (final Feature feature : features) {
            Geometry geometry = feature.getGeometry();

            if (geometry != null) {
                geometry = geometry.getEnvelope();

                if (first) {
                    srid = geometry.getSRID();
                    first = false;
                } else {
                    if (geometry.getSRID() != srid) {
                        geometry = CrsTransformer.transformToGivenCrs(geometry, EBGenerator.SRS);
                    }
                }

                geomList.add(geometry);
            }
            mapProvider.addFeature(feature);
            if (mapProvider.getMappingComponent().getPFeatureHM().get(feature) != null) {
                final PNode annotationNode = mapProvider.getMappingComponent()
                            .getPFeatureHM()
                            .get(feature)
                            .getPrimaryAnnotationNode();
                if (annotationNode != null) {
                    final PBounds bounds = annotationNode.getBounds();
                    bounds.x = -bounds.width / 2;
                    bounds.y = -bounds.height / 2;
                    annotationNode.setBounds(bounds);
                }
            }
        }

        final GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), srid);
        Geometry union = factory.buildGeometry(geomList);
        if (union instanceof GeometryCollection) {
            union = ((GeometryCollection)union).union();
        }
        final XBoundingBox boundingBox = new XBoundingBox(union);

        final double oldWidth = boundingBox.getWidth();
        final double oldHeight = boundingBox.getHeight();
        final double centerX = boundingBox.getX1() + (oldWidth / 2);
        final double centerY = boundingBox.getY1() + (oldHeight / 2);

        final double newScaleDenominator;
        if (scaleDenominator != null) {
            newScaleDenominator = scaleDenominator;
        } else {
            final double oldScaleDenominator = getScaleDenom(oldWidth, oldHeight);
            newScaleDenominator = Math.round((oldScaleDenominator / 100) + 0.5d) * 100;
        }

        final double mapWidthInMeter = (mapWidth / PPI) * METERS_TO_INCH_FACTOR;
        final double mapHeightInMeter = (mapHeight / PPI) * METERS_TO_INCH_FACTOR;
        final double worldWidthInPx = mapWidthInMeter * newScaleDenominator;
        final double worldHeightInPx = mapHeightInMeter * newScaleDenominator;

        boundingBox.setX1(centerX - (worldWidthInPx / 2d));
        boundingBox.setX2(centerX + (worldWidthInPx / 2d));
        boundingBox.setY1(centerY - (worldHeightInPx / 2d));
        boundingBox.setY2(centerY + (worldHeightInPx / 2d));

        mapProvider.setBoundingBox(boundingBox);

        final int mapDPI = Integer.parseInt(properties.getProperty("FEBReportBean.mapDPI"));

        mapProvider.setFeatureResolutionFactor(mapDPI);
        try {
            mapImage = mapProvider.getImageAndWait(72, mapDPI, mapWidth, mapHeight);
        } catch (final Exception ex) {
            LOG.error("error while creating mapImage", ex);
        }
        scale = "1:"
                    + (NumberFormat.getIntegerInstance().format(newScaleDenominator));
    }

    /**
     * DOCUMENT ME!
     *
     * @param   worldWith    DOCUMENT ME!
     * @param   worldHeight  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private double getScaleDenom(final double worldWith, final double worldHeight) {
        final double ratio = mapWidth / (double)mapHeight;
        final double mapWithOrHeightInPx = ((worldWith * ratio) > worldHeight) ? mapWidth : mapHeight;
        final double mapWidthOrHeightInMeter = (mapWithOrHeightInPx / PPI) * METERS_TO_INCH_FACTOR;
        final double worldWidthOrHeightInMeter = ((worldWith * ratio) > worldHeight) ? worldWith : worldHeight;
        return worldWidthOrHeightInMeter / mapWidthOrHeightInMeter; // x meter abgebildet auf y meter ergibt maßstab
                                                                    // von z
    }

    /**
     * DOCUMENT ME!
     *
     * @param   wishedScale           DOCUMENT ME!
     * @param   oldScale              DOCUMENT ME!
     * @param   realWorldWithInMeter  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private double getMapScaleDenom(final double wishedScale,
            final double oldScale,
            final double realWorldWithInMeter) {
        final double mapReportWidthInMeter = (mapWidth / PPI)
                    * 0.0254d;
        final double mapBoundingBoxWidth = wishedScale
                    * mapReportWidthInMeter;
        return mapBoundingBoxWidth
                    * oldScale
                    / realWorldWithInMeter;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Image getBarcodeImage() {
        return barcodeImage;
    }

    /**
     * DOCUMENT ME!
     */
    protected void genBarcode() {
        try {
            final int dpi = 72;
            final Code39Bean bean = new Code39Bean();
            // BarcodeDimension dimension = bean.calcDimensions(getKznr());
            bean.setModuleWidth(UnitConv.in2mm(1.0f / dpi)); // makes the narrow bar
            // width exactly one pixel
            bean.setWideFactor(3);
            bean.setModuleWidth(1);
            bean.setIntercharGapWidth(1);
            bean.setFontSize(8);
            bean.setBarHeight(22);
            bean.setPattern("*________*");

            final BitmapCanvasProvider provider = new BitmapCanvasProvider(dpi, BufferedImage.TYPE_BYTE_GRAY, true, 0);
            bean.generateBarcode(provider, getKznr());

            provider.finish();

            barcodeImage = provider.getBufferedImage();
        } catch (final IOException ex) {
            LOG.error("error while generating Barcode", ex);
        }
    }
}
