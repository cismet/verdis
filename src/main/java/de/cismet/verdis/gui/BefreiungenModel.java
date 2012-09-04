/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis.gui;

import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.observablecollections.ObservableListListener;

import java.sql.Date;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.List;

import javax.swing.table.DefaultTableModel;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;

import de.cismet.verdis.CidsAppBackend;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten.hell@cismet.de
 * @version  $Revision$, $Date$
 */
public class BefreiungenModel extends DefaultTableModel implements CidsBeanStore {

    //~ Instance fields --------------------------------------------------------

    CidsBean kassenzeichenBean;
    List<CidsBean> befreiungen;
    final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    private final transient org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BefreiungenModel object.
     */
    public BefreiungenModel() {
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public int getRowCount() {
        if (befreiungen != null) {
            return befreiungen.size();
        } else {
            return 0;
        }
    }

    @Override
    public String getColumnName(final int column) {
        if (column == 0) {
            return "Aktenzeichen";
        } else {
            return "g\u00FCltig bis";
        }
    }

    @Override
    public Object getValueAt(final int row, final int column) {
        final CidsBean be = getBefreiungAt(row);
        if (column == 0) {
            return be.getProperty("aktenzeichen");
        } else {
            final Date gueltigBis = (Date)be.getProperty("gueltig_bis");
            if (gueltigBis != null) {
                return dateFormat.format(gueltigBis);
            } else {
                return "";
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   row  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getBefreiungAt(final int row) {
        return befreiungen.get(row);
    }

    @Override
    public void setValueAt(final Object value, final int row, final int column) {
        final CidsBean be = befreiungen.get(row);
        try {
            if (column == 0) {
                be.setProperty("aktenzeichen", value.toString());
            } else {
                try {
                    be.setProperty("gueltig_bis", new Date(dateFormat.parse(value.toString()).getTime()));
                } catch (ParseException e) {
                    be.setProperty("gueltig_bis", null);
                }
            }
        } catch (Exception e) {
            log.error("Fehler beim Ändern eines Attributes", e);
        }
    }

    @Override
    public boolean isCellEditable(final int row, final int column) {
        return false;
    }

    /**
     * DOCUMENT ME!
     */
    public void addRow() {
        try {
            befreiungen.add(kassenzeichenBean.createNewCidsBeanFromTableName(
                    CidsAppBackend.getInstance().getDomain(),
                    "befreiungerlaubniss"));
        } catch (Exception e) {
            log.error("Fehler beim Hinzufuegen einer neuen Befreiung", e);
        }
        fireTableDataChanged();
    }

    @Override
    public CidsBean getCidsBean() {
        return kassenzeichenBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        kassenzeichenBean = cidsBean;
        if (cidsBean != null) {
            befreiungen = kassenzeichenBean.getBeanCollectionProperty("kanalanschluss.befreiungenunderlaubnisse");
            if (befreiungen != null) {
                ((ObservableList<CidsBean>)befreiungen).addObservableListListener(new ObservableListListener() {

                        @Override
                        public void listElementsAdded(final ObservableList list, final int index, final int length) {
                            fireTableDataChanged();
                        }

                        @Override
                        public void listElementsRemoved(final ObservableList list,
                                final int index,
                                final List oldElements) {
                            fireTableDataChanged();
                        }

                        @Override
                        public void listElementReplaced(final ObservableList list,
                                final int index,
                                final Object oldElement) {
                            fireTableDataChanged();
                        }

                        @Override
                        public void listElementPropertyChanged(final ObservableList list, final int index) {
                            fireTableDataChanged();
                        }
                    });
            }
        }
        fireTableDataChanged();
    }
}
