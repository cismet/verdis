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

import de.cismet.cids.dynamics.CidsBean;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author jruiz
 */
public abstract class CidsBeanTableModel extends AbstractTableModel {

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CidsBeanTableModel.class);

    private List<CidsBean> cidsBeans;
    private final String[] columnNames;
    private final Class[] columnClasses;

    protected CidsBeanTableModel(final String[] columnNames, final Class[] columnClasses) {
        this.columnNames = columnNames;
        this.columnClasses = columnClasses;
        
    }
    
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(final int column) {
        return columnNames[column];
    }

    @Override
    public Class getColumnClass(final int column) {
        return columnClasses[column];
    }    
            
    public List<CidsBean> getCidsBeans() {
        return cidsBeans;
    }

    public void setCidsBeans(final List<CidsBean> cidsBeans) {
        this.cidsBeans = cidsBeans;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        if (cidsBeans == null) {
            return 0;
        }
        return cidsBeans.size();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   modelIndex  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getCidsBeanByIndex(final int modelIndex) {
        if (cidsBeans == null) {
            return null;
        }
        try {
            return (CidsBean) cidsBeans.get(modelIndex);
        } catch (Exception e) {
            LOG.debug("CidsBean at index " + modelIndex + " not found. will return null", e);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   cidsBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getIndexByCidsBean(final CidsBean cidsBean) {
        if (cidsBeans == null) {
            return -1;
        }
        try {
            return cidsBeans.indexOf(cidsBean);
        } catch (Exception e) {
            LOG.error("error in getIndexByCidsBean(). will return -1", e);
            return -1;
        }
    }

    public void addCidsBean(CidsBean cidsBean) {
        if (cidsBeans != null) {
            cidsBeans.add(cidsBean);
            fireTableDataChanged();
        }
    }

    public void removeCidsBean(CidsBean cidsBean) {
        try {
            cidsBean.delete();
            fireTableDataChanged();
        } catch (Exception ex) {
            LOG.error("error while deleting bean", ex);
        }
    }
}
