/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * TableSorter.java
 *
 * Created on 12. Januar 2005, 10:27
 */
package de.cismet.verdis.gui;
import java.awt.*;
import java.awt.event.*;

import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;

/**
 * TableSorter is a decorator for TableModels; adding sorting functionality to a supplied TableModel. TableSorter does
 * not store or copy the data in its TableModel; instead it maintains a map from the row indexes of the view to the row
 * indexes of the model. As requests are made of the sorter (like getValueAt(row, col)) they are passed to the
 * underlying model after the row numbers have been translated via the internal mapping array. This way, the TableSorter
 * appears to hold another copy of the table with the rows in a different order.
 *
 * <p/>TableSorter registers itself as a listener to the underlying model, just as the JTable itself would. Events
 * recieved from the model are examined, sometimes manipulated (typically widened), and then passed on to the
 * TableSorter's listeners (typically the JTable). If a change to the model has invalidated the order of TableSorter's
 * rows, a note of this is made and the sorter will resort the rows the next time a value is requested.</p>
 *
 * <p>When the tableHeader property is set, either by using the setTableHeader() method or the two argument constructor,
 * the table header may be used as a complete UI for TableSorter. The default renderer of the tableHeader is decorated
 * with a renderer that indicates the sorting status of each column. In addition, a mouse listener is installed with the
 * following behavior:</p>
 *
 * <ul>
 *   <li>Mouse-click: Clears the sorting status of all other columns and advances the sorting status of that column
 *     through three values: {NOT_SORTED, ASCENDING, DESCENDING} (then back to NOT_SORTED again).</li>
 *   <li>SHIFT-mouse-click: Clears the sorting status of all other columns and cycles the sorting status of the column
 *     through the same three values, in the opposite order: {NOT_SORTED, DESCENDING, ASCENDING}.</li>
 *   <li>CONTROL-mouse-click and CONTROL-SHIFT-mouse-click: as above except that the changes to the column do not cancel
 *     the statuses of columns that are already sorting - giving a way to initiate a compound sort.</li>
 * </ul>
 *
 * <p>This is a long overdue rewrite of a class of the same name that first appeared in the swing table demos in 1997.
 * </p>
 *
 * @author   Philip Milne
 * @author   Brendon McLean
 * @author   Dan van Enckevort
 * @author   Parwinder Sekhon
 * @author   Thorsten Hell (minor Changes)
 * @version  2.0 02/27/04
 */

public class TableSorter extends AbstractTableModel {

    //~ Static fields/initializers ---------------------------------------------

    /** Globale Variable. */
    public static final int DESCENDING = -1;
    /** Globale Variable. */
    public static final int NOT_SORTED = 0;
    /** Globale Variable. */
    public static final int ASCENDING = 1;

    private static Directive EMPTY_DIRECTIVE = new Directive(-1, NOT_SORTED);

    /** Globale Variable. */
    public static final Comparator COMPARABLE_COMAPRATOR = new Comparator() {

            @Override
            public int compare(final Object o1, final Object o2) {
                return ((Comparable)o1).compareTo(o2);
            }
        };

    /** Globale Variable. */
    public static final Comparator LEXICAL_COMPARATOR = new Comparator() {

            @Override
            public int compare(final Object o1, final Object o2) {
                return o1.toString().compareTo(o2.toString());
            }
        };

    //~ Instance fields --------------------------------------------------------

    protected TableModel tableModel;
    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private boolean tmpNoSort = false;

    private Row[] viewToModel;
    private int[] modelToView;

    private JTableHeader tableHeader;
    private MouseListener mouseListener;
    private TableModelListener tableModelListener;
    private Map columnComparators = new HashMap();
    private List sortingColumns = new ArrayList();

    //~ Constructors -----------------------------------------------------------

    /**
     * Leerer Konstruktor.
     */
    public TableSorter() {
        this.mouseListener = new MouseHandler();
        this.tableModelListener = new TableModelHandler();
    }

    /**
     * Konstruktor.
     *
     * @param  tableModel  DOCUMENT ME!
     */
    public TableSorter(final TableModel tableModel) {
        this();
        setTableModel(tableModel);
    }

