/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis.gui;

import com.vividsolutions.jts.geom.Geometry;

import edu.umd.cs.piccolox.event.PNotification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.features.FeatureCollectionEvent;
import de.cismet.cismap.commons.features.FeatureCollectionListener;
import de.cismet.cismap.commons.features.PureNewFeature;

import de.cismet.cismap.navigatorplugin.CidsFeature;

import de.cismet.verdis.CidsAppBackend;
import de.cismet.verdis.FeatureAttacher;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public abstract class AbstractCidsBeanWithGeometryTable extends AbstractCidsBeanTable
        implements FeatureCollectionListener,
            FeatureAttacher {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            AbstractCidsBeanWithGeometryTable.class);

    //~ Instance fields --------------------------------------------------------

    private final HashMap<CidsBean, CidsFeature> featureMap = new HashMap<>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AbstractCidsBeanWithGeometryTable object.
     *
     * @param  modus  DOCUMENT ME!
     * @param  model  DOCUMENT ME!
     */
    public AbstractCidsBeanWithGeometryTable(final CidsAppBackend.Mode modus, final AbstractCidsBeanTableModel model) {
        super(modus, model);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  notification  DOCUMENT ME!
     */
    public abstract void attachFeatureRequested(final PNotification notification);

    @Override
    public void addNewBean() {
        try {
            final CidsBean newBean = createNewBean();
            if (newBean != null) {
                addBean(newBean);
                final CidsFeature feature = featureMap.get(newBean);
                if (feature != null) {
                    Main.getMappingComponent().getFeatureCollection().select(feature);
                }
            }
        } catch (final Exception ex) {
            LOG.error("error while creating new bean", ex);
        }
    }

    @Override
    public void removeBean(final CidsBean cidsBean) {
        if (cidsBean != null) {
            try {
                getModel().removeCidsBean(cidsBean);
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
    }

    @Override
    public void featureCollectionChanged() {
    }

    @Override
    public void featureReconsiderationRequested(final FeatureCollectionEvent fce) {
    }

    @Override
    public void featureSelectionChanged(final FeatureCollectionEvent fce) {
        if (CidsAppBackend.getInstance().getMode().equals(getModus())) {
            getSelectionModel().removeListSelectionListener(this);

            try {
                final Collection<Feature> selectedFeatures = CidsAppBackend.getInstance()
                            .getMainMap()
                            .getFeatureCollection()
                            .getSelectedFeatures(); // fce.getEventFeatures();

                final Collection<Feature> selectedFeaturesWithoutPostgis = new ArrayList<>();
                for (final Feature feature : selectedFeatures) {
                    if ((feature instanceof CidsFeature) || (feature instanceof PureNewFeature)
                                || (feature instanceof KartenPanel.AnnotationFeature)) {
                        selectedFeaturesWithoutPostgis.add(feature);
                    }
                }

                // do not change selection, if only one CidsBean is selected in the table, which has no feature
                // and also if no feature or one feature, without a CidsBean, is selected in the map
                if ((getSelectedRowCount() == 1)
                            && (selectedFeaturesWithoutPostgis.isEmpty()
                                || isSelectedFeatureNotBound(selectedFeaturesWithoutPostgis))) {
                    final int viewIndex = getSelectedRow();
                    final int modelIndex = convertRowIndexToModel(viewIndex);
                    final CidsBean cidsBean = getModel().getCidsBeanByIndex(modelIndex);
                    if (getGeometry(cidsBean) != null) {
                        featureSelectionChanged_helper(selectedFeaturesWithoutPostgis);
                    } else {
                        // do nothing, to keep the selection
                    }
                } else {
                    featureSelectionChanged_helper(selectedFeaturesWithoutPostgis);
                }
            } finally {
                SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            getSelectionModel().removeListSelectionListener(AbstractCidsBeanWithGeometryTable.this);
                            getSelectionModel().addListSelectionListener(AbstractCidsBeanWithGeometryTable.this);
                            Main.getInstance().selectionChanged();
                            repaint();
                        }
                    });
            }
        }
    }

    /**
     * return true if only one feature is selected in the map, and if that feature is not bound to a CidsBean otherwise
     * return false.
     *
     * @param   features  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean isSelectedFeatureNotBound(final Collection<Feature> features) {
        // check if only one feature
        if (features.size() != 1) {
            return false;
        }

        final Feature feature = (Feature)features.toArray()[0];

        if (feature instanceof PureNewFeature) {
            return true;
        } else if ((feature instanceof CidsFeature) && (((CidsFeature)feature).getMetaObject().getBean() == null)) {
            return true;
        } else {
            return false;
        }
    }

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
     * @param  selectedFeatures  DOCUMENT ME!
     */
    private void featureSelectionChanged_helper(final Collection<Feature> selectedFeatures) {
        clearSelection();

        KartenPanel.AnnotationFeature annotationFeature = null;

        for (final Feature selectedFeature : selectedFeatures) {
            if (selectedFeature instanceof CidsFeature) {
                final int index = getModel().getIndexByCidsBean(((CidsFeature)selectedFeature).getMetaObject()
                                .getBean());
                if (index >= 0) {
                    final int viewIndex = convertRowIndexToView(index);
                    getSelectionModel().addSelectionInterval(viewIndex, viewIndex);
                }
            } else if (selectedFeature instanceof KartenPanel.AnnotationFeature) {
                annotationFeature = (KartenPanel.AnnotationFeature)selectedFeature;
                break;
            }
        }

        if (annotationFeature != null) {
            getSelectedRowListener().setAnnotationGeoJsonFeature(annotationFeature.getGeoJsonFeature());
        } else {
            setDetailBeans(getSelectedBeans());
        }
    }

    @Override
    public void restoreBean(final CidsBean cidsBean) {
        super.restoreBean(cidsBean);
        try {
            final CidsFeature cidsFeature = createCidsFeature(cidsBean);
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
        super.addBean(cidsBean);

        if (getModel().getCidsBeans() != null) {
            final CidsFeature cidsFeature = createCidsFeature(cidsBean);
            Main.getMappingComponent().getFeatureCollection().addFeature(cidsFeature);
        }
    }

    @Override
    public void valueChanged(final ListSelectionEvent ev) {
        super.valueChanged(ev);
        if (!((ev == null) || ev.getValueIsAdjusting())) {
            final int[] selection = getSelectedRows();
            final int[] modelSelection = new int[selection.length];
            final ArrayList<Feature> selectedFeatures = new ArrayList<Feature>(selection.length);

            for (int index = 0; index < selection.length; ++index) {
                modelSelection[index] = convertRowIndexToModel(selection[index]);
                final CidsBean cb = getModel().getCidsBeanByIndex(modelSelection[index]);
                final CidsFeature cidsFeature = createCidsFeature(cb);
                selectedFeatures.add(cidsFeature);
            }

            // Kartenselektion
            CidsAppBackend.getInstance().getMainMap().getFeatureCollection().removeFeatureCollectionListener(this);
            CidsAppBackend.getInstance().getMainMap().getFeatureCollection().select(selectedFeatures);
            CidsAppBackend.getInstance().getMainMap().getFeatureCollection().addFeatureCollectionListener(this);
        }
    }

    @Override
    public void featuresAdded(final FeatureCollectionEvent fce) {
    }

    @Override
    public void featuresChanged(final FeatureCollectionEvent fce) {
        if (CidsAppBackend.getInstance().getMode().equals(getModus())) {
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
    }

    @Override
    public void requestFeatureAttach(final Feature feature) {
        if (getSelectedRowCount() == 1) {
            try {
                final Geometry geom = feature.getGeometry();
                final int selection = getSelectedRow();
                final int modelSelection = convertRowIndexToModel(selection);
                final CidsBean selectedBean = getModel().getCidsBeanByIndex(modelSelection);
                setGeometry(geom, selectedBean);
                setDetailBean(selectedBean);
                CidsAppBackend.getInstance().getMainMap().getFeatureCollection().removeFeature(feature);
            } catch (Exception exception) {
                LOG.error("error when trying to attach new feature to existing bean", exception);
            }
        }
    }

    @Override
    public void setCidsBeans(final List<CidsBean> cidsBeans) {
        featureMap.clear();
        super.setCidsBeans(cidsBeans);
    }
}
