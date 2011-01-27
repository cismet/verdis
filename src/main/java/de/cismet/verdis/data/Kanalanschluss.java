/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis.data;

import java.util.Vector;

import javax.swing.DefaultComboBoxModel;

import de.cismet.tools.gui.dbwriter.SimpleDbAction;

import de.cismet.validation.NotValidException;

import de.cismet.verdis.gui.BefreiungenModel;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten.hell@cismet.de
 * @version  $Revision$, $Date$
 */
public class Kanalanschluss {

    //~ Static fields/initializers ---------------------------------------------

    public static final int JA = 1;
    public static final int NEIN = 2;
    public static final int UNKLAR = 4;
    public static final int NOT_SET = -1;

    //~ Instance fields --------------------------------------------------------

    private long id = -1;
    private boolean rkVorhanden = false;
    private boolean mkrVorhanden = false;
    private boolean mksVorhanden = false;
    private boolean skVorhanden = false;
    private int rkAngeschlossen = NOT_SET;
    private int mkrAngeschlossen = NOT_SET;
    private int mksAngeschlossen = NOT_SET;
    private int skAngeschlossen = NOT_SET;
    private boolean sgVorhanden = false;
    private boolean kkaVorhanden = false;
    private boolean sgEntleerung = false;
    private boolean kkaEntleerung = false;
    private boolean evg = false;
    private Vector<BefreiungErlaubnis> befreiungen = new Vector<BefreiungErlaubnis>();
    private Vector<BefreiungErlaubnis> befreiungenToDelete = new Vector<BefreiungErlaubnis>();

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
    private int kassenzeichen = -1;
    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of Kanalanschluss.
     */
    public Kanalanschluss() {
        initModels();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   container  DOCUMENT ME!
     *
     * @throws  NotValidException  DOCUMENT ME!
     */
    public void addStatements(final Vector container) throws NotValidException {
        add2Container(container, getStatement4Kassenzeichen());
        add2Container(container, getStatement4Kanalanschluss());
        addStatements4BefreiungErlaubnis(container);
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
     * @param  oa           DOCUMENT ME!
     * @param  befreiungen  DOCUMENT ME!
     */
    public void fillFromObjectArray(final Object[] oa, final Vector befreiungen) {
        this.befreiungen = befreiungen;
//id,rkvorhanden,mkrvorhanden,mksvorhanden,skvorhanden,rkangeschlossen,mkrangeschlossen,mksangeschlossen,skangeschlossen,sgvorhanden,kkavorhanden,sgentleerung,kkaentleerung,evg,befreiungenunderlaubnisse
        id = ((Integer)oa[0]).intValue();
        rkVorhanden = oa[1].toString().trim().toLowerCase().equals("t");
        mkrVorhanden = oa[2].toString().trim().toLowerCase().equals("t");
        mksVorhanden = oa[3].toString().trim().toLowerCase().equals("t");
        skVorhanden = oa[4].toString().trim().toLowerCase().equals("t");
        rkAngeschlossen = ((Integer)oa[5]).intValue();
        ;
        mkrAngeschlossen = ((Integer)oa[6]).intValue();
        ;
        mksAngeschlossen = ((Integer)oa[7]).intValue();
        ;
        skAngeschlossen = ((Integer)oa[8]).intValue();
        ;
        sgVorhanden = oa[9].toString().trim().toLowerCase().equals("t");
        kkaVorhanden = oa[10].toString().trim().toLowerCase().equals("t");
        sgEntleerung = oa[11].toString().trim().toLowerCase().equals("t");
        kkaEntleerung = oa[12].toString().trim().toLowerCase().equals("t");
        evg = oa[13].toString().trim().toLowerCase().equals("t");
        updateModels();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        final Kanalanschluss k = new Kanalanschluss();
        k.id = id;
        k.rkVorhanden = rkVorhanden;
        k.mkrVorhanden = mkrVorhanden;
        k.mksVorhanden = mksVorhanden;
        k.skVorhanden = skVorhanden;
        k.rkAngeschlossen = rkAngeschlossen;
        k.mkrAngeschlossen = mkrAngeschlossen;
        k.mksAngeschlossen = mksAngeschlossen;
        k.skAngeschlossen = skAngeschlossen;
        k.sgVorhanden = sgVorhanden;
        k.kkaVorhanden = kkaVorhanden;
        k.sgEntleerung = sgEntleerung;
        k.kkaEntleerung = kkaEntleerung;
        k.evg = evg;

        k.befreiungen = new Vector<BefreiungErlaubnis>();
        for (final BefreiungErlaubnis elem : befreiungen) {
            final BefreiungErlaubnis be = (BefreiungErlaubnis)elem.clone();
            k.befreiungen.add(be);
        }

        k.updateModels();
        return k;
    }

    @Override
    public boolean equals(final Object tester) {
        if ((tester instanceof Kanalanschluss)
                    && (tester != null)) {
            final Kanalanschluss k = (Kanalanschluss)tester;
            boolean befreiungenTester = true;
            if (befreiungen.size() == k.befreiungen.size()) {
                for (int i = 0; i < befreiungen.size(); i++) {
                    befreiungenTester = befreiungen.get(i).equals(k.befreiungen.get(i)) && befreiungenTester;
                }
            } else {
                befreiungenTester = false;
            }

            return (befreiungenTester
                            && (k.id == id)
                            && (k.rkVorhanden == rkVorhanden)
                            && (k.mkrVorhanden == mkrVorhanden)
                            && (k.mksVorhanden == mksVorhanden)
                            && (k.skVorhanden == skVorhanden)
                            && (k.rkAngeschlossen == rkAngeschlossen)
                            && (k.mkrAngeschlossen == mkrAngeschlossen)
                            && (k.mksAngeschlossen == mksAngeschlossen)
                            && (k.skAngeschlossen == skAngeschlossen)
                            && (k.sgVorhanden == sgVorhanden)
                            && (k.kkaVorhanden == kkaVorhanden)
                            && (k.sgEntleerung == sgEntleerung)
                            && (k.kkaEntleerung == kkaEntleerung)
                            && (k.evg == evg));
        } else {
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void backup() {
        try {
            backup = (Kanalanschluss)clone();
        } catch (CloneNotSupportedException ex) {
            log.error("backup() ging schief", ex);
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void setToBackup() {
        id = id;
        rkVorhanden = backup.rkVorhanden;
        mkrVorhanden = backup.mkrVorhanden;
        mksVorhanden = backup.mksVorhanden;
        skVorhanden = backup.skVorhanden;
        rkAngeschlossen = backup.rkAngeschlossen;
        mkrAngeschlossen = backup.mkrAngeschlossen;
        mksAngeschlossen = backup.mksAngeschlossen;
        skAngeschlossen = backup.skAngeschlossen;
        sgVorhanden = backup.sgVorhanden;
        kkaVorhanden = backup.kkaVorhanden;
        sgEntleerung = backup.sgEntleerung;
        kkaEntleerung = backup.kkaEntleerung;
        evg = backup.evg;
        updateModels();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean hasChanged() {
        return !equals(backup);
    }

    /**
     * DOCUMENT ME!
     */
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
        befreiungenModel = new BefreiungenModel(befreiungen);
    }

    /**
     * DOCUMENT ME!
     */
    public void initModels() {
        rkVorhandenModel = new CheckBoxModel(CheckBoxModel.RK_VORH);
        mkrVorhandenModel = new CheckBoxModel(CheckBoxModel.MKR_VORH);
        mksVorhandenModel = new CheckBoxModel(CheckBoxModel.MKS_VORH);
        skVorhandenModel = new CheckBoxModel(CheckBoxModel.SK_VORH);
        sgVorhandenModel = new CheckBoxModel(CheckBoxModel.SG_VORH);
        kkaVorhandenModel = new CheckBoxModel(CheckBoxModel.KKA_VORH);
        sgEntleerungModel = new CheckBoxModel(CheckBoxModel.SG_ENTL);
        kkaEntleerungModel = new CheckBoxModel(CheckBoxModel.KKA_ENTL);
        evgModel = new CheckBoxModel(CheckBoxModel.EVG);
        rkAngeschlossenModel = new ComboBoxModel(ComboBoxModel.RK_ANGESCHL);
        mkrAngeschlossenModel = new ComboBoxModel(ComboBoxModel.MKR_ANGESCHL);
        mksAngeschlossenModel = new ComboBoxModel(ComboBoxModel.MKS_ANGESCHL);
        skAngeschlossenModel = new ComboBoxModel(ComboBoxModel.SK_ANGESCHL);
        befreiungenModel = new BefreiungenModel();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public long getId() {
        return id;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  id  DOCUMENT ME!
     */
    public void setId(final long id) {
        this.id = id;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isRkVorhanden() {
        return rkVorhanden;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  rkVorhanden  DOCUMENT ME!
     */
    public void setRkVorhanden(final boolean rkVorhanden) {
        this.rkVorhanden = rkVorhanden;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isMkrVorhanden() {
        return mkrVorhanden;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  mkrVorhanden  DOCUMENT ME!
     */
    public void setMkrVorhanden(final boolean mkrVorhanden) {
        this.mkrVorhanden = mkrVorhanden;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isMksVorhanden() {
        return mksVorhanden;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  mksVorhanden  DOCUMENT ME!
     */
    public void setMksVorhanden(final boolean mksVorhanden) {
        this.mksVorhanden = mksVorhanden;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isSkVorhanden() {
        return skVorhanden;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  skVorhanden  DOCUMENT ME!
     */
    public void setSkVorhanden(final boolean skVorhanden) {
        this.skVorhanden = skVorhanden;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getRkAngeschlossen() {
        return rkAngeschlossen;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  rkAngeschlossen  DOCUMENT ME!
     */
    public void setRkAngeschlossen(final int rkAngeschlossen) {
        this.rkAngeschlossen = rkAngeschlossen;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getMkrAngeschlossen() {
        return mkrAngeschlossen;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  mkrAngeschlossen  DOCUMENT ME!
     */
    public void setMkrAngeschlossen(final int mkrAngeschlossen) {
        this.mkrAngeschlossen = mkrAngeschlossen;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getMksAngeschlossen() {
        return mksAngeschlossen;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  mksAngeschlossen  DOCUMENT ME!
     */
    public void setMksAngeschlossen(final int mksAngeschlossen) {
        this.mksAngeschlossen = mksAngeschlossen;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getSkAngeschlossen() {
        return skAngeschlossen;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  skAngeschlossen  DOCUMENT ME!
     */
    public void setSkAngeschlossen(final int skAngeschlossen) {
        this.skAngeschlossen = skAngeschlossen;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isSgVorhanden() {
        return sgVorhanden;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  sgVorhanden  DOCUMENT ME!
     */
    public void setSgVorhanden(final boolean sgVorhanden) {
        this.sgVorhanden = sgVorhanden;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isKkaVorhanden() {
        return kkaVorhanden;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kkaVorhanden  DOCUMENT ME!
     */
    public void setKkaVorhanden(final boolean kkaVorhanden) {
        this.kkaVorhanden = kkaVorhanden;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isSgEntleerung() {
        return sgEntleerung;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  sgEntleerung  DOCUMENT ME!
     */
    public void setSgEntleerung(final boolean sgEntleerung) {
        this.sgEntleerung = sgEntleerung;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isKkaEntleerung() {
        return kkaEntleerung;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kkaEntleerung  DOCUMENT ME!
     */
    public void setKkaEntleerung(final boolean kkaEntleerung) {
        this.kkaEntleerung = kkaEntleerung;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isEvg() {
        return evg;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evg  DOCUMENT ME!
     */
    public void setEvg(final boolean evg) {
        this.evg = evg;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CheckBoxModel getRkVorhandenModel() {
        return rkVorhandenModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  rkVorhandenModel  DOCUMENT ME!
     */
    public void setRkVorhandenModel(final CheckBoxModel rkVorhandenModel) {
        this.rkVorhandenModel = rkVorhandenModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CheckBoxModel getMkrVorhandenModel() {
        return mkrVorhandenModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  mkrVorhandenModel  DOCUMENT ME!
     */
    public void setMkrVorhandenModel(final CheckBoxModel mkrVorhandenModel) {
        this.mkrVorhandenModel = mkrVorhandenModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CheckBoxModel getMksVorhandenModel() {
        return mksVorhandenModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  mksVorhandenModel  DOCUMENT ME!
     */
    public void setMksVorhandenModel(final CheckBoxModel mksVorhandenModel) {
        this.mksVorhandenModel = mksVorhandenModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CheckBoxModel getSkVorhandenModel() {
        return skVorhandenModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  skVorhandenModel  DOCUMENT ME!
     */
    public void setSkVorhandenModel(final CheckBoxModel skVorhandenModel) {
        this.skVorhandenModel = skVorhandenModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CheckBoxModel getSgVorhandenModel() {
        return sgVorhandenModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  sgVorhandenModel  DOCUMENT ME!
     */
    public void setSgVorhandenModel(final CheckBoxModel sgVorhandenModel) {
        this.sgVorhandenModel = sgVorhandenModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CheckBoxModel getKkaVorhandenModel() {
        return kkaVorhandenModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kkaVorhandenModel  DOCUMENT ME!
     */
    public void setKkaVorhandenModel(final CheckBoxModel kkaVorhandenModel) {
        this.kkaVorhandenModel = kkaVorhandenModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CheckBoxModel getSgEntleerungModel() {
        return sgEntleerungModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  sgEntleerungModel  DOCUMENT ME!
     */
    public void setSgEntleerungModel(final CheckBoxModel sgEntleerungModel) {
        this.sgEntleerungModel = sgEntleerungModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CheckBoxModel getKkaEntleerungModel() {
        return kkaEntleerungModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kkaEntleerungModel  DOCUMENT ME!
     */
    public void setKkaEntleerungModel(final CheckBoxModel kkaEntleerungModel) {
        this.kkaEntleerungModel = kkaEntleerungModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CheckBoxModel getEvgModel() {
        return evgModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evgModel  DOCUMENT ME!
     */
    public void setEvgModel(final CheckBoxModel evgModel) {
        this.evgModel = evgModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ComboBoxModel getRkAngeschlossenModel() {
        return rkAngeschlossenModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  rkAngeschlossenModel  DOCUMENT ME!
     */
    public void setRkAngeschlossenModel(final ComboBoxModel rkAngeschlossenModel) {
        this.rkAngeschlossenModel = rkAngeschlossenModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ComboBoxModel getMkrAngeschlossenModel() {
        return mkrAngeschlossenModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  mkrAngeschlossenModel  DOCUMENT ME!
     */
    public void setMkrAngeschlossenModel(final ComboBoxModel mkrAngeschlossenModel) {
        this.mkrAngeschlossenModel = mkrAngeschlossenModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ComboBoxModel getMksAngeschlossenModel() {
        return mksAngeschlossenModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  mksAngeschlossenModel  DOCUMENT ME!
     */
    public void setMksAngeschlossenModel(final ComboBoxModel mksAngeschlossenModel) {
        this.mksAngeschlossenModel = mksAngeschlossenModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ComboBoxModel getSkAngeschlossenModel() {
        return skAngeschlossenModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  skAngeschlossenModel  DOCUMENT ME!
     */
    public void setSkAngeschlossenModel(final ComboBoxModel skAngeschlossenModel) {
        this.skAngeschlossenModel = skAngeschlossenModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   b  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String convertBoolean(final boolean b) {
        String s = "'F'";
        if (b) {
            s = "'T'";
        }
        return s;
    }
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  NotValidException  DOCUMENT ME!
     */
    private SimpleDbAction getStatement4Kassenzeichen() throws NotValidException {
        final SimpleDbAction sdba = new SimpleDbAction();
        if (id == -1) {
            // Es gibt noch kein Eintrag
            sdba.setStatement("update kassenzeichen set "
                        + "kanalanschluss=nextval('kanalanschluss_seq') "
                        + "where id=" + kassenzeichen);
            sdba.setDescription("Update von >>Kassenzeichen<<");
            sdba.setType(SimpleDbAction.UPDATE);
            return sdba;
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isValid() {
        return !((rkVorhanden && (rkAngeschlossen == -1))
                        || (mkrVorhanden && (mkrAngeschlossen == -1))
                        || (mksVorhanden && (mksAngeschlossen == -1))
                        || (skVorhanden && (skAngeschlossen == -1))
                        || (!rkVorhanden && (rkAngeschlossen != -1))
                        || (!mkrVorhanden && (mkrAngeschlossen != -1))
                        || (!mksVorhanden && (mksAngeschlossen != -1))
                        || (!skVorhanden && (skAngeschlossen != -1))
                        || (!sgVorhanden && sgEntleerung)
                        || (!kkaVorhanden && kkaEntleerung));
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  NotValidException  DOCUMENT ME!
     */
    private SimpleDbAction getStatement4Kanalanschluss() throws NotValidException {
        if (!isValid()) {
            throw new NotValidException();
        } else {
            final SimpleDbAction sdba = new SimpleDbAction();
            if (id > 0) {
                sdba.setStatement("UPDATE kanalanschluss SET "
                            + "rkvorhanden=" + convertBoolean(rkVorhanden) + " , "
                            + "mkrvorhanden=" + convertBoolean(mkrVorhanden) + " , "
                            + "mksvorhanden=" + convertBoolean(mksVorhanden) + " , "
                            + "skvorhanden=" + convertBoolean(skVorhanden) + " , "
                            + "rkangeschlossen=" + rkAngeschlossen + " , "
                            + "mkrangeschlossen=" + mkrAngeschlossen + " , "
                            + "mksangeschlossen=" + mksAngeschlossen + " , "
                            + "skangeschlossen=" + skAngeschlossen + " , "
                            + "sgvorhanden=" + convertBoolean(sgVorhanden) + " , "
                            + "kkavorhanden=" + convertBoolean(kkaVorhanden) + " , "
                            + "sgentleerung=" + convertBoolean(sgEntleerung) + " , "
                            + "kkaentleerung=" + convertBoolean(kkaEntleerung) + " , "
                            + "evg=" + convertBoolean(evg) + " , "
                            + "befreiungenunderlaubnisse=" + id + " "
                            + "where id=" + id);
                sdba.setDescription("Update von >>Kanalanschluss<<");
                sdba.setType(SimpleDbAction.UPDATE);
                return sdba;
            } else if (id == -1) {
                sdba.setStatement(
                    "insert into kanalanschluss (id, rkvorhanden, mkrvorhanden, mksvorhanden, skvorhanden, rkangeschlossen, mkrangeschlossen, mksangeschlossen, skangeschlossen, sgvorhanden, kkavorhanden, sgentleerung, kkaentleerung, evg, befreiungenunderlaubnisse) values ("
                            + "currval('kanalanschluss_seq') ,"
                            + convertBoolean(rkVorhanden)
                            + " , "
                            + convertBoolean(mkrVorhanden)
                            + " , "
                            + convertBoolean(mksVorhanden)
                            + " , "
                            + convertBoolean(skVorhanden)
                            + " , "
                            + rkAngeschlossen
                            + " , "
                            + mkrAngeschlossen
                            + " , "
                            + mksAngeschlossen
                            + " , "
                            + skAngeschlossen
                            + " , "
                            + convertBoolean(sgVorhanden)
                            + " , "
                            + convertBoolean(kkaVorhanden)
                            + " , "
                            + convertBoolean(sgEntleerung)
                            + " , "
                            + convertBoolean(kkaEntleerung)
                            + " , "
                            + convertBoolean(evg)
                            + " , "
                            + "currval('kanalanschluss_seq') "
                            + ")");

                sdba.setDescription("Insert von >>Kanalanschluss<<");
                sdba.setType(SimpleDbAction.INSERT);
                return sdba;
            }
            return null;
        }
    }
    /**
     * Collect notwendig.
     *
     * @param   v  DOCUMENT ME!
     *
     * @throws  NotValidException  DOCUMENT ME!
     */
    private void addStatements4BefreiungErlaubnis(final Vector v) throws NotValidException {
        SimpleDbAction sdba = new SimpleDbAction();
        for (final BefreiungErlaubnis elem : befreiungen) {
            if ((elem.getId() == -1) && (elem.getAktenzeichen().trim().length() > 0)) {
                sdba = new SimpleDbAction();
                String kanalanschlussPart = "currval('kanalanschluss_seq')";
                if (id > 0) {
                    kanalanschlussPart = "" + id;
                }
                sdba.setStatement(
                    "insert into befreiungerlaubnisarray  (id,befreiungerlaubnis,kanalanschluss_reference) values ("
                            + "nextval('befreiungerlaubnisarray_seq'),nextval('befreiungerlaubnis_seq'),"
                            + kanalanschlussPart
                            + ")");
                sdba.setDescription("Insert in >>Befreiungerlaubnisarray<<");
                sdba.setType(SimpleDbAction.INSERT);
                v.add(sdba);
                sdba = new SimpleDbAction();
                sdba.setStatement("insert into befreiungerlaubnis (id,aktenzeichen,gueltig_bis) values ("
                            + "currval('befreiungerlaubnis_seq'), '" + elem.getAktenzeichen() + "','"
                            + elem.getGueltigBis() + "')");
                sdba.setDescription("Insert in >>Befreiungerlaubnis<<");
                sdba.setType(SimpleDbAction.INSERT);
                v.add(sdba);
            } else if ((elem.getId() > 0) && (elem.getAktenzeichen().trim().length() == 0)
                        && (elem.getGueltigBis().trim().length() == 0)) {
                sdba = new SimpleDbAction();
                String kanalanschlussPart = "currval('kanalanschluss_seq')";
                if (id > 0) {
                    kanalanschlussPart = "" + id;
                }
                sdba.setStatement("delete from befreiungerlaubnis where id=" + elem.getId());
                sdba.setDescription("Delete in >>Befreiungerlaubnis<<");
                sdba.setType(SimpleDbAction.DELETE);
                v.add(sdba);
                sdba = new SimpleDbAction();
                sdba.setStatement("delete from befreiungerlaubnisarray where befreiungerlaubnis=" + elem.getId());
                sdba.setDescription("Delete in >>Befreiungerlaubnisarray<<");
                sdba.setType(SimpleDbAction.DELETE);
                v.add(sdba);
            } else if (elem.getId() > 0) {
                sdba = new SimpleDbAction();
                sdba.setStatement("update befreiungerlaubnis  set "
                            + "aktenzeichen='" + elem.getAktenzeichen() + "'"
                            + ",gueltig_bis='" + elem.getGueltigBis() + "'"
                            + "where id=" + elem.getId());
                sdba.setDescription("UPDATE in >>Befreiungerlaubnis<<");
                sdba.setType(SimpleDbAction.UPDATE);
                v.add(sdba);
            }
        }
        for (final BefreiungErlaubnis elem : befreiungenToDelete) {
            if (elem.getId() > 0) {
                sdba = new SimpleDbAction();
                sdba.setStatement("delete from befreiungerlaubnis where id=" + elem.getId());
                sdba.setDescription("Delete in >>Befreiungerlaubnis<<");
                sdba.setType(SimpleDbAction.DELETE);
                v.add(sdba);
                sdba = new SimpleDbAction();
                sdba.setStatement("delete from befreiungerlaubnisarray where befreiungerlaubnis=" + elem.getId());
                sdba.setDescription("Delete in >>Befreiungerlaubnisarray<<");
                sdba.setType(SimpleDbAction.DELETE);
                v.add(sdba);
            }
        }
        befreiungenToDelete.clear();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public BefreiungenModel getBefreiungenModel() {
        return befreiungenModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  befreiungenModel  DOCUMENT ME!
     */
    public void setBefreiungenModel(final BefreiungenModel befreiungenModel) {
        this.befreiungenModel = befreiungenModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getKassenzeichen() {
        return kassenzeichen;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kassenzeichen  DOCUMENT ME!
     */
    public void setKassenzeichen(final int kassenzeichen) {
        this.kassenzeichen = kassenzeichen;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Vector<BefreiungErlaubnis> getBefreiungen() {
        return befreiungen;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  befreiungen  DOCUMENT ME!
     */
    public void setBefreiungen(final Vector<BefreiungErlaubnis> befreiungen) {
        this.befreiungen = befreiungen;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Vector<BefreiungErlaubnis> getBefreiungenToDelete() {
        return befreiungenToDelete;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  befreiungToDelete  DOCUMENT ME!
     */
    public void addBefreiungToDelete(final BefreiungErlaubnis befreiungToDelete) {
        befreiungenToDelete.add(befreiungToDelete);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class ComboBoxModel extends DefaultComboBoxModel {

        //~ Static fields/initializers -----------------------------------------

        public static final int RK_ANGESCHL = 1;
        public static final int MKR_ANGESCHL = 2;
        public static final int MKS_ANGESCHL = 4;
        public static final int SK_ANGESCHL = 8;

        //~ Instance fields ----------------------------------------------------

        private int comboBoxIdentifier = -1;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ComboBoxModel object.
         *
         * @param  which  DOCUMENT ME!
         */
        public ComboBoxModel(final int which) {
            super(new String[] { "ja", "nein", "fraglich" });
            comboBoxIdentifier = which;
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param  index  DOCUMENT ME!
         */
        public void setSelectedIndex(final int index) {
            setSelectedItem(super.getElementAt(index));
        }
        @Override
        public void setSelectedItem(final Object anObject) {
            super.setSelectedItem(anObject);
            final int index = super.getIndexOf(anObject);
            switch (comboBoxIdentifier) {
                case RK_ANGESCHL: {
                    rkAngeschlossen = index;
                    break;
                }
                case MKR_ANGESCHL: {
                    mkrAngeschlossen = index;
                    break;
                }
                case MKS_ANGESCHL: {
                    mksAngeschlossen = index;
                    break;
                }
                case SK_ANGESCHL: {
                    skAngeschlossen = index;
                }
            }
        }

        @Override
        public Object getSelectedItem() {
            switch (comboBoxIdentifier) {
                case RK_ANGESCHL: {
                    super.setSelectedItem(super.getElementAt(rkAngeschlossen));
                    break;
                }
                case MKR_ANGESCHL: {
                    super.setSelectedItem(super.getElementAt(mkrAngeschlossen));
                    break;
                }
                case MKS_ANGESCHL: {
                    super.setSelectedItem(super.getElementAt(mksAngeschlossen));
                    break;
                }
                case SK_ANGESCHL: {
                    super.setSelectedItem(super.getElementAt(skAngeschlossen));
                }
            }

            final Object retValue;
            retValue = super.getSelectedItem();
            return retValue;
        }
    }
    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class CheckBoxModel extends javax.swing.JToggleButton.ToggleButtonModel {

        //~ Static fields/initializers -----------------------------------------

        public static final int RK_VORH = 1;
        public static final int MKR_VORH = 2;
        public static final int MKS_VORH = 4;
        public static final int SK_VORH = 8;
        public static final int SG_VORH = 16;
        public static final int KKA_VORH = 32;
        public static final int SG_ENTL = 64;
        public static final int KKA_ENTL = 128;
        public static final int EVG = 256;

        //~ Instance fields ----------------------------------------------------

        private int checkBoxIdentifier = -1;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new CheckBoxModel object.
         *
         * @param  value  DOCUMENT ME!
         */
        public CheckBoxModel(final int value) {
            checkBoxIdentifier = value;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void setSelected(final boolean b) {
            switch (checkBoxIdentifier) {
                case RK_VORH: {
                    setRkVorhanden(b);
                    break;
                }
                case MKR_VORH: {
                    setMkrVorhanden(b);
                    break;
                }
                case MKS_VORH: {
                    setMksVorhanden(b);
                    break;
                }
                case SK_VORH: {
                    setSkVorhanden(b);
                    break;
                }
                case SG_VORH: {
                    setSgVorhanden(b);
                    break;
                }
                case KKA_VORH: {
                    setKkaVorhanden(b);
                    break;
                }
                case SG_ENTL: {
                    setSgEntleerung(b);
                    break;
                }
                case KKA_ENTL: {
                    setKkaEntleerung(b);
                    break;
                }
                case EVG: {
                    setEvg(b);
                }
            }
        }
        @Override
        public boolean isSelected() {
            switch (checkBoxIdentifier) {
                case RK_VORH: {
                    return isRkVorhanden();
                }
                case MKR_VORH: {
                    return isMkrVorhanden();
                }
                case MKS_VORH: {
                    return isMksVorhanden();
                }
                case SK_VORH: {
                    return isSkVorhanden();
                }
                case SG_VORH: {
                    return isSgVorhanden();
                }
                case KKA_VORH: {
                    return isKkaVorhanden();
                }
                case SG_ENTL: {
                    return isSgEntleerung();
                }
                case KKA_ENTL: {
                    return isKkaEntleerung();
                }
                case EVG: {
                    return isEvg();
                }
            }
            return false;
        }
    }
}
