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
 * TabellenPanel.java
 *
 * Created on 24.11.2010, 20:42:25
 */
package de.cismet.verdis.gui;

import de.cismet.verdis.constants.WDSRPropertyConstants;
import Sirius.navigator.connection.SessionManager;
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import de.cismet.cids.custom.util.BindingValidationSupport;
import de.cismet.cids.custom.util.CidsBeanTableHelper;

import javax.swing.event.ListSelectionEvent;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;

import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.features.FeatureCollectionEvent;
import de.cismet.cismap.commons.features.PureNewFeature;
import de.cismet.cismap.commons.gui.piccolo.PFeature;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.AttachFeatureListener;

import de.cismet.cismap.navigatorplugin.CidsFeature;
import de.cismet.validation.Validator;
import de.cismet.validation.ValidatorState;
import de.cismet.validation.ValidatorStateImpl;
import de.cismet.validation.validator.AggregatedValidator;
import de.cismet.validation.validator.CidsBeanValidator;

import de.cismet.verdis.CidsAppBackend;
import de.cismet.verdis.constants.VerdisMetaClassConstants;
import de.cismet.verdis.interfaces.CidsBeanTable;
import edu.umd.cs.piccolox.event.PNotification;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.sql.Date;
import java.util.Calendar;

import java.util.List;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class WDSRTabellenPanel extends javax.swing.JPanel implements CidsBeanTable, WDSRPropertyConstants {

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(WDSRTabellenPanel.class);

    //~ Instance fields --------------------------------------------------------

    private final CidsBeanTableHelper helper;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXTable jxtOverview;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form TabellenPanel.
     */
    public WDSRTabellenPanel() {
        final WDSRTableModel model = new WDSRTableModel();

        initComponents();
        jxtOverview.setModel(model);
        final HighlightPredicate errorPredicate = new HighlightPredicate() {

                @Override
                public boolean isHighlighted(final Component renderer, final ComponentAdapter componentAdapter) {
                    final int displayedIndex = componentAdapter.row;
                    final int modelIndex = jxtOverview.getFilters().convertRowIndexToModel(displayedIndex);
                    final CidsBean cidsBean = model.getCidsBeanByIndex(modelIndex);
                    return getItemValidator(cidsBean).getState().isError();
                }
            };

        final Highlighter errorHighlighter = new ColorHighlighter(errorPredicate, Color.RED, Color.WHITE);

        final HighlightPredicate changedPredicate = new HighlightPredicate() {

                @Override
                public boolean isHighlighted(final Component renderer, final ComponentAdapter componentAdapter) {
                    final int displayedIndex = componentAdapter.row;
                    final int modelIndex = jxtOverview.getFilters().convertRowIndexToModel(displayedIndex);
                    final CidsBean cidsBean = model.getCidsBeanByIndex(modelIndex);
                    if (cidsBean != null) {
                        return cidsBean.getMetaObject().getStatus() == MetaObject.MODIFIED;
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
                    final CidsBean cidsBean = model.getCidsBeanByIndex(modelIndex);
                    return WDSRDetailsPanel.getGeometry(cidsBean) == null;
                }
            };

        final Highlighter noGeometryHighlighter = new ColorHighlighter(noGeometryPredicate, Color.lightGray, null);

        jxtOverview.setHighlighters(changedHighlighter, noGeometryHighlighter, errorHighlighter);

        helper = new CidsBeanTableHelper(this, model);

        BindingValidationSupport.attachBindingValidationToAllTargets(bindingGroup);
    }

    //~ Methods ----------------------------------------------------------------


    @Override
    public Validator getItemValidator(final CidsBean frontBean) {
        final AggregatedValidator aggVal = new AggregatedValidator();
        aggVal.add(getValidatorNummer(frontBean));
        aggVal.add(getValidatorLaengeGrafik(frontBean));
        aggVal.add(getValidatorLaengeKorrektur(frontBean));
        aggVal.add(getValidatorDatumErfassung(frontBean));
        aggVal.add(getValidatorVeranlagungWD(frontBean));
        aggVal.add(getValidatorVeranlagungSR(frontBean));
        return aggVal;
    }

    @Override
    public Validator getValidator() {
        return helper.getValidator();
    }

    public static Validator getValidatorNummer(final CidsBean frontBean) {
        return new CidsBeanValidator(frontBean, WDSRPropertyConstants.PROP__NUMMER) {

            @Override
            public ValidatorState performValidation() {
                final CidsBean cidsBean = getCidsBean();
                if (cidsBean == null) {
                    return null;
                }

//                final String bezeichnung = (String) cidsBean.getProperty(PROP__"flaechenbezeichnung");
//                final int art = (cidsBean.getProperty(PROP__"flaecheninfo.flaechenart.id") == null) ? 0 : (Integer) cidsBean.getProperty(PROP__"flaecheninfo.flaechenart.id");
//
//                final Action action = new AbstractAction() {
//
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
//                        final int answer = JOptionPane.showConfirmDialog(
//                                Main.THIS,
//                                "Soll die n\u00E4chste freie Bezeichnung gew\u00E4hlt werden?",
//                                "Bezeichnung automatisch setzen",
//                                JOptionPane.YES_NO_OPTION);
//                        if (answer == JOptionPane.YES_OPTION) {
//                            final CidsBean cidsBean = getCidsBean();
//                            int art;
//                            try {
//                                art = (Integer) cidsBean.getProperty(PROP__"flaecheninfo.flaechenart.id");
//                            } catch (final NumberFormatException ex) {
//                                art = 0;
//                            }
//                            final String newValue = Main.THIS.getRegenFlaechenTabellenPanel().getValidFlaechenname(art);
//                            try {
//                                cidsBean.setProperty(PROP__"flaechenbezeichnung", newValue);
//                            } catch (Exception ex) {
//                                if (log.isDebugEnabled()) {
//                                    log.debug("error while setting flaechenbezeichnung", ex);
//                                }
//                            }
//                        }
//                    }
//                };
//                boolean numerisch = false;
//                Integer tester = null;
//                try {
//                    tester = Integer.parseInt(bezeichnung);
//                    numerisch = true;
//                } catch (final Exception ex) {
//                    numerisch = false;
//                }
//
//                if ((art == Main.PROPVAL_ART_DACH) || (art == Main.PROPVAL_ART_GRUENDACH)) {
//                    if (!numerisch) {
//                        return new ValidatorStateImpl(ValidatorState.ERROR, "Fl\u00E4chenbezeichnung muss eine Zahl sein.", action);
//                    } else {
//                        if ((tester.intValue() > 1000) || (tester.intValue() < 0)) {
//                            return new ValidatorStateImpl(ValidatorState.ERROR, "Fl\u00E4chenbezeichnung muss zwischen 0 und 1000 liegen.", action);
//                        }
//                    }
//                } else {
//                    if (bezeichnung != null) {
//                        final int len = bezeichnung.length();
//                        if (numerisch || ((len > 3) || ((len == 3) && (bezeichnung.compareTo("BBB") > 0)))) {
//                            return new ValidatorStateImpl(ValidatorState.ERROR, "Fl\u00E4chenbezeichnung muss zwischen A und BBB liegen.", action);
//                        }
//                    }
//                }
                return new ValidatorStateImpl(ValidatorState.Type.VALID);
            }
        };
    }


    public static Validator getValidatorLaengeGrafik(final CidsBean frontBean) {
        return new CidsBeanValidator(frontBean, WDSRPropertyConstants.PROP__LAENGE_GRAFIK) {

            @Override
            public ValidatorState performValidation() {
                final CidsBean cidsBean = getCidsBean();
                if (cidsBean == null) {
                    return null;
                }

                final Integer laenge_grafik = (Integer) cidsBean.getProperty(WDSRPropertyConstants.PROP__LAENGE_GRAFIK);
                final Geometry geom = WDSRDetailsPanel.getGeometry(cidsBean);
                final Action action = new AbstractAction() {

                    @Override
                    public void actionPerformed(final ActionEvent event) {
                        final CidsBean cidsBean = getCidsBean();
                        final Geometry geom = WDSRDetailsPanel.getGeometry(cidsBean);

                        if (Main.THIS.isInEditMode()) {
                            if (geom != null) {
                                final int answer = JOptionPane.showConfirmDialog(
                                        Main.THIS,
                                        "Soll die Länge aus der Grafik \u00FCbernommen werden?",
                                        "Länge automatisch setzen",
                                        JOptionPane.YES_NO_OPTION);
                                if (answer == JOptionPane.YES_OPTION) {
                                    try {
                                        final int laenge_grafik = (int) Math.abs(geom.getLength());
                                        cidsBean.setProperty(WDSRPropertyConstants.PROP__LAENGE_GRAFIK, laenge_grafik);
                                    } catch (final Exception ex) {
                                        if (LOG.isDebugEnabled()) {
                                            LOG.debug("error while setting laenge_grafik", ex);
                                        }
                                    }
                                }
                            }
                        }
                    }
                };

                if (laenge_grafik == null) {
                    return new ValidatorStateImpl(ValidatorState.Type.ERROR, "Wert ist leer", action);
                }

                if (geom != null && laenge_grafik != (int) Math.abs(geom.getLength())) {
                    return new ValidatorStateImpl(ValidatorState.Type.WARNING, "L\u00E4nge der Geometrie stimmt nicht \u00FCberein (" + ((int) (geom.getLength())) + ")", action);
                }
                return new ValidatorStateImpl(ValidatorState.Type.VALID);
            }
        };
    }

    public static Validator getValidatorLaengeKorrektur(final CidsBean frontBean) {
        return new CidsBeanValidator(frontBean, WDSRPropertyConstants.PROP__LAENGE_KORREKTUR) {

            @Override
            public ValidatorState performValidation() {
                final CidsBean cidsBean = getCidsBean();
                if (cidsBean == null) {
                    return null;
                }

                Integer laenge_grafik = (Integer) cidsBean.getProperty(WDSRPropertyConstants.PROP__LAENGE_GRAFIK);
                final Integer laenge_korrektur = (Integer) cidsBean.getProperty(WDSRPropertyConstants.PROP__LAENGE_KORREKTUR);
                final Action action = new AbstractAction() {

                    @Override
                    public void actionPerformed(final ActionEvent event) {
                        final CidsBean cidsBean = getCidsBean();
                        final Geometry geom = WDSRDetailsPanel.getGeometry(cidsBean);

                        if (Main.THIS.isInEditMode()) {
                            if (geom != null) {
                                final int answer = JOptionPane.showConfirmDialog(
                                        Main.THIS,
                                        "Soll die Länge aus der Grafik \u00FCbernommen werden?",
                                        "Länge automatisch setzen",
                                        JOptionPane.YES_NO_OPTION);
                                if (answer == JOptionPane.YES_OPTION) {
                                    try {
                                        final Integer laenge_grafik = (Integer) cidsBean.getProperty(WDSRPropertyConstants.PROP__LAENGE_GRAFIK);
                                        cidsBean.setProperty(WDSRPropertyConstants.PROP__LAENGE_KORREKTUR, laenge_grafik);
                                    } catch (final Exception ex) {
                                        if (LOG.isDebugEnabled()) {
                                            LOG.debug("error while setting laenge_korrektur", ex);
                                        }
                                    }
                                }
                            }
                        }
                    }
                };

                if (laenge_korrektur != null) {
                    if (laenge_grafik == null) {
                        laenge_grafik = 0;
                    }
                    final int diff = laenge_korrektur - laenge_grafik;
                    if (Math.abs(diff) > 20) {
                        return new ValidatorStateImpl(ValidatorState.Type.WARNING, "Differenz zwischen Korrekturwert und Länge > 20m.", action);
                    }
                }
                return new ValidatorStateImpl(ValidatorState.Type.VALID);
            }
        };
    }

    public static Validator getValidatorDatumErfassung(final CidsBean frontBean) {
        return new CidsBeanValidator(frontBean, WDSRPropertyConstants.PROP__ERFASSUNGSDATUM) {

            @Override
            public ValidatorState performValidation() {
                final CidsBean cidsBean = getCidsBean();
                if (cidsBean == null) {
                    return null;
                }

                // jedes gültige Datum ist valide
                return new ValidatorStateImpl(ValidatorState.Type.VALID);
            }
        };
    }

    public static Validator getValidatorVeranlagungSR(final CidsBean frontBean) {
        return new CidsBeanValidator(frontBean, WDSRPropertyConstants.PROP__SR_VERANLAGUNG) {

            @Override
            public ValidatorState performValidation() {
                final CidsBean cidsBean = getCidsBean();
                if (cidsBean == null) {
                    return null;
                }

                final String veranlagungsdatum = (String) cidsBean.getProperty(WDSRPropertyConstants.PROP__SR_VERANLAGUNG);

                if (veranlagungsdatum != null) {
                    final boolean matches = Pattern.matches(
                            "\\d\\d/(01|02|03|04|05|06|07|08|09|10|11|12)",
                            veranlagungsdatum);
                    if (!matches) {
                        return new ValidatorStateImpl(ValidatorState.Type.ERROR, "Veranlagungsdatum muss im Format JJ/MM eingegeben werden.");
                    }
                }
                return new ValidatorStateImpl(ValidatorState.Type.VALID);
            }
        };
    }

    public static Validator getValidatorVeranlagungWD(final CidsBean frontBean) {
        return new CidsBeanValidator(frontBean, WDSRPropertyConstants.PROP__WD_VERANLAGUNG) {

            @Override
            public ValidatorState performValidation() {
                final CidsBean cidsBean = getCidsBean();
                if (cidsBean == null) {
                    return null;
                }

                final String veranlagungsdatum = (String) cidsBean.getProperty(WDSRPropertyConstants.PROP__WD_VERANLAGUNG);

                if (veranlagungsdatum != null) {
                    final boolean matches = Pattern.matches(
                            "\\d\\d/(01|02|03|04|05|06|07|08|09|10|11|12)",
                            veranlagungsdatum);
                    if (!matches) {
                        return new ValidatorStateImpl(ValidatorState.Type.ERROR, "Veranlagungsdatum muss im Format JJ/MM eingegeben werden.");
                    }
                }
                return new ValidatorStateImpl(ValidatorState.Type.VALID);
            }
        };
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jScrollPane1 = new javax.swing.JScrollPane();
        jxtOverview = new org.jdesktop.swingx.JXTable();

        setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));

        jxtOverview.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        final org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create(
                "${cidsBean}");
        final org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings
                    .createJTableBinding(
                        org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                        this,
                        eLProperty,
                        jxtOverview);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        final org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${selectedRow}"),
                jxtOverview,
                org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(jxtOverview);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    @Override
    public CidsBeanTableHelper getTableHelper() {
        return helper;
    }

    public void attachFeatureRequested(final PNotification notification) {
        final Object o = notification.getObject();
        if (o instanceof AttachFeatureListener) {
            final AttachFeatureListener afl = (AttachFeatureListener)o;
            final PFeature pf = afl.getFeatureToAttach();
            if (pf.getFeature() instanceof PureNewFeature && pf.getFeature().getGeometry() instanceof LineString) {
                CidsBean selectedBean = getSelectedBean();
                if (selectedBean != null) {
                    boolean hasGeometrie = WDSRDetailsPanel.getGeometry(selectedBean) != null;
                    boolean isMarkedForDeletion = selectedBean.getMetaObject().getStatus() == MetaObject.TO_DELETE;
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
                                WDSRDetailsPanel.setGeometry(geom, selectedBean);
                                selectedBean.setProperty(PROP__LAENGE_GRAFIK, laenge);
                                selectedBean.setProperty(PROP__LAENGE_KORREKTUR, laenge);
                                CidsFeature cidsFeature = CidsBeanTableHelper.getCidsFeature(selectedBean, true);
                                Main.getMappingComponent().getFeatureCollection().addFeature(cidsFeature);
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

    private CidsBean getSelectedBean() {
        if (getSelectedBeans().size() > 0) {
            return getSelectedBeans().get(0);
        } else {
            return null;
        }
    }

    public int getValidNummer() {
        int highestNummer = 0;
        for (CidsBean flaecheBean : getAllBeans()) {
            final Integer nummer = (Integer) flaecheBean.getProperty(PROP__NUMMER);
            if (nummer == null) {
                break;
            }
            try {
                final int num = new Integer(nummer).intValue();
                if (num > highestNummer) {
                    highestNummer = num;
                }
            } catch (Exception ex) {
                break;
            }
        }
        return highestNummer + 1;
    }

    @Override
    public CidsBean createNewBean(final int id) throws Exception {
        final MetaClass srMC = CidsAppBackend.getInstance().getVerdisMetaClass(VerdisMetaClassConstants.MC_STRASSENREINIGUNG);
        final MetaClass wdMC = CidsAppBackend.getInstance().getVerdisMetaClass(VerdisMetaClassConstants.MC_WINTERDIENST);
        final String srQuery = "SELECT " + srMC.getID() + ", " + srMC.getPrimaryKey() + " FROM " + srMC.getTableName() + " WHERE schluessel = -100;";
        final String wdQuery = "SELECT " + wdMC.getID() + ", " + wdMC.getPrimaryKey() + " FROM " + wdMC.getTableName() + " WHERE schluessel = -200;";

        CidsBean cidsBean = CidsAppBackend.getInstance().getVerdisMetaClass(VerdisMetaClassConstants.MC_FRONTINFO).getEmptyInstance().getBean();
        CidsBean geomBean = CidsAppBackend.getInstance().getVerdisMetaClass(VerdisMetaClassConstants.MC_GEOM).getEmptyInstance().getBean();
        CidsBean strassenreinigungBean = SessionManager.getProxy().getMetaObjectByQuery(srQuery, 0)[0].getBean();
        CidsBean winterdienstBean = SessionManager.getProxy().getMetaObjectByQuery(wdQuery, 0)[0].getBean();


        //CidsBean strasseBean = SessionManager.getProxy().getMetaObject(8, PROP__"strasse".getId(), Main.DOMAIN).getBean();
        
        //cidsBean.setProperty(PROP__"strasse", strasseBean);
        cidsBean.setProperty(PROP__ID, id);
        cidsBean.setProperty(PROP__GEOMETRIE, geomBean);
        cidsBean.setProperty(PROP__SR_KLASSE_OR, strassenreinigungBean);
        cidsBean.setProperty(PROP__WD_PRIO_OR, winterdienstBean);
        cidsBean.setProperty(PROP__NUMMER, getValidNummer());
        cidsBean.setProperty(PROP__ERFASSUNGSDATUM, new Date(Calendar.getInstance().getTime().getTime()));

        final PFeature sole = Main.getMappingComponent().getSolePureNewFeature();
        if (sole != null && sole.getFeature().getGeometry() instanceof LineString) {
            final int answer = JOptionPane.showConfirmDialog(
                    Main.THIS,
                    "Soll die vorhandene, noch nicht zugeordnete Geometrie der neuen Front zugeordnet werden?",
                    "Geometrie verwenden?",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (answer == JOptionPane.YES_OPTION) {
                try {
                    Geometry geom = sole.getFeature().getGeometry();
                    // größe berechnen und zuweisen
                    int laenge = (int) Math.abs(geom.getLength());
                    cidsBean.setProperty(PROP__LAENGE_GRAFIK, laenge);
                    cidsBean.setProperty(PROP__LAENGE_KORREKTUR, laenge);
                    WDSRDetailsPanel.setGeometry(geom, cidsBean);
                    cidsBean.setProperty(PROP__NUMMER, getValidNummer());

                    // unzugeordnete Geometrie aus Karte entfernen
                    Main.getMappingComponent().getFeatureCollection().removeFeature(sole.getFeature());
                } catch (Exception ex) {
                    LOG.error("error while assigning feature to new flaeche", ex);
                }
            }
        }
        return cidsBean;
   }

    // komplettes CidsBeanTable interface wird vom Helper übernommen

    @Override
    public void addNewBean() {
        helper.addNewBean();
    }

    @Override
    public void removeSelectedBeans() {
        helper.removeSelectedBeans();
    }

    @Override
    public void addBean(final CidsBean cidsBean) {
        helper.addBean(cidsBean);
    }

    @Override
    public void removeBean(final CidsBean cidsBean) {
        helper.removeBean(cidsBean);
    }

    @Override
    public void restoreSelectedBeans() {
        helper.restoreSelectedBeans();
    }

    @Override
    public void setSelectedRowListener(final CidsBeanStore selectedRowListener) {
        helper.setSelectedRowListener(selectedRowListener);
    }

    @Override
    public CidsBeanStore getSelectedRowListener() {
        return helper.getSelectedRowListener();
    }

    @Override
    public List<CidsBean> getSelectedBeans() {
        return helper.getSelectedBeans();
    }

    @Override
    public JXTable getJXTable() {
        return jxtOverview;
    }

    @Override
    public void featuresAdded(final FeatureCollectionEvent fce) {
        helper.featuresAdded(fce);
    }

    @Override
    public void allFeaturesRemoved(final FeatureCollectionEvent fce) {
        helper.allFeaturesRemoved(fce);
    }

    @Override
    public void featuresRemoved(final FeatureCollectionEvent fce) {
        helper.featuresRemoved(fce);
    }

    @Override
    public void featuresChanged(final FeatureCollectionEvent fce) {
        helper.featuresChanged(fce);
    }

    @Override
    public void featureSelectionChanged(final FeatureCollectionEvent fce) {
        if (CidsAppBackend.getInstance().getMode() == CidsAppBackend.Mode.ESW) {
            helper.featureSelectionChanged(fce);
        }
    }

    @Override
    public void featureReconsiderationRequested(final FeatureCollectionEvent fce) {
        helper.featureReconsiderationRequested(fce);
    }

    @Override
    public void featureCollectionChanged() {
        helper.featureCollectionChanged();
    }

    @Override
    public void valueChanged(final ListSelectionEvent e) {
        helper.valueChanged(e);
    }

    @Override
    public CidsBean getCidsBean() {
        return (helper == null) ? null : helper.getCidsBean();
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        helper.setCidsBean(cidsBean);
    }

    @Override
    public void requestFeatureAttach(final Feature f) {
        helper.requestFeatureAttach(f);
    }

    @Override
    public List<CidsBean> getAllBeans() {
        return helper.getAllBeans();
    }

    @Override
    public void selectCidsBean(final CidsBean cidsBean) {
        helper.selectCidsBean(cidsBean);
    }

}
