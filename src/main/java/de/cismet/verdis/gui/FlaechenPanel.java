/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * FlaechenPanel.java
 *
 * Created on 5. Januar 2005, 14:01
 */
package de.cismet.verdis.gui;
import edu.umd.cs.piccolo.PCanvas;

import java.awt.Color;
import java.awt.Dimension;

import java.sql.*;

import java.util.Iterator;
import java.util.Vector;

import javax.swing.*;

import de.cismet.cismap.commons.preferences.CismapPreferences;

import de.cismet.gui.tools.TableSorter;

import de.cismet.validation.NotValidException;

import de.cismet.verdis.data.Flaeche;

import de.cismet.verdis.interfaces.*;
/**
 * DOCUMENT ME!
 *
 * @author   hell
 * @version  $Revision$, $Date$
 */
public class FlaechenPanel extends javax.swing.JPanel implements KassenzeichenChangedListener,
    FlaechenAuswahlChangedListener,
    Storable {

    //~ Instance fields --------------------------------------------------------

    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private Connection connection;
    private ImageIcon ic_default;
    private ImageIcon ic_1;
    private boolean editmode = false;
    private Color myBlue = new java.awt.Color(0, 51, 153);
    private Main main;
    private JButton cmdUndo = new JButton();
    private JButton cmdRemove = new JButton();
    private JButton cmdAdd = new JButton();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.cismet.verdis.gui.FlaechenDetailPanel flDetailPanel;
    private de.cismet.verdis.gui.FlaechenUebersichtsTabellenPanel flOverviewPanel;
    private javax.swing.JPanel panFlaechen;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form FlaechenPanel.
     */
    public FlaechenPanel() {
        ic_default = new javax.swing.ImageIcon(getClass().getResource(
                    "/de/cismet/verdis/res/images/titlebars/flaechen.png"));
        ic_1 = new javax.swing.ImageIcon(getClass().getResource(
                    "/de/cismet/verdis/res/images/titlebars/animation/flaechen_1.png"));
        initComponents();
        createButtons();
        flOverviewPanel.addAuswahlChangedListener(this);
        final PCanvas pc = this.flOverviewPanel.getMappingComponent().getSelectedObjectPresenter();
        pc.setBackground(this.getBackground());
//        PCanvas pc=new PCanvas(); // PPT ;-)
//        pc.setBackground(this.getBackground()); //PPT ;-)
        this.flDetailPanel.setPCanvas(pc);
    }

    //~ Methods ----------------------------------------------------------------

// public void addWmsBackground(String url) {
// flOverviewPanel.addWmsBackground(url);
// }
    // Inserting Docking Window functionalty (Sebastian) 24.07.07
    @Override
    public void kassenzeichenChanged(final String kz) {
        flDetailPanel.clearDetails();
        final java.awt.event.ActionListener iconAnimator = new java.awt.event.ActionListener() {

                int counter = 0;

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent event) {
                    final javax.swing.Timer tt = (javax.swing.Timer)event.getSource();

                    if (tt.isRepeats() == false) {
                        // lblTitle.setIcon(ic_default);
                        main.setFlaechenPanelIcon(ic_default);
                    } else {
                        if (counter == 0) {
                            // lblTitle.setIcon(ic_1);
                            main.setFlaechenPanelIcon(ic_1);
                            counter++;
                        } else if (counter == 1) {
                            // lblTitle.setIcon(ic_default);
                            main.setFlaechenPanelIcon(ic_default);
                            counter = 0;
                        }
                    }
                }
            };

        final javax.swing.Timer animationTimer = new javax.swing.Timer(75, iconAnimator);
        animationTimer.setRepeats(true);
        animationTimer.start();

        flOverviewPanel.kassenzeichenChanged(kz, animationTimer);
    }

    @Override
    public void flaechenAuswahlChanged(final de.cismet.verdis.data.Flaeche f) {
        if (f != null) {
            flDetailPanel.setDetails(f);
            if (f.hasChanged() && (editmode == true)) {
                cmdUndo.setEnabled(true);
            } else {
                cmdUndo.setEnabled(false);
            }
        } else {
            flDetailPanel.clearDetails();
        }
    }

    @Override
    public void setEnabled(final boolean b) {
        this.flDetailPanel.setEnabled(b);
        this.flOverviewPanel.setEnabled(b);
        cmdAdd.setEnabled(b);
        cmdRemove.setEnabled(b);
        cmdUndo.setEnabled(b);
    }
    /**
     * Inserting Docking Window functionalty (Sebastian) 24.07.07 temporary disabled --> handled in Main.java.
     *
     * @param  c  DOCUMENT ME!
     */
    public void setLeftTitlebarColor(final Color c) {
        // panTitle.setLeftColor(c);
        // panTitle.repaint();
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
            log.fatal("Fehler beim Aufbau der Datenbankverbindung!"
                        + "(Url:" + connectionInfo.getUrl()
                        + " User:" + connectionInfo.getUser(),
                sqlEx);
        } catch (Throwable t) {
            log.fatal("Fehler beim Aufbau der Datenbankverbindung! "
                        + "(Url:" + connectionInfo.getUrl()
                        + " User:" + connectionInfo.getUser(),
                t);
        }
        flOverviewPanel.setConnection(connection);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panFlaechen = new javax.swing.JPanel();
        flOverviewPanel = new de.cismet.verdis.gui.FlaechenUebersichtsTabellenPanel();
        flDetailPanel = new de.cismet.verdis.gui.FlaechenDetailPanel();

        setLayout(new java.awt.BorderLayout());

        panFlaechen.setLayout(new java.awt.GridBagLayout());

        flOverviewPanel.setMinimumSize(new java.awt.Dimension(360, 55));
        flOverviewPanel.setPreferredSize(new java.awt.Dimension(370, 434));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panFlaechen.add(flOverviewPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panFlaechen.add(flDetailPanel, gridBagConstraints);

        add(panFlaechen, java.awt.BorderLayout.CENTER);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Vector<JButton> getCustomButtons() {
        final Vector<JButton> tmp = new Vector<JButton>();
        tmp.add(cmdAdd);
        tmp.add(cmdRemove);
        tmp.add(cmdUndo);
        return tmp;
    }

    /**
     * DOCUMENT ME!
     */
    private void createButtons() {
        cmdUndo.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/undo.png")));
        cmdUndo.setBorderPainted(false);
        cmdUndo.setFocusPainted(false);
        cmdUndo.setMinimumSize(new java.awt.Dimension(25, 25));
        cmdUndo.setOpaque(false);
        cmdUndo.setPreferredSize(new java.awt.Dimension(25, 25));
        cmdUndo.setRolloverIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/undo2.png")));
        cmdUndo.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdUndoActionPerformed(evt);
                }
            });

        cmdRemove.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/remove.png")));
        cmdRemove.setBorderPainted(false);
        cmdRemove.setFocusPainted(false);
        cmdRemove.setMinimumSize(new java.awt.Dimension(25, 25));
        cmdRemove.setOpaque(false);
        cmdRemove.setPreferredSize(new java.awt.Dimension(25, 25));
        cmdRemove.setRolloverIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/remove2.png")));
        cmdRemove.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdRemoveActionPerformed(evt);
                }
            });

        cmdAdd.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/add.png")));
        cmdAdd.setBorderPainted(false);
        cmdAdd.setFocusPainted(false);
        cmdAdd.setMinimumSize(new java.awt.Dimension(25, 25));
        cmdAdd.setOpaque(false);
        cmdAdd.setPreferredSize(new java.awt.Dimension(25, 25));
        cmdAdd.setRolloverIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/verdis/res/images/titlebars/add2.png")));
        cmdAdd.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdAddActionPerformed(evt);
                }
            });
    }
    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdRemoveActionPerformed(final java.awt.event.ActionEvent evt) {
        flOverviewPanel.removeSelectedFlaeche();
        flDetailPanel.clearDetails();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdAddActionPerformed(final java.awt.event.ActionEvent evt) {
        final Object[] possibleValues = {
                "Dachfl\u00E4che",
                "Gr\u00FCndach",
                "versiegelte Fl\u00E4che",
                "\u00D6kopflaster",
                "st\u00E4dtische Stra\u00DFenfl\u00E4che",
                "st\u00E4dtische Stra\u00DFenfl\u00E4che (\u00D6kopflaster)"
            };
        final Object selectedValue = JOptionPane.showInputDialog(
                this,
                "W\u00E4hlen Sie die Art der neuen Fl\u00E4che aus",
                "Neue Fl\u00E4che",
                JOptionPane.QUESTION_MESSAGE,
                null,
                possibleValues,
                possibleValues[0]);
        // if (selectedValue!=null)
        int art = -1;
        for (int i = 0; i < possibleValues.length; ++i) {
            if (possibleValues[i].equals(selectedValue)) {
                art = 1 + i;
                break;
            }
        }
        flOverviewPanel.addNewFlaeche(art, this);
        flaechenAuswahlChanged(flOverviewPanel.getModel().getSelectedFlaeche());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdUndoActionPerformed(final java.awt.event.ActionEvent evt) {
        this.flOverviewPanel.undoSelectedFlaeche();
    }

    @Override
    public boolean changesPending() {
        return flOverviewPanel.getModel().hasChanged();
    }
    @Override
    public void enableEditing(final boolean b) {
        setEnabled(b);
        editmode = b;
    }
    @Override
    public boolean lockDataset() {
        return true;
    }
    @Override
    public void unlockDataset() {
    }
    @Override
    public void addStoreChangeStatements(final java.util.Vector v) throws NotValidException {
        flOverviewPanel.getModel().createAndAddStatements(v);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Object getSelectedFlaeche() {
        if ((flOverviewPanel.getModel().getSelectedFlaeche() == null)
                    && (flOverviewPanel.getJxtOverview().getSelectedRowCount() > 1)) {
            final Vector clipboard = new Vector();

            final int[] rows = flOverviewPanel.getJxtOverview().getSelectedRows();
            for (int i = 0; i < rows.length; ++i) {
                final int modelIndex = flOverviewPanel.getJxtOverview().getFilters().convertRowIndexToModel(rows[i]);
                final Flaeche f = flOverviewPanel.getTableModel().getFlaechebyIndex(modelIndex);
                f.setClipboardStatus(Flaeche.COPIED);
                final Flaeche c = (Flaeche)f.clone();
                clipboard.add(c);
            }
            return clipboard;
        } else {
            final Flaeche sf = flOverviewPanel.getModel().getSelectedFlaeche();
            sf.setClipboardStatus(Flaeche.COPIED);
            final Flaeche c = (Flaeche)sf.clone();
            c.setNewFlaeche(true);
            return c;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Object cutSelectedFlaeche() {
        if ((flOverviewPanel.getModel().getSelectedFlaeche() == null)
                    && (flOverviewPanel.getJxtOverview().getSelectedRowCount() > 1)) {
            final Vector clipboard = new Vector();
            final Vector deletedFlaechen = new Vector();
            final int[] rows = flOverviewPanel.getJxtOverview().getSelectedRows();
            for (int i = 0; i < rows.length; ++i) {
                final int modelIndex = flOverviewPanel.getJxtOverview().getFilters().convertRowIndexToModel(rows[i]);
                if (log.isDebugEnabled()) {
                    log.debug("copy " + rows[i] + "(" + modelIndex);
                }
                final Flaeche f = flOverviewPanel.getTableModel().getFlaechebyIndex(modelIndex);
//                Flaeche f=flOverviewPanel.getTableModel().getFlaechebyIndex(rows[i]);
                f.setClipboardStatus(Flaeche.CUTTED);
                deletedFlaechen.add(f);
                final Flaeche c = (Flaeche)f.clone();
                c.setMarkedForDeletion(false);
                clipboard.add(c);
            }
            final Iterator it = deletedFlaechen.iterator();
            while (it.hasNext()) {
                flOverviewPanel.removeFlaeche((Flaeche)it.next());
            }
            return clipboard;
        } else {
            final Flaeche sf = flOverviewPanel.getModel().getSelectedFlaeche();
            if (sf != null) {
                sf.setClipboardStatus(Flaeche.CUTTED);
                final Flaeche c = (Flaeche)sf.clone();
                flOverviewPanel.removeSelectedFlaeche();
                flDetailPanel.clearDetails();
                c.setMarkedForDeletion(false);
                return c;
            } else {
                return null;
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  f  DOCUMENT ME!
     */
    public void pasteFlaeche(final Flaeche f) {
        flOverviewPanel.getModel().addFlaeche(f);
    }
    /**
     * DOCUMENT ME!
     *
     * @param  f  DOCUMENT ME!
     */
    public void pasteFlaecheWithoutRefresh(final Flaeche f) {
        flOverviewPanel.getModel().addFlaecheWithoutRefresh(f);
    }
    /**
     * DOCUMENT ME!
     *
     * @param  f  DOCUMENT ME!
     */
    public void refreshTableAndMapAfterPaste(final Flaeche f) {
        flOverviewPanel.getModel().refreshAfterAdd(f);
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

    /**
     * DOCUMENT ME!
     *
     * @param  p  DOCUMENT ME!
     */
    public void setCismapPreferences(final CismapPreferences p) {
        flOverviewPanel.setCismapPreferences(p);
    }
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Connection getConnection() {
        return connection;
    }
    /**
     * DOCUMENT ME!
     */
    public void reEnumerateFlaechen() {
        flOverviewPanel.reEnumerateFlaechen();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public de.cismet.verdis.gui.FlaechenUebersichtsTabellenPanel getFlOverviewPanel() {
        return flOverviewPanel;
    }
}
