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
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.newuser.User;

import Sirius.util.collections.MultiMap;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import java.awt.Color;
import java.awt.Frame;

import java.util.*;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.server.actions.ServerActionParameter;
import de.cismet.cids.server.search.CidsServerSearch;

import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.gui.MappingComponent;

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.historybutton.DefaultHistoryModel;
import de.cismet.tools.gui.historybutton.HistoryModelListener;

import de.cismet.verdis.commons.constants.ArbeitspaketPropertyConstants;
import de.cismet.verdis.commons.constants.FlaechePropertyConstants;
import de.cismet.verdis.commons.constants.FrontPropertyConstants;
import de.cismet.verdis.commons.constants.KassenzeichenPropertyConstants;
import de.cismet.verdis.commons.constants.VerdisMetaClassConstants;

import de.cismet.verdis.data.AppPreferences;

import de.cismet.verdis.gui.AlreadyLockedObjectsPanel;
import de.cismet.verdis.gui.GrundbuchblattSucheDialog;
import de.cismet.verdis.gui.LockAlreadyExistsException;
import de.cismet.verdis.gui.Main;
import de.cismet.verdis.gui.RegenFlaechenDetailsPanel;
import de.cismet.verdis.gui.WaitDialog;

import de.cismet.verdis.server.search.FlaechenCrossReferencesServerSearch;
import de.cismet.verdis.server.search.FrontenCrossReferencesServerSearch;

