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

import de.cismet.verdis.interfaces.CidsBeanComponent;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class KassenzeichenGeometrienList extends JList<CidsBean> implements CidsBeanComponent {

    //~ Static fields/initializers ---------------------------------------------

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            KassenzeichenGeometrienList.class);

    //~ Instance fields --------------------------------------------------------

    private CidsBean kassenzeichen;
    private KassenzeichenGeometrienPanel panel;

    //~ Methods ----------------------------------------------------------------

    @Override
    public void addBean(final CidsBean cidsBean) {
//        final Collection<CidsBean> geos = (Collection<CidsBean>)kassenzeichen.getProperty(
//                KassenzeichenPropertyConstants.PROP__KASSENZEICHEN_GEOMETRIEN);
//        geos.add(cidsBean);
        panel.addKassenzeichenGeometrieBean(cidsBean);
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

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getKassenzeichen() {
        return kassenzeichen;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  panel  DOCUMENT ME!
     */
    public void setPanel(final KassenzeichenGeometrienPanel panel) {
        this.panel = panel;
    }
}
