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

import com.vividsolutions.jts.geom.Geometry;

import org.jdesktop.swingx.JXTable;

import java.util.*;

import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.cismet.cids.custom.util.CidsBeanSupport;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;

import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.features.FeatureCollectionEvent;
import de.cismet.cismap.commons.features.FeatureCollectionListener;

import de.cismet.cismap.navigatorplugin.CidsFeature;

import de.cismet.validation.Validator;

import de.cismet.validation.validator.AggregatedValidator;

import de.cismet.verdis.CidsAppBackend;
import de.cismet.verdis.FeatureAttacher;

import de.cismet.verdis.interfaces.CidsBeanTable;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public abstract class AbstractCidsBeanTable extends JPanel implements CidsBeanTable,
    FeatureCollectionListener,
    ListSelectionListener,
    FeatureAttacher {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AbstractCidsBeanTable.class);

    //~ Instance fields --------------------------------------------------------

    private final Map<Integer, CidsBean> beanBackups = new HashMap<Integer, CidsBean>();
    private final AggregatedValidator aggVal = new AggregatedValidator();
    private final HashMap<CidsBean, Validator> beanToValidatorMap = new HashMap<CidsBean, Validator>();
    private final HashMap<CidsBean, CidsFeature> featureMap = new HashMap<CidsBean, CidsFeature>();
    private final JXTable jxTable = new JXTable();
    private final CidsAppBackend.Mode modus;
    private final CidsBeanTableModel model;

    private CidsBeanStore selectedRowListener = null;
    private int newBeanId = 0;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AbstractCidsBeanTable object.
     *
     * @param  modus  DOCUMENT ME!
     * @param  model  DOCUMENT ME!
     */
    public AbstractCidsBeanTable(final CidsAppBackend.Mode modus, final CidsBeanTableModel model) {
        this.modus = modus;
        this.model = model;

        init();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void init() {
        jxTable.getSelectionModel().addListSelectionListener(this);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getNextNewBeanId() {
        return --newBeanId;
    }

    @Override
    public void addNewBean() {
        try {
            final CidsBean newBean = createNewBean();
            addBean(newBean);
            Main.getMappingComponent().getFeatureCollection().select(new CidsFeature(newBean.getMetaObject()));
        } catch (final Exception ex) {
            LOG.error("error while creating new bean", ex);
        }
    }

    @Override
    public void removeSelectedBeans() {
        for (final CidsBean cidsBean : getSelectedBeans()) {
            removeBean(cidsBean);
            aggVal.remove(beanToValidatorMap.get(cidsBean));
            beanToValidatorMap.remove(cidsBean);
        }
    }

    @Override
    public void restoreSelectedBeans() {
        final Collection<CidsBean> cidsBeans = getSelectedBeans();
        for (final CidsBean cidsBean : cidsBeans) {
            restoreBean(cidsBean);
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void clearBackups() {
        beanBackups.clear();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    public void backupBean(final CidsBean cidsBean) {
        try {
            final int id = (Integer)cidsBean.getProperty("id");
            final CidsBean backupBean = CidsBeanSupport.cloneCidsBean(cidsBean);
            beanBackups.put(id, backupBean);
        } catch (Exception ex) {
            LOG.error("error while making backup of bean", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    public void unbackupBean(final CidsBean cidsBean) {
        beanBackups.remove((Integer)cidsBean.getProperty("id"));
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    public void restoreBean(final CidsBean cidsBean) {
        try {
            final CidsFeature cidsFeature = createCidsFeature(cidsBean);

            final CidsBean backupBean = beanBackups.get((Integer)cidsBean.getProperty("id"));
            CidsBeanSupport.copyProperties(backupBean, cidsBean);

            if (cidsFeature != null) {
                Main.getMappingComponent().getFeatureCollection().removeFeature(cidsFeature);
                final CidsFeature backupFeature = createCidsFeature(cidsBean);
                Main.getMappingComponent().getFeatureCollection().addFeature(backupFeature);
            }
        } catch (Exception ex) {
            LOG.error("error while making backup of bean", ex);
        }
    }

    @Override
    public void addBean(final CidsBean cidsBean) {
        if (model.getCidsBeans() != null) {
            backupBean(cidsBean);
            model.addCidsBean(cidsBean);
            final Validator validator = getItemValidator(cidsBean);
            beanToValidatorMap.put(cidsBean, validator);
            aggVal.add(validator);

            final CidsFeature cidsFeature = createCidsFeature(cidsBean);

            Main.getMappingComponent().getFeatureCollection().addFeature(cidsFeature);
        }
    }

    @Override
    public void removeBean(final CidsBean cidsBean) {
        if (cidsBean != null) {
            try {
                getAllBeans().remove(cidsBean);
                model.removeCidsBean(cidsBean);
                unbackupBean(cidsBean);
                Main.getMappingComponent()
                        .getFeatureCollection()
                        .removeFeature(new CidsFeature(cidsBean.getMetaObject()));
            } catch (Exception ex) {
                LOG.error("error while removing bean", ex);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   cidsBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsFeature createCidsFeature(final CidsBean cidsBean) {
        if (cidsBean == null) {
            return null;
        }
        final CidsFeature cidsFeature = new CidsFeature(cidsBean.getMetaObject());
        cidsFeature.setEditable(CidsAppBackend.getInstance().isEditable());
        featureMap.put(cidsBean, cidsFeature);
        return cidsFeature;
    }

    @Override
    public void allFeaturesRemoved(final FeatureCollectionEvent fce) {
        if (CidsAppBackend.getInstance().getMode().equals(modus)) {
        }
    }

    @Override
    public void featureCollectionChanged() {
        if (CidsAppBackend.getInstance().getMode().equals(modus)) {
        }
    }

    @Override
    public void featureReconsiderationRequested(final FeatureCollectionEvent fce) {
        if (CidsAppBackend.getInstance().getMode().equals(modus)) {
        }
    }

    @Override
    public void featureSelectionChanged(final FeatureCollectionEvent fce) {
        if (CidsAppBackend.getInstance().getMode().equals(modus)) {
            getJXTable().getSelectionModel().removeListSelectionListener(this);
            getJXTable().getSelectionModel().clearSelection();

            final Collection<Feature> selectedFeatures = CidsAppBackend.getInstance()
                        .getMainMap()
                        .getFeatureCollection()
                        .getSelectedFeatures(); // fce.getEventFeatures();
            final Collection<Feature> selectedFlaechenFeatures = new ArrayList<Feature>();

            for (final Feature selectedFeature : selectedFeatures) {
                if (selectedFeature instanceof CidsFeature) {
                    final int index = model.getIndexByCidsBean(((CidsFeature)selectedFeature).getMetaObject()
                                    .getBean());
                    final int viewIndex = getJXTable().convertRowIndexToView(index);
                    getJXTable().getSelectionModel().addSelectionInterval(viewIndex, viewIndex);
                    selectedFlaechenFeatures.add(selectedFeature);
                }
            }

            if (selectedFlaechenFeatures.size() == 1) {
                final CidsFeature feature = (CidsFeature)selectedFlaechenFeatures.toArray()[0];
                setDetailBean(feature.getMetaObject().getBean());
            } else {
                setDetailBean(null);
            }

            getJXTable().getSelectionModel().addListSelectionListener(this);
            Main.getCurrentInstance().selectionChanged();
        }
    }

    @Override
    public void featuresAdded(final FeatureCollectionEvent fce) {
        if (CidsAppBackend.getInstance().getMode().equals(modus)) {
        }
    }

    @Override
    public void featuresChanged(final FeatureCollectionEvent fce) {
        if (CidsAppBackend.getInstance().getMode().equals(modus)) {
            final Collection<Feature> features = fce.getEventFeatures();
            for (final Feature feature : features) {
                if ((feature instanceof CidsFeature) && featureMap.containsValue((CidsFeature)feature)) {
                    final CidsBean cidsBean = ((CidsFeature)feature).getMetaObject().getBean();
                    try {
                        setGeometry(feature.getGeometry(), cidsBean);
                    } catch (Exception ex) {
                        LOG.error("error while updating geometry", ex);
                    }
                }
            }
        }
    }

    @Override
    public void featuresRemoved(final FeatureCollectionEvent fce) {
        if (CidsAppBackend.getInstance().getMode().equals(modus)) {
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public CidsBeanStore getSelectedRowListener() {
        return selectedRowListener;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  selectedRowListener  DOCUMENT ME!
     */
    @Override
    public void setSelectedRowListener(final CidsBeanStore selectedRowListener) {
        this.selectedRowListener = selectedRowListener;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cb  DOCUMENT ME!
     */
    private void setDetailBean(final CidsBean cb) {
        if (selectedRowListener != null) {
            selectedRowListener.setCidsBean(cb);
        }
    }

    @Override
    public void valueChanged(final ListSelectionEvent ev) {
        if (!((ev == null) || ev.getValueIsAdjusting())) {
            final int[] selection = getJXTable().getSelectedRows();
            final int[] modelSelection = new int[selection.length];
            final ArrayList<Feature> selectedFeatures = new ArrayList<Feature>(selection.length);

            for (int index = 0; index < selection.length; ++index) {
                modelSelection[index] = getJXTable().convertRowIndexToModel(selection[index]);
                final CidsBean cb = model.getCidsBeanByIndex(modelSelection[index]);
                final CidsFeature cidsFeature = createCidsFeature(cb);
                selectedFeatures.add(cidsFeature);
            }

            // Kartenselektion
            CidsAppBackend.getInstance().getMainMap().getFeatureCollection().removeFeatureCollectionListener(this);
            CidsAppBackend.getInstance().getMainMap().getFeatureCollection().select(selectedFeatures);
            CidsAppBackend.getInstance().getMainMap().getFeatureCollection().addFeatureCollectionListener(this);

            // DetailPanel --> nur setzen wenn # selektierte zeilen==1
            if (getJXTable().getSelectedRowCount() == 1) {
                setDetailBean(model.getCidsBeanByIndex(modelSelection[0]));
            } else {
                setDetailBean(null);
            }

            Main.getCurrentInstance().selectionChanged();
        }
    }

    @Override
    public void setCidsBeans(final List<CidsBean> cidsBeans) {
        model.setCidsBeans(cidsBeans);
        beanToValidatorMap.clear();
        aggVal.clear();
        clearBackups();
        if (cidsBeans != null) {
            for (final CidsBean tableBean : cidsBeans) {
                final Validator validator = getItemValidator(tableBean);
                beanToValidatorMap.put(tableBean, validator);
                aggVal.add(validator);
                backupBean(tableBean);
            }
        }
    }

    @Override
    public List<CidsBean> getSelectedBeans() {
        final List<CidsBean> cidsBeans = new ArrayList<CidsBean>();

        final int[] rows;
        if (getJXTable().getSelectedRowCount() <= 0) {
            return cidsBeans;
        } else if (getJXTable().getSelectedRowCount() == 1) {
            rows = new int[] { getJXTable().getSelectedRow() };
        } else {
            rows = getJXTable().getSelectedRows();
        }

        for (int i = 0; i < rows.length; ++i) {
            cidsBeans.add(model.getCidsBeanByIndex(getJXTable().getFilters().convertRowIndexToModel(rows[i])));
        }

        return cidsBeans;
    }

    @Override
    public void requestFeatureAttach(final Feature feature) {
        if (getJXTable().getSelectedRowCount() == 1) {
            try {
                final Geometry geom = feature.getGeometry();
                final int selection = getJXTable().getSelectedRow();
                final int modelSelection = getJXTable().convertRowIndexToModel(selection);
                final CidsBean selectedBean = model.getCidsBeanByIndex(modelSelection);
                setGeometry(geom, selectedBean);
                setDetailBean(selectedBean);
                CidsAppBackend.getInstance().getMainMap().getFeatureCollection().removeFeature(feature);
            } catch (Exception exception) {
                LOG.error("error when trying to attach new feature to existing bean", exception);
            }
        }
//
//        if (tableModel.getSelectedFlaeche().getGeom_id() < 0 && tableModel.getSelectedFlaeche().getGeometry() == null && tableModel.getSelectedFlaeche().isMarkedForDeletion()) {
//                    JOptionPane.showMessageDialog(mappingComp, "Dieser Fl\u00E4che kann im Moment keine Geometrie zugewiesen werden. Bitte zuerst speichern.");
//                } else if (tableModel.getSelectedBean().getGeom_id() < 0 && tableModel.getSelectedFlaeche().getGeometry() == null && !tableModel.getSelectedFlaeche().isMarkedForDeletion()) {
//                    Geometry g = pf.getFeature().getGeometry();
//                    mappingComp.getFeatureCollection().removeFeature(pf.getFeature());
//                    getSelectedBean().setGeometry(g);
//                    getSelectedBean().setGeometryRemoved(false);
//                    mappingComp.getFeatureCollection().addFeature(getSelectedBean());
//                    getSelectedBean().setGr_grafik(new Integer((int) (getSelectedBean().getGeometry().getArea())));
//                    getSelectedBean().setGr_korrektur(getSelectedBean().getGr_grafik());
//                    getSelectedBean().sync();
//                }
//                } else if (pf.getFeature() instanceof Flaeche) {
//                    JOptionPane.showMessageDialog(mainMap, "Es k\u00F6nnen nur nicht bereits zugeordnete Fl\u00E4chen zugeordnet werden.");
//                }
    }

    @Override
    public AbstractCidsBeanTable getTableHelper() {
        return this;
    }

    @Override
    public List<CidsBean> getAllBeans() {
        return model.getCidsBeans();
    }

    @Override
    public void selectCidsBean(final CidsBean cidsBean) {
        final int index = model.getIndexByCidsBean(cidsBean);
        if (index >= 0) {
            final int viewIndex = getJXTable().convertRowIndexToView(index);
            getJXTable().getSelectionModel().setSelectionInterval(viewIndex, viewIndex);
        }
    }

    @Override
    public final Validator getValidator() {
        return aggVal;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected final CidsBeanTableModel getModel() {
        return model;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected final JXTable getJXTable() {
        return jxTable;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public abstract CidsBean createNewBean() throws Exception;

    /**
     * DOCUMENT ME!
     *
     * @param   geometry  DOCUMENT ME!
     * @param   cidsBean  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public abstract void setGeometry(final Geometry geometry, final CidsBean cidsBean) throws Exception;

    /**
     * DOCUMENT ME!
     *
     * @param   cidsBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public abstract Geometry getGeometry(final CidsBean cidsBean);

    /**
     * DOCUMENT ME!
     *
     * @param   cidsBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public abstract Validator getItemValidator(final CidsBean cidsBean);
}
