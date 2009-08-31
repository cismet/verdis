/*
 * Kanalanschluss.java
 * Copyright (C) 2005 by:
 *
 *----------------------------
 * cismet GmbH
 * Goebenstrasse 40
 * 66117 Saarbruecken
 * http://www.cismet.de
 *----------------------------
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *----------------------------
 * Author:
 * thorsten.hell@cismet.de
 *----------------------------
 *
 * Created on 10. April 2006, 13:13
 *
 */

package de.cismet.verdis.data;

import de.cismet.tools.gui.dbwriter.SimpleDbAction;
import de.cismet.validation.NotValidException;
import de.cismet.verdis.gui.BefreiungenModel;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author thorsten.hell@cismet.de
 */
public class Kanalanschluss {
    public final static int JA=1;
    public final static int NEIN=2;
    public final static int UNKLAR=4;
    public final static int NOT_SET=-1;
    
    private long id=-1;
    private boolean rkVorhanden=false;
    private boolean mkrVorhanden=false;
    private boolean mksVorhanden=false;
    private boolean skVorhanden=false;
    private int rkAngeschlossen=NOT_SET;
    private int mkrAngeschlossen=NOT_SET;
    private int mksAngeschlossen=NOT_SET;
    private int skAngeschlossen=NOT_SET;
    private boolean sgVorhanden=false;
    private boolean kkaVorhanden=false;
    private boolean sgEntleerung=false;
    private boolean kkaEntleerung=false;
    private boolean evg=false;
    private Vector<BefreiungErlaubnis> befreiungen=new Vector<BefreiungErlaubnis>();
    
    
    private CheckBoxModel rkVorhandenModel;
    private CheckBoxModel mkrVorhandenModel;
    private CheckBoxModel mksVorhandenModel;
    private CheckBoxModel skVorhandenModel;
    private CheckBoxModel sgVorhandenModel;
    private CheckBoxModel kkaVorhandenModel;
    private CheckBoxModel sgEntleerungModel;
    private CheckBoxModel kkaEntleerungModel;
    private CheckBoxModel evgModel;
    
    private ComboBoxModel rkAngeschlossenModel;
    private ComboBoxModel mkrAngeschlossenModel;
    private ComboBoxModel mksAngeschlossenModel;
    private ComboBoxModel skAngeschlossenModel;
    
    private BefreiungenModel befreiungenModel;
    
    private Kanalanschluss backup;
    private int kassenzeichen=-1;
    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    /** Creates a new instance of Kanalanschluss */
    public Kanalanschluss() {
        initModels();
    }
    public void addStatements(Vector container) throws NotValidException{
        add2Container(container,getStatement4Kassenzeichen());
        add2Container(container,getStatement4Kanalanschluss());
        addStatements4BefreiungErlaubnis(container);
        
    }
    
    private void add2Container(Vector container,SimpleDbAction sdba){
        if (sdba!=null) {
            container.add(sdba);
        }
    }
    public void fillFromObjectArray(Object[] oa,Vector befreiungen) {
        this.befreiungen=befreiungen;
//id,rkvorhanden,mkrvorhanden,mksvorhanden,skvorhanden,rkangeschlossen,mkrangeschlossen,mksangeschlossen,skangeschlossen,sgvorhanden,kkavorhanden,sgentleerung,kkaentleerung,evg,befreiungenunderlaubnisse
        id=((Integer)oa[0]).intValue();
        rkVorhanden=oa[1].toString().trim().toLowerCase().equals("t");
        mkrVorhanden=oa[2].toString().trim().toLowerCase().equals("t");
        mksVorhanden=oa[3].toString().trim().toLowerCase().equals("t");
        skVorhanden=oa[4].toString().trim().toLowerCase().equals("t");
        rkAngeschlossen=((Integer)oa[5]).intValue();;
        mkrAngeschlossen=((Integer)oa[6]).intValue();;
        mksAngeschlossen=((Integer)oa[7]).intValue();;
        skAngeschlossen=((Integer)oa[8]).intValue();;
        sgVorhanden=oa[9].toString().trim().toLowerCase().equals("t");
        kkaVorhanden=oa[10].toString().trim().toLowerCase().equals("t");
        sgEntleerung=oa[11].toString().trim().toLowerCase().equals("t");
        kkaEntleerung=oa[12].toString().trim().toLowerCase().equals("t");
        evg=oa[13].toString().trim().toLowerCase().equals("t");
        updateModels();
    }
    
