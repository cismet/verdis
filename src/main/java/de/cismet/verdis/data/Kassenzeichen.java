/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * Kassenzeichen.java
 *
 * Created on 20. Januar 2005, 11:32
 */
package de.cismet.verdis.data;
import java.awt.event.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.*;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.text.*;

import de.cismet.tools.gui.dbwriter.SimpleDbAction;

import de.cismet.validation.Validatable;

import de.cismet.verdis.gui.Main;

import de.cismet.verdis.models.*;

/**
 * DOCUMENT ME!
 *
 * @author   hell
 * @version  $Revision$, $Date$
 */
public class Kassenzeichen implements Cloneable {

    //~ Instance fields --------------------------------------------------------

    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private Kassenzeichen backup;

    private String kassenzeichenString;
    private String erfassungsdatum;
    private String veranlagungsdatum;
    private String bemerkung;
    private boolean sperre;
    private String bemerkung_sperre;

    private PlainDocument kassenzeichenModel;
    private PlainDocument erfassungsdatumModel;
    private PlainDocument veranlagungsdatumModel;
    private PlainDocument bemerkungsModel;
    private javax.swing.JToggleButton.ToggleButtonModel sperrenModel;
    private PlainDocument bemerkungSperreModel;

    private String letzteAenderung;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of Kassenzeichen.
     */
    public Kassenzeichen() {
        initModels();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    public void updateModels() {
        try {
            kassenzeichenModel.insertString(0, kassenzeichenString, null);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("???", e);
            }
        }
        try {
            erfassungsdatumModel.insertString(0, erfassungsdatum, null);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("???", e);
            }
        }
        try {
            veranlagungsdatumModel.insertString(0, veranlagungsdatum, null);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("???", e);
            }
        }
        try {
            bemerkungsModel.insertString(0, bemerkung, null);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("???", e);
            }
        }
