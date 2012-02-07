/*
 * Copyright (C) 2012 cismet GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.cismet.verdis.gui;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.verdis.constants.FortfuehrungPropertyConstants;
import java.text.DateFormat;
import java.util.Date;

/**
 *
 * @author jruiz
 */
public class FortfuehrungenTableModel extends CidsBeanTableModel {

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FortfuehrungenTableModel.class);

    private static final String[] COLUMN_NAMES = {
        "Datum",
        "Typ",
        "Beschreibung"
    };
    
    private static final Class[] COLUMN_CLASSES = {
        String.class,
        String.class,
        String.class        
    };
    
    public FortfuehrungenTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        final CidsBean fortfuehrungBean = getCidsBeanByIndex(rowIndex);
        if (fortfuehrungBean == null) {
            return null;
        }
        if (columnIndex == 0) {
            try {                
                final Date date = (Date) fortfuehrungBean.getProperty(FortfuehrungPropertyConstants.PROP__DATUM);
                if (date != null) {
                    return DateFormat.getInstance().format(date);
                } else {
                    return "";
                }
            } catch (Exception e) {
                LOG.warn("exception in tablemodel", e);
                return "";
            }            
        }  else if (columnIndex == 1) {
            try {
                return (String) fortfuehrungBean.getProperty(FortfuehrungPropertyConstants.PROP__ANLASS_NAME);
            } catch (Exception e) {
                LOG.warn("exception in tablemodel", e);
                return "";
            }            
        } else if (columnIndex == 2) {
            try {
                return (String) fortfuehrungBean.getProperty(FortfuehrungPropertyConstants.PROP__BESCHREIBUNG);
            } catch (Exception e) {
                LOG.warn("exception in tablemodel", e);
                return "";
            }            
        }
        return null;        
    }
    
}
