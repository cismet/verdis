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

import lombok.Getter;
import lombok.Setter;

import org.geojson.Feature;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    private final ChangeListenerHandler changeListenerHandler = new ChangeListenerHandler();
    private final List<CidsBean> aenderungsanfrageBeans = new ArrayList<>();

    @Getter private boolean filterActive = true;
    @Getter private boolean filterOwn = true;

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
                                        reloadCurrentAnfrage();
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
     * @param   stacId             DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public AenderungsanfrageResultJson sendAenderungsanfrage(final AenderungsanfrageJson aenderungsanfrage,
            final Integer stacId) throws Exception {
        return sendAenderungsanfrage(aenderungsanfrage, stacId, null);
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
        return sendAenderungsanfrage(aenderungsanfrage, getStacId(), null);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   aenderungsanfrage                DOCUMENT ME!
     * @param   speichernMitOderOhneVeranlagung  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public AenderungsanfrageResultJson sendAenderungsanfrage(final AenderungsanfrageJson aenderungsanfrage,
            final Boolean speichernMitOderOhneVeranlagung) throws Exception {
        return sendAenderungsanfrage(aenderungsanfrage, getStacId(), speichernMitOderOhneVeranlagung);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   aenderungsanfrage                DOCUMENT ME!
     * @param   stacId                           DOCUMENT ME!
     * @param   speichernMitOderOhneVeranlagung  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public AenderungsanfrageResultJson sendAenderungsanfrage(final AenderungsanfrageJson aenderungsanfrage,
            final Integer stacId,
            final Boolean speichernMitOderOhneVeranlagung) throws Exception {
        final ServerActionParameter<String> paramAenderungsanfrage = new ServerActionParameter<>(
                KassenzeichenChangeRequestServerAction.Parameter.CHANGEREQUEST_JSON.toString(),
                aenderungsanfrage.toJson());
        final ServerActionParameter<Integer> paramStacId = new ServerActionParameter<>(
                KassenzeichenChangeRequestServerAction.Parameter.STAC_ID.toString(),
                stacId);
        final ServerActionParameter<Boolean> paramSpeichern = new ServerActionParameter<>(
                KassenzeichenChangeRequestServerAction.Parameter.CLERK_IS_SAVING.toString(),
                speichernMitOderOhneVeranlagung);

        final Object result = SessionManager.getConnection()
                    .executeTask(
                        SessionManager.getSession().getUser(),
                        KassenzeichenChangeRequestServerAction.TASKNAME,
                        VerdisConstants.DOMAIN,
                        null,
                        ConnectionContext.createDeprecated(),
                        paramStacId,
                        paramAenderungsanfrage,
                        paramSpeichern);
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
    public void reloadCurrentAnfrage() throws Exception {
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
     * @param  newAenderungsanfrageBeans  DOCUMENT ME!
     */
    public void setAenderungsanfrageBeans(final List<CidsBean> newAenderungsanfrageBeans) {
        final List<CidsBean> aenderungsanfrageBeans = getAenderungsanfrageBeans();
        aenderungsanfrageBeans.clear();
        aenderungsanfrageBeans.addAll(newAenderungsanfrageBeans);
        getChangeListenerHandler().aenderungsanfrageBeansChanged(aenderungsanfrageBeans);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public void reloadAenderungsanfrageBeans() throws Exception {
        synchronized (changeListenerHandler) {
            final List<CidsBean> aenderungsanfrageBeans = getAenderungsanfrageBeans();
            changeListenerHandler.loadingStarted();
            new SwingWorker<List<CidsBean>, Void>() {

                    @Override
                    protected List<CidsBean> doInBackground() throws Exception {
                        return searchAll();
                    }

                    @Override
                    protected void done() {
                        try {
                            setAenderungsanfrageBeans(get());
                            changeListenerHandler.loadingFinished();
                        } catch (final Exception ex) {
                            LOG.error(ex, ex);
                        }
                    }
                }.execute();
        }
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
            search.setActive(Boolean.TRUE);
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
     * @throws  Exception  DOCUMENT ME!
     */
    public void startProcessing() throws Exception {
        if (CidsAppBackend.getInstance().getAppPreferences().isAenderungsanfrageEnabled()) {
            final AenderungsanfrageJson aenderungsanfrage = getAenderungsanfrage();
            if (aenderungsanfrage != null) {
                final Integer stacId = getStacId();
                final CidsAppBackend.StacOptionsEntry stacEntry = (stacId != null)
                    ? CidsAppBackend.getInstance().getStacOptionsEntry(stacId) : null;
                final Timestamp expiratonDate = (stacEntry != null) ? stacEntry.getTimestamp() : null;
                if ((expiratonDate != null) && expiratonDate.after(new Date())) {
                    // create empty draft pruefung for signaling start of processing
                    for (final String bezeichnung : aenderungsanfrage.getFlaechen().keySet()) {
                        final FlaecheAenderungJson flaeche = aenderungsanfrage.getFlaechen().get(bezeichnung);
                        if (flaeche != null) {
                            if (flaeche.getPruefung() == null) {
                                flaeche.setPruefung(new FlaechePruefungJson(null, null, null));
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
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   kassenzeichenBean  DOCUMENT ME!
     * @param   veranlagt          DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public void finishProcessing(final CidsBean kassenzeichenBean, final boolean veranlagt) throws Exception {
        if (CidsAppBackend.getInstance().getAppPreferences().isAenderungsanfrageEnabled()) {
            final AenderungsanfrageJson aenderungsanfrage = getAenderungsanfrage();
            if (aenderungsanfrage != null) {
                for (final NachrichtJson nachricht : aenderungsanfrage.getNachrichten()) {
                    if ((nachricht != null) && NachrichtJson.Typ.CLERK.equals(nachricht.getTyp())
                                && Boolean.TRUE.equals(nachricht.getDraft())) {
                        nachricht.setDraft(null);
                    }
                }

                final Map<String, CidsBean> existingFlaechen = new HashMap<>();
                for (final CidsBean flaecheBean
                            : kassenzeichenBean.getBeanCollectionProperty(VerdisConstants.PROP.KASSENZEICHEN.FLAECHEN)) {
                    final String flaechenBezeichnung = (String)flaecheBean.getProperty(
                            VerdisConstants.PROP.FLAECHE.FLAECHENBEZEICHNUNG);
                    if (flaechenBezeichnung != null) {
                        existingFlaechen.put(flaechenBezeichnung.toUpperCase(), flaecheBean);
                    }
                }

                for (final String bezeichnung : aenderungsanfrage.getFlaechen().keySet()) {
                    final FlaecheAenderungJson flaeche = aenderungsanfrage.getFlaechen().get(bezeichnung);
                    final FlaechePruefungJson pruefung = (flaeche != null) ? flaeche.getPruefung() : null;
                    final PruefungGroesseJson pruefungGroesse = (pruefung != null) ? pruefung.getGroesse() : null;
                    final PruefungFlaechenartJson pruefungFlaechenart = (pruefung != null) ? pruefung.getFlaechenart()
                                                                                           : null;
                    final PruefungAnschlussgradJson pruefungAnschlussgrad = (pruefung != null)
                        ? pruefung.getAnschlussgrad() : null;

                    final CidsBean flaecheBean = existingFlaechen.get(bezeichnung);
                    final Integer groesseCids = (flaecheBean != null)
                        ? (Integer)flaecheBean.getProperty(
                            VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                    + "."
                                    + VerdisConstants.PROP.FLAECHENINFO.GROESSE_KORREKTUR) : null;
                    final FlaecheFlaechenartJson flaechenartCids =
                        ((flaecheBean != null)
                                    && (flaecheBean.getProperty(
                                            VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                            + "."
                                            + VerdisConstants.PROP.FLAECHENINFO.FLAECHENART) != null))
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
                    final FlaecheAnschlussgradJson anschlussgradCids =
                        ((flaecheBean != null)
                                    && (flaecheBean.getProperty(
                                            VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                            + "."
                                            + VerdisConstants.PROP.FLAECHENINFO.ANSCHLUSSGRAD) != null))
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

                    if ((pruefungGroesse != null) && !Objects.equals(pruefungGroesse, groesseCids)) {
                        pruefungGroesse.setValue(groesseCids);
                        pruefungGroesse.setPending(Boolean.TRUE);
                    }
                    if ((pruefungFlaechenart != null) && !Objects.equals(pruefungFlaechenart, flaechenartCids)) {
                        pruefungFlaechenart.setValue(flaechenartCids);
                        pruefungFlaechenart.setPending(Boolean.TRUE);
                    }
                    if ((pruefungAnschlussgrad != null) && !Objects.equals(pruefungAnschlussgrad, anschlussgradCids)) {
                        pruefungAnschlussgrad.setValue(anschlussgradCids);
                        pruefungAnschlussgrad.setPending(Boolean.TRUE);
                    }
                }
                sendAenderungsanfrage(aenderungsanfrage, veranlagt);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public void cancelProcessing() throws Exception {
        if (CidsAppBackend.getInstance().getAppPreferences().isAenderungsanfrageEnabled()) {
            final AenderungsanfrageJson aenderungsanfrage = getAenderungsanfrage();
            if (aenderungsanfrage != null) {
                final Collection<NachrichtJson> nachrichtenToRemove = new HashSet<>();
                for (final NachrichtJson nachricht : aenderungsanfrage.getNachrichten()) {
                    if ((nachricht != null) && NachrichtJson.Typ.CLERK.equals(nachricht.getTyp())
                                && Boolean.TRUE.equals(nachricht.getDraft())) {
                        nachrichtenToRemove.add(nachricht);
                    }
                }
                aenderungsanfrage.getNachrichten().removeAll(nachrichtenToRemove);

                for (final FlaecheAenderungJson flaeche : aenderungsanfrage.getFlaechen().values()) {
                    if (flaeche != null) {
                        final FlaechePruefungJson pruefung = flaeche.getPruefung();
                        if (pruefung != null) {
                            if ((pruefung.getFlaechenart() != null)
                                        && Boolean.TRUE.equals(pruefung.getFlaechenart().getPending())) {
                                pruefung.setFlaechenart(null);
                            }
                            if ((pruefung.getAnschlussgrad() != null)
                                        && Boolean.TRUE.equals(pruefung.getAnschlussgrad().getPending())) {
                                pruefung.setAnschlussgrad(null);
                            }
                            if ((pruefung.getGroesse() != null)
                                        && Boolean.TRUE.equals(pruefung.getGroesse().getPending())) {
                                pruefung.setGroesse(null);
                            }
                        }
                    }
                }
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
            if (aenderungsanfrage != null) {
                for (final NachrichtJson nachricht : aenderungsanfrage.getNachrichten()) {
                    if (NachrichtJson.Typ.CLERK.equals(nachricht.getTyp())
                                && Boolean.TRUE.equals(nachricht.getDraft())) {
                        return true;
                    }
                }
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
            }
        }
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  filterActive  DOCUMENT ME!
     */
    public void setFilterActive(final boolean filterActive) {
        this.filterActive = filterActive;
        if (!filterActive) {
            try {
                reloadAenderungsanfrageBeans();
            } catch (final Exception exception) {
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  filterOwn  DOCUMENT ME!
     */
    public void setFilterOwn(final boolean filterOwn) {
        this.filterOwn = filterOwn;
        if (!filterOwn) {
            try {
                reloadAenderungsanfrageBeans();
            } catch (final Exception exception) {
            }
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
            if (isFilterActive()) {
                search.setActive(Boolean.TRUE);
            } else {
                search.setActive(null);
            }

            if (isFilterOwn()) {
                search.setClerk(SessionManager.getSession().getUser().getName());
            }

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
         */
        void loadingStarted();

        /**
         * DOCUMENT ME!
         */
        void loadingFinished();

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
        public void loadingStarted() {
            for (final ChangeListener listener : changeListeners) {
                listener.loadingStarted();
            }
        }

        @Override
        public void loadingFinished() {
            for (final ChangeListener listener : changeListeners) {
                listener.loadingFinished();
            }
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
