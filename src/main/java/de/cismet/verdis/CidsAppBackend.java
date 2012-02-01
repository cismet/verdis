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
import Sirius.server.middleware.types.HistoryObject;
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.search.CidsServerSearch;
import Sirius.util.collections.MultiMap;
import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;
import de.cismet.verdis.constants.KassenzeichenPropertyConstants;
import de.cismet.verdis.data.AppPreferences;
import java.awt.Frame;
import java.util.*;
import javax.swing.SwingWorker;


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
    private static final int KASSENZEICHEN_CLASS_ID = 11;
    public static final String DOMAIN = "VERDIS_GRUNDIS";
    private static CidsAppBackend instance = null;
    private static AppPreferences appprefs;

    //~ Instance fields --------------------------------------------------------
    private final MultiMap crossReferences = new MultiMap();
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

    //~ Constructors -----------------------------------------------------------
    /**
     * Creates a new CidsAppBackend object.
     */
    private CidsAppBackend(ConnectionProxy proxy) {
        try {
            this.proxy = proxy;
            if (!SessionManager.isInitialized()) {
                SessionManager.init(proxy);
            }

        } catch (Throwable e) {
            log.fatal("no connection to the cids server possible. too bad.", e);
        }
    }

    public synchronized static void init(ConnectionProxy proxy) {
//       if (instance!=null){
//           throw new IllegalStateException("Backend is already inited. Please call init(AppPreferences prefs) only once.");
//       }else {
        instance = new CidsAppBackend(proxy);
//       }

    }

    public static void devInit() {
        try {
            Log4JQuickConfig.configure4LumbermillOnLocalhost();
            final String callServerURL = "http://localhost:9986/callserver/binary";
            final String connectionClass = "Sirius.navigator.connection.RESTfulConnection";
            final Connection connection = ConnectionFactory.getFactory().createConnection(connectionClass, callServerURL);

            final ConnectionInfo connectionInfo = new ConnectionInfo();
            connectionInfo.setCallserverURL(callServerURL);
            connectionInfo.setPassword("sb");
            connectionInfo.setUserDomain(DOMAIN);
            connectionInfo.setUsergroup("VORN");
            connectionInfo.setUsergroupDomain(DOMAIN);
            connectionInfo.setUsername("SteinbacherD102");

            ConnectionSession session = ConnectionFactory.getFactory().createSession(connection, connectionInfo, true);

            System.out.println("CidsAppBackend: session created");
            ConnectionProxy proxy = ConnectionFactory.getFactory().createProxy("Sirius.navigator.connection.proxy.DefaultConnectionProxyHandler", session);
            init(proxy);


        } catch (Throwable e) {
            log.fatal("no connection to the cids server possible. too bad.", e);
        }
    }

    //~ Methods ----------------------------------------------------------------
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public synchronized static CidsAppBackend getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Backend is not inited. Please call init(AppPreferences prefs) first.");
        }
        return instance;
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
            final int nummer = (Integer) kassenzeichenBean.getProperty(KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER);
            retrieveKassenzeichenByNummer(nummer);
        } else {
            log.warn("kassenzeichen = null . cannot be refreshed");
        }
    }

    public MetaClass getVerdisMetaClass(String tablename) {
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
     * @param   kassenzeichen  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public void retrieveKassenzeichenByNummer(final int kassenzeichen) {
        new SwingWorker<Boolean, Void>() {

            @Override
            protected Boolean doInBackground() throws Exception {
                CidsBean ksBean = loadKassenzeichenByNummer(kassenzeichen);
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

    public CidsBean loadKassenzeichenByNummer(final int kassenzeichen) {
        String query = "SELECT " + KASSENZEICHEN_CLASS_ID + ", id FROM kassenzeichen WHERE " + KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER + " = " + kassenzeichen + ";";

        try {
            final MetaObject[] mos = proxy.getMetaObjectByQuery(query, 0);
            if (mos == null || mos.length < 1) {
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

    public List<CidsBean> loadFortfuehrungBeansByDates(final Date fromDate, final Date toDate) {
        final String query = "SELECT "
                + "   31, "
                + "   id FROM fortfuehrung "
                + "WHERE "
                + "   fortfuehrung.abgearbeitet IS FALSE AND "
                + "   fortfuehrung.datum BETWEEN '" + fromDate + "' AND '" + toDate + "';";        

        try {
            final MetaObject[] mos = proxy.getMetaObjectByQuery(query, 0);
            if (mos == null || mos.length < 1) {
                return null;
            } else {
                final List<CidsBean> cbs = new ArrayList<CidsBean>();
                for (final MetaObject mo : mos) {
                    cbs.add(mo.getBean());
                }
                return cbs;
            }
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

    private void updateCrossReferences(final CidsBean cidsBean) {
        final int kassenzeichenNummer = (Integer) cidsBean.getProperty(KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER);
        final CidsServerSearch search = new FlaechenCrossReferencesServerSearch(kassenzeichenNummer);
        try {
            crossReferences.clear();
            Collection collection = getProxy().customServerSearch(CidsAppBackend.getInstance().getSession().getUser(), search);
            for (Object row : collection) {
                Object[] r = ((Collection)row).toArray();
//                  synchronized (kassenzeichenChangedBlocker) {
                crossReferences.put(r[1], r[3] + ":" + r[4]);
//                  }
            }
        } catch (ConnectionException ex) {
            log.error("error during retrieval of object", ex);
        }
    }

    public Collection<String> getCrossReferencesFor(int kassenzeichenNummer) {
        return (Collection<String>) crossReferences.get(kassenzeichenNummer);
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
     * @param   howMuch        DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public HistoryObject[] getHistory(final int kassenzeichenId, final int howMuch) {
        try {
            return proxy.getHistory(KASSENZEICHEN_CLASS_ID, kassenzeichenId, DOMAIN, SessionManager.getSession().getUser(), howMuch);
        } catch (ConnectionException ex) {
            log.error("error in retrieving the history og " + kassenzeichenId, ex);
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
        //final MetaObject mo = CidsAppBackend.getInstance().proxy.getMetaObject(6000467, 11, DOMAIN);
//            MetaObject mo = proxy.getMetaObject(6021737, 11, DOMAIN);
        System.out.println("dauer:" + (System.currentTimeMillis() - l));
        System.out.println("retrieved 6000467");
        // System.out.println(mo.getBean().toJSONString());
        final HistoryObject[] hoA = CidsAppBackend.getInstance().proxy.getHistory(
                KASSENZEICHEN_CLASS_ID,
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
     * @param   object_id  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean lockDataset(final String object_id) {
//        lockNonce = "VERDIS:" + System.currentTimeMillis();
        return true;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  object_id  DOCUMENT ME!
     */
    public void unlockDataset(final String object_id) {
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

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public ConnectionProxy getProxy() {
        return proxy;
    }

    public ConnectionSession getSession() {
        return proxy.getSession();
    }


}
