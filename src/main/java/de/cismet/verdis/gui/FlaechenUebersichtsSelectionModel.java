/*
 * FlaechenUebersichtsSelectionModel.java
 *
 * Created on 14. Januar 2005, 15:05
 */

package de.cismet.verdis.gui;
import javax.swing.*;

/**
 *
 * @author hell
 */
public class FlaechenUebersichtsSelectionModel extends DefaultListSelectionModel{
    private int oldSelection;

    /** Creates a new instance of FlaechenUebersichtsSelectionModel */
    public FlaechenUebersichtsSelectionModel() {
        super();
    }
    
    public void setSelectionInterval(int i0,int i1) {
        super.setSelectionInterval(i0,i1);
        System.out.println(i0);
        oldSelection=i0;
    }
    
    
}
