/*
 * FlaechenUebersichtsTabellenModel.java
 *
 * Created on 7. Januar 2005, 11:55
 */

package de.cismet.verdis.gui;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.jtsgeometryfactories.PostGisGeometryFactory;
import de.cismet.tools.collections.MultiMap;
import de.cismet.tools.gui.dbwriter.SimpleDbAction;
import de.cismet.validation.NotValidException;
import de.cismet.validation.Validatable;
import de.cismet.verdis.data.Flaeche;
import de.cismet.verdis.data.FlaechenBeschreibung;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.SortOrder;

/**
 *
 * @author hell
 */
public class FlaechenUebersichtsTableModel extends AbstractTableModel implements DocumentListener, TableCellRenderer, ActionListener,ListDataListener {
    private Flaeche selectedFlaeche;
    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private Connection connection;
    private Thread lookupThread;
    private DefaultTableCellRenderer defaultRenderer=new DefaultTableCellRenderer();
    String[] columnNames={" ","Bezeichnung"," ", "Gr\u00F6\u00DFe in m²","Fl\u00E4chenart","Anschlu\u00DFgrad"};
    javax.swing.ImageIcon mult;
    javax.swing.ImageIcon edited;
    javax.swing.ImageIcon warn;
    
    Vector data=new Vector();
    Vector fresh=new Vector();
    Vector deleted=new Vector();
    
    JTable table;
    LinkedHashMap reverseArtHm=new LinkedHashMap();
    LinkedHashMap reverseGradHm=new LinkedHashMap();
    LinkedHashMap artHm=new LinkedHashMap();
    LinkedHashMap gradHm=new LinkedHashMap();
    
    LinkedHashMap beschreibungHm=new LinkedHashMap();
    LinkedHashMap reverseBeschreibungHm=new LinkedHashMap();
    
    private String kassenzeichen;
    
    MappingComponent mappingComponent=null;
    private boolean tmpNoSort=false;
    /** Creates a new instance of FlaechenUebersichtsTabellenModel */
    public FlaechenUebersichtsTableModel(Connection c,JTable table,MappingComponent mappingComponent) {
        connection=c;
        this.mappingComponent=mappingComponent;
        
        mult=new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/table/mult.png"));
        edited=new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/table/edited.png"));
        warn=new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/table/warn.png"));
        this.table=table;
    }
    
    public void createAndAddStatements(Vector v) throws NotValidException{
        //Neue und ge\u00E4nderte
        boolean aAreaChanged=false;
        Iterator it=data.iterator();
        while(it.hasNext()) {
            Flaeche f=(Flaeche)it.next();
            if (f.isValid()==false) {
                throw new NotValidException();
            } else if (f.isNewFlaeche()||f.hasChanged()){
                f.addStatements(v);
                aAreaChanged=true;
            }
        }
        //Hier wird nun noch das GeomFeld in Kassenzeichen aktualisiert
        v.add(getStatement4KassenzeichenGeometry());
        
        
        //Gel\u00F6schte
        Iterator delIt=deleted.iterator();
        while(delIt.hasNext()) {
            Flaeche f=(Flaeche)delIt.next();
            f.addStatements(v);
        }
    }
    public boolean canBeDisabled(){
        return false;
    }
    public void setEnabled(boolean b) {}
    public boolean isEnabled(){
        return true;
    }
    
    
    private Geometry getEnvelope(Vector flaechen) {
        return getKassenzeichenGeometry(flaechen).getEnvelope().buffer(0.01).getEnvelope();
    }

    private GeometryCollection getKassenzeichenGeometry(Vector flaechen) {
        Vector notNullGeometries=new Vector();
        Iterator it=flaechen.iterator();
        int i=0;
        while(it.hasNext()) {
            Flaeche f=(Flaeche)it.next();
            if (f.getGeometry()!=null) {
                notNullGeometries.add(f.getGeometry());
            }
        }
        Geometry[] gArr=new Geometry[notNullGeometries.size()];
        gArr=(Geometry[])notNullGeometries.toArray(gArr);

        GeometryCollection gCol=new GeometryCollection(gArr,new GeometryFactory());
        return gCol;
    }

    //ToDo maybe not enough what about fresh,removed
    public Geometry getKassenzeichenGeometry(){
        //ToDo make tolerance configurable ??
        return getKassenzeichenGeometry(data).buffer(0.01);
    }
    
