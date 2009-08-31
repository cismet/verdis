/*
 * FlaechenDetails.java
 *
 * Created on 10. Januar 2005, 11:02
 */
package de.cismet.verdis.data;

import com.vividsolutions.jts.geom.Geometry;
import de.cismet.cismap.commons.features.StyledFeature;
import de.cismet.cismap.commons.gui.piccolo.FeatureAnnotationSymbol;
import de.cismet.cismap.commons.jtsgeometryfactories.PostGisGeometryFactory;
import de.cismet.tools.gui.dbwriter.SimpleDbAction;
import de.cismet.validation.Validatable;
import de.cismet.verdis.gui.FlaechenUebersichtsTableModel;
import de.cismet.verdis.gui.Main;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.*;
import java.util.*;
import de.cismet.verdis.models.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Paint;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.regex.Pattern;
import org.postgis.PGgeometry;

/**
 *
 * @author hell
 */
public class Flaeche implements Cloneable, StyledFeature {

    private int fl_id;
    private String bezeichnung;
    transient private SimpleDocumentModel bezeichnungsModel;
    private Integer gr_grafik;
    transient private SimpleDocumentModel gr_grafikModel;
    private Integer gr_korrektur;
    transient private SimpleDocumentModel gr_korrekturModel;
    private int art;
    //    private String art_abk;
    transient private ComboBoxModel artModel;
    private int grad;
    transient private ComboBoxModel gradModel;
    //    private String grad_abk;
    private Integer anteil;
    transient private SimpleDocumentModel anteilModel;
    private String erfassungsdatum;
    transient private SimpleDocumentModel erfassungsdatumModel;
    private String veranlagungsdatum;
    transient private SimpleDocumentModel veranlagungsdatumModel;
    private String bemerkung;
    transient private SimpleDocumentModel bemerkungsModel;
    private boolean sperre;
    transient private ButtonModel sperreModel;
    private String bem_sperre;
    transient private SimpleDocumentModel bem_sperreModel;
    private String feb_id;
    transient private SimpleDocumentModel feb_idModel;
    private Flaeche backup;
    transient private org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private int flaecheninfo_id = -1;
    private String kassenzeichen;
    private LinkedHashMap reverseArtHm;
    private LinkedHashMap reverseGradHm;
    private LinkedHashMap artHm;
    private LinkedHashMap gradHm;
    private LinkedHashMap<Integer, FlaechenBeschreibung> beschreibungHm = new LinkedHashMap<Integer, FlaechenBeschreibung>();
    private LinkedHashMap<FlaechenBeschreibung, Integer> reverseBeschreibungHm = new LinkedHashMap<FlaechenBeschreibung, Integer>();
    private Integer beschreibung;
    transient private ComboBoxModel beschreibungsmodel;
    private com.vividsolutions.jts.geom.Geometry geom = null;
    private int geom_id = -1;
    private boolean markedForDeletion = false;
    transient private FlaechenUebersichtsTableModel context = null;
    private boolean newFlaeche = false;
    private boolean editable;
    private Collection<String> teileigentumCrossReferences = null;
    private boolean geometryRemoved = false;
    private int removedGeometryId = -1;
    //Status f\u00FCr die Zwischenablage
    public static final int NONE = 0;
    public static final int CUTTED = 1;
    public static final int COPIED = 2;
    private int clipboardStatus = 0;
    transient private Flaeche THIS = null;

    /** Creates a new instance of FlaechenDetails */
    public Flaeche(LinkedHashMap reverseArtHm, LinkedHashMap reverseGradHm, LinkedHashMap artHm, LinkedHashMap gradHm, LinkedHashMap beschreibungHm, LinkedHashMap reverseBeschreibungHm) {
        this.reverseArtHm = reverseArtHm;
        this.reverseGradHm = reverseGradHm;
        this.artHm = artHm;
        this.gradHm = gradHm;
        this.beschreibungHm = beschreibungHm;
        this.reverseBeschreibungHm = reverseBeschreibungHm;
        initModels();
        THIS = this;
    }

    public void setContext(FlaechenUebersichtsTableModel context) {
        this.context = context;
    }

    public void setGeometry(com.vividsolutions.jts.geom.Geometry geom) {
        this.geom = geom;
    }

    public com.vividsolutions.jts.geom.Geometry getGeometry() {
        return geom;
    }

    public void setKassenzeichen(String kz) {
        kassenzeichen = kz;
    }

    public void backup() {
        try {
            backup = (Flaeche) (this.clone());
        } catch (Exception e) {
            log.error("Fehler beim Clonen.", e);
        }
    }

    public Integer getBeschreibung() {
        return beschreibung;
    }

    public FlaechenBeschreibung getFlaechenBeschreibung() {
        return beschreibungHm.get(beschreibung);
    }

    public void setBeschreibung(Integer beschreibung) {
        this.beschreibung = beschreibung;
    }

    public void setToBackupFlaeche() {
        if (backup != null) {
            setAnteil(backup.anteil);
            setArt(backup.art);
            //setArt_abk(backup.art_abk);
            setBemerkung(backup.bemerkung);
            setBem_sperre(backup.bem_sperre);
            setBezeichnung(backup.bezeichnung);
            setErfassungsdatum(backup.erfassungsdatum);
            setFeb_id(backup.feb_id);
            setFl_id(backup.fl_id);
            setGr_grafik(backup.gr_grafik);
            setGr_korrektur(backup.gr_korrektur);
            setGrad(backup.grad);
            //setGrad_abk(backup.grad_abk);
            setSperre(backup.sperre);
            setVeranlagungsdatum(backup.veranlagungsdatum);

            setClipboardStatus(backup.clipboardStatus);
            setContext(backup.context);
            setGeom_id(backup.geom_id);
            if (geom != null) {
                setGeometry((Geometry) (backup.geom.clone()));
            } else {
                setGeometry(null);
            }

            setKassenzeichen(backup.kassenzeichen);
            setFlaecheninfo_id(backup.flaecheninfo_id);
            setBeschreibung(backup.beschreibung);

            sync();
        }
    }