    public Object clone() throws CloneNotSupportedException {
        Kanalanschluss k=new Kanalanschluss();
        k.id=id;
        k.rkVorhanden=rkVorhanden;
        k.mkrVorhanden=mkrVorhanden;
        k.mksVorhanden=mksVorhanden;
        k.skVorhanden=skVorhanden;
        k.rkAngeschlossen=rkAngeschlossen;
        k.mkrAngeschlossen=mkrAngeschlossen;
        k.mksAngeschlossen=mksAngeschlossen;
        k.skAngeschlossen=skAngeschlossen;
        k.sgVorhanden=sgVorhanden;
        k.kkaVorhanden=kkaVorhanden;
        k.sgEntleerung=sgEntleerung;
        k.kkaEntleerung=kkaEntleerung;
        k.evg=evg;
        
        k.befreiungen=new Vector<BefreiungErlaubnis>();
        for (BefreiungErlaubnis elem : befreiungen) {
            BefreiungErlaubnis be=(BefreiungErlaubnis)elem.clone();
            k.befreiungen.add(be);
        }
        
        
        k.updateModels();
        return k;
    }
    
    public boolean equals(Object tester) {
        if (tester instanceof Kanalanschluss
                &&
                tester!=null) {
            Kanalanschluss k=(Kanalanschluss)tester;
            boolean befreiungenTester=true;
            if (befreiungen.size()==k.befreiungen.size()) {
                for (int i = 0; i < befreiungen.size(); i++) {
                    befreiungenTester=befreiungen.get(i).equals(k.befreiungen.get(i))&&befreiungenTester;
                }
            } else {
                befreiungenTester=false;
            }
            
            return (
                    befreiungenTester &&
                    k.id==id &&
                    k.rkVorhanden==rkVorhanden &&
                    k.mkrVorhanden==mkrVorhanden &&
                    k.mksVorhanden==mksVorhanden &&
                    k.skVorhanden==skVorhanden &&
                    k.rkAngeschlossen==rkAngeschlossen &&
                    k.mkrAngeschlossen==mkrAngeschlossen &&
                    k.mksAngeschlossen==mksAngeschlossen &&
                    k.skAngeschlossen==skAngeschlossen &&
                    k.sgVorhanden==sgVorhanden &&
                    k.kkaVorhanden==kkaVorhanden &&
                    k.sgEntleerung==sgEntleerung &&
                    k.kkaEntleerung==kkaEntleerung &&
                    k.evg==evg
                    );
        } else {
            return false;
        }
    }
    
    public void backup(){
        try {
            backup=(Kanalanschluss)clone();
        } catch (CloneNotSupportedException ex) {
            log.error("backup() ging schief",ex);
        }
    }
    
    public void setToBackup() {
        id=id;
        rkVorhanden=backup.rkVorhanden;
        mkrVorhanden=backup.mkrVorhanden;
        mksVorhanden=backup.mksVorhanden;
        skVorhanden=backup.skVorhanden;
        rkAngeschlossen=backup.rkAngeschlossen;
        mkrAngeschlossen=backup.mkrAngeschlossen;
        mksAngeschlossen=backup.mksAngeschlossen;
        skAngeschlossen=backup.skAngeschlossen;
        sgVorhanden=backup.sgVorhanden;
        kkaVorhanden=backup.kkaVorhanden;
        sgEntleerung=backup.sgEntleerung;
        kkaEntleerung=backup.kkaEntleerung;
        evg=backup.evg;
        updateModels();
    }
    
    public boolean hasChanged() {
        return !equals(backup);
    }
    
