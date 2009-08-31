/*
 * Test1.java
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

import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

/**
 *
 * @author thorsten.hell@cismet.de
 */
public class Test1 {
    
    /** Creates a new instance of Test1 */
    public Test1() {
    }
    
    public static void main(String[] args) {
        try {
            TestBean tb2=new TestBean();
            tb2.setF1(2.2f);
            tb2.setI1(2);
            tb2.setS1("two");
            tb2.setTb1(null);
            TestBean tb1=new TestBean();
            tb1.setF1(1.1f);
            tb1.setI1(1);
            tb1.setS1("one");
            tb1.setTb1(tb2);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileOutputStream fos=new FileOutputStream("C:\\test.xml");
            XMLEncoder xmlEncoder = new XMLEncoder(fos);

            xmlEncoder.writeObject(tb1);
            xmlEncoder.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    
}
