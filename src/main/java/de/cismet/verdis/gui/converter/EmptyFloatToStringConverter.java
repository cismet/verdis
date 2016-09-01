/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.verdis.gui.converter;

import org.jdesktop.beansbinding.Converter;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class EmptyFloatToStringConverter extends Converter<Float, String> {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String convertForward(final Float value) {
        if (value != null) {
            return value.toString();
        } else {
            return null;
        }
    }

    @Override
    public Float convertReverse(final String value) {
        if ((value != null) && (value.length() > 0)) {
            return Float.parseFloat(value);
        } else {
            return null;
        }
    }
}
