/*
 * Main.java
 *
 * Created on 6. Januar 2005, 14:55
 */
package de.cismet.verdis.gui;

import Sirius.navigator.connection.Connection;
import Sirius.navigator.connection.ConnectionFactory;
import Sirius.navigator.connection.ConnectionInfo;
import Sirius.navigator.connection.ConnectionSession;
import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.connection.proxy.ConnectionProxy;
import de.cismet.rmplugin.RMPlugin;
import de.cismet.tools.gui.dbwriter.SimpleDbAction;
import java.awt.EventQueue;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.*;
import java.util.*;
import de.cismet.verdis.interfaces.*;

import Sirius.navigator.plugin.context.*;
import Sirius.navigator.plugin.interfaces.*;
import Sirius.navigator.plugin.listener.*;
import Sirius.navigator.search.dynamic.FormDataBean;
import Sirius.navigator.types.iterator.*;
import Sirius.navigator.types.treenode.*;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;

import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.Dom4JDriver;

import com.vividsolutions.jts.geom.Geometry;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.layerwidget.ActiveLayerModel;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.CreateGeometryListener;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.CustomFeatureInfoListener;
import de.cismet.cismap.commons.wfsforms.AbstractWFSForm;
import de.cismet.extensions.timeasy.TimEasyDialog;
import de.cismet.extensions.timeasy.TimEasyEvent;
import de.cismet.extensions.timeasy.TimEasyListener;
import de.cismet.extensions.timeasy.TimEasyPureNewFeature;

import de.cismet.lagisEE.bean.LagisServerRemote;
import de.cismet.lagisEE.crossover.LagisCrossoverRemote;
import de.cismet.lagisEE.crossover.entity.WfsFlurstuecke;
import de.cismet.lagisEE.entity.core.FlurstueckSchluessel;
import de.cismet.tools.StaticDebuggingTools;
import de.cismet.tools.configuration.Configurable;
import de.cismet.tools.configuration.ConfigurationManager;
import de.cismet.tools.gui.Static2DTools;
import de.cismet.tools.gui.dbwriter.DbWriterDialog;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;
import de.cismet.validation.NotValidException;
import de.cismet.verdis.data.AppPreferences;
import de.cismet.verdis.data.Flaeche;
import de.cismet.verdis.data.Kassenzeichen;
import java.applet.AppletContext;
import java.awt.BorderLayout;
import java.awt.Color;

import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;


import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.Remote;
import java.util.prefs.Preferences;
import javax.swing.filechooser.FileFilter;
import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.View;
import net.infonode.docking.mouse.DockingWindowActionMouseButtonListener;
import net.infonode.docking.properties.RootWindowProperties;
import net.infonode.docking.theme.DockingWindowsTheme;
import net.infonode.docking.theme.ShapedGradientDockingTheme;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.PropertiesUtil;
import net.infonode.docking.util.StringViewMap;
import net.infonode.gui.componentpainter.AlphaGradientComponentPainter;
import net.infonode.gui.componentpainter.GradientComponentPainter;
import net.infonode.util.Direction;
import org.jdesktop.swingx.JXLoginPane;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.auth.DefaultUserNameStore;
import org.jdesktop.swingx.auth.LoginService;

//TODO instead of giving a reference of main to the widgets so they can change icons and othe docking stuff
import org.jdom.Element;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
//There should be a referece to their own view so that they can change it by themself

/**
 *
 * @author  hell
 */
public class Main extends javax.swing.JFrame implements PluginSupport, FloatingPluginUI, Storable, Configurable {

    public static Main getCurrentInstance() {
        return Main.THIS;
    }
    //private Color myBlue=new java.awt.Color(0, 51, 153);
    private Color myBlue = new Color(124, 160, 221);
    de.cismet.tools.ConnectionInfo connectionInfo = new de.cismet.tools.ConnectionInfo();
    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private Vector stores = new Vector();
    private boolean editmode = false;
    private boolean plugin = false;
    private boolean readonly = true;
    /** Creates new form Main */
    private String userString;
    public static int KASSENZEICHEN_CLASS_ID = 11;
    public static int GEOM_CLASS_ID = 0;
    public static int DMS_URL_BASE_ID = 1;
    public static int DMS_URL_ID = 2;
    public static double INITIAL_WMS_BB_X1 = 2569442.79;
    public static double INITIAL_WMS_BB_Y1 = 5668858.33;
    public static double INITIAL_WMS_BB_X2 = 2593744.91;
    public static double INITIAL_WMS_BB_Y2 = 5688416.22;
    private String domainServer = "VERDIS";
    private String kassenzeichenSuche = "kassenzeichenSuche";
    private String wmsBackgroundUrlTemplate = "";
    private DbWriterDialog dbWriter;
    private String userGroup = "noGroup";
    private Object clipboard = null;
    private boolean clipboardPasted = true; //wegen des ersten mals
    private AppPreferences prefs;
    //Inserting Docking Window functionalty (Sebastian) 24.07.07
    private View vKassenzeichen, vKanaldaten, vSummen, vDokumente, vFlaechen;
    private RootWindow rootWindow;
    private StringViewMap viewMap = new StringViewMap();
    private String home = System.getProperty("user.home");
    private String fs = System.getProperty("file.separator");
    private String verdisDirectory = home + fs + ".verdis";
    private String defaultLayoutFile = verdisDirectory + fs + "verdis.layout";
    private String pluginPathname = home + fs + ".verdis" + fs + "plugin.layout";
    private String clipboardBackup = home + fs + ".verdis" + fs + "clipboardBackup.xml";
    private boolean isInit = true;
    private PluginContext context;
    public static Main THIS;
    private ArrayList<JMenuItem> menues = new ArrayList<JMenuItem>();
    private final ConfigurationManager configurationManager = new ConfigurationManager();
    private ActiveLayerModel mappingModel = new ActiveLayerModel();
    private final String verdisConfig = ".verdisConfig";
    //Inserting Docking Window functionalty (Sebastian) 24.07.07

    private void setupDefaultLayout() {
        rootWindow.setWindow(new SplitWindow(false, 0.31842107f,
                new SplitWindow(true, 0.24596775f,
                vKassenzeichen,
                new SplitWindow(true, 0.67061925f,
                new SplitWindow(true, 0.29148936f,
                vSummen,
                vKanaldaten),
                vDokumente)),
                vFlaechen));
        //log.debug("layout: "+flPanel.getCustomButtons());
        //vFlaechen.getCustomTabComponents().addAll(flPanel.getCustomButtons());
        rootWindow.getWindowBar(Direction.LEFT).setEnabled(true);
        rootWindow.getWindowBar(Direction.RIGHT).setEnabled(true);
    }
    JDialog about = null;

    public Main() {
        this(null);
    }
    //Inserting Docking Window functionalty (Sebastian) 24.07.07
    Icon icoKassenzeichen = new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/titlebars/kassenzeichen.png"));
    Icon icoSummen = new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/titlebars/sum.png"));
    Icon icoKanal = new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/titlebars/pipe.png"));
    Icon icoDokumente = new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/titlebars/docs.png"));
    Icon icoFlaechen = new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/titlebars/flaechen.png"));
//    public void doConfigKeystrokes(){
//        KeyStroke configLoggerKeyStroke = KeyStroke.getKeyStroke('L',InputEvent.CTRL_MASK);
//        Action configAction = new AbstractAction(){
//            public void actionPerformed(ActionEvent e) {
//                java.awt.EventQueue.invokeLater(new Runnable() {
//                    public void run() {
//                        DeveloperUtil.createWindowLayoutFrame("Momentanes Layout", rootWindow).setVisible(true);
//                    }
//                });
//            }
//        };
//        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(configLoggerKeyStroke, "SHOW_LAYOUT");
//        getRootPane().getActionMap().put("SHOW_LAYOUT", configAction);
//
//        KeyStroke layoutKeyStroke = KeyStroke.getKeyStroke('S',InputEvent.CTRL_MASK);
//        Action layoutAction = new AbstractAction(){
//            public void actionPerformed(ActionEvent e) {
//                java.awt.EventQueue.invokeLater(new Runnable() {
//                    public void run() {
//                        setupDefaultLayout();
//                    }
//                });
//            }
//        };
//        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(layoutKeyStroke, "RESET_LAYOUT");
//        getRootPane().getActionMap().put("RESET_LAYOUT", layoutAction);
//    }
    private SummenPanel sumPanel;
    private DokumentenPanel dokPanel;
    private FlaechenPanel flPanel;
    private KassenzeichenPanel kzPanel;
    private KanaldatenPanel kanaldatenPanel;

