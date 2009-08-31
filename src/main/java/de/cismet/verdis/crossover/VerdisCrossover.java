/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.verdis.crossover;

import de.cismet.verdis.gui.Main;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 *
 * @author spuhl
 */
@Path("/verdis/")
public class VerdisCrossover {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(VerdisCrossover.class);
    
    @GET
    @Produces("text/html")
    @Path("/gotoKassenzeichen/")
    public String gotoKassenzeichen(@QueryParam("kassenzeichen") String kassenzeichen) {
        log.debug("Crossover: gotoKassenzeichen");
        //Mit History
        try{
            //ToDo ugly
        Main.getCurrentInstance().getKzPanel().gotoKassenzeichen(kassenzeichen);
        }catch(Exception ex){
            log.error("Fehler bei gotoKassenzeichen: ",ex);
        }
        return "<html>Test</html>";
    }
}
