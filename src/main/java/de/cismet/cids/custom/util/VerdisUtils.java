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
package de.cismet.cids.custom.util;

import Sirius.server.middleware.types.MetaObject;

import org.apache.log4j.Logger;

import java.sql.Date;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Collection;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.verdis.commons.constants.VerdisConstants;

import de.cismet.verdis.gui.AbstractCidsBeanTable;
import de.cismet.verdis.gui.befreiungerlaubnis.BefreiungerlaubnisTable;
import de.cismet.verdis.gui.regenflaechen.RegenFlaechenTable;
import de.cismet.verdis.gui.srfronten.SRFrontenTable;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class VerdisUtils {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(VerdisUtils.class);

    public static final int PROPVAL_ART_DACH = 1;
    public static final int PROPVAL_ART_GRUENDACH = 2;
    public static final int PROPVAL_ART_VERSIEGELTEFLAECHE = 3;
    public static final int PROPVAL_ART_OEKOPFLASTER = 4;
    public static final int PROPVAL_ART_STAEDTISCHESTRASSENFLAECHE = 5;
    public static final int PROPVAL_ART_STAEDTISCHESTRASSENFLAECHEOEKOPLFASTER = 6;
    public static final int PROPVAL_ART_VORLAEUFIGEVERANLASSUNG = 7;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   clipboardBean          DOCUMENT ME!
     * @param   targetBeansCollection  DOCUMENT ME!
     * @param   crossreference         DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static CidsBean createPastedFlaecheBean(final CidsBean clipboardBean,
            final Collection<CidsBean> targetBeansCollection,
            final boolean crossreference) throws Exception {
        final CidsBean pasteBean = CidsBeanSupport.deepcloneCidsBean(clipboardBean);
        if (!crossreference) {
            final CidsBean flaecheInfo = (CidsBean)pasteBean.getProperty(VerdisConstants.PROP.FLAECHE.FLAECHENINFO);
            if (flaecheInfo != null) {
                flaecheInfo.setProperty("id", -1);
                flaecheInfo.getMetaObject().setID(-1);
                flaecheInfo.getMetaObject().forceStatus(MetaObject.NEW);
                final CidsBean geomBean = (CidsBean)flaecheInfo.getProperty(
                        VerdisConstants.PROP.FLAECHENINFO.GEOMETRIE);
                if (geomBean != null) {
                    geomBean.setProperty("id", -1);
                    geomBean.getMetaObject().setID(-1);
                    geomBean.getMetaObject().forceStatus(MetaObject.NEW);
                }
            }
        }

        pasteBean.setProperty(VerdisConstants.PROP.FLAECHE.BEMERKUNG, null);
        pasteBean.setProperty(
            VerdisConstants.PROP.FLAECHE.FLAECHENBEZEICHNUNG,
            getValidFlaechenname(
                (Integer)clipboardBean.getProperty(
                    VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                            + "."
                            + VerdisConstants.PROP.FLAECHENINFO.FLAECHENART
                            + "."
                            + VerdisConstants.PROP.FLAECHENART.ID),
                targetBeansCollection));
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);
        final SimpleDateFormat vDat = new SimpleDateFormat("yy/MM");
        pasteBean.setProperty(VerdisConstants.PROP.FLAECHE.DATUM_VERANLAGUNG, vDat.format(cal.getTime()));
        pasteBean.setProperty(
            VerdisConstants.PROP.FLAECHE.DATUM_AENDERUNG,
            new java.sql.Date(Calendar.getInstance().getTime().getTime()));

        final int id = RegenFlaechenTable.getNextNewBeanId();
        pasteBean.setProperty(VerdisConstants.PROP.FLAECHE.ID, id);
        pasteBean.getMetaObject().setID(id);
        pasteBean.getMetaObject().forceStatus(MetaObject.NEW);

        return pasteBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   clipboardBean          DOCUMENT ME!
     * @param   targetBeansCollection  DOCUMENT ME!
     * @param   crossreference         DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static CidsBean createPastedFrontBean(final CidsBean clipboardBean,
            final Collection<CidsBean> targetBeansCollection,
            final boolean crossreference) throws Exception {
        final CidsBean pasteBean = CidsBeanSupport.deepcloneCidsBean(clipboardBean);

        final int newNummer = getValidNummer(targetBeansCollection);
        pasteBean.setProperty(VerdisConstants.PROP.FRONT.NUMMER, newNummer);

        pasteBean.setProperty(VerdisConstants.PROP.FRONT.BEARBEITET_DURCH, null);

        if (clipboardBean.getProperty(VerdisConstants.PROP.FRONT.FRONTINFO) != null) {
            if (!crossreference) {
                final CidsBean frontInfo = (CidsBean)pasteBean.getProperty(VerdisConstants.PROP.FRONT.FRONTINFO);
                if (frontInfo != null) {
                    frontInfo.setProperty("id", -1);
                    frontInfo.getMetaObject().setID(-1);
                    frontInfo.getMetaObject().forceStatus(MetaObject.NEW);
                    final CidsBean geomBean = (CidsBean)frontInfo.getProperty(VerdisConstants.PROP.FRONTINFO.GEOMETRIE);
                    if (geomBean != null) {
                        geomBean.setProperty("id", -1);
                        geomBean.getMetaObject().setID(-1);
                        geomBean.getMetaObject().forceStatus(MetaObject.NEW);
                    }
                }
            }
        }

        final Calendar cal = Calendar.getInstance();
        pasteBean.setProperty(VerdisConstants.PROP.FRONT.ERFASSUNGSDATUM, new Date(cal.getTime().getTime()));

        final int id = SRFrontenTable.getNextNewBeanId();
        pasteBean.setProperty(VerdisConstants.PROP.FRONT.ID, id);
        pasteBean.getMetaObject().setID(id);
        pasteBean.getMetaObject().forceStatus(MetaObject.NEW);

        return pasteBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   clipboardBean          DOCUMENT ME!
     * @param   targetBeansCollection  DOCUMENT ME!
     * @param   crossreference         DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static CidsBean createPastedBefreiungerlaubnisGeometrieBean(final CidsBean clipboardBean,
            final Collection<CidsBean> targetBeansCollection,
            final boolean crossreference) throws Exception {
        final CidsBean pasteBean = CidsBeanSupport.deepcloneCidsBean(clipboardBean);

        if (!crossreference) {
            final CidsBean befreiungerlaubnisBean = pasteBean;
            final int id = BefreiungerlaubnisTable.getNextNewBeanId();
            befreiungerlaubnisBean.setProperty("id", id);
            befreiungerlaubnisBean.getMetaObject().setID(id);
            befreiungerlaubnisBean.getMetaObject().forceStatus(MetaObject.NEW);
        }

        return pasteBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   clipboardBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static CidsBean createPastedInfoBean(final CidsBean clipboardBean) throws Exception {
        final CidsBean pasteBean = CidsBeanSupport.deepcloneCidsBean(clipboardBean);

        final CidsBean geomBean = (CidsBean)pasteBean.getProperty(
                VerdisConstants.PROP.KASSENZEICHEN_GEOMETRIE.GEOMETRIE);
        if (geomBean != null) {
            geomBean.setProperty("id", -1);
            geomBean.getMetaObject().setID(-1);
            geomBean.getMetaObject().forceStatus(MetaObject.NEW);
        }

        final int id = AbstractCidsBeanTable.getNextNewBeanId();
        pasteBean.setProperty(VerdisConstants.PROP.KASSENZEICHEN_GEOMETRIE.ID, id);
        pasteBean.getMetaObject().setID(id);
        pasteBean.getMetaObject().forceStatus(MetaObject.NEW);

        return pasteBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   art           DOCUMENT ME!
     * @param   flaecheBeans  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getValidFlaechenname(final int art, final Collection<CidsBean> flaecheBeans) {
        int highestNumber = 0;
        String highestBezeichner = null;
        boolean noFlaeche = true;
        for (final CidsBean flaecheBean : flaecheBeans) {
            noFlaeche = false;
            final int a = (Integer)flaecheBean.getProperty(
                    VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                            + "."
                            + VerdisConstants.PROP.FLAECHENINFO.FLAECHENART
                            + "."
                            + VerdisConstants.PROP.FLAECHENART.ID);
            final String bezeichnung = (String)flaecheBean.getProperty(
                    VerdisConstants.PROP.FLAECHE.FLAECHENBEZEICHNUNG);
            if (bezeichnung == null) {
                break;
            }
            if (a == PROPVAL_ART_VORLAEUFIGEVERANLASSUNG) {
                return "A";
            } else if ((a == PROPVAL_ART_DACH) || (a == PROPVAL_ART_GRUENDACH)) {
                // In Bezeichnung m\u00FCsste eigentlich ne Zahl stehen. Einfach ignorieren falls nicht.
                try {
                    final int num = new Integer(bezeichnung).intValue();
                    if (num > highestNumber) {
                        highestNumber = num;
                    }
                } catch (Exception e) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("getValidFlaechenname", e);
                    }
                    break;
                }
            } else {
                if (highestBezeichner == null) {
                    highestBezeichner = bezeichnung;
                } else if ((bezeichnung.trim().length() > highestBezeichner.trim().length())
                            || ((bezeichnung.trim().length() == highestBezeichner.trim().length())
                                && (bezeichnung.compareTo(highestBezeichner) > 0))) {
                    highestBezeichner = bezeichnung;
                }
            }
        }
        if (noFlaeche == true) {
            highestBezeichner = null;
        }
        // highestBezeichner steht jetzt der lexikographisch h\u00F6chste Bezeichner
        // In highestNumber steht die gr\u00F6\u00DFte vorkommende Zahl f\u00FCr Dachfl\u00E4chen
        // log.debug(highestBezeichner);
        // log.debug(highestNumber+"");

        // n\u00E4chste freie Zahl f\u00FCr Dachfl\u00E4chen
        final int newHighestNumber = highestNumber
                    + 1;

        // n\u00E4chste freie Bezeichnung f\u00FCr versiegelte Fl\u00E4chen
        final String newHighestBezeichner = nextFlBez(highestBezeichner);

        switch (art) {
            case PROPVAL_ART_DACH:
            case PROPVAL_ART_GRUENDACH: {
                return newHighestNumber
                            + "";
            }
            case PROPVAL_ART_VERSIEGELTEFLAECHE:
            case PROPVAL_ART_OEKOPFLASTER:
            case PROPVAL_ART_STAEDTISCHESTRASSENFLAECHE:
            case PROPVAL_ART_STAEDTISCHESTRASSENFLAECHEOEKOPLFASTER:
            default: {
                if (noFlaeche) {
                    return "A";
                }
                return newHighestBezeichner;
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   s  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String nextFlBez(String s) {
        boolean carry = false;
        if (s != null) {
            s = s.trim().toUpperCase();
            final char[] charArr = s.toCharArray();
            for (int i = charArr.length - 1; i >= 0; --i) {
                if (charArr[i] != 'Z') {
                    charArr[i] = (char)(charArr[i] + 1);
                    carry = false;
                    break;
                } else {
                    charArr[i] = 'A';
                    carry = true;
                }
            }
            final String end = new String(charArr);

            if (carry) {
                return "A"
                            + end;
            } else {
                return end;
            }
        }
        return "A";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   frontBeans  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static int getValidNummer(final Collection<CidsBean> frontBeans) {
        int highestNummer = 0;
        for (final CidsBean frontBean : frontBeans) {
            final Integer nummer = (Integer)frontBean.getProperty(VerdisConstants.PROP.FRONT.NUMMER);
            if (nummer == null) {
                break;
            }
            try {
                final int num = new Integer(nummer).intValue();
                if (num > highestNummer) {
                    highestNummer = num;
                }
            } catch (Exception ex) {
                break;
            }
        }
        return highestNummer + 1;
    }
}
