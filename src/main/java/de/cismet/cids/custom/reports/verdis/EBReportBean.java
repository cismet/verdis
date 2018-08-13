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

import java.awt.Image;

import java.util.Properties;

import de.cismet.cids.dynamics.CidsBean;

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

    //~ Instance fields --------------------------------------------------------

    private CidsBean kassenzeichen;
    private String kznr;
    private String scale;
    private Image mapImage = null;
    private Image barcodeImage = null;
    private boolean fillAbflusswirksamkeit = false;
    private String mapHint = null;
    private final Properties properties;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FebBean object.
     *
     * @param  properties     DOCUMENT ME!
     * @param  kassenzeichen  DOCUMENT ME!
     * @param  fillAbfluss    DOCUMENT ME!
     */
    public EBReportBean(final Properties properties, final CidsBean kassenzeichen, final boolean fillAbfluss) {
        this.properties = properties;
        this.kassenzeichen = kassenzeichen;
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
    public String getMapHint() {
        return mapHint;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  mapHint  DOCUMENT ME!
     */
    public void setMapHint(final String mapHint) {
        this.mapHint = mapHint;
    }

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
    public Properties getProperties() {
        return properties;
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
     *
     * @param  barcodeImage  DOCUMENT ME!
     */
    public void setBarcodeImage(final Image barcodeImage) {
        this.barcodeImage = barcodeImage;
    }
}
