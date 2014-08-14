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

import Sirius.server.middleware.interfaces.proxy.MetaService;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.newuser.User;

import org.apache.log4j.Logger;

import java.sql.Date;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Collection;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.verdis.CidsAppBackend;

import de.cismet.verdis.commons.constants.FlaechePropertyConstants;
import de.cismet.verdis.commons.constants.FlaechenartPropertyConstants;
import de.cismet.verdis.commons.constants.FlaecheninfoPropertyConstants;
import de.cismet.verdis.commons.constants.FrontPropertyConstants;
import de.cismet.verdis.commons.constants.FrontinfoPropertyConstants;
import de.cismet.verdis.commons.constants.VerdisConstants;
import de.cismet.verdis.commons.constants.VerdisMetaClassConstants;

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
        final CidsBean pasteBean = deepcloneFlaecheBean(clipboardBean);
        if (clipboardBean.getProperty(FlaechePropertyConstants.PROP__FLAECHENINFO) != null) {
            final int flaecheninfoId = (Integer)clipboardBean.getProperty(
                    FlaechePropertyConstants.PROP__FLAECHENINFO
                            + "."
                            + FlaecheninfoPropertyConstants.PROP__ID);
            final CidsBean flaecheninfoBean = CidsAppBackend.getInstance()
                        .getVerdisMetaObject(
                                flaecheninfoId,
                                CidsBean.getMetaClassFromTableName(
                                    VerdisConstants.DOMAIN,
                                    VerdisMetaClassConstants.MC_FLAECHENINFO).getId())
                        .getBean();
            if (crossreference) {
                pasteBean.setProperty(FlaechePropertyConstants.PROP__FLAECHENINFO, flaecheninfoBean);
            } else {
                pasteBean.setProperty(FlaechePropertyConstants.PROP__FLAECHENINFO + "."
                            + FlaecheninfoPropertyConstants.PROP__ANSCHLUSSGRAD,
                    flaecheninfoBean.getProperty(FlaecheninfoPropertyConstants.PROP__ANSCHLUSSGRAD));
                pasteBean.setProperty(FlaechePropertyConstants.PROP__FLAECHENINFO + "."
                            + FlaecheninfoPropertyConstants.PROP__FLAECHENART,
                    flaecheninfoBean.getProperty(FlaecheninfoPropertyConstants.PROP__BESCHREIBUNG));
                pasteBean.setProperty(FlaechePropertyConstants.PROP__FLAECHENINFO + "."
                            + FlaecheninfoPropertyConstants.PROP__FLAECHENART,
                    flaecheninfoBean.getProperty(FlaecheninfoPropertyConstants.PROP__BESCHREIBUNG));
            }
        }

        pasteBean.setProperty(FlaechePropertyConstants.PROP__BEMERKUNG, null);
        pasteBean.setProperty(
            FlaechePropertyConstants.PROP__FLAECHENBEZEICHNUNG,
            getValidFlaechenname(
                (Integer)clipboardBean.getProperty(
                    FlaechePropertyConstants.PROP__FLAECHENINFO
                            + "."
                            + FlaecheninfoPropertyConstants.PROP__FLAECHENART
                            + "."
                            + FlaechenartPropertyConstants.PROP__ID),
                targetBeansCollection));
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);
        final SimpleDateFormat vDat = new SimpleDateFormat("yy/MM");
        pasteBean.setProperty(FlaechePropertyConstants.PROP__DATUM_VERANLAGUNG, vDat.format(cal.getTime()));
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
        final CidsBean pasteBean = deepcloneFrontBean(clipboardBean);

        final int newNummer = getValidNummer(targetBeansCollection);
        pasteBean.setProperty(FrontPropertyConstants.PROP__NUMMER, newNummer);

        pasteBean.setProperty(FrontPropertyConstants.PROP__BEARBEITET_DURCH, null);

        if (clipboardBean.getProperty(FrontPropertyConstants.PROP__FRONTINFO) != null) {
            final int frontinfoId = (Integer)clipboardBean.getProperty(
                    FrontPropertyConstants.PROP__FRONTINFO
                            + "."
                            + FrontinfoPropertyConstants.PROP__ID);
            final CidsBean frontinfoBean = CidsAppBackend.getInstance()
                        .getVerdisMetaObject(
                                frontinfoId,
                                CidsBean.getMetaClassFromTableName(
                                    VerdisConstants.DOMAIN,
                                    VerdisMetaClassConstants.MC_FRONTINFO).getId())
                        .getBean();
            if (crossreference) {
                pasteBean.setProperty(FrontPropertyConstants.PROP__FRONTINFO, frontinfoBean);
            } else {
                pasteBean.setProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                            + FrontinfoPropertyConstants.PROP__STRASSE,
                    frontinfoBean.getProperty(FrontinfoPropertyConstants.PROP__STRASSE));
                pasteBean.setProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                            + FrontinfoPropertyConstants.PROP__WD_PRIO_OR,
                    frontinfoBean.getProperty(FrontinfoPropertyConstants.PROP__WD_PRIO_OR));
                pasteBean.setProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                            + FrontinfoPropertyConstants.PROP__WD_VERANLAGUNG,
                    frontinfoBean.getProperty(FrontinfoPropertyConstants.PROP__WD_VERANLAGUNG));
                pasteBean.setProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                            + FrontinfoPropertyConstants.PROP__SR_KLASSE_OR,
                    frontinfoBean.getProperty(FrontinfoPropertyConstants.PROP__SR_KLASSE_OR));
                pasteBean.setProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                            + FrontinfoPropertyConstants.PROP__SR_VERANLAGUNG,
                    frontinfoBean.getProperty(FrontinfoPropertyConstants.PROP__SR_VERANLAGUNG));
            }
        }

        final Calendar cal = Calendar.getInstance();
        pasteBean.setProperty(FrontPropertyConstants.PROP__ERFASSUNGSDATUM, new Date(cal.getTime().getTime()));
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
        final CidsBean pasteBean = deepcloneFrontBean(clipboardBean);
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
                    FlaechePropertyConstants.PROP__FLAECHENINFO
                            + "."
                            + FlaecheninfoPropertyConstants.PROP__FLAECHENART
                            + "."
                            + FlaechenartPropertyConstants.PROP__ID);
            final String bezeichnung = (String)flaecheBean.getProperty(
                    FlaechePropertyConstants.PROP__FLAECHENBEZEICHNUNG);
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
     * @param   cidsBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static CidsBean deepcloneFlaecheBean(final CidsBean cidsBean) throws Exception {
        final CidsBean deepclone = CidsBeanSupport.deepcloneCidsBean(cidsBean);
        final CidsBean origFlaecheninfo = (CidsBean)cidsBean.getProperty(
                FlaechePropertyConstants.PROP__FLAECHENINFO);
        if (origFlaecheninfo != null) {
            deepclone.setProperty(
                FlaechePropertyConstants.PROP__FLAECHENINFO
                        + "."
                        + FlaecheninfoPropertyConstants.PROP__ANSCHLUSSGRAD,
                cidsBean.getProperty(
                    FlaechePropertyConstants.PROP__FLAECHENINFO
                            + "."
                            + FlaecheninfoPropertyConstants.PROP__ANSCHLUSSGRAD));
            deepclone.setProperty(
                FlaechePropertyConstants.PROP__FLAECHENINFO
                        + "."
                        + FlaecheninfoPropertyConstants.PROP__FLAECHENART,
                cidsBean.getProperty(
                    FlaechePropertyConstants.PROP__FLAECHENINFO
                            + "."
                            + FlaecheninfoPropertyConstants.PROP__FLAECHENART));
        }
        return deepclone;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   cidsBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static CidsBean deepcloneFrontBean(final CidsBean cidsBean) throws Exception {
        final CidsBean deepclone = CidsBeanSupport.deepcloneCidsBean(cidsBean);
        deepclone.setProperty(
            FrontPropertyConstants.PROP__FRONTINFO
                    + "."
                    + FrontinfoPropertyConstants.PROP__SR_KLASSE_OR,
            cidsBean.getProperty(
                FrontPropertyConstants.PROP__FRONTINFO
                        + "."
                        + FrontinfoPropertyConstants.PROP__SR_KLASSE_OR));
        deepclone.setProperty(
            FrontPropertyConstants.PROP__FRONTINFO
                    + "."
                    + FrontinfoPropertyConstants.PROP__WD_PRIO_OR,
            cidsBean.getProperty(
                FrontPropertyConstants.PROP__FRONTINFO
                        + "."
                        + FrontinfoPropertyConstants.PROP__WD_PRIO_OR));
        deepclone.setProperty(
            FrontPropertyConstants.PROP__FRONTINFO
                    + "."
                    + FrontinfoPropertyConstants.PROP__STRASSE,
            cidsBean.getProperty(
                FrontPropertyConstants.PROP__FRONTINFO
                        + "."
                        + FrontinfoPropertyConstants.PROP__STRASSE));
        // deepclone.setProperty(FrontinfoPropertyConstants.PROP__SATZUNG,
        // cidsBean.getProperty(FrontinfoPropertyConstants.PROP__SATZUNG));

        final CidsBean sr_klasse = (CidsBean)deepclone.getProperty(FrontPropertyConstants.PROP__FRONTINFO
                        + "."
                        + FrontinfoPropertyConstants.PROP__SR_KLASSE_OR);
        if (sr_klasse != null) {
            sr_klasse.getMetaObject().forceStatus(MetaObject.NO_STATUS);
        }

        final CidsBean wd_prio = (CidsBean)deepclone.getProperty(FrontPropertyConstants.PROP__FRONTINFO
                        + "."
                        + FrontinfoPropertyConstants.PROP__WD_PRIO_OR);
        if (wd_prio != null) {
            wd_prio.getMetaObject().forceStatus(MetaObject.NO_STATUS);
        }

        final CidsBean strasse = (CidsBean)deepclone.getProperty(FrontPropertyConstants.PROP__FRONTINFO
                        + "."
                        + FrontinfoPropertyConstants.PROP__STRASSE);
        if (strasse != null) {
            strasse.getMetaObject().forceStatus(MetaObject.NO_STATUS);
        }
        return deepclone;
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
            final Integer nummer = (Integer)frontBean.getProperty(FrontPropertyConstants.PROP__NUMMER);
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
