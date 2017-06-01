/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.io.InputStream;

import java.net.URL;

import java.util.*;

import de.cismet.cismap.commons.preferences.CismapPreferences;
import de.cismet.cismap.commons.wfsforms.AbstractWFSForm;
import de.cismet.cismap.commons.wfsforms.WFSFormFactory;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten.hell@cismet.de
 * @version  $Revision$, $Date$
 */
@Getter
@Setter(AccessLevel.PRIVATE)
public class AppPreferences {

    //~ Instance fields --------------------------------------------------------

    private String version;
    private String environment;
    private String mode;

    private String domainserver;
    private int kassenzeichenClassId;
    private int geomClassId;
    private String kassenzeichenSuche;
    private String fortfuehrungLinkFormat;
    private double coordinateDuplicateThreshold;
    private Vector usergroups = new Vector();
    private Vector rwGroups = new Vector();
    private CismapPreferences cismapPrefs;
    private String standaloneDomainname;
    private String standaloneCallServerHost;
    private String reportUrl = "http://s10220:8090/verdis/vorn.pdf?KASSENZEICHEN=";
    private String albUrl = "http://www.cismet.de";
    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private LinkedHashMap<String, AbstractWFSForm> wfsForms = new LinkedHashMap<String, AbstractWFSForm>();
    // ADDED FOR RM PLUGIN FUNCTIONALTY 22.07.07 Sebastian Puhl
    private int primaryPort;
    private int secondaryPort;
    private String rmRegistryServerPath;
    private int verdisCrossoverPort;
    private int lagisCrossoverPort;
    private double flurstueckBuffer = -0.5;
    private boolean veranlagungOnlyForChangedValues = false;

    private String appbackendDomain = null;
    private String appbackendConnectionclass = null;
    private String appbackendCallserverurl = null;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of AppPreferences.
     *
     * @param  is  DOCUMENT ME!
     */

