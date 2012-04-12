/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 *  Copyright (C) 2011 jruiz
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.cismet.verdis.gui;

import Sirius.server.middleware.types.HistoryObject;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class HistoryComboBoxModel implements ComboBoxModel {

    //~ Instance fields --------------------------------------------------------

    private HistoryObject[] historyObjects;
    private Object selectedObject;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new HistoryComboBoxModel object.
     *
     * @param  historyObjects  DOCUMENT ME!
     */
    public HistoryComboBoxModel(HistoryObject[] historyObjects) {
        if (historyObjects == null) {
            historyObjects = new HistoryObject[0];
        }
        this.historyObjects = historyObjects;
        this.selectedObject = getElementAt(0);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void setSelectedItem(final Object o) {
        this.selectedObject = o;
    }

    @Override
    public Object getSelectedItem() {
        return selectedObject;
    }

    @Override
    public int getSize() {
        return historyObjects.length + 1;
    }

    @Override
    public final Object getElementAt(final int i) {
        if (i == 0) {
            return "Datum auswählen";
        }
        return historyObjects[i - 1];
    }

    @Override
    public void addListDataListener(final ListDataListener ll) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeListDataListener(final ListDataListener ll) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }
}