    private SimpleDbAction getStatement4KassenzeichenGeometry() {
        
        SimpleDbAction sdba=new SimpleDbAction();
        sdba.setStatement("update geom set "+
                "geo_field='"+PostGisGeometryFactory.getPostGisCompliantDbString(getEnvelope(data))+"' "+
                "from kassenzeichen "+
                "where geom.id=kassenzeichen.geometrie and kassenzeichen.id="+kassenzeichen );
        sdba.setDescription("Aktualisiere die zusammenfassende Geometrie");
        sdba.setType(SimpleDbAction.UPDATE);
        
        return sdba;
        
    }
    
    Object sortIdentifier;
    SortOrder sortOrder;
    
    public void setTmpNoSort(boolean b) {
        try {
            tmpNoSort=b;
            
            if (tmpNoSort) {
                sortIdentifier=((JXTable)table).getSortedColumn().getIdentifier();
                if (sortIdentifier!=null) {
                    sortOrder=((JXTable)table).getSortOrder(sortIdentifier);
                    ((JXTable)table).setSortOrder(sortIdentifier,SortOrder.UNSORTED);
                    
                }
                
            } else {
                if (sortIdentifier!=null) {
                    ((JXTable)table).setSortOrder(sortIdentifier,sortOrder);
                    ((JXTable)table).getTableHeader().repaint();
                    
                }
                
            }
        } catch (Throwable t) {
            log.warn("Fehler in setTmpNoSort("+b+")",t);
        }
    }
    public boolean getTmpNoSort(){
        return tmpNoSort;
    }
    
    
    public void addFlaecheWithoutRefresh(Flaeche f) {
        if(f.getClipboardStatus()==Flaeche.CUTTED) {
            //f.setClipboardStatus(Flaeche.NONE);
            f.setKassenzeichen(kassenzeichen);
            f.backup();
            //Vorbelegung der Werte
            f.setBezeichnung(getValidFlaechenname(f.getArt()));
            DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMANY);
            Calendar cal=Calendar.getInstance();
            f.setErfassungsdatum(df.format(cal.getTime()));
            cal.add(Calendar.MONTH,1);
            SimpleDateFormat vDat = new SimpleDateFormat("yy/MM");
            
            f.setVeranlagungsdatum(vDat.format(cal.getTime()));
            
            f.sync();
            f.setNewFlaeche(true);
            data.add(f);
            mappingComponent.getFeatureCollection().addFeature(f);
            fresh.add(f);
        } else if (f.getClipboardStatus()==Flaeche.COPIED) {
            Flaeche c=(Flaeche)f.clone();
            c.setFl_id((fresh.size()+1)*(-1));
            //c.setAnteil(null);
            c.setBemerkung(null);
            c.setKassenzeichen(kassenzeichen);
            //Vorbelegung der Werte
            c.setBezeichnung(getValidFlaechenname(f.getArt()));
            DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMANY);
            Calendar cal=Calendar.getInstance();
            c.setErfassungsdatum(df.format(cal.getTime()));
            cal.add(Calendar.MONTH,1);
            SimpleDateFormat vDat = new SimpleDateFormat("yy/MM");
            c.setNewFlaeche(true);
            c.setVeranlagungsdatum(vDat.format(cal.getTime()));
            c.backup();
            c.sync();
            
