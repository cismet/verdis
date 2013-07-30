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
 * RegenFlaechenTabellenPanel.java
 *
 * Created on 03.12.2010, 21:50:28
 */
package de.cismet.verdis.gui;
import Sirius.navigator.connection.SessionManager;

import Sirius.server.middleware.types.MetaObject;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

import edu.umd.cs.piccolox.event.PNotification;

import org.jdesktop.swingx.decorator.*;

import java.awt.Color;
import java.awt.Component;

import java.sql.Date;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;

import de.cismet.cismap.commons.features.PureNewFeature;
import de.cismet.cismap.commons.gui.piccolo.PFeature;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.AttachFeatureListener;

import de.cismet.cismap.navigatorplugin.CidsFeature;

import de.cismet.tools.NumberStringComparator;

import de.cismet.validation.Validator;

import de.cismet.validation.validator.AggregatedValidator;

import de.cismet.verdis.CidsAppBackend;

import de.cismet.verdis.commons.constants.*;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class RegenFlaechenTabellenPanel extends AbstractCidsBeanTable implements CidsBeanStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            RegenFlaechenTabellenPanel.class);

    //~ Instance fields --------------------------------------------------------

    private CidsBean cidsBean;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXTable jxtOverview;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form RegenFlaechenTabellenPanel.
     */
    public RegenFlaechenTabellenPanel() {
        super(CidsAppBackend.Mode.REGEN, new RegenFlaechenTableModel());

        initComponents();

        jxtOverview.setModel(getModel());
        final HighlightPredicate errorPredicate = new HighlightPredicate() {

                @Override
                public boolean isHighlighted(final Component renderer, final ComponentAdapter componentAdapter) {
                    final int displayedIndex = componentAdapter.row;
                    final int modelIndex = jxtOverview.getFilters().convertRowIndexToModel(displayedIndex);
                    final CidsBean cidsBean = getModel().getCidsBeanByIndex(modelIndex);
                    return getItemValidator(cidsBean).getState().isError();
                }
            };

        final Highlighter errorHighlighter = new ColorHighlighter(
                errorPredicate,
                Color.RED.brighter().brighter().brighter(),
                Color.WHITE);

        final HighlightPredicate changedPredicate = new HighlightPredicate() {

                @Override
                public boolean isHighlighted(final Component renderer, final ComponentAdapter componentAdapter) {
                    final int displayedIndex = componentAdapter.row;
                    final int modelIndex = jxtOverview.getFilters().convertRowIndexToModel(displayedIndex);
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
                    final int modelIndex = jxtOverview.getFilters().convertRowIndexToModel(displayedIndex);
                    final CidsBean cidsBean = getModel().getCidsBeanByIndex(modelIndex);
                    return getGeometry(cidsBean) == null;
                }
            };

        final Highlighter noGeometryHighlighter = new ColorHighlighter(noGeometryPredicate, Color.lightGray, null);

        jxtOverview.setHighlighters(changedHighlighter, noGeometryHighlighter, errorHighlighter);

        jxtOverview.getColumnModel().getColumn(0).setCellRenderer(jxtOverview.getDefaultRenderer(Icon.class));
        jxtOverview.getColumnModel().getColumn(2).setCellRenderer(jxtOverview.getDefaultRenderer(Icon.class));
        jxtOverview.getColumnModel().getColumn(3).setCellRenderer(jxtOverview.getDefaultRenderer(Number.class));

        jxtOverview.getColumnExt(1).setComparator(new NumberStringComparator());

        jxtOverview.getColumnModel().getColumn(0).setPreferredWidth(24);
        jxtOverview.getColumnModel().getColumn(1).setPreferredWidth(80);
        jxtOverview.getColumnModel().getColumn(2).setPreferredWidth(24);
        jxtOverview.getColumnModel().getColumn(3).setPreferredWidth(70);
        jxtOverview.getColumnModel().getColumn(4).setPreferredWidth(70);
        jxtOverview.getColumnModel().getColumn(5).setPreferredWidth(80);

        jxtOverview.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jxtOverview.setDragEnabled(false);

        jxtOverview.getTableHeader().setResizingAllowed(false);
        jxtOverview.getTableHeader().setReorderingAllowed(false);
        jxtOverview.setSortOrder(1, SortOrder.ASCENDING);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jScrollPane1 = new javax.swing.JScrollPane();
        jxtOverview = getJXTable();

        setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));

        jxtOverview.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jScrollPane1.setViewportView(jxtOverview);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  notification  DOCUMENT ME!
     */
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
                                "Dieser Fl\u00E4che kann im Moment keine Geometrie zugewiesen werden. Bitte zuerst speichern.");
                        } else {
                            try {
                                final Geometry geom = pf.getFeature().getGeometry();
                                final int groesse = (int)geom.getArea();
                                Main.getMappingComponent().getFeatureCollection().removeFeature(pf.getFeature());
                                setGeometry(geom, selectedBean);
                                selectedBean.setProperty(
                                    FlaechePropertyConstants.PROP__FLAECHENINFO
                                            + "."
                                            + FlaecheninfoPropertyConstants.PROP__GROESSE_GRAFIK,
                                    groesse);
                                selectedBean.setProperty(
                                    FlaechePropertyConstants.PROP__FLAECHENINFO
                                            + "."
                                            + FlaecheninfoPropertyConstants.PROP__GROESSE_KORREKTUR,
                                    groesse);
                                final CidsFeature cidsFeature = createCidsFeature(selectedBean);
                                final boolean editable = CidsAppBackend.getInstance().isEditable();
                                cidsFeature.setEditable(editable);
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
        aggVal.add(RegenFlaechenDetailsPanel.getValidatorFebId(flaecheBean));
        return aggVal;
    }

    /**
     * DOCUMENT ME!
     */
    public void reEnumerateFlaechen() {
        final TableSorter sort = new TableSorter(getModel());
        sort.setSortingStatus(3, TableSorter.DESCENDING);
        int counterInt = 0;
        String counterString = null;
        for (int i = 0; i < getModel().getRowCount(); ++i) {
            final CidsBean flaecheBean = getModel().getCidsBeanByIndex(sort.getSortedIndex(i));
            if (flaecheBean != null) {
                final int art = (Integer)flaecheBean.getProperty(
                        FlaechePropertyConstants.PROP__FLAECHENINFO
                                + "."
                                + FlaecheninfoPropertyConstants.PROP__FLAECHENART
                                + "."
                                + FlaechenartPropertyConstants.PROP__ID);
                switch (art) {
                    case 1:
                    case 2: {
                        counterInt++;
                        try {
                            flaecheBean.setProperty(
                                FlaechePropertyConstants.PROP__FLAECHENBEZEICHNUNG,
                                new Integer(counterInt).toString());
                        } catch (Exception ex) {
                            LOG.error("error while setting flaechenbezeichnung", ex);
                        }
                        break;
                    }
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    default: {
                        counterString = nextFlBez(counterString);
                        try {
                            flaecheBean.setProperty(
                                FlaechePropertyConstants.PROP__FLAECHENBEZEICHNUNG,
                                counterString);
                        } catch (Exception ex) {
                            LOG.error("error while setting flaechenbezeichnung", ex);
                        }
                    }
                }
                getModel().fireTableDataChanged();
                repaint();
            }
        }
    }

    @Override
    public CidsBean createNewBean() throws Exception {
        final Object[] possibleValues = {
                "Dachfl\u00E4che",
                "Gr\u00FCndach",
                "versiegelte Fl\u00E4che",
                "\u00D6kopflaster",
                "st\u00E4dtische Stra\u00DFenfl\u00E4che",
                "st\u00E4dtische Stra\u00DFenfl\u00E4che (\u00D6kopflaster)"
            };
        final Object selectedValue = JOptionPane.showInputDialog(
                Main.getCurrentInstance(),
                "W\u00E4hlen Sie die Art der neuen Fl\u00E4che aus",
                "Neue Fl\u00E4che",
                JOptionPane.QUESTION_MESSAGE,
                null,
                possibleValues,
                possibleValues[0]);
        // if (selectedValue!=null)
        int art = -1;
        for (int i = 0; i < possibleValues.length; ++i) {
            if (possibleValues[i].equals(selectedValue)) {
                art = 1 + i;
                break;
            }
        }

        final CidsBean flaecheBean = CidsAppBackend.getInstance()
                    .getVerdisMetaClass(VerdisMetaClassConstants.MC_FLAECHE)
                    .getEmptyInstance()
                    .getBean();
        final CidsBean anschlussgradBean = SessionManager.getProxy()
                    .getMetaObject(
                            1,
                            CidsAppBackend.getInstance().getVerdisMetaClass(VerdisMetaClassConstants.MC_ANSCHLUSSGRAD)
                                .getId(),
                            VerdisConstants.DOMAIN)
                    .getBean();
        final CidsBean flaechenartBean = SessionManager.getProxy()
                    .getMetaObject(
                            art,
                            CidsAppBackend.getInstance().getVerdisMetaClass(VerdisMetaClassConstants.MC_FLAECHENART)
                                .getId(),
                            VerdisConstants.DOMAIN)
                    .getBean();
        final CidsBean flaecheninfoBean = CidsAppBackend.getInstance()
                    .getVerdisMetaClass(VerdisMetaClassConstants.MC_FLAECHENINFO)
                    .getEmptyInstance()
                    .getBean();
        final CidsBean geomBean = CidsAppBackend.getInstance()
                    .getVerdisMetaClass(VerdisMetaClassConstants.MC_GEOM)
                    .getEmptyInstance()
                    .getBean();

        final int newId = getNextNewBeanId();
        flaecheBean.setProperty(FlaechePropertyConstants.PROP__ID, newId);
        flaecheBean.getMetaObject().setID(newId);

        flaecheBean.setProperty(FlaechePropertyConstants.PROP__FLAECHENINFO, flaecheninfoBean);
        flaecheBean.setProperty(FlaechePropertyConstants.PROP__FLAECHENINFO + "."
                    + FlaecheninfoPropertyConstants.PROP__GEOMETRIE,
            geomBean);
        flaecheBean.setProperty(FlaechePropertyConstants.PROP__FLAECHENINFO + "."
                    + FlaecheninfoPropertyConstants.PROP__ANSCHLUSSGRAD,
            anschlussgradBean);
        flaecheBean.setProperty(FlaechePropertyConstants.PROP__FLAECHENINFO + "."
                    + FlaecheninfoPropertyConstants.PROP__FLAECHENART,
            flaechenartBean);
        flaecheBean.setProperty(FlaechePropertyConstants.PROP__FLAECHENBEZEICHNUNG, getValidFlaechenname(art));
        final Calendar cal = Calendar.getInstance();
        flaecheBean.setProperty(
            FlaechePropertyConstants.PROP__DATUM_ERFASSUNG,
            new Date(cal.getTime().getTime()));
        cal.add(Calendar.MONTH, 1);
        flaecheBean.setProperty(
            FlaechePropertyConstants.PROP__DATUM_VERANLAGUNG,
            new SimpleDateFormat("yy/MM").format(cal.getTime()));

        final PFeature sole = Main.getMappingComponent().getSolePureNewFeature();
        if ((sole != null) && (sole.getFeature().getGeometry() instanceof Polygon)) {
            final int answer = JOptionPane.showConfirmDialog(
                    Main.getCurrentInstance(),
                    "Soll die vorhandene, noch nicht zugeordnete Geometrie der neuen Fl\u00E4che zugeordnet werden?",
                    "Geometrie verwenden?",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (answer == JOptionPane.YES_OPTION) {
                try {
                    final Geometry geom = sole.getFeature().getGeometry();

                    final int groesse = new Integer((int)(geom.getArea()));
                    flaecheBean.setProperty(FlaechePropertyConstants.PROP__FLAECHENINFO + "."
                                + FlaecheninfoPropertyConstants.PROP__GROESSE_GRAFIK,
                        groesse);
                    flaecheBean.setProperty(
                        FlaechePropertyConstants.PROP__FLAECHENINFO
                                + "."
                                + FlaecheninfoPropertyConstants.PROP__GROESSE_KORREKTUR,
                        groesse);
                    setGeometry(geom, flaecheBean);

                    // unzugeordnete Geometrie aus Karte entfernen
                    Main.getMappingComponent().getFeatureCollection().removeFeature(sole.getFeature());
                } catch (Exception ex) {
                    LOG.error("error while assigning feature to new flaeche", ex);
                }
            }
        }
        return flaecheBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   art  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getValidFlaechenname(final int art) {
        int highestNumber = 0;
        String highestBezeichner = null;
        boolean noFlaeche = true;
        for (final CidsBean flaecheBean : getAllBeans()) {
            noFlaeche = false;
            final int a = (Integer)flaecheBean.getProperty(
                    FlaechePropertyConstants.PROP__FLAECHENINFO
                            + "."
                            + FlaecheninfoPropertyConstants.PROP__FLAECHENART
                            + "."
                            + FlaechenartPropertyConstants.PROP__ID);
            final String bezeichnung = (String)flaecheBean.getProperty(
                    FlaechePropertyConstants.PROP__FLAECHENBEZEICHNUNG);
            if (bezeichnung == null) {
                break;
            }
            if ((a == Main.PROPVAL_ART_DACH) || (a == Main.PROPVAL_ART_GRUENDACH)) {
                // In Bezeichnung m\u00FCsste eigentlich ne Zahl stehen. Einfach ignorieren falls nicht.
                try {
                    final int num = new Integer(bezeichnung).intValue();
                    if (num > highestNumber) {
                        highestNumber = num;
                    }
                } catch (Exception e) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("getValidFlaechenname", e);
                    }
                    break;
                }
            } else {
                if (highestBezeichner == null) {
                    highestBezeichner = bezeichnung;
                } else if ((bezeichnung.trim().length() > highestBezeichner.trim().length())
                            || ((bezeichnung.trim().length() == highestBezeichner.trim().length())
                                && (bezeichnung.compareTo(highestBezeichner) > 0))) {
                    highestBezeichner = bezeichnung;
                }
            }
        }
        if (noFlaeche == true) {
            highestBezeichner = null;
        }
        // highestBezeichner steht jetzt der lexikographisch h\u00F6chste Bezeichner
        // In highestNumber steht die gr\u00F6\u00DFte vorkommende Zahl f\u00FCr Dachfl\u00E4chen
        // log.debug(highestBezeichner);
        // log.debug(highestNumber+"");

        // n\u00E4chste freie Zahl f\u00FCr Dachfl\u00E4chen
        final int newHighestNumber = highestNumber + 1;

        // n\u00E4chste freie Bezeichnung f\u00FCr versiegelte Fl\u00E4chen
        final String newHighestBezeichner = nextFlBez(highestBezeichner);

        switch (art) {
            case Main.PROPVAL_ART_DACH:
            case Main.PROPVAL_ART_GRUENDACH: {
                return newHighestNumber + "";
            }
            case Main.PROPVAL_ART_VERSIEGELTEFLAECHE:
            case Main.PROPVAL_ART_OEKOPFLASTER:
            case Main.PROPVAL_ART_STAEDTISCHESTRASSENFLAECHE:
            case Main.PROPVAL_ART_STAEDTISCHESTRASSENFLAECHEOEKOPLFASTER:
            default: {
                if (noFlaeche) {
                    return "A";
                }
                return newHighestBezeichner;
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   s  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String nextFlBez(String s) {
        boolean carry = false;
        if (s != null) {
            s = s.trim().toUpperCase();
            final char[] charArr = s.toCharArray();
            for (int i = charArr.length - 1; i >= 0; --i) {
                if (charArr[i] != 'Z') {
                    charArr[i] = (char)(charArr[i] + 1);
                    carry = false;
                    break;
                } else {
                    charArr[i] = 'A';
                    carry = true;
                }
            }
            final String end = new String(charArr);

            if (carry) {
                return "A" + end;
            } else {
                return end;
            }
        }
        return "A";
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

        final String prop = KassenzeichenPropertyConstants.PROP__FLAECHEN;
        if ((cidsBean != null) && (cidsBean.getProperty(prop) instanceof List)) {
            setCidsBeans((List<CidsBean>)cidsBean.getProperty(prop));
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
