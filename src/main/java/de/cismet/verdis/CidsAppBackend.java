/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 *  Copyright (C) 2010 thorsten
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.cismet.verdis;

import Sirius.navigator.connection.*;
import Sirius.navigator.connection.proxy.ConnectionProxy;
import Sirius.navigator.exception.ConnectionException;

import Sirius.server.middleware.types.AbstractAttributeRepresentationFormater;
import Sirius.server.middleware.types.HistoryObject;
import Sirius.server.middleware.types.LightweightMetaObject;
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.newuser.User;

import Sirius.util.collections.MultiMap;

import java.awt.Frame;

import java.sql.Timestamp;

import java.util.*;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.server.search.CidsServerSearch;

import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.gui.MappingComponent;

import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;

import de.cismet.verdis.commons.constants.KassenzeichenPropertyConstants;
import de.cismet.verdis.commons.constants.VerdisMetaClassConstants;

import de.cismet.verdis.data.AppPreferences;

import de.cismet.verdis.gui.Main;

import de.cismet.verdis.server.search.FlaechenCrossReferencesServerSearch;
import de.cismet.verdis.server.search.FrontenCrossReferencesServerSearch;

import static de.cismet.verdis.commons.constants.VerdisConstants.DOMAIN;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class CidsAppBackend implements CidsBeanStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
            CidsAppBackend.class);
    private static CidsAppBackend instance = null;
    private static AppPreferences appprefs;

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum Mode {

        //~ Enum constants -----------------------------------------------------

        REGEN {

            @Override
            public String toString() {
                return "REGEN";
            }
        },
        ESW {

            @Override
            public String toString() {
                return "ESW";
            }
        },
        ALLGEMEIN {

            @Override
            public String toString() {
                return "ALLGEMEIN";
            }
        }
    }

    //~ Instance fields --------------------------------------------------------

    private Map<Integer, CidsBean> sperreBeanMap = new HashMap<Integer, CidsBean>();

    private final MultiMap flaechenCrossReferences = new MultiMap();
    private final MultiMap frontenCrossReferences = new MultiMap();
    private final HashMap<Mode, FeatureAttacher> featureAttacherByMode = new HashMap<Mode, FeatureAttacher>(3);
    private final ArrayList<CidsBeanStore> beanStores = new ArrayList<CidsBeanStore>();
    private final ArrayList<EditModeListener> editModeListeners = new ArrayList<EditModeListener>();
    private final ArrayList<AppModeListener> appModeListeners = new ArrayList<AppModeListener>();
    private String domain = DOMAIN;
    private ConnectionProxy proxy = null;
    private CidsBean kassenzeichenBean = null;
    private boolean editable = false;
    private Frame frameToDisplayDialogs = null;
    private MappingComponent mainMap = null;
    private Mode mode = null;
    private int lastSplitFlaecheId = -1;
    private Map<CidsBean, Integer> flaecheToKassenzeichenQuerverweisMap = new HashMap<CidsBean, Integer>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CidsAppBackend object.
     *
     * @param  proxy  DOCUMENT ME!
     */
    private CidsAppBackend(final ConnectionProxy proxy) {
        try {
            this.proxy = proxy;
            if (!SessionManager.isInitialized()) {
                SessionManager.init(proxy);
                ClassCacheMultiple.setInstance(domain);
            }
        } catch (Throwable e) {
            log.fatal("no connection to the cids server possible. too bad.", e);
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  proxy  DOCUMENT ME!
     */
    public static synchronized void init(final ConnectionProxy proxy) {
//       if (instance!=null){
//           throw new IllegalStateException("Backend is already inited. Please call init(AppPreferences prefs) only once.");
//       }else {
        instance = new CidsAppBackend(proxy);
//       }
    }

    /**
     * DOCUMENT ME!
     */
    public static void devInit() {
        try {
            Log4JQuickConfig.configure4LumbermillOnLocalhost();
            final String callServerURL = "http://localhost:9986/callserver/binary";
            final String connectionClass = "Sirius.navigator.connection.RESTfulConnection";
            final Connection connection = ConnectionFactory.getFactory()
                        .createConnection(connectionClass, callServerURL);

            final ConnectionInfo connectionInfo = new ConnectionInfo();
            connectionInfo.setCallserverURL(callServerURL);
            connectionInfo.setPassword("sb");
            connectionInfo.setUserDomain(DOMAIN);
            connectionInfo.setUsergroup("VORN");
            connectionInfo.setUsergroupDomain(DOMAIN);
            connectionInfo.setUsername("SteinbacherD102");

            final ConnectionSession session = ConnectionFactory.getFactory()
                        .createSession(connection, connectionInfo, true);

            System.out.println("CidsAppBackend: session created");
            final ConnectionProxy proxy = ConnectionFactory.getFactory()
                        .createProxy("Sirius.navigator.connection.proxy.DefaultConnectionProxyHandler", session);
            init(proxy);
        } catch (Throwable e) {
            log.fatal("no connection to the cids server possible. too bad.", e);
        }
    }
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    public static synchronized CidsAppBackend getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Backend is not inited. Please call init(AppPreferences prefs) first.");
        }
        return instance;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   query   DOCUMENT ME!
     * @param   domain  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public MetaObject[] getMetaObject(final String query, final String domain) {
        MetaObject[] mos = null;
        try {
            final User user = SessionManager.getSession().getUser();
            final ConnectionProxy proxy = getProxy();
            mos = proxy.getMetaObjectByQuery(user, query, domain);
        } catch (ConnectionException ex) {
            log.error("error retrieving metaobject by query", ex);
        }
        return mos;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  editable  DOCUMENT ME!
     */
    public void setEditable(final boolean editable) {
        this.editable = editable;
        fireEditModeChanged();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * DOCUMENT ME!
     */
    public void save() {
        try {
            kassenzeichenBean.persist();
        } catch (Exception e) {
            log.error("couldn't persist cidsbean", e);
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void refresh() {
        if (kassenzeichenBean != null) {
            final int nummer = (Integer)kassenzeichenBean.getProperty(
                    KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER);
            retrieveKassenzeichenByNummer(nummer);
        } else {
            log.warn("kassenzeichen = null . cannot be refreshed");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   tablename  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public MetaClass getVerdisMetaClass(final String tablename) {
        try {
            return CidsBean.getMetaClassFromTableName(domain, tablename);
        } catch (Exception exception) {
            log.error("couldn't load metaclass for " + tablename, exception);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean lock() {
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kassenzeichen  DOCUMENT ME!
     */
    public void retrieveKassenzeichenByNummer(final int kassenzeichen) {
        new SwingWorker<Boolean, Void>() {

                @Override
                protected Boolean doInBackground() throws Exception {
                    final CidsBean ksBean = loadKassenzeichenByNummer(kassenzeichen);
                    setCidsBean(ksBean);
                    return ksBean != null;
                }

                @Override
                protected void done() {
                    try {
                    } catch (Exception e) {
                        log.error("Exception in Background Thread", e);
                    }
                }
            }.execute();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   kassenzeichen  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean loadKassenzeichenByNummer(final int kassenzeichen) {
        try {
            final MetaClass mcKassenzeichen = ClassCacheMultiple.getMetaClass(
                    DOMAIN,
                    VerdisMetaClassConstants.MC_KASSENZEICHEN);
            final String query = "SELECT " + mcKassenzeichen.getId() + ", " + KassenzeichenPropertyConstants.PROP__ID
                        + " FROM " + VerdisMetaClassConstants.MC_KASSENZEICHEN + " WHERE "
                        + KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER + " = " + kassenzeichen + ";";
            final MetaObject[] mos = proxy.getMetaObjectByQuery(query, 0);
            if ((mos == null) || (mos.length < 1)) {
                return null;
            } else {
                final MetaObject mo = mos[0];
                return mo.getBean();
            }
        } catch (ConnectionException ex) {
            log.error("error during retrieval of object", ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   query  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<CidsBean> getBeansByQuery(final String query) {
        try {
            final MetaObject[] mos = proxy.getMetaObjectByQuery(query, 0);
            final Collection<CidsBean> beans = new ArrayList<CidsBean>();
            for (final MetaObject mo : mos) {
                beans.add(mo.getBean());
            }
            return beans;
        } catch (ConnectionException ex) {
            log.error("error during retrieval of object", ex);
            return null;
        }
    }

    @Override
    public CidsBean getCidsBean() {
        return kassenzeichenBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        kassenzeichenBean = cidsBean;
        for (final CidsBeanStore cbs : beanStores) {
            if (cbs != this) { // Avoid endless loop
                cbs.setCidsBean(cidsBean);
            }
        }
        if (cidsBean != null) {
            updateCrossReferences(cidsBean);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    private void updateCrossReferences(final CidsBean cidsBean) {
        updateFlaechenCrossReferences(cidsBean);
        updateFrontenCrossReferences(cidsBean);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    private void updateFrontenCrossReferences(final CidsBean cidsBean) {
        final int kassenzeichenNummer = (Integer)cidsBean.getProperty(
                KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER);

        frontenCrossReferences.clear();

        new SwingWorker<Collection, Void>() {

                @Override
                protected Collection doInBackground() throws Exception {
                    final CidsServerSearch search = new FrontenCrossReferencesServerSearch(kassenzeichenNummer);
                    try {
                        return getProxy().customServerSearch(CidsAppBackend.getInstance().getSession().getUser(),
                                search);
                    } catch (ConnectionException ex) {
                        log.error("error during retrieval of object", ex);
                        return null;
                    }
                }

                @Override
                protected void done() {
                    try {
                        final Collection collection = get();
                        for (final Object row : collection) {
                            final Object[] fields = ((Collection)row).toArray();
                            // synchronized (kassenzeichenChangedBlocker) {
                            frontenCrossReferences.put(fields[1], fields[3] + ":" + fields[4]);
                            // }
                        }
                        Main.getCurrentInstance().getWdsrFrontenDetailsPanel().updateCrossReferences();
                    } catch (Exception ex) {
                        log.error("error while doing server search", ex);
                    }
                }
            }.execute();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    private void updateFlaechenCrossReferences(final CidsBean cidsBean) {
        final int kassenzeichenNummer = (Integer)cidsBean.getProperty(
                KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER);

        flaechenCrossReferences.clear();

        new SwingWorker<Collection, Void>() {

                @Override
                protected Collection doInBackground() throws Exception {
                    final CidsServerSearch search = new FlaechenCrossReferencesServerSearch(kassenzeichenNummer);
                    try {
                        return getProxy().customServerSearch(CidsAppBackend.getInstance().getSession().getUser(),
                                search);
                    } catch (ConnectionException ex) {
                        log.error("error during retrieval of object", ex);
                        return null;
                    }
                }

                @Override
                protected void done() {
                    try {
                        final Collection collection = get();
                        for (final Object row : collection) {
                            final Object[] fields = ((Collection)row).toArray();
                            final FlaecheCrossreference flaechenCrossReference = new FlaecheCrossreference((Integer)
                                    fields[0],
                                    (Integer)fields[1],
                                    (String)fields[2],
                                    (Integer)fields[3],
                                    (String)fields[4]);
                            flaechenCrossReferences.put((Integer)fields[1], flaechenCrossReference);
                        }
                        Main.getCurrentInstance().getRegenFlaechenDetailsPanel().updateCrossReferences();
                    } catch (Exception ex) {
                        log.error("error while doing server search", ex);
                    }
                }
            }.execute();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flaechenId  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<FlaecheCrossreference> getFlaechenCrossReferencesFor(final int flaechenId) {
        return (Collection<FlaecheCrossreference>)flaechenCrossReferences.get((Integer)flaechenId);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   kassenzeichenNummer  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<String> getFrontenCrossReferencesFor(final int kassenzeichenNummer) {
        return (Collection<String>)frontenCrossReferences.get(kassenzeichenNummer);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cbs  DOCUMENT ME!
     */
    public void addCidsBeanStore(final CidsBeanStore cbs) {
        beanStores.add(cbs);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cbs  DOCUMENT ME!
     */
    public void removeCidsBeanStore(final CidsBeanStore cbs) {
        beanStores.remove(cbs);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Frame getFrameToDisplayDialogs() {
        return frameToDisplayDialogs;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  frameToDisplayDialogs  DOCUMENT ME!
     */
    public void setFrameToDisplayDialogs(final Frame frameToDisplayDialogs) {
        this.frameToDisplayDialogs = frameToDisplayDialogs;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  editModeListener  DOCUMENT ME!
     */
    public void addEditModeListener(final EditModeListener editModeListener) {
        editModeListeners.add(editModeListener);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  editModeListener  DOCUMENT ME!
     */
    public void removeEditModeListener(final EditModeListener editModeListener) {
        editModeListeners.remove(editModeListener);
    }

    /**
     * DOCUMENT ME!
     */
    private void fireEditModeChanged() {
        for (final EditModeListener eml : editModeListeners) {
            eml.editModeChanged();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  appModeListener  DOCUMENT ME!
     */
    public void addAppModeListener(final AppModeListener appModeListener) {
        appModeListeners.add(appModeListener);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  appModeListener  DOCUMENT ME!
     */
    public void removeAppModeListener(final EditModeListener appModeListener) {
        appModeListeners.remove(appModeListener);
    }

    /**
     * DOCUMENT ME!
     */
    private void fireAppModeChanged() {
        for (final AppModeListener aml : appModeListeners) {
            aml.appModeChanged();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public MappingComponent getMainMap() {
        return mainMap;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  mainMap  DOCUMENT ME!
     */
    public void setMainMap(final MappingComponent mainMap) {
        this.mainMap = mainMap;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   kassenzeichen  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public HistoryObject[] getHistory(final int kassenzeichen) {
        return getHistory(kassenzeichen, 10);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   kassenzeichenId  DOCUMENT ME!
     * @param   howMuch          DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public HistoryObject[] getHistory(final int kassenzeichenId, final int howMuch) {
        try {
            final MetaClass mcKassenzeichen = ClassCacheMultiple.getMetaClass(
                    DOMAIN,
                    VerdisMetaClassConstants.MC_KASSENZEICHEN);
            return proxy.getHistory(mcKassenzeichen.getId(),
                    kassenzeichenId,
                    DOMAIN,
                    SessionManager.getSession().getUser(),
                    howMuch);
        } catch (ConnectionException ex) {
            log.error("error in retrieving the history og " + kassenzeichenId, ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   objectId  DOCUMENT ME!
     * @param   classtId  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public MetaObject getVerdisMetaObject(final int objectId, final int classtId) {
        try {
            return proxy.getMetaObject(objectId, classtId, DOMAIN);
        } catch (ConnectionException ex) {
            log.error("error in retrieving the metaobject " + objectId + " of classid " + classtId, ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   args  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void main(final String[] args) throws Exception {
        final long l = System.currentTimeMillis();
        // final MetaObject mo = CidsAppBackend.getInstance().proxy.getMetaObject(6000467, 11, DOMAIN);
// MetaObject mo = proxy.getMetaObject(6021737, 11, DOMAIN);
        System.out.println("dauer:" + (System.currentTimeMillis() - l));
        System.out.println("retrieved 6000467");
        // System.out.println(mo.getBean().toJSONString());
        final HistoryObject[] hoA = CidsAppBackend.getInstance().proxy.getHistory(
                11,
                6000467,
                DOMAIN,
                SessionManager.getSession().getUser(),
                2);
        System.out.println(hoA[0].getJsonData());
        System.out.println(hoA[1].getJsonData());
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String getAccountName() {
        final ConnectionSession session = SessionManager.getSession();
        final User user = session.getUser();

        final String userString = user.getName() + "@" + user.getUserGroup().getName();
        return userString;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   stringArray  DOCUMENT ME!
     * @param   delimiter    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static String implode(final Object[] stringArray, final String delimiter) {
        if (stringArray.length == 0) {
            return "";
        } else {
            final StringBuilder sb = new StringBuilder();
            sb.append(stringArray[0]);
            for (int index = 1; index < stringArray.length; index++) {
                sb.append(delimiter);
                sb.append(stringArray[index]);
            }
            return sb.toString();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   kassenzeichenBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean acquireLock(final CidsBean kassenzeichenBean) {
        try {
            if (kassenzeichenBean != null) {
                // Map wird verwendet, damit identische kassenzeichen nicht unnötigerweise
                // doppelt geladen und  gespeichert werden
                final Map<Integer, CidsBean> kassenzeichenNummerToKassenzeichenMap = new HashMap();
                for (final Collection<FlaecheCrossreference> flaechenCrossReferenceList
                            : (Collection<Collection<FlaecheCrossreference>>)flaechenCrossReferences.values()) {
                    for (final FlaecheCrossreference flaechenCrossReference
                                : (Collection<FlaecheCrossreference>)flaechenCrossReferenceList) {
                        final Integer kassenzeichennummer = flaechenCrossReference.getFlaecheToKassenzeichen();
                        if (!kassenzeichenNummerToKassenzeichenMap.containsKey(kassenzeichennummer)) {
                            try {
                                final CidsBean querverweisZielKassenzeichen = CidsAppBackend.getInstance()
                                            .loadKassenzeichenByNummer(kassenzeichennummer);
                                kassenzeichenNummerToKassenzeichenMap.put(
                                    kassenzeichennummer,
                                    querverweisZielKassenzeichen);
                            } catch (final Exception ex) {
                                JOptionPane.showMessageDialog(
                                    Main.getCurrentInstance(),
                                    "Es ist ein Querverweis auf das Kassenzeichen "
                                            + kassenzeichennummer
                                            + " vorhanden, dessen Sperren nicht überprüft werden konnte.",
                                    "Kein Editieren möglich",
                                    JOptionPane.INFORMATION_MESSAGE);
                                log.error("error while loading toLockKassenzeichen:" + kassenzeichennummer, ex);
                                return false;
                            }
                        }
                    }
                }

                // gesperrt werden sollen das eigentliche Kassenzeichen UND alle
                // Kassenzeichen mit einem Querverweis zu diesem.
                final Collection<CidsBean> kassenzeichenListToLock = new ArrayList<CidsBean>();
                kassenzeichenListToLock.add(kassenzeichenBean);
                kassenzeichenListToLock.addAll(kassenzeichenNummerToKassenzeichenMap.values());

                final Collection<Integer> kassenzeichenIds = new ArrayList<Integer>();
                for (final CidsBean kassenzeichenToLock : kassenzeichenListToLock) {
                    kassenzeichenIds.add((Integer)kassenzeichenToLock.getProperty(
                            KassenzeichenPropertyConstants.PROP__ID));
                }

                final String idInString = implode(kassenzeichenIds.toArray(new Integer[0]), ", ");
                final MetaObject[] oldSperren = getMetaObject("SELECT " + getVerdisMetaClass("sperre").getId()
                                + ", id FROM sperre WHERE fk_kassenzeichen IN (" + idInString + ")",
                        DOMAIN);

                if ((oldSperren != null) && (oldSperren.length > 0)) {
                    final CidsBean oldSperreBean = oldSperren[0].getBean();
                    final String benutzerkonto = (String)oldSperreBean.getProperty("benutzerkonto");
                    log.info("Sperre für Kassenzeichen "
                                + kassenzeichenBean.getProperty(KassenzeichenPropertyConstants.PROP__ID)
                                + " bereitsvorhanden von Benutzer " + benutzerkonto);
                    JOptionPane.showMessageDialog(
                        Main.getCurrentInstance(),
                        "Der Datensatz ist schon vom Benutzer "
                                + benutzerkonto
                                + " zum Verändern gesperrt",
                        "Kein Editieren möglich",
                        JOptionPane.INFORMATION_MESSAGE);
                    return false;
                } else {
                    for (final CidsBean oneOfAllKz : kassenzeichenListToLock) {
                        if (oneOfAllKz != null) {
                            final Integer kassenzeichennummer = (Integer)oneOfAllKz.getProperty(
                                    KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER);
                            final CidsBean newSperre = CidsBean.createNewCidsBeanFromTableName(DOMAIN, "sperre");
                            newSperre.setProperty("fk_kassenzeichen", oneOfAllKz);
                            newSperre.setProperty("benutzerkonto", getAccountName());
                            newSperre.setProperty("zeitstempel_timestamp", new Timestamp(new Date().getTime()));
                            sperreBeanMap.put(kassenzeichennummer, newSperre.persist());
                            if (log.isDebugEnabled()) {
                                log.debug("Sperre konnte erfolgreich angelegt werden");
                            }
                        }
                    }
                    return true;
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Kassenzeichen ist null");
                }
                return false;
            }
        } catch (Exception ex) {
            log.error("Fehler beim anlegen der Sperre", ex);
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean releaseLock() {
        if (!sperreBeanMap.isEmpty()) {
            boolean allOK = true;
            for (final CidsBean sperreBean : sperreBeanMap.values()) {
                try {
                    sperreBean.delete();
                    sperreBean.persist();
                    if (log.isDebugEnabled()) {
                        log.debug("Sperre konnte erfolgreich gelöst werden");
                    }
                } catch (Exception ex) {
                    log.error("Fehler beim lösen der Sperre", ex);
                    allOK = false;
                }
            }
            sperreBeanMap.clear();
            return allOK;
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Sperre ist null");
            }
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Mode getMode() {
        return mode;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  mode  DOCUMENT ME!
     */
    public void setMode(final Mode mode) {
        if (this.mode != mode) {
            this.mode = mode;
            fireAppModeChanged();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  mode  DOCUMENT ME!
     * @param  fa    DOCUMENT ME!
     */
    public void setFeatureAttacher(final Mode mode, final FeatureAttacher fa) {
        featureAttacherByMode.put(mode, fa);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  f  DOCUMENT ME!
     */
    public void attachFeatureAccordingToAppMode(final Feature f) {
        final FeatureAttacher fa = featureAttacherByMode.get(mode);
        if (fa != null) {
            fa.requestFeatureAttach(f);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getDomain() {
        return domain;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  domain  DOCUMENT ME!
     */
    public void setDomain(final String domain) {
        this.domain = domain;
    }

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
    public ConnectionSession getSession() {
        return proxy.getSession();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   domainName  DOCUMENT ME!
     * @param   tabName     DOCUMENT ME!
     * @param   query       DOCUMENT ME!
     * @param   fields      DOCUMENT ME!
     * @param   formatter   DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static MetaObject[] getLightweightMetaObjectsForQuery(final String domainName,
            final String tabName,
            final String query,
            final String[] fields,
            AbstractAttributeRepresentationFormater formatter) {
        if (formatter == null) {
            formatter = new AbstractAttributeRepresentationFormater() {

                    @Override
                    public String getRepresentation() {
                        final StringBuffer sb = new StringBuffer();
                        for (final String attribute : fields) {
                            sb.append(getAttribute(attribute.toLowerCase())).append(" ");
                        }
                        return sb.toString().trim();
                    }
                };
        }
        try {
            final User user = SessionManager.getSession().getUser();
            final MetaClass mc = ClassCacheMultiple.getMetaClass(domainName, tabName);
            if (mc != null) {
                return SessionManager.getProxy()
                            .getLightweightMetaObjectsByQuery(mc.getID(), user, query, fields, formatter);
            } else {
                log.error("Can not find MetaClass for Tablename: " + tabName);
            }
        } catch (Exception ex) {
            log.error(ex, ex);
        }
        return new MetaObject[0];
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getLastSplitFlaecheId() {
        return lastSplitFlaecheId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  lastSplitFlaecheId  DOCUMENT ME!
     */
    public void setLastSplitFlaecheId(final int lastSplitFlaecheId) {
        this.lastSplitFlaecheId = lastSplitFlaecheId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Map<CidsBean, Integer> getFlaecheToKassenzeichenQuerverweisMap() {
        return flaecheToKassenzeichenQuerverweisMap;
    }
}