    public Object clone() {
        Flaeche f = new Flaeche(reverseArtHm, reverseGradHm, artHm, gradHm, beschreibungHm, reverseBeschreibungHm);
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
            f.setGeometry((Geometry) geom.clone());
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
     * Mit dieser Methode werden die Daten aus dem Resultset in das Objekt \u00FCbertragen
     * @param oa
     * @throws java.lang.Exception
     */
    // <editor-fold defaultstate="collapsed" desc=" fillFromObjectArray() ">   
    public void fillFromObjectArray(Object[] oa) throws Exception {
        fl_id = new Integer(oa[0].toString()).intValue();
        try {
            anteil = new Integer((int) (new Double(oa[1].toString()).doubleValue()));
            log.debug("Anteil not null");
        } catch (Exception e) {
            log.debug("Fehler beim Setzen von anteil", e);
            anteil = null;
        }
        try {
            bezeichnung = oa[2].toString();
        } catch (Exception e) {
            log.debug("Fehler beim Setzen von bezeichnung", e);
            bezeichnung = null;
        }
        try {
            bemerkung = oa[3].toString();
        } catch (Exception e) {
            log.debug("Fehler beim Setzen von bemerkung", e);
            bemerkung = null;
        }
        try {
            java.sql.Date d = (java.sql.Date) oa[4];
            erfassungsdatum = java.text.DateFormat.getDateInstance().format(d);
        } catch (Exception e) {
            log.debug("Fehler beim Setzen von erfassungsdatum", e);
            erfassungsdatum = null;
        }
        try {
            veranlagungsdatum = oa[5].toString();

        } catch (Exception e) {
            log.debug("Fehler beim Setzen von veranlagungsdatum", e);
            veranlagungsdatum = null;
        }
        String sperrString = oa[6].toString().trim().toUpperCase();
        if (sperrString.equals("T")) {
            sperre = true;
        } else {
            sperre = false;
        }
        try {
            bem_sperre = oa[7].toString();
        } catch (Exception e) {
            log.debug("Fehler beim Setzen von bem_sperre", e);
            bem_sperre = null;
        }
        try {
            gr_grafik = new Integer(oa[8].toString());
        } catch (Exception e) {
            log.debug("Fehler beim Setzen von gr_grafik", e);
            gr_grafik = null;
        }
        try {
            gr_korrektur = new Integer(oa[9].toString());
        } catch (Exception e) {
            log.debug("Fehler beim Setzen von gr_korrektur", e);
            gr_korrektur = null;
        }
        art = new Integer(oa[14].toString()).intValue();
        grad = new Integer(oa[15].toString()).intValue();
        try {
            feb_id = oa[16].toString();
        } catch (Exception e) {
            log.debug("Fehler beim Setzen von feb_id", e);
            feb_id = null;
        }
        flaecheninfo_id = new Integer(oa[17].toString()).intValue();
        try {
            log.debug("Geometrie:" + oa[18]);
            PGgeometry postgresGeom = (PGgeometry) oa[18];
            org.postgis.Geometry postgisGeom = postgresGeom.getGeometry();
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
            log.debug("beschreibung=" + beschreibung);
        } catch (Exception e) {
            log.warn("Fehler beim Anlegen der BeschreibungsID.", e);
        }
        updateModels();

    }
    // </editor-fold>
    public void sync() {
        this.initModels();
        this.updateModels();
    }

    public void addStatements(Vector container) {
        add2Container(container, getStatement4Flaechen());
        add2Container(container, getStatement4Flaeche());
        add2Container(container, getStatement4Flaecheninfo());
        add2Container(container, getStatement4Geom());
        add2Container(container, getStatement4Index());
        setClipboardStatus(Flaeche.NONE);
    }

    private void add2Container(Vector container, SimpleDbAction sdba) {
        if (sdba != null) {
            container.add(sdba);
        }
    }

    private String nullAwareSqlStringMaker(String s) {
        if (s != null) {
            return "'" + s + "'";
        } else {
            return null;
        }
    }

    // <editor-fold defaultstate="collapsed" desc=" SQL Kram (hier werden Statements zum CREATE,UPDATE und DELETE erzeugt ">   
    private SimpleDbAction getStatement4Flaechen() {
        SimpleDbAction sdba = new SimpleDbAction();
        if (isMarkedForDeletion()) {
            if (fl_id > 0) {
                sdba.setStatement("DELETE FROM flaechen where " +
                        "flaeche=" + this.fl_id + " and " +
                        "kassenzeichen_reference=" + this.kassenzeichen);
                sdba.setDescription("Verbindung l\u00F6schen zwischen >>KASSENZEICHEN<< und >>FLAECHE<<");
                sdba.setType(SimpleDbAction.DELETE);
            } else {
                sdba = null;
            }
        } else if (isNewFlaeche() && fl_id > 0) { //ist vorher irgendwo ausgeschnitten worden
            sdba.setStatement("INSERT INTO flaechen (id,flaeche,kassenzeichen_reference) " +
                    "VALUES(" +
                    "nextval('FLAECHEN_SEQ')," + fl_id + "," + this.kassenzeichen +
                    ")");
            sdba.setDescription("Verbindung zwischen >>KASSENZEICHEN<< und >>FLAECHE<<");
            sdba.setType(SimpleDbAction.INSERT);
        } else if (hasChanged() && fl_id > 0) {
            sdba = null;
        } else if (fl_id < 0) {
            sdba.setStatement("INSERT INTO flaechen (id,flaeche,kassenzeichen_reference) " +
                    "VALUES(" +
                    "nextval('FLAECHEN_SEQ'),nextval('FLAECHE_SEQ')," + this.kassenzeichen +
                    ")");
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

    private SimpleDbAction getStatement4Flaeche() {
        SimpleDbAction sdba = new SimpleDbAction();
        String sperrenString = "";
        if (sperre) {
            sperrenString = "T";
        }

        if (isMarkedForDeletion() && clipboardStatus != CUTTED) {
            if (fl_id > 0) {
                sdba.setStatement("DELETE FROM flaeche where " +
                        "id=" + this.fl_id);
                sdba.setDescription("Datensatz in >>FLAECHE<< l\u00F6schen");
                sdba.setType(SimpleDbAction.DELETE);
            } else {
                sdba = null;
            }

        } else if (hasChanged() && fl_id > 0) {
            sdba.setStatement("update flaeche set " +
                    "anteil=" + this.anteil + ", " +
                    "flaechenbezeichnung='" + this.bezeichnung + "', " +
                    "bemerkung=" + nullAwareSqlStringMaker(this.bemerkung) + ", " +
                    "datum_erfassung='" + this.erfassungsdatum + "', " +
                    "datum_veranlagung='" + this.veranlagungsdatum + "', " +
                    "sperre='" + sperrenString + "', " +
                    "bemerkung_sperre=" + nullAwareSqlStringMaker(this.bem_sperre) + ", " +
                    "feb_id=" + nullAwareSqlStringMaker(this.feb_id) + " " +
                    "where id=" + this.fl_id);
            sdba.setDescription("Ver\u00E4ndere die Tabelle >>FLAECHE<<");
            sdba.setType(SimpleDbAction.UPDATE);
        } else if (fl_id < 0) {
            String flaecheninfo_idString = ", nextval('FLAECHENINFO_SEQ')";
            if (this.flaecheninfo_id > 0) {
                flaecheninfo_idString = "," + flaecheninfo_id;
            }
            sdba.setStatement("INSERT INTO flaeche " +
                    "(id,flaecheninfo,anteil,flaechenbezeichnung,bemerkung,datum_erfassung,datum_veranlagung,sperre,bemerkung_sperre,feb_id) " +
                    "VALUES(" +
                    "currval('FLAECHE_SEQ')" +
                    flaecheninfo_idString +
                    "," + this.anteil +
                    ",'" + this.bezeichnung + "'" +
                    "," + nullAwareSqlStringMaker(this.bemerkung) +
                    ",'" + this.erfassungsdatum + "'" +
                    ",'" + this.veranlagungsdatum + "'" +
                    ",'" + sperrenString + "'" +
                    "," + nullAwareSqlStringMaker(this.bem_sperre) +
                    "," + nullAwareSqlStringMaker(this.feb_id) +
                    ")");
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

    private SimpleDbAction getStatement4Flaecheninfo() {
        SimpleDbAction sdba = new SimpleDbAction();
        if (gr_korrektur == null && gr_grafik != null) {
            gr_korrektur = new Integer(gr_grafik.intValue());
        }
        if (isMarkedForDeletion() && clipboardStatus != CUTTED) {
            if (this.flaecheninfo_id > 0) {
                sdba = new SimpleDbAction() {

                    public void executeAction(Connection conn) throws SQLException {
                        Statement checker = conn.createStatement();
                        ResultSet check = checker.executeQuery("SELECT count(*) FROM FLAECHE WHERE flaecheninfo=" + flaecheninfo_id);
                        check.next();
                        int counter = check.getInt(1);
                        if (counter == 0) {
                            super.executeAction(conn);
                        }
                    }
                };
                sdba.setStatement("DELETE FROM flaecheninfo where " +
                        "id=" + this.flaecheninfo_id);
                sdba.setDescription("Datensatz in >>FLAECHENINFO<< l\u00F6schen");
                sdba.setType(SimpleDbAction.DELETE);
            } else {
                sdba = null;
            }

        } else if (hasChanged() && flaecheninfo_id > 0) {
            String geometrieIdString = "null";
            if (geom_id > 0 && geom != null) {
                geometrieIdString = new Integer(geom_id).toString();
            } else if (geom_id < 0 && geom != null) {
                geometrieIdString = "nextval('GEOM_SEQ')";
            }
            sdba.setStatement("update flaecheninfo set " +
                    "groesse_aus_grafik=" + this.gr_grafik + ", " +
                    "groesse_korrektur=" + this.gr_korrektur + ", " +
                    "flaechenart=" + this.art + ", " +
                    "anschlussgrad=" + this.grad + ", " +
                    "geometrie=" + geometrieIdString + ", " +
                    "beschreibung=" + this.beschreibung + " " +
                    "where id=" + this.flaecheninfo_id);
            sdba.setDescription("Ver\u00E4ndere die Tabelle >>FLAECHENINFO<<");
            sdba.setType(SimpleDbAction.UPDATE);
        } else if (flaecheninfo_id < 0) {
            String geomInsert = "";
            if (geom != null && geom_id == -1) {
                geomInsert = "nextval('GEOM_SEQ')";
            } else {
                geomInsert = "null";
            }

            sdba.setStatement("INSERT INTO flaecheninfo " +
                    "(id,groesse_aus_grafik,groesse_korrektur,flaechenart,anschlussgrad,beschreibung,geometrie) " +
                    "VALUES(" +
                    "currval('FLAECHENINFO_SEQ') " +
                    "," + this.gr_grafik +
                    "," + this.gr_korrektur +
                    "," + this.art +
                    "," + this.grad +
                    "," + this.beschreibung +
                    "," + geomInsert + " " +
                    ")");
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

    private SimpleDbAction getStatement4Geom() {
        SimpleDbAction sdba = new SimpleDbAction();
        if (isMarkedForDeletion() && clipboardStatus != CUTTED || isGeometryRemoved()) {
            int geomIdInThisCase = 0;
            if (isGeometryRemoved()) {
                geomIdInThisCase = removedGeometryId;
            } else {
                geomIdInThisCase = geom_id;
            }
            if (geomIdInThisCase > 0) {
                final int gid = geomIdInThisCase;
                sdba = new SimpleDbAction() {

                    public void executeAction(Connection conn) throws SQLException {
                        Statement checker = conn.createStatement();
                        String s = "SELECT count(*) FROM FLAECHENINFO WHERE geometrie=" + gid;
                        ResultSet check = checker.executeQuery(s);
                        check.next();
                        int counter = check.getInt(1);
                        log.info("Test:" + s + "(" + counter + ")");

                        if (counter == 0) {
                            super.executeAction(conn);
                        }

                    }
                };
                sdba.setStatement("DELETE FROM geom where " +
                        "id=" + geomIdInThisCase);
                sdba.setDescription("Geometrie in >>GEOM<< l\u00F6schen");
                sdba.setType(SimpleDbAction.DELETE);
            } else {
                sdba = null;
            }

        } else if (hasChanged() && geom != null && geom_id > 0) {
            sdba.setStatement("update geom set " +
                    "geo_field='" + PostGisGeometryFactory.getPostGisCompliantDbString(this.geom) + "' " +
                    "where id=" + this.geom_id);
            sdba.setDescription("Ver\u00E4ndere die Tabelle >>GEOM<<");
            sdba.setType(SimpleDbAction.UPDATE);
        } else if (geom != null && geom_id < 0) {
            sdba.setStatement("insert into geom " +
                    "(id,geo_field) " +
                    "VALUES(" +
                    "currval('GEOM_SEQ'),'" + PostGisGeometryFactory.getPostGisCompliantDbString(this.geom) + "' " +
                    ")");
            sdba.setDescription("F\u00FCllen der Tabelle >>GEOM<<");
            sdba.setType(SimpleDbAction.INSERT);
        } else {
            sdba = null;
        }
        return sdba;

    }

    private SimpleDbAction getStatement4Index() {
        SimpleDbAction sdba = new SimpleDbAction();
        int geomIdInThisCase = 0;
        if (isGeometryRemoved()) {
            geomIdInThisCase = removedGeometryId;
        } else {
            geomIdInThisCase = geom_id;
        }
        if (isMarkedForDeletion() || isGeometryRemoved()) { //Auch beim Ausschneiden
            //Beim loeschen einer Geometrie
            sdba.setStatement("DELETE FROM cs_all_attr_mapping where " +
                    "class_id=" + Main.KASSENZEICHEN_CLASS_ID + " and " +
                    "object_id=" + kassenzeichen + " and " +
                    "attr_class_id=" + Main.GEOM_CLASS_ID + " and " +
                    "attr_object_id=" + geomIdInThisCase);
            sdba.setDescription("Geometrie im Index l\u00F6schen");
            sdba.setType(SimpleDbAction.DELETE);

        } else if (geom != null && (clipboardStatus == CUTTED || clipboardStatus == COPIED)) { //beim Einfuegen
            sdba.setStatement("insert into cs_all_attr_mapping (class_id, object_id, attr_class_id, attr_object_id) values ( " +
                    Main.KASSENZEICHEN_CLASS_ID + "," +
                    this.kassenzeichen + "," +
                    Main.GEOM_CLASS_ID + "," +
                    geomIdInThisCase +
                    ")");
            sdba.setDescription("Index der Geometrie anlegen.");
            sdba.setType(SimpleDbAction.INSERT);
        } else if (hasChanged() && geom != null && geom_id > 0) {
            //Bei einer reinen Ver\u00E4nderung passiert hier nix
            sdba = null;
        } else if (geom != null && geom_id < 0) {
            sdba.setStatement("insert into cs_all_attr_mapping (class_id, object_id, attr_class_id, attr_object_id) values ( " +
                    Main.KASSENZEICHEN_CLASS_ID + "," +
                    this.kassenzeichen + "," +
                    Main.GEOM_CLASS_ID + "," +
                    "currval('GEOM_SEQ')" +
                    ")");
            sdba.setDescription("Index der Geometrie anlegen.");
            sdba.setType(SimpleDbAction.INSERT);
        } else {
            sdba = null;
        }
        return sdba;


    }
    // </editor-fold>
    public boolean isValid() {
        if (bezeichnungsModel.getStatus() == Validatable.ERROR || gr_grafikModel.getStatus() == Validatable.ERROR || gr_korrekturModel.getStatus() == Validatable.ERROR || anteilModel.getStatus() == Validatable.ERROR || erfassungsdatumModel.getStatus() == Validatable.ERROR || veranlagungsdatumModel.getStatus() == Validatable.ERROR || feb_idModel.getStatus() == Validatable.ERROR) {
            return false;
        } else {
            return true;
        }
    }

    // <editor-fold defaultstate="collapsed" desc=" Getter und Setter">   
    public int getFl_id() {
        return fl_id;
    }

    public void setFl_id(int fl_id) {
        this.fl_id = fl_id;
    }

    public int getFlaecheninfo_id() {
        return this.flaecheninfo_id;
    }

    public void setFlaecheninfo_id(int id) {
        flaecheninfo_id = id;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public SimpleDocumentModel getBezeichnungsModel() {
        return bezeichnungsModel;
    }

    public Integer getGr_grafik() {
        return gr_grafik;
    }

    public String getGr_grafikString() {
        if (gr_grafik != null) {
            return gr_grafik.toString();
        } else {
            return "";
        }
    }

    public void setGr_grafik(Integer gr_grafik) {
        this.gr_grafik = gr_grafik;
    }

    public SimpleDocumentModel getGr_GrafikModel() {
        return gr_grafikModel;
    }

    public Integer getGr_korrektur() {
        return gr_korrektur;
    }

    public String getGr_korrekturString() {
        if (gr_korrektur != null) {
            return gr_korrektur.toString();
        } else {
            return "";
        }
    }

    public void setGr_korrektur(Integer gr_korrektur) {
        this.gr_korrektur = gr_korrektur;
    }

    public SimpleDocumentModel getGr_KorrekturModel() {
        return gr_korrekturModel;
    }

    public int getArt() {
        return art;
    }

    public void setArt(int art) {
        this.art = art;
    //if (art>0) log.fatal(art+","+reverseArtHm.keySet().toArray()[art-1]);

    }

    public String getArt_abk() {
        return (String) artHm.get(new Integer(art));
    }

    public ComboBoxModel getArtModel() {
        return artModel;
    }

    public int getGrad() {
        return grad;
    }

    public void setGrad(int grad) {
        //if (grad>0) log.fatal(grad+","+reverseGradHm.keySet().toArray()[grad-1]);
        this.grad = grad;
    }

    public String getGrad_abk() {
        return (String) gradHm.get(new Integer(grad));
    }

    public ComboBoxModel getGradModel() {
        return gradModel;
    }

    public Integer getAnteil() {
        return anteil;
    }

    public String getAnteilString() {
        if (anteil != null) {
            return anteil.toString();
        } else {
            return "";
        }
    }

    public void setAnteil(Integer anteil) {
        this.anteil = anteil;
    }

    public SimpleDocumentModel getAnteilModel() {
        return anteilModel;
    }

    public String getErfassungsdatum() {
        return erfassungsdatum;
    }

    public void setErfassungsdatum(String erfassungsdatum) {
        this.erfassungsdatum = erfassungsdatum;
    }

    public SimpleDocumentModel getErfassungsdatumModel() {
        return erfassungsdatumModel;
    }

    public String getVeranlagungsdatum() {
        return veranlagungsdatum;
    }

    public void setVeranlagungsdatum(String veranlagungsdatum) {
        this.veranlagungsdatum = veranlagungsdatum;
    }

    public SimpleDocumentModel getVeranlagungsdatumModel() {
        return veranlagungsdatumModel;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    public SimpleDocumentModel getBemerkungsModel() {
        return bemerkungsModel;
    }

    public boolean isSperre() {
        return sperre;
    }

    public void setSperre(boolean sperre) {
        this.sperre = sperre;
    }

    public String getBem_sperre() {
        return bem_sperre;
    }

    public SimpleDocumentModel getBem_sperreModel() {
        return bem_sperreModel;
    }

    public void setBem_sperre(String bem_sperre) {
        this.bem_sperre = bem_sperre;
    }

    public ButtonModel getSperrenModel() {
        return sperreModel;
    }

    public String getFeb_id() {
        return feb_id;
    }

    public void setFeb_id(String feb_id) {
        this.feb_id = feb_id;
    }

    public SimpleDocumentModel getFeb_IdModel() {
        return this.feb_idModel;
    }

    public ComboBoxModel getBeschreibungsmodel() {
        return beschreibungsmodel;
    }
    // </editor-fold>
    private boolean isValidBezeichnung(String bez) {
        return true;
    }

    private boolean isValidAnteil(String d) {
        return true;
    }

    private boolean isValidDatum(String d) {
        return true;
    }

    private boolean isValidVeranlagungsdatum(String d) {
        return true;
    }

    public void updateModels() {
        //bringe die models auf den richtigen stand
        try {
            bezeichnungsModel.insertString(0, bezeichnung, null);
        } catch (Exception e) {
            log.debug("???", e);
        }

        try {
            gr_grafikModel.insertString(0, gr_grafik.toString(), null);
        } catch (Exception e) {
            log.debug("???", e);
        }

        try {
            gr_korrekturModel.insertString(0, gr_korrektur.toString(), null);
        } catch (Exception e) {
            log.debug("???", e);
        }

        try {
            anteilModel.insertString(0, anteil.toString(), null);
        } catch (Exception e) {
            log.debug("???", e);
        }

        try {
            erfassungsdatumModel.insertString(0, erfassungsdatum.toString(), null);
        } catch (Exception e) {
            log.debug("???", e);
        }

        try {
            veranlagungsdatumModel.insertString(0, veranlagungsdatum.toString(), null);
        } catch (Exception e) {
            log.debug("???", e);
        }

        try {
            bemerkungsModel.insertString(0, bemerkung.toString(), null);
        } catch (Exception e) {
            log.debug("???", e);
        }

        try {
            this.feb_idModel.insertString(0, feb_id.toString(), null);
        } catch (Exception e) {
            log.debug("???", e);
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

    private void updateBemSperreModel() {
        try {
            this.bem_sperreModel.remove(0, bem_sperreModel.getLength());
            this.bem_sperreModel.insertString(0, bem_sperre.toString(), null);
        } catch (Exception e) {
            log.debug("???", e);
        }
    }

    public void initAfterXMLLoad() {
        log = org.apache.log4j.Logger.getLogger(this.getClass());
        backup = (Flaeche) clone();
        initModels();
    }

    // <editor-fold defaultstate="collapsed" desc=" Initialisierung der Modelle">   
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

    private void initBezeichnungsModel() {
        bezeichnungsModel = new SimpleDocumentModel() {

            public void insertString(int offset, String string, AttributeSet attributes) throws BadLocationException {
                super.insertString(offset, string.toUpperCase(), attributes);
            }

            public void assignValue(String newValue) {
                bezeichnung = newValue;
                fireValidationStateChanged();
            }

            public int getStatus() {
                boolean numerisch = false;
                try {
                    Integer tester = new Integer(bezeichnung);
                    numerisch = true;
                } catch (Exception e) {
                    numerisch = false;
                }

                if (art == 1 || art == 2) {
                    if (!numerisch) {
                        statusDescription = "Fl\u00E4chenbezeichnung muss zw. 0 und 1000 liegen.";
                        return Validatable.ERROR;
                    }

                    try {
                        Integer tester = new Integer(bezeichnung);
                        if (tester.intValue() > 1000 || tester.intValue() < 0) {
                            statusDescription = "Fl\u00E4chenbezeichnung muss zw. 0 und 1000 bzw. zw. A und BBB liegen.";
                            return Validatable.ERROR;
                        }
                    } catch (Exception e) {

                    }
                } else {
                    if (bezeichnung != null) {
                        int len = bezeichnung.length();
                        if (numerisch || (len > 3 || (len == 3 && bezeichnung.compareTo("BBB") > 0))) {
                            statusDescription = "Fl\u00E4chenbezeichnung muss zw. A und BBB liegen.";
                            return Validatable.ERROR;
                        }
                    }
                }
                statusDescription = "";
                return Validatable.VALID;
            }

            public void showAssistent(Component parent) {
                if (context != null) {
                    if (getStatus() == Validatable.ERROR) {
                        int answer = JOptionPane.showConfirmDialog(parent, "Soll die n\u00E4chste freie Bezeichnung gew\u00E4hlt werden", "Bezeichnung automatisch setzen", JOptionPane.YES_NO_OPTION);
                        if (answer == JOptionPane.YES_OPTION) {
                            String newValue = context.getValidFlaechenname(art);
                            log.info("Neuer Wert (von Assistent):" + newValue);
                            try {
                                bezeichnungsModel.remove(0, bezeichnung.length());
                                bezeichnungsModel.insertString(0, newValue, null);
                            } catch (Exception e) {
                                log.debug("???", e);
                            }
                        }
                    }
                }
            }
        };
    }

    private void initGrGrafikModel() {
        gr_grafikModel = new SimpleIntegerDocumentModel() {

            public void assignValue(Integer newInteger) {
                gr_grafik = newInteger;
                fireValidationStateChanged();
                log.debug("fireValidationStateChanged");
            }

            public int getStatus() {
                if (gr_grafik == null) {
                    statusDescription = "Wert ist leer";
                    return Validatable.ERROR;
                }
                if (gr_grafik != null && geom != null && !(gr_grafik.equals(new Integer((int) (geom.getArea()))))) {
                    statusDescription = "Fl\u00E4che der Geometrie stimmt nicht \u00FCberein (" + ((int) (geom.getArea())) + ")";
                    return Validatable.WARNING;
                } else if (gr_korrektur != null) {
                    int diff = gr_korrektur.intValue() - gr_grafik.intValue();
                    if (Math.abs(diff) > 20) {
                        statusDescription = "Differenz zwischen Korrekturwert und Gr\u00F6\u00DFe > 20m²";
                        return Validatable.WARNING;
                    }
                }
                statusDescription = "";
                return Validatable.VALID;
            }

            public void showAssistent(Component parent) {

                if (context != null && Main.THIS.isInEditMode()) {
                    if ((getStatus() == Validatable.WARNING || getStatus() == Validatable.ERROR) && geom != null) {
                        int answer = JOptionPane.showConfirmDialog(parent, "Soll die Gr\u00F6\u00DFe aus der Grafik \u00FCbernommen werden", "Gr\u00F6\u00DFe automatisch setzen", JOptionPane.YES_NO_OPTION);
                        if (answer == JOptionPane.YES_OPTION) {
                            //gr_grafik=new Integer((int)(geom.getArea()));
                            try {

                                //gr_grafikModel.remove(0, gr_grafik.toString().length());
                                Integer tmp = new Integer((int) (geom.getArea()));
                                gr_grafikModel.insertNewString(tmp.toString(), null);
                            } catch (Exception e) {
                                log.debug("???", e);
                            }

                            context.fireTableDataChanged();

                        }
                    }
                }
            }
        };
    }

    private void initGrKorrekturModel() {
        gr_korrekturModel = new SimpleIntegerDocumentModel() {

            public void assignValue(Integer newInteger) {
                gr_korrektur = newInteger;
                fireValidationStateChanged();
            }

            public int getStatus() {
                if (gr_grafik == null) {
                    statusDescription = "Wert ist leer";
                    return Validatable.WARNING;
                } else if (gr_korrektur != null) {
                    int diff = gr_korrektur.intValue() - gr_grafik.intValue();
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

    private void initArtModel() {
        artModel = new DefaultComboBoxModel(reverseArtHm.keySet().toArray()) {

            public void setSelectedItem(Object selItem) {
                try {
                    super.setSelectedItem(selItem);
                    log.debug("selItem:" + selItem);
                    art = new Integer(reverseArtHm.get(selItem).toString()).intValue();
                    log.debug("art:" + art);
                    ((SimpleDocumentModel) bezeichnungsModel).fireValidationStateChanged();
                } catch (Exception e) {
                    log.warn("Fehler ind initArtModel:", e);
                }
            }
        };
    }

    private void initGradModel() {
        gradModel = new DefaultComboBoxModel(reverseGradHm.keySet().toArray()) {

            public void setSelectedItem(Object selItem) {
                super.setSelectedItem(selItem);
                grad = new Integer(reverseGradHm.get(selItem).toString()).intValue();
            }
        };
    }

    private void initBeschreibungsmodel() {
        Boolean dach = null;
        if (artModel.getSelectedItem() != null) {
            String artMString = artModel.getSelectedItem().toString();
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
                for (FlaechenBeschreibung fb : reverseBeschreibungHm.keySet()) {
                    if (fb.isDachflaeche()) {
                        v.add(fb);
                    }
                }
            } else {
                for (FlaechenBeschreibung fb : reverseBeschreibungHm.keySet()) {
                    if (!fb.isDachflaeche()) {
                        v.add(fb);
                    }
                }
            }
        }
        beschreibungsmodel = new DefaultComboBoxModel(v) {

            @Override
            public void setSelectedItem(Object selItem) {
                super.setSelectedItem(selItem);
                if (selItem != null) {
                    beschreibung = new Integer(reverseBeschreibungHm.get(selItem).toString()).intValue();
                } else {
                    beschreibung = null;
                }
            }
        };
    }

    private void initAnteilModel() {
        anteilModel = new SimpleIntegerDocumentModel() {

            public void assignValue(Integer newInteger) {
                anteil = newInteger;
                fireValidationStateChanged();
            }

            public int getStatus() {
                if (anteil != null) {
                    if (gr_korrektur != null && anteil.intValue() > gr_korrektur.intValue()) {
                        statusDescription = "Anteil ist h\u00F6her als Gr\u00F6\u00DFe.";
                        return Validatable.ERROR;
                    } else if (gr_grafik != null && anteil.intValue() > gr_grafik.intValue()) {
                        statusDescription = "Anteil ist h\u00F6her als Gr\u00F6\u00DFe.";
                        return Validatable.ERROR;
                    }
                }
                return Validatable.VALID;
            }
        };
    }

    private void initErfassungsdatumModel() {
        erfassungsdatumModel = new SimpleDocumentModel() {

            public void assignValue(String newValue) {
                erfassungsdatum = newValue;
                fireValidationStateChanged();
            }

            public int getStatus() {
                //java.sql.Date d = new java.sql.Date
                java.text.DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY);
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

    private void initVeranlagungsdatumModel() {
        veranlagungsdatumModel = new SimpleDocumentModel() {

            public void assignValue(String newValue) {
                veranlagungsdatum = newValue;
                fireValidationStateChanged();
            }

            public int getStatus() {
                if (veranlagungsdatum != null) {
                    boolean b = Pattern.matches("\\d\\d/(01|02|03|04|05|06|07|08|09|10|11|12)", veranlagungsdatum);
                    if (b) {
                        return Validatable.VALID;
                    }
                }
                statusDescription = "Veranlagungsdatum muss im Format JJ/MM eingegeben werden.";
                return Validatable.ERROR;
            }
        };
    }

    private void initBemerkungsModel() {
        bemerkungsModel = new SimpleDocumentModel() {

            public void assignValue(String newValue) {
                bemerkung = newValue;
            }
        };
    }

    private void initSperreModel() {
        sperreModel = new javax.swing.JToggleButton.ToggleButtonModel() {

            public void setSelected(boolean b) {
                log.debug("sperreModel: setSelected(" + b + ")");
                super.setSelected(b);
                boolean oldSperre = sperre;
                sperre = b;
                if (b && oldSperre != sperre) {
                    String answer = "";
                    while (answer.trim().length() == 0) {
                        answer = JOptionPane.showInputDialog(de.cismet.verdis.gui.Main.THIS.getRootPane(), "Bitte eine Bemerkung zur Sperre angeben.", bem_sperre);
                    }
                    bem_sperre = answer;
                    updateBemSperreModel();
                }
                if (!sperre) {
                    try {
                        bem_sperreModel.remove(0, bem_sperreModel.getLength());
                    } catch (Exception e) {
                        log.debug("???", e);
                    }
                } else {
                    updateBemSperreModel();
                }

                javax.swing.JToggleButton.ToggleButtonModel t;
                fireActionPerformed(new ActionEvent(this, 0, "AfterDialog"));

            }

            public boolean isSelected() {
                return sperre;
            }
        };
    }

    private void initFebIdModel() {
        feb_idModel = new SimpleDocumentModel() {

            public void assignValue(String newValue) {
                feb_id = newValue;
                fireValidationStateChanged();
            }

            public int getStatus() {
                if (feb_id != null) {
                    try {
                        Integer tester = new Integer(feb_id);
                        if (tester.intValue() < 20000001 || tester.intValue() > 20200000) {
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

    private void initBemerkungSperreModel() {
        this.bem_sperreModel = new SimpleDocumentModel();
    }
    // </editor-fold>
    public boolean equals(Object o) {
        // <editor-fold defaultstate="collapsed" desc="Vergleich aller Werte">       
        Flaeche f;
        try {
            f = (Flaeche) o;
        } catch (Exception e) {
            return false;
        }
        boolean t1 = ((bezeichnung == null && f.bezeichnung == null) || (bezeichnung != null && bezeichnung.equals(f.bezeichnung)));
        boolean t2 = ((gr_grafik == null && f.gr_grafik == null) || (gr_grafik != null && gr_grafik.equals(f.gr_grafik)));
        boolean t3 = ((gr_korrektur == null && f.gr_korrektur == null) || (gr_korrektur != null && gr_korrektur.equals(f.gr_korrektur)));
        boolean t4 = (art == f.art);
        boolean t5 = (grad == f.grad);
        boolean t6 = ((anteil == null && f.anteil == null) || (anteil != null && anteil.equals(f.anteil)));
        boolean t7 = ((erfassungsdatum == null && f.erfassungsdatum == null) || (erfassungsdatum != null && erfassungsdatum.equals(f.erfassungsdatum)));
        boolean t8 = ((veranlagungsdatum == null && f.veranlagungsdatum == null) || (veranlagungsdatum != null && veranlagungsdatum.equals(f.veranlagungsdatum)));
        boolean t9 = ((bemerkung == null & f.bemerkung == null) || (bemerkung != null && bemerkung.equals(f.bemerkung)));
        boolean t10 = (sperre == f.sperre);
        boolean t11 = ((bem_sperre == null && f.bem_sperre == null) || (bem_sperre != null && bem_sperre.equals(f.bem_sperre)));
        boolean t12 = ((feb_id == null & f.feb_id == null) || (feb_id != null && feb_id.equals(f.feb_id)));
        boolean t13 = ((kassenzeichen == null && f.kassenzeichen == null) || (kassenzeichen != null && kassenzeichen.equals(f.kassenzeichen)));
        boolean t14 = ((geom == null && f.geom == null) || (f.geom != null && geom != null && geom.equalsExact(f.geom)));
        boolean t15 = (geom_id == f.geom_id);
        boolean t16 = (beschreibung == null && f.beschreibung == null) || (beschreibung != null && beschreibung.equals(f.beschreibung));

        try {

            if (((bezeichnung == null && f.bezeichnung == null) || (bezeichnung != null && bezeichnung.equals(f.bezeichnung))) &&
                    ((gr_grafik == null && f.gr_grafik == null) || (gr_grafik != null && gr_grafik.equals(f.gr_grafik))) &&
                    ((gr_korrektur == null && f.gr_korrektur == null) || (gr_korrektur != null && gr_korrektur.equals(f.gr_korrektur))) &&
                    (art == f.art) &&
                    (grad == f.grad) &&
                    ((anteil == null && f.anteil == null) || (anteil != null && anteil.equals(f.anteil))) &&
                    ((erfassungsdatum == null && f.erfassungsdatum == null) || (erfassungsdatum != null && erfassungsdatum.equals(f.erfassungsdatum))) &&
                    ((veranlagungsdatum == null && f.veranlagungsdatum == null) || (veranlagungsdatum != null && veranlagungsdatum.equals(f.veranlagungsdatum))) &&
                    ((bemerkung == null & f.bemerkung == null) || (bemerkung != null && bemerkung.equals(f.bemerkung))) &&
                    (sperre == f.sperre) &&
                    ((bem_sperre == null && f.bem_sperre == null) || (bem_sperre != null && bem_sperre.equals(f.bem_sperre))) &&
                    ((feb_id == null & f.feb_id == null) || (feb_id != null && feb_id.equals(f.feb_id))) &&
                    ((kassenzeichen == null && f.kassenzeichen == null) || (kassenzeichen != null && kassenzeichen.equals(f.kassenzeichen))) &&
                    ((geom == null && f.geom == null) || (f.geom != null && geom != null && geom.equalsExact(f.geom))) &&
                    (geom_id == f.geom_id) &&
                    (geometryRemoved == f.geometryRemoved) &&
                    (removedGeometryId == f.removedGeometryId) &&
                    ((beschreibung == null && f.beschreibung == null) || (beschreibung != null && beschreibung.equals(f.beschreibung)))) {
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

    public boolean hasChanged() {
        if (equals(backup)) {
            return false;
        } else {
            return true;
        }
    }

    public java.awt.Stroke getLineStyle() {
        return null;
    }

    public java.awt.Paint getFillingPaint() {
        int alpha = 0;
        if (markedForDeletion) {
            alpha = 25;
        } else {
            alpha = 150;
        }
        switch (art) {
            case 1:
                return new java.awt.Color(162, 76, 41, alpha);//Dach
            case 2:
                return new java.awt.Color(106, 122, 23, alpha);//Gr\u00FCndach
            case 3:
                return new java.awt.Color(120, 129, 128, alpha);//versiegelte Fl\u00E4che
            case 4:
                return new java.awt.Color(159, 155, 108, alpha);//\u00D6kopflaster
            case 5:
                return new java.awt.Color(138, 134, 132, alpha);//st\u00E4dtische Strassenflaeche
            case 6:
                return new java.awt.Color(126, 91, 71, alpha);//staedtische Strassenflaeche Oekopflaster
            default:
                return null;
        }

    }

    public float getTransparency() {
        return 1.0f;
    }

    public int getGeom_id() {
        return geom_id;
    }

    public void setGeom_id(int geom_id) {
        this.geom_id = geom_id;
    }

    public boolean isMarkedForDeletion() {
        return markedForDeletion;
    }

    public void setMarkedForDeletion(boolean markedForDeletion) {
        this.markedForDeletion = markedForDeletion;
    }

    public int getClipboardStatus() {
        return clipboardStatus;
    }

    public void setClipboardStatus(int clipboardStatus) {
        this.clipboardStatus = clipboardStatus;
    }

    public boolean isNewFlaeche() {
        return newFlaeche;
    }

    public void setNewFlaeche(boolean newFlaeche) {
        this.newFlaeche = newFlaeche;
    }

    public Flaeche getBackup() {
        return backup;
    }

    public boolean isGeometryRemoved() {
        return geometryRemoved;
    }

    public void setGeometryRemoved(boolean geometryRemoved) {
        this.geometryRemoved = geometryRemoved;
    }

    public int getRemovedGeometryId() {
        return removedGeometryId;
    }

    public void setRemovedGeometryId(int removedGeometryId) {
        this.removedGeometryId = removedGeometryId;
    }

    public Paint getLinePaint() {
        return Color.black;
    }

    public String getJoinBackupString() {
        String ret = "<JOIN ";
        ret += "bez=\"" + bezeichnung +
                "\" gr=\"" + gr_grafik +
                "\" grk=\"" + gr_korrektur +
                "\" edat=\"" + erfassungsdatum +
                "\" vdat=\"" + veranlagungsdatum +
                "\" sp=\"" + sperre +
                "\" spbem=\"" + bem_sperre +
                "\" febid=\"" + feb_id + "  >\n";
        ret += bemerkung;
        if (bemerkung != null && bemerkung.trim().length() > 0 && !bemerkung.endsWith("\n")) {
            ret += "\n";
        }
        ret += "</JOIN>";
        return ret;
    }

    public String getKassenzeichen() {
        return kassenzeichen;
    }

    public boolean canBeSelected() {
        return true;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public void hide(boolean hiding) {
    }

    public boolean isHidden() {
        return false;
    }

    public Collection<String> getTeileigentumCrossReferences() {
        return teileigentumCrossReferences;
    }

    public void setTeileigentumCrossReferences(Collection<String> teileigentumCrossReferences) {
        this.teileigentumCrossReferences = teileigentumCrossReferences;
    }

    public FeatureAnnotationSymbol getPointAnnotationSymbol() {
        return null;
    }

    public String getSimpleAnnotation() {
        return null;
    }

    public int getLineWidth() {
       return 1;
    }

    public boolean isHighlightingEnabled() {
        return true;
    }

    public void setFillingPaint(Paint fillingStyle) {
        
    }

    public void setHighlightingEnabled(boolean enabled) {
        
    }

    public void setLinePaint(Paint linePaint) {
        
    }

    public void setLineWidth(int width) {
        
    }

    public void setPointAnnotationSymbol(FeatureAnnotationSymbol featureAnnotationSymbol) {
        
    }

    public void setTransparency(float transparrency) {
        
    }
    
    
} 
