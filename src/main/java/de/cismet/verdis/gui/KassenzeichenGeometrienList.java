/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.verdis.commons.constants.KassenzeichenPropertyConstants;

import de.cismet.verdis.interfaces.AbstractCidsBeanComponent;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class KassenzeichenGeometrienList extends JList<CidsBean> implements AbstractCidsBeanComponent {

    //~ Instance fields --------------------------------------------------------

    CidsBean kassenzeichen;

    //~ Methods ----------------------------------------------------------------

    @Override
    public void addBean(final CidsBean cidsBean) {
        final Collection<CidsBean> geos = (Collection<CidsBean>)kassenzeichen.getProperty(
                KassenzeichenPropertyConstants.PROP__KASSENZEICHEN_GEOMETRIEN);
        geos.add(cidsBean);
    }

    @Override
    public List<CidsBean> getSelectedBeans() {
        final List<CidsBean> cidsBeans = new ArrayList<CidsBean>();

        final int[] selectedIndices = getSelectedIndices();

        for (final int index : selectedIndices) {
            cidsBeans.add(getModel().getElementAt(index));
        }

        return cidsBeans;
    }

    @Override
    public void removeBean(final CidsBean cidsBean) {
        final Collection<CidsBean> geos = (Collection<CidsBean>)kassenzeichen.getProperty(
                KassenzeichenPropertyConstants.PROP__KASSENZEICHEN_GEOMETRIEN);
        geos.remove(cidsBean);
    }

    @Override
    public List<CidsBean> getAllBeans() {
        final List<CidsBean> cidsBeans = new ArrayList<CidsBean>();

        for (int i = 0; i < getModel().getSize(); i++) {
            cidsBeans.add(getModel().getElementAt(i));
        }

        return cidsBeans;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kassenzeichen  DOCUMENT ME!
     */
    public void setKassenzeichen(final CidsBean kassenzeichen) {
        this.kassenzeichen = kassenzeichen;
    }
}
