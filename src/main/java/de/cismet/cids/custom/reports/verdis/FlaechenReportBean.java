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

import lombok.Getter;
import lombok.Setter;

import net.sf.jasperreports.engine.JasperReport;

import org.apache.log4j.Logger;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import de.cismet.cids.custom.featurerenderer.verdis_grundis.FlaecheFeatureRenderer;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.utils.serverresources.ServerResourcesLoader;

import de.cismet.cismap.commons.features.DefaultXStyledFeature;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.gui.piccolo.CustomFixedWidthStroke;
import de.cismet.cismap.commons.gui.piccolo.FeatureAnnotationSymbol;

import de.cismet.verdis.commons.constants.VerdisConstants;

import de.cismet.verdis.server.utils.VerdisServerResources;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
@Getter
@Setter
public class FlaechenReportBean extends EBReportBean {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(FlaechenReportBean.class);
    private static final int FLAECHE_TRANSPARENCY = 150;

    //~ Instance fields --------------------------------------------------------

    private List<CidsBean> dachflaechen = new LinkedList<>();
    private List<CidsBean> versiegelteflaechen = new LinkedList<>();
    private String hinweise;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FebBean object.
     *
     * @param  properties     DOCUMENT ME!
     * @param  kassenzeichen  DOCUMENT ME!
     * @param  hinweise       DOCUMENT ME!
     * @param  fillAbfluss    DOCUMENT ME!
     */
    public FlaechenReportBean(final Properties properties,
            final CidsBean kassenzeichen,
            final String hinweise,
            final boolean fillAbfluss) {
        super(properties, kassenzeichen, fillAbfluss);
        final List<CidsBean> flaechen = (List<CidsBean>)kassenzeichen.getProperty(
                VerdisConstants.PROP.KASSENZEICHEN.FLAECHEN);

        for (final CidsBean flaeche : flaechen) {
            final String flaechenart = (String)flaeche.getProperty(VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                            + "."
                            + VerdisConstants.PROP.FLAECHENINFO.FLAECHENART + "."
                            + VerdisConstants.PROP.FLAECHENART.ART_ABKUERZUNG);
            if (flaechenart.equals("DF") || flaechenart.equals("GDF")) {
                dachflaechen.add(flaeche);
            } else if (flaechenart.equals("VF") || flaechenart.equals("LVF") || flaechenart.equals("VFS")
                        || flaechenart.equals("LVS")) {
                versiegelteflaechen.add(flaeche);
            }
        }
        Collections.sort(dachflaechen, new DachflaechenComparator());
        Collections.sort(versiegelteflaechen, new VersiegelteFlaechenComparator());
        this.hinweise = hinweise;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public JasperReport getDachflaechenSubreport() throws Exception {
        return ServerResourcesLoader.getInstance()
                    .loadJasperReport(VerdisServerResources.EB_FLAECHEN_DACH_JASPER.getValue());
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public JasperReport getVersiegelteFlaechenSubreport() throws Exception {
        return ServerResourcesLoader.getInstance()
                    .loadJasperReport(VerdisServerResources.EB_FLAECHEN_VERSIEGELT_JASPER.getValue());
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public JasperReport getHinweiseSubreport() throws Exception {
        return ServerResourcesLoader.getInstance()
                    .loadJasperReport(VerdisServerResources.EB_FLAECHEN_HINWEISE_JASPER.getValue());
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

    /**
     * DOCUMENT ME!
     *
     * @param   kassenzeichenBean  DOCUMENT ME!
     * @param   properties         DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Collection<Feature> createFeatures(final CidsBean kassenzeichenBean, final Properties properties) {
        final Collection<Feature> features = new ArrayList<>();
        final List<CidsBean> flaechen = kassenzeichenBean.getBeanCollectionProperty(
                VerdisConstants.PROP.KASSENZEICHEN.FLAECHEN);
        final FlaecheFeatureRenderer fr = new FlaecheFeatureRenderer();
        final int fontSize = Integer.parseInt(properties.getProperty("annotationFontSize"));
        for (final CidsBean b : flaechen) {
            try {
                fr.setMetaObject(b.getMetaObject());
            } catch (final ConnectionException ex) {
                LOG.error(ex, ex);
            }
            final Geometry g = (Geometry)b.getProperty(VerdisConstants.PROP.FLAECHE.FLAECHENINFO + "."
                            + VerdisConstants.PROP.FLAECHENINFO.GEOMETRIE + "." + "geo_field");
            final DefaultXStyledFeature dsf = new DefaultXStyledFeature(
                    null,
                    "",
                    "",
                    null,
                    new CustomFixedWidthStroke(2f));
            dsf.setGeometry(g);
            final Color c = (Color)fr.getFillingStyle();
            final Color c2;
            c2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), FLAECHE_TRANSPARENCY);
            dsf.setFillingPaint(c2);
            dsf.setLinePaint(Color.RED);
            features.add(dsf);
        }
        for (final CidsBean b : flaechen) {
            try {
                fr.setMetaObject(b.getMetaObject());
            } catch (final ConnectionException ex) {
                LOG.error(ex, ex);
            }
            final Geometry g = (Geometry)b.getProperty(VerdisConstants.PROP.FLAECHE.FLAECHENINFO + "."
                            + VerdisConstants.PROP.FLAECHENINFO.GEOMETRIE + "." + "geo_field");
            if (g != null) {
                final Geometry xg = g.getInteriorPoint();

                final String flaechenbez = (String)b.getProperty(VerdisConstants.PROP.FLAECHE.FLAECHENBEZEICHNUNG);
                final DefaultXStyledFeature dsf = new DefaultXStyledFeature(
                        null,
                        "",
                        "",
                        null,
                        new CustomFixedWidthStroke(2f));
                dsf.setGeometry(xg);
                dsf.setAutoScale(true);
                dsf.setPrimaryAnnotation(flaechenbez);
                dsf.setPrimaryAnnotationJustification(JLabel.CENTER_ALIGNMENT);
                dsf.setPrimaryAnnotationPaint(Color.decode("#1a008b"));
                dsf.setPrimaryAnnotationFont(new Font("SansSerif", Font.PLAIN, (int)((fontSize * 0.5) + 0.5)));

                final BufferedImage bi = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
                final FeatureAnnotationSymbol symb = new FeatureAnnotationSymbol(bi);
                symb.setSweetSpotX(0.5);
                symb.setSweetSpotY(0.5);
                dsf.setIconImage(new ImageIcon(bi));
                dsf.setFeatureAnnotationSymbol(symb);
                features.add(dsf);
            }
        }
        return features;
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
                        VerdisConstants.PROP.FLAECHE.FLAECHENBEZEICHNUNG));
            final Integer df2Name = Integer.parseInt((String)df2.getProperty(
                        VerdisConstants.PROP.FLAECHE.FLAECHENBEZEICHNUNG));

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
            final String df1Name = (String)df1.getProperty(VerdisConstants.PROP.FLAECHE.FLAECHENBEZEICHNUNG);
            final String df2Name = (String)df2.getProperty(VerdisConstants.PROP.FLAECHE.FLAECHENBEZEICHNUNG);

            return df1Name.compareTo(df2Name);
        }
    }
}
