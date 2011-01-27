/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * SummenPanel.java
 *
 * Created on 5. Januar 2005, 14:01
 */
package de.cismet.verdis.gui;
import java.awt.Color;

import java.sql.*;

import java.util.*;

import javax.swing.*;
import javax.swing.table.*;
/**
 * DOCUMENT ME!
 *
 * @author   hell
 * @version  $Revision$, $Date$
 */
public class SummenPanel extends javax.swing.JPanel
        implements de.cismet.verdis.interfaces.KassenzeichenChangedListener {

    //~ Instance fields --------------------------------------------------------

    private Connection connection;
    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private SummenTableModel tableModel;
    private boolean busy = false;
    private ImageIcon ic_default;
    private ImageIcon ic_0;
    private ImageIcon ic_1;
    private ImageIcon ic_2;
    private ImageIcon ic_3;
    private ImageIcon ic_4;
    private ImageIcon ic_5;
    private ImageIcon ic_6;
    private ImageIcon ic_7;
    // PERHAPS BETTER TO GIVE THE SUMMENPANEL THE VIEWOBJECT
    private Main main;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel panTab;
    private javax.swing.JTable tabSum;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form SummenPanel.
     */
    public SummenPanel() {
        ic_default = new javax.swing.ImageIcon(getClass().getResource(
                    "/de/cismet/verdis/res/images/titlebars/sum.png"));
        ic_0 = new javax.swing.ImageIcon(getClass().getResource(
                    "/de/cismet/verdis/res/images/titlebars/animation/sum_0.png"));
        ic_1 = new javax.swing.ImageIcon(getClass().getResource(
                    "/de/cismet/verdis/res/images/titlebars/animation/sum_1.png"));
        ic_2 = new javax.swing.ImageIcon(getClass().getResource(
                    "/de/cismet/verdis/res/images/titlebars/animation/sum_2.png"));
        ic_3 = new javax.swing.ImageIcon(getClass().getResource(
                    "/de/cismet/verdis/res/images/titlebars/animation/sum_3.png"));
        ic_4 = new javax.swing.ImageIcon(getClass().getResource(
                    "/de/cismet/verdis/res/images/titlebars/animation/sum_4.png"));
        ic_5 = new javax.swing.ImageIcon(getClass().getResource(
                    "/de/cismet/verdis/res/images/titlebars/animation/sum_5.png"));
        ic_6 = new javax.swing.ImageIcon(getClass().getResource(
                    "/de/cismet/verdis/res/images/titlebars/animation/sum_6.png"));
        ic_7 = new javax.swing.ImageIcon(getClass().getResource(
                    "/de/cismet/verdis/res/images/titlebars/animation/sum_7.png"));
        initComponents();
        tableModel = new SummenTableModel(connection);
        tabSum.setModel(tableModel);
        tabSum.setDefaultRenderer(Object.class, new SummenTableCellRenderer());
        // tabSum.getColumnModel().getColumn(0).setPreferredWidth(10);
        // tabSum.setTableHeader(null);
        // tabSum.getColumnModel().getColumn(0).setMaxWidth(40);
        // tabSum.getColumnModel().getColumn(1).setMaxWidth(40);
        // tabSum.getColumnModel().getColumn(2).setMaxWidth(20);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Inserting Docking Window functionalty (Sebastian) 24.07.07 temporary disabled --> handled in Main.java.
     *
     * @param  c  DOCUMENT ME!
     */
    public void setLeftTitlebarColor(final Color c) {
        // panTitle.setLeftColor(c);
        // panTitle.repaint();
    }

    @Override
    public void kassenzeichenChanged(final String kz) {
        final java.awt.event.ActionListener iconAnimator = new java.awt.event.ActionListener() {

                int counter = 0;

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent event) {
                    final javax.swing.Timer tt = (javax.swing.Timer)event.getSource();
                    if (tt.isRepeats() == false) {
                        // TODO DOCKING
                        // lblTitle.setIcon(ic_default);
                        main.setSummenPanelIcon(ic_default);
                    } else {
                        if (counter == 0) {
                            // lblTitle.setIcon(ic_0);
                            main.setSummenPanelIcon(ic_0);
                            counter++;
                        } else if (counter == 1) {
                            // lblTitle.setIcon(ic_1);
                            main.setSummenPanelIcon(ic_1);
                            counter++;
                        } else if (counter == 2) {
                            // lblTitle.setIcon(ic_2);
                            main.setSummenPanelIcon(ic_2);
                            counter++;
                        } else if (counter == 3) {
                            // lblTitle.setIcon(ic_3);
                            main.setSummenPanelIcon(ic_3);
                            counter++;
                        } else if (counter == 4) {
                            // lblTitle.setIcon(ic_4);
                            main.setSummenPanelIcon(ic_4);
                            counter++;
                        } else if (counter == 5) {
                            // lblTitle.setIcon(ic_5);
                            main.setSummenPanelIcon(ic_5);
                            counter++;
                        } else if (counter == 6) {
                            // lblTitle.setIcon(ic_6);
                            main.setSummenPanelIcon(ic_6);
                            counter++;
                        } else if (counter == 7) {
                            // lblTitle.setIcon(ic_7);
                            main.setSummenPanelIcon(ic_7);
                            counter++;
                        } else if (counter == 8) {
                            // lblTitle.setIcon(ic_0);
                            main.setSummenPanelIcon(ic_0);
                            counter++;
                        } else if (counter == 9) {
                            // lblTitle.setIcon(ic_7);
                            main.setSummenPanelIcon(ic_7);
                            counter++;
                        } else if (counter == 10) {
                            // lblTitle.setIcon(ic_6);
                            main.setSummenPanelIcon(ic_6);
                            counter++;
                        } else if (counter == 11) {
                            // lblTitle.setIcon(ic_5);
                            main.setSummenPanelIcon(ic_5);
                            counter++;
                        } else if (counter == 12) {
                            // lblTitle.setIcon(ic_4);
                            main.setSummenPanelIcon(ic_4);
                            counter++;
                        } else if (counter == 13) {
                            // lblTitle.setIcon(ic_3);
                            main.setSummenPanelIcon(ic_3);
                            counter++;
                        } else if (counter == 14) {
                            // lblTitle.setIcon(ic_2);
                            main.setSummenPanelIcon(ic_2);
                            counter++;
                        } else if (counter == 15) {
                            // lblTitle.setIcon(ic_1);
                            main.setSummenPanelIcon(ic_1);
                            counter++;
                        } else if (counter == 16) {
                            // lblTitle.setIcon(ic_0);
                            main.setSummenPanelIcon(ic_0);
                            counter = 0;
                        }
                    }
                }
            };

        final javax.swing.Timer animationTimer = new javax.swing.Timer(40, iconAnimator);
        animationTimer.setRepeats(true);
        animationTimer.start();

        tableModel = new SummenTableModel(connection);
        tabSum.setModel(tableModel);
        tableModel.kassenzeichenChanged(kz, animationTimer);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  connectionInfo  DOCUMENT ME!
     */
    public void setConnectionInfo(final de.cismet.tools.ConnectionInfo connectionInfo) {
        try {
            Class.forName(connectionInfo.getDriver());
            connection = DriverManager.getConnection(connectionInfo.getUrl(),
                    connectionInfo.getUser(),
                    connectionInfo.getPass());
        } catch (ClassNotFoundException cnfEx) {
            log.fatal("Datenbanktreiber nicht gefunden!", cnfEx);
        } catch (java.sql.SQLException sqlEx) {
            log.fatal("Fehler beim Aufbau der Datenbankverbindung!", sqlEx);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        final java.awt.GridBagConstraints gridBagConstraints;

        panTab = new javax.swing.JPanel();
        tabSum = new javax.swing.JTable();

        setLayout(new java.awt.BorderLayout());

        panTab.setLayout(new java.awt.BorderLayout());

        panTab.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panTab.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        panTab.setMinimumSize(new java.awt.Dimension(100, 84));
        panTab.setPreferredSize(new java.awt.Dimension(120, 84));
        tabSum.setBackground(javax.swing.UIManager.getDefaults().getColor("Label.background"));
        tabSum.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                    { null, null, null, null },
                    { null, null, null, null },
                    { null, null, null, null },
                    { null, null, null, null }
                },
                new String[] { "Title 1", "Title 2", "Title 3", "Title 4" }));
        tabSum.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tabSum.setFocusable(false);
        tabSum.setRowSelectionAllowed(false);
        tabSum.setShowHorizontalLines(false);
        tabSum.setShowVerticalLines(false);
        panTab.add(tabSum, java.awt.BorderLayout.CENTER);

        add(panTab, java.awt.BorderLayout.CENTER);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public java.sql.Connection getConnection() {
        return connection;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Main getMain() {
        return main;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  main  DOCUMENT ME!
     */
    public void setMain(final Main main) {
        this.main = main;
    }
}
