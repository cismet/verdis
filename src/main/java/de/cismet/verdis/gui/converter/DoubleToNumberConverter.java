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
public class DoubleToNumberConverter extends Converter<Double, Number> {

    //~ Methods ----------------------------------------------------------------

    @Override
    public Number convertForward(final Double value) {
        return value;
    }

    @Override
    public Double convertReverse(final Number value) {
        return value.doubleValue();
    }
}
