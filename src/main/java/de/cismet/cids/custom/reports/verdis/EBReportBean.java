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

import lombok.Getter;
import lombok.Setter;

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
@Getter
@Setter
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
    private String bearbeiterKuerzel;
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
        final String letzteAenderungVon = (String)this.kassenzeichen.getProperty("letzte_aenderung_von");
        if ((letzteAenderungVon != null) && !letzteAenderungVon.isEmpty()) {
            this.bearbeiterKuerzel = letzteAenderungVon.split("@")[0].substring(0, 3);
            for (int i = 1; i < letzteAenderungVon.length(); i++) {
                if (Character.isUpperCase(letzteAenderungVon.charAt(i))) {
                    bearbeiterKuerzel += letzteAenderungVon.charAt(i);
                    break;
                }
            }
        } else {
            this.bearbeiterKuerzel = null;
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isReadyToProceed() {
        return (mapImage != null);
    }
}