    public Main(PluginContext context) {
        try {
            javax.swing.UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //EventDispatchThreadHangMonitor.initMonitoring();
        //RepaintManager.setCurrentManager(new CheckThreadViolationRepaintManager());
        sumPanel = new de.cismet.verdis.gui.SummenPanel();
        sumPanel.setMain(this);
        dokPanel = new de.cismet.verdis.gui.DokumentenPanel();
        flPanel = new de.cismet.verdis.gui.FlaechenPanel();
        kzPanel = new de.cismet.verdis.gui.KassenzeichenPanel();
        kanaldatenPanel = new de.cismet.verdis.gui.KanaldatenPanel();
        kanaldatenPanel.setMain(this);
        THIS = this;
        plugin = !(context == null);
        this.context = context;


        try {
            try {
                if (context != null && context.getEnvironment() != null && this.context.getEnvironment().isProgressObservable()) {
                    this.context.getEnvironment().getProgressObserver().setProgress(0, "verdis Plugin laden...");
                }
            } catch (Exception e) {
                System.err.print("Keine Progressmeldung");
                e.printStackTrace();
            }

            if (context == null) { //ACHTUNG
                try {
                    if (StaticDebuggingTools.checkHomeForFile("cismetCustomLog4JConfigurationInDotVerdis")) {
                        try {
                            org.apache.log4j.PropertyConfigurator.configure(home + fs + ".verdis" + fs + "custom.log4j.properties");
                            log.info("CustomLoggingOn");
                        } catch (Exception ex) {
                            org.apache.log4j.PropertyConfigurator.configure(ClassLoader.getSystemResource("de/cismet/verdis/res/log4j.properties"));
                        }
                    } else {
                        org.apache.log4j.PropertyConfigurator.configure(ClassLoader.getSystemResource("de/cismet/verdis/res/log4j.properties"));
                    }
                } catch (Exception e) {
                    System.err.println("Fehler bei Log4J-Config");
                    //e.printStackTrace();
                }
                Action doNothing = new AbstractAction() {

                    public void actionPerformed(ActionEvent e) {
                        System.out.println("do nothing");
                    }
                };
            }

//            else {
//                try {
//                   String log4jProperties = context.getEnvironment().getParameter("log4j");//context.getEnvironment().getAppletContext().getApplet("cids - WuNDa Navigator Applet").getParameter("log4j");
//                   org.apache.log4j.PropertyConfigurator.configure(new URL(context.getEnvironment().getCodeBase().toString() + '/' + log4jProperties));
//                } catch (Exception e) {
//                   try {
//                       org.apache.log4j.PropertyConfigurator.configure(new URL(context.getEnvironment().getCodeBase().toString() + "/config/log4j.properties"));
//                   }
//                   catch(Throwable t) {
//                       t.printStackTrace();
//                   }
//                   System.err.println("Error before");
//                   e.printStackTrace();
//                }
//            }

            //ClearLookManager.setMode(ClearLookMode.ON);
            //PlasticLookAndFeel.setMyCurrentTheme(new DesertBlue());
            try {
                //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()) ;
                javax.swing.UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
                //javax.swing.UIManager.setLookAndFeel(new PlasticLookAndFeel());
                //javax.swing.UIManager.setLookAndFeel(new com.jgoodies.plaf.plastic.PlasticXPLookAndFeel());
                //UIManager.setLookAndFeel(new com.sun.java.swing.plaf.windows.WindowsLookAndFeel());
                // UIManager.setLookAndFeel(new PlasticLookAndFeel());
            } catch (Exception e) {
                log.warn("Fehler beim Einstellen des Look&Feels's!", e);
            }

            try {
                this.loadProperties();
            } catch (Exception propEx) {
                log.fatal("Fehler beim Laden der Properties!", propEx);
            }

            log.info("Verdis gestartet :-)");
            if (context != null && context.getEnvironment() != null && this.context.getEnvironment().isProgressObservable()) {
                this.context.getEnvironment().getProgressObserver().setProgress(100, "verdis Plugin DB Verbindung setzen...");
            }

            dbWriter = new DbWriterDialog(this, true);
            dbWriter.setConnection(connectionInfo);

            log.debug("Gui kram erledigt");

//                 UIDefaults uiDefaults = UIManager.getDefaults();
//                java.util.Enumeration keys = uiDefaults.keys();
//                while (keys.hasMoreElements())
//                {
//                    Object key=keys.nextElement();
//                    System.out.println(key.toString()+":"+uiDefaults.get(key));
//                }
            log.debug("initComponents()");
            if (context != null && context.getEnvironment() != null && this.context.getEnvironment().isProgressObservable()) {
                this.context.getEnvironment().getProgressObserver().setProgress(200, "verdis Plugin: Oberfl\u00E4che initialisieren ...");
            }

            initComponents();
            //Menu for Navigator
            if (plugin) {
                JMenu navigatorMenue = new JMenu("Verdis");
                navigatorMenue.add(mniKassenzeichen);
                navigatorMenue.add(mniDokumente);
                navigatorMenue.add(mniSummen);
                navigatorMenue.add(mniFlaechen);
                navigatorMenue.add(mniKanalanschluss);
                //navigatorMenue.add(menWindows.getItem(4));
                navigatorMenue.add(new JSeparator());
                //navigatorMenue.add(menWindows.getItem(6));
                navigatorMenue.add(mniLoadLayout);
                navigatorMenue.add(mniSaveLayout);
                navigatorMenue.add(mniResetWindowLayout);
                menues.add(navigatorMenue);
            }

            if (!plugin) {
                configurationManager.setFileName("configuration.xml");
            } else {
                configurationManager.setFileName("configurationPlugin.xml");
            }
            configurationManager.setClassPathFolder("/verdis/");
            configurationManager.setFolder(".verdis");
            log.debug("mc:" + getMappingComponent());
            configurationManager.addConfigurable(mappingModel);
            configurationManager.addConfigurable(getMappingComponent());


            EventQueue.invokeLater(new Runnable() {

                public void run() {
                    validateTree(); //is needed to compute the mappingComponent size, so that
                    //the layers are displayed correctly
                }
            });

            //Anwendungslogik
            if (context != null && context.getEnvironment() != null && this.context.getEnvironment().isProgressObservable()) {
                this.context.getEnvironment().getProgressObserver().setProgress(300, "verdis Plugin: Widgets verbinden ...");
            }

            stores.add(kzPanel);
            stores.add(flPanel);
            stores.add(dokPanel);
            stores.add(kanaldatenPanel);

//
//
//            Thread t=new Thread() {
//                public void run() {
            getKzPanel().setConnectionInfo(connectionInfo);
            sumPanel.setConnectionInfo(connectionInfo);
            getFlPanel().setConnectionInfo(connectionInfo);
            dokPanel.setConnectionInfo(connectionInfo);
            kanaldatenPanel.setConnectionInfo(connectionInfo);
            getKzPanel().addKassenzeichenChangedListener(sumPanel);
            getKzPanel().addKassenzeichenChangedListener(getFlPanel());
            getKzPanel().addKassenzeichenChangedListener(dokPanel);
            getKzPanel().addKassenzeichenChangedListener(kanaldatenPanel);
//                }
//            };
//            t.setPriority(Thread.NORM_PRIORITY);
//            t.start();
//        flPanel.addWmsBackground(this.wmsBackgroundUrlTemplate);
//        flPanel.addWmsBackground("http://geoportal.wuppertal.de/deegree/wms?null&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&WIDTH=<cids:width>&HEIGHT=<cids:height>&BBOX=<cids:boundingBox>&SRS=EPSG:31466&FORMAT=image/png&TRANSPARENT=true&BGCOLOR=0xF0F0F0&EXCEPTIONS=application/vnd.ogc.se_xml&LAYERS=R102:DGK5hausnr");

            if (context != null && context.getEnvironment() != null && this.context.getEnvironment().isProgressObservable()) {
                this.context.getEnvironment().getProgressObserver().setProgress(500, "verdis Plugin: GIS Einstellungen ...");
            }
            log.info("Einstellungen der Karte vornehmen");
//            flPanel.setCismapPreferences(prefs.getCismapPrefs());
            enableEditing(false);
            log.debug("fertig");
            String host = "unknown";
            try {
                java.net.InetAddress i = java.net.InetAddress.getLocalHost();
                host = i.getHostAddress();
                host = i.getHostName();
            } catch (Exception e) {
                log.debug("kein Hostname", e);
            }

            log.debug(userString);

            kzPanel.setMainApp(this);
            flPanel.setMain(this);
            KeyStroke configLoggerKeyStroke = KeyStroke.getKeyStroke('L', InputEvent.CTRL_MASK);
            Action configAction = new AbstractAction() {

                public void actionPerformed(ActionEvent e) {
                    java.awt.EventQueue.invokeLater(new Runnable() {

                        public void run() {
                            Log4JQuickConfig.getSingletonInstance().setVisible(true);
                        }
                    });
                }
            };
            getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(configLoggerKeyStroke, "CONFIGLOGGING");
            getRootPane().getActionMap().put("CONFIGLOGGING", configAction);


            //WFSForms
//            Thread wfsformsThread=new Thread() {
//                public void run() {
            Set<String> keySet = prefs.getWfsForms().keySet();
//            JMenu wfsFormsMenu=new JMenu();
            log.debug("WFSForms " + keySet);
            for (String key : keySet) {
                //
                try {
                    log.debug("WFSForms: " + key);
                    final AbstractWFSForm form = prefs.getWfsForms().get(key);
//                form.setPreferredSize(new Dimension(450,50));
                    final JDialog formView = new JDialog(Main.this, form.getTitle());
                    formView.getContentPane().setLayout(new BorderLayout());
                    formView.getContentPane().add(form, BorderLayout.CENTER);
                    formView.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                    form.setMappingComponent(getFlPanel().getFlOverviewPanel().getMappingComponent());

                    formView.pack();
//                final View formView=createView(form.getId(),form.getTitle(),form);
//                wfsFormViews.add(formView);
//                formView.setTabText("   "+formView.getTabText()+"   ");
//                formView.setTabIcon(Static2DTools.borderIcon(form.getIcon(),10,0,3,0));
//                formView.setIcon(form.getIcon());
                    //Menu
                    final JButton cmd = new JButton(null, form.getIcon());
                    cmd.setToolTipText(form.getMenuString());
                    cmd.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            formView.setLocationRelativeTo(Main.this);
                            formView.setVisible(true);
                        }
                    });

                    EventQueue.invokeLater(new Runnable() {

                        public void run() {
                            tobVerdis.add(cmd);
                        }
                    });


                } catch (Throwable thr) {
                    log.error("Fehler beim Hinzuf\u00FCgen einer WFSForm", thr);
                }
            }
//                }
//            };
//            wfsformsThread.setPriority(Thread.NORM_PRIORITY);
//            wfsformsThread.start();
            //Inserting Docking Window functionalty (Sebastian) 24.07.07
            rootWindow = DockingUtil.createRootWindow(viewMap, true);

            vKassenzeichen = new View("Kassenzeichen", Static2DTools.borderIcon(icoKassenzeichen, 0, 3, 0, 1), kzPanel);
            viewMap.addView("Kassenzeichen", vKassenzeichen);
            vKassenzeichen.getCustomTitleBarComponents().addAll(kzPanel.getCustomButtons());

            vSummen = new View("Summen", Static2DTools.borderIcon(icoSummen, 0, 3, 0, 1), sumPanel);
            viewMap.addView("Summen", vSummen);

            vFlaechen = new View("Fl\u00E4chen", Static2DTools.borderIcon(icoFlaechen, 0, 3, 0, 1), flPanel);
            viewMap.addView("Flaechen", vFlaechen);
//            JButton button = new JButton(BUTTON_ICON);
//            button.setOpaque(false);
//            button.setBorder(null);
//            button.setFocusable(false);
//            button.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent e) {
//                    JOptionPane.showMessageDialog(Main.this,
//                            "You clicked the custom view button.",
//                            "Custom View Button",
//                            JOptionPane.INFORMATION_MESSAGE);
//                }
//            });
            vFlaechen.getCustomTitleBarComponents().addAll(flPanel.getCustomButtons());

            vKanaldaten = new View("Kanalanschluss", Static2DTools.borderIcon(icoKanal, 0, 3, 0, 1), kanaldatenPanel);
            viewMap.addView("Kanalanschluss", vKanaldaten);

            vDokumente = new View("Dokumente", Static2DTools.borderIcon(icoDokumente, 0, 3, 0, 1), dokPanel);
            viewMap.addView("Dokumente", vDokumente);

            rootWindow.addTabMouseButtonListener(DockingWindowActionMouseButtonListener.MIDDLE_BUTTON_CLOSE_LISTENER);

            DockingWindowsTheme theme = new ShapedGradientDockingTheme();

            rootWindow.getRootWindowProperties().addSuperObject(
                    theme.getRootWindowProperties());

            RootWindowProperties titleBarStyleProperties =
                    PropertiesUtil.createTitleBarStyleRootWindowProperties();

            rootWindow.getRootWindowProperties().addSuperObject(
                    titleBarStyleProperties);

            rootWindow.getRootWindowProperties().getDockingWindowProperties().setUndockEnabled(true);

            AlphaGradientComponentPainter x = new AlphaGradientComponentPainter(java.awt.SystemColor.inactiveCaptionText, java.awt.SystemColor.activeCaptionText, java.awt.SystemColor.activeCaptionText, java.awt.SystemColor.inactiveCaptionText);
            //vMap.getViewProperties().getViewTitleBarProperties().getNormalProperties().getCloseButtonProperties().setVisible(true);
            rootWindow.getRootWindowProperties().getDragRectangleShapedPanelProperties().setComponentPainter(x);
            rootWindow.getRootWindowProperties().getViewProperties().getViewTitleBarProperties().getNormalProperties().getShapedPanelProperties().setComponentPainter(new GradientComponentPainter(new Color(124, 160, 221), new Color(236, 233, 216), new Color(124, 160, 221), new Color(236, 233, 216)));