    /**
     * Konstruktor.
     *
     * @param  tableModel   DOCUMENT ME!
     * @param  tableHeader  DOCUMENT ME!
     */
    public TableSorter(final TableModel tableModel, final JTableHeader tableHeader) {
        this();
        setTableHeader(tableHeader);
        setTableModel(tableModel);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void clearSortingState() {
        viewToModel = null;
        modelToView = null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public TableModel getTableModel() {
        return tableModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  tableModel  DOCUMENT ME!
     */
    public void setTableModel(final TableModel tableModel) {
        if (this.tableModel != null) {
            this.tableModel.removeTableModelListener(tableModelListener);
        }

        this.tableModel = tableModel;
        if (this.tableModel != null) {
            this.tableModel.addTableModelListener(tableModelListener);
        }

        clearSortingState();
        fireTableStructureChanged();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JTableHeader getTableHeader() {
        return tableHeader;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  tableHeader  DOCUMENT ME!
     */
    public void setTableHeader(final JTableHeader tableHeader) {
        if (this.tableHeader != null) {
            this.tableHeader.removeMouseListener(mouseListener);
            final TableCellRenderer defaultRenderer = this.tableHeader.getDefaultRenderer();
            if (defaultRenderer instanceof SortableHeaderRenderer) {
                this.tableHeader.setDefaultRenderer(((SortableHeaderRenderer)defaultRenderer).tableCellRenderer);
            }
        }
        this.tableHeader = tableHeader;
        if (this.tableHeader != null) {
            this.tableHeader.addMouseListener(mouseListener);
            this.tableHeader.setDefaultRenderer(
                new SortableHeaderRenderer(this.tableHeader.getDefaultRenderer()));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isSorting() {
        return sortingColumns.size() != 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   column  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Directive getDirective(final int column) {
        for (int i = 0; i < sortingColumns.size(); i++) {
            final Directive directive = (Directive)sortingColumns.get(i);
            if (directive.column == column) {
                return directive;
            }
        }
        return EMPTY_DIRECTIVE;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   column  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getSortingStatus(final int column) {
        return getDirective(column).direction;
    }

    /**
     * DOCUMENT ME!
     */
    private void sortingStatusChanged() {
        clearSortingState();
        fireTableDataChanged();
        if (tableHeader != null) {
            tableHeader.repaint();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  column  DOCUMENT ME!
     * @param  status  DOCUMENT ME!
     */
    public void setSortingStatus(final int column, final int status) {
        final Directive directive = getDirective(column);
        if (directive != EMPTY_DIRECTIVE) {
            sortingColumns.remove(directive);
        }
        if (status != NOT_SORTED) {
            sortingColumns.add(new Directive(column, status));
        }
        sortingStatusChanged();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   column  DOCUMENT ME!
     * @param   size    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected Icon getHeaderRendererIcon(final int column, final int size) {
        final Directive directive = getDirective(column);
        if (directive == EMPTY_DIRECTIVE) {
            return null;
        }
        return new Arrow(directive.direction == DESCENDING, size, sortingColumns.indexOf(directive));
    }

    /**
     * DOCUMENT ME!
     */
    private void cancelSorting() {
        sortingColumns.clear();
        sortingStatusChanged();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  type        DOCUMENT ME!
     * @param  comparator  DOCUMENT ME!
     */
    public void setColumnComparator(final Class type, final Comparator comparator) {
        if (comparator == null) {
            columnComparators.remove(type);
        } else {
            columnComparators.put(type, comparator);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  column      DOCUMENT ME!
     * @param  comparator  DOCUMENT ME!
     */
    public void setColumnComparatorByColumn(final int column, final Comparator comparator) {
        final Integer key = new Integer(column);
        if (comparator == null) {
            columnComparators.remove(key);
        } else {
            columnComparators.put(key, comparator);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   column  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected Comparator getComparator(final int column) {
        final Integer key = new Integer(column);
        final Object o = columnComparators.get(key);
        if (o != null) {
            return (Comparator)o;
        } else {
            final Class columnType = tableModel.getColumnClass(column);
            final Comparator comparator = (Comparator)columnComparators.get(columnType);
            if (comparator != null) {
                return comparator;
            }
            if (Comparable.class.isAssignableFrom(columnType)) {
                return COMPARABLE_COMAPRATOR;
            }
            return LEXICAL_COMPARATOR;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Row[] getViewToModel() {
        if (viewToModel == null) {
            final int tableModelRowCount = tableModel.getRowCount();
            viewToModel = new Row[tableModelRowCount];

            for (int i = 0; i < 100; i++) {
                if (viewToModel != null) {
                    break;
                }
                log.warn("viewToModel war null.... Versuch zu beheben Nr.: " + i);
                viewToModel = new Row[tableModelRowCount];
            }
            for (int row = 0; row < tableModelRowCount; row++) {
                viewToModel[row] = new Row(row);
            }

            if (isSorting()) {
                Arrays.sort(viewToModel);
            }
        }
        return viewToModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   viewIndex  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int modelIndex(final int viewIndex) {
        Row[] r = getViewToModel();
        if (r == null) {
            log.warn("modelIndex(" + viewIndex + ")");
            for (int i = 0; i < 100; i++) {
                log.warn("Synchronisationsproblem " + i);
                r = getViewToModel();
                if (r != null) {
                    return r[viewIndex].modelIndex;
                }
            }
        }
        return r[viewIndex].modelIndex;
            // return getViewToModel()[viewIndex].modelIndex;
    }
    /**
     * DOCUMENT ME!
     *
     * @param   i  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int viewIndex(final int i) {
        final int[] r = getModelToView();
        return r[i];
            // return getViewToModel()[viewIndex].modelIndex;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private int[] getModelToView() {
        if (modelToView == null) {
            final int n = getViewToModel().length;
            modelToView = new int[n];
            for (int i = 0; i < n; i++) {
                modelToView[modelIndex(i)] = i;
            }
        }
        return modelToView;
    }

    // TableModel interface methods

    @Override
    public int getRowCount() {
        return (tableModel == null) ? 0 : tableModel.getRowCount();
    }

    @Override
    public int getColumnCount() {
        return (tableModel == null) ? 0 : tableModel.getColumnCount();
    }

    @Override
    public String getColumnName(final int column) {
        return tableModel.getColumnName(column);
    }

    @Override
    public Class getColumnClass(final int column) {
        return tableModel.getColumnClass(column);
    }

    @Override
    public boolean isCellEditable(final int row, final int column) {
        return tableModel.isCellEditable(modelIndex(row), column);
    }

    @Override
    public Object getValueAt(final int row, final int column) {
        try {
            if (!isSorting()) {
                return tableModel.getValueAt(row, column);
            } else {
                return tableModel.getValueAt(modelIndex(row), column);
            }
        } catch (Exception e) {
            log.warn("Fehler bei getValueAt() implements TableSorter", e);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   row  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getSortedIndex(final int row) {
        return modelIndex(row);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   row  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getSortedPosition(final int row) {
        return viewIndex(row);
    }

    @Override
    public void setValueAt(final Object aValue, final int row, final int column) {
        tableModel.setValueAt(aValue, modelIndex(row), column);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * Helper classes.
     *
     * @version  $Revision$, $Date$
     */
    private class Row implements Comparable, Cloneable {

        //~ Instance fields ----------------------------------------------------

        private int modelIndex;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new Row object.
         *
         * @param  index  DOCUMENT ME!
         */
        public Row(final int index) {
            this.modelIndex = index;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public Object clone() {
            final Row r = new Row(modelIndex);
            return r;
        }

        @Override
        public int compareTo(final Object o) {
            final int row1 = modelIndex;
            final int row2 = ((Row)o).modelIndex;

            for (final Iterator it = sortingColumns.iterator(); it.hasNext();) {
                final Directive directive = (Directive)it.next();
                final int column = directive.column;
                final Object o1 = tableModel.getValueAt(row1, column);
                final Object o2 = tableModel.getValueAt(row2, column);

                int comparison = 0;
                // Define null less than everything, except null.
                if ((o1 == null) && (o2 == null)) {
                    comparison = 0;
                } else if (o1 == null) {
                    comparison = -1;
                } else if (o2 == null) {
                    comparison = 1;
                } else {
                    comparison = getComparator(column).compare(o1, o2);
                }
                if (comparison != 0) {
                    return (directive.direction == DESCENDING) ? -comparison : comparison;
                }
            }
            return 0;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class TableModelHandler implements TableModelListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void tableChanged(final TableModelEvent e) {
            // If we're not sorting by anything, just pass the event along.
            if (!isSorting()) {
                clearSortingState();
                fireTableChanged(e);

                return;
            }

            // If the table structure has changed, cancel the sorting; the
            // sorting columns may have been either moved or deleted from
            // the model.
            if (e.getFirstRow() == TableModelEvent.HEADER_ROW) {
                cancelSorting();
                fireTableChanged(e);

                return;
            }

            // We can map a cell event through to the view without widening
            // when the following conditions apply:
            //
            // a) all the changes are on one row (e.getFirstRow() == e.getLastRow()) and,
            // b) all the changes are in one column (column != TableModelEvent.ALL_COLUMNS) and,
            // c) we are not sorting on that column (getSortingStatus(column) == NOT_SORTED) and,
            // d) a reverse lookup will not trigger a sort (modelToView != null)
            //
            // Note: INSERT and DELETE events fail this test as they have column == ALL_COLUMNS.
            //
            // The last check, for (modelToView != null) is to see if modelToView
            // is already allocated. If we don't do this check; sorting can become
            // a performance bottleneck for applications where cells
            // change rapidly in different parts of the table. If cells
            // change alternately in the sorting column and then outside of
            // it this class can end up re-sorting on alternate cell updates -
            // which can be a performance problem for large tables. The last
            // clause avoids this problem.
            final int column = e.getColumn();
            if ((e.getFirstRow() == e.getLastRow())
                        && (column != TableModelEvent.ALL_COLUMNS)
                        && (getSortingStatus(column) == NOT_SORTED)
                        && (modelToView != null)) {
                final int viewIndex = getModelToView()[e.getFirstRow()];
                fireTableChanged(new TableModelEvent(TableSorter.this,
                        viewIndex, viewIndex,
                        column, e.getType()));

                return;
            }

            // Something has happened to the data that may have invalidated the row order.
            clearSortingState();
            fireTableDataChanged();

            return;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class MouseHandler extends MouseAdapter {

        //~ Methods ------------------------------------------------------------

        @Override
        public void mouseClicked(final MouseEvent e) {
            final JTableHeader h = (JTableHeader)e.getSource();
            final TableColumnModel columnModel = h.getColumnModel();
            final int viewColumn = columnModel.getColumnIndexAtX(e.getX());
            final int column = columnModel.getColumn(viewColumn).getModelIndex();
            if (column != -1) {
                int status = getSortingStatus(column);
                if (!e.isControlDown()) {
                    cancelSorting();
                }
                // Cycle the sorting states through {NOT_SORTED, ASCENDING, DESCENDING} or
                // {NOT_SORTED, DESCENDING, ASCENDING} depending on whether shift is pressed.
                status = status + (e.isShiftDown() ? -1 : 1);
                status = ((status + 4) % 3) - 1; // signed mod, returning {-1, 0, 1}
                setSortingStatus(column, status);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static class Arrow implements Icon {

        //~ Instance fields ----------------------------------------------------

        private boolean descending;
        private int size;
        private int priority;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new Arrow object.
         *
         * @param  descending  DOCUMENT ME!
         * @param  size        DOCUMENT ME!
         * @param  priority    DOCUMENT ME!
         */
        public Arrow(final boolean descending, final int size, final int priority) {
            this.descending = descending;
            this.size = size;
            this.priority = priority;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void paintIcon(final Component c, final Graphics g, final int x, int y) {
            final Color color = (c == null) ? Color.GRAY : c.getBackground();
            // In a compound sort, make each succesive triangle 20%
            // smaller than the previous one.
            final int dx = (int)(size / 2 * Math.pow(0.8, priority));
            final int dy = descending ? dx : -dx;
            // Align icon (roughly) with font baseline.
            y = y + (5 * size / 6) + (descending ? -dy : 0);
            final int shift = descending ? 1 : -1;
            g.translate(x, y);

            // Right diagonal.
            g.setColor(color.darker());
            g.drawLine(dx / 2, dy, 0, 0);
            g.drawLine(dx / 2, dy + shift, 0, shift);

            // Left diagonal.
            g.setColor(color.brighter());
            g.drawLine(dx / 2, dy, dx, 0);
            g.drawLine(dx / 2, dy + shift, dx, shift);

            // Horizontal line.
            if (descending) {
                g.setColor(color.darker().darker());
            } else {
                g.setColor(color.brighter().brighter());
            }
            g.drawLine(dx, 0, 0, 0);

            g.setColor(color);
            g.translate(-x, -y);
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class SortableHeaderRenderer implements TableCellRenderer {

        //~ Instance fields ----------------------------------------------------

        private TableCellRenderer tableCellRenderer;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new SortableHeaderRenderer object.
         *
         * @param  tableCellRenderer  DOCUMENT ME!
         */
        public SortableHeaderRenderer(final TableCellRenderer tableCellRenderer) {
            this.tableCellRenderer = tableCellRenderer;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public Component getTableCellRendererComponent(final JTable table,
                final Object value,
                final boolean isSelected,
                final boolean hasFocus,
                final int row,
                final int column) {
            final Component c = tableCellRenderer.getTableCellRendererComponent(
                    table,
                    value,
                    isSelected,
                    hasFocus,
                    row,
                    column);
            if (c instanceof JLabel) {
                final JLabel l = (JLabel)c;
                l.setHorizontalTextPosition(JLabel.LEFT);
                final int modelColumn = table.convertColumnIndexToModel(column);
                l.setIcon(getHeaderRendererIcon(modelColumn, l.getFont().getSize()));
            }
            return c;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static class Directive {

        //~ Instance fields ----------------------------------------------------

        private int column;
        private int direction;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new Directive object.
         *
         * @param  column     DOCUMENT ME!
         * @param  direction  DOCUMENT ME!
         */
        public Directive(final int column, final int direction) {
            this.column = column;
            this.direction = direction;
        }
    }
}
