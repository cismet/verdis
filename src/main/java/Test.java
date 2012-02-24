
import de.cismet.cids.client.tools.DevelopmentTools;
import de.cismet.cids.dynamics.CidsBean;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;
import de.cismet.verdis.CidsAppBackend;
import de.cismet.verdis.constants.FrontinfoPropertyConstants;
import de.cismet.verdis.constants.KassenzeichenPropertyConstants;
import java.util.Collection;
import java.util.List;
import org.openide.util.Exceptions;

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

/**
 *
 * @author jruiz
 */
public class Test {
    
    private final static transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(Test.class);

    public static void main(String[] args) {
        try {
            Log4JQuickConfig.configure4LumbermillOnLocalhost();
            final CidsBean kassenzeiechenBean = DevelopmentTools.createCidsBeanFromRMIConnectionOnLocalhost(CidsAppBackend.DOMAIN, "VORN", "SteinbacherD102", "vds102", "kassenzeichen", 41);            
            final List<CidsBean> fronten = (List<CidsBean>)kassenzeiechenBean.getProperty(KassenzeichenPropertyConstants.PROP__FRONTEN);
            final CidsBean frontBean = fronten.get(fronten.size() - 1);            
            final CidsBean geomBean = (CidsBean) frontBean.getProperty(FrontinfoPropertyConstants.PROP__GEOMETRIE);            
            
            if (geomBean != null) {
                geomBean.delete();
            }
            frontBean.delete();
            fronten.remove(frontBean);            
            
            LOG.fatal(kassenzeiechenBean.getMOString());
            final CidsBean persistedBean = kassenzeiechenBean.persist();
            LOG.fatal(persistedBean.getMOString());
        } catch (Exception ex) {
            LOG.fatal(ex, ex);
        }
    }
    
}
