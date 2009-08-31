/*
 * TestBean.java
 * Copyright (C) 2005 by:
 *
 *----------------------------
 * cismet GmbH
 * Goebenstrasse 40
 * 66117 Saarbruecken
 * http://www.cismet.de
 *----------------------------
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *----------------------------
 * Author:
 * thorsten.hell@cismet.de
 *----------------------------
 *
 * Created on 16. August 2005, 10:34
 *
 */

package de.cismet.verdis;

/**
 *
 * @author thorsten.hell@cismet.de
 */
public class TestBean {
    private TestBean tb1;
    /** Creates a new instance of TestBean */
    public TestBean() {
    }

    /**
     * Holds value of property s1.
     */
    private String s1;

    /**
     * Getter for property s1.
     * @return Value of property s1.
     */
    public String getS1() {

        return this.s1;
    }

    /**
     * Setter for property s1.
     * @param s1 New value of property s1.
     */
    public void setS1(String s1) {

        this.s1 = s1;
    }

    /**
     * Holds value of property i1.
     */
    private int i1;

    /**
     * Getter for property i1.
     * @return Value of property i1.
     */
    public int getI1() {

        return this.i1;
    }

    /**
     * Setter for property i1.
     * @param i1 New value of property i1.
     */
    public void setI1(int i1) {

        this.i1 = i1;
    }

    /**
     * Holds value of property f1.
     */
    private float f1;

    /**
     * Getter for property f1.
     * @return Value of property f1.
     */
    public float getF1() {

        return this.f1;
    }

    /**
     * Setter for property f1.
     * @param f1 New value of property f1.
     */
    public void setF1(float f1) {

        this.f1 = f1;
    }

    public TestBean getTb1() {
        return tb1;
    }

    public void setTb1(TestBean tb1) {
        this.tb1 = tb1;
    }
    
}
