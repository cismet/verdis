/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.verdis.data;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class FlaechenBeschreibung {

    //~ Instance fields --------------------------------------------------------

    private boolean dachflaeche = true;
    private String beschreibung;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FlaechenBeschreibung object.
     *
     * @param  dachflaeche   DOCUMENT ME!
     * @param  beschreibung  DOCUMENT ME!
     */
    public FlaechenBeschreibung(final boolean dachflaeche, final String beschreibung) {
        this.beschreibung = beschreibung;
        this.dachflaeche = dachflaeche;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getBeschreibung() {
        return beschreibung;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  beschreibung  DOCUMENT ME!
     */
    public void setBeschreibung(final String beschreibung) {
        this.beschreibung = beschreibung;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isDachflaeche() {
        return dachflaeche;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  dachflaeche  DOCUMENT ME!
     */
    public void setDachflaeche(final boolean dachflaeche) {
        this.dachflaeche = dachflaeche;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FlaechenBeschreibung other = (FlaechenBeschreibung)obj;
        if (this.dachflaeche != other.dachflaeche) {
            return false;
        }
        if ((this.beschreibung != other.beschreibung)
                    && ((this.beschreibung == null) || !this.beschreibung.equals(other.beschreibung))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = (83 * hash) + (this.dachflaeche ? 1 : 0);
        hash = (83 * hash) + ((this.beschreibung != null) ? this.beschreibung.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return beschreibung;
    }
}
