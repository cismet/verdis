/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * Main.java
 *
 * Created on 6. Januar 2005, 14:55
 */
package de.cismet.verdis.gui;

import Sirius.navigator.DefaultNavigatorExceptionHandler;
import Sirius.navigator.connection.Connection;
import Sirius.navigator.connection.ConnectionFactory;
import Sirius.navigator.connection.ConnectionInfo;
import Sirius.navigator.connection.ConnectionSession;
import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.connection.proxy.ConnectionProxy;
import Sirius.navigator.downloadmanager.CsvExportSearchDownload;
import Sirius.navigator.event.CatalogueSelectionListener;
import Sirius.navigator.exception.ConnectionException;
import Sirius.navigator.resource.PropertyManager;
import Sirius.navigator.types.treenode.RootTreeNode;
import Sirius.navigator.ui.ComponentRegistry;
import Sirius.navigator.ui.DescriptionPane;
import Sirius.navigator.ui.DescriptionPaneFS;
import Sirius.navigator.ui.LayoutedContainer;
import Sirius.navigator.ui.MutableMenuBar;
import Sirius.navigator.ui.MutablePopupMenu;
import Sirius.navigator.ui.MutableToolBar;
import Sirius.navigator.ui.attributes.AttributeViewer;
import Sirius.navigator.ui.attributes.editor.AttributeEditor;
import Sirius.navigator.ui.tree.MetaCatalogueTree;
import Sirius.navigator.ui.tree.ResultNodeListener;
import Sirius.navigator.ui.tree.SearchResultsTree;

import Sirius.server.localserver.attribute.MemberAttributeInfo;
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.MetaObjectNode;
import Sirius.server.middleware.types.Node;
import Sirius.server.newuser.User;

import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;

import com.vividsolutions.jts.geom.Geometry;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolox.event.PNotification;
import edu.umd.cs.piccolox.event.PNotificationCenter;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import net.infonode.docking.mouse.DockingWindowActionMouseButtonListener;
import net.infonode.docking.properties.RootWindowProperties;
import net.infonode.docking.theme.DockingWindowsTheme;
import net.infonode.docking.theme.ShapedGradientDockingTheme;
import net.infonode.docking.util.DeveloperUtil;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.PropertiesUtil;
import net.infonode.docking.util.StringViewMap;
import net.infonode.gui.componentpainter.AlphaGradientComponentPainter;
import net.infonode.gui.componentpainter.GradientComponentPainter;
import net.infonode.util.Direction;

import org.apache.commons.lang.StringUtils;

import org.jdesktop.swingx.JXLoginPane;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.auth.DefaultUserNameStore;
import org.jdesktop.swingx.auth.LoginService;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.sql.Timestamp;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.EnumMap;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;

import de.cismet.cids.custom.commons.gui.BaulastSuchDialog;
import de.cismet.cids.custom.commons.gui.ObjectRendererDialog;
import de.cismet.cids.custom.commons.gui.VermessungsrissSuchDialog;
import de.cismet.cids.custom.commons.searchgeometrylistener.BaulastblattNodesSearchCreateSearchGeometryListener;
import de.cismet.cids.custom.commons.searchgeometrylistener.FlurstueckNodesSearchCreateSearchGeometryListener;
import de.cismet.cids.custom.commons.searchgeometrylistener.NodesSearchCreateSearchGeometryListener;
import de.cismet.cids.custom.commons.searchgeometrylistener.RissNodesSearchCreateSearchGeometryListener;
import de.cismet.cids.custom.navigatorstartuphooks.MotdStartUpHook;
import de.cismet.cids.custom.reports.verdis.EBGeneratorDialog;
import de.cismet.cids.custom.util.VerdisUtils;
import de.cismet.cids.custom.wunda_blau.startuphooks.MotdWundaStartupHook;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;

import de.cismet.cids.navigatorstartuphooks.CidsServerMessageStartUpHook;

import de.cismet.cids.search.QuerySearchResultsAction;
import de.cismet.cids.search.QuerySearchResultsActionDialog;

import de.cismet.cids.server.actions.ServerActionParameter;
import de.cismet.cids.server.search.builtin.CsvExportSearchStatement;

import de.cismet.cids.servermessage.CidsServerMessageNotifier;
import de.cismet.cids.servermessage.CidsServerMessageNotifierListener;
import de.cismet.cids.servermessage.CidsServerMessageNotifierListenerEvent;

import de.cismet.cismap.commons.CidsLayerFactory;
import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.ModeLayer;
import de.cismet.cismap.commons.ModeLayerRegistry;
import de.cismet.cismap.commons.PNodeProvider;
import de.cismet.cismap.commons.ServiceLayer;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.features.FeatureCollectionAdapter;
import de.cismet.cismap.commons.features.FeatureCollectionEvent;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.layerwidget.ActiveLayerModel;
import de.cismet.cismap.commons.gui.piccolo.PFeature;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.AttachFeatureListener;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.CreateGeometryListener;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.CustomFeatureInfoListener;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.FeatureMoveListener;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.JoinPolygonsListener;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.SelectionListener;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.SimpleMoveListener;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.SplitPolygonListener;
import de.cismet.cismap.commons.gui.simplelayerwidget.NewSimpleInternalLayerWidget;
import de.cismet.cismap.commons.interaction.CismapBroker;
import de.cismet.cismap.commons.interaction.events.ActiveLayerEvent;
import de.cismet.cismap.commons.tools.PFeatureTools;
import de.cismet.cismap.commons.wfsforms.AbstractWFSForm;

import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.lookupoptions.gui.OptionsClient;
import de.cismet.lookupoptions.gui.OptionsDialog;

import de.cismet.remote.RESTRemoteControlStarter;

import de.cismet.tools.Static2DTools;
import de.cismet.tools.StaticDebuggingTools;

import de.cismet.tools.configuration.Configurable;
import de.cismet.tools.configuration.ConfigurationManager;

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerAction;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;
import de.cismet.tools.gui.startup.StaticStartupTools;

import de.cismet.validation.Validator;
import de.cismet.validation.ValidatorListener;
import de.cismet.validation.ValidatorState;

import de.cismet.validation.validator.AggregatedValidator;

import de.cismet.verdis.AbstractClipboard;
import de.cismet.verdis.AppModeListener;
import de.cismet.verdis.BefreiungerlaubnisGeometrieClipboard;
import de.cismet.verdis.CidsAppBackend;
import de.cismet.verdis.ClipboardListener;
import de.cismet.verdis.CrossReference;
import de.cismet.verdis.FlaechenClipboard;
import de.cismet.verdis.FrontenClipboard;
import de.cismet.verdis.KassenzeichenGeometrienClipboard;
import de.cismet.verdis.Version;

import de.cismet.verdis.commons.constants.AnschlussgradPropertyConstants;
import de.cismet.verdis.commons.constants.FlaechePropertyConstants;
import de.cismet.verdis.commons.constants.FlaechenartPropertyConstants;
import de.cismet.verdis.commons.constants.FlaecheninfoPropertyConstants;
import de.cismet.verdis.commons.constants.FrontPropertyConstants;
import de.cismet.verdis.commons.constants.FrontinfoPropertyConstants;
import de.cismet.verdis.commons.constants.KanalanschlussPropertyConstants;
import de.cismet.verdis.commons.constants.KassenzeichenPropertyConstants;
import de.cismet.verdis.commons.constants.StrassePropertyConstants;
import de.cismet.verdis.commons.constants.StrassenreinigungPropertyConstants;
import de.cismet.verdis.commons.constants.VeranlagungPropertyConstants;
import de.cismet.verdis.commons.constants.VeranlagungseintragPropertyConstants;
import de.cismet.verdis.commons.constants.VeranlagungsgrundlagePropertyConstants;
import de.cismet.verdis.commons.constants.VeranlagungsnummerPropertyConstants;
import de.cismet.verdis.commons.constants.VeranlagungspostenPropertyConstants;
import de.cismet.verdis.commons.constants.VerdisConstants;
import de.cismet.verdis.commons.constants.VerdisMetaClassConstants;

import de.cismet.verdis.data.AppPreferences;

import de.cismet.verdis.gui.befreiungerlaubnis.BefreiungerlaubnisTable;
import de.cismet.verdis.gui.befreiungerlaubnis_geometrie.BefreiungerlaubnisGeometrieDetailsPanel;
import de.cismet.verdis.gui.befreiungerlaubnis_geometrie.BefreiungerlaubnisGeometrieTable;
import de.cismet.verdis.gui.befreiungerlaubnis_geometrie.BefreiungerlaubnisGeometrieTablePanel;
import de.cismet.verdis.gui.history.HistoryPanel;
import de.cismet.verdis.gui.kassenzeichen_geometrie.KassenzeichenGeometrienPanel;
import de.cismet.verdis.gui.regenflaechen.RegenFlaechenDetailsPanel;
import de.cismet.verdis.gui.regenflaechen.RegenFlaechenSummenPanel;
import de.cismet.verdis.gui.regenflaechen.RegenFlaechenTable;
import de.cismet.verdis.gui.regenflaechen.RegenFlaechenTablePanel;
import de.cismet.verdis.gui.srfronten.SRFrontenDetailsPanel;
import de.cismet.verdis.gui.srfronten.SRFrontenSummenPanel;
import de.cismet.verdis.gui.srfronten.SRFrontenTable;
import de.cismet.verdis.gui.srfronten.SRFrontenTablePanel;

import de.cismet.verdis.search.ServerSearchCreateSearchGeometryListener;

import de.cismet.verdis.server.action.EBReportServerAction;
import de.cismet.verdis.server.action.RenameKassenzeichenServerAction;
import de.cismet.verdis.server.search.AssignLandparcelGeomSearch;
import de.cismet.verdis.server.search.DeletedKassenzeichenIdSearchStatement;
import de.cismet.verdis.server.search.KassenzeichenGeomSearch;
import de.cismet.verdis.server.search.NextKassenzeichenWithoutKassenzeichenGeometrieSearchStatement;

/**
 * DOCUMENT ME!
 *
 * @author   hell
 * @version  $Revision$, $Date$
 */
public final class Main extends javax.swing.JFrame implements AppModeListener, Configurable, CidsBeanStore {

    //~ Static fields/initializers ---------------------------------------------

    public static double INITIAL_WMS_BB_X1 = 2569442.79;
    public static double INITIAL_WMS_BB_Y1 = 5668858.33;
    public static double INITIAL_WMS_BB_X2 = 2593744.91;
    public static double INITIAL_WMS_BB_Y2 = 5688416.22;
    public static final String KASSENZEICHEN_SEARCH_GEOMETRY_LISTENER = "KASSENZEICHEN_SEARCH_GEOMETRY_LISTENER";
    public static final String KASSENZEICHEN_GEOMETRIE_ASSIGN_GEOMETRY_LISTENER =
        "KASSENZEICHEN_GEOMETRIE_ASSIGN_GEOMETRY_LISTENER";
    private static final String DIRECTORYPATH_HOME = System.getProperty("user.home");
    private static final String FILESEPARATOR = System.getProperty("file.separator");
    private static final String DIRECTORYEXTENSION = System.getProperty("directory.extension");
    private static final String DIRECTORY_VERDISHOME = ".verdis"
                + ((DIRECTORYEXTENSION != null) ? DIRECTORYEXTENSION : "");
    private static final String FILE_LAYOUT = "verdis.layout";
    private static final String FILE_SCREEN = "verdis.screen";
    private static final String FILE_MAP = "verdis.map";
    private static final String DIRECTORYPATH_VERDIS = DIRECTORYPATH_HOME + FILESEPARATOR + DIRECTORY_VERDISHOME;
    private static final String FILEPATH_LAYOUT = DIRECTORYPATH_VERDIS + FILESEPARATOR + FILE_LAYOUT;
    private static final String FILEPATH_MAP = DIRECTORYPATH_VERDIS + FILESEPARATOR + FILE_MAP;
    private static final String FILEPATH_SCREEN = DIRECTORYPATH_VERDIS + FILESEPARATOR + FILE_SCREEN;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(Main.class);
    private static JFrame SPLASH;
    private static final Image BANNER_IMAGE = new javax.swing.ImageIcon(Main.class.getResource(
                "/de/cismet/verdis/login.png")).getImage();

    //~ Instance fields --------------------------------------------------------

    final WindowAdapter loadLayoutWhenOpenedAdapter = new WindowAdapter() {

            @Override
            public void windowOpened(final WindowEvent e) {
                switch (CidsAppBackend.getInstance().getMode()) {
                    case ALLGEMEIN: {
                        setupLayoutInfo();
                    }
                    break;
                    case SR: {
                        setupLayoutSR();
                    }
                    break;
                    case REGEN: {
                        setupLayoutRegen();
                    }
                    break;
                    case KANALDATEN: {
                        setupLayoutKanaldaten();
                    }
                    break;
                }
                removeWindowListener(loadLayoutWhenOpenedAdapter);
            }
        };

    private boolean loggedIn = false;

    private DescriptionPane descriptionPane;
    private SearchResultsTree searchResultsTree;

    private final Map<String, CidsBean> veranlagungsnummern = new HashMap<>();
    private final Map<String, CidsBean> veranlagungsgrundlageMap = new HashMap<>();
    private final Map<String, Double> veranlagungSummeMap = new HashMap<>();
    private CidsAppBackend.Mode currentMode = null;
    private JDialog about = null;
    // Inserting Docking Window functionalty (Sebastian) 24.07.07
    private final Icon icoKassenzeichen = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/verdis/res/images/titlebars/kassenzeichen.png"));
    private final Icon icoKassenzeichenList = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/verdis/res/images/titlebars/kassenzeichen.png"));
    private final Icon icoSummen = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/verdis/res/images/titlebars/sum.png"));
    private final Icon icoKanal = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/verdis/res/images/titlebars/pipe.png"));
    private final Icon icoKarte = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/verdis/res/images/titlebars/flaechen.png"));
    private final Icon icoTabelle = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/verdis/res/images/titlebars/flaechen.png"));
    private final Icon icoDetails = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/verdis/res/images/titlebars/flaechen.png"));
    // private Color myBlue=new java.awt.Color(0, 51, 153);
    private final Color myBlue = new Color(124, 160, 221);
    private boolean editMode = false;
    private boolean readonly = true;
    private String userString;
    private final EnumMap<CidsAppBackend.Mode, AbstractClipboard> clipboards = new EnumMap<>(CidsAppBackend.Mode.class);

    // Inserting Docking Window functionalty (Sebastian) 24.07.07
    private View vKassenzeichen;
    private View vKassenzeichenList;
    private View vKanaldaten;
    private View vSummen;
    private View vKarte;
    private View vTabelleSR;
    private View vDetailsSR;
    private View vZusammenfassungSR;
    private View vDetailsAllgemein;
    private View vInfoAllgemein;
    private View vTabelleRegen;
    private View vDetailsRegen;
    private View vTabelleVersickerung;
    private View vDetailsVersickerung;
//    private View vHistory;
    private RootWindow rootWindow;
    private final StringViewMap viewMap = new StringViewMap();
    private final ActiveLayerModel mappingModel = new ActiveLayerModel();
    private final ConfigurationManager configurationManager = new ConfigurationManager();
    private final RegenFlaechenSummenPanel regenSumPanel = new RegenFlaechenSummenPanel();
    private final KassenzeichenPanel kassenzeichenPanel = new KassenzeichenPanel();

    private final KassenzeichenListPanel kassenzeichenListPanel = new KassenzeichenListPanel(true, true);
    private final KanaldatenPanel kanaldatenPanel = new KanaldatenPanel();
    private final KassenzeichenGeometrienPanel kassenzeichenGeometrienPanel = new KassenzeichenGeometrienPanel();
    private final AllgemeineInfosPanel allgInfosPanel = new AllgemeineInfosPanel();
    private final RegenFlaechenDetailsPanel regenFlaechenDetailsPanel = RegenFlaechenDetailsPanel.getInstance();
    private final RegenFlaechenTablePanel regenFlaechenTablePanel = new RegenFlaechenTablePanel();
    private final BefreiungerlaubnisGeometrieDetailsPanel befreiungerlaubnisGeometrieDetailsPanel =
        BefreiungerlaubnisGeometrieDetailsPanel.getInstance();
    private final BefreiungerlaubnisGeometrieTablePanel befreiungerlaubnisGeometrieTablePanel =
        new BefreiungerlaubnisGeometrieTablePanel();
    private final KartenPanel kartenPanel = new KartenPanel();
    private final SRFrontenTablePanel srFrontenTablePanel = new SRFrontenTablePanel();
    private final SRFrontenDetailsPanel srFrontenDetailsPanel = new SRFrontenDetailsPanel();
    private final SRFrontenSummenPanel srSummenPanel = new SRFrontenSummenPanel();
    private final AggregatedValidator aggValidator = new AggregatedValidator();
    private final SAPClipboardListener sapClipboardListener = new SAPClipboardListener();
    private final TimeRecoveryPanel timeRecoveryPanel = TimeRecoveryPanel.getInstance();

    private boolean fixMapExtent;
    private boolean fixMapExtentMode;
    private boolean isInit = true;
    private CidsBean kassenzeichenBean;
    private ObjectRendererDialog alkisRendererDialog;

    private QuerySearchResultsActionDialog abfrageDialog;

