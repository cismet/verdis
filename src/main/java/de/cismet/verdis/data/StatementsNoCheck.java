/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * StatementNoCheck.java
 *
 * Created on 22. Januar 2005, 17:22
 */
package de.cismet.verdis.data;
import java.sql.*;
/**
 * DOCUMENT ME!
 *
 * @author   hell
 * @version  $Revision$, $Date$
 */
public class StatementsNoCheck {

    //~ Instance fields --------------------------------------------------------

    java.util.Vector statements = new java.util.Vector();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of StatementNoCheck.
     */
    public StatementsNoCheck() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  s  DOCUMENT ME!
     */
    public void addStatements(final String s) {
        statements.add(s);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public java.util.Vector getStatements() {
        return statements;
    }
}
