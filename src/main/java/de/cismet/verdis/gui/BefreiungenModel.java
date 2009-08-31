/*
 * BefreiungenModel.java
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
 * Created on 13. April 2006, 14:09
 *
 */

package de.cismet.verdis.gui;

import de.cismet.verdis.data.BefreiungErlaubnis;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author thorsten.hell@cismet.de
 */
public class BefreiungenModel extends DefaultTableModel {
    Vector<BefreiungErlaubnis> data=new Vector<BefreiungErlaubnis>();
    public BefreiungenModel() {
        
    }
    public BefreiungenModel(Vector<BefreiungErlaubnis> data) {
        this.data=data;
    }
    public int getColumnCount(){
        return 2;
    }
    public int getRowCount() {
        if (data!=null) {
            return data.size();
        } else {
            return 0;
        }
        
    }
    public String getColumnName(int column) {
        if (column==0) {
            return "Aktenzeichen";
        } else {
            return "g\u00FCltig bis";
        }
    }
    public Object getValueAt(int row,int column) {
        BefreiungErlaubnis be=data.get(row);
        if (column==0) {
            return be.getAktenzeichen();
        } else {
            return be.getGueltigBis();
        }
    }
    public void setValueAt(Object value, int row, int column)  {
        BefreiungErlaubnis be=data.get(row);
        if (column==0) {
            be.setAktenzeichen(value.toString());
        } else {
            be.setGueltigBis(value.toString());
        }
    }
    public void addRow() {
        data.add(new BefreiungErlaubnis());
        fireTableDataChanged();
    }
    public void removeAll() {
        data=new Vector<BefreiungErlaubnis>();
        fireTableDataChanged();
    }
}
