/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis.gui.befreiungerlaubnis;

import java.sql.Date;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.List;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.verdis.commons.constants.BefreiungerlaubnisPropertyConstants;

import de.cismet.verdis.gui.AbstractCidsBeanTableModel;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten.hell@cismet.de
 * @version  $Revision$, $Date$
 */
public class BefreiungerlaubnisTableModel extends AbstractCidsBeanTableModel {

    //~ Static fields/initializers ---------------------------------------------

    private static final String[] COLUMN_NAMES = {
            "Aktenzeichen",
            "Antrag vom",
            "gültig bis",
            "Nutzung"
        };

    private static final Class[] COLUMN_CLASSES = {
            String.class,
            java.util.Date.class,
            java.util.Date.class,
            CidsBean.class
        };

    static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    //~ Instance fields --------------------------------------------------------

    List<CidsBean> befreiungen;

    private final transient org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new KanaldatenTableModel object.
     */
    public BefreiungerlaubnisTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        final CidsBean cidsBean = getCidsBeanByIndex(rowIndex);
        if (cidsBean == null) {
            return null;
        }
        switch (columnIndex) {
            case 0: {
                return (String)cidsBean.getProperty(BefreiungerlaubnisPropertyConstants.PROP__AKTENZEICHEN);
            }
            case 1: {
                final Date date = (Date)cidsBean.getProperty(BefreiungerlaubnisPropertyConstants.PROP__ANTRAG_VOM);
                return (date != null) ? DATE_FORMAT.format(date) : date;
            }
            case 2: {
                final Date date = (Date)cidsBean.getProperty(BefreiungerlaubnisPropertyConstants.PROP__GUELTIG_BIS);
                return (date != null) ? DATE_FORMAT.format(date) : date;
            }
            case 3: {
                return cidsBean.getProperty(BefreiungerlaubnisPropertyConstants.PROP__NUTZUNG);
            }
            default: {
                return null;
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
            switch (column) {
                case 0: {
                    be.setProperty(BefreiungerlaubnisPropertyConstants.PROP__AKTENZEICHEN, value.toString());
                }
                break;
                case 1: {
                    try {
                        be.setProperty(
                            BefreiungerlaubnisPropertyConstants.PROP__ANTRAG_VOM,
                            new Date(DATE_FORMAT.parse(value.toString()).getTime()));
                    } catch (ParseException e) {
                        be.setProperty(BefreiungerlaubnisPropertyConstants.PROP__ANTRAG_VOM, null);
                    }
                }
                break;
                case 2: {
                    try {
                        be.setProperty(
                            BefreiungerlaubnisPropertyConstants.PROP__GUELTIG_BIS,
                            new Date(DATE_FORMAT.parse(value.toString()).getTime()));
                    } catch (ParseException e) {
                        be.setProperty(BefreiungerlaubnisPropertyConstants.PROP__GUELTIG_BIS, null);
                    }
                }
                break;
                case 3: {
                    try {
                        be.setProperty(BefreiungerlaubnisPropertyConstants.PROP__NUTZUNG, value);
                    } catch (ParseException e) {
                        be.setProperty(BefreiungerlaubnisPropertyConstants.PROP__NUTZUNG, null);
                    }
                }
                break;
            }
        } catch (Exception e) {
            log.error("Fehler beim Ändern eines Attributes", e);
        }
    }

    @Override
    public boolean isCellEditable(final int row, final int column) {
        return false;
    }

//    /**
//     * DOCUMENT ME!
//     */
//    public void addRow() {
//        try {
//            befreiungen.add(cidsBean.createNewCidsBeanFromTableName(
//                    CidsAppBackend.getInstance().getDomain(),
//                    "befreiungerlaubniss"));
//        } catch (Exception e) {
//            log.error("Fehler beim Hinzufuegen einer neuen Befreiung", e);
//        }
//        fireTableDataChanged();
//    }
//    @Override
//    public void setCidsBean(final CidsBean kassenzeichenBean) {
//            befreiungen = kassenzeichenBean.getBeanCollectionProperty(VerdisMetaClassConstants.MC_KANALANSCHLUSS + "." + KanalanschlussPropertyConstants.PROP__BEFREIUNGENUNDERLAUBNISSE);
//            if (befreiungen != null) {
//                ((ObservableList<CidsBean>)befreiungen).addObservableListListener(new ObservableListListener() {
//
//                        @Override
//                        public void listElementsAdded(final ObservableList list, final int index, final int length) {
//                            fireTableDataChanged();
//                        }
//
//                        @Override
//                        public void listElementsRemoved(final ObservableList list,
//                                final int index,
//                                final List oldElements) {
//                            fireTableDataChanged();
//                        }
//
//                        @Override
//                        public void listElementReplaced(final ObservableList list,
//                                final int index,
//                                final Object oldElement) {
//                            fireTableDataChanged();
//                        }
//
//                        @Override
//                        public void listElementPropertyChanged(final ObservableList list, final int index) {
//                            fireTableDataChanged();
//                        }
//                    });
//            }
//        fireTableDataChanged();
//    }
}
