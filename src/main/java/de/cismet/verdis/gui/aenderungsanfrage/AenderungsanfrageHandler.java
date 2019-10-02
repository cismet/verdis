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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.verdis.CidsAppBackend;

import de.cismet.verdis.commons.constants.VerdisConstants;

import de.cismet.verdis.server.json.aenderungsanfrage.AenderungsanfrageJson;
import de.cismet.verdis.server.json.aenderungsanfrage.FlaecheAenderungJson;
import de.cismet.verdis.server.json.aenderungsanfrage.FlaecheAnschlussgradJson;
import de.cismet.verdis.server.json.aenderungsanfrage.FlaecheFlaechenartJson;
import de.cismet.verdis.server.json.aenderungsanfrage.FlaechePruefungJson;
import de.cismet.verdis.server.json.aenderungsanfrage.NachrichtJson;
import de.cismet.verdis.server.json.aenderungsanfrage.NachrichtParameterJson;
import de.cismet.verdis.server.json.aenderungsanfrage.PruefungJson;
import de.cismet.verdis.server.search.AenderungsanfrageSearchStatement;
import java.util.Objects;

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

    //~ Methods ----------------------------------------------------------------

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
     */
    public void refreshAenderungsanfrageJson() {
        final CidsBean aenderungsanfrageBean = getAenderungsanfrageBean();
        if (aenderungsanfrageBean != null) {
            try {
                aenderungsanfrageJson = AenderungsanfrageJson.readValue((String)aenderungsanfrageBean.getProperty(
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
        getAenderungsanfrageJson().getNachrichten()
                .add(new NachrichtJson.System(new Date(), nachrichtenParameter, username));
    }

    /**
     * DOCUMENT ME!
     *
     * @param   cidsBean  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public void updateAenderungsanfrageBean(final CidsBean cidsBean) throws Exception {
        aenderungsanfrageBean = null;
        final AenderungsanfrageSearchStatement search = new AenderungsanfrageSearchStatement();
        search.setKassenzeichennummer((Integer)cidsBean.getProperty(
                VerdisConstants.PROP.KASSENZEICHEN.KASSENZEICHENNUMMER));
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
        final NachrichtParameterJson parameters = NachrichtParameterJson.readValue(json);
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
                final PruefungJson.Groesse pruefungGroesse = (flaecheJson.getPruefung() != null)
                    ? flaecheJson.getPruefung().getGroesse() : null;
                final PruefungJson.Groesse newPruefungGroesse =
                    (isNewPruefungDone(groesse, groesseAenderung, pruefungGroesse))
                    ? new PruefungJson.Groesse(groesseAenderung, "test", now) : pruefungGroesse;

                // identifying flaechenart pruefung
                final String flaechenart = (String)flaecheBean.getProperty(VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                + "."
                                + VerdisConstants.PROP.FLAECHENINFO.FLAECHENART + "."
                                + VerdisConstants.PROP.FLAECHENART.ART);
                final FlaecheFlaechenartJson flaechenartAenderung = flaecheJson.getFlaechenart();
                final PruefungJson.Flaechenart pruefungFlaechenartJson = (flaecheJson.getPruefung() != null)
                    ? flaecheJson.getPruefung().getFlaechenart() : null;
                final PruefungJson.Flaechenart newPruefungFlaechenartJson =
                    (isNewPruefungDone(flaechenart, flaechenartAenderung, pruefungFlaechenartJson))
                    ? new PruefungJson.Flaechenart(flaecheJson.getFlaechenart(), "test", now) : pruefungFlaechenartJson;

                // identifying anschlussgrad pruefung
                final String anschlussgrad = (String)flaecheBean.getProperty(VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                + "."
                                + VerdisConstants.PROP.FLAECHENINFO.ANSCHLUSSGRAD + "."
                                + VerdisConstants.PROP.ANSCHLUSSGRAD.GRAD);
                final FlaecheAnschlussgradJson anschlussgradAenderung = flaecheJson.getAnschlussgrad();
                final PruefungJson.Anschlussgrad pruefungAnschlussgradJson = (flaecheJson.getPruefung() != null)
                    ? flaecheJson.getPruefung().getAnschlussgrad() : null;
                final PruefungJson.Anschlussgrad newPruefungAnschlussgradJson =
                    (isNewPruefungDone(anschlussgrad, anschlussgradAenderung, pruefungAnschlussgradJson))
                    ? new PruefungJson.Anschlussgrad(flaecheJson.getAnschlussgrad(), "test", now)
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
                                && !groesse.equals(pruefungGroesse.getAnfrage());

                    if (!Objects.equals(pruefungGroesse, newPruefungGroesse) || isPruefungGroesseInvalid) {
                        addSystemMessage(new NachrichtParameterJson.Groesse(
                                groesseAenderung.equals(groesse) ? NachrichtParameterJson.Type.CHANGED
                                                                 : NachrichtParameterJson.Type.REJECTED,
                                bezeichnung,
                                groesseAenderung),
                            username);
                    }
                    final boolean isPruefungFlaechenartInvalid = (pruefungFlaechenartJson != null)
                                && !flaechenart.equals(pruefungFlaechenartJson.getAnfrage().getArt());
                    if (!Objects.equals(pruefungFlaechenartJson, newPruefungFlaechenartJson) || isPruefungFlaechenartInvalid) {
                        addSystemMessage(new NachrichtParameterJson.Flaechenart(
                                flaechenartAenderung.getArt().equals(flaechenart) ? NachrichtParameterJson.Type.CHANGED
                                                                         : NachrichtParameterJson.Type.REJECTED,
                                bezeichnung,
                                flaechenartAenderung),
                            username);
                    }
                    final boolean isPruefungAnschlussgradInvalid = (pruefungAnschlussgradJson != null)
                                && !anschlussgrad.equals(pruefungAnschlussgradJson.getAnfrage().getGrad());
                    if (!Objects.equals(pruefungAnschlussgradJson, newPruefungAnschlussgradJson) || isPruefungAnschlussgradInvalid) {
                        addSystemMessage(new NachrichtParameterJson.Anschlussgrad(
                                anschlussgradAenderung.getGrad().equals(anschlussgrad) ? NachrichtParameterJson.Type.CHANGED
                                                                             : NachrichtParameterJson.Type.REJECTED,
                                bezeichnung,
                                anschlussgradAenderung),
                            username);
                    }
                }
            }
        }
        return AenderungsanfrageJson.readValue(aenderungsanfrage.toJson());
    }

    /**
     * DOCUMENT ME!
     *
     * @param   kassenzeichenBean  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public void persistAenderungsanfrageBean(final CidsBean kassenzeichenBean) throws Exception {
        final CidsBean aenderungsanfrageBean = getAenderungsanfrageBean();
        if (aenderungsanfrageBean != null) {
            final AenderungsanfrageJson aenderungsanfrage = getAenderungsanfrageJson();
            doPruefung(aenderungsanfrage, kassenzeichenBean);

            aenderungsanfrageBean.setProperty(
                VerdisConstants.PROP.AENDERUNGSANFRAGE.CHANGES_JSON,
                aenderungsanfrage.toJson());
            this.aenderungsanfrageBean = aenderungsanfrageBean.persist();
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
    public AenderungsanfrageJson getAenderungsanfrageJson() {
        return aenderungsanfrageJson;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<CidsBean> searchAenderungsanfrageBeans() {
        final AenderungsanfrageSearchStatement search = new AenderungsanfrageSearchStatement();
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
            return cidsBeanList;
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
        return null;
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
