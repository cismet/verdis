/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * FlaechenDetails.java
 *
 * Created on 10. Januar 2005, 11:02
 */
package de.cismet.verdis.data;

import com.vividsolutions.jts.geom.Geometry;

import org.postgis.PGgeometry;

import java.awt.Color;
import java.awt.Component;
import java.awt.Paint;
import java.awt.event.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.text.DateFormat;

import java.util.*;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.text.*;

import de.cismet.cismap.commons.features.StyledFeature;
import de.cismet.cismap.commons.gui.piccolo.FeatureAnnotationSymbol;
import de.cismet.cismap.commons.jtsgeometryfactories.PostGisGeometryFactory;

import de.cismet.tools.gui.dbwriter.SimpleDbAction;

import de.cismet.validation.Validatable;

import de.cismet.verdis.gui.FlaechenUebersichtsTableModel;
import de.cismet.verdis.gui.Main;

import de.cismet.verdis.models.*;

/**
 * DOCUMENT ME!
 *
 * @author   hell
 * @version  $Revision$, $Date$
 */
public class Flaeche implements Cloneable, StyledFeature {

    //~ Static fields/initializers ---------------------------------------------

    // Status f\u00FCr die Zwischenablage
    public static final int NONE = 0;
    public static final int CUTTED = 1;
    public static final int COPIED = 2;

