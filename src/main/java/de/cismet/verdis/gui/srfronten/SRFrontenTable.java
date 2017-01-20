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
/*
 * SRFrontenTable.java
 *
 * Created on 24.11.2010, 20:42:25
 */
package de.cismet.verdis.gui.srfronten;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;

import edu.umd.cs.piccolox.event.PNotification;

import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;

import java.awt.Color;
import java.awt.Component;

import java.sql.Date;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;

import de.cismet.cids.custom.util.VerdisUtils;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.features.PureNewFeature;
import de.cismet.cismap.commons.features.SplittedNewFeature;
import de.cismet.cismap.commons.gui.piccolo.PFeature;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.AttachFeatureListener;

import de.cismet.cismap.navigatorplugin.CidsFeature;

import de.cismet.tools.gui.StaticSwingTools;

import de.cismet.validation.Validator;

import de.cismet.validation.validator.AggregatedValidator;

import de.cismet.verdis.CidsAppBackend;
import de.cismet.verdis.CrossReference;

import de.cismet.verdis.commons.constants.FrontPropertyConstants;
import de.cismet.verdis.commons.constants.FrontinfoPropertyConstants;
import de.cismet.verdis.commons.constants.KassenzeichenPropertyConstants;
import de.cismet.verdis.commons.constants.VerdisMetaClassConstants;

import de.cismet.verdis.gui.AbstractCidsBeanWithGeometryTable;
import de.cismet.verdis.gui.Main;

