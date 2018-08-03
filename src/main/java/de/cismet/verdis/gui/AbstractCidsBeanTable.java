/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis.gui;

import org.jdesktop.swingx.JXTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;

import de.cismet.cids.custom.util.CidsBeanSupport;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.validation.Validator;

import de.cismet.validation.validator.AggregatedValidator;

import de.cismet.verdis.CidsAppBackend;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public abstract class AbstractCidsBeanTable extends JXTable implements CidsBeanTable, ListSelectionListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AbstractCidsBeanTable.class);
    private static int NEW_BEAN_ID = 0;

    //~ Instance fields --------------------------------------------------------

    private final Map<Integer, CidsBean> beanBackups = new HashMap<Integer, CidsBean>();
    private final AggregatedValidator aggVal = new AggregatedValidator();
    private final HashMap<CidsBean, Validator> beanToValidatorMap = new HashMap<CidsBean, Validator>();
    private AbstractCidsBeanDetailsPanel selectedRowListener = null;
    private final CidsAppBackend.Mode modus;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form AbstractCidsBeanTable2.
     *
     * @param  modus  DOCUMENT ME!
     * @param  model  DOCUMENT ME!
     */
    public AbstractCidsBeanTable(final CidsAppBackend.Mode modus, final AbstractCidsBeanTableModel model) {
        this.modus = modus;
        setModel(model);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    getSelectionModel().removeListSelectionListener(AbstractCidsBeanTable.this);
                    getSelectionModel().addListSelectionListener(AbstractCidsBeanTable.this);
                }
            });
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected CidsAppBackend.Mode getModus() {
        return modus;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static int getNextNewBeanId() {
        return --NEW_BEAN_ID;
    }

    @Override
    public void addNewBean() {
        try {
            final CidsBean newBean = createNewBean();
        } catch (final Exception ex) {
            LOG.error("error while creating new bean", ex);
        }
    }

    @Override
    public void removeSelectedBeans() {
        for (final CidsBean cidsBean : getSelectedBeans()) {
            removeBean(cidsBean);
            aggVal.remove(beanToValidatorMap.get(cidsBean));
            aggVal.validate();
            beanToValidatorMap.remove(cidsBean);
        }
    }

    @Override
    public void restoreSelectedBeans() {
        final Collection<CidsBean> cidsBeans = getSelectedBeans();
        for (final CidsBean cidsBean : cidsBeans) {
            restoreBean(cidsBean);
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void clearBackups() {
        beanBackups.clear();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    public void backupBean(final CidsBean cidsBean) {
        try {
            beanBackups.put((Integer)cidsBean.getProperty("id"), CidsBeanSupport.deepcloneCidsBean(cidsBean));
        } catch (Exception ex) {
            LOG.error("error while making backup of bean", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    public void unbackupBean(final CidsBean cidsBean) {
        beanBackups.remove((Integer)cidsBean.getProperty("id"));
    }

    @Override
    public CidsBean getBeanBackup(final CidsBean cidsBean) {
        return beanBackups.get((Integer)cidsBean.getProperty("id"));
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    public void restoreBean(final CidsBean cidsBean) {
        try {
            final CidsBean backupBean = beanBackups.get((Integer)cidsBean.getProperty("id"));
            CidsBeanSupport.copyAllProperties(backupBean, cidsBean);
        } catch (Exception ex) {
            LOG.error("error while making backup of bean", ex);
        }
    }

    @Override
    public void addBean(final CidsBean cidsBean) {
        if (getModel().getCidsBeans() != null) {
            backupBean(cidsBean);
            getModel().addCidsBean(cidsBean);
            final Validator validator = getItemValidator(cidsBean);
            beanToValidatorMap.put(cidsBean, validator);
            aggVal.add(validator);
            aggVal.validate();
        }
    }

    @Override
    public void removeBean(final CidsBean cidsBean) {
        if (cidsBean != null) {
            try {
                getModel().removeCidsBean(cidsBean);
                unbackupBean(cidsBean);
            } catch (Exception ex) {
                LOG.error("error while removing bean", ex);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public AbstractCidsBeanDetailsPanel getSelectedRowListener() {
        return selectedRowListener;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  selectedRowListener  DOCUMENT ME!
     */
    @Override
    public void setSelectedRowListener(final AbstractCidsBeanDetailsPanel selectedRowListener) {
        this.selectedRowListener = selectedRowListener;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cb  DOCUMENT ME!
     */
    protected void setDetailBean(final CidsBean cb) {
        if (selectedRowListener != null) {
            selectedRowListener.setCidsBean(cb);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cbs  DOCUMENT ME!
     */
    protected void setDetailBeans(final Collection<CidsBean> cbs) {
        if (selectedRowListener != null) {
            selectedRowListener.setCidsBeans(cbs);
        }
    }

    @Override
    public void valueChanged(final ListSelectionEvent ev) {
        if (!((ev == null) || ev.getValueIsAdjusting())) {
            final int[] selection = getSelectedRows();
            final int[] modelSelection = new int[selection.length];

            for (int index = 0; index < selection.length; ++index) {
                modelSelection[index] = convertRowIndexToModel(selection[index]);
            }

            setDetailBeans(getModel().getCidsBeansByIndices(modelSelection));
            repaint();

            Main.getInstance().selectionChanged();
        }
    }

    @Override
    public void setCidsBeans(final List<CidsBean> cidsBeans) {
        getModel().setCidsBeans(cidsBeans);
        beanToValidatorMap.clear();
        aggVal.clear();
        clearBackups();
        if (cidsBeans != null) {
            for (final CidsBean tableBean : cidsBeans) {
                final Validator validator = getItemValidator(tableBean);
                beanToValidatorMap.put(tableBean, validator);
                aggVal.add(validator);
                backupBean(tableBean);
            }
        }
        aggVal.validate();
    }

    @Override
    public List<CidsBean> getSelectedBeans() {
        final List<CidsBean> cidsBeans = new ArrayList<CidsBean>();

        final int[] rows;
        if (getSelectedRowCount() <= 0) {
            return cidsBeans;
        } else if (getSelectedRowCount() == 1) {
            rows = new int[] { getSelectedRow() };
        } else {
            rows = getSelectedRows();
        }

        for (int i = 0; i < rows.length; ++i) {
            cidsBeans.add(getModel().getCidsBeanByIndex(convertRowIndexToModel(rows[i])));
        }

        return cidsBeans;
    }

    @Override
    public AbstractCidsBeanTable getTableHelper() {
        return this;
    }

    @Override
    public List<CidsBean> getAllBeans() {
        return getModel().getCidsBeans();
    }

    @Override
    public void selectCidsBean(final CidsBean cidsBean) {
        final int index = getModel().getIndexByCidsBean(cidsBean);
        if (index >= 0) {
            final int viewIndex = convertRowIndexToView(index);
            getSelectionModel().setSelectionInterval(viewIndex, viewIndex);
        }
    }

    @Override
    public void selectCidsBeans(final List<CidsBean> beans) {
        getSelectionModel().clearSelection();
        for (final CidsBean bean : beans) {
            final int index = getModel().getIndexByCidsBean(bean);
            getSelectionModel().addSelectionInterval(index, index);
        }
    }

    @Override
    public final Validator getValidator() {
        return aggVal;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public final AbstractCidsBeanTableModel getModel() {
        final TableModel model = super.getModel();
        if (model instanceof AbstractCidsBeanTableModel) {
            return (AbstractCidsBeanTableModel)model;
        } else {
            return new AbstractCidsBeanTableModel(new String[] {}, new Class[] {}) {

                    @Override
                    public Object getValueAt(final int rowIndex, final int columnIndex) {
                        return null;
                    }
                };
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public abstract CidsBean createNewBean() throws Exception;

    /**
     * DOCUMENT ME!
     *
     * @param   cidsBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public abstract Validator getItemValidator(final CidsBean cidsBean);
}
