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
package de.cismet.verdis.gui;

import com.vividsolutions.jts.geom.Geometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class FortfuehrungItem implements Comparable<FortfuehrungItem> {

    //~ Instance fields --------------------------------------------------------

    private final Integer anlassId;
    private final String ffn;
    private final boolean ist_abgearbeitet;
    private final String anlass;
    private final String flurstueck_alt;
    private final String flurstueck_neu;
    private final Date beginn;
    private final Collection<Geometry> geoms = new ArrayList<Geometry>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FortfuehrungItem object.
     *
     * @param  anlassId        DOCUMENT ME!
     * @param  ffn             DOCUMENT ME!
     * @param  anlass          DOCUMENT ME!
     * @param  beginn          DOCUMENT ME!
     * @param  flurstueck_alt  DOCUMENT ME!
     * @param  flurstueck_neu  DOCUMENT ME!
     */
    public FortfuehrungItem(final Integer anlassId,
            final String ffn,
            final String anlass,
            final Date beginn,
            final String flurstueck_alt,
            final String flurstueck_neu) {
        this.ffn = ffn;
        this.anlassId = anlassId;
        this.anlass = anlass;
        this.beginn = beginn;
        this.flurstueck_alt = flurstueck_alt;
        this.flurstueck_neu = flurstueck_neu;
        this.ist_abgearbeitet = false;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Integer getAnlassId() {
        return anlassId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getFfn() {
        return ffn;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isIst_abgearbeitet() {
        return ist_abgearbeitet;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getAnlass() {
        return anlass;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getFlurstueck_alt() {
        return flurstueck_alt;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getFlurstueck_neu() {
        return flurstueck_neu;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Date getBeginn() {
        return beginn;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<Geometry> getGeoms() {
        return geoms;
    }

    @Override
    public int compareTo(final FortfuehrungItem o) {
        if (o == null) {
            return 1;
        } else if (getBeginn() != null) {
            return getBeginn().compareTo(o.getBeginn());
        } else if (o.getBeginn() == null) {
            return 0;
        } else {
            return -1;
        }
    }
}
