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

import com.fasterxml.jackson.databind.ObjectMapper;

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
import de.cismet.verdis.server.json.aenderungsanfrage.FlaechePruefungJson;
import de.cismet.verdis.server.json.aenderungsanfrage.NachrichtJson;
import de.cismet.verdis.server.json.aenderungsanfrage.PruefungJson;
import de.cismet.verdis.server.search.AenderungsanfrageSearchStatement;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class AenderungsanfrageHandler {

    //~ Static fields/initializers ---------------------------------------------

    private static final ObjectMapper MAPPER = new ObjectMapper();
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
     * @param  message  DOCUMENT ME!
     */
    public void addSystemMessage(final String message) {
        getAenderungsanfrageJson().getNachrichten().add(new NachrichtJson(new Date(), message));
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
     * @param   anfrageJson        DOCUMENT ME!
     * @param   kassenzeichenBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public AenderungsanfrageJson doPruefung(final AenderungsanfrageJson anfrageJson, final CidsBean kassenzeichenBean)
            throws Exception {
        if (anfrageJson == null) {
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

            final Date now = new Date();
            for (final String bezeichnung : anfrageJson.getFlaechen().keySet()) {
                final FlaecheAenderungJson flaecheJson = anfrageJson.getFlaechen().get(bezeichnung);
                final CidsBean flaecheBean = flaechenBeans.get(bezeichnung);
                // TODO message => flaechenänderung entfernt
                if (flaecheBean == null) {
                    anfrageJson.getFlaechen().remove(bezeichnung);
                    continue;
                }

                // identifying groesse pruefung
                final Integer groesse = (Integer)flaecheBean.getProperty(VerdisConstants.PROP.FLAECHE.FLAECHENINFO + "."
                                + VerdisConstants.PROP.FLAECHENINFO.GROESSE_KORREKTUR);
                final Integer groesseAenderung = flaecheJson.getGroesse();
                final PruefungJson.Groesse pruefungGroesseJson = (flaecheJson.getPruefung() != null)
                    ? flaecheJson.getPruefung().getGroesse() : null;
                final PruefungJson.Groesse newPruefungGroesseJson =
                    (isNewPruefungDone(groesse, groesseAenderung, pruefungGroesseJson))
                    ? new PruefungJson.Groesse(groesseAenderung, "test", now) : pruefungGroesseJson;

                // identifying flaechenart pruefung
                final String flaechenart = (String)flaecheBean.getProperty(VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                + "."
                                + VerdisConstants.PROP.FLAECHENINFO.FLAECHENART + "."
                                + VerdisConstants.PROP.FLAECHENART.ART);
                final String flaechenartAenderung = flaecheJson.getFlaechenart().getArt();
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
                final String anschlussgradAenderung = flaecheJson.getAnschlussgrad().getGrad();
                final PruefungJson.Anschlussgrad pruefungAnschlussgradJson = (flaecheJson.getPruefung() != null)
                    ? flaecheJson.getPruefung().getAnschlussgrad() : null;
                final PruefungJson.Anschlussgrad newPruefungAnschlussgradJson =
                    (isNewPruefungDone(anschlussgrad, anschlussgradAenderung, pruefungAnschlussgradJson))
                    ? new PruefungJson.Anschlussgrad(flaecheJson.getAnschlussgrad(), "test", now)
                    : pruefungAnschlussgradJson;

                // setting pruefung
                final FlaechePruefungJson flaechePruefungJson = (flaecheJson.getPruefung() != null)
                    ? flaecheJson.getPruefung() : new FlaechePruefungJson(null, null, null);
                flaechePruefungJson.setGroesse(newPruefungGroesseJson);
                flaechePruefungJson.setFlaechenart(newPruefungFlaechenartJson);
                flaechePruefungJson.setAnschlussgrad(newPruefungAnschlussgradJson);
                flaecheJson.setPruefung(flaechePruefungJson);

                // adding systemMessages

                final String pruefungFunctionString =
                    "pruefung({ what: '%s', accepted: %b,  bezeichnung: '%s', groesseAenderung: '%s' })";
                final boolean isPruefungGroesseInvalid = (pruefungGroesseJson != null)
                            && !groesse.equals(pruefungGroesseJson.getAnfrage());
                if ((pruefungGroesseJson != newPruefungGroesseJson) || isPruefungGroesseInvalid) {
                    addSystemMessage(String.format(
                            pruefungFunctionString,
                            "groesse",
                            groesseAenderung.equals(groesse),
                            bezeichnung,
                            String.valueOf(groesseAenderung)));
                }
                final boolean isPruefungFlaechenartInvalid = (pruefungFlaechenartJson != null)
                            && !flaechenart.equals(pruefungFlaechenartJson.getAnfrage());
                if ((pruefungFlaechenartJson != newPruefungFlaechenartJson) || isPruefungFlaechenartInvalid) {
                    addSystemMessage(String.format(
                            pruefungFunctionString,
                            "flaechenart",
                            flaechenartAenderung.equals(flaechenart),
                            bezeichnung,
                            String.valueOf(flaechenartAenderung)));
                }
                final boolean isPruefungAnschlussgradInvalid = (pruefungAnschlussgradJson != null)
                            && !anschlussgrad.equals(pruefungAnschlussgradJson.getAnfrage());
                if ((pruefungAnschlussgradJson != newPruefungAnschlussgradJson) || isPruefungAnschlussgradInvalid) {
                    addSystemMessage(String.format(
                            pruefungFunctionString,
                            "anschlussgrad",
                            anschlussgradAenderung.equals(anschlussgrad),
                            bezeichnung,
                            String.valueOf(anschlussgradAenderung)));
                }
            }
        }
        return AenderungsanfrageJson.readValue(MAPPER.writeValueAsString(anfrageJson));
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
            final AenderungsanfrageJson anfrageJson = getAenderungsanfrageJson();
            doPruefung(anfrageJson, kassenzeichenBean);

            aenderungsanfrageBean.setProperty(
                VerdisConstants.PROP.AENDERUNGSANFRAGE.CHANGES_JSON,
                MAPPER.writeValueAsString(anfrageJson));
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
