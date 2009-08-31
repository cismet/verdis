/*
 * SimpleDoubleDocumentModel.java
 *
 * Created on 25. Januar 2005, 16:34
 */

package de.cismet.verdis.models;

/**
 *
 * @author hell
 */
public class SimpleIntegerDocumentModel extends SimpleDocumentModel{
    
    /** Creates a new instance of SimpleDoubleDocumentModel */
    public boolean acceptChanges(String newValue) {
        boolean ok=true;
        try {
            if (newValue!=null && newValue.equals("")) {
                assignValue((Integer)null);
                return true;
            }
            Double d=new Double(newValue);
        }
        catch (Exception e) {
            ok=false;
        }
        return ok;
    }
    
    public void assignValue(String newValue) {
        try {
            Integer d=new Integer(newValue);
            assignValue(d);
        }
        catch (Exception e) {
            
        }
       
    }
   
    public void assignValue(Integer newDouble) {
        
    }
    
    
}
