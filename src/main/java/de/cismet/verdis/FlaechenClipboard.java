/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 *  Copyright (C) 2011 jruiz
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.cismet.verdis;

import Sirius.navigator.connection.SessionManager;

import java.sql.Date;

import java.text.SimpleDateFormat;

import java.util.Calendar;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.verdis.commons.constants.FlaechePropertyConstants;
import de.cismet.verdis.commons.constants.FlaechenartPropertyConstants;
import de.cismet.verdis.commons.constants.FlaecheninfoPropertyConstants;
import de.cismet.verdis.commons.constants.VerdisConstants;
import de.cismet.verdis.commons.constants.VerdisMetaClassConstants;

import de.cismet.verdis.gui.RegenFlaechenTabellenPanel;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class FlaechenClipboard extends AbstractClipboard {

    //~ Static fields/initializers ---------------------------------------------

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FlaechenClipboard.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FlaechenClipboard object.
     *
     * @param  table  DOCUMENT ME!
     */
    public FlaechenClipboard(final RegenFlaechenTabellenPanel table) {
        super(table);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   clipboardBean  is an instance of Flaeche
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @Override
    public CidsBean createPastedBean(final CidsBean clipboardBean) throws Exception {
        final CidsBean pasteBean = getTable().getModel().deepcloneBean(clipboardBean);
        final int id = getTable().getTableHelper().getNextNewBeanId();
        pasteBean.setProperty(FlaechePropertyConstants.PROP__ID, id);
        pasteBean.getMetaObject().setID(id);

        if (clipboardBean.getProperty(FlaechePropertyConstants.PROP__FLAECHENINFO) != null) {
            final int flaecheninfoId = (Integer)clipboardBean.getProperty(
                    FlaechePropertyConstants.PROP__FLAECHENINFO
                            + "."
                            + FlaecheninfoPropertyConstants.PROP__ID);
            final CidsBean flaecheninfoBean = SessionManager.getProxy()
                        .getMetaObject(
                                flaecheninfoId,
                                CidsAppBackend.getInstance().getVerdisMetaClass(
                                    VerdisMetaClassConstants.MC_FLAECHENINFO).getId(),
                                VerdisConstants.DOMAIN)
                        .getBean();
            pasteBean.setProperty(FlaechePropertyConstants.PROP__FLAECHENINFO, flaecheninfoBean);
        }

        pasteBean.setProperty(FlaechePropertyConstants.PROP__BEMERKUNG, null);
        pasteBean.setProperty(
            FlaechePropertyConstants.PROP__FLAECHENBEZEICHNUNG,
            ((RegenFlaechenTabellenPanel)getTable()).getValidFlaechenname(
                (Integer)clipboardBean.getProperty(
                    FlaechePropertyConstants.PROP__FLAECHENINFO
                            + "."
                            + FlaecheninfoPropertyConstants.PROP__FLAECHENART
                            + "."
                            + FlaechenartPropertyConstants.PROP__ID)));
        final Calendar cal = Calendar.getInstance();
        pasteBean.setProperty(FlaechePropertyConstants.PROP__DATUM_ERFASSUNG, new Date(cal.getTime().getTime()));
        cal.add(Calendar.MONTH, 1);
        final SimpleDateFormat vDat = new SimpleDateFormat("yy/MM");
        pasteBean.setProperty(FlaechePropertyConstants.PROP__DATUM_VERANLAGUNG, vDat.format(cal.getTime()));

        return pasteBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   clipboardFlaecheBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public boolean isPastable(final CidsBean clipboardFlaecheBean) {
        if (clipboardFlaecheBean == null) {
            return false;
        }

        for (final CidsBean flaecheBean : getTable().getAllBeans()) {
            final int id = (Integer)flaecheBean.getProperty(FlaechePropertyConstants.PROP__FLAECHENINFO + "."
                            + FlaecheninfoPropertyConstants.PROP__ID);
            final int ownId = (Integer)clipboardFlaecheBean.getProperty(
                    FlaechePropertyConstants.PROP__FLAECHENINFO
                            + "."
                            + FlaecheninfoPropertyConstants.PROP__ID);
            if (id == ownId) {
                return false;
            }
        }

        return true;
    }
}
