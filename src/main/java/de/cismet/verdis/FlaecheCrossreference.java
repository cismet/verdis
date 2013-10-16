/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class FlaecheCrossreference {

    //~ Instance fields --------------------------------------------------------

    private final int flaechefromKassenzeichen;
    private final int flaechefromId;
    private final String flaechefromBezeichnung;
    private final int flaecheToKassenzeichen;
    private final String flaecheToBezeichnung;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FlaecheCrossreference object.
     *
     * @param  flaechefromKassenzeichen  DOCUMENT ME!
     * @param  flaechefromId             DOCUMENT ME!
     * @param  flaechefromBezeichnung    DOCUMENT ME!
     * @param  flaecheToKassenzeichen    DOCUMENT ME!
     * @param  flaecheToBezeichnung      DOCUMENT ME!
     */
    public FlaecheCrossreference(
            final int flaechefromKassenzeichen,
            final int flaechefromId,
            final String flaechefromBezeichnung,
            final int flaecheToKassenzeichen,
            final String flaecheToBezeichnung) {
        this.flaechefromKassenzeichen = flaechefromKassenzeichen;
        this.flaechefromId = flaechefromId;
        this.flaechefromBezeichnung = flaechefromBezeichnung;
        this.flaecheToKassenzeichen = flaecheToKassenzeichen;
        this.flaecheToBezeichnung = flaecheToBezeichnung;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getFlaechefromId() {
        return flaechefromId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getFlaechefromBezeichnung() {
        return flaechefromBezeichnung;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getFlaechefromKassenzeichen() {
        return flaechefromKassenzeichen;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getFlaecheToBezeichnung() {
        return flaecheToBezeichnung;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getFlaecheToKassenzeichen() {
        return flaecheToKassenzeichen;
    }

    @Override
    public boolean equals(final Object obj) {
        if ((obj == null) || !(obj instanceof FlaecheCrossreference)) {
            return false;
        }
        final FlaecheCrossreference other = (FlaecheCrossreference)obj;
        return other.flaecheToBezeichnung.equals(flaecheToBezeichnung)
                    && (other.flaecheToKassenzeichen == flaecheToKassenzeichen)
                    && (other.flaechefromId == flaechefromId)
                    && other.flaechefromBezeichnung.equals(flaechefromBezeichnung)
                    && (other.flaechefromKassenzeichen == flaechefromKassenzeichen);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = (13 * hash) + this.flaechefromKassenzeichen;
        hash = (13 * hash) + this.flaechefromId;
        hash = (13 * hash) + ((this.flaechefromBezeichnung != null) ? this.flaechefromBezeichnung.hashCode() : 0);
        hash = (13 * hash) + this.flaecheToKassenzeichen;
        hash = (13 * hash) + ((this.flaecheToBezeichnung != null) ? this.flaecheToBezeichnung.hashCode() : 0);
        return hash;
    }
}
