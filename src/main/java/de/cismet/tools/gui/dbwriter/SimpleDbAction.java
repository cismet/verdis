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
 *
 * @author hell
 */
public class SimpleDbAction {
    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private String statement;
    private String description;
    private int type;
    public static final int INSERT=0;
    public static final int UPDATE=1;
    public static final int DELETE=2;
    /** Creates a new instance of SimpleDbAction */
    public SimpleDbAction() {
    
    }

    public String getStatement() {
        return statement;
    }
    
    public void setStatement(String statement) {
        this.statement = statement;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    
    public void executeAction(Connection conn) throws SQLException{
       Statement stmnt=conn.createStatement();
       log.info("SQL-EXECUTE:"+statement);
       if (!DbWriterDialog.DEBUG_MODE){
             stmnt.execute(statement);
        }
       stmnt.close();
    }
    
}