    public void updateModels() {
        rkVorhandenModel.setSelected(rkVorhanden);
        mkrVorhandenModel.setSelected(mkrVorhanden);
        mksVorhandenModel.setSelected(mksVorhanden);
        skVorhandenModel.setSelected(skVorhanden);
        sgVorhandenModel.setSelected(sgVorhanden);
        kkaVorhandenModel.setSelected(kkaVorhanden);
        sgEntleerungModel.setSelected(sgEntleerung);
        kkaEntleerungModel.setSelected(kkaEntleerung);
        evgModel.setSelected(evg);
        rkAngeschlossenModel.setSelectedIndex(rkAngeschlossen);
        mkrAngeschlossenModel.setSelectedIndex(mkrAngeschlossen);
        mksAngeschlossenModel.setSelectedIndex(mksAngeschlossen);
        skAngeschlossenModel.setSelectedIndex(skAngeschlossen);
        befreiungenModel=new BefreiungenModel(befreiungen);
    }
    
    
    
    
    public void initModels() {
        rkVorhandenModel=new CheckBoxModel(CheckBoxModel.RK_VORH);
        mkrVorhandenModel=new CheckBoxModel(CheckBoxModel.MKR_VORH);
        mksVorhandenModel=new CheckBoxModel(CheckBoxModel.MKS_VORH);
        skVorhandenModel=new CheckBoxModel(CheckBoxModel.SK_VORH);
        sgVorhandenModel=new CheckBoxModel(CheckBoxModel.SG_VORH);
        kkaVorhandenModel=new CheckBoxModel(CheckBoxModel.KKA_VORH);
        sgEntleerungModel=new CheckBoxModel(CheckBoxModel.SG_ENTL);
        kkaEntleerungModel=new CheckBoxModel(CheckBoxModel.KKA_ENTL);
        evgModel=new CheckBoxModel(CheckBoxModel.EVG);
        rkAngeschlossenModel=new ComboBoxModel(ComboBoxModel.RK_ANGESCHL);
        mkrAngeschlossenModel=new ComboBoxModel(ComboBoxModel.MKR_ANGESCHL);
        mksAngeschlossenModel=new ComboBoxModel(ComboBoxModel.MKS_ANGESCHL);
        skAngeschlossenModel=new ComboBoxModel(ComboBoxModel.SK_ANGESCHL);
        befreiungenModel=new BefreiungenModel();
    }
    
    
    
    
    
    
    class ComboBoxModel extends DefaultComboBoxModel {
        public final static int RK_ANGESCHL=1;
        public final static int MKR_ANGESCHL=2;
        public final static int MKS_ANGESCHL=4;
        public final static int SK_ANGESCHL=8;
        private int comboBoxIdentifier=-1;
        public ComboBoxModel(int which) {
            super(new String[]{"ja","nein","fraglich"});
            comboBoxIdentifier=which;
        }
        
        public void setSelectedIndex(int index) {
            setSelectedItem(super.getElementAt(index));
        }
        public void setSelectedItem(Object anObject) {
            super.setSelectedItem(anObject);
            int index=super.getIndexOf(anObject);
            switch (comboBoxIdentifier)  {
                case RK_ANGESCHL:
                    rkAngeschlossen=index;
                    break;
                case MKR_ANGESCHL:
                    mkrAngeschlossen=index;
                    break;
                case MKS_ANGESCHL:
                    mksAngeschlossen=index;
                    break;
                case SK_ANGESCHL:
                    skAngeschlossen=index;
            }
        }
        
