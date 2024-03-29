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
 * RegenFlaechenTable.java
 *
 * Created on 03.12.2010, 21:50:28
 */
package de.cismet.verdis.gui.regenflaechen;

import Sirius.server.middleware.types.MetaObject;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

import edu.umd.cs.piccolox.event.PNotification;

import org.jdesktop.swingx.decorator.*;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;

import java.awt.Color;
import java.awt.Component;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SortOrder;

import de.cismet.cids.custom.util.VerdisUtils;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.features.PureNewFeature;
import de.cismet.cismap.commons.features.SplittedNewFeature;
import de.cismet.cismap.commons.gui.piccolo.PFeature;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.AttachFeatureListener;

import de.cismet.cismap.navigatorplugin.CidsFeature;

import de.cismet.tools.NumberStringComparator;

import de.cismet.tools.gui.StaticSwingTools;

import de.cismet.validation.Validator;

import de.cismet.validation.validator.AggregatedValidator;

import de.cismet.verdis.CidsAppBackend;
import de.cismet.verdis.CrossReference;

import de.cismet.verdis.commons.constants.VerdisConstants;

import de.cismet.verdis.gui.AbstractCidsBeanWithGeometryTable;
import de.cismet.verdis.gui.Main;
import de.cismet.verdis.gui.aenderungsanfrage.AenderungsanfrageHandler;

import de.cismet.verdis.server.json.AenderungsanfrageJson;
import de.cismet.verdis.server.json.FlaecheAenderungJson;