            data.add(c);
            c.setEditable(true);
            mappingComponent.getFeatureCollection().addFeature(c);
            fresh.add(c);
            //c.setClipboardStatus(Flaeche.NONE);
            c.backup();
        }
    }
    
    public void addFlaeche(Flaeche f) {
        addFlaecheWithoutRefresh(f);
        refreshAfterAdd(f);
    }
    
    public void refreshAfterAdd(Flaeche f) {
        fireTableDataChanged();
    }
    
    public void setConnection(Connection c) {
        connection=c;
        ((org.postgresql.PGConnection)connection).addDataType("geometry","org.postgis.PGgeometry");
        ((org.postgresql.PGConnection)connection).addDataType("box3d","org.postgis.PGbox3d");
        try {
            Statement stmnt=c.createStatement();
            ResultSet rs=stmnt.executeQuery("select id,art_abkuerzung  from flaechenart order by 1");
            while (rs.next()) {
                reverseArtHm.put(rs.getString(2), rs.getString(1));
                artHm.put(new Integer(rs.getInt(1)), rs.getString(2));
            }
            rs=stmnt.executeQuery("select id,grad_abkuerzung  from anschlussgrad order by 1");
            while (rs.next()) {
                reverseGradHm.put(rs.getString(2), rs.getString(1));
                gradHm.put(new Integer(rs.getInt(1)), rs.getString(2));
            }
            rs=stmnt.executeQuery("select id,beschreibung,dachflaeche from flaechenbeschreibung order by 1");
            while (rs.next()) {
                reverseBeschreibungHm.put(new FlaechenBeschreibung(rs.getBoolean(3), rs.getString(2)),new Integer(rs.getInt(1)));
                beschreibungHm.put(new Integer(rs.getInt(1)), new FlaechenBeschreibung(rs.getBoolean(3), rs.getString(2)));
            }
        } catch (Exception e) {
            log.error("Fehler beim Auslesen von Art und Grad",e);
        }
    }
    
    public String getColumnName(int column) {
        return columnNames[column];
    }
    public int getRowCount() {
        return data.size();
    }
    public int getColumnCount() {
        return 6;
    }
    public Class getColumnClass(int column) {
        if ( column==0 ||column==2 ) {
            return javax.swing.Icon.class;
        } else {
            return String.class;
        }
    }
    
    public Flaeche getFlaechebyIndex(int index) {
        if (data.size()>0) {
            return (Flaeche)data.get(index);
        } else {
            return null;
        }
    }
    
    public int getIndexOfFlaeche(Flaeche f) {
        return data.indexOf(f);
    }
    
    