    private String totd;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnHistory;
    private javax.swing.JButton btnTimeRecovery;
    private javax.swing.JButton cmdAbfrageeditor;
    private javax.swing.JButton cmdAdd;
    private javax.swing.JButton cmdArbeitspakete;
    private javax.swing.JButton cmdCancel;
    private javax.swing.JButton cmdCopy;
    private javax.swing.JButton cmdCut;
    private javax.swing.JButton cmdDeleteKassenzeichen;
    private javax.swing.JButton cmdDownloads;
    private javax.swing.JButton cmdEditMode;
    private javax.swing.JButton cmdFortfuehrung;
    private javax.swing.JButton cmdGrundbuchblattSuche;
    private javax.swing.JButton cmdInfo;
    private javax.swing.JButton cmdLagisCrossover;
    private javax.swing.JButton cmdMemory;
    private javax.swing.JButton cmdNewKassenzeichen;
    private javax.swing.JButton cmdNextKassenzeichenWithoutGeom;
    private javax.swing.JButton cmdOk;
    private javax.swing.JButton cmdOpenInD3;
    private javax.swing.JButton cmdPaste;
    private javax.swing.JButton cmdPdf;
    private javax.swing.JButton cmdRecalculateArea;
    private javax.swing.JButton cmdRefreshEnumeration;
    private javax.swing.JButton cmdRemove;
    private javax.swing.JToggleButton cmdSAPCheck;
    private javax.swing.JButton cmdSearchBaulasten;
    private javax.swing.JButton cmdSearchRisse;
    private javax.swing.JButton cmdTest;
    private javax.swing.JButton cmdTest2;
    private javax.swing.JButton cmdUndo;
    private javax.swing.JButton cmdVeranlagungsdatei;
    private javax.swing.JButton cmdWorkflow;
    private de.cismet.tools.gui.exceptionnotification.ExceptionNotificationStatusPanel exceptionNotificationStatusPanel;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator11;
    private javax.swing.JSeparator jSeparator14;
    private javax.swing.JSeparator jSeparator15;
    private javax.swing.JSeparator jSeparator16;
    private javax.swing.JSeparator jSeparator17;
    private javax.swing.JSeparator jSeparator18;
    private javax.swing.JSeparator jSeparator19;
    private javax.swing.JSeparator jSeparator20;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JMenu menEdit;
    private javax.swing.JMenu menExtras;
    private javax.swing.JMenu menFile;
    private javax.swing.JMenu menHelp;
    private javax.swing.JMenu menWindows;
    private javax.swing.JMenuItem mniClose;
    private javax.swing.JMenuItem mniDetails;
    private javax.swing.JMenuItem mniKanalanschluss;
    private javax.swing.JMenuItem mniKarte;
    private javax.swing.JMenuItem mniKassenzeichen;
    private javax.swing.JMenuItem mniKassenzeichen1;
    private javax.swing.JMenuItem mniLoadLayout;
    private javax.swing.JMenuItem mniOptions;
    private javax.swing.JMenuItem mniResetWindowLayout;
    private javax.swing.JMenuItem mniSaveLayout;
    private javax.swing.JMenuItem mniSummen;
    private javax.swing.JMenuItem mniTabelle;
    private javax.swing.JMenuItem mnuChangeUser;
    private javax.swing.JMenuItem mnuHelp;
    private javax.swing.JMenuItem mnuInfo;
    private javax.swing.JMenuItem mnuNewKassenzeichen;
    private javax.swing.JMenuItem mnuRenameAnyKZ;
    private javax.swing.JMenuItem mnuRenameCurrentKZ;
    private javax.swing.JPanel panMain;
    private javax.swing.JPopupMenu.Separator sepOptions;
    private de.cismet.cids.navigator.utils.SimpleMemoryMonitoringToolbarWidget simpleMemoryMonitoringToolbarWidget1;
    private javax.swing.JToolBar tobVerdis;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Main object.
     */
    private Main() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    public void init() {
        readonly = readonly
                    || CidsAppBackend.getInstance().getAppPreferences().getMode().trim().toLowerCase()
                    .equals("readonly");

        StaticSwingTools.tweakUI();

        CidsAppBackend.getInstance().addCidsBeanStore(this);
        CidsAppBackend.getInstance().addCidsBeanStore(kassenzeichenPanel);
        CidsAppBackend.getInstance().addCidsBeanStore(kassenzeichenListPanel);
        CidsAppBackend.getInstance().addCidsBeanStore(getSRFrontenTable());
        CidsAppBackend.getInstance().addCidsBeanStore(srSummenPanel);
        CidsAppBackend.getInstance().addCidsBeanStore(kartenPanel);
        CidsAppBackend.getInstance().addCidsBeanStore(kassenzeichenGeometrienPanel);
        CidsAppBackend.getInstance().addCidsBeanStore(allgInfosPanel);
//        CidsAppBackend.getInstance().addCidsBeanStore(historyPanel);
        CidsAppBackend.getInstance().addCidsBeanStore(getRegenFlaechenTable());
        CidsAppBackend.getInstance().addCidsBeanStore(kanaldatenPanel);
        CidsAppBackend.getInstance().addCidsBeanStore(regenSumPanel);
        CidsAppBackend.getInstance().addCidsBeanStore(timeRecoveryPanel);

        CidsAppBackend.getInstance().addEditModeListener(kassenzeichenPanel);
        CidsAppBackend.getInstance().addEditModeListener(kassenzeichenListPanel);
        CidsAppBackend.getInstance().addEditModeListener(srFrontenDetailsPanel);
        CidsAppBackend.getInstance().addEditModeListener(kassenzeichenGeometrienPanel);
        CidsAppBackend.getInstance().addEditModeListener(allgInfosPanel);
        CidsAppBackend.getInstance().addEditModeListener(regenFlaechenDetailsPanel);
        CidsAppBackend.getInstance().addEditModeListener(befreiungerlaubnisGeometrieDetailsPanel);
        CidsAppBackend.getInstance().addEditModeListener(kartenPanel);
        CidsAppBackend.getInstance().addEditModeListener(kanaldatenPanel);
        CidsAppBackend.getInstance().addEditModeListener(timeRecoveryPanel);

        CidsAppBackend.getInstance().addAppModeListener(kartenPanel);
        CidsAppBackend.getInstance().addAppModeListener(this);
        CidsAppBackend.getInstance().addAppModeListener(kassenzeichenPanel);

        CidsAppBackend.getInstance()
                .getMainMap()
                .getFeatureCollection()
                .addFeatureCollectionListener(getRegenFlaechenTable());
        CidsAppBackend.getInstance()
                .getMainMap()
                .getFeatureCollection()
                .addFeatureCollectionListener(getSRFrontenTable());
        CidsAppBackend.getInstance()
                .getMainMap()
                .getFeatureCollection()
                .addFeatureCollectionListener(getBefreiungerlaubnisGeometrieTable());
        CidsAppBackend.getInstance()
                .getMainMap()
                .getFeatureCollection()
                .addFeatureCollectionListener(kassenzeichenGeometrienPanel);
        CidsAppBackend.getInstance()
                .getMainMap()
                .getFeatureCollection()
                .addFeatureCollectionListener(new FeatureCollectionAdapter() {

                        @Override
                        public void featureSelectionChanged(final FeatureCollectionEvent fce) {
                            refreshItemButtons();
                        }

                        @Override
                        public void featuresAdded(final FeatureCollectionEvent fce) {
                            refreshItemButtons();
                        }

                        @Override
                        public void featuresChanged(final FeatureCollectionEvent fce) {
                            refreshItemButtons();
                        }

                        @Override
                        public void featuresRemoved(final FeatureCollectionEvent fce) {
                            refreshItemButtons();
                        }
                    });

        final PCanvas pc = CidsAppBackend.getInstance().getMainMap().getSelectedObjectPresenter();
        pc.setBackground(this.getBackground());
        regenFlaechenDetailsPanel.setBackgroundPCanvas(pc);
        srFrontenDetailsPanel.setBackgroundPCanvas(pc);

        getSRFrontenTable().setSelectedRowListener(srFrontenDetailsPanel);
        getRegenFlaechenTable().setSelectedRowListener(regenFlaechenDetailsPanel);
        getBefreiungerlaubnisGeometrieTable().setSelectedRowListener(befreiungerlaubnisGeometrieDetailsPanel);

        try {
            try {
                javax.swing.UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
            } catch (Exception e) {
                LOG.warn("Fehler beim Einstellen des Look&Feels's!", e);
            }

            LOG.info("Verdis gestartet :-)");

            if (LOG.isDebugEnabled()) {
                LOG.debug("Gui kram erledigt");
                LOG.debug("initComponents()");
            }

            initComponents();
            DefaultNavigatorExceptionHandler.getInstance().addListener(exceptionNotificationStatusPanel);

            // dialog for alkis_landparcel - muss vor initComponentRegistry erzeugt werden
            initComponentRegistry(this);

            kartenPanel.initPluginToolbarComponents();

            abfrageDialog = new QuerySearchResultsActionDialog(
                    this,
                    false,
                    new QuerySearchResultsAction() {

                        @Override
                        public String getName() {
                            return "nach CSV exportieren";
                        }

                        @Override
                        public void doAction() {
                            final String title = getMetaClass().getName();

                            if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(Main.this)) {
                                final List<String> header = new ArrayList<>(getAttributeNames().size());
                                final List<String> fields = new ArrayList<>(getAttributeNames().size());
                                for (final String attrKey : getAttributeKeys()) {
                                    final MemberAttributeInfo mai = (MemberAttributeInfo)getMetaClass()
                                                    .getMemberAttributeInfos().get(attrKey);
                                    header.add(getAttributeNames().get(attrKey));
                                    fields.add(mai.getFieldName());
                                }
                                final CsvExportSearchStatement search = new CsvExportSearchStatement(
                                        getMetaClass().getTableName(),
                                        VerdisConstants.DOMAIN,
                                        fields,
                                        getWhereCause());
                                search.setDateFormat("dd.MM.yyyy");
                                search.setBooleanFormat(new String[] { "nein", "ja" });
                                DownloadManager.instance()
                                            .add(
                                                new CsvExportSearchDownload(
                                                    search,
                                                    title,
                                                    DownloadManagerDialog.getInstance().getJobName(),
                                                    title,
                                                    header));
                                final DownloadManagerDialog downloadManagerDialog = DownloadManagerDialog.getInstance();
                                downloadManagerDialog.pack();
                                StaticSwingTools.showDialog(Main.this, downloadManagerDialog, true);
                            }
                        }
                    });

            abfrageDialog.getQuerySearchResultsActionPanel().setDateFormat("dd.MM.yyyy");

            alkisRendererDialog = new ObjectRendererDialog(this, false, descriptionPane);
            alkisRendererDialog.setSize(1000, 800);

            configurationManager.setFileName("configuration.xml");
            configurationManager.setClassPathFolder("/verdis/");
            configurationManager.setFolder(DIRECTORY_VERDISHOME);
            if (LOG.isDebugEnabled()) {
                LOG.debug("mc:" + getMappingComponent());
            }
            configurationManager.addConfigurable(mappingModel);
            configurationManager.addConfigurable(getMappingComponent());
            configurationManager.addConfigurable(this);

            configurationManager.addConfigurable(OptionsClient.getInstance());
            configurationManager.configure(OptionsClient.getInstance());

            // Anwendungslogik
            LOG.info("Einstellungen der Karte vornehmen");
            setEditMode(false);
            if (LOG.isDebugEnabled()) {
                LOG.debug("fertig");
            }

            kassenzeichenPanel.setMainApp(this);

            final KeyStroke configLoggerKeyStroke = KeyStroke.getKeyStroke('L', InputEvent.CTRL_MASK);
            final Action configAction = new AbstractAction() {

                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        java.awt.EventQueue.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    Log4JQuickConfig.getSingletonInstance().setVisible(true);
                                }
                            });
                    }
                };
            getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(configLoggerKeyStroke, "CONFIGLOGGING");
            getRootPane().getActionMap().put("CONFIGLOGGING", configAction);

            // WFSForms
            final Set<String> keySet = CidsAppBackend.getInstance().getAppPreferences().getWfsForms().keySet();
            if (LOG.isDebugEnabled()) {
                LOG.debug("WFSForms " + keySet);
            }
            for (final String key : keySet) {
                //
                try {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("WFSForms: " + key);
                    }
                    final AbstractWFSForm form = CidsAppBackend.getInstance()
                                .getAppPreferences()
                                .getWfsForms()
                                .get(key);
                    final JDialog formView = new JDialog(this, form.getTitle());
                    formView.getContentPane().setLayout(new BorderLayout());
                    formView.getContentPane().add(form, BorderLayout.CENTER);
                    formView.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                    form.setMappingComponent(CidsAppBackend.getInstance().getMainMap());

                    formView.pack();

                    // Menu
                    final JButton cmd = new JButton(null, form.getIcon());
                    cmd.setToolTipText(form.getMenuString());
                    cmd.addActionListener(new ActionListener() {

                            @Override
                            public void actionPerformed(final ActionEvent e) {
                                StaticSwingTools.showDialog(formView);
                            }
                        });

                    EventQueue.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                tobVerdis.add(cmd);
                            }
                        });
                } catch (Throwable thr) {
                    LOG.error("Fehler beim Hinzuf\u00FCgen einer WFSForm", thr);
                }
            }

            // Inserting Docking Window functionalty (Sebastian) 24.07.07
            rootWindow = DockingUtil.createRootWindow(viewMap, true);

            // Views anlegen
            vKassenzeichen = new View(
                    "Kassenzeichen",
                    Static2DTools.borderIcon(icoKassenzeichen, 0, 3, 0, 1),
                    kassenzeichenPanel);
            viewMap.addView("Kassenzeichen", vKassenzeichen);
            vKassenzeichen.getCustomTitleBarComponents().addAll(kassenzeichenPanel.getCustomButtons());

            vKassenzeichenList = new View(
                    "Kassenzeichen-Liste",
                    Static2DTools.borderIcon(icoKassenzeichenList, 0, 3, 0, 1),
                    kassenzeichenListPanel);
            viewMap.addView("Kassenzeichen-Liste", vKassenzeichenList);

            vSummen = new View("Summen", Static2DTools.borderIcon(icoSummen, 0, 3, 0, 1), regenSumPanel);
            viewMap.addView("Summen", vSummen);

            vKanaldaten = new View("Kanalanschluss", Static2DTools.borderIcon(icoKanal, 0, 3, 0, 1), kanaldatenPanel);
            viewMap.addView("Kanalanschluss", vKanaldaten);

            vKarte = new View("Karte", Static2DTools.borderIcon(icoKarte, 0, 3, 0, 1), kartenPanel);
            viewMap.addView("Karte", vKarte);

            vTabelleSR = new View(
                    "Tabellenansicht (Fronten)",
                    Static2DTools.borderIcon(icoTabelle, 0, 3, 0, 1),
                    srFrontenTablePanel);
            viewMap.addView("Tabellenansicht (Fronten)", vTabelleSR);

            vDetailsSR = new View(
                    "Details (Fronten)",
                    Static2DTools.borderIcon(icoDetails, 0, 3, 0, 1),
                    srFrontenDetailsPanel);
            viewMap.addView("Details", vDetailsSR);

            vZusammenfassungSR = new View(
                    "ESW Zusammenfassung",
                    Static2DTools.borderIcon(icoTabelle, 0, 3, 0, 1),
                    srSummenPanel);
            viewMap.addView("ESW Zusammenfassung", vZusammenfassungSR);

            vDetailsAllgemein = new View(
                    "Details (Flächen)",
                    Static2DTools.borderIcon(icoTabelle, 0, 3, 0, 1),
                    kassenzeichenGeometrienPanel);
            viewMap.addView("Kassenzeichen-Flächen", vDetailsAllgemein);

            vInfoAllgemein = new View(
                    "Informationen",
                    Static2DTools.borderIcon(icoTabelle, 0, 3, 0, 1),
                    allgInfosPanel);
            viewMap.addView("Informationen", vInfoAllgemein);

            vTabelleRegen = new View(
                    "Tabellenansicht (versiegelte Fl\u00E4chen)",
                    Static2DTools.borderIcon(icoTabelle, 0, 3, 0, 1),
                    regenFlaechenTablePanel);
            viewMap.addView("Tabellenansicht (versiegelte Flaechen)", vTabelleRegen);

            vDetailsRegen = new View(
                    "Details (versiegelte Fl\u00E4chen)",
                    Static2DTools.borderIcon(icoTabelle, 0, 3, 0, 1),
                    regenFlaechenDetailsPanel);
            viewMap.addView("Details (versiegelte Flaechen)", vDetailsRegen);

            vTabelleVersickerung = new View(
                    "Tabellenansicht (Versickerung/Einleitung)",
                    Static2DTools.borderIcon(icoTabelle, 0, 3, 0, 1),
                    befreiungerlaubnisGeometrieTablePanel);
            viewMap.addView("Tabellenansicht (Versickerung/Einleitung)", vTabelleVersickerung);

            vDetailsVersickerung = new View(
                    "Details (Versickerung/Einleitung)",
                    Static2DTools.borderIcon(icoTabelle, 0, 3, 0, 1),
                    befreiungerlaubnisGeometrieDetailsPanel);
            viewMap.addView("Details (Versickerung/Einleitung)", vDetailsVersickerung);

            rootWindow.addTabMouseButtonListener(DockingWindowActionMouseButtonListener.MIDDLE_BUTTON_CLOSE_LISTENER);

            final DockingWindowsTheme theme = new ShapedGradientDockingTheme();

            rootWindow.getRootWindowProperties().addSuperObject(
                theme.getRootWindowProperties());

            final RootWindowProperties titleBarStyleProperties = PropertiesUtil
                        .createTitleBarStyleRootWindowProperties();

            rootWindow.getRootWindowProperties().addSuperObject(
                titleBarStyleProperties);

            rootWindow.getRootWindowProperties().getDockingWindowProperties().setUndockEnabled(true);

            final AlphaGradientComponentPainter x = new AlphaGradientComponentPainter(
                    java.awt.SystemColor.inactiveCaptionText,
                    java.awt.SystemColor.activeCaptionText,
                    java.awt.SystemColor.activeCaptionText,
                    java.awt.SystemColor.inactiveCaptionText);
            rootWindow.getRootWindowProperties().getDragRectangleShapedPanelProperties().setComponentPainter(x);
            rootWindow.getRootWindowProperties()
                    .getViewProperties()
                    .getViewTitleBarProperties()
                    .getNormalProperties()
                    .getShapedPanelProperties()
                    .setComponentPainter(new GradientComponentPainter(
                            new Color(124, 160, 221),
                            new Color(236, 233, 216),
                            new Color(124, 160, 221),
                            new Color(236, 233, 216)));

            // Inserting Docking Window functionalty (Sebastian) 24.07.07
            // TODO UGLY PERHAPS CENTRAL HANDLer FOR THE CREATION OF CONFIGURATION
            final File verdisDir = new File(DIRECTORYPATH_VERDIS);
            if (!verdisDir.exists()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Verdis Directory angelegt");
                }
                verdisDir.mkdir();
            }

            panMain.add(rootWindow);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Crossover: starte server.");
            }
            initCrossoverServer(CidsAppBackend.getInstance().getAppPreferences().getVerdisCrossoverPort());
        } catch (final Throwable t) {
            LOG.error("Fehler im Konstruktor", t);
        }

        final MappingComponent mainMap = CidsAppBackend.getInstance().getMainMap();
        configurationManager.configure(mappingModel);
        mainMap.preparationSetMappingModel(mappingModel);
        ((NewSimpleInternalLayerWidget)mainMap.getInternalWidget(MappingComponent.LAYERWIDGET)).setMappingModel(
            mappingModel);
        configurationManager.configure(mainMap);
        mainMap.setMappingModel(mappingModel);
        kartenPanel.changeSelectedButtonAccordingToInteractionMode();

        mainMap.unlock();

        // CustomFeatureInfo
        final CustomFeatureInfoListener cfil = (CustomFeatureInfoListener)mainMap.getInputListener(
                MappingComponent.CUSTOM_FEATUREINFO);
        cfil.setFeatureInforetrievalUrl(CidsAppBackend.getInstance().getAppPreferences().getAlbUrl());

        final File dotverdisDir = new File(DIRECTORYPATH_VERDIS);
        dotverdisDir.mkdir();

        initClipboards();

        configurationManager.configure(this);

        // Piccolo Listener
        PNotificationCenter.defaultCenter()
                .addListener(
                    kartenPanel,
                    "coordinatesChanged",
                    SimpleMoveListener.COORDINATES_CHANGED,
                    getMappingComponent().getInputListener(MappingComponent.MOTION));
        PNotificationCenter.defaultCenter()
                .addListener(
                    kartenPanel,
                    "selectionChanged",
                    SelectionListener.SELECTION_CHANGED_NOTIFICATION,
                    getMappingComponent().getInputListener(MappingComponent.SELECT));
        PNotificationCenter.defaultCenter()
                .addListener(
                    kartenPanel,
                    "doubleClickPerformed",
                    SelectionListener.DOUBLECLICK_POINT_NOTIFICATION,
                    getMappingComponent().getInputListener(MappingComponent.SELECT));
        PNotificationCenter.defaultCenter()
                .addListener(
                    kartenPanel,
                    "selectionChanged",
                    FeatureMoveListener.SELECTION_CHANGED_NOTIFICATION,
                    getMappingComponent().getInputListener(MappingComponent.MOVE_POLYGON));
        PNotificationCenter.defaultCenter()
                .addListener(
                    kartenPanel,
                    "selectionChanged",
                    SplitPolygonListener.SELECTION_CHANGED,
                    getMappingComponent().getInputListener(MappingComponent.SPLIT_POLYGON));
        PNotificationCenter.defaultCenter()
                .addListener(
                    this,
                    "attachFeatureRequested",
                    AttachFeatureListener.ATTACH_FEATURE_NOTIFICATION,
                    getMappingComponent().getInputListener(MappingComponent.ATTACH_POLYGON_TO_ALPHADATA));
        PNotificationCenter.defaultCenter()
                .addListener(
                    kartenPanel,
                    "splitPolygon",
                    SplitPolygonListener.SPLIT_FINISHED,
                    getMappingComponent().getInputListener(MappingComponent.SPLIT_POLYGON));
        PNotificationCenter.defaultCenter()
                .addListener(
                    kartenPanel,
                    "joinPolygons",
                    JoinPolygonsListener.FEATURE_JOIN_REQUEST_NOTIFICATION,
                    getMappingComponent().getInputListener(MappingComponent.JOIN_POLYGONS));

        initValidator();

        initGeomServerSearches();

        initVeranlagung();

        addWindowListener(loadLayoutWhenOpenedAdapter);

        initTotd();
        initStartupHooks();

        isInit = false;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public KartenPanel getKartenPanel() {
        return kartenPanel;
    }

    /**
     * DOCUMENT ME!
     */
    private void initTotd() {
        try {
            if (SessionManager.getConnection().hasConfigAttr(
                            SessionManager.getSession().getUser(),
                            "csm://"
                            + MotdWundaStartupHook.MOTD_MESSAGE_TOTD)) {
                CidsServerMessageNotifier.getInstance()
                        .subscribe(new CidsServerMessageNotifierListener() {

                                @Override
                                public void messageRetrieved(final CidsServerMessageNotifierListenerEvent event) {
                                    try {
                                        final String totd = (String)event.getMessage().getContent();
                                        Main.this.totd = totd;
                                        refreshTitle();
                                    } catch (final Exception ex) {
                                        LOG.warn(ex, ex);
                                    }
                                }
                            },
                            MotdWundaStartupHook.MOTD_MESSAGE_TOTD);
            }
        } catch (final ConnectionException ex) {
            LOG.warn("Konnte Rechte an csm://" + MotdWundaStartupHook.MOTD_MESSAGE_TOTD
                        + " nicht abfragen. Keine Titleleiste des Tages !",
                ex);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void initStartupHooks() {
        try {
            new MotdStartUpHook().applicationStarted();
            new CidsServerMessageStartUpHook().applicationStarted();
        } catch (Exception ex) {
            LOG.error("Fehler beim Ausführen der StartupHooks: ", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   frame  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public void initComponentRegistry(final JFrame frame) throws Exception {
        PropertyManager.getManager().setEditable(true);

        searchResultsTree = new SearchResultsTree() {

                @Override
                public void setResultNodes(final Node[] nodes,
                        final boolean append,
                        final PropertyChangeListener listener,
                        final boolean simpleSort,
                        final boolean sortActive) {
                    super.setResultNodes(nodes, append, listener, simpleSort, false);
                }
            };

        descriptionPane = new DescriptionPaneFS();
        final MutableToolBar toolBar = new MutableToolBar();
        final MutableMenuBar menuBar = new MutableMenuBar();
        final LayoutedContainer container = new LayoutedContainer(toolBar, menuBar, true);
        final AttributeViewer attributeViewer = new AttributeViewer();
        final AttributeEditor attributeEditor = new AttributeEditor();
        final MutablePopupMenu popupMenu = new MutablePopupMenu();

        final RootTreeNode rootTreeNode = new RootTreeNode(new Node[0]);
        final MetaCatalogueTree metaCatalogueTree = new MetaCatalogueTree(
                rootTreeNode,
                PropertyManager.getManager().isEditable(),
                true,
                PropertyManager.getManager().getMaxConnections());

        final CatalogueSelectionListener catalogueSelectionListener = new CatalogueSelectionListener(
                attributeViewer,
                descriptionPane);
        searchResultsTree.addTreeSelectionListener(catalogueSelectionListener);

        ComponentRegistry.registerComponents(
            frame,
            container,
            menuBar,
            toolBar,
            popupMenu,
            metaCatalogueTree,
            searchResultsTree,
            null,
            attributeViewer,
            attributeEditor,
            descriptionPane);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  nodes  DOCUMENT ME!
     */
    public void showRenderer(final Node[] nodes) {
        try {
            alkisRendererDialog.setNodes(nodes);
        } catch (Exception ex) {
            // TODO fehlerdialog
            LOG.error("error while loading renderer", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  readonly  DOCUMENT ME!
     */
    public void setReadonly(final boolean readonly) {
        this.readonly = readonly;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  metaObjects  DOCUMENT ME!
     */
    public void showRenderer(final MetaObject[] metaObjects) {
        try {
            final List<MetaObjectNode> mons = new ArrayList<>();
            for (final MetaObject metaObject : metaObjects) {
                mons.add(new MetaObjectNode(
                        metaObject.getDomain(),
                        metaObject.getId(),
                        metaObject.getClassID(),
                        "bla bli blubb",
                        null,
                        null));
            }
            final DescriptionPane descPane = ComponentRegistry.getRegistry().getDescriptionPane();
            descPane.gotoMetaObjectNodes(mons.toArray(new MetaObjectNode[0]));
            if (!alkisRendererDialog.isVisible()) {
                StaticSwingTools.showDialog(alkisRendererDialog);
            }
        } catch (Exception ex) {
            // TODO fehlerdialog
            LOG.error("error while loading renderer", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isRendererVisible() {
        return alkisRendererDialog.isVisible();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  metaObject  DOCUMENT ME!
     */
    public void showRenderer(final MetaObject metaObject) {
        try {
            final DescriptionPane descPane = ComponentRegistry.getRegistry().getDescriptionPane();
            descPane.gotoMetaObjectNode(new MetaObjectNode(metaObject.getBean()));
            if (!alkisRendererDialog.isVisible()) {
                StaticSwingTools.showDialog(alkisRendererDialog);
            }
        } catch (final Exception ex) {
            // TODO fehlerdialog
            LOG.error("error while loading renderer", ex);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void initGeomServerSearches() {
        final ServerSearchCreateSearchGeometryListener kassenzeichenSearchGeometryListener =
            new ServerSearchCreateSearchGeometryListener(CidsAppBackend.getInstance().getMainMap(),
                new KassenzeichenGeomSearch());
        CidsAppBackend.getInstance()
                .getMainMap()
                .addCustomInputListener(KASSENZEICHEN_SEARCH_GEOMETRY_LISTENER, kassenzeichenSearchGeometryListener);
        CidsAppBackend.getInstance()
                .getMainMap()
                .putCursor(KASSENZEICHEN_SEARCH_GEOMETRY_LISTENER, new Cursor(Cursor.CROSSHAIR_CURSOR));
        kassenzeichenSearchGeometryListener.addPropertyChangeListener(new PropertyChangeListener() {

                @Override
                public void propertyChange(final PropertyChangeEvent evt) {
                    if (evt.getPropertyName().equals(ServerSearchCreateSearchGeometryListener.ACTION_SEARCH_STARTED)) {
                        kassenzeichenListPanel.searchStarted();
                    } else if (evt.getPropertyName().equals(
                                    ServerSearchCreateSearchGeometryListener.ACTION_SEARCH_DONE)) {
                        final Collection<Integer> kassenzeichenNummern = (Collection<Integer>)evt.getNewValue();
                        kassenzeichenListPanel.searchFinished(kassenzeichenNummern);
                    } else if (evt.getPropertyName().equals(
                                    ServerSearchCreateSearchGeometryListener.ACTION_SEARCH_FAILED)) {
                        final Exception ex = (Exception)evt.getNewValue();
                        LOG.error("error while searching kassenzeichen", ex);
                        final String exMessage = ex.getMessage();
                        kassenzeichenListPanel.searchFailed(exMessage);
                    }
                }
            });

        ComponentRegistry.getRegistry().getSearchResultsTree().addResultNodeListener(new ResultNodeListener() {

                @Override
                public void resultNodesChanged() {
                    if (!alkisRendererDialog.isVisible()) {
                        StaticSwingTools.showDialog(alkisRendererDialog);
                    }
                }

                @Override
                public void resultNodesCleared() {
                }

                @Override
                public void resultNodesFiltered() {
                }
            });

        final PropertyChangeListener propChangeListener = new PropertyChangeListener() {

                @Override
                public void propertyChange(final PropertyChangeEvent evt) {
                    final String propName = evt.getPropertyName();

                    if (NodesSearchCreateSearchGeometryListener.ACTION_SEARCH_STARTED.equals(propName)) {
                        ComponentRegistry.getRegistry().getSearchResultsTree().clear();
                    } else if (NodesSearchCreateSearchGeometryListener.ACTION_SEARCH_DONE.equals(propName)) {
                        final Node[] nodes = (Node[])evt.getNewValue();
                        if ((nodes == null) || (nodes.length == 0)) {
                            JOptionPane.showMessageDialog(
                                Main.this,
                                "<html>Es wurden in dem markierten Bereich<br/>keine Objekte gefunden.",
                                "Keine Risse gefunden.",
                                JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            Main.getInstance().showRenderer(nodes);
                        }
                    } else if (NodesSearchCreateSearchGeometryListener.ACTION_SEARCH_FAILED.equals(propName)) {
                        LOG.error("error while searching", (Exception)evt.getNewValue());
                    }
                }
            };

        final FlurstueckNodesSearchCreateSearchGeometryListener flurstueckCreateSearchGeomListener =
            new FlurstueckNodesSearchCreateSearchGeometryListener(CidsAppBackend.getInstance().getMainMap(),
                propChangeListener,
                ConnectionContext.createDeprecated());
        CidsAppBackend.getInstance()
                .getMainMap()
                .addCustomInputListener(
                    FlurstueckNodesSearchCreateSearchGeometryListener.NAME,
                    flurstueckCreateSearchGeomListener);
        CidsAppBackend.getInstance()
                .getMainMap()
                .putCursor(FlurstueckNodesSearchCreateSearchGeometryListener.NAME, new Cursor(Cursor.CROSSHAIR_CURSOR));
        flurstueckCreateSearchGeomListener.setMode(CreateGeometryListener.POINT);

        final RissNodesSearchCreateSearchGeometryListener rissCreateSearchGeomListener =
            new RissNodesSearchCreateSearchGeometryListener(CidsAppBackend.getInstance().getMainMap(),
                propChangeListener,
                ConnectionContext.createDeprecated());
        CidsAppBackend.getInstance()
                .getMainMap()
                .addCustomInputListener(RissNodesSearchCreateSearchGeometryListener.NAME,
                    rissCreateSearchGeomListener);
        CidsAppBackend.getInstance()
                .getMainMap()
                .putCursor(RissNodesSearchCreateSearchGeometryListener.NAME, new Cursor(Cursor.CROSSHAIR_CURSOR));
        rissCreateSearchGeomListener.setMode(CreateGeometryListener.POINT);

        final BaulastblattNodesSearchCreateSearchGeometryListener baulastblattCreateSearchGeomListener =
            new BaulastblattNodesSearchCreateSearchGeometryListener(CidsAppBackend.getInstance().getMainMap(),
                propChangeListener,
                ConnectionContext.createDeprecated());
        CidsAppBackend.getInstance()
                .getMainMap()
                .addCustomInputListener(
                    BaulastblattNodesSearchCreateSearchGeometryListener.NAME,
                    baulastblattCreateSearchGeomListener);
        CidsAppBackend.getInstance()
                .getMainMap()
                .putCursor(
                    BaulastblattNodesSearchCreateSearchGeometryListener.NAME,
                    new Cursor(Cursor.CROSSHAIR_CURSOR));
        baulastblattCreateSearchGeomListener.setMode(CreateGeometryListener.POINT);

        final AssignLandparcelGeomSearch assignLandparcelGeomSearch = new AssignLandparcelGeomSearch();
        final String assignLandparcelGeomCrs = assignLandparcelGeomSearch.getCrs();
        final ServerSearchCreateSearchGeometryListener kassenzeichenGeomtrieAssignGeometryListener =
            new ServerSearchCreateSearchGeometryListener(CidsAppBackend.getInstance().getMainMap(),
                assignLandparcelGeomSearch);
        CidsAppBackend.getInstance()
                .getMainMap()
                .addCustomInputListener(
                    KASSENZEICHEN_GEOMETRIE_ASSIGN_GEOMETRY_LISTENER,
                    kassenzeichenGeomtrieAssignGeometryListener);
        CidsAppBackend.getInstance()
                .getMainMap()
                .putCursor(KASSENZEICHEN_GEOMETRIE_ASSIGN_GEOMETRY_LISTENER, new Cursor(Cursor.CROSSHAIR_CURSOR));
        kassenzeichenGeomtrieAssignGeometryListener.setMode(CreateGeometryListener.POINT);
        kassenzeichenGeomtrieAssignGeometryListener.addPropertyChangeListener(new PropertyChangeListener() {

                @Override
                public void propertyChange(final PropertyChangeEvent evt) {
                    final String propName = evt.getPropertyName();

                    if (ServerSearchCreateSearchGeometryListener.ACTION_SEARCH_STARTED.equals(propName)) {
                    } else if (ServerSearchCreateSearchGeometryListener.ACTION_SEARCH_DONE.equals(propName)) {
                        if ((getCidsBean() != null) && CidsAppBackend.getInstance().isEditable()) {
                            final Collection data = (Collection)evt.getNewValue();
                            if ((data == null) || data.isEmpty()) {
                                JOptionPane.showMessageDialog(
                                    Main.this,
                                    "<html>Es wurden in dem markierten Bereich<br/>keine Flurstücke gefunden.",
                                    "Keine FLurstücke gefunden.",
                                    JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                final Iterator iterator = data.iterator();
                                while (iterator.hasNext()) {
                                    final Geometry geometry = (Geometry)iterator.next();
                                    final String bezeichnung = (String)iterator.next();
                                    if (geometry != null) {
                                        try {
                                            geometry.setSRID(
                                                CrsTransformer.extractSridFromCrs(assignLandparcelGeomCrs));
                                            final Geometry transformedGeom = CrsTransformer.transformToCurrentCrs(
                                                    geometry);
                                            transformedGeom.setSRID(CrsTransformer.getCurrentSrid());

                                            final CidsBean kassenzeichenGeometrieBean =
                                                kassenzeichenGeometrienPanel.createNewKassenzeichenGeometrieBean(
                                                    transformedGeom,
                                                    bezeichnung,
                                                    false);
                                            kassenzeichenGeometrienPanel.getKassenzeichenGeometrienList()
                                                    .addKassenzeichenGeometrieBean(
                                                        kassenzeichenGeometrieBean);
                                        } catch (Exception ex) {
                                            LOG.fatal("", ex);
                                        }
                                    }
                                }
                            }
                        } else if (ServerSearchCreateSearchGeometryListener.ACTION_SEARCH_FAILED.equals(propName)) {
                            LOG.error("error while searching alkis landparcel", (Exception)evt.getNewValue());
                        }
                    }
                }
            });
    }

    /**
     * DOCUMENT ME!
     *
     * @param  notification  DOCUMENT ME!
     */
    public void attachFeatureRequested(final PNotification notification) {
        switch (currentMode) {
            case SR: {
                getSRFrontenTable().attachFeatureRequested(notification);
            }
            break;
            case REGEN: {
                getRegenFlaechenTable().attachFeatureRequested(notification);
            }
            break;
            case KANALDATEN: {
                getBefreiungerlaubnisGeometrieTable().attachFeatureRequested(notification);
            }
            break;
            case ALLGEMEIN: {
                kassenzeichenGeometrienPanel.attachFeatureRequested(notification);
            }
            break;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public RegenFlaechenTable getRegenFlaechenTable() {
        return (RegenFlaechenTable)regenFlaechenTablePanel.getTable();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public SRFrontenTable getSRFrontenTable() {
        return (SRFrontenTable)srFrontenTablePanel.getTable();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public SRFrontenDetailsPanel getSRFrontenDetailsPanel() {
        return srFrontenDetailsPanel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public BefreiungerlaubnisGeometrieDetailsPanel getBefreiungerlaubnisGeometrieDetailsPanel() {
        return befreiungerlaubnisGeometrieDetailsPanel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public BefreiungerlaubnisGeometrieTable getBefreiungerlaubnisGeometrieTable() {
        return (BefreiungerlaubnisGeometrieTable)befreiungerlaubnisGeometrieTablePanel.getTable();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public KanaldatenPanel getKanaldatenPanel() {
        return kanaldatenPanel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public BefreiungerlaubnisTable getBefreiungerlaubnisTable() {
        return (BefreiungerlaubnisTable)getKanaldatenPanel().getBefreiungerlaubnisTablePanel().getTable();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Main getInstance() {
        return LazyInitialiser.INSTANCE;
    }

    /**
     * Inserting Docking Window functionalty (Sebastian) 24.07.07.
     */
    private void setupDefaultLayout() {
        if (currentMode != null) {
            if (currentMode.equals(CidsAppBackend.Mode.ALLGEMEIN)) {
                setupDefaultLayoutInfo();
            } else if (currentMode.equals(CidsAppBackend.Mode.SR)) {
                setupDefaultLayoutSR();
            } else {
                setupDefaultLayoutRegen();
            }
        } else {
            CidsAppBackend.getInstance().setMode(CidsAppBackend.Mode.ALLGEMEIN);
        }
    }

    /**
     * DOCUMENT ME!
     */
    @Override
    public void appModeChanged() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));

        if ((currentMode != null) && !isInit) {
            saveLayout(FILEPATH_LAYOUT + "." + currentMode.name());
            saveConfig(FILEPATH_MAP + "." + currentMode.name());
        }

        final CidsAppBackend.Mode mode = CidsAppBackend.getInstance().getMode();
        if (!isInit) {
            switch (mode) {
                case ALLGEMEIN: {
                    setupLayoutInfo();
                }
                break;
                case SR: {
                    setupLayoutSR();
                }
                break;
                case REGEN: {
                    setupLayoutRegen();
                }
                break;
                case KANALDATEN: {
                    setupLayoutKanaldaten();
                }
                break;
            }
        }
        currentMode = mode;
        refreshClipboardButtons();
        refreshKassenzeichenButtons();
        refreshClipboardButtonsToolTipText();
        refreshItemButtons();
        final ModeLayer ml = ModeLayerRegistry.getInstance().getModeLayer("verdisAppModeLayer");
        if (ml != null) {
            ml.forceMode(mode.toString());
        }
        setupMap(currentMode);

        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * DOCUMENT ME!
     *
     * @param  appMode  DOCUMENT ME!
     */
    public void setupMap(final CidsAppBackend.Mode appMode) {
        final String fileName = FILEPATH_MAP + "." + appMode.name();
        final TreeMap ms = mappingModel.getMapServices();
        try {
            if (new File(fileName).exists()) {
//                configurationManager.configure(mappingModel, fileName);
                final SAXBuilder builder = new SAXBuilder(false);
                final Document doc = builder.build(new File(fileName));

                final Element rootObject = doc.getRootElement();
                final Element conf = rootObject.getChild("cismapActiveLayerConfiguration"); // NOI18N
                final Element layerElement = conf.getChild("Layers");
                final Element[] orderedLayers = CidsLayerFactory.orderLayers(layerElement);
                int counter = 0;
                for (final Element element : orderedLayers) {
                    boolean visible = true;
                    boolean enabled = true;
                    double translucency = 1.0d;

                    try {
                        visible = element.getAttribute("visible").getBooleanValue();
                    } catch (final Exception skip) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Setting default value for visible", skip);
                        }
                    }
                    try {
                        enabled = element.getAttribute("enabled").getBooleanValue();
                    } catch (final Exception skip) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Setting default value for enabled", skip);
                        }
                    }
                    try {
                        translucency = element.getAttribute("translucency").getDoubleValue();
                    } catch (final Exception skip) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Setting default value for translucency", skip);
                        }
                    }
                    if (ms.get(counter) instanceof ModeLayer) {
                        ((ModeLayer)ms.get(counter)).setVisible(visible);
                    }
                    if (ms.get(counter) instanceof PNodeProvider) {
                        ((PNodeProvider)ms.get(counter)).getPNode().setTransparency((float)translucency);
                        ((PNodeProvider)ms.get(counter)).getPNode().setVisible(visible);
                    }

                    final ActiveLayerEvent ale = new ActiveLayerEvent();
                    ale.setLayer(ms.get(counter));
                    CismapBroker.getInstance().fireLayerVisibilityChanged(ale);

                    ((ServiceLayer)ms.get(counter)).setEnabled(enabled);
                    ((ServiceLayer)ms.get(counter)).setTranslucency((float)translucency);

                    counter++;
                }
            }
        } catch (final Exception e) {
            LOG.error("Problem beim Lesen des MapFiles " + fileName, e);
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void setupLayoutRegen() {
        final CidsAppBackend.Mode mode = CidsAppBackend.getInstance().getMode();
        final String fileName = FILEPATH_LAYOUT + "." + mode.name();
        try {
            loadLayout(fileName);
        } catch (Exception e) {
            LOG.info("Problem beim Lesen des LayoutFiles " + fileName);
            setupDefaultLayoutRegen();
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void setupLayoutKanaldaten() {
        final CidsAppBackend.Mode mode = CidsAppBackend.getInstance().getMode();
        final String fileName = FILEPATH_LAYOUT + "." + mode.name();
        try {
            loadLayout(fileName);
        } catch (Exception e) {
            LOG.info("Problem beim Lesen des LayoutFiles " + fileName);
            setupDefaultLayoutKanaldaten();
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void setupLayoutSR() {
        final CidsAppBackend.Mode mode = CidsAppBackend.getInstance().getMode();
        final String fileName = FILEPATH_LAYOUT + "." + mode.name();
        try {
            loadLayout(fileName);
        } catch (Exception e) {
            LOG.info("Problem beim Lesen des LayoutFiles " + fileName);
            setupDefaultLayoutSR();
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void setupLayoutInfo() {
        final CidsAppBackend.Mode mode = CidsAppBackend.getInstance().getMode();
        final String fileName = FILEPATH_LAYOUT + "." + mode.name();
        try {
            loadLayout(fileName);
        } catch (Exception e) {
            LOG.info("Problem beim Lesen des LayoutFiles " + fileName);
            setupDefaultLayoutInfo();
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void setupDefaultLayoutRegen() {
        EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    rootWindow.setWindow(createSplitWindow(vSummen, vTabelleRegen, vDetailsRegen));
                    rootWindow.getWindowBar(Direction.LEFT).setEnabled(true);
                    rootWindow.getWindowBar(Direction.RIGHT).setEnabled(true);
                }
            });
    }

    /**
     * DOCUMENT ME!
     */
    public void setupDefaultLayoutKanaldaten() {
        EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    rootWindow.setWindow(
                        createSplitWindow(vKanaldaten, vTabelleVersickerung, vDetailsVersickerung));
                    rootWindow.getWindowBar(Direction.LEFT).setEnabled(true);
                    rootWindow.getWindowBar(Direction.RIGHT).setEnabled(true);
                }
            });
    }

    /**
     * DOCUMENT ME!
     */
    public void setupDefaultLayoutSR() {
        EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    rootWindow.setWindow(createSplitWindow(vZusammenfassungSR, vTabelleSR, vDetailsSR));
                    rootWindow.getWindowBar(Direction.LEFT).setEnabled(true);
                    rootWindow.getWindowBar(Direction.RIGHT).setEnabled(true);
                }
            });
    }

    /**
     * DOCUMENT ME!
     */
    public void setupDefaultLayoutInfo() {
        EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    rootWindow.setWindow(createSplitWindow(vInfoAllgemein, vDetailsAllgemein));
                    rootWindow.getWindowBar(Direction.LEFT).setEnabled(true);
                    rootWindow.getWindowBar(Direction.RIGHT).setEnabled(true);
                }
            });
    }

    /**
     * DOCUMENT ME!
     *
     * @param   table    DOCUMENT ME!
     * @param   details  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private SplitWindow createMapTableDetailsSplitWindow(final View table, final View details) {
        return new SplitWindow(true, 0.66f,
                new TabWindow(new DockingWindow[] { vKarte, table }), details);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   table  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private SplitWindow createMapTableDetailsSplitWindow(final View table) {
        return new SplitWindow(true, 0.66f, vKarte, table);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   meta  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private SplitWindow createMetaInfoSplitWindow(final View meta) {
        return new SplitWindow(true, 0.5f, createKassenzeichenSplitWindow(), meta);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private SplitWindow createKassenzeichenSplitWindow() {
        return new SplitWindow(true, 0.62f, vKassenzeichen, vKassenzeichenList);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   meta     DOCUMENT ME!
     * @param   table    DOCUMENT ME!
     * @param   details  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private SplitWindow createSplitWindow(final View meta, final View table, final View details) {
        return new SplitWindow(
                false,
                0.4353147f,
                createMetaInfoSplitWindow(meta),
                createMapTableDetailsSplitWindow(table, details));
    }

    /**
     * DOCUMENT ME!
     *
     * @param   meta   DOCUMENT ME!
     * @param   table  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private SplitWindow createSplitWindow(final View meta, final View table) {
        return new SplitWindow(
                false,
                0.4353147f,
                createMetaInfoSplitWindow(meta),
                createMapTableDetailsSplitWindow(table));
    }

    /**
     * DOCUMENT ME!
     *
     * @param  crossoverServerPort  DOCUMENT ME!
     */
    private void initCrossoverServer(final int crossoverServerPort) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Crossover: initCrossoverServer");
        }
        final int defaultServerPort = 8888;
        boolean defaultServerPortUsed = false;
        try {
            if ((crossoverServerPort < 0) || (crossoverServerPort > 65535)) {
                LOG.warn("Crossover: Invalid Crossover serverport: " + crossoverServerPort
                            + ". Going to use default port: " + defaultServerPort);
                defaultServerPortUsed = true;
                RESTRemoteControlStarter.initRestRemoteControlMethods(defaultServerPort);
            } else {
                RESTRemoteControlStarter.initRestRemoteControlMethods(crossoverServerPort);
            }
        } catch (Exception ex) {
            LOG.error("Crossover: Error while creating crossover server on port: " + crossoverServerPort, ex);
            if (!defaultServerPortUsed) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Crossover: Trying to create server with defaultPort: " + defaultServerPort);
                }
                defaultServerPortUsed = true;
                try {
                    RESTRemoteControlStarter.initRestRemoteControlMethods(defaultServerPort);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Crossover: Server started at port: " + defaultServerPort, ex);
                    }
                } catch (Exception ex1) {
                    LOG.error("Crossover: Failed to initialize Crossover server on defaultport: " + defaultServerPort
                                + ". No Server is started",
                        ex);
                    cmdLagisCrossover.setEnabled(false);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static AppPreferences loadAppPreferences() {
        // Read properties file.
        return new AppPreferences(Main.class.getResourceAsStream("/verdis2properties.xml"));
    }

    /**
     * DOCUMENT ME!
     *
     * @param  b  DOCUMENT ME!
     */
    @Override
    public void setEnabled(final boolean b) {
        kassenzeichenPanel.setEnabled(b);
    }

    /**
     * DOCUMENT ME!
     */
    public void refreshLeftTitleBarColor() {
        if (editMode) {
            setLeftTitleBarColor(Color.red);
        } else if (kassenzeichenPanel.isLocked()) {
            setLeftTitleBarColor(Color.orange);
        } else {
            setLeftTitleBarColor(myBlue);
        }
    }

    /**
     * Inserting Docking Window functionalty (Sebastian) 24.07.07 former all components are signaled to change the color
     * Now the docking framework will do that.
     *
     * @param  c  DOCUMENT ME!
     */
    public void setLeftTitleBarColor(final Color c) {
        if (!isInit) {
            rootWindow.getRootWindowProperties()
                    .getViewProperties()
                    .getViewTitleBarProperties()
                    .getNormalProperties()
                    .getShapedPanelProperties()
                    .setComponentPainter(new GradientComponentPainter(
                            c,
                            new Color(236, 233, 216),
                            c,
                            new Color(236, 233, 216)));
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        cmdTest = new javax.swing.JButton();
        cmdTest2 = new javax.swing.JButton();
        simpleMemoryMonitoringToolbarWidget1 = new de.cismet.cids.navigator.utils.SimpleMemoryMonitoringToolbarWidget();
        jPanel1 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        cmdMemory = new javax.swing.JButton();
        exceptionNotificationStatusPanel =
            new de.cismet.tools.gui.exceptionnotification.ExceptionNotificationStatusPanel();
        tobVerdis = new javax.swing.JToolBar();
        cmdEditMode = new javax.swing.JButton();
        cmdCancel = new javax.swing.JButton();
        cmdOk = new javax.swing.JButton();
        cmdDeleteKassenzeichen = new javax.swing.JButton();
        cmdNewKassenzeichen = new javax.swing.JButton();
        cmdNextKassenzeichenWithoutGeom = new javax.swing.JButton();
        cmdSAPCheck = new javax.swing.JToggleButton();
        jPanel2 = new javax.swing.JPanel();
        jSeparator6 = new javax.swing.JSeparator();
        cmdCut = new javax.swing.JButton();
        cmdCopy = new javax.swing.JButton();
        cmdPaste = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jSeparator14 = new javax.swing.JSeparator();
        cmdRefreshEnumeration = new javax.swing.JButton();
        cmdRecalculateArea = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jSeparator15 = new javax.swing.JSeparator();
        cmdAdd = new javax.swing.JButton();
        cmdRemove = new javax.swing.JButton();
        cmdUndo = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jSeparator16 = new javax.swing.JSeparator();
        cmdPdf = new javax.swing.JButton();
        cmdWorkflow = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jSeparator17 = new javax.swing.JSeparator();
        cmdInfo = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jSeparator19 = new javax.swing.JSeparator();
        btnHistory = new javax.swing.JButton();
        btnTimeRecovery = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jSeparator20 = new javax.swing.JSeparator();
        cmdLagisCrossover = new javax.swing.JButton();
        cmdDownloads = new javax.swing.JButton();
        cmdFortfuehrung = new javax.swing.JButton();
        cmdGrundbuchblattSuche = new javax.swing.JButton();
        cmdArbeitspakete = new javax.swing.JButton();
        cmdAbfrageeditor = new javax.swing.JButton();
        cmdVeranlagungsdatei = new javax.swing.JButton();
        cmdOpenInD3 = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jSeparator18 = new javax.swing.JSeparator();
        cmdSearchBaulasten = new javax.swing.JButton();
        cmdSearchRisse = new javax.swing.JButton();
        panMain = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        menFile = new javax.swing.JMenu();
        jSeparator9 = new javax.swing.JSeparator();
        mniSaveLayout = new javax.swing.JMenuItem();
        mniLoadLayout = new javax.swing.JMenuItem();
        jSeparator10 = new javax.swing.JSeparator();
        mniClose = new javax.swing.JMenuItem();
        menEdit = new javax.swing.JMenu();
        mnuNewKassenzeichen = new javax.swing.JMenuItem();
        mnuRenameCurrentKZ = new javax.swing.JMenuItem();
        mnuRenameAnyKZ = new javax.swing.JMenuItem();
        menExtras = new javax.swing.JMenu();
        mniOptions = new javax.swing.JMenuItem();
        sepOptions = new javax.swing.JPopupMenu.Separator();
        mnuChangeUser = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        menWindows = new javax.swing.JMenu();
        mniKassenzeichen = new javax.swing.JMenuItem();
        mniKassenzeichen1 = new javax.swing.JMenuItem();
        mniSummen = new javax.swing.JMenuItem();
        mniKanalanschluss = new javax.swing.JMenuItem();
        jSeparator11 = new javax.swing.JSeparator();
        mniKarte = new javax.swing.JMenuItem();
        mniTabelle = new javax.swing.JMenuItem();
        mniDetails = new javax.swing.JMenuItem();
        mniResetWindowLayout = new javax.swing.JMenuItem();
        menHelp = new javax.swing.JMenu();
        mnuHelp = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        mnuInfo = new javax.swing.JMenuItem();

        cmdTest.setText("Test ClipboardStore");
        cmdTest.setFocusable(false);
        cmdTest.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdTest.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdTest.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdTestActionPerformed(evt);
                }
            });

        cmdTest2.setText("Test Clipboard Load");
        cmdTest2.setFocusable(false);
        cmdTest2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdTest2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdTest2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdTest2ActionPerformed(evt);
                }
            });

        simpleMemoryMonitoringToolbarWidget1.startTimer();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(getTitle());
        addWindowListener(new java.awt.event.WindowAdapter() {

                @Override
                public void windowOpened(final java.awt.event.WindowEvent evt) {
                    formWindowOpened(evt);
                }
                @Override
                public void windowClosing(final java.awt.event.WindowEvent evt) {
                    formWindowClosing(evt);
                }
                @Override
                public void windowClosed(final java.awt.event.WindowEvent evt) {
                    formWindowClosed(evt);
                }
            });
        addKeyListener(new java.awt.event.KeyAdapter() {

                @Override
                public void keyTyped(final java.awt.event.KeyEvent evt) {
                    formKeyTyped(evt);
                }
                @Override
                public void keyPressed(final java.awt.event.KeyEvent evt) {
                    formKeyPressed(evt);
                }
                @Override
                public void keyReleased(final java.awt.event.KeyEvent evt) {
                    formKeyReleased(evt);
                }
            });

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jToolBar1.setRollover(true);

        cmdMemory.setAction(simpleMemoryMonitoringToolbarWidget1);
        cmdMemory.setFocusPainted(false);
        cmdMemory.setFocusable(false);
        cmdMemory.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdMemory.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdMemory.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdMemoryActionPerformed(evt);
                }
            });
        jToolBar1.add(cmdMemory);

        exceptionNotificationStatusPanel.setMaximumSize(new java.awt.Dimension(34, 34));
        exceptionNotificationStatusPanel.setMinimumSize(new java.awt.Dimension(34, 34));
        exceptionNotificationStatusPanel.setPreferredSize(new java.awt.Dimension(34, 34));
        jToolBar1.add(exceptionNotificationStatusPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel1.add(jToolBar1, gridBagConstraints);

        tobVerdis.setRollover(true);
        tobVerdis.setAlignmentY(0.48387095F);
        tobVerdis.setMaximumSize(new java.awt.Dimension(679, 32769));
        tobVerdis.setMinimumSize(new java.awt.Dimension(667, 33));
        tobVerdis.setPreferredSize(new java.awt.Dimension(691, 35));

        cmdEditMode.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/editmode.png"))); // NOI18N
        cmdEditMode.setToolTipText("Editormodus");
        cmdEditMode.setEnabled(false);
        cmdEditMode.setFocusPainted(false);
        cmdEditMode.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdEditModeActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdEditMode);

        cmdCancel.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/cancel.png"))); // NOI18N
        cmdCancel.setToolTipText("Änderungen abbrechen");
        cmdCancel.setFocusPainted(false);
        cmdCancel.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdCancelActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdCancel);

        cmdOk.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/ok.png"))); // NOI18N
        cmdOk.setToolTipText("Änderungen annehmen");
        cmdOk.setFocusPainted(false);
        cmdOk.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdOkActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdOk);

        cmdDeleteKassenzeichen.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/deleteKassenzeichen.png"))); // NOI18N
        cmdDeleteKassenzeichen.setToolTipText("Kassenzeichen löschen");
        cmdDeleteKassenzeichen.setEnabled(false);
        cmdDeleteKassenzeichen.setFocusPainted(false);
        cmdDeleteKassenzeichen.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdDeleteKassenzeichenActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdDeleteKassenzeichen);

        cmdNewKassenzeichen.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/newKassenzeichen.png"))); // NOI18N
        cmdNewKassenzeichen.setToolTipText("Neues Kassenzeichen");
        cmdNewKassenzeichen.setFocusPainted(false);
        cmdNewKassenzeichen.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdNewKassenzeichenActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdNewKassenzeichen);

        cmdNextKassenzeichenWithoutGeom.setIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/de/cismet/verdis/res/images/toolbar/next_kassenzeichen_without_geometries.png"))); // NOI18N
        cmdNextKassenzeichenWithoutGeom.setToolTipText("Nächstes Kassenzeichen ohne allgemeine Geometrien");
        cmdNextKassenzeichenWithoutGeom.setFocusable(false);
        cmdNextKassenzeichenWithoutGeom.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdNextKassenzeichenWithoutGeom.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdNextKassenzeichenWithoutGeom.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdNextKassenzeichenWithoutGeomActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdNextKassenzeichenWithoutGeom);

        cmdSAPCheck.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/goto_kassenzeichen_sap.png")));          // NOI18N
        cmdSAPCheck.setToolTipText("SAP Check");
        cmdSAPCheck.setContentAreaFilled(false);
        cmdSAPCheck.setFocusable(false);
        cmdSAPCheck.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdSAPCheck.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/goto_kassenzeichen_sap_selected.png"))); // NOI18N
        cmdSAPCheck.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdSAPCheck.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdSAPCheckActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdSAPCheck);

        jPanel2.setMaximumSize(new java.awt.Dimension(2, 38));
        jPanel2.setMinimumSize(new java.awt.Dimension(2, 38));
        jPanel2.setPreferredSize(new java.awt.Dimension(2, 38));
        jPanel2.setLayout(new java.awt.BorderLayout());

        jSeparator6.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator6.setMaximumSize(new java.awt.Dimension(2, 32767));
        jPanel2.add(jSeparator6, java.awt.BorderLayout.CENTER);

        tobVerdis.add(jPanel2);

        cmdCut.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/cutFl.png"))); // NOI18N
        cmdCut.setToolTipText("Fläche ausschneiden");
        cmdCut.setEnabled(false);
        cmdCut.setFocusPainted(false);
        cmdCut.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdCutActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdCut);

        cmdCopy.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/copyFl.png"))); // NOI18N
        cmdCopy.setToolTipText("Fläche kopieren (Teileigentum erzeugen)");
        cmdCopy.setEnabled(false);
        cmdCopy.setFocusPainted(false);
        cmdCopy.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdCopyActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdCopy);

        cmdPaste.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/pasteFl.png"))); // NOI18N
        cmdPaste.setToolTipText("Fläche einfügen");
        cmdPaste.setEnabled(false);
        cmdPaste.setFocusPainted(false);
        cmdPaste.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdPasteActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdPaste);

        jPanel3.setMaximumSize(new java.awt.Dimension(2, 38));
        jPanel3.setMinimumSize(new java.awt.Dimension(2, 38));
        jPanel3.setPreferredSize(new java.awt.Dimension(2, 38));
        jPanel3.setLayout(new java.awt.BorderLayout());

        jSeparator14.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator14.setMaximumSize(new java.awt.Dimension(2, 32767));
        jPanel3.add(jSeparator14, java.awt.BorderLayout.CENTER);

        tobVerdis.add(jPanel3);

        cmdRefreshEnumeration.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/refreshEnum.png"))); // NOI18N
        cmdRefreshEnumeration.setToolTipText("Alle Flächen neu nummerieren");
        cmdRefreshEnumeration.setEnabled(false);
        cmdRefreshEnumeration.setFocusPainted(false);
        cmdRefreshEnumeration.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdRefreshEnumerationActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdRefreshEnumeration);

        cmdRecalculateArea.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/recalculateArea.png"))); // NOI18N
        cmdRecalculateArea.setToolTipText("Neuberechnung der Flächen");
        cmdRecalculateArea.setFocusPainted(false);
        cmdRecalculateArea.setFocusable(false);
        cmdRecalculateArea.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdRecalculateArea.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdRecalculateArea.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdRecalculateAreaActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdRecalculateArea);

        jPanel4.setMaximumSize(new java.awt.Dimension(2, 38));
        jPanel4.setMinimumSize(new java.awt.Dimension(2, 38));
        jPanel4.setPreferredSize(new java.awt.Dimension(2, 38));
        jPanel4.setLayout(new java.awt.BorderLayout());

        jSeparator15.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator15.setMaximumSize(new java.awt.Dimension(2, 32767));
        jPanel4.add(jSeparator15, java.awt.BorderLayout.CENTER);

        tobVerdis.add(jPanel4);

        cmdAdd.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/add.png")));  // NOI18N
        cmdAdd.setEnabled(false);
        cmdAdd.setFocusable(false);
        cmdAdd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdAdd.setRolloverIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/add2.png"))); // NOI18N
        cmdAdd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdAdd.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdAddActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdAdd);

        cmdRemove.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/remove.png")));  // NOI18N
        cmdRemove.setEnabled(false);
        cmdRemove.setFocusable(false);
        cmdRemove.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdRemove.setRolloverIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/remove2.png"))); // NOI18N
        cmdRemove.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdRemove.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdRemoveActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdRemove);

        cmdUndo.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/undo.png")));  // NOI18N
        cmdUndo.setEnabled(false);
        cmdUndo.setFocusable(false);
        cmdUndo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdUndo.setRolloverIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/undo2.png"))); // NOI18N
        cmdUndo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdUndo.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdUndoActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdUndo);

        jPanel5.setMaximumSize(new java.awt.Dimension(2, 38));
        jPanel5.setMinimumSize(new java.awt.Dimension(2, 38));
        jPanel5.setPreferredSize(new java.awt.Dimension(2, 38));
        jPanel5.setLayout(new java.awt.BorderLayout());

        jSeparator16.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator16.setMaximumSize(new java.awt.Dimension(2, 32767));
        jPanel5.add(jSeparator16, java.awt.BorderLayout.CENTER);

        tobVerdis.add(jPanel5);

        cmdPdf.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/pdf.png"))); // NOI18N
        cmdPdf.setToolTipText("Drucken");
        cmdPdf.setFocusPainted(false);
        cmdPdf.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdPdfActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdPdf);

        cmdWorkflow.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/workflow.png"))); // NOI18N
        cmdWorkflow.setToolTipText("Workflow");
        cmdWorkflow.setEnabled(false);
        cmdWorkflow.setFocusPainted(false);
        cmdWorkflow.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdWorkflowActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdWorkflow);

        jPanel6.setMaximumSize(new java.awt.Dimension(2, 38));
        jPanel6.setMinimumSize(new java.awt.Dimension(2, 38));
        jPanel6.setPreferredSize(new java.awt.Dimension(2, 38));
        jPanel6.setLayout(new java.awt.BorderLayout());

        jSeparator17.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator17.setMaximumSize(new java.awt.Dimension(2, 32767));
        jPanel6.add(jSeparator17, java.awt.BorderLayout.CENTER);

        tobVerdis.add(jPanel6);

        cmdInfo.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/info.png"))); // NOI18N
        cmdInfo.setToolTipText("Versionsanzeige");
        cmdInfo.setFocusPainted(false);
        cmdInfo.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdInfoActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdInfo);

        jPanel8.setMaximumSize(new java.awt.Dimension(2, 38));
        jPanel8.setMinimumSize(new java.awt.Dimension(2, 38));
        jPanel8.setPreferredSize(new java.awt.Dimension(2, 38));
        jPanel8.setLayout(new java.awt.BorderLayout());

        jSeparator19.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator19.setMaximumSize(new java.awt.Dimension(2, 32767));
        jPanel8.add(jSeparator19, java.awt.BorderLayout.CENTER);

        tobVerdis.add(jPanel8);

        btnHistory.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/history.png"))); // NOI18N
        btnHistory.setToolTipText("öffne Kassenzeichen-Verlauf");
        btnHistory.setFocusPainted(false);
        btnHistory.setFocusable(false);
        btnHistory.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnHistory.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnHistory.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnHistoryActionPerformed(evt);
                }
            });
        tobVerdis.add(btnHistory);

        btnTimeRecovery.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/recovery.png"))); // NOI18N
        btnTimeRecovery.setToolTipText("Kassenzeichen-Wiederherstellung");
        btnTimeRecovery.setFocusPainted(false);
        btnTimeRecovery.setFocusable(false);
        btnTimeRecovery.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnTimeRecovery.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnTimeRecovery.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnTimeRecoveryActionPerformed(evt);
                }
            });
        tobVerdis.add(btnTimeRecovery);
        try {
            btnTimeRecovery.setVisible(SessionManager.getConnection().getConfigAttr(
                    SessionManager.getSession().getUser(),
                    "grundis.timerecovery.dialog") != null);
        } catch (final Exception ex) {
            LOG.error("error while checking for grundis.timerecovery.dialog", ex);
            btnTimeRecovery.setVisible(false);
        }

        jPanel9.setMaximumSize(new java.awt.Dimension(2, 38));
        jPanel9.setMinimumSize(new java.awt.Dimension(2, 38));
        jPanel9.setPreferredSize(new java.awt.Dimension(2, 38));
        jPanel9.setLayout(new java.awt.BorderLayout());

        jSeparator20.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator20.setMaximumSize(new java.awt.Dimension(2, 32767));
        jPanel9.add(jSeparator20, java.awt.BorderLayout.CENTER);

        tobVerdis.add(jPanel9);

        cmdLagisCrossover.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/lagisCrossover.png"))); // NOI18N
        cmdLagisCrossover.setToolTipText("Öffne zugehöriges Flurstück in LagIS");
        cmdLagisCrossover.setFocusPainted(false);
        cmdLagisCrossover.setFocusable(false);
        cmdLagisCrossover.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdLagisCrossover.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdLagisCrossover.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdLagisCrossoverActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdLagisCrossover);

        cmdDownloads.setAction(new DownloadManagerAction(this));
        cmdDownloads.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/download.png"))); // NOI18N
        cmdDownloads.setFocusPainted(false);
        cmdDownloads.setFocusable(false);
        cmdDownloads.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdDownloads.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tobVerdis.add(cmdDownloads);

        cmdFortfuehrung.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/fortfuehrung.png"))); // NOI18N
        cmdFortfuehrung.setToolTipText("Fortführung");
        cmdFortfuehrung.setFocusable(false);
        cmdFortfuehrung.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdFortfuehrung.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdFortfuehrung.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdFortfuehrungActionPerformed(evt);
                }
            });
        try {
            cmdFortfuehrung.setVisible(SessionManager.getConnection().getConfigAttr(
                    SessionManager.getSession().getUser(),
                    "grundis.fortfuehrungsanlaesse.dialog") != null);
        } catch (final Exception ex) {
            LOG.error("error while checking for grundis.fortfuehrungsanlaesse.dialog", ex);
            cmdFortfuehrung.setVisible(false);
        }
        tobVerdis.add(cmdFortfuehrung);

        cmdGrundbuchblattSuche.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/BPlan.png"))); // NOI18N
        cmdGrundbuchblattSuche.setToolTipText("Kassenzeichensuche über Buchungsblatt");
        cmdGrundbuchblattSuche.setFocusPainted(false);
        cmdGrundbuchblattSuche.setFocusable(false);
        cmdGrundbuchblattSuche.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdGrundbuchblattSuche.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdGrundbuchblattSuche.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdGrundbuchblattSucheActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdGrundbuchblattSuche);

        cmdArbeitspakete.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/workdone.png"))); // NOI18N
        cmdArbeitspakete.setToolTipText("Arbeitspakete verwalten");
        cmdArbeitspakete.setFocusable(false);
        cmdArbeitspakete.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdArbeitspakete.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdArbeitspakete.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdArbeitspaketeActionPerformed(evt);
                }
            });
        try {
            cmdFortfuehrung.setVisible(SessionManager.getConnection().getConfigAttr(
                    SessionManager.getSession().getUser(),
                    "grundis.fortfuehrungsanlaesse.dialog") != null);
        } catch (final Exception ex) {
            LOG.error("error while checking for grundis.fortfuehrungsanlaesse.dialog", ex);
            cmdFortfuehrung.setVisible(false);
        }
        tobVerdis.add(cmdArbeitspakete);
        try {
            cmdArbeitspakete.setVisible(SessionManager.getConnection().getConfigAttr(
                    SessionManager.getSession().getUser(),
                    "grundis.arbeitspaketemanager.dialog") != null);
        } catch (final Exception ex) {
            LOG.error("error while checking for grundis.fortfuehrungsanlaesse.dialog", ex);
            cmdArbeitspakete.setVisible(false);
        }

        cmdAbfrageeditor.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/gui/table-export.png"))); // NOI18N
        cmdAbfrageeditor.setToolTipText("Abfragen-Export (nach CSV)");
        cmdAbfrageeditor.setFocusable(false);
        cmdAbfrageeditor.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdAbfrageeditor.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdAbfrageeditor.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdAbfrageeditorActionPerformed(evt);
                }
            });
        try {
            cmdFortfuehrung.setVisible(SessionManager.getConnection().getConfigAttr(
                    SessionManager.getSession().getUser(),
                    "grundis.fortfuehrungsanlaesse.dialog") != null);
        } catch (final Exception ex) {
            LOG.error("error while checking for grundis.abfragennachcsv.dialog", ex);
            cmdFortfuehrung.setVisible(false);
        }
        tobVerdis.add(cmdAbfrageeditor);
        try {
            cmdAbfrageeditor.setVisible(SessionManager.getConnection().getConfigAttr(
                    SessionManager.getSession().getUser(),
                    "grundis.arbeitspaketemanager.dialog") != null);
        } catch (final Exception ex) {
            LOG.error("error while checking for grundis.abfragennachcsv.dialog", ex);
            cmdAbfrageeditor.setVisible(false);
        }

        cmdVeranlagungsdatei.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/gui/reports-stack.png"))); // NOI18N
        cmdVeranlagungsdatei.setToolTipText("Erzeugung Veranlagungsdatei planen");
        cmdVeranlagungsdatei.setFocusable(false);
        cmdVeranlagungsdatei.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdVeranlagungsdatei.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdVeranlagungsdatei.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdVeranlagungsdateiActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdVeranlagungsdatei);
        try {
            cmdVeranlagungsdatei.setVisible(SessionManager.getConnection().getConfigAttr(
                    SessionManager.getSession().getUser(),
                    "csa://veranlagungsdatei") != null);
        } catch (final Exception ex) {
            LOG.error("error while checking for csa://veranlagungsdatei", ex);
            cmdVeranlagungsdatei.setVisible(false);
        }

        cmdOpenInD3.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/toolbar/d.3.png"))); // NOI18N
        cmdOpenInD3.setToolTipText("in d.3 öffnen");
        cmdOpenInD3.setFocusPainted(false);
        cmdOpenInD3.setFocusable(false);
        cmdOpenInD3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdOpenInD3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdOpenInD3.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdOpenInD3ActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdOpenInD3);

        jPanel7.setMaximumSize(new java.awt.Dimension(2, 38));
        jPanel7.setMinimumSize(new java.awt.Dimension(2, 38));
        jPanel7.setPreferredSize(new java.awt.Dimension(2, 38));
        jPanel7.setLayout(new java.awt.BorderLayout());

        jSeparator18.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator18.setMaximumSize(new java.awt.Dimension(2, 32767));
        jPanel7.add(jSeparator18, java.awt.BorderLayout.CENTER);

        tobVerdis.add(jPanel7);

        cmdSearchBaulasten.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/commons/gui/baulastsuche.png"))); // NOI18N
        cmdSearchBaulasten.setToolTipText("Baulast-Suche");
        cmdSearchBaulasten.setFocusPainted(false);
        cmdSearchBaulasten.setFocusable(false);
        cmdSearchBaulasten.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdSearchBaulasten.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdSearchBaulasten.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdSearchBaulastenActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdSearchBaulasten);
        cmdSearchBaulasten.setVisible(CidsAppBackend.getInstance().checkPermissionBaulasten());

        cmdSearchRisse.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/commons/gui/vermessungsrisssuche.png"))); // NOI18N
        cmdSearchRisse.setToolTipText("Vermessungsriss-Suche");
        cmdSearchRisse.setFocusPainted(false);
        cmdSearchRisse.setFocusable(false);
        cmdSearchRisse.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdSearchRisse.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdSearchRisse.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdSearchRisseActionPerformed(evt);
                }
            });
        tobVerdis.add(cmdSearchRisse);
        cmdSearchRisse.setVisible(CidsAppBackend.getInstance().checkPermissionRisse());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(tobVerdis, gridBagConstraints);

        getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

        panMain.setLayout(new java.awt.BorderLayout());
        getContentPane().add(panMain, java.awt.BorderLayout.CENTER);

        menFile.setMnemonic('D');
        menFile.setText("Datei");
        menFile.add(jSeparator9);

        mniSaveLayout.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_S,
                java.awt.event.InputEvent.CTRL_MASK));
        mniSaveLayout.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/layout.png"))); // NOI18N
        mniSaveLayout.setText("Aktuelles Layout speichern");
        mniSaveLayout.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniSaveLayoutActionPerformed(evt);
                }
            });
        menFile.add(mniSaveLayout);

        mniLoadLayout.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_O,
                java.awt.event.InputEvent.CTRL_MASK));
        mniLoadLayout.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/layout.png"))); // NOI18N
        mniLoadLayout.setText("Layout laden");
        mniLoadLayout.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniLoadLayoutActionPerformed(evt);
                }
            });
        menFile.add(mniLoadLayout);
        menFile.add(jSeparator10);

        mniClose.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_F4,
                java.awt.event.InputEvent.ALT_MASK));
        mniClose.setText("Beenden");
        mniClose.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mnuExitActionPerformed(evt);
                }
            });
        menFile.add(mniClose);

        jMenuBar1.add(menFile);

        menEdit.setText("Bearbeiten");

        mnuNewKassenzeichen.setText("Neues Kassenzeichen");
        mnuNewKassenzeichen.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mnuNewKassenzeichenActionPerformed(evt);
                }
            });
        menEdit.add(mnuNewKassenzeichen);

        mnuRenameCurrentKZ.setText("Aktuelles Kassenzeichen umbenennen");
        mnuRenameCurrentKZ.setEnabled(false);
        mnuRenameCurrentKZ.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mnuRenameCurrentKZActionPerformed(evt);
                }
            });
        menEdit.add(mnuRenameCurrentKZ);

        mnuRenameAnyKZ.setText("Beliebiges Kassenzeichen umbenennen");
        mnuRenameAnyKZ.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mnuRenameAnyKZActionPerformed(evt);
                }
            });
        menEdit.add(mnuRenameAnyKZ);

        jMenuBar1.add(menEdit);

        menExtras.setMnemonic('E');
        menExtras.setText("Extras");

        mniOptions.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/tooloptions.png"))); // NOI18N
        mniOptions.setText("Optionen");
        mniOptions.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniOptionsActionPerformed(evt);
                }
            });
        menExtras.add(mniOptions);
        menExtras.add(sepOptions);

        mnuChangeUser.setText("User wechseln");
        mnuChangeUser.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mnuChangeUserActionPerformed(evt);
                }
            });
        menExtras.add(mnuChangeUser);

        jMenuItem1.setText("WindowManagementTool");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jMenuItem1ActionPerformed(evt);
                }
            });
        menExtras.add(jMenuItem1);

        jMenuBar1.add(menExtras);

        menWindows.setMnemonic('F');
        menWindows.setText("Fenster");
        menWindows.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    menWindowsActionPerformed(evt);
                }
            });

        mniKassenzeichen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_1,
                java.awt.event.InputEvent.CTRL_MASK));
        mniKassenzeichen.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/kassenzeichen.png"))); // NOI18N
        mniKassenzeichen.setMnemonic('L');
        mniKassenzeichen.setText("Kassenzeichen");
        mniKassenzeichen.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniKassenzeichenActionPerformed(evt);
                }
            });
        menWindows.add(mniKassenzeichen);

        mniKassenzeichen1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_2,
                java.awt.event.InputEvent.CTRL_MASK));
        mniKassenzeichen1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/kassenzeichen.png"))); // NOI18N
        mniKassenzeichen1.setMnemonic('L');
        mniKassenzeichen1.setText("Kassenzeichen-Liste");
        mniKassenzeichen1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniKassenzeichen1ActionPerformed(evt);
                }
            });
        menWindows.add(mniKassenzeichen1);

        mniSummen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_3,
                java.awt.event.InputEvent.CTRL_MASK));
        mniSummen.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/sum.png"))); // NOI18N
        mniSummen.setMnemonic('C');
        mniSummen.setText("Summen");
        mniSummen.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniSummenActionPerformed(evt);
                }
            });
        menWindows.add(mniSummen);

        mniKanalanschluss.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_4,
                java.awt.event.InputEvent.CTRL_MASK));
        mniKanalanschluss.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/pipe.png"))); // NOI18N
        mniKanalanschluss.setMnemonic('F');
        mniKanalanschluss.setText("Kanalanschluss");
        mniKanalanschluss.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniKanalanschlussActionPerformed(evt);
                }
            });
        menWindows.add(mniKanalanschluss);
        menWindows.add(jSeparator11);

        mniKarte.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_7,
                java.awt.event.InputEvent.CTRL_MASK));
        mniKarte.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/flaechen.png"))); // NOI18N
        mniKarte.setMnemonic('S');
        mniKarte.setText("Karte");
        mniKarte.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniKarteActionPerformed(evt);
                }
            });
        menWindows.add(mniKarte);

        mniTabelle.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_8,
                java.awt.event.InputEvent.CTRL_MASK));
        mniTabelle.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/flaechen.png"))); // NOI18N
        mniTabelle.setMnemonic('T');
        mniTabelle.setText("Tabellenansicht");
        mniTabelle.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniTabelleActionPerformed(evt);
                }
            });
        menWindows.add(mniTabelle);

        mniDetails.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_9,
                java.awt.event.InputEvent.CTRL_MASK));
        mniDetails.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/flaechen.png"))); // NOI18N
        mniDetails.setMnemonic('D');
        mniDetails.setText("Details");
        mniDetails.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniDetailsActionPerformed(evt);
                }
            });
        menWindows.add(mniDetails);

        mniResetWindowLayout.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_R,
                java.awt.event.InputEvent.CTRL_MASK));
        mniResetWindowLayout.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/layout.png"))); // NOI18N
        mniResetWindowLayout.setText("Fensteranordnung zurücksetzen");
        mniResetWindowLayout.setToolTipText("Standard Fensteranordnung wiederherstellen");
        mniResetWindowLayout.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniResetWindowLayoutActionPerformed(evt);
                }
            });
        menWindows.add(mniResetWindowLayout);

        jMenuBar1.add(menWindows);

        menHelp.setMnemonic('E');
        menHelp.setText("?");

        mnuHelp.setMnemonic('H');
        mnuHelp.setText("Hilfe");
        menHelp.add(mnuHelp);
        menHelp.add(jSeparator1);

        mnuInfo.setMnemonic('I');
        mnuInfo.setText("Info");
        mnuInfo.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mnuInfoActionPerformed(evt);
                }
            });
        menHelp.add(mnuInfo);

        jMenuBar1.add(menHelp);

        setJMenuBar(jMenuBar1);

        setSize(new java.awt.Dimension(1261, 868));
        setLocationRelativeTo(null);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * Inserting Docking Window functionalty (Sebastian) 24.07.07.
     *
     * @param  evt  DOCUMENT ME!
     */
    private void menWindowsActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_menWindowsActionPerformed