import static de.cismet.verdis.commons.constants.VerdisConstants.DOMAIN;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class CidsAppBackend implements CidsBeanStore, HistoryModelListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            CidsAppBackend.class);
    private static CidsAppBackend INSTANCE = null;

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

    private final List<CidsBean> csLocks = new ArrayList();
    private final DefaultHistoryModel historyModel = new DefaultHistoryModel();
    private final MultiMap flaechenidToCrossReferences = new MultiMap();
    private final MultiMap frontenCrossReferences = new MultiMap();
    private final HashMap<Mode, FeatureAttacher> featureAttacherByMode = new HashMap<Mode, FeatureAttacher>(3);
    private final ArrayList<CidsBeanStore> beanStores = new ArrayList<CidsBeanStore>();
    private final ArrayList<EditModeListener> editModeListeners = new ArrayList<EditModeListener>();
    private final ArrayList<AppModeListener> appModeListeners = new ArrayList<AppModeListener>();
    private final Map<CidsBean, Integer> flaecheToKassenzeichenQuerverweisMap = new HashMap<CidsBean, Integer>();
    private final Map<CidsBean, Integer> frontToKassenzeichenQuerverweisMap = new HashMap<CidsBean, Integer>();
    private String domain = DOMAIN;
    private final ConnectionProxy proxy;
    private final AppPreferences appPreferences;
    private CidsBean kassenzeichenBean = null;
    private boolean editable = false;
    private Frame frameToDisplayDialogs = null;
    private MappingComponent mainMap = null;
    private Mode mode = null;
    private int lastSplitFlaecheId = -1;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CidsAppBackend object.
     *
     * @param  proxy           DOCUMENT ME!
     * @param  appPreferences  DOCUMENT ME!
     */
    private CidsAppBackend(final ConnectionProxy proxy, final AppPreferences appPreferences) {
        this.proxy = proxy;
        this.appPreferences = appPreferences;
        if (!SessionManager.isInitialized()) {
            SessionManager.init(proxy);
            ClassCacheMultiple.setInstance(domain);
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public AppPreferences getAppPreferences() {
        return appPreferences;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   proxy  DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    public static synchronized void init(final ConnectionProxy proxy) {
        if (INSTANCE != null) {
            throw new IllegalStateException("Backend is already inited.");
        } else {
            INSTANCE = new CidsAppBackend(proxy, null);
            INSTANCE.historyModel.addHistoryModelListener(INSTANCE);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   proxy           DOCUMENT ME!
     * @param   appPreferences  DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    public static synchronized void init(final ConnectionProxy proxy, final AppPreferences appPreferences) {
        if (INSTANCE != null) {
            throw new IllegalStateException("Backend is already inited.");
        } else {
            INSTANCE = new CidsAppBackend(proxy, appPreferences);
            INSTANCE.historyModel.addHistoryModelListener(INSTANCE);
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
        if (INSTANCE == null) {
            throw new IllegalStateException("Backend is not inited. Please call init(AppPreferences prefs) first.");
        }
        return INSTANCE;
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
            mos = proxy.getMetaObjectByQuery(user, query, domain);
        } catch (ConnectionException ex) {
            LOG.error("error retrieving metaobject by query", ex);
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
            LOG.error("couldn't persist cidsbean", e);
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
            LOG.error("couldn't load metaclass for " + tablename, exception);
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
     * @param   kassenzeichen  DOCUMENT ME! // * @param lightweight DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  java.lang.Exception
     */
    public CidsBean loadKassenzeichenByNummer(final int kassenzeichen) throws Exception {
        final MetaClass mcKassenzeichen = ClassCacheMultiple.getMetaClass(
                DOMAIN,
                VerdisMetaClassConstants.MC_KASSENZEICHEN);
        final String query = "SELECT " + mcKassenzeichen.getId() + ", " + KassenzeichenPropertyConstants.PROP__ID
                    + ", " + KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER
                    + " FROM " + VerdisMetaClassConstants.MC_KASSENZEICHEN + " WHERE "
                    + KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER + " = " + kassenzeichen + ";";
        final MetaObject[] mos;
        mos = proxy.getMetaObjectByQuery(query, 0);

        if ((mos == null) || (mos.length < 1)) {
            return null;
        } else {
            final MetaObject mo = mos[0];
            return mo.getBean();
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
            LOG.error("error during retrieval of object", ex);
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
        final Collection collection = getProxy().customServerSearch(getSession().getUser(),
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
     * @param   serverSearch  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConnectionException  DOCUMENT ME!
     */
    public Collection<Object> executeCustomServerSearch(final CidsServerSearch serverSearch)
            throws ConnectionException {
        final Collection collection = getProxy().customServerSearch(getSession().getUser(), serverSearch);
        return collection;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   taskname  DOCUMENT ME!
     * @param   body      DOCUMENT ME!
     * @param   params    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConnectionException  DOCUMENT ME!
     */
    public Object executeServerAction(final String taskname, final Object body, final ServerActionParameter... params)
            throws ConnectionException {
        final Object result = getProxy().executeTask(taskname, domain, body, params);
        return result;
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
        final Collection collection = getProxy().customServerSearch(getSession().getUser(),
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
        frontenCrossReferences.clear();
        if (cidsBean != null) {
            final int kassenzeichenNummer = (Integer)cidsBean.getProperty(
                    KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER);
            try {
                final Collection<CrossReference> crossReferences = searchFrontenCrossReferences(kassenzeichenNummer);
                for (final CrossReference crossReference : crossReferences) {
                    frontenCrossReferences.put(crossReference.getEntityFromId(), crossReference);
                }
                // TODO inform via listener
                Main.getInstance().getWdsrFrontenDetailsPanel().updateCrossReferences();
            } catch (ConnectionException ex) {
                LOG.error("error during retrieval of object", ex);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    private void updateFlaechenCrossReferences(final CidsBean cidsBean) {
        flaechenidToCrossReferences.clear();
        if (cidsBean != null) {
            final int kassenzeichenNummer = (Integer)cidsBean.getProperty(
                    KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER);
            try {
                final Collection<CrossReference> crossReferences = searchFlaechenCrossReferences(kassenzeichenNummer);
                for (final CrossReference crossReference : crossReferences) {
                    flaechenidToCrossReferences.put(crossReference.getEntityFromId(), crossReference);
                }
                // TODO inform via listener
                RegenFlaechenDetailsPanel.getInstance().updateCrossReferences();
            } catch (ConnectionException ex) {
                LOG.error("error during retrieval of object", ex);
            }
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
    public void removeAppModeListener(final AppModeListener appModeListener) {
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
            LOG.error("error in retrieving the history og " + kassenzeichenId, ex);
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
            return proxy.getMetaObject(SessionManager.getSession().getUser(), objectId, classtId, DOMAIN);
        } catch (ConnectionException ex) {
            LOG.error("error in retrieving the metaobject " + objectId + " of classid " + classtId, ex);
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
    public MetaObject[] getVerdisMetaObject(final String query) {
        try {
            return proxy.getMetaObjectByQuery(SessionManager.getSession().getUser(), query, DOMAIN);
        } catch (ConnectionException ex) {
            LOG.error("error in retrieving the metaobject: " + query, ex);
            return null;
        }
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
    public static String implode(final Object[] stringArray, final String delimiter) {
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
        dialog.add(new AlreadyLockedObjectsPanel(locks));
        dialog.setResizable(false);
        dialog.pack();
        StaticSwingTools.showDialog(dialog);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  LockAlreadyExistsException  DOCUMENT ME!
     * @throws  Exception                   DOCUMENT ME!
     */
    public void acquireLocks() throws LockAlreadyExistsException, Exception {
        csLocks.clear();
        final Collection<CidsBean> locks = acquireLock(kassenzeichenBean, true, true);
        if (locks != null) {
            csLocks.addAll(locks);
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
     * @throws  Exception                   DOCUMENT ME!
     */
    public Collection<CidsBean> acquireLock(final CidsBean kassenzeichenBean, final boolean includeCrossReferences)
            throws LockAlreadyExistsException, Exception {
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
     * @throws  Exception                   DOCUMENT ME!
     */
    public Collection<CidsBean> acquireLock(final CidsBean kassenzeichenBean,
            final boolean includeCrossReferences,
            final boolean editMode) throws LockAlreadyExistsException, Exception {
        try {
            final Collection<CidsBean> existingLocks = new ArrayList<CidsBean>();
            final List<Integer> allKassenzeichenNummernToLock = new ArrayList<Integer>();

            final Map<Integer, CidsBean> crossreferenceFromMap = new HashMap<Integer, CidsBean>();

            if (editMode) {
                WaitDialog.getInstance().startCheckCrosslinks();
            }
            if (kassenzeichenBean != null) {
                // gesperrt werden sollen das eigentliche Kassenzeichen UND alle
                // Kassenzeichen mit einem Querverweis zu diesem.
                allKassenzeichenNummernToLock.add((Integer)kassenzeichenBean.getProperty(
                        KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER));
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
                    allKassenzeichenNummernToLock.addAll(crossreferenceFromMap.keySet());
                }
            }

            if (editMode) {
                WaitDialog.getInstance().startCheckLocks();
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
                    WaitDialog.getInstance().startLockOrRelease(true, allKassenzeichenNummernToLock.size());
                }

                final Collection<CidsBean> createdLocks = new ArrayList<CidsBean>();
                for (int index = 0; index < allKassenzeichenNummernToLock.size(); index++) {
                    final Integer kassenzeichenNummernToLock = allKassenzeichenNummernToLock.get(index);
                    final CidsBean kassenzeichenBeanToLock = loadKassenzeichenByNummer(kassenzeichenNummernToLock);
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
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Sperre konnte erfolgreich angelegt werden");
                        }
                    }
                    if (editMode) {
                        WaitDialog.getInstance().progressLockOrRelease(index);
                    }
                }
                WaitDialog.getInstance().progressLockOrRelease(allKassenzeichenNummernToLock.size());

                return createdLocks;
            } else {
                throw new LockAlreadyExistsException(
                    "A lock for the desired object is already existing",
                    existingLocks);
            }
        } catch (final LockAlreadyExistsException ex) {
            LOG.error("Sperre bereits vorhanden", ex);
            throw ex;
        } catch (final Exception ex) {
            LOG.error("Fehler beim anlegen der Sperre", ex);
            throw ex;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConnectionException  DOCUMENT ME!
     */
    public List<CidsBean> getArbeitspakete() throws ConnectionException {
        final List<CidsBean> allArbeitspakete = new ArrayList<CidsBean>();
        final MetaClass mcArbeitspaket = ClassCacheMultiple.getMetaClass(
                DOMAIN,
                VerdisMetaClassConstants.MC_ARBEITSPAKET);

        final String query = "SELECT " + mcArbeitspaket.getId() + ", " + ArbeitspaketPropertyConstants.PROP__ID
                    + " FROM " + mcArbeitspaket.getTableName() + " ORDER BY ID ASC;";

        final MetaObject[] mos = proxy.getMetaObjectByQuery(SessionManager.getSession().getUser(), query, DOMAIN);

        for (final MetaObject mo : mos) {
            allArbeitspakete.add(mo.getBean());
        }

        return allArbeitspakete;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   csLock  kassenzeichenNummer DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public void releaseLock(final CidsBean csLock) throws Exception {
        if (csLock != null) {
            csLock.delete();
            csLock.persist();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Sperre konnte erfolgreich gelöst werden");
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public void releaseLocks() throws Exception {
        releaseLocks(csLocks);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   locks  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public void releaseLocks(final List<CidsBean> locks) throws Exception {
        if (!locks.isEmpty()) {
            WaitDialog.getInstance().startLockOrRelease(false, locks.size());
            for (int index = 0; index < locks.size(); index++) {
                WaitDialog.getInstance().progressLockOrRelease(index);
                final CidsBean lock = locks.get(index);
                releaseLock(lock);
            }
            WaitDialog.getInstance().progressLockOrRelease(locks.size());
            locks.clear();
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.error("Keine Sperren vorhanden");
            }
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
                LOG.error("Can not find MetaClass for Tablename: " + tabName);
            }
        } catch (Exception ex) {
            LOG.error(ex, ex);
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

    /**
     * DOCUMENT ME!
     *
     * @param  kassenzeichen  DOCUMENT ME!
     */
    public void gotoKassenzeichen(final String kassenzeichen) {
        gotoKassenzeichen(kassenzeichen, false, true);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kassenzeichen  DOCUMENT ME!
     */
    public void gotoKassenzeichenAndEdit(final String kassenzeichen) {
        gotoKassenzeichen(kassenzeichen, true, false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kassenzeichen  DOCUMENT ME!
     */
    public void gotoKassenzeichenWithoutHistory(final String kassenzeichen) {
        gotoKassenzeichen(kassenzeichen, false, false);
    }

    /**
     * former synchronized method.
     *
     * @param  kassenzeichen   DOCUMENT ME!
     * @param  edit            DOCUMENT ME!
     * @param  historyEnabled  DOCUMENT ME!
     */
    private void gotoKassenzeichen(final String kassenzeichen, final boolean edit, final boolean historyEnabled) {
        final String[] test = kassenzeichen.split(":");

        final String kassenzeichenNummer;
        final String flaechenBez;
        if (test.length > 1) {
            kassenzeichenNummer = test[0];
            flaechenBez = test[1];
        } else {
            kassenzeichenNummer = kassenzeichen;
            flaechenBez = "";
        }

        if (!Main.getInstance().isInEditMode()) {
            Main.getInstance().disableKassenzeichenCmds();
            Main.getInstance().getKassenzeichenPanel().setSearchStarted();
            GrundbuchblattSucheDialog.getInstance().setEnabled(false);
            Main.getInstance().getKassenzeichenPanel().setSearchField(kassenzeichen);

            new SwingWorker<CidsBean, Void>() {

                    @Override
                    protected CidsBean doInBackground() throws Exception {
                        final CidsBean cidsBean = loadKassenzeichenByNummer(Integer.parseInt(kassenzeichenNummer));
                        updateCrossReferences(cidsBean);
                        return cidsBean;
                    }

                    @Override
                    protected void done() {
                        try {
                            final CidsBean cidsBean = get();

                            if (cidsBean != null) {
                                setCidsBean(cidsBean);
                                selectCidsBeanByIdentifier(flaechenBez);
                                Main.getInstance().getKassenzeichenPanel().flashSearchField(Color.GREEN);
                                if (historyEnabled) {
                                    historyModel.addToHistory(kassenzeichen);
                                }
                            } else {
                                Main.getInstance().getKassenzeichenPanel().flashSearchField(Color.RED);
                            }
                        } catch (final Exception ex) {
                            LOG.error("Exception in Background Thread", ex);
                            Main.getInstance().getKassenzeichenPanel().flashSearchField(Color.RED);
                            showError("Fehler beim Laden", "Kassenzeichen konnte nicht geladen werden", ex);
                        }
                        Main.getInstance().getKassenzeichenPanel().setSearchFinished();
                        GrundbuchblattSucheDialog.getInstance().setEnabled(true);
                        Main.getInstance().refreshKassenzeichenButtons();
                        if (edit) {
                            WaitDialog.getInstance().showDialog();
                            new SwingWorker<Boolean, Void>() {

                                    @Override
                                    protected Boolean doInBackground() throws Exception {
                                        if (Main.getInstance().acquireLocks()) { // try to acquire
                                            return true;
                                        }
                                        return null;
                                    }

                                    @Override
                                    protected void done() {
                                        try {
                                            final Boolean enableEditing = get();
                                            if (enableEditing != null) {
                                                Main.getInstance().setEditMode(enableEditing);
                                            }
                                        } catch (final Exception ex) {
                                            LOG.error(ex, ex);
                                        } finally {
                                            WaitDialog.getInstance().dispose();
                                        }
                                    }
                                }.execute();
                        }
                    }
                }.execute();
        } else {
            JOptionPane.showMessageDialog(
                Main.getInstance(),
                "Das Kassenzeichen kann nur gewechselt werden wenn alle \u00C4nderungen gespeichert oder verworfen worden sind.",
                "Wechseln nicht m\u00F6glich",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  identifier  DOCUMENT ME!
     */
    private void selectCidsBeanByIdentifier(final String identifier) {
        if (mode.equals(CidsAppBackend.Mode.REGEN)) {
            selectFlaecheByBezeichner(identifier);
        } else if (mode.equals(CidsAppBackend.Mode.ESW)) {
            selectFrontByNummer(identifier);
        } else if (mode.equals(CidsAppBackend.Mode.ALLGEMEIN)) {
            // do nothing
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  bez  DOCUMENT ME!
     */
    private void selectFlaecheByBezeichner(final String bez) {
        for (final CidsBean flaeche
                    : (Collection<CidsBean>)kassenzeichenBean.getProperty(KassenzeichenPropertyConstants.PROP__FLAECHEN)) {
            if (((String)flaeche.getProperty(FlaechePropertyConstants.PROP__FLAECHENBEZEICHNUNG)).equals(bez)) {
                Main.getInstance().getRegenFlaechenTabellenPanel().selectCidsBean(flaeche);
                return;
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  nummer  DOCUMENT ME!
     */
    private void selectFrontByNummer(final String nummer) {
        final int nummerAsInt;
        try {
            nummerAsInt = Integer.parseInt(nummer);
        } catch (NumberFormatException e) {
            // the Nummer is an invalid identifier for a Front, so do nothing
            return;
        }

        for (final CidsBean front
                    : (Collection<CidsBean>)kassenzeichenBean.getProperty(KassenzeichenPropertyConstants.PROP__FRONTEN)) {
            if (((Integer)front.getProperty(FrontPropertyConstants.PROP__NUMMER)).equals(nummerAsInt)) {
                Main.getInstance().getWdsrFrontenTabellenPanel().selectCidsBean(front);
                return;
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  title      DOCUMENT ME!
     * @param  message    DOCUMENT ME!
     * @param  exception  DOCUMENT ME!
     */
    public void showError(final String title, final String message, final Exception exception) {
        if (SwingUtilities.isEventDispatchThread()) {
            final ErrorInfo errorInfo = new ErrorInfo(title, message, null, "", exception, null, null);
            JXErrorPane.showDialog(Main.getInstance(), errorInfo);
        } else {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {

                        @Override
                        public void run() {
                            showError(title, message, exception);
                        }
                    });
            } catch (final Exception ex) {
                LOG.error(ex, ex);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public DefaultHistoryModel getHistoryModel() {
        return historyModel;
    }

    @Override
    public void historyChanged() {
        if ((historyModel != null) && (historyModel.getCurrentElement() != null)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("historyChanged:" + historyModel.getCurrentElement().toString());
            }
            if (historyModel.getCurrentElement() != null) {
                final String kassenzeichenText = historyModel.getCurrentElement().toString();
                gotoKassenzeichenWithoutHistory(kassenzeichenText);
            }
        }
    }

    @Override
    public void forwardStatusChanged() {
    }

    @Override
    public void backStatusChanged() {
    }

    @Override
    public void historyActionPerformed() {
    }
}
