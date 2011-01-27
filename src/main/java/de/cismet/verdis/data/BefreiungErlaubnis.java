/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis.data;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten.hell@cismet.de
 * @version  $Revision$, $Date$
 */
public class BefreiungErlaubnis {

    //~ Instance fields --------------------------------------------------------

    private int id = -1;
    private String aktenzeichen = "";
    private String gueltigBis = "";

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of BefreiungErlaubnis.
     */
    public BefreiungErlaubnis() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  oa  DOCUMENT ME!
     */
    public void fillFromObjectArray(final Object[] oa) {
        id = ((Integer)oa[0]);
        aktenzeichen = oa[1].toString();
        final java.sql.Date d = (java.sql.Date)oa[2];
        gueltigBis = java.text.DateFormat.getDateInstance().format(d);
    }
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getId() {
        return id;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  id  DOCUMENT ME!
     */
    public void setId(final int id) {
        this.id = id;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getAktenzeichen() {
        return aktenzeichen;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  aktenzeichen  DOCUMENT ME!
     */
    public void setAktenzeichen(final String aktenzeichen) {
        this.aktenzeichen = aktenzeichen;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getGueltigBis() {
        return gueltigBis;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  gueltigBis  DOCUMENT ME!
     */
    public void setGueltigBis(final String gueltigBis) {
        this.gueltigBis = gueltigBis;
    }
    @Override
    public boolean equals(final Object o) {
        if (o instanceof BefreiungErlaubnis) {
            final BefreiungErlaubnis be = (BefreiungErlaubnis)o;
            return ((be.id == id) && be.aktenzeichen.equals(aktenzeichen) && be.gueltigBis.equals(gueltigBis));
        } else {
            return false;
        }
    }
    @Override
    public Object clone() {
        final BefreiungErlaubnis be = new BefreiungErlaubnis();
        be.id = id;
        be.aktenzeichen = aktenzeichen;
        be.gueltigBis = gueltigBis;
        return be;
    }
}
