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

import org.apache.bcel.generic.D2F;
import org.apache.log4j.Logger;

import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import de.cismet.cids.custom.featurerenderer.verdis_grundis.FlaecheFeatureRenderer;
import de.cismet.cids.custom.reports.wunda_blau.MauernReportBeanWithMapAndImages;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.BoundingBox;
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

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class FebReportBean {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(FebReportBean.class);

    //~ Instance fields --------------------------------------------------------

    private CidsBean kassenzeichen;
    private List<CidsBean> dachflaechen = new LinkedList<CidsBean>();
    private List<CidsBean> versiegelteflaechen = new LinkedList<CidsBean>();
    private String kznr;
    private String hinweise;
    private Image mapImage = null;
    private boolean mapError = false;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FebBean object.
     *
     * @param  kassenzeichen  DOCUMENT ME!
     * @param  hinweise       DOCUMENT ME!
     */
    public FebReportBean(final CidsBean kassenzeichen, final String hinweise) {
        this.hinweise = hinweise;
        this.kassenzeichen = kassenzeichen;
        // loadMap();
        try {
            mapImage = ImageIO.read(new URL(
                        "https://a248.e.akamai.net/camo.github.com/cb9fb6dbdfbac1f2d42e7fd6a710ef6e245b97c6/687474703a2f2f7777772e6369736d65742e64652f696d616765732f70726f6a656374732f73637265656e65722f77756e64612e706e67"));
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
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
            if (flaechenart.equals("Dachfläche")) {
                dachflaechen.add(flaeche);
            } else if (flaechenart.equals("versiegelte Fläche")) {
                versiegelteflaechen.add(flaeche);
            }
        }
        Collections.sort(dachflaechen, new DachflaechenComparator());
        Collections.sort(versiegelteflaechen, new DachflaechenComparator());
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
    public String getKassenzeichennummer() {
        return kznr;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kassenzeichennummer  DOCUMENT ME!
     */
    public void setKassenzeichennummer(final String kassenzeichennummer) {
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
                    "http://s102x284:8399/arcgis/services/WuNDa-Grundlagenkarten/MapServer/WMSServer?service=WMS&VERSION=1.1.1&REQUEST=GetMap&WIDTH=<cismap:width>&HEIGHT=<cismap:height>&BBOX=<cismap:boundingBox>&SRS=EPSG:25832&FORMAT=image/png&TRANSPARENT=TRUE&BGCOLOR=0xF0F0F0&EXCEPTIONS=application/vnd.ogc.se_xml&LAYERS=0&STYLES=default"));
        final MappingComponent map = new MappingComponent(false);

        final ActiveLayerModel mappingModel = new ActiveLayerModel();
        s.addRetrievalListener(new RetrievalListener() {

                @Override
                public void retrievalStarted(final RetrievalEvent e) {
                    System.out.println("Start");
                }

                @Override
                public void retrievalProgress(final RetrievalEvent e) {
                }

                @Override
                public void retrievalComplete(final RetrievalEvent e) {
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
                                final BoundingBox bb = map.getCurrentBoundingBox();
                                final String geolink = "http://localhost:" + 9098 + "/gotoBoundingBox?x1=" + bb.getX1()
                                            + "&y1=" + bb.getY1() + "&x2=" + bb.getX2() + "&y2=" + bb.getY2();     // NOI18N
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
                    mapError = true;
                }

                @Override
                public void retrievalError(final RetrievalEvent e) {
                    mapError = true;
                }
            });
        // disable internal layer widget
        map.setInternalLayerWidgetAvailable(false);
        mappingModel.setSrs(new Crs("EPSG:25832", "", "", true, true));
        mappingModel.addHome(new XBoundingBox(
                374271.251964098,
                5681514.032498134,
                374682.9413952776,
                5681773.852810634,
                "EPSG:25832",
                true));
        // set the model
        map.setMappingModel(mappingModel);
//        final int height = Integer.parseInt(NbBundle.getMessage(
//                    MauernReportBeanWithMapAndImages.class,
//                    "MauernReportBeanWithMapAndImages.mapHeight"));
//        final int width = Integer.parseInt(NbBundle.getMessage(
//                    MauernReportBeanWithMapAndImages.class,
//                    "MauernReportBeanWithMapAndImages.mapWidth"));
//        map.setSize(width, height);
        map.setSize(540, 219);
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
            dsf.setFillingPaint(fr.getFillingStyle());
            dsf.setLineWidth(1);
            dsf.setLinePaint(Color.BLACK);
            dsf.setTransparency(0.8f);
            map.getFeatureCollection().addFeature(dsf);
        }
        map.zoomToFeatureCollection();

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
            final String df1Name = (String)df1.getProperty(FlaechePropertyConstants.PROP__FLAECHENBEZEICHNUNG);
            final String df2Name = (String)df2.getProperty(FlaechePropertyConstants.PROP__FLAECHENBEZEICHNUNG);

            return df1Name.compareTo(df2Name);
        }
    }
}