            //Inserting Docking Window functionalty (Sebastian) 24.07.07
            if (plugin) {
                //DockingManager.setDefaultPersistenceKey("pluginPerspectives.xml");
                loadLayout(pluginPathname);

            } else {
                //DockingManager.setDefaultPersistenceKey("cismapPerspectives.xml");
                loadLayout(defaultLayoutFile);
            }
            //TODO UGLY PERHAPS CENTRAL HANDLer FOR THE CREATION OF CONFIGURATION
            File verdisDir = new File(verdisDirectory);
            if (!verdisDir.exists()) {
                log.debug("Verdis Directory angelegt");
                verdisDir.mkdir();
            }

            panMain.add(rootWindow);
            //doConfigKeystrokes();

            log.debug("Crossover: starte server.");
            initCrossoverServer(prefs.getVerdisCrossoverPort());
            isInit = false;
        } catch (Throwable t) {
            t.printStackTrace();
            log.error("Fehler im Konstruktor", t);
        }

        if (context != null) {
            try {
                this.cmdPutKassenzeichenToSearchTree.setEnabled(true);
                if (context != null && context.getEnvironment() != null && this.context.getEnvironment().isProgressObservable()) {
                    this.context.getEnvironment().getProgressObserver().setProgress(700, "verdis Plugin: Methoden initialisieren ...");
                }

                this.context.getMetadata().addMetaNodeSelectionListener(new NodeChangeListener());
                userString = Sirius.navigator.connection.SessionManager.getSession().getUser().getName() + "@" + Sirius.navigator.connection.SessionManager.getSession().getUser().getUserGroup().getName();
                userGroup = Sirius.navigator.connection.SessionManager.getSession().getUser().getUserGroup().toString();
                log.debug("prefs: Vector index of " + (prefs.getRwGroups().indexOf(userGroup.toLowerCase()) >= 0));
                log.debug("prefs: userGroup " + userGroup.toLowerCase());
                if (prefs.getRwGroups().indexOf(userGroup.toLowerCase()) >= 0) {
                    readonly = false;
                } else {
                    readonly = true;
                }
                kzPanel.setUserString(userString);
                dokPanel.setAppletContext(context.getEnvironment().getAppletContext());
                //java.lang.Runtime.getRuntime().addShutdownHook(hook)
                if (readonly) {
                    cmdEditMode.setEnabled(false);
                    cmdNewKassenzeichen.setEnabled(false);
                }
                if (context != null && context.getEnvironment() != null && this.context.getEnvironment().isProgressObservable()) {
                    this.context.getEnvironment().getProgressObserver().setProgress(1000, "verdis Plugin fertig...");
                }
                if (context != null && context.getEnvironment() != null && context.getEnvironment().isProgressObservable()) {
                    this.context.getEnvironment().getProgressObserver().setFinished(true);
                }
            } catch (Throwable t) {
                log.error("Fehler im PluginKonstruktor", t);
                t.printStackTrace();
            }
        } else {
            this.setIconImage(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/money_add.png")).getImage());
        }


        //TimEasy
        ((CreateGeometryListener) getFlPanel().getFlOverviewPanel().getMappingComponent().getInputListener(MappingComponent.NEW_POLYGON)).setGeometryFeatureClass(TimEasyPureNewFeature.class);
        final MappingComponent mapC = getFlPanel().getFlOverviewPanel().getMappingComponent();
        TimEasyDialog.addTimTimEasyListener(new TimEasyListener() {

            public void timEasyObjectInserted(TimEasyEvent tee) {
                mapC.getFeatureCollection().removeFeature(tee.getPureNewfeature());
                mapC.refresh();
            }
        });
        configurationManager.configure(mappingModel);
        mapC.preparationSetMappingModel(mappingModel);
        configurationManager.configure(mapC);
        mapC.setMappingModel(mappingModel);
        getFlPanel().getFlOverviewPanel().changeSelectedButtonAccordingToInteractionMode();

        mapC.unlock();



        //CustomFeatureInfo
        CustomFeatureInfoListener cfil = (CustomFeatureInfoListener) mapC.getInputListener(MappingComponent.CUSTOM_FEATUREINFO);
        cfil.setFeatureInforetrievalUrl(prefs.getAlbUrl());


        File dotverdisDir = new File(verdisDirectory);
        dotverdisDir.mkdir();

        loadClipboardBackup();
        if (clipboard != null) {
            JOptionPane.showMessageDialog(this.getComponent(), "Der Inhalt der Zwischenablage steht Ihnen weiterhin zur Verf\u00FCgung.", "Verdis wurde nicht ordnungsgem\u00E4\u00DF beendet.", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void initCrossoverServer(int crossoverServerPort) {
        log.debug("Crossover: initCrossoverServer");
        final int defaultServerPort = 8888;
        boolean defaultServerPortUsed = false;
        try {
            if (crossoverServerPort < 0 || crossoverServerPort > 65535) {
                log.warn("Crossover: Invalid Crossover serverport: " + crossoverServerPort + ". Going to use default port: " + defaultServerPort);
                defaultServerPortUsed = true;
                initCrossoverServerImpl(defaultServerPort);
            } else {
                initCrossoverServerImpl(crossoverServerPort);
            }
        } catch (Exception ex) {
            log.error("Crossover: Error while creating crossover server on port: " + crossoverServerPort);
            if (!defaultServerPortUsed) {
                log.debug("Crossover: Trying to create server with defaultPort: " + defaultServerPort);
                defaultServerPortUsed = true;
                try {
                    initCrossoverServerImpl(defaultServerPort);
                    log.debug("Crossover: Server started at port: " + defaultServerPort);
                } catch (Exception ex1) {
                    log.error("Crossover: Failed to initialize Crossover server on defaultport: " + defaultServerPort + ". No Server is started");
                }
            }
        }
    }

    private void initCrossoverServerImpl(int crossoverServerPort) throws Exception {
        ServletHolder sh = new ServletHolder(ServletContainer.class);
        sh.setInitParameter("com.sun.jersey.config.property.packages", "de.cismet.verdis.crossover");
        Server server = new Server(crossoverServerPort);
        Context context = new Context(server, "/", Context.SESSIONS);
        context.addServlet(sh, "/*");
        server.start();
    }

    private void loadProperties() {
        // Read properties file.

        prefs = new AppPreferences(getClass().getResourceAsStream("/verdis2properties.xml"));
        log.debug(getClass().getClassLoader());
        KASSENZEICHEN_CLASS_ID = prefs.getKassenzeichenClassId();
        GEOM_CLASS_ID = prefs.getGeomClassId();
        DMS_URL_BASE_ID = prefs.getDmsUrlBaseClassId();
        DMS_URL_ID = prefs.getDmsUrlClassId();
//        INITIAL_WMS_BB_X1 = prefs.getCismapPrefs().getGlobalPrefs().getInitialBoundingBox().getX1();
//        INITIAL_WMS_BB_Y1 = prefs.getCismapPrefs().getGlobalPrefs().getInitialBoundingBox().getY1();
//        INITIAL_WMS_BB_X2 = prefs.getCismapPrefs().getGlobalPrefs().getInitialBoundingBox().getX2();
//        INITIAL_WMS_BB_Y2 = prefs.getCismapPrefs().getGlobalPrefs().getInitialBoundingBox().getY2();

        connectionInfo = prefs.getDbConnectionInfo();
        if (!plugin) {
            if (prefs.getMode().trim().toLowerCase().equals("readonly")) {
                readonly = true;
            } else {
                readonly = false;
            }
        }
    }

    public void setEnabled(boolean b) {
        kzPanel.setEnabled(b);
        flPanel.setEnabled(b);
    }

    public void refreshLeftTitleBarColor() {
        if (editmode) {
            setLeftTitleBarColor(Color.red);
        } else {
            if (kzPanel.isLocked()) {
                setLeftTitleBarColor(Color.orange);
            } else {
                setLeftTitleBarColor(myBlue);
            }
        }
    }

    //Inserting Docking Window functionalty (Sebastian) 24.07.07
    //former all components are signaled to change the color
    //Now the docking framework will do that
    public void setLeftTitleBarColor(Color c) {
        if (!isInit) {
            rootWindow.getRootWindowProperties().getViewProperties().getViewTitleBarProperties().getNormalProperties().getShapedPanelProperties().setComponentPainter(new GradientComponentPainter(c, new Color(236, 233, 216), c, new Color(236, 233, 216)));
        }
//        sumPanel.setLeftTitlebarColor(c);
//        kanaldatenPanel.setLeftTitlebarColor(c);
//        dokPanel.setLeftTitlebarColor(c);
//        flPanel.setLeftTitlebarColor(c);
//        kzPanel.setLeftTitlebarColor(c);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cmdTest = new javax.swing.JButton();
        cmdTest2 = new javax.swing.JButton();
        tobVerdis = new javax.swing.JToolBar();
        cmdPutKassenzeichenToSearchTree = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JSeparator();
        cmdEditMode = new javax.swing.JButton();
        cmdCancel = new javax.swing.JButton();
        cmdOk = new javax.swing.JButton();
        cmdNewKassenzeichen = new javax.swing.JButton();
        cmdDeleteKassenzeichen = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JSeparator();
        cmdCutFlaeche = new javax.swing.JButton();
        cmdCopyFlaeche = new javax.swing.JButton();
        cmdPasteFlaeche = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JSeparator();
        cmdRefreshEnumeration = new javax.swing.JButton();
        jSeparator7 = new javax.swing.JSeparator();
        cmdPdf = new javax.swing.JButton();
        cmdWorkflow = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        cmdInfo = new javax.swing.JButton();
        jSeparator8 = new javax.swing.JSeparator();
        cmdLagisCrossover = new javax.swing.JButton();
        panMain = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        menFile = new javax.swing.JMenu();
        jSeparator9 = new javax.swing.JSeparator();
        mniSaveLayout = new javax.swing.JMenuItem();
        mniLoadLayout = new javax.swing.JMenuItem();
        jSeparator10 = new javax.swing.JSeparator();
        mniClose = new javax.swing.JMenuItem();
        menEdit = new javax.swing.JMenu();
        mnuEditMode = new javax.swing.JMenuItem();
        mnuNewKassenzeichen = new javax.swing.JMenuItem();
        menExtras = new javax.swing.JMenu();
        mnuChangeUser = new javax.swing.JMenuItem();
        menWindows = new javax.swing.JMenu();
        mniKassenzeichen = new javax.swing.JMenuItem();
        mniSummen = new javax.swing.JMenuItem();
        mniKanalanschluss = new javax.swing.JMenuItem();
        mniDokumente = new javax.swing.JMenuItem();
        mniFlaechen = new javax.swing.JMenuItem();
        jSeparator11 = new javax.swing.JSeparator();
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
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdTestActionPerformed(evt);
            }
        });

        cmdTest2.setText("Test Clipboard Load");
        cmdTest2.setFocusable(false);
        cmdTest2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdTest2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdTest2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdTest2ActionPerformed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("verdis");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                formKeyTyped(evt);
            }
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formKeyReleased(evt);
            }
        });

        tobVerdis.setRollover(true);
        tobVerdis.setAlignmentY(0.48387095F);
        tobVerdis.setMaximumSize(new java.awt.Dimension(679, 32769));
        tobVerdis.setMinimumSize(new java.awt.Dimension(667, 33));
        tobVerdis.setPreferredSize(new java.awt.Dimension(691, 35));

        cmdPutKassenzeichenToSearchTree.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/show_kassenzeichen_in_search_tree.png"))); // NOI18N
        cmdPutKassenzeichenToSearchTree.setToolTipText("Zeige Kassenzeichen im Navigator");
        cmdPutKassenzeichenToSearchTree.setEnabled(false);
        cmdPutKassenzeichenToSearchTree.setFocusPainted(false);
        cmdPutKassenzeichenToSearchTree.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdPutKassenzeichenToSearchTreeActionPerformed(evt);
            }
        });
        tobVerdis.add(cmdPutKassenzeichenToSearchTree);

        jSeparator5.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator5.setMaximumSize(new java.awt.Dimension(2, 32767));
        tobVerdis.add(jSeparator5);

        cmdEditMode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/editmode.png"))); // NOI18N
        cmdEditMode.setToolTipText("Editormodus");
        cmdEditMode.setFocusPainted(false);
        cmdEditMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdEditModeActionPerformed(evt);
            }
        });
        tobVerdis.add(cmdEditMode);

        cmdCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/cancel.png"))); // NOI18N
        cmdCancel.setToolTipText("Änderungen abbrechen");
        cmdCancel.setFocusPainted(false);
        cmdCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdCancelActionPerformed(evt);
            }
        });
        tobVerdis.add(cmdCancel);

        cmdOk.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/ok.png"))); // NOI18N
        cmdOk.setToolTipText("Änderungen annehmen");
        cmdOk.setFocusPainted(false);
        cmdOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdOkActionPerformed(evt);
            }
        });
        tobVerdis.add(cmdOk);

        cmdNewKassenzeichen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/newKassenzeichen.png"))); // NOI18N
        cmdNewKassenzeichen.setToolTipText("Neues Kassenzeichen");
        cmdNewKassenzeichen.setFocusPainted(false);
        cmdNewKassenzeichen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdNewKassenzeichenActionPerformed(evt);
            }
        });
        tobVerdis.add(cmdNewKassenzeichen);

        cmdDeleteKassenzeichen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/deleteKassenzeichen.png"))); // NOI18N
        cmdDeleteKassenzeichen.setToolTipText("Kassenzeichen löschen");
        cmdDeleteKassenzeichen.setEnabled(false);
        cmdDeleteKassenzeichen.setFocusPainted(false);
        cmdDeleteKassenzeichen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdDeleteKassenzeichenActionPerformed(evt);
            }
        });
        tobVerdis.add(cmdDeleteKassenzeichen);

        jSeparator6.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator6.setMaximumSize(new java.awt.Dimension(2, 32767));
        tobVerdis.add(jSeparator6);

        cmdCutFlaeche.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/cutFl.png"))); // NOI18N
        cmdCutFlaeche.setToolTipText("Fläche ausschneiden");
        cmdCutFlaeche.setEnabled(false);
        cmdCutFlaeche.setFocusPainted(false);
        cmdCutFlaeche.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdCutFlaecheActionPerformed(evt);
            }
        });
        tobVerdis.add(cmdCutFlaeche);

        cmdCopyFlaeche.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/copyFl.png"))); // NOI18N
        cmdCopyFlaeche.setToolTipText("Fläche kopieren (Teileigentum erzeugen)");
        cmdCopyFlaeche.setEnabled(false);
        cmdCopyFlaeche.setFocusPainted(false);
        cmdCopyFlaeche.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdCopyFlaecheActionPerformed(evt);
            }
        });
        tobVerdis.add(cmdCopyFlaeche);

        cmdPasteFlaeche.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/pasteFl.png"))); // NOI18N
        cmdPasteFlaeche.setToolTipText("Fläche einfügen");
        cmdPasteFlaeche.setEnabled(false);
        cmdPasteFlaeche.setFocusPainted(false);
        cmdPasteFlaeche.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdPasteFlaecheActionPerformed(evt);
            }
        });
        tobVerdis.add(cmdPasteFlaeche);

        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator4.setMaximumSize(new java.awt.Dimension(2, 32767));
        tobVerdis.add(jSeparator4);

        cmdRefreshEnumeration.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/refreshEnum.png"))); // NOI18N
        cmdRefreshEnumeration.setToolTipText("Alle Flächen neu nummerieren");
        cmdRefreshEnumeration.setEnabled(false);
        cmdRefreshEnumeration.setFocusPainted(false);
        cmdRefreshEnumeration.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdRefreshEnumerationActionPerformed(evt);
            }
        });
        tobVerdis.add(cmdRefreshEnumeration);

        jSeparator7.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator7.setMaximumSize(new java.awt.Dimension(2, 32767));
        tobVerdis.add(jSeparator7);

        cmdPdf.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/pdf.png"))); // NOI18N
        cmdPdf.setToolTipText("Drucken");
        cmdPdf.setFocusPainted(false);
        cmdPdf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdPdfActionPerformed(evt);
            }
        });
        tobVerdis.add(cmdPdf);

        cmdWorkflow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/workflow.png"))); // NOI18N
        cmdWorkflow.setToolTipText("Workflow");
        cmdWorkflow.setEnabled(false);
        cmdWorkflow.setFocusPainted(false);
        cmdWorkflow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdWorkflowActionPerformed(evt);
            }
        });
        tobVerdis.add(cmdWorkflow);

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator3.setMaximumSize(new java.awt.Dimension(2, 32767));
        tobVerdis.add(jSeparator3);

        cmdInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/info.png"))); // NOI18N
        cmdInfo.setToolTipText("Versionsanzeige");
        cmdInfo.setFocusPainted(false);
        cmdInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdInfoActionPerformed(evt);
            }
        });
        tobVerdis.add(cmdInfo);

        jSeparator8.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator8.setMaximumSize(new java.awt.Dimension(2, 32767));
        tobVerdis.add(jSeparator8);

        cmdLagisCrossover.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/lagisCrossover.png"))); // NOI18N
        cmdLagisCrossover.setToolTipText("Öffne zugehöriges Flurstück in LagIS");
        cmdLagisCrossover.setFocusPainted(false);
        cmdLagisCrossover.setFocusable(false);
        cmdLagisCrossover.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdLagisCrossover.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdLagisCrossover.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdLagisCrossoverActionPerformed(evt);
            }
        });
        tobVerdis.add(cmdLagisCrossover);

        getContentPane().add(tobVerdis, java.awt.BorderLayout.NORTH);

        panMain.setLayout(new java.awt.BorderLayout());
        getContentPane().add(panMain, java.awt.BorderLayout.CENTER);

        menFile.setMnemonic('D');
        menFile.setText("Datei");
        menFile.add(jSeparator9);

        mniSaveLayout.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        mniSaveLayout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/titlebars/layout.png"))); // NOI18N
        mniSaveLayout.setText("Aktuelles Layout speichern");
        mniSaveLayout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniSaveLayoutActionPerformed(evt);
            }
        });
        menFile.add(mniSaveLayout);

        mniLoadLayout.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        mniLoadLayout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/titlebars/layout.png"))); // NOI18N
        mniLoadLayout.setText("Layout laden");
        mniLoadLayout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniLoadLayoutActionPerformed(evt);
            }
        });
        menFile.add(mniLoadLayout);
        menFile.add(jSeparator10);

        mniClose.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        mniClose.setText("Beenden");
        mniClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExitActionPerformed(evt);
            }
        });
        menFile.add(mniClose);

        jMenuBar1.add(menFile);

        menEdit.setText("Bearbeiten");

        mnuEditMode.setText("In den Editormodus wechseln");
        mnuEditMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuEditModeActionPerformed(evt);
            }
        });
        menEdit.add(mnuEditMode);

        mnuNewKassenzeichen.setText("Neues Kassenzeichen");
        mnuNewKassenzeichen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuNewKassenzeichenActionPerformed(evt);
            }
        });
        menEdit.add(mnuNewKassenzeichen);

        jMenuBar1.add(menEdit);

        menExtras.setMnemonic('E');
        menExtras.setText("Extras");

        mnuChangeUser.setText("User wechseln");
        mnuChangeUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuChangeUserActionPerformed(evt);
            }
        });
        menExtras.add(mnuChangeUser);

        jMenuBar1.add(menExtras);

        menWindows.setMnemonic('F');
        menWindows.setText("Fenster");
        menWindows.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menWindowsActionPerformed(evt);
            }
        });

        mniKassenzeichen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_1, java.awt.event.InputEvent.CTRL_MASK));
        mniKassenzeichen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/titlebars/kassenzeichen.png"))); // NOI18N
        mniKassenzeichen.setMnemonic('L');
        mniKassenzeichen.setText("Kassenzeichen");
        mniKassenzeichen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniKassenzeichenActionPerformed(evt);
            }
        });
        menWindows.add(mniKassenzeichen);

        mniSummen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_2, java.awt.event.InputEvent.CTRL_MASK));
        mniSummen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/titlebars/sum.png"))); // NOI18N
        mniSummen.setMnemonic('C');
        mniSummen.setText("Summen");
        mniSummen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniSummenActionPerformed(evt);
            }
        });
        menWindows.add(mniSummen);

        mniKanalanschluss.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_3, java.awt.event.InputEvent.CTRL_MASK));
        mniKanalanschluss.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/titlebars/pipe.png"))); // NOI18N
        mniKanalanschluss.setMnemonic('F');
        mniKanalanschluss.setText("Kanalanschluss");
        mniKanalanschluss.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniKanalanschlussActionPerformed(evt);
            }
        });
        menWindows.add(mniKanalanschluss);

        mniDokumente.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_4, java.awt.event.InputEvent.CTRL_MASK));
        mniDokumente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/titlebars/docs.png"))); // NOI18N
        mniDokumente.setMnemonic('a');
        mniDokumente.setText("Dokumente");
        mniDokumente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniDokumenteActionPerformed(evt);
            }
        });
        menWindows.add(mniDokumente);

        mniFlaechen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_5, java.awt.event.InputEvent.CTRL_MASK));
        mniFlaechen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/titlebars/flaechen.png"))); // NOI18N
        mniFlaechen.setMnemonic('S');
        mniFlaechen.setText("Flächen");
        mniFlaechen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniFlaechenActionPerformed(evt);
            }
        });
        menWindows.add(mniFlaechen);
        menWindows.add(jSeparator11);

        mniResetWindowLayout.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        mniResetWindowLayout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/titlebars/layout.png"))); // NOI18N
        mniResetWindowLayout.setText("Fensteranordnung zurücksetzen");
        mniResetWindowLayout.setToolTipText("Standard Fensteranordnung wiederherstellen");
        mniResetWindowLayout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
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
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuInfoActionPerformed(evt);
            }
        });
        menHelp.add(mnuInfo);

        jMenuBar1.add(menHelp);

        setJMenuBar(jMenuBar1);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-1024)/2, (screenSize.height-868)/2, 1024, 868);
    }// </editor-fold>//GEN-END:initComponents
    //Inserting Docking Window functionalty (Sebastian) 24.07.07
    private void menWindowsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menWindowsActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_menWindowsActionPerformed

    private void mniResetWindowLayoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniResetWindowLayoutActionPerformed
        setupDefaultLayout();
    }//GEN-LAST:event_mniResetWindowLayoutActionPerformed

    private void mniFlaechenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniFlaechenActionPerformed
        showOrHideView(vFlaechen);
    }//GEN-LAST:event_mniFlaechenActionPerformed

    private void mniDokumenteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniDokumenteActionPerformed
        showOrHideView(vDokumente);
    }//GEN-LAST:event_mniDokumenteActionPerformed

    private void mniKanalanschlussActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniKanalanschlussActionPerformed
        showOrHideView(vKanaldaten);
    }//GEN-LAST:event_mniKanalanschlussActionPerformed

    private void mniSummenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniSummenActionPerformed
        showOrHideView(vSummen);
    }//GEN-LAST:event_mniSummenActionPerformed

    private void mniKassenzeichenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniKassenzeichenActionPerformed
        showOrHideView(vKassenzeichen);
    }//GEN-LAST:event_mniKassenzeichenActionPerformed
    //Inserting Docking Window functionalty (Sebastian) 24.07.07
    private void mniLoadLayoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniLoadLayoutActionPerformed
        JFileChooser fc = new JFileChooser(verdisDirectory);
        fc.setFileFilter(new FileFilter() {

            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".layout");
            }

            public String getDescription() {
                return "Layout";
            }
        });
        fc.setMultiSelectionEnabled(false);
        int state = fc.showOpenDialog(this);
        if (state == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String name = file.getAbsolutePath();
            name = name.toLowerCase();
            if (name.endsWith(".layout")) {
                loadLayout(name);
            } else {
                JOptionPane.showMessageDialog(this, java.util.ResourceBundle.getBundle("de/cismet/verdis/res/i18n/Bundle").getString("CismapPlugin.InfoNode.format_failure_message"), java.util.ResourceBundle.getBundle("de/cismet/verdis/res/i18n/Bundle").getString("CismapPlugin.InfoNode.message_title"), JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_mniLoadLayoutActionPerformed
    //Inserting Docking Window functionalty (Sebastian) 24.07.07
    private void mniSaveLayoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniSaveLayoutActionPerformed
        JFileChooser fc = new JFileChooser(verdisDirectory);
        fc.setFileFilter(new FileFilter() {

            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".layout");
            }

            public String getDescription() {
                return "Layout";
            }
        });
        fc.setMultiSelectionEnabled(false);
        int state = fc.showSaveDialog(this);
        log.debug("state:" + state);
        if (state == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            log.debug("file:" + file);
            String name = file.getAbsolutePath();
            name = name.toLowerCase();
            if (name.endsWith(".layout")) {
                saveLayout(name);
            } else {
                saveLayout(name + ".layout");
            }
        }
    }//GEN-LAST:event_mniSaveLayoutActionPerformed
    //TODO Bundle
    //Inserting Docking Window functionalty (Sebastian) 24.07.07

    public void loadLayout(String file) {
        log.debug("Load Layout.. from " + file);
        File layoutFile = new File(file);

        if (layoutFile.exists()) {
            log.debug("Layout File exists");
            try {
                FileInputStream layoutInput = new FileInputStream(layoutFile);
                ObjectInputStream in = new ObjectInputStream(layoutInput);
                rootWindow.read(in);
                in.close();
                rootWindow.getWindowBar(Direction.LEFT).setEnabled(true);
                rootWindow.getWindowBar(Direction.RIGHT).setEnabled(true);
                if (isInit) {
                    int count = viewMap.getViewCount();
                    for (int i = 0; i < count; i++) {
                        View current = viewMap.getViewAtIndex(i);
                        if (current.isUndocked()) {
                            current.dock();
                        }
                    }
                }
                log.debug("Loading Layout successfull");
            } catch (IOException ex) {
                log.error("Layout File IO Exception --> loading default Layout", ex);
                if (isInit) {
                    JOptionPane.showMessageDialog(this, java.util.ResourceBundle.getBundle("de/cismet/verdis/res/i18n/Bundle").getString("CismapPlugin.InfoNode.loading_layout_failure_message_init"), java.util.ResourceBundle.getBundle("de/cismet/verdis/res/i18n/Bundle").getString("CismapPlugin.InfoNode.message_title"), JOptionPane.INFORMATION_MESSAGE);
                    setupDefaultLayout();
                } else {
                    JOptionPane.showMessageDialog(this, java.util.ResourceBundle.getBundle("de/cismet/verdis/res/i18n/Bundle").getString("CismapPlugin.InfoNode.loading_layout_failure_message"), java.util.ResourceBundle.getBundle("de/cismet/verdis/res/i18n/Bundle").getString("CismapPlugin.InfoNode.message_title"), JOptionPane.INFORMATION_MESSAGE);
                }

            }
        } else {
            if (isInit) {
                log.fatal("Datei exitstiert nicht --> default layout (init)");
                EventQueue.invokeLater(new Runnable() {

                    public void run() {
                        //UGLY WINNING --> Gefixed durch IDW Version 1.5
                        //setupDefaultLayout();
                        //DeveloperUtil.createWindowLayoutFrame("nach setup1",rootWindow).setVisible(true);
                        setupDefaultLayout();
                        //DeveloperUtil.createWindowLayoutFrame("nach setup2",rootWindow).setVisible(true);
                    }
                });
            } else {
                log.fatal("Datei exitstiert nicht)");
                JOptionPane.showMessageDialog(this, java.util.ResourceBundle.getBundle("de/cismet/verdis/res/i18n/Bundle").getString("CismapPlugin.InfoNode.layout_does_not_exist"), java.util.ResourceBundle.getBundle("de/cismet/verdis/res/i18n/Bundle").getString("CismapPlugin.InfoNode.message_title"), JOptionPane.INFORMATION_MESSAGE);
            }

        }
    }
    //Inserting Docking Window functionalty (Sebastian) 24.07.07

    public void saveLayout(String file) {
        log.debug("Saving Layout.. to " + file);
        File layoutFile = new File(file);
        try {
            if (!layoutFile.exists()) {
                log.debug("Saving Layout.. File does not exit");
                File verdisDir = new File(verdisDirectory);
                if (!verdisDir.exists()) {
                    log.debug("Verdis Directory angelegt");
                    verdisDir.mkdir();
                }
                layoutFile.createNewFile();
            } else {
                log.debug("Saving Layout.. File does exit");
            }
            FileOutputStream layoutOutput = new FileOutputStream(layoutFile);
            ObjectOutputStream out = new ObjectOutputStream(layoutOutput);
            setLeftTitleBarColor(myBlue);
            rootWindow.write(out);
            out.flush();
            out.close();
            log.debug("Saving Layout.. to " + file + " successfull");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, java.util.ResourceBundle.getBundle("de/cismet/verdis/res/i18n/Bundle").getString("CismapPlugin.InfoNode.saving_layout_failure"), java.util.ResourceBundle.getBundle("de/cismet/verdis/res/i18n/Bundle").getString("CismapPlugin.InfoNode.message_title"), JOptionPane.INFORMATION_MESSAGE);
            log.error("A failure occured during writing the layout file", ex);
        }
    }

    //Inserting Docking Window functionalty (Sebastian) 24.07.07
    private void showOrHideView(View v) {
        ///irgendwas besser als Closable ??
        // Problem wenn floating --> close -> open  (muss zweimal open)




        if (v.isClosable()) {
            v.close();
        } else {
            v.restore();
        }
    }

    //Inserting Docking Window functionalty (Sebastian) 24.07.07
    public void setFlaechenPanelIcon(Icon icoFlaeche) {
        if (!isInit) {
            vFlaechen.getViewProperties().setIcon(icoFlaeche);
        }
    }

    //Inserting Docking Window functionalty (Sebastian) 24.07.07
    public void setLetzteAenderungTooltip(String letzteAenderung) {
        if (!isInit) {
            //((ImageIcon)vKassenzeichen.getIcon()).setDescription()
            //vKassenzeichen.get
            //vKassenzeichen.getViewProperties().getViewTitleBarProperties().getNormalProperties().
        }
    }

    //Inserting Docking Window functionalty (Sebastian) 24.07.07
    public void setSummenPanelIcon(Icon icoSumme) {
        if (!isInit) {
            vSummen.getViewProperties().setIcon(icoSumme);
        }
    }

    //Inserting Docking Window functionalty (Sebastian) 24.07.07
    public void setKanalTitleForeground(Color foreground) {
        if (!isInit) {
            vKanaldaten.getViewProperties().getViewTitleBarProperties().getNormalProperties().getComponentProperties().setForegroundColor(foreground);
        }
    }

    private void mnuChangeUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuChangeUserActionPerformed
        formWindowOpened(null);
    }//GEN-LAST:event_mnuChangeUserActionPerformed

    private void mnuNewKassenzeichenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuNewKassenzeichenActionPerformed
        cmdNewKassenzeichenActionPerformed(null);
    }//GEN-LAST:event_mnuNewKassenzeichenActionPerformed

    private void mnuEditModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuEditModeActionPerformed
        cmdEditModeActionPerformed(null);
    }//GEN-LAST:event_mnuEditModeActionPerformed

    private void mnuExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExitActionPerformed
        dispose();
    }//GEN-LAST:event_mnuExitActionPerformed
