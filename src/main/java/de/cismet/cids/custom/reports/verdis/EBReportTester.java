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

import javax.swing.JFrame;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.verdis.commons.constants.VerdisConstants;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class EBReportTester {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(EBReportTester.class);

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
//                126638);
                5930);
//                41);
        final CidsBean[] beans = new CidsBean[1];
        beans[0] = kassenzeiechenBean;
        if (LOG.isDebugEnabled()) {
            LOG.debug("Anzahl:" + beans.length);
        }
//        final CidsBean[] reportBeans = new CidsBean[beans.length];

        final EBGeneratorDialog dialog = new EBGeneratorDialog(
                kassenzeiechenBean,
                new JFrame(),
                EBGeneratorDialog.Mode.FLAECHEN);
        dialog.show();
        System.out.println("alles fertich.ok");
    }
}
