/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;

import de.cismet.verdis.gui.GrundbuchblattSucheDialog;

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
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class Test {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(Test.class);

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        try {
            Log4JQuickConfig.configure4LumbermillOnLocalhost();
//            final CidsBean kassenzeiechenBean = DevelopmentTools.createCidsBeanFromRMIConnectionOnLocalhost(
//                    VerdisConstants.DOMAIN,
//                    "VORN",
//                    "SteinbacherD102",
//                    "",
//                    "kassenzeichen",
//                    41);
//            final List<CidsBean> fronten = (List<CidsBean>)kassenzeiechenBean.getProperty(
//                    KassenzeichenPropertyConstants.PROP__FRONTEN);
//            final CidsBean frontBean = fronten.get(fronten.size() - 1);
//            final CidsBean geomBean = (CidsBean)frontBean.getProperty(FrontinfoPropertyConstants.PROP__GEOMETRIE);
//
//            if (geomBean != null) {
//                geomBean.delete();
//            }
//            frontBean.delete();
//            fronten.remove(frontBean);
//
//            LOG.fatal(kassenzeiechenBean.getMOString());
//            final CidsBean persistedBean = kassenzeiechenBean.persist();
//            LOG.fatal(persistedBean.getMOString());
            final GrundbuchblattSucheDialog dialog = GrundbuchblattSucheDialog.getInstance();
            StaticSwingTools.showDialog(dialog);
        } catch (Exception ex) {
            LOG.fatal(ex, ex);
        }
    }
}