import static de.cismet.verdis.gui.AbstractCidsBeanTable.getNextNewBeanId;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class SRFrontenTable extends AbstractCidsBeanWithGeometryTable {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            SRFrontenTable.class);

    //~ Instance fields --------------------------------------------------------

    private CidsBean cidsBean;

    //~ Constructors -----------------------------------------------------------

    // Variables declaration - do not modify
    // End of variables declaration
    /**
     * Creates new form SRFrontenTable.
     */
    public SRFrontenTable() {
        super(CidsAppBackend.Mode.SR, new SRFrontenTableModel());

        initComponents();
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        final HighlightPredicate errorPredicate = new HighlightPredicate() {

                @Override
                public boolean isHighlighted(final Component renderer, final ComponentAdapter componentAdapter) {
                    final int displayedIndex = componentAdapter.row;
                    final int modelIndex = convertRowIndexToModel(displayedIndex);
                    final CidsBean cidsBean = getModel().getCidsBeanByIndex(modelIndex);
                    return getItemValidator(cidsBean).getState().isError();
                }
            };

        final Highlighter errorHighlighter = new ColorHighlighter(errorPredicate, Color.RED, Color.WHITE);

        final HighlightPredicate changedPredicate = new HighlightPredicate() {

                @Override
                public boolean isHighlighted(final Component renderer, final ComponentAdapter componentAdapter) {
                    final int displayedIndex = componentAdapter.row;
                    final int modelIndex = convertRowIndexToModel(displayedIndex);
                    final CidsBean cidsBean = getModel().getCidsBeanByIndex(modelIndex);
                    if (cidsBean != null) {
                        return CidsAppBackend.getInstance().isEditable()
                                    && (cidsBean.getMetaObject().getStatus() == MetaObject.MODIFIED);
                    } else {
                        return false;
                    }
                }
            };

        final Highlighter changedHighlighter = new ColorHighlighter(changedPredicate, null, Color.RED);

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

        final HighlightPredicate warningPredicate = new HighlightPredicate() {

                @Override
                public boolean isHighlighted(final Component renderer, final ComponentAdapter componentAdapter) {
                    final int displayedRowIndex = componentAdapter.row;
                    final int modelRowIndex = convertRowIndexToModel(displayedRowIndex);
                    final int displayedColumnIndex = componentAdapter.column;
                    final int modelColumnIndex = convertColumnIndexToModel(displayedColumnIndex);

                    final CidsBean frontBean = getModel().getCidsBeanByIndex(modelRowIndex);
                    final Validator validator;

                    switch (modelColumnIndex) {
                        case 0: {
                            validator = SRFrontenDetailsPanel.getValidatorNummer(frontBean);
                        }
                        break;
                        case 1: {
                            final AggregatedValidator aggVal = new AggregatedValidator();
                            aggVal.add(SRFrontenDetailsPanel.getValidatorLaengeGrafik(frontBean));
                            aggVal.add(SRFrontenDetailsPanel.getValidatorLaengeKorrektur(frontBean));
                            validator = aggVal;
                        }
                        break;
                        default: {
                            final AggregatedValidator aggVal = new AggregatedValidator();
                            aggVal.add(SRFrontenDetailsPanel.getValidatorDatumErfassung(frontBean));
                            aggVal.add(SRFrontenDetailsPanel.getValidatorVeranlagungSR(frontBean));
                            validator = aggVal;
                        }
                        break;
                    }
                    validator.validate();

                    return validator.getState().isWarning();
                }
            };

        final Highlighter warningHighlighter = new ColorHighlighter(
                warningPredicate,
                Color.ORANGE,
                Color.BLACK);

        setHighlighters(changedHighlighter, warningHighlighter, noGeometryHighlighter, errorHighlighter);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        this.cidsBean = cidsBean;

        if ((cidsBean != null)) {
            setCidsBeans((List<CidsBean>)cidsBean.getProperty(KassenzeichenPropertyConstants.PROP__FRONTEN));
        } else {
            setCidsBeans(new ArrayList<CidsBean>());
        }
    }

    @Override
    public Validator getItemValidator(final CidsBean frontBean) {
        final AggregatedValidator aggVal = new AggregatedValidator();
        aggVal.add(SRFrontenDetailsPanel.getValidatorNummer(frontBean));
        aggVal.add(SRFrontenDetailsPanel.getValidatorLaengeGrafik(frontBean));
        aggVal.add(SRFrontenDetailsPanel.getValidatorLaengeKorrektur(frontBean));
        aggVal.add(SRFrontenDetailsPanel.getValidatorDatumErfassung(frontBean));
        aggVal.add(SRFrontenDetailsPanel.getValidatorVeranlagungSR(frontBean));
        aggVal.validate();
        return aggVal;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
    } // </editor-fold>

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
            if ((pf.getFeature() instanceof PureNewFeature) && (pf.getFeature().getGeometry() instanceof LineString)) {
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
                                "Dieser Fl\u00E4che kann im Moment keine Geometrie zugewiesen werden. Bitte zuerst speichern.");
                        } else {
                            try {
                                final Geometry geom = pf.getFeature().getGeometry();
                                final int laenge = (int)Math.abs(geom.getLength());
                                Main.getMappingComponent().getFeatureCollection().removeFeature(pf.getFeature());
                                setGeometry(geom, selectedBean);
                                selectedBean.setProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                                            + FrontinfoPropertyConstants.PROP__LAENGE_GRAFIK,
                                    laenge);
                                selectedBean.setProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                                            + FrontinfoPropertyConstants.PROP__LAENGE_KORREKTUR,
                                    laenge);
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
    public CidsBean createNewBean() throws Exception {
        final MetaClass srMC = CidsAppBackend.getInstance()
                    .getVerdisMetaClass(VerdisMetaClassConstants.MC_STRASSENREINIGUNG);

        final String srQuery = "SELECT " + srMC.getID() + ", " + srMC.getPrimaryKey() + " FROM " + srMC.getTableName()
                    + " WHERE schluessel = -100;";

        final CidsBean frontBean = CidsAppBackend.getInstance()
                    .getVerdisMetaClass(VerdisMetaClassConstants.MC_FRONT)
                    .getEmptyInstance()
                    .getBean();
        final CidsBean frontinfoBean = CidsAppBackend.getInstance()
                    .getVerdisMetaClass(VerdisMetaClassConstants.MC_FRONTINFO)
                    .getEmptyInstance()
                    .getBean();
        final CidsBean geomBean = CidsAppBackend.getInstance()
                    .getVerdisMetaClass(VerdisMetaClassConstants.MC_GEOM)
                    .getEmptyInstance()
                    .getBean();
        final CidsBean strassenreinigungBean = CidsAppBackend.getInstance().getVerdisMetaObject(srQuery)[0].getBean();

        final int newId = getNextNewBeanId();
        frontBean.setProperty(FrontinfoPropertyConstants.PROP__ID,
            newId);
        frontBean.getMetaObject().setID(newId);

        // cidsBean.setProperty(PROP__"strasse", strasseBean);
        frontBean.setProperty(FrontPropertyConstants.PROP__FRONTINFO, frontinfoBean);
        frontBean.setProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                    + FrontinfoPropertyConstants.PROP__GEOMETRIE,
            geomBean);
        frontBean.setProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                    + FrontinfoPropertyConstants.PROP__SR_KLASSE_OR,
            strassenreinigungBean);
        frontBean.setProperty(FrontPropertyConstants.PROP__NUMMER,
            VerdisUtils.getValidNummer(getAllBeans()));
        frontBean.setProperty(
            FrontPropertyConstants.PROP__ERFASSUNGSDATUM,
            new Date(Calendar.getInstance().getTime().getTime()));

        frontBean.setProperty(FrontPropertyConstants.PROP__FRONTINFO
                    + "."
                    + FrontinfoPropertyConstants.PROP__STRASSE,
            Main.getInstance().getSRFrontenDetailsPanel().getLastStrasseBean());

        final PFeature sole = Main.getMappingComponent().getSolePureNewFeature();
        if ((sole != null) && (sole.getFeature().getGeometry() instanceof LineString)) {
            final int answer = JOptionPane.showConfirmDialog(
                    Main.getInstance(),
                    "Soll die vorhandene, noch nicht zugeordnete Geometrie der neuen Front zugeordnet werden?",
                    "Geometrie verwenden?",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (answer == JOptionPane.YES_OPTION) {
                final Integer lastSplitFrontId = CidsAppBackend.getInstance().getLastSplitFrontId();
                final Collection<CrossReference> crossreferences = CidsAppBackend.getInstance()
                            .getFrontenCrossReferencesForFrontid(lastSplitFrontId);

                if ((crossreferences != null) && !crossreferences.isEmpty()) {
                    final NewFrontDialog dialog = new NewFrontDialog();
                    StaticSwingTools.showDialog(dialog);

                    if (NewFrontDialog.RET_OK == dialog.getReturnStatus()) {
                        if (dialog.isQuerverweiseChecked()) {
                            for (final CrossReference crossreference : crossreferences) {
                                final int kassenzeichenNummer = crossreference.getEntityToKassenzeichen();
                                CidsAppBackend.getInstance()
                                        .getFrontToKassenzeichenQuerverweisMap()
                                        .put(frontBean, kassenzeichenNummer);
                            }
                        }
                    } else {
                        return null;
                    }
                }

                CidsBean strasse = Main.getInstance().getSRFrontenDetailsPanel().getLastStrasseBean();
                CidsBean lage = null;
                CidsBean reinigung = null;

                try {
                    final Geometry geom = sole.getFeature().getGeometry();

                    if (sole.getFeature() instanceof SplittedNewFeature) {
                        final SplittedNewFeature splittedFeature = (SplittedNewFeature)sole.getFeature();
                        if (splittedFeature.getSplittedFromPFeature().getFeature() instanceof CidsFeature) {
                            final CidsBean sourceFrontBean = ((CidsFeature)splittedFeature.getSplittedFromPFeature()
                                            .getFeature()).getMetaObject().getBean();
                            strasse = (CidsBean)sourceFrontBean.getProperty(
                                    FrontPropertyConstants.PROP__FRONTINFO
                                            + "."
                                            + FrontinfoPropertyConstants.PROP__STRASSE);
                            lage = (CidsBean)sourceFrontBean.getProperty(
                                    FrontPropertyConstants.PROP__FRONTINFO
                                            + "."
                                            + FrontinfoPropertyConstants.PROP__LAGE_SR);
                            reinigung = (CidsBean)sourceFrontBean.getProperty(
                                    FrontPropertyConstants.PROP__FRONTINFO
                                            + "."
                                            + FrontinfoPropertyConstants.PROP__SR_KLASSE_OR);
                        }
                    }

                    // größe berechnen und zuweisen
                    final double abs_laenge = Math.abs(geom.getLength());
                    // round to second decimal place
                    final int laenge = (int)Math.round(abs_laenge * 100) / 100;
                    frontBean.setProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                                + FrontinfoPropertyConstants.PROP__LAENGE_GRAFIK,
                        laenge);
                    frontBean.setProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                                + FrontinfoPropertyConstants.PROP__LAENGE_KORREKTUR,
                        laenge);
                    frontBean.setProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                                + FrontinfoPropertyConstants.PROP__STRASSE,
                        strasse);
                    frontBean.setProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                                + FrontinfoPropertyConstants.PROP__LAGE_SR,
                        lage);
                    frontBean.setProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                                + FrontinfoPropertyConstants.PROP__SR_KLASSE_OR,
                        reinigung);

                    setGeometry(geom, frontBean);
                    frontBean.setProperty(
                        FrontPropertyConstants.PROP__NUMMER,
                        VerdisUtils.getValidNummer(getAllBeans()));

                    // unzugeordnete Geometrie aus Karte entfernen
                    Main.getMappingComponent().getFeatureCollection().removeFeature(sole.getFeature());
                } catch (Exception ex) {
                    LOG.error("error while assigning feature to new flaeche", ex);
                }
            }
        }
        return frontBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   strasseBean        DOCUMENT ME!
     * @param   lageBean           DOCUMENT ME!
     * @param   reinigungBean      DOCUMENT ME!
     * @param   otherFrontenBeans  DOCUMENT ME!
     * @param   geom               DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static CidsBean createNewFrontBean(final CidsBean strasseBean,
            final CidsBean lageBean,
            final CidsBean reinigungBean,
            final Collection<CidsBean> otherFrontenBeans,
            final Geometry geom) throws Exception {
        final CidsBean frontBean = CidsAppBackend.getInstance()
                    .getVerdisMetaClass(VerdisMetaClassConstants.MC_FRONT)
                    .getEmptyInstance()
                    .getBean();
        final CidsBean frontinfoBean = CidsAppBackend.getInstance()
                    .getVerdisMetaClass(VerdisMetaClassConstants.MC_FRONTINFO)
                    .getEmptyInstance()
                    .getBean();
        final CidsBean geomBean = CidsAppBackend.getInstance()
                    .getVerdisMetaClass(VerdisMetaClassConstants.MC_GEOM)
                    .getEmptyInstance()
                    .getBean();

        final int newId = getNextNewBeanId();
        frontBean.setProperty(FrontPropertyConstants.PROP__ID, newId);
        frontBean.getMetaObject().setID(newId);

        frontBean.setProperty(FrontPropertyConstants.PROP__FRONTINFO, frontinfoBean);
        frontBean.setProperty(FrontPropertyConstants.PROP__FRONTINFO + "." + FrontinfoPropertyConstants.PROP__GEOMETRIE,
            geomBean);
        frontBean.setProperty(FrontPropertyConstants.PROP__FRONTINFO + "." + FrontinfoPropertyConstants.PROP__STRASSE,
            strasseBean);
        frontBean.setProperty(FrontPropertyConstants.PROP__FRONTINFO + "." + FrontinfoPropertyConstants.PROP__LAGE_SR,
            lageBean);
        frontBean.setProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                    + FrontinfoPropertyConstants.PROP__SR_KLASSE_OR,
            reinigungBean);
        frontBean.setProperty(FrontPropertyConstants.PROP__NUMMER, VerdisUtils.getValidNummer(otherFrontenBeans));

        if (geom != null) {
            try {
                final int laenge = (int)(geom.getLength());
                frontBean.setProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                            + FrontinfoPropertyConstants.PROP__LAENGE_GRAFIK,
                    laenge);
                frontBean.setProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                            + FrontinfoPropertyConstants.PROP__LAENGE_GRAFIK,
                    laenge);
                SRFrontenDetailsPanel.setGeometry(geom, frontBean);
            } catch (Exception ex) {
                LOG.error("error while assigning feature to new front", ex);
            }
        }

        return frontBean;
    }

    @Override
    public void removeBean(final CidsBean cidsBean) {
        if (cidsBean != null) {
            try {
                cidsBean.delete();
            } catch (final Exception ex) {
                LOG.error("error while removing frontbean", ex);
            }
        }
        super.removeBean(cidsBean);
    }

    @Override
    public void setGeometry(final Geometry geometry, final CidsBean cidsBean) throws Exception {
        SRFrontenDetailsPanel.setGeometry(geometry, cidsBean);
    }

    @Override
    public Geometry getGeometry(final CidsBean cidsBean) {
        return SRFrontenDetailsPanel.getGeometry(cidsBean);
    }
}
