/*
 * Kassenzeichen.java
 *
 * Created on 20. Januar 2005, 11:32
 */

package de.cismet.verdis.data;
import de.cismet.tools.gui.dbwriter.SimpleDbAction;
import de.cismet.validation.Validatable;
import de.cismet.verdis.gui.Main;
import de.cismet.verdis.models.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

/**
 *
 * @author hell
 */
public class Kassenzeichen implements Cloneable{
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
    
    /** Creates a new instance of Kassenzeichen */
    public Kassenzeichen() {
        initModels();
        
    }
    
    
    public void updateModels() {
        try {kassenzeichenModel.insertString(0,kassenzeichenString,null);}
        catch (Exception e) {log.debug("???",e);}
        try {erfassungsdatumModel.insertString(0,erfassungsdatum,null);}
        catch (Exception e) {log.debug("???",e);}
        try {veranlagungsdatumModel.insertString(0, veranlagungsdatum,null);}
        catch (Exception e) {log.debug("???",e);}
        try {bemerkungsModel.insertString(0,bemerkung,null);}
        catch (Exception e) {log.debug("???",e);}
//    try {bemerkungSperreModel.insertString(0,bemerkung_sperre,null);}
//        catch (Exception e) {log.debug("???",e);}    
       
        if (sperre) {
            updateBemSperreModel();
        }

    }
    public void updateBemSperreModel() {
        try {
            this.bemerkungSperreModel.remove(0,bemerkungSperreModel.getLength());
            this.bemerkungSperreModel.insertString(0,bemerkung_sperre.toString(),null);
        }
        catch (Exception e) {log.debug("???",e);}
    }

    public void fillFromObjectArray(Object[] oa) {
        //id,datum_erfassung,datum_veranlagung,bemerkung,sperre,bemerkung_sperre,,letzte_aenderung_von,letzte_aenderung_am
        kassenzeichenString=oa[0].toString();
        java.sql.Date d=(java.sql.Date)oa[1];
        erfassungsdatum=java.text.DateFormat.getDateInstance().format(d);
        if (oa[2]!=null) {
            veranlagungsdatum=oa[2].toString();
        }
        if (oa[3]!=null) {
            bemerkung=oa[3].toString();
        }
        if (oa[4].toString().trim().toLowerCase().equals("t")) {
            sperre=true;
        }
        else {
            sperre=false;
        }
        if (oa[5]!=null) {
            bemerkung_sperre=oa[5].toString();
        }
        
        if (oa[6]!=null&&oa[7]!=null) {
            setLetzteAenderung(oa[6]+" ("+oa[7]+")");
        }
        updateModels();
    }
    
