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
package de.cismet.cids.custom.report;

import Sirius.navigator.exception.ConnectionException;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import org.openide.util.Exceptions;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.image.BufferedImage;

import java.io.File;

import java.text.NumberFormat;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import de.cismet.cids.custom.featurerenderer.verdis_grundis.FlaecheFeatureRenderer;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.Crs;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.features.DefaultStyledFeature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.layerwidget.ActiveLayerModel;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;
import de.cismet.cismap.commons.retrieval.RetrievalEvent;
import de.cismet.cismap.commons.retrieval.RetrievalListener;

import de.cismet.verdis.commons.constants.FlaechePropertyConstants;
import de.cismet.verdis.commons.constants.FlaechenartPropertyConstants;
import de.cismet.verdis.commons.constants.FlaecheninfoPropertyConstants;
import de.cismet.verdis.commons.constants.KassenzeichenPropertyConstants;

import de.cismet.verdis.gui.Main;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class FEBReportBean {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(FEBReportBean.class);
    private static final int FLAECHE_TRANSPARENCY = 150;
    private static final Color FEB_REPORT_OEKOPFLASTER_COLOR = new Color(140, 198, 96, FLAECHE_TRANSPARENCY);

    //~ Instance fields --------------------------------------------------------

    private CidsBean kassenzeichen;
    private List<CidsBean> dachflaechen = new LinkedList<CidsBean>();
    private List<CidsBean> versiegelteflaechen = new LinkedList<CidsBean>();
    private String kznr;
    private String hinweise;
    private String scale;
    private Image mapImage = null;
    private boolean mapError = false;
    private int mapWidth;
    private int mapHeight;
    private Double scaleDenominator;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FebBean object.
     *
     * @param  kassenzeichen     DOCUMENT ME!
     * @param  hinweise          DOCUMENT ME!
     * @param  mapHeight         DOCUMENT ME!
     * @param  mapWidth          DOCUMENT ME!
     * @param  scaleDenominator  DOCUMENT ME!
     */
    public FEBReportBean(final CidsBean kassenzeichen,
            final String hinweise,
            final int mapHeight,
            final int mapWidth,
            final Double scaleDenominator) {
        this.hinweise = hinweise;
        this.kassenzeichen = kassenzeichen;
        this.mapHeight = mapHeight;
        this.mapWidth = mapWidth;
        this.scaleDenominator = scaleDenominator;
        loadMap();
        kznr = ""
                    + (Integer)this.kassenzeichen.getProperty(
                        KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER);
        final List<CidsBean> flaechen = (List<CidsBean>)this.kassenzeichen.getProperty(
                KassenzeichenPropertyConstants.PROP__FLAECHEN);

        for (final CidsBean flaeche : flaechen) {
            final String flaechenart = (String)flaeche.getProperty(FlaechePropertyConstants.PROP__FLAECHENINFO
                            + "."
                            + FlaecheninfoPropertyConstants.PROP__FLAECHENART + "."
                            + FlaechenartPropertyConstants.PROP__ART);
            if (flaechenart.equals("Dachfläche") || flaechenart.equals("Gründach")) {
                dachflaechen.add(flaeche);
            } else if (flaechenart.equals("versiegelte Fläche") || flaechenart.equals("Ökopflaster")) {
                versiegelteflaechen.add(flaeche);
            }
        }
        Collections.sort(dachflaechen, new DachflaechenComparator());
        Collections.sort(versiegelteflaechen, new VersiegelteFlaechenComparator());
    }

    //~ Methods ----------------------------------------------------------------

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
    public List<CidsBean> getDachflaechen() {
        return dachflaechen;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  dachFlaechen  DOCUMENT ME!
     */
    public void setDachflaechen(final List<CidsBean> dachFlaechen) {
        this.dachflaechen = dachFlaechen;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<CidsBean> getVersiegelteflaechen() {
        return versiegelteflaechen;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  versiegelteFlaechen  DOCUMENT ME!
     */
    public void setVersiegelteflaechen(final List<CidsBean> versiegelteFlaechen) {
        this.versiegelteflaechen = versiegelteFlaechen;
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
    public String getHinweise() {
        return hinweise;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  hinweise  DOCUMENT ME!
     */
    public void setHinweise(final String hinweise) {
        this.hinweise = hinweise;
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
        return ((mapImage != null) || mapError) && (kassenzeichen != null) && (versiegelteflaechen != null)
                    && (dachflaechen != null);
    }

    /**
     * DOCUMENT ME!
     */
    private void loadMap() {
        final SimpleWMS s = new SimpleWMS(new SimpleWmsGetMapUrl(
                    "http://s10221.wuppertal-intra.de:7098/alkis/services?&VERSION=1.1.1&REQUEST=GetMap&BBOX=<cismap:boundingBox>&WIDTH=<cismap:width>&HEIGHT=<cismap:height>&SRS=EPSG:31466&FORMAT=image/png&TRANSPARENT=TRUE&BGCOLOR=0xF0F0F0&EXCEPTIONS=application/vnd.ogc.se_xml&LAYERS=algw&STYLES=default"));
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
                    LOG.fatal("map retrieval for feb report complete");
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
                                System.out.println("karte geladen " + bi);
                            } catch (Exception ex) {
                                Exceptions.printStackTrace(ex);
                            }
                        }
                    }.start();
                }

                @Override
                public void retrievalAborted(final RetrievalEvent e) {
//                    LOG.fatal("map retrieval for feb report aborted");
//                    LOG.fatal("map retrieval for feb report aborted: " + e.getErrorType() + " " + e.toString());
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
//                    LOG.fatal("map retrieval error for feb report");
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
        if (LOG.isDebugEnabled()) {
            LOG.debug("setting map size(h/w) to: " + mapHeight + "/" + mapWidth);
        }
        map.setSize(mapWidth, mapHeight);
        // initial positioning of the map
        map.setAnimationDuration(0);
        map.gotoInitialBoundingBox();

        map.unlock();

        final List<CidsBean> flaechen = (List<CidsBean>)kassenzeichen.getProperty(
                KassenzeichenPropertyConstants.PROP__FLAECHEN);
        final FlaecheFeatureRenderer fr = new FlaecheFeatureRenderer();
        for (final CidsBean b : flaechen) {
            try {
                fr.setMetaObject(b.getMetaObject());
            } catch (ConnectionException ex) {
                Exceptions.printStackTrace(ex);
            }
            final Geometry g = (Geometry)b.getProperty(FlaechePropertyConstants.PROP__FLAECHENINFO + "."
                            + FlaecheninfoPropertyConstants.PROP__GEOMETRIE + "." + "geo_field");
            final DefaultStyledFeature dsf = new DefaultStyledFeature();
            dsf.setGeometry(g);
            final Color c = (Color)fr.getFillingStyle();
            final Color c2;
            c2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), FLAECHE_TRANSPARENCY);
            dsf.setFillingPaint(c2);
            dsf.setLineWidth(1);
            dsf.setLinePaint(Color.RED);
            map.getFeatureCollection().addFeature(dsf);
        }
        map.zoomToFeatureCollection();

        if (scaleDenominator != null) {
            map.gotoBoundingBoxWithHistory(map.getBoundingBoxFromScale(scaleDenominator));
            scale = "1:" + NumberFormat.getIntegerInstance().format(scaleDenominator);
        } else {
            double so;
            if (map.getScaleDenominator() > 1000) {
                so = Math.round((map.getScaleDenominator() / 100) + 0.5d) * 100;
            } else {
                so = Math.round((map.getScaleDenominator() / 10) + 0.5) * 10;
            }
            scale = "1:" + NumberFormat.getIntegerInstance().format(so);

            map.gotoBoundingBoxWithHistory(map.getBoundingBoxFromScale(so));
        }
        map.setInteractionMode(MappingComponent.SELECT);

        mappingModel.addLayer(s);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class DachflaechenComparator implements Comparator<CidsBean> {

        //~ Methods ------------------------------------------------------------

        @Override
        public int compare(final CidsBean df1, final CidsBean df2) {
            final Integer df1Name = Integer.parseInt((String)df1.getProperty(
                        FlaechePropertyConstants.PROP__FLAECHENBEZEICHNUNG));
            final Integer df2Name = Integer.parseInt((String)df2.getProperty(
                        FlaechePropertyConstants.PROP__FLAECHENBEZEICHNUNG));

            return df1Name.compareTo(df2Name);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class VersiegelteFlaechenComparator implements Comparator<CidsBean> {

        //~ Methods ------------------------------------------------------------

        @Override
        public int compare(final CidsBean df1, final CidsBean df2) {
            final String df1Name = (String)df1.getProperty(FlaechePropertyConstants.PROP__FLAECHENBEZEICHNUNG);
            final String df2Name = (String)df2.getProperty(FlaechePropertyConstants.PROP__FLAECHENBEZEICHNUNG);

            return df1Name.compareTo(df2Name);
        }
    }
}
