/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis;

import java.beans.XMLEncoder;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten.hell@cismet.de
 * @version  $Revision$, $Date$
 */
public class Test1 {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of Test1.
     */
    public Test1() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        try {
            final TestBean tb2 = new TestBean();
            tb2.setF1(2.2f);
            tb2.setI1(2);
            tb2.setS1("two");
            tb2.setTb1(null);
            final TestBean tb1 = new TestBean();
            tb1.setF1(1.1f);
            tb1.setI1(1);
            tb1.setS1("one");
            tb1.setTb1(tb2);

            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final FileOutputStream fos = new FileOutputStream("C:\\test.xml");
            final XMLEncoder xmlEncoder = new XMLEncoder(fos);

            xmlEncoder.writeObject(tb1);
            xmlEncoder.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
