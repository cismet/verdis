/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis;

import Sirius.navigator.connection.SessionManager;
import Sirius.server.middleware.types.MetaObject;

import java.sql.Date;

import java.util.Calendar;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.verdis.commons.constants.FlaechePropertyConstants;
import de.cismet.verdis.commons.constants.FrontPropertyConstants;
import de.cismet.verdis.commons.constants.FrontinfoPropertyConstants;
import de.cismet.verdis.commons.constants.SatzungPropertyConstants;
import de.cismet.verdis.commons.constants.VerdisConstants;
import de.cismet.verdis.commons.constants.VerdisMetaClassConstants;

import de.cismet.verdis.gui.WDSRTabellenPanel;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class FrontenClipboard extends AbstractClipboard {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FrontenClipboard object.
     *
     * @param  table  DOCUMENT ME!
     */
    public FrontenClipboard(final WDSRTabellenPanel table) {
        super(table);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   clipboardBean  is an instance of Front
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @Override
    public CidsBean createPastedBean(final CidsBean clipboardBean) throws Exception {
        final WDSRTabellenPanel table = (WDSRTabellenPanel)getComponent();
        final CidsBean pasteBean = table.getModel().deepcloneBean(clipboardBean);
        final int id = table.getTableHelper().getNextNewBeanId();
        pasteBean.setProperty(FrontPropertyConstants.PROP__ID, id);
        pasteBean.getMetaObject().setID(id);

        final int newNummer = ((WDSRTabellenPanel)getComponent()).getValidNummer();
        pasteBean.setProperty(FrontPropertyConstants.PROP__NUMMER, newNummer);

        pasteBean.setProperty(FrontPropertyConstants.PROP__BEARBEITET_DURCH, null);

        if (clipboardBean.getProperty(FrontPropertyConstants.PROP__FRONTINFO) != null) {
            final int frontinfoId = (Integer)clipboardBean.getProperty(
                    FrontPropertyConstants.PROP__FRONTINFO
                            + "."
                            + FrontinfoPropertyConstants.PROP__ID);
            final CidsBean frontinfoBean = SessionManager.getProxy()
                        .getMetaObject(
                                frontinfoId,
                                CidsAppBackend.getInstance().getVerdisMetaClass(
                                    VerdisMetaClassConstants.MC_FRONTINFO).getId(),
                                VerdisConstants.DOMAIN)
                        .getBean();
            pasteBean.setProperty(FrontPropertyConstants.PROP__FRONTINFO, frontinfoBean);
        }

        final Calendar cal = Calendar.getInstance();
        pasteBean.setProperty(FrontPropertyConstants.PROP__ERFASSUNGSDATUM, new Date(cal.getTime().getTime()));

        return pasteBean;
    }

    @Override
    public boolean isPastable(final CidsBean clipboardFrontBean) {
        if (clipboardFrontBean == null) {
            return false;
        }

        for (final CidsBean frontBean : getComponent().getAllBeans()) {
            final int id = (Integer)frontBean.getProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                            + FrontinfoPropertyConstants.PROP__ID);
            final int ownId = (Integer)clipboardFrontBean.getProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                            + FrontinfoPropertyConstants.PROP__ID);
            if (id == ownId) {
                return false;
            }
        }

        return true;
    }
}