        public Object getSelectedItem() {
            switch (comboBoxIdentifier)  {
                case RK_ANGESCHL:
                    super.setSelectedItem(super.getElementAt(rkAngeschlossen));
                    break;
                case MKR_ANGESCHL:
                    super.setSelectedItem(super.getElementAt(mkrAngeschlossen));
                    break;
                case MKS_ANGESCHL:
                    super.setSelectedItem(super.getElementAt(mksAngeschlossen));
                    break;
                case SK_ANGESCHL:
                    super.setSelectedItem(super.getElementAt(skAngeschlossen));
            }
            
            Object retValue;
            retValue = super.getSelectedItem();
            return retValue;
        }
        
    }
    class CheckBoxModel extends javax.swing.JToggleButton.ToggleButtonModel {
        public final static int RK_VORH=1;
        public final static int MKR_VORH=2;
        public final static int MKS_VORH=4;
        public final static int SK_VORH=8;
        public final static int SG_VORH=16;
        public final static int KKA_VORH=32;
        public final static int SG_ENTL=64;
        public final static int KKA_ENTL=128;
        public final static int EVG=256;
        private int checkBoxIdentifier=-1;
        public CheckBoxModel(int value) {
            checkBoxIdentifier=value;
        }
        
        public void setSelected(boolean b) {
            switch (checkBoxIdentifier) {
                case RK_VORH:
                    setRkVorhanden(b);
                    break;
                case MKR_VORH:
                    setMkrVorhanden(b);
                    break;
                case MKS_VORH:
                    setMksVorhanden(b);
                    break;
                case SK_VORH:
                    setSkVorhanden(b);
                    break;
                case SG_VORH:
                    setSgVorhanden(b);
                    break;
                case KKA_VORH:
                    setKkaVorhanden(b);
                    break;
                case SG_ENTL:
                    setSgEntleerung(b);
                    break;
                case KKA_ENTL:
                    setKkaEntleerung(b);
                    break;
                case EVG:
                    setEvg(b);
            }
        }
        public boolean isSelected() {
            switch (checkBoxIdentifier) {
                case RK_VORH:
                    return isRkVorhanden();
                case MKR_VORH:
                    return isMkrVorhanden();
                case MKS_VORH:
                    return isMksVorhanden();
                case SK_VORH:
                    return isSkVorhanden();
                case SG_VORH:
                    return isSgVorhanden();
                case KKA_VORH:
                    return isKkaVorhanden();
                case SG_ENTL:
                    return isSgEntleerung();
                case KKA_ENTL:
                    return isKkaEntleerung();
                case EVG:
                    return isEvg();
            }
            return false;
        }
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public boolean isRkVorhanden() {
        return rkVorhanden;
    }
    
    public void setRkVorhanden(boolean rkVorhanden) {
        this.rkVorhanden = rkVorhanden;
    }
    
    public boolean isMkrVorhanden() {
        return mkrVorhanden;
    }
    
    public void setMkrVorhanden(boolean mkrVorhanden) {
        this.mkrVorhanden = mkrVorhanden;
    }
    
    public boolean isMksVorhanden() {
        return mksVorhanden;
    }
    
    public void setMksVorhanden(boolean mksVorhanden) {
        this.mksVorhanden = mksVorhanden;
    }
    
    public boolean isSkVorhanden() {
        return skVorhanden;
    }
    
    public void setSkVorhanden(boolean skVorhanden) {
        this.skVorhanden = skVorhanden;
    }
    
    public int getRkAngeschlossen() {
        return rkAngeschlossen;
    }
    
    public void setRkAngeschlossen(int rkAngeschlossen) {
        this.rkAngeschlossen = rkAngeschlossen;
    }
    
    public int getMkrAngeschlossen() {
        return mkrAngeschlossen;
    }
    
    public void setMkrAngeschlossen(int mkrAngeschlossen) {
        this.mkrAngeschlossen = mkrAngeschlossen;
    }
    
    public int getMksAngeschlossen() {
        return mksAngeschlossen;
    }
    
    public void setMksAngeschlossen(int mksAngeschlossen) {
        this.mksAngeschlossen = mksAngeschlossen;
    }
    
    public int getSkAngeschlossen() {
        return skAngeschlossen;
    }
    
    public void setSkAngeschlossen(int skAngeschlossen) {
        this.skAngeschlossen = skAngeschlossen;
    }
    
    public boolean isSgVorhanden() {
        return sgVorhanden;
    }
    
    public void setSgVorhanden(boolean sgVorhanden) {
        this.sgVorhanden = sgVorhanden;
    }
    
    public boolean isKkaVorhanden() {
        return kkaVorhanden;
    }
    
