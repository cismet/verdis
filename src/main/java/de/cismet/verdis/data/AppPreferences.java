/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis.data;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.io.InputStream;

import java.net.URL;

import java.util.*;

import de.cismet.cismap.commons.preferences.CismapPreferences;
import de.cismet.cismap.commons.wfsforms.AbstractWFSForm;
import de.cismet.cismap.commons.wfsforms.WFSFormFactory;

import de.cismet.tools.ConnectionInfo;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten.hell@cismet.de
 * @version  $Revision$, $Date$
 */
public class AppPreferences {

    //~ Instance fields --------------------------------------------------------

    private String version;
    private String environment;
    private String mode;

    private String domainserver;
    private int kassenzeichenClassId;
    private int geomClassId;
    private int dmsUrlBaseClassId;
    private int dmsUrlClassId;
    private String kassenzeichenSuche;
    private Vector usergroups = new Vector();
    private Vector rwGroups = new Vector();
    private ConnectionInfo dbConnectionInfo;
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

    private String appbackenddomain = null;
    private String appbackendconnectionclass = null;
    private String appbackendcallserverurl = null;

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
            dmsUrlBaseClassId = root.getChild("general").getAttribute("dmsUrlBaseClassId").getIntValue();
            dmsUrlClassId = root.getChild("general").getAttribute("dmsUrlClassId").getIntValue();
            kassenzeichenSuche = root.getChild("general").getAttribute("kassenzeichenSuche").getValue();
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
                final String crossoverServerPort = crossoverPrefs.getChildText("ServerPort");
                if (log.isDebugEnabled()) {
                    log.debug("Crossover: Crossover port: " + crossoverServerPort);
                }
                setVerdisCrossoverPort(Integer.parseInt(crossoverServerPort));
            } catch (Exception ex) {
                log.warn("Crossover: Error beim setzen des Server ports", ex);
            }

            try {
                final Element crossoverPrefs = root.getChild("CrossoverConfiguration");
                final String lagisHost = crossoverPrefs.getChild("LagisConfiguration").getChildText("Host");
                if (log.isDebugEnabled()) {
                    log.debug("Crossover: lagisHost: " + lagisHost);
                }
                final String lagisORBPort = crossoverPrefs.getChild("LagisConfiguration").getChildText("ORBPort");
                if (log.isDebugEnabled()) {
                    log.debug("Crossover: lagisHost: " + lagisORBPort);
                }
                setLagisCrossoverPort(Integer.parseInt(
                        crossoverPrefs.getChild("LagisConfiguration").getChildText("LagisCrossoverPort")));
                if (log.isDebugEnabled()) {
                    log.debug("Crossover: LagisCrossoverPort: " + getLagisCrossoverPort());
                }
            } catch (Exception ex) {
                log.warn("Crossover: Error beim setzen des LagIS servers", ex);
            }
            try {
                final Element crossoverPrefs = root.getChild("CrossoverConfiguration");
                flurstueckBuffer = Double.parseDouble(crossoverPrefs.getChildText("FlurstueckBuffer"));
            } catch (Exception ex) {
                log.error("Crossover: Fehler beim setzen den buffers für die Flurstückabfrage", ex);
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
            dbConnectionInfo = new ConnectionInfo(root.getChild("dbConnectionInfo"));

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
            appbackenddomain = cidsappbackendPrefs.getChildText("domain");
            appbackendconnectionclass = cidsappbackendPrefs.getChildText("connectionclass");
            appbackendcallserverurl = cidsappbackendPrefs.getChildText("callserverurl");
        } catch (Exception ex) {
            log.error("Crossover: Fehler beim setzen den buffers für die Flurstückabfrage", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getVerdisCrossoverPort() {
        return verdisCrossoverPort;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  verdisCrossoverPort  DOCUMENT ME!
     */
    public void setVerdisCrossoverPort(final int verdisCrossoverPort) {
        this.verdisCrossoverPort = verdisCrossoverPort;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getLagisCrossoverPort() {
        return lagisCrossoverPort;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  lagisCrossoverPort  DOCUMENT ME!
     */
    public void setLagisCrossoverPort(final int lagisCrossoverPort) {
        this.lagisCrossoverPort = lagisCrossoverPort;
    }

    /**
     * ADDED FOR RM PLUGIN FUNCTIONALTY 22.07.07 Sebastian Puhl.
     *
     * @return  DOCUMENT ME!
     */
    public int getPrimaryPort() {
        return primaryPort;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getSecondaryPort() {
        return secondaryPort;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getRmRegistryServerPath() {
        return rmRegistryServerPath;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getVersion() {
        return version;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  version  DOCUMENT ME!
     */
    public void setVersion(final String version) {
        this.version = version;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getEnvironment() {
        return environment;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  environment  DOCUMENT ME!
     */
    public void setEnvironment(final String environment) {
        this.environment = environment;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getMode() {
        return mode;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  mode  DOCUMENT ME!
     */
    public void setMode(final String mode) {
        this.mode = mode;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getDomainserver() {
        return domainserver;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  domainserver  DOCUMENT ME!
     */
    public void setDomainserver(final String domainserver) {
        this.domainserver = domainserver;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getKassenzeichenClassId() {
        return kassenzeichenClassId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kassenzeichenClassId  DOCUMENT ME!
     */
    public void setKassenzeichenClassId(final int kassenzeichenClassId) {
        this.kassenzeichenClassId = kassenzeichenClassId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getGeomClassId() {
        return geomClassId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  geomClassId  DOCUMENT ME!
     */
    public void setGeomClassId(final int geomClassId) {
        this.geomClassId = geomClassId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getDmsUrlBaseClassId() {
        return dmsUrlBaseClassId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  dmsUrlBaseClassId  DOCUMENT ME!
     */
    public void setDmsUrlBaseClassId(final int dmsUrlBaseClassId) {
        this.dmsUrlBaseClassId = dmsUrlBaseClassId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getDmsUrlClassId() {
        return dmsUrlClassId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  dmsUrlClassId  DOCUMENT ME!
     */
    public void setDmsUrlClassId(final int dmsUrlClassId) {
        this.dmsUrlClassId = dmsUrlClassId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getKassenzeichenSuche() {
        return kassenzeichenSuche;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kassenzeichenSuche  DOCUMENT ME!
     */
    public void setKassenzeichenSuche(final String kassenzeichenSuche) {
        this.kassenzeichenSuche = kassenzeichenSuche;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ConnectionInfo getDbConnectionInfo() {
        return dbConnectionInfo;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  dbConnectionInfo  DOCUMENT ME!
     */
    public void setDbConnectionInfo(final ConnectionInfo dbConnectionInfo) {
        this.dbConnectionInfo = dbConnectionInfo;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CismapPreferences getCismapPrefs() {
        return cismapPrefs;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cismapPrefs  DOCUMENT ME!
     */
    public void setCismapPrefs(final CismapPreferences cismapPrefs) {
        this.cismapPrefs = cismapPrefs;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Vector getRwGroups() {
        return rwGroups;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getReportUrl() {
        return reportUrl;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  reportUrl  DOCUMENT ME!
     */
    public void setReportUrl(final String reportUrl) {
        this.reportUrl = reportUrl;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public HashMap<String, AbstractWFSForm> getWfsForms() {
        return wfsForms;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Vector getUsergroups() {
        return usergroups;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  usergroups  DOCUMENT ME!
     */
    public void setUsergroups(final Vector usergroups) {
        this.usergroups = usergroups;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getStandaloneDomainname() {
        return standaloneDomainname;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  standaloneDomainname  DOCUMENT ME!
     */
    public void setStandaloneDomainname(final String standaloneDomainname) {
        this.standaloneDomainname = standaloneDomainname;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getStandaloneCallServerHost() {
        return standaloneCallServerHost;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getCallServerUrl() {
        return standaloneCallServerHost;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  standaloneCallServerHost  DOCUMENT ME!
     */
    public void setStandaloneCallServerHost(final String standaloneCallServerHost) {
        this.standaloneCallServerHost = standaloneCallServerHost;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public double getFlurstueckBuffer() {
        return flurstueckBuffer;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  flurstueckBuffer  DOCUMENT ME!
     */
    public void setFlurstueckBuffer(final double flurstueckBuffer) {
        this.flurstueckBuffer = flurstueckBuffer;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getAppbackendCallserverurl() {
        return appbackendcallserverurl;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getAppbackendConnectionclass() {
        return appbackendconnectionclass;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getAppbackendDomain() {
        return appbackenddomain;
    }
}