//TODO right ??
    /* RM Plugin functionalty added at 22.07.07 Sebastian Puhl
     * @see also RMPlugin.java
     *
     */
    private RMPlugin rmPlugin;

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        if (!plugin) {
            Thread t = new Thread(new Runnable() {

                public void run() {
                    final DefaultUserNameStore usernames = new DefaultUserNameStore();
                    Preferences appPrefs = Preferences.userNodeForPackage(this.getClass());
                    usernames.setPreferences(appPrefs.node("login"));
                    WundaAuthentification wa = new WundaAuthentification();
                    final JXLoginPane login = new JXLoginPane(wa, null, usernames) {

                        protected Image createLoginBanner() {
                            return getBannerImage();
                        }
                    };
                    String u = null;
                    try {
                        u = usernames.getUserNames()[usernames.getUserNames().length - 1];
                    } catch (Exception skip) {
                    }
                    if (u != null) {
                        login.setUserName(u);
                    }
                    final JXLoginPane.JXLoginDialog d = new JXLoginPane.JXLoginDialog(Main.this, login);

                    d.addComponentListener(new ComponentAdapter() {

                        public void componentHidden(ComponentEvent e) {
                            handleLoginStatus(d.getStatus(), usernames, login);
                        }
                    });
                    d.addWindowListener(new WindowAdapter() {

                        public void windowClosed(WindowEvent e) {
                            handleLoginStatus(d.getStatus(), usernames, login);

                        }
                    });
//                    EventQueue.invokeLater(new Runnable() {

                    login.setPassword("".toCharArray());
                    d.setLocationRelativeTo(Main.this);
                    try {
                        ((JXPanel) ((JXPanel) login.getComponent(1)).getComponent(1)).getComponent(3).requestFocus();
                    } catch (Exception skip) {
                    }
                    d.setVisible(true);
//                        }
                    //});

                }
            });
            t.setPriority(Thread.NORM_PRIORITY);
            t.start();
        }
    }//GEN-LAST:event_formWindowOpened

    private void handleLoginStatus(JXLoginPane.Status status, DefaultUserNameStore usernames, JXLoginPane login) {
        if (status == JXLoginPane.Status.SUCCEEDED) {
            //Damit wird sichergestellt, dass dieser als erstes vorgeschlagen wird
            usernames.removeUserName(login.getUserName());
            usernames.saveUserNames();
            usernames.addUserName((login.getUserName()));
            usernames.saveUserNames();
            //Added for RM Plugin functionalty 22.07.2007 Sebastian Puhl
            log.debug("Login erfolgreich");
        } else {
            log.debug("Login fehlgeschlagen");
            System.exit(0);
        }
    }

    private void cmdRefreshEnumerationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdRefreshEnumerationActionPerformed
        flPanel.reEnumerateFlaechen();
    }//GEN-LAST:event_cmdRefreshEnumerationActionPerformed

    private void cmdPdfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdPdfActionPerformed
        if (kzPanel.getShownKassenzeichen() != null && kzPanel.getShownKassenzeichen().length() > 0) {
            try {
                String gotoUrl = prefs.getReportUrl() + kzPanel.getShownKassenzeichen();
                AppletContext appletContext = null;
                try {
                    appletContext = context.getEnvironment().getAppletContext();
                } catch (Exception npe) {
                    //nothing to do
                }
                if (appletContext == null) {
                    de.cismet.tools.BrowserLauncher.openURL(gotoUrl);
                } else {
                    java.net.URL u = new java.net.URL(gotoUrl);
                    appletContext.showDocument(u, "verdisReportFrame");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Fehler beim Anzeigen des VERDIS-Reports", "Fehler", JOptionPane.ERROR_MESSAGE);
                log.error("Fehler beim Anzeigen des VERDIS-Reports", e);
            }
        }
    }//GEN-LAST:event_cmdPdfActionPerformed

    private void cmdDeleteKassenzeichenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdDeleteKassenzeichenActionPerformed
        deleteKZ();
    }//GEN-LAST:event_cmdDeleteKassenzeichenActionPerformed

    private void cmdPasteFlaecheActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdPasteFlaecheActionPerformed
        if (clipboard != null) {
            if (clipboard instanceof Flaeche) {
                Flaeche clipboardFlaeche = (Flaeche) clipboard;
                if (clipboardFlaeche.getClipboardStatus() == Flaeche.CUTTED) {
                    flPanel.pasteFlaeche(clipboardFlaeche);
//                    clipboard=null;
//                    cmdPasteFlaeche.setEnabled(false);
                    //siehe unten
                } else {
                    flPanel.pasteFlaeche(clipboardFlaeche);
                    clipboardPasted = true;
                }
            } else if (clipboard instanceof Vector) {
                Iterator it = ((Vector) clipboard).iterator();
                boolean cutting = false;
                //////////////////////////////////

                while (it.hasNext()) {
                    Flaeche clipboardFlaeche = (Flaeche) it.next();
                    if (clipboardFlaeche.getClipboardStatus() == Flaeche.CUTTED) {
                        /////////////////////////////////////////////////////////////////////////////////////

                        flPanel.pasteFlaecheWithoutRefresh((Flaeche) clipboardFlaeche.clone());
                        //((Vector)clipboard).remove(clipboardFlaeche);
                        cutting = true;
                    } else {
                        flPanel.pasteFlaecheWithoutRefresh((Flaeche) clipboardFlaeche.clone());
                    }
                    flPanel.refreshTableAndMapAfterPaste((Flaeche) clipboardFlaeche.clone());
                }

                clipboardPasted = true;
//Vorerst keine Sonderbehandlung nach dem ausgeschnittennen Einf\u00FCgen wg. bug
//                if (cutting) {
//                    cmdPasteFlaeche.setEnabled(false);
//                    clipboard=null;
//                }

            }
        }

    }//GEN-LAST:event_cmdPasteFlaecheActionPerformed

    private void cmdCutFlaecheActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdCutFlaecheActionPerformed
        int answer = JOptionPane.YES_OPTION;
        if (!clipboardPasted) {
            answer = JOptionPane.showConfirmDialog(this, "In der Verdis-Zwischenablage befinden sich noch Daten.\nSollen die Daten verworfen und die ausgew\u00E4hlte Selektion ausgeschnitten werden ?", "Ausschneiden", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        }
        if (answer == JOptionPane.YES_OPTION) {
            clipboard = flPanel.cutSelectedFlaeche();
            storeClipboardBackup();

            if (clipboard != null) {
                this.cmdPasteFlaeche.setEnabled(true);
                clipboardPasted = false;
            } else {
                this.cmdPasteFlaeche.setEnabled(false);
            }
        }


    }//GEN-LAST:event_cmdCutFlaecheActionPerformed

    private void cmdInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdInfoActionPerformed
