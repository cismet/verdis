/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten.hell@cismet.de
 * @version  $Revision$, $Date$
 */
public class TestBean {

    //~ Instance fields --------------------------------------------------------

    private TestBean tb1;

    /** Holds value of property s1. */
    private String s1;

    /** Holds value of property i1. */
    private int i1;

    /** Holds value of property f1. */
    private float f1;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of TestBean.
     */
    public TestBean() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Getter for property s1.
     *
     * @return  Value of property s1.
     */
    public String getS1() {
        return this.s1;
    }

    /**
     * Setter for property s1.
     *
     * @param  s1  New value of property s1.
     */
    public void setS1(final String s1) {
        this.s1 = s1;
    }

    /**
     * Getter for property i1.
     *
     * @return  Value of property i1.
     */
    public int getI1() {
        return this.i1;
    }

    /**
     * Setter for property i1.
     *
     * @param  i1  New value of property i1.
     */
    public void setI1(final int i1) {
        this.i1 = i1;
    }

    /**
     * Getter for property f1.
     *
     * @return  Value of property f1.
     */
    public float getF1() {
        return this.f1;
    }

    /**
     * Setter for property f1.
     *
     * @param  f1  New value of property f1.
     */
    public void setF1(final float f1) {
        this.f1 = f1;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public TestBean getTb1() {
        return tb1;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  tb1  DOCUMENT ME!
     */
    public void setTb1(final TestBean tb1) {
        this.tb1 = tb1;
    }
}