// TODO add your handling code here:
    } //GEN-LAST:event_menWindowsActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniResetWindowLayoutActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniResetWindowLayoutActionPerformed
        setupDefaultLayout();
    }                                                                                        //GEN-LAST:event_mniResetWindowLayoutActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniKanalanschlussActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniKanalanschlussActionPerformed
        showOrHideView(vKanaldaten);
    }                                                                                     //GEN-LAST:event_mniKanalanschlussActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniSummenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniSummenActionPerformed
        showOrHideView(vSummen);
    }                                                                             //GEN-LAST:event_mniSummenActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniKassenzeichenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniKassenzeichenActionPerformed
        showOrHideView(vKassenzeichen);
    }                                                                                    //GEN-LAST:event_mniKassenzeichenActionPerformed

    /**
     * Inserting Docking Window functionalty (Sebastian) 24.07.07.
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniLoadLayoutActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniLoadLayoutActionPerformed
        final JFileChooser fc = new JFileChooser(DIRECTORYPATH_VERDIS);
        fc.setFileFilter(new FileFilter() {

                @Override
                public boolean accept(final File f) {
                    return f.getName().toLowerCase().endsWith(".layout");
                }

                @Override
                public String getDescription() {
                    return "Layout";
                }
            });
        fc.setMultiSelectionEnabled(false);
        final int state = fc.showOpenDialog(this);
        if (state == JFileChooser.APPROVE_OPTION) {
            final File file = fc.getSelectedFile();
            String name = file.getAbsolutePath();
            name = name.toLowerCase();
            try {
                if (name.endsWith(".layout")) {
                    loadLayout(name);
                } else {
                    JOptionPane.showMessageDialog(
                        this,
                        java.util.ResourceBundle.getBundle("de/cismet/verdis/res/i18n/Bundle").getString(
                            "CismapPlugin.InfoNode.format_failure_message"),
                        java.util.ResourceBundle.getBundle("de/cismet/verdis/res/i18n/Bundle").getString(
                            "CismapPlugin.InfoNode.message_title"),
                        JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (final Exception ex) {
                LOG.error("error while loading layout", ex);
            }
        }
    } //GEN-LAST:event_mniLoadLayoutActionPerformed

    /**
     * Inserting Docking Window functionalty (Sebastian) 24.07.07.
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniSaveLayoutActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniSaveLayoutActionPerformed
        final JFileChooser fc = new JFileChooser(DIRECTORYPATH_VERDIS);
        fc.setFileFilter(new FileFilter() {

                @Override
                public boolean accept(final File f) {
                    return f.getName().toLowerCase().endsWith(".layout");
                }

                @Override
                public String getDescription() {
                    return "Layout";
                }
            });
        fc.setMultiSelectionEnabled(false);
        final int state = fc.showSaveDialog(this);
        if (LOG.isDebugEnabled()) {
            LOG.debug("state:" + state);
        }
        if (state == JFileChooser.APPROVE_OPTION) {
            final File file = fc.getSelectedFile();
            if (LOG.isDebugEnabled()) {
                LOG.debug("file:" + file);
            }
            String name = file.getAbsolutePath();
            name = name.toLowerCase();
            if (name.endsWith(".layout")) {
                saveLayout(name);
            } else {
                saveLayout(name + ".layout");
            }
        }
    } //GEN-LAST:event_mniSaveLayoutActionPerformed

    /**
     * TODO Bundle Inserting Docking Window functionalty (Sebastian) 24.07.07.
     *
     * @param   file  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public void loadLayout(final String file) throws Exception {
        if (!isInit) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Load Layout.. from " + file);
            }
            final File layoutFile = new File(file);

            final FileInputStream layoutInput = new FileInputStream(layoutFile);
            final ObjectInputStream in = new ObjectInputStream(layoutInput);
            rootWindow.read(in);
            in.close();
            rootWindow.getWindowBar(Direction.LEFT).setEnabled(true);
            rootWindow.getWindowBar(Direction.RIGHT).setEnabled(true);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Loading Layout successfull");
            }
        }
    }
    // Inserting Docking Window functionalty (Sebastian) 24.07.07

    /**
     * DOCUMENT ME!
     *
     * @param  file  DOCUMENT ME!
     */
    public void saveConfig(final String file) {
        configurationManager.writeConfiguration(file);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  file  DOCUMENT ME!
     */
    public void saveLayout(final String file) {
        if (!isInit) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Saving Layout.. to " + file);
            }
            final File layoutFile = new File(file);
            try {
                if (!layoutFile.exists()) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Saving Layout.. File does not exit");
                    }
                    final File verdisDir = new File(DIRECTORYPATH_VERDIS);
                    if (!verdisDir.exists()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Verdis Directory angelegt");
                        }
                        verdisDir.mkdir();
                    }
                    layoutFile.createNewFile();
                } else if (LOG.isDebugEnabled()) {
                    LOG.debug("Saving Layout.. File does exit");
                }
                final FileOutputStream layoutOutput = new FileOutputStream(layoutFile);
                final ObjectOutputStream out = new ObjectOutputStream(layoutOutput);
                setLeftTitleBarColor(myBlue);
                rootWindow.write(out);
                out.flush();
                out.close();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Saving Layout.. to " + file + " successfull");
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                    this,
                    java.util.ResourceBundle.getBundle("de/cismet/verdis/res/i18n/Bundle").getString(
                        "CismapPlugin.InfoNode.saving_layout_failure"),
                    java.util.ResourceBundle.getBundle("de/cismet/verdis/res/i18n/Bundle").getString(
                        "CismapPlugin.InfoNode.message_title"),
                    JOptionPane.INFORMATION_MESSAGE);
                LOG.error("A failure occured during writing the layout file", ex);
            }
        }
    }

    /**
     * Inserting Docking Window functionalty (Sebastian) 24.07.07.
     *
     * @param  v  DOCUMENT ME!
     */
    private void showOrHideView(final View v) {
        ///irgendwas besser als Closable ??
        // Problem wenn floating --> close -> open  (muss zweimal open)

        try {
            if (v.isClosable()) {
                v.close();
            } else {
                v.restore();
            }
        } catch (Exception e) {
            LOG.error("problem during hide or view", e);
        }
    }

    /**
     * Inserting Docking Window functionalty (Sebastian) 24.07.07.
     *
     * @param  icoFlaeche  DOCUMENT ME!
     */
    public void setFlaechenPanelIcon(final Icon icoFlaeche) {
    }

    /**
     * Inserting Docking Window functionalty (Sebastian) 24.07.07.
     *
     * @param  letzteAenderung  DOCUMENT ME!
     */
    public void setLetzteAenderungTooltip(final String letzteAenderung) {
        if (!isInit) {
            // ((ImageIcon)vKassenzeichen.getIcon()).setDescription()
            // vKassenzeichen.get
            // vKassenzeichen.getViewProperties().getViewTitleBarProperties().getNormalProperties().
        }
    }

    /**
     * Inserting Docking Window functionalty (Sebastian) 24.07.07.
     *
     * @param  icoSumme  DOCUMENT ME!
     */
    public void setSummenPanelIcon(final Icon icoSumme) {
        if (!isInit) {
            vSummen.getViewProperties().setIcon(icoSumme);
        }
    }

    /**
     * Inserting Docking Window functionalty (Sebastian) 24.07.07.
     *
     * @param  foreground  DOCUMENT ME!
     */
    public void setKanalTitleForeground(final Color foreground) {
        if (!isInit) {
            vKanaldaten.getViewProperties()
                    .getViewTitleBarProperties()
                    .getNormalProperties()
                    .getComponentProperties()
                    .setForegroundColor(foreground);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mnuChangeUserActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mnuChangeUserActionPerformed
        formWindowOpened(null);
    }                                                                                 //GEN-LAST:event_mnuChangeUserActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mnuNewKassenzeichenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mnuNewKassenzeichenActionPerformed
        cmdNewKassenzeichenActionPerformed(null);
    }                                                                                       //GEN-LAST:event_mnuNewKassenzeichenActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mnuExitActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mnuExitActionPerformed
        dispose();
    }                                                                           //GEN-LAST:event_mnuExitActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void formWindowOpened(final java.awt.event.WindowEvent evt) { //GEN-FIRST:event_formWindowOpened
    }                                                                     //GEN-LAST:event_formWindowOpened

    /**
     * DOCUMENT ME!
     *
     * @param   appPreferences  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static CidsAuthentification login(final AppPreferences appPreferences) {
        final DefaultUserNameStore usernames = new DefaultUserNameStore();
        final Preferences appPrefs = Preferences.userNodeForPackage(Main.class);
        usernames.setPreferences(appPrefs.node("login"));
        final CidsAuthentification cidsAuth = new CidsAuthentification(appPreferences);
        final JXLoginPane login = new JXLoginPane(cidsAuth, null, usernames) {

                @Override
                protected Image createLoginBanner() {
                    return getBannerImage();
                }
            };

        String u = null;
        try {
            u = usernames.getUserNames()[usernames.getUserNames().length - 1];
        } catch (final Exception skip) {
        }
        if (u != null) {
            login.setUserName(u);
        }

        final JXLoginPane.JXLoginDialog d = new JXLoginPane.JXLoginDialog(SPLASH, login);
        login.setPassword("".toCharArray());

        try {
            ((JXPanel)((JXPanel)login.getComponent(1)).getComponent(1)).getComponent(3).requestFocus();
        } catch (final Exception skip) {
        }

        // StaticSwingTools.showDialog(d);
        d.setLocationRelativeTo(SPLASH);
        d.setVisible(true);
        if (handleLoginStatus(d.getStatus(), usernames, login)) {
            return cidsAuth;
        } else {
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   status     DOCUMENT ME!
     * @param   usernames  DOCUMENT ME!
     * @param   login      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static boolean handleLoginStatus(final JXLoginPane.Status status,
            final DefaultUserNameStore usernames,
            final JXLoginPane login) {
        if (status == JXLoginPane.Status.SUCCEEDED) {
            // Damit wird sichergestellt, dass dieser als erstes vorgeschlagen wird
            usernames.removeUserName(login.getUserName());
            usernames.saveUserNames();
            usernames.addUserName((login.getUserName()));
            usernames.saveUserNames();
            if (LOG.isDebugEnabled()) {
                // Added for RM Plugin functionalty 22.07.2007 Sebastian Puhl
                LOG.debug("Login erfolgreich");
            }
            return true;
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Login fehlgeschlagen");
            }
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdRefreshEnumerationActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdRefreshEnumerationActionPerformed
        regenFlaechenTablePanel.reEnumerateFlaechen();
    }                                                                                         //GEN-LAST:event_cmdRefreshEnumerationActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdPdfActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdPdfActionPerformed
        if (kassenzeichenBean != null) {
            final EBGeneratorDialog.Mode ebMode;
            if (CidsAppBackend.Mode.SR.equals(CidsAppBackend.getInstance().getMode())) {
                ebMode = EBGeneratorDialog.Mode.FRONTEN;
            } else {
                ebMode = EBGeneratorDialog.Mode.FLAECHEN;
            }
            final EBGeneratorDialog dialog = new EBGeneratorDialog(kassenzeichenBean, this, ebMode);

            SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        StaticSwingTools.showDialog(dialog);
                    }
                });
        }
    } //GEN-LAST:event_cmdPdfActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdDeleteKassenzeichenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdDeleteKassenzeichenActionPerformed
        deleteKassenzeichen();
    }                                                                                          //GEN-LAST:event_cmdDeleteKassenzeichenActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdPasteActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdPasteActionPerformed
        final AbstractClipboard clipboard = getCurrentClipboard();
        if (clipboard != null) {
            clipboard.storeToFile();
            clipboard.paste();
        }
    }                                                                            //GEN-LAST:event_cmdPasteActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public AbstractClipboard getCurrentClipboard() {
        return clipboards.get(CidsAppBackend.getInstance().getMode());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdCutActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdCutActionPerformed
        final AbstractClipboard clipboard = getCurrentClipboard();
        if (clipboard != null) {
            clipboard.cut();
        }
    }                                                                          //GEN-LAST:event_cmdCutActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DCUMENT ME!
     */
    private void cmdInfoActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdInfoActionPerformed
