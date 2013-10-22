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

import java.util.Collection;
import java.util.List;

import de.cismet.cids.custom.featurerenderer.verdis_grundis.FrontFeatureRenderer;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.features.DefaultXStyledFeature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.piccolo.OldFixedWidthStroke;

import de.cismet.verdis.commons.constants.FrontPropertyConstants;
import de.cismet.verdis.commons.constants.FrontinfoPropertyConstants;
import de.cismet.verdis.commons.constants.KassenzeichenPropertyConstants;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class FrontenReportBean extends EBReportBean {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(FrontenReportBean.class);
    private static final int FRONT_TRANSPARENCY = 200;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FebBean object.
     *
     * @param  kassenzeichen     DOCUMENT ME!
     * @param  mapHeight         DOCUMENT ME!
     * @param  mapWidth          DOCUMENT ME!
     * @param  scaleDenominator  DOCUMENT ME!
     */
    public FrontenReportBean(final CidsBean kassenzeichen,
            final int mapHeight,
            final int mapWidth,
            final Double scaleDenominator) {
        super(kassenzeichen, mapHeight, mapWidth, scaleDenominator);

        loadMap();
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

    @Override
    protected void loadFeaturesInMap(final MappingComponent map) {
        final FrontFeatureRenderer fr = new FrontFeatureRenderer();
        final Collection<CidsBean> fronten = (List<CidsBean>)getKassenzeichenBean().getProperty(
                KassenzeichenPropertyConstants.PROP__FRONTEN);

        final int fontSize = Integer.parseInt(NbBundle.getMessage(
                    FrontenReportBean.class,
                    "FEBReportBean.annotationFontSize"));
        for (final CidsBean b : fronten) {
            try {
                fr.setMetaObject(b.getMetaObject());
            } catch (ConnectionException ex) {
                Exceptions.printStackTrace(ex);
            }
            fr.assign();
            final Geometry g = (Geometry)b.getProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                            + FrontinfoPropertyConstants.PROP__GEOMETRIE + "." + "geo_field");
            final Integer frontNummer = (Integer)b.getProperty(FrontPropertyConstants.PROP__NUMMER);
            final DefaultXStyledFeature dsf = new DefaultXStyledFeature(
                    null,
                    "",
                    "",
                    null,
                    fr.getLineStyle());
            dsf.setGeometry(g);
            final Color c = (Color)fr.getLinePaint();
            final Color c2;
            c2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), FRONT_TRANSPARENCY);
            dsf.setLinePaint(c2);
            final OldFixedWidthStroke ls2 = new OldFixedWidthStroke();
            ls2.setMultiplyer(25);
            dsf.setLineSytle(ls2);
            dsf.setPrimaryAnnotation(Integer.toString(frontNummer));
            dsf.setPrimaryAnnotationPaint(Color.RED);
            dsf.setAutoScale(true);
            dsf.setPrimaryAnnotationFont(new Font("SansSerif", Font.PLAIN, fontSize));
            map.getFeatureCollection().addFeature(dsf);
        }
    }
}
