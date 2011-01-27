/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * SimpleDocumentModel.java
 *
 * Created on 24. Januar 2005, 16:26
 */
package de.cismet.verdis.models;
import java.awt.Component;

import javax.swing.text.*;

import de.cismet.validation.Validatable;
import de.cismet.validation.ValidationStateChangedListener;
/**
 * DOCUMENT ME!
 *
 * @author   hell
 * @version  $Revision$, $Date$
 */
public class SimpleDocumentModel extends PlainDocument implements Validatable {

    //~ Instance fields --------------------------------------------------------

    protected String statusDescription = "";
    java.util.Vector listeners = new java.util.Vector();

    //~ Methods ----------------------------------------------------------------

    /**
     * Zum \u00DCberschreiben.
     *
     * @param   newValue  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean acceptChanges(final String newValue) {
        return true;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  newValue  DOCUMENT ME!
     */
    public void assignValue(final String newValue) {
    }

    /**
     * DOCUMENT ME!
     *
     * @param   string      DOCUMENT ME!
     * @param   attributes  DOCUMENT ME!
     *
     * @throws  BadLocationException  DOCUMENT ME!
     */
    public void insertNewString(final String string, final AttributeSet attributes) throws BadLocationException {
        if (string == null) {
            return;
        }
        super.remove(0, getLength());
        insertString(0, string, null);
    }

    @Override
    public void insertString(final int offset, final String string, final AttributeSet attributes)
            throws BadLocationException {
        if (string == null) {
            return;
        } else {
            String newValue;
            final int length = getLength();
            if (length == 0) {
                newValue = string;
            } else {
                final String currentContent = getText(0, length);
                final StringBuffer currentBuffer = new StringBuffer(currentContent);
                currentBuffer.insert(offset, string);
                newValue = currentBuffer.toString();
            }

            if (acceptChanges(newValue)) {
                assignValue(newValue);
                super.insertString(offset, string, attributes);
            }
        }
    }
    @Override
    public void remove(final int offs, final int len) throws BadLocationException {
        final StringBuffer currentBuffer = new StringBuffer(getText(0, getLength()));
        currentBuffer.delete(offs, offs + len);
        final String newValue = currentBuffer.toString();
        if (acceptChanges(newValue)) {
            assignValue(newValue);
            super.remove(offs, len);
        }
    }

    @Override
    public void removeValidationStateChangedListener(final de.cismet.validation.ValidationStateChangedListener l) {
        listeners.remove(l);
    }

    @Override
    public void addValidationStateChangedListener(final de.cismet.validation.ValidationStateChangedListener l) {
        listeners.add(l);
    }

    @Override
    public String getValidationMessage() {
        return statusDescription;
    }

    @Override
    public int getStatus() {
        return Validatable.VALID;
    }

    /**
     * DOCUMENT ME!
     */
    public void fireValidationStateChanged() {
        final java.util.Iterator it = listeners.iterator();
        while (it.hasNext()) {
            final ValidationStateChangedListener v = (ValidationStateChangedListener)it.next();
            v.validationStateChanged();
        }
    }

    @Override
    public void showAssistent(final Component parent) {
    }
}
