/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.gui.tools;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import java.io.File;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.TransferHandler;
/**
 * DOCUMENT ME!
 *
 * @author   thorsten.hell@cismet.de
 * @version  $Revision$, $Date$
 */
public class DropFileTransferHandler extends TransferHandler {

    //~ Methods ----------------------------------------------------------------

    @Override
    public boolean canImport(final JComponent arg0, final DataFlavor[] arg1) {
        for (int i = 0; i < arg1.length; i++) {
            final DataFlavor flavor = arg1[i];
            if (flavor.equals(DataFlavor.javaFileListFlavor)) {
                System.out.println("canImport: JavaFileList FLAVOR: " + flavor);
                return true;
            }
            if (flavor.equals(DataFlavor.stringFlavor)) {
                System.out.println("canImport: String FLAVOR: " + flavor);
                return true;
            }
            System.err.println("canImport: Rejected Flavor: " + flavor);
        }
        // Didn't find any that match, so:
        return false;
    }

    /**
     * Do the actual import.
     *
     * @param   comp  DOCUMENT ME!
     * @param   t     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @see     javax.swing.TransferHandler#importData(javax.swing.JComponent, java.awt.datatransfer.Transferable)
     */
    @Override
    public boolean importData(final JComponent comp, final Transferable t) {
        final DataFlavor[] flavors = t.getTransferDataFlavors();
        System.out.println("Trying to import:" + t);
        System.out.println("... which has " + flavors.length + " flavors.");
        for (int i = 0; i < flavors.length; i++) {
            final DataFlavor flavor = flavors[i];
            try {
                if (flavor.equals(DataFlavor.javaFileListFlavor)) {
                    System.out.println("importData: FileListFlavor");

                    final List l = (List)t.getTransferData(DataFlavor.javaFileListFlavor);
                    final Iterator iter = l.iterator();
                    while (iter.hasNext()) {
                        final File file = (File)iter.next();
                        System.out.println("GOT FILE: "
                                    + file.getCanonicalPath());
                        // Now do something with the file...
                    }
                    return true;
                } else if (flavor.equals(DataFlavor.stringFlavor)) {
                    System.out.println("importData: String Flavor");
                    final String fileOrURL = (String)t.getTransferData(flavor);
                    System.out.println("GOT STRING: " + fileOrURL);
                    try {
                        final URL url = new URL(fileOrURL);
                        System.out.println("Valid URL: " + url.toString());
                        // Do something with the contents...
                        return true;
                    } catch (MalformedURLException ex) {
                        System.err.println("Not a valid URL");
                        return false;
                    }
                    // now do something with the String.
                } else {
                    System.out.println("importData rejected: " + flavor);
                    // Don't return; try next flavor.
                }
            } catch (IOException ex) {
                System.err.println("IOError getting data: " + ex);
            } catch (UnsupportedFlavorException e) {
                System.err.println("Unsupported Flavor: " + e);
            }
        }
        // If you get here, I didn't like the flavor.
        Toolkit.getDefaultToolkit().beep();
        return false;
    }
}