//    try {bemerkungSperreModel.insertString(0,bemerkung_sperre,null);}
//        catch (Exception e) {log.debug("???",e);}

        if (sperre) {
            updateBemSperreModel();
        }
    }
    /**
     * DOCUMENT ME!
     */
    public void updateBemSperreModel() {
        try {
            this.bemerkungSperreModel.remove(0, bemerkungSperreModel.getLength());
            this.bemerkungSperreModel.insertString(0, bemerkung_sperre.toString(), null);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("???", e);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  oa  DOCUMENT ME!
     */
    public void fillFromObjectArray(final Object[] oa) {
        // id,datum_erfassung,datum_veranlagung,bemerkung,sperre,bemerkung_sperre,,letzte_aenderung_von,letzte_aenderung_am
        kassenzeichenString = oa[0].toString();
        final java.sql.Date d = (java.sql.Date)oa[1];
        erfassungsdatum = java.text.DateFormat.getDateInstance().format(d);
        if (oa[2] != null) {
            veranlagungsdatum = oa[2].toString();
        }
        if (oa[3] != null) {
            bemerkung = oa[3].toString();
        }
        if (oa[4].toString().trim().toLowerCase().equals("t")) {
            sperre = true;
        } else {
            sperre = false;
        }
        if (oa[5] != null) {
            bemerkung_sperre = oa[5].toString();
        }

        if ((oa[6] != null) && (oa[7] != null)) {
            setLetzteAenderung(oa[6] + " (" + oa[7] + ")");
        }
        updateModels();
    }

    /**
     * DOCUMENT ME!
     */
    public void initModels() {
        initKassenzeichenModel();
        initErfassungsdatumModel();
        initVeranlagungsdatumModel();
        initBemerkungsModel();
        initSperrenModel();
        initBemerkungSperrenModel();
    }
    /**
     * DOCUMENT ME!
     */
    public void initKassenzeichenModel() {
        kassenzeichenModel = new SimpleDocumentModel() {

                @Override
                public void assignValue(final String newValue) {
                    kassenzeichenString = newValue;
                    fireValidationStateChanged();
                }
                @Override
                public int getStatus() {
                    if (kassenzeichenString == null) {
                        statusDescription = "Kassenzeichen leer.";
                        return Validatable.ERROR;
                    } else {
                        try {
                            final int kz = new Integer(kassenzeichenString).intValue();
                            if (((kz > 6000000) && (kz < 10000000)) || ((kz > 20000000) && (kz < 20200000))) {
                                return Validatable.VALID;
                            } else {
                                statusDescription = "Kassenzeichen nicht im g\u00FCltigen Bereich.";
                                return Validatable.ERROR;
                            }
                        } catch (Exception e) {
                            statusDescription = "Kassenzeichen muss eine g\u00FCltige Zahl sein.";
                            return Validatable.ERROR;
                        }
                    }
                }
            };
    }
    /**
     * DOCUMENT ME!
     */
    public void initErfassungsdatumModel() {
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
    public void initVeranlagungsdatumModel() {
        veranlagungsdatumModel = new SimpleDocumentModel() {

                @Override
                public void assignValue(final String newValue) {
                    veranlagungsdatum = newValue;
                    fireValidationStateChanged();
                }
                @Override
                public int getStatus() {
                    if (veranlagungsdatum == null) {
                        statusDescription = "Veranlagungsdatum darf nicht leer sein.";
                        return Validatable.ERROR;
                    }

                    final boolean b = Pattern.matches(
                            "\\d\\d/(01|02|03|04|05|06|07|08|09|10|11|12)",
                            veranlagungsdatum);
                    if (b) {
                        return Validatable.VALID;
                    } else {
                        statusDescription = "Veranlagungsdatum muss im Format JJ/MM eingegeben werden.";
                        return Validatable.ERROR;
                    }
                }
            };
    }
    /**
     * DOCUMENT ME!
     */
    public void initBemerkungsModel() {
        bemerkungsModel = new SimpleDocumentModel() {

                @Override
                public void assignValue(final String newValue) {
                    bemerkung = newValue;
                }
            };
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
     */
    public void initSperrenModel() {
        sperrenModel = new SperrenModel();
    }
    /**
     * DOCUMENT ME!
     */
    public void initBemerkungSperrenModel() {
        bemerkungSperreModel = new PlainDocument();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public PlainDocument getKassenzeichenModel() {
        return kassenzeichenModel;
    }
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public PlainDocument getErfassungsdatumModel() {
        return erfassungsdatumModel;
    }
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public PlainDocument getVeranlagungsdatumModel() {
        return veranlagungsdatumModel;
    }
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public PlainDocument getBemerkungsModel() {
        return bemerkungsModel;
    }
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public javax.swing.JToggleButton.ToggleButtonModel getSperrenModel() {
        return sperrenModel;
    }
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public PlainDocument getBemerkungSperreModel() {
        return bemerkungSperreModel;
    }

    @Override
    public String toString() {
        return this.kassenzeichenString;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getKassenzeichen() {
        return kassenzeichenString;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        final Kassenzeichen k = new Kassenzeichen();
        k.bemerkung = bemerkung;
        k.bemerkung_sperre = bemerkung_sperre;
        k.erfassungsdatum = erfassungsdatum;
        k.kassenzeichenString = kassenzeichenString;
        k.sperre = sperre;
        k.veranlagungsdatum = veranlagungsdatum;
        return k;
    }

    @Override
    public boolean equals(final Object o) {
        Kassenzeichen k = null;
        try {
            k = (Kassenzeichen)o;
        } catch (Exception e) {
            return false;
        }
        try {
            if ((((kassenzeichenString == null) && (k.kassenzeichenString == null))
                            || ((k.kassenzeichenString != null) && k.kassenzeichenString.equals(kassenzeichenString)))
                        && (((bemerkung == null) && (k.bemerkung == null))
                            || ((k.bemerkung != null) && k.bemerkung.equals(bemerkung)))
                        && (((bemerkung_sperre == null) && (k.bemerkung_sperre == null))
                            || ((k.bemerkung_sperre != null) && k.bemerkung_sperre.equals(bemerkung_sperre)))
                        && (((erfassungsdatum == null) && (k.erfassungsdatum == null))
                            || ((k.erfassungsdatum != null) && k.erfassungsdatum.equals(erfassungsdatum)))
                        && (((veranlagungsdatum == null) && (k.veranlagungsdatum == null))
                            || ((k.veranlagungsdatum != null) && k.veranlagungsdatum.equals(veranlagungsdatum)))
                        && (sperre == k.sperre)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
    /**
     * DOCUMENT ME!
     */
    public void backup() {
        try {
            backup = (Kassenzeichen)(this.clone());
        } catch (Exception e) {
            log.error("Fehler beim Clonen.", e);
        }
    }
    /**
     * DOCUMENT ME!
     */
    public void setToBackupFlaeche() {
        bemerkung = backup.bemerkung;
        bemerkung_sperre = backup.bemerkung_sperre;
        erfassungsdatum = backup.erfassungsdatum;
        kassenzeichenString = backup.kassenzeichenString;
        sperre = backup.sperre;
        veranlagungsdatum = backup.veranlagungsdatum;
        updateModels();
    }
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean hasChanged() {
        if (this.equals(backup)) {
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
    public SimpleDbAction createUpdateAction() {
        String sperreString = "F";
        if (sperre) {
            sperreString = "T";
        }
        final SimpleDbAction sdba = new SimpleDbAction();
        sdba.setStatement("update kassenzeichen "
                    + "set "
                    + "datum_erfassung='" + erfassungsdatum + "',"
                    + "datum_veranlagung='" + veranlagungsdatum + "',"
                    + "bemerkung=" + nullAwareSqlStringMaker(bemerkung) + ","
                    + "sperre='" + sperreString + "',"
                    + "bemerkung_sperre=" + nullAwareSqlStringMaker(bemerkung_sperre) + " "
                    + "where id=" + kassenzeichenString);
        sdba.setDescription("Ver\u00E4ndere die Tabelle >>KASSENZEICHEN<<");
        sdba.setType(SimpleDbAction.UPDATE);
        return sdba;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  v      DOCUMENT ME!
     * @param  oldKZ  DOCUMENT ME!
     * @param  newKZ  DOCUMENT ME!
     */
    public static void collectActions4RenameKassenzeichen(final Vector v, final String oldKZ, final String newKZ) {
        v.add(getAction4RenameKassenzeichen_CS_CAT_NODE(oldKZ, newKZ));
        v.add(getAction4RenameKassenzeichen_KASSENZEICHEN(oldKZ, newKZ));
        v.add(getAction4RenameKassenzeichen_DMS_URLS(oldKZ, newKZ));
        v.add(getAction4RenameKassenzeichen_FLAECHEN(oldKZ, newKZ));
        v.add(getAction4RenameKassenzeichen_CS_ALL_ATTR_MAPPING(oldKZ, newKZ));
        v.add(getAction4RenameKassenzeichen_CS_ATTR_STRING(oldKZ, newKZ));
        v.add(getAction4RenameKassenzeichen_CS_LOCKS(oldKZ, newKZ));
    }
    /**
     * DOCUMENT ME!
     *
     * @param   oldKZ  DOCUMENT ME!
     * @param   newKZ  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static SimpleDbAction getAction4RenameKassenzeichen_CS_CAT_NODE(final String oldKZ, final String newKZ) {
        final SimpleDbAction sdba = new SimpleDbAction();
        sdba.setStatement("UPDATE cs_cat_node SET "
                    + "   name = 'Kassenzeichen: " + newKZ + "',"
                    + "   object_id = " + newKZ + ", "
                    + "   org = NULL "
                    + "WHERE "
                    + "   class_id = " + Main.KASSENZEICHEN_CLASS_ID + " AND "
                    + "   object_id = " + oldKZ);
        sdba.setDescription("UPDATE KASSENZEICHEN SET id ...");
        sdba.setType(SimpleDbAction.UPDATE);
        return sdba;
    }
    /**
     * DOCUMENT ME!
     *
     * @param   oldKZ  DOCUMENT ME!
     * @param   newKZ  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static SimpleDbAction getAction4RenameKassenzeichen_KASSENZEICHEN(final String oldKZ, final String newKZ) {
        final SimpleDbAction sdba = new SimpleDbAction();
        sdba.setStatement("UPDATE kassenzeichen SET "
                    + "   id = " + newKZ + " "
                    + "WHERE "
                    + "   id = " + oldKZ);
        sdba.setDescription("UPDATE KASSENZEICHEN SET id ...");
        sdba.setType(SimpleDbAction.UPDATE);
        return sdba;
    }
    /**
     * DOCUMENT ME!
     *
     * @param   oldKZ  DOCUMENT ME!
     * @param   newKZ  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static SimpleDbAction getAction4RenameKassenzeichen_DMS_URLS(final String oldKZ, final String newKZ) {
        final SimpleDbAction sdba = new SimpleDbAction();
        sdba.setStatement("UPDATE dms_urls SET "
                    + "   kassenzeichen_reference = " + newKZ + " "
                    + "WHERE "
                    + "   kassenzeichen_reference = " + oldKZ);
        sdba.setDescription("UPDATE DMS_URLS SET kassenzeichen ...");
        sdba.setType(SimpleDbAction.UPDATE);
        return sdba;
    }
    /**
     * DOCUMENT ME!
     *
     * @param   oldKZ  DOCUMENT ME!
     * @param   newKZ  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static SimpleDbAction getAction4RenameKassenzeichen_FLAECHEN(final String oldKZ, final String newKZ) {
        final SimpleDbAction sdba = new SimpleDbAction();
        sdba.setStatement("UPDATE flaechen SET "
                    + "   kassenzeichen_reference = " + newKZ + " "
                    + "WHERE "
                    + "   kassenzeichen_reference = " + oldKZ);
        sdba.setDescription("UPDATE FLAECHEN SET kassenzeichen ...");
        sdba.setType(SimpleDbAction.UPDATE);
        return sdba;
    }
    /**
     * DOCUMENT ME!
     *
     * @param   oldKZ  DOCUMENT ME!
     * @param   newKZ  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static SimpleDbAction getAction4RenameKassenzeichen_CS_ALL_ATTR_MAPPING(final String oldKZ,
            final String newKZ) {
        final SimpleDbAction sdba = new SimpleDbAction();
        sdba.setStatement("UPDATE cs_all_attr_mapping SET "
                    + "   object_id = " + newKZ + " "
                    + "WHERE "
                    + "   class_id = " + Main.KASSENZEICHEN_CLASS_ID + " AND "
                    + "   object_id = " + oldKZ);
        sdba.setDescription("UPDATE CS_ALL_ATTR_MAPPING SET kassenzeichen ...");
        sdba.setType(SimpleDbAction.UPDATE);
        return sdba;
    }
    /**
     * DOCUMENT ME!
     *
     * @param   oldKZ  DOCUMENT ME!
     * @param   newKZ  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static SimpleDbAction getAction4RenameKassenzeichen_CS_ATTR_STRING(final String oldKZ, final String newKZ) {
        final SimpleDbAction sdba = new SimpleDbAction();
        sdba.setStatement("UPDATE cs_attr_string SET "
                    + "   object_id = " + newKZ + " "
                    + "WHERE "
                    + "   class_id = " + Main.KASSENZEICHEN_CLASS_ID + " AND "
                    + "   object_id = " + oldKZ);
        sdba.setDescription("UPDATE CS_ATTR_STRING SET kassenzeichen ...");
        sdba.setType(SimpleDbAction.UPDATE);
        return sdba;
    }
    /**
     * DOCUMENT ME!
     *
     * @param   oldKZ  DOCUMENT ME!
     * @param   newKZ  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static SimpleDbAction getAction4RenameKassenzeichen_CS_LOCKS(final String oldKZ, final String newKZ) {
        final SimpleDbAction sdba = new SimpleDbAction();
        sdba.setStatement("UPDATE cs_locks SET "
                    + "   object_id = " + newKZ + " "
                    + "WHERE "
                    + "   class_id = " + Main.KASSENZEICHEN_CLASS_ID + " AND "
                    + "   object_id = " + oldKZ);
        sdba.setDescription("UPDATE CS_ATTR_STRING SET kassenzeichen ...");
        sdba.setType(SimpleDbAction.UPDATE);
        return sdba;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  v      DOCUMENT ME!
     * @param  newKZ  DOCUMENT ME!
     */
    public static void collectActions4NewKassenzeichen(final Vector v, final String newKZ) {
        v.add(getAction4NewKassenzeichen_CS_CAT_NODE(newKZ));
        v.add(getAction4NewKassenzeichen_Kassenzeichen(newKZ));
        v.add(getAction4NewKassenzeichen_Geom(newKZ));
        v.add(getAction4NewKassenzeichen_DMS_URLS(newKZ));
//        v.add(getAction4NewKassenzeichen_DMS_URL(newKZ));
//        v.add(getAction4NewKassenzeichen_URL(newKZ));
    }
    /**
     * DOCUMENT ME!
     *
     * @param   newKZ  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static SimpleDbAction getAction4NewKassenzeichen_CS_CAT_NODE(final String newKZ) {
        final SimpleDbAction sdba = new SimpleDbAction();
        sdba.setStatement("insert into cs_cat_node "
                    + "(id, name, descr, class_id, object_id, node_type, is_root, org) "
                    + "values ("
                    + "nextval('cs_cat_node_sequence'),"
                    + "'Kassenzeichen: " + newKZ + "',"
                    + "null,"
                    + Main.KASSENZEICHEN_CLASS_ID + ","
                    + newKZ + ","
                    + "'O',"
                    + "'F',"
                    + "null"
                    + ")");
        sdba.setDescription("INSERT INTO KASSENZEICHEN ...");
        sdba.setType(SimpleDbAction.INSERT);
        return sdba;
    }
    /**
     * DOCUMENT ME!
     *
     * @param   newKZ  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static SimpleDbAction getAction4NewKassenzeichen_Kassenzeichen(final String newKZ) {
        final SimpleDbAction sdba = new SimpleDbAction();
        final DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMANY);
        final Calendar cal = Calendar.getInstance();
        final String erfassungsdatum = "'" + df.format(cal.getTime()) + "'";
        cal.add(Calendar.MONTH, 1);
        final SimpleDateFormat vDat = new SimpleDateFormat("yy/MM");
        final String veranlagungsdatum = "'" + vDat.format(cal.getTime()) + "'";
        sdba.setStatement("insert into kassenzeichen "
                    + "(id, datum_erfassung, datum_veranlagung, bemerkung, sperre, bemerkung_sperre, dms_urls, flaechen, geometrie,letzte_aenderung_von,letzte_aenderung_ts) "
                    + "values ("
                    + newKZ + ","
                    + erfassungsdatum + ","
                    + veranlagungsdatum + ","
                    + "null,"
                    + "'F',"
                    + "null,"
                    + newKZ + ","
                    + newKZ + ","
                    + "nextval('geom_seq'),"
                    + "'" + Main.THIS.getUserString() + "',"
                    + "now()"
                    + ")");
        sdba.setDescription("INSERT INTO KASSENZEICHEN ...");
        sdba.setType(SimpleDbAction.INSERT);
        return sdba;
    }
    /**
     * DOCUMENT ME!
     *
     * @param   newKZ  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static SimpleDbAction getAction4NewKassenzeichen_Geom(final String newKZ) {
        final SimpleDbAction sdba = new SimpleDbAction();
        sdba.setStatement("insert into geom (id, geo_field) "
                    + "values ("
                    + "currval('geom_seq'),"
                    + "null"
                    + ")");
        sdba.setDescription("INSERT INTO GEOM ... (zusammenfassende BoundingBox)");
        sdba.setType(SimpleDbAction.INSERT);
        return sdba;
    }
    /**
     * DOCUMENT ME!
     *
     * @param   newKZ  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static SimpleDbAction getAction4NewKassenzeichen_DMS_URLS(final String newKZ) {
        final SimpleDbAction sdba = new SimpleDbAction();
        sdba.setStatement("insert into dms_urls (id, dms_url, kassenzeichen_reference) "
                    + "values ("
                    + "nextval('dms_urls_seq'),"
                    + Main.DMS_URL_ID + ","
                    + newKZ
                    + ")");
        sdba.setDescription("INSERT INTO DMS_URLS ...");
        sdba.setType(SimpleDbAction.INSERT);
        return sdba;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  v   DOCUMENT ME!
     * @param  kz  DOCUMENT ME!
     */
    public static void collectActions4DeleteKassenzeichen(final Vector v, final String kz) {
        v.add(getAction4DeletionKassenzeichen_CS_CAT_NODE(kz));
        v.add(getAction4DeletionKassenzeichen_GEOM(kz));
        v.add(getAction4DeletionKassenzeichen_FLAECHENINFO(kz));
        v.add(getAction4DeletionKassenzeichen_FLAECHE(kz));
        v.add(getAction4DeletionKassenzeichen_FLAECHEN(kz));
        v.add(getAction4DeletionKassenzeichen_FLAECHEN(kz));
        v.add(getAction4DeletionKassenzeichen_GEOM_KZ(kz));
        v.add(getAction4DeletionKassenzeichen_BEFREIUNGERLAUBNIS(kz));
        v.add(getAction4DeletionKassenzeichen_BEFREIUNGERLAUBNISARRAY(kz));
        v.add(getAction4DeletionKassenzeichen_KANALANSCHLUSS(kz));
//        v.add(getAction4DeletionKassenzeichen_URL_BASE(kz));
//        v.add(getAction4DeletionKassenzeichen_URL(kz));
//        v.add(getAction4DeletionKassenzeichen_DMS_URL(kz));
        v.add(getAction4DeletionKassenzeichen_DMS_URLS(kz));

        // als allerletztes
        v.add(getAction4DeletionKassenzeichen_KASSENZEICHEN(kz));
    }
    /**
     * DOCUMENT ME!
     *
     * @param   kz  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static SimpleDbAction getAction4DeletionKassenzeichen_CS_CAT_NODE(final String kz) {
        final String stmnt = "delete from cs_cat_node where class_id=" + Main.KASSENZEICHEN_CLASS_ID + " and object_id="
                    + kz;
        final SimpleDbAction sdba = new SimpleDbAction();
        sdba.setType(SimpleDbAction.DELETE);
        sdba.setDescription("DELETE FROM GEOM (Geometrien) ...");
        sdba.setStatement(stmnt);
        return sdba;
    }
    /**
     * DOCUMENT ME!
     *
     * @param   kz  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static SimpleDbAction getAction4DeletionKassenzeichen_GEOM(final String kz) {
        final String stmnt = "delete from geom where id in ( "
                    + "select geom.id  from flaechen,flaeche,flaecheninfo,geom,flaeche ff  "
                    + "where flaechen.flaeche=flaeche.id  "
                    + "and flaeche.flaecheninfo=flaecheninfo.id "
                    + "and flaecheninfo.geometrie=geom.id "
                    + "and flaecheninfo.id=ff.flaecheninfo "
                    + "and kassenzeichen_reference= " + kz + " "
                    + "group by geom.id "
                    + "having count(ff.id)<2)";
        final SimpleDbAction sdba = new SimpleDbAction();
        sdba.setType(SimpleDbAction.DELETE);
        sdba.setDescription("DELETE FROM GEOM (Geometrien) ...");
        sdba.setStatement(stmnt);
        return sdba;
    }
    /**
     * DOCUMENT ME!
     *
     * @param   kz  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static SimpleDbAction getAction4DeletionKassenzeichen_FLAECHENINFO(final String kz) {
        final String stmnt = "delete from flaecheninfo where id in (     "
                    + "select flaecheninfo.id  from flaechen,flaeche,flaecheninfo,flaeche ff where "
                    + "flaechen.flaeche=flaeche.id and flaeche.flaecheninfo=flaecheninfo.id "
                    + "and flaecheninfo.id=ff.flaecheninfo "
                    + "and kassenzeichen_reference= " + kz + " "
                    + "group by flaecheninfo.id "
                    + "having count(ff.id)<2) ";
        final SimpleDbAction sdba = new SimpleDbAction();
        sdba.setType(SimpleDbAction.DELETE);
        sdba.setDescription("DELETE FROM FLAECHENINFO ...");
        sdba.setStatement(stmnt);
        return sdba;
    }
    /**
     * DOCUMENT ME!
     *
     * @param   kz  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static SimpleDbAction getAction4DeletionKassenzeichen_FLAECHE(final String kz) {
        final String stmnt = "delete from flaeche where id in ( "
                    + "select flaeche from flaechen where kassenzeichen_reference=" + kz + ")";
        final SimpleDbAction sdba = new SimpleDbAction();
        sdba.setType(SimpleDbAction.DELETE);
        sdba.setDescription("DELETE FROM FLAECHE ...");
        sdba.setStatement(stmnt);
        return sdba;
    }
    /**
     * DOCUMENT ME!
     *
     * @param   kz  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static SimpleDbAction getAction4DeletionKassenzeichen_FLAECHEN(final String kz) {
        final String stmnt = "delete from flaechen where kassenzeichen_reference= " + kz;
        final SimpleDbAction sdba = new SimpleDbAction();
        sdba.setType(SimpleDbAction.DELETE);
        sdba.setDescription("DELETE FROM FLAECHEN ...");
        sdba.setStatement(stmnt);
        return sdba;
    }
    /**
     * DOCUMENT ME!
     *
     * @param   kz  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static SimpleDbAction getAction4DeletionKassenzeichen_GEOM_KZ(final String kz) {
        final String stmnt = "delete from geom where id in (select geom.id from "
                    + "kassenzeichen,geom where geometrie=geom.id and kassenzeichen.id= " + kz + ")";
        final SimpleDbAction sdba = new SimpleDbAction();
        sdba.setType(SimpleDbAction.DELETE);
        sdba.setDescription("DELETE FROM GEOM (zusammenfassende Geometrie) ...");
        sdba.setStatement(stmnt);
        return sdba;
    }
    /**
     * DOCUMENT ME!
     *
     * @param   kz  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static SimpleDbAction getAction4DeletionKassenzeichen_KASSENZEICHEN(final String kz) {
        final String stmnt = "delete from kassenzeichen where id= " + kz;
        final SimpleDbAction sdba = new SimpleDbAction();
        sdba.setType(SimpleDbAction.DELETE);
        sdba.setDescription("DELETE FROM GEOM (Geometrien) ...");
        sdba.setStatement(stmnt);
        return sdba;
    }
    /**
     * DOCUMENT ME!
     *
     * @param   kz  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static SimpleDbAction getAction4DeletionKassenzeichen_URL_BASE(final String kz) {
        final String stmnt = "delete from url_base where id in ("
                    + "select url_base.id from dms_urls,dms_url,url, dms_url as dd "
                    + "where dms_urls.dms_url=dms_url.id  "
                    + "and dms_url.url_id=url.id "
                    + "and kassenzeichen_reference=" + kz + " "
                    + "and dd.url_id=url.id "
                    + "group by url_base.id "
                    + "having count(dd.id)<2 )";
        final SimpleDbAction sdba = new SimpleDbAction();
        sdba.setType(SimpleDbAction.DELETE);
        sdba.setDescription("DELETE FROM URL_BASE ...");
        sdba.setStatement(stmnt);
        return sdba;
    }
    /**
     * DOCUMENT ME!
     *
     * @param   kz  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static SimpleDbAction getAction4DeletionKassenzeichen_URL(final String kz) {
        final String stmnt = "delete from url where id in ( "
                    + "select url.id from dms_urls,dms_url,url, dms_url as dd "
                    + "where dms_urls.dms_url=dms_url.id  "
                    + "and dms_url.url_id=url.id "
                    + "and kassenzeichen_reference= " + kz + " "
                    + "and dd.url_id=url.id "
                    + "group by url.id "
                    + "having count(dd.id)<2) ";
        final SimpleDbAction sdba = new SimpleDbAction();
        sdba.setType(SimpleDbAction.DELETE);
        sdba.setDescription("DELETE FROM URL ...");
        sdba.setStatement(stmnt);
        return sdba;
    }
    /**
     * DOCUMENT ME!
     *
     * @param   kz  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static SimpleDbAction getAction4DeletionKassenzeichen_DMS_URL(final String kz) {
        final String stmnt = "delete from dms_urls where dms_url in( "
                    + "select dms_url.id  from dms_urls,dms_url,dms_urls as dd "
                    + "where dms_urls.dms_url=dms_url.id "
                    + "and dd.dms_url=dms_url.id "
                    + "and dms_urls.kassenzeichen_reference= " + kz + " "
                    + "group by dms_url.id "
                    + "having count(dd.id)<2) ";
        final SimpleDbAction sdba = new SimpleDbAction();
        sdba.setType(SimpleDbAction.DELETE);
        sdba.setDescription("DELETE FROM DMS_URL ...");
        sdba.setStatement(stmnt);
        return sdba;
    }
    /**
     * DOCUMENT ME!
     *
     * @param   kz  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static SimpleDbAction getAction4DeletionKassenzeichen_DMS_URLS(final String kz) {
        final String stmnt = "delete from dms_urls where kassenzeichen_reference= " + kz;
        final SimpleDbAction sdba = new SimpleDbAction();
        sdba.setType(SimpleDbAction.DELETE);
        sdba.setDescription("DELETE FROM DMS_URLS ...");
        sdba.setStatement(stmnt);
        return sdba;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   kz  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static SimpleDbAction getAction4DeletionKassenzeichen_KANALANSCHLUSS(final String kz) {
        final String stmnt =
            "delete from kanalanschluss where id in (select kanalanschluss from kassenzeichen where id = "
                    + kz
                    + ")";
        final SimpleDbAction sdba = new SimpleDbAction();
        sdba.setType(SimpleDbAction.DELETE);
        sdba.setDescription("DELETE FROM KANALANSCHLUSS ...");
        sdba.setStatement(stmnt);
        return sdba;
    }
    /**
     * DOCUMENT ME!
     *
     * @param   kz  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static SimpleDbAction getAction4DeletionKassenzeichen_BEFREIUNGERLAUBNISARRAY(final String kz) {
        final String stmnt =
            "delete from BEFREIUNGERLAUBNISARRAY where kanalanschluss_reference in (select befreiungenunderlaubnisse from "
                    + "kassenzeichen,kanalanschluss where kassenzeichen.kanalanschluss=kanalanschluss.id and kassenzeichen.id="
                    + kz
                    + ")";
        final SimpleDbAction sdba = new SimpleDbAction();
        sdba.setType(SimpleDbAction.DELETE);
        sdba.setDescription("DELETE FROM BEFREIUNGERLAUBNISARRAY ...");
        sdba.setStatement(stmnt);
        return sdba;
    }
    /**
     * DOCUMENT ME!
     *
     * @param   kz  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static SimpleDbAction getAction4DeletionKassenzeichen_BEFREIUNGERLAUBNIS(final String kz) {
        final String stmnt =
            "delete from BEFREIUNGERLAUBNIS where id in (select befreiungerlaubnisarray.befreiungerlaubnis from "
                    + "befreiungerlaubnisarray,kanalanschluss,kassenzeichen where   "
                    + "kassenzeichen.kanalanschluss=kanalanschluss.id and "
                    + "kassenzeichen.id="
                    + kz
                    + " and "
                    + "befreiungenunderlaubnisse=befreiungerlaubnisarray.kanalanschluss_reference)";
        final SimpleDbAction sdba = new SimpleDbAction();
        sdba.setType(SimpleDbAction.DELETE);
        sdba.setDescription("DELETE FROM BEFREIUNGERLAUBNIS ...");
        sdba.setStatement(stmnt);
        return sdba;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getLetzteAenderung() {
        return letzteAenderung;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  letzteAenderung  DOCUMENT ME!
     */
    public void setLetzteAenderung(final String letzteAenderung) {
        this.letzteAenderung = letzteAenderung;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class SperrenModel extends javax.swing.JToggleButton.ToggleButtonModel {

        //~ Methods ------------------------------------------------------------

        @Override
        public void setSelected(final boolean b) {
            if (log.isDebugEnabled()) {
                log.debug("sperrenModel: setSelected(" + b + ")");
            }
            super.setSelected(b);
            final boolean oldSperre = sperre;
            sperre = b;
            if (b && (oldSperre != sperre)) {
                String answer = null;
                while ((answer == null) || (answer.trim().length() == 0)) {
                    answer = JOptionPane.showInputDialog(de.cismet.verdis.gui.Main.THIS.getRootPane(),
                            "Bitte eine Bemerkung zur Sperre angeben.",
                            bemerkung_sperre);
                }
                bemerkung_sperre = answer;
                updateBemSperreModel();
            }
            if (!sperre) {
                try {
                    bemerkungSperreModel.remove(0, bemerkungSperreModel.getLength());
                } catch (Exception e) {
                    if (log.isDebugEnabled()) {
                        log.debug("???", e);
                    }
                }
            } else {
                updateBemSperreModel();
            }

            fireActionPerformed(new ActionEvent(this, 0, "AfterDialog"));
        }

        @Override
        public boolean isSelected() {
            return sperre;
        }
    }
}