//        String info="Verdis Plugin\n"
//                + "cismet GmbH\n\n"
//                + de.cismet.verdis.Version.getVersion()+"\n"
//                + de.cismet.cismap.commons.Version.getVersion();
//        JOptionPane.showMessageDialog(this,info,"Info",JOptionPane.INFORMATION_MESSAGE);

        if (about == null) {
            final JDialog d = new JDialog(this, "Info");
            d.setLayout(new BorderLayout());

            // JLabel infoLabel=new JLabel(Version.getVersion()+"\n"+
            // de.cismet.cismap.commons.Version.getVersion());
            // d.add(infoLabel,BorderLayout.SOUTH);
            final JLabel image = new JLabel(new ImageIcon(getBannerImage()));
            d.add(image, BorderLayout.CENTER);
            final JLabel version = new JLabel(Version.getVersion());

            d.add(version, BorderLayout.SOUTH);
            d.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
            d.pack();
            about = d;
        }
        StaticSwingTools.showDialog(about);
    } //GEN-LAST:event_cmdInfoActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mnuInfoActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mnuInfoActionPerformed
        cmdInfoActionPerformed(null);
    }                                                                           //GEN-LAST:event_mnuInfoActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void formKeyReleased(final java.awt.event.KeyEvent evt) { //GEN-FIRST:event_formKeyReleased
        // TODO add your handling code here:
    } //GEN-LAST:event_formKeyReleased

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void formKeyPressed(final java.awt.event.KeyEvent evt) { //GEN-FIRST:event_formKeyPressed

        if ((evt.getKeyCode() == KeyEvent.VK_F1) && evt.isControlDown()) {
        }
        // TODO add your handling code here:
    } //GEN-LAST:event_formKeyPressed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void formKeyTyped(final java.awt.event.KeyEvent evt) { //GEN-FIRST:event_formKeyTyped
    }                                                              //GEN-LAST:event_formKeyTyped

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DCUMENT ME!
     */
    private void cmdWorkflowActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdWorkflowActionPerformed
    }                                                                               //GEN-LAST:event_cmdWorkflowActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void formWindowClosing(final java.awt.event.WindowEvent evt) { //GEN-FIRST:event_formWindowClosing
        LOG.info("formWindowClosing");
        if (editMode && !kassenzeichenPanel.isEmpty()) {
            if (changesPending()) {
                final int answer = JOptionPane.showConfirmDialog(
                        this,
                        "Wollen Sie die gemachten \u00C4nderungen speichern?",
                        "Verdis \u00C4nderungen",
                        JOptionPane.YES_NO_OPTION);
                if (answer == JOptionPane.YES_OPTION) {
                    saveKassenzeichenAndAssessement();
                } else {
                    releaseLocks();
                }
            } else {
                releaseLocks();
            }
        }
        closeAllConnections();
    }                                                                      //GEN-LAST:event_formWindowClosing

    /**
     * DOCUMENT ME!
     */
    private void closeAllConnections() {
        try {
        } catch (final Exception ex) {
            LOG.error("Fehler beim Schlie\u00DFen der Connections", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void formWindowClosed(final java.awt.event.WindowEvent evt) { //GEN-FIRST:event_formWindowClosed
    }                                                                     //GEN-LAST:event_formWindowClosed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdNewKassenzeichenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdNewKassenzeichenActionPerformed
        if (!readonly) {
            if (changesPending()) {
                final int answer = JOptionPane.showConfirmDialog(
                        this,
                        "Wollen Sie die gemachten \u00C4nderungen zuerst speichern?",
                        "Neues Kassenzeichen",
                        JOptionPane.YES_NO_CANCEL_OPTION);
                if (answer == JOptionPane.YES_OPTION) {
                    saveKassenzeichenAndAssessement();
                    newKassenzeichen();
                } else if (answer == JOptionPane.NO_OPTION) {
                    newKassenzeichen();
                }
            } else {
                newKassenzeichen();
            }
        }
    }                                                                                       //GEN-LAST:event_cmdNewKassenzeichenActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdOkActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdOkActionPerformed
        if (checkForDuplicateCoordinates() && changesPending()) {
            saveKassenzeichenAndAssessement();
        }
    }                                                                         //GEN-LAST:event_cmdOkActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean checkForDuplicateCoordinates() {
        final Collection<PFeature> pFeatures = new ArrayList<>();
        for (final Feature feature : getMappingComponent().getFeatureCollection().getAllFeatures()) {
            pFeatures.add(getMappingComponent().getPFeatureHM().get(feature));
        }
        final List<PFeatureTools.PFeatureCoordinateInformation> infos = PFeatureTools.identifyMergeableCoordinates(
                pFeatures,
                AutomergeCoordinatesDialog.getCoordinateDuplicateThreshold());
        if (infos.isEmpty()) {
            return true;
        } else {
            final AutomergeCoordinatesDialog.Status status = AutomergeCoordinatesDialog.getInstance().display(infos);
            if (status != null) {
                switch (status) {
                    case IGNORE: {
                        return true;
                    }
                    case MERGE: {
                        final List<PFeatureTools.PFeatureCoordinateInformation> unmergedInfos = PFeatureTools
                                    .automergeCoordinates(infos);
                        for (final PFeatureTools.PFeatureCoordinateInformation unmergedInfo : unmergedInfos) {
                            LOG.warn("unmerged coordinate of " + unmergedInfo.getPFeature().getFeature() + ": "
                                        + unmergedInfo.getCoordinate());
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdCancelActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdCancelActionPerformed
        if (changesPending()) {
            final int answer = JOptionPane.showConfirmDialog(
                    this,
                    "Wollen Sie die gemachten \u00C4nderungen verwerfen?",
                    "Abbrechen",
                    JOptionPane.YES_NO_OPTION);
            if (answer != JOptionPane.YES_OPTION) {
                return;
            }
        }
        WaitDialog.getInstance().showDialog();
        new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    releaseLocks();
                    return null;
                }

                @Override
                protected void done() {
                    WaitDialog.getInstance().dispose();
                    setEditMode(false);
                    kassenzeichenPanel.refresh();
                }
            }.execute();
    } //GEN-LAST:event_cmdCancelActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdEditModeActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdEditModeActionPerformed
        if (!readonly) {
            WaitDialog.getInstance().showDialog();
            new SwingWorker<Boolean, Void>() {

                    @Override
                    protected Boolean doInBackground() throws Exception {
                        if (editMode) {              // this is before switching the mode
                            if (!changesPending()) { // only if no save is needed
                                releaseLocks();
                                return false;
                            }
                        } else {
                            if (acquireLocks()) {    // try to acquire
                                return true;
                            }
                            releaseLocks();          // release if acquire failed
                        }
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            final Boolean enableEditing = get();
                            if (enableEditing != null) {
                                setEditMode(enableEditing);
                            }
                        } catch (final Exception ex) {
                            LOG.error(ex, ex);
                        } finally {
                            WaitDialog.getInstance().dispose();
                        }
                    }
                }.execute();
        }
    } //GEN-LAST:event_cmdEditModeActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean acquireLocks() {
        try {
            CidsAppBackend.getInstance().acquireLocks();
            return true;
        } catch (final LockAlreadyExistsException ex) {
            CidsAppBackend.getInstance().showObjectsLockedDialog(ex.getAlreadyExisingLocks());
            return false;
        } catch (final Exception ex) {
            LOG.error("error during acquireLocks", ex);
            CidsAppBackend.getInstance()
                    .showError("Fehler beim Sperren", "Beim Sperren des Kassenzeichens kam es zu einem Fehler.", ex);
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdCopyActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdCopyActionPerformed
        final AbstractClipboard clipboard = clipboards.get(CidsAppBackend.getInstance().getMode());
        if (clipboard != null) {
            clipboard.storeToFile();
            clipboard.copy();
        }
    }                                                                           //GEN-LAST:event_cmdCopyActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdTestActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdTestActionPerformed
    }                                                                           //GEN-LAST:event_cmdTestActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdTest2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdTest2ActionPerformed
    }                                                                            //GEN-LAST:event_cmdTest2ActionPerformed

    /**
     * ToDo Threading and Progressbar.
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdLagisCrossoverActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdLagisCrossoverActionPerformed
        try {
            final JDialog dialog = new JDialog(this, "", true);
            final PopupLagisCrossoverPanel lcp = new PopupLagisCrossoverPanel(CidsAppBackend.getInstance()
                            .getAppPreferences().getLagisCrossoverPort());
            dialog.add(lcp);
            dialog.pack();
            dialog.setIconImage(new javax.swing.ImageIcon(
                    getClass().getResource("/de/cismet/verdis/res/images/toolbar/lagisCrossover.png")).getImage());
            dialog.setTitle("Flurstück in LagIS öffnen.");
            lcp.startSearch();
            StaticSwingTools.showDialog(dialog);
        } catch (Exception ex) {
            LOG.error("Crossover: Fehler im LagIS Crossover", ex);
            // ToDo Meldung an Benutzer
        }
    }                                                                                     //GEN-LAST:event_cmdLagisCrossoverActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mnuRenameCurrentKZActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mnuRenameCurrentKZActionPerformed
        if (!readonly) {
            if (editMode) {
                if (changesPending()) {
                    final int answer = JOptionPane.showConfirmDialog(
                            this,
                            "Wollen Sie die gemachten \u00C4nderungen zuerst speichern?",
                            "Kassenzeichen umbenennen",
                            JOptionPane.YES_NO_CANCEL_OPTION);
                    if (answer == JOptionPane.YES_OPTION) {
                        saveKassenzeichenAndAssessement();
                        renameCurrentKassenzeichen();
                    } else if (answer == JOptionPane.NO_OPTION) {
                        releaseLocks();
                        kassenzeichenPanel.refresh();
                        renameCurrentKassenzeichen();
                    }
                } else {
                    WaitDialog.getInstance().showDialog();
                    new SwingWorker<Void, Void>() {

                            @Override
                            protected Void doInBackground() throws Exception {
                                releaseLocks();
                                return null;
                            }

                            @Override
                            protected void done() {
                                WaitDialog.getInstance().dispose();
                                setEditMode(false);
                                kassenzeichenPanel.refresh();
                                renameCurrentKassenzeichen();
                            }
                        }.execute();
                }
            } else {
                renameCurrentKassenzeichen();
            }
        }
    } //GEN-LAST:event_mnuRenameCurrentKZActionPerformed

    /**
     * DOCUMENT ME!
     */
    public void releaseLocks() {
        try {
            CidsAppBackend.getInstance().releaseLocks();
        } catch (final Exception ex) {
            LOG.error("error during releaseLocks", ex);
            CidsAppBackend.getInstance()
                    .showError(
                        "Fehler beim Freigeben",
                        "Beim Freigeben der Kassenzeichensperre kam es zu einem Fehler.",
                        ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniKarteActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniKarteActionPerformed
        showOrHideView(vKarte);
    }                                                                            //GEN-LAST:event_mniKarteActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniTabelleActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniTabelleActionPerformed
        switch (CidsAppBackend.getInstance().getMode()) {
            case REGEN: {
                showOrHideView(vTabelleRegen);
            }
            break;
            case SR: {
                showOrHideView(vTabelleSR);
            }
            break;
            case KANALDATEN: {
                showOrHideView(vTabelleVersickerung);
            }
            break;
        }
    }                                                                              //GEN-LAST:event_mniTabelleActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniDetailsActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniDetailsActionPerformed
        switch (CidsAppBackend.getInstance().getMode()) {
            case REGEN: {
                showOrHideView(vDetailsRegen);
            }
            break;
            case SR: {
                showOrHideView(vDetailsSR);
            }
            break;
            case ALLGEMEIN: {
                showOrHideView(vDetailsAllgemein);
            }
            break;
            case KANALDATEN: {
                showOrHideView(vDetailsVersickerung);
            }
            break;
        }
    }                                                                              //GEN-LAST:event_mniDetailsActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jMenuItem1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jMenuItem1ActionPerformed
        DeveloperUtil.createWindowLayoutFrame("Momentanes Layout", rootWindow).setVisible(true);
    }                                                                              //GEN-LAST:event_jMenuItem1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnHistoryActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnHistoryActionPerformed
        final HistoryPanel historyPan = new HistoryPanel();
        historyPan.setCidsBean(kassenzeichenBean);

        final JDialog dial = new JDialog(this, true);
        dial.setTitle("Kassenzeichen-Verlauf");
        dial.setContentPane(historyPan);
        dial.setSize(800, 600);
        StaticSwingTools.showDialog(dial);
    } //GEN-LAST:event_btnHistoryActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private CidsBeanTable getCurrentCidsbeanTable() {
        CidsBeanTable cidsBeanTable = null;
        switch (CidsAppBackend.getInstance().getMode()) {
            case REGEN: {
                cidsBeanTable = getRegenFlaechenTable();
            }
            break;
            case SR: {
                cidsBeanTable = getSRFrontenTable();
            }
            break;
            case KANALDATEN: {
                cidsBeanTable = getBefreiungerlaubnisGeometrieTable();
            }
            break;
        }
        return cidsBeanTable;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  geom  DOCUMENT ME!
     */
    public static void transformToDefaultCrsNeeded(Geometry geom) {
        if (geom == null) {
            return;
        }

        // Srid des solefeatures prüfen
        int srid = geom.getSRID();
        final int defaultSrid = CrsTransformer.extractSridFromCrs(CismapBroker.getInstance().getDefaultCrs());
        if (srid == CismapBroker.getInstance().getDefaultCrsAlias()) {
            srid = defaultSrid;
        }
        // gegebenenfalls transformieren
        if (srid != defaultSrid) {
            final int ans = JOptionPane.showConfirmDialog(
                    getInstance(),
                    "Die angegebene Geometrie befindet sich nicht im Standard-CRS. Soll die Geometrie konvertiert werden?",
                    "Geometrie konvertieren?",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (ans == JOptionPane.YES_OPTION) {
                geom = CrsTransformer.transformToDefaultCrs(geom);
                geom.setSRID(CismapBroker.getInstance().getDefaultCrsAlias());
            }
        } else {
            geom.setSRID(CismapBroker.getInstance().getDefaultCrsAlias());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdAddActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdAddActionPerformed
        if (!CidsAppBackend.getInstance().getMode().equals(CidsAppBackend.Mode.ALLGEMEIN)) {
            final CidsBeanTable cidsBeanTable = getCurrentCidsbeanTable();
            if (cidsBeanTable != null) {
                cidsBeanTable.addNewBean();
            }
        }
    }                                                                          //GEN-LAST:event_cmdAddActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdRemoveActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdRemoveActionPerformed
        if (!CidsAppBackend.getInstance().getMode().equals(CidsAppBackend.Mode.ALLGEMEIN)) {
            final CidsBeanTable cidsBeanTable = getCurrentCidsbeanTable();
            if (cidsBeanTable != null) {
                cidsBeanTable.removeSelectedBeans();
            }
        }
    }                                                                             //GEN-LAST:event_cmdRemoveActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdUndoActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdUndoActionPerformed
        final CidsBeanTable cidsBeanTable = getCurrentCidsbeanTable();
        if (cidsBeanTable != null) {
            cidsBeanTable.restoreSelectedBeans();
        }
    }                                                                           //GEN-LAST:event_cmdUndoActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DCUMENT ME!
     */
    private void mniOptionsActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniOptionsActionPerformed
        final OptionsDialog od = new OptionsDialog(this, true);
        StaticSwingTools.showDialog(od);
    }                                                                              //GEN-LAST:event_mniOptionsActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCMENT ME!
     */
    private void cmdFortfuehrungActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdFortfuehrungActionPerformed
        StaticSwingTools.showDialog(VerdisFortfuehrungsanlaesseDialog.getInstance());
    }                                                                                   //GEN-LAST:event_cmdFortfuehrungActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DCUMENT ME!
     */
    private void cmdNextKassenzeichenWithoutGeomActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdNextKassenzeichenWithoutGeomActionPerformed
        final Integer kassenzeichennummer8;
        if (getCidsBean() != null) {
            kassenzeichennummer8 = (Integer)getCidsBean().getProperty(
                    KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER);
        } else {
            kassenzeichennummer8 = null;
        }
        final NextKassenzeichenWithoutKassenzeichenGeometrieSearchStatement serverSearch =
            new NextKassenzeichenWithoutKassenzeichenGeometrieSearchStatement(kassenzeichennummer8);

        try {
            final List<Integer> result = (List<Integer>)SessionManager.getProxy()
                        .customServerSearch(SessionManager.getSession().getUser(), serverSearch);
            if ((result != null) && !result.isEmpty()) {
                final Integer nextKassenzeichennummer = result.get(0);
                CidsAppBackend.getInstance().gotoKassenzeichen(Integer.toString(nextKassenzeichennummer));
            }
        } catch (final ConnectionException ex) {
            LOG.error("error while executing next kassenzeichensearch", ex);
        }
    } //GEN-LAST:event_cmdNextKassenzeichenWithoutGeomActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdGrundbuchblattSucheActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdGrundbuchblattSucheActionPerformed
        StaticSwingTools.showDialog(GrundbuchblattSucheDialog.getInstance());
    }                                                                                          //GEN-LAST:event_cmdGrundbuchblattSucheActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniKassenzeichen1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniKassenzeichen1ActionPerformed
        showOrHideView(vKassenzeichenList);
    }                                                                                     //GEN-LAST:event_mniKassenzeichen1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdSAPCheckActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdSAPCheckActionPerformed
        if (cmdSAPCheck.isSelected()) {
            sapClipboardListener.gainOwnership();
        }
    }                                                                               //GEN-LAST:event_cmdSAPCheckActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdMemoryActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdMemoryActionPerformed
        // TODO add your handling code here:
    } //GEN-LAST:event_cmdMemoryActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdRecalculateAreaActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdRecalculateAreaActionPerformed
        if (isInEditMode()) {
            regenFlaechenTablePanel.recalculateAreaOfFlaechen();
        }
    }                                                                                      //GEN-LAST:event_cmdRecalculateAreaActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdArbeitspaketeActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdArbeitspaketeActionPerformed
        ArbeitspaketeManagerPanel.getInstance().loadArbeitspakete();
        StaticSwingTools.showDialog(ArbeitspaketeManagerPanel.getInstance().getDialog());
    }                                                                                    //GEN-LAST:event_cmdArbeitspaketeActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdAbfrageeditorActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdAbfrageeditorActionPerformed
        StaticSwingTools.showDialog(abfrageDialog);
    }                                                                                    //GEN-LAST:event_cmdAbfrageeditorActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdVeranlagungsdateiActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdVeranlagungsdateiActionPerformed
        VeranlagungsdateiScheduleDialog.getInstance().pack();
        StaticSwingTools.showDialog(VeranlagungsdateiScheduleDialog.getInstance());
    }                                                                                        //GEN-LAST:event_cmdVeranlagungsdateiActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnTimeRecoveryActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnTimeRecoveryActionPerformed
        if (editMode) {
            StaticSwingTools.showDialog(timeRecoveryPanel.getDialog());
        } else {
            final int value = JOptionPane.showConfirmDialog(
                    this,
                    "Dieser Vorgang kann eine Weile dauern.\n"
                            + "Möchten Sie dennoch fortfahren ?",
                    "Gelöschtes Kassenzeichen suchen",
                    JOptionPane.YES_NO_OPTION);

            if (value == JOptionPane.YES_OPTION) {
                final int kassenzeichenNummer = Integer.parseInt(kassenzeichenPanel.getSearchField());

                final DeletedKassenzeichenIdSearchStatement search = new DeletedKassenzeichenIdSearchStatement(
                        kassenzeichenNummer);

                WaitDialog.getInstance().showDialog();
                new SwingWorker<List<Integer>, Void>() {

                        @Override
                        protected List<Integer> doInBackground() throws Exception {
                            WaitDialog.getInstance().startSearchDeletedKassenzeichenFromHistory();
                            final List<Integer> tmpList = new ArrayList<>();
                            final Collection coll = CidsAppBackend.getInstance().executeCustomServerSearch(search);
                            tmpList.addAll(coll);
                            return tmpList;
                        }

                        @Override
                        protected void done() {
                            try {
                                final List<Integer> list = get();

                                if (!list.isEmpty()) {
                                    final Integer kassenzeichenId = list.get(0);

                                    final CidsBean dummyKassenzeichenBean = CidsBean.createNewCidsBeanFromTableName(
                                            VerdisConstants.DOMAIN,
                                            VerdisMetaClassConstants.MC_KASSENZEICHEN,
                                            new HashMap<String, Object>() {

                                                {
                                                    put(KassenzeichenPropertyConstants.PROP__ID, kassenzeichenId);
                                                    put(KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER,
                                                        kassenzeichenNummer);
                                                }
                                            });
                                    dummyKassenzeichenBean.getMetaObject().forceStatus(MetaObject.NEW);
                                    CidsAppBackend.getInstance().setCidsBean(dummyKassenzeichenBean);
                                    setEditMode(true);
                                    StaticSwingTools.showDialog(timeRecoveryPanel.getDialog());
                                } else {
                                    JOptionPane.showMessageDialog(
                                        Main.this,
                                        "Es konnte für dieses Kassenzeichen keine Historie gefunden werden.",
                                        "Nicht gefunden",
                                        JOptionPane.INFORMATION_MESSAGE);
                                }
                            } catch (final Exception ex) {
                                CidsAppBackend.getInstance()
                                        .showError("Fehler", "Fehler bei der Suche nach gelöschten Kassenzeichen", ex);
                                LOG.warn(ex, ex);
                            } finally {
                                WaitDialog.getInstance().dispose();
                            }
                        }
                    }.execute();
            }
        }
    } //GEN-LAST:event_btnTimeRecoveryActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdOpenInD3ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdOpenInD3ActionPerformed
        if (kassenzeichenBean != null) {
            final String kz = getCidsBean().getProperty(
                    KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER).toString();
            try {
                final Runtime rt = Runtime.getRuntime();
                final Process pr = rt.exec("clink.exe verdis " + kz);
            } catch (final Exception e) {
                CidsAppBackend.getInstance()
                        .showError(
                            "Fehler beim Öffnen von d.3",
                            "Beim Öffnen der Dokumente zu Kassenzeichen "
                            + kz
                            + " ist ein Fehler aufgetreten.",
                            e);
            }
        }
    }                                                                               //GEN-LAST:event_cmdOpenInD3ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  current  evt DOCUMENT ME!
     */
    private void showRenameFailed(final boolean current) {
        final String[] options = new String[] {
                "Ja, wiederholen",
                "Nein, abbrechen"
            };
        final int option = JOptionPane.showOptionDialog(
                Main.this,
                "<html>Möchten Sie es erneut versuchen?",
                "Umbenennen wiederholen?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
        if (JOptionPane.YES_OPTION == option) {
            showRenameKassenzeichen(current);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  current  DOCUMENT ME!
     */
    private void showRenameKassenzeichen(final boolean current) {
        final String oldKassenzeichen;
        if (current) {
            oldKassenzeichen = Integer.toString((Integer)kassenzeichenBean.getProperty(
                        KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER));
        } else {
            oldKassenzeichen = JOptionPane.showInputDialog(
                    this,
                    "<html>Geben Sie das <b>alte</b> Kassenzeichen ein: ",
                    "Kassenzeichen wählen.",
                    JOptionPane.PLAIN_MESSAGE);
        }
        if (oldKassenzeichen != null) {
            final String newKassenzeichen = JOptionPane.showInputDialog(
                    this,
                    "<html>Geben Sie das <b>neue</b> Kassenzeichen ein: ",
                    "Kassenzeichen Umbenennen.",
                    JOptionPane.PLAIN_MESSAGE);
            if (newKassenzeichen != null) {
                final int newKassenzeichenNummer;
                final int oldKassenzeichenNummer;

                if (newKassenzeichen.toCharArray()[0] != oldKassenzeichen.toCharArray()[0]) {
                    final String[] options = new String[] {
                            "Ja, umbenennen",
                            "Nein, neue Eingabe"
                        };
                    final int option = JOptionPane.showOptionDialog(
                            Main.this,
                            "<html>Das neue Kassenzeichen beginnt mit einer<br/>"
                                    + "anderen Ziffer als das alte Kassenzeichen.</br><br/><br/>"
                                    + "Möchten Sie das Kassenzeichen dennoch umbenennen?",
                            "Umbenennen wiederholen?",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE,
                            null,
                            options,
                            options[0]);
                    if (JOptionPane.YES_OPTION != option) {
                        showRenameKassenzeichen(current);
                        return;
                    }
                }
                try {
                    newKassenzeichenNummer = new Integer(newKassenzeichen);
                    oldKassenzeichenNummer = new Integer(oldKassenzeichen);
                } catch (final NumberFormatException e) {
                    CidsAppBackend.getInstance()
                            .showError("Fehler beim Umbenennen.", "Das Kassenzeichen muss eine Zahl sein.", null);
                    showRenameFailed(current);
                    return;
                }

                disableKassenzeichenCmds();
                new SwingWorker<Object, Void>() {

                        @Override
                        protected Object doInBackground() throws Exception {
                            return CidsAppBackend.getInstance()
                                        .executeServerAction(
                                            RenameKassenzeichenServerAction.TASKNAME,
                                            null,
                                            new ServerActionParameter<>(
                                                RenameKassenzeichenServerAction.ParameterType.KASSENZEICHENNUMMER_OLD
                                                    .toString(),
                                                oldKassenzeichenNummer),
                                            new ServerActionParameter<>(
                                                RenameKassenzeichenServerAction.ParameterType.KASSENZEICHENNUMMER_NEW
                                                    .toString(),
                                                newKassenzeichenNummer));
                        }

                        @Override
                        protected void done() {
                            try {
                                final Object result = get();
                                if (result instanceof Exception) {
                                    final Exception ex = (Exception)result;

                                    CidsAppBackend.getInstance()
                                            .showError("Fehler beim Umbenennen", ex.getMessage(), ex);

                                    showRenameFailed(current);
                                } else if (current) {
                                    CidsAppBackend.getInstance().gotoKassenzeichen(newKassenzeichen);
                                } else {
                                    final String[] options = new String[] {
                                            "Umbenennen",
                                            "Laden",
                                            "Abbrechen"
                                        };
                                    final int option = JOptionPane.showOptionDialog(
                                            Main.this,
                                            "<html>Das Kassenzeichen wurde erfolgreich umbenannt.<br/><br/>Wie möchten Sie fortfahren?<br/>"
                                                    + "<ul>"
                                                    + "<li><b>"
                                                    + options[0]
                                                    + "</b>: ein weiteres Kassenzeichen umbennenen.</li>"
                                                    + "<li><b>"
                                                    + options[1]
                                                    + "</b>: das umbenannte Kassenzeichen laden.</li>",
                                            "Kassenzeichen erfolgreich umbenannt.",
                                            JOptionPane.YES_NO_CANCEL_OPTION,
                                            JOptionPane.QUESTION_MESSAGE,
                                            null,
                                            options,
                                            options[0]);
                                    if (JOptionPane.YES_OPTION == option) {
                                        showRenameKassenzeichen(current);
                                    } else if (JOptionPane.NO_OPTION == option) {
                                        CidsAppBackend.getInstance().gotoKassenzeichen(newKassenzeichen);
                                    }
                                }
                            } catch (final Exception ex) {
                                LOG.error("error while loading kassenzeichen " + newKassenzeichenNummer, ex);
                                CidsAppBackend.getInstance()
                                        .showError(
                                            "Fehler beim Umbenennen",
                                            "<html>Das Kassenzeichen konnte nicht umbenannt werden.",
                                            ex);
                            } finally {
                                refreshKassenzeichenButtons();
                            }
                        }
                    }.execute();
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mnuRenameAnyKZActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mnuRenameAnyKZActionPerformed
        showRenameKassenzeichen(false);
    }                                                                                  //GEN-LAST:event_mnuRenameAnyKZActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdSearchRisseActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdSearchRisseActionPerformed
        StaticSwingTools.showDialog(new VermessungsrissSuchDialog(this, false, ConnectionContext.createDeprecated()));
    }                                                                                  //GEN-LAST:event_cmdSearchRisseActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdSearchBaulastenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdSearchBaulastenActionPerformed
        StaticSwingTools.showDialog(new BaulastSuchDialog(this, false, ConnectionContext.createDeprecated()));
    }                                                                                      //GEN-LAST:event_cmdSearchBaulastenActionPerformed

    /**
     * DOCUMENT ME!
     */
    public void renameCurrentKassenzeichen() {
        showRenameKassenzeichen(true);
    }

    /**
     * DOCUMENT ME!
     */
    public void newKassenzeichen() {
        final String newKassenzeichennummer = JOptionPane.showInputDialog(
                this,
                "Geben Sie das neue Kassenzeichen ein:",
                "Neues Kassenzeichen",
                JOptionPane.QUESTION_MESSAGE);
        if (!((newKassenzeichennummer == null) || newKassenzeichennummer.equals(""))) {
            try {
                final int kassenzeichenNummer = new Integer(newKassenzeichennummer);

                new SwingWorker<Integer, Void>() {

                        @Override
                        protected Integer doInBackground() throws Exception {
                            // prüfen ob kassenzeichen bereits existiert
                            final CidsBean newBean = CidsAppBackend.getInstance()
                                        .loadKassenzeichenByNummer(kassenzeichenNummer);
                            if (newBean != null) {
                                return null;
                            } else {
                                final CidsBean kassenzeichen = createNewKassenzeichen(kassenzeichenNummer);
                                kassenzeichen.persist();
                                return kassenzeichenNummer;
                            }
                        }

                        @Override
                        protected void done() {
                            try {
                                final Integer kassenzeichenNummer = get();
                                if (kassenzeichenNummer != null) {
                                    CidsAppBackend.getInstance().gotoKassenzeichenAndEdit(newKassenzeichennummer);
                                } else {
                                    JOptionPane.showMessageDialog(
                                        Main.this,
                                        "Dieses Kassenzeichen existiert bereits.",
                                        "Fehler",
                                        JOptionPane.ERROR_MESSAGE);
                                }
                            } catch (final Exception ex) {
                                LOG.error(ex, ex);
                                CidsAppBackend.getInstance()
                                        .showError(
                                            "Fehler beim Erzeugen",
                                            "Beim Erzeugen des Kassenzeichens ist ein Fehler aufgetreten.",
                                            ex);
                            }
                        }
                    }.execute();
            } catch (final Exception ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "Kassenzeichen muss eine Zahl sein.",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   nummer  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private CidsBean createNewKassenzeichen(final int nummer) throws Exception {
        final Calendar cal = Calendar.getInstance();
        final java.sql.Date erfassungsdatum = new java.sql.Date(cal.getTimeInMillis());
        cal.add(Calendar.MONTH, 1);
        final SimpleDateFormat vDat = new SimpleDateFormat("yy/MM");
        final String veranlagungsdatum = "'" + vDat.format(cal.getTime()) + "'";

        final MetaObject kassenzeichenMo = CidsAppBackend.getInstance()
                    .getVerdisMetaClass(VerdisMetaClassConstants.MC_KASSENZEICHEN)
                    .getEmptyInstance();
        final MetaObject kanalanschlussMo = CidsAppBackend.getInstance()
                    .getVerdisMetaClass(VerdisMetaClassConstants.MC_KANALANSCHLUSS)
                    .getEmptyInstance();

        final CidsBean kassenzeichen = kassenzeichenMo.getBean();
        final CidsBean kanalanschluss = kanalanschlussMo.getBean();

        // TODO sobald die FEBs und DMS auf 8-stellige kassenzeichen umgestellt worden sind, kann diese zeile
        // rausfliegen
        kassenzeichen.setProperty(KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER_OLD, nummer);
        // --
        kassenzeichen.setProperty(KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER, nummer);
        kassenzeichen.setProperty(KassenzeichenPropertyConstants.PROP__KANALANSCHLUSS, kanalanschluss);
        kassenzeichen.setProperty(KassenzeichenPropertyConstants.PROP__DATUM_VERANLAGUNG, veranlagungsdatum);
        kassenzeichen.setProperty(KassenzeichenPropertyConstants.PROP__DATUM_ERFASSUNG, erfassungsdatum);
        kassenzeichen.setProperty(KassenzeichenPropertyConstants.PROP__BEMERKUNG, "");
        kassenzeichen.setProperty(KassenzeichenPropertyConstants.PROP__SPERRE, false);
        kassenzeichen.setProperty(KassenzeichenPropertyConstants.PROP__BEMERKUNG_SPERRE, "");
        kassenzeichen.setProperty(
            KassenzeichenPropertyConstants.PROP__LETZTE_AENDERUNG_TIMESTAMP,
            new Timestamp(new java.util.Date().getTime()));
        kassenzeichen.setProperty(
            KassenzeichenPropertyConstants.PROP__LETZTE_AENDERUNG_USER,
            getUserString());
        return kassenzeichen;
    }

    /**
     * DOCUMENT ME!
     */
    public void deleteKassenzeichen() {
        if ((kassenzeichenPanel.getShownKassenzeichen() != null)
                    && !(kassenzeichenPanel.getShownKassenzeichen().trim().equals(""))) {
            final int answer = JOptionPane.showConfirmDialog(
                    this,
                    "Wollen Sie wirklich das Kassenzeichen "
                            + kassenzeichenPanel.getShownKassenzeichen()
                            + " l\u00F6schen?",
                    "Kassenzeichen l\u00F6schen",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (answer == JOptionPane.YES_OPTION) {
                WaitDialog.getInstance().showDialog();
                new SwingWorker<Void, Void>() {

                        @Override
                        protected Void doInBackground() throws Exception {
                            WaitDialog.getInstance().startDeletingKassenzeichen(1);

                            final List<CidsBean> toDeleteBeans = new ArrayList<>();

                            // flaechen loeschen
                            final Collection<CidsBean> flaechenBeans = getRegenFlaechenTable().getAllBeans();
                            for (final CidsBean flaecheBean : flaechenBeans.toArray(new CidsBean[0])) {
                                getRegenFlaechenTable().removeBean(flaecheBean);
                            }
                            toDeleteBeans.addAll(flaechenBeans);

                            // fronten loeschen
                            final Collection<CidsBean> frontenBeans = getSRFrontenTable().getAllBeans();
                            for (final CidsBean frontBean : frontenBeans.toArray(new CidsBean[0])) {
                                getSRFrontenTable().removeBean(frontBean);
                            }
                            toDeleteBeans.addAll(frontenBeans);

                            // kassenzeichengeometrien loeschen
                            final Collection<CidsBean> kassenzeichenGeomtrieBeans =
                                kassenzeichenGeometrienPanel.getKassenzeichenGeometrienList().getAllBeans();
                            for (final CidsBean kassenzeichenGeomtrieBean
                                        : kassenzeichenGeomtrieBeans.toArray(new CidsBean[0])) {
                                kassenzeichenGeometrienPanel.getKassenzeichenGeometrienList()
                                        .removeBean(kassenzeichenGeomtrieBean);
                            }
                            toDeleteBeans.addAll(kassenzeichenGeomtrieBeans);

                            // kanalanschluss löschen
                            final CidsBean kanalanschlussBean = (CidsBean)kassenzeichenBean.getProperty(
                                    KassenzeichenPropertyConstants.PROP__KANALANSCHLUSS);
                            if (kanalanschlussBean != null) {
                                // befreiungen und erlaubnisse von kanalanschluss löschen
                                final Collection<CidsBean> befUndErlBeans = (Collection<CidsBean>)
                                    kanalanschlussBean.getProperty(
                                        KanalanschlussPropertyConstants.PROP__BEFREIUNGENUNDERLAUBNISSE);
                                for (final CidsBean befUndErlBean : befUndErlBeans.toArray(new CidsBean[0])) {
                                    befUndErlBeans.remove(befUndErlBean);
                                }
                                toDeleteBeans.addAll(befUndErlBeans);
                                toDeleteBeans.add(kanalanschlussBean);
                            }

                            // kassenzeichen selbst löschen
                            toDeleteBeans.add(kassenzeichenBean);

                            // und ab dafür!
                            WaitDialog.getInstance().startDeletingKassenzeichen(toDeleteBeans.size());

                            for (int index = 0; index < toDeleteBeans.size(); index++) {
                                final CidsBean toDeleteBean = toDeleteBeans.get(index);
                                WaitDialog.getInstance().progressKassenzeichen(index);
                                toDeleteBean.delete();
                                toDeleteBean.persist();
                            }
                            WaitDialog.getInstance().progressKassenzeichen(toDeleteBeans.size());
                            return null;
                        }

                        @Override
                        protected void done() {
                            try {
                                get();
                                releaseLocks();
                                CidsAppBackend.getInstance().clearCrossReferences();
                                CidsAppBackend.getInstance().setCidsBean(null);
                                setEditMode(false);
                            } catch (final Exception ex) {
                                JOptionPane.showMessageDialog(
                                    Main.this,
                                    "Das Kassenzeichen konnte nicht gelöscht werden.",
                                    "Fehler beim Löschen",
                                    JOptionPane.ERROR_MESSAGE);
                                LOG.error("error while deleting kassenzeichen", ex);
                            } finally {
                                WaitDialog.getInstance().dispose();
                            }
                        }
                    }.execute();
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  the command line arguments
     */
    public static void main(final String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(DefaultNavigatorExceptionHandler.getInstance());

        // LOOK AND FEEL
        try {
            javax.swing.UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
        } catch (Exception ex) {
            LOG.error("error while setting setLookAndFeel", ex);
        }

        // SPLASHSCREEN
        try {
            SPLASH = StaticStartupTools.showGhostFrame(FILEPATH_SCREEN, "verdis [Startup]");
        } catch (Exception e) {
            LOG.warn("Problem beim Darstellen des Pre-Loading-Frame", e);
        }

        // BEANSBINDING DEBUGGING
        try {
            if (StaticDebuggingTools.checkHomeForFile("cismetBeansbindingDebuggingOn")) { // NOI18N
                System.setProperty("cismet.beansdebugging", "true");                      // NOI18N
            }
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Fehler bei cismetBeansbindingDebuggingOn", e);
            }
        }

        // CUSTOM LOG4J
        try {
            if (StaticDebuggingTools.checkHomeForFile("cismetCustomLog4JConfigurationInDotVerdis")) {
                try {
                    org.apache.log4j.PropertyConfigurator.configure(DIRECTORYPATH_VERDIS + FILESEPARATOR
                                + "custom.log4j.properties");
                    LOG.info("CustomLoggingOn");
                } catch (final Exception ex) {
                    org.apache.log4j.PropertyConfigurator.configure(ClassLoader.getSystemResource(
                            "log4j.properties"));
                }
            } else {
                org.apache.log4j.PropertyConfigurator.configure(Main.class.getResource(
                        "log4j.properties"));
            }
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Fehler bei Log4J-Config", e);
            }
        }

        try {
            // LOAD PROPERTIES
            final AppPreferences appPreferences = loadAppPreferences();

            // LOGIN
            final CidsAuthentification cidsAuth = login(appPreferences);
            if (cidsAuth != null) {
                CidsAppBackend.init(cidsAuth.getProxy(), appPreferences);

                final Main main = getInstance();
                main.setLoggedIn(true);
                main.setUserString(cidsAuth.getUserString());
                main.setReadonly(cidsAuth.isReadOnly());
                main.init();

                // REMOVE SPLASHSCREEN
                if (SPLASH != null) {
                    main.setBounds(SPLASH.getBounds());
                    SPLASH.dispose();
                }

                main.setVisible(true);
                SPLASH = null;
            }
        } catch (Exception propEx) {
            LOG.fatal("Fehler beim Laden der Properties!", propEx);
            System.exit(1);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  b  DOCUMENT ME!
     */
    @Override
    public void setVisible(final boolean b) {
        super.setVisible(b);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean changesPending() {
        if ((kassenzeichenBean == null) || !editMode) {
            return false;
        }
        return (kassenzeichenBean.getMetaObject().getStatus() == MetaObject.MODIFIED)
                    || ((kassenzeichenBean.getMetaObject().getStatus() == MetaObject.NEW)
                        || kassenzeichenBean.hasArtificialChangeFlag());
    }

    /**
     * DOCUMENT ME!
     */
    public void selectionChanged() {
        refreshClipboardButtons();
        refreshItemButtons();
    }

    /**
     * DOCUMENT ME!
     */
    public void refreshClipboardButtons() {
        final AbstractClipboard clipboard = clipboards.get(CidsAppBackend.getInstance().getMode());
        final boolean isEditable = CidsAppBackend.getInstance().isEditable();
        if ((clipboard == null)) {
            cmdCopy.setEnabled(false);
            cmdPaste.setEnabled(false);
            cmdCut.setEnabled(false);
        } else {
            cmdCopy.setEnabled(clipboard.isCopyable());
            cmdPaste.setEnabled(isEditable && clipboard.isPastable());
            cmdCut.setEnabled(isEditable && clipboard.isCutable());
        }
    }

    /**
     * gets called on Mode changed to set the right tooltip for the clipboard buttons.
     */
    private void refreshClipboardButtonsToolTipText() {
        switch (CidsAppBackend.getInstance().getMode()) {
            case REGEN: {
                cmdCopy.setToolTipText("Fläche kopieren (Teileigentum erzeugen)");
                cmdPaste.setToolTipText("Fläche einfügen");
                cmdCut.setToolTipText("Fläche ausschneiden");
                break;
            }
            case SR: {
                cmdCopy.setToolTipText("Fronten kopieren");
                cmdPaste.setToolTipText("Fronten einfügen");
                cmdCut.setToolTipText("Fronten ausschneiden");
                break;
            }
            case KANALDATEN: {
                cmdCopy.setToolTipText("Befreiung/Erlaubnis kopieren");
                cmdPaste.setToolTipText("Befreiung/Erlaubnis einfügen");
                cmdCut.setToolTipText("Befreiung/Erlaubnis ausschneiden");
                break;
            }
            case ALLGEMEIN: {
                cmdCopy.setToolTipText("Kassenzeichengeometrie kopieren");
                cmdPaste.setToolTipText("Kassenzeichengeometrie einfügen");
                cmdCut.setToolTipText("Kassenzeichengeometrie ausschneiden");
                break;
            }
            default: {
                cmdCopy.setToolTipText("kopieren");
                cmdPaste.setToolTipText("einfügen");
                cmdCut.setToolTipText("ausschneiden");
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void refreshItemButtons() {
        final boolean isEditable = CidsAppBackend.getInstance().isEditable();

        if (isEditable) {
            List<CidsBean> selectedBeans = null;

            switch (CidsAppBackend.getInstance().getMode()) {
                case REGEN: {
                    boolean canAdd = true;
                    if (kassenzeichenBean != null) {
                        final Collection<CidsBean> flaechen = kassenzeichenBean.getBeanCollectionProperty(
                                KassenzeichenPropertyConstants.PROP__FLAECHEN);
                        if ((flaechen.size() == 1)
                                    && (VerdisUtils.PROPVAL_ART_VORLAEUFIGEVERANLASSUNG
                                        == (Integer)flaechen.iterator().next().getProperty(
                                            FlaechePropertyConstants.PROP__FLAECHENINFO
                                            + "."
                                            + FlaecheninfoPropertyConstants.PROP__FLAECHENART
                                            + "."
                                            + FlaechenartPropertyConstants.PROP__ID))) {
                            canAdd = false;
                        }
                    }
                    cmdAdd.setEnabled(canAdd);
                    selectedBeans = getRegenFlaechenTable().getSelectedBeans();
                }
                break;
                case SR: {
                    cmdAdd.setEnabled(true);
                    selectedBeans = getSRFrontenTable().getSelectedBeans();
                }
                break;
                case KANALDATEN: {
                    boolean canAdd = true;
                    canAdd = getBefreiungerlaubnisTable().getSelectedBeans().size() > 0;
                    cmdAdd.setEnabled(canAdd);
                    selectedBeans = getBefreiungerlaubnisGeometrieTable().getSelectedBeans();
                }
                break;
                case ALLGEMEIN: {
                    cmdAdd.setEnabled(false);
                    cmdRemove.setEnabled(false);
                    cmdUndo.setEnabled(false);
                }
                return;
            }
            final boolean hasItemsInSelection = (selectedBeans != null) && !selectedBeans.isEmpty();

            cmdRemove.setEnabled(hasItemsInSelection);
            cmdUndo.setEnabled(hasItemsInSelection);
        } else {
            cmdAdd.setEnabled(false);
            cmdRemove.setEnabled(false);
            cmdUndo.setEnabled(false);
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void refreshKassenzeichenButtons() {
        final boolean b = editMode;

        cmdOk.setEnabled(b && !aggValidator.getState().isError());
        cmdCancel.setEnabled(b);
        cmdDeleteKassenzeichen.setEnabled(b && (kassenzeichenBean != null));
        cmdNewKassenzeichen.setEnabled(!b && !readonly);
        cmdEditMode.setEnabled(!b && !readonly && (kassenzeichenBean != null));

        if (b) {
            btnTimeRecovery.setEnabled(!readonly && (kassenzeichenBean != null));
        } else {
            btnTimeRecovery.setEnabled(!readonly && (kassenzeichenBean == null)
                        && (kassenzeichenPanel.getSearchField() != null)
                        && !kassenzeichenPanel.getSearchField().isEmpty());
        }
        cmdRefreshEnumeration.setEnabled(b && CidsAppBackend.Mode.REGEN.equals(CidsAppBackend.getInstance().getMode()));
        cmdRecalculateArea.setEnabled(b && CidsAppBackend.Mode.REGEN.equals(CidsAppBackend.getInstance().getMode()));
        if (!b && CidsAppBackend.Mode.REGEN.equals(CidsAppBackend.getInstance().getMode())) {
            cmdPdf.setEnabled((kassenzeichenBean != null)
                        && !kassenzeichenBean.getBeanCollectionProperty(KassenzeichenPropertyConstants.PROP__FLAECHEN)
                        .isEmpty());
        } else if (!b && CidsAppBackend.Mode.SR.equals(CidsAppBackend.getInstance().getMode())) {
            cmdPdf.setEnabled((kassenzeichenBean != null)
                        && !kassenzeichenBean.getBeanCollectionProperty(KassenzeichenPropertyConstants.PROP__FRONTEN)
                        .isEmpty());
        } else {
            cmdPdf.setEnabled(false);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  editMode  DOCUMENT ME!
     */
    public void setEditMode(final boolean editMode) {
        CidsAppBackend.getInstance().setEditable(editMode);
        try {
            this.editMode = editMode;

            refreshKassenzeichenButtons();
            refreshClipboardButtons();
            refreshItemButtons();

            cmdSAPCheck.setEnabled(!editMode);
            kartenPanel.setEnabled(editMode);

//            final Iterator it = stores.iterator();
//            while (it.hasNext()) {
//                final Storable store = (Storable) it.next();
//                store.enableEditing(b);
//            }
            refreshLeftTitleBarColor();

            CidsAppBackend.getInstance().getMainMap().getMemRedo().clear();
            CidsAppBackend.getInstance().getMainMap().getMemUndo().clear();
        } catch (Exception e) {
            LOG.error("Fehler beim Wechseln in den EditMode", e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  b  DOCUMENT ME!
     */
    public void enableSave(final boolean b) {
        cmdOk.setEnabled(CidsAppBackend.getInstance().isEditable() && b);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   oldVeranlagungSummeMap  DOCUMENT ME!
     * @param   newVeranlagungSummeMap  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Map<String, Double> calculateChangedVeranlagungSummeMap(final Map<String, Double> oldVeranlagungSummeMap,
            final Map<String, Double> newVeranlagungSummeMap) {
        Map<String, Double> changedVeranlagungSummeMap;
        fillVeranlagungMaps(newVeranlagungSummeMap);

        if (CidsAppBackend.getInstance().getAppPreferences().isVeranlagungOnlyForChangedValues()) {
            changedVeranlagungSummeMap = new HashMap<>();
            for (final CidsBean veranlagungsgrundlage : veranlagungsgrundlageMap.values()) {
                final String bezeichner = (String)veranlagungsgrundlage.getProperty("veranlagungsnummer.bezeichner");
                if ((newVeranlagungSummeMap.get(bezeichner) - oldVeranlagungSummeMap.get(bezeichner)) != 0.0d) {
                    changedVeranlagungSummeMap.put(bezeichner, newVeranlagungSummeMap.get(bezeichner));
//                } else {
//                    changedVeranlagungSummeMap.put(bezeichner, null);
                }
            }
        } else {
            changedVeranlagungSummeMap = newVeranlagungSummeMap;
        }
        return changedVeranlagungSummeMap;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   oldVeranlagungSummeMap  DOCUMENT ME!
     * @param   newVeranlagungSummeMap  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private AssessmentDialog createAssessmentDialog(final Map<String, Double> oldVeranlagungSummeMap,
            final Map<String, Double> newVeranlagungSummeMap) {
        final Date datumJetzt = new Date();
        final Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(datumJetzt);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.MONTH, 1);
        final Date datumVeranlagung = cal.getTime();

        final AssessmentDialog assessmentDialog = new AssessmentDialog(CidsAppBackend.getInstance().getAppPreferences()
                        .isVeranlagungOnlyForChangedValues());
        assessmentDialog.setDatum(datumJetzt);
        assessmentDialog.setVeranlagungsdatum(datumVeranlagung);
        assessmentDialog.setBezeichners(veranlagungsnummern.keySet());
        assessmentDialog.setOldSchluesselSummeMap(oldVeranlagungSummeMap);
        assessmentDialog.setNewSchluesselSummeMap(newVeranlagungSummeMap);
        return assessmentDialog;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   zielKassenzeichennummern  kassenzeichennummerToBeanQuerverweisMap DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private Map<Integer, CidsBean> loadKassenzeichennummerToBeanMap(final Collection<Integer> zielKassenzeichennummern)
            throws Exception {
        // Map wird verwendet, damit identische kassenzeichen nicht unnötigerweise
        // doppelt geladen und  gespeichert werden
        final Map<Integer, CidsBean> kassenzeichenNummerToKassenzeichenMap = new HashMap();

        WaitDialog.getInstance().startLoadingKassenzeichen(zielKassenzeichennummern.size());
        int index = 0;
        for (final Integer zielKassenzeichennummer : zielKassenzeichennummern) {
            WaitDialog.getInstance().progressLoadingKassenzeichen(index);
            if (!kassenzeichenNummerToKassenzeichenMap.containsKey(zielKassenzeichennummer)) {
                final CidsBean querverweisZielKassenzeichen = CidsAppBackend.getInstance()
                            .loadKassenzeichenByNummer(zielKassenzeichennummer);
                kassenzeichenNummerToKassenzeichenMap.put(
                    zielKassenzeichennummer,
                    querverweisZielKassenzeichen);
            }
            index++;
        }
        WaitDialog.getInstance().progressLoadingKassenzeichen(zielKassenzeichennummern.size());

        return kassenzeichenNummerToKassenzeichenMap;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   kassenzeichenBean                    DOCUMENT ME!
     * @param   bezeichnungToKassenzeichennummerMap  DOCUMENT ME!
     * @param   crosslinkKassenzeichenBeanMap        DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private void createFlaecheCrosslinkKassenzeichenBeans(final CidsBean kassenzeichenBean,
            final Map<String, Collection<Integer>> bezeichnungToKassenzeichennummerMap,
            final Map<Integer, CidsBean> crosslinkKassenzeichenBeanMap) throws Exception {
        final List<CidsBean> savedFlaechen = kassenzeichenBean.getBeanCollectionProperty(
                KassenzeichenPropertyConstants.PROP__FLAECHEN);
        WaitDialog.getInstance().startCreateCrossLinks(savedFlaechen.size());
        for (int index = 0; index < savedFlaechen.size(); index++) {
            WaitDialog.getInstance().progressCreateCrossLinks(index);
            final CidsBean savedFlaeche = savedFlaechen.get(index);
            final String flaechenBezeichnung = (String)savedFlaeche.getProperty(
                    FlaechePropertyConstants.PROP__FLAECHENBEZEICHNUNG);

            // in der Map schauen ob für die abgespeicherte Flaechen ein Querverweis erzeugt werden soll
            if (bezeichnungToKassenzeichennummerMap.containsKey(flaechenBezeichnung)) {
                for (final int zielKassenzeichennummer : bezeichnungToKassenzeichennummerMap.get(flaechenBezeichnung)) {
                    // in welches Kassenzeichen soll der Querverweis erzeugt werden
                    final CidsBean querverweisKassenzeichenBean = crosslinkKassenzeichenBeanMap.get(
                            zielKassenzeichennummer);
                    if (querverweisKassenzeichenBean != null) {
                        final Collection<CidsBean> flaechenOfQuerverweisKassenzeichen =
                            querverweisKassenzeichenBean.getBeanCollectionProperty(
                                KassenzeichenPropertyConstants.PROP__FLAECHEN);

                        // Neue Flaeche mit der selben Flaechenart wie die gespeicherte Flaeche erstellen.
                        final CidsBean flaechenartBean = (CidsBean)savedFlaeche.getProperty(
                                FlaechePropertyConstants.PROP__FLAECHENINFO
                                        + "."
                                        + FlaecheninfoPropertyConstants.PROP__FLAECHENART);
                        final CidsBean querverweisFlaecheBean = RegenFlaechenTable.createNewFlaecheBean(
                                flaechenartBean,
                                flaechenOfQuerverweisKassenzeichen,
                                null);

                        // Überschreiben der FlaechenInfo zum Erzeugen des Querverweises auf der neuen Flaeche.
                        querverweisFlaecheBean.setProperty(
                            FlaechePropertyConstants.PROP__FLAECHENINFO,
                            savedFlaeche.getProperty(FlaechePropertyConstants.PROP__FLAECHENINFO));

                        flaechenOfQuerverweisKassenzeichen.add(querverweisFlaecheBean);
                    } else {
                        LOG.error("kassenzeichen " + zielKassenzeichennummer + " konnte nicht geladen werden");
                    }
                }
            }
        }
        WaitDialog.getInstance().progressCreateCrossLinks(savedFlaechen.size());
    }

    /**
     * DOCUMENT ME!
     *
     * @param   kassenzeichenBean              DOCUMENT ME!
     * @param   crosslinkKassenzeichenBeanMap  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private void createGeometrieCrosslinkKassenzeichenBeans(final CidsBean kassenzeichenBean,
            final Map<Integer, CidsBean> crosslinkKassenzeichenBeanMap) throws Exception {
        final List<CidsBean> savedGeoms = kassenzeichenBean.getBeanCollectionProperty(
                KassenzeichenPropertyConstants.PROP__KASSENZEICHEN_GEOMETRIEN);

        final Collection<CrossReference> crossrefs = CidsAppBackend.getInstance().getFlaechenCrossReferences();

        final Collection<CidsBean> notAllSameKzBeans = new ArrayList<>(crossrefs.size());
        WaitDialog.getInstance().startCreateCrossLinks(crossrefs.size());

        final Map<Integer, CidsBean> crosslinkKassenzeichenBeanTmpMap = new HashMap<>(crossrefs.size());

        final Collection<CidsBean> crossKassenzeichenList = new ArrayList<>();
        // alle (Flächen-)Querverweise durchgehen
        int index = 0;
        for (final CrossReference crossref : crossrefs.toArray(new CrossReference[0])) {
            WaitDialog.getInstance().progressCreateCrossLinks(index);
            final CidsBean crossKassenzeichen;
            final Integer crossKassenzeichenNummer = crossref.getEntityToKassenzeichen();
            if (crosslinkKassenzeichenBeanMap.containsKey(crossKassenzeichenNummer)) {
                crossKassenzeichen = crosslinkKassenzeichenBeanMap.get(crossKassenzeichenNummer);
            } else {
                crossKassenzeichen = CidsAppBackend.getInstance().loadKassenzeichenByNummer(crossKassenzeichenNummer);
            }
            crosslinkKassenzeichenBeanTmpMap.put(crossKassenzeichenNummer, crossKassenzeichen);

            crossKassenzeichenList.add(crossKassenzeichen);
            final List<CidsBean> crossGeoms = crossKassenzeichen.getBeanCollectionProperty(
                    KassenzeichenPropertyConstants.PROP__KASSENZEICHEN_GEOMETRIEN);

            // selbe Anzahl an KZ-Geometrien vorhanden ?
            boolean allSame = crossGeoms.size() == savedGeoms.size();
            for (final CidsBean savedGeom : savedGeoms) {
                if (!allSame) {
                    break;
                }
                // Querverweise-Kz-Geometrie enthält Kz-Geometrie ?
                allSame = crossGeoms.contains(savedGeom);
            }

            // nicht alle Querverweis-Kz-Geometrien identisch mit allen KZ-Geometrien ?
            if (!allSame) {
                notAllSameKzBeans.add(crossKassenzeichen);
            }
            index++;
        }

        if (!notAllSameKzBeans.isEmpty()) {
            final int answer = JOptionPane.showConfirmDialog(
                    this,
                    "Soll die Georeferenzierung auf alle verbundenen Kassenzeichen (Querverweise) übernommen werden?",
                    "Georeferenzierung - Querverweise",
                    JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                for (final CidsBean crossKassenzeichen : crossKassenzeichenList) {
                    final List<CidsBean> crossGeoms = crossKassenzeichen.getBeanCollectionProperty(
                            KassenzeichenPropertyConstants.PROP__KASSENZEICHEN_GEOMETRIEN);
                    // Querverweis-Kz-Geometrien ersetzen mit KZ-Geometrien
                    crossGeoms.clear();
                    crossGeoms.addAll(savedGeoms);
                }
                crosslinkKassenzeichenBeanMap.putAll(crosslinkKassenzeichenBeanTmpMap);
            }
        }
        WaitDialog.getInstance().progressCreateCrossLinks(crossKassenzeichenList.size());
    }

    /**
     * DOCUMENT ME!
     *
     * @param   kassenzeichenBean               DOCUMENT ME!
     * @param   nummerToKassenzeichennummerMap  DOCUMENT ME!
     * @param   crosslinkKassenzeichenBeanMap   DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private void createFrontenCrosslinkKassenzeichenBeans(final CidsBean kassenzeichenBean,
            final Map<Integer, Collection<Integer>> nummerToKassenzeichennummerMap,
            final Map<Integer, CidsBean> crosslinkKassenzeichenBeanMap) throws Exception {
        final List<CidsBean> savedFronten = kassenzeichenBean.getBeanCollectionProperty(
                KassenzeichenPropertyConstants.PROP__FRONTEN);
        WaitDialog.getInstance().startCreateCrossLinks(savedFronten.size());
        for (int index = 0; index < savedFronten.size(); index++) {
            WaitDialog.getInstance().progressCreateCrossLinks(index);
            final CidsBean savedFront = savedFronten.get(index);
            final Integer frontNummer = (Integer)savedFront.getProperty(
                    FrontPropertyConstants.PROP__NUMMER);

            // in der Map schauen ob für die abgespeicherte Front ein Querverweis erzeugt werden soll
            if (nummerToKassenzeichennummerMap.containsKey(frontNummer)) {
                for (final int zielKassenzeichennummer : nummerToKassenzeichennummerMap.get(frontNummer)) {
                    // in welches Kassenzeichen soll der Querverweis erzeugt werden
                    final CidsBean querverweisKassenzeichenBean = crosslinkKassenzeichenBeanMap.get(
                            zielKassenzeichennummer);
                    if (querverweisKassenzeichenBean != null) {
                        final Collection<CidsBean> frontenOfQuerverweisKassenzeichen =
                            querverweisKassenzeichenBean.getBeanCollectionProperty(
                                KassenzeichenPropertyConstants.PROP__FRONTEN);

                        // Neue Flaeche mit der selben Flaechenart wie die gespeicherte Flaeche erstellen.
                        final CidsBean strasseBean = (CidsBean)savedFront.getProperty(
                                FrontPropertyConstants.PROP__FRONTINFO
                                        + "."
                                        + FrontinfoPropertyConstants.PROP__STRASSE);
                        final CidsBean lageBean = (CidsBean)savedFront.getProperty(
                                FrontPropertyConstants.PROP__FRONTINFO
                                        + "."
                                        + FrontinfoPropertyConstants.PROP__LAGE_SR);
                        final CidsBean reinigungBean = (CidsBean)savedFront.getProperty(
                                FrontPropertyConstants.PROP__FRONTINFO
                                        + "."
                                        + FrontinfoPropertyConstants.PROP__SR_KLASSE_OR);
                        final CidsBean querverweisFrontBean = SRFrontenTable.createNewFrontBean(
                                strasseBean,
                                lageBean,
                                reinigungBean,
                                frontenOfQuerverweisKassenzeichen,
                                null);

                        // Überschreiben der FrontInfo zum Erzeugen des Querverweises auf der neuen Front.
                        querverweisFrontBean.setProperty(
                            FrontPropertyConstants.PROP__FRONTINFO,
                            savedFront.getProperty(FrontPropertyConstants.PROP__FRONTINFO));

                        frontenOfQuerverweisKassenzeichen.add(querverweisFrontBean);
                    } else {
                        LOG.error("kassenzeichen " + zielKassenzeichennummer + " konnte nicht geladen werden");
                    }
                }
            }
        }
        WaitDialog.getInstance().progressCreateCrossLinks(savedFronten.size());
    }

    /**
     * DOCUMENT ME!
     *
     * @param   kassenzeichenBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public CidsBean saveKassenzeichen(final CidsBean kassenzeichenBean) throws Exception {
        WaitDialog.getInstance().startSavingKassenzeichen(1);

        final Collection<CidsBean> flaecheBeans = kassenzeichenBean.getBeanCollectionProperty(
                KassenzeichenPropertyConstants.PROP__FLAECHEN);
        // set change date for flaechen
        for (final CidsBean flaecheBean : flaecheBeans) {
            if ((flaecheBean != null)
                        && ((flaecheBean.getMetaObject().getStatus() == MetaObject.MODIFIED)
                            || (flaecheBean.getMetaObject().getStatus() == MetaObject.NEW))) {
                flaecheBean.setProperty(
                    FlaechePropertyConstants.PROP__DATUM_AENDERUNG,
                    new java.sql.Date(Calendar.getInstance().getTime().getTime()));
            }
        }

        final Map<CidsBean, Collection<Integer>> flaecheToCrosslinknummerMap = CidsAppBackend.getInstance()
                    .getFlaecheToKassenzeichenQuerverweisMap();
        final Map<String, Collection<Integer>> flaechebezeichnungToKassenzeichennummerMap =
            createFlaechebezeichnungToKassenzeichennummerMap(flaecheToCrosslinknummerMap);

        final Map<CidsBean, Collection<Integer>> frontToCrosslinknummerMap = CidsAppBackend.getInstance()
                    .getFrontToKassenzeichenQuerverweisMap();
        final Map<Integer, Collection<Integer>> frontNummerToKassenzeichennummerMap =
            createFrontnummerToKassenzeichennummerMap(
                frontToCrosslinknummerMap);

        final CidsBean persistedKassenzeichenBean = persistKassenzeichen(kassenzeichenBean);

        final Collection<Integer> kassenzeichennummern = new ArrayList<>();
        for (final Collection<Integer> flaechecrosslinks : flaecheToCrosslinknummerMap.values()) {
            kassenzeichennummern.addAll(flaechecrosslinks);
        }
        for (final Collection<Integer> frontcrosslinks : frontToCrosslinknummerMap.values()) {
            kassenzeichennummern.addAll(frontcrosslinks);
        }

        final Map<Integer, CidsBean> kassenzeichennummerToBeanMap = loadKassenzeichennummerToBeanMap(
                kassenzeichennummern);

        createFlaecheCrosslinkKassenzeichenBeans(
            persistedKassenzeichenBean,
            flaechebezeichnungToKassenzeichennummerMap,
            kassenzeichennummerToBeanMap);

        // (flächen-)querverweise vorhanden?
        boolean allTeileigentum = !CidsAppBackend.getInstance().getFlaechenCrossReferences().isEmpty();
        for (final CidsBean flaecheBean : flaecheBeans) {
            if (!allTeileigentum) {
                break;
            }
            if (flaecheBean != null) {
                // Anteil gesetzt ?
                allTeileigentum = flaecheBean.getProperty(FlaechePropertyConstants.PROP__ANTEIL) != null;
            }
        }

        // alle Flächen haben einen Anteil gesetzt ?
        if (allTeileigentum) {
            // Geometrie-Querverweise anlegen (falls nötig)
            createGeometrieCrosslinkKassenzeichenBeans(persistedKassenzeichenBean, kassenzeichennummerToBeanMap);
        }

        createFrontenCrosslinkKassenzeichenBeans(
            persistedKassenzeichenBean,
            frontNummerToKassenzeichennummerMap,
            kassenzeichennummerToBeanMap);

        saveCrosslinkKassenzeichenBeans(kassenzeichennummerToBeanMap.values());

        // querverweise wurden angelegt, map muss leer gemacht werden
        // damit beim nächsten Speichern nicht nochmal die selben
        // Querverweise angelegt werden
        flaecheToCrosslinknummerMap.clear();
        frontToCrosslinknummerMap.clear();

        return persistedKassenzeichenBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  crosslinkKassenzeichenBeans  DOCUMENT ME!
     */
    private void saveCrosslinkKassenzeichenBeans(final Collection<CidsBean> crosslinkKassenzeichenBeans) {
        // Kassenzeichen speichern in denen neue Querverweise erzeugt wurden
        WaitDialog.getInstance().startSavingKassenzeichen(crosslinkKassenzeichenBeans.size());
        final int index = 0;
        for (final CidsBean querverweisKassenzeichenBean : crosslinkKassenzeichenBeans) {
            WaitDialog.getInstance().progressSavingKassenzeichen(index);
            try {
                persistKassenzeichen(querverweisKassenzeichenBean);
            } catch (final Exception ex) {
                CidsAppBackend.getInstance()
                        .showError(
                            "Fehler beim Schreiben",
                            "Beim Speichern des Querverweises "
                            + querverweisKassenzeichenBean
                            + " kam es zu einem Fehler.",
                            ex);
            }
        }
        WaitDialog.getInstance().progressSavingKassenzeichen(crosslinkKassenzeichenBeans.size());
    }

    /**
     * Map merkt sich: Die gespeicherte Flaeche mit der Bezeichnung x soll einen Querverweis im Kassenzeichen y anlegen
     * MUSS VOR PERSIST GESCHEHEN
     *
     * @param   flaecheToKassenzeichenQuerverweisMap  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static HashMap<String, Collection<Integer>> createFlaechebezeichnungToKassenzeichennummerMap(
            final Map<CidsBean, Collection<Integer>> flaecheToKassenzeichenQuerverweisMap) {
        final HashMap<String, Collection<Integer>> bezeichnungToKassenzeichennummerMap = new HashMap<>();
        for (final CidsBean unsavedFlaecheBean : flaecheToKassenzeichenQuerverweisMap.keySet()) {
            final String flaechenBezeichnung = (String)unsavedFlaecheBean.getProperty(
                    FlaechePropertyConstants.PROP__FLAECHENBEZEICHNUNG);
            final Collection<Integer> querverweisKassenzeichenNummer = flaecheToKassenzeichenQuerverweisMap.get(
                    unsavedFlaecheBean);
            bezeichnungToKassenzeichennummerMap.put(flaechenBezeichnung, querverweisKassenzeichenNummer);
        }
        return bezeichnungToKassenzeichennummerMap;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   frontToKassenzeichenQuerverweisMap  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static HashMap<Integer, Collection<Integer>> createFrontnummerToKassenzeichennummerMap(
            final Map<CidsBean, Collection<Integer>> frontToKassenzeichenQuerverweisMap) {
        final HashMap<Integer, Collection<Integer>> nummerToKassenzeichennummerMap = new HashMap<>();
        for (final CidsBean unsavedFrontBean : frontToKassenzeichenQuerverweisMap.keySet()) {
            final Integer frontNummer = (Integer)unsavedFrontBean.getProperty(
                    FrontPropertyConstants.PROP__NUMMER);
            final Collection<Integer> querverweisKassenzeichenNummer = frontToKassenzeichenQuerverweisMap.get(
                    unsavedFrontBean);
            nummerToKassenzeichennummerMap.put(frontNummer, querverweisKassenzeichenNummer);
        }
        return nummerToKassenzeichennummerMap;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   changedVeranlagungSummeMap  DOCUMENT ME!
     * @param   datum                       DOCUMENT ME!
     * @param   veranlagungsdatum           DOCUMENT ME!
     * @param   kassenzeichenBean           DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private void saveAssessement(final Map<String, Double> changedVeranlagungSummeMap,
            final Date datum,
            final Date veranlagungsdatum,
            final CidsBean kassenzeichenBean) throws Exception {
        final MetaObject veranlagungseintragMo = CidsAppBackend.getInstance()
                    .getVerdisMetaClass(VerdisMetaClassConstants.MC_VERANLAGUNGSEINTRAG)
                    .getEmptyInstance();
        final MetaObject veranlagungspostenMo = CidsAppBackend.getInstance()
                    .getVerdisMetaClass(VerdisMetaClassConstants.MC_VERANLAGUNGSPOSTEN)
                    .getEmptyInstance();

        final MetaObject veranlagungMo = CidsAppBackend.getInstance()
                    .getVerdisMetaClass(VerdisMetaClassConstants.MC_VERANLAGUNG)
                    .getEmptyInstance();
        final CidsBean veranlagungBean = veranlagungMo.getBean();
        veranlagungBean.setProperty(VeranlagungPropertyConstants.PROP__KASSENZEICHEN, kassenzeichenBean);
        veranlagungBean.setProperty(
            VeranlagungPropertyConstants.PROP__DATUM,
            new java.sql.Date(datum.getTime()));
        veranlagungBean.setProperty(
            VeranlagungPropertyConstants.PROP__VERANLAGUNGSDATUM,
            new java.sql.Date(veranlagungsdatum.getTime()));
        veranlagungBean.setProperty(
            VeranlagungPropertyConstants.PROP__G_200,
            changedVeranlagungSummeMap.get("null--200"));
        veranlagungBean.setProperty(
            VeranlagungPropertyConstants.PROP__G_100,
            changedVeranlagungSummeMap.get("null--100"));
        veranlagungBean.setProperty(
            VeranlagungPropertyConstants.PROP__G305,
            changedVeranlagungSummeMap.get("A1-305"));
        veranlagungBean.setProperty(
            VeranlagungPropertyConstants.PROP__G306,
            changedVeranlagungSummeMap.get("Z1-306"));
        veranlagungBean.setProperty(
            VeranlagungPropertyConstants.PROP__G310,
            changedVeranlagungSummeMap.get("A1V-310"));
        veranlagungBean.setProperty(
            VeranlagungPropertyConstants.PROP__G311,
            changedVeranlagungSummeMap.get("Z1V-311"));
        veranlagungBean.setProperty(
            VeranlagungPropertyConstants.PROP__G315,
            changedVeranlagungSummeMap.get("A2-315"));
        veranlagungBean.setProperty(
            VeranlagungPropertyConstants.PROP__G320,
            changedVeranlagungSummeMap.get("A2V-320"));
        veranlagungBean.setProperty(
            VeranlagungPropertyConstants.PROP__G321,
            changedVeranlagungSummeMap.get("A3-321"));
        veranlagungBean.setProperty(
            VeranlagungPropertyConstants.PROP__G322,
            changedVeranlagungSummeMap.get("A3V-322"));
        veranlagungBean.setProperty(
            VeranlagungPropertyConstants.PROP__G325,
            changedVeranlagungSummeMap.get("B1-325"));
        veranlagungBean.setProperty(
            VeranlagungPropertyConstants.PROP__G330,
            changedVeranlagungSummeMap.get("B1V-330"));
        veranlagungBean.setProperty(
            VeranlagungPropertyConstants.PROP__G335,
            changedVeranlagungSummeMap.get("B2-335"));
        veranlagungBean.setProperty(
            VeranlagungPropertyConstants.PROP__G340,
            changedVeranlagungSummeMap.get("B2V-340"));
        veranlagungBean.setProperty(
            VeranlagungPropertyConstants.PROP__G345,
            changedVeranlagungSummeMap.get("D1-345"));
        veranlagungBean.setProperty(
            VeranlagungPropertyConstants.PROP__G350,
            changedVeranlagungSummeMap.get("D2-350"));
        veranlagungBean.setProperty(
            VeranlagungPropertyConstants.PROP__G361,
            changedVeranlagungSummeMap.get("P1-361"));
        veranlagungBean.setProperty(
            VeranlagungPropertyConstants.PROP__G362,
            changedVeranlagungSummeMap.get("P2-362"));
        veranlagungBean.setProperty(
            VeranlagungPropertyConstants.PROP__G710,
            changedVeranlagungSummeMap.get("710-DF"));
        veranlagungBean.setProperty(
            VeranlagungPropertyConstants.PROP__G715,
            changedVeranlagungSummeMap.get("715-GDF"));
        veranlagungBean.setProperty(
            VeranlagungPropertyConstants.PROP__G720,
            changedVeranlagungSummeMap.get("720-VF"));
        veranlagungBean.setProperty(
            VeranlagungPropertyConstants.PROP__G725,
            changedVeranlagungSummeMap.get("725-VFÖ"));
        veranlagungBean.setProperty(
            VeranlagungPropertyConstants.PROP__G730,
            changedVeranlagungSummeMap.get("730-Va-über"));
        veranlagungBean.setProperty(
            VeranlagungPropertyConstants.PROP__G740,
            changedVeranlagungSummeMap.get("740-VFS"));
        veranlagungBean.setProperty(
            VeranlagungPropertyConstants.PROP__G999,
            changedVeranlagungSummeMap.get("999-Rest"));

        veranlagungBean.persist();

        final CidsBean veranlagungseintragBean = veranlagungseintragMo.getBean();
        veranlagungseintragBean.setProperty(
            VeranlagungseintragPropertyConstants.PROP__KASSENZEICHEN,
            kassenzeichenBean);
        veranlagungseintragBean.setProperty(
            VeranlagungseintragPropertyConstants.PROP__DATUM,
            new java.sql.Date(datum.getTime()));
        veranlagungseintragBean.setProperty(
            VeranlagungseintragPropertyConstants.PROP__VERANLAGUNGSDATUM,
            new java.sql.Date(veranlagungsdatum.getTime()));

        for (final String key : changedVeranlagungSummeMap.keySet()) {
            final CidsBean veranlagungspostenBean = veranlagungspostenMo.getBean();
            veranlagungspostenBean.setProperty(
                VeranlagungspostenPropertyConstants.PROP__VERANLAGUNGSNUMMER,
                veranlagungsnummern.get(key));
            veranlagungspostenBean.setProperty(
                VeranlagungspostenPropertyConstants.PROP__WERT,
                changedVeranlagungSummeMap.get(key));

            veranlagungseintragBean.getBeanCollectionProperty(VeranlagungseintragPropertyConstants.PROP__POSTEN)
                    .add(veranlagungspostenBean);
        }

        veranlagungseintragBean.persist();
    }

    /**
     * DOCUMENT ME!
     */
    public void saveKassenzeichenAndAssessement() {
        final Map<String, Double> oldVeranlagungSummeMap = veranlagungSummeMap;
        final Map<String, Double> newVeranlagungSummeMap = new HashMap<>();

        final Map<String, Double> changedVeranlagungSummeMap = calculateChangedVeranlagungSummeMap(
                oldVeranlagungSummeMap,
                newVeranlagungSummeMap);

        final AssessmentDialog assessmentDialog = createAssessmentDialog(
                oldVeranlagungSummeMap,
                newVeranlagungSummeMap);
        StaticSwingTools.showDialog(assessmentDialog, true);
        final int returnType = assessmentDialog.getReturnType();

        if (returnType != AssessmentDialog.RETURN_CANCEL) {
            WaitDialog.getInstance().showDialog();

            new SwingWorker<CidsBean, Void>() {

                    @Override
                    protected CidsBean doInBackground() throws Exception {
                        final CidsBean savedKassenzeichenBean = saveKassenzeichen(kassenzeichenBean);
                        if (returnType == AssessmentDialog.RETURN_WITH_ASSESSEMENT) {
                            try {
                                saveAssessement(
                                    changedVeranlagungSummeMap,
                                    assessmentDialog.getDatum(),
                                    assessmentDialog.getVeranlagungsdatum(),
                                    savedKassenzeichenBean);

                                savedKassenzeichenBean.setProperty(
                                    KassenzeichenPropertyConstants.PROP__VERANLAGUNGSZETTEL,
                                    null);
                                savedKassenzeichenBean.setProperty(KassenzeichenPropertyConstants.PROP__SPERRE, false);
                                savedKassenzeichenBean.setProperty(
                                    KassenzeichenPropertyConstants.PROP__BEMERKUNG_SPERRE,
                                    "");
                            } catch (Exception ex) {
                                LOG.error("error while storing veranlagung", ex);
                            }
                        } else {
                            try {
                                final String veranlagungszettel = assessmentDialog.getZettelHtml();
                                savedKassenzeichenBean.setProperty(
                                    KassenzeichenPropertyConstants.PROP__VERANLAGUNGSZETTEL,
                                    veranlagungszettel);
                                savedKassenzeichenBean.setProperty(KassenzeichenPropertyConstants.PROP__SPERRE, true);
                                savedKassenzeichenBean.setProperty(
                                    KassenzeichenPropertyConstants.PROP__BEMERKUNG_SPERRE,
                                    "beim letzten Speichern nicht veranlagt");
                            } catch (Exception ex) {
                                LOG.error("error while storing veranlagungszettel", ex);
                            }
                        }

                        final CidsBean assessedKassenzeichenBean = persistKassenzeichen(savedKassenzeichenBean);
                        releaseLocks();
                        return assessedKassenzeichenBean;
                    }

                    @Override
                    protected void done() {
                        try {
                            final CidsBean persistedKassenzeichenBean = get();
                            setEditMode(false);
                            reloadKassenzeichen(persistedKassenzeichenBean);
                        } catch (final Exception ex) {
                            LOG.error("error during persist", ex);
                            CidsAppBackend.getInstance()
                                    .showError(
                                        "Fehler beim Schreiben",
                                        "Beim Speichern des Kassenzeichens kam es zu einem Fehler.",
                                        ex);
                        } finally {
                            WaitDialog.getInstance().dispose();
                        }
                    }
                }.execute();
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void disableKassenzeichenCmds() {
        cmdEditMode.setEnabled(false);
        cmdOk.setEnabled(false);
        cmdCancel.setEnabled(false);
        cmdDeleteKassenzeichen.setEnabled(false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   kassenzeichenBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private CidsBean persistKassenzeichen(final CidsBean kassenzeichenBean) throws Exception {
        try {
            kassenzeichenBean.setProperty(
                KassenzeichenPropertyConstants.PROP__LETZTE_AENDERUNG_TIMESTAMP,
                new Timestamp(new java.util.Date().getTime()));
            kassenzeichenBean.setProperty(
                KassenzeichenPropertyConstants.PROP__LETZTE_AENDERUNG_USER,
                getUserString());
        } catch (Exception ex) {
            LOG.error("error while setting letzte aenderung", ex);
        }
        return kassenzeichenBean.persist();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kassenzeichenBean  DOCUMENT ME!
     */
    public void reloadKassenzeichen(final CidsBean kassenzeichenBean) {
        CidsAppBackend.getInstance().setCidsBean(kassenzeichenBean);

        final String refreshingKassenzeichen = Integer.toString((Integer)kassenzeichenBean.getProperty(
                    KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER));
        if (refreshingKassenzeichen == null) {
            getKassenzeichenPanel().refresh();
        } else {
            CidsAppBackend.getInstance().gotoKassenzeichen(refreshingKassenzeichen);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public KassenzeichenPanel getKassenzeichenPanel() {
        return kassenzeichenPanel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isInEditMode() {
        return editMode;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getUserString() {
        return userString;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  parent  DOCUMENT ME!
     */
    @Override
    public void configure(final Element parent) {
        try {
            final Element verdis = parent.getChild("verdis");
            final String modeString = verdis.getAttribute("mode").getValue();
            final CidsAppBackend.Mode mode = CidsAppBackend.Mode.valueOf(modeString);
            CidsAppBackend.getInstance().setMode(mode);
        } catch (Exception e) {
            CidsAppBackend.getInstance().setMode(CidsAppBackend.Mode.ALLGEMEIN);
            LOG.warn("Problem beim Setzen des Modes", e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Element getConfiguration() {
        try {
            final Element verdis = new Element("verdis");
            verdis.setAttribute("mode", CidsAppBackend.getInstance().getMode().name());
            return verdis;
        } catch (Exception e) {
            LOG.error("Fehler beim Schreiben der Config", e);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  parent  DOCUMENT ME!
     */
    @Override
    public void masterConfigure(final Element parent) {
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static MappingComponent getMappingComponent() {
        return CidsAppBackend.getInstance().getMainMap();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  userString  DOCUMENT ME!
     */
    public void setUserString(final String userString) {
        this.userString = userString;
        refreshTitle();
    }

    /**
     * DOCUMENT ME!
     */
    public void refreshTitle() {
        String zusatz = " [" + userString + "]";

        if (getKassenzeichenPanel() != null) {
            final String kassenzeichen = getKassenzeichenPanel().getShownKassenzeichen();
            if ((kassenzeichen != null) && (kassenzeichen.length() > 1)) {
                zusatz += " " + kassenzeichen;
            }
        }
        if (totd != null) {
            zusatz += " - " + totd;
        }
        setTitle("verdis" + zusatz);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Image getBannerImage() {
        return BANNER_IMAGE;
    }

    /**
     * DOCUMENT ME!
     */
    @Override
    public void dispose() {
        try {
            StaticStartupTools.saveScreenshotOfFrame(this, FILEPATH_SCREEN);
        } catch (Exception ex) {
            LOG.fatal("Fehler beim Capturen des App-Inhaltes", ex);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Dispose: Verdis wird beendet.");
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Dispose: layout wird gespeichert.");
        }
        // Inserting Docking Window functionalty (Sebastian) 24.07.07
        configurationManager.writeConfiguration();
        saveLayout(FILEPATH_LAYOUT + "." + currentMode);
        saveConfig(FILEPATH_MAP + "." + currentMode);
        allClipboardsDeleteStoreFile();
        super.dispose();
        System.exit(0);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isLoggedIn() {
        return loggedIn;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  loggedIn  DOCUMENT ME!
     */
    public void setLoggedIn(final boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public CidsBean getCidsBean() {
        return kassenzeichenBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   kassenzeichenBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Validator getValidatorKassenzeichen(final CidsBean kassenzeichenBean) {
        final AggregatedValidator aggVal = new AggregatedValidator();
        aggVal.add(kassenzeichenPanel.getValidator());
        aggVal.add(getRegenFlaechenTable().getValidator());
        aggVal.add(getSRFrontenTable().getValidator());
        aggVal.validate();
        return aggVal;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        kassenzeichenBean = cidsBean;

        if (cidsBean != null) {
            fillVeranlagungMaps(veranlagungSummeMap);
        }
        mnuRenameCurrentKZ.setEnabled(cidsBean != null);

        aggValidator.validate();
    }

    /**
     * DOCUMENT ME!
     */
    private void initVeranlagung() {
        final MetaClass veranlagungsgrundlageMc = CidsAppBackend.getInstance()
                    .getVerdisMetaClass(VerdisMetaClassConstants.MC_VERANLAGUNGSGRUNDLAGE);
        final MetaClass veranlagungsnummerMc = CidsAppBackend.getInstance()
                    .getVerdisMetaClass(VerdisMetaClassConstants.MC_VERANLAGUNGSNUMMER);
        final MetaObject[] veranlagungsgrundlageMos = CidsAppBackend.getInstance()
                    .getMetaObject(""
                        + "SELECT " + veranlagungsgrundlageMc.getId() + ", grundlage."
                        + VeranlagungsgrundlagePropertyConstants.PROP__ID + " "
                        + "FROM " + veranlagungsgrundlageMc.getTableName() + " AS grundlage, "
                        + veranlagungsnummerMc.getTableName() + " AS nummer "
                        + "WHERE grundlage." + VeranlagungsgrundlagePropertyConstants.PROP__VERANLAGUNGSNUMMER
                        + " = nummer." + VeranlagungsnummerPropertyConstants.PROP__ID + " "
                        + "ORDER BY nummer." + VeranlagungsnummerPropertyConstants.PROP__BEZEICHNER,
                        VerdisConstants.DOMAIN);

        final MetaObject[] veranlagungsnummerMos = CidsAppBackend.getInstance()
                    .getMetaObject("SELECT " + veranlagungsnummerMc.getId() + ", "
                        + VeranlagungsnummerPropertyConstants.PROP__ID + " "
                        + "FROM " + veranlagungsnummerMc.getTableName(),
                        VerdisConstants.DOMAIN);

        for (final MetaObject veranlagungsgrundlageMo : veranlagungsgrundlageMos) {
            final CidsBean veranlagungsgrundlageBean = veranlagungsgrundlageMo.getBean();
            final Integer flaechenart = (Integer)veranlagungsgrundlageBean.getProperty("flaechenart.id");
            final Integer anschlussgrad = (Integer)veranlagungsgrundlageBean.getProperty("anschlussgrad.id");
            final String mapKey = Integer.toString(flaechenart) + "-" + Integer.toString(anschlussgrad);

            veranlagungsgrundlageMap.put(mapKey, veranlagungsgrundlageBean);
        }

        for (final MetaObject veranlagungsnummerMo : veranlagungsnummerMos) {
            final CidsBean veranlagungsnummerBean = veranlagungsnummerMo.getBean();
            final String mapKey = (String)veranlagungsnummerBean.getProperty(
                    VeranlagungsnummerPropertyConstants.PROP__BEZEICHNER);

            veranlagungsnummern.put(mapKey, veranlagungsnummerBean);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  veranlagungSummeMap  DOCUMENT ME!
     */
    private void fillVeranlagungMaps(final Map<String, Double> veranlagungSummeMap) {
        veranlagungSummeMap.clear();

        for (final String mapKey : veranlagungsnummern.keySet()) {
            veranlagungSummeMap.put(mapKey, 0d);
        }

        fillFlaechenVeranlagungSummeMap(veranlagungSummeMap);
        fillStrassenreinigungSummeMap(veranlagungSummeMap);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  veranlagungSummeMap  DOCUMENT ME!
     */
    public void fillFlaechenVeranlagungSummeMap(final Map<String, Double> veranlagungSummeMap) {
        final Collection<CidsBean> flaechen = getCidsBean().getBeanCollectionProperty(
                KassenzeichenPropertyConstants.PROP__FLAECHEN);

        for (final CidsBean flaeche : flaechen) {
            final Float anteil = (Float)flaeche.getProperty(FlaechePropertyConstants.PROP__ANTEIL);
            final Integer flaechenart = (Integer)flaeche.getProperty(FlaechePropertyConstants.PROP__FLAECHENINFO + "."
                            + FlaecheninfoPropertyConstants.PROP__FLAECHENART + "."
                            + FlaechenartPropertyConstants.PROP__ID);
            final Integer anschlussgrad = (Integer)flaeche.getProperty(
                    FlaechePropertyConstants.PROP__FLAECHENINFO
                            + "."
                            + FlaecheninfoPropertyConstants.PROP__ANSCHLUSSGRAD
                            + "."
                            + AnschlussgradPropertyConstants.PROP__ID);
            final String mapKey = Integer.toString(flaechenart) + "-" + Integer.toString(anschlussgrad);

            final CidsBean veranlagungsgrundlageBean = veranlagungsgrundlageMap.get(mapKey);
            float veranlagungsschluessel;
            try {
                veranlagungsschluessel = (Float)veranlagungsgrundlageBean.getProperty("veranlagungsschluessel");
            } catch (final Exception e) {
                veranlagungsschluessel = 0;
            }

            final String bezeichner = (String)veranlagungsgrundlageBean.getProperty("veranlagungsnummer.bezeichner");

            double groesse;
            if (anteil == null) {
                try {
                    groesse = (Integer)flaeche.getProperty(
                            FlaechePropertyConstants.PROP__FLAECHENINFO
                                    + "."
                                    + FlaecheninfoPropertyConstants.PROP__GROESSE_KORREKTUR);
                } catch (final Exception e) {
                    groesse = 0;
                }
            } else {
                groesse = anteil.doubleValue();
            }
            final double groesseGewichtet = groesse * veranlagungsschluessel;

            final double summeveranlagt = (veranlagungSummeMap.containsKey(bezeichner))
                ? veranlagungSummeMap.get(bezeichner) : 0.0d;

            if (groesseGewichtet > 0) {
                veranlagungSummeMap.put(bezeichner, Math.round((summeveranlagt + groesseGewichtet) * 1000) / 1000d);
            } else {
                veranlagungSummeMap.put(bezeichner, Math.round((summeveranlagt + groesse) * 1000) / 1000d);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  flaechenAnschlussgradSummeMap  DOCUMENT ME!
     */
    public void fillFlaechenAnschlussgradSummeMap(final Map<String, Double> flaechenAnschlussgradSummeMap) {
        final Collection<CidsBean> flaechen = getCidsBean().getBeanCollectionProperty(
                KassenzeichenPropertyConstants.PROP__FLAECHEN);

        for (final CidsBean flaeche : flaechen) {
            final Float anteil = (Float)flaeche.getProperty(FlaechePropertyConstants.PROP__ANTEIL);
            final Integer flaechenart = (Integer)flaeche.getProperty(FlaechePropertyConstants.PROP__FLAECHENINFO + "."
                            + FlaecheninfoPropertyConstants.PROP__FLAECHENART + "."
                            + FlaechenartPropertyConstants.PROP__ID);
            final Integer anschlussgrad = (Integer)flaeche.getProperty(
                    FlaechePropertyConstants.PROP__FLAECHENINFO
                            + "."
                            + FlaecheninfoPropertyConstants.PROP__ANSCHLUSSGRAD
                            + "."
                            + AnschlussgradPropertyConstants.PROP__ID);
            final String mapKey = Integer.toString(flaechenart) + "-" + Integer.toString(anschlussgrad);
            final String anschlussgradKey = (String)flaeche.getProperty(
                    FlaechePropertyConstants.PROP__FLAECHENINFO
                            + "."
                            + FlaecheninfoPropertyConstants.PROP__ANSCHLUSSGRAD
                            + "."
                            + AnschlussgradPropertyConstants.PROP__GRAD_ABKUERZUNG);

            final CidsBean veranlagungsgrundlageBean = veranlagungsgrundlageMap.get(mapKey);
            final Float veranlagungsschluessel = (Float)veranlagungsgrundlageBean.getProperty(
                    "veranlagungsschluessel");

            final double groesse;
            if (anteil == null) {
                groesse = (Integer)flaeche.getProperty(
                        FlaechePropertyConstants.PROP__FLAECHENINFO
                                + "."
                                + FlaecheninfoPropertyConstants.PROP__GROESSE_KORREKTUR);
            } else {
                groesse = anteil.doubleValue();
            }
            final double groesseGewichtet = groesse * veranlagungsschluessel;

            final double summeAnschlussgrad = (flaechenAnschlussgradSummeMap.containsKey(anschlussgradKey))
                ? flaechenAnschlussgradSummeMap.get(anschlussgradKey) : 0d;

            if (groesseGewichtet > 0) {
                flaechenAnschlussgradSummeMap.put(
                    anschlussgradKey,
                    Math.round((summeAnschlussgrad + groesseGewichtet) * 1000)
                            / 1000d);
            } else {
                flaechenAnschlussgradSummeMap.put(
                    anschlussgradKey,
                    Math.round((summeAnschlussgrad + groesse) * 1000)
                            / 1000d);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  strasseSummeMap  DOCUMENT ME!
     */
    public void fillStrasseSummeMap(final Map<String, Double> strasseSummeMap) {
        final List<CidsBean> fronten = kassenzeichenBean.getBeanCollectionProperty(
                KassenzeichenPropertyConstants.PROP__FRONTEN);

        for (final CidsBean front : fronten) {
            int laenge;
            try {
                laenge = (Integer)front.getProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                                + FrontinfoPropertyConstants.PROP__LAENGE_KORREKTUR);
            } catch (final Exception e) {
                laenge = 0;
            }

            final CidsBean satzung_strassenreinigung = (CidsBean)front.getProperty(
                    FrontPropertyConstants.PROP__FRONTINFO
                            + "."
                            + FrontinfoPropertyConstants.PROP__LAGE_SR);
            final String key;
            final Integer schluessel;
            if (satzung_strassenreinigung == null) {
                key = (String)front.getProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                                + FrontinfoPropertyConstants.PROP__SR_KLASSE_OR + "."
                                + StrassenreinigungPropertyConstants.PROP__KEY);
                schluessel = (Integer)front.getProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                                + FrontinfoPropertyConstants.PROP__SR_KLASSE_OR + "."
                                + StrassenreinigungPropertyConstants.PROP__SCHLUESSEL);
            } else {
                key = (String)satzung_strassenreinigung.getProperty("sr_klasse.key");
                schluessel = (Integer)satzung_strassenreinigung.getProperty("sr_klasse.schluessel");
            }
            final String strasseName = (String)front.getProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                            + FrontinfoPropertyConstants.PROP__STRASSE + "." + StrassePropertyConstants.PROP__NAME);
            final String strasseKeyName;
            if (strasseName == null) {
                strasseKeyName = "<keine>";
            } else {
                final Integer strasseKey = (Integer)front.getProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                                + FrontinfoPropertyConstants.PROP__STRASSE + "."
                                + StrassePropertyConstants.PROP__SCHLUESSEL);
                strasseKeyName = "#" + StringUtils.leftPad(String.valueOf(strasseKey), 4, "0") + "  " + strasseName;
            }

            final String srKey = key + "-" + schluessel + " - " + strasseKeyName;
            final double summe = (strasseSummeMap.containsKey(srKey)) ? strasseSummeMap.get(srKey) : 0.0d;
            strasseSummeMap.put(srKey, Math.round((summe + laenge) * 1000) / 1000d);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  srSummeMap  DOCUMENT ME!
     */
    public void fillStrassenreinigungSummeMap(final Map<String, Double> srSummeMap) {
        final List<CidsBean> fronten = kassenzeichenBean.getBeanCollectionProperty(
                KassenzeichenPropertyConstants.PROP__FRONTEN);

        for (final CidsBean front : fronten) {
            int laenge;
            try {
                laenge = (Integer)front.getProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                                + FrontinfoPropertyConstants.PROP__LAENGE_KORREKTUR);
            } catch (final Exception e) {
                laenge = 0;
            }

            final CidsBean satzung_strassenreinigung = (CidsBean)front.getProperty(
                    FrontPropertyConstants.PROP__FRONTINFO
                            + "."
                            + FrontinfoPropertyConstants.PROP__LAGE_SR);
            final String key;
            final Integer schluessel;
            if (satzung_strassenreinigung == null) {
                key = (String)front.getProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                                + FrontinfoPropertyConstants.PROP__SR_KLASSE_OR + "."
                                + StrassenreinigungPropertyConstants.PROP__KEY);
                schluessel = (Integer)front.getProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                                + FrontinfoPropertyConstants.PROP__SR_KLASSE_OR + "."
                                + StrassenreinigungPropertyConstants.PROP__SCHLUESSEL);
            } else {
                key = (String)satzung_strassenreinigung.getProperty("sr_klasse.key");
                schluessel = (Integer)satzung_strassenreinigung.getProperty("sr_klasse.schluessel");
            }

            final String srKey = key + "-" + schluessel;
            final double summe = (srSummeMap.containsKey(srKey)) ? srSummeMap.get(srKey) : 0.0d;
            srSummeMap.put(srKey, Math.round((summe + laenge) * 1000) / 1000d);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void allClipboardsDeleteStoreFile() {
        for (final AbstractClipboard clipboard : clipboards.values()) {
            clipboard.deleteStoreFile();
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void initValidator() {
        aggValidator.clear();
        aggValidator.addListener(new ValidatorListener() {

                @Override
                public void stateChanged(final Validator source, final ValidatorState state) {
                    enableSave(!state.isError());
                }
            });
        aggValidator.add(kassenzeichenPanel.getValidator());
        aggValidator.add(getRegenFlaechenTable().getValidator());
        aggValidator.add(getSRFrontenTable().getValidator());
        aggValidator.add(regenFlaechenDetailsPanel.getValidator());
        aggValidator.add(srFrontenDetailsPanel.getValidator());
    }

    /**
     * DOCUMENT ME!
     */
    private void initClipboards() {
        clipboards.clear();

        final ClipboardListener clipboardListener = new ClipboardListener() {

                @Override
                public void clipboardChanged() {
                    kassenzeichenListPanel.clipboardChanged();
                    refreshClipboardButtons();
                }
            };

        clipboards.clear();
        final AbstractClipboard flaechenClipboard = new FlaechenClipboard(getRegenFlaechenTable());
        flaechenClipboard.addListener(clipboardListener);
        flaechenClipboard.loadFromFile();

        if (flaechenClipboard.isPastable()) {
            JOptionPane.showMessageDialog(
                this,
                "Der Inhalt der Zwischenablage steht Ihnen weiterhin zur Verf\u00FCgung.",
                "Verdis wurde nicht ordnungsgem\u00E4\u00DF beendet.",
                JOptionPane.INFORMATION_MESSAGE);
        }

        clipboards.put(CidsAppBackend.Mode.REGEN, flaechenClipboard);

        final AbstractClipboard frontenClipboard = new FrontenClipboard(getSRFrontenTable());
        frontenClipboard.addListener(clipboardListener);
        frontenClipboard.loadFromFile();

        if (frontenClipboard.isPastable()) {
            JOptionPane.showMessageDialog(
                this,
                "Der Inhalt der Zwischenablage steht Ihnen weiterhin zur Verf\u00FCgung.",
                "Verdis wurde nicht ordnungsgem\u00E4\u00DF beendet.",
                JOptionPane.INFORMATION_MESSAGE);
        }

        clipboards.put(CidsAppBackend.Mode.SR, frontenClipboard);

        final AbstractClipboard befreiungGeometrieClipboard = new BefreiungerlaubnisGeometrieClipboard(
                getBefreiungerlaubnisTable());
        befreiungGeometrieClipboard.addListener(clipboardListener);
        befreiungGeometrieClipboard.loadFromFile();

        if (befreiungGeometrieClipboard.isPastable()) {
            JOptionPane.showMessageDialog(
                this,
                "Der Inhalt der Zwischenablage steht Ihnen weiterhin zur Verf\u00FCgung.",
                "Verdis wurde nicht ordnungsgem\u00E4\u00DF beendet.",
                JOptionPane.INFORMATION_MESSAGE);
        }

        clipboards.put(CidsAppBackend.Mode.KANALDATEN, befreiungGeometrieClipboard);

        final AbstractClipboard kassenzeichenClipboard = new KassenzeichenGeometrienClipboard(
                kassenzeichenGeometrienPanel.getKassenzeichenGeometrienList());
        kassenzeichenClipboard.addListener(clipboardListener);
        kassenzeichenClipboard.loadFromFile();

        if (kassenzeichenClipboard.isPastable()) {
            JOptionPane.showMessageDialog(
                this,
                "Der Inhalt der Zwischenablage steht Ihnen weiterhin zur Verf\u00FCgung.",
                "Verdis wurde nicht ordnungsgem\u00E4\u00DF beendet.",
                JOptionPane.INFORMATION_MESSAGE);
        }

        clipboards.put(CidsAppBackend.Mode.ALLGEMEIN, kassenzeichenClipboard);
    }

    /**
     * DOCUMENT ME!
     */
    private void processSapClipboardContents() {
        final Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                final String string = ((String)transferable.getTransferData(DataFlavor.stringFlavor)).trim();
                if ((string.length() == 8)
                            && (string.startsWith("6") || string.startsWith("8"))) {
                    Integer.parseInt(string); // check if its a number
                    CidsAppBackend.getInstance().gotoKassenzeichen(string);
                }
            } catch (final Exception ex) {
                LOG.warn("error processing system clipboard", ex);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isFixMapExtent() {
        return fixMapExtent;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  fixMapExtent  DOCUMENT ME!
     */
    public void setFixMapExtent(final boolean fixMapExtent) {
        this.fixMapExtent = fixMapExtent;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isFixMapExtentMode() {
        return fixMapExtentMode;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  fixMapExtentMode  DOCUMENT ME!
     */
    public void setFixMapExtentMode(final boolean fixMapExtentMode) {
        this.fixMapExtentMode = fixMapExtentMode;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class LazyInitialiser {

        //~ Static fields/initializers -----------------------------------------

        private static final Main INSTANCE = new Main();

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LazyInitialiser object.
         */
        private LazyInitialiser() {
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    static class CidsAuthentification extends LoginService {

        //~ Static fields/initializers -----------------------------------------

        public static final String CONNECTION_PROXY_CLASS =
            "Sirius.navigator.connection.proxy.DefaultConnectionProxyHandler";

        //~ Instance fields ----------------------------------------------------

        private final AppPreferences appPreferences;
        private ConnectionProxy proxy = null;
        private boolean readOnly = true;
        private String userString = null;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new CidsAuthentification object.
         *
         * @param  appPreferences  DOCUMENT ME!
         */
        public CidsAuthentification(final AppPreferences appPreferences) {
            this.appPreferences = appPreferences;
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public ConnectionProxy getProxy() {
            return proxy;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public boolean isReadOnly() {
            return readOnly;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public String getUserString() {
            return userString;
        }

        /**
         * DOCUMENT ME!
         *
         * @param   name      DOCUMENT ME!
         * @param   password  DOCUMENT ME!
         * @param   server    DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         *
         * @throws  Exception  DOCUMENT ME!
         */
        @Override
        public boolean authenticate(final String name, final char[] password, final String server) throws Exception {
            System.setProperty("sun.rmi.transport.connectionTimeout", "15");
            final String username = name.split("@")[0];
            final String usergroup = name.split("@")[1];

            final String callServerURL = appPreferences.getAppbackendCallserverurl();
            if (LOG.isDebugEnabled()) {
                LOG.debug("callServerUrl:" + callServerURL);
            }
            final String domain = appPreferences.getAppbackendDomain();
            final String connectionclass = appPreferences.getAppbackendConnectionclass();

            try {
                final Connection connection = ConnectionFactory.getFactory()
                            .createConnection(connectionclass, callServerURL, appPreferences.isCompressionEnabled());
                final ConnectionInfo connectionInfo = new ConnectionInfo();
                connectionInfo.setCallserverURL(callServerURL);
                connectionInfo.setPassword(new String(password));
                connectionInfo.setUserDomain(domain);
                connectionInfo.setUsergroup(usergroup);
                connectionInfo.setUsergroupDomain(domain);
                connectionInfo.setUsername(username);
                final ConnectionSession session = ConnectionFactory.getFactory()
                            .createSession(connection, connectionInfo, true);
                proxy = ConnectionFactory.getFactory().createProxy(CONNECTION_PROXY_CLASS, session);

                final User user = session.getUser();
                if (proxy.hasConfigAttr(user, "grundis.access.readwrite")) {
                    userString = name;
                    readOnly = false;
                    return true;
                } else if (proxy.hasConfigAttr(user, "grundis.access.read")) {
                    readOnly = true;
                    userString = name;
                    return true;
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(
                            "no read, no write: probably the config attrs (grundis.access.readwrite or grundis.access.read) are not set for this user.");
                    }
                    return false;
                }
            } catch (final Exception ex) {
                LOG.error("Fehler beim Anmelden", ex);
                return false;
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class SAPClipboardListener implements ClipboardOwner {

        //~ Methods ------------------------------------------------------------

        @Override
        public void lostOwnership(final Clipboard clipboard, final Transferable transferable) {
            if (cmdSAPCheck.isSelected()) {
                SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            if (!isInEditMode()) {
                                processSapClipboardContents();
                            }
                        }
                    });
                gainOwnership();
            }
        }

        /**
         * DOCUMENT ME!
         */
        public void gainOwnership() {
            Toolkit.getDefaultToolkit()
                    .getSystemClipboard()
                    .setContents(Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null), this);
        }
    }
}