//        String info="Verdis Plugin\n"
//                + "cismet GmbH\n\n"
//                + de.cismet.verdis.Version.getVersion()+"\n"
//                + de.cismet.cismap.commons.Version.getVersion();
//        JOptionPane.showMessageDialog(this,info,"Info",JOptionPane.INFORMATION_MESSAGE);

        if (about == null) {
            JDialog d = new JDialog(this, "Info");
            d.setLayout(new BorderLayout());


            //JLabel infoLabel=new JLabel(de.cismet.verdis.Version.getVersion()+"\n"+ de.cismet.cismap.commons.Version.getVersion());

            //d.add(infoLabel,BorderLayout.SOUTH);

            JLabel image = new JLabel(new ImageIcon(getBannerImage()));
            d.add(image, BorderLayout.CENTER);
            JLabel version = new JLabel(de.cismet.verdis.Version.getVersion());

            d.add(version, BorderLayout.SOUTH);
            d.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
            d.pack();
            about = d;
        }
        about.setLocationRelativeTo(this);
        about.setVisible(true);

    }//GEN-LAST:event_cmdInfoActionPerformed

    private void mnuInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuInfoActionPerformed
        cmdInfoActionPerformed(null);
    }//GEN-LAST:event_mnuInfoActionPerformed

    private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_formKeyReleased

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed

        if (evt.getKeyCode() == KeyEvent.VK_F1 && evt.isControlDown()) {
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_formKeyPressed

    private void formKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyTyped
    }//GEN-LAST:event_formKeyTyped

    private void cmdPutKassenzeichenToSearchTreeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdPutKassenzeichenToSearchTreeActionPerformed
        if (kzPanel.getShownKassenzeichen() != null && !kzPanel.getShownKassenzeichen().trim().equals("")) {
            log.debug("Alle verf\u00FCgbaren Suchen:" + context.getSearch().getDataBeans().keySet());
            Object object = context.getSearch().getDataBeans().get(kassenzeichenSuche + "@" + domainServer);
            if (object != null) {
                FormDataBean kassenzeichenSucheParam = (FormDataBean) object;
                kassenzeichenSucheParam.setBeanParameter("Kassenzeichen", kzPanel.getShownKassenzeichen());
                Vector v = new Vector();
                String cid = String.valueOf(this.KASSENZEICHEN_CLASS_ID) + "@" + domainServer;
                v.add(cid);
                try {
                    log.debug("vor KassenzeichenSuche aus Plugin");
                    context.getSearch().performSearch(v, kassenzeichenSucheParam, context.getUserInterface().getFrameFor((PluginUI) this), true);
                } catch (Exception e) {
                    kzPanel.flashSearchField(java.awt.Color.red);
                }
            } else {
                log.warn("KassenzeichenSuche (" + kassenzeichenSuche + "@" + domainServer + ") nicht vorhanden!!!");
            }
        }
    }//GEN-LAST:event_cmdPutKassenzeichenToSearchTreeActionPerformed

    private void cmdWorkflowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdWorkflowActionPerformed
    }//GEN-LAST:event_cmdWorkflowActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        log.info("formWindowClosing");
        if (editmode && !kzPanel.isEmpty()) {
            if (changesPending()) {
                int answer = JOptionPane.showConfirmDialog(this, "Wollen Sie die gemachten \u00C4nderungen speichern?", "Verdis \u00C4nderungen", JOptionPane.YES_NO_OPTION);
                if (answer == JOptionPane.YES_OPTION) {
                    storeChanges();
                }
                unlockDataset();
            } else {
                unlockDataset();
            }
        }
        closeAllConnections();

    }//GEN-LAST:event_formWindowClosing

    private void closeAllConnections() {
        try {
            flPanel.getConnection().close();
            dokPanel.getConnection().close();
            kzPanel.getConnection().close();
            sumPanel.getConnection().close();
        } catch (Exception e) {
            log.error("Fehler beim Schlie\u00DFen der Connections");
        }
    }

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
    }//GEN-LAST:event_formWindowClosed

    private void cmdNewKassenzeichenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdNewKassenzeichenActionPerformed
        if (!readonly) {
            if (changesPending()) {
                int answer = JOptionPane.showConfirmDialog(this, "Wollen Sie die gemachten \u00C4nderungen zuerst speichern?", "Neues Kassenzeichen", JOptionPane.YES_NO_CANCEL_OPTION);
                if (answer == JOptionPane.YES_OPTION) {
                    storeChanges();
                    newKZ();
                } else if (answer == JOptionPane.NO_OPTION) {
                    newKZ();
                }
            } else {
                newKZ();
            }
        }
    }//GEN-LAST:event_cmdNewKassenzeichenActionPerformed

    private void cmdOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdOkActionPerformed
        if (changesPending()) {
            storeChanges();
        }


    }//GEN-LAST:event_cmdOkActionPerformed

    private void cmdCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdCancelActionPerformed
        if (changesPending()) {
            int answer = JOptionPane.showConfirmDialog(this, "Wollen Sie die gemachten \u00C4nderungen verwerfen?", "Abbrechen", JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                fixMapExtent = getFlPanel().getFlOverviewPanel().getMappingComponent().isFixedMapExtent();
                getFlPanel().getFlOverviewPanel().getMappingComponent().setFixedMapExtent(true);
                kzPanel.refresh();
                enableEditing(false);
                unlockDataset();
            }
        } else {
            enableEditing(false);
            unlockDataset();
        }
    }//GEN-LAST:event_cmdCancelActionPerformed

    private void cmdEditModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdEditModeActionPerformed
        if (!readonly) {
            if (!editmode && (kzPanel.isEmpty() || lockDataset())) {
                enableEditing(true);
            } else if (editmode && changesPending() == false) {
                unlockDataset();
                enableEditing(false);
            }
        }
    }//GEN-LAST:event_cmdEditModeActionPerformed

    private void cmdCopyFlaecheActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdCopyFlaecheActionPerformed
        log.fatal("cmdCopyFlaecheActionPerformed");
        int answer = JOptionPane.YES_OPTION;
        if (!clipboardPasted) {
            answer = JOptionPane.showConfirmDialog(this, "In der Verdis-Zwischenablage befinden sich noch Daten.\nSollen die Daten verworfen und die ausgew\u00E4hlte Selektion kopiert werden ?", "Ausschneiden", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        }
        if (answer == JOptionPane.YES_OPTION) {
            clipboard = flPanel.getSelectedFlaeche();
            storeClipboardBackup();
            if (clipboard != null) {
                clipboardPasted = false;
                this.cmdPasteFlaeche.setEnabled(true);
            } else {
                this.cmdPasteFlaeche.setEnabled(false);
            }
        }

    }//GEN-LAST:event_cmdCopyFlaecheActionPerformed

    private void storeClipboardBackup() {
        try {
            XStream x = new XStream(new Dom4JDriver());
            FileWriter f = new FileWriter(clipboardBackup);
            x.toXML(clipboard, f);
            f.close();

        } catch (Exception ex) {
            log.error("Beim Sichern des Clipboards ist etwas schiefgegangen", ex);
        }
    }

    private void loadClipboardBackup() {
        try {

            XStream x = new XStream(new Dom4JDriver());
            Flaeche f = (Flaeche) x.fromXML(new FileReader(clipboardBackup));
            f.initAfterXMLLoad();
            clipboard = f;
        } catch (Exception exception) {
            log.error("Beim Laden des ClipboardBackups ist etwas schiefgegangen", exception);
        }

    }

    private void deleteClipboardBackup() {
        try {
            File f = new File(clipboardBackup);
            f.delete();

        } catch (Exception exception) {
            log.warn("Beim L\u00F6schen vom Clipboardbackup ging etwas schief", exception);
        }
    }
    private void cmdTestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdTestActionPerformed
}//GEN-LAST:event_cmdTestActionPerformed

    private void cmdTest2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdTest2ActionPerformed
    }//GEN-LAST:event_cmdTest2ActionPerformed


    //ToDo Threading and Progressbar
    private void cmdLagisCrossoverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdLagisCrossoverActionPerformed
        try {
            final String currentKZ = getKzPanel().getShownKassenzeichen();
            if (currentKZ != null && currentKZ.length() > 0) {
                final Geometry kassenzeichenGeom = getFlPanel().getFlOverviewPanel().getModel().getKassenzeichenGeometry();
                if (kassenzeichenGeom != null) {
                    log.info("Crossover: Geometrie zum bestimmen der Flurstücke: " + kassenzeichenGeom);
                    final LagisCrossoverRemote lagisCrossover = prefs.getLagisCrossoverAccessor();
                    final LagisServerRemote lagisServer = prefs.getLagisServerAccessor();
                    if (lagisCrossover != null && lagisServer != null) {
                        final Set<WfsFlurstuecke> wfsFlurstuecke = lagisCrossover.getIntersectingFlurstuecke(kassenzeichenGeom);
                        if (wfsFlurstuecke != null && wfsFlurstuecke.size() > 0) {
                            log.debug("Crossover: Anzahl WFS Flurstücke: " + wfsFlurstuecke.size());
                            final Set<FlurstueckSchluessel> flurstueckSchluessel = lagisServer.getFlurstueckSchluesselForWFSFlurstueck(wfsFlurstuecke);
                            if (flurstueckSchluessel != null && flurstueckSchluessel.size() > 0) {
                                log.debug("Crossover: Anzahl Flurstück Schlüssel: " + flurstueckSchluessel.size());
                                if (flurstueckSchluessel.size() != wfsFlurstuecke.size()) {
                                    log.warn("Crossover: Achtung Anzahl WFS/Schlüssel sind unterschiedlich");
                                }
                                final JDialog dialog = new JDialog(this, "", true);
                                dialog.add(new LagisCrossoverPanel(prefs.getLagisCrossoverPort(),flurstueckSchluessel));
                                dialog.pack();
                                dialog.setIconImage(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/toolbar/lagisCrossover.png")).getImage());
                                dialog.setTitle("Flurstück in LagIS öffnen.");
                                dialog.setLocationRelativeTo(this);
                                dialog.setVisible(true);
                            } else {
                                log.info("Crossover: Keine geschnittenen Flurstücke gefunden(Schlüssel).");
                                if (wfsFlurstuecke.size() != 0) {
                                    log.warn("Crossover: Achtung Anzahl WFS/Schlüssel sind unterschiedlich");
                                }
                            }
                        } else {
                            log.info("Crossover: Keine geschnittenen Flurstücke gefunden(WFS).");
                            //ToDo Meldung an benutzer
                        }
                    } else {
                        log.warn("Crossover: Kann die Flurstücke nicht bestimmen, weil die Verbindung zum server nicht richtig konfiguriert ist.");
                        log.warn("Crossover: lagisCrossover=" + lagisCrossover);
                        log.warn("Crossover: lagisServer=" + lagisServer);
                    }
                } else {
                    //ToDo user message !
                    log.warn("Crossover: Keine Geometrie vorhanden zum bestimmen der Flurstücke");
                }
            } else {
                //ToDo user message !
                log.warn("Crossover: Kein Kassenzeichen ausgewählt kann Lagis Flurstück nicht bestimmen");
            }
        } catch (Exception ex) {
            log.error("Crossover: Fehler im LagIS Crossover", ex);
            //ToDo Meldung an Benutzer
        }
    }//GEN-LAST:event_cmdLagisCrossoverActionPerformed

    public void newKZ() {

        String newKZ = JOptionPane.showInputDialog(this, "Geben Sie das neue Kassenzeichen ein:", "Neues Kassenzeichen", JOptionPane.QUESTION_MESSAGE);
        if (!(newKZ == null || newKZ.equals(""))) {
            try {
                Integer test = new Integer(newKZ);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Kassenzeichen muss eine Zahl sein.", "Fehler", JOptionPane.ERROR_MESSAGE);
                return;
            }

            this.unlockDataset();
            if (this.lockDataset(newKZ)) {
                Vector newKZStatements = new Vector();
                Kassenzeichen.collectActions4NewKassenzeichen(newKZStatements, newKZ);
                kzPanel.setKZSearchField(newKZ);
                storeChanges(newKZStatements, true, newKZ);
                this.unlockDataset(newKZ);
                //kzPanel.gotoKassenzeichen(newKZ);
                flPanel.kassenzeichenChanged(newKZ);
                enableEditing(true);
            } else {
                JOptionPane.showMessageDialog(this, "Neues Kassenzeichen kann nicht gesperrt werden.", "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void deleteKZ() {
        if (kzPanel.getShownKassenzeichen() != null && !(kzPanel.getShownKassenzeichen().trim().equals(""))) {
            int answer = JOptionPane.showConfirmDialog(this, "Wollen Sie wirklich das Kassenzeichen " + kzPanel.getShownKassenzeichen() + " l\u00F6schen?", "Kassenzeichen l\u00F6schen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (answer == JOptionPane.YES_OPTION) {
                Vector deleteKZStatements = new Vector();
                String kz2delete = kzPanel.getShownKassenzeichen();
                Kassenzeichen.collectActions4DeleteKassenzeichen(deleteKZStatements, kz2delete);
                kzPanel.setKZSearchField("");
                storeChanges(deleteKZStatements, false);
                this.unlockDataset(kz2delete);
                //kzPanel.gotoKassenzeichen("6000467");
                kzPanel.clear();
                //flPanel.clear();

            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        Thread t = new Thread() {

            public void run() {
                final Main m = new Main();
                EventQueue.invokeLater(new Runnable() {

                    public void run() {
                        ((JFrame) m).setVisible(true);
                    }
                });
            }
        };
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();
    }

    public void setVisible(boolean b) {
        if (!plugin) {
            super.setVisible(b);
        }

    }

    //Methoden f\u00FCr die Plugin Schnittstelle
    public PluginUI getUI(String str) {
        return this;
    }

    public PluginMethod getMethod(String str) {
        return null;
    }

    public void setActive(boolean param) {
        log.debug("setActive(" + param + ")");
        if (param == false && editmode && !kzPanel.isEmpty()) {
            if (changesPending()) {
                int answer = JOptionPane.showConfirmDialog(this, "Wollen Sie die gemachten \u00C4nderungen speichern?", "Verdis \u00C4nderungen", JOptionPane.YES_NO_OPTION);
                if (answer == JOptionPane.YES_OPTION) {
                    storeChanges();
                }
                unlockDataset();
            } else {
                unlockDataset();
            }
        }
        if (param == false) {
            closeAllConnections();
            //Inserting Docking Window functionalty (Sebastian) 24.07.07
            configurationManager.writeConfiguration();
            saveLayout(pluginPathname);
        }
        deleteClipboardBackup();
    }

    public java.util.Iterator getUIs() {
        LinkedList ll = new LinkedList();
        ll.add(this);
        return ll.iterator();
    }

    public PluginProperties getProperties() {
        return null;
    }

    public java.util.Iterator getMethods() {
        LinkedList ll = new LinkedList();
        return ll.iterator();
    }

    public void shown() {
    }

    public void resized() {
    }

    public void moved() {
    }

    public void hidden() {
    }

    public java.util.Collection getMenus() {
        return menues;
    }

    public String getId() {
        return "verdis";
    }

    public JComponent getComponent() {
        return panMain;
    }

    public java.util.Collection getButtons() {
        return Arrays.asList(this.tobVerdis.getComponents());
        //return null;
    }

    public void floatingStopped() {
    }

    public void floatingStarted() {
    }

    private class NodeChangeListener extends MetaNodeSelectionListener {

        private final SingleAttributeIterator attributeIterator;
        private final Collection classNames;
        private final Collection attributeNames;

        private NodeChangeListener() {
            this.classNames = context.getEnvironment().getAttributeMappings("className");
            this.attributeNames = context.getEnvironment().getAttributeMappings("attributeName");
            if (this.attributeNames.size() == 0) {
                this.attributeNames.add("id");
            }

            AttributeRestriction attributeRestriction = new ComplexAttributeRestriction(AttributeRestriction.OBJECT, AttributeRestriction.IGNORE, null, this.attributeNames, null);
            this.attributeIterator = new SingleAttributeIterator(attributeRestriction, false);
        }
        private Object nodeSelectionChangedBlocker = new Object();

        protected void nodeSelectionChanged(final Collection collection) {

            Thread t = new Thread() {

                public void run() {
                    synchronized (nodeSelectionChangedBlocker) {
                        if (collection != null || collection.size() != 0) {
                            Object selectedNode = collection.iterator().next();
                            if (selectedNode instanceof ObjectTreeNode) {
                                ObjectTreeNode objectTreeNode = (ObjectTreeNode) selectedNode;
                                try {
                                    if (NodeChangeListener.this.classNames.size() == 0 || NodeChangeListener.this.classNames.contains(objectTreeNode.getMetaClass().getName())) {
                                        attributeIterator.init(objectTreeNode);
                                        Object kassenzeichen = null;
                                        if (attributeIterator.hasNext()) {
                                            kassenzeichen = attributeIterator.next().getValue();
                                            getKzPanel().gotoKassenzeichen(kassenzeichen.toString());
                                        } else {
                                            log.debug("falscher attribute name");
                                            log.debug(kassenzeichen);
                                        }
                                    } else {
                                        log.debug("falscher class name");
                                        log.debug(objectTreeNode.getMetaClass().getName());
                                        log.debug(classNames);

                                    }
                                } catch (Throwable t) {
                                    log.error(t.getMessage(), t);
                                }
                            } else {
                                log.debug("keine object node");
                            }
                        }
                    }
                }
            };
            t.setPriority(Thread.NORM_PRIORITY);
            t.start();
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdCancel;
    private javax.swing.JButton cmdCopyFlaeche;
    private javax.swing.JButton cmdCutFlaeche;
    private javax.swing.JButton cmdDeleteKassenzeichen;
    private javax.swing.JButton cmdEditMode;
    private javax.swing.JButton cmdInfo;
    private javax.swing.JButton cmdLagisCrossover;
    private javax.swing.JButton cmdNewKassenzeichen;
    private javax.swing.JButton cmdOk;
    private javax.swing.JButton cmdPasteFlaeche;
    private javax.swing.JButton cmdPdf;
    private javax.swing.JButton cmdPutKassenzeichenToSearchTree;
    private javax.swing.JButton cmdRefreshEnumeration;
    private javax.swing.JButton cmdTest;
    private javax.swing.JButton cmdTest2;
    private javax.swing.JButton cmdWorkflow;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator11;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JMenu menEdit;
    private javax.swing.JMenu menExtras;
    private javax.swing.JMenu menFile;
    private javax.swing.JMenu menHelp;
    private javax.swing.JMenu menWindows;
    private javax.swing.JMenuItem mniClose;
    private javax.swing.JMenuItem mniDokumente;
    private javax.swing.JMenuItem mniFlaechen;
    private javax.swing.JMenuItem mniKanalanschluss;
    private javax.swing.JMenuItem mniKassenzeichen;
    private javax.swing.JMenuItem mniLoadLayout;
    private javax.swing.JMenuItem mniResetWindowLayout;
    private javax.swing.JMenuItem mniSaveLayout;
    private javax.swing.JMenuItem mniSummen;
    private javax.swing.JMenuItem mnuChangeUser;
    private javax.swing.JMenuItem mnuEditMode;
    private javax.swing.JMenuItem mnuHelp;
    private javax.swing.JMenuItem mnuInfo;
    private javax.swing.JMenuItem mnuNewKassenzeichen;
    private javax.swing.JPanel panMain;
    private javax.swing.JToolBar tobVerdis;
    // End of variables declaration//GEN-END:variables

    public boolean changesPending() {
        Iterator it = stores.iterator();
        while (it.hasNext()) {
            Storable store = (Storable) it.next();
            if (store.changesPending()) {
                return true;
            }
        }
        return false;
    }

    public void enableEditing(boolean b) {
        try {
            editmode = b;


            cmdOk.setEnabled(b);
            cmdCancel.setEnabled(b);
            cmdDeleteKassenzeichen.setEnabled(b);

            cmdPasteFlaeche.setEnabled(b);
            cmdCopyFlaeche.setEnabled(b);
            cmdCutFlaeche.setEnabled(b);
            cmdRefreshEnumeration.setEnabled(b);


            Iterator it = stores.iterator();
            while (it.hasNext()) {
                Storable store = (Storable) it.next();
                store.enableEditing(b);
            }
            refreshLeftTitleBarColor();

            flPanel.getFlOverviewPanel().getMappingComponent().getMemRedo().clear();
            flPanel.getFlOverviewPanel().getMappingComponent().getMemUndo().clear();
        } catch (Exception e) {
            log.error("Fehler beim Wechseln in den EditMode", e);
        }
    }

    public boolean lockDataset() {
        return kzPanel.lockDataset();
    }

    public boolean lockDataset(String object_id) {
        return kzPanel.lockDataset(object_id);
    }

    public void unlockDataset() {
        kzPanel.unlockDataset();
    }

    public void unlockDataset(String object_id) {
        kzPanel.unlockDataset(object_id);
    }

    public void storeChanges() {
        Vector allStatements = new Vector();
        try {
            addStoreChangeStatements(allStatements);
            storeChanges(allStatements, false);
        } catch (NotValidException nve) {
            JOptionPane.showMessageDialog(this, "\u00C4nderungen k\u00F6nnen nur gespeichert werden wenn alle Inhalte korrekt sind.\nBitte berichtigen Sie die Inhalte oder machen Sie die jeweiligen \u00C4nderungen r\u00FCckg\u00E4ngig.", "Fehler", JOptionPane.WARNING_MESSAGE);
        }

    }

    public void storeChanges(Vector statements, final boolean editModeAfterStoring, String refreshingKassenzeichen) {
        Vector allStatements = statements;

        dbWriter.write(allStatements);
        Point p = getLocation();
        dbWriter.setLocation(p.x + (getWidth() - dbWriter.getWidth()) / 2, p.y + (getHeight() - dbWriter.getHeight()) / 2);
        this.dbWriter.setVisible(true);
        if (!editModeAfterStoring) {
            unlockDataset();
        }
        enableEditing(editModeAfterStoring);

        fixMapExtent = getFlPanel().getFlOverviewPanel().getMappingComponent().isFixedMapExtent();
        getFlPanel().getFlOverviewPanel().getMappingComponent().setFixedMapExtent(true);
        if (refreshingKassenzeichen == null) {
            getKzPanel().refresh();
        } else {
            getKzPanel().gotoKassenzeichen(refreshingKassenzeichen);
        }



    }
    private boolean fixMapExtent;

    public void resetFixedMapExtent() {
        getFlPanel().getFlOverviewPanel().getMappingComponent().setFixedMapExtent(fixMapExtent);
    }

    public void storeChanges(Vector statements, final boolean editModeAfterStoring) {
        storeChanges(statements, editModeAfterStoring, null);
    }

    public void addStoreChangeStatements(Vector v) throws NotValidException {
        Iterator it = stores.iterator();
        while (it.hasNext()) {
            Storable store = (Storable) it.next();
            if (store.changesPending()) {
                store.addStoreChangeStatements(v);
            }
        }
        if (v.size() > 0) {
            //Aktualisiere Timestamp und User in Kassenzeichen
            SimpleDbAction dba = new SimpleDbAction();
            dba.setDescription("Aktualisiere Userinformation und Timestamp der letzten \u00C4nderung");
            dba.setType(SimpleDbAction.UPDATE);
            dba.setStatement("update kassenzeichen set letzte_aenderung_ts=now(),letzte_aenderung_von='" + userString + "'  where id=" + kzPanel.getShownKassenzeichen());
            v.add(dba);
        }
    }

    public de.cismet.verdis.gui.KassenzeichenPanel getKzPanel() {
        return kzPanel;
    }

    public KanaldatenPanel getKanalPanel() {
        return kanaldatenPanel;
    }

    public de.cismet.verdis.gui.FlaechenPanel getFlPanel() {
        return flPanel;
    }

    public boolean isInEditMode() {
        return editmode;
    }

    public String getUserString() {

        return userString;

    }

    public void configure(Element parent) {
//        Element verdis = parent.getChild("verdis");
//        try {
//            final BoundingBox bb = new BoundingBox(verdis);
//            log.info("Setze Startumgebung (initialBoundingBox) aus User Home:" + bb);
//            ((DefaultMappingModel)getMappingComponent().getMappingModel()).setInitialBoundingBox(bb);
//
//        } catch (DataConversionException ex) {
//            log.warn("Fehler in configure", ex);
//        }
    }

    public Element getConfiguration() {
//        try {
//            Element verdis = new Element("verdis");
//        
//        verdis.addContent(getMappingComponent().getConfiguration());
////        getMappingComponent().getMappingModel().getRasterServices()
//        return verdis;
//        }
//        catch (Exception e) {
//            log.error("Fehler beim Schreiben der Config",e);
//            return null;
//        }
        return null;
    }

    public void masterConfigure(Element arg0) {
    }

    public MappingComponent getMappingComponent() {
        return getFlPanel().getFlOverviewPanel().getMappingComponent();
    }

    class WundaAuthentification extends LoginService {

        public static final String CONNECTION_CLASS = "Sirius.navigator.connection.RMIConnection";
        public static final String CONNECTION_PROXY_CLASS = "Sirius.navigator.connection.proxy.DefaultConnectionProxyHandler";

        public boolean authenticate(String name, char[] password, String server) throws Exception {
            System.setProperty("sun.rmi.transport.connectionTimeout", "15");
            String user = name.split("@")[0];
            String group = name.split("@")[1];

            String callServerURL = "rmi://" + prefs.getStandaloneCallServerHost() + "/callServer";
            log.debug("callServerUrl:" + callServerURL);
            String domain = prefs.getStandaloneDomainname();

            Remote r = null;
            try {
                Connection connection = ConnectionFactory.getFactory().createConnection(CONNECTION_CLASS, callServerURL);
                ConnectionSession session = null;
                ConnectionProxy proxy = null;
                ConnectionInfo connectionInfo = new ConnectionInfo();
                connectionInfo.setCallserverURL(callServerURL);
                connectionInfo.setPassword(new String(password));
                connectionInfo.setUserDomain(domain);
                connectionInfo.setUsergroup(group);
                connectionInfo.setUsergroupDomain(domain);
                connectionInfo.setUsername(user);

                session = ConnectionFactory.getFactory().createSession(connection, connectionInfo, true);
                proxy = ConnectionFactory.getFactory().createProxy(CONNECTION_PROXY_CLASS, session);
                //proxy = ConnectionFactory.getFactory().createProxy(CONNECTION_CLASS,CONNECTION_PROXY_CLASS, connectionInfo,false);
                SessionManager.init(proxy);
                String tester = (group + "@" + domain).toLowerCase();
                log.debug("authentication: tester = :" + tester);
                log.debug("authentication: name = :" + name);
                log.debug("authentication: RM Plugin key = :" + name + "@" + domain);
                if (prefs.getRwGroups().contains(tester)) {
                    Main.this.readonly = false;
                    setUserString(name);
                    log.debug("RMPlugin: wird initialisiert (VerdisStandalone)");
                    log.debug("RMPlugin: Mainframe " + Main.this);
                    log.debug("RMPlugin: PrimaryPort " + prefs.getPrimaryPort());
                    log.debug("RMPlugin: SecondaryPort " + prefs.getSecondaryPort());
                    log.debug("RMPlugin: Username " + (name + "@" + prefs.getStandaloneDomainname()));
                    log.debug("RMPlugin: RegistryPath " + prefs.getRmRegistryServerPath());
                    rmPlugin = new RMPlugin(Main.this, prefs.getPrimaryPort(), prefs.getSecondaryPort(), prefs.getRmRegistryServerPath(), name + "@" + prefs.getStandaloneDomainname());
                    log.debug("RMPlugin: erfolgreich initialisiert (VerdisStandalone)");
                    return true;
                } else if (prefs.getUsergroups().contains(tester)) {
                    Main.this.readonly = true;
                    setUserString(name);
                    rmPlugin = new RMPlugin(Main.this, prefs.getPrimaryPort(), prefs.getSecondaryPort(), prefs.getRmRegistryServerPath(), name + "@" + prefs.getStandaloneDomainname());
                    return true;
                } else {
                    log.debug("authentication else false");
                    return false;
                }


            } catch (Throwable t) {
                log.error("Fehler beim Anmelden", t);
                return false;
            }


        }
    }

    public void setUserString(String userString) {
        this.userString = userString;
        Main.this.setTitle("verdis [" + userString + "]");
        kzPanel.setUserString(userString);
    }
    Image banner = new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/login.png")).getImage();

    public Image getBannerImage() {
        return banner;
    }

    public void dispose() {
        super.dispose();
        log.debug("Dispose: Verdis wird beendet.");
        if (rmPlugin != null) {
            log.debug("Dispose: RMPlugin wird heruntergefahren");
            rmPlugin.setActive(false);
        }
        log.debug("Dispose: layout wird gespeichert.");
        //Inserting Docking Window functionalty (Sebastian) 24.07.07
        configurationManager.writeConfiguration();
        saveLayout(defaultLayoutFile);
        deleteClipboardBackup();
        System.exit(0);
    }
}
