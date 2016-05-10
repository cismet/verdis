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

import org.openide.util.NbBundle;

import java.awt.Image;
import java.awt.image.BufferedImage;

import java.io.IOException;

import java.text.NumberFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FebBean object.
     *
     * @param  kassenzeichen     DOCUMENT ME!
     * @param  mapHeight         DOCUMENT ME!
     * @param  mapWidth          DOCUMENT ME!
     * @param  scaleDenominator  DOCUMENT ME!
     * @param  fillAbfluss       DOCUMENT ME!
     */
    public EBReportBean(final CidsBean kassenzeichen,
            final int mapHeight,
            final int mapWidth,
            final Double scaleDenominator,
            final boolean fillAbfluss) {
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
     */
    protected void loadMap() {
        final SimpleWMS simpleWms = new SimpleWMS(new SimpleWmsGetMapUrl(

                    // "http://s10221.wuppertal-intra.de:7098/alkis/services?&VERSION=1.1.1&REQUEST=GetMap&BBOX=<cismap:boundingBox>&WIDTH=<cismap:width>&HEIGHT=<cismap:height>&SRS=EPSG:31466&FORMAT=image/png&TRANSPARENT=TRUE&BGCOLOR=0xF0F0F0&EXCEPTIONS=application/vnd.ogc.se_xml&LAYERS=algw&STYLES=default"));
                    "http://S102w484:8399/arcgis/services/WuNDa-ALKIS-Hintergrund/MapServer/WMSServer?&VERSION=1.1.1&REQUEST=GetMap&BBOX=<cismap:boundingBox>&WIDTH=<cismap:width>&HEIGHT=<cismap:height>&SRS=EPSG:31466&FORMAT=image/png&TRANSPARENT=FALSE&BGCOLOR=0xF0F0F0&EXCEPTIONS=application/vnd.ogc.se_xml&LAYERS=2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19&STYLES=default,default,default,default,default,default,default,default,default,default,default,default,default,default,default,default,default,default"));
        final HeadlessMapProvider mapProvider = new HeadlessMapProvider();
        mapProvider.setCenterMapOnResize(true);
        final String crsString = "EPSG:31466";
        final Crs crs = new Crs(crsString, "", "", true, true);
        mapProvider.setCrs(crs);
        mapProvider.addLayer(simpleWms);

        final Collection<Feature> features = createFeatures();
        int srid = CrsTransformer.extractSridFromCrs(crsString);
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
                        geometry = CrsTransformer.transformToGivenCrs(geometry, crsString);
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

        final double oldScale = getScaleDenom(oldWidth, oldHeight);
        final double newScale;
        if (scaleDenominator != null) {
            newScale = scaleDenominator;
        } else {
            newScale = Math.round((oldScale / 100) + 0.5d) * 100;
        }

        final double newWidth = oldWidth * newScale / oldScale;
        final double newHeight = oldHeight * newScale / oldScale;

        boundingBox.setX1(centerX - (newWidth / 2));
        boundingBox.setX2(centerX + (newWidth / 2));
        boundingBox.setY1(centerY - (newHeight / 2));
        boundingBox.setY2(centerY + (newHeight / 2));

        mapProvider.setBoundingBox(boundingBox);

        final int mapDPI = Integer.parseInt(NbBundle.getMessage(
                    EBReportBean.class,
                    "FEBReportBean.mapDPI"));

        mapProvider.setFeatureResolutionFactor(mapDPI);
        try {
            mapImage = mapProvider.getImageAndWait(72, mapDPI, mapWidth, mapHeight);
        } catch (final Exception ex) {
            LOG.error("error while creating mapImage", ex);
        }
        scale = "1:"
                    + (NumberFormat.getIntegerInstance().format(newScale));
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
        final double mapWidthOrHeightInMeter = ((((worldWith * ratio) > worldHeight) ? mapWidth : mapHeight) / PPI)
                    * METERS_TO_INCH_FACTOR;
        return (((worldWith * ratio) > worldHeight) ? worldWith : worldHeight) / mapWidthOrHeightInMeter;
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
