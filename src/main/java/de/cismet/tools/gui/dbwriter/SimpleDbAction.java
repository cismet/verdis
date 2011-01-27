/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * SimpleDbAction.java
 *
 * Created on 24. M\u00E4rz 2005, 12:51
 */
package de.cismet.tools.gui.dbwriter;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DOCUMENT ME!
 *
 * @author   hell
 * @version  $Revision$, $Date$
 */
public class SimpleDbAction {

    //~ Static fields/initializers ---------------------------------------------

    public static final int INSERT = 0;
    public static final int UPDATE = 1;
    public static final int DELETE = 2;

    //~ Instance fields --------------------------------------------------------

    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private String statement;
    private String description;
    private int type;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of SimpleDbAction.
     */
    public SimpleDbAction() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getStatement() {
        return statement;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  statement  DOCUMENT ME!
     */
    public void setStatement(final String statement) {
        this.statement = statement;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getDescription() {
        return description;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  description  DOCUMENT ME!
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getType() {
        return type;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  type  DOCUMENT ME!
     */
    public void setType(final int type) {
        this.type = type;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   conn  DOCUMENT ME!
     *
     * @throws  SQLException  DOCUMENT ME!
     */
    public void executeAction(final Connection conn) throws SQLException {
        final Statement stmnt = conn.createStatement();
        log.info("SQL-EXECUTE:" + statement);
        if (!DbWriterDialog.DEBUG_MODE) {
            stmnt.execute(statement);
        }
        stmnt.close();
    }
}
