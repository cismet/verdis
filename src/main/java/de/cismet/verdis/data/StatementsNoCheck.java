/*
 * StatementNoCheck.java
 *
 * Created on 22. Januar 2005, 17:22
 */

package de.cismet.verdis.data;
import java.sql.*;
/**
 *
 * @author hell
 */
public class StatementsNoCheck {
    java.util.Vector statements=new java.util.Vector();
    /** Creates a new instance of StatementNoCheck */
    public StatementsNoCheck() {
    }
    
    public void addStatements(String s) {
        statements.add(s);
    }
    
    public java.util.Vector getStatements() {
        return statements;
    }
    
    
}
