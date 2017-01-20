/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis.data;

import java.util.Collection;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class CsLock {

    //~ Instance fields --------------------------------------------------------

    private final int class_id;
    private final int object_id;
    private final String user_string;
    private final String additional_info;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CsLock object.
     *
     * @param  class_id         DOCUMENT ME!
     * @param  object_id        DOCUMENT ME!
     * @param  user_string      DOCUMENT ME!
     * @param  additional_info  DOCUMENT ME!
     */
    public CsLock(final int class_id, final int object_id, final String user_string, final String additional_info) {
        this.class_id = class_id;
        this.object_id = object_id;
        this.user_string = user_string;
        this.additional_info = additional_info;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getClass_id() {
        return class_id;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getObject_id() {
        return object_id;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getUser_string() {
        return user_string;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getAdditional_info() {
        return additional_info;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  csLock  DOCUMENT ME!
     */
    public static void save(final CsLock csLock) {
    }

    /**
     * DOCUMENT ME!
     *
     * @param   class_id   DOCUMENT ME!
     * @param   object_id  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Collection<CsLock> searchFor(final int class_id, final int object_id) {
        return null;
    }
}