    public void setKkaVorhanden(boolean kkaVorhanden) {
        this.kkaVorhanden = kkaVorhanden;
    }
    
    public boolean isSgEntleerung() {
        return sgEntleerung;
    }
    
    public void setSgEntleerung(boolean sgEntleerung) {
        this.sgEntleerung = sgEntleerung;
    }
    
    public boolean isKkaEntleerung() {
        return kkaEntleerung;
    }
    
    public void setKkaEntleerung(boolean kkaEntleerung) {
        this.kkaEntleerung = kkaEntleerung;
    }
    
    public boolean isEvg() {
        return evg;
    }
    
    public void setEvg(boolean evg) {
        this.evg = evg;
    }
    
    public CheckBoxModel getRkVorhandenModel() {
        return rkVorhandenModel;
    }
    
    public void setRkVorhandenModel(CheckBoxModel rkVorhandenModel) {
        this.rkVorhandenModel = rkVorhandenModel;
    }
    
    public CheckBoxModel getMkrVorhandenModel() {
        return mkrVorhandenModel;
    }
    
    public void setMkrVorhandenModel(CheckBoxModel mkrVorhandenModel) {
        this.mkrVorhandenModel = mkrVorhandenModel;
    }
    
    public CheckBoxModel getMksVorhandenModel() {
        return mksVorhandenModel;
    }
    
    public void setMksVorhandenModel(CheckBoxModel mksVorhandenModel) {
        this.mksVorhandenModel = mksVorhandenModel;
    }
    
    public CheckBoxModel getSkVorhandenModel() {
        return skVorhandenModel;
    }
    
    public void setSkVorhandenModel(CheckBoxModel skVorhandenModel) {
        this.skVorhandenModel = skVorhandenModel;
    }
    
    public CheckBoxModel getSgVorhandenModel() {
        return sgVorhandenModel;
    }
    
    public void setSgVorhandenModel(CheckBoxModel sgVorhandenModel) {
        this.sgVorhandenModel = sgVorhandenModel;
    }
    
    public CheckBoxModel getKkaVorhandenModel() {
        return kkaVorhandenModel;
    }
    
    public void setKkaVorhandenModel(CheckBoxModel kkaVorhandenModel) {
        this.kkaVorhandenModel = kkaVorhandenModel;
    }
    
    public CheckBoxModel getSgEntleerungModel() {
        return sgEntleerungModel;
    }
    
    public void setSgEntleerungModel(CheckBoxModel sgEntleerungModel) {
        this.sgEntleerungModel = sgEntleerungModel;
    }
    
    public CheckBoxModel getKkaEntleerungModel() {
        return kkaEntleerungModel;
    }
    
    public void setKkaEntleerungModel(CheckBoxModel kkaEntleerungModel) {
        this.kkaEntleerungModel = kkaEntleerungModel;
    }
    
    public CheckBoxModel getEvgModel() {
        return evgModel;
    }
    
    public void setEvgModel(CheckBoxModel evgModel) {
        this.evgModel = evgModel;
    }
    
    public ComboBoxModel getRkAngeschlossenModel() {
        return rkAngeschlossenModel;
    }
    
    public void setRkAngeschlossenModel(ComboBoxModel rkAngeschlossenModel) {
        this.rkAngeschlossenModel = rkAngeschlossenModel;
    }
    
    public ComboBoxModel getMkrAngeschlossenModel() {
        return mkrAngeschlossenModel;
    }
    
    public void setMkrAngeschlossenModel(ComboBoxModel mkrAngeschlossenModel) {
        this.mkrAngeschlossenModel = mkrAngeschlossenModel;
    }
    
    public ComboBoxModel getMksAngeschlossenModel() {
        return mksAngeschlossenModel;
    }
    
    public void setMksAngeschlossenModel(ComboBoxModel mksAngeschlossenModel) {
        this.mksAngeschlossenModel = mksAngeschlossenModel;
    }
    
    public ComboBoxModel getSkAngeschlossenModel() {
        return skAngeschlossenModel;
    }
    
