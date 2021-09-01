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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import java.net.URI;
import java.net.URL;

import java.nio.file.Paths;

import java.util.*;

import de.cismet.cismap.commons.preferences.CismapPreferences;
import de.cismet.cismap.commons.wfsforms.AbstractWFSForm;
import de.cismet.cismap.commons.wfsforms.WFSFormFactory;

import de.cismet.netutil.ProxyProperties;

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
    private CismapPreferences cismapPrefs;
    private String standaloneDomainname;
    private String standaloneCallServerHost;
    private String reportUrl = "http://s10220:8090/verdis/vorn.pdf?KASSENZEICHEN=";
    private String albUrl = "http://www.cismet.de";
    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private LinkedHashMap<String, AbstractWFSForm> wfsForms = new LinkedHashMap<String, AbstractWFSForm>();
    // ADDED FOR RM PLUGIN FUNCTIONALTY 22.07.07 Sebastian Puhl
    private int verdisCrossoverPort;
    private int lagisCrossoverPort;
    private double flurstueckBuffer = -0.5;
    private boolean veranlagungOnlyForChangedValues = false;
    private boolean aenderungsanfrageEnabled = false;

    private String appbackendDomain = null;
    private String appbackendConnectionclass = null;
    private String appbackendCallserverurl = null;
    private boolean compressionEnabled = false;
    private Integer nachgewiesenFalseThreshold = 10;
    private final ProxyProperties proxyProperties = new ProxyProperties();

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
     * @param   from  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private static InputStream getInputStreamFrom(final String from) throws Exception {
        if ((from.indexOf("http://") == 0) || (from.indexOf("https://") == 0)
                    || (from.indexOf("file:/") == 0)) {
            return new URL(from).openStream();
        } else {
            return new BufferedInputStream(new FileInputStream(from));
        }
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
                nachgewiesenFalseThreshold = Integer.valueOf(root.getChild("general").getChild(
                            "nachgewiesenFalseThreshold").getTextTrim());
            } catch (Exception e) {
                log.error("Fehler beim auslesen von nachgewiesenFalseThreshold", e);
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

            try {
                veranlagungOnlyForChangedValues = Boolean.parseBoolean(root.getChild("general").getAttribute(
                            "veranlassungOnlyForChangedValues").getValue());
            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.fatal("Fehler beim parsen von veranlassungOnlyForChangedValues --> benutze default false", e);
                }
            }

            try {
                aenderungsanfrageEnabled = Boolean.parseBoolean(root.getChild("general").getAttribute(
                            "aenderungsanfrageEnabled").getValue());
            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.fatal("Fehler beim parsen von aenderungsanfrageEnabled --> benutze default false", e);
                }
            }

            try {
                final WFSFormFactory wfsFormFactory = WFSFormFactory.getInstance();
                wfsFormFactory.masterConfigure(root);
                wfsForms = wfsFormFactory.getForms();
            } catch (Exception e) {
                log.warn("Fehler beim Auslesen der WFSFormsProperties", e);
            }
        } catch (Exception e) {
            log.error("Einstellungen konnten nicht gelesen werden", e);
        }

        final String cfgFile = JnlpSystemPropertyHelper.getProperty("configFile");
        if (cfgFile != null) {
            try {
                final AppProperties appProperties = new AppProperties(getInputStreamFrom(cfgFile));
                appbackendCallserverurl = appProperties.getCallserverUrl();
                compressionEnabled = appProperties.isCompressionEnabled();
                appbackendDomain = appProperties.getDomain();
                appbackendConnectionclass = appProperties.getConnectionClass();

                final String cfgFileName = Paths.get(new URI(cfgFile).getPath()).getFileName().toString();
                final String cfgDirname = cfgFile.substring(0, cfgFile.lastIndexOf(cfgFileName));
                final String proxyConfig = appProperties.getProxyConfig();
                final String cfgProxy = (proxyConfig != null && !proxyConfig.isEmpty()) ? (cfgDirname + proxyConfig) : null;

                proxyProperties.load(getInputStreamFrom(cfgProxy));
            } catch (final Exception ex) {
                log.fatal("Error while reading config file", ex);
                System.exit(2);
            }
        } else {
            try {
                final Element cidsappbackendPrefs = root.getChild("cidsAppBackend");
                appbackendDomain = cidsappbackendPrefs.getChildText("domain");
                appbackendConnectionclass = cidsappbackendPrefs.getChildText("connectionclass");
                appbackendCallserverurl = cidsappbackendPrefs.getChildText("callserverurl");
                try {
                    compressionEnabled = Boolean.parseBoolean((String)cidsappbackendPrefs.getChildText(
                                "compressionEnabled"));
                } catch (final Exception e) {
                    if (log.isDebugEnabled()) {
                        log.fatal("Fehler beim parsen von compressionEnabled --> benutze default false", e);
                    }
                }
            } catch (Exception ex) {
                log.error("Fehler beim parsen von cidsAppBackend", ex);
            }
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static class JnlpSystemPropertyHelper {

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param   propertyName  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public static String getProperty(final String propertyName) {
            return getProperty(propertyName, null);
        }

        /**
         * DOCUMENT ME!
         *
         * @param   propertyName  DOCUMENT ME!
         * @param   defaultValue  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public static String getProperty(String propertyName, final String defaultValue) {
            if (propertyName == null) {
                return null;
            }

            final String normalPropertyValue = System.getProperty(propertyName);

            if (normalPropertyValue == null) {
                propertyName = "jnlp." + propertyName;
            }

            return System.getProperty(propertyName, defaultValue);
        }

        /**
         * DOCUMENT ME!
         *
         * @param  args  DOCUMENT ME!
         */
        public static void main(final String[] args) {
            System.setProperty("testProp", "ohne jnlp");
            System.setProperty("jnlp.testProp", "mit jnlp");
            System.out.println(getProperty("testProp"));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static class AppProperties extends PropertyResourceBundle {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new AppProperties object.
         *
         * @param   is  url DOCUMENT ME!
         *
         * @throws  Exception  DOCUMENT ME!
         */
        public AppProperties(final InputStream is) throws Exception {
            super(is);
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public String getCallserverUrl() {
            return getString("callserverUrl");
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public String getProxyConfig() {
            return getString("proxy.config");
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public boolean isCompressionEnabled() {
            return Boolean.parseBoolean(getString("compressionEnabled"));
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public String getConnectionClass() {
            return getString("connectionClass");
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public String getDomain() {
            return getString("domain");
        }
    }
}