    /**
     * <editor-fold defaultstate="collapsed" desc=" SQL Kram (hier werden Statements zum CREATE,UPDATE und DELETE
     * erzeugt ">.
     *
     * @return  DOCUMENT ME!
     */
    private SimpleDbAction getStatement4Flaechen() {
        SimpleDbAction sdba = new SimpleDbAction();
        if (isMarkedForDeletion()) {
            if (fl_id > 0) {
                sdba.setStatement("DELETE FROM flaechen where "
                            + "flaeche=" + this.fl_id + " and "
                            + "kassenzeichen_reference=" + this.kassenzeichen);
                sdba.setDescription("Verbindung l\u00F6schen zwischen >>KASSENZEICHEN<< und >>FLAECHE<<");
                sdba.setType(SimpleDbAction.DELETE);
            } else {
                sdba = null;
            }
        } else if (isNewFlaeche() && (fl_id > 0)) { // ist vorher irgendwo ausgeschnitten worden
            sdba.setStatement("INSERT INTO flaechen (id,flaeche,kassenzeichen_reference) "
                        + "VALUES("
                        + "nextval('FLAECHEN_SEQ')," + fl_id + "," + this.kassenzeichen
                        + ")");
            sdba.setDescription("Verbindung zwischen >>KASSENZEICHEN<< und >>FLAECHE<<");
            sdba.setType(SimpleDbAction.INSERT);
        } else if (hasChanged() && (fl_id > 0)) {
            sdba = null;
        } else if (fl_id < 0) {
            sdba.setStatement("INSERT INTO flaechen (id,flaeche,kassenzeichen_reference) "
                        + "VALUES("
                        + "nextval('FLAECHEN_SEQ'),nextval('FLAECHE_SEQ')," + this.kassenzeichen
                        + ")");
            sdba.setDescription("Verbindung zwischen >>KASSENZEICHEN<< und >>FLAECHE<<");
            sdba.setType(SimpleDbAction.INSERT);
        } else {
            sdba = null;
        }
        if (sdba != null) {
            log.info(sdba.getStatement());
        }
        return sdba;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private SimpleDbAction getStatement4Flaeche() {
        SimpleDbAction sdba = new SimpleDbAction();
        String sperrenString = "";
        if (sperre) {
            sperrenString = "T";
        }

        if (isMarkedForDeletion() && (clipboardStatus != CUTTED)) {
            if (fl_id > 0) {
                sdba.setStatement("DELETE FROM flaeche where "
                            + "id=" + this.fl_id);
                sdba.setDescription("Datensatz in >>FLAECHE<< l\u00F6schen");
                sdba.setType(SimpleDbAction.DELETE);
            } else {
                sdba = null;
            }
        } else if (hasChanged() && (fl_id > 0)) {
            sdba.setStatement("update flaeche set "
                        + "anteil=" + this.anteil + ", "
                        + "flaechenbezeichnung='" + this.bezeichnung + "', "
                        + "bemerkung=" + nullAwareSqlStringMaker(this.bemerkung) + ", "
                        + "datum_erfassung='" + this.erfassungsdatum + "', "
                        + "datum_veranlagung='" + this.veranlagungsdatum + "', "
                        + "sperre='" + sperrenString + "', "
                        + "bemerkung_sperre=" + nullAwareSqlStringMaker(this.bem_sperre) + ", "
                        + "feb_id=" + nullAwareSqlStringMaker(this.feb_id) + " "
                        + "where id=" + this.fl_id);
            sdba.setDescription("Ver\u00E4ndere die Tabelle >>FLAECHE<<");
            sdba.setType(SimpleDbAction.UPDATE);
        } else if (fl_id < 0) {
            String flaecheninfo_idString = ", nextval('FLAECHENINFO_SEQ')";
            if (this.flaecheninfo_id > 0) {
                flaecheninfo_idString = "," + flaecheninfo_id;
            }
            sdba.setStatement("INSERT INTO flaeche "
                        + "(id,flaecheninfo,anteil,flaechenbezeichnung,bemerkung,datum_erfassung,datum_veranlagung,sperre,bemerkung_sperre,feb_id) "
                        + "VALUES("
                        + "currval('FLAECHE_SEQ')"
                        + flaecheninfo_idString
                        + "," + this.anteil
                        + ",'" + this.bezeichnung + "'"
                        + "," + nullAwareSqlStringMaker(this.bemerkung)
                        + ",'" + this.erfassungsdatum + "'"
                        + ",'" + this.veranlagungsdatum + "'"
                        + ",'" + sperrenString + "'"
                        + "," + nullAwareSqlStringMaker(this.bem_sperre)
                        + "," + nullAwareSqlStringMaker(this.feb_id)
                        + ")");
            sdba.setDescription("F\u00FCllen der Tabelle >>FLAECHE<<");
            sdba.setType(SimpleDbAction.INSERT);
        } else {
            sdba = null;
        }
        if (sdba != null) {
            log.info(sdba.getStatement());
        }
        return sdba;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private SimpleDbAction getStatement4Flaecheninfo() {
        SimpleDbAction sdba = new SimpleDbAction();
        if ((gr_korrektur == null) && (gr_grafik != null)) {
            gr_korrektur = new Integer(gr_grafik.intValue());
        }
        if (isMarkedForDeletion() && (clipboardStatus != CUTTED)) {
            if (this.flaecheninfo_id > 0) {
                sdba = new SimpleDbAction() {

                        @Override
                        public void executeAction(final Connection conn) throws SQLException {
                            final Statement checker = conn.createStatement();
                            final ResultSet check = checker.executeQuery(
                                    "SELECT count(*) FROM FLAECHE WHERE flaecheninfo="
                                            + flaecheninfo_id);
                            check.next();
                            final int counter = check.getInt(1);
                            if (counter == 0) {
                                super.executeAction(conn);
                            }
                        }
                    };
                sdba.setStatement("DELETE FROM flaecheninfo where "
                            + "id=" + this.flaecheninfo_id);
                sdba.setDescription("Datensatz in >>FLAECHENINFO<< l\u00F6schen");
                sdba.setType(SimpleDbAction.DELETE);
            } else {
                sdba = null;
            }
        } else if (hasChanged() && (flaecheninfo_id > 0)) {
            String geometrieIdString = "null";
            if ((geom_id > 0) && (geom != null)) {
                geometrieIdString = new Integer(geom_id).toString();
            } else if ((geom_id < 0) && (geom != null)) {
                geometrieIdString = "nextval('GEOM_SEQ')";
            }
            sdba.setStatement("update flaecheninfo set "
                        + "groesse_aus_grafik=" + this.gr_grafik + ", "
                        + "groesse_korrektur=" + this.gr_korrektur + ", "
                        + "flaechenart=" + this.art + ", "
                        + "anschlussgrad=" + this.grad + ", "
                        + "geometrie=" + geometrieIdString + ", "
                        + "beschreibung=" + this.beschreibung + " "
                        + "where id=" + this.flaecheninfo_id);
            sdba.setDescription("Ver\u00E4ndere die Tabelle >>FLAECHENINFO<<");
            sdba.setType(SimpleDbAction.UPDATE);
        } else if (flaecheninfo_id < 0) {
            String geomInsert = "";
            if ((geom != null) && (geom_id == -1)) {
                geomInsert = "nextval('GEOM_SEQ')";
            } else {
                geomInsert = "null";
            }

            sdba.setStatement("INSERT INTO flaecheninfo "
                        + "(id,groesse_aus_grafik,groesse_korrektur,flaechenart,anschlussgrad,beschreibung,geometrie) "
                        + "VALUES("
                        + "currval('FLAECHENINFO_SEQ') "
                        + "," + this.gr_grafik
                        + "," + this.gr_korrektur
                        + "," + this.art
                        + "," + this.grad
                        + "," + this.beschreibung
                        + "," + geomInsert + " "
                        + ")");
            sdba.setDescription("F\u00FCllen der Tabelle >>FLAECHENINFO<<");
            sdba.setType(SimpleDbAction.INSERT);
        } else {
            sdba = null;
        }
        if (sdba != null) {
            log.info(sdba.getStatement());
        }
        return sdba;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private SimpleDbAction getStatement4Geom() {
        SimpleDbAction sdba = new SimpleDbAction();
        if ((isMarkedForDeletion() && (clipboardStatus != CUTTED)) || isGeometryRemoved()) {
            int geomIdInThisCase = 0;
            if (isGeometryRemoved()) {
                geomIdInThisCase = removedGeometryId;
            } else {
                geomIdInThisCase = geom_id;
            }
            if (geomIdInThisCase > 0) {
                final int gid = geomIdInThisCase;
                sdba = new SimpleDbAction() {

                        @Override
                        public void executeAction(final Connection conn) throws SQLException {
                            final Statement checker = conn.createStatement();
                            final String s = "SELECT count(*) FROM FLAECHENINFO WHERE geometrie=" + gid;
                            final ResultSet check = checker.executeQuery(s);
                            check.next();
                            final int counter = check.getInt(1);
                            log.info("Test:" + s + "(" + counter + ")");

                            if (counter == 0) {
                                super.executeAction(conn);
                            }
                        }
                    };
                sdba.setStatement("DELETE FROM geom where "
                            + "id=" + geomIdInThisCase);
                sdba.setDescription("Geometrie in >>GEOM<< l\u00F6schen");
                sdba.setType(SimpleDbAction.DELETE);
            } else {
                sdba = null;
            }
        } else if (hasChanged() && (geom != null) && (geom_id > 0)) {
            sdba.setStatement("update geom set "
                        + "geo_field='" + PostGisGeometryFactory.getPostGisCompliantDbString(this.geom) + "' "
                        + "where id=" + this.geom_id);
            sdba.setDescription("Ver\u00E4ndere die Tabelle >>GEOM<<");
            sdba.setType(SimpleDbAction.UPDATE);
        } else if ((geom != null) && (geom_id < 0)) {
            sdba.setStatement("insert into geom "
                        + "(id,geo_field) "
                        + "VALUES("
                        + "currval('GEOM_SEQ'),'" + PostGisGeometryFactory.getPostGisCompliantDbString(this.geom) + "' "
                        + ")");
            sdba.setDescription("F\u00FCllen der Tabelle >>GEOM<<");
            sdba.setType(SimpleDbAction.INSERT);
        } else {
            sdba = null;
        }
        return sdba;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private SimpleDbAction getStatement4Index() {
        SimpleDbAction sdba = new SimpleDbAction();
        int geomIdInThisCase = 0;
        if (isGeometryRemoved()) {
            geomIdInThisCase = removedGeometryId;
        } else {
            geomIdInThisCase = geom_id;
        }
        if (isMarkedForDeletion() || isGeometryRemoved()) { // Auch beim Ausschneiden
            // Beim loeschen einer Geometrie
            sdba.setStatement("DELETE FROM cs_all_attr_mapping where "
                        + "class_id=" + Main.KASSENZEICHEN_CLASS_ID + " and "
                        + "object_id=" + kassenzeichen + " and "
                        + "attr_class_id=" + Main.GEOM_CLASS_ID + " and "
                        + "attr_object_id=" + geomIdInThisCase);
            sdba.setDescription("Geometrie im Index l\u00F6schen");
            sdba.setType(SimpleDbAction.DELETE);
        } else if ((geom != null) && ((clipboardStatus == CUTTED) || (clipboardStatus == COPIED))) { // beim Einfuegen
            sdba.setStatement(
                "insert into cs_all_attr_mapping (class_id, object_id, attr_class_id, attr_object_id) values ( "
                        + Main.KASSENZEICHEN_CLASS_ID
                        + ","
                        + this.kassenzeichen
                        + ","
                        + Main.GEOM_CLASS_ID
                        + ","
                        + geomIdInThisCase
                        + ")");
            sdba.setDescription("Index der Geometrie anlegen.");
            sdba.setType(SimpleDbAction.INSERT);
        } else if (hasChanged() && (geom != null) && (geom_id > 0)) {
            // Bei einer reinen Ver\u00E4nderung passiert hier nix
            sdba = null;
        } else if ((geom != null) && (geom_id < 0)) {
            sdba.setStatement(
                "insert into cs_all_attr_mapping (class_id, object_id, attr_class_id, attr_object_id) values ( "
                        + Main.KASSENZEICHEN_CLASS_ID
                        + ","
                        + this.kassenzeichen
                        + ","
                        + Main.GEOM_CLASS_ID
                        + ","
                        + "currval('GEOM_SEQ')"
                        + ")");
            sdba.setDescription("Index der Geometrie anlegen.");
            sdba.setType(SimpleDbAction.INSERT);
        } else {
            sdba = null;
        }
        return sdba;
    }
    // </editor-fold>
    /**
     * <editor-fold defaultstate="collapsed" desc=" Getter und Setter">.
     *
     * @return  DOCUMENT ME!
     */
    public int getFl_id() {
        return fl_id;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  fl_id  DOCUMENT ME!
     */
    public void setFl_id(final int fl_id) {
        this.fl_id = fl_id;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getFlaecheninfo_id() {
        return this.flaecheninfo_id;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  id  DOCUMENT ME!
     */
    public void setFlaecheninfo_id(final int id) {
        flaecheninfo_id = id;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getBezeichnung() {
        return bezeichnung;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  bezeichnung  DOCUMENT ME!
     */
    public void setBezeichnung(final String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public SimpleDocumentModel getBezeichnungsModel() {
        return bezeichnungsModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Integer getGr_grafik() {
        return gr_grafik;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getGr_grafikString() {
        if (gr_grafik != null) {
            return gr_grafik.toString();
        } else {
            return "";
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  gr_grafik  DOCUMENT ME!
     */
    public void setGr_grafik(final Integer gr_grafik) {
        this.gr_grafik = gr_grafik;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public SimpleDocumentModel getGr_GrafikModel() {
        return gr_grafikModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Integer getGr_korrektur() {
        return gr_korrektur;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getGr_korrekturString() {
        if (gr_korrektur != null) {
            return gr_korrektur.toString();
        } else {
            return "";
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  gr_korrektur  DOCUMENT ME!
     */
    public void setGr_korrektur(final Integer gr_korrektur) {
        this.gr_korrektur = gr_korrektur;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public SimpleDocumentModel getGr_KorrekturModel() {
        return gr_korrekturModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getArt() {
        return art;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  art  DOCUMENT ME!
     */
    public void setArt(final int art) {
        this.art = art;
        // if (art>0) log.fatal(art+","+reverseArtHm.keySet().toArray()[art-1]);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getArt_abk() {
        return (String)artHm.get(new Integer(art));
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ComboBoxModel getArtModel() {
        return artModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getGrad() {
        return grad;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  grad  DOCUMENT ME!
     */
    public void setGrad(final int grad) {
        // if (grad>0) log.fatal(grad+","+reverseGradHm.keySet().toArray()[grad-1]);
        this.grad = grad;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getGrad_abk() {
        return (String)gradHm.get(new Integer(grad));
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ComboBoxModel getGradModel() {
        return gradModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Integer getAnteil() {
        return anteil;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getAnteilString() {
        if (anteil != null) {
            return anteil.toString();
        } else {
            return "";
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  anteil  DOCUMENT ME!
     */
    public void setAnteil(final Integer anteil) {
        this.anteil = anteil;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public SimpleDocumentModel getAnteilModel() {
        return anteilModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getErfassungsdatum() {
        return erfassungsdatum;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  erfassungsdatum  DOCUMENT ME!
     */
    public void setErfassungsdatum(final String erfassungsdatum) {
        this.erfassungsdatum = erfassungsdatum;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public SimpleDocumentModel getErfassungsdatumModel() {
        return erfassungsdatumModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getVeranlagungsdatum() {
        return veranlagungsdatum;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  veranlagungsdatum  DOCUMENT ME!
     */
    public void setVeranlagungsdatum(final String veranlagungsdatum) {
        this.veranlagungsdatum = veranlagungsdatum;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public SimpleDocumentModel getVeranlagungsdatumModel() {
        return veranlagungsdatumModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getBemerkung() {
        return bemerkung;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  bemerkung  DOCUMENT ME!
     */
    public void setBemerkung(final String bemerkung) {
        this.bemerkung = bemerkung;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public SimpleDocumentModel getBemerkungsModel() {
        return bemerkungsModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isSperre() {
        return sperre;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  sperre  DOCUMENT ME!
     */
    public void setSperre(final boolean sperre) {
        this.sperre = sperre;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getBem_sperre() {
        return bem_sperre;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public SimpleDocumentModel getBem_sperreModel() {
        return bem_sperreModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  bem_sperre  DOCUMENT ME!
     */
    public void setBem_sperre(final String bem_sperre) {
        this.bem_sperre = bem_sperre;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ButtonModel getSperrenModel() {
        return sperreModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getFeb_id() {
        return feb_id;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  feb_id  DOCUMENT ME!
     */
    public void setFeb_id(final String feb_id) {
        this.feb_id = feb_id;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public SimpleDocumentModel getFeb_IdModel() {
        return this.feb_idModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ComboBoxModel getBeschreibungsmodel() {
        return beschreibungsmodel;
    }
    // </editor-fold>
    /**
     * <editor-fold defaultstate="collapsed" desc=" Initialisierung der Modelle">.
     */
    public void initModels() {
        initBezeichnungsModel();
        initGrGrafikModel();
        initGrKorrekturModel();
        initArtModel();
        initGradModel();
        initAnteilModel();
        initErfassungsdatumModel();
        initVeranlagungsdatumModel();
        initBemerkungsModel();
        initSperreModel();
        initFebIdModel();
        initBeschreibungsmodel();
        this.initBemerkungSperreModel();
    }

    /**
     * DOCUMENT ME!
     */
    private void initBezeichnungsModel() {
        bezeichnungsModel = new SimpleDocumentModel() {

                @Override
                public void insertString(final int offset, final String string, final AttributeSet attributes)
                        throws BadLocationException {
                    super.insertString(offset, string.toUpperCase(), attributes);
                }

                @Override
                public void assignValue(final String newValue) {
                    bezeichnung = newValue;
                    fireValidationStateChanged();
                }

                @Override
                public int getStatus() {
                    boolean numerisch = false;
                    try {
                        final Integer tester = new Integer(bezeichnung);
                        numerisch = true;
                    } catch (Exception e) {
                        numerisch = false;
                    }

                    if ((art == 1) || (art == 2)) {
                        if (!numerisch) {
                            statusDescription = "Fl\u00E4chenbezeichnung muss zw. 0 und 1000 liegen.";
                            return Validatable.ERROR;
                        }

                        try {
                            final Integer tester = new Integer(bezeichnung);
                            if ((tester.intValue() > 1000) || (tester.intValue() < 0)) {
                                statusDescription =
                                    "Fl\u00E4chenbezeichnung muss zw. 0 und 1000 bzw. zw. A und BBB liegen.";
                                return Validatable.ERROR;
                            }
                        } catch (Exception e) {
                        }
                    } else {
                        if (bezeichnung != null) {
                            final int len = bezeichnung.length();
                            if (numerisch || ((len > 3) || ((len == 3) && (bezeichnung.compareTo("BBB") > 0)))) {
                                statusDescription = "Fl\u00E4chenbezeichnung muss zw. A und BBB liegen.";
                                return Validatable.ERROR;
                            }
                        }
                    }
                    statusDescription = "";
                    return Validatable.VALID;
                }

                @Override
                public void showAssistent(final Component parent) {
                    if (context != null) {
                        if (getStatus() == Validatable.ERROR) {
                            final int answer = JOptionPane.showConfirmDialog(
                                    parent,
                                    "Soll die n\u00E4chste freie Bezeichnung gew\u00E4hlt werden",
                                    "Bezeichnung automatisch setzen",
                                    JOptionPane.YES_NO_OPTION);
                            if (answer == JOptionPane.YES_OPTION) {
                                final String newValue = context.getValidFlaechenname(art);
                                log.info("Neuer Wert (von Assistent):" + newValue);
                                try {
                                    bezeichnungsModel.remove(0, bezeichnung.length());
                                    bezeichnungsModel.insertString(0, newValue, null);
                                } catch (Exception e) {
                                    if (log.isDebugEnabled()) {
                                        log.debug("???", e);
                                    }
                                }
                            }
                        }
                    }
                }
            };
    }

    /**
     * DOCUMENT ME!
     */
    private void initGrGrafikModel() {
        gr_grafikModel = new SimpleIntegerDocumentModel() {

                @Override
                public void assignValue(final Integer newInteger) {
                    gr_grafik = newInteger;
                    fireValidationStateChanged();
                    if (log.isDebugEnabled()) {
                        log.debug("fireValidationStateChanged");
                    }
                }

                @Override
                public int getStatus() {
                    if (gr_grafik == null) {
                        statusDescription = "Wert ist leer";
                        return Validatable.ERROR;
                    }
                    if ((gr_grafik != null) && (geom != null)
                                && !(gr_grafik.equals(new Integer((int)(geom.getArea()))))) {
                        statusDescription = "Fl\u00E4che der Geometrie stimmt nicht \u00FCberein ("
                                    + ((int)(geom.getArea())) + ")";
                        return Validatable.WARNING;
                    } else if (gr_korrektur != null) {
                        final int diff = gr_korrektur.intValue() - gr_grafik.intValue();
                        if (Math.abs(diff) > 20) {
                            statusDescription = "Differenz zwischen Korrekturwert und Gr\u00F6\u00DFe > 20m²";
                            return Validatable.WARNING;
                        }
                    }
                    statusDescription = "";
                    return Validatable.VALID;
                }

                @Override
                public void showAssistent(final Component parent) {
                    if ((context != null) && Main.THIS.isInEditMode()) {
                        if (((getStatus() == Validatable.WARNING) || (getStatus() == Validatable.ERROR))
                                    && (geom != null)) {
                            final int answer = JOptionPane.showConfirmDialog(
                                    parent,
                                    "Soll die Gr\u00F6\u00DFe aus der Grafik \u00FCbernommen werden",
                                    "Gr\u00F6\u00DFe automatisch setzen",
                                    JOptionPane.YES_NO_OPTION);
                            if (answer == JOptionPane.YES_OPTION) {
                                // gr_grafik=new Integer((int)(geom.getArea()));
                                try {
                                    // gr_grafikModel.remove(0, gr_grafik.toString().length());
                                    final Integer tmp = new Integer((int)(geom.getArea()));
                                    gr_grafikModel.insertNewString(tmp.toString(), null);
                                } catch (Exception e) {
                                    if (log.isDebugEnabled()) {
                                        log.debug("???", e);
                                    }
                                }

                                context.fireTableDataChanged();
                            }
                        }
                    }
                }
            };
    }

    /**
     * DOCUMENT ME!
     */
    private void initGrKorrekturModel() {
        gr_korrekturModel = new SimpleIntegerDocumentModel() {

                @Override
                public void assignValue(final Integer newInteger) {
                    gr_korrektur = newInteger;
                    fireValidationStateChanged();
                }

                @Override
                public int getStatus() {
                    if (gr_grafik == null) {
                        statusDescription = "Wert ist leer";
                        return Validatable.WARNING;
                    } else if (gr_korrektur != null) {
                        final int diff = gr_korrektur.intValue() - gr_grafik.intValue();
                        if (Math.abs(diff) > 20) {
                            statusDescription = "Differenz zwischen Korrekturwert und Gr\u00F6\u00DFe > 20m²";
                            return Validatable.WARNING;
                        }
                    }
                    statusDescription = "";
                    return Validatable.VALID;
                }
            };
    }

    /**
     * DOCUMENT ME!
     */
    private void initArtModel() {
        artModel = new DefaultComboBoxModel(reverseArtHm.keySet().toArray()) {

                @Override
                public void setSelectedItem(final Object selItem) {
                    try {
                        super.setSelectedItem(selItem);
                        if (log.isDebugEnabled()) {
                            log.debug("selItem:" + selItem);
                        }
                        art = new Integer(reverseArtHm.get(selItem).toString()).intValue();
                        if (log.isDebugEnabled()) {
                            log.debug("art:" + art);
                        }
                        ((SimpleDocumentModel)bezeichnungsModel).fireValidationStateChanged();
                    } catch (Exception e) {
                        log.warn("Fehler ind initArtModel:", e);
                    }
                }
            };
    }

    /**
     * DOCUMENT ME!
     */
    private void initGradModel() {
        gradModel = new DefaultComboBoxModel(reverseGradHm.keySet().toArray()) {

                @Override
                public void setSelectedItem(final Object selItem) {
                    super.setSelectedItem(selItem);
                    grad = new Integer(reverseGradHm.get(selItem).toString()).intValue();
                }
            };
    }

    /**
     * DOCUMENT ME!
     */
    private void initBeschreibungsmodel() {
        Boolean dach = null;
        if (artModel.getSelectedItem() != null) {
            final String artMString = artModel.getSelectedItem().toString();
            if (artMString.matches("DF|GDF")) {
                dach = true;
            } else {
                dach = false;
            }
        }
        Vector v = new Vector();
        if (dach == null) {
            v = new Vector(reverseBeschreibungHm.keySet());
        } else {
            if (dach == Boolean.TRUE) {
                for (final FlaechenBeschreibung fb : reverseBeschreibungHm.keySet()) {
                    if (fb.isDachflaeche()) {
                        v.add(fb);
                    }
                }
            } else {
                for (final FlaechenBeschreibung fb : reverseBeschreibungHm.keySet()) {
                    if (!fb.isDachflaeche()) {
                        v.add(fb);
                    }
                }
            }
        }
        beschreibungsmodel = new DefaultComboBoxModel(v) {

                @Override
                public void setSelectedItem(final Object selItem) {
                    super.setSelectedItem(selItem);
                    if (selItem != null) {
                        beschreibung = new Integer(reverseBeschreibungHm.get(selItem).toString()).intValue();
                    } else {
                        beschreibung = null;
                    }
                }
            };
    }

    /**
     * DOCUMENT ME!
     */
    private void initAnteilModel() {
        anteilModel = new SimpleIntegerDocumentModel() {

                @Override
                public void assignValue(final Integer newInteger) {
                    anteil = newInteger;
                    fireValidationStateChanged();
                }

                @Override
                public int getStatus() {
                    if (anteil != null) {
                        if ((gr_korrektur != null) && (anteil.intValue() > gr_korrektur.intValue())) {
                            statusDescription = "Anteil ist h\u00F6her als Gr\u00F6\u00DFe.";
                            return Validatable.ERROR;
                        } else if ((gr_grafik != null) && (anteil.intValue() > gr_grafik.intValue())) {
                            statusDescription = "Anteil ist h\u00F6her als Gr\u00F6\u00DFe.";
                            return Validatable.ERROR;
                        }
                    }
                    return Validatable.VALID;
                }
            };
    }

    /**
     * DOCUMENT ME!
     */
    private void initErfassungsdatumModel() {
        erfassungsdatumModel = new SimpleDocumentModel() {

                @Override
                public void assignValue(final String newValue) {
                    erfassungsdatum = newValue;
                    fireValidationStateChanged();
                }

                @Override
                public int getStatus() {
                    // java.sql.Date d = new java.sql.Date
                    final java.text.DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY);
                    try {
                        df.parse(erfassungsdatum);
                    } catch (java.text.ParseException pe) {
                        statusDescription = "Kein g\u00FCltiges Datum.";
                        return Validatable.ERROR;
                    }
                    return Validatable.VALID;
                }
            };
    }

    /**
     * DOCUMENT ME!
     */
    private void initVeranlagungsdatumModel() {
        veranlagungsdatumModel = new SimpleDocumentModel() {

                @Override
                public void assignValue(final String newValue) {
                    veranlagungsdatum = newValue;
                    fireValidationStateChanged();
                }

                @Override
                public int getStatus() {
                    if (veranlagungsdatum != null) {
                        final boolean b = Pattern.matches(
                                "\\d\\d/(01|02|03|04|05|06|07|08|09|10|11|12)",
                                veranlagungsdatum);
                        if (b) {
                            return Validatable.VALID;
                        }
                    }
                    statusDescription = "Veranlagungsdatum muss im Format JJ/MM eingegeben werden.";
                    return Validatable.ERROR;
                }
            };
    }

    /**
     * DOCUMENT ME!
     */
    private void initBemerkungsModel() {
        bemerkungsModel = new SimpleDocumentModel() {

                @Override
                public void assignValue(final String newValue) {
                    bemerkung = newValue;
                }
            };
    }

    /**
     * DOCUMENT ME!
     */
    private void initSperreModel() {
        sperreModel = new javax.swing.JToggleButton.ToggleButtonModel() {

                @Override
                public void setSelected(final boolean b) {
                    if (log.isDebugEnabled()) {
                        log.debug("sperreModel: setSelected(" + b + ")");
                    }
                    super.setSelected(b);
                    final boolean oldSperre = sperre;
                    sperre = b;
                    if (b && (oldSperre != sperre)) {
                        String answer = "";
                        while (answer.trim().length() == 0) {
                            answer = JOptionPane.showInputDialog(de.cismet.verdis.gui.Main.THIS.getRootPane(),
                                    "Bitte eine Bemerkung zur Sperre angeben.",
                                    bem_sperre);
                        }
                        bem_sperre = answer;
                        updateBemSperreModel();
                    }
                    if (!sperre) {
                        try {
                            bem_sperreModel.remove(0, bem_sperreModel.getLength());
                        } catch (Exception e) {
                            if (log.isDebugEnabled()) {
                                log.debug("???", e);
                            }
                        }
                    } else {
                        updateBemSperreModel();
                    }

                    final javax.swing.JToggleButton.ToggleButtonModel t;
                    fireActionPerformed(new ActionEvent(this, 0, "AfterDialog"));
                }

                @Override
                public boolean isSelected() {
                    return sperre;
                }
            };
    }

    /**
     * DOCUMENT ME!
     */
    private void initFebIdModel() {
        feb_idModel = new SimpleDocumentModel() {

                @Override
                public void assignValue(final String newValue) {
                    feb_id = newValue;
                    fireValidationStateChanged();
                }

                @Override
                public int getStatus() {
                    if (feb_id != null) {
                        try {
                            final Integer tester = new Integer(feb_id);
                            if ((tester.intValue() < 20000001) || (tester.intValue() > 20200000)) {
                                statusDescription = "FEB muss zwischen 20.000.000 und 20.200.000 liegen.";
                                return Validatable.WARNING;
                            }
                        } catch (Exception e) {
                            statusDescription = "FEB Nr darf nur Zahlen enthalten";
                            return Validatable.WARNING;
                        }
                    }
                    return Validatable.VALID;
                }
            };
    }

    /**
     * DOCUMENT ME!
     */
    private void initBemerkungSperreModel() {
        this.bem_sperreModel = new SimpleDocumentModel();
    }
    // </editor-fold>

    //~ Instance fields --------------------------------------------------------

    private int fl_id;
    private String bezeichnung;
    private transient SimpleDocumentModel bezeichnungsModel;
    private Integer gr_grafik;
    private transient SimpleDocumentModel gr_grafikModel;
    private Integer gr_korrektur;
    private transient SimpleDocumentModel gr_korrekturModel;
    private int art;
    // private String art_abk;
    private transient ComboBoxModel artModel;
    private int grad;
    private transient ComboBoxModel gradModel;
    // private String grad_abk;
    private Integer anteil;
    private transient SimpleDocumentModel anteilModel;
    private String erfassungsdatum;
    private transient SimpleDocumentModel erfassungsdatumModel;
    private String veranlagungsdatum;
    private transient SimpleDocumentModel veranlagungsdatumModel;
    private String bemerkung;
    private transient SimpleDocumentModel bemerkungsModel;
    private boolean sperre;
    private transient ButtonModel sperreModel;
    private String bem_sperre;
    private transient SimpleDocumentModel bem_sperreModel;
    private String feb_id;
    private transient SimpleDocumentModel feb_idModel;
    private Flaeche backup;
    private transient org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private int flaecheninfo_id = -1;
    private String kassenzeichen;
    private LinkedHashMap reverseArtHm;
    private LinkedHashMap reverseGradHm;
    private LinkedHashMap artHm;
    private LinkedHashMap gradHm;
    private LinkedHashMap<Integer, FlaechenBeschreibung> beschreibungHm =
        new LinkedHashMap<Integer, FlaechenBeschreibung>();
    private LinkedHashMap<FlaechenBeschreibung, Integer> reverseBeschreibungHm =
        new LinkedHashMap<FlaechenBeschreibung, Integer>();
    private Integer beschreibung;
    private transient ComboBoxModel beschreibungsmodel;
    private com.vividsolutions.jts.geom.Geometry geom = null;
    private int geom_id = -1;
    private boolean markedForDeletion = false;
    private transient FlaechenUebersichtsTableModel context = null;
    private boolean newFlaeche = false;
    private boolean editable;
    private Collection<String> teileigentumCrossReferences = null;
    private boolean geometryRemoved = false;
    private int removedGeometryId = -1;
    private int clipboardStatus = 0;
    private transient Flaeche THIS = null;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of FlaechenDetails.
     *
     * @param  reverseArtHm           DOCUMENT ME!
     * @param  reverseGradHm          DOCUMENT ME!
     * @param  artHm                  DOCUMENT ME!
     * @param  gradHm                 DOCUMENT ME!
     * @param  beschreibungHm         DOCUMENT ME!
     * @param  reverseBeschreibungHm  DOCUMENT ME!
     */
    public Flaeche(final LinkedHashMap reverseArtHm,
            final LinkedHashMap reverseGradHm,
            final LinkedHashMap artHm,
            final LinkedHashMap gradHm,
            final LinkedHashMap beschreibungHm,
            final LinkedHashMap reverseBeschreibungHm) {
        this.reverseArtHm = reverseArtHm;
        this.reverseGradHm = reverseGradHm;
        this.artHm = artHm;
        this.gradHm = gradHm;
        this.beschreibungHm = beschreibungHm;
        this.reverseBeschreibungHm = reverseBeschreibungHm;
        initModels();
        THIS = this;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  context  DOCUMENT ME!
     */
    public void setContext(final FlaechenUebersichtsTableModel context) {
        this.context = context;
    }

    @Override
    public void setGeometry(final com.vividsolutions.jts.geom.Geometry geom) {
        this.geom = geom;
    }

    @Override
    public com.vividsolutions.jts.geom.Geometry getGeometry() {
        return geom;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kz  DOCUMENT ME!
     */
    public void setKassenzeichen(final String kz) {
        kassenzeichen = kz;
    }

    /**
     * DOCUMENT ME!
     */
    public void backup() {
        try {
            backup = (Flaeche)(this.clone());
        } catch (Exception e) {
            log.error("Fehler beim Clonen.", e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Integer getBeschreibung() {
        return beschreibung;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public FlaechenBeschreibung getFlaechenBeschreibung() {
        return beschreibungHm.get(beschreibung);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  beschreibung  DOCUMENT ME!
     */
    public void setBeschreibung(final Integer beschreibung) {
        this.beschreibung = beschreibung;
    }

    /**
     * DOCUMENT ME!
     */
    public void setToBackupFlaeche() {
        if (backup != null) {
            setAnteil(backup.anteil);
            setArt(backup.art);
            // setArt_abk(backup.art_abk);
            setBemerkung(backup.bemerkung);
            setBem_sperre(backup.bem_sperre);
            setBezeichnung(backup.bezeichnung);
            setErfassungsdatum(backup.erfassungsdatum);
            setFeb_id(backup.feb_id);
            setFl_id(backup.fl_id);
            setGr_grafik(backup.gr_grafik);
            setGr_korrektur(backup.gr_korrektur);
            setGrad(backup.grad);
            // setGrad_abk(backup.grad_abk);
            setSperre(backup.sperre);
            setVeranlagungsdatum(backup.veranlagungsdatum);

            setClipboardStatus(backup.clipboardStatus);
            setContext(backup.context);
            setGeom_id(backup.geom_id);
            if (geom != null) {
                setGeometry((Geometry)(backup.geom.clone()));
            } else {
                setGeometry(null);
            }

            setKassenzeichen(backup.kassenzeichen);
            setFlaecheninfo_id(backup.flaecheninfo_id);
            setBeschreibung(backup.beschreibung);

            sync();
        }
    }

    @Override
    public Object clone() {
        final Flaeche f = new Flaeche(
                reverseArtHm,
                reverseGradHm,
                artHm,
                gradHm,
                beschreibungHm,
                reverseBeschreibungHm);
        f.setAnteil(anteil);
        f.setArt(art);
        f.setBemerkung(bemerkung);
        f.setBem_sperre(bem_sperre);
        f.setBezeichnung(bezeichnung);
        f.setErfassungsdatum(erfassungsdatum);
        f.setFeb_id(feb_id);
        f.setFl_id(fl_id);
        f.setGr_grafik(gr_grafik);
        f.setGr_korrektur(gr_korrektur);
        f.setGrad(grad);
        f.setSperre(sperre);
        f.setVeranlagungsdatum(veranlagungsdatum);
        f.setClipboardStatus(clipboardStatus);
        f.setContext(context);
        f.setGeom_id(geom_id);
        if (geom != null) {
            f.setGeometry((Geometry)geom.clone());
        } else {
            f.setGeometry(null);
        }

        f.setKassenzeichen(this.kassenzeichen);
        f.setFlaecheninfo_id(this.flaecheninfo_id);
        f.setGeometryRemoved(geometryRemoved);
        f.setRemovedGeometryId(removedGeometryId);
        f.setBeschreibung(beschreibung);
        return f;
    }

    /**
     * Mit dieser Methode werden die Daten aus dem Resultset in das Objekt \u00FCbertragen.
     *
     * @param   oa  DOCUMENT ME!
     *
     * @throws  Exception  java.lang.Exception
     */
    // <editor-fold defaultstate="collapsed" desc=" fillFromObjectArray() ">
    public void fillFromObjectArray(final Object[] oa) throws Exception {
        fl_id = new Integer(oa[0].toString()).intValue();
        try {
            anteil = new Integer((int)(new Double(oa[1].toString()).doubleValue()));
            if (log.isDebugEnabled()) {
                log.debug("Anteil not null");
            }
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Fehler beim Setzen von anteil", e);
            }
            anteil = null;
        }
        try {
            bezeichnung = oa[2].toString();
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Fehler beim Setzen von bezeichnung", e);
            }
            bezeichnung = null;
        }
        try {
            bemerkung = oa[3].toString();
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Fehler beim Setzen von bemerkung", e);
            }
            bemerkung = null;
        }
        try {
            final java.sql.Date d = (java.sql.Date)oa[4];
            erfassungsdatum = java.text.DateFormat.getDateInstance().format(d);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Fehler beim Setzen von erfassungsdatum", e);
            }
            erfassungsdatum = null;
        }
        try {
            veranlagungsdatum = oa[5].toString();
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Fehler beim Setzen von veranlagungsdatum", e);
            }
            veranlagungsdatum = null;
        }
        final String sperrString = oa[6].toString().trim().toUpperCase();
        if (sperrString.equals("T")) {
            sperre = true;
        } else {
            sperre = false;
        }
        try {
            bem_sperre = oa[7].toString();
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Fehler beim Setzen von bem_sperre", e);
            }
            bem_sperre = null;
        }
        try {
            gr_grafik = new Integer(oa[8].toString());
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Fehler beim Setzen von gr_grafik", e);
            }
            gr_grafik = null;
        }
        try {
            gr_korrektur = new Integer(oa[9].toString());
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Fehler beim Setzen von gr_korrektur", e);
            }
            gr_korrektur = null;
        }
        art = new Integer(oa[14].toString()).intValue();
        grad = new Integer(oa[15].toString()).intValue();
        try {
            feb_id = oa[16].toString();
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Fehler beim Setzen von feb_id", e);
            }
            feb_id = null;
        }
        flaecheninfo_id = new Integer(oa[17].toString()).intValue();
        try {
            if (log.isDebugEnabled()) {
                log.debug("Geometrie:" + oa[18]);
            }
            final PGgeometry postgresGeom = (PGgeometry)oa[18];
            final org.postgis.Geometry postgisGeom = postgresGeom.getGeometry();
            setGeometry(PostGisGeometryFactory.createJtsGeometry(postgisGeom));
        } catch (Exception e) {
            log.warn("Fehler beim Anlegen der Geometrie.", e);
        }

        try {
            this.geom_id = new Integer(oa[19].toString()).intValue();
        } catch (Exception e) {
            log.warn("Fehler beim Anlegen der GeometrieID.", e);
        }

        try {
            beschreibung = new Integer(oa[20].toString()).intValue();
            if (log.isDebugEnabled()) {
                log.debug("beschreibung=" + beschreibung);
            }
        } catch (Exception e) {
            log.warn("Fehler beim Anlegen der BeschreibungsID.", e);
        }
        updateModels();
    }
    // </editor-fold>
    /**
     * DOCUMENT ME!
     */
    public void sync() {
        this.initModels();
        this.updateModels();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  container  DOCUMENT ME!
     */
    public void addStatements(final Vector container) {
        add2Container(container, getStatement4Flaechen());
        add2Container(container, getStatement4Flaeche());
        add2Container(container, getStatement4Flaecheninfo());
        add2Container(container, getStatement4Geom());
        add2Container(container, getStatement4Index());
        setClipboardStatus(Flaeche.NONE);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  container  DOCUMENT ME!
     * @param  sdba       DOCUMENT ME!
     */
    private void add2Container(final Vector container, final SimpleDbAction sdba) {
        if (sdba != null) {
            container.add(sdba);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   s  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String nullAwareSqlStringMaker(final String s) {
        if (s != null) {
            return "'" + s + "'";
        } else {
            return null;
        }
    }
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isValid() {
        if ((bezeichnungsModel.getStatus() == Validatable.ERROR) || (gr_grafikModel.getStatus() == Validatable.ERROR)
                    || (gr_korrekturModel.getStatus() == Validatable.ERROR)
                    || (anteilModel.getStatus() == Validatable.ERROR)
                    || (erfassungsdatumModel.getStatus() == Validatable.ERROR)
                    || (veranlagungsdatumModel.getStatus() == Validatable.ERROR)
                    || (feb_idModel.getStatus() == Validatable.ERROR)) {
            return false;
        } else {
            return true;
        }
    }
    /**
     * DOCUMENT ME!
     *
     * @param   bez  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean isValidBezeichnung(final String bez) {
        return true;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   d  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean isValidAnteil(final String d) {
        return true;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   d  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean isValidDatum(final String d) {
        return true;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   d  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean isValidVeranlagungsdatum(final String d) {
        return true;
    }

    /**
     * DOCUMENT ME!
     */
    public void updateModels() {
        // bringe die models auf den richtigen stand
        try {
            bezeichnungsModel.insertString(0, bezeichnung, null);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("???", e);
            }
        }

        try {
            gr_grafikModel.insertString(0, gr_grafik.toString(), null);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("???", e);
            }
        }

        try {
            gr_korrekturModel.insertString(0, gr_korrektur.toString(), null);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("???", e);
            }
        }

        try {
            anteilModel.insertString(0, anteil.toString(), null);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("???", e);
            }
        }

        try {
            erfassungsdatumModel.insertString(0, erfassungsdatum.toString(), null);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("???", e);
            }
        }

        try {
            veranlagungsdatumModel.insertString(0, veranlagungsdatum.toString(), null);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("???", e);
            }
        }

        try {
            bemerkungsModel.insertString(0, bemerkung.toString(), null);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("???", e);
            }
        }

        try {
            this.feb_idModel.insertString(0, feb_id.toString(), null);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("???", e);
            }
        }

        if (this.getArt_abk() != null) {
            artModel.setSelectedItem(getArt_abk());
        }
        if (this.getGrad_abk() != null) {
            gradModel.setSelectedItem(getGrad_abk());
        }

        initBeschreibungsmodel();
        beschreibungsmodel.setSelectedItem(getFlaechenBeschreibung());

        if (sperre) {
            updateBemSperreModel();
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void updateBemSperreModel() {
        try {
            this.bem_sperreModel.remove(0, bem_sperreModel.getLength());
            this.bem_sperreModel.insertString(0, bem_sperre.toString(), null);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("???", e);
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void initAfterXMLLoad() {
        log = org.apache.log4j.Logger.getLogger(this.getClass());
        backup = (Flaeche)clone();
        initModels();
    }
    @Override
    public boolean equals(final Object o) {
        // <editor-fold defaultstate="collapsed" desc="Vergleich aller Werte">
        final Flaeche f;
        try {
            f = (Flaeche)o;
        } catch (Exception e) {
            return false;
        }
        final boolean t1 = (((bezeichnung == null) && (f.bezeichnung == null))
                        || ((bezeichnung != null) && bezeichnung.equals(f.bezeichnung)));
        final boolean t2 = (((gr_grafik == null) && (f.gr_grafik == null))
                        || ((gr_grafik != null) && gr_grafik.equals(f.gr_grafik)));
        final boolean t3 = (((gr_korrektur == null) && (f.gr_korrektur == null))
                        || ((gr_korrektur != null) && gr_korrektur.equals(f.gr_korrektur)));
        final boolean t4 = (art == f.art);
        final boolean t5 = (grad == f.grad);
        final boolean t6 = (((anteil == null) && (f.anteil == null)) || ((anteil != null) && anteil.equals(f.anteil)));
        final boolean t7 = (((erfassungsdatum == null) && (f.erfassungsdatum == null))
                        || ((erfassungsdatum != null) && erfassungsdatum.equals(f.erfassungsdatum)));
        final boolean t8 = (((veranlagungsdatum == null) && (f.veranlagungsdatum == null))
                        || ((veranlagungsdatum != null) && veranlagungsdatum.equals(f.veranlagungsdatum)));
        final boolean t9 = (((bemerkung == null) & (f.bemerkung == null))
                        || ((bemerkung != null) && bemerkung.equals(f.bemerkung)));
        final boolean t10 = (sperre == f.sperre);
        final boolean t11 = (((bem_sperre == null) && (f.bem_sperre == null))
                        || ((bem_sperre != null) && bem_sperre.equals(f.bem_sperre)));
        final boolean t12 = (((feb_id == null) & (f.feb_id == null)) || ((feb_id != null) && feb_id.equals(f.feb_id)));
        final boolean t13 = (((kassenzeichen == null) && (f.kassenzeichen == null))
                        || ((kassenzeichen != null) && kassenzeichen.equals(f.kassenzeichen)));
        final boolean t14 = (((geom == null) && (f.geom == null))
                        || ((f.geom != null) && (geom != null) && geom.equalsExact(f.geom)));
        final boolean t15 = (geom_id == f.geom_id);
        final boolean t16 = ((beschreibung == null) && (f.beschreibung == null))
                    || ((beschreibung != null) && beschreibung.equals(f.beschreibung));

        try {
            if ((((bezeichnung == null) && (f.bezeichnung == null))
                            || ((bezeichnung != null) && bezeichnung.equals(f.bezeichnung)))
                        && (((gr_grafik == null) && (f.gr_grafik == null))
                            || ((gr_grafik != null) && gr_grafik.equals(f.gr_grafik)))
                        && (((gr_korrektur == null) && (f.gr_korrektur == null))
                            || ((gr_korrektur != null) && gr_korrektur.equals(f.gr_korrektur)))
                        && (art == f.art)
                        && (grad == f.grad)
                        && (((anteil == null) && (f.anteil == null)) || ((anteil != null) && anteil.equals(f.anteil)))
                        && (((erfassungsdatum == null) && (f.erfassungsdatum == null))
                            || ((erfassungsdatum != null) && erfassungsdatum.equals(f.erfassungsdatum)))
                        && (((veranlagungsdatum == null) && (f.veranlagungsdatum == null))
                            || ((veranlagungsdatum != null) && veranlagungsdatum.equals(f.veranlagungsdatum)))
                        && (((bemerkung == null) & (f.bemerkung == null))
                            || ((bemerkung != null) && bemerkung.equals(f.bemerkung)))
                        && (sperre == f.sperre)
                        && (((bem_sperre == null) && (f.bem_sperre == null))
                            || ((bem_sperre != null) && bem_sperre.equals(f.bem_sperre)))
                        && (((feb_id == null) & (f.feb_id == null)) || ((feb_id != null) && feb_id.equals(f.feb_id)))
                        && (((kassenzeichen == null) && (f.kassenzeichen == null))
                            || ((kassenzeichen != null) && kassenzeichen.equals(f.kassenzeichen)))
                        && (((geom == null) && (f.geom == null))
                            || ((f.geom != null) && (geom != null) && geom.equalsExact(f.geom)))
                        && (geom_id == f.geom_id)
                        && (geometryRemoved == f.geometryRemoved)
                        && (removedGeometryId == f.removedGeometryId)
                        && (((beschreibung == null) && (f.beschreibung == null))
                            || ((beschreibung != null) && beschreibung.equals(f.beschreibung)))) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("Null", e);
            return false;
        }
        // </editor-fold>
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean hasChanged() {
        if (equals(backup)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public java.awt.Stroke getLineStyle() {
        return null;
    }

    @Override
    public java.awt.Paint getFillingPaint() {
        int alpha = 0;
        if (markedForDeletion) {
            alpha = 25;
        } else {
            alpha = 150;
        }
        switch (art) {
            case 1: {
                return new java.awt.Color(162, 76, 41, alpha);   // Dach
            }
            case 2: {
                return new java.awt.Color(106, 122, 23, alpha);  // Gr\u00FCndach
            }
            case 3: {
                return new java.awt.Color(120, 129, 128, alpha); // versiegelte Fl\u00E4che
            }
            case 4: {
                return new java.awt.Color(159, 155, 108, alpha); // \u00D6kopflaster
            }
            case 5: {
                return new java.awt.Color(138, 134, 132, alpha); // st\u00E4dtische Strassenflaeche
            }
            case 6: {
                return new java.awt.Color(126, 91, 71, alpha);   // staedtische Strassenflaeche Oekopflaster
            }
            default: {
                return null;
            }
        }
    }

    @Override
    public float getTransparency() {
        return 1.0f;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getGeom_id() {
        return geom_id;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  geom_id  DOCUMENT ME!
     */
    public void setGeom_id(final int geom_id) {
        this.geom_id = geom_id;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isMarkedForDeletion() {
        return markedForDeletion;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  markedForDeletion  DOCUMENT ME!
     */
    public void setMarkedForDeletion(final boolean markedForDeletion) {
        this.markedForDeletion = markedForDeletion;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getClipboardStatus() {
        return clipboardStatus;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  clipboardStatus  DOCUMENT ME!
     */
    public void setClipboardStatus(final int clipboardStatus) {
        this.clipboardStatus = clipboardStatus;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isNewFlaeche() {
        return newFlaeche;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  newFlaeche  DOCUMENT ME!
     */
    public void setNewFlaeche(final boolean newFlaeche) {
        this.newFlaeche = newFlaeche;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Flaeche getBackup() {
        return backup;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isGeometryRemoved() {
        return geometryRemoved;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  geometryRemoved  DOCUMENT ME!
     */
    public void setGeometryRemoved(final boolean geometryRemoved) {
        this.geometryRemoved = geometryRemoved;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getRemovedGeometryId() {
        return removedGeometryId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  removedGeometryId  DOCUMENT ME!
     */
    public void setRemovedGeometryId(final int removedGeometryId) {
        this.removedGeometryId = removedGeometryId;
    }

    @Override
    public Paint getLinePaint() {
        return Color.black;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getJoinBackupString() {
        String ret = "<JOIN ";
        ret += "bez=\"" + bezeichnung
                    + "\" gr=\"" + gr_grafik
                    + "\" grk=\"" + gr_korrektur
                    + "\" edat=\"" + erfassungsdatum
                    + "\" vdat=\"" + veranlagungsdatum
                    + "\" sp=\"" + sperre
                    + "\" spbem=\"" + bem_sperre
                    + "\" febid=\"" + feb_id + "  >\n";
        ret += bemerkung;
        if ((bemerkung != null) && (bemerkung.trim().length() > 0) && !bemerkung.endsWith("\n")) {
            ret += "\n";
        }
        ret += "</JOIN>";
        return ret;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getKassenzeichen() {
        return kassenzeichen;
    }

    @Override
    public void setCanBeSelected(final boolean canBeSelected) {
    }

    @Override
    public boolean canBeSelected() {
        return true;
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

    @Override
    public void setEditable(final boolean editable) {
        this.editable = editable;
    }

    @Override
    public void hide(final boolean hiding) {
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<String> getTeileigentumCrossReferences() {
        return teileigentumCrossReferences;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  teileigentumCrossReferences  DOCUMENT ME!
     */
    public void setTeileigentumCrossReferences(final Collection<String> teileigentumCrossReferences) {
        this.teileigentumCrossReferences = teileigentumCrossReferences;
    }

    @Override
    public FeatureAnnotationSymbol getPointAnnotationSymbol() {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getSimpleAnnotation() {
        return null;
    }

    @Override
    public int getLineWidth() {
        return 1;
    }

    @Override
    public boolean isHighlightingEnabled() {
        return true;
    }

    @Override
    public void setFillingPaint(final Paint fillingStyle) {
    }

    @Override
    public void setHighlightingEnabled(final boolean enabled) {
    }

    @Override
    public void setLinePaint(final Paint linePaint) {
    }

    @Override
    public void setLineWidth(final int width) {
    }

    @Override
    public void setPointAnnotationSymbol(final FeatureAnnotationSymbol featureAnnotationSymbol) {
    }

    @Override
    public void setTransparency(final float transparrency) {
    }
}