    public void setSkAngeschlossenModel(ComboBoxModel skAngeschlossenModel) {
        this.skAngeschlossenModel = skAngeschlossenModel;
    }
    
    private String convertBoolean(boolean b) {
        String s="'F'";
        if (b) s="'T'";
        return s;
    }
    private SimpleDbAction getStatement4Kassenzeichen() throws NotValidException{
        SimpleDbAction sdba=new SimpleDbAction();
        if (id==-1) {
            //Es gibt noch kein Eintrag
            sdba.setStatement("update kassenzeichen set "+
                    "kanalanschluss=nextval('kanalanschluss_seq') "+
                    "where id="+kassenzeichen
                    );
            sdba.setDescription("Update von >>Kassenzeichen<<");
            sdba.setType(SimpleDbAction.UPDATE);
            return sdba;
        }
        
        return null;
    }
    
    public boolean isValid()  {
        
        return  !(
                rkVorhanden&&rkAngeschlossen==-1 ||
                mkrVorhanden&&mkrAngeschlossen==-1 ||
                mksVorhanden&&mksAngeschlossen==-1 ||
                skVorhanden&&skAngeschlossen==-1||
                
                !rkVorhanden&&rkAngeschlossen!=-1 ||
                !mkrVorhanden&&mkrAngeschlossen!=-1 ||
                !mksVorhanden&&mksAngeschlossen!=-1 ||
                !skVorhanden&&skAngeschlossen!=-1||
                
                !sgVorhanden&&sgEntleerung ||
                !kkaVorhanden&&kkaEntleerung
                );
    }
    
