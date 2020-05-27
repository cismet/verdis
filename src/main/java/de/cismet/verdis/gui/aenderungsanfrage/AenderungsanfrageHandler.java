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

import de.cismet.verdis.CidsAppBackend;

import de.cismet.verdis.commons.constants.VerdisConstants;

import de.cismet.verdis.server.json.AenderungsanfrageJson;
import de.cismet.verdis.server.json.FlaecheAenderungJson;
import de.cismet.verdis.server.json.FlaecheAnschlussgradJson;
import de.cismet.verdis.server.json.FlaecheFlaechenartJson;
import de.cismet.verdis.server.json.FlaechePruefungJson;
import de.cismet.verdis.server.json.NachrichtJson;
import de.cismet.verdis.server.json.NachrichtParameterAnschlussgradJson;
import de.cismet.verdis.server.json.NachrichtParameterFlaechenartJson;
import de.cismet.verdis.server.json.NachrichtParameterGroesseJson;
import de.cismet.verdis.server.json.NachrichtParameterJson;
import de.cismet.verdis.server.json.NachrichtSystemJson;
import de.cismet.verdis.server.json.PruefungAnschlussgradJson;
import de.cismet.verdis.server.json.PruefungFlaechenartJson;
import de.cismet.verdis.server.json.PruefungGroesseJson;
import de.cismet.verdis.server.json.PruefungJson;
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
        return false;
    }

    /**
     * DOCUMENT ME!
     */
    public void refreshAenderungsanfrageJson() {
        final CidsBean aenderungsanfrageBean = getAenderungsanfrageBean();
        if (aenderungsanfrageBean != null) {
            try {
                aenderungsanfrageJson = AenderungsanfrageUtils.createAenderungsanfrageJson((String)
                        aenderungsanfrageBean.getProperty(
                            VerdisConstants.PROP.AENDERUNGSANFRAGE.CHANGES_JSON));
                return;
            } catch (final Exception ex) {
                LOG.error(ex, ex);
            }
        }
        aenderungsanfrageJson = null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  nachrichtenParameter  message DOCUMENT ME!
     * @param  username              DOCUMENT ME!
     */
    public void addSystemMessage(final NachrichtParameterJson nachrichtenParameter, final String username) {
        getAenderungsanfrage().getNachrichten()
                .add(new NachrichtSystemJson(new Date(), nachrichtenParameter, username));
    }

    /**
     * DOCUMENT ME!
     *
     * @param   cidsBean  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public void updateAenderungsanfrageBean(final CidsBean cidsBean) throws Exception {
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
    public void updateAenderungsanfrageBean(final Integer stacId) throws Exception {
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
        aenderungsanfrageBean = null;
        if (CidsAppBackend.getInstance().getAppPreferences().isAenderungsanfrageEnabled()) {
            final Collection<MetaObjectNode> mons = (Collection)CidsAppBackend.getInstance()
                        .executeCustomServerSearch(search);
            if (mons != null) {
                for (final MetaObjectNode mon : mons) {
                    final MetaObject mo = CidsAppBackend.getInstance()
                                .getVerdisMetaObject(mon.getObjectId(), mon.getClassId());
                    if (mo != null) {
                        aenderungsanfrageBean = mo.getBean();
                    }
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   valueOrig       DOCUMENT ME!
     * @param   valueAenderung  DOCUMENT ME!
     * @param   pruefung        DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean isNewPruefungDone(final Object valueOrig,
            final Object valueAenderung,
            final PruefungJson pruefung) {
        if (valueAenderung != null) {
            final boolean hasPruefung = pruefung != null;
            return (valueAenderung.equals(valueOrig) && !hasPruefung)
                        || (hasPruefung && Boolean.TRUE.equals(pruefung.getPending()));
        }
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   json  s DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private void interpretePruefung(final String json) throws Exception {
        final NachrichtParameterJson parameters = AenderungsanfrageUtils.createNachrichtParameterJson(json);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   aenderungsanfrage  DOCUMENT ME!
     * @param   kassenzeichenBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public AenderungsanfrageJson doPruefung(final AenderungsanfrageJson aenderungsanfrage,
            final CidsBean kassenzeichenBean) throws Exception {
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

            for (final NachrichtJson nachricht : aenderungsanfrage.getNachrichten()) {
                if (NachrichtJson.Typ.CLERK.equals(nachricht.getTyp())) {
                    nachricht.setDraft(null);
                }
            }

            final Date now = new Date();
            for (final String bezeichnung : aenderungsanfrage.getFlaechen().keySet()) {
                final FlaecheAenderungJson flaecheJson = aenderungsanfrage.getFlaechen().get(bezeichnung);
                final CidsBean flaecheBean = flaechenBeans.get(bezeichnung);
                // TODO message => flaechenänderung entfernt
                if (flaecheBean == null) {
                    aenderungsanfrage.getFlaechen().remove(bezeichnung);
                    continue;
                }

                // identifying groesse pruefung
                final Integer groesse = (Integer)flaecheBean.getProperty(VerdisConstants.PROP.FLAECHE.FLAECHENINFO + "."
                                + VerdisConstants.PROP.FLAECHENINFO.GROESSE_KORREKTUR);
                final Integer groesseAenderung = flaecheJson.getGroesse();
                final PruefungGroesseJson pruefungGroesse = (flaecheJson.getPruefung() != null)
                    ? flaecheJson.getPruefung().getGroesse() : null;
                final PruefungGroesseJson newPruefungGroesse =
                    (isNewPruefungDone(groesse, groesseAenderung, pruefungGroesse))
                    ? new PruefungGroesseJson(groesseAenderung, "test", now) : pruefungGroesse;

                // identifying flaechenart pruefung
                final String flaechenart = (String)flaecheBean.getProperty(VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                + "."
                                + VerdisConstants.PROP.FLAECHENINFO.FLAECHENART + "."
                                + VerdisConstants.PROP.FLAECHENART.ART);
                final FlaecheFlaechenartJson flaechenartAenderung = flaecheJson.getFlaechenart();
                final PruefungFlaechenartJson pruefungFlaechenartJson = (flaecheJson.getPruefung() != null)
                    ? flaecheJson.getPruefung().getFlaechenart() : null;
                final PruefungFlaechenartJson newPruefungFlaechenartJson =
                    (isNewPruefungDone(flaechenart, flaechenartAenderung, pruefungFlaechenartJson))
                    ? new PruefungFlaechenartJson(flaecheJson.getFlaechenart(), "test", now) : pruefungFlaechenartJson;

                // identifying anschlussgrad pruefung
                final String anschlussgrad = (String)flaecheBean.getProperty(VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                + "."
                                + VerdisConstants.PROP.FLAECHENINFO.ANSCHLUSSGRAD + "."
                                + VerdisConstants.PROP.ANSCHLUSSGRAD.GRAD);
                final FlaecheAnschlussgradJson anschlussgradAenderung = flaecheJson.getAnschlussgrad();
                final PruefungAnschlussgradJson pruefungAnschlussgradJson = (flaecheJson.getPruefung() != null)
                    ? flaecheJson.getPruefung().getAnschlussgrad() : null;
                final PruefungAnschlussgradJson newPruefungAnschlussgradJson =
                    (isNewPruefungDone(anschlussgrad, anschlussgradAenderung, pruefungAnschlussgradJson))
                    ? new PruefungAnschlussgradJson(flaecheJson.getAnschlussgrad(), "test", now)
                    : pruefungAnschlussgradJson;

                if ((newPruefungGroesse != null) || (newPruefungFlaechenartJson != null)
                            || (newPruefungAnschlussgradJson != null)) {
                    // setting pruefung
                    final FlaechePruefungJson flaechePruefungJson = (flaecheJson.getPruefung() != null)
                        ? flaecheJson.getPruefung() : new FlaechePruefungJson(null, null, null);
                    flaechePruefungJson.setGroesse(newPruefungGroesse);
                    flaechePruefungJson.setFlaechenart(newPruefungFlaechenartJson);
                    flaechePruefungJson.setAnschlussgrad(newPruefungAnschlussgradJson);
                    flaecheJson.setPruefung(flaechePruefungJson);

                    // adding systemMessages

                    final String username = SessionManager.getSession().getUser().getName();
                    final boolean isPruefungGroesseInvalid = (pruefungGroesse != null)
                                && !groesse.equals(pruefungGroesse.getValue());
                    if (!Objects.equals(pruefungGroesse, newPruefungGroesse) || isPruefungGroesseInvalid) {
                        addSystemMessage(new NachrichtParameterGroesseJson(
                                groesseAenderung.equals(groesse) ? NachrichtParameterJson.Type.CHANGED
                                                                 : NachrichtParameterJson.Type.REJECTED,
                                bezeichnung,
                                groesseAenderung),
                            username);
                    }
                    final boolean isPruefungFlaechenartInvalid = (pruefungFlaechenartJson != null)
                                && !flaechenart.equals(pruefungFlaechenartJson.getValue().getArt());
                    if (!Objects.equals(pruefungFlaechenartJson, newPruefungFlaechenartJson)
                                || isPruefungFlaechenartInvalid) {
                        addSystemMessage(new NachrichtParameterFlaechenartJson(
                                flaechenartAenderung.getArt().equals(flaechenart)
                                    ? NachrichtParameterJson.Type.CHANGED : NachrichtParameterJson.Type.REJECTED,
                                bezeichnung,
                                flaechenartAenderung),
                            username);
                    }
                    final boolean isPruefungAnschlussgradInvalid = (pruefungAnschlussgradJson != null)
                                && !anschlussgrad.equals(pruefungAnschlussgradJson.getValue().getGrad());
                    if (!Objects.equals(pruefungAnschlussgradJson, newPruefungAnschlussgradJson)
                                || isPruefungAnschlussgradInvalid) {
                        addSystemMessage(new NachrichtParameterAnschlussgradJson(
                                anschlussgradAenderung.getGrad().equals(anschlussgrad)
                                    ? NachrichtParameterJson.Type.CHANGED : NachrichtParameterJson.Type.REJECTED,
                                bezeichnung,
                                anschlussgradAenderung),
                            username);
                    }
                }
            }
        }
        return AenderungsanfrageUtils.createAenderungsanfrageJson(aenderungsanfrage.toJson());
    }

    /**
     * DOCUMENT ME!
     *
     * @param   kassenzeichenBean  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public void persistAenderungsanfrageBean(final CidsBean kassenzeichenBean) throws Exception {
        if (CidsAppBackend.getInstance().getAppPreferences().isAenderungsanfrageEnabled()) {
            final CidsBean aenderungsanfrageBean = getAenderungsanfrageBean();
            if (aenderungsanfrageBean != null) {
                final AenderungsanfrageJson aenderungsanfrage = getAenderungsanfrage();
                doPruefung(aenderungsanfrage, kassenzeichenBean);

                aenderungsanfrageBean.setProperty(
                    VerdisConstants.PROP.AENDERUNGSANFRAGE.CHANGES_JSON,
                    aenderungsanfrage.toJson());
                this.aenderungsanfrageBean = aenderungsanfrageBean.persist();
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
    public AenderungsanfrageJson getAenderungsanfrage() {
        return aenderungsanfrageJson;
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
                    ? AenderungsanfrageUtils.createAenderungsanfrageJson(aenderungsanfrageOrigJson) : null;
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
