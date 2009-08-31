/*
 * SummenTableCellRenderer.java
 *
 * Created on 7. Januar 2005, 10:32
 */

package de.cismet.verdis.gui;
import javax.swing.table.*;
import javax.swing.*;
import java.awt.Component;
/**
 *
 * @author hell
 */
public class SummenTableCellRenderer extends DefaultTableCellRenderer {
    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    
    /** Creates a new instance of SummenTableCellRenderer */
    public SummenTableCellRenderer() {
        super();
    }
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label=new JLabel(value.toString());
        if (column==1) {
            label.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        } else {
            label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        }
        return label;
    }
}
