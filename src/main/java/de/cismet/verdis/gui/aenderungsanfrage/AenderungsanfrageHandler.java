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

import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.verdis.CidsAppBackend;

import de.cismet.verdis.commons.constants.VerdisConstants;

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
import de.cismet.verdis.server.search.AenderungsanfrageStatusSearchStatement;
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
    private CidsBean aenderungsanfrageBean = null;
    private AenderungsanfrageJson aenderungsanfrageJson;
    private Map<AenderungsanfrageUtils.Status, CidsBean> statusBeanMap = new HashMap();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AenderungsanfrageHandler object.
     */
    private AenderungsanfrageHandler() {
        final AenderungsanfrageStatusSearchStatement search = new AenderungsanfrageStatusSearchStatement();

        try {
            final Collection<MetaObjectNode> mons = CidsAppBackend.getInstance().executeCustomServerSearch(search);
            if (mons != null) {
                for (final MetaObjectNode mon : mons) {
                    final MetaObject mo = CidsAppBackend.getInstance()
                                .getVerdisMetaObject(mon.getObjectId(), mon.getClassId());
                    if (mo != null) {
                        final CidsBean statusBean = mo.getBean();
                        statusBeanMap.put(AenderungsanfrageUtils.Status.valueOf(
                                (String)statusBean.getProperty("schluessel")),
                            statusBean);
                    }
                }
            }
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Map<AenderungsanfrageUtils.Status, CidsBean> getStatusBeanMap() {
        return statusBeanMap;
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
     * @param   valueOrig    DOCUMENT ME!
     * @param   valueChange  DOCUMENT ME!
     * @param   hasPruefung  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean isPending(final Object valueOrig, final Object valueChange, final boolean hasPruefung) {
        return !Objects.equals(valueOrig, valueChange) && !hasPruefung;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flaecheBean  DOCUMENT ME!
     * @param   flaecheJson  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean isPending(final CidsBean flaecheBean, final FlaecheAenderungJson flaecheJson) {
        final Integer groesse = (flaecheBean != null)
            ? (Integer)flaecheBean.getProperty(VerdisConstants.PROP.FLAECHE.FLAECHENINFO + "."
                        + VerdisConstants.PROP.FLAECHENINFO.GROESSE_KORREKTUR) : null;
        final Integer groesseAenderung = flaecheJson.getGroesse();
        final boolean hasPruefungGroesse = (flaecheJson.getPruefung() != null)
                    && (flaecheJson.getPruefung().getGroesse() != null);

        final String flaechenart = (flaecheBean != null)
            ? (String)flaecheBean.getProperty(VerdisConstants.PROP.FLAECHE.FLAECHENINFO + "."
                        + VerdisConstants.PROP.FLAECHENINFO.FLAECHENART + "."
                        + VerdisConstants.PROP.FLAECHENART.ART_ABKUERZUNG) : null;
        final String flaechenartAenderung = (flaecheJson.getFlaechenart() != null)
            ? flaecheJson.getFlaechenart().getArtAbkuerzung() : null;
        final boolean hasPruefungFlaechenart = (flaecheJson.getPruefung() != null)
                    && (flaecheJson.getPruefung().getFlaechenart() != null);

        final String anschlussgrad = (flaecheBean != null)
            ? (String)flaecheBean.getProperty(VerdisConstants.PROP.FLAECHE.FLAECHENINFO + "."
                        + VerdisConstants.PROP.FLAECHENINFO.ANSCHLUSSGRAD + "."
                        + VerdisConstants.PROP.ANSCHLUSSGRAD.GRAD_ABKUERZUNG) : null;
        final String anschlussgradAenderung = (flaecheJson.getAnschlussgrad() != null)
            ? flaecheJson.getAnschlussgrad().getGradAbkuerzung() : null;
        final boolean hasPruefungAnschlussgrad = (flaecheJson.getPruefung() != null)
                    && (flaecheJson.getPruefung().getAnschlussgrad() != null);

        final boolean pendingGroesse = (groesseAenderung != null)
                    && isPending(groesse, groesseAenderung, hasPruefungGroesse);
        final boolean pendingFlaechenart = (flaechenartAenderung != null)
                    && isPending(flaechenart, flaechenartAenderung, hasPruefungFlaechenart);
        final boolean pendingAnschlussgrad = (anschlussgradAenderung != null)
                    && isPending(anschlussgrad, anschlussgradAenderung, hasPruefungAnschlussgrad);

        return pendingGroesse || pendingFlaechenart || pendingAnschlussgrad;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   kassenzeichenBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isPending(final CidsBean kassenzeichenBean) {
        if ((getAenderungsanfrage() != null) && (getAenderungsanfrage().getFlaechen() != null)
                    && (getAenderungsanfrage().getFlaechen().keySet() != null)) {
            final Map<String, CidsBean> flaechenBeans = new HashMap<>();
            for (final CidsBean flaecheBean
                        : kassenzeichenBean.getBeanCollectionProperty(VerdisConstants.PROP.KASSENZEICHEN.FLAECHEN)) {
                final String bezeichnung = (String)flaecheBean.getProperty(
                        VerdisConstants.PROP.FLAECHE.FLAECHENBEZEICHNUNG);
                flaechenBeans.put(bezeichnung, flaecheBean);
            }

            for (final String bezeichnung : getAenderungsanfrage().getFlaechen().keySet()) {
                final CidsBean flaecheBean = flaechenBeans.get(bezeichnung);
                final FlaecheAenderungJson flaecheJson = getAenderungsanfrage().getFlaechen().get(bezeichnung);
                if (isPending(flaecheBean, flaecheJson)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * DOCUMENT ME!
     */
    public void refreshAenderungsanfrageJson() {
        final CidsBean aenderungsanfrageBean = getAenderungsanfrageBean();
        if (aenderungsanfrageBean != null) {
            try {
                setAenderungsanfrageJson(AenderungsanfrageUtils.getInstance().createAenderungsanfrageJson(
                        (String)aenderungsanfrageBean.getProperty(
                            VerdisConstants.PROP.AENDERUNGSANFRAGE.CHANGES_JSON)));
                return;
            } catch (final Exception ex) {
                LOG.error(ex, ex);
            }
        }
        setAenderungsanfrageJson(aenderungsanfrageJson);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  aenderungsanfrageJson  DOCUMENT ME!
     */
    public void setAenderungsanfrageJson(final AenderungsanfrageJson aenderungsanfrageJson) {
        this.aenderungsanfrageJson = aenderungsanfrageJson;
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
                AenderungsanfrageHandler.getInstance().getStacId());

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
                setAenderungsanfrageJson(resultJson.getAenderungsanfrage());
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
     * @param   cidsBean  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public void loadAenderungsanfrageBean(final CidsBean cidsBean) throws Exception {
        if (cidsBean != null) {
            final AenderungsanfrageSearchStatement search = new AenderungsanfrageSearchStatement();
            search.setKassenzeichennummer((Integer)cidsBean.getProperty(
                    VerdisConstants.PROP.KASSENZEICHEN.KASSENZEICHENNUMMER));
            updateAenderungsanfrageBean(search);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   stacId  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public void loadAenderungsanfrageBean(final Integer stacId) throws Exception {
        if (stacId != null) {
            final AenderungsanfrageSearchStatement search = new AenderungsanfrageSearchStatement();
            search.setStacId(stacId);
            updateAenderungsanfrageBean(search);
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
        setAenderungsanfrageBean(null);
        setStacId(null);
        if (CidsAppBackend.getInstance().getAppPreferences().isAenderungsanfrageEnabled()) {
            final Collection<MetaObjectNode> mons = (Collection)CidsAppBackend.getInstance()
                        .executeCustomServerSearch(search);
            if ((mons != null) && !mons.isEmpty()) {
                final MetaObjectNode mon = mons.iterator().next();
                final MetaObject mo = CidsAppBackend.getInstance()
                            .getVerdisMetaObject(mon.getObjectId(), mon.getClassId());
                if (mo != null) {
                    setAenderungsanfrageBean(mo.getBean());
                    setStacId((Integer)getAenderungsanfrageBean().getProperty("stac_id"));
                }
            }
        }
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

            for (final String bezeichnung : aenderungsanfrage.getFlaechen().keySet()) {
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
    public CidsBean getAenderungsanfrageBean() {
        return aenderungsanfrageBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Integer getStacId() {
        return stacId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  stacId  DOCUMENT ME!
     */
    public void setStacId(final Integer stacId) {
        this.stacId = stacId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public AenderungsanfrageJson getAenderungsanfrage() {
        return aenderungsanfrageJson;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  aenderungsanfrageBean  DOCUMENT ME!
     */
    public void setAenderungsanfrageBean(final CidsBean aenderungsanfrageBean) {
        this.aenderungsanfrageBean = aenderungsanfrageBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean changesPending() {
        if (CidsAppBackend.getInstance().getAppPreferences().isAenderungsanfrageEnabled()) {
            final AenderungsanfrageJson aenderungsanfrage = getAenderungsanfrage();
            final String aenderungsanfrageOrigJson = (String)getAenderungsanfrageBean().getProperty(
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
    public List<CidsBean> searchAenderungsanfrageBeans() throws Exception {
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
     * @param   kassenzeichennummer  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean searchAenderungsanfrageBean(final Integer kassenzeichennummer) {
        if (CidsAppBackend.getInstance().getAppPreferences().isAenderungsanfrageEnabled()) {
            if (kassenzeichennummer != null) {
                final AenderungsanfrageSearchStatement search = new AenderungsanfrageSearchStatement();
                search.setKassenzeichennummer(kassenzeichennummer);
                final List<CidsBean> cidsBeanList = new ArrayList<>();
                try {
                    final Collection<MetaObjectNode> mons = SessionManager.getProxy()
                                .customServerSearch(SessionManager.getSession().getUser(), search);
                    for (final MetaObjectNode mon : mons) {
                        final MetaObject mo = SessionManager.getProxy()
                                    .getMetaObject(mon.getObjectId(), mon.getClassId(), VerdisConstants.DOMAIN);
                        cidsBeanList.add(mo.getBean());
                    }
                } catch (final ConnectionException ex) {
                    LOG.fatal(ex, ex);
                }
                if (!cidsBeanList.isEmpty()) {
                    return cidsBeanList.get(0);
                }
            }
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   aenderungsanfrageBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getStacIdHash(final CidsBean aenderungsanfrageBean) {
        if (aenderungsanfrageBean != null) {
            final String md5 = DigestUtils.md5Hex(
                    Integer.toString(
                        (Integer)aenderungsanfrageBean.getProperty(
                            VerdisConstants.PROP.AENDERUNGSANFRAGE.KASSENZEICHEN_NUMMER))
                            + ";"
                            + Integer.toString(
                                (Integer)aenderungsanfrageBean.getProperty(
                                    VerdisConstants.PROP.AENDERUNGSANFRAGE.STAC_ID)));
            return md5.substring(0, 4);
        } else {
            return null;
        }
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
}
