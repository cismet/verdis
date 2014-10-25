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
package de.cismet.verdis.crossover;

import org.openide.util.lookup.ServiceProvider;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import de.cismet.remote.AbstractRESTRemoteControlMethod;
import de.cismet.remote.RESTRemoteControlMethod;

import de.cismet.verdis.CidsAppBackend;

import de.cismet.verdis.gui.Main;

/**
 * DOCUMENT ME!
 *
 * @author   spuhl
 * @author   Benjamin Friedrich (benjamin.friedrich@cismet.de)
 * @version  $Revision$, $Date$
 */
@Path("/gotoKassenzeichen/")
@ServiceProvider(service = RESTRemoteControlMethod.class)
public class GoToKassenzeichenRemoteMethod extends AbstractRESTRemoteControlMethod {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
            GoToKassenzeichenRemoteMethod.class);

    //~ Instance fields --------------------------------------------------------

    @Context
    private UriInfo context;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new GoToKassenzeichenRemoteMethod object.
     */
    public GoToKassenzeichenRemoteMethod() {
        super(-1, "/gotoKassenzeichen/");
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   kassenzeichen  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @GET
    @Produces("text/html")
    public String gotoKassenzeichen(@QueryParam("kassenzeichen") final String kassenzeichen) {
        if (log.isDebugEnabled()) {
            log.debug("Crossover: gotoKassenzeichen");
        }

        try {
            final String host = context.getBaseUri().getHost();
            if (!host.equals("localhost") && !host.equals("127.0.0.1")) {
                log.info("Keine Request von remote rechnern möglich: " + host);
                return
                    "<html>Es können nur Requests vom lokalen Rechner abgesetzt werden. Es kann nicht zum gewünschten Kassenzeichen gewechselt werden</html>";
            }
        } catch (Exception ex) {
            log.error("Fehler beim bestimmen des Hosts Request nicht möglich");
            return
                "<html>Der Host konnte nicht bestimmt werden. Es kann nicht zum gewünschten Kassenzeichen gewechselt werden</html>";
        }

        // Mit History
        if (Main.getInstance().isLoggedIn()) {
            try {
                // ToDo ugly
                CidsAppBackend.getInstance().gotoKassenzeichen(kassenzeichen);
            } catch (Exception ex) {
                log.error("Fehler bei gotoKassenzeichen: ", ex);
                return "<html>Fehler beim laden des Kassenzeichens: " + ex.getMessage() + "</html>";
            }
            return "<html>Gehe zu kassenzeichen: " + kassenzeichen + "</html>";
        } else {
            final String notLoggedIn = "Kassenzeichen kann nicht geladen werden. Benutzer ist noch nicht eingeloggt.";
            if (log.isDebugEnabled()) {
                log.debug(notLoggedIn);
            }
            return "<html>" + notLoggedIn + "</html>";
        }
    }
}
