/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis.gui;

import com.vividsolutions.jts.geom.Geometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.features.Feature;

import de.cismet.cismap.navigatorplugin.CidsFeature;

import de.cismet.verdis.CidsAppBackend;

import de.cismet.verdis.commons.constants.GeomPropertyConstants;
import de.cismet.verdis.commons.constants.KassenzeichenGeometriePropertyConstants;
import de.cismet.verdis.commons.constants.KassenzeichenPropertyConstants;

import de.cismet.verdis.interfaces.CidsBeanComponent;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class KassenzeichenGeometrienList extends JList<CidsBean> implements CidsBeanComponent {

    //~ Static fields/initializers ---------------------------------------------

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            KassenzeichenGeometrienList.class);

    //~ Instance fields --------------------------------------------------------

    // private CidsBean kassenzeichen;
    private KassenzeichenGeometrienPanel panel;

    //~ Methods ----------------------------------------------------------------

    @Override
    public void addBean(final CidsBean cidsBean) {
        addKassenzeichenGeometrieBean(cidsBean);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kassenzeichenGeometrieBeanToAdd  DOCUMENT ME!
     */
    public void addKassenzeichenGeometrieBean(final CidsBean kassenzeichenGeometrieBeanToAdd) {
        if (kassenzeichenGeometrieBeanToAdd != null) {
            final Collection<CidsBean> kassenzeichenGeometrieBeanToAdds = new ArrayList<CidsBean>();
            kassenzeichenGeometrieBeanToAdds.add(kassenzeichenGeometrieBeanToAdd);
            addKassenzeichenGeometrieBeans(kassenzeichenGeometrieBeanToAdds);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kassenzeichenGeometrieBeansToAdd  DOCUMENT ME!
     */
    public void addKassenzeichenGeometrieBeans(final Collection<CidsBean> kassenzeichenGeometrieBeansToAdd) {
        final CidsBean kassenzBean = panel.getCidsBean();
        if (kassenzBean != null) {
            final Collection<Feature> featuresToSelect = new ArrayList<Feature>();
            final Collection<Feature> featuresToAdd = new ArrayList<Feature>();
            final Collection<CidsBean> beansToAdd = new ArrayList<CidsBean>();

            for (final CidsBean kassenzeichenGeometrieBeanToAdd : kassenzeichenGeometrieBeansToAdd) {
                final Geometry kassenzeichenGeometrieGeomToAdd = (Geometry)kassenzeichenGeometrieBeanToAdd.getProperty(
                        KassenzeichenGeometriePropertyConstants.PROP__GEOMETRIE
                                + "."
                                + GeomPropertyConstants.PROP__GEO_FIELD);
                final Collection<CidsBean> kassenzeichenGeometrieBeans = kassenzBean.getBeanCollectionProperty(
                        KassenzeichenPropertyConstants.PROP__KASSENZEICHEN_GEOMETRIEN);
                CidsFeature geomAlreadyInMapFeature = null;
                for (final CidsBean kassenzeichenGeometrieBean : kassenzeichenGeometrieBeans) {
                    final Geometry kassenzeichenGeometrieGeom = (Geometry)kassenzeichenGeometrieBean.getProperty(
                            KassenzeichenGeometriePropertyConstants.PROP__GEOMETRIE
                                    + "."
                                    + GeomPropertyConstants.PROP__GEO_FIELD);
                    if (kassenzeichenGeometrieGeomToAdd.equals(kassenzeichenGeometrieGeom)) {
                        geomAlreadyInMapFeature = new CidsFeature(
                                kassenzeichenGeometrieBean.getMetaObject());
                        break;
                    }
                }

                if (geomAlreadyInMapFeature == null) {
                    beansToAdd.add(kassenzeichenGeometrieBeanToAdd);

                    final CidsFeature cidsFeatureToAdd = new CidsFeature(
                            kassenzeichenGeometrieBeanToAdd.getMetaObject());
                    cidsFeatureToAdd.setEditable(CidsAppBackend.getInstance().isEditable());
                    featuresToAdd.add(cidsFeatureToAdd);
                    featuresToSelect.add(cidsFeatureToAdd);
                } else {
                    featuresToSelect.add(geomAlreadyInMapFeature);
                }
            }

            kassenzBean.getBeanCollectionProperty(KassenzeichenPropertyConstants.PROP__KASSENZEICHEN_GEOMETRIEN)
                    .addAll(beansToAdd);
            Main.getMappingComponent().getFeatureCollection().addFeatures(featuresToAdd);
            Main.getMappingComponent().getFeatureCollection().select(featuresToSelect);
        }
    }

    @Override
    public List<CidsBean> getSelectedBeans() {
        final List<CidsBean> cidsBeans = new ArrayList<CidsBean>();

        final int[] selectedIndices = getSelectedIndices();

        for (final int index : selectedIndices) {
            cidsBeans.add(getModel().getElementAt(index));
        }

        return cidsBeans;
    }

    @Override
    public void removeBean(final CidsBean cidsBean) {
        final Collection<CidsBean> geos = (Collection<CidsBean>)panel.getCidsBean()
                    .getProperty(
                            KassenzeichenPropertyConstants.PROP__KASSENZEICHEN_GEOMETRIEN);
        geos.remove(cidsBean);
    }

    @Override
    public List<CidsBean> getAllBeans() {
        final List<CidsBean> cidsBeans = new ArrayList<CidsBean>();

        for (int i = 0; i < getModel().getSize(); i++) {
            cidsBeans.add(getModel().getElementAt(i));
        }

        return cidsBeans;
    }

//    /**
//     * DOCUMENT ME!
//     *
//     * @param  kassenzeichen  DOCUMENT ME!
//     */
//    public void setKassenzeichen(final CidsBean kassenzeichen) {
//        this.kassenzeichen = kassenzeichen;
//    }
//
//    /**
//     * DOCUMENT ME!
//     *
//     * @return  DOCUMENT ME!
//     */
//    public CidsBean getKassenzeichen() {
//        return kassenzeichen;
//    }

    /**
     * DOCUMENT ME!
     *
     * @param  panel  DOCUMENT ME!
     */
    public void setPanel(final KassenzeichenGeometrienPanel panel) {
        this.panel = panel;
    }
}