    public void initModels() {
        initKassenzeichenModel();
        initErfassungsdatumModel();
        initVeranlagungsdatumModel();
        initBemerkungsModel();
        initSperrenModel();
        initBemerkungSperrenModel();
    }
    public void initKassenzeichenModel() {
        kassenzeichenModel= new SimpleDocumentModel(){
            public void assignValue(String newValue) {
                kassenzeichenString=newValue;
                fireValidationStateChanged();
            }
            public int getStatus() {
                if (kassenzeichenString==null) {
                    statusDescription="Kassenzeichen leer.";
                    return Validatable.ERROR;
                }
                else {
                    try {
                        int kz=new Integer(kassenzeichenString).intValue();
                        if ((kz>6000000&&kz<10000000)||(kz>20000000&&kz<20200000)) {
                            return Validatable.VALID;
                        }
                        else {
                            statusDescription="Kassenzeichen nicht im g\u00FCltigen Bereich.";
                            return Validatable.ERROR;
                        }
                    }
                    catch (Exception e) {
                        statusDescription="Kassenzeichen muss eine g\u00FCltige Zahl sein.";
                        return Validatable.ERROR;
                    }
                }
            }
        };
    }
    public void initErfassungsdatumModel() {
        erfassungsdatumModel= new SimpleDocumentModel(){
            public void assignValue(String newValue) {
                erfassungsdatum=newValue;
                fireValidationStateChanged();
            }
            public int getStatus() {
                //java.sql.Date d = new java.sql.Date
                java.text.DateFormat df=DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY);
                try {
                    df.parse(erfassungsdatum);
                }
                catch (java.text.ParseException pe) {
                    statusDescription="Kein g\u00FCltiges Datum.";
                    return Validatable.ERROR;
                }
                return Validatable.VALID;
            }
        };
     }
    public void initVeranlagungsdatumModel() {
        veranlagungsdatumModel= new SimpleDocumentModel(){
            public void assignValue(String newValue) {
                veranlagungsdatum=newValue;
                fireValidationStateChanged();
            }
            public int getStatus() {
                if (veranlagungsdatum==null) {
                    statusDescription="Veranlagungsdatum darf nicht leer sein.";
                    return Validatable.ERROR;
                }
                
                boolean b = Pattern.matches("\\d\\d/(01|02|03|04|05|06|07|08|09|10|11|12)", veranlagungsdatum);
                if (b) {
                    return Validatable.VALID;
                }
                else {
                    statusDescription="Veranlagungsdatum muss im Format JJ/MM eingegeben werden.";
                    return Validatable.ERROR;
                }
            }
        };
     }
    public void initBemerkungsModel() {
        bemerkungsModel= new SimpleDocumentModel(){
            public void assignValue(String newValue) {
                bemerkung=newValue;
            }
        };
    }
    private String nullAwareSqlStringMaker(String s){
        if (s!=null){
            return "'"+s+"'";
        }
        else {
            return null;
        }
    }

    public void initSperrenModel() {
        sperrenModel=new  SperrenModel();
    }
    public void initBemerkungSperrenModel() {
        bemerkungSperreModel= new PlainDocument();
    }
    
    public PlainDocument getKassenzeichenModel() {
        return kassenzeichenModel;
    }
    public PlainDocument getErfassungsdatumModel() {
        return erfassungsdatumModel;
    }
    public PlainDocument getVeranlagungsdatumModel() {
        return veranlagungsdatumModel;
    }
    public PlainDocument getBemerkungsModel() {
        return bemerkungsModel;
    }
    public javax.swing.JToggleButton.ToggleButtonModel getSperrenModel() {
        return sperrenModel;
    }
    public PlainDocument getBemerkungSperreModel() {
        return bemerkungSperreModel;
    }

    public String toString() {
        return this.kassenzeichenString;
    }
    
    public String getKassenzeichen(){
        return kassenzeichenString;
    }
    
    public Object clone() throws CloneNotSupportedException {
        Kassenzeichen k=new Kassenzeichen();
        k.bemerkung=bemerkung;
        k.bemerkung_sperre=bemerkung_sperre;
        k.erfassungsdatum=erfassungsdatum;
        k.kassenzeichenString=kassenzeichenString;
        k.sperre=sperre;
        k.veranlagungsdatum=veranlagungsdatum;
        return k;
    }

    public boolean equals(Object o) {
        Kassenzeichen k=null;
        try {
            k=(Kassenzeichen)o;
        }
        catch (Exception e) {
            return false;
        }
        try {
            if (
                    ((kassenzeichenString==null&&k.kassenzeichenString==null)||(k.kassenzeichenString!=null&&k.kassenzeichenString.equals(kassenzeichenString)))&&
                    ((bemerkung==null&&k.bemerkung==null)||(k.bemerkung!=null&&k.bemerkung.equals(bemerkung)))&&
                    ((bemerkung_sperre==null&&k.bemerkung_sperre==null)||(k.bemerkung_sperre!=null&&k.bemerkung_sperre.equals(bemerkung_sperre)))&&
                    ((erfassungsdatum==null&&k.erfassungsdatum==null)||(k.erfassungsdatum!=null&&k.erfassungsdatum.equals(erfassungsdatum)))&&
                    ((veranlagungsdatum==null&&k.veranlagungsdatum==null)||(k.veranlagungsdatum!=null&&k.veranlagungsdatum.equals(veranlagungsdatum)))&&
                    sperre==k.sperre
                ){
                return true;
            }
            else {
                return false;
            }
        }
        catch (Exception e) {
            return false;
        }
    }
    public void backup() {
        try {
            backup=(Kassenzeichen)(this.clone());
        }
        catch (Exception e) {
            log.error("Fehler beim Clonen.",e);
        }
    }
    public void setToBackupFlaeche() {
        bemerkung=backup.bemerkung;
        bemerkung_sperre=backup.bemerkung_sperre;
        erfassungsdatum=backup.erfassungsdatum;
        kassenzeichenString=backup.kassenzeichenString;
        sperre=backup.sperre;
        veranlagungsdatum=backup.veranlagungsdatum;
        updateModels();
    }
    public boolean hasChanged() {
        if (this.equals(backup)) {
            return false;
        }
        else {
            return true;
        }
    }
    

    
    public SimpleDbAction createUpdateAction() {
        String sperreString="F";
        if (sperre) {
            sperreString="T";
        }
        SimpleDbAction sdba=new SimpleDbAction();
        sdba.setStatement("update kassenzeichen "+
                "set "+
                "datum_erfassung='"+erfassungsdatum+"',"+
                "datum_veranlagung='"+veranlagungsdatum+"',"+
                "bemerkung="+nullAwareSqlStringMaker(bemerkung)+","+
                "sperre='"+sperreString+"',"+
                "bemerkung_sperre="+nullAwareSqlStringMaker(bemerkung_sperre)+" " +
                "where id="+kassenzeichenString);
        sdba.setDescription("Ver\u00E4ndere die Tabelle >>KASSENZEICHEN<<");
        sdba.setType(SimpleDbAction.UPDATE);
        return sdba;
            
    }
    
    public static void collectActions4RenameKassenzeichen(Vector v,String oldKZ,String newKZ) {
        v.add(getAction4RenameKassenzeichen_CS_CAT_NODE(oldKZ, newKZ));
        v.add(getAction4RenameKassenzeichen_KASSENZEICHEN(oldKZ, newKZ));
        v.add(getAction4RenameKassenzeichen_DMS_URLS(oldKZ, newKZ));
        v.add(getAction4RenameKassenzeichen_FLAECHEN(oldKZ, newKZ));
        v.add(getAction4RenameKassenzeichen_CS_ALL_ATTR_MAPPING(oldKZ, newKZ));
        v.add(getAction4RenameKassenzeichen_CS_ATTR_STRING(oldKZ, newKZ));
        v.add(getAction4RenameKassenzeichen_CS_LOCKS(oldKZ, newKZ));

    }
    private static SimpleDbAction getAction4RenameKassenzeichen_CS_CAT_NODE(String oldKZ,String newKZ) {
        SimpleDbAction sdba = new SimpleDbAction();
        sdba.setStatement("UPDATE cs_cat_node SET " +
                "   name = 'Kassenzeichen: " + newKZ + "'," +
                "   object_id = " + newKZ + ", " +
                "   org = NULL " +
                "WHERE " +
                "   class_id = " + Main.KASSENZEICHEN_CLASS_ID + " AND " +
                "   object_id = " + oldKZ
        );
        sdba.setDescription("UPDATE KASSENZEICHEN SET id ...");
        sdba.setType(SimpleDbAction.UPDATE);
        return sdba;
    }
    private static SimpleDbAction getAction4RenameKassenzeichen_KASSENZEICHEN(String oldKZ,String newKZ) {
        SimpleDbAction sdba = new SimpleDbAction();
        sdba.setStatement("UPDATE kassenzeichen SET " +
                "   id = " + newKZ + " " +
                "WHERE " +
                "   id = " + oldKZ
        );
        sdba.setDescription("UPDATE KASSENZEICHEN SET id ...");
        sdba.setType(SimpleDbAction.UPDATE);
        return sdba;
    }
    private static SimpleDbAction getAction4RenameKassenzeichen_DMS_URLS(String oldKZ,String newKZ) {
        SimpleDbAction sdba = new SimpleDbAction();
        sdba.setStatement("UPDATE dms_urls SET " +
                "   kassenzeichen_reference = " + newKZ + " " +
                "WHERE " +
                "   kassenzeichen_reference = " + oldKZ
        );
        sdba.setDescription("UPDATE DMS_URLS SET kassenzeichen ...");
        sdba.setType(SimpleDbAction.UPDATE);
        return sdba;
    }
    private static SimpleDbAction getAction4RenameKassenzeichen_FLAECHEN(String oldKZ,String newKZ) {
        SimpleDbAction sdba = new SimpleDbAction();
        sdba.setStatement("UPDATE flaechen SET " +
                "   kassenzeichen_reference = " + newKZ + " " +
                "WHERE " +
                "   kassenzeichen_reference = " + oldKZ
        );
        sdba.setDescription("UPDATE FLAECHEN SET kassenzeichen ...");
        sdba.setType(SimpleDbAction.UPDATE);
        return sdba;
    }
    private static SimpleDbAction getAction4RenameKassenzeichen_CS_ALL_ATTR_MAPPING(String oldKZ,String newKZ) {
        SimpleDbAction sdba = new SimpleDbAction();
        sdba.setStatement("UPDATE cs_all_attr_mapping SET " +
                "   object_id = " + newKZ + " " +
                "WHERE " +
                "   class_id = " + Main.KASSENZEICHEN_CLASS_ID + " AND " +
                "   object_id = " + oldKZ
        );
        sdba.setDescription("UPDATE CS_ALL_ATTR_MAPPING SET kassenzeichen ...");
        sdba.setType(SimpleDbAction.UPDATE);
        return sdba;
    }
    private static SimpleDbAction getAction4RenameKassenzeichen_CS_ATTR_STRING(String oldKZ,String newKZ) {
        SimpleDbAction sdba = new SimpleDbAction();
        sdba.setStatement("UPDATE cs_attr_string SET " +
                "   object_id = " + newKZ + " " +
                "WHERE " +
                "   class_id = " + Main.KASSENZEICHEN_CLASS_ID + " AND " +
                "   object_id = " + oldKZ
        );
        sdba.setDescription("UPDATE CS_ATTR_STRING SET kassenzeichen ...");
        sdba.setType(SimpleDbAction.UPDATE);
        return sdba;
    }
    private static SimpleDbAction getAction4RenameKassenzeichen_CS_LOCKS(String oldKZ,String newKZ) {
        SimpleDbAction sdba = new SimpleDbAction();
        sdba.setStatement("UPDATE cs_locks SET " +
                "   object_id = " + newKZ + " " +
                "WHERE " +
                "   class_id = " + Main.KASSENZEICHEN_CLASS_ID + " AND " +
                "   object_id = " + oldKZ
        );
        sdba.setDescription("UPDATE CS_ATTR_STRING SET kassenzeichen ...");
        sdba.setType(SimpleDbAction.UPDATE);
        return sdba;
    }

    public static void collectActions4NewKassenzeichen(Vector v,String newKZ) {
        v.add(getAction4NewKassenzeichen_CS_CAT_NODE(newKZ));
        v.add(getAction4NewKassenzeichen_Kassenzeichen(newKZ));
        v.add(getAction4NewKassenzeichen_Geom(newKZ));
        v.add(getAction4NewKassenzeichen_DMS_URLS(newKZ));
//        v.add(getAction4NewKassenzeichen_DMS_URL(newKZ));
//        v.add(getAction4NewKassenzeichen_URL(newKZ));
        
    }
    private static SimpleDbAction getAction4NewKassenzeichen_CS_CAT_NODE(String newKZ) {
        SimpleDbAction sdba=new SimpleDbAction();
        sdba.setStatement("insert into cs_cat_node " +
                "(id, name, descr, class_id, object_id, node_type, is_root, org) " +
                "values (" +
                "nextval('cs_cat_node_sequence')," +
                "'Kassenzeichen: "+newKZ+"',"+
                "null,"+
                Main.KASSENZEICHEN_CLASS_ID+","+
                newKZ+","+
                "'O',"+
                "'F',"+
                "null"+
                ")"
        );
        sdba.setDescription("INSERT INTO KASSENZEICHEN ...");
        sdba.setType(SimpleDbAction.INSERT);
        return sdba;
    }
    private static SimpleDbAction getAction4NewKassenzeichen_Kassenzeichen(String newKZ) {
        SimpleDbAction sdba=new SimpleDbAction();
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMANY);
        Calendar cal=Calendar.getInstance();
        String erfassungsdatum="'"+df.format(cal.getTime())+"'";
        cal.add(Calendar.MONTH,1);
        SimpleDateFormat vDat = new SimpleDateFormat("yy/MM");
        String veranlagungsdatum="'"+vDat.format(cal.getTime())+"'";
        sdba.setStatement("insert into kassenzeichen " +
                "(id, datum_erfassung, datum_veranlagung, bemerkung, sperre, bemerkung_sperre, dms_urls, flaechen, geometrie,letzte_aenderung_von,letzte_aenderung_ts) " +
                "values (" +
                newKZ +","+
                erfassungsdatum+","+
                veranlagungsdatum +","+
                "null,"+
                "'F',"+
                "null,"+
                newKZ+","+
                newKZ+","+
                "nextval('geom_seq')," +
                "'"+Main.THIS.getUserString()+"',"+
                "now()"+
                ")"
        );
        sdba.setDescription("INSERT INTO KASSENZEICHEN ...");
        sdba.setType(SimpleDbAction.INSERT);
        return sdba;
    }
    private static SimpleDbAction getAction4NewKassenzeichen_Geom(String newKZ) {
        SimpleDbAction sdba=new SimpleDbAction();
        sdba.setStatement("insert into geom (id, geo_field) " +
                "values (" +
                "currval('geom_seq'),"+
                "null"+
                ")"
        );
        sdba.setDescription("INSERT INTO GEOM ... (zusammenfassende BoundingBox)");
        sdba.setType(SimpleDbAction.INSERT);
        return sdba;
    }
    private static SimpleDbAction getAction4NewKassenzeichen_DMS_URLS(String newKZ) {
        SimpleDbAction sdba=new SimpleDbAction();
        sdba.setStatement("insert into dms_urls (id, dms_url, kassenzeichen_reference) " +
                "values (" +
                "nextval('dms_urls_seq'),"+
                Main.DMS_URL_ID+","+
                newKZ+
                ")"
        );
        sdba.setDescription("INSERT INTO DMS_URLS ...");
        sdba.setType(SimpleDbAction.INSERT);
        return sdba;
    }
    
    public static void collectActions4DeleteKassenzeichen(Vector v,String kz) {
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

        //als allerletztes
        v.add(getAction4DeletionKassenzeichen_KASSENZEICHEN(kz));
        
    }
    private static SimpleDbAction getAction4DeletionKassenzeichen_CS_CAT_NODE(String kz) {
        String stmnt="delete from cs_cat_node where class_id="+Main.KASSENZEICHEN_CLASS_ID+" and object_id="+kz;
        SimpleDbAction sdba= new SimpleDbAction();
        sdba.setType(SimpleDbAction.DELETE);
        sdba.setDescription("DELETE FROM GEOM (Geometrien) ...");
        sdba.setStatement(stmnt);
        return sdba;
        
    }
    private static SimpleDbAction getAction4DeletionKassenzeichen_GEOM(String kz) {
        String stmnt="delete from geom where id in ( "+
        "select geom.id  from flaechen,flaeche,flaecheninfo,geom,flaeche ff  "+
        "where flaechen.flaeche=flaeche.id  "+
        "and flaeche.flaecheninfo=flaecheninfo.id "+
        "and flaecheninfo.geometrie=geom.id "+
        "and flaecheninfo.id=ff.flaecheninfo "+
        "and kassenzeichen_reference= "+ kz + " " +
        "group by geom.id "+
        "having count(ff.id)<2)";
        SimpleDbAction sdba= new SimpleDbAction();
        sdba.setType(SimpleDbAction.DELETE);
        sdba.setDescription("DELETE FROM GEOM (Geometrien) ...");
        sdba.setStatement(stmnt);
        return sdba;
    }
    private static SimpleDbAction getAction4DeletionKassenzeichen_FLAECHENINFO(String kz) {
        String stmnt="delete from flaecheninfo where id in (     "+
        "select flaecheninfo.id  from flaechen,flaeche,flaecheninfo,flaeche ff where "+
        "flaechen.flaeche=flaeche.id and flaeche.flaecheninfo=flaecheninfo.id "+
        "and flaecheninfo.id=ff.flaecheninfo "+
        "and kassenzeichen_reference= "+kz + " "+
        "group by flaecheninfo.id "+
        "having count(ff.id)<2) ";
        SimpleDbAction sdba= new SimpleDbAction();
        sdba.setType(SimpleDbAction.DELETE);
        sdba.setDescription("DELETE FROM FLAECHENINFO ...");
        sdba.setStatement(stmnt);
        return sdba;
    }
    private static SimpleDbAction getAction4DeletionKassenzeichen_FLAECHE(String kz) {
        String stmnt="delete from flaeche where id in ( "+
        "select flaeche from flaechen where kassenzeichen_reference="+kz+")";
        SimpleDbAction sdba= new SimpleDbAction();
        sdba.setType(SimpleDbAction.DELETE);
        sdba.setDescription("DELETE FROM FLAECHE ...");
        sdba.setStatement(stmnt);
        return sdba;
    }
    private static SimpleDbAction getAction4DeletionKassenzeichen_FLAECHEN(String kz) {
        String stmnt="delete from flaechen where kassenzeichen_reference= "+kz;
        SimpleDbAction sdba= new SimpleDbAction();
        sdba.setType(SimpleDbAction.DELETE);
        sdba.setDescription("DELETE FROM FLAECHEN ...");
        sdba.setStatement(stmnt);
        return sdba;
    }
    private static SimpleDbAction getAction4DeletionKassenzeichen_GEOM_KZ(String kz) {
        String stmnt="delete from geom where id in (select geom.id from " +
        "kassenzeichen,geom where geometrie=geom.id and kassenzeichen.id= "+kz+")";
        SimpleDbAction sdba= new SimpleDbAction();
        sdba.setType(SimpleDbAction.DELETE);
        sdba.setDescription("DELETE FROM GEOM (zusammenfassende Geometrie) ...");
        sdba.setStatement(stmnt);
        return sdba;
    }
    private static SimpleDbAction getAction4DeletionKassenzeichen_KASSENZEICHEN(String kz) {
        String stmnt="delete from kassenzeichen where id= "+kz;
        SimpleDbAction sdba= new SimpleDbAction();
        sdba.setType(SimpleDbAction.DELETE);
        sdba.setDescription("DELETE FROM GEOM (Geometrien) ...");
        sdba.setStatement(stmnt);
        return sdba;
    }
    private static SimpleDbAction getAction4DeletionKassenzeichen_URL_BASE(String kz) {
        String stmnt="delete from url_base where id in (" +
        "select url_base.id from dms_urls,dms_url,url, dms_url as dd "+
        "where dms_urls.dms_url=dms_url.id  "+
        "and dms_url.url_id=url.id "+
        "and kassenzeichen_reference="+kz+" "+
        "and dd.url_id=url.id "+
        "group by url_base.id "+
        "having count(dd.id)<2 )";
        SimpleDbAction sdba= new SimpleDbAction();
        sdba.setType(SimpleDbAction.DELETE);
        sdba.setDescription("DELETE FROM URL_BASE ...");
        sdba.setStatement(stmnt);
        return sdba;
    }    
    private static SimpleDbAction getAction4DeletionKassenzeichen_URL(String kz) {
        String stmnt="delete from url where id in ( "+
        "select url.id from dms_urls,dms_url,url, dms_url as dd "+
        "where dms_urls.dms_url=dms_url.id  "+
        "and dms_url.url_id=url.id "+
        "and kassenzeichen_reference= "+kz+" "+
        "and dd.url_id=url.id "+
        "group by url.id "+
        "having count(dd.id)<2) ";
        SimpleDbAction sdba= new SimpleDbAction();
        sdba.setType(SimpleDbAction.DELETE);
        sdba.setDescription("DELETE FROM URL ...");
        sdba.setStatement(stmnt);
        return sdba;
    }    
    private static SimpleDbAction getAction4DeletionKassenzeichen_DMS_URL(String kz) {
        String stmnt="delete from dms_urls where dms_url in( "+
        "select dms_url.id  from dms_urls,dms_url,dms_urls as dd "+
        "where dms_urls.dms_url=dms_url.id "+
        "and dd.dms_url=dms_url.id "+
        "and dms_urls.kassenzeichen_reference= "+kz+" "+
        "group by dms_url.id "+
        "having count(dd.id)<2) ";
        SimpleDbAction sdba= new SimpleDbAction();
        sdba.setType(SimpleDbAction.DELETE);
        sdba.setDescription("DELETE FROM DMS_URL ...");
        sdba.setStatement(stmnt);
        return sdba;
    }    
    private static SimpleDbAction getAction4DeletionKassenzeichen_DMS_URLS(String kz) {
        String stmnt="delete from dms_urls where kassenzeichen_reference= "+kz;
        SimpleDbAction sdba= new SimpleDbAction();
        sdba.setType(SimpleDbAction.DELETE);
        sdba.setDescription("DELETE FROM DMS_URLS ...");
        sdba.setStatement(stmnt);
        return sdba;
    }
    
    private static SimpleDbAction getAction4DeletionKassenzeichen_KANALANSCHLUSS(String kz) {
        String stmnt="delete from kanalanschluss where id in (select kanalanschluss from kassenzeichen where id = "+kz+")";
        SimpleDbAction sdba= new SimpleDbAction();
        sdba.setType(SimpleDbAction.DELETE);
        sdba.setDescription("DELETE FROM KANALANSCHLUSS ...");
        sdba.setStatement(stmnt);
        return sdba;
    }
    private static SimpleDbAction getAction4DeletionKassenzeichen_BEFREIUNGERLAUBNISARRAY(String kz) {
        String stmnt="delete from BEFREIUNGERLAUBNISARRAY where kanalanschluss_reference in (select befreiungenunderlaubnisse from " +
                "kassenzeichen,kanalanschluss where kassenzeichen.kanalanschluss=kanalanschluss.id and kassenzeichen.id="+kz+")";
        SimpleDbAction sdba= new SimpleDbAction();
        sdba.setType(SimpleDbAction.DELETE);
        sdba.setDescription("DELETE FROM BEFREIUNGERLAUBNISARRAY ...");
        sdba.setStatement(stmnt);
        return sdba;
    }
    private static SimpleDbAction getAction4DeletionKassenzeichen_BEFREIUNGERLAUBNIS(String kz) {
        String stmnt="delete from BEFREIUNGERLAUBNIS where id in (select befreiungerlaubnisarray.befreiungerlaubnis from " +
                "befreiungerlaubnisarray,kanalanschluss,kassenzeichen where   " +
                "kassenzeichen.kanalanschluss=kanalanschluss.id and " +
                "kassenzeichen.id="+kz+" and " +
                "befreiungenunderlaubnisse=befreiungerlaubnisarray.kanalanschluss_reference)";
        SimpleDbAction sdba= new SimpleDbAction();
        sdba.setType(SimpleDbAction.DELETE);
        sdba.setDescription("DELETE FROM BEFREIUNGERLAUBNIS ...");
        sdba.setStatement(stmnt);
        return sdba;
    }
        
    
    
    
    
    private class SperrenModel extends javax.swing.JToggleButton.ToggleButtonModel {
        public void setSelected(boolean b) {
            log.debug("sperrenModel: setSelected("+b+")");
            super.setSelected(b);
            boolean oldSperre=sperre;
            sperre=b;
            if (b&&oldSperre!=sperre) {
                String answer=null;
                while (answer==null || answer.trim().length()==0) {
                    answer=JOptionPane.showInputDialog(de.cismet.verdis.gui.Main.THIS.getRootPane(),"Bitte eine Bemerkung zur Sperre angeben.",bemerkung_sperre);
                }
                bemerkung_sperre=answer;
                updateBemSperreModel();
            }
            if (!sperre) {
                 try {
                    bemerkungSperreModel.remove(0,bemerkungSperreModel.getLength());
                }
                catch (Exception e) {log.debug("???",e);}
            } 
            else {
                updateBemSperreModel();
            }

            fireActionPerformed(new ActionEvent(this,0,"AfterDialog"));
                        
        }
            
        public boolean isSelected() {
           return sperre;
        }
    }

    public String getLetzteAenderung() {
        return letzteAenderung;
    }

    public void setLetzteAenderung(String letzteAenderung) {
        this.letzteAenderung = letzteAenderung;
    }

}
