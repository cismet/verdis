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
public class CrossReference {

    //~ Instance fields --------------------------------------------------------

    private final int entityFromKassenzeichen;
    private final int entityFromId;
    private final String entityFromBezeichnung;
    private final int entityToKassenzeichen;
    private final String entityToBezeichnung;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new EntityCrossreference object.
     *
     * @param  entityfromKassenzeichen  DOCUMENT ME!
     * @param  entityFromId             DOCUMENT ME!
     * @param  entityfromBezeichnung    DOCUMENT ME!
     * @param  entityToKassenzeichen    DOCUMENT ME!
     * @param  entityToBezeichnung      DOCUMENT ME!
     */
    public CrossReference(
            final int entityfromKassenzeichen,
            final int entityFromId,
            final String entityfromBezeichnung,
            final int entityToKassenzeichen,
            final String entityToBezeichnung) {
        this.entityFromKassenzeichen = entityfromKassenzeichen;
        this.entityFromId = entityFromId;
        this.entityFromBezeichnung = entityfromBezeichnung;
        this.entityToKassenzeichen = entityToKassenzeichen;
        this.entityToBezeichnung = entityToBezeichnung;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getEntityFromId() {
        return entityFromId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getEntityfromBezeichnung() {
        return entityFromBezeichnung;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getEntityfromKassenzeichen() {
        return entityFromKassenzeichen;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getEntityToBezeichnung() {
        return entityToBezeichnung;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getEntityToKassenzeichen() {
        return entityToKassenzeichen;
    }

    @Override
    public boolean equals(final Object obj) {
        if ((obj == null) || !(obj instanceof CrossReference)) {
            return false;
        }
        final CrossReference other = (CrossReference)obj;
        return other.entityToBezeichnung.equals(entityToBezeichnung)
                    && (other.entityToKassenzeichen == entityToKassenzeichen)
                    && (other.entityFromId == entityFromId)
                    && other.entityFromBezeichnung.equals(entityFromBezeichnung)
                    && (other.entityFromKassenzeichen == entityFromKassenzeichen);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = (13 * hash) + this.entityFromKassenzeichen;
        hash = (13 * hash) + this.entityFromId;
        hash = (13 * hash) + ((this.entityFromBezeichnung != null) ? this.entityFromBezeichnung.hashCode() : 0);
        hash = (13 * hash) + this.entityToKassenzeichen;
        hash = (13 * hash) + ((this.entityToBezeichnung != null) ? this.entityToBezeichnung.hashCode() : 0);
        return hash;
    }
}
