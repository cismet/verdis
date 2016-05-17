/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 *  Copyright (C) 2010 thorsten
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
package de.cismet.verdis.gui.befreiungerlaubnis_geometrie;

import Sirius.server.middleware.types.MetaObject;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

import edu.umd.cs.piccolox.event.PNotification;

import org.jdesktop.swingx.decorator.*;

import java.awt.Color;
import java.awt.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SortOrder;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.features.FeatureCollectionEvent;
import de.cismet.cismap.commons.features.PureNewFeature;
import de.cismet.cismap.commons.gui.piccolo.PFeature;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.AttachFeatureListener;

import de.cismet.cismap.navigatorplugin.CidsFeature;

import de.cismet.tools.NumberStringComparator;

import de.cismet.tools.gui.StaticSwingTools;

import de.cismet.validation.Validator;

import de.cismet.validation.validator.AggregatedValidator;

import de.cismet.verdis.CidsAppBackend;

import de.cismet.verdis.commons.constants.*;

import de.cismet.verdis.gui.AbstractCidsBeanWithGeometryTable;
import de.cismet.verdis.gui.Main;
import de.cismet.verdis.gui.befreiungerlaubnis.BefreiungerlaubnisTable;

import static de.cismet.verdis.gui.AbstractCidsBeanTable.getNextNewBeanId;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class BefreiungerlaubnisGeometrieTable extends AbstractCidsBeanWithGeometryTable {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            BefreiungerlaubnisGeometrieTable.class);

    //~ Instance fields --------------------------------------------------------

    private CidsBean cidsBean;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BefreiungerlaubnisGeometrieTable object.
     */
    public BefreiungerlaubnisGeometrieTable() {
        super(CidsAppBackend.Mode.KANALDATEN, new BefreiungerlaubnisGeometrieTableModel());

        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        final HighlightPredicate noGeometryPredicate = new HighlightPredicate() {

                @Override
                public boolean isHighlighted(final Component renderer, final ComponentAdapter componentAdapter) {
                    final int displayedIndex = componentAdapter.row;
                    final int modelIndex = convertRowIndexToModel(displayedIndex);
                    final CidsBean cidsBean = getModel().getCidsBeanByIndex(modelIndex);
                    return getGeometry(cidsBean) == null;
                }
            };

        final Highlighter noGeometryHighlighter = new ColorHighlighter(noGeometryPredicate, Color.lightGray, null);

        setHighlighters(noGeometryHighlighter);

//        getColumnModel().getColumn(0).setCellRenderer(getDefaultRenderer(Icon.class));
//        getColumnModel().getColumn(2).setCellRenderer(getDefaultRenderer(Number.class));
//        getColumnModel().getColumn(3).setCellRenderer(getDefaultRenderer(Boolean.class));

        getColumnExt(3).setComparator(new NumberStringComparator());

        getColumnModel().getColumn(0).setPreferredWidth(24);
        getColumnModel().getColumn(1).setPreferredWidth(80);
        getColumnModel().getColumn(3).setPreferredWidth(70);

        setDragEnabled(false);

        getTableHeader().setResizingAllowed(false);
        getTableHeader().setReorderingAllowed(false);
        setSortOrder(1, SortOrder.ASCENDING);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  notification  DOCUMENT ME!
     */
    @Override
    public void attachFeatureRequested(final PNotification notification) {
        final Object o = notification.getObject();
        if (o instanceof AttachFeatureListener) {
            final AttachFeatureListener afl = (AttachFeatureListener)o;
            final PFeature pf = afl.getFeatureToAttach();
            if ((pf.getFeature() instanceof PureNewFeature) && (pf.getFeature().getGeometry() instanceof Polygon)) {
                final List<CidsBean> selectedBeans = getSelectedBeans();
                final CidsBean selectedBean = (!selectedBeans.isEmpty()) ? selectedBeans.get(0) : null;
                if (selectedBean != null) {
                    final boolean hasGeometrie = getGeometry(selectedBean) != null;
                    final boolean isMarkedForDeletion = selectedBean.getMetaObject().getStatus()
                                == MetaObject.TO_DELETE;
                    if (!hasGeometrie) {
                        if (isMarkedForDeletion) {
                            JOptionPane.showMessageDialog(
                                Main.getMappingComponent(),
                                "Diesem Objekt kann im Moment keine Geometrie zugewiesen werden. Bitte zuerst speichern.");
                        } else {
                            try {
                                final Geometry geom = pf.getFeature().getGeometry();
                                Main.getMappingComponent().getFeatureCollection().removeFeature(pf.getFeature());
                                setGeometry(geom, selectedBean);
                                final CidsFeature cidsFeature = createCidsFeature(selectedBean);
                                final boolean editable = CidsAppBackend.getInstance().isEditable();
                                cidsFeature.setEditable(editable);
                                Main.getMappingComponent().getFeatureCollection().removeFeature(cidsFeature);
                                Main.getMappingComponent().getFeatureCollection().addFeature(cidsFeature);
                                Main.getMappingComponent().getFeatureCollection().select(cidsFeature);
                            } catch (Exception ex) {
                                LOG.error("error while attaching feature", ex);
                            }
                        }
                    }
                }
            } else if (pf.getFeature() instanceof CidsFeature) {
                JOptionPane.showMessageDialog(
                    Main.getMappingComponent(),
                    "Es k\u00F6nnen nur nicht bereits zugeordnete Fl\u00E4chen zugeordnet werden.");
            }
        }
    }

    @Override
    public Validator getItemValidator(final CidsBean beferBean) {
        final AggregatedValidator aggVal = new AggregatedValidator();
        aggVal.add(BefreiungerlaubnisGeometrieDetailsPanel.getValidatorDurchfluss(beferBean));
        aggVal.add(BefreiungerlaubnisGeometrieDetailsPanel.getValidatorFilterkonstante(beferBean));
        aggVal.validate();
        return aggVal;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   geom  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static CidsBean createNewBean(final Geometry geom) throws Exception {
        final CidsBean beferBean = CidsAppBackend.getInstance()
                    .getVerdisMetaClass(VerdisMetaClassConstants.MC_BEFREIUNGERLAUBNIS_GEOMETRIE)
                    .getEmptyInstance()
                    .getBean();
        final CidsBean geomBean = CidsAppBackend.getInstance()
                    .getVerdisMetaClass(VerdisMetaClassConstants.MC_GEOM)
                    .getEmptyInstance()
                    .getBean();

        final int newId = getNextNewBeanId();
        beferBean.setProperty(BefreiungerlaubnisGeometriePropertyConstants.PROP__ID, newId);
        beferBean.getMetaObject().setID(newId);

        beferBean.setProperty(BefreiungerlaubnisGeometriePropertyConstants.PROP__GEOMETRIE, geomBean);

        if (geom != null) {
            try {
                BefreiungerlaubnisGeometrieDetailsPanel.setGeometry(geom, beferBean);
            } catch (Exception ex) {
                LOG.error("error while assigning feature to new befer", ex);
            }
        }

        return beferBean;
    }

    @Override
    public CidsBean createNewBean() throws Exception {
        final PFeature sole = Main.getMappingComponent().getSolePureNewFeature();

        final CidsBean parentBean = (Main.getInstance().getBefreiungerlaubnisTable().getSelectedBeans().size() == 1)
            ? Main.getInstance().getBefreiungerlaubnisTable().getSelectedBeans().iterator().next() : null;
        final boolean isVersickerung = (parentBean != null)
                    && (parentBean.getProperty(BefreiungerlaubnisPropertyConstants.PROP__AKTENZEICHEN) != null)
                    && ((String)parentBean.getProperty(BefreiungerlaubnisPropertyConstants.PROP__AKTENZEICHEN))
                    .startsWith("747-");
        final boolean isEinleitung = (parentBean != null)
                    && (parentBean.getProperty(BefreiungerlaubnisPropertyConstants.PROP__AKTENZEICHEN) != null)
                    && ((String)parentBean.getProperty(BefreiungerlaubnisPropertyConstants.PROP__AKTENZEICHEN))
                    .startsWith("748-");

        final NewBefreiungerlaubnisGeometrieDialog dialog = new NewBefreiungerlaubnisGeometrieDialog(isVersickerung);
        dialog.setSoleNewExists((sole != null) && (sole.getFeature().getGeometry() instanceof Polygon));

        StaticSwingTools.showDialog(dialog);

        if (dialog.getReturnStatus() == NewBefreiungerlaubnisGeometrieDialog.RET_OK) {
            final CidsBean artBean = dialog.getSelectedTyp();
            Geometry geom = null;
            if (dialog.isSoleNewChecked()) {
                if (sole != null) {
                    geom = sole.getFeature().getGeometry();
                    // unzugeordnete Geometrie aus Karte entfernen
                    Main.getMappingComponent().getFeatureCollection().removeFeature(sole.getFeature());
                }
            }

            final CidsBean beferBean = createNewBean(geom);
            if (isVersickerung) {
                beferBean.setProperty(BefreiungerlaubnisGeometriePropertyConstants.PROP__TYP_VERSICKERUNG, artBean);
            } else if (isEinleitung) {
                beferBean.setProperty(BefreiungerlaubnisGeometriePropertyConstants.PROP__TYP_EINLEITUNG, artBean);
            }
            return beferBean;
        } else {
            return null;
        }
    }

    @Override
    public void featureSelectionChanged(final FeatureCollectionEvent fce) {
        final Collection<Feature> selectedFeatures = CidsAppBackend.getInstance()
                    .getMainMap()
                    .getFeatureCollection()
                    .getSelectedFeatures(); // fce.getEventFeatures();

        final BefreiungerlaubnisTable befreiungerlaubnisTable = Main.getInstance().getBefreiungerlaubnisTable();

        final Collection<CidsBean> parentBeans = new ArrayList<CidsBean>();
        for (final Feature selectedFeature : selectedFeatures) {
            if (selectedFeature instanceof CidsFeature) {
                for (final CidsBean parentBean : befreiungerlaubnisTable.getAllBeans()) {
                    final CidsBean childBean = ((CidsFeature)selectedFeature).getMetaObject().getBean();
                    if (parentBean.getBeanCollectionProperty(BefreiungerlaubnisPropertyConstants.PROP__GEOMETRIEN)
                                .contains(childBean)) {
                        parentBeans.add(parentBean);
                    }
                }
            }
        }

        for (final CidsBean parentBean : parentBeans) {
            final int index = befreiungerlaubnisTable.getModel().getIndexByCidsBean(parentBean);
            if (index >= 0) {
                final int viewIndex = befreiungerlaubnisTable.convertRowIndexToView(index);
                befreiungerlaubnisTable.getSelectionModel().addSelectionInterval(viewIndex, viewIndex);
            }
        }

        super.featureSelectionChanged(fce);
    }

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        this.cidsBean = cidsBean;

        if (cidsBean != null) {
            setCidsBeans(cidsBean.getBeanCollectionProperty(BefreiungerlaubnisPropertyConstants.PROP__GEOMETRIEN));
        } else {
            setCidsBeans(new ArrayList<CidsBean>());
        }
    }

    @Override
    public void removeBean(final CidsBean cidsBean) {
        if (cidsBean != null) {
            try {
                cidsBean.delete();
            } catch (final Exception ex) {
                LOG.error("error while removing befer", ex);
            }
        }
        super.removeBean(cidsBean);
    }

    @Override
    public void setGeometry(final Geometry geometry, final CidsBean cidsBean) throws Exception {
        BefreiungerlaubnisGeometrieDetailsPanel.setGeometry(geometry, cidsBean);
    }

    @Override
    public Geometry getGeometry(final CidsBean cidsBean) {
        return BefreiungerlaubnisGeometrieDetailsPanel.getGeometry(cidsBean);
    }
}