    private SimpleDbAction getStatement4Kanalanschluss() throws NotValidException{
        if (!isValid()){
            throw new NotValidException();
        } else {
            
            SimpleDbAction sdba=new SimpleDbAction();
            if (id>0) {
                sdba.setStatement("UPDATE kanalanschluss SET " +
                        "rkvorhanden="+convertBoolean(rkVorhanden)+" , " +
                        "mkrvorhanden="+convertBoolean(mkrVorhanden)+" , " +
                        "mksvorhanden="+convertBoolean(mksVorhanden)+" , " +
                        "skvorhanden="+convertBoolean(skVorhanden)+" , " +
                        "rkangeschlossen="+rkAngeschlossen+" , " +
                        "mkrangeschlossen="+mkrAngeschlossen+" , " +
                        "mksangeschlossen="+mksAngeschlossen+" , " +
                        "skangeschlossen="+skAngeschlossen+" , " +
                        "sgvorhanden="+convertBoolean(sgVorhanden)+" , " +
                        "kkavorhanden="+convertBoolean(kkaVorhanden)+" , " +
                        "sgentleerung="+convertBoolean(sgEntleerung)+" , " +
                        "kkaentleerung="+convertBoolean(kkaEntleerung)+" , " +
                        "evg="+convertBoolean(evg)+" , " +
                        "befreiungenunderlaubnisse="+id +" "+
                        "where id="+id);
                sdba.setDescription("Update von >>Kanalanschluss<<");
                sdba.setType(SimpleDbAction.UPDATE);
                return sdba;
            } else if (id==-1) {
                sdba.setStatement("insert into kanalanschluss (id, rkvorhanden, mkrvorhanden, mksvorhanden, skvorhanden, rkangeschlossen, mkrangeschlossen, mksangeschlossen, skangeschlossen, sgvorhanden, kkavorhanden, sgentleerung, kkaentleerung, evg, befreiungenunderlaubnisse) values (" +
                        "currval('kanalanschluss_seq') ," +
                        convertBoolean(rkVorhanden)+" , " +
                        convertBoolean(mkrVorhanden)+" , " +
                        convertBoolean(mksVorhanden)+" , " +
                        convertBoolean(skVorhanden)+" , " +
                        rkAngeschlossen+" , " +
                        mkrAngeschlossen+" , " +
                        mksAngeschlossen+" , " +
                        skAngeschlossen+" , " +
                        convertBoolean(sgVorhanden)+" , " +
                        convertBoolean(kkaVorhanden)+" , " +
                        convertBoolean(sgEntleerung)+" , " +
                        convertBoolean(kkaEntleerung)+" , " +
                        convertBoolean(evg)+" , " +
                        "currval('kanalanschluss_seq') " +
                        ")");
                
                sdba.setDescription("Insert von >>Kanalanschluss<<");
                sdba.setType(SimpleDbAction.INSERT);
                return sdba;
            }
            return null;
        }
    }
    //Collect notwendig
    private void addStatements4BefreiungErlaubnis(Vector v) throws NotValidException{
        SimpleDbAction sdba=new SimpleDbAction();
        for (BefreiungErlaubnis elem : befreiungen) {
            if (elem.getId()==-1&&elem.getAktenzeichen().trim().length()>0) {
                sdba=new SimpleDbAction();
                String kanalanschlussPart="currval('kanalanschluss_seq')";
                if (id>0) {
                    kanalanschlussPart=""+id;
                }
                sdba.setStatement("insert into befreiungerlaubnisarray  (id,befreiungerlaubnis,kanalanschluss_reference) values ("+
                        "nextval('befreiungerlaubnisarray_seq'),nextval('befreiungerlaubnis_seq'),"+kanalanschlussPart+")");
                sdba.setDescription("Insert in >>Befreiungerlaubnisarray<<");
                sdba.setType(SimpleDbAction.INSERT);
                v.add(sdba);
                sdba=new SimpleDbAction();
                sdba.setStatement("insert into befreiungerlaubnis (id,aktenzeichen,gueltig_bis) values (" +
                        "currval('befreiungerlaubnis_seq'), '"+elem.getAktenzeichen()+"','"+elem.getGueltigBis()+"')");
                sdba.setDescription("Insert in >>Befreiungerlaubnis<<");
                sdba.setType(SimpleDbAction.INSERT);
                v.add(sdba);
            } else if (elem.getId()>0&&elem.getAktenzeichen().trim().length()==0&&elem.getGueltigBis().trim().length()==0){
                sdba=new SimpleDbAction();
                String kanalanschlussPart="currval('kanalanschluss_seq')";
                if (id>0) {
                    kanalanschlussPart=""+id;
                }
                sdba.setStatement("delete from befreiungerlaubnis where id="+elem.getId());
                sdba.setDescription("Delete in >>Befreiungerlaubnis<<");
                sdba.setType(SimpleDbAction.DELETE);
                v.add(sdba);
                sdba=new SimpleDbAction();
                sdba.setStatement("delete from befreiungerlaubnisarray where befreiungerlaubnis="+elem.getId());
                sdba.setDescription("Delete in >>Befreiungerlaubnisarray<<");
                sdba.setType(SimpleDbAction.DELETE);
                v.add(sdba);
                
            } else if (elem.getId()>0){
                sdba=new SimpleDbAction();
                sdba.setStatement("update befreiungerlaubnis  set " +
                        "aktenzeichen='" +elem.getAktenzeichen()+"'"+
                        ",gueltig_bis='"+elem.getGueltigBis()+"'"+
                        "where id="+elem.getId());
                sdba.setDescription("UPDATE in >>Befreiungerlaubnis<<");
                sdba.setType(SimpleDbAction.UPDATE);
                v.add(sdba);
                
            }
        }
    }
    
    public BefreiungenModel getBefreiungenModel() {
        return befreiungenModel;
    }
    
    public void setBefreiungenModel(BefreiungenModel befreiungenModel) {
        this.befreiungenModel = befreiungenModel;
    }
    
    public int getKassenzeichen() {
        return kassenzeichen;
    }
    
    public void setKassenzeichen(int kassenzeichen) {
        this.kassenzeichen = kassenzeichen;
    }
    
    public Vector<BefreiungErlaubnis> getBefreiungen() {
        return befreiungen;
    }
    
    public void setBefreiungen(Vector<BefreiungErlaubnis> befreiungen) {
        this.befreiungen = befreiungen;
    }
    
}

