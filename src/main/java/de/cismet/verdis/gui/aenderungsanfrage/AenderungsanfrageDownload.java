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

import java.util.HashSet;
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
import de.cismet.verdis.server.utils.AenderungsanfrageUtils;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class AenderungsanfrageDownload extends AbstractCancellableDownload implements ConnectionContextProvider {

    //~ Instance fields --------------------------------------------------------

    private final AenderungsanfrageJson aenderungsanfrage;
    private final ConnectionContext connectionContext;

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

        try {
            if (!downloadFuture.isCancelled()) {
                createZip();
            }
            status = State.COMPLETED;
            stateChanged();
        } catch (final Exception ex) {
            log.warn("Couldn't write downloaded content to file '" + fileToSaveTo + "'.", ex);
            error(ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private void createZip() throws Exception {
        try(final FileOutputStream fos = new FileOutputStream(getFileToSaveTo());
                    final ZipOutputStream zos = new ZipOutputStream(fos)) {
            final Set<NachrichtAnhangJson> nachrichtAnhaenge = new HashSet<>();

            for (final NachrichtJson nachrichtJson : aenderungsanfrage.getNachrichten()) {
                if (!Boolean.TRUE.equals(nachrichtJson.getDraft())) {
                    if (nachrichtJson.getAnhang() != null) {
                        for (final NachrichtAnhangJson nachrichtAnhang : nachrichtJson.getAnhang()) {
                            nachrichtAnhaenge.add(nachrichtAnhang);
                        }
                    }
                }
            }
            zos.putNextEntry(new ZipEntry("nachrichten.html"));
            zos.write(AenderungsanfrageUtils.createChatHtmlFromAenderungsanfrage(
                    aenderungsanfrage,
                    14,
                    true,
                    true,
                    true).getBytes());
            zos.closeEntry();

            final Set<String> entryNames = new HashSet<>();
            for (final NachrichtAnhangJson nachrichtAnhang : nachrichtAnhaenge) {
                final String fileName = nachrichtAnhang.getName();

                final int indexOfExtension = fileName.lastIndexOf(".");
                final String basename = (indexOfExtension >= 0) ? fileName.substring(0, indexOfExtension) : fileName;
                final String extension = (indexOfExtension >= 0) ? fileName.substring(indexOfExtension) : "";

                int fileCounter = 0;
                String entryName = fileName;
                while (entryNames.contains(entryName)) {
                    entryName = String.format("%s (%d)%s", basename, ++fileCounter, extension);
                }
                entryNames.add(entryName);

                zos.putNextEntry(new ZipEntry(entryName));
                zos.write((byte[])SessionManager.getProxy().executeTask(
                        DownloadChangeRequestAnhangServerAction.TASK_NAME,
                        VerdisConstants.DOMAIN,
                        nachrichtAnhang.toJson(),
                        getConnectionContext()));
                zos.closeEntry();
            }
        }
    }

    @Override
    public final ConnectionContext getConnectionContext() {
        return connectionContext;
    }
}
