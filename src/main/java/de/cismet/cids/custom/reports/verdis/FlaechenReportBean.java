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

import Sirius.navigator.exception.ConnectionException;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

import java.awt.Color;
import java.awt.Font;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import de.cismet.cids.custom.featurerenderer.verdis_grundis.FlaecheFeatureRenderer;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.features.DefaultXStyledFeature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.piccolo.CustomFixedWidthStroke;

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
public class FlaechenReportBean extends EBReportBean {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(FlaechenReportBean.class);
    private static final int FLAECHE_TRANSPARENCY = 150;

    //~ Instance fields --------------------------------------------------------

    private List<CidsBean> dachflaechen = new LinkedList<CidsBean>();
    private List<CidsBean> versiegelteflaechen = new LinkedList<CidsBean>();
    private String hinweise;

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
    public FlaechenReportBean(final CidsBean kassenzeichen,
            final String hinweise,
            final int mapHeight,
            final int mapWidth,
            final Double scaleDenominator) {
        super(kassenzeichen, mapHeight, mapWidth, scaleDenominator);
        loadMap();
        final List<CidsBean> flaechen = (List<CidsBean>)kassenzeichen.getProperty(
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
    @Override
    public boolean isReadyToProceed() {
        return super.isReadyToProceed() && (versiegelteflaechen != null) && (dachflaechen != null);
    }

    @Override
    protected void loadFeaturesInMap(final MappingComponent map) {
        final List<CidsBean> flaechen = (List<CidsBean>)getKassenzeichenBean().getProperty(
                KassenzeichenPropertyConstants.PROP__FLAECHEN);
        final FlaecheFeatureRenderer fr = new FlaecheFeatureRenderer();
        final int fontSize = Integer.parseInt(NbBundle.getMessage(
                    FlaechenReportBean.class,
                    "FEBReportBean.annotationFontSize"));
        for (final CidsBean b : flaechen) {
            try {
                fr.setMetaObject(b.getMetaObject());
            } catch (ConnectionException ex) {
                Exceptions.printStackTrace(ex);
            }
            final Geometry g = (Geometry)b.getProperty(FlaechePropertyConstants.PROP__FLAECHENINFO + "."
                            + FlaecheninfoPropertyConstants.PROP__GEOMETRIE + "." + "geo_field");
            final String flaechenbez = (String)b.getProperty(FlaechePropertyConstants.PROP__FLAECHENBEZEICHNUNG);
            final DefaultXStyledFeature dsf = new DefaultXStyledFeature(
                    null,
                    "",
                    "",
                    null,
                    new CustomFixedWidthStroke(1f));
            dsf.setGeometry(g);
            final Color c = (Color)fr.getFillingStyle();
            final Color c2;
            c2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), FLAECHE_TRANSPARENCY);
            dsf.setFillingPaint(c2);
            dsf.setLinePaint(Color.RED);
            dsf.setPrimaryAnnotation(flaechenbez);
            dsf.setPrimaryAnnotationPaint(Color.RED);
            dsf.setAutoScale(true);
            dsf.setPrimaryAnnotationFont(new Font("SansSerif", Font.PLAIN, fontSize));
            map.getFeatureCollection().addFeature(dsf);
        }
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