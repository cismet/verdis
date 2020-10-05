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
package de.cismet.verdis.gui.aenderungsanfrage;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.MetaObjectNode;

import org.geojson.Feature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.swing.SwingWorker;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.cids.servermessage.CidsServerMessageNotifier;
import de.cismet.cids.servermessage.CidsServerMessageNotifierListener;
import de.cismet.cids.servermessage.CidsServerMessageNotifierListenerEvent;

import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.verdis.CidsAppBackend;

import de.cismet.verdis.commons.constants.VerdisConstants;

import de.cismet.verdis.gui.Main;

import de.cismet.verdis.server.action.KassenzeichenChangeRequestServerAction;
import de.cismet.verdis.server.json.AenderungsanfrageJson;
import de.cismet.verdis.server.json.AenderungsanfrageResultJson;
import de.cismet.verdis.server.json.FlaecheAenderungJson;
import de.cismet.verdis.server.json.FlaecheAnschlussgradJson;
import de.cismet.verdis.server.json.FlaecheFlaechenartJson;
import de.cismet.verdis.server.json.FlaechePruefungJson;
import de.cismet.verdis.server.json.NachrichtJson;
import de.cismet.verdis.server.json.PruefungAnschlussgradJson;
import de.cismet.verdis.server.json.PruefungFlaechenartJson;
import de.cismet.verdis.server.json.PruefungGroesseJson;
import de.cismet.verdis.server.search.AenderungsanfrageSearchStatement;
import de.cismet.verdis.server.utils.AenderungsanfrageUtils;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class AenderungsanfrageHandler {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            AenderungsanfrageHandler.class);

    //~ Instance fields --------------------------------------------------------

    private Integer stacId = null;
    private CidsBean cidsBean = null;
    private AenderungsanfrageJson aenderungsanfrage;
    private ChangeListenerHandler changeListenerHandler = new ChangeListenerHandler();
    private final List<CidsBean> aenderungsanfrageBeans = new ArrayList<>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AenderungsanfrageHandler object.
     */
    private AenderungsanfrageHandler() {
        try {
            if (SessionManager.getConnection().hasConfigAttr(
                            SessionManager.getSession().getUser(),
                            "csm://"
                            + KassenzeichenChangeRequestServerAction.CSM_NEWREQUEST)) {
                CidsServerMessageNotifier.getInstance()
                        .subscribe(new CidsServerMessageNotifierListener() {

                                @Override
                                public void messageRetrieved(final CidsServerMessageNotifierListenerEvent event) {
                                    try {
                                        final KassenzeichenChangeRequestServerAction.ServerMessage serverMessage =
                                            (KassenzeichenChangeRequestServerAction.ServerMessage)event
                                            .getMessage().getContent();

                                        if ((serverMessage.getStacId() != null)
                                            && serverMessage.getStacId().equals(getStacId())) {
                                            if (serverMessage.getAenderungsanfrage() != null) {
                                                setAenderungsanfrage(
                                                    AenderungsanfrageUtils.getInstance().createAenderungsanfrageJson(
                                                        serverMessage.getAenderungsanfrage()));
                                            }
                                        }
                                        reload();
                                    } catch (final Exception ex) {
                                        LOG.warn(ex, ex);
                                    }
                                }
                            },
                            KassenzeichenChangeRequestServerAction.CSM_NEWREQUEST);
            }
        } catch (final ConnectionException ex) {
            LOG.warn("Konnte Rechte an csm://" + KassenzeichenChangeRequestServerAction.CSM_NEWREQUEST
                        + " nicht abfragen.",
                ex);
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private ChangeListenerHandler getChangeListenerHandler() {
        return changeListenerHandler;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static AenderungsanfrageHandler getInstance() {
        return LazyInitialiser.INSTANCE;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  aenderungsanfrage  DOCUMENT ME!
     */
    private void setAenderungsanfrage(final AenderungsanfrageJson aenderungsanfrage) {
        this.aenderungsanfrage = aenderungsanfrage;
        changeListenerHandler.aenderungsanfrageChanged(aenderungsanfrage);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   aenderungsanfrage  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public AenderungsanfrageResultJson sendAenderungsanfrage(final AenderungsanfrageJson aenderungsanfrage)
            throws Exception {
        final ServerActionParameter<String> paramAenderungsanfrage = new ServerActionParameter<>(
                KassenzeichenChangeRequestServerAction.Parameter.CHANGEREQUEST_JSON.toString(),
                aenderungsanfrage.toJson());
        final ServerActionParameter<Integer> paramStacId = new ServerActionParameter<>(
                KassenzeichenChangeRequestServerAction.Parameter.STAC_ID.toString(),
                getStacId());

        final Object result = SessionManager.getConnection()
                    .executeTask(SessionManager.getSession().getUser(),
                        KassenzeichenChangeRequestServerAction.TASKNAME,
                        VerdisConstants.DOMAIN,
                        null,
                        ConnectionContext.createDeprecated(),
                        paramStacId,
                        paramAenderungsanfrage);
        if (result instanceof String) {
            final AenderungsanfrageResultJson resultJson = AenderungsanfrageUtils.getInstance()
                        .createAenderungsanfrageResultJson((String)result);
            if (AenderungsanfrageResultJson.ResultStatus.SUCCESS.equals(resultJson.getResultStatus())) {
                setAenderungsanfrage(resultJson.getAenderungsanfrage());
                return resultJson;
            } else {
                throw new Exception(resultJson.getErrorMessage());
            }
        } else if (result instanceof Exception) {
            throw (Exception)result;
        } else {
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public void reload() throws Exception {
        loadByStacId(getStacId());
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<CidsBean> getAenderungsanfrageBeans() {
        return aenderungsanfrageBeans;
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public void reloadBeans() throws Exception {
        final List<CidsBean> aenderungsanfrageBeans = getAenderungsanfrageBeans();
        aenderungsanfrageBeans.clear();
        aenderungsanfrageBeans.addAll(searchAll());
        getChangeListenerHandler().aenderungsanfrageBeansChanged(aenderungsanfrageBeans);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   kassenzeichenNummer  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public void loadByKassenzeichennummer(final Integer kassenzeichenNummer) throws Exception {
        if (kassenzeichenNummer != null) {
            final AenderungsanfrageSearchStatement search = new AenderungsanfrageSearchStatement();
            search.setKassenzeichennummer(kassenzeichenNummer);
            updateAenderungsanfrageBean(search);
        } else {
            updateAenderungsanfrageBean(null);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   stacId  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public void loadByStacId(final Integer stacId) throws Exception {
        if (stacId != null) {
            final AenderungsanfrageSearchStatement search = new AenderungsanfrageSearchStatement();
            search.setStacId(stacId);
            updateAenderungsanfrageBean(search);
        } else {
            updateAenderungsanfrageBean(null);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   search  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private void updateAenderungsanfrageBean(final AenderungsanfrageSearchStatement search) throws Exception {
        setCidsBean(null);
        setStacId(null);
        new SwingWorker<CidsBean, Void>() {

                @Override
                protected CidsBean doInBackground() throws Exception {
                    if ((search != null)
                                && CidsAppBackend.getInstance().getAppPreferences().isAenderungsanfrageEnabled()) {
                        final Collection<MetaObjectNode> mons = (Collection)CidsAppBackend.getInstance()
                                    .executeCustomServerSearch(search);
                        if ((mons != null) && !mons.isEmpty()) {
                            final MetaObjectNode mon = mons.iterator().next();
                            final MetaObject mo = CidsAppBackend.getInstance()
                                        .getVerdisMetaObject(mon.getObjectId(), mon.getClassId());
                            if (mo != null) {
                                return mo.getBean();
                            }
                        }
                    }
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        final CidsBean cidsBean = get();
                        setCidsBean(cidsBean);
                        setStacId((cidsBean != null) ? (Integer)cidsBean.getProperty("stac_id") : null);

                        setAenderungsanfrage((getCidsBean() != null)
                                ? AenderungsanfrageUtils.getInstance().createAenderungsanfrageJson(
                                    (String)getCidsBean().getProperty(
                                        VerdisConstants.PROP.AENDERUNGSANFRAGE.CHANGES_JSON)) : null);
                    } catch (final Exception ex) {
                        LOG.error(ex, ex);
                        setCidsBean(null);
                        setStacId(null);
                        setAenderungsanfrage(null);
                    }
                    Main.getInstance()
                            .getKartenPanel()
                            .refreshInMap(!Main.getInstance().isInEditMode() && !Main.getInstance().isFixMapExtent()
                                && !Main.getInstance().isFixMapExtentMode());
                }
            }.execute();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  aenderungsanfrage  DOCUMENT ME!
     */
    public void doUndraft(final AenderungsanfrageJson aenderungsanfrage) {
        if (aenderungsanfrage != null) {
            for (final NachrichtJson nachricht : aenderungsanfrage.getNachrichten()) {
                if (NachrichtJson.Typ.CLERK.equals(nachricht.getTyp())) {
                    nachricht.setDraft(null);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   aenderungsanfrage  DOCUMENT ME!
     * @param   kassenzeichenBean  DOCUMENT ME!
     * @param   timestamp          DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public AenderungsanfrageJson doPruefung(final AenderungsanfrageJson aenderungsanfrage,
            final CidsBean kassenzeichenBean,
            final Date timestamp) throws Exception {
        if (aenderungsanfrage == null) {
            return null;
        }
        if (kassenzeichenBean != null) {
            // preparing flaecheBeanMap
            final Map<String, CidsBean> flaechenBeans = new HashMap<>();
            for (final CidsBean flaecheBean
                        : kassenzeichenBean.getBeanCollectionProperty(VerdisConstants.PROP.KASSENZEICHEN.FLAECHEN)) {
                final String bezeichnung = (String)flaecheBean.getProperty(
                        VerdisConstants.PROP.FLAECHE.FLAECHENBEZEICHNUNG);
                flaechenBeans.put(bezeichnung, flaecheBean);
            }

            final Set<String> bezeichnungen = new HashSet<>(aenderungsanfrage.getFlaechen().keySet());
            for (final String bezeichnung : bezeichnungen) {
                final FlaecheAenderungJson flaecheJson = aenderungsanfrage.getFlaechen().get(bezeichnung);
                final CidsBean flaecheBean = flaechenBeans.get(bezeichnung);
                // TODO message => flaechenänderung entfernt
                if (flaecheBean == null) {
                    aenderungsanfrage.getFlaechen().remove(bezeichnung);
                    continue;
                }

                final Integer groesse = (Integer)flaecheBean.getProperty(
                        VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                + "."
                                + VerdisConstants.PROP.FLAECHENINFO.GROESSE_KORREKTUR);
                final FlaecheFlaechenartJson flaechenart =
                    (flaecheBean.getProperty(
                            VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                    + "."
                                    + VerdisConstants.PROP.FLAECHENINFO.FLAECHENART) != null)
                    ? new FlaecheFlaechenartJson((String)flaecheBean.getProperty(
                            VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                    + "."
                                    + VerdisConstants.PROP.FLAECHENINFO.FLAECHENART
                                    + "."
                                    + VerdisConstants.PROP.FLAECHENART.ART),
                        (String)flaecheBean.getProperty(
                            VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                    + "."
                                    + VerdisConstants.PROP.FLAECHENINFO.FLAECHENART
                                    + "."
                                    + VerdisConstants.PROP.FLAECHENART.ART_ABKUERZUNG)) : null;
                final FlaecheAnschlussgradJson anschlussgrad =
                    (flaecheBean.getProperty(
                            VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                    + "."
                                    + VerdisConstants.PROP.FLAECHENINFO.ANSCHLUSSGRAD) != null)
                    ? new FlaecheAnschlussgradJson((String)flaecheBean.getProperty(
                            VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                    + "."
                                    + VerdisConstants.PROP.FLAECHENINFO.ANSCHLUSSGRAD
                                    + "."
                                    + VerdisConstants.PROP.ANSCHLUSSGRAD.GRAD),
                        (String)flaecheBean.getProperty(
                            VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                    + "."
                                    + VerdisConstants.PROP.FLAECHENINFO.ANSCHLUSSGRAD
                                    + "."
                                    + VerdisConstants.PROP.ANSCHLUSSGRAD.GRAD_ABKUERZUNG)) : null;

                if (flaecheJson.getPruefung() != null) {
                    final PruefungGroesseJson pruefungGroesse =
                        ((flaecheJson.getPruefung().getGroesse() != null)
                                    && !Boolean.TRUE.equals(flaecheJson.getPruefung().getGroesse().getPending()))
                        ? flaecheJson.getPruefung().getGroesse() : null;
                    final PruefungFlaechenartJson pruefungFlaechenart =
                        ((flaecheJson.getPruefung().getFlaechenart() != null)
                                    && !Boolean.TRUE.equals(flaecheJson.getPruefung().getFlaechenart().getPending()))
                        ? flaecheJson.getPruefung().getFlaechenart() : null;
                    final PruefungAnschlussgradJson pruefungAnschlussgrad =
                        ((flaecheJson.getPruefung().getAnschlussgrad() != null)
                                    && !Boolean.TRUE.equals(flaecheJson.getPruefung().getAnschlussgrad().getPending()))
                        ? flaecheJson.getPruefung().getAnschlussgrad() : null;
                    PruefungGroesseJson newPruefungGroesse = null;
                    PruefungFlaechenartJson newPruefungFlaechenart = null;
                    PruefungAnschlussgradJson newPruefungAnschlussgrad = null;

                    // identifying groesse pruefung
                    if (pruefungGroesse == null) {
                        // wird zufällig "angenommen" weil gleich mit anfrage
                        if (Objects.equals(groesse, flaecheJson.getGroesse())) {
                            newPruefungGroesse = new PruefungGroesseJson(groesse);
                        }
                    } else if (!Boolean.TRUE.equals(pruefungGroesse.getPending())) {
                        if (!Objects.equals(groesse, pruefungGroesse.getValue())) {
                            newPruefungGroesse = new PruefungGroesseJson(groesse);
                        }
                    }

                    // identifying flaechenart pruefung
                    if (pruefungFlaechenart == null) {
                        // wird zufällig "angenommen" weil gleich mit anfrage
                        if (Objects.equals(flaechenart, flaecheJson.getFlaechenart())) {
                            newPruefungFlaechenart = new PruefungFlaechenartJson(flaechenart);
                        }
                    } else if (!Boolean.TRUE.equals(pruefungFlaechenart.getPending())) {
                        // vorhandene Prüfung wurde geändert
                        if (!Objects.equals(flaechenart, pruefungFlaechenart.getValue())) {
                            newPruefungFlaechenart = new PruefungFlaechenartJson(flaechenart);
                        }
                    }

                    // identifying anschlussgrad pruefung
                    if (pruefungAnschlussgrad == null) {
                        // wird zufällig "angenommen" weil gleich mit anfrage
                        if (Objects.equals(anschlussgrad, flaecheJson.getAnschlussgrad())) {
                            newPruefungAnschlussgrad = new PruefungAnschlussgradJson(anschlussgrad);
                        }
                    } else if (!Boolean.TRUE.equals(pruefungAnschlussgrad.getPending())) {
                        // vorhandene Prüfung wurde geändert
                        if (!Objects.equals(anschlussgrad, pruefungAnschlussgrad.getValue())) {
                            newPruefungAnschlussgrad = new PruefungAnschlussgradJson(anschlussgrad);
                        }
                    }

                    if ((newPruefungGroesse != null) || (newPruefungFlaechenart != null)
                                || (newPruefungAnschlussgrad != null)) {
                        // setting pruefung
                        final FlaechePruefungJson flaechePruefungJson = (flaecheJson.getPruefung() != null)
                            ? flaecheJson.getPruefung() : new FlaechePruefungJson(null, null, null);
                        flaechePruefungJson.setGroesse(newPruefungGroesse);
                        flaechePruefungJson.setFlaechenart(newPruefungFlaechenart);
                        flaechePruefungJson.setAnschlussgrad(newPruefungAnschlussgrad);
                        flaecheJson.setPruefung(flaechePruefungJson);
                    }
                }
            }
        }
        return AenderungsanfrageUtils.getInstance().createAenderungsanfrageJson(aenderungsanfrage.toJson());
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public void startProcessing() throws Exception {
        if (CidsAppBackend.getInstance().getAppPreferences().isAenderungsanfrageEnabled()) {
            final AenderungsanfrageJson aenderungsanfrage = getAenderungsanfrage();

            // create empty draft pruefung for signaling start of processing
            if (aenderungsanfrage != null) {
                for (final String bezeichnung : aenderungsanfrage.getFlaechen().keySet()) {
                    final FlaecheAenderungJson flaeche = aenderungsanfrage.getFlaechen().get(bezeichnung);
                    if (flaeche != null) {
                        if (flaeche.getPruefung() == null) {
                            flaeche.setPruefung(new FlaechePruefungJson(null, null, null));
                        }
                        if ((flaeche.getGroesse() != null) && (flaeche.getPruefung().getGroesse() == null)) {
                            flaeche.getPruefung().setGroesse(new PruefungGroesseJson());
                        }
                        if ((flaeche.getAnschlussgrad() != null)
                                    && (flaeche.getPruefung().getAnschlussgrad() == null)) {
                            flaeche.getPruefung().setAnschlussgrad(new PruefungAnschlussgradJson());
                        }
                        if ((flaeche.getFlaechenart() != null) && (flaeche.getPruefung().getFlaechenart() == null)) {
                            flaeche.getPruefung().setFlaechenart(new PruefungFlaechenartJson());
                        }
                    }
                }
                for (final String bezeichnung : aenderungsanfrage.getGeometrien().keySet()) {
                    final Feature feature = (Feature)aenderungsanfrage.getGeometrien().get(bezeichnung);
                    if ((feature != null) && !Boolean.TRUE.equals(feature.getProperty("draft"))) {
                        if (feature.getProperty("pruefung") == null) {
                            feature.setProperty("pruefung", false);
                        }
                    }
                }
            }
            sendAenderungsanfrage(aenderungsanfrage);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   kassenzeichenBean  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public void finishProcessing(final CidsBean kassenzeichenBean) throws Exception {
        if (CidsAppBackend.getInstance().getAppPreferences().isAenderungsanfrageEnabled()) {
            final AenderungsanfrageJson aenderungsanfrage = getAenderungsanfrage();
            if ((kassenzeichenBean != null) && (aenderungsanfrage != null)) {
                doUndraft(aenderungsanfrage);
                doPruefung(aenderungsanfrage, kassenzeichenBean, new Date());
                sendAenderungsanfrage(aenderungsanfrage);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Integer getStacId() {
        return stacId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  stacId  DOCUMENT ME!
     */
    private void setStacId(final Integer stacId) {
        this.stacId = stacId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public AenderungsanfrageJson getAenderungsanfrage() {
        return aenderungsanfrage;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    public void setCidsBean(final CidsBean cidsBean) {
        this.cidsBean = cidsBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean changesPending() {
        if (CidsAppBackend.getInstance().getAppPreferences().isAenderungsanfrageEnabled()) {
            final AenderungsanfrageJson aenderungsanfrage = getAenderungsanfrage();
            final String aenderungsanfrageOrigJson = (String)getCidsBean().getProperty(
                    VerdisConstants.PROP.AENDERUNGSANFRAGE.CHANGES_JSON);
            AenderungsanfrageJson aenderungsanfrageOrig;
            try {
                aenderungsanfrageOrig = (aenderungsanfrageOrigJson != null)
                    ? AenderungsanfrageUtils.getInstance().createAenderungsanfrageJson(aenderungsanfrageOrigJson)
                    : null;
            } catch (final Exception ex) {
                LOG.error(ex, ex);
                aenderungsanfrageOrig = null;
            }
            return !Objects.equals(aenderungsanfrage, aenderungsanfrageOrig);
        } else {
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private List<CidsBean> searchAll() throws Exception {
        final List<CidsBean> aenderungsanfrageBeans = new ArrayList<>();

        if (CidsAppBackend.getInstance().getAppPreferences().isAenderungsanfrageEnabled()) {
            final AenderungsanfrageSearchStatement search = new AenderungsanfrageSearchStatement();
            try {
                final Collection<MetaObjectNode> mons = SessionManager.getProxy()
                            .customServerSearch(SessionManager.getSession().getUser(), search);
                for (final MetaObjectNode mon : mons) {
                    final MetaObject mo = SessionManager.getProxy()
                                .getMetaObject(mon.getObjectId(), mon.getClassId(), VerdisConstants.DOMAIN);
                    aenderungsanfrageBeans.add(mo.getBean());
                }
            } catch (final ConnectionException ex) {
                LOG.fatal(ex, ex);
            }
            if (!aenderungsanfrageBeans.isEmpty()) {
                return aenderungsanfrageBeans;
            }
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  changeListener  DOCUMENT ME!
     */
    public void addChangeListener(final ChangeListener changeListener) {
        getChangeListenerHandler().getChangeListeners().add(changeListener);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  changeListener  DOCUMENT ME!
     */
    public void removeChangeListener(final ChangeListener changeListener) {
        getChangeListenerHandler().getChangeListeners().remove(changeListener);
    }

    //~ Inner Interfaces -------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static interface ChangeListener {

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param  aenderungsanfrageJson  DOCUMENT ME!
         */
        void aenderungsanfrageChanged(final AenderungsanfrageJson aenderungsanfrageJson);

        /**
         * DOCUMENT ME!
         *
         * @param  aenderungsanfrageBeans  DOCUMENT ME!
         */
        void aenderungsanfrageBeansChanged(final List<CidsBean> aenderungsanfrageBeans);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class LazyInitialiser {

        //~ Static fields/initializers -----------------------------------------

        private static final AenderungsanfrageHandler INSTANCE = new AenderungsanfrageHandler();

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
    private class ChangeListenerHandler implements ChangeListener {

        //~ Instance fields ----------------------------------------------------

        private final Collection<ChangeListener> changeListeners = new ArrayList<>();

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public Collection<ChangeListener> getChangeListeners() {
            return changeListeners;
        }

        @Override
        public void aenderungsanfrageChanged(final AenderungsanfrageJson aenderungsanfrageJson) {
            for (final ChangeListener listener : changeListeners) {
                listener.aenderungsanfrageChanged(aenderungsanfrageJson);
            }
        }

        @Override
        public void aenderungsanfrageBeansChanged(final List<CidsBean> aenderungsanfrageBeans) {
            for (final ChangeListener listener : changeListeners) {
                listener.aenderungsanfrageBeansChanged(aenderungsanfrageBeans);
            }
        }
    }
}
