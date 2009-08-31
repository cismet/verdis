    /*
 * SummenTableModel.java
 *
 * Created on 7. Januar 2005, 10:32
 */

package de.cismet.verdis.gui;
import javax.swing.table.*;
import java.sql.*;
import java.util.*;
import java.awt.event.ActionEvent;
/**
 *
 * @author hell
 */





public class SummenTableModel extends DefaultTableModel{
    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private Thread lookupThread;
    private Connection connection;
    /** Creates a new instance of SummenTableModel */
    public SummenTableModel(Connection c) {
        super(0,2);
        connection=c;
    }
    
    public void kassenzeichenChanged(final String kz) {
        kassenzeichenChanged(kz,null);
    }
    
    private void emptyModel() {
        this.dataVector.removeAllElements();
        fireTableDataChanged();
        
    }
    public void kassenzeichenChanged(final String kz,final javax.swing.Timer t) {
        
        if (lookupThread!=null&&lookupThread.isAlive()) {
            lookupThread.interrupt();
        }
        emptyModel();
        
        lookupThread=new Thread() {
            public void run() {
                try {
                    Statement stmnt=connection.createStatement();
//                    select sub.bezeichner,sum(groesse) as Groesse,round(sum(groesseGewichtet)*10000)/10000 as GroesseGewichtet   from
//                    (
//                    select bezeichner, flaecheninfo.groesse_korrektur as Groesse, flaecheninfo.groesse_korrektur*veranlagungsgrundlage.veranlagungsschluessel as GroesseGewichtet
//                    from flaechen,flaeche,flaecheninfo ,veranlagungsgrundlage
//                    where
//                    anteil is null and
//                    flaechen.kassenzeichen_reference=6000467 and
//                    flaechen.flaeche=flaeche.id  and
//                    flaeche.flaecheninfo=flaecheninfo.id  and
//                    flaecheninfo.flaechenart=veranlagungsgrundlage.flaechenart  and
//                    flaecheninfo.anschlussgrad=veranlagungsgrundlage.anschlussgrad
//
//                    union
//
//                    select bezeichner, flaeche.anteil  as Groesse, flaeche.anteil *veranlagungsgrundlage.veranlagungsschluessel as GroesseGewichtet
//                    from flaechen,flaeche,flaecheninfo ,veranlagungsgrundlage
//                    where
//                    anteil is not null and
//                    flaechen.kassenzeichen_reference=6000467 and
//                    flaechen.flaeche=flaeche.id  and
//                    flaeche.flaecheninfo=flaecheninfo.id  and
//                    flaecheninfo.flaechenart=veranlagungsgrundlage.flaechenart  and
//                    flaecheninfo.anschlussgrad=veranlagungsgrundlage.anschlussgrad
//                    ) as sub
//                    group by bezeichner
//                    having bezeichner is not null
                    
                    String query=
                            "select sub.bezeichner,sum(groesse) as Groesse,round(sum(groesseGewichtet)*10000)/10000 as GroesseGewichtet   from "+
                            "( "+
                            "select flaeche.id,bezeichner, flaecheninfo.groesse_korrektur as Groesse, flaecheninfo.groesse_korrektur*veranlagungsgrundlage.veranlagungsschluessel as GroesseGewichtet "+
                            "from flaechen,flaeche,flaecheninfo ,veranlagungsgrundlage "+
                            "where   "+
                            "anteil is null and "+
                            "flaechen.kassenzeichen_reference="+kz+" and "+
                            "flaechen.flaeche=flaeche.id  and "+
                            "flaeche.flaecheninfo=flaecheninfo.id  and  "+
                            "flaecheninfo.flaechenart=veranlagungsgrundlage.flaechenart  and  "+
                            "flaecheninfo.anschlussgrad=veranlagungsgrundlage.anschlussgrad  "+
                            
                            "union "+
                            
                            "select  flaeche.id,bezeichner, flaeche.anteil  as Groesse, flaeche.anteil *veranlagungsgrundlage.veranlagungsschluessel as GroesseGewichtet "+
                            "from flaechen,flaeche,flaecheninfo ,veranlagungsgrundlage "+
                            "where   "+
                            "anteil is not null and "+
                            "flaechen.kassenzeichen_reference="+kz+" and "+
                            "flaechen.flaeche=flaeche.id  and "+
                            "flaeche.flaecheninfo=flaecheninfo.id  and  "+
                            "flaecheninfo.flaechenart=veranlagungsgrundlage.flaechenart  and  "+
                            "flaecheninfo.anschlussgrad=veranlagungsgrundlage.anschlussgrad  "+
                            ") as sub "+
                            "group by bezeichner "+
                            "having bezeichner is not null order by 1";
                    ResultSet rs=stmnt.executeQuery(query);
                    //this.setCursor(java.awt.Cursor.getDefaultCursor());
                    while (rs.next()) {
                        Vector row = new Vector(3);
                        row.addElement(rs.getString(1));
                        boolean is0=true;
                        try {
                            is0=new Double(rs.getString(3)).doubleValue()==0.0;
                        } catch(Exception x) {
                            log.warn("Wrong Format",x);
                        }

                        if (is0) {
                            String value=rs.getString(2);
                            try {
                                value=new Integer(((int)new Double(value.toString()).doubleValue())).toString();
                            } catch (Exception e){
                                log.warn("Fehler beim Abschneiden der Nachkommastellen",e);
                            }
                            row.addElement(value+ " m\u00B2"); //not multiplied with veranlagungsschluessel
                        } else {
                            String value=rs.getString(3);
                            try {
                                value=new Integer(((int)new Double(value.toString()).doubleValue())).toString();
                            } catch (Exception e){
                                log.warn("Fehler beim Abschneiden der Nachkommastellen",e);
                            }
                            row.addElement(value+ " m\u00B2"); //multiplied with veranlagungsschluessen
                        }
                        addRow(row);
                    }
                    fireTableDataChanged();
                } catch ( Exception  e ) {
                    log.error("Fehler beim Berechnen der Summen!",e);
                }
                if (t!=null) {
                    t.setRepeats(false);
                    t.setDelay(1);
                }
            } };
            lookupThread.setPriority(Thread.NORM_PRIORITY);
            lookupThread.start();
    }
    
    public boolean isCellEditable(int row,int column ) {
        return false;
    }
    
    
    
    
    
}
