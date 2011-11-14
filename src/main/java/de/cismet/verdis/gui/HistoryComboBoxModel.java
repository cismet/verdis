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
 *
 * @author jruiz
 */
public class HistoryComboBoxModel implements ComboBoxModel {

    private HistoryObject[] historyObjects;
    private Object selectedObject;

    public HistoryComboBoxModel(HistoryObject[] historyObjects) {
        if (historyObjects == null) {
            historyObjects = new HistoryObject[0];
        }
        this.historyObjects = historyObjects;
        this.selectedObject = getElementAt(0);
    }

    @Override
    public void setSelectedItem(Object o) {
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
    public final Object getElementAt(int i) {
        if (i == 0) {
            return "Datum auswählen";
        }
        return historyObjects[i - 1];
    }

    @Override
    public void addListDataListener(ListDataListener ll) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeListDataListener(ListDataListener ll) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

}
