/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.reports.verdis;

import Sirius.navigator.connection.ConnectionFactory;
import Sirius.navigator.connection.ConnectionInfo;
import Sirius.navigator.connection.ConnectionSession;
import Sirius.navigator.connection.RESTfulConnection;
import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.connection.proxy.ConnectionProxy;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.newuser.User;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import org.openide.util.Exceptions;

import java.awt.Dimension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.server.actions.GetServerResourceServerAction;

import de.cismet.cids.utils.jasperreports.ReportHelper;

import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.layerwidget.ActiveLayerModel;
import de.cismet.cismap.commons.interaction.CismapBroker;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;

import de.cismet.connectioncontext.AbstractConnectionContext;
import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.netutil.Proxy;

import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;

import de.cismet.verdis.commons.constants.VerdisConstants;
import de.cismet.verdis.commons.constants.VerdisMetaClassConstants;

import de.cismet.verdis.server.action.EBReportServerAction;
import de.cismet.verdis.server.utils.VerdisServerResources;

import static de.cismet.cismap.commons.gui.layerwidget.LayerDropUtils.LOG;

import static de.cismet.verdis.server.action.EBReportServerAction.MapFormat.A3LS;
import static de.cismet.verdis.server.action.EBReportServerAction.MapFormat.A3P;
import static de.cismet.verdis.server.action.EBReportServerAction.MapFormat.A4LS;
import static de.cismet.verdis.server.action.EBReportServerAction.MapFormat.A4P;
import static de.cismet.verdis.server.action.EBReportServerAction.Type.FLAECHEN;
import static de.cismet.verdis.server.action.EBReportServerAction.Type.FRONTEN;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class EBGenerator {

    //~ Static fields/initializers ---------------------------------------------

    private static final String MAP_REPORT = "<subreportDir>/<mode>_map<format><orientation>.jasper";
    private static final String A4_FORMAT = "A4";
    private static final String A3_FORMAT = "A3";
    private static final String LANDSCAPE_ORIENTATION = "LS";
    private static final String PORTRAIT_ORIENTATION = "P";

    private static final Option OPTION__CALLSERVER_URL = new Option("c", "callserver-url", true, "Callserver");
    private static final Option OPTION__GZIP_COMPRESSION = new Option(
            "z",
            "gzip-compression",
            false,
            "gzip compression");
    private static final Option OPTION__USER = new Option("u", "user", true, "User");
    private static final Option OPTION__GROUP = new Option("g", "group", true, "Group");
    private static final Option OPTION__DOMAIN = new Option("d", "group", true, "Group");
    private static final Option OPTION__PASSWORD = new Option("p", "password", true, "Password");
    private static final Option OPTION__KASSENZEICHEN_ID = new Option(
            "k",
            "kassenzeichen-id",
            true,
            "Kassenzeichen ID");
    private static final Option OPTION__TYPE = new Option("t", "type", true, "Type [FLAECHEN|FRONTEN]");
    private static final Option OPTION__MAP_FORMAT = new Option(
            "f",
            "map-format",
            true,
            "Map format [A4LS|A3LS|A4P|A3P]");
    private static final Option OPTION__HINTS = new Option("h", "hints", true, "Hints");
    private static final Option OPTION__SCALE_DENOMINATOR = new Option("s", "scale", true, "Scale denominator");
    private static final Option OPTION__ABFLUSSWIRKSAMKEIT = new Option(
            "w",
            "abfluss-wirksamkeit",
            false,
            "Abflusswirksamkeit");

    public static final String SRS = "EPSG:31466";
    public static final String WMS_CALL =
        "http://S102w484:8399/arcgis/services/WuNDa-ALKIS-Hintergrund/MapServer/WMSServer?&VERSION=1.1.1&REQUEST=GetMap&BBOX=<cismap:boundingBox>&WIDTH=<cismap:width>&HEIGHT=<cismap:height>&SRS=EPSG:31466&FORMAT=image/png&TRANSPARENT=FALSE&BGCOLOR=0xF0F0F0&EXCEPTIONS=application/vnd.ogc.se_xml&LAYERS=2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19&STYLES=default,default,default,default,default,default,default,default,default,default,default,default,default,default,default,default,default,default";

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   args  DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        Log4JQuickConfig.configure4LumbermillOnLocalhost();

        final CommandLine cmd;
        try {
            cmd =
                new DefaultParser().parse(new Options().addOption(OPTION__CALLSERVER_URL).addOption(
                        OPTION__GZIP_COMPRESSION).addOption(OPTION__KASSENZEICHEN_ID).addOption(OPTION__USER).addOption(
                        OPTION__GROUP).addOption(OPTION__DOMAIN).addOption(OPTION__PASSWORD).addOption(OPTION__TYPE)
                            .addOption(OPTION__MAP_FORMAT).addOption(OPTION__HINTS).addOption(
                        OPTION__SCALE_DENOMINATOR).addOption(OPTION__ABFLUSSWIRKSAMKEIT),
                    args);
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
            System.exit(1);
            throw new IllegalStateException();
        }

        final String callserverUrl = cmd.hasOption(OPTION__CALLSERVER_URL.getOpt())
            ? cmd.getOptionValue(OPTION__CALLSERVER_URL.getOpt()) : null;
        final boolean compressionEnabled = cmd.hasOption(OPTION__GZIP_COMPRESSION.getOpt());
        final String user = cmd.hasOption(OPTION__USER.getOpt()) ? cmd.getOptionValue(OPTION__USER.getOpt()) : null;
        final String group = cmd.hasOption(OPTION__GROUP.getOpt()) ? cmd.getOptionValue(OPTION__GROUP.getOpt()) : null;
        final String domain = cmd.hasOption(OPTION__DOMAIN.getOpt()) ? cmd.getOptionValue(OPTION__DOMAIN.getOpt())
                                                                     : null;
        final String password = cmd.hasOption(OPTION__PASSWORD.getOpt()) ? cmd.getOptionValue(OPTION__PASSWORD.getOpt())
                                                                         : null;
        final EBReportServerAction.Type type = cmd.hasOption(OPTION__TYPE.getOpt())
            ? EBReportServerAction.Type.valueOf(cmd.getOptionValue(OPTION__TYPE.getOpt())) : null;
        final Integer kassenzeichenId = cmd.hasOption(OPTION__KASSENZEICHEN_ID.getOpt())
            ? Integer.valueOf(cmd.getOptionValue(OPTION__KASSENZEICHEN_ID.getOpt())) : null;
        final EBReportServerAction.MapFormat mapFormat = cmd.hasOption(OPTION__MAP_FORMAT.getOpt())
            ? EBReportServerAction.MapFormat.valueOf(cmd.getOptionValue(OPTION__MAP_FORMAT.getOpt())) : null;
        final String hints = cmd.hasOption(OPTION__HINTS.getOpt()) ? cmd.getOptionValue(OPTION__HINTS.getOpt()) : null;
        final Double scaleDenominator = cmd.hasOption(OPTION__SCALE_DENOMINATOR.getOpt())
            ? Double.valueOf(cmd.getOptionValue(OPTION__SCALE_DENOMINATOR.getOpt())) : null;
        final boolean abflussWirksamkeit = cmd.hasOption(OPTION__ABFLUSSWIRKSAMKEIT.getOpt());

        try {
            initSessionManager(
                callserverUrl,
                domain,
                group,
                user,
                password,
                compressionEnabled,
                ConnectionContext.create(
                    AbstractConnectionContext.Category.OTHER,
                    EBGenerator.class.getCanonicalName()));
            initMap();
            final byte[] bytes = gen(
                    kassenzeichenId,
                    type,
                    mapFormat,
                    scaleDenominator,
                    hints,
                    abflussWirksamkeit,
                    ConnectionContext.createDummy());
            System.out.println(Base64.getEncoder().encodeToString(bytes));
            System.exit(0);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            System.exit(1);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   callserverUrl       DOCUMENT ME!
     * @param   domain              DOCUMENT ME!
     * @param   group               DOCUMENT ME!
     * @param   user                DOCUMENT ME!
     * @param   pass                DOCUMENT ME!
     * @param   compressionEnabled  DOCUMENT ME!
     * @param   connectionContext   DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void initSessionManager(final String callserverUrl,
            final String domain,
            final String group,
            final String user,
            final String pass,
            final boolean compressionEnabled,
            final ConnectionContext connectionContext) throws Exception {
        final ConnectionInfo info = new ConnectionInfo();
        info.setCallserverURL(callserverUrl);
        info.setUsername(user);
        info.setUsergroup(group);
        info.setPassword(pass);
        info.setUserDomain(domain);
        info.setUsergroupDomain(domain);

        final Sirius.navigator.connection.Connection connection = ConnectionFactory.getFactory()
                    .createConnection(
                        RESTfulConnection.class.getCanonicalName(),
                        info.getCallserverURL(),
                        Proxy.fromPreferences(),
                        compressionEnabled,
                        connectionContext);
        final ConnectionSession session = ConnectionFactory.getFactory()
                    .createSession(connection, info, true, connectionContext);
        final ConnectionProxy conProxy = ConnectionFactory.getFactory()
                    .createProxy(
                        "Sirius.navigator.connection.proxy.DefaultConnectionProxyHandler",
                        session,
                        connectionContext);
        SessionManager.init(conProxy);

        ClassCacheMultiple.setInstance(domain, connectionContext);
    }

    /**
     * DOCUMENT ME!
     */
    private static void initMap() {
        final MappingComponent mappingComponent = new MappingComponent();
        final Dimension d = new Dimension(300, 300);
        mappingComponent.setPreferredSize(d);
        mappingComponent.setSize(d);

        final ActiveLayerModel mappingModel = new ActiveLayerModel();
        mappingModel.addHome(new XBoundingBox(6.7d, 49.1, 7.1d, 49.33d, SRS, false));
        mappingModel.setSrs(SRS);

        final SimpleWMS swms = new SimpleWMS(new SimpleWmsGetMapUrl(WMS_CALL));
        mappingModel.addLayer(swms);
        mappingComponent.setInteractionMode(MappingComponent.SELECT);
        mappingComponent.setMappingModel(mappingModel);
        mappingComponent.gotoInitialBoundingBox();
        mappingComponent.unlock();

        CismapBroker.getInstance().setMappingComponent(mappingComponent);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   kassenzeichenId     DOCUMENT ME!
     * @param   type                DOCUMENT ME!
     * @param   mapFormat           DOCUMENT ME!
     * @param   scaleDenominator    DOCUMENT ME!
     * @param   hints               DOCUMENT ME!
     * @param   abflusswirksamkeit  DOCUMENT ME!
     * @param   connectionContext   DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static byte[] gen(final int kassenzeichenId,
            final EBReportServerAction.Type type,
            final EBReportServerAction.MapFormat mapFormat,
            final Double scaleDenominator,
            final String hints,
            final boolean abflusswirksamkeit,
            final ConnectionContext connectionContext) throws Exception {
        final User user = SessionManager.getSession().getUser();
        final MetaClass kassenzeichenMc = CidsBean.getMetaClassFromTableName(
                VerdisConstants.DOMAIN,
                VerdisMetaClassConstants.MC_KASSENZEICHEN,
                connectionContext);
        final CidsBean kassenzeichen = SessionManager.getConnection()
                    .getMetaObject(
                            user,
                            kassenzeichenId,
                            kassenzeichenMc.getId(),
                            VerdisConstants.DOMAIN,
                            connectionContext)
                    .getBean();

        final FileOutputStream out = null;
        final boolean forceQuit = false;
        try {
            final Properties properties = getProperties(user, connectionContext);

            if (LOG.isDebugEnabled()) {
                LOG.debug("generating report beans");
            }
            String repMap;
            String mapHeightPropkey = "FEPGeneratorDialog.mapHeight";
            String mapWidthPropkey = "FEPGeneratorDialog.mapWidth";
            if (EBReportServerAction.MapFormat.A4LS.equals(mapFormat)
                        || EBReportServerAction.MapFormat.A4P.equals(mapFormat)) {
                repMap = MAP_REPORT.replace("<format>", A4_FORMAT);
                mapHeightPropkey += A4_FORMAT;
                mapWidthPropkey += A4_FORMAT;
            } else {
                repMap = MAP_REPORT.replace("<format>", A3_FORMAT);
                mapHeightPropkey += A3_FORMAT;
                mapWidthPropkey += A3_FORMAT;
            }

            if (EBReportServerAction.MapFormat.A4LS.equals(mapFormat)
                        || EBReportServerAction.MapFormat.A3LS.equals(mapFormat)) {
                repMap = repMap.replace("<orientation>", LANDSCAPE_ORIENTATION);
                mapHeightPropkey += LANDSCAPE_ORIENTATION;
                mapWidthPropkey += LANDSCAPE_ORIENTATION;
            } else {
                repMap = repMap.replace("<orientation>", PORTRAIT_ORIENTATION);
                mapHeightPropkey += PORTRAIT_ORIENTATION;
                mapWidthPropkey += PORTRAIT_ORIENTATION;
            }

            if (EBReportServerAction.Type.FRONTEN.equals(type)) {
                repMap = repMap.replace("<mode>", "fronten");
            } else {
                repMap = repMap.replace("<mode>", "feb");
            }
            repMap = repMap.replace("<subreportDir>", properties.getProperty("reportsDirectory"));

            if (LOG.isDebugEnabled()) {
                LOG.debug("Report File for Map: " + repMap);
            }

            final int mapWidth = Integer.parseInt(properties.getProperty(mapWidthPropkey));
            final int mapHeight = Integer.parseInt(properties.getProperty(mapHeightPropkey));

            final EBReportBean reportBean;
            if (EBReportServerAction.Type.FRONTEN.equals(type)) {
                reportBean = new FrontenReportBean(
                        properties,
                        kassenzeichen,
                        mapHeight,
                        mapWidth,
                        scaleDenominator);
            } else {
                reportBean = new FlaechenReportBean(
                        properties,
                        kassenzeichen,
                        hints,
                        mapHeight,
                        mapWidth,
                        scaleDenominator,
                        abflusswirksamkeit);
            }
            final Collection<EBReportBean> reportBeans = new LinkedList<>();
            reportBeans.add(reportBean);
            boolean ready;

            do {
                ready = true;
                for (final EBReportBean rb : reportBeans) {
                    if (!rb.isReadyToProceed() || forceQuit) {
                        ready = false;
                        break;
                    }
                }
            } while (!ready);
            if (LOG.isDebugEnabled()) {
                LOG.debug("ready to procced");
            }
            final HashMap parameters = new HashMap();
            parameters.put("SUBREPORT_DIR", properties.getProperty("reportsDirectory"));
            parameters.put("fillKanal", reportBean.isFillAbflusswirksamkeit());

            final List<InputStream> ins = new ArrayList<>();

            if (mapFormat != null) {
                final VerdisServerResources reportResource;
                switch (mapFormat) {
                    case A4LS: {
                        reportResource = VerdisServerResources.EB_MAP_A4LS_JASPER;
                    }
                    break;
                    case A4P: {
                        reportResource = VerdisServerResources.EB_MAP_A4P_JASPER;
                    }
                    break;
                    case A3LS: {
                        reportResource = VerdisServerResources.EB_MAP_A3LS_JASPER;
                    }
                    break;
                    case A3P: {
                        reportResource = VerdisServerResources.EB_MAP_A3P_JASPER;
                    }
                    break;
                    default: {
                        reportResource = null;
                    }
                }
                if (reportResource != null) {
                    final JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportBeans);
                    final byte[] bytes = generateReport(
                            reportResource,
                            parameters,
                            dataSource,
                            connectionContext);
                    ins.add(new ByteArrayInputStream(bytes));
                }
            }

            if (type != null) {
                VerdisServerResources reportResource;
                switch (type) {
                    case FLAECHEN: {
                        reportResource = VerdisServerResources.EB_FLAECHEN_JASPER;
                    }
                    break;
                    case FRONTEN: {
                        reportResource = VerdisServerResources.EB_FRONTEN_JASPER;
                    }
                    break;
                    default: {
                        reportResource = null;
                    }
                }

                if (reportResource != null) {
                    final JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportBeans);
                    final byte[] bytes = generateReport(reportResource,
                            parameters,
                            dataSource,
                            connectionContext);
                    ins.add(new ByteArrayInputStream(bytes));
                }
            }

            final ByteArrayOutputStream byteArrayReportsStream = new ByteArrayOutputStream();
            ReportHelper.concatPDFs(ins, byteArrayReportsStream, false);
            return byteArrayReportsStream.toByteArray();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   user               DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private static Properties getProperties(final User user, final ConnectionContext connectionContext)
            throws Exception {
        final Properties properties = new Properties();
        properties.load(new StringReader(
                (String)SessionManager.getProxy().executeTask(
                    user,
                    GetServerResourceServerAction.TASK_NAME,
                    VerdisConstants.DOMAIN,
                    VerdisServerResources.EB_REPORT_PROPERTIES.getValue(),
                    connectionContext)));
        return properties;
    }
    /**
     * DOCUMENT ME!
     *
     * @param   reportResource     jasperReport DOCUMENT ME!
     * @param   parameters         DOCUMENT ME!
     * @param   dataSource         DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private static byte[] generateReport(final VerdisServerResources reportResource,
            final Map<String, Object> parameters,
            final JRDataSource dataSource,
            final ConnectionContext connectionContext) throws Exception {
        final User user = SessionManager.getSession().getUser();
        final JasperReport jasperReport = (JasperReport)SessionManager.getProxy()
                    .executeTask(
                            user,
                            GetServerResourceServerAction.TASK_NAME,
                            VerdisConstants.DOMAIN,
                            reportResource.getValue(),
                            connectionContext);
        final JasperPrint print = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(print, os);
            final byte[] bytes = os.toByteArray();
            return bytes;
        } finally {
            if (os != null) {
                os.close();
            }
        }
    }
}
