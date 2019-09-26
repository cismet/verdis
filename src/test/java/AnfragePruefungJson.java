/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.cismet.cids.dynamics.CidsBean;
import de.cismet.verdis.gui.aenderungsanfrage.AenderungsanfrageHandler;
import de.cismet.verdis.server.json.aenderungsanfrage.AenderungsanfrageJson;
import de.cismet.verdis.server.json.aenderungsanfrage.FlaecheAnschlussgradJson;
import de.cismet.verdis.server.json.aenderungsanfrage.FlaecheFlaechenartJson;
import de.cismet.verdis.server.json.aenderungsanfrage.FlaecheAenderungJson;
import de.cismet.verdis.server.json.aenderungsanfrage.FlaechePruefungJson;
import de.cismet.verdis.server.json.aenderungsanfrage.NachrichtAnhangJson;
import de.cismet.verdis.server.json.aenderungsanfrage.NachrichtJson;
import de.cismet.verdis.server.json.aenderungsanfrage.PruefungJson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 *
 * @author pd
 */
public class AnfragePruefungJson {
    private ObjectMapper mapper;
    
    public AnfragePruefungJson() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);        
    }
    
    @After
    public void tearDown() {
    }
        
    private AenderungsanfrageJson getSimpleAnfrageJson() {                    
            final Map<String, FlaecheAenderungJson> flaechen = new HashMap<>();
            flaechen.put("5", new FlaecheAenderungJson.Groesse(12));
            final AenderungsanfrageJson anfrageJson = new AenderungsanfrageJson(
                    60004629,
                    flaechen,
                    new ArrayList<>(
                        Arrays.asList(
                            new NachrichtJson(NachrichtJson.Typ.CITIZEN,
                                new Date(47110815),
                                "Da passt was nicht weil isso, siehe lustiges pdf !",
                                "Bürger",
                                Arrays.asList(new NachrichtAnhangJson("lustiges.pdf", "aaa-bbb-ccc"))
                            )
                        )
                    )
            );
            return anfrageJson;
    }

    private AenderungsanfrageJson getComplexAnfrageJson() {
        final AenderungsanfrageJson anfrageJson = getSimpleAnfrageJson();        
        anfrageJson.getNachrichten().add(new NachrichtJson(NachrichtJson.Typ.CLERK,
            new Date(47110815),
            "Konnte nichts feststellen, alles in Ordnung.", "Dirk Steinbacher"));
        anfrageJson.getNachrichten().add(new NachrichtJson(
            new Date(47110815),
            "REJECT_GROESSE(5, 12)"));
        anfrageJson.getNachrichten().add(new NachrichtJson(NachrichtJson.Typ.CITIZEN,
            new Date(47110815),
            "Oh, falsches PDF, siehe richtiges pdf.", 
            "Bürger",
            Arrays.asList(new NachrichtAnhangJson("richtiges.pdf", "ddd-eee-fff"))));
        anfrageJson.getNachrichten().add(new NachrichtJson(NachrichtJson.Typ.CLERK,
            new Date(47110815), 
            "Ach so, verstehe. Alles Klar !", "Dirk Steinbacher"));
        anfrageJson.getNachrichten().add(new NachrichtJson(
            new Date(47110815),
            "ACCEPT_GROESSE(5, 12)"));
        anfrageJson.getNachrichten().add(new NachrichtJson(NachrichtJson.Typ.CITIZEN,
                new Date(47110815), 
                "Geht doch, danke.", "Bürger"));

        anfrageJson.getFlaechen().get("5").setFlaechenart(new FlaecheFlaechenartJson("Gründach", "GDF"));
        anfrageJson.getFlaechen().get("5").setAnschlussgrad(new FlaecheAnschlussgradJson("Am Kanal angeschlossen", "angeschl."));
        anfrageJson.getFlaechen().get("5").setPruefung(new FlaechePruefungJson(
                new PruefungJson.Groesse(12, "test", new Date(47110815)),
                new PruefungJson.Flaechenart(new FlaecheFlaechenartJson("Dachfläche", "DGF"), "test", new Date(47110815)),
                null
            )
        );

        return anfrageJson;
    }

    //@Test
    public void testAenderungsanfragePruefung() throws JsonProcessingException, Exception {        
            final AenderungsanfrageJson anfrageJson = getComplexAnfrageJson();
            final CidsBean kassenzeichenBean = CidsBean.createNewCidsBeanFromJSON(
                true, 
                IOUtils.toString(getClass().getClassLoader().getResourceAsStream("kassenzeichen60004629.json"), "UTF-8")
            );            
            
            AenderungsanfrageHandler.getInstance().doPruefung(anfrageJson, kassenzeichenBean);            
    }
}
