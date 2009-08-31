/*
 * DbWriterTableModel.java
 *
 * Created on 24. M\u00E4rz 2005, 17:37
 */

package de.cismet.tools.gui.dbwriter;

import java.util.Vector;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author hell
 */
public class DbWriterTableModel extends AbstractTableModel{
    private Vector actions;
    private javax.swing.ImageIcon insert;
    private javax.swing.ImageIcon update;
    private javax.swing.ImageIcon delete;

    /** Creates a new instance of DbWriterTableModel */
    public DbWriterTableModel(Vector actions) {
        insert=new javax.swing.ImageIcon(getClass().getResource("/de/cismet/tools/gui/dbwriter/res/new_table_row.png"));
        update=new javax.swing.ImageIcon(getClass().getResource("/de/cismet/tools/gui/dbwriter/res/update_table_row.png"));
        delete=new javax.swing.ImageIcon(getClass().getResource("/de/cismet/tools/gui/dbwriter/res/delete_table_row.png"));

        this.actions=actions;
    }

    /**
     * Returns the value for the cell at <code>columnIndex</code> and
     * <code>rowIndex</code>.
     * 
     * @param	rowIndex	the row whose value is to be queried
     * @param	columnIndex 	the column whose value is to be queried
     * @return	the value Object at the specified cell
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object o=actions.get(rowIndex);
        if (o instanceof SimpleDbAction) {
            SimpleDbAction sda=(SimpleDbAction)o;
                switch(columnIndex) {
                    case 0:
                        switch(sda.getType()) {
                            case SimpleDbAction.INSERT:
                                return this.insert;
                            case SimpleDbAction.UPDATE:
                                return this.update;
                            case SimpleDbAction.DELETE:
                                return this.delete;
                            default:
                                return null;
                        }
                    case 1:
                        return sda.getDescription();
                    default: 
                        return null;
                }
        }
        else if (o instanceof String && columnIndex==1) {
           return o.toString(); 
        }
        else {
            return null;
        }
    }

    /**
     * Returns the number of rows in the model. A
     * <code>JTable</code> uses this method to determine how many rows it
     * should display.  This method should be quick, as it
     * is called frequently during rendering.
     * 
     * @return the number of rows in the model
     * @see #getColumnCount
     */
    public int getRowCount() {
        return actions.size();
    }

    /**
     * Returns the number of columns in the model. A
     * <code>JTable</code> uses this method to determine how many columns it
     * should create and display by default.
     * 
     * @return the number of columns in the model
     * @see #getRowCount
     */
    public int getColumnCount() {
      return 2;
    }

    /**
     *  Returns <code>Object.class</code> regardless of <code>columnIndex</code>.
     * 
     *  @param columnIndex  the column being queried
     *  @return the Object.class
     */
    public Class getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return javax.swing.Icon.class;
            case 1:
            default:
                return java.lang.String.class;
        }
    }
    
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return " ";
            case 1:
            default:
                return "Task";
        }
    }
    
}
