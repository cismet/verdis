/*
 * BefreiungErlaubnis.java
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
 * Created on 13. April 2006, 13:21
 *
 */

package de.cismet.verdis.data;

/**
 *
 * @author thorsten.hell@cismet.de
 */
public class BefreiungErlaubnis {
    private int id=-1;
    private String aktenzeichen="";
    private String gueltigBis="";
    /** Creates a new instance of BefreiungErlaubnis */
    public BefreiungErlaubnis() {
    }
    public void fillFromObjectArray(Object[] oa) {
        id=((Integer)oa[0]);
        aktenzeichen=oa[1].toString();
        java.sql.Date d=(java.sql.Date)oa[2];
        gueltigBis=java.text.DateFormat.getDateInstance().format(d);        
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAktenzeichen() {
        return aktenzeichen;
    }

    public void setAktenzeichen(String aktenzeichen) {
        this.aktenzeichen = aktenzeichen;
    }

    public String getGueltigBis() {
        return gueltigBis;
    }

    public void setGueltigBis(String gueltigBis) {
        this.gueltigBis = gueltigBis;
    }
    public boolean equals(Object o) {
        if (o instanceof BefreiungErlaubnis) {
            BefreiungErlaubnis be=(BefreiungErlaubnis)o;
            return (be.id==id&&be.aktenzeichen.equals(aktenzeichen) && be.gueltigBis.equals(gueltigBis));
        }
        else {
            return false;
        }
    }
    public Object clone() {
        BefreiungErlaubnis be=new BefreiungErlaubnis();
        be.id=id;
        be.aktenzeichen=aktenzeichen;
        be.gueltigBis=gueltigBis;
        return be;
    }
    
    
    
}
