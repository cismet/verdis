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

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;

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
//                74211);
                41);
        final CidsBean[] beans = new CidsBean[1];
        beans[0] = kassenzeiechenBean;
        if (LOG.isDebugEnabled()) {
            LOG.debug("Anzahl:" + beans.length);
        }
//        final CidsBean[] reportBeans = new CidsBean[beans.length];

        final FEPGeneratorDialog dialog = new FEPGeneratorDialog(kassenzeiechenBean, new JFrame());
        dialog.show();
        System.out.println("alles fertich.ok");
    }
}