import static de.cismet.verdis.gui.AbstractCidsBeanTable.getNextNewBeanId;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class RegenFlaechenTable extends AbstractCidsBeanWithGeometryTable {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RegenFlaechenTable.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    //~ Instance fields --------------------------------------------------------

    private Float lastSplitAnteil = null;
    private CidsBean cidsBean;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form RegenFlaechenTable.
     */
    public RegenFlaechenTable() {
        super(CidsAppBackend.Mode.REGEN, new RegenFlaechenTableModel());

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

        final Highlighter errorHighlighter = new ColorHighlighter(
                errorPredicate,
                Color.RED.brighter().brighter().brighter(),
                Color.WHITE);

        final HighlightPredicate warningPredicate = new HighlightPredicate() {

                @Override
                public boolean isHighlighted(final Component renderer, final ComponentAdapter componentAdapter) {
                    final int displayedRowIndex = componentAdapter.row;
                    final int modelRowIndex = convertRowIndexToModel(displayedRowIndex);
                    final int displayedColumnIndex = componentAdapter.column;
                    final int modelColumnIndex = convertColumnIndexToModel(displayedColumnIndex);

                    final CidsBean flaecheBean = getModel().getCidsBeanByIndex(modelRowIndex);
                    final Validator validator;

                    switch (modelColumnIndex) {
                        case 1: {
                            validator = RegenFlaechenDetailsPanel.getValidatorFlaechenBezeichnung(flaecheBean);
                        }
                        break;
                        case 3: {
                            final AggregatedValidator aggVal = new AggregatedValidator();
                            aggVal.add(RegenFlaechenDetailsPanel.getValidatorGroesseGrafik(flaecheBean));
                            aggVal.add(RegenFlaechenDetailsPanel.getValidatorGroesseKorrektur(flaecheBean));
                            validator = aggVal;
                        }
                        break;
                        case 6: {
                            final AggregatedValidator aggVal = new AggregatedValidator();
                            aggVal.add(RegenFlaechenDetailsPanel.getValidatorDatumErfassung(flaecheBean));
                            aggVal.add(RegenFlaechenDetailsPanel.getValidatorDatumVeranlagung(flaecheBean));
                            validator = aggVal;
                        }
                        break;
                        default: {
                            validator = RegenFlaechenDetailsPanel.getValidatorAnteil(flaecheBean);
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
                    return ((cidsBean.getProperty(
                                    VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                            + "."
                                            + VerdisConstants.PROP.FLAECHENINFO.FLAECHENART) != null)
                                    && (VerdisUtils.PROPVAL_ART_VORLAEUFIGEVERANLASSUNG
                                        != (Integer)cidsBean.getProperty(
                                            VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                            + "."
                                            + VerdisConstants.PROP.FLAECHENINFO.FLAECHENART
                                            + "."
                                            + VerdisConstants.PROP.FLAECHENART.ID)))
                                && (getGeometry(cidsBean) == null);
                }
            };

        final Highlighter noGeometryHighlighter = new ColorHighlighter(noGeometryPredicate, Color.lightGray, null);

        setHighlighters(changedHighlighter, warningHighlighter, noGeometryHighlighter, errorHighlighter);

        // getColumnModel().getColumn(0).setCellRenderer(getDefaultRenderer(Icon.class));
        // getColumnModel().getColumn(2).setCellRenderer(getDefaultRenderer(Icon.class));
        getColumnModel().getColumn(3).setCellRenderer(new DefaultTableRenderer() {

                @Override
                public Component getTableCellRendererComponent(final JTable table,
                        final Object value,
                        final boolean isSelected,
                        final boolean hasFocus,
                        final int row,
                        final int column) {
                    final Component comp = super.getTableCellRendererComponent(
                            table,
                            value,
                            isSelected,
                            hasFocus,
                            row,
                            column);
                    if ((comp instanceof JLabel) && (value instanceof Integer)) {
                        final FlaecheAenderungJson aenderungsanfrageFlaeche = ((RegenFlaechenTableModel)getModel())
                                    .getFlaecheAenderungAt(convertRowIndexToModel(row));

                        final Integer groesse = (Integer)value;
                        final Integer groesseAenderung =
                            ((aenderungsanfrageFlaeche != null)
                                        && !Boolean.TRUE.equals(aenderungsanfrageFlaeche.getDraft()))
                            ? aenderungsanfrageFlaeche.getGroesse() : null;

                        ((JLabel)comp).setText(
                            (groesseAenderung != null) ? (groesse + " (" + groesseAenderung + ")")
                                                       : Integer.toString(groesse));
                    }
                    return comp;
                }
            });
        getColumnModel().getColumn(4).setCellRenderer(new DefaultTableRenderer() {

                @Override
                public Component getTableCellRendererComponent(final JTable table,
                        final Object value,
                        final boolean isSelected,
                        final boolean hasFocus,
                        final int row,
                        final int column) {
                    final Component comp = super.getTableCellRendererComponent(
                            table,
                            value,
                            isSelected,
                            hasFocus,
                            row,
                            column);
                    if ((comp instanceof JLabel) && (value instanceof String)) {
                        final FlaecheAenderungJson aenderungsanfrageFlaeche = ((RegenFlaechenTableModel)getModel())
                                    .getFlaecheAenderungAt(convertRowIndexToModel(row));

                        final String flaechenart = (String)value;
                        final String flaechenartAenderung =
                            (((aenderungsanfrageFlaeche != null)
                                            && !Boolean.TRUE.equals(aenderungsanfrageFlaeche.getDraft()))
                                        && (aenderungsanfrageFlaeche.getFlaechenart() != null))
                            ? aenderungsanfrageFlaeche.getFlaechenart().getArtAbkuerzung() : null;
                        ((JLabel)comp).setText(
                            flaechenart
                                    + ((flaechenartAenderung != null) ? (" (" + flaechenartAenderung + ")") : ""));
                    }
                    return comp;
                }
            });
        getColumnModel().getColumn(5).setCellRenderer(new DefaultTableRenderer() {

                @Override
                public Component getTableCellRendererComponent(final JTable table,
                        final Object value,
                        final boolean isSelected,
                        final boolean hasFocus,
                        final int row,
                        final int column) {
                    final Component comp = super.getTableCellRendererComponent(
                            table,
                            value,
                            isSelected,
                            hasFocus,
                            row,
                            column);
                    if ((comp instanceof JLabel) && (value instanceof String)) {
                        final FlaecheAenderungJson aenderungsanfrageFlaeche = ((RegenFlaechenTableModel)getModel())
                                    .getFlaecheAenderungAt(convertRowIndexToModel(row));

                        final String anschlussgrad = (String)value;
                        final String anschlussgradAenderung =
                            (((aenderungsanfrageFlaeche != null)
                                            && !Boolean.TRUE.equals(aenderungsanfrageFlaeche.getDraft()))
                                        && (aenderungsanfrageFlaeche.getAnschlussgrad() != null))
                            ? aenderungsanfrageFlaeche.getAnschlussgrad().getGradAbkuerzung() : null;

                        ((JLabel)comp).setText(
                            anschlussgrad
                                    + ((anschlussgradAenderung != null) ? (" (" + anschlussgradAenderung + ")")
                                                                        : ""));
                    }
                    return comp;
                }
            });
        getColumnModel().getColumn(7).setCellRenderer(new DefaultTableRenderer() {

                @Override
                public Component getTableCellRendererComponent(final JTable table,
                        final Object value,
                        final boolean isSelected,
                        final boolean hasFocus,
                        final int row,
                        final int column) {
                    final Component comp = super.getTableCellRendererComponent(
                            table,
                            value,
                            isSelected,
                            hasFocus,
                            row,
                            column);
                    if ((comp instanceof JLabel) && (value instanceof Date)) {
                        ((JLabel)comp).setText((String)DATE_FORMAT.format((Date)value));
                    }
                    return comp;
                }
            });

        getColumnExt(1).setComparator(new NumberStringComparator());

        getColumnModel().getColumn(0).setPreferredWidth(24);
        getColumnModel().getColumn(1).setPreferredWidth(80);
        getColumnModel().getColumn(2).setPreferredWidth(24);
        getColumnModel().getColumn(3).setPreferredWidth(70);
        getColumnModel().getColumn(4).setPreferredWidth(70);
        getColumnModel().getColumn(5).setPreferredWidth(80);

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
                if ((selectedBean != null)
                            && (VerdisUtils.PROPVAL_ART_VORLAEUFIGEVERANLASSUNG
                                != (Integer)selectedBean.getProperty(
                                    VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                    + "."
                                    + VerdisConstants.PROP.FLAECHENINFO.FLAECHENART
                                    + "."
                                    + VerdisConstants.PROP.FLAECHENART.ID))) {
                    Float oldAnteil = null;
                    double oldArea = 0;
                    double ratio = 0;
                    if (pf.getFeature() instanceof SplittedNewFeature) {
                        final SplittedNewFeature splittedFeature = (SplittedNewFeature)pf.getFeature();
                        if (splittedFeature.getSplittedFromPFeature().getFeature() instanceof CidsFeature) {
                            final CidsBean sourceFlaecheBean = ((CidsFeature)splittedFeature.getSplittedFromPFeature()
                                            .getFeature()).getMetaObject().getBean();
                            oldAnteil = (Float)sourceFlaecheBean.getProperty(VerdisConstants.PROP.FLAECHE.ANTEIL);
                            oldArea = (int)splittedFeature.getSplittedFromPFeature().getFeature().getGeometry()
                                        .getArea();

                            final double area = (int)pf.getFeature().getGeometry().getArea();
                            ratio = (oldArea != 0) ? (area / oldArea) : 0;

                            lastSplitAnteil = oldAnteil;
                        }
                    }

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
                                final int groesse = (int)geom.getArea();
                                Main.getMappingComponent().getFeatureCollection().removeFeature(pf.getFeature());
                                setGeometry(geom, selectedBean);
                                selectedBean.setProperty(
                                    VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                            + "."
                                            + VerdisConstants.PROP.FLAECHENINFO.GROESSE_GRAFIK,
                                    groesse);
                                selectedBean.setProperty(
                                    VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                            + "."
                                            + VerdisConstants.PROP.FLAECHENINFO.GROESSE_KORREKTUR,
                                    groesse);
                                if (oldAnteil != null) {
                                    selectedBean.setProperty(
                                        VerdisConstants.PROP.FLAECHE.ANTEIL,
                                        (float)(int)(oldAnteil * ratio));
                                }
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
    public Validator getItemValidator(final CidsBean flaecheBean) {
        final AggregatedValidator aggVal = new AggregatedValidator();
        aggVal.add(RegenFlaechenDetailsPanel.getValidatorFlaechenBezeichnung(flaecheBean));
        aggVal.add(RegenFlaechenDetailsPanel.getValidatorGroesseGrafik(flaecheBean));
        aggVal.add(RegenFlaechenDetailsPanel.getValidatorGroesseKorrektur(flaecheBean));
        aggVal.add(RegenFlaechenDetailsPanel.getValidatorAnteil(flaecheBean));
        aggVal.add(RegenFlaechenDetailsPanel.getValidatorDatumErfassung(flaecheBean));
        aggVal.add(RegenFlaechenDetailsPanel.getValidatorDatumVeranlagung(flaecheBean));
        aggVal.validate();
        return aggVal;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flaechenartBean     DOCUMENT ME!
     * @param   otherFlaechenBeans  flaechenBezeichnung DOCUMENT ME!
     * @param   geom                DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static CidsBean createNewFlaecheBean(final CidsBean flaechenartBean,
            final Collection<CidsBean> otherFlaechenBeans,
            final Geometry geom) throws Exception {
        final CidsBean flaecheBean = CidsAppBackend.getInstance()
                    .getVerdisMetaClass(VerdisConstants.MC.FLAECHE)
                    .getEmptyInstance()
                    .getBean();
        final CidsBean anschlussgradBean = CidsAppBackend.getInstance()
                    .getVerdisMetaObject(
                            1,
                            CidsAppBackend.getInstance().getVerdisMetaClass(VerdisConstants.MC.ANSCHLUSSGRAD).getId())
                    .getBean();
        final CidsBean flaecheninfoBean = CidsAppBackend.getInstance()
                    .getVerdisMetaClass(VerdisConstants.MC.FLAECHENINFO)
                    .getEmptyInstance()
                    .getBean();
        final CidsBean geomBean = CidsAppBackend.getInstance()
                    .getVerdisMetaClass(VerdisConstants.MC.GEOM)
                    .getEmptyInstance()
                    .getBean();

        final int newId = getNextNewBeanId();
        final String bezeichnung;
        if (flaechenartBean != null) {
            bezeichnung = VerdisUtils.getValidFlaechenname(flaechenartBean.getMetaObject().getId(), otherFlaechenBeans);
        } else {
            bezeichnung = null;
        }

        flaecheBean.setProperty(VerdisConstants.PROP.FLAECHE.ID, newId);
        flaecheBean.getMetaObject().setID(newId);

        flaecheBean.setProperty(
            VerdisConstants.PROP.FLAECHE.DATUM_AENDERUNG,
            new java.sql.Date(Calendar.getInstance().getTime().getTime()));

        flaecheBean.setProperty(VerdisConstants.PROP.FLAECHE.FLAECHENINFO, flaecheninfoBean);
        flaecheBean.setProperty(VerdisConstants.PROP.FLAECHE.FLAECHENINFO + "."
                    + VerdisConstants.PROP.FLAECHENINFO.GEOMETRIE,
            geomBean);
        flaecheBean.setProperty(VerdisConstants.PROP.FLAECHE.FLAECHENINFO + "."
                    + VerdisConstants.PROP.FLAECHENINFO.ANSCHLUSSGRAD,
            anschlussgradBean);
        flaecheBean.setProperty(VerdisConstants.PROP.FLAECHE.FLAECHENINFO + "."
                    + VerdisConstants.PROP.FLAECHENINFO.FLAECHENART,
            flaechenartBean);
        flaecheBean.setProperty(VerdisConstants.PROP.FLAECHE.FLAECHENBEZEICHNUNG, bezeichnung);
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);
        flaecheBean.setProperty(
            VerdisConstants.PROP.FLAECHE.DATUM_VERANLAGUNG,
            new SimpleDateFormat("yy/MM").format(cal.getTime()));

        if (geom != null) {
            try {
                final int groesse = new Integer((int)(geom.getArea()));
                flaecheBean.setProperty(VerdisConstants.PROP.FLAECHE.FLAECHENINFO + "."
                            + VerdisConstants.PROP.FLAECHENINFO.GROESSE_GRAFIK,
                    groesse);
                flaecheBean.setProperty(
                    VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                            + "."
                            + VerdisConstants.PROP.FLAECHENINFO.GROESSE_KORREKTUR,
                    groesse);
                RegenFlaechenDetailsPanel.setGeometry(geom, flaecheBean);
            } catch (Exception ex) {
                LOG.error("error while assigning feature to new flaeche", ex);
            }
        }

        return flaecheBean;
    }

    @Override
    public CidsBean createNewBean() throws Exception {
        final PFeature sole = Main.getMappingComponent().getSolePureNewFeature();

        final Integer lastSplitFlaecheId = CidsAppBackend.getInstance().getLastSplitFlaecheId();
        final Collection<CrossReference> crossreferences = CidsAppBackend.getInstance()
                    .getFlaechenCrossReferencesForFlaecheid(lastSplitFlaecheId);

        final NewFlaecheDialog dialog = new NewFlaecheDialog();
        dialog.setSoleNewExists((sole != null) && (sole.getFeature().getGeometry() instanceof Polygon));
        dialog.setQuerverweiseExists((crossreferences != null) && !crossreferences.isEmpty());

        StaticSwingTools.showDialog(dialog);

        if (dialog.getReturnStatus() == NewFlaecheDialog.RET_OK) {
            final CidsBean flaechenartBean = dialog.getSelectedArt();
            Geometry geom = null;
            boolean flaecheSplitted = false;
            CidsBean anschlussgradBean = null;
            Float oldAnteil = null;
            double oldArea = 0;
            double ratio = 0;
            if ((VerdisUtils.PROPVAL_ART_VORLAEUFIGEVERANLASSUNG
                            != (Integer)flaechenartBean.getProperty(VerdisConstants.PROP.FLAECHENART.ID))
                        && dialog.isSoleNewChecked()) {
                if (sole != null) {
                    geom = sole.getFeature().getGeometry();
                    // unzugeordnete Geometrie aus Karte entfernen
                    Main.getMappingComponent().getFeatureCollection().removeFeature(sole.getFeature());

                    if (sole.getFeature() instanceof SplittedNewFeature) {
                        flaecheSplitted = true;
                        final SplittedNewFeature splittedFeature = (SplittedNewFeature)sole.getFeature();
                        if (splittedFeature.getSplittedFromPFeature().getFeature() instanceof CidsFeature) {
                            final CidsBean sourceFlaecheBean = ((CidsFeature)splittedFeature.getSplittedFromPFeature()
                                            .getFeature()).getMetaObject().getBean();
                            anschlussgradBean = (CidsBean)sourceFlaecheBean.getProperty(
                                    VerdisConstants.PROP.FLAECHE.FLAECHENINFO
                                            + "."
                                            + VerdisConstants.PROP.FLAECHENINFO.ANSCHLUSSGRAD);
                            oldAnteil = lastSplitAnteil;
                            oldArea = (int)splittedFeature.getSplittedFromPFeature().getFeature().getGeometry()
                                        .getArea();
                        }
                    }
                    final double area = (int)geom.getArea();
                    ratio = (oldArea != 0) ? (area / oldArea) : 0;
                }
            }

            final CidsBean flaecheBean = createNewFlaecheBean(
                    flaechenartBean,
                    getAllBeans(),
                    geom);
            if (flaecheSplitted) {
                flaecheBean.setProperty(VerdisConstants.PROP.FLAECHE.FLAECHENINFO + "."
                            + VerdisConstants.PROP.FLAECHENINFO.ANSCHLUSSGRAD,
                    anschlussgradBean);
                if (oldAnteil != null) {
                    flaecheBean.setProperty(VerdisConstants.PROP.FLAECHE.ANTEIL, (float)(int)(oldAnteil * ratio));
                }
            }

            if (dialog.isQuerverweiseChecked()) {
                if (crossreferences != null) {
                    for (final CrossReference crossreference : crossreferences) {
                        final int kassenzeichenNummer = crossreference.getEntityToKassenzeichen();

                        final Map<CidsBean, Collection<Integer>> crosslinkMap = CidsAppBackend.getInstance()
                                    .getFlaecheToKassenzeichenQuerverweisMap();
                        final Collection<Integer> crosslinks;
                        if (crosslinkMap.containsKey(flaecheBean)) {
                            crosslinks = crosslinkMap.get(flaecheBean);
                        } else {
                            crosslinks = new ArrayList<>();
                            crosslinkMap.put(flaecheBean, crosslinks);
                        }
                        crosslinks.add(kassenzeichenNummer);
                    }
                }
            }

            return flaecheBean;
        } else {
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   art  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getValidFlaechenname(final int art) {
        return VerdisUtils.getValidFlaechenname(art, getAllBeans());
    }

    @Override
    public void removeBean(final CidsBean cidsBean) {
        if (cidsBean != null) {
            try {
                cidsBean.delete();
            } catch (final Exception ex) {
                LOG.error("error while removing flaechebean", ex);
            }
        }
        super.removeBean(cidsBean);
    }

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        this.cidsBean = cidsBean;

        if ((cidsBean != null)) {
            setCidsBeans((List<CidsBean>)cidsBean.getProperty(VerdisConstants.PROP.KASSENZEICHEN.FLAECHEN));
        } else {
            setCidsBeans(new ArrayList<CidsBean>());
        }
    }

    @Override
    public void setGeometry(final Geometry geometry, final CidsBean cidsBean) throws Exception {
        RegenFlaechenDetailsPanel.setGeometry(geometry, cidsBean);
    }

    @Override
    public Geometry getGeometry(final CidsBean cidsBean) {
        return RegenFlaechenDetailsPanel.getGeometry(cidsBean);
    }
}
