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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.linearref.LengthIndexedLine;

import lombok.Getter;
import lombok.Setter;

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

import javax.swing.JLabel;

import de.cismet.cids.custom.featurerenderer.verdis_grundis.FrontFeatureRenderer;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.features.DefaultXStyledFeature;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.gui.piccolo.CustomFixedWidthStroke;
import de.cismet.cismap.commons.gui.piccolo.FeatureAnnotationSymbol;

import de.cismet.verdis.commons.constants.VerdisConstants;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
@Getter
@Setter
public class FrontenReportBean extends EBReportBean {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(FrontenReportBean.class);
    private static final int FRONT_TRANSPARENCY = 200;

    //~ Instance fields --------------------------------------------------------

    private List<CidsBean> fronten = new LinkedList<>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FebBean object.
     *
     * @param  properties     DOCUMENT ME!
     * @param  kassenzeichen  DOCUMENT ME!
     */
    public FrontenReportBean(final Properties properties, final CidsBean kassenzeichen) {
        super(properties, kassenzeichen, false);
        this.fronten = (List<CidsBean>)kassenzeichen.getProperty(VerdisConstants.PROP.KASSENZEICHEN.FRONTEN);
        Collections.sort(this.fronten, new FrontenComparator());
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public boolean isReadyToProceed() {
        return super.isReadyToProceed();
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
        final FrontFeatureRenderer fr = new FrontFeatureRenderer();
        final Collection<CidsBean> fronten = kassenzeichenBean.getBeanCollectionProperty(
                VerdisConstants.PROP.KASSENZEICHEN.FRONTEN);

        final int fontSize = Integer.parseInt(properties.getProperty("annotationFontSize"));
        for (final CidsBean b : fronten) {
            try {
                fr.setMetaObject(b.getMetaObject());
            } catch (final ConnectionException ex) {
                LOG.error(ex, ex);
            }
            fr.assign();
            final Geometry g = (Geometry)b.getProperty(VerdisConstants.PROP.FRONT.FRONTINFO + "."
                            + VerdisConstants.PROP.FRONTINFO.GEOMETRIE + "." + "geo_field");
            if (g != null) {
                final Integer frontNummer = (Integer)b.getProperty(VerdisConstants.PROP.FRONT.NUMMER);
                final DefaultXStyledFeature dsf = new DefaultXStyledFeature(
                        null,
                        "",
                        "",
                        null,
                        fr.getLineStyle());
                dsf.setGeometry(g);
                dsf.setAutoScale(true);
                final Color c = (Color)fr.getLinePaint();
                final Color c2;
                c2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), FRONT_TRANSPARENCY);
                dsf.setLinePaint(c2);
                final CustomFixedWidthStroke ls2 = new CustomFixedWidthStroke(25);
                dsf.setLineSytle(ls2);
                features.add(dsf);
                final DefaultXStyledFeature dsfAnno = new DefaultXStyledFeature(
                        null,
                        "",
                        "",
                        null,
                        null);
                dsfAnno.setAutoScale(true);

                final LengthIndexedLine lil = new LengthIndexedLine(g);
                final Coordinate coordinate = lil.extractPoint(((LineString)g).getLength() / 2d);
                final CoordinateSequence coordSeq = new CoordinateArraySequence(new Coordinate[] { coordinate });
                final Point point = new Point(coordSeq, g.getFactory());
                dsfAnno.setGeometry(point);
                final FeatureAnnotationSymbol symb = new FeatureAnnotationSymbol(new BufferedImage(
                            10,
                            10,
                            BufferedImage.TYPE_INT_ARGB)); // ((StyledFeature)
                symb.setSweetSpotX(0);
                symb.setSweetSpotY(0);
                dsfAnno.setFeatureAnnotationSymbol(symb);
                dsfAnno.setPrimaryAnnotationJustification(JLabel.CENTER_ALIGNMENT);
                dsfAnno.setPrimaryAnnotation(Integer.toString(frontNummer));
                dsfAnno.setPrimaryAnnotationPaint(Color.RED);
                dsfAnno.setPrimaryAnnotationFont(new Font("SansSerif", Font.PLAIN, fontSize));
                features.add(dsfAnno);
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
    private static class FrontenComparator implements Comparator<CidsBean> {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new FrontenComparator object.
         */
        public FrontenComparator() {
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public int compare(final CidsBean o1, final CidsBean o2) {
            final Integer frontNr1 = (Integer)o1.getProperty(VerdisConstants.PROP.FRONT.NUMMER);
            final Integer frontNr2 = (Integer)o2.getProperty(VerdisConstants.PROP.FRONT.NUMMER);

            return frontNr1.compareTo(frontNr2);
        }
    }
}
