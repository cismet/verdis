/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 *  Copyright (C) 2010 thorsten
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
package de.cismet.verdis.gui.befreiungerlaubnis;

import java.sql.Date;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.ListSelectionModel;
import javax.swing.SortOrder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;

import de.cismet.validation.Validator;
import de.cismet.validation.ValidatorState;
import de.cismet.validation.ValidatorStateImpl;

import de.cismet.validation.validator.AggregatedValidator;
import de.cismet.validation.validator.CidsBeanValidator;

import de.cismet.verdis.CidsAppBackend;

import de.cismet.verdis.commons.constants.BefreiungerlaubnisPropertyConstants;
import de.cismet.verdis.commons.constants.KanalanschlussPropertyConstants;
import de.cismet.verdis.commons.constants.VerdisMetaClassConstants;

import de.cismet.verdis.gui.AbstractCidsBeanTable;
import de.cismet.verdis.gui.Main;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class BefreiungerlaubnisTable extends AbstractCidsBeanTable implements CidsBeanStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BefreiungerlaubnisTable.class);

    //~ Instance fields --------------------------------------------------------

    private CidsBean cidsBean;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BefreiungerlaubnisTable object.
     */
    public BefreiungerlaubnisTable() {
        super(CidsAppBackend.Mode.KANALDATEN, new BefreiungerlaubnisTableModel());

        initComponents();
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        setDragEnabled(false);

        getTableHeader().setResizingAllowed(true);
        getTableHeader().setReorderingAllowed(true);
        setSortOrder(0, SortOrder.ASCENDING);

        getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                @Override
                public void valueChanged(final ListSelectionEvent e) {
                    final Iterator<CidsBean> it = getSelectedBeans().iterator();
                    final CidsBean selectedBean = it.hasNext() ? it.next() : null;
                    Main.getInstance().getBefreiungerlaubnisGeometrieTable().setCidsBean(selectedBean);
                }
            });
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   befreiungBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Validator createValidatorGueltigbis(final CidsBean befreiungBean) {
        return new CidsBeanValidator(befreiungBean, BefreiungerlaubnisPropertyConstants.PROP__GUELTIG_BIS) {

                @Override
                public ValidatorState performValidation() {
                    final CidsBean cidsBean = getCidsBean();
                    if (cidsBean == null) {
                        return null;
                    }
                    final Date gueltigBis = (Date)cidsBean.getProperty(
                            BefreiungerlaubnisPropertyConstants.PROP__GUELTIG_BIS);

                    if (gueltigBis == null) {
                        return new ValidatorStateImpl(
                                ValidatorState.Type.ERROR,
                                "Es wurde kein gültiges Datum angegeben.");
                    } else {
                        return new ValidatorStateImpl(ValidatorState.Type.VALID);
                    }
                }
            };
    }

    /**
     * DOCUMENT ME!
     *
     * @param   befreiungBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Validator createValidatorAntragVom(final CidsBean befreiungBean) {
        return new CidsBeanValidator(befreiungBean, BefreiungerlaubnisPropertyConstants.PROP__ANTRAG_VOM) {

                @Override
                public ValidatorState performValidation() {
                    final CidsBean cidsBean = getCidsBean();
                    if (cidsBean == null) {
                        return null;
                    }
                    final Date gueltigBis = (Date)cidsBean.getProperty(
                            BefreiungerlaubnisPropertyConstants.PROP__ANTRAG_VOM);

                    if (gueltigBis == null) {
                        return new ValidatorStateImpl(
                                ValidatorState.Type.ERROR,
                                "Es wurde kein gültiges Datum angegeben.");
                    } else {
                        return new ValidatorStateImpl(ValidatorState.Type.VALID);
                    }
                }
            };
    }

    /**
     * DOCUMENT ME!
     *
     * @param   befreiungBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Validator createValidatorAktenzeichen(final CidsBean befreiungBean) {
        return new CidsBeanValidator(befreiungBean, BefreiungerlaubnisPropertyConstants.PROP__AKTENZEICHEN) {

                @Override
                public ValidatorState performValidation() {
                    final CidsBean cidsBean = getCidsBean();
                    if (cidsBean == null) {
                        return null;
                    }
                    final String aktenzeichen = (String)cidsBean.getProperty(
                            BefreiungerlaubnisPropertyConstants.PROP__AKTENZEICHEN);

                    if ((aktenzeichen != null) && aktenzeichen.matches("^[0-9]{3}-[0-9]{3}-[0-9]{4}\\/([0-9]+)$")) {
                        return new ValidatorStateImpl(ValidatorState.Type.VALID);
                    } else {
                        return new ValidatorStateImpl(
                                ValidatorState.Type.ERROR,
                                "Es wurde kein gültiges Aktenzeichen angegeben.");
                    }
                }
            };
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        setLayout(new java.awt.BorderLayout());
    } // </editor-fold>

    @Override
    public Validator getItemValidator(final CidsBean beferBean) {
        final AggregatedValidator aggVal = new AggregatedValidator();
        aggVal.validate();
        return aggVal;
    }

    @Override
    public CidsBean createNewBean() throws Exception {
        return null;
    }

    @Override
    public void removeBean(final CidsBean cidsBean) {
        if (cidsBean != null) {
            try {
                cidsBean.delete();
            } catch (final Exception ex) {
                LOG.error("error while removing befer", ex);
            }
        }
        super.removeBean(cidsBean);
    }

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        this.cidsBean = cidsBean;

        Main.getInstance().getBefreiungerlaubnisGeometrieTable().setCidsBean(null);
        if (cidsBean != null) {
            setCidsBeans(cidsBean.getBeanCollectionProperty(
                    VerdisMetaClassConstants.MC_KANALANSCHLUSS
                            + "."
                            + KanalanschlussPropertyConstants.PROP__BEFREIUNGENUNDERLAUBNISSE));
        } else {
            setCidsBeans(new ArrayList<CidsBean>());
        }
    }
}
