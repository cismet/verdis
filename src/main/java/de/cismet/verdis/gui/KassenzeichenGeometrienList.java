/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.verdis.interfaces.AbstractCidsBeanComponent;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class KassenzeichenGeometrienList extends JList<CidsBean> implements AbstractCidsBeanComponent {

    //~ Methods ----------------------------------------------------------------

    @Override
    public void addBean(final CidsBean cidsBean) {
        final DefaultListModel<CidsBean> dfm = (DefaultListModel<CidsBean>)getModel();
        dfm.addElement(cidsBean);
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
        final DefaultListModel<CidsBean> dfm = (DefaultListModel<CidsBean>)getModel();
        dfm.removeElement(cidsBean);
    }

    @Override
    public List<CidsBean> getAllBeans() {
        final List<CidsBean> cidsBeans = new ArrayList<CidsBean>();
        final DefaultListModel<CidsBean> dfm = (DefaultListModel<CidsBean>)getModel();

        for (int i = 0; i < dfm.size(); i++) {
            cidsBeans.add(dfm.getElementAt(i));
        }

        return cidsBeans;
    }
}
