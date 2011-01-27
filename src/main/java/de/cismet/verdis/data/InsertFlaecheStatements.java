/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * InsertFlaecheStatements.java
 *
 * Created on 22. Januar 2005, 17:31
 */
package de.cismet.verdis.data;
import java.sql.*;

import java.util.*;
/**
 * DOCUMENT ME!
 *
 * @author   hell
 * @version  $Revision$, $Date$
 */
public class InsertFlaecheStatements {

    //~ Instance fields --------------------------------------------------------

    Flaeche newFlaeche;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of InsertFlaecheStatements.
     *
     * @param  f  DOCUMENT ME!
     */
    public InsertFlaecheStatements(final Flaeche f) {
        newFlaeche = f;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   conn2Check  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public java.util.Vector getStatements(final Connection conn2Check) throws Exception {
        final Statement stmnt = conn2Check.createStatement();
        // Herausfinden der neuen ID f\u00FCr flaechen
        ResultSet rs = stmnt.executeQuery("select max(id) from flaechen");
        rs.next();
        final int new_flaechen_id = rs.getInt(1) + 1;
        rs.close();
        // Herausfinden der neuen ID f\u00FCr flaeche
        rs = stmnt.executeQuery("select max(id) from flaeche");
        rs.next();
        final int new_flaeche_id = rs.getInt(1) + 1;
        rs.close();

        int new_flaecheninfo_id;

        // unsinn weil kein insert mehr bei vorhandenen flaecheninfo
        if (newFlaeche.getFlaecheninfo_id() == 0) {
            // Herausfinden der neuen ID f\u00FCr flaecheninfo
            rs = stmnt.executeQuery("select max(id) from flaecheninfo");
            rs.next();
            new_flaecheninfo_id = rs.getInt(1) + 1;
            rs.close();
        } else {
            new_flaecheninfo_id = newFlaeche.getFlaecheninfo_id();
        }

        // insert into flaechen
        // insert into flaeche
        // insert into flaecheninfo
        final Vector v = new Vector();
        return v;
    }
}
