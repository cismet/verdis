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
import de.cismet.verdis.server.json.aenderungsanfrage.FlaecheAenderungFlaechenartJson;
import de.cismet.verdis.server.json.aenderungsanfrage.FlaecheAenderungGroesseJson;
import de.cismet.verdis.server.json.aenderungsanfrage.FlaecheAnschlussgradJson;
import de.cismet.verdis.server.json.aenderungsanfrage.FlaecheFlaechenartJson;
import de.cismet.verdis.server.json.aenderungsanfrage.FlaecheAenderungJson;
import de.cismet.verdis.server.json.aenderungsanfrage.FlaechePruefungFlaechenartJson;
import de.cismet.verdis.server.json.aenderungsanfrage.FlaechePruefungGroesseJson;
import de.cismet.verdis.server.json.aenderungsanfrage.NachrichtAnhangJson;
import de.cismet.verdis.server.json.aenderungsanfrage.NachrichtBuergerJson;
import de.cismet.verdis.server.json.aenderungsanfrage.NachrichtJson;
import de.cismet.verdis.server.json.aenderungsanfrage.NachrichtParameterAnschlussgradJson;
import de.cismet.verdis.server.json.aenderungsanfrage.NachrichtParameterJson;
import de.cismet.verdis.server.json.aenderungsanfrage.NachrichtSachberarbeiterJson;
import de.cismet.verdis.server.json.aenderungsanfrage.NachrichtSystemJson;
import de.cismet.verdis.server.json.aenderungsanfrage.PruefungFlaechenartJson;
import de.cismet.verdis.server.json.aenderungsanfrage.PruefungGroesseJson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

    private AenderungsanfrageJson getComplexAnfrageJson() {
        final Map<String, FlaecheAenderungJson> flaechen = new HashMap<>();
        flaechen.put("1", new FlaecheAenderungGroesseJson(1430));            
        flaechen.put("2", new FlaecheAenderungGroesseJson(921, 
                new FlaechePruefungGroesseJson(new PruefungGroesseJson(921, "SteinbacherD102", new Date(47110815)))));
        flaechen.put("8", new FlaecheAenderungFlaechenartJson(
                new FlaecheFlaechenartJson("Gründachfläche", "GDF"), 
                new FlaechePruefungFlaechenartJson(
                        new PruefungFlaechenartJson(new FlaecheFlaechenartJson("Gründachfläche", "GDF"), "SteinbacherD102", new Date(47110815))
                )
        ));

        final List<NachrichtJson> nachrichten = new ArrayList<>();
        nachrichten.add(new NachrichtSachberarbeiterJson(
            new Date(1562059800000l),
            "Sehr geehrte*r Nutzer*in, hier haben Sie die Möglichkeit Änderungen an Ihren Flächen mitzuteilen.",
            "verdis"
        ));
        nachrichten.add(new NachrichtBuergerJson(
            new Date(1562060700000l),
            "Fläche B ist kleiner. Sie ist nicht 40 m² groß, sondern nur 37 m². Sie ist auch nicht an dem Kanal angeschlossen, sondern besteht aus Ökopflaster und versickert. Siehe Foto.",
            "...",
            Arrays.asList(new NachrichtAnhangJson("Ökopflasterfoto.pdf", "1337"))                
        ));
        nachrichten.add(new NachrichtSachberarbeiterJson(
            new Date(1562136300000l),
            "Die Änderung der Fläche werde ich übernehmen. Das Foto ist nicht ausreichend. Bitte übersenden Sie zusätzlich ein Foto der gesamten Fläche. Ökopflaster wird auch nicht als vollständig versickernd angesehen, sondern muss laut Satzung mit 70% seiner Flächen zur Gebührenerhebung herangezogen werden.",
            "Dirk Steinbacher" 
        ));
        nachrichten.add(new NachrichtSystemJson(
            new Date(1562136360000l),
            new NachrichtParameterAnschlussgradJson(NachrichtParameterJson.Type.REJECTED, "1", new FlaecheAnschlussgradJson("Dachfläche", "DF")),
            "Dirk Steinbacher"
        ));
        nachrichten.add(new NachrichtBuergerJson(
            new Date(1562179560000l),
            "Hier das gewünschte Foto. Die Zufahrt entwässert seitlich in die Beete.",
            "...",
            Arrays.asList(new NachrichtAnhangJson("Foto2.pdf", "13374"))                
        ));
        nachrichten.add(new NachrichtSachberarbeiterJson(
            new Date(1562227500000l),
            "Auf dem 2ten Foto sind Rasenkantensteine und ein Gully zu erkennen. Aus diesem Grund muss ich für diese Fläche 24 m² (70% von 37 m²) zur Veranlagung an das Steueramt weitergeben.",
            "Dirk Steinbacher" 
        ));
        nachrichten.add(new NachrichtSystemJson(
            new Date(1562227560000l),
            new NachrichtParameterAnschlussgradJson(NachrichtParameterJson.Type.CHANGED, "1", new FlaecheAnschlussgradJson("Dachfläche", "DF")),
            "Dirk Steinbacher"
        ));
        nachrichten.add(new NachrichtBuergerJson(
            new Date(1562486760000l),
            "So wird eine Nachricht visualisiert, die noch nicht abgesschickt ist.",
            "...",
            true
        ));

        final AenderungsanfrageJson aenderungsanfrage = new AenderungsanfrageJson(
                60004629,
                flaechen,
                nachrichten
        );
        return aenderungsanfrage;
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