//    public void setSelectedFlaeche(Flaeche f) {
//        selectedFlaeche=f;
////        mappingComponent.getFeatureCollection().unselectAll();
////        mappingComponent.getFeatureCollection().select(f);
//    }
    
    /**
     * @deprecated
     */
    public Flaeche getSelectedFlaeche() {
        if (table.getSelectedRows().length>1||table.getSelectedRows().length==0) {
            return null;
        } else {
            int displayedIndex=table.getSelectedRow();
            int modelIndex=((JXTable)table).getFilters().convertRowIndexToModel(displayedIndex);
            return getFlaechebyIndex(modelIndex);
        }
    }
    
    
    
    
    public Object getValueAt(int row, int column) {
        try {
            Flaeche flaeche=(Flaeche)data.get(row);
            // Allgemeine Icon Spalte
            // hier kommen ein Group Icon rein wenn es eine Eigen-
            // tuemergemeinschaft gibt
            // und ein Warning Icon wenn eine Sperre besteht
            if (column==0) {
                if (flaeche.getAnteil()!=null) {
                    return mult;
                }
                if (flaeche.isSperre()) {
                    return warn;
                }
                return null;
            }
            // Bezeichnungsspalte
            else if (column==1) {
                return flaeche.getBezeichnung();
            }
            // Edit Icon Spalte
            // hier kommt ein Edit Icon rein wenn die Gr\u00F6\u00DFe von
            // Hand ge\u00E4ndert wurde
            else if (column==2) {
                if (!(flaeche.getGr_korrektur()==null||flaeche.getGr_korrektur().equals(flaeche.getGr_grafik()))) {
                    return edited;
                } else {
                    return null;
                }
            }
            // Groesse
            // Wenn in flaecheninfo.groesse_korrektur was drinsteht
            // wird das genommen
            else if (column==3) {
                if (flaeche.getGr_korrektur()!=null) {
                    return flaeche.getGr_korrektur();
                } else {
                    return flaeche.getGr_grafik();
                }
            }
            // Flaechenart
            else if (column==4) {
                return flaeche.getArt_abk();
            }
            // Anschlussgrad
            else if (column==5) {
                return flaeche.getGrad_abk();
            } else  {
                return null;
            }
        } catch (Exception e) {
            log.warn("Fehler in getValue()",e);
            return null;
        }
    }
    public void kassenzeichenChanged(final String kz) {
        kassenzeichenChanged(kz,null);
    }
    
    public Thread getLookupThread() {
        return lookupThread;
    }
    
    
    public String getValidFlaechenname(int art) {
        Iterator it=data.iterator();
        int highestNumber=0;
        String highestBezeichner=null;
        boolean noFlaeche=true;
        while (it.hasNext()) {
            noFlaeche=false;
            Flaeche f=(Flaeche)it.next();
            int a=f.getArt();
            String b=f.getBezeichnung();
            if (b==null) break;
            if (a==1||a==2) {
                //In Bezeichnung m\u00FCsste eigentlich ne Zahl stehen. Einfach ignorieren falls nicht.
                try {
                    int num=new Integer(b).intValue();
                    if (num>highestNumber) {
                        highestNumber=num;
                    }
                } catch (Exception e) {
                    log.debug("getValidFlaechenname",e);
                    break;
                }
            } else {
                //In Bezeichner m\u00FCsste jetzt ein String stehen. Fehlerhafte Bezeichner werden ignoriert.
                if (((Validatable)f.getBezeichnungsModel()).getStatus()==Validatable.VALID) {
                    
                    if (highestBezeichner==null) {
                        highestBezeichner=b;
                    } else if (
                            ( b.trim().length()>highestBezeichner.trim().length())
                            ||
                            ( b.trim().length()==highestBezeichner.trim().length() && b.compareTo(highestBezeichner)>0)
                            ) {
                        highestBezeichner=b;
                    }
                }
            }
        }
        if (noFlaeche==true) {
            highestBezeichner=null;
        }
        //highestBezeichner steht jetzt der lexikographisch h\u00F6chste Bezeichner
        //In highestNumber steht die gr\u00F6\u00DFte vorkommende Zahl f\u00FCr Dachfl\u00E4chen
        //log.debug(highestBezeichner);
        //log.debug(highestNumber+"");
        
        //n\u00E4chste freie Zahl f\u00FCr Dachfl\u00E4chen
        final int newHighestNumber=highestNumber+1;
        
        //n\u00E4chste freie Bezeichnung f\u00FCr versiegelte Fl\u00E4chen
        final String newHighestBezeichner=nextFlBez(highestBezeichner);
        log.debug("highestBezeichner"+highestBezeichner);
        log.debug("highestNumber"+highestNumber);
        log.debug("newHighestBezeichner"+newHighestBezeichner);
        log.debug("newHighestNumber"+newHighestNumber);
        
        
        switch(art) {
            case 1:
            case 2:
                return new String(newHighestNumber+"");
            case 3:
            case 4:
            case 5:
            case 6:
            default:
                if (noFlaeche) {
                    return "A";
                }
                return newHighestBezeichner;
        }
    }
    
    
    public String nextFlBez(String s) {
        boolean carry=false;
        if (s!=null) {
            s=s.trim().toUpperCase();
            char[] charArr=s.toCharArray();
            for (int i=charArr.length-1;i>=0;--i) {
                if (charArr[i]!='Z') {
                    charArr[i]=(char)(charArr[i]+1);
                    carry=false;
                    break;
                } else {
                    charArr[i]='A';
                    carry=true;
                }
            }
            String end=new String(charArr);
            
            if (carry) {
                return "A"+end;
            } else {
                return end;
            }
        }
        return "A";
    }
    
    private void emptyModel() {
        data= new Vector();
        deleted=new Vector();
        fresh=new Vector();
        selectedFlaeche=null;
        fireTableDataChanged();
//        Thread t=new Thread() {
//            public void run() {
                mappingComponent.getFeatureCollection().removeAllFeatures();
//            }
//        };
        
        //t.start();
        
        
        
    }
    
    Object kassenzeichenChangedBlocker=new Object();
    Object kassenzeichenChangedBlocker2=new Object();
    Object kassenzeichenChangedBlocker3=new Object();
    public void kassenzeichenChanged(final String kz,final javax.swing.Timer t) {
        kassenzeichen=kz;
        //TODO: zuerst \u00FCberpr\u00FCfen ob das kassenzeichen nicht gleich dem alten Kassenzeichen ist
        if (lookupThread!=null&&lookupThread.isAlive()) {
            lookupThread.interrupt();
        }
        emptyModel();
        
        lookupThread=new Thread() {
            public void run() {
                try {
                    setTmpNoSort(true);
                    Statement stmnt=connection.createStatement();
                    
//Als erstes wird mal ne Hashmap produziert mit den Querverweissfl\u00E4chen
                    
//                    select flaeche1.id as fid,flaechen1.kassenzeichen_reference as  kz,flaeche1.flaechenbezeichnung as f1,flaechen2.kassenzeichen_reference as kz2,flaeche2.flaechenbezeichnung as f2
//                    from
//                    flaechen as flaechen1,flaechen as flaechen2,
//                    flaeche as flaeche1,flaeche as flaeche2,
//                    flaecheninfo as flaecheninfo1,flaecheninfo as flaecheninfo2
//                    where
//                    flaechen1.flaeche=flaeche1.id and
//                    flaeche1.flaecheninfo=flaecheninfo1.id and
//                    flaeche1.anteil is not null
//                    and
//                    flaecheninfo2.id=flaecheninfo1.id and
//                    flaechen2.flaeche=flaeche2.id and
//                    flaeche2.flaecheninfo=flaecheninfo2.id and
//                    not flaechen2.kassenzeichen_reference=flaechen1.kassenzeichen_reference
//                    and
//                    flaechen1.kassenzeichen_reference=6446066
                    
                    ResultSet rs=stmnt.executeQuery(
                            "select flaechen1.kassenzeichen_reference as  kz,flaeche1.id as fid,flaeche1.flaechenbezeichnung as f1,flaechen2.kassenzeichen_reference as kz2,flaeche2.flaechenbezeichnung as f2 "+
                            "from "+
                            "flaechen as flaechen1,flaechen as flaechen2,"+
                            "flaeche as flaeche1,flaeche as flaeche2,"+
                            "flaecheninfo as flaecheninfo1,flaecheninfo as flaecheninfo2  "+
                            "where "+
                            "flaechen1.flaeche=flaeche1.id and "+
                            "flaeche1.flaecheninfo=flaecheninfo1.id " +
                            //" and "+
                            //"flaeche1.anteil is not null "+
                            "and "+
                            "flaecheninfo2.id=flaecheninfo1.id and "+
                            "flaechen2.flaeche=flaeche2.id and "+
                            "flaeche2.flaecheninfo=flaecheninfo2.id and "+
                            "not flaechen2.kassenzeichen_reference=flaechen1.kassenzeichen_reference "+
                            "and "+
                            "flaechen1.kassenzeichen_reference="+kz);
                    MultiMap crossReferences=new MultiMap();
                    
                    while (rs.next()&&!isInterrupted()) {
                        synchronized (kassenzeichenChangedBlocker){
                            crossReferences.put(rs.getInt("fid"),rs.getString("kz2")+":"+rs.getString("f2"));
                        }
                    }
                    rs.close();
                    
//                Musste ich mit UNION machen weil left outer join 12 mal langsamer war
//                select
//                        flaeche.id,
//                        flaeche.anteil,
//                        flaeche.flaechenbezeichnung,
//                        flaeche.bemerkung,
//                        flaeche.datum_erfassung,
//                        flaeche.datum_veranlagung,
//                        flaeche.sperre,
//                        flaeche.bemerkung_sperre,
//                        flaecheninfo.groesse_aus_grafik,
//                        flaecheninfo.groesse_korrektur,
//                        art_abkuerzung,
//                        art,
//                        grad_abkuerzung,
//                        grad,
//                        flaechenart.id,
//                        anschlussgrad.id,
//                        feb_id,
//                        flaecheninfo.id  ,
//                        geom.geo_field,
//                        geom.id
//                    from flaechen,flaeche,flaechenart,anschlussgrad,flaecheninfo ,geom
//                    where
//                    flaechen.flaeche=flaeche.id
//                    and flaeche.flaecheninfo=flaecheninfo.id
//                    and flaecheninfo.anschlussgrad=anschlussgrad.id
//                    and flaecheninfo.flaechenart=flaechenart.id
//                    and kassenzeichen_reference=6000467
//                    and (flaecheninfo.geometrie=geom.id)
//                union select
//                        flaeche.id,
//                        flaeche.anteil,
//                        flaeche.flaechenbezeichnung,
//                        flaeche.bemerkung,
//                        flaeche.datum_erfassung,
//                        flaeche.datum_veranlagung,
//                        flaeche.sperre,
//                        flaeche.bemerkung_sperre,
//                        flaecheninfo.groesse_aus_grafik,
//                        flaecheninfo.groesse_korrektur,
//                        art_abkuerzung,
//                        art,
//                        grad_abkuerzung,
//                        grad,
//                        flaechenart.id,
//                        anschlussgrad.id,
//                        feb_id,
//                        flaecheninfo.id  ,
//                        null as geo_field,
//                        -1
//                    from flaechen,flaeche,flaechenart,anschlussgrad,flaecheninfo
//                    where
//                    flaechen.flaeche=flaeche.id
//                    and flaeche.flaecheninfo=flaecheninfo.id
//                    and flaecheninfo.anschlussgrad=anschlussgrad.id
//                    and flaecheninfo.flaechenart=flaechenart.id
//                    and kassenzeichen_reference=6000467
//                    and flaecheninfo.geometrie is null
//                order by 1
                    Flaeche selectionRequest=null;
                    rs=stmnt.executeQuery("select flaeche.id,flaeche.anteil,flaeche.flaechenbezeichnung,flaeche.bemerkung,flaeche.datum_erfassung,flaeche.datum_veranlagung,flaeche.sperre,flaeche.bemerkung_sperre,flaecheninfo.groesse_aus_grafik,flaecheninfo.groesse_korrektur,art_abkuerzung,art,grad_abkuerzung,grad,flaechenart.id,anschlussgrad.id,feb_id,flaecheninfo.id,geom.geo_field,geom.id,beschreibung from flaechen,flaeche,flaechenart,anschlussgrad, flaecheninfo,geom where flaechen.flaeche=flaeche.id and flaeche.flaecheninfo=flaecheninfo.id and flaecheninfo.anschlussgrad=anschlussgrad.id and flaecheninfo.flaechenart=flaechenart.id and kassenzeichen_reference="+kz+" and flaecheninfo.geometrie=geom.id union select flaeche.id,flaeche.anteil,flaeche.flaechenbezeichnung,flaeche.bemerkung,flaeche.datum_erfassung,flaeche.datum_veranlagung,flaeche.sperre,flaeche.bemerkung_sperre,flaecheninfo.groesse_aus_grafik,flaecheninfo.groesse_korrektur,art_abkuerzung,art,grad_abkuerzung,grad,flaechenart.id,anschlussgrad.id,feb_id,flaecheninfo.id,null as geo_field,-1 as geomid,beschreibung from flaechen,flaeche,flaechenart,anschlussgrad, flaecheninfo where flaechen.flaeche=flaeche.id and flaeche.flaecheninfo=flaecheninfo.id and flaecheninfo.anschlussgrad=anschlussgrad.id and flaecheninfo.flaechenart=flaechenart.id and kassenzeichen_reference="+kz+" and flaecheninfo.geometrie is null order by 1");
                    //this.setCursor(java.awt.Cursor.getDefaultCursor());
                    //data=new Vector();
                    int counter=0;
                    while (rs.next()&&!isInterrupted()) {
                        synchronized (kassenzeichenChangedBlocker2){
                            //TODO: evtl um den MutiReload Bug (unsinnige Exceptions) zu beheben den synchronized Block ausweiten
                            int cc=rs.getMetaData().getColumnCount();
                            log.debug("Anzahl der Spalten:"+cc);
                            Object[] rowdata=new Object[cc];
                            for (int i=0; i<cc;++i) {
                                rowdata[i]=rs.getObject(i+1);
                            }
                            final Flaeche f=new Flaeche(reverseArtHm,reverseGradHm,artHm,gradHm,beschreibungHm,reverseBeschreibungHm);
                            
                            //DANGER
                            if (!mappingComponent.isReadOnly()) {
                                f.setEditable(true);
                            }
                            
                            
                            f.setContext(FlaechenUebersichtsTableModel.this);
                            f.setKassenzeichen(kz);
                            
                            f.getBezeichnungsModel().addDocumentListener(FlaechenUebersichtsTableModel.this);
                            f.getBemerkungsModel().addDocumentListener(FlaechenUebersichtsTableModel.this);
                            f.getGr_GrafikModel().addDocumentListener(FlaechenUebersichtsTableModel.this);
                            f.getVeranlagungsdatumModel().addDocumentListener(FlaechenUebersichtsTableModel.this);
                            f.getErfassungsdatumModel().addDocumentListener(FlaechenUebersichtsTableModel.this);
                            f.getGr_KorrekturModel().addDocumentListener(FlaechenUebersichtsTableModel.this);
                            f.getGr_KorrekturModel().addDocumentListener(FlaechenUebersichtsTableModel.this);
                            f.getAnteilModel().addDocumentListener(FlaechenUebersichtsTableModel.this);
                            f.getFeb_IdModel().addDocumentListener(FlaechenUebersichtsTableModel.this);
                            f.getSperrenModel().addActionListener(FlaechenUebersichtsTableModel.this);
                            f.getArtModel().addListDataListener(FlaechenUebersichtsTableModel.this);
                            f.getGradModel().addListDataListener(FlaechenUebersichtsTableModel.this);
                            f.getBeschreibungsmodel().addListDataListener(FlaechenUebersichtsTableModel.this);
                            f.fillFromObjectArray(rowdata);
                            f.setTeileigentumCrossReferences((Collection)crossReferences.get(f.getFl_id()));
                            f.backup();
                            
                            
                            data.add(f);
                            mappingComponent.getFeatureCollection().addFeature(f);
                            fireTableRowsInserted(counter,counter);
                            ++counter;
                            if (f.getBezeichnung().equals(Main.THIS.getKzPanel().getRequestForSelectionFlaeche())){
                                selectionRequest=f;
                            }
                            
                            
                            
                        }
                    }
                    if (!isInterrupted()) {
                        synchronized (kassenzeichenChangedBlocker3){
                            log.debug("Anzahl Flaechen:"+data.size());
                            setTmpNoSort(false);
                            fireTableDataChanged();
                            getValidFlaechenname(0);
                            if (!mappingComponent.isFixedMapExtent()) {
                                mappingComponent.zoomToFeatureCollection();
                            }
                            else {
                                log.debug("Kein Zoom auf die FeatureCollection weil die MappingComponent auf fixedExtent steht.");
                            }
                            Main.THIS.resetFixedMapExtent();
                            final Flaeche selectionRequestCopy=selectionRequest;
                            EventQueue.invokeLater(new Runnable() {

                                public void run() {
                                    mappingComponent.getFeatureCollection().select(selectionRequestCopy);
                                }
                            });
                        }
                    }
                    
                } catch ( Exception  e ) {
                    log.error("Fehler beim Auslesen der Flaecheninformationen (Uebersicht)!",e);
                }
                if (t!=null) {
                    t.setRepeats(false);
                    t.setDelay(1);
                }
            } };
            lookupThread.setPriority(Thread.NORM_PRIORITY);
            lookupThread.start();
    }
    
    
    public Flaeche addNewFlaeche(int art) {
        if (kassenzeichen!=null) {
            Flaeche f=new Flaeche(reverseArtHm,reverseGradHm,artHm,gradHm,beschreibungHm,reverseBeschreibungHm);
            f.setNewFlaeche(true);
            f.setKassenzeichen(kassenzeichen);
            f.setFl_id((fresh.size()+1)*(-1));
            f.setContext(this);
            f.setArt(art);
            f.setGrad(1);
            //Vorbelegung der Werte
            f.setBezeichnung(getValidFlaechenname(art));
            DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMANY);
            Calendar cal=Calendar.getInstance();
            f.setErfassungsdatum(df.format(cal.getTime()));
            cal.add(Calendar.MONTH,1);
            SimpleDateFormat vDat = new SimpleDateFormat("yy/MM");
            
            f.setVeranlagungsdatum(vDat.format(cal.getTime()));
            
            f.updateModels();
            
            f.getBezeichnungsModel().addDocumentListener(FlaechenUebersichtsTableModel.this);
            f.getBemerkungsModel().addDocumentListener(FlaechenUebersichtsTableModel.this);
            f.getGr_GrafikModel().addDocumentListener(FlaechenUebersichtsTableModel.this);
            f.getVeranlagungsdatumModel().addDocumentListener(FlaechenUebersichtsTableModel.this);
            f.getErfassungsdatumModel().addDocumentListener(FlaechenUebersichtsTableModel.this);
            f.getGr_KorrekturModel().addDocumentListener(FlaechenUebersichtsTableModel.this);
            f.getGr_KorrekturModel().addDocumentListener(FlaechenUebersichtsTableModel.this);
            f.getAnteilModel().addDocumentListener(FlaechenUebersichtsTableModel.this);
            f.getFeb_IdModel().addDocumentListener(FlaechenUebersichtsTableModel.this);
            f.getSperrenModel().addActionListener(FlaechenUebersichtsTableModel.this);
            f.getArtModel().addListDataListener(FlaechenUebersichtsTableModel.this);
            f.getGradModel().addListDataListener(FlaechenUebersichtsTableModel.this);
            f.backup();
            data.add(f);
            mappingComponent.getFeatureCollection().addFeature(f);
            fresh.add(f);
            mappingComponent.getFeatureCollection().select(f);
            //this.setSelectedFlaeche(f);
            this.fireTableDataChanged();
            return f;
        }
        return null;
        
    }
    
    public void removeSelectedFlaeche() {
        removeFlaeche(getSelectedFlaeche());
        
    }
    
    
    public void removeFlaeche(Flaeche f) {
        if (f!=null) {
            data.remove(f);
            mappingComponent.getFeatureCollection().removeFeature(f);
            deleted.add(f);
            f.setMarkedForDeletion(true);
            fireTableDataChanged();
        }
    }
    
    //Document Listener
    public void changedUpdate(DocumentEvent e) {
        fireTableDataChanged();
    }
    public void insertUpdate(DocumentEvent e) {
        fireTableDataChanged();
    }
    
    public void removeUpdate(DocumentEvent e) {
        fireTableDataChanged();
    }
    
    //ActionListener
    public void actionPerformed(ActionEvent e) {
        fireTableDataChanged();
    }
    
    //ListDataListener
    public void intervalRemoved(ListDataEvent e) {
        fireTableDataChanged();
    }
    
    public void intervalAdded(ListDataEvent e) {
        fireTableDataChanged();
    }
    
    public void contentsChanged(ListDataEvent e) {
        fireTableDataChanged();
    }
    
    
    //TableCellRenderer
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        boolean myIsSelected=false;
        boolean hasChanged=false;
        
        JLabel label=new JLabel();
        Flaeche thisFlaeche=null;
        try {
            int displayedIndex=row;
            int modelIndex=((JXTable)table).getFilters().convertRowIndexToModel(displayedIndex);
            thisFlaeche=getFlaechebyIndex(modelIndex);
            if (thisFlaeche.getBackup()==null) {
                log.warn("getBackup()==null "+thisFlaeche.getBezeichnung());
            }
        } catch (Exception e) {
            log.warn("Fehler in getFlaechebyIndex()",e);
        }
        
        if (getSelectedFlaeche()!=null) {
            if (thisFlaeche.getFl_id()==getSelectedFlaeche().getFl_id()) {
                myIsSelected=true;
            }
            if (thisFlaeche.hasChanged()) {
                hasChanged=true;
            } else {
                hasChanged=false;
            }
            label=(JLabel)defaultRenderer.getTableCellRendererComponent(table, value, myIsSelected, hasFocus, row, column);
        } else {
            label=(JLabel)defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
        
        if (thisFlaeche!=null&&thisFlaeche.isValid()==false) {
            label.setFont(new java.awt.Font("Microsoft Sans Serif", 1, 11));
        }
        if (hasChanged) {
            label.setForeground(Color.red);
        } else if (thisFlaeche!=null&&thisFlaeche.getGeometry()==null) {
            label.setForeground(Color.GRAY);
        } else {
            label.setForeground(Color.black);
        }
        if (column==0 || column==2) {
            label.setIcon((ImageIcon)value);
            label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            label.setText("");
        } else if ( column==1){
            label.setIcon(null);
            if (value==null) {
                label.setText("");
            } else {
                label.setText(value.toString());
            }
            label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        } else if ( column==3){
            label.setIcon(null);
            try {
                //Schneide das Komma ab
                //value= new Integer((int)(((Double)value).doubleValue()));
                value=(Integer)value;
            } catch (Exception e) {
                //nicht so schlimm
                log.debug("Komma weg ging schief",e);
            }
            if (value==null) {
                label.setText("");
            } else {
                label.setText(value.toString()+" ");
            }
            label.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        } else if ( column==4){
            label.setIcon(null);
            if (value==null) {
                label.setText("");
            } else {
                label.setText(value.toString());
            }
            label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        } else if ( column==5){
            label.setIcon(null);
            if (value==null) {
                label.setText("");
            } else {
                label.setText(value.toString());
            }
            label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        }
        return label;
    }
    //former synchronized
    Object hasChangedBlocker=new Object();
    public boolean hasChanged() {
        if (deleted.size()>0||fresh.size()>0) {
            return true;
        }
        
        synchronized (hasChangedBlocker) {
            Iterator it=data.iterator();
            while (it.hasNext()) {
                Flaeche f=(Flaeche)it.next();
                if (f.hasChanged()){
                    return true;
                }
            }
            return false;
        }
    }
}
