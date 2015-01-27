/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis.gui;

import Sirius.navigator.exception.ConnectionException;

import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Collection;
import java.util.List;

import de.cismet.tools.gui.downloadmanager.AbstractDownload;

import de.cismet.verdis.CidsAppBackend;

import de.cismet.verdis.server.search.CsvExportSearchStatement;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class CsvExportSearchDownload extends AbstractDownload {

    //~ Instance fields --------------------------------------------------------

    private final CsvExportSearchStatement search;
    private final List<String> header;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CsvExportSearchDownload object.
     *
     * @param  search     DOCUMENT ME!
     * @param  title      DOCUMENT ME!
     * @param  directory  DOCUMENT ME!
     * @param  filename   DOCUMENT ME!
     * @param  header     DOCUMENT ME!
     */
    public CsvExportSearchDownload(final CsvExportSearchStatement search,
            final String title,
            final String directory,
            final String filename,
            final List<String> header) {
        this.search = search;
        this.title = title;
        this.directory = directory;
        this.header = header;

        status = State.WAITING;

        determineDestinationFile(filename, ".csv");
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void run() {
        if (status != State.WAITING) {
            return;
        }

        status = State.RUNNING;
        stateChanged();

        final Collection<String> csvColl;
        try {
            csvColl = (Collection)CidsAppBackend.getInstance().executeCustomServerSearch(search);
        } catch (final ConnectionException ex) {
            error(ex);
            return;
        }

        final String csv = ((header != null)
                ? (CsvExportSearchStatement.implode(
                        header.toArray(new String[0]),
                        CsvExportSearchStatement.CSV_SEPARATOR) + "\n") : "")
                    + CsvExportSearchStatement.implode(csvColl.toArray(new String[0]), "\n");
        final byte[] content = csv.getBytes();

        if ((content == null) || (content.length <= 0)) {
            log.info("Downloaded content seems to be empty..");

            if (status == State.RUNNING) {
                status = State.COMPLETED;
                stateChanged();
            }

            return;
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fileToSaveTo);
            out.write(content);
        } catch (final IOException ex) {
            log.warn("Couldn't write downloaded content to file '" + fileToSaveTo + "'.", ex);
            error(ex);
            return;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                }
            }
        }

        if (status == State.RUNNING) {
            status = State.COMPLETED;
            stateChanged();
        }
    }
}
