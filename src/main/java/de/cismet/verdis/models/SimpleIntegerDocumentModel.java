/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * SimpleDoubleDocumentModel.java
 *
 * Created on 25. Januar 2005, 16:34
 */
package de.cismet.verdis.models;

/**
 * DOCUMENT ME!
 *
 * @author   hell
 * @version  $Revision$, $Date$
 */
public class SimpleIntegerDocumentModel extends SimpleDocumentModel {

    //~ Methods ----------------------------------------------------------------

    /**
     * Creates a new instance of SimpleDoubleDocumentModel.
     *
     * @param   newValue  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public boolean acceptChanges(final String newValue) {
        boolean ok = true;
        try {
            if ((newValue != null) && newValue.equals("")) {
                assignValue((Integer)null);
                return true;
            }
            final Double d = new Double(newValue);
        } catch (Exception e) {
            ok = false;
        }
        return ok;
    }

    @Override
    public void assignValue(final String newValue) {
        try {
            final Integer d = new Integer(newValue);
            assignValue(d);
        } catch (Exception e) {
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  newDouble  DOCUMENT ME!
     */
    public void assignValue(final Integer newDouble) {
    }
}
