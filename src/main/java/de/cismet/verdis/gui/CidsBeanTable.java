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

import java.util.List;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;

import de.cismet.validation.Validatable;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public interface CidsBeanTable extends Validatable, CidsBeanComponent, CidsBeanStore {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    void addNewBean();

    /**
     * DOCUMENT ME!
     */
    void removeSelectedBeans();

    /**
     * DOCUMENT ME!
     */
    void restoreSelectedBeans();

    /**
     * DOCUMENT ME!
     *
     * @param  selectedRowListener  cidsBean DOCUMENT ME!
     */
    // void addBean(final CidsBean cidsBean);

    /**
     * DOCUMENT ME!
     *
     * @param  selectedRowListener  cidsBean DOCUMENT ME!
     */
    // void removeBean(final CidsBean cidsBean);

    /**
     * DOCUMENT ME!
     *
     * @param  selectedRowListener  DOCUMENT ME!
     */
    void setSelectedRowListener(final AbstractCidsBeanDetailsPanel selectedRowListener);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    AbstractCidsBeanDetailsPanel getSelectedRowListener();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    // List<CidsBean> getAllBeans();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    // List<CidsBean> getSelectedBeans();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    AbstractCidsBeanTable getTableHelper();

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBeans  DOCUMENT ME!
     */
    void setCidsBeans(final List<CidsBean> cidsBeans);

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    void selectCidsBean(final CidsBean cidsBean);
}
