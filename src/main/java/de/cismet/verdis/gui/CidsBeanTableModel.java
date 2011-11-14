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
import de.cismet.cids.dynamics.CidsBeanStore;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author jruiz
 */
public abstract class CidsBeanTableModel extends AbstractTableModel implements CidsBeanStore {

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CidsBeanTableModel.class);

    public abstract String getBeansProperty();

    public List<CidsBean> getCidsBeans() {
        String prop = getBeansProperty();
        if ((kassenzeichenBean != null) && (kassenzeichenBean.getProperty(prop) instanceof List)) {
            return (List)kassenzeichenBean.getProperty(prop);
        } else {
            return new ArrayList<CidsBean>();
        }
    }

    private CidsBean kassenzeichenBean;

    @Override
    public CidsBean getCidsBean() {
        return kassenzeichenBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        kassenzeichenBean = cidsBean;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return getCidsBeans().size();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   modelIndex  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getCidsBeanByIndex(final int modelIndex) {
        try {
            return (CidsBean) getCidsBeans().get(modelIndex);
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
        try {
            return getCidsBeans().indexOf(cidsBean);
        } catch (Exception e) {
            LOG.error("error in getIndexByCidsBean(). will return -1", e);
            return -1;
        }
    }

    public void addCidsBean(CidsBean cidsBean) {
        getCidsBeans().add(cidsBean);
        fireTableDataChanged();
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
