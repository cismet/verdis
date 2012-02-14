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
package de.cismet.verdis.interfaces;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;
import de.cismet.validation.Validatable;
import de.cismet.verdis.gui.AbstractCidsBeanTable;
import java.util.List;

/**
 *
 * @author jruiz
 */
public interface CidsBeanTable extends Validatable {

    public void addNewBean();

    public void removeSelectedBeans();

    public void restoreSelectedBeans();

    public void addBean(final CidsBean cidsBean);

    public void removeBean(final CidsBean cidsBean);

    public void setSelectedRowListener(final CidsBeanStore selectedRowListener);

    public CidsBeanStore getSelectedRowListener();

    public List<CidsBean> getAllBeans();

    public List<CidsBean> getSelectedBeans();

    public AbstractCidsBeanTable getTableHelper();

    public void setCidsBeans(final List<CidsBean> cidsBeans);

    public void selectCidsBean(final CidsBean cidsBean);

}
