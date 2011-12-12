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

import com.vividsolutions.jts.geom.Geometry;
import de.cismet.cids.custom.util.CidsBeanTableHelper;
import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;
import de.cismet.cismap.commons.features.FeatureCollectionListener;
import de.cismet.validation.Validatable;
import de.cismet.validation.Validator;
import de.cismet.verdis.FeatureAttacher;
import java.util.List;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.swingx.JXTable;

/**
 *
 * @author jruiz
 */
public interface CidsBeanTable extends FeatureCollectionListener, ListSelectionListener, CidsBeanStore, FeatureAttacher, Validatable {

    public void addNewBean();

    public void removeSelectedBeans();

    public void restoreSelectedBeans();

    public void addBean(final CidsBean cidsBean);

    public void removeBean(final CidsBean cidsBean);

    public void setSelectedRowListener(final CidsBeanStore selectedRowListener);

    public CidsBeanStore getSelectedRowListener();

    public CidsBean createNewBean() throws Exception;

    public void setGeometry(final Geometry geometry, final CidsBean cidsBean) throws Exception;

    public Geometry getGeometry(final CidsBean cidsBean);

    public List<CidsBean> getAllBeans();

    public List<CidsBean> getSelectedBeans();

    public CidsBeanTableHelper getTableHelper();

    public JXTable getJXTable();

    public void selectCidsBean(final CidsBean cidsBean);

    public Validator getItemValidator(final CidsBean cidsBean);
}
