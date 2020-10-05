/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.verdis.gui.aenderungsanfrage;

import Sirius.navigator.connection.SessionManager;

import java.io.FileOutputStream;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

import de.cismet.tools.gui.downloadmanager.AbstractCancellableDownload;

import de.cismet.verdis.commons.constants.VerdisConstants;

import de.cismet.verdis.server.action.DownloadChangeRequestAnhangServerAction;
import de.cismet.verdis.server.json.AenderungsanfrageJson;
import de.cismet.verdis.server.json.NachrichtAnhangJson;
import de.cismet.verdis.server.json.NachrichtJson;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class AenderungsanfrageDownload extends AbstractCancellableDownload implements ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

    //~ Instance fields --------------------------------------------------------

    private final AenderungsanfrageJson aenderungsanfrage;
    private final ConnectionContext connectionContext;

    private final Set<NachrichtAnhangJson> nachrichtAnhaenge = new HashSet<>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AenderungsanfrageDownload object.
     *
     * @param  directory          DOCUMENT ME!
     * @param  aenderungsanfrage  DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public AenderungsanfrageDownload(final String directory,
            final AenderungsanfrageJson aenderungsanfrage,
            final ConnectionContext connectionContext) {
        this.directory = directory;
        this.aenderungsanfrage = aenderungsanfrage;
        this.connectionContext = connectionContext;

        final String name = String.format(
                "%d_%d",
                aenderungsanfrage.getKassenzeichen(),
                Math.abs(aenderungsanfrage.hashCode()));
        this.title = String.format("Gesprächsprotokoll %s", name);
        determineDestinationFile(name, ".zip");

        status = State.WAITING;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void run() {
        if (status != State.WAITING) {
            return;
        }

        status = State.RUNNING;
        stateChanged();

        if (!downloadFuture.isCancelled()) {
            createZip();
        }
        status = State.COMPLETED;
        stateChanged();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String getProtokoll() {
        final StringBuffer sb = new StringBuffer();
        for (final NachrichtJson nachrichtJson : aenderungsanfrage.getNachrichten()) {
            if (!Boolean.TRUE.equals(nachrichtJson.getDraft())) {
                final String anhangString;
                if (nachrichtJson.getAnhang() != null) {
                    final List<String> anhaenge = new ArrayList<>();
                    for (final NachrichtAnhangJson nachrichtAnhang : nachrichtJson.getAnhang()) {
                        anhaenge.add(nachrichtAnhang.getName());
                        nachrichtAnhaenge.add(nachrichtAnhang);
                    }
                    if (anhaenge.isEmpty()) {
                        anhangString = null;
                    } else {
                        anhangString = String.join(", ", anhaenge);
                    }
                } else {
                    anhangString = null;
                }

                final String typ;
                if (nachrichtJson.getTyp() != null) {
                    switch (nachrichtJson.getTyp()) {
                        case CITIZEN: {
                            typ = "Bürger";
                        }
                        break;
                        case CLERK: {
                            typ = "Bearbeiter";
                        }
                        break;
                        case SYSTEM: {
                            typ = "System";
                        }
                        break;
                        default: {
                            typ = null;
                        }
                    }
                } else {
                    typ = null;
                }

                final String text = AenderungsanfrageNachrichtPanel.createText(nachrichtJson);
                sb.append(DATE_FORMAT.format(nachrichtJson.getTimestamp()))
                        .append(" - ")
                        .append(typ)
                        .append(":")
                        .append((text != null) ? (" " + text) : "")
                        .append((anhangString != null) ? (" [" + anhangString + "]") : "")
                        .append(System.lineSeparator());
            }
        }
        return sb.toString();
    }

    /**
     * DOCUMENT ME!
     */
    private void createZip() {
        try(final FileOutputStream fos = new FileOutputStream(getFileToSaveTo());
                    final ZipOutputStream zos = new ZipOutputStream(fos)) {
            final String nachrichten = getProtokoll();
            zos.putNextEntry(new ZipEntry("nachrichten.txt"));
            zos.write(nachrichten.getBytes());
            zos.closeEntry();
            for (final NachrichtAnhangJson nachrichtAnhang : nachrichtAnhaenge) {
                zos.putNextEntry(new ZipEntry(nachrichtAnhang.getName()));
                zos.write((byte[])SessionManager.getProxy().executeTask(
                        DownloadChangeRequestAnhangServerAction.TASK_NAME,
                        VerdisConstants.DOMAIN,
                        nachrichtAnhang.toJson(),
                        getConnectionContext()));
                zos.closeEntry();
            }
        } catch (final Exception ex) {
            log.warn("Couldn't write downloaded content to file '" + fileToSaveTo + "'.", ex);
            error(ex);
        }
    }

    @Override
    public final ConnectionContext getConnectionContext() {
        return connectionContext;
    }
}
