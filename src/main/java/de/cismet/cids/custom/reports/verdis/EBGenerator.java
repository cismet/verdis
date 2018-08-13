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
import Sirius.navigator.exception.ConnectionException;

import Sirius.server.middleware.types.MetaObjectNode;
import Sirius.server.newuser.User;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

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

import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

import org.openide.util.Exceptions;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import java.text.NumberFormat;

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

import de.cismet.cismap.commons.Crs;
import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.HeadlessMapProvider;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.features.Feature;
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

import de.cismet.verdis.server.action.EBReportServerAction;
import de.cismet.verdis.server.search.KassenzeichenSearchStatement;
import de.cismet.verdis.server.utils.VerdisServerResources;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class EBGenerator {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EBGenerator.class);
    public static final String NULL_VALUE = "_null_";

    private static final String A4_FORMAT = "A4";
    private static final String A3_FORMAT = "A3";
    private static final String LANDSCAPE_ORIENTATION = "LS";
    private static final String PORTRAIT_ORIENTATION = "P";

    private static final Option OPTION__LOGGER = new Option("l", "logger", false, "Logger");
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
    private static final Option OPTION__KASSENZEICHEN = new Option(
            "k",
            "kassenzeichen",
            true,
            "Kassenzeichen");
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

    private static final double PPI = 72.156d;
    private static final double METERS_TO_INCH_FACTOR = 0.0254d;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   args  DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        final CommandLine cmd;
        try {
            cmd =
                new DefaultParser().parse(new Options().addOption(OPTION__CALLSERVER_URL).addOption(OPTION__LOGGER)
                            .addOption(
                                OPTION__GZIP_COMPRESSION).addOption(OPTION__KASSENZEICHEN).addOption(OPTION__USER)
                            .addOption(
                                OPTION__GROUP).addOption(OPTION__DOMAIN).addOption(OPTION__PASSWORD).addOption(
                        OPTION__TYPE).addOption(OPTION__MAP_FORMAT).addOption(OPTION__HINTS).addOption(
                        OPTION__SCALE_DENOMINATOR).addOption(OPTION__ABFLUSSWIRKSAMKEIT),
                    args);
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
            System.exit(1);
            throw new IllegalStateException();
        }

        final boolean loggerEnabled = cmd.hasOption(OPTION__LOGGER.getOpt());
        if (loggerEnabled) {
            Log4JQuickConfig.configure4LumbermillOnLocalhost();
        } else {
            Log4JQuickConfig.configure4LumbermillOnLocalhost("OFF");
        }

        final String callserverUrl = cmd.hasOption(OPTION__CALLSERVER_URL.getOpt())
            ? cmd.getOptionValue(OPTION__CALLSERVER_URL.getOpt()) : null;
        final boolean compressionEnabled = cmd.hasOption(OPTION__GZIP_COMPRESSION.getOpt());
        final String login = cmd.hasOption(OPTION__USER.getOpt()) ? cmd.getOptionValue(OPTION__USER.getOpt()) : null;
        final String group = cmd.hasOption(OPTION__GROUP.getOpt()) ? cmd.getOptionValue(OPTION__GROUP.getOpt()) : null;
        final String domain = cmd.hasOption(OPTION__DOMAIN.getOpt()) ? cmd.getOptionValue(OPTION__DOMAIN.getOpt())
                                                                     : null;
        final String password = cmd.hasOption(OPTION__PASSWORD.getOpt()) ? cmd.getOptionValue(OPTION__PASSWORD.getOpt())
                                                                         : null;
        final EBReportServerAction.Type type =
            (cmd.hasOption(OPTION__TYPE.getOpt()) && !NULL_VALUE.equals(cmd.getOptionValue(OPTION__TYPE.getOpt())))
            ? EBReportServerAction.Type.valueOf(cmd.getOptionValue(OPTION__TYPE.getOpt())) : null;
        final Integer kassenzeichen = cmd.hasOption(OPTION__KASSENZEICHEN.getOpt())
            ? Integer.valueOf(cmd.getOptionValue(OPTION__KASSENZEICHEN.getOpt())) : null;
        final EBReportServerAction.MapFormat mapFormat =
            (cmd.hasOption(OPTION__MAP_FORMAT.getOpt())
                        && !NULL_VALUE.equals(cmd.getOptionValue(OPTION__MAP_FORMAT.getOpt())))
            ? EBReportServerAction.MapFormat.valueOf(cmd.getOptionValue(OPTION__MAP_FORMAT.getOpt())) : null;
        final String hints = cmd.hasOption(OPTION__HINTS.getOpt()) ? cmd.getOptionValue(OPTION__HINTS.getOpt()) : null;
        final Double scaleDenominator =
            (cmd.hasOption(OPTION__SCALE_DENOMINATOR.getOpt())
                        && !NULL_VALUE.equals(cmd.getOptionValue(OPTION__SCALE_DENOMINATOR.getOpt())))
            ? Double.valueOf(cmd.getOptionValue(OPTION__SCALE_DENOMINATOR.getOpt())) : null;
        final boolean abflussWirksamkeit = cmd.hasOption(OPTION__ABFLUSSWIRKSAMKEIT.getOpt());

        try {
            final ConnectionContext connectionContext = ConnectionContext.create(
                    AbstractConnectionContext.Category.OTHER,
                    EBGenerator.class.getCanonicalName());

            initSessionManager(
                callserverUrl,
                domain,
                group,
                login,
                password,
                compressionEnabled,
                connectionContext);
            final Properties properties = getProperties(connectionContext);

            initMap(properties);
            final byte[] bytes = gen(
                    properties,
                    kassenzeichen,
                    type,
                    mapFormat,
                    scaleDenominator,
                    hints,
                    abflussWirksamkeit,
                    connectionContext);
            System.out.println(Base64.getEncoder().encodeToString(bytes));
            System.exit(0);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            LOG.error(ex, ex);
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
     *
     * @param  properties  DOCUMENT ME!
     */
    private static void initMap(final Properties properties) {
        final MappingComponent mappingComponent = new MappingComponent();
        final Dimension d = new Dimension(300, 300);
        mappingComponent.setPreferredSize(d);
        mappingComponent.setSize(d);

        final String srs = properties.getProperty("mapSrs");
        final ActiveLayerModel mappingModel = new ActiveLayerModel();
        mappingModel.addHome(new XBoundingBox(6.7d, 49.1, 7.1d, 49.33d, srs, false));
        mappingModel.setSrs(srs);

        final SimpleWMS swms = new SimpleWMS(new SimpleWmsGetMapUrl(properties.getProperty("mapUrl")));
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
     * @param   reportResource     DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConnectionException  DOCUMENT ME!
     */
    private static JasperReport getReport(final VerdisServerResources reportResource,
            final ConnectionContext connectionContext) throws ConnectionException {
        final User user = SessionManager.getSession().getUser();
        final JasperReport jasperReport = (JasperReport)SessionManager.getProxy()
                    .executeTask(
                            user,
                            GetServerResourceServerAction.TASK_NAME,
                            VerdisConstants.DOMAIN,
                            reportResource.getValue(),
                            connectionContext);
        return jasperReport;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   properties           DOCUMENT ME!
     * @param   kassenzeichenNummer  DOCUMENT ME!
     * @param   type                 DOCUMENT ME!
     * @param   mapFormat            DOCUMENT ME!
     * @param   scaleDenominator     DOCUMENT ME!
     * @param   hints                DOCUMENT ME!
     * @param   abflusswirksamkeit   DOCUMENT ME!
     * @param   connectionContext    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static byte[] gen(final Properties properties,
            final Integer kassenzeichenNummer,
            final EBReportServerAction.Type type,
            final EBReportServerAction.MapFormat mapFormat,
            final Double scaleDenominator,
            final String hints,
            final boolean abflusswirksamkeit,
            final ConnectionContext connectionContext) throws Exception {
        final KassenzeichenSearchStatement search = new KassenzeichenSearchStatement(Integer.toString(
                    kassenzeichenNummer));
        final Collection<MetaObjectNode> mons = SessionManager.getProxy().customServerSearch(search, connectionContext);
        if ((mons == null) || (mons.size() != 1)) {
            throw new Exception(String.format("kassenzeichen %d not found", kassenzeichenNummer));
        }

        final MetaObjectNode mon = mons.iterator().next();
        final CidsBean kassenzeichenBean = SessionManager.getProxy()
                    .getMetaObject(
                            SessionManager.getSession().getUser(),
                            mon.getObjectId(),
                            mon.getClassId(),
                            mon.getDomain(),
                            connectionContext)
                    .getBean();

        final FileOutputStream out = null;
        final boolean forceQuit = false;
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("generating report beans");
            }

            final HashMap parameters = new HashMap();

            final EBReportBean reportBean;
            final Collection<Feature> features;
            if (EBReportServerAction.Type.FRONTEN.equals(type)) {
                reportBean = new FrontenReportBean(
                        properties,
                        kassenzeichenBean);

                parameters.put(
                    "TABLE_SUBREPORT",
                    getReport(VerdisServerResources.EB_FRONTEN_TABLE_JASPER, connectionContext));
                features = FrontenReportBean.createFeatures(kassenzeichenBean, properties);
            } else {
                reportBean = new FlaechenReportBean(
                        properties,
                        kassenzeichenBean,
                        hints,
                        abflusswirksamkeit);

                parameters.put(
                    "DACH_SUBREPORT",
                    getReport(VerdisServerResources.EB_FLAECHEN_DACH_JASPER, connectionContext));
                parameters.put(
                    "VERSIEGELT_SUBREPORT",
                    getReport(VerdisServerResources.EB_FLAECHEN_VERSIEGELT_JASPER, connectionContext));
                parameters.put(
                    "HINWEISE_SUBREPORT",
                    getReport(VerdisServerResources.EB_FLAECHEN_HINWEISE_JASPER, connectionContext));
                features = FlaechenReportBean.createFeatures(kassenzeichenBean, properties);
            }

            final XBoundingBox boundingBox = genBoundingBox(features, properties);

            final boolean hasOrientation = (mapFormat != null)
                        && (EBReportServerAction.MapFormat.LS.equals(mapFormat)
                            || EBReportServerAction.MapFormat.P.equals(mapFormat)
                            || EBReportServerAction.MapFormat.A4LS.equals(mapFormat)
                            || EBReportServerAction.MapFormat.A4P.equals(mapFormat)
                            || EBReportServerAction.MapFormat.A3LS.equals(mapFormat)
                            || EBReportServerAction.MapFormat.A3P.equals(mapFormat));

            final boolean hasFormat = (mapFormat != null)
                        && (EBReportServerAction.MapFormat.A4.equals(mapFormat)
                            || EBReportServerAction.MapFormat.A3.equals(mapFormat)
                            || EBReportServerAction.MapFormat.A4LS.equals(mapFormat)
                            || EBReportServerAction.MapFormat.A4P.equals(mapFormat)
                            || EBReportServerAction.MapFormat.A3LS.equals(mapFormat)
                            || EBReportServerAction.MapFormat.A3P.equals(mapFormat));

            final boolean landscape = hasOrientation
                ? (EBReportServerAction.MapFormat.LS.equals(mapFormat)
                            || EBReportServerAction.MapFormat.A4LS.equals(mapFormat)
                            || EBReportServerAction.MapFormat.A3LS.equals(mapFormat))
                : (boundingBox.getHeight() < boundingBox.getWidth());

            final boolean a4;
            if (hasFormat) {
                a4 = (EBReportServerAction.MapFormat.A4.equals(mapFormat)
                                || EBReportServerAction.MapFormat.A4LS.equals(mapFormat)
                                || EBReportServerAction.MapFormat.A4P.equals(mapFormat));
            } else {
                final int mapHeight = Integer.parseInt(properties.getProperty(
                            "mapHeight"
                                    + A4_FORMAT
                                    + (landscape ? LANDSCAPE_ORIENTATION : PORTRAIT_ORIENTATION)));
                final int mapWidth = Integer.parseInt(properties.getProperty(
                            "mapWidth"
                                    + A4_FORMAT
                                    + (landscape ? LANDSCAPE_ORIENTATION : PORTRAIT_ORIENTATION)));

                final double bbFittingScaleDenominatorA4 = getScaleDenom(
                        mapWidth,
                        mapHeight,
                        boundingBox.getWidth(),
                        boundingBox.getHeight());

                // fits in 1:500 ?
                a4 = bbFittingScaleDenominatorA4 < 500;
            }

            final int mapHeightLs = Integer.parseInt(properties.getProperty(
                        "mapHeight"
                                + (a4 ? A4_FORMAT : A3_FORMAT)
                                + LANDSCAPE_ORIENTATION));
            final int mapWidthLs = Integer.parseInt(properties.getProperty(
                        "mapWidth"
                                + (a4 ? A4_FORMAT : A3_FORMAT)
                                + LANDSCAPE_ORIENTATION));
            final int mapHeightP = Integer.parseInt(properties.getProperty(
                        "mapHeight"
                                + (a4 ? A4_FORMAT : A3_FORMAT)
                                + PORTRAIT_ORIENTATION));
            final int mapWidthP = Integer.parseInt(properties.getProperty(
                        "mapWidth"
                                + (a4 ? A4_FORMAT : A3_FORMAT)
                                + PORTRAIT_ORIENTATION));

            final int mapHeight;
            final int mapWidth;
            final double newScaleDenominator;
            if (scaleDenominator != null) {
                mapWidth = landscape ? mapWidthLs : mapWidthP;
                mapHeight = landscape ? mapHeightLs : mapHeightP;
                newScaleDenominator = scaleDenominator;
            } else {
                mapWidth = landscape ? mapWidthLs : mapWidthP;
                mapHeight = landscape ? mapHeightLs : mapHeightP;
                final double bbFittingScaleDenominator = getScaleDenom(
                        mapWidth,
                        mapHeight,
                        boundingBox.getWidth(),
                        boundingBox.getHeight());
                final double bbFittingScaleDenominatorWithBorder = Math.round((bbFittingScaleDenominator / 100) + 0.5d)
                            * 100;
                final Integer minScale = (properties.get("minScale") != null)
                    ? Integer.parseInt((String)properties.get("minScale")) : null;
                if ((minScale != null) && (bbFittingScaleDenominatorWithBorder < minScale)) {
                    newScaleDenominator = minScale;
                } else {
                    newScaleDenominator = bbFittingScaleDenominatorWithBorder;
                }
            }

            reportBean.setMapImage(genMapImage(features, mapWidth, mapHeight, newScaleDenominator, properties));
            reportBean.setBarcodeImage(genBarcodeImage(reportBean.getKznr()));
            reportBean.setScale("1:" + (NumberFormat.getIntegerInstance().format(newScaleDenominator)));

            final Integer maxScaleBeforeHint = (properties.getProperty("maxScaleBeforHint") != null)
                ? Integer.parseInt(properties.getProperty("maxScaleBeforHint")) : null;
            final String maxScaleHint = properties.getProperty("maxScaleHint");
            if ((maxScaleBeforeHint != null) && (scaleDenominator > maxScaleBeforeHint)) {
                reportBean.setMapHint(maxScaleHint);
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
            parameters.put("fillKanal", reportBean.isFillAbflusswirksamkeit());

            final List<InputStream> ins = new ArrayList<>();

            final VerdisServerResources reportResourceMap;
            if (EBReportServerAction.Type.FRONTEN.equals(type)) {
                if (a4 && landscape) {
                    reportResourceMap = VerdisServerResources.MAP_FRONTEN_A4LS_JASPER;
                } else if (a4) {
                    reportResourceMap = VerdisServerResources.MAP_FRONTEN_A4P_JASPER;
                } else if (landscape) {
                    reportResourceMap = VerdisServerResources.MAP_FRONTEN_A3LS_JASPER;
                } else {
                    reportResourceMap = VerdisServerResources.MAP_FRONTEN_A3P_JASPER;
                }
            } else {
                if (a4 && landscape) {
                    reportResourceMap = VerdisServerResources.MAP_FLAECHEN_A4LS_JASPER;
                } else if (a4) {
                    reportResourceMap = VerdisServerResources.MAP_FLAECHEN_A4P_JASPER;
                } else if (landscape) {
                    reportResourceMap = VerdisServerResources.MAP_FLAECHEN_A3LS_JASPER;
                } else {
                    reportResourceMap = VerdisServerResources.MAP_FLAECHEN_A3P_JASPER;
                }
            }

            final JRBeanCollectionDataSource dataSourceMap = new JRBeanCollectionDataSource(reportBeans);
            final byte[] bytesMap = generateReport(
                    reportResourceMap,
                    parameters,
                    dataSourceMap,
                    connectionContext);
            ins.add(new ByteArrayInputStream(bytesMap));

            if (type != null) {
                VerdisServerResources reportResourceTable;
                switch (type) {
                    case FLAECHEN: {
                        reportResourceTable = VerdisServerResources.EB_FLAECHEN_JASPER;
                    }
                    break;
                    case FRONTEN: {
                        reportResourceTable = VerdisServerResources.EB_FRONTEN_JASPER;
                    }
                    break;
                    default: {
                        reportResourceTable = null;
                    }
                }

                if (reportResourceTable != null) {
                    final JRBeanCollectionDataSource dataSourceTable = new JRBeanCollectionDataSource(reportBeans);
                    final byte[] bytesTable = generateReport(
                            reportResourceTable,
                            parameters,
                            dataSourceTable,
                            connectionContext);
                    ins.add(new ByteArrayInputStream(bytesTable));
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
     * @param   kassenzeichenNummer  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static Image genBarcodeImage(final String kassenzeichenNummer) {
        try {
            final int dpi = 72;
            final Code39Bean bean = new Code39Bean();
            // BarcodeDimension dimension = bean.calcDimensions(getKznr());
            bean.setModuleWidth(UnitConv.in2mm(1.0f / dpi)); // makes the narrow bar
            // width exactly one pixel
            bean.setWideFactor(3);
            bean.setModuleWidth(1);
            bean.setIntercharGapWidth(1);
            bean.setFontSize(8);
            bean.setBarHeight(22);
            bean.setPattern("*________*");

            final BitmapCanvasProvider provider = new BitmapCanvasProvider(dpi, BufferedImage.TYPE_BYTE_GRAY, true, 0);
            bean.generateBarcode(provider, kassenzeichenNummer);

            provider.finish();

            return provider.getBufferedImage();
        } catch (final IOException ex) {
            LOG.error("error while generating Barcode", ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   features    DOCUMENT ME!
     * @param   properties  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static XBoundingBox genBoundingBox(final Collection<Feature> features, final Properties properties) {
        final String srs = properties.getProperty("mapSrs");
        int srid = CrsTransformer.extractSridFromCrs(srs);
        boolean first = true;
        final Collection<Geometry> geoms = new ArrayList<>(features.size());
        for (final Feature feature : features) {
            Geometry geometry = feature.getGeometry();

            if (geometry != null) {
                geometry = geometry.getEnvelope();

                if (first) {
                    srid = geometry.getSRID();
                    first = false;
                } else {
                    if (geometry.getSRID() != srid) {
                        geometry = CrsTransformer.transformToGivenCrs(geometry, srs);
                    }
                }

                geoms.add(geometry);
            }
        }
        final GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), srid);
        Geometry union = factory.buildGeometry(geoms);
        if (union instanceof GeometryCollection) {
            union = ((GeometryCollection)union).union();
        }
        final XBoundingBox boundingBox = new XBoundingBox(union);
        return boundingBox;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   features          DOCUMENT ME!
     * @param   mapWidth          DOCUMENT ME!
     * @param   mapHeight         DOCUMENT ME!
     * @param   scaleDenominator  DOCUMENT ME!
     * @param   properties        DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static Image genMapImage(final Collection<Feature> features,
            final int mapWidth,
            final int mapHeight,
            final Double scaleDenominator,
            final Properties properties) {
        final SimpleWMS simpleWms = new SimpleWMS(new SimpleWmsGetMapUrl(properties.getProperty("mapUrl")));
        final HeadlessMapProvider mapProvider = new HeadlessMapProvider();
        mapProvider.setCenterMapOnResize(true);
        final String srs = properties.getProperty("mapSrs");
        final Crs crs = new Crs(srs, "", "", true, true);
        mapProvider.setCrs(crs);
        mapProvider.addLayer(simpleWms);

        for (final Feature feature : features) {
            mapProvider.addFeature(feature);
            if (mapProvider.getMappingComponent().getPFeatureHM().get(feature) != null) {
                final PNode annotationNode = mapProvider.getMappingComponent()
                            .getPFeatureHM()
                            .get(feature)
                            .getPrimaryAnnotationNode();
                if (annotationNode != null) {
                    final PBounds bounds = annotationNode.getBounds();
                    bounds.x = -bounds.width / 2;
                    bounds.y = -bounds.height / 2;
                    annotationNode.setBounds(bounds);
                }
            }
        }

        final XBoundingBox boundingBox = genBoundingBox(features, properties);

        final double bbWidth = boundingBox.getWidth();
        final double bbHeight = boundingBox.getHeight();
        final double bbCenterX = boundingBox.getX1() + (bbWidth / 2);
        final double bbCenterY = boundingBox.getY1() + (bbHeight / 2);

        final double mapWidthInMeter = (mapWidth / PPI) * METERS_TO_INCH_FACTOR;
        final double mapHeightInMeter = (mapHeight / PPI) * METERS_TO_INCH_FACTOR;
        final double worldWidthInPx = mapWidthInMeter * scaleDenominator;
        final double worldHeightInPx = mapHeightInMeter * scaleDenominator;

        boundingBox.setX1(bbCenterX - (worldWidthInPx / 2d));
        boundingBox.setX2(bbCenterX + (worldWidthInPx / 2d));
        boundingBox.setY1(bbCenterY - (worldHeightInPx / 2d));
        boundingBox.setY2(bbCenterY + (worldHeightInPx / 2d));

        mapProvider.setBoundingBox(boundingBox);

        final int mapDPI = Integer.parseInt(properties.getProperty("mapDPI"));

        mapProvider.setFeatureResolutionFactor(mapDPI);
        try {
            return mapProvider.getImageAndWait(72, mapDPI, mapWidth, mapHeight);
        } catch (final Exception ex) {
            LOG.error("error while creating mapImage", ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   mapWidthInPx     DOCUMENT ME!
     * @param   mapHeightInPx    DOCUMENT ME!
     * @param   bbWidthInMeter   DOCUMENT ME!
     * @param   bbHeightInMeter  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static double getScaleDenom(final int mapWidthInPx,
            final int mapHeightInPx,
            final double bbWidthInMeter,
            final double bbHeightInMeter) {
        final double mapRatio = mapWidthInPx / (double)mapHeightInPx;
        final double bbRatio = bbWidthInMeter / bbHeightInMeter;

        final double mapWidthInMeter = (mapWidthInPx / PPI) * METERS_TO_INCH_FACTOR;
        final double mapHeightInMeter = (mapHeightInPx / PPI) * METERS_TO_INCH_FACTOR;

        return (bbRatio > mapRatio) ? (bbWidthInMeter / mapWidthInMeter) : (bbHeightInMeter / mapHeightInMeter);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static Properties getProperties(final ConnectionContext connectionContext) throws Exception {
        final User user = SessionManager.getSession().getUser();
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
        final JasperReport jasperReport = getReport(reportResource, connectionContext);
        final JasperPrint print = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        try(final ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            JasperExportManager.exportReportToPdfStream(print, os);
            final byte[] bytes = os.toByteArray();
            return bytes;
        }
    }
}
