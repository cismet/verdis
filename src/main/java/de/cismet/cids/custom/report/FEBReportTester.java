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
package de.cismet.cids.custom.report;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.verdis.commons.constants.FlaechePropertyConstants;
import de.cismet.verdis.commons.constants.FlaecheninfoPropertyConstants;
import de.cismet.verdis.commons.constants.KassenzeichenPropertyConstants;
import de.cismet.verdis.commons.constants.VerdisConstants;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class FEBReportTester {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(FEBReportTester.class);

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   args  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void main(final String[] args) throws Exception {
        final CidsBean kassenzeiechenBean = DevelopmentTools.createCidsBeanFromRMIConnectionOnLocalhost(
                VerdisConstants.DOMAIN,
                "VORN_schreiben_KA",
                "SteinbacherD102",
                "kif",
                "kassenzeichen",
                74211);
//                41);
        final CidsBean[] beans = new CidsBean[1];
        beans[0] = kassenzeiechenBean;
        if (LOG.isDebugEnabled()) {
            LOG.debug("Anzahl:" + beans.length);
        }
//        final CidsBean[] reportBeans = new CidsBean[beans.length];
        final List<FebReportBean> reportbeans = new ArrayList<FebReportBean>();
        final int i = 0;
        for (final CidsBean kassenzeichen : beans) {
            final FebReportBean x = new FebReportBean(kassenzeichen, null);
            reportbeans.add(x);
        }

        final ArrayList<String> fields = new ArrayList<String>(Arrays.asList(
                    KassenzeichenPropertyConstants.PROP__FLAECHEN
                            + "."
                            + FlaechePropertyConstants.PROP__FLAECHENINFO
                            + "."
                            + FlaecheninfoPropertyConstants.PROP__GROESSE_KORREKTUR,
                    KassenzeichenPropertyConstants.PROP__FLAECHEN
                            + "."
                            + FlaechePropertyConstants.PROP__FLAECHENINFO
                            + "."
                            + FlaechePropertyConstants.PROP__FLAECHENBEZEICHNUNG));
        LOG.fatal(Arrays.toString(fields.toArray()));

        for (final String pv : fields) {
            try {
//                final Object out = BeanUtils.getProperty(beans[0], pv);
                System.out.println("<field name=\"" + pv + "\" class=\"java.lang.String\"/>");
            } catch (Exception skip) {
                LOG.fatal(skip, skip);

                System.out.println("!" + pv + "-->Problem");
            }
        }

        boolean ready = false;
        do {
            ready = true;
            for (final FebReportBean f : reportbeans) {
                if (!f.isReadyToProceed()) {
                    ready = false;
                    break;
                }
            }
        } while (!ready);

        DevelopmentTools.showReportForBeans(
            "/de/cismet/cids/custom/report/feb.jasper",
            reportbeans);
        DevelopmentTools.showReportForBeans(
            "/de/cismet/cids/custom/report/feb_map.jasper",
            reportbeans);
        System.out.println("alles fertich.ok");
    }
}
