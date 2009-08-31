/*
 * DropFileTransferHandler.java
 * Copyright (C) 2005 by:
 *
 *----------------------------
 * cismet GmbH
 * Goebenstrasse 40
 * 66117 Saarbruecken
 * http://www.cismet.de
 *----------------------------
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *----------------------------
 * Author:
 * thorsten.hell@cismet.de
 *----------------------------
 *
 * Created on 8. Februar 2006, 16:12
 *
 */

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
 *
 * @author thorsten.hell@cismet.de
 */
public class DropFileTransferHandler extends TransferHandler{
  public boolean canImport(JComponent arg0, DataFlavor[] arg1) {
    for (int i = 0; i < arg1.length; i++) {
      DataFlavor flavor = arg1[i];
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
   * @see javax.swing.TransferHandler#importData(javax.swing.JComponent,
   *      java.awt.datatransfer.Transferable)
   */
  public boolean importData(JComponent comp, Transferable t) {
    DataFlavor[] flavors = t.getTransferDataFlavors();
    System.out.println("Trying to import:" + t);
    System.out.println("... which has " + flavors.length + " flavors.");
    for (int i = 0; i < flavors.length; i++) {
      DataFlavor flavor = flavors[i];
      try {
        if (flavor.equals(DataFlavor.javaFileListFlavor)) {
          System.out.println("importData: FileListFlavor");

          List l = (List) t
              .getTransferData(DataFlavor.javaFileListFlavor);
          Iterator iter = l.iterator();
          while (iter.hasNext()) {
            File file = (File) iter.next();
            System.out.println("GOT FILE: "
                + file.getCanonicalPath());
            // Now do something with the file...
          }
          return true;
        } else if (flavor.equals(DataFlavor.stringFlavor)) {
          System.out.println("importData: String Flavor");
          String fileOrURL = (String) t.getTransferData(flavor);
          System.out.println("GOT STRING: " + fileOrURL);
          try {
            URL url = new URL(fileOrURL);
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
