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

import org.openide.util.Exceptions;

import java.awt.Frame;

import java.sql.Timestamp;

import java.util.*;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.server.search.CidsServerSearch;

import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.gui.MappingComponent;

import de.cismet.tools.collections.HashArrayList;

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;

import de.cismet.verdis.commons.constants.KassenzeichenPropertyConstants;
import de.cismet.verdis.commons.constants.VerdisMetaClassConstants;

import de.cismet.verdis.data.AppPreferences;

import de.cismet.verdis.gui.AlreadyLockedObjectsPanel;
import de.cismet.verdis.gui.KassenzeichenListPanel;
import de.cismet.verdis.gui.LockAlreadyExistsException;
import de.cismet.verdis.gui.LockingDialog;
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

    private AlreadyLockedObjectsPanel lockPanel;

    private Collection<CidsBean> csLocks = new ArrayList();

    private final MultiMap flaechenidToCrossReferences = new MultiMap();
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
    private Map<CidsBean, Integer> frontToKassenzeichenQuerverweisMap = new HashMap<CidsBean, Integer>();

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
                    updateCrossReferences(ksBean);
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
        return loadKassenzeichenByNummer(kassenzeichen, false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   kassenzeichen  DOCUMENT ME!
     * @param   lightweight    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean loadKassenzeichenByNummer(final int kassenzeichen, final boolean lightweight) {
        try {
            final MetaClass mcKassenzeichen = ClassCacheMultiple.getMetaClass(
                    DOMAIN,
                    VerdisMetaClassConstants.MC_KASSENZEICHEN);
            final String query = "SELECT " + mcKassenzeichen.getId() + ", " + KassenzeichenPropertyConstants.PROP__ID
                        + ", " + KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER
                        + " FROM " + VerdisMetaClassConstants.MC_KASSENZEICHEN + " WHERE "
                        + KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER + " = " + kassenzeichen + ";";
            final MetaObject[] mos;
            if (lightweight) {
                mos = proxy.getLightweightMetaObjectsByQuery(
                        mcKassenzeichen.getId(),
                        SessionManager.getSession().getUser(),
                        query,
                        new String[] { KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER },
                        "%d");
            } else {
                mos = proxy.getMetaObjectByQuery(query, 0);
            }

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
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    public void updateCrossReferences(final CidsBean cidsBean) {
        updateFlaechenCrossReferences(cidsBean);
        updateFrontenCrossReferences(cidsBean);
    }

    /**
     * DOCUMENT ME!
     */
    public void clearCrossReferences() {
        frontenCrossReferences.clear();
        flaechenidToCrossReferences.clear();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   kassenzeichenNummer  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConnectionException  DOCUMENT ME!
     */
    private Collection<CrossReference> searchFlaechenCrossReferences(final int kassenzeichenNummer)
            throws ConnectionException {
        final Collection<CrossReference> crossReferences = new ArrayList<CrossReference>();

        final CidsServerSearch search = new FlaechenCrossReferencesServerSearch(kassenzeichenNummer);
        final Collection collection = getProxy().customServerSearch(CidsAppBackend.getInstance().getSession().getUser(),
                search);
        for (final Object row : collection) {
            final Object[] fields = ((Collection)row).toArray();
            final CrossReference crossReference = new CrossReference((Integer)fields[0],
                    (Integer)fields[1],
                    (String)fields[2],
                    (Integer)fields[3],
                    (String)fields[4]);
            crossReferences.add(crossReference);
        }
        return crossReferences;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   kassenzeichenNummer  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConnectionException  DOCUMENT ME!
     */
    private Collection<CrossReference> searchFrontenCrossReferences(final int kassenzeichenNummer)
            throws ConnectionException {
        final Collection<CrossReference> crossReferences = new ArrayList<CrossReference>();

        final CidsServerSearch search = new FrontenCrossReferencesServerSearch(kassenzeichenNummer);
        final Collection collection = getProxy().customServerSearch(CidsAppBackend.getInstance().getSession().getUser(),
                search);
        for (final Object row : collection) {
            final Object[] fields = ((Collection)row).toArray();
            final CrossReference crossReference = new CrossReference((Integer)fields[0],
                    (Integer)fields[1],
                    Integer.toString((Integer)fields[2]),
                    (Integer)fields[3],
                    Integer.toString((Integer)fields[4]));
            crossReferences.add(crossReference);
        }
        return crossReferences;
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
        try {
            final Collection<CrossReference> crossReferences = searchFrontenCrossReferences(kassenzeichenNummer);
            for (final CrossReference crossReference : crossReferences) {
                frontenCrossReferences.put(crossReference.getEntityFromId(), crossReference);
            }
            Main.getCurrentInstance().getWdsrFrontenDetailsPanel().updateCrossReferences();
        } catch (ConnectionException ex) {
            log.error("error during retrieval of object", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    private void updateFlaechenCrossReferences(final CidsBean cidsBean) {
        final int kassenzeichenNummer = (Integer)cidsBean.getProperty(
                KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER);

        flaechenidToCrossReferences.clear();
        try {
            final Collection<CrossReference> crossReferences = searchFlaechenCrossReferences(kassenzeichenNummer);
            for (final CrossReference crossReference : crossReferences) {
                flaechenidToCrossReferences.put(crossReference.getEntityFromId(), crossReference);
            }
            Main.getCurrentInstance().getRegenFlaechenDetailsPanel().updateCrossReferences();
        } catch (ConnectionException ex) {
            log.error("error during retrieval of object", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flaecheId  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<CrossReference> getFlaechenCrossReferencesForFlaecheid(final int flaecheId) {
        return (Collection<CrossReference>)flaechenidToCrossReferences.get(flaecheId);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   frontId  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<CrossReference> getFrontenCrossReferencesForFrontid(final int frontId) {
        return (Collection<CrossReference>)frontenCrossReferences.get(frontId);
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
     * @param  locks  DOCUMENT ME!
     */
    public void showObjectsLockedDialog(final Collection<CidsBean> locks) {
        final JDialog dialog = new JDialog((JFrame)null,
                "Gesperrte Objekte...",
                true);
        if (lockPanel == null) {
            lockPanel = new AlreadyLockedObjectsPanel(locks);
        } else {
            lockPanel.setLocks(locks);
        }
        // dialog.setIconImage(((ImageIcon)BelisIcons.icoError16).getImage());
        dialog.add(lockPanel);
        dialog.setResizable(false);
        dialog.pack();
        StaticSwingTools.showDialog(dialog);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  LockAlreadyExistsException  DOCUMENT ME!
     */
    public boolean acquireLocks() throws LockAlreadyExistsException {
        try {
            if (!csLocks.isEmpty()) {
                log.error("there are still some locks, cant acquire");
                return false;
            }
            final Collection<CidsBean> locks = acquireLock(kassenzeichenBean, true, true);
            if (locks != null) {
                csLocks.addAll(locks);
            }
            return (locks != null);
        } catch (final LockAlreadyExistsException ex) {
            showObjectsLockedDialog(ex.getAlreadyExisingLocks());
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   kassenzeichenBean       DOCUMENT ME!
     * @param   includeCrossReferences  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  LockAlreadyExistsException  DOCUMENT ME!
     */
    public Collection<CidsBean> acquireLock(final CidsBean kassenzeichenBean, final boolean includeCrossReferences)
            throws LockAlreadyExistsException {
        return acquireLock(kassenzeichenBean, includeCrossReferences, false);
    }
    /**
     * DOCUMENT ME!
     *
     * @param   kassenzeichenBean       DOCUMENT ME!
     * @param   includeCrossReferences  DOCUMENT ME!
     * @param   editMode                DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  LockAlreadyExistsException  DOCUMENT ME!
     */
    public Collection<CidsBean> acquireLock(final CidsBean kassenzeichenBean,
            final boolean includeCrossReferences,
            final boolean editMode) throws LockAlreadyExistsException {
        try {
            final Collection<CidsBean> existingLocks = new ArrayList<CidsBean>();
            final List<CidsBean> allKassenzeichenBeansToLock = new HashArrayList<CidsBean>();

            final Map<Integer, CidsBean> crossreferenceFromMap = new HashMap<Integer, CidsBean>();

            if (editMode) {
                SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            LockingDialog.getInstance().reset();
                        }
                    });
            }
            if (kassenzeichenBean != null) {
                if (includeCrossReferences) {
                    for (final CrossReference flaechenCrossReference
                                : searchFlaechenCrossReferences(
                                    (Integer)kassenzeichenBean.getProperty(
                                        KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER))) {
                        final Integer querverweisKassenzeichennummer =
                            flaechenCrossReference.getEntityToKassenzeichen();
                        crossreferenceFromMap.put(querverweisKassenzeichennummer, kassenzeichenBean);
                    }
                    for (final CrossReference frontenCrossReference
                                : searchFrontenCrossReferences(
                                    (Integer)kassenzeichenBean.getProperty(
                                        KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER))) {
                        final Integer querverweisKassenzeichennummer = frontenCrossReference.getEntityToKassenzeichen();
                        crossreferenceFromMap.put(querverweisKassenzeichennummer, kassenzeichenBean);
                    }
                }

                // gesperrt werden sollen das eigentliche Kassenzeichen UND alle
                // Kassenzeichen mit einem Querverweis zu diesem.
                allKassenzeichenBeansToLock.add(kassenzeichenBean);
            }

            final Collection<Integer> allKassenzeichenNummernToLock = new ArrayList<Integer>();
            for (final CidsBean kassenzeichenToLock : allKassenzeichenBeansToLock) {
                allKassenzeichenNummernToLock.add((Integer)kassenzeichenToLock.getProperty(
                        KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER));
            }

            if (editMode) {
                SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            LockingDialog.getInstance().check();
                        }
                    });
            }

            final String nummernInString = implode(allKassenzeichenNummernToLock.toArray(new Integer[0]), ", ");
            final MetaObject[] oldCsLocks = getMetaObject(""
                            + "SELECT " + getVerdisMetaClass("cs_locks").getId() + ", cs_locks.id "
                            + "FROM cs_locks, "
                            + getVerdisMetaClass(VerdisMetaClassConstants.MC_KASSENZEICHEN).getTableName()
                            + " AS kassenzeichen "
                            + "WHERE kassenzeichen.id = cs_locks.object_id "
                            + "AND cs_locks.class_id = "
                            + getVerdisMetaClass(VerdisMetaClassConstants.MC_KASSENZEICHEN).getId()
                            + " "
                            + "AND kassenzeichen." + KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER + " IN ("
                            + nummernInString + ")",
                    DOMAIN);
            for (final MetaObject oldcsLock : oldCsLocks) {
                existingLocks.add(oldcsLock.getBean());
            }

            if (existingLocks.isEmpty()) {
                if (editMode) {
                    SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                LockingDialog.getInstance().setMaxProgress(allKassenzeichenBeansToLock.size());
                            }
                        });
                }

                final Collection<CidsBean> createdLocks = new ArrayList<CidsBean>();
                for (int i = 0; i < allKassenzeichenBeansToLock.size(); i++) {
                    final CidsBean kassenzeichenBeanToLock = allKassenzeichenBeansToLock.get(i);
                    if (kassenzeichenBeanToLock != null) {
                        final CidsBean newCsLock = CidsBean.createNewCidsBeanFromTableName(DOMAIN, "cs_locks");
                        newCsLock.setProperty("object_id", kassenzeichenBeanToLock.getMetaObject().getId());
                        newCsLock.setProperty(
                            "class_id",
                            kassenzeichenBeanToLock.getMetaObject().getMetaClass().getId());
                        newCsLock.setProperty("user_string", getAccountName());
                        newCsLock.setProperty(
                            "additional_info",
                            kassenzeichenBeanToLock
                                    + ";"
                                    + kassenzeichenBean
                                    + ";"
                                    + new Date().toString());
                        createdLocks.add(newCsLock.persist());
                        if (log.isDebugEnabled()) {
                            log.debug("Sperre konnte erfolgreich angelegt werden");
                        }
                    }
                    final int prog = i;
                    if (editMode) {
                        SwingUtilities.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    LockingDialog.getInstance().setProgress(prog);
                                }
                            });
                    }
                }
                return createdLocks;
            } else {
                throw new LockAlreadyExistsException(
                    "A lock for the desired object is already existing",
                    existingLocks);
            }
        } catch (final LockAlreadyExistsException ex) {
            throw ex;
        } catch (final Exception ex) {
            log.error("Fehler beim anlegen der Sperre", ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   csLock  kassenzeichenNummer DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean releaseLock(final CidsBean csLock) {
        if (csLock != null) {
            try {
                csLock.delete();
                csLock.persist();
                if (log.isDebugEnabled()) {
                    log.debug("Sperre konnte erfolgreich gelöst werden");
                }
            } catch (Exception ex) {
                log.error("Fehler beim lösen der Sperre", ex);
                return false;
            }
            return true;
        } else {
            if (log.isDebugEnabled()) {
                log.error("Sperre ist null");
            }
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean releaseLocks() {
        return releaseLocks(csLocks);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   locks  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean releaseLocks(final Collection<CidsBean> locks) {
        if (!locks.isEmpty()) {
            boolean allOK = true;
            for (final CidsBean lock : locks) {
                if (!releaseLock(lock)) {
                    allOK = false;
                }
            }
            locks.clear();
            return allOK;
        } else {
            if (log.isDebugEnabled()) {
                log.error("Keine Sperren vorhanden");
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

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Map<CidsBean, Integer> getFrontToKassenzeichenQuerverweisMap() {
        return frontToKassenzeichenQuerverweisMap;
    }
}