    public AppPreferences(final InputStream is) {
        try {
            final SAXBuilder builder = new SAXBuilder(false);
            final Document doc = builder.build(is);

            final Element prefs = doc.getRootElement();
            readFromAppPreferences(prefs);
        } catch (Exception e) {
            log.warn("Fehler beim Lesen der Einstellungen (InputStream)", e);
        }
    }
    /**
     * Creates a new AppPreferences object.
     *
     * @param  url  DOCUMENT ME!
     */
    public AppPreferences(final URL url) {
        try {
            final SAXBuilder builder = new SAXBuilder(false);
            final Document doc = builder.build(url);
            final Element prefs = doc.getRootElement();
            readFromAppPreferences(prefs);
        } catch (Exception e) {
            log.warn("Fehler beim Lesen der Einstellungen (" + url.toString() + ")", e);
        }
    }
    /**
     * Creates a new AppPreferences object.
     *
     * @param  appPreferences  DOCUMENT ME!
     */
    public AppPreferences(final Element appPreferences) {
        readFromAppPreferences(appPreferences);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getAlbUrl() {
        return albUrl;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  albUrl  DOCUMENT ME!
     */
    public void setAlbUrl(final String albUrl) {
        this.albUrl = albUrl;
    }
    /**
     * DOCUMENT ME!
     *
     * @param  root  DOCUMENT ME!
     */
    private void readFromAppPreferences(final Element root) {
        try {
            version = root.getChild("general").getAttribute("version").getValue();
            environment = root.getChild("general").getAttribute("environment").getValue();
            mode = root.getChild("general").getAttribute("mode").getValue();
            domainserver = root.getChild("general").getAttribute("domainserver").getValue();
            kassenzeichenClassId = root.getChild("general").getAttribute("kassenzeichenClassId").getIntValue();
            geomClassId = root.getChild("general").getAttribute("geomClassId").getIntValue();
            kassenzeichenSuche = root.getChild("general").getAttribute("kassenzeichenSuche").getValue();
            fortfuehrungLinkFormat = root.getChild("general").getAttribute("fortfuehrungLinkFormat").getValue();
            try {
                coordinateDuplicateThreshold = Double.parseDouble(root.getChild("general").getAttribute(
                            "coordinateDuplicateThreshold").getValue());
            } catch (final Exception ex) {
                coordinateDuplicateThreshold = 0;
            }
            standaloneDomainname = root.getChild("standalone").getAttribute("userdomainname").getValue();
            standaloneCallServerHost = root.getChild("standalone").getAttribute("callserverhost").getValue();
            try {
                // Added for RM Plugin functionalty 22.07.2007 Sebastian Puhl
                primaryPort = Integer.parseInt(root.getChild("rmPlugin").getChild("primaryPort").getText());
            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.debug("Fehler beim parsen des primaryPorts --> benutze default 1099");
                }
                primaryPort = 1099;
            }

            try {
                secondaryPort = Integer.parseInt(root.getChild("rmPlugin").getChild("secondaryPort").getText());
            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.debug("Fehler beim parsen des primaryPorts --> benutze default 9001");
                }
                secondaryPort = 9001;
            }

            try {
                rmRegistryServerPath = root.getChild("rmPlugin").getChild("rmRegistryServer").getText();
            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.debug(
                        "Fehler beim parsen des primaryPorts --> benutze default rmi://localhost:1099/RMRegistryServer");
                }
                rmRegistryServerPath = "rmi://localhost:1099/RMRegistryServer";
            }

            try {
                reportUrl = root.getChild("general").getAttribute("reportUrl").getValue();
            } catch (Exception e) {
                // nix passiert, da mit Standardwert vorbelegt
            }

            try {
                albUrl = root.getChild("general").getChild("albUrl").getTextTrim();
            } catch (Exception e) {
                log.error("Fehler beim auslesen von albUrl", e);
            }

            try {
                albUrl = root.getChild("general").getChild("albUrl").getTextTrim();
            } catch (Exception e) {
                log.error("Fehler beim auslesen von albUrl", e);
            }

            try {
                final Element crossoverPrefs = root.getChild("CrossoverConfiguration");
                try {
                    final String crossoverServerPort = crossoverPrefs.getChildText("ServerPort");
                    if (log.isDebugEnabled()) {
                        log.debug("Crossover: Crossover port: " + crossoverServerPort);
                    }
                    setVerdisCrossoverPort(Integer.parseInt(crossoverServerPort));
                } catch (Exception ex) {
                    log.warn("Crossover: Error beim setzen des Server ports.", ex);
                }
                try {
                    setLagisCrossoverPort(Integer.parseInt(crossoverPrefs.getChildText("LagisCrossoverPort")));
                    if (log.isDebugEnabled()) {
                        log.debug("Crossover: LagisCrossoverPort: " + getLagisCrossoverPort());
                    }
                } catch (Exception ex) {
                    log.warn("Crossover: Error beim setzen des LagIS servers.", ex);
                }
                try {
                    flurstueckBuffer = Double.parseDouble(crossoverPrefs.getChildText("FlurstueckBuffer"));
                } catch (Exception ex) {
                    log.error("Crossover: Fehler beim setzen den buffers für die Flurstückabfrage.", ex);
                }
            } catch (Exception ex) {
                log.error("Crossover: Fehler beim Konfigurieren.", ex);
            }

            final List list = root.getChild("usergroups").getChildren("ug");
            final Iterator it = list.iterator();
            while (it.hasNext()) {
                final Object o = it.next();
                if (o instanceof Element) {
                    final Element e = (Element)o;
                    usergroups.add(e.getText().toLowerCase());
                    if (((Element)o).getAttribute("rw").getBooleanValue()) {
                        rwGroups.add(e.getText().toLowerCase());
                    }
                }
            }

            try {
                veranlagungOnlyForChangedValues = Boolean.parseBoolean(root.getChild("general").getAttribute(
                            "veranlassungOnlyForChangedValues").getValue());
            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.fatal("Fehler beim parsen von veranlassungOnlyForChangedValues --> benutze default false", e);
                }
            }

            // cismapPrefs = new CismapPreferences(root.getChild("cismapPreferences"));

            try {
                final WFSFormFactory wfsFormFactory = WFSFormFactory.getInstance();
                wfsFormFactory.masterConfigure(root);
                wfsForms = wfsFormFactory.getForms();
            } catch (Exception e) {
                log.warn("Fehler beim Auslesen der WFSFormsProperties");
            }
        } catch (Exception e) {
            log.error("Einstellungen konnten nicht gelesen werden", e);
        }

        try {
            final Element cidsappbackendPrefs = root.getChild("cidsAppBackend");
            appbackendDomain = cidsappbackendPrefs.getChildText("domain");
            appbackendConnectionclass = cidsappbackendPrefs.getChildText("connectionclass");
            appbackendCallserverurl = cidsappbackendPrefs.getChildText("callserverurl");
        } catch (Exception ex) {
            log.error("Crossover: Fehler beim setzen den buffers für die Flurstückabfrage", ex);
        }
    }
}
