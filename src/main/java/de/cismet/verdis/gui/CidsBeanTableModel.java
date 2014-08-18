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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import de.cismet.cids.custom.util.CidsBeanSupport;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public abstract class CidsBeanTableModel extends AbstractTableModel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            CidsBeanTableModel.class);

    //~ Instance fields --------------------------------------------------------

    private List<CidsBean> cidsBeans;
    private final String[] columnNames;
    private final Class[] columnClasses;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CidsBeanTableModel object.
     *
     * @param  columnNames    DOCUMENT ME!
     * @param  columnClasses  DOCUMENT ME!
     */
    protected CidsBeanTableModel(final String[] columnNames, final Class[] columnClasses) {
        this.columnNames = columnNames;
        this.columnClasses = columnClasses;
    }

    //~ Methods ----------------------------------------------------------------

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

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<CidsBean> getCidsBeans() {
        return cidsBeans;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBeans  DOCUMENT ME!
     */
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
     * @param   modelIndices  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<CidsBean> getCidsBeansByIndices(final int[] modelIndices) {
        final Collection<CidsBean> cidsBeans = new ArrayList<CidsBean>();
        for (int i = 0; i < modelIndices.length; i++) {
            cidsBeans.add(getCidsBeanByIndex(modelIndices[i]));
        }
        return cidsBeans;
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
            return (CidsBean)cidsBeans.get(modelIndex);
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("CidsBean at index " + modelIndex + " not found. will return null", e);
            }
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

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    public void addCidsBean(final CidsBean cidsBean) {
        if (cidsBeans != null) {
            cidsBeans.add(cidsBean);
            fireTableDataChanged();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    public void removeCidsBean(final CidsBean cidsBean) {
        try {
            cidsBean.delete();
            fireTableDataChanged();
        } catch (Exception ex) {
            LOG.error("error while deleting bean", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   cidsBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public abstract CidsBean deepcloneBean(final CidsBean cidsBean) throws Exception;
}
