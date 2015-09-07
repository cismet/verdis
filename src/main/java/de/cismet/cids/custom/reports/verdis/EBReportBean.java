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

import org.apache.log4j.Logger;

import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

import org.openide.util.NbBundle;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import java.io.IOException;

import java.text.NumberFormat;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.BoundingBox;
import de.cismet.cismap.commons.Crs;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.layerwidget.ActiveLayerModel;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;
import de.cismet.cismap.commons.retrieval.RetrievalEvent;
import de.cismet.cismap.commons.retrieval.RetrievalListener;

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
    private static final double ppi = 72.156d;

    //~ Instance fields --------------------------------------------------------

    private CidsBean kassenzeichen;
    private String kznr;
    private String scale;
    private Image mapImage = null;
    private Image barcodeImage = null;
    private boolean mapError = false;
    private int mapWidth;
    private int mapHeight;
    private Double scaleDenominator;
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
        return ((mapImage != null) || mapError) && (kassenzeichen != null);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  map  DOCUMENT ME!
     */
    protected abstract void loadFeaturesInMap(final MappingComponent map);

    /**
     * DOCUMENT ME!
     */
    protected void loadMap() {
        final SimpleWMS s = new SimpleWMS(new SimpleWmsGetMapUrl(

                    // "http://s10221.wuppertal-intra.de:7098/alkis/services?&VERSION=1.1.1&REQUEST=GetMap&BBOX=<cismap:boundingBox>&WIDTH=<cismap:width>&HEIGHT=<cismap:height>&SRS=EPSG:31466&FORMAT=image/png&TRANSPARENT=TRUE&BGCOLOR=0xF0F0F0&EXCEPTIONS=application/vnd.ogc.se_xml&LAYERS=algw&STYLES=default"));
                    "http://S102w484:8399/arcgis/services/WuNDa-ALKIS-Hintergrund/MapServer/WMSServer?&VERSION=1.1.1&REQUEST=GetMap&BBOX=<cismap:boundingBox>&WIDTH=<cismap:width>&HEIGHT=<cismap:height>&SRS=EPSG:31466&FORMAT=image/png&TRANSPARENT=FALSE&BGCOLOR=0xF0F0F0&EXCEPTIONS=application/vnd.ogc.se_xml&LAYERS=2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19&STYLES=default,default,default,default,default,default,default,default,default,default,default,default,default,default,default,default,default,default"));
        final MappingComponent map = new MappingComponent(false);

        final ActiveLayerModel mappingModel = new ActiveLayerModel();
        s.addRetrievalListener(new RetrievalListener() {

                @Override
                public void retrievalStarted(final RetrievalEvent e) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("start map retrieval for feb report");
                    }
                }

                @Override
                public void retrievalProgress(final RetrievalEvent e) {
                }

                @Override
                public void retrievalComplete(final RetrievalEvent e) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("map retrieval for feb report complete");
                    }
                    new Thread() {

                        @Override
                        public void run() {
                            try {
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException ex) {
                                }
                                final java.awt.Image img = map.getCamera()
                                            .toImage(map.getWidth(), map.getHeight(), new Color(255, 255, 255, 0));
                                final BufferedImage bi = new BufferedImage(
                                        img.getWidth(null),
                                        img.getHeight(null),
                                        BufferedImage.TYPE_INT_RGB);
                                final Graphics2D g2 = bi.createGraphics();
                                // Draw img into bi so we can write it to file.
                                g2.drawImage(img, 0, 0, null);
                                g2.dispose();
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("bi size (h/w): " + bi.getHeight() + "/" + bi.getWidth());
                                }
                                mapImage = bi;
                            } catch (Exception ex) {
                                LOG.error("error while creating mapImage", ex);
                            }
                        }
                    }.start();
                }

                @Override
                public void retrievalAborted(final RetrievalEvent e) {
                    LOG.fatal(
                        "map retrieval for feb report aborted: "
                                + e.getErrorType() // NOI18N
                                + " Errors: "      // NOI18N
                                + e.getErrors()
                                + " Cause: "
                                + e.getRetrievedObject()); // NOI18N
//                    mapError = true;
                }

                @Override
                public void retrievalError(final RetrievalEvent e) {
                    LOG.fatal("map retrieval error for feb report: " + e.getErrorType() + " " + e.toString());
                    mapError = true;
                }
            });
        // disable internal layer widget
        map.setInternalLayerWidgetAvailable(false);
        mappingModel.setSrs(new Crs("EPSG:31466", "", "", true, true));
        mappingModel.addHome(new XBoundingBox(
                579146.311157169,
                5679930.726695932,
                2579645.8713909937,
                5680274.612347874,
                "EPSG:31466",
                true));
        // set the model
        map.setMappingModel(mappingModel);
        final int mapDPI = Integer.parseInt(NbBundle.getMessage(
                    EBReportBean.class,
                    "FEBReportBean.mapDPI"));
        final int factor = mapDPI / 72;
        map.setSize(mapWidth * factor, mapHeight * factor);
        // initial positioning of the map
        map.setAnimationDuration(0);
        map.gotoInitialBoundingBox();

        map.unlock();

        loadFeaturesInMap(map);

        final double oldScale = map.getScaleDenominator();
        final BoundingBox bbox = map.getBoundingBoxFromScale(oldScale);
        final double realWorldWidthInMeter = bbox.getWidth();

        map.zoomToFeatureCollection();
        final double newScale;

        if (scaleDenominator != null) {
            newScale = scaleDenominator;
        } else {
            final double roundScale = getReportScaleDenom(map.getBoundingBoxFromScale(map.getScaleDenominator())
                            .getWidth());
            newScale = Math.round((roundScale / 100) + 0.5d) * 100;
        }
        map.gotoBoundingBoxWithHistory(map.getBoundingBoxFromScale(
                getMapScaleDenom(newScale, oldScale, realWorldWidthInMeter)));

        // lets calculate the correct scale for the printed report
        final double so = getReportScaleDenom(map.getBoundingBoxFromScale(map.getScaleDenominator()).getWidth());
        scale = "1:" + NumberFormat.getIntegerInstance().format(so);

        map.setInteractionMode(MappingComponent.SELECT);
        mappingModel.addLayer(s);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   realWorldWidthInMeter  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private double getReportScaleDenom(final double realWorldWidthInMeter) {
        final double mapReportWidthInMeter = (mapWidth / ppi) * 0.0254d;
        return realWorldWidthInMeter / mapReportWidthInMeter;
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
        final double mapReportWidthInMeter = (mapWidth / ppi) * 0.0254d;
        final double mapBoundingBoxWidth = wishedScale * mapReportWidthInMeter;
        return mapBoundingBoxWidth * oldScale / realWorldWithInMeter;
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
