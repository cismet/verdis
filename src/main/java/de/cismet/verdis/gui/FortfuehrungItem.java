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

import Sirius.server.middleware.types.MetaClass;

import java.util.Date;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.verdis.CidsAppBackend;

import de.cismet.verdis.commons.constants.FortfuehrungAnlassPropertyConstants;
import de.cismet.verdis.commons.constants.FortfuehrungPropertyConstants;
import de.cismet.verdis.commons.constants.VerdisConstants;
import de.cismet.verdis.commons.constants.VerdisMetaClassConstants;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class FortfuehrungItem implements Comparable<FortfuehrungItem> {

    //~ Instance fields --------------------------------------------------------

    private Integer anlassId;
    private boolean ist_abgearbeitet;
    private String anlass;
    private String flurstueck_alt;
    private String flurstueck_neu;
    private Date beginn;
    private CidsBean bean;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FortfuehrungItem object.
     *
     * @param  anlassId          DOCUMENT ME!
     * @param  anlass            DOCUMENT ME!
     * @param  beginn            DOCUMENT ME!
     * @param  flurstueck_alt    DOCUMENT ME!
     * @param  flurstueck_neu    DOCUMENT ME!
     * @param  ist_abgearbeitet  DOCUMENT ME!
     */
    public FortfuehrungItem(final Integer anlassId,
            final String anlass,
            final Date beginn,
            final String flurstueck_alt,
            final String flurstueck_neu,
            final boolean ist_abgearbeitet) {
        setAnlassId(anlassId);
        setAnlass(anlass);
        setBeginn(beginn);
        setFlurstueck_alt(flurstueck_alt);
        setFlurstueck_neu(flurstueck_neu);
        setIst_abgearbeitet(ist_abgearbeitet);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Integer getAnlassId() {
        if (bean != null) {
            setAnlassId((Integer)bean.getProperty(FortfuehrungPropertyConstants.PROP__ID));
        }
        return anlassId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  anlassId  DOCUMENT ME!
     */
    private void setAnlassId(final Integer anlassId) {
        this.anlassId = anlassId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isIst_abgearbeitet() {
        if (bean != null) {
            setIst_abgearbeitet((Boolean)bean.getProperty(FortfuehrungPropertyConstants.PROP__IST_ABGEARBEITET));
        }
        return ist_abgearbeitet;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  ist_abgearbeitet  DOCUMENT ME!
     */
    private void setIst_abgearbeitet(final boolean ist_abgearbeitet) {
        this.ist_abgearbeitet = ist_abgearbeitet;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getAnlass() {
        if (bean != null) {
            setAnlass((String)bean.getProperty(
                    FortfuehrungPropertyConstants.PROP__ANLASS
                            + "."
                            + FortfuehrungAnlassPropertyConstants.PROP__NAME));
        }
        return anlass;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  anlass  DOCUMENT ME!
     */
    private void setAnlass(final String anlass) {
        this.anlass = anlass;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getFlurstueck_alt() {
        if (bean != null) {
            setFlurstueck_alt((String)bean.getProperty(FortfuehrungPropertyConstants.PROP__FLURSTUECK_ALT));
        }
        return flurstueck_alt;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  flurstueck_alt  DOCUMENT ME!
     */
    private void setFlurstueck_alt(final String flurstueck_alt) {
        this.flurstueck_alt = flurstueck_alt;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getFlurstueck_neu() {
        if (bean != null) {
            setFlurstueck_neu((String)bean.getProperty(FortfuehrungPropertyConstants.PROP__FLURSTUECK_NEU));
        }
        return flurstueck_neu;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  flurstueck_neu  DOCUMENT ME!
     */
    private void setFlurstueck_neu(final String flurstueck_neu) {
        this.flurstueck_neu = flurstueck_neu;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Date getBeginn() {
        if (bean != null) {
            setBeginn((Date)bean.getProperty(FortfuehrungPropertyConstants.PROP__BEGINN));
        }
        return beginn;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  beginn  DOCUMENT ME!
     */
    private void setBeginn(final Date beginn) {
        this.beginn = beginn;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getBean() {
        if (bean == null) {
            final MetaClass mc = CidsAppBackend.getInstance()
                        .getVerdisMetaClass(VerdisMetaClassConstants.MC_FORTFUEHRUNG);
            setBean(CidsAppBackend.getInstance().getVerdisMetaObject(getAnlassId(), mc.getId()).getBean());
        }
        return bean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  bean  DOCUMENT ME!
     */
    private void setBean(final CidsBean bean) {
        this.bean = bean;
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
