/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis.interfaces;

import java.util.List;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public interface AbstractCidsBeanComponent {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    void addBean(final CidsBean cidsBean);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    List<CidsBean> getSelectedBeans();

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    void removeBean(final CidsBean cidsBean);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    List<CidsBean> getAllBeans();
}
