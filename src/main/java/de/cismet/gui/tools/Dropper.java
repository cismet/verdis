/*
 * Dropper.java
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
 * Created on 8. Februar 2006, 16:04
 *
 */

package de.cismet.gui.tools;

/*
 * Copyright (c) Ian F. Darwin, http://www.darwinsys.com/, 1996-2002.
 * All rights reserved. Software written by Ian F. Darwin and others.
 * $Id: Dropper.java,v 1.1.1.1 2009-08-31 08:29:42 spuhl Exp $
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * Java, the Duke mascot, and all variants of Sun's Java "steaming coffee
 * cup" logo are trademarks of Sun Microsystems. Sun's, and James Gosling's,
 * pioneering role in inventing and promulgating (and standardizing) the Java 
 * language and environment is gratefully acknowledged.
 * 
 * The pioneering role of Dennis Ritchie and Bjarne Stroustrup, of AT&T, for
 * inventing predecessor languages C and C++ is also gratefully acknowledged.
 */

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
 * Dropper - show File Drop Target from Drag-n-Drop
 * 
 * @version $Id: Dropper.java,v 1.1.1.1 2009-08-31 08:29:42 spuhl Exp $
 */
public class Dropper extends JFrame {

  /**
   * Construct trivial GUI and connect a TransferHandler to it.
   */
  public Dropper() {
    super("Drop Target");

    JComponent cp = (JComponent) getContentPane();
    cp.setTransferHandler(new MyFileTransferHandler()); // see below

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(150, 150);
  }

  /** Instantiate and show the GUI */
  public static void main(String[] args) {
    new Dropper().setVisible(true);
  }
}

/** Non-public class to handle filename drops */

class MyFileTransferHandler extends TransferHandler {

  /**
   * @see javax.swing.TransferHandler#canImport(javax.swing.JComponent,
   *      java.awt.datatransfer.DataFlavor[])
   */
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
