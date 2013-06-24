/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis;

import Sirius.server.middleware.types.MetaObject;

import java.sql.Date;

import java.util.Calendar;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.verdis.commons.constants.FlaechePropertyConstants;
import de.cismet.verdis.commons.constants.FrontinfoPropertyConstants;
import de.cismet.verdis.commons.constants.SatzungPropertyConstants;

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
     * @param   clipboardBean  is an instance of Frontinfo
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

        // the values for PROP__SR_KLASSE_OR, PROP__WD_PRIO_OR and PROP__STRASSE were already set in
        // WDSRTableModel.deepclone()

        final int newNummer = ((WDSRTabellenPanel)getTable()).getValidNummer();
        pasteBean.setProperty(FrontinfoPropertyConstants.PROP__NUMMER, newNummer);

        final Calendar cal = Calendar.getInstance();
        pasteBean.setProperty(FrontinfoPropertyConstants.PROP__ERFASSUNGSDATUM, new Date(cal.getTime().getTime()));

        createPastedBean_Satzung(FrontinfoPropertyConstants.PROP__LAGE_SR, clipboardBean, pasteBean);
        createPastedBean_Satzung(FrontinfoPropertyConstants.PROP__LAGE_WD, clipboardBean, pasteBean);

        pasteBean.setProperty(
            FrontinfoPropertyConstants.PROP__LAGE_SR,
            clipboardBean.getProperty(FrontinfoPropertyConstants.PROP__LAGE_SR));

        pasteBean.setProperty(FrontinfoPropertyConstants.PROP__SR_BEM, null);
        pasteBean.setProperty(FrontinfoPropertyConstants.PROP__WD_BEM, null);

        pasteBean.setProperty(FrontinfoPropertyConstants.PROP__WD_VERANLAGUNG, null);
        pasteBean.setProperty(FrontinfoPropertyConstants.PROP__SR_VERANLAGUNG, null);

        return pasteBean;
    }

    /**
     * PROP__LAGE_SR and PROP__LAGE_WD use the same Type, namely Satzung. Therefore their forceStatus() calls are the
     * same. Those calls are needed to avoid duplicate database entries.
     *
     * @param   identifier     FrontinfoPropertyConstants.PROP__LAGE_SR or FrontinfoPropertyConstants.PROP__LAGE_WD
     * @param   clipboardBean  DOCUMENT ME!
     * @param   pasteBean      DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private void createPastedBean_Satzung(final String identifier,
            final CidsBean clipboardBean,
            final CidsBean pasteBean) throws Exception {
        pasteBean.setProperty(
            identifier,
            clipboardBean.getProperty(identifier));
        final CidsBean satzung = (CidsBean)pasteBean.getProperty(identifier);
        if (satzung != null) {
            final CidsBean satzung__sr_klasse = (CidsBean)satzung.getProperty(SatzungPropertyConstants.PROP__SR_KLASSE);
            if (satzung__sr_klasse != null) {
                satzung__sr_klasse.getMetaObject().forceStatus(MetaObject.NO_STATUS);
            }
            satzung.setProperty(SatzungPropertyConstants.PROP__SR_BEM, null);
            final CidsBean satzung__wd_prio = (CidsBean)satzung.getProperty(SatzungPropertyConstants.PROP__WD_PRIO);
            if (satzung__wd_prio != null) {
                satzung__wd_prio.getMetaObject().forceStatus(MetaObject.NO_STATUS);
            }
            satzung.setProperty(SatzungPropertyConstants.PROP__WD_BEM, null);
            final CidsBean satzung__strasse = (CidsBean)satzung.getProperty(SatzungPropertyConstants.PROP__STRASSE);
            if (satzung__strasse != null) {
                satzung__strasse.getMetaObject().forceStatus(MetaObject.NO_STATUS);
            }
        }
    }

    @Override
    public boolean isPastable(final CidsBean clipboardFlaecheBean) {
        if (clipboardFlaecheBean == null) {
            return false;
        }

        for (final CidsBean flaecheBean : getTable().getAllBeans()) {
            final int id = (Integer)flaecheBean.getProperty(FrontinfoPropertyConstants.PROP__ID);
            final int ownId = (Integer)clipboardFlaecheBean.getProperty(FrontinfoPropertyConstants.PROP__ID);
            if (id == ownId) {
                return false;
            }
        }

        return true;
    }
}
